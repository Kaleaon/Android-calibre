// Plex-inspired Library Details Screen with media grid
import React, { useState, useEffect } from 'react';
import {
  Box,
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Grid,
  Card,
  CardContent,
  Button,
  Chip,
  ToggleButtonGroup,
  ToggleButton,
  TextField,
  InputAdornment,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Avatar,
  CircularProgress,
  Paper,
  Rating,
  Menu,
  MenuItem,
  Divider,
  Fab,
} from '@mui/material';
import {
  ArrowBack as ArrowBackIcon,
  GridView as GridViewIcon,
  ViewList as ViewListIcon,
  Search as SearchIcon,
  Star as StarIcon,
  StarBorder as StarBorderIcon,
  Book as BookIcon,
  Movie as MovieIcon,
  MusicNote as MusicIcon,
  FilterList as FilterListIcon,
  Sort as SortIcon,
  MoreVert as MoreVertIcon,
  Play as PlayIcon,
  Edit as EditIcon,
  Add as AddIcon,
  ViewComfy as ViewComfyIcon,
} from '@mui/icons-material';
import { useNavigate, useParams } from 'react-router-dom';
import { ViewMode, BookDetails, MediaItem, Library } from '../types';
import { MediaItemService, MetadataService } from '../services/database';
import { MetadataAPIService } from '../services/metadataApi';

// Demo data for media items
const generateDemoMediaItems = (libraryType: Library['type'], count: number = 20) => {
  const items: any[] = [];
  
  for (let i = 1; i <= count; i++) {
    const baseItem = {
      itemId: i,
      libraryId: 1,
      filePath: `/demo/${libraryType.toLowerCase()}/${i}`,
      fileName: `Demo ${libraryType} ${i}`,
      fileSize: Math.floor(Math.random() * 500000000) + 1000000,
      mimeType: libraryType === 'BOOK' ? 'application/epub+zip' : 
                libraryType === 'MOVIE' ? 'video/mp4' : 'audio/mp3',
      mediaType: libraryType,
      dateAdded: new Date(Date.now() - Math.random() * 365 * 24 * 60 * 60 * 1000),
      lastModified: new Date(),
    };

    const metadata = {
      itemId: i,
      title: getRandomTitle(libraryType, i),
      description: getRandomDescription(libraryType),
      rating: Math.round((Math.random() * 4 + 1) * 2) / 2, // 1-5 stars, half increments
      thumbnailPath: getRandomThumbnail(libraryType, i),
      isFavorite: Math.random() > 0.8,
      isDownloaded: true,
      genre: getRandomGenres(libraryType),
      year: 2020 + Math.floor(Math.random() * 5),
      author: libraryType === 'BOOK' ? getRandomAuthor() : 
              libraryType === 'MOVIE' ? getRandomDirector() : 
              getRandomArtist(),
    };

    items.push({
      mediaItem: baseItem,
      metadataCommon: metadata,
      metadataBook: libraryType === 'BOOK' ? {
        ...metadata,
        isbn: `978-${Math.floor(Math.random() * 9000000000) + 1000000000}`,
        publisher: getRandomPublisher(),
        pageCount: Math.floor(Math.random() * 500) + 100,
        series: Math.random() > 0.7 ? getRandomSeries() : undefined,
      } : undefined,
    });
  }
  
  return items;
};

const getRandomTitle = (type: string, index: number) => {
  const titles = {
    BOOK: ['The Silent Echo', 'Midnight Chronicles', 'Digital Shadows', 'The Last Algorithm', 'Neon Dreams', 'Quantum Hearts', 'The Memory Thief', 'Stellar Winds', 'The Code Breaker', 'Future Past'],
    MOVIE: ['Cyber Storm', 'The Last Stand', 'Digital Revolution', 'Neon City', 'Time Paradox', 'The Algorithm', 'Space Odyssey 2025', 'Virtual Reality', 'The Matrix Reborn', 'Future Wars'],
    MUSIC: ['Electronic Dreams', 'Synthwave Nights', 'Digital Pulse', 'Neon Beats', 'Cyber Sounds', 'Future Funk', 'Retrowave', 'Space Disco', 'Neural Network', 'Binary Code'],
  };
  return titles[type as keyof typeof titles]?.[index % 10] || `${type} Title ${index}`;
};

const getRandomDescription = (type: string) => {
  const descriptions = {
    BOOK: 'A captivating tale of adventure, mystery, and discovery that will keep you turning pages late into the night.',
    MOVIE: 'An action-packed thriller with stunning visuals and a gripping storyline that will leave you on the edge of your seat.',
    MUSIC: 'A collection of electronic beats and synthesized melodies that create an atmospheric journey through sound.',
  };
  return descriptions[type as keyof typeof descriptions] || 'An engaging piece of media content.';
};

