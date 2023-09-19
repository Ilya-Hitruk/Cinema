package com.example.cinema.controller;

import com.example.cinema.service.MoviesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/movies")
public class MoviesController {
    private final MoviesServiceImpl moviesService;

    @Autowired
    public MoviesController(MoviesServiceImpl moviesService) {
        this.moviesService = moviesService;
    }

    @GetMapping
    public String showAll(Model model) {
        model.addAttribute("movies", moviesService.getAllMovies());
        return "movies/showAll";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("movie", moviesService.getMovieById(id));
        return "movies/show";
    }

    @GetMapping("/{id}/genres")
    public String movieGenres(@PathVariable("id") int id, Model model) {
        model.addAttribute("genres", moviesService.getMovieById(id).getGenres());
        return "movies/genres";
    }
}
