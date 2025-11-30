package io.github.joshuaingles.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.github.joshuaingles.Entity.Movie;
import io.github.joshuaingles.Repository.MovieRepository;

@Service
public class MovieService {
    private MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    /**
     * Create Movie
     *
     * Uses the provided movie param to save a Movie entry to the H2 DB.
     *
     * @param movie Movie
     * @return Movie
     */
    public Movie createMovie(Movie movie) {
        Boolean movieExists = (movieRepository.findByTitleAndReleaseYear(movie.getTitle(), movie.getReleaseYear()) != null) 
            ? true : false;

        if (movieExists) {
            return null;
        } else {
            return movieRepository.save(movie);
        }
    }

    /**
     * Create Movies
     *
     * Uses the provided movies to save several Movie entries to the H2 DB.
     *
     * @param movies List<Movie>
     * @return Movie
     */
    public List<Movie> createMovies(List<Movie> movies) {
            List<Movie> result = new ArrayList<Movie>();
            for(Movie movie : movies) {
                Boolean movieExists = (movieRepository.findByTitleAndReleaseYear(movie.getTitle(), movie.getReleaseYear()) != null) 
                    ? true : false;

                if (!movieExists) {
                    result.add(movieRepository.save(movie));
                }
            }

        return result;
    }

    /**
     * Get Movie
     *
     * Retrieves a Movie from the H2 DB using the provided id.
     *
     * @param id Long
     * @return Movie
     */
    public Optional<Movie> getMovie(Long id) {
        return movieRepository.findById(id);
    }

    /**
     * Get Movies
     *
     * Retrieves all Movie resources from H2 DB.
     * 
     * Can provide releaseYear and/or genre to filter results.
     *
     * @param releaseYear String
     * @param genre String
     * @return List<Movie>
     */
    public List<Movie> getMovies(String releaseYear, String genre) {
        List<Movie> result;

        // Filters by both releaseYear and genre
        if(releaseYear != null && genre != null) {
            result = movieRepository.findByReleaseYearAndGenresContaining(releaseYear, genre);
        }
        // Filters by releaseYear 
        else if(releaseYear != null && genre == null) {
            result = movieRepository.findByReleaseYear(releaseYear);
        } 
        // Filters by genre
        else if(releaseYear == null && genre != null) {
            result = movieRepository.findByGenresContaining(genre);
        } 
        // Returns all movies
        else {
            result = movieRepository.findAll();
        }

        return result;
    }

    /**
     * Update Movie
     *
     * Updates a Movie entry by id with the fields provided in moviePatch.
     * 
     *
     * @param id Long
     * @param moviePatch Movie
     * @return Movie
     */
    public Movie updateMovie(Long id, Movie moviePatch) {
        Optional<Movie> result = movieRepository.findById(id);
        Movie movie = result.get();

        if (result.isPresent()) {
            if (moviePatch.getTitle() != null) {
            movie.setTitle(moviePatch.getTitle());
            }
            if (moviePatch.getReleaseYear() != null) {
                movie.setReleaseYear(moviePatch.getReleaseYear());
            }

            if (moviePatch.getGenres() != null) {
                movie.setGenres(moviePatch.getGenres());
            }

            return movieRepository.save(movie);
        } else {
            return null;
        }
    }

    /**
     * Delete Movie
     *
     * Deletes a Movie entry in the H2 DB using the provided id.
     *
     * @param id Long
     * @return void
     */
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }
}
