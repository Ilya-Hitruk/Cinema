package com.example.cinema.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenresDto {
    private Integer id; // can be nullable
    @NonNull
    private String title;
}
