// Working Plex-inspired CleverFerret App
import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, useNavigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { 
  CssBaseline, 
  Box, 
  AppBar, 
  Toolbar, 
  Typography, 
  Card, 
  CardContent, 
  Grid, 
  Button,
  Container,
  Chip,
  Avatar,
  Fab
} from '@mui/material';
import {
  Add as AddIcon,
  Book as BookIcon,
  Movie as MovieIcon,
  MusicNote as MusicIcon,
  Collections as CollectionsIcon,
} from '@mui/icons-material';

// Plex-inspired dark theme
const plexTheme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#e5a00d', // Plex gold
      light: '#f4b942',
      dark: '#cc8f00',
      contrastText: '#000000',
    },
    background: {
      default: '#1a1a1a',
      paper: '#1f2326',
    },
    text: {
      primary: '#ffffff',
      secondary: '#b3b3b3',
    },
  },
  components: {
    MuiCard: {
      styleOverrides: {
        root: {
          backgroundColor: '#1f2326',
          border: '1px solid #2d3136',
          transition: 'all 0.3s ease',
          '&:hover': {
            transform: 'translateY(-4px)',
            borderColor: '#e5a00d',
            boxShadow: '0 8px 25px rgba(0, 0, 0, 0.3)',
          },
        },
      },
    },
  },
});

// Demo library data
const demoLibraries = [
  {
    id: 1,
    name: 'My Books',
    type: 'BOOK',
    itemCount: 284,
    icon: <BookIcon sx={{ fontSize: 40, color: 'white' }} />,
    gradient: 'linear-gradient(135deg, #2C5F2D 0%, #97BC62 100%)',
  },
  {
    id: 2,
    name: 'Movies & TV',
    type: 'MOVIE',
    itemCount: 156,
    icon: <MovieIcon sx={{ fontSize: 40, color: 'white' }} />,
    gradient: 'linear-gradient(135deg, #1565C0 0%, #42A5F5 100%)',
  },
  {
    id: 3,
    name: 'Music Library',
    type: 'MUSIC',
    itemCount: 1847,
    icon: <MusicIcon sx={{ fontSize: 40, color: 'white' }} />,
    gradient: 'linear-gradient(135deg, #7B1FA2 0%, #BA68C8 100%)',
  },
];

// Library card component
const LibraryCard: React.FC<{ library: any; onClick: () => void }> = ({ library, onClick }) => {
  return (
    <Card
      sx={{
        height: 280,
        cursor: 'pointer',
        position: 'relative',
        overflow: 'hidden',
      }}
      onClick={onClick}
    >
      <Box
        sx={{
          height: 160,
          background: library.gradient,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          position: 'relative',
        }}
      >
        {library.icon}
        <Chip
          label={`${library.itemCount} items`}
          size="small"
          sx={{
            position: 'absolute',
            top: 12,
            right: 12,
            bgcolor: 'rgba(0, 0, 0, 0.7)',
            color: 'white',
          }}
        />
      </Box>
      <CardContent sx={{ p: 2 }}>
        <Typography variant="h6" sx={{ fontWeight: 600, mb: 1 }}>
          {library.name}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {library.type.toLowerCase()} library
        </Typography>
      </CardContent>
    </Card>
  );
};

// Home screen component
const HomeScreen: React.FC = () => {
  const navigate = useNavigate();

  return (
    <Box sx={{ minHeight: '100vh' }}>
      <AppBar position="static" elevation={0}>
        <Toolbar>
          <Avatar
            sx={{
              bgcolor: 'primary.main',
              color: 'black',
              width: 40,
              height: 40,
              mr: 2,
              fontWeight: 700
            }}
          >
            CF
          </Avatar>
          <Typography variant="h5" sx={{ fontWeight: 300 }}>
            CleverFerret
          </Typography>
        </Toolbar>
      </AppBar>

      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 300, mb: 1 }}>
          Your Libraries
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
          {demoLibraries.length} libraries
        </Typography>

        <Grid container spacing={3}>
          {demoLibraries.map((library) => (
            <Grid item xs={12} sm={6} md={4} key={library.id}>
              <LibraryCard
                library={library}
                onClick={() => navigate(`/library/${library.id}`)}
              />
            </Grid>
          ))}
        </Grid>
      </Container>

      <Fab
        sx={{
          position: 'fixed',
          bottom: 24,
          right: 24,
          bgcolor: 'primary.main',
          color: 'black',
        }}
        onClick={() => alert('Add library functionality')}
      >
        <AddIcon />
      </Fab>
    </Box>
  );
};

