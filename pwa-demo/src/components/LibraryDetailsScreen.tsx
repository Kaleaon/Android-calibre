// React component equivalent to Android LibraryDetailsScreen
import React, { useState, useEffect } from 'react';
import {
  Box,
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Grid,
  Card,
  CardMedia,
  CardContent,
  CardActions,
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
  Divider,
  CircularProgress
} from '@mui/material';
import {
  ArrowBack as ArrowBackIcon,
  GridView as GridViewIcon,
  ViewList as ViewListIcon,
  ViewCarousel as ViewCarouselIcon,
  Search as SearchIcon,
  Star as StarIcon,
  StarBorder as StarBorderIcon,
  Book as BookIcon,
  FilterList as FilterListIcon
} from '@mui/icons-material';
import { useNavigate, useParams } from 'react-router-dom';
import { ViewMode, BookDetails, MediaItem } from '../types';
import { MediaItemService, MetadataService } from '../services/database';

interface LibraryDetailsScreenProps {
  libraryId: string;
}

const BookCard: React.FC<{
  book: BookDetails;
  viewMode: ViewMode;
  onClick: () => void;
  onFavoriteToggle: () => void;
}> = ({ book, viewMode, onClick, onFavoriteToggle }) => {
  if (viewMode === ViewMode.LIST) {
    return (
      <ListItem>
        <ListItemAvatar>
          <Avatar>
            <BookIcon />
          </Avatar>
        </ListItemAvatar>
        <ListItemText
          primary={book.metadataCommon.title || book.mediaItem.fileName}
          secondary={book.metadataBook?.author || 'Unknown Author'}
        />
        <IconButton onClick={onFavoriteToggle}>
          {book.metadataCommon.isFavorite ? <StarIcon color="primary" /> : <StarBorderIcon />}
        </IconButton>
      </ListItem>
    );
  }

  return (
    <Card 
      sx={{ 
        height: '100%', 
        display: 'flex', 
        flexDirection: 'column',
        borderRadius: 2,
        transition: 'all 0.3s ease-in-out',
        '&:hover': {
          transform: 'translateY(-4px)',
          boxShadow: '0 8px 25px rgba(0, 0, 0, 0.15)',
        }
      }}
    >
      <CardMedia
        component="div"
        sx={{
          height: 220,
          background: 'linear-gradient(135deg, #f5f5f5 0%, #e8e8e8 100%)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          position: 'relative'
        }}
      >
        {book.metadataCommon.thumbnailPath ? (
          <img 
            src={book.metadataCommon.thumbnailPath} 
            alt={book.metadataCommon.title}
            style={{ 
              maxHeight: '100%', 
              maxWidth: '100%',
              borderRadius: '4px',
              boxShadow: '0 4px 12px rgba(0, 0, 0, 0.2)'
            }}
          />
        ) : (
          <BookIcon 
            sx={{ 
              fontSize: 64, 
              color: 'grey.400',
              filter: 'drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1))'
            }} 
          />
        )}
        {book.metadataCommon.isFavorite && (
          <StarIcon 
            sx={{ 
              position: 'absolute',
              top: 8,
              right: 8,
              color: '#FFD700',
              fontSize: 20,
              filter: 'drop-shadow(0 1px 2px rgba(0, 0, 0, 0.3))'
            }}
          />
        )}
      </CardMedia>
      <CardContent sx={{ flexGrow: 1, p: 2 }}>
        <Typography 
          variant="h6" 
          component="h2" 
          sx={{
            fontWeight: 600,
            fontSize: '1rem',
            lineHeight: 1.3,
            mb: 1,
            display: '-webkit-box',
            WebkitLineClamp: 2,
            WebkitBoxOrient: 'vertical',
            overflow: 'hidden',
          }}
        >
          {book.metadataCommon.title || book.mediaItem.fileName}
        </Typography>
        <Typography 
          variant="body2" 
          color="text.secondary"
          sx={{
            mb: 1.5,
            display: '-webkit-box',
            WebkitLineClamp: 1,
            WebkitBoxOrient: 'vertical',
            overflow: 'hidden',
          }}
        >
          {book.metadataBook?.author || 'Unknown Author'}
        </Typography>
        {book.metadataBook?.series && (
          <Chip
            size="small"
            label={book.metadataBook.series}
            sx={{ 
              mb: 1,
              fontSize: '0.75rem',
              height: 24,
              bgcolor: 'primary.light',
              color: 'primary.contrastText'
            }}
          />
        )}
      </CardContent>
      <CardActions sx={{ p: 2, pt: 0 }}>
        <Button 
          size="small" 
          onClick={onClick}
          sx={{
            borderRadius: 2,
            textTransform: 'none',
            fontWeight: 600,
          }}
        >
          Read
        </Button>
        <IconButton 
          size="small" 
          onClick={onFavoriteToggle}
          sx={{ 
            ml: 'auto',
            color: book.metadataCommon.isFavorite ? '#FFD700' : 'text.secondary',
            '&:hover': {
              color: '#FFD700'
            }
          }}
        >
          {book.metadataCommon.isFavorite ? <StarIcon /> : <StarBorderIcon />}
        </IconButton>
      </CardActions>
    </Card>
  );
};