const getRandomThumbnail = (type: string, index: number) => {
  const colors = ['4A90E2', 'F5A623', '7ED321', 'D0021B', '9013FE', '50E3C2'];
  const color = colors[index % colors.length];
  const text = type === 'BOOK' ? 'BOOK' : type === 'MOVIE' ? 'MOVIE' : 'MUSIC';
  return `https://via.placeholder.com/300x450/${color}/ffffff?text=${text}+${index}`;
};

const getRandomGenres = (type: string) => {
  const genres = {
    BOOK: ['Fiction', 'Sci-Fi', 'Mystery', 'Thriller', 'Fantasy', 'Romance'],
    MOVIE: ['Action', 'Drama', 'Comedy', 'Sci-Fi', 'Thriller', 'Adventure'],
    MUSIC: ['Electronic', 'Synthwave', 'Ambient', 'Techno', 'House', 'Chill'],
  };
  const typeGenres = genres[type as keyof typeof genres] || ['General'];
  return [typeGenres[Math.floor(Math.random() * typeGenres.length)]];
};

const getRandomAuthor = () => {
  const authors = ['Alex Chen', 'Sarah Johnson', 'Michael Rodriguez', 'Emily Zhang', 'David Kim', 'Lisa Thompson'];
  return authors[Math.floor(Math.random() * authors.length)];
};

const getRandomDirector = () => {
  const directors = ['Christopher Davis', 'Jennifer Liu', 'Robert Miller', 'Amanda Wilson', 'James Park', 'Michelle Brown'];
  return directors[Math.floor(Math.random() * directors.length)];
};

const getRandomArtist = () => {
  const artists = ['Neon Pulse', 'Cyber Dreams', 'Digital Flow', 'Synth Master', 'Echo Wave', 'Future Sound'];
  return artists[Math.floor(Math.random() * artists.length)];
};

const getRandomPublisher = () => {
  const publishers = ['Digital Press', 'Future Books', 'Neon Publishing', 'Cyber Works', 'Tech Literature', 'Modern Tales'];
  return publishers[Math.floor(Math.random() * publishers.length)];
};

const getRandomSeries = () => {
  const series = ['Cyber Chronicles', 'Digital Age', 'Future Tales', 'Neon Series', 'Tech Saga', 'Modern Myths'];
  return series[Math.floor(Math.random() * series.length)];
};

