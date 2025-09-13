// Metadata editing interface with API integration
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box,
  AppBar,
  Toolbar,
  IconButton,
  Typography,
  Paper,
  TextField,
  Button,
  Grid,
  Card,
  CardMedia,
  CardContent,
  Chip,
  CircularProgress,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
  Avatar,
  Divider,
  Rating,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from '@mui/material';
import {
  ArrowBack as ArrowBackIcon,
  Save as SaveIcon,
  Search as SearchIcon,
  Image as ImageIcon,
  Movie as MovieIcon,
  MusicNote as MusicIcon,
  Book as BookIcon,
} from '@mui/icons-material';
import { MediaItem, MetadataCommon, MetadataBook } from '../types';
import { MetadataAPIService, MetadataSearchResult } from '../services/metadataApi';
import { MetadataService } from '../services/database';

export const MetadataEditorScreen: React.FC = () => {
  const { mediaId } = useParams<{ mediaId: string }>();
  const navigate = useNavigate();
  
  // Form state
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [author, setAuthor] = useState('');
  const [genre, setGenre] = useState<string[]>([]);
  const [year, setYear] = useState<number | ''>('');
  const [rating, setRating] = useState<number | null>(null);
  const [thumbnailPath, setThumbnailPath] = useState('');
  const [mediaType, setMediaType] = useState<'BOOK' | 'MOVIE' | 'MUSIC' | 'PODCAST' | 'MAGAZINE' | 'DOCUMENT'>('BOOK');
  
  // Book-specific fields
  const [isbn, setIsbn] = useState('');
  const [publisher, setPublisher] = useState('');
  const [pageCount, setPageCount] = useState<number | ''>('');
  const [series, setSeries] = useState('');
  
  // API search state
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<MetadataSearchResult[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const [showSearchDialog, setShowSearchDialog] = useState(false);
  
  // General state
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadMetadata();
  }, [mediaId]);

  const loadMetadata = async () => {
    if (!mediaId) {
      setError('No media ID provided');
      setLoading(false);
      return;
    }

    try {
      // For demo, create mock metadata
      const mockMetadata = {
        title: `Demo ${mediaType} ${mediaId}`,
        description: 'This is demonstration metadata that would be loaded from the database.',
        author: 'Demo Author',
        genre: ['Fiction', 'Adventure'],
        year: 2024,
        rating: 4.2,
        thumbnailPath: 'https://via.placeholder.com/300x450/1a1a1a/e5a00d?text=Demo+Cover',
        isbn: '978-0-123456-78-9',
        publisher: 'Demo Publisher',
        pageCount: 256,
        series: 'Demo Series',
      };

      setTitle(mockMetadata.title);
      setDescription(mockMetadata.description);
      setAuthor(mockMetadata.author);
      setGenre(mockMetadata.genre);
      setYear(mockMetadata.year);
      setRating(mockMetadata.rating);
      setThumbnailPath(mockMetadata.thumbnailPath);
      setIsbn(mockMetadata.isbn);
      setPublisher(mockMetadata.publisher);
      setPageCount(mockMetadata.pageCount);
      setSeries(mockMetadata.series);

    } catch (err) {
      setError('Failed to load metadata');
      console.error('Error loading metadata:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (!searchQuery.trim()) return;

    setIsSearching(true);
    try {
      const results = await MetadataAPIService.searchMetadata(searchQuery, mediaType);
      setSearchResults(results);
      setShowSearchDialog(true);
    } catch (err) {
      console.error('Search error:', err);
      setError('Failed to search metadata');
    } finally {
      setIsSearching(false);
    }
  };

  const applySearchResult = (result: MetadataSearchResult) => {
    setTitle(result.title);
    if (result.description) setDescription(result.description);
    if (result.author) setAuthor(result.author);
    if (result.director && mediaType === 'MOVIE') setAuthor(result.director);
    if (result.artist && mediaType === 'MUSIC') setAuthor(result.artist);
    if (result.genre) setGenre(result.genre);
    if (result.year) setYear(result.year);
    if (result.rating) setRating(result.rating);
    if (result.cover || result.poster) setThumbnailPath(result.cover || result.poster || '');
    if (result.isbn) setIsbn(result.isbn);
    
    setShowSearchDialog(false);
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      // In a real app, this would save to the database
      const updatedMetadata = {
        title,
        description,
        rating,
        thumbnailPath,
        genre: genre.join(', '),
      };

      console.log('Saving metadata:', updatedMetadata);
      
      // Simulate save delay
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      navigate(-1);
    } catch (err) {
      setError('Failed to save metadata');
      console.error('Save error:', err);
    } finally {
      setSaving(false);
    }
  };

  const getIcon = (type: string) => {
    switch (type) {
      case 'BOOK': return <BookIcon />;
      case 'MOVIE': return <MovieIcon />;
      case 'MUSIC': return <MusicIcon />;
      default: return <ImageIcon />;
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

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
      {/* Header */}
      <AppBar position="static" elevation={0}>
        <Toolbar>
          <IconButton edge="start" color="inherit" onClick={() => navigate(-1)}>
            <ArrowBackIcon />
          </IconButton>
          <Typography variant="h6" sx={{ flexGrow: 1, ml: 2 }}>
            Edit Metadata
          </Typography>
          <Button
            color="inherit"
            startIcon={<SaveIcon />}
            onClick={handleSave}
            disabled={saving}
          >
            {saving ? 'Saving...' : 'Save'}
          </Button>
        </Toolbar>
      </AppBar>

      {error && (
        <Alert severity="error" sx={{ m: 2 }}>
          {error}
        </Alert>
      )}

      <Box sx={{ p: 3 }}>
        <Grid container spacing={3}>
          {/* Cover/Poster */}
          <Grid item xs={12} md={4}>
            <Paper sx={{ p: 2, textAlign: 'center' }}>
              <Typography variant="h6" gutterBottom>
                Cover Art
              </Typography>
              <Box sx={{ 
                width: '100%',
                height: 300,
                bgcolor: 'secondary.main',
                borderRadius: 2,
                mb: 2,
                backgroundImage: thumbnailPath ? `url(${thumbnailPath})` : 'none',
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}>
                {!thumbnailPath && getIcon(mediaType)}
              </Box>
              <TextField
                fullWidth
                label="Cover URL"
                value={thumbnailPath}
                onChange={(e) => setThumbnailPath(e.target.value)}
                size="small"
              />
            </Paper>
          </Grid>

          {/* Metadata Form */}
          <Grid item xs={12} md={8}>
            <Paper sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Metadata Information
              </Typography>

              {/* Search Bar */}
              <Box sx={{ mb: 3, display: 'flex', gap: 2 }}>
                <TextField
                  fullWidth
                  label="Search for metadata"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder="Enter title, author, or keywords..."
                  onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                />
                <Button
                  variant="contained"
                  startIcon={<SearchIcon />}
                  onClick={handleSearch}
                  disabled={isSearching}
                  sx={{ minWidth: 120 }}
                >
                  {isSearching ? <CircularProgress size={20} /> : 'Search'}
                </Button>
              </Box>

              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Title"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    required
                  />
                </Grid>

                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label={mediaType === 'MOVIE' ? 'Director' : mediaType === 'MUSIC' ? 'Artist' : 'Author'}
                    value={author}
                    onChange={(e) => setAuthor(e.target.value)}
                  />
                </Grid>

                <Grid item xs={12} sm={6}>
                  <FormControl fullWidth>
                    <InputLabel>Media Type</InputLabel>
                    <Select
                      value={mediaType}
                      label="Media Type"
                      onChange={(e) => setMediaType(e.target.value as any)}
                    >
                      <MenuItem value="BOOK">Book</MenuItem>
                      <MenuItem value="MOVIE">Movie</MenuItem>
                      <MenuItem value="MUSIC">Music</MenuItem>
                      <MenuItem value="PODCAST">Podcast</MenuItem>
                      <MenuItem value="MAGAZINE">Magazine</MenuItem>
                      <MenuItem value="DOCUMENT">Document</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <TextField
                    fullWidth
                    label="Year"
                    type="number"
                    value={year}
                    onChange={(e) => setYear(e.target.value ? parseInt(e.target.value) : '')}
                  />
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Box>
                    <Typography component="legend" variant="body2" gutterBottom>
                      Rating
                    </Typography>
                    <Rating
                      value={rating}
                      onChange={(event, newValue) => setRating(newValue)}
                      precision={0.5}
                    />
                  </Box>
                </Grid>

                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Description"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    multiline
                    rows={4}
                  />
                </Grid>

                {/* Book-specific fields */}
                {mediaType === 'BOOK' && (
                  <>
                    <Grid item xs={12} sm={6}>
                      <TextField
                        fullWidth
                        label="ISBN"
                        value={isbn}
                        onChange={(e) => setIsbn(e.target.value)}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                      <TextField
                        fullWidth
                        label="Publisher"
                        value={publisher}
                        onChange={(e) => setPublisher(e.target.value)}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                      <TextField
                        fullWidth
                        label="Page Count"
                        type="number"
                        value={pageCount}
                        onChange={(e) => setPageCount(e.target.value ? parseInt(e.target.value) : '')}
                      />
                    </Grid>

                    <Grid item xs={12} sm={6}>
                      <TextField
                        fullWidth
                        label="Series"
                        value={series}
                        onChange={(e) => setSeries(e.target.value)}
                      />
                    </Grid>
                  </>
                )}

                <Grid item xs={12}>
                  <Typography variant="body2" gutterBottom>
                    Genres
                  </Typography>
                  <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                    {genre.map((g, index) => (
                      <Chip
                        key={index}
                        label={g}
                        onDelete={() => setGenre(genre.filter((_, i) => i !== index))}
                        color="primary"
                        variant="outlined"
                      />
                    ))}
                    <Chip
                      label="+ Add Genre"
                      onClick={() => {
                        const newGenre = prompt('Enter genre:');
                        if (newGenre) setGenre([...genre, newGenre]);
                      }}
                      variant="outlined"
                    />
                  </Box>
                </Grid>
              </Grid>
            </Paper>
          </Grid>
        </Grid>
      </Box>

      {/* Search Results Dialog */}
      <Dialog
        open={showSearchDialog}
        onClose={() => setShowSearchDialog(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          Search Results for "{searchQuery}"
        </DialogTitle>
        <DialogContent>
          <List>
            {searchResults.map((result, index) => (
              <React.Fragment key={result.id}>
                <ListItem
                  button
                  onClick={() => applySearchResult(result)}
                  sx={{ 
                    borderRadius: 2,
                    mb: 1,
                    '&:hover': { bgcolor: 'secondary.main' }
                  }}
                >
                  <ListItemAvatar>
                    <Avatar
                      src={result.cover || result.poster}
                      sx={{ bgcolor: 'primary.main' }}
                    >
                      {getIcon(mediaType)}
                    </Avatar>
                  </ListItemAvatar>
                  <ListItemText
                    primary={result.title}
                    secondary={
                      <Box>
                        <Typography variant="body2">
                          {result.author || result.director || result.artist} • {result.year}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          Source: {result.source}
                        </Typography>
                      </Box>
                    }
                  />
                </ListItem>
                {index < searchResults.length - 1 && <Divider />}
              </React.Fragment>
            ))}
            {searchResults.length === 0 && (
              <Typography variant="body2" color="text.secondary" sx={{ p: 2, textAlign: 'center' }}>
                No results found. Try a different search term.
              </Typography>
            )}
          </List>
        </DialogContent>
      </Dialog>
    </Box>
  );
};