export const LibraryDetailsScreen: React.FC = () => {
  const navigate = useNavigate();
  const { libraryId } = useParams<{ libraryId: string }>();
  const [books, setBooks] = useState<BookDetails[]>([]);
  const [filteredBooks, setFilteredBooks] = useState<BookDetails[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [viewMode, setViewMode] = useState<ViewMode>(ViewMode.GRID);
  const [searchQuery, setSearchQuery] = useState('');
  const [showFilters, setShowFilters] = useState(false);

  useEffect(() => {
    loadBooks();
  }, [libraryId]);

  useEffect(() => {
    // Filter books based on search query
    if (searchQuery.trim()) {
      const filtered = books.filter(book =>
        book.metadataCommon.title?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        book.metadataBook?.author?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        book.mediaItem.fileName.toLowerCase().includes(searchQuery.toLowerCase())
      );
      setFilteredBooks(filtered);
    } else {
      setFilteredBooks(books);
    }
  }, [books, searchQuery]);

  const loadBooks = async () => {
    if (!libraryId) return;
    
    setIsLoading(true);
    try {
      const mediaItems = await MediaItemService.getItemsByLibrary(parseInt(libraryId));
      const bookDetails = await Promise.all(
        mediaItems.map(async (item) => {
          const details = await MetadataService.getBookDetails(item.itemId!);
          return {
            mediaItem: item,
            metadataCommon: details.metadataCommon || {
              itemId: item.itemId!,
              title: item.fileName,
              isFavorite: false,
              isDownloaded: true
            },
            metadataBook: details.metadataBook
          };
        })
      );
      setBooks(bookDetails);
    } catch (error) {
      console.error('Failed to load books:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleFavoriteToggle = async (itemId: number) => {
    try {
      await MetadataService.toggleFavorite(itemId);
      await loadBooks(); // Refresh the list
    } catch (error) {
      console.error('Failed to toggle favorite:', error);
    }
  };

  const handleViewModeChange = (
    event: React.MouseEvent<HTMLElement>,
    newViewMode: ViewMode | null,
  ) => {
    if (newViewMode !== null) {
      setViewMode(newViewMode);
    }
  };

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar 
        position="static" 
        elevation={0}
        sx={{
          background: 'linear-gradient(135deg, #6750A4 0%, #7C4DFF 100%)',
        }}
      >
        <Toolbar>
          <IconButton
            edge="start"
            color="inherit"
            onClick={() => navigate('/')}
            sx={{ 
              mr: 2,
              '&:hover': {
                bgcolor: 'rgba(255, 255, 255, 0.1)'
              }
            }}
          >
            <ArrowBackIcon />
          </IconButton>
          <Typography 
            variant="h6" 
            component="div" 
            sx={{ 
              flexGrow: 1,
              fontWeight: 600,
            }}
          >
            Library Contents
          </Typography>
          <IconButton
            color="inherit"
            onClick={() => setShowFilters(!showFilters)}
            sx={{
              '&:hover': {
                bgcolor: 'rgba(255, 255, 255, 0.1)'
              }
            }}
          >
            <FilterListIcon />
          </IconButton>
        </Toolbar>
      </AppBar>

      <Box sx={{ p: 2 }}>
        {/* Search Bar with enhanced design */}
        <Card 
          elevation={0}
          sx={{ 
            mb: 3,
            border: '1px solid',
            borderColor: 'divider',
            borderRadius: 2
          }}
        >
          <CardContent sx={{ p: 2 }}>
            <TextField
              fullWidth
              placeholder="Search by title, author, or filename..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon sx={{ color: 'text.secondary' }} />
                  </InputAdornment>
                ),
              }}
              sx={{ 
                '& .MuiOutlinedInput-root': {
                  borderRadius: 2,
                  '&:hover .MuiOutlinedInput-notchedOutline': {
                    borderColor: 'primary.main',
                  },
                },
              }}
            />
          </CardContent>
        </Card>

        {/* View Mode Toggle */}
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="body2" color="text.secondary">
            {filteredBooks.length} books
          </Typography>
          <ToggleButtonGroup
            value={viewMode}
            exclusive
            onChange={handleViewModeChange}
            size="small"
          >
            <ToggleButton value={ViewMode.GRID}>
              <GridViewIcon />
            </ToggleButton>
            <ToggleButton value={ViewMode.LIST}>
              <ViewListIcon />
            </ToggleButton>
            <ToggleButton value={ViewMode.COVER_FLOW}>
              <ViewCarouselIcon />
            </ToggleButton>
          </ToggleButtonGroup>
        </Box>

        {/* Content */}
        {isLoading ? (
          <Box display="flex" justifyContent="center" mt={4}>
            <CircularProgress />
          </Box>
        ) : filteredBooks.length === 0 ? (
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
                width: 80,
                height: 80,
                borderRadius: '50%',
                background: searchQuery 
                  ? 'linear-gradient(135deg, #FF6B35 0%, #FF8E53 100%)'
                  : 'linear-gradient(135deg, #6750A4 0%, #7C4DFF 100%)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                mb: 3,
                opacity: 0.8
              }}
            >
              <SearchIcon sx={{ fontSize: 40, color: 'white' }} />
            </Box>
            
            <Typography 
              variant="h6" 
              gutterBottom
              sx={{ 
                fontWeight: 600,
                mb: 1
              }}
            >
              {searchQuery ? 'No books found matching your search' : 'No books in this library'}
            </Typography>
            <Typography 
              variant="body2" 
              color="text.secondary"
              sx={{ 
                lineHeight: 1.5,
                opacity: 0.8
              }}
            >
              {searchQuery ? 'Try a different search term or browse all books' : 'Add some books to get started with your collection'}
            </Typography>
            
            {searchQuery && (
              <Button
                variant="outlined"
                onClick={() => setSearchQuery('')}
                sx={{ mt: 2 }}
              >
                Clear Search
              </Button>
            )}
          </Box>
        ) : viewMode === ViewMode.LIST ? (
          <List>
            {filteredBooks.map((book, index) => (
              <React.Fragment key={book.mediaItem.itemId}>
                <BookCard
                  book={book}
                  viewMode={viewMode}
                  onClick={() => navigate(`/book/${book.mediaItem.itemId}`)}
                  onFavoriteToggle={() => handleFavoriteToggle(book.mediaItem.itemId!)}
                />
                {index < filteredBooks.length - 1 && <Divider />}
              </React.Fragment>
            ))}
          </List>
        ) : (
          <Grid container spacing={2}>
            {filteredBooks.map((book) => (
              <Grid
                item
                xs={12}
                sm={6}
                md={4}
                lg={viewMode === ViewMode.COVER_FLOW ? 2 : 3}
                key={book.mediaItem.itemId}
              >
                <BookCard
                  book={book}
                  viewMode={viewMode}
                  onClick={() => navigate(`/book/${book.mediaItem.itemId}`)}
                  onFavoriteToggle={() => handleFavoriteToggle(book.mediaItem.itemId!)}
                />
              </Grid>
            ))}
          </Grid>
        )}
      </Box>
    </Box>
  );
};