package io.github.joshuaingles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import io.github.joshuaingles.Controller.MovieController;
import io.github.joshuaingles.Entity.Movie;
import io.github.joshuaingles.Service.MovieService;

public class MovieControllerUnitTest {

    @Mock
    private MovieService movieService;

    private MovieController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        controller = new MovieController(movieService);
    }

    @Test
    void createMovie_success_returnsCreated() {
        Movie movie = Movie.builder().id(1L).title("A").releaseYear("2025").genres(List.of("Action")).build();
        when(movieService.createMovie(any(Movie.class))).thenReturn(movie);

        ResponseEntity<Movie> resp = controller.createMovie(movie);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertEquals(movie, resp.getBody());
        verify(movieService, times(1)).createMovie(movie);
    }

    @Test
    void createMovie_conflict_returns409() {
        Movie movie = Movie.builder().title("A").releaseYear("2025").genres(List.of("Action")).build();
        when(movieService.createMovie(any(Movie.class))).thenReturn(null);

        ResponseEntity<Movie> resp = controller.createMovie(movie);

        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertNull(resp.getBody());
        verify(movieService, times(1)).createMovie(movie);
    }

    @Test
    void createMovies_success_returnsCreated() {
        Movie a = Movie.builder().id(1L).title("A").releaseYear("2025").genres(List.of("Action")).build();
        Movie b = Movie.builder().id(2L).title("B").releaseYear("2024").genres(List.of("Drama")).build();
        List<Movie> input = List.of(a, b);
        when(movieService.createMovies(anyList())).thenReturn(input);

        ResponseEntity<List<Movie>> resp = controller.createMovies(input);

        assertEquals(HttpStatus.CREATED, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertEquals(2, resp.getBody().size());
        verify(movieService, times(1)).createMovies(input);
    }

    @Test
    void createMovies_conflict_returns409() {
        List<Movie> input = List.of();
        when(movieService.createMovies(anyList())).thenReturn(List.of());

        ResponseEntity<List<Movie>> resp = controller.createMovies(input);

        assertEquals(HttpStatus.CONFLICT, resp.getStatusCode());
        assertNull(resp.getBody());
        verify(movieService, times(1)).createMovies(input);
    }

    @Test
    void getMovie_found_returns200() {
        Movie movie = Movie.builder().id(1L).title("A").releaseYear("2025").genres(List.of("Action")).build();
        when(movieService.getMovie(1L)).thenReturn(Optional.of(movie));

        ResponseEntity<Movie> resp = controller.getMovie(1L);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(movie, resp.getBody());
        verify(movieService, times(1)).getMovie(1L);
    }

    @Test
    void getMovie_notFound_returns404() {
        when(movieService.getMovie(1L)).thenReturn(Optional.empty());

        ResponseEntity<Movie> resp = controller.getMovie(1L);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertNull(resp.getBody());
        verify(movieService, times(1)).getMovie(1L);
    }

    @Test
    void getMovies_withFilters_returns200() {
        Movie movie = Movie.builder().id(1L).title("A").releaseYear("2025").genres(List.of("Action")).build();
        List<Movie> result = List.of(movie);
        when(movieService.getMovies("2025", "Action")).thenReturn(result);

        ResponseEntity<List<Movie>> resp = controller.getMovies("2025", "Action");

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(result, resp.getBody());
        verify(movieService, times(1)).getMovies("2025", "Action");
    }

    @Test
    void getMovies_empty_returns404() {
        when(movieService.getMovies(null, null)).thenReturn(new ArrayList<>());

        ResponseEntity<List<Movie>> resp = controller.getMovies(null, null);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertNull(resp.getBody());
        verify(movieService, times(1)).getMovies(null, null);
    }

    @Test
    void updateMovie_success_returns200() {
        Movie patch = Movie.builder().releaseYear("9999").build();
        Movie updated = Movie.builder().id(1L).title("A").releaseYear("9999").genres(List.of("Action")).build();
        when(movieService.updateMovie(1L, patch)).thenReturn(updated);

        ResponseEntity<Movie> resp = controller.updateMovie(1L, patch);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(updated, resp.getBody());
        verify(movieService, times(1)).updateMovie(1L, patch);
    }

    @Test
    void updateMovie_notFound_returns404() {
        Movie patch = Movie.builder().releaseYear("9999").build();
        when(movieService.updateMovie(1L, patch)).thenReturn(null);

        ResponseEntity<Movie> resp = controller.updateMovie(1L, patch);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertNull(resp.getBody());
        verify(movieService, times(1)).updateMovie(1L, patch);
    }

    @Test
    void deleteMovie_always_returns200_and_callsService() {
        // no need to stub since void
        ResponseEntity<Movie> resp = controller.deleteMovie(1L);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        verify(movieService, times(1)).deleteMovie(1L);
    }
}
