// Settings screen for CleverFerret
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  AppBar,
  Toolbar,
  IconButton,
  Typography,
  Paper,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  ListItemSecondaryAction,
  Switch,
  Divider,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
  Alert,
  Card,
  CardContent,
} from '@mui/material';
import {
  ArrowBack as ArrowBackIcon,
  DarkMode as DarkModeIcon,
  Notifications as NotificationsIcon,
  CloudSync as CloudSyncIcon,
  Storage as StorageIcon,
  Security as SecurityIcon,
  Language as LanguageIcon,
  Download as DownloadIcon,
  Update as UpdateIcon,
  Info as InfoIcon,
  Settings as SettingsIcon,
  Api as ApiIcon,
  Backup as BackupIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';

export const SettingsScreen: React.FC = () => {
  const navigate = useNavigate();
  const [notifications, setNotifications] = useState(true);
  const [autoSync, setAutoSync] = useState(false);
  const [offlineMode, setOfflineMode] = useState(true);
  const [showApiDialog, setShowApiDialog] = useState(false);
  const [showAboutDialog, setShowAboutDialog] = useState(false);
  
  // API Keys state
  const [apiKeys, setApiKeys] = useState({
    googleBooks: '',
    tmdb: '',
    omdbApi: '',
    spotify: '',
    musicbrainz: '', // No key needed but keeping for consistency
  });

  const handleApiKeysSave = () => {
    // In a real app, this would save to secure storage
    console.log('Saving API keys:', apiKeys);
    setShowApiDialog(false);
  };

  const settingsSections = [
    {
      title: 'Appearance',
      items: [
        {
          icon: <DarkModeIcon />,
          primary: 'Dark Mode',
          secondary: 'Currently enabled (Plex-style theme)',
          action: <Switch checked={true} disabled />,
        },
        {
          icon: <LanguageIcon />,
          primary: 'Language',
          secondary: 'English (US)',
          action: <Button size="small">Change</Button>,
        },
      ],
    },
    {
      title: 'Library & Sync',
      items: [
        {
          icon: <NotificationsIcon />,
          primary: 'Notifications',
          secondary: 'Show import progress and updates',
          action: (
            <Switch
              checked={notifications}
              onChange={(e) => setNotifications(e.target.checked)}
            />
          ),
        },
        {
          icon: <CloudSyncIcon />,
          primary: 'Auto Sync',
          secondary: 'Automatically sync library changes',
          action: (
            <Switch
              checked={autoSync}
              onChange={(e) => setAutoSync(e.target.checked)}
            />
          ),
        },
        {
          icon: <DownloadIcon />,
          primary: 'Offline Mode',
          secondary: 'Download content for offline viewing',
          action: (
            <Switch
              checked={offlineMode}
              onChange={(e) => setOfflineMode(e.target.checked)}
            />
          ),
        },
      ],
    },
    {
      title: 'Data & Privacy',
      items: [
        {
          icon: <ApiIcon />,
          primary: 'Metadata APIs',
          secondary: 'Configure API keys for metadata fetching',
          action: (
            <Button size="small" onClick={() => setShowApiDialog(true)}>
              Configure
            </Button>
          ),
        },
        {
          icon: <StorageIcon />,
          primary: 'Storage',
          secondary: 'Manage local storage and cache',
          action: <Button size="small">Manage</Button>,
        },
        {
          icon: <BackupIcon />,
          primary: 'Backup & Restore',
          secondary: 'Export/import library data',
          action: <Button size="small">Backup</Button>,
        },
        {
          icon: <SecurityIcon />,
          primary: 'Privacy Settings',
          secondary: 'Control data sharing and analytics',
          action: <Button size="small">Review</Button>,
        },
      ],
    },
    {
      title: 'About',
      items: [
        {
          icon: <UpdateIcon />,
          primary: 'Check for Updates',
          secondary: 'Version 1.0.0 - PWA Demo',
          action: <Button size="small">Check</Button>,
        },
        {
          icon: <InfoIcon />,
          primary: 'About CleverFerret',
          secondary: 'Universal Media Library',
          action: (
            <Button size="small" onClick={() => setShowAboutDialog(true)}>
              Info
            </Button>
          ),
        },
      ],
    },
  ];

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
      {/* Header */}
      <AppBar position="static" elevation={0}>
        <Toolbar>
          <IconButton edge="start" color="inherit" onClick={() => navigate(-1)}>
            <ArrowBackIcon />
          </IconButton>
          <SettingsIcon sx={{ ml: 2, mr: 2 }} />
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            Settings
          </Typography>
        </Toolbar>
      </AppBar>

      {/* Settings Content */}
      <Box sx={{ p: 3, maxWidth: 800, mx: 'auto' }}>
        {settingsSections.map((section, sectionIndex) => (
          <Paper
            key={section.title}
            sx={{
              mb: 3,
              bgcolor: 'background.paper',
              border: '1px solid #2d3136',
            }}
          >
            <Box sx={{ p: 2, borderBottom: '1px solid #2d3136' }}>
              <Typography variant="h6" sx={{ fontWeight: 500 }}>
                {section.title}
              </Typography>
            </Box>
            <List disablePadding>
              {section.items.map((item, itemIndex) => (
                <React.Fragment key={itemIndex}>
                  <ListItem sx={{ py: 2 }}>
                    <ListItemIcon sx={{ color: 'primary.main' }}>
                      {item.icon}
                    </ListItemIcon>
                    <ListItemText
                      primary={item.primary}
                      secondary={item.secondary}
                      secondaryTypographyProps={{
                        color: 'text.secondary',
                        fontSize: '0.875rem',
                      }}
                    />
                    <ListItemSecondaryAction>
                      {item.action}
                    </ListItemSecondaryAction>
                  </ListItem>
                  {itemIndex < section.items.length - 1 && (
                    <Divider sx={{ borderColor: '#2d3136' }} />
                  )}
                </React.Fragment>
              ))}
            </List>
          </Paper>
        ))}

        {/* Danger Zone */}
        <Paper
          sx={{
            bgcolor: 'background.paper',
            border: '1px solid #d32f2f',
          }}
        >
          <Box sx={{ p: 2, borderBottom: '1px solid #d32f2f' }}>
            <Typography variant="h6" sx={{ fontWeight: 500, color: 'error.main' }}>
              Danger Zone
            </Typography>
          </Box>
          <Box sx={{ p: 3 }}>
            <Alert severity="warning" sx={{ mb: 2 }}>
              These actions are irreversible. Please be careful.
            </Alert>
            <Button
              variant="outlined"
              color="error"
              startIcon={<DeleteIcon />}
              sx={{ mr: 2, mb: 1 }}
            >
              Clear All Data
            </Button>
            <Button
              variant="outlined"
              color="error"
              startIcon={<DeleteIcon />}
              sx={{ mb: 1 }}
            >
              Reset Settings
            </Button>
          </Box>
        </Paper>
      </Box>

      {/* API Keys Dialog */}
      <Dialog
        open={showApiDialog}
        onClose={() => setShowApiDialog(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          Configure Metadata API Keys
        </DialogTitle>
        <DialogContent>
          <Typography variant="body2" color="text.secondary" paragraph>
            Configure API keys to enable automatic metadata fetching from various sources.
            All keys are stored locally and never shared.
          </Typography>

          <Card sx={{ mb: 3, bgcolor: 'secondary.main' }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Available APIs
              </Typography>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                <Chip label="Google Books API" color="primary" variant="outlined" />
                <Chip label="TMDB (Movies)" color="primary" variant="outlined" />
                <Chip label="OMDb API" color="primary" variant="outlined" />
                <Chip label="Spotify Web API" color="primary" variant="outlined" />
                <Chip label="MusicBrainz (Free)" color="success" />
                <Chip label="Open Library (Free)" color="success" />
              </Box>
            </CardContent>
          </Card>

          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            <TextField
              label="Google Books API Key"
              value={apiKeys.googleBooks}
              onChange={(e) => setApiKeys({ ...apiKeys, googleBooks: e.target.value })}
              placeholder="Enter your Google Books API key"
              helperText="Get your key from Google Cloud Console"
              fullWidth
            />
            <TextField
              label="TMDB API Key"
              value={apiKeys.tmdb}
              onChange={(e) => setApiKeys({ ...apiKeys, tmdb: e.target.value })}
              placeholder="Enter your TMDB API key"
              helperText="Get your key from themoviedb.org"
              fullWidth
            />
            <TextField
              label="OMDb API Key"
              value={apiKeys.omdbApi}
              onChange={(e) => setApiKeys({ ...apiKeys, omdbApi: e.target.value })}
              placeholder="Enter your OMDb API key"
              helperText="Get your key from omdbapi.com"
              fullWidth
            />
            <TextField
              label="Spotify Client ID"
              value={apiKeys.spotify}
              onChange={(e) => setApiKeys({ ...apiKeys, spotify: e.target.value })}
              placeholder="Enter your Spotify Client ID"
              helperText="Get your client ID from Spotify Developer Dashboard"
              fullWidth
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setShowApiDialog(false)}>
            Cancel
          </Button>
          <Button onClick={handleApiKeysSave} variant="contained">
            Save API Keys
          </Button>
        </DialogActions>
      </Dialog>

      {/* About Dialog */}
      <Dialog
        open={showAboutDialog}
        onClose={() => setShowAboutDialog(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>
          About CleverFerret
        </DialogTitle>
        <DialogContent>
          <Box sx={{ textAlign: 'center', mb: 3 }}>
            <Typography variant="h4" gutterBottom sx={{ fontWeight: 300 }}>
              CleverFerret
            </Typography>
            <Typography variant="h6" color="text.secondary" gutterBottom>
              Universal Media Library
            </Typography>
            <Chip label="PWA Demo v1.0.0" color="primary" />
          </Box>

          <Typography paragraph>
            CleverFerret is a comprehensive universal media library for managing your complete
            collection of books, movies, music, podcasts, and more, all in one place.
          </Typography>

          <Typography variant="h6" gutterBottom sx={{ mt: 3 }}>
            Features:
          </Typography>
          <List dense>
            <ListItem>
              <ListItemText primary="📚 Multi-format book reading (EPUB, PDF, etc.)" />
            </ListItem>
            <ListItem>
              <ListItemText primary="🎬 Video playback with advanced controls" />
            </ListItem>
            <ListItem>
              <ListItemText primary="🎵 Audio player with playlist support" />
            </ListItem>
            <ListItem>
              <ListItemText primary="🔍 Automatic metadata fetching from multiple APIs" />
            </ListItem>
            <ListItem>
              <ListItemText primary="📱 Progressive Web App (installable)" />
            </ListItem>
            <ListItem>
              <ListItemText primary="🌙 Plex-inspired dark theme" />
            </ListItem>
            <ListItem>
              <ListItemText primary="💾 Offline-first with local storage" />
            </ListItem>
          </List>

          <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
            This is a demonstration of the web version. The full Android app includes additional
            features like Calibre import, advanced reading tools, and more.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setShowAboutDialog(false)} variant="contained">
            Close
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};