// Media item component with realistic data
const MediaItem: React.FC<{ item: any; onClick: () => void }> = ({ item, onClick }) => {
  const [menuAnchor, setMenuAnchor] = useState<null | HTMLElement>(null);

  return (
    <Card 
      sx={{ 
        height: 350,
        cursor: 'pointer',
        position: 'relative',
        '&:hover .media-overlay': {
          opacity: 1,
        },
      }}
      onClick={onClick}
    >
      <Box
        sx={{
          height: 220,
          bgcolor: 'secondary.main',
          backgroundImage: item.poster ? `url(${item.poster})` : 'none',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          position: 'relative',
        }}
      >
        {!item.poster && <CollectionsIcon sx={{ fontSize: 50, color: 'primary.main' }} />}
        
        {/* Rating badge */}
        <Box
          sx={{
            position: 'absolute',
            top: 8,
            right: 8,
            bgcolor: 'rgba(0, 0, 0, 0.8)',
            borderRadius: 1,
            px: 1,
            py: 0.5,
          }}
        >
          <Typography variant="caption" sx={{ color: 'primary.main', fontWeight: 600 }}>
            ⭐ {item.rating}
          </Typography>
        </Box>

        {/* Hover overlay */}
        <Box
          className="media-overlay"
          sx={{
            position: 'absolute',
            inset: 0,
            bgcolor: 'rgba(0, 0, 0, 0.7)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            opacity: 0,
            transition: 'opacity 0.3s ease',
          }}
        >
          <Button
            variant="contained"
            sx={{
              bgcolor: 'primary.main',
              color: 'black',
              fontWeight: 600,
            }}
          >
            {item.type === 'BOOK' ? '📖 Read' : item.type === 'MOVIE' ? '▶️ Watch' : '🎵 Play'}
          </Button>
        </Box>
      </Box>

      <CardContent sx={{ p: 2 }}>
        <Typography variant="h6" sx={{ fontSize: '0.95rem', fontWeight: 600, mb: 1 }}>
          {item.title}
        </Typography>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
          {item.author} • {item.year}
        </Typography>
        <Chip 
          label={item.genre} 
          size="small" 
          sx={{ 
            fontSize: '0.7rem',
            bgcolor: 'rgba(229, 160, 13, 0.2)',
            color: 'primary.main',
          }} 
        />
      </CardContent>
    </Card>
  );
};

// Library details component with realistic media
const LibraryScreen: React.FC = () => {
  const navigate = useNavigate();

  // Generate realistic demo media items
  const demoItems = [
    {
      id: 1,
      title: 'The Digital Frontier',
      author: 'Sarah Chen',
      year: 2024,
      rating: 4.5,
      genre: 'Sci-Fi',
      type: 'BOOK',
      poster: 'https://via.placeholder.com/300x450/2C5F2D/ffffff?text=📚+Digital+Frontier',
    },
    {
      id: 2,
      title: 'Cyber Dreams',
      author: 'Michael Rodriguez',
      year: 2023,
      rating: 4.8,
      genre: 'Thriller',
      type: 'MOVIE',
      poster: 'https://via.placeholder.com/300x450/1565C0/ffffff?text=🎬+Cyber+Dreams',
    },
    {
      id: 3,
      title: 'Neon Nights',
      author: 'Synthwave Collective',
      year: 2024,
      rating: 4.2,
      genre: 'Electronic',
      type: 'MUSIC',
      poster: 'https://via.placeholder.com/300x450/7B1FA2/ffffff?text=🎵+Neon+Nights',
    },
    {
      id: 4,
      title: 'AI Revolution',
      author: 'Dr. Emily Zhang',
      year: 2024,
      rating: 4.6,
      genre: 'Non-Fiction',
      type: 'BOOK',
      poster: 'https://via.placeholder.com/300x450/2C5F2D/ffffff?text=📚+AI+Revolution',
    },
    {
      id: 5,
      title: 'Matrix Reborn',
      author: 'James Cameron',
      year: 2025,
      rating: 4.9,
      genre: 'Action',
      type: 'MOVIE',
      poster: 'https://via.placeholder.com/300x450/1565C0/ffffff?text=🎬+Matrix+Reborn',
    },
    {
      id: 6,
      title: 'Future Bass',
      author: 'Digital Dreams',
      year: 2024,
      rating: 4.3,
      genre: 'EDM',
      type: 'MUSIC',
      poster: 'https://via.placeholder.com/300x450/7B1FA2/ffffff?text=🎵+Future+Bass',
    },
  ];

  return (
    <Box sx={{ minHeight: '100vh' }}>
      <AppBar position="static" elevation={0}>
        <Toolbar>
          <Button color="inherit" onClick={() => navigate('/')}>
            ← Back
          </Button>
          <Typography variant="h6" sx={{ ml: 2 }}>
            Library Contents
          </Typography>
        </Toolbar>
      </AppBar>

      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 300, mb: 1 }}>
          Demo Library
        </Typography>
        <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
          {demoItems.length} items • Mixed media
        </Typography>
        
        <Grid container spacing={3}>
          {demoItems.map((item) => (
            <Grid item xs={12} sm={6} md={4} lg={3} key={item.id}>
              <MediaItem
                item={item}
                onClick={() => alert(`Opening ${item.title} - ${item.type} viewer would load here`)}
              />
            </Grid>
          ))}
        </Grid>
      </Container>
    </Box>
  );
};

// Main App component
const App: React.FC = () => {
  return (
    <ThemeProvider theme={plexTheme}>
      <CssBaseline />
      <Router>
        <Routes>
          <Route path="/" element={<HomeScreen />} />
          <Route path="/library/:id" element={<LibraryScreen />} />
        </Routes>
      </Router>
    </ThemeProvider>
  );
};

export default App;