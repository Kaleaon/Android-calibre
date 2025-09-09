// Zustand store for state management - equivalent to Android ViewModels
import { create } from 'zustand';
import { Library, ImportStatus } from '../types';
import { LibraryService } from '../services/database';

interface AppState {
  // Libraries state
  libraries: Library[];
  isLoading: boolean;
  
  // Import state
  importStatus: ImportStatus;
  
  // Actions
  loadLibraries: () => Promise<void>;
  addLibrary: (name: string, type: Library['type'], path: string) => Promise<void>;
  deleteLibrary: (libraryId: number) => Promise<void>;
  setImportStatus: (status: ImportStatus) => void;
}

export const useAppStore = create<AppState>((set, get) => ({
  libraries: [],
  isLoading: false,
  importStatus: { isImporting: false, status: '' },

  loadLibraries: async () => {
    set({ isLoading: true });
    try {
      const libraries = await LibraryService.getAllLibraries();
      set({ libraries, isLoading: false });
      
      // Initialize with default library if empty (like Android version)
      if (libraries.length === 0) {
        await get().addLibrary('My Books', 'BOOK', '/books');
      }
    } catch (error) {
      console.error('Failed to load libraries:', error);
      set({ isLoading: false });
    }
  },

  addLibrary: async (name: string, type: Library['type'], path: string) => {
    try {
      await LibraryService.addLibrary({ name, type, path });
      await get().loadLibraries(); // Refresh the list
    } catch (error) {
      console.error('Failed to add library:', error);
    }
  },

  deleteLibrary: async (libraryId: number) => {
    try {
      await LibraryService.deleteLibrary(libraryId);
      await get().loadLibraries(); // Refresh the list
    } catch (error) {
      console.error('Failed to delete library:', error);
    }
  },

  setImportStatus: (status: ImportStatus) => {
    set({ importStatus: status });
  }
}));