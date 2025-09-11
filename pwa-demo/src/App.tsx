// Main React App component - equivalent to Android MainActivity
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { CssBaseline, Box } from '@mui/material';
import { LibraryListScreen } from './components/LibraryListScreen';
import { LibraryDetailsScreen } from './components/LibraryDetailsScreen';

// Material You inspired theme
const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: '#6750A4',
      light: '#EADDFF',
      dark: '#21005D',
    },
    secondary: {
      main: '#625B71',
      light: '#E8DEF8',
      dark: '#1D192B',
    },
    background: {
      default: '#FFFBFE',
      paper: '#FFFBFE',
    },
    surface: {
      main: '#FEF7FF',
    }
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
  },
  shape: {
    borderRadius: 12,
  },
  components: {
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          boxShadow: '0px 1px 3px rgba(0, 0, 0, 0.12), 0px 1px 2px rgba(0, 0, 0, 0.24)',
        },
      },
    },
    MuiFab: {
      styleOverrides: {
        root: {
          borderRadius: 16,
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