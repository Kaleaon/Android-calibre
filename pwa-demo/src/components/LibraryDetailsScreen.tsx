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
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <CardMedia
        component="div"
        sx={{
          height: 200,
          bgcolor: 'grey.200',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center'
        }}
      >
        {book.metadataCommon.thumbnailPath ? (
          <img 
            src={book.metadataCommon.thumbnailPath} 
            alt={book.metadataCommon.title}
            style={{ maxHeight: '100%', maxWidth: '100%' }}
          />
        ) : (
          <BookIcon sx={{ fontSize: 60, color: 'grey.400' }} />
        )}
      </CardMedia>
      <CardContent sx={{ flexGrow: 1 }}>
        <Typography variant="h6" component="h2" noWrap>
          {book.metadataCommon.title || book.mediaItem.fileName}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {book.metadataBook?.author || 'Unknown Author'}
        </Typography>
        {book.metadataBook?.series && (
          <Chip
            size="small"
            label={book.metadataBook.series}
            sx={{ mt: 1 }}
          />
        )}
      </CardContent>
      <CardActions>
        <Button size="small" onClick={onClick}>
          Read
        </Button>
        <IconButton size="small" onClick={onFavoriteToggle}>
          {book.metadataCommon.isFavorite ? <StarIcon color="primary" /> : <StarBorderIcon />}
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
      <AppBar position="static" elevation={0}>
        <Toolbar>
          <IconButton
            edge="start"
            color="inherit"
            onClick={() => navigate('/')}
            sx={{ mr: 2 }}
          >
            <ArrowBackIcon />
          </IconButton>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Library Contents
          </Typography>
          <IconButton
            color="inherit"
            onClick={() => setShowFilters(!showFilters)}
          >
            <FilterListIcon />
          </IconButton>
        </Toolbar>
      </AppBar>

      <Box sx={{ p: 2 }}>
        {/* Search Bar */}
        <TextField
          fullWidth
          placeholder="Search books..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
          }}
          sx={{ mb: 2 }}
        />

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
          <Box textAlign="center" mt={4}>
            <Typography variant="h6" gutterBottom>
              {searchQuery ? 'No books found matching your search' : 'No books in this library'}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {searchQuery ? 'Try a different search term' : 'Add some books to get started'}
            </Typography>
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