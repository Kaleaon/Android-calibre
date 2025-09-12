// Main React App component - equivalent to Android MainActivity
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { CssBaseline, Box } from '@mui/material';
import { LibraryListScreen } from './components/LibraryListScreen';
import { LibraryDetailsScreen } from './components/LibraryDetailsScreen';

// Material You inspired theme with enhanced visual hierarchy
const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#6750A4',
      light: '#EADDFF',
      dark: '#21005D',
      contrastText: '#FFFFFF',
    },
    secondary: {
      main: '#625B71',
      light: '#E8DEF8',
      dark: '#1D192B',
    },
    background: {
      default: '#FEFBFF',
      paper: '#FFFBFE',
    },
    surface: {
      main: '#FEF7FF',
    },
    text: {
      primary: '#1D1B20',
      secondary: '#49454F',
    },
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h4: {
      fontWeight: 600,
      letterSpacing: '0.25px',
    },
    h6: {
      fontWeight: 600,
      letterSpacing: '0.15px',
    },
    body1: {
      letterSpacing: '0.5px',
    },
    body2: {
      letterSpacing: '0.25px',
    },
    button: {
      textTransform: 'none',
      fontWeight: 600,
      letterSpacing: '0.1px',
    },
  },
  shape: {
    borderRadius: 12,
  },
  shadows: [
    'none',
    '0px 1px 2px rgba(0, 0, 0, 0.3), 0px 1px 3px 1px rgba(0, 0, 0, 0.15)',
    '0px 1px 2px rgba(0, 0, 0, 0.3), 0px 2px 6px 2px rgba(0, 0, 0, 0.15)',
    '0px 1px 3px rgba(0, 0, 0, 0.3), 0px 4px 8px 3px rgba(0, 0, 0, 0.15)',
    '0px 2px 3px rgba(0, 0, 0, 0.3), 0px 6px 10px 4px rgba(0, 0, 0, 0.15)',
    '0px 4px 4px rgba(0, 0, 0, 0.3), 0px 8px 12px 6px rgba(0, 0, 0, 0.15)',
    '0px 6px 10px rgba(103, 80, 164, 0.15), 0px 12px 24px rgba(103, 80, 164, 0.1)',
    '0px 6px 10px rgba(0, 0, 0, 0.3), 0px 20px 25px 5px rgba(0, 0, 0, 0.15)',
    '0px 8px 25px rgba(0, 0, 0, 0.15), 0px 16px 32px rgba(0, 0, 0, 0.1)',
    // Continue with standard Material-UI shadows for indices 9-24
    ...createTheme().shadows.slice(9),
  ],
  components: {
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 16,
          border: 'none',
          transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
        },
      },
    },
    MuiFab: {
      styleOverrides: {
        root: {
          borderRadius: 16,
          boxShadow: '0 8px 24px rgba(103, 80, 164, 0.25)',
          '&:hover': {
            boxShadow: '0 12px 32px rgba(103, 80, 164, 0.35)',
          },
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 20,
          textTransform: 'none',
          fontWeight: 600,
          padding: '8px 24px',
        },
        contained: {
          boxShadow: '0 4px 16px rgba(103, 80, 164, 0.25)',
          '&:hover': {
            boxShadow: '0 6px 20px rgba(103, 80, 164, 0.35)',
          },
        },
      },
    },
    MuiDialog: {
      styleOverrides: {
        paper: {
          borderRadius: 24,
          padding: '8px',
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          boxShadow: 'none',
          borderBottom: '1px solid rgba(0, 0, 0, 0.08)',
        },
      },
    },
  },
});

const App: React.FC = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
          <Routes>
            <Route path="/" element={<LibraryListScreen />} />
            <Route path="/library/:libraryId" element={<LibraryDetailsScreen />} />
            <Route path="/book/:bookId" element={
              <div style={{ padding: '20px', textAlign: 'center' }}>
                <h2>Book Reader</h2>
                <p>This would be the e-book reader component using HTML/CSS rendering</p>
                <p>Similar to Android's epub4j integration</p>
              </div>
            } />
            <Route path="/settings" element={
              <div style={{ padding: '20px', textAlign: 'center' }}>
                <h2>Settings</h2>
                <p>Settings screen with reading preferences, themes, etc.</p>
              </div>
            } />
          </Routes>
        </Box>
      </Router>
    </ThemeProvider>
  );
};

export default App;