// Media viewer for different content types
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  AppBar,
  Toolbar,
  IconButton,
  Typography,
  Paper,
  CircularProgress,
  Alert,
  Fab,
  Menu,
  MenuItem,
} from '@mui/material';
import {
  ArrowBack as ArrowBackIcon,
  Edit as EditIcon,
  MoreVert as MoreVertIcon,
  PlayArrow as PlayIcon,
  Pause as PauseIcon,
  VolumeUp as VolumeIcon,
  Fullscreen as FullscreenIcon,
} from '@mui/icons-material';
import { MediaItem, MetadataCommon } from '../types';
import { MediaItemService, MetadataService } from '../services/database';

interface MediaViewerProps {
  mediaItem?: MediaItem;
  metadata?: MetadataCommon;
}

const EBookViewer: React.FC<MediaViewerProps> = ({ mediaItem, metadata }) => {
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages] = useState(250); // Demo value
  const [fontSize, setFontSize] = useState(16);

  const demoContent = `
    <h1>${metadata?.title || 'Sample Book'}</h1>
    <p>This is a demonstration of the e-book reader component. In a production environment, this would render actual EPUB content using a library like epub.js or similar.</p>
    
    <p>The reader would support:</p>
    <ul>
      <li>EPUB 2 and EPUB 3 formats</li>
      <li>Text reflow and pagination</li>
      <li>Font size adjustment</li>
      <li>Night/day mode themes</li>
      <li>Bookmarks and annotations</li>
      <li>Search functionality</li>
      <li>Table of contents navigation</li>
    </ul>
    
    <p>Users could navigate through pages using touch gestures, keyboard arrows, or the navigation controls.</p>
    
    <h2>Chapter 1: Getting Started</h2>
    <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</p>
    
    <p>Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
  `;

  return (
    <Box sx={{ 
      bgcolor: 'background.paper', 
      minHeight: '100vh',
      display: 'flex',
      flexDirection: 'column'
    }}>
      {/* Reader Controls */}
      <Box sx={{ 
        p: 2, 
        borderBottom: '1px solid #2d3136',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
      }}>
        <Typography variant="body2" color="text.secondary">
          Page {currentPage} of {totalPages}
        </Typography>
        <Box>
          <IconButton onClick={() => setFontSize(Math.max(14, fontSize - 2))}>
            <Typography variant="h6">A-</Typography>
          </IconButton>
          <IconButton onClick={() => setFontSize(Math.min(24, fontSize + 2))}>
            <Typography variant="h5">A+</Typography>
          </IconButton>
        </Box>
      </Box>

      {/* Content Area */}
      <Box sx={{ 
        flex: 1, 
        p: 4,
        maxWidth: '800px',
        mx: 'auto',
        '& h1': { color: 'primary.main', mb: 3 },
        '& h2': { color: 'primary.main', mt: 4, mb: 2 },
        '& p': { mb: 2, lineHeight: 1.6, fontSize: `${fontSize}px` },
        '& ul': { pl: 3, mb: 2 },
        '& li': { mb: 1, fontSize: `${fontSize}px` }
      }}>
        <div dangerouslySetInnerHTML={{ __html: demoContent }} />
      </Box>

      {/* Navigation */}
      <Box sx={{ 
        p: 2, 
        borderTop: '1px solid #2d3136',
        display: 'flex',
        justifyContent: 'space-between'
      }}>
        <IconButton 
          disabled={currentPage <= 1}
          onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
        >
          ← Previous
        </IconButton>
        <IconButton 
          disabled={currentPage >= totalPages}
          onClick={() => setCurrentPage(Math.min(totalPages, currentPage + 1))}
        >
          Next →
        </IconButton>
      </Box>
    </Box>
  );
};

