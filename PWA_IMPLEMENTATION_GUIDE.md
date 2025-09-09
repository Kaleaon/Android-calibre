# React PWA Implementation Guide

This document provides a step-by-step guide for implementing the Progressive Web App version of CleverFerret, based on the Android Kotlin application.

## Quick Start

### 1. Prerequisites
```bash
# Install Node.js 18+ and npm
node --version  # Should be 18.0.0 or higher
npm --version   # Should be 9.0.0 or higher
```

### 2. Setup Development Environment
```bash
cd pwa-demo
npm install
npm run dev
```

The development server will start at `http://localhost:3000`

### 3. Build for Production
```bash
npm run build
npm run preview  # Preview production build
```

## Architecture Mapping

### Android → React Component Mapping

| Android Component | React Component | File Location |
|------------------|------------------|---------------|
| `MainActivity.kt` | `App.tsx` | `/src/App.tsx` |
| `LibraryListScreen` (Compose) | `LibraryListScreen` | `/src/components/LibraryListScreen.tsx` |
| `LibraryDetailsScreen` (Compose) | `LibraryDetailsScreen` | `/src/components/LibraryDetailsScreen.tsx` |
| `MainViewModel` | `useAppStore` (Zustand) | `/src/store/app-store.ts` |

### Data Layer Mapping

| Android (Room) | React (IndexedDB) | File Location |
|----------------|-------------------|---------------|
| `AppDatabase.kt` | `CleverFerretDB` | `/src/services/database.ts` |
| `LibraryDao` | `LibraryService` | `/src/services/database.ts` |
| `MediaItemDao` | `MediaItemService` | `/src/services/database.ts` |
| `MetadataDao` | `MetadataService` | `/src/services/database.ts` |

## Key Features Implementation

### 1. Database Operations

The React PWA uses IndexedDB with Dexie.js to replicate Room database functionality:

```typescript
// Android Room equivalent
@Query("SELECT * FROM libraries")
fun getAllLibraries(): Flow<List<Library>>

// React IndexedDB equivalent
static async getAllLibraries(): Promise<Library[]> {
  return await db.libraries.toArray();
}
```

### 2. State Management

Zustand replaces Android ViewModels and StateFlow:

```typescript
// Android ViewModel
@HiltViewModel
class MainViewModel @Inject constructor(
    private val libraryDao: LibraryDao
) : ViewModel() {
    val libraries: StateFlow<List<Library>> = libraryDao.getAllLibraries()
}

// React Zustand Store
export const useAppStore = create<AppState>((set, get) => ({
  libraries: [],
  loadLibraries: async () => {
    const libraries = await LibraryService.getAllLibraries();
    set({ libraries });
  }
}));
```

### 3. UI Components

Material-UI components replace Jetpack Compose:

```tsx
// Android Compose
@Composable
fun LibraryCard(library: Library, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        // Card content
    }
}

// React Material-UI
const LibraryCard: React.FC<{
  library: Library;
  onClick: () => void;
}> = ({ library, onClick }) => {
  return (
    <Card onClick={onClick}>
      {/* Card content */}
    </Card>
  );
};
```

## Advanced Features

### 1. File System Access

The PWA handles file operations differently than Android:

```typescript
// File selection (replaces Android file picker)
const handleFileSelect = async () => {
  if ('showOpenFilePicker' in window) {
    // Use File System Access API (Chrome-based browsers)
    const [fileHandle] = await window.showOpenFilePicker();
    const file = await fileHandle.getFile();
    return file;
  } else {
    // Fallback to input element
    const input = document.createElement('input');
    input.type = 'file';
    input.click();
    // Handle file selection
  }
};
```

### 2. Background Processing

Service Workers replace Android Services:

```javascript
// Service Worker for background tasks
self.addEventListener('sync', (event) => {
  if (event.tag === 'calibre-import') {
    event.waitUntil(processCalibreImport());
  }
});
```

### 3. Offline Functionality

PWA caching strategy for offline access:

```javascript
// Cache-first strategy for app shell
event.respondWith(
  caches.match(request)
    .then((cachedResponse) => {
      return cachedResponse || fetch(request);
    })
);
```

## Development Workflow

### 1. Component Development
1. Create React components in `/src/components/`
2. Use Material-UI components for consistent design
3. Implement TypeScript interfaces from `/src/types/`
4. Connect to Zustand store for state management

