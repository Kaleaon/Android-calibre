// Service Worker for PWA functionality
const CACHE_NAME = 'cleverferret-v1';
const STATIC_CACHE = 'cleverferret-static-v1';
const DYNAMIC_CACHE = 'cleverferret-dynamic-v1';

// Files to cache on install
const STATIC_FILES = [
  '/',
  '/index.html',
  '/manifest.json',
  '/icons/icon-192.png',
  '/icons/icon-512.png',
  // Add other static assets
];

// Install event - cache static files
self.addEventListener('install', (event) => {
  console.log('Service Worker installing...');
  event.waitUntil(
    caches.open(STATIC_CACHE)
      .then((cache) => {
        console.log('Pre-caching static files...');
        return cache.addAll(STATIC_FILES);
      })
      .then(() => {
        return self.skipWaiting();
      })
  );
});

// Activate event - clean up old caches
self.addEventListener('activate', (event) => {
  console.log('Service Worker activating...');
  event.waitUntil(
    caches.keys().then((keyList) => {
      return Promise.all(keyList.map((key) => {
        if (key !== STATIC_CACHE && key !== DYNAMIC_CACHE) {
          console.log('Removing old cache:', key);
          return caches.delete(key);
        }
      }));
    }).then(() => {
      return self.clients.claim();
    })
  );
});

// Fetch event - serve cached content when offline
self.addEventListener('fetch', (event) => {
  const { request } = event;

  // Skip non-GET requests
  if (request.method !== 'GET') {
    return;
  }

  // Skip external requests
  if (!request.url.startsWith(self.location.origin)) {
    return;
  }

  event.respondWith(
    // Try cache first, then network
    caches.match(request)
      .then((cachedResponse) => {
        if (cachedResponse) {
          return cachedResponse;
        }

        // If not in cache, fetch from network
        return fetch(request)
          .then((networkResponse) => {
            // Cache dynamic content for offline access
            if (networkResponse.ok) {
              const responseClone = networkResponse.clone();
              caches.open(DYNAMIC_CACHE)
                .then((cache) => {
                  cache.put(request, responseClone);
                });
            }
            return networkResponse;
          })
          .catch(() => {
            // If network fails, return offline page for navigation requests
            if (request.mode === 'navigate') {
              return caches.match('/offline.html');
            }
          });
      })
  );
});

// Background sync for importing files
self.addEventListener('sync', (event) => {
  if (event.tag === 'calibre-import') {
    event.waitUntil(processCalibreImport());
  }
});

// Background sync for media processing
async function processCalibreImport() {
  try {
    // Process any pending import tasks
    console.log('Processing background import...');
    // Implementation would handle actual import logic
  } catch (error) {
    console.error('Background import failed:', error);
  }
}

// Handle file uploads and processing
self.addEventListener('message', (event) => {
  if (event.data && event.data.type === 'PROCESS_FILE') {
    const { file, type } = event.data;
    processFileUpload(file, type)
      .then((result) => {
        event.ports[0].postMessage({ success: true, result });
      })
      .catch((error) => {
        event.ports[0].postMessage({ success: false, error: error.message });
      });
  }
});

async function processFileUpload(file, type) {
  // Handle different file types
  switch (type) {
    case 'epub':
      return processEpubFile(file);
    case 'pdf':
      return processPdfFile(file);
    case 'calibre-db':
      return processCalibreDatabase(file);
    default:
      throw new Error(`Unsupported file type: ${type}`);
  }
}

async function processEpubFile(file) {
  // Extract metadata from EPUB file
  // This would use epub parsing libraries in the actual implementation
  return {
    title: 'Extracted Title',
    author: 'Extracted Author',
    size: file.size
  };
}

async function processPdfFile(file) {
  // Extract metadata from PDF file
  return {
    title: file.name,
    size: file.size,
    type: 'pdf'
  };
}

async function processCalibreDatabase(file) {
  // Process Calibre metadata.db file
  // This would involve SQLite parsing in the actual implementation
  return {
    booksImported: 0,
    metadataProcessed: true
  };
}