const VideoPlayer: React.FC<MediaViewerProps> = ({ mediaItem, metadata }) => {
  const [isPlaying, setIsPlaying] = useState(false);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration] = useState(7200); // 2 hours demo

  const formatTime = (seconds: number) => {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = Math.floor(seconds % 60);
    return `${hours}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <Box sx={{ 
      bgcolor: '#000', 
      minHeight: '100vh',
      display: 'flex',
      flexDirection: 'column',
      position: 'relative'
    }}>
      {/* Video Area */}
      <Box sx={{ 
        flex: 1, 
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(45deg, #1a1a1a 25%, transparent 25%), linear-gradient(-45deg, #1a1a1a 25%, transparent 25%), linear-gradient(45deg, transparent 75%, #1a1a1a 75%), linear-gradient(-45deg, transparent 75%, #1a1a1a 75%)',
        backgroundSize: '20px 20px',
        backgroundPosition: '0 0, 0 10px, 10px -10px, -10px 0px'
      }}>
        <Box sx={{ 
          textAlign: 'center',
          color: 'white',
          p: 4
        }}>
          <Typography variant="h4" gutterBottom>
            {metadata?.title || 'Video Player'}
          </Typography>
          <Typography variant="body1" color="grey.400" mb={4}>
            Demo video player. In production, this would use a video.js player or similar component to handle various video formats (MP4, WebM, etc.)
          </Typography>
          
          <IconButton 
            sx={{ 
              bgcolor: 'primary.main',
              color: 'black',
              width: 80,
              height: 80,
              '&:hover': { bgcolor: 'primary.light' }
            }}
            onClick={() => setIsPlaying(!isPlaying)}
          >
            {isPlaying ? <PauseIcon sx={{ fontSize: 40 }} /> : <PlayIcon sx={{ fontSize: 40 }} />}
          </IconButton>
        </Box>
      </Box>

      {/* Video Controls */}
      <Box sx={{ 
        bgcolor: 'rgba(0,0,0,0.8)',
        p: 2,
        display: 'flex',
        alignItems: 'center',
        gap: 2
      }}>
        <IconButton onClick={() => setIsPlaying(!isPlaying)} sx={{ color: 'white' }}>
          {isPlaying ? <PauseIcon /> : <PlayIcon />}
        </IconButton>
        
        <Typography variant="body2" color="white" sx={{ minWidth: '80px' }}>
          {formatTime(currentTime)}
        </Typography>
        
        <Box sx={{ 
          flex: 1, 
          height: 4, 
          bgcolor: 'grey.700', 
          borderRadius: 2,
          position: 'relative',
          cursor: 'pointer'
        }}>
          <Box sx={{
            position: 'absolute',
            left: 0,
            top: 0,
            height: '100%',
            bgcolor: 'primary.main',
            borderRadius: 2,
            width: `${(currentTime / duration) * 100}%`
          }} />
        </Box>
        
        <Typography variant="body2" color="white" sx={{ minWidth: '80px' }}>
          {formatTime(duration)}
        </Typography>
        
        <IconButton sx={{ color: 'white' }}>
          <VolumeIcon />
        </IconButton>
        
        <IconButton sx={{ color: 'white' }}>
          <FullscreenIcon />
        </IconButton>
      </Box>
    </Box>
  );
};

const AudioPlayer: React.FC<MediaViewerProps> = ({ mediaItem, metadata }) => {
  const [isPlaying, setIsPlaying] = useState(false);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration] = useState(180); // 3 minutes demo

  return (
    <Box sx={{ 
      bgcolor: 'background.default',
      minHeight: '100vh',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      p: 4
    }}>
      {/* Album Art */}
      <Paper sx={{ 
        width: 300,
        height: 300,
        mb: 4,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        bgcolor: 'secondary.main',
        backgroundImage: metadata?.thumbnailPath ? `url(${metadata.thumbnailPath})` : 'none',
        backgroundSize: 'cover',
        backgroundPosition: 'center'
      }}>
        {!metadata?.thumbnailPath && (
          <VolumeIcon sx={{ fontSize: 80, color: 'primary.main' }} />
        )}
      </Paper>

      {/* Track Info */}
      <Typography variant="h4" gutterBottom align="center">
        {metadata?.title || 'Audio Track'}
      </Typography>
      <Typography variant="h6" color="text.secondary" gutterBottom align="center">
        Unknown Artist
      </Typography>

      {/* Audio Controls */}
      <Box sx={{ 
        width: '100%',
        maxWidth: 400,
        mt: 4
      }}>
        <Box sx={{ 
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          mb: 2
        }}>
          <IconButton 
            sx={{ 
              bgcolor: 'primary.main',
              color: 'black',
              width: 60,
              height: 60,
              '&:hover': { bgcolor: 'primary.light' }
            }}
            onClick={() => setIsPlaying(!isPlaying)}
          >
            {isPlaying ? <PauseIcon sx={{ fontSize: 30 }} /> : <PlayIcon sx={{ fontSize: 30 }} />}
          </IconButton>
        </Box>

        <Box sx={{ 
          display: 'flex',
          alignItems: 'center',
          gap: 2
        }}>
          <Typography variant="caption">
            {Math.floor(currentTime / 60)}:{(currentTime % 60).toString().padStart(2, '0')}
          </Typography>
          
          <Box sx={{ 
            flex: 1, 
            height: 4, 
            bgcolor: 'secondary.main', 
            borderRadius: 2,
            position: 'relative',
            cursor: 'pointer'
          }}>
            <Box sx={{
              position: 'absolute',
              left: 0,
              top: 0,
              height: '100%',
              bgcolor: 'primary.main',
              borderRadius: 2,
              width: `${(currentTime / duration) * 100}%`
            }} />
          </Box>
          
          <Typography variant="caption">
            {Math.floor(duration / 60)}:{(duration % 60).toString().padStart(2, '0')}
          </Typography>
        </Box>
      </Box>
    </Box>
  );
};

export const MediaViewerScreen: React.FC = () => {
  const { mediaId } = useParams<{ mediaId: string }>();
  const navigate = useNavigate();
  const [mediaItem, setMediaItem] = useState<MediaItem | null>(null);
  const [metadata, setMetadata] = useState<MetadataCommon | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [menuAnchor, setMenuAnchor] = useState<null | HTMLElement>(null);

  useEffect(() => {
    loadMediaItem();
  }, [mediaId]);

  const loadMediaItem = async () => {
    if (!mediaId) {
      setError('No media ID provided');
      setLoading(false);
      return;
    }

    try {
      // For demo purposes, create mock data
      const mockMediaItem: MediaItem = {
        itemId: parseInt(mediaId),
        libraryId: 1,
        filePath: `/demo/${mediaId}`,
        fileName: `Demo File ${mediaId}`,
        fileSize: 1024000,
        mimeType: 'application/epub+zip',
        mediaType: 'BOOK',
        dateAdded: new Date(),
        lastModified: new Date(),
      };

      const mockMetadata: MetadataCommon = {
        itemId: parseInt(mediaId),
        title: `Demo ${mockMediaItem.mediaType} ${mediaId}`,
        description: 'This is a demonstration of the media viewer functionality.',
        rating: 4.5,
        isFavorite: false,
        isDownloaded: true,
      };

      setMediaItem(mockMediaItem);
      setMetadata(mockMetadata);
    } catch (err) {
      setError('Failed to load media item');
      console.error('Error loading media:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setMenuAnchor(event.currentTarget);
  };

  const handleMenuClose = () => {
    setMenuAnchor(null);
  };

  const renderViewer = () => {
    if (!mediaItem || !metadata) return null;

    switch (mediaItem.mediaType) {
      case 'BOOK':
        return <EBookViewer mediaItem={mediaItem} metadata={metadata} />;
      case 'MOVIE':
        return <VideoPlayer mediaItem={mediaItem} metadata={metadata} />;
      case 'MUSIC':
        return <AudioPlayer mediaItem={mediaItem} metadata={metadata} />;
      default:
        return (
          <Box sx={{ p: 4, textAlign: 'center' }}>
            <Typography variant="h5" gutterBottom>
              Viewer not implemented for {mediaItem.mediaType}
            </Typography>
            <Typography color="text.secondary">
              This media type viewer is not yet implemented.
            </Typography>
          </Box>
        );
    }
  };

  if (loading) {
    return (
      <Box sx={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        minHeight: '100vh' 
      }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ p: 4 }}>
        <Alert severity="error">{error}</Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ minHeight: '100vh' }}>
      {/* Header */}
      <AppBar position="static" elevation={0}>
        <Toolbar>
          <IconButton edge="start" color="inherit" onClick={() => navigate(-1)}>
            <ArrowBackIcon />
          </IconButton>
          <Typography variant="h6" sx={{ flexGrow: 1, ml: 2 }}>
            {metadata?.title}
          </Typography>
          <IconButton color="inherit" onClick={handleMenuOpen}>
            <MoreVertIcon />
          </IconButton>
        </Toolbar>
      </AppBar>

      {/* Media Viewer */}
      {renderViewer()}

      {/* Edit FAB */}
      <Fab
        sx={{
          position: 'fixed',
          bottom: 24,
          right: 24,
        }}
        onClick={() => navigate(`/edit/${mediaId}`)}
      >
        <EditIcon />
      </Fab>

      {/* Menu */}
      <Menu
        anchorEl={menuAnchor}
        open={Boolean(menuAnchor)}
        onClose={handleMenuClose}
      >
        <MenuItem onClick={() => {
          handleMenuClose();
          navigate(`/edit/${mediaId}`);
        }}>
          Edit Metadata
        </MenuItem>
        <MenuItem onClick={handleMenuClose}>
          Add to Favorites
        </MenuItem>
        <MenuItem onClick={handleMenuClose}>
          Download Offline
        </MenuItem>
      </Menu>
    </Box>
  );
};