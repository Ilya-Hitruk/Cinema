package com.example.cinema.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class MoviesDto {
    private Integer id;
    @NonNull private String title;
    @NonNull private LocalDate releaseDate;
    @NonNull private String director;
    @NonNull private Integer durationMinutes;
    private String description;
    @NonNull private List<String> genres;
}