### 2. Database Operations
1. Define entities in `/src/types/index.ts`
2. Create service methods in `/src/services/database.ts`
3. Use Dexie.js for IndexedDB operations
4. Handle migrations for schema changes

### 3. PWA Features
1. Configure manifest in `/public/manifest.json`
2. Implement service worker in `/public/sw.js`
3. Add caching strategies for offline support
4. Test installation and offline functionality

## Testing

### 1. PWA Testing
```bash
# Test PWA features
npm run build
npm run preview

# Use Chrome DevTools:
# - Application tab → Manifest
# - Application tab → Service Workers
# - Lighthouse audit for PWA score
```

### 2. Cross-Browser Testing
- Chrome 80+ (full feature support)
- Firefox 75+ (limited File System Access)
- Safari 13+ (basic PWA support)
- Edge 80+ (full feature support)

### 3. Mobile Testing
- Use Chrome DevTools device simulation
- Test on actual mobile devices
- Verify touch interactions and responsive design

## Deployment

### 1. Build Configuration
```bash
# Production build
npm run build

# Verify service worker registration
# Check manifest.json validation
# Test offline functionality
```

### 2. Hosting Requirements
- HTTPS required for PWA features
- Proper MIME types for manifest.json
- Service worker served from root domain
- Caching headers for static assets

### 3. CDN Configuration
```nginx
# Example nginx configuration
location /manifest.json {
    add_header Cache-Control "public, max-age=3600";
}

location /sw.js {
    add_header Cache-Control "public, max-age=0";
}
```

## Performance Optimization

### 1. Bundle Splitting
```typescript
// Code splitting for routes
const LibraryDetailsScreen = React.lazy(() => 
  import('./components/LibraryDetailsScreen')
);
```

### 2. Database Optimization
```typescript
// Index optimization for queries
this.version(3).stores({
  libraries: '++libraryId, name, type, path',
  mediaItems: '++itemId, libraryId, [libraryId+mediaType]',
  metadataCommon: 'itemId, [itemId+isFavorite]'
});
```

### 3. Media Optimization
- Lazy loading for large media libraries
- Virtual scrolling for thousands of items
- Image compression and WebP support
- Progressive loading for book content

## Migration Strategy

### 1. Data Migration
```typescript
// Convert Android SQLite to IndexedDB
const migrateCalibreData = async (sqliteFile: File) => {
  // Parse SQLite database
  // Transform schema to IndexedDB format
  // Bulk insert data with transaction handling
};
```

### 2. Feature Parity
- [ ] Core library management ✅
- [ ] Book reading interface
- [ ] Audio/video playback
- [ ] Metadata editing
- [ ] Search and filtering ✅
- [ ] Import/export functionality
- [ ] Settings and preferences

### 3. Progressive Enhancement
1. Start with core features (library management)
2. Add media playback capabilities
3. Implement advanced features (OCR, metadata fetching)
4. Add offline synchronization
5. Integrate cloud storage options

## Troubleshooting

### Common Issues

1. **Service Worker not updating**
   ```javascript
   // Force service worker update
   navigator.serviceWorker.register('/sw.js', { updateViaCache: 'none' });
   ```

2. **IndexedDB storage limits**
   ```typescript
   // Check storage quota
   const estimate = await navigator.storage.estimate();
   console.log('Storage used:', estimate.usage);
   console.log('Storage quota:', estimate.quota);
   ```

3. **File access limitations**
   ```typescript
   // Feature detection
   const hasFileSystemAccess = 'showOpenFilePicker' in window;
   if (!hasFileSystemAccess) {
     // Use fallback file input
   }
   ```

### Browser Compatibility

| Feature | Chrome | Firefox | Safari | Edge |
|---------|--------|---------|---------|------|
| PWA Install | ✅ | ✅ | ✅ | ✅ |
| Service Workers | ✅ | ✅ | ✅ | ✅ |
| IndexedDB | ✅ | ✅ | ✅ | ✅ |
| File System Access | ✅ | ❌ | ❌ | ✅ |
| Background Sync | ✅ | ❌ | ❌ | ✅ |

## Next Steps

1. **Complete the demo**: Add missing components and features
2. **Test extensively**: Cross-browser and mobile testing
3. **Optimize performance**: Bundle size and runtime performance
4. **Add advanced features**: Media playback, OCR, cloud sync
5. **Deploy and iterate**: Production deployment and user feedback

This implementation provides a solid foundation for converting the Android CleverFerret app to a React PWA while maintaining feature parity and user experience.