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

 // ─── Customer Rentals ─────────────────────────────────────────
    @GetMapping("/customer-rentals")
    public String customerRentalsPage(HttpSession session, Model model) {
        String token = getToken(session);
        if (notLoggedIn(token)) return "redirect:/login";
        model.addAttribute("username", session.getAttribute("username"));
        return "customer-rentals";
    }

    @GetMapping("/customer-rentals/data")
    public String customerRentalsData(@RequestParam(defaultValue = "1") int customerId,
                                       HttpSession session, Model model) {
        String token = getToken(session);
        if (notLoggedIn(token)) return "redirect:/login";

        List<Map<String, Object>> data = Collections.emptyList();
        String errorMsg = null;
        try {
            data = apiService.getCustomerRentals(token, customerId);
            if (data == null) data = Collections.emptyList();
        } catch (Exception e) {
            errorMsg = "Error fetching data: " + e.getMessage();
        }

        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rentals", data);
        model.addAttribute("customerId", customerId);
        model.addAttribute("errorMsg", errorMsg);
        return "customer-rentals";
    }
 // ─── Payment Details ──────────────────────────────────────────
    @GetMapping("/payment-details")
    public String paymentDetails(HttpSession session, Model model) {
        String token = getToken(session);
        if (notLoggedIn(token)) return "redirect:/login";

        List<Map<String, Object>> data = Collections.emptyList();
        String errorMsg = null;
        try {
            data = apiService.getPaymentDetails(token);
            if (data == null) data = Collections.emptyList();
        } catch (Exception e) {
            errorMsg = "Error fetching data: " + e.getMessage();
        }

        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("payments", data);
        model.addAttribute("errorMsg", errorMsg);
        return "payment-details";
    }

    // ─── Rental Staff ─────────────────────────────────────────────
    @GetMapping("/rental-staff")
    public String rentalStaff(HttpSession session, Model model) {
        String token = getToken(session);
        if (notLoggedIn(token)) return "redirect:/login";

        List<Map<String, Object>> data = Collections.emptyList();
        String errorMsg = null;
        try {
            data = apiService.getRentalStaff(token);
            if (data == null) data = Collections.emptyList();
        } catch (Exception e) {
            errorMsg = "Error fetching data: " + e.getMessage();
        }

        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rentals", data);
        model.addAttribute("errorMsg", errorMsg);
        return "rental-staff";
    }

    // ─── Rental Films ─────────────────────────────────────────────
    @GetMapping("/rental-films")
    public String rentalFilms(HttpSession session, Model model) {
        String token = getToken(session);
        if (notLoggedIn(token)) return "redirect:/login";

        List<Map<String, Object>> data = Collections.emptyList();
        String errorMsg = null;
        try {
            data = apiService.getRentalFilms(token);
            if (data == null) data = Collections.emptyList();
        } catch (Exception e) {
            errorMsg = "Error fetching data: " + e.getMessage();
        }

        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rentals", data);
        model.addAttribute("errorMsg", errorMsg);
        return "rental-films";
    }
}