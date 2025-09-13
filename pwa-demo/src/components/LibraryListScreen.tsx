// Plex-inspired Library List Screen
import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  Fab,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  AppBar,
  Toolbar,
  IconButton,
  Menu,
  MenuItem,
  CircularProgress,
  Backdrop,
  Paper,
  Avatar,
  Chip,
} from '@mui/material';
import {
  Add as AddIcon,
  MoreVert as MoreVertIcon,
  Book as BookIcon,
  Movie as MovieIcon,
  MusicNote as MusicIcon,
  Description as DocumentIcon,
  Podcast as PodcastIcon,
  Article as MagazineIcon,
  VideoLibrary as VideoIcon,
  LibraryMusic as LibraryMusicIcon,
  Collections as CollectionsIcon,
  Settings as SettingsIcon,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAppStore } from '../store/app-store';
import { Library } from '../types';

// Plex-inspired library card component
const LibraryCard: React.FC<{
  library: Library;
  onClick: () => void;
}> = ({ library, onClick }) => {
  // Mock data for demonstration
  const mockStats = {
    itemCount: Math.floor(Math.random() * 500) + 10,
    lastSync: new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000),
    recentlyAdded: Math.floor(Math.random() * 20),
    isActive: Math.random() > 0.3
  };

  const getIcon = () => {
    switch (library.type) {
      case 'BOOK': return <BookIcon sx={{ fontSize: 40, color: '#ffffff' }} />;
      case 'MOVIE': return <VideoIcon sx={{ fontSize: 40, color: '#ffffff' }} />;
      case 'MUSIC': return <LibraryMusicIcon sx={{ fontSize: 40, color: '#ffffff' }} />;
      case 'PODCAST': return <PodcastIcon sx={{ fontSize: 40, color: '#ffffff' }} />;
      case 'MAGAZINE': return <MagazineIcon sx={{ fontSize: 40, color: '#ffffff' }} />;
      default: return <CollectionsIcon sx={{ fontSize: 40, color: '#ffffff' }} />;
    }
  };

  const getBackgroundColor = () => {
    switch (library.type) {
      case 'BOOK': return 'linear-gradient(135deg, #2C5F2D 0%, #97BC62 100%)';
      case 'MOVIE': return 'linear-gradient(135deg, #1565C0 0%, #42A5F5 100%)';
      case 'MUSIC': return 'linear-gradient(135deg, #7B1FA2 0%, #BA68C8 100%)';
      case 'PODCAST': return 'linear-gradient(135deg, #EF6C00 0%, #FFB74D 100%)';
      case 'MAGAZINE': return 'linear-gradient(135deg, #D32F2F 0%, #F48FB1 100%)';
      default: return 'linear-gradient(135deg, #455A64 0%, #90A4AE 100%)';
    }
  };

  const formatLastSync = (date: Date) => {
    const now = new Date();
    const diffTime = Math.abs(now.getTime() - date.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays === 1) return 'Updated today';
    if (diffDays <= 7) return `Updated ${diffDays} days ago`;
    return `Updated ${date.toLocaleDateString()}`;
  };

  return (
    <Card
      sx={{
        height: 280,
        cursor: 'pointer',
        position: 'relative',
        overflow: 'hidden',
        transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
        bgcolor: 'secondary.main',
        border: '1px solid #2d3136',
        '&:hover': {
          transform: 'translateY(-8px) scale(1.02)',
          boxShadow: '0 12px 40px rgba(0, 0, 0, 0.4)',
          borderColor: 'primary.main',
          '& .library-overlay': {
            opacity: 1,
          }
        }
      }}
      onClick={onClick}
    >
      {/* Background Header */}
      <Box
        sx={{
          height: 160,
          background: getBackgroundColor(),
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          position: 'relative',
          '&::after': {
            content: '""',
            position: 'absolute',
            bottom: 0,
            left: 0,
            right: 0,
            height: '40px',
            background: 'linear-gradient(transparent, rgba(31, 35, 38, 0.8))',
          }
        }}
      >
        {getIcon()}
        
        {/* Status indicator */}
        {mockStats.isActive && (
          <Box
            sx={{
              position: 'absolute',
              top: 12,
              right: 12,
              width: 12,
              height: 12,
              borderRadius: '50%',
              bgcolor: 'primary.main',
              boxShadow: '0 0 0 3px rgba(229, 160, 13, 0.3)',
            }}
          />
        )}

        {/* Recently added badge */}
        {mockStats.recentlyAdded > 0 && (
          <Chip
            label={`+${mockStats.recentlyAdded}`}
            size="small"
            sx={{
              position: 'absolute',
              top: 12,
              left: 12,
              bgcolor: 'primary.main',
              color: 'black',
              fontWeight: 600,
              fontSize: '0.75rem',
            }}
          />
        )}

        {/* Hover overlay */}
        <Box
          className="library-overlay"
          sx={{
            position: 'absolute',
            inset: 0,
            bgcolor: 'rgba(0, 0, 0, 0.4)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            opacity: 0,
            transition: 'opacity 0.3s ease',
          }}
        >
          <Typography
            variant="h6"
            sx={{
              color: 'white',
              fontWeight: 600,
              textShadow: '0 2px 8px rgba(0, 0, 0, 0.6)',
            }}
          >
            Open Library
          </Typography>
        </Box>
      </Box>

      {/* Content */}
      <CardContent sx={{ p: 2, height: 120, display: 'flex', flexDirection: 'column' }}>
        <Typography 
          variant="h6" 
          sx={{ 
            fontWeight: 600,
            fontSize: '1.1rem',
            mb: 0.5,
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap'
          }}
        >
          {library.name}
        </Typography>
        
        <Typography 
          variant="body2" 
          color="text.secondary"
          sx={{ 
            mb: 1.5, 
            textTransform: 'capitalize',
            fontSize: '0.875rem'
          }}
        >
          {library.type.toLowerCase()} library
        </Typography>

        {/* Stats */}
        <Box sx={{ mt: 'auto' }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
            <Typography 
              variant="body2" 
              sx={{ 
                fontWeight: 600,
                color: 'primary.main',
                fontSize: '0.875rem'
              }}
            >
              {mockStats.itemCount.toLocaleString()} items
            </Typography>
            <Typography 
              variant="caption" 
              color="text.secondary"
              sx={{ fontSize: '0.75rem' }}
            >
              {mockStats.recentlyAdded} new
            </Typography>
          </Box>
          
          <Typography 
            variant="caption" 
            color="text.secondary"
            sx={{ fontSize: '0.7rem', opacity: 0.8 }}
          >
            {formatLastSync(mockStats.lastSync)}
          </Typography>
        </Box>
      </CardContent>
    </Card>
  );
};

const AddLibraryDialog: React.FC<{
  open: boolean;
  onClose: () => void;
  onAdd: (name: string, type: Library['type'], path: string) => void;
}> = ({ open, onClose, onAdd }) => {
  const [name, setName] = useState('');
  const [type, setType] = useState<Library['type']>('BOOK');
  const [path, setPath] = useState('');

  const handleSubmit = () => {
    if (name.trim()) {
      onAdd(name.trim(), type, path.trim() || '/default');
      setName('');
      setPath('');
      onClose();
    }
  };

  return (
    <Dialog 
      open={open} 
      onClose={onClose} 
      maxWidth="sm" 
      fullWidth
      PaperProps={{
        sx: {
          borderRadius: 3,
          boxShadow: '0 24px 48px rgba(0, 0, 0, 0.15)'
        }
      }}
    >
      <DialogTitle 
        sx={{
          pb: 1,
          fontSize: '1.25rem',
          fontWeight: 600
        }}
      >
        Add New Library
      </DialogTitle>
      <DialogContent sx={{ pb: 2 }}>
        <TextField
          autoFocus
          margin="normal"
          label="Library Name"
          fullWidth
          variant="outlined"
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="e.g., My Books, Audiobooks, Comics"
          sx={{ 
            mb: 3,
            '& .MuiOutlinedInput-root': {
              borderRadius: 2,
            },
          }}
        />
        <TextField
          select
          margin="normal"
          label="Library Type"
          fullWidth
          variant="outlined"
          value={type}
          onChange={(e) => setType(e.target.value as Library['type'])}
          sx={{ 
            mb: 3,
            '& .MuiOutlinedInput-root': {
              borderRadius: 2,
            },
          }}
        >
          <MenuItem value="BOOK">📚 Books</MenuItem>
          <MenuItem value="MOVIE">🎬 Movies</MenuItem>
          <MenuItem value="MUSIC">🎵 Music</MenuItem>
          <MenuItem value="PODCAST">🎙️ Podcasts</MenuItem>
          <MenuItem value="MAGAZINE">📰 Magazines</MenuItem>
          <MenuItem value="DOCUMENT">📄 Documents</MenuItem>
        </TextField>
        <TextField
          margin="normal"
          label="Library Path (optional)"
          fullWidth
          variant="outlined"
          value={path}
          onChange={(e) => setPath(e.target.value)}
          placeholder="/path/to/library"
          helperText="Optional path to your media files"
          sx={{ 
            '& .MuiOutlinedInput-root': {
              borderRadius: 2,
            },
          }}
        />
      </DialogContent>
      <DialogActions sx={{ p: 3, pt: 1 }}>
        <Button 
          onClick={onClose}
          sx={{
            textTransform: 'none',
            fontWeight: 600,
            borderRadius: 2,
            px: 3
          }}
        >
          Cancel
        </Button>
        <Button 
          onClick={handleSubmit} 
          variant="contained" 
          disabled={!name.trim()}
          sx={{
            textTransform: 'none',
            fontWeight: 600,
            borderRadius: 2,
            px: 3,
            background: 'linear-gradient(135deg, #6750A4 0%, #7C4DFF 100%)',
            '&:hover': {
              background: 'linear-gradient(135deg, #5A47A0 0%, #7043F5 100%)',
            },
            '&:disabled': {
              background: 'rgba(0, 0, 0, 0.12)',
            }
          }}
        >
          Create Library
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export const LibraryListScreen: React.FC = () => {
  const navigate = useNavigate();
  const { 
    libraries, 
    isLoading, 
    importStatus, 
    loadLibraries, 
    addLibrary 
  } = useAppStore();
  
  const [showDialog, setShowDialog] = useState(false);
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);

  useEffect(() => {
    loadLibraries();
  }, [loadLibraries]);

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const handleImportCalibre = () => {
    // Simulate Calibre import process
    // In real implementation, this would trigger file selection
    // and process metadata.db file
    alert('Import Calibre Library functionality would be implemented here');
    handleMenuClose();
  };

  const handleAddLibrary = (name: string, type: Library['type'], path: string) => {
    addLibrary(name, type, path);
  };

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar 
        position="static" 
        elevation={0}
        sx={{
          background: 'linear-gradient(135deg, #6750A4 0%, #7C4DFF 100%)',
          '& .MuiToolbar-root': {
            minHeight: { xs: 56, sm: 64 }
          }
        }}
      >
        <Toolbar>
          <Typography 
            variant="h6" 
            component="div" 
            sx={{ 
              flexGrow: 1,
              fontWeight: 700,
              fontSize: { xs: '1.2rem', sm: '1.3rem' },
              letterSpacing: 0.5
            }}
          >
            CleverFerret
          </Typography>
          <IconButton
            color="inherit"
            onClick={handleMenuOpen}
            sx={{
              '&:hover': {
                bgcolor: 'rgba(255, 255, 255, 0.1)'
              }
            }}
          >
            <MoreVertIcon />
          </IconButton>
          <Menu
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={handleMenuClose}
            PaperProps={{
              sx: {
                borderRadius: 2,
                mt: 1,
                minWidth: 200,
                boxShadow: '0 8px 32px rgba(0, 0, 0, 0.15)'
              }
            }}
          >
            <MenuItem 
              onClick={handleImportCalibre}
              sx={{ 
                py: 1.5,
                '&:hover': {
                  bgcolor: 'primary.light',
                  color: 'primary.contrastText'
                }
              }}
            >
              Import Calibre Library
            </MenuItem>
            <MenuItem 
              onClick={handleMenuClose}
              sx={{ 
                py: 1.5,
                '&:hover': {
                  bgcolor: 'primary.light',
                  color: 'primary.contrastText'
                }
              }}
            >
              Settings
            </MenuItem>
          </Menu>
        </Toolbar>
      </AppBar>

      <Box sx={{ p: 3 }}>
        {isLoading ? (
          <Box display="flex" justifyContent="center" mt={4}>
            <CircularProgress />
          </Box>
        ) : libraries.length === 0 ? (
          <Box 
            textAlign="center" 
            mt={6}
            sx={{
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              maxWidth: 400,
              mx: 'auto',
              px: 3
            }}
          >
            <Box
              sx={{
                width: 120,
                height: 120,
                borderRadius: '50%',
                background: 'linear-gradient(135deg, #6750A4 0%, #7C4DFF 100%)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                mb: 3,
                boxShadow: '0 8px 32px rgba(103, 80, 164, 0.3)'
              }}
            >
              <BookIcon sx={{ fontSize: 60, color: 'white' }} />
            </Box>
            
            <Typography 
              variant="h4" 
              gutterBottom
              sx={{ 
                fontWeight: 600,
                color: 'text.primary',
                mb: 1
              }}
            >
              Welcome to CleverFerret
            </Typography>
            
            <Typography 
              variant="body1" 
              color="text.secondary" 
              mb={4}
              sx={{ 
                lineHeight: 1.6,
                fontSize: '1.1rem'
              }}
            >
              Your personal universal media library. Create your first library to start organizing your books, movies, music, and more.
            </Typography>
            
            <Button
              variant="contained"
              size="large"
              startIcon={<AddIcon />}
              onClick={() => setShowDialog(true)}
              sx={{
                borderRadius: 3,
                px: 4,
                py: 1.5,
                fontSize: '1rem',
                fontWeight: 600,
                background: 'linear-gradient(135deg, #6750A4 0%, #7C4DFF 100%)',
                boxShadow: '0 4px 16px rgba(103, 80, 164, 0.3)',
                '&:hover': {
                  background: 'linear-gradient(135deg, #5A47A0 0%, #7043F5 100%)',
                  transform: 'translateY(-2px)',
                  boxShadow: '0 6px 20px rgba(103, 80, 164, 0.4)',
                }
              }}
            >
              Create Your First Library
            </Button>
            
            <Typography 
              variant="caption" 
              color="text.secondary" 
              mt={3}
              sx={{ opacity: 0.7 }}
            >
              You can also import an existing Calibre library from the menu above
            </Typography>
          </Box>
        ) : (
          <Grid container spacing={3} sx={{ pb: 10 }}>
            {libraries.map((library) => (
              <Grid 
                item 
                xs={12} 
                sm={6} 
                md={4} 
                lg={3} 
                xl={2.4}
                key={library.libraryId}
              >
                <LibraryCard
                  library={library}
                  onClick={() => navigate(`/library/${library.libraryId}`)}
                />
              </Grid>
            ))}
          </Grid>
        )}
      </Box>

      <Fab
        color="primary"
        aria-label="add"
        sx={{
          position: 'fixed',
          bottom: 24,
          right: 24,
          background: 'linear-gradient(135deg, #6750A4 0%, #7C4DFF 100%)',
          boxShadow: '0 8px 24px rgba(103, 80, 164, 0.3)',
          '&:hover': {
            background: 'linear-gradient(135deg, #5A47A0 0%, #7043F5 100%)',
            transform: 'scale(1.05)',
            boxShadow: '0 12px 32px rgba(103, 80, 164, 0.4)',
          },
          transition: 'all 0.3s ease-in-out'
        }}
        onClick={() => setShowDialog(true)}
      >
        <AddIcon />
      </Fab>

      <AddLibraryDialog
        open={showDialog}
        onClose={() => setShowDialog(false)}
        onAdd={handleAddLibrary}
      />

      {/* Import status overlay - equivalent to Android import overlay */}
      <Backdrop
        sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
        open={importStatus.isImporting}
      >
        <Card sx={{ p: 3, textAlign: 'center' }}>
          <CircularProgress color="inherit" sx={{ mb: 2 }} />
          <Typography variant="h6">
            {importStatus.status}
          </Typography>
        </Card>
      </Backdrop>
    </Box>
  );
};