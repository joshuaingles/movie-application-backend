package io.github.joshuaingles;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import io.github.joshuaingles.Entity.Movie;
import io.github.joshuaingles.Repository.MovieRepository;

@DataJpaTest
public class MovieRepositoryUnitTest {

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void findByTitleAndReleaseYear_returnsMovie_whenExists() {
        Movie m = Movie.builder().title("A").releaseYear("2025").genres(List.of("Action")).build();
        movieRepository.save(m);

        Movie found = movieRepository.findByTitleAndReleaseYear("A", "2025");

        assertNotNull(found);
        assertEquals("A", found.getTitle());
        assertEquals("2025", found.getReleaseYear());
        assertTrue(found.getGenres().contains("Action"));
    }

    @Test
    void findByTitleAndReleaseYear_returnsNull_whenNotFound() {
        Movie found = movieRepository.findByTitleAndReleaseYear("DoesNotExist", "9999");
        assertNull(found);
    }

    @Test
    void findByReleaseYearAndGenresContaining_returnsMatchingMovies() {
        Movie a = Movie.builder().title("A").releaseYear("2025").genres(List.of("Action", "Drama")).build();
        Movie b = Movie.builder().title("B").releaseYear("2025").genres(List.of("Comedy")).build();
        movieRepository.save(a);
        movieRepository.save(b);

        List<Movie> results = movieRepository.findByReleaseYearAndGenresContaining("2025", "Action");

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("A", results.get(0).getTitle());
    }

    @Test
    void findByReleaseYear_returnsMovies_forYear() {
        Movie a = Movie.builder().title("A").releaseYear("2024").genres(List.of("Action")).build();
        Movie b = Movie.builder().title("B").releaseYear("2024").genres(List.of("Drama")).build();
        movieRepository.save(a);
        movieRepository.save(b);

        List<Movie> results = movieRepository.findByReleaseYear("2024");

        assertNotNull(results);
        assertEquals(2, results.size());
    }

    @Test
    void findByGenresContaining_returnsMovies_withGenre() {
        Movie a = Movie.builder().title("A").releaseYear("2022").genres(List.of("Action", "Thriller")).build();
        Movie b = Movie.builder().title("B").releaseYear("2023").genres(List.of("Action")).build();
        Movie c = Movie.builder().title("C").releaseYear("2023").genres(List.of("Comedy")).build();
        movieRepository.save(a);
        movieRepository.save(b);
        movieRepository.save(c);

        List<Movie> results = movieRepository.findByGenresContaining("Action");

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(m -> m.getTitle().equals("A")));
        assertTrue(results.stream().anyMatch(m -> m.getTitle().equals("B")));
    }
}
