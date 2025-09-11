// IndexedDB service using Dexie - equivalent to Android Room
import Dexie, { Table } from 'dexie';
import { Library, MediaItem, MetadataCommon, MetadataBook } from '../types';

export class CleverFerretDB extends Dexie {
  libraries!: Table<Library>;
  mediaItems!: Table<MediaItem>;
  metadataCommon!: Table<MetadataCommon>;
  metadataBooks!: Table<MetadataBook>;

  constructor() {
    super('CleverFerretDB');
    
    this.version(3).stores({
      libraries: '++libraryId, name, type, path',
      mediaItems: '++itemId, libraryId, fileName, mediaType, filePath',
      metadataCommon: 'itemId, title, isFavorite, isDownloaded',
      metadataBooks: 'itemId, author, isbn, series'
    });
  }
}

export const db = new CleverFerretDB();

// Service layer - equivalent to Android DAOs
export class LibraryService {
  static async getAllLibraries(): Promise<Library[]> {
    return await db.libraries.toArray();
  }

  static async addLibrary(library: Omit<Library, 'libraryId'>): Promise<number> {
    return await db.libraries.add({
      ...library,
      dateCreated: new Date(),
      dateModified: new Date()
    });
  }

  static async deleteLibrary(libraryId: number): Promise<void> {
    await db.transaction('rw', [db.libraries, db.mediaItems, db.metadataCommon], async () => {
      // Delete all related media items first
      const mediaItems = await db.mediaItems.where('libraryId').equals(libraryId).toArray();
      const itemIds = mediaItems.map(item => item.itemId!);
      
      // Delete metadata for all items
      await db.metadataCommon.where('itemId').anyOf(itemIds).delete();
      await db.metadataBooks.where('itemId').anyOf(itemIds).delete();
      
      // Delete media items
      await db.mediaItems.where('libraryId').equals(libraryId).delete();
      
      // Finally delete the library
      await db.libraries.delete(libraryId);
    });
  }
}

export class MediaItemService {
  static async getItemsByLibrary(libraryId: number): Promise<MediaItem[]> {
    return await db.mediaItems.where('libraryId').equals(libraryId).toArray();
  }

  static async addMediaItem(item: Omit<MediaItem, 'itemId'>): Promise<number> {
    return await db.mediaItems.add({
      ...item,
      dateAdded: new Date(),
      lastModified: new Date()
    });
  }

  static async searchItems(query: string): Promise<MediaItem[]> {
    return await db.mediaItems
      .filter(item => 
        item.fileName.toLowerCase().includes(query.toLowerCase())
      )
      .toArray();
  }
}

export class MetadataService {
  static async getBookDetails(itemId: number) {
    const [mediaItem, metadataCommon, metadataBook] = await Promise.all([
      db.mediaItems.get(itemId),
      db.metadataCommon.get(itemId),
      db.metadataBooks.get(itemId)
    ]);

    return {
      mediaItem,
      metadataCommon,
      metadataBook
    };
  }

  static async updateMetadata(itemId: number, metadata: Partial<MetadataCommon>): Promise<void> {
    await db.metadataCommon.update(itemId, metadata);
  }

  static async toggleFavorite(itemId: number): Promise<void> {
    const current = await db.metadataCommon.get(itemId);
    if (current) {
      await db.metadataCommon.update(itemId, { isFavorite: !current.isFavorite });
    }
  }
}