// Media card component with Plex styling
const MediaCard: React.FC<{
  item: any;
  viewMode: ViewMode;
  onClick: () => void;
  onEdit: () => void;
  onFavoriteToggle: () => void;
}> = ({ item, viewMode, onClick, onEdit, onFavoriteToggle }) => {
  const [menuAnchor, setMenuAnchor] = useState<null | HTMLElement>(null);

  const getIcon = () => {
    switch (item.mediaItem.mediaType) {
      case 'BOOK': return <BookIcon sx={{ fontSize: 40, color: 'rgba(255,255,255,0.7)' }} />;
      case 'MOVIE': return <MovieIcon sx={{ fontSize: 40, color: 'rgba(255,255,255,0.7)' }} />;
      case 'MUSIC': return <MusicIcon sx={{ fontSize: 40, color: 'rgba(255,255,255,0.7)' }} />;
      default: return <BookIcon sx={{ fontSize: 40, color: 'rgba(255,255,255,0.7)' }} />;
    }
  };

  const getActionLabel = () => {
    switch (item.mediaItem.mediaType) {
      case 'BOOK': return 'Read';
      case 'MOVIE': return 'Watch';
      case 'MUSIC': return 'Play';
      default: return 'Open';
    }
  };

  if (viewMode === ViewMode.LIST) {
    return (
      <Paper 
        sx={{ 
          mb: 1, 
          bgcolor: 'background.paper',
          border: '1px solid #2d3136',
          '&:hover': {
            borderColor: 'primary.main',
            bgcolor: 'secondary.main',
          }
        }}
      >
        <ListItem>
          <ListItemAvatar>
            <Avatar
              src={item.metadataCommon.thumbnailPath}
              sx={{ width: 50, height: 50, bgcolor: 'secondary.main' }}
            >
              {getIcon()}
            </Avatar>
          </ListItemAvatar>
          <ListItemText
            primary={
              <Typography variant="h6" sx={{ fontSize: '1rem', fontWeight: 500 }}>
                {item.metadataCommon.title}
              </Typography>
            }
            secondary={
              <Box>
                <Typography variant="body2" color="text.secondary">
                  {item.metadataCommon.author} • {item.metadataCommon.year}
                </Typography>
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mt: 1 }}>
                  <Rating value={item.metadataCommon.rating} readOnly size="small" />
                  {item.metadataCommon.genre && (
                    <Chip label={item.metadataCommon.genre[0]} size="small" />
                  )}
                </Box>
              </Box>
            }
          />
          <Box sx={{ display: 'flex', gap: 1 }}>
            <Button size="small" startIcon={<PlayIcon />} onClick={onClick}>
              {getActionLabel()}
            </Button>
            <IconButton size="small" onClick={onEdit}>
              <EditIcon />
            </IconButton>
            <IconButton 
              size="small" 
              onClick={onFavoriteToggle}
              sx={{ color: item.metadataCommon.isFavorite ? 'primary.main' : 'text.secondary' }}
            >
              {item.metadataCommon.isFavorite ? <StarIcon /> : <StarBorderIcon />}
            </IconButton>
          </Box>
        </ListItem>
      </Paper>
    );
  }

  return (
    <Card
      sx={{
        height: viewMode === ViewMode.COVER_FLOW ? 380 : 420,
        bgcolor: 'background.paper',
        border: '1px solid #2d3136',
        cursor: 'pointer',
        transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
        position: 'relative',
        '&:hover': {
          transform: 'translateY(-8px) scale(1.02)',
          boxShadow: '0 12px 40px rgba(0, 0, 0, 0.4)',
          borderColor: 'primary.main',
          '& .media-overlay': {
            opacity: 1,
          },
        },
      }}
    >
      {/* Poster/Cover */}
      <Box
        sx={{
          height: viewMode === ViewMode.COVER_FLOW ? 220 : 260,
          bgcolor: 'secondary.main',
          position: 'relative',
          backgroundImage: item.metadataCommon.thumbnailPath ? 
            `url(${item.metadataCommon.thumbnailPath})` : 'none',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        {!item.metadataCommon.thumbnailPath && getIcon()}
        
        {/* Favorite indicator */}
        {item.metadataCommon.isFavorite && (
          <Chip
            icon={<StarIcon />}
            label="Favorite"
            size="small"
            sx={{
              position: 'absolute',
              top: 8,
              left: 8,
              bgcolor: 'primary.main',
              color: 'black',
              fontWeight: 600,
            }}
          />
        )}

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
            display: 'flex',
            alignItems: 'center',
            gap: 0.5,
          }}
        >
          <StarIcon sx={{ fontSize: 14, color: 'primary.main' }} />
          <Typography variant="caption" sx={{ color: 'white', fontWeight: 600 }}>
            {item.metadataCommon.rating}
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
            size="large"
            startIcon={<PlayIcon />}
            onClick={(e) => {
              e.stopPropagation();
              onClick();
            }}
            sx={{
              bgcolor: 'primary.main',
              color: 'black',
              fontWeight: 600,
              '&:hover': {
                bgcolor: 'primary.light',
              },
            }}
          >
            {getActionLabel()}
          </Button>
        </Box>
      </Box>

      {/* Content */}
      <CardContent sx={{ p: 2, height: viewMode === ViewMode.COVER_FLOW ? 160 : 160 }}>
        <Typography
          variant="h6"
          sx={{
            fontSize: '1rem',
            fontWeight: 600,
            mb: 1,
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap',
          }}
        >
          {item.metadataCommon.title}
        </Typography>

        <Typography
          variant="body2"
          color="text.secondary"
          sx={{
            mb: 1,
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap',
          }}
        >
          {item.metadataCommon.author} • {item.metadataCommon.year}
        </Typography>

        {/* Genre chips */}
        {item.metadataCommon.genre && (
          <Box sx={{ mb: 1 }}>
            <Chip
              label={item.metadataCommon.genre[0]}
              size="small"
              sx={{
                bgcolor: 'rgba(229, 160, 13, 0.2)',
                color: 'primary.main',
                borderColor: 'primary.main',
                fontSize: '0.75rem',
              }}
              variant="outlined"
            />
          </Box>
        )}

        {/* Series info for books */}
        {item.metadataBook?.series && (
          <Typography variant="caption" color="text.secondary" sx={{ fontSize: '0.7rem' }}>
            {item.metadataBook.series}
          </Typography>
        )}

        {/* Action buttons */}
        <Box sx={{ 
          position: 'absolute', 
          bottom: 8, 
          right: 8,
          display: 'flex',
          gap: 0.5
        }}>
          <IconButton
            size="small"
            onClick={(e) => {
              e.stopPropagation();
              setMenuAnchor(e.currentTarget);
            }}
            sx={{ bgcolor: 'rgba(0, 0, 0, 0.5)' }}
          >
            <MoreVertIcon sx={{ fontSize: 16, color: 'white' }} />
          </IconButton>
        </Box>
      </CardContent>

      {/* Context Menu */}
      <Menu
        anchorEl={menuAnchor}
        open={Boolean(menuAnchor)}
        onClose={() => setMenuAnchor(null)}
        PaperProps={{
          sx: {
            bgcolor: 'background.paper',
            border: '1px solid #2d3136',
          }
        }}
      >
        <MenuItem onClick={() => {
          setMenuAnchor(null);
          onClick();
        }}>
          <PlayIcon sx={{ mr: 1, fontSize: 16 }} />
          {getActionLabel()}
        </MenuItem>
        <MenuItem onClick={() => {
          setMenuAnchor(null);
          onEdit();
        }}>
          <EditIcon sx={{ mr: 1, fontSize: 16 }} />
          Edit Metadata
        </MenuItem>
        <MenuItem onClick={() => {
          setMenuAnchor(null);
          onFavoriteToggle();
        }}>
          {item.metadataCommon.isFavorite ? <StarIcon sx={{ mr: 1, fontSize: 16 }} /> : <StarBorderIcon sx={{ mr: 1, fontSize: 16 }} />}
          {item.metadataCommon.isFavorite ? 'Remove from Favorites' : 'Add to Favorites'}
        </MenuItem>
      </Menu>
    </Card>
  );
};

