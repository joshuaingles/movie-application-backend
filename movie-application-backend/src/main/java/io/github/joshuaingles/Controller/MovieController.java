package io.github.joshuaingles.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.joshuaingles.Entity.Movie;
import io.github.joshuaingles.Service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {
    private MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;

    }

    @Operation(
        summary = "Create a Movie",
        description = "Create a Movie based on the provided Movie in the Request Body",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Movie Created",
                content = @Content(
                    schema = @Schema(implementation = Movie.class)
                )
            ),
            @ApiResponse(
                responseCode = "409", 
                description = "Movie Already Exists"
            )
        }
    )
    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
        Movie result = movieService.createMovie(movie);

        if (result != null) {
                return new ResponseEntity<>(result, HttpStatus.CREATED);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
    }

    @Operation(
        summary = "Create several Movies",
        description = "Creates several Movies based on the provided List<Movie> in the Request Body",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Movies Created",
                content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = Movie.class))
                )
            ),
            @ApiResponse(
                responseCode = "409", 
                description = "Movie Already Exists"
            )
        }
    )
    @PostMapping("/bulk")
    public ResponseEntity<List<Movie>> createMovies(@RequestBody List<Movie> movies) {
        List<Movie> result = movieService.createMovies(movies);

        if (!result.isEmpty()) {
                return new ResponseEntity<>(result, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
    }

    @Operation(
        summary = "Get a Movie",
        description = "Retrieves a Movie based on the provided id",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Movie Retrieved",
                content = @Content(
                    schema = @Schema(implementation = Movie.class)
                )
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "Movie Not Found"
            )
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovie(@PathVariable Long id) {
        Optional<Movie> result = movieService.getMovie(id);

        if (result.isPresent()) {
                return new ResponseEntity<>(result.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
    }

    @Operation(
        summary = "Get All Movies - Optional Filters",
        description = "Retrieves all Movies with optional filters for releaseYear and genre",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Movies Retrieved",
                content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = Movie.class))
                )
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "Movies Not Found"
            )
        }
    )
    @GetMapping
    public ResponseEntity<List<Movie>> getMovies(@RequestParam(required = false) String releaseYear, 
            @RequestParam(required = false) String genre) {
        List<Movie> result = movieService.getMovies(releaseYear, genre);

        if (!result.isEmpty()) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
    }

    @Operation(
        summary = "Update Movie",
        description = "Updates a Movie by id and with data provided in the Request Body",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Movies Updated",
                content = @Content(
                    schema = @Schema(implementation = Movie.class)
                )
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "Movie Not Found"
            )
        }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie moviePatch) {
        Movie result = movieService.updateMovie(id, moviePatch);

        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } 
    }

    @Operation(
        summary = "Delete Movie",
        description = "Deletes a Movie by id and with data provided in the Request Body",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Movie Deleted"
            )
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Movie> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return new ResponseEntity<>(HttpStatus.OK);
    } 
}