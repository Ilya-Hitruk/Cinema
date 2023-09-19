package com.example.cinema.model.entity;

import lombok.*;

@Data
@AllArgsConstructor
public class GenresEntity {
    private Integer id;
    @NonNull
    private String genre;
}