export const LibraryDetailsScreen: React.FC = () => {
  const navigate = useNavigate();
  const { libraryId } = useParams<{ libraryId: string }>();
  const [items, setItems] = useState<any[]>([]);
  const [filteredItems, setFilteredItems] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [viewMode, setViewMode] = useState<ViewMode>(ViewMode.GRID);
  const [searchQuery, setSearchQuery] = useState('');
  const [sortBy, setSortBy] = useState('title');
  const [libraryType, setLibraryType] = useState<Library['type']>('BOOK');

  useEffect(() => {
    loadLibraryItems();
  }, [libraryId]);

  useEffect(() => {
    // Filter and sort items
    let filtered = items;
    
    if (searchQuery.trim()) {
      filtered = items.filter(item =>
        item.metadataCommon.title?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.metadataCommon.author?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.metadataCommon.genre?.some((g: string) => g.toLowerCase().includes(searchQuery.toLowerCase()))
      );
    }

    // Sort items
    filtered.sort((a, b) => {
      switch (sortBy) {
        case 'title':
          return a.metadataCommon.title.localeCompare(b.metadataCommon.title);
        case 'author':
          return a.metadataCommon.author.localeCompare(b.metadataCommon.author);
        case 'year':
          return b.metadataCommon.year - a.metadataCommon.year;
        case 'rating':
          return b.metadataCommon.rating - a.metadataCommon.rating;
        case 'dateAdded':
          return new Date(b.mediaItem.dateAdded).getTime() - new Date(a.mediaItem.dateAdded).getTime();
        default:
          return 0;
      }
    });

    setFilteredItems(filtered);
  }, [items, searchQuery, sortBy]);

  const loadLibraryItems = async () => {
    setIsLoading(true);
    try {
      // For demo, determine library type and generate demo data
      const types: Library['type'][] = ['BOOK', 'MOVIE', 'MUSIC'];
      const selectedType = types[parseInt(libraryId || '1') % types.length];
      setLibraryType(selectedType);
      
      // Generate demo items
      const demoItems = generateDemoMediaItems(selectedType, 24);
      setItems(demoItems);
    } catch (error) {
      console.error('Failed to load items:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleFavoriteToggle = (itemId: number) => {
    setItems(prevItems =>
      prevItems.map(item =>
        item.mediaItem.itemId === itemId
          ? {
              ...item,
              metadataCommon: {
                ...item.metadataCommon,
                isFavorite: !item.metadataCommon.isFavorite
              }
            }
          : item
      )
    );
  };

  const getLibraryTitle = () => {
    switch (libraryType) {
      case 'BOOK': return 'Books Library';
      case 'MOVIE': return 'Movies & TV';
      case 'MUSIC': return 'Music Library';
      default: return 'Media Library';
    }
  };

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
      {/* Header */}
      <AppBar position="static" elevation={0}>
        <Toolbar>
          <IconButton edge="start" color="inherit" onClick={() => navigate('/')}>
            <ArrowBackIcon />
          </IconButton>
          <Typography variant="h6" sx={{ flexGrow: 1, ml: 2, fontWeight: 300 }}>
            {getLibraryTitle()}
          </Typography>
        </Toolbar>
      </AppBar>

      <Box sx={{ p: 3 }}>
        {/* Search and Filter Bar */}
        <Paper sx={{ p: 2, mb: 3, bgcolor: 'background.paper', border: '1px solid #2d3136' }}>
          <Box sx={{ display: 'flex', gap: 2, alignItems: 'center', flexWrap: 'wrap' }}>
            <TextField
              placeholder={`Search ${libraryType.toLowerCase()}s...`}
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              sx={{ flex: 1, minWidth: 250 }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon sx={{ color: 'text.secondary' }} />
                  </InputAdornment>
                ),
              }}
            />
            
            <Button
              startIcon={<SortIcon />}
              onClick={(e) => {
                // Simple sort toggle for demo
                const sorts = ['title', 'author', 'year', 'rating', 'dateAdded'];
                const currentIndex = sorts.indexOf(sortBy);
                setSortBy(sorts[(currentIndex + 1) % sorts.length]);
              }}
              sx={{ minWidth: 120 }}
            >
              Sort: {sortBy}
            </Button>

            <ToggleButtonGroup
              value={viewMode}
              exclusive
              onChange={(e, newMode) => newMode && setViewMode(newMode)}
              size="small"
            >
              <ToggleButton value={ViewMode.GRID}>
                <GridViewIcon />
              </ToggleButton>
              <ToggleButton value={ViewMode.COVER_FLOW}>
                <ViewComfyIcon />
              </ToggleButton>
              <ToggleButton value={ViewMode.LIST}>
                <ViewListIcon />
              </ToggleButton>
            </ToggleButtonGroup>
          </Box>
        </Paper>

        {/* Results Info */}
        <Box sx={{ mb: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Typography variant="h5" sx={{ fontWeight: 300 }}>
            {searchQuery ? `Search Results (${filteredItems.length})` : `All ${libraryType}s (${filteredItems.length})`}
          </Typography>
          {searchQuery && (
            <Button onClick={() => setSearchQuery('')} size="small">
              Clear Search
            </Button>
          )}
        </Box>

        {/* Content */}
        {isLoading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
            <CircularProgress />
          </Box>
        ) : filteredItems.length === 0 ? (
          <Paper sx={{ p: 6, textAlign: 'center', bgcolor: 'background.paper', border: '1px solid #2d3136' }}>
            <Typography variant="h5" gutterBottom sx={{ fontWeight: 300 }}>
              {searchQuery ? 'No results found' : 'Library is empty'}
            </Typography>
            <Typography color="text.secondary" paragraph>
              {searchQuery 
                ? 'Try adjusting your search terms or browse all items.'
                : 'Add some media to get started with your collection.'
              }
            </Typography>
            {searchQuery && (
              <Button variant="contained" onClick={() => setSearchQuery('')}>
                Browse All Items
              </Button>
            )}
          </Paper>
        ) : viewMode === ViewMode.LIST ? (
          <Box>
            {filteredItems.map((item) => (
              <MediaCard
                key={item.mediaItem.itemId}
                item={item}
                viewMode={viewMode}
                onClick={() => navigate(`/media/${item.mediaItem.itemId}`)}
                onEdit={() => navigate(`/edit/${item.mediaItem.itemId}`)}
                onFavoriteToggle={() => handleFavoriteToggle(item.mediaItem.itemId)}
              />
            ))}
          </Box>
        ) : (
          <Grid container spacing={2.5}>
            {filteredItems.map((item) => (
              <Grid
                item
                xs={12}
                sm={6}
                md={viewMode === ViewMode.COVER_FLOW ? 3 : 4}
                lg={viewMode === ViewMode.COVER_FLOW ? 2.4 : 3}
                xl={viewMode === ViewMode.COVER_FLOW ? 2 : 2.4}
                key={item.mediaItem.itemId}
              >
                <MediaCard
                  item={item}
                  viewMode={viewMode}
                  onClick={() => navigate(`/media/${item.mediaItem.itemId}`)}
                  onEdit={() => navigate(`/edit/${item.mediaItem.itemId}`)}
                  onFavoriteToggle={() => handleFavoriteToggle(item.mediaItem.itemId)}
                />
              </Grid>
            ))}
          </Grid>
        )}
      </Box>

      {/* Add Media FAB */}
      <Fab
        sx={{
          position: 'fixed',
          bottom: 24,
          right: 24,
          bgcolor: 'primary.main',
          color: 'black',
        }}
        onClick={() => {
          // In a real app, this would open file picker or metadata search
          alert('Add new media functionality would be implemented here');
        }}
      >
        <AddIcon />
      </Fab>
    </Box>
  );
};