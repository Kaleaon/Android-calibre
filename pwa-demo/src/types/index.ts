// Data types corresponding to the Android Room entities

export interface Library {
  libraryId?: number;
  name: string;
  type: 'BOOK' | 'MOVIE' | 'MUSIC' | 'PODCAST' | 'MAGAZINE' | 'DOCUMENT';
  path: string;
  dateCreated?: Date;
  dateModified?: Date;
}

export interface MediaItem {
  itemId?: number;
  libraryId: number;
  filePath: string;
  fileName: string;
  fileSize: number;
  mimeType: string;
  mediaType: 'BOOK' | 'MOVIE' | 'MUSIC' | 'PODCAST' | 'MAGAZINE' | 'DOCUMENT';
  dateAdded?: Date;
  lastModified?: Date;
  fileHash?: string;
}

export interface MetadataCommon {
  itemId: number;
  title: string;
  description?: string;
  thumbnailPath?: string;
  rating?: number;
  tags?: string;
  dateCreated?: Date;
  lastAccessed?: Date;
  isFavorite: boolean;
  isDownloaded: boolean;
}

export interface MetadataBook extends MetadataCommon {
  author?: string;
  isbn?: string;
  publisher?: string;
  publicationDate?: string;
  pageCount?: number;
  language?: string;
  series?: string;
  seriesIndex?: number;
  format?: string;
}

export interface BookDetails {
  mediaItem: MediaItem;
  metadataCommon: MetadataCommon;
  metadataBook?: MetadataBook;
}

export enum ViewMode {
  GRID = 'GRID',
  LIST = 'LIST',
  COVER_FLOW = 'COVER_FLOW'
}

export interface ImportStatus {
  isImporting: boolean;
  status: string;
  progress?: number;
}