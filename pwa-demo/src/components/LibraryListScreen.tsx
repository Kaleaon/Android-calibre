// React component equivalent to Android LibraryListScreen
import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  CardActions,
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
  Backdrop
} from '@mui/material';
import {
  Add as AddIcon,
  MoreVert as MoreVertIcon,
  Book as BookIcon,
  Movie as MovieIcon,
  MusicNote as MusicIcon,
  Description as DocumentIcon
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useAppStore } from '../store/app-store';
import { Library } from '../types';

const LibraryCard: React.FC<{
  library: Library;
  onClick: () => void;
}> = ({ library, onClick }) => {
  const getIcon = () => {
    switch (library.type) {
      case 'BOOK': return <BookIcon sx={{ fontSize: 40 }} />;
      case 'MOVIE': return <MovieIcon sx={{ fontSize: 40 }} />;
      case 'MUSIC': return <MusicIcon sx={{ fontSize: 40 }} />;
      default: return <DocumentIcon sx={{ fontSize: 40 }} />;
    }
  };

  return (
    <Card
      sx={{
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        cursor: 'pointer',
        '&:hover': {
          boxShadow: 6
        }
      }}
      onClick={onClick}
    >
      <CardContent sx={{ flexGrow: 1, textAlign: 'center', py: 3 }}>
        <Box sx={{ color: 'primary.main', mb: 2 }}>
          {getIcon()}
        </Box>
        <Typography variant="h6" component="h2" gutterBottom>
          {library.name}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {library.type.toLowerCase()} library
        </Typography>
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
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Add New Library</DialogTitle>
      <DialogContent>
        <TextField
          autoFocus
          margin="dense"
          label="Library Name"
          fullWidth
          variant="outlined"
          value={name}
          onChange={(e) => setName(e.target.value)}
          sx={{ mb: 2 }}
        />
        <TextField
          select
          margin="dense"
          label="Library Type"
          fullWidth
          variant="outlined"
          value={type}
          onChange={(e) => setType(e.target.value as Library['type'])}
          sx={{ mb: 2 }}
        >
          <MenuItem value="BOOK">Books</MenuItem>
          <MenuItem value="MOVIE">Movies</MenuItem>
          <MenuItem value="MUSIC">Music</MenuItem>
          <MenuItem value="PODCAST">Podcasts</MenuItem>
          <MenuItem value="MAGAZINE">Magazines</MenuItem>
          <MenuItem value="DOCUMENT">Documents</MenuItem>
        </TextField>
        <TextField
          margin="dense"
          label="Library Path (optional)"
          fullWidth
          variant="outlined"
          value={path}
          onChange={(e) => setPath(e.target.value)}
          placeholder="/path/to/library"
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button onClick={handleSubmit} variant="contained" disabled={!name.trim()}>
          Add
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
      <AppBar position="static" elevation={0}>
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            CleverFerret
          </Typography>
          <IconButton
            color="inherit"
            onClick={handleMenuOpen}
          >
            <MoreVertIcon />
          </IconButton>
          <Menu
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={handleMenuClose}
          >
            <MenuItem onClick={handleImportCalibre}>
              Import Calibre Library
            </MenuItem>
            <MenuItem onClick={handleMenuClose}>
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
          <Box textAlign="center" mt={4}>
            <Typography variant="h6" gutterBottom>
              No libraries found
            </Typography>
            <Typography variant="body2" color="text.secondary" mb={3}>
              Create your first library to get started
            </Typography>
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => setShowDialog(true)}
            >
              Add Library
            </Button>
          </Box>
        ) : (
          <Grid container spacing={3}>
            {libraries.map((library) => (
              <Grid item xs={12} sm={6} md={4} lg={3} key={library.libraryId}>
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
          bottom: 16,
          right: 16,
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