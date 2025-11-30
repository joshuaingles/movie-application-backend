package io.github.joshuaingles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;

import io.github.joshuaingles.Entity.Movie;
import io.github.joshuaingles.Repository.MovieRepository;
import io.github.joshuaingles.Service.MovieService;

public class MovieServiceUnitTest {

    @Mock
    private MovieRepository movieRepository;

    private MovieService movieService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        movieService = new MovieService(movieRepository);
    }

    @Test
    void createMovie_success_returnsSavedMovie() {
        Movie input = Movie.builder().title("A").releaseYear("2025").genres(List.of("Action")).build();
        Movie saved = Movie.builder().id(1L).title("A").releaseYear("2025").genres(List.of("Action")).build();

        when(movieRepository.findByTitleAndReleaseYear("A", "2025")).thenReturn(null);
        when(movieRepository.save(any(Movie.class))).thenReturn(saved);

        Movie result = movieService.createMovie(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(movieRepository, times(1)).findByTitleAndReleaseYear("A", "2025");
        verify(movieRepository, times(1)).save(input);
    }

    @Test
    void createMovie_conflict_returnsNullWhenExists() {
        Movie existing = Movie.builder().id(5L).title("A").releaseYear("2025").genres(List.of("Action")).build();
        Movie input = Movie.builder().title("A").releaseYear("2025").genres(List.of("Action")).build();

        when(movieRepository.findByTitleAndReleaseYear("A", "2025")).thenReturn(existing);

        Movie result = movieService.createMovie(input);

        assertNull(result);
        verify(movieRepository, times(1)).findByTitleAndReleaseYear("A", "2025");
        verify(movieRepository, never()).save(any());
    }

    @Test
    void createMovies_savesOnlyNonExistingMovies() {
        Movie a = Movie.builder().title("A").releaseYear("2025").genres(List.of("Action")).build();
        Movie b = Movie.builder().title("B").releaseYear("2024").genres(List.of("Drama")).build();
        List<Movie> input = Arrays.asList(a, b);

        // both do not exist
        when(movieRepository.findByTitleAndReleaseYear("A", "2025")).thenReturn(null);
        when(movieRepository.findByTitleAndReleaseYear("B", "2024")).thenReturn(null);

        AtomicLong idGen = new AtomicLong(1);
        when(movieRepository.save(any(Movie.class))).thenAnswer(inv -> {
            Movie m = inv.getArgument(0);
            m.setId(idGen.getAndIncrement());
            return m;
        });

        List<Movie> result = movieService.createMovies(input);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(movieRepository, times(1)).save(a);
        verify(movieRepository, times(1)).save(b);
    }

    @Test
    void createMovies_skipsExistingAndSavesOthers() {
        Movie a = Movie.builder().title("A").releaseYear("2025").genres(List.of("Action")).build();
        Movie b = Movie.builder().title("B").releaseYear("2024").genres(List.of("Drama")).build();
        List<Movie> input = Arrays.asList(a, b);

        // a exists, b does not
        when(movieRepository.findByTitleAndReleaseYear("A", "2025")).thenReturn(new Movie());
        when(movieRepository.findByTitleAndReleaseYear("B", "2024")).thenReturn(null);

        when(movieRepository.save(any(Movie.class))).thenAnswer(inv -> {
            Movie m = inv.getArgument(0);
            m.setId(99L);
            return m;
        });

        List<Movie> result = movieService.createMovies(input);

        assertEquals(1, result.size());
        assertEquals(99L, result.get(0).getId());
        verify(movieRepository, times(0)).save(a);
        verify(movieRepository, times(1)).save(b);
    }

    @Test
    void getMovie_found_returnsOptionalWithMovie() {
        Movie m = Movie.builder().id(1L).title("A").releaseYear("2025").genres(List.of("Action")).build();
        when(movieRepository.findById(1L)).thenReturn(Optional.of(m));

        Optional<Movie> result = movieService.getMovie(1L);

        assertTrue(result.isPresent());
        assertEquals(m, result.get());
        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void getMovie_notFound_returnsEmptyOptional() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Movie> result = movieService.getMovie(1L);

        assertTrue(result.isEmpty());
        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void getMovies_withBothFilters_callsReleaseYearAndGenreRepoMethod() {
        Movie m = Movie.builder().id(1L).title("A").releaseYear("2025").genres(List.of("Action")).build();
        when(movieRepository.findByReleaseYearAndGenresContaining("2025", "Action")).thenReturn(List.of(m));

        List<Movie> result = movieService.getMovies("2025", "Action");

        assertEquals(1, result.size());
        assertEquals(m, result.get(0));
        verify(movieRepository, times(1)).findByReleaseYearAndGenresContaining("2025", "Action");
    }

    @Test
    void getMovies_withReleaseYearOnly_callsReleaseYearRepoMethod() {
        Movie m = Movie.builder().id(2L).title("B").releaseYear("2024").genres(List.of("Drama")).build();
        when(movieRepository.findByReleaseYear("2024")).thenReturn(List.of(m));

        List<Movie> result = movieService.getMovies("2024", null);

        assertEquals(1, result.size());
        assertEquals(m, result.get(0));
        verify(movieRepository, times(1)).findByReleaseYear("2024");
    }

    @Test
    void getMovies_withGenreOnly_callsGenreRepoMethod() {
        Movie m = Movie.builder().id(3L).title("C").releaseYear("2023").genres(List.of("Comedy")).build();
        when(movieRepository.findByGenresContaining("Comedy")).thenReturn(List.of(m));

        List<Movie> result = movieService.getMovies(null, "Comedy");

        assertEquals(1, result.size());
        assertEquals(m, result.get(0));
        verify(movieRepository, times(1)).findByGenresContaining("Comedy");
    }

    @Test
    void getMovies_noFilters_callsFindAll() {
        Movie m = Movie.builder().id(4L).title("D").releaseYear("2022").genres(List.of("Thriller")).build();
        when(movieRepository.findAll()).thenReturn(List.of(m));

        List<Movie> result = movieService.getMovies(null, null);

        assertEquals(1, result.size());
        assertEquals(m, result.get(0));
        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void updateMovie_success_updatesFieldsAndReturnsSaved() {
        Movie existing = Movie.builder().id(1L).title("A").releaseYear("2000").genres(List.of("Action")).build();
        Movie patch = Movie.builder().releaseYear("9999").build();
        when(movieRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(movieRepository.save(any(Movie.class))).thenAnswer(inv -> inv.getArgument(0));

        Movie result = movieService.updateMovie(1L, patch);

        assertNotNull(result);
        assertEquals("9999", result.getReleaseYear());
        assertEquals("A", result.getTitle()); // unchanged
        verify(movieRepository, times(1)).findById(1L);
        verify(movieRepository, times(1)).save(existing);
    }

    @Test
    void updateMovie_notFound_throwsNoSuchElementException() {
        Movie patch = Movie.builder().releaseYear("9999").build();
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> movieService.updateMovie(1L, patch));
        verify(movieRepository, times(1)).findById(1L);
        verify(movieRepository, never()).save(any());
    }

    @Test
    void deleteMovie_callsRepositoryDelete() {
        doNothing().when(movieRepository).deleteById(1L);

        movieService.deleteMovie(1L);

        verify(movieRepository, times(1)).deleteById(1L);
    }
}
