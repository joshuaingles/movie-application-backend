package io.github.joshuaingles.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.joshuaingles.Entity.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>{
    Movie findByTitleAndReleaseYear(String title, String releaseYear);
    List<Movie> findByReleaseYearAndGenresContaining(String releaseYear, String genre);
    List<Movie> findByReleaseYear(String releaseYear);
    List<Movie> findByGenresContaining(String genre);
}
