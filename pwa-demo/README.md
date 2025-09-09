# CleverFerret PWA Demo

This is a demonstration of how the CleverFerret Android application could be converted to a Progressive Web App using React.

## Features Demonstrated

- **React Components**: Equivalent to Android Jetpack Compose screens
- **IndexedDB Database**: Replaces Android Room database
- **State Management**: Zustand store replaces Android ViewModels
- **Material Design**: Material-UI components with Material You theme
- **PWA Functionality**: Service worker, manifest, offline support
- **File Handling**: Web-based file operations
- **Responsive Design**: Works on desktop and mobile

## Quick Start

```bash
cd pwa-demo
npm install
npm run dev
```

Open `http://localhost:3000` in your browser.

## Key Files

- `src/App.tsx` - Main application component
- `src/components/LibraryListScreen.tsx` - Library management screen
- `src/components/LibraryDetailsScreen.tsx` - Book listing screen
- `src/services/database.ts` - IndexedDB operations
- `src/store/app-store.ts` - Zustand state management
- `public/manifest.json` - PWA manifest
- `public/sw.js` - Service worker

## Architecture

This demo shows how Android concepts map to web technologies:

- **Kotlin** → **TypeScript**
- **Jetpack Compose** → **React + Material-UI**
- **Room Database** → **IndexedDB + Dexie.js**
- **ViewModels + StateFlow** → **Zustand Store**
- **Android Services** → **Service Workers**
- **Material You** → **Material-UI Theming**

## Building

```bash
npm run build
npm run preview
```

## PWA Testing

1. Build the project
2. Serve from `dist` folder over HTTPS
3. Use Chrome DevTools → Application → Manifest
4. Test offline functionality
5. Try installing as PWA

## Note

This is a demonstration project showing the conversion approach. A full implementation would require:

- Complete e-book reader functionality
- Audio/video playback components
- Advanced file handling
- Metadata extraction and editing
- Import/export features
- Cross-browser compatibility testing
- Performance optimization