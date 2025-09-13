// CleverFerret PWA - Plex-inspired Universal Media Library
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { CssBaseline, Box } from '@mui/material';
import { LibraryListScreen } from './components/LibraryListScreen';
import { LibraryDetailsScreen } from './components/LibraryDetailsScreen';
import { MediaViewerScreen } from './components/MediaViewerScreen';
import { MetadataEditorScreen } from './components/MetadataEditorScreen';
import { SettingsScreen } from './components/SettingsScreen';

// Plex-inspired dark theme
const plexTheme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#e5a00d', // Plex gold/orange
      light: '#f4b942',
      dark: '#cc8f00',
      contrastText: '#000000',
    },
    secondary: {
      main: '#1f2326', // Dark gray for cards
      light: '#2d3136',
      dark: '#15181a',
    },
    background: {
      default: '#1a1a1a', // Main background
      paper: '#1f2326', // Card background
    },
    surface: {
      main: '#282c34',
    },
    text: {
      primary: '#ffffff',
      secondary: '#b3b3b3',
    },
    info: {
      main: '#17a2b8',
    },
    success: {
      main: '#28a745',
    },
    warning: {
      main: '#ffc107',
    },
    error: {
      main: '#dc3545',
    },
  },
  typography: {
    fontFamily: '"Open Sans", "Roboto", "Helvetica", "Arial", sans-serif',
    h1: {
      fontWeight: 300,
      fontSize: '2.5rem',
    },
    h2: {
      fontWeight: 300,
      fontSize: '2rem',
    },
    h3: {
      fontWeight: 400,
      fontSize: '1.75rem',
    },
    h4: {
      fontWeight: 400,
      fontSize: '1.5rem',
    },
    h5: {
      fontWeight: 500,
      fontSize: '1.25rem',
    },
    h6: {
      fontWeight: 500,
      fontSize: '1.1rem',
    },
    body1: {
      fontSize: '1rem',
      lineHeight: 1.5,
    },
    body2: {
      fontSize: '0.875rem',
      lineHeight: 1.43,
    },
    button: {
      textTransform: 'none',
      fontWeight: 500,
    },
  },
  shape: {
    borderRadius: 8,
  },
  components: {
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundColor: '#1f2326',
          border: '1px solid #2d3136',
          borderRadius: 8,
          transition: 'all 0.3s ease',
          '&:hover': {
            transform: 'translateY(-4px)',
            boxShadow: '0 8px 25px rgba(0, 0, 0, 0.3)',
            borderColor: '#e5a00d',
          },
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 6,
          textTransform: 'none',
          fontWeight: 500,
          padding: '8px 16px',
        },
        contained: {
          backgroundColor: '#e5a00d',
          color: '#000',
          '&:hover': {
            backgroundColor: '#cc8f00',
          },
        },
        outlined: {
          borderColor: '#e5a00d',
          color: '#e5a00d',
          '&:hover': {
            borderColor: '#f4b942',
            backgroundColor: 'rgba(229, 160, 13, 0.1)',
          },
        },
      },
    },
    MuiFab: {
      styleOverrides: {
        root: {
          backgroundColor: '#e5a00d',
          color: '#000',
          '&:hover': {
            backgroundColor: '#f4b942',
          },
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          backgroundColor: '#1a1a1a',
          borderBottom: '1px solid #2d3136',
          boxShadow: 'none',
        },
      },
    },
    MuiDialog: {
      styleOverrides: {
        paper: {
          backgroundColor: '#1f2326',
          border: '1px solid #2d3136',
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            '& fieldset': {
              borderColor: '#2d3136',
            },
            '&:hover fieldset': {
              borderColor: '#e5a00d',
            },
            '&.Mui-focused fieldset': {
              borderColor: '#e5a00d',
            },
          },
        },
      },
    },
  },
});

const App: React.FC = () => {
  return (
    <ThemeProvider theme={plexTheme}>
      <CssBaseline />
      <Router>
        <Box sx={{ 
          minHeight: '100vh', 
          bgcolor: 'background.default',
          color: 'text.primary'
        }}>
          <Routes>
            <Route path="/" element={<LibraryListScreen />} />
            <Route path="/library/:libraryId" element={<LibraryDetailsScreen />} />
            <Route path="/media/:mediaId" element={<MediaViewerScreen />} />
            <Route path="/edit/:mediaId" element={<MetadataEditorScreen />} />
            <Route path="/settings" element={<SettingsScreen />} />
          </Routes>
        </Box>
      </Router>
    </ThemeProvider>
  );
};

export default App;