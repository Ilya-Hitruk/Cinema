package com.example.cinema.model.entity;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class MoviesEntity {
    @NonNull
    private Integer id;  // UUID
    @NonNull
    private String title;
    @NonNull
    private LocalDate releaseDate;
    @NonNull
    private String director;
    @NonNull
    private Integer durationMinutes;
    private String description;
    @NonNull
    private List<String> genres;


}
