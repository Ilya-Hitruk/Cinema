package com.example.cinema.controller;

import com.example.cinema.model.dto.GenresDto;
import com.example.cinema.service.GenreServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/genres")
public class GenresController {
    private final GenreServiceImpl genreService;

    public GenresController(GenreServiceImpl genreService) {
        this.genreService = genreService;
    }


    @GetMapping
    public String showAll(Model model) {
        model.addAttribute("genres", genreService.getAllGenres());
        return "genres/showAll";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("genre", genreService.getGenreById(id));
        return "genres/show";
    }

    @GetMapping("/create")
    public String newGenre(@ModelAttribute("genre") GenresDto genresDto) {
        return "genres/create";
    }

    @PostMapping
    public String create(@ModelAttribute("genre") GenresDto genresDto) {
        genreService.create(genresDto);
        return "redirect:/genres";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("genre", genreService.getGenreById(id));
        return "genres/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("genre") GenresDto genresDto, @PathVariable("id") int id) {
        genreService.update(id, genresDto);
        return "redirect:/genres";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        genreService.delete(id);
        return "redirect:/genres";
    }

    @GetMapping("/{id}/movies")
    public String findMoviesByGenre(@PathVariable("id") int id, Model model) {
        model.addAttribute("movies", genreService.findMoviesByGenreId(id));
        return "genres/movies";
    }
}