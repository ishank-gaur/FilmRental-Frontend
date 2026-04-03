package com.filmrental.frontend.controller;

import com.filmrental.frontend.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class ApiDataController {

    private final ApiService apiService;

    public ApiDataController(ApiService apiService) {
        this.apiService = apiService;
    }

    // ─── Helper ───────────────────────────────────────────────────
    private String getToken(HttpSession session) {
        return (String) session.getAttribute("token");
    }

    private boolean notLoggedIn(String token) {
        return token == null;
    }



    // ─── Film Actors ──────────────────────────────────────────────
    @GetMapping("/film-actors")
    public String filmActorsPage(HttpSession session, Model model) {
        String token = getToken(session);
        if (notLoggedIn(token)) return "redirect:/login";
        model.addAttribute("username", session.getAttribute("username"));
        return "film-actors";
    }

    @GetMapping("/film-actors/data")
    public String filmActorsData(@RequestParam(defaultValue = "1") int filmId,
                                 HttpSession session, Model model) {
        String token = getToken(session);
        if (notLoggedIn(token)) return "redirect:/login";

        List<Object[]> data = Collections.emptyList();
        String errorMsg = null;
        try {
            data = apiService.getFilmActors(token, filmId);
            if (data == null) data = Collections.emptyList();
        } catch (Exception e) {
            errorMsg = "Error fetching data: " + e.getMessage();
        }

        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("actors", data);
        model.addAttribute("filmId", filmId);
        model.addAttribute("errorMsg", errorMsg);
        return "film-actors";
    }


}