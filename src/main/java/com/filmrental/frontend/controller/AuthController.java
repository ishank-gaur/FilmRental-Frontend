package com.filmrental.frontend.controller;

import com.filmrental.frontend.service.ApiService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final ApiService apiService;

    public AuthController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model,
                            @RequestParam(value = "error", required = false) String error) {
        if (session.getAttribute("token") != null) {
            return "redirect:/dashboard";
        }
        if (error != null) {
            model.addAttribute("error", "Invalid username or password. Please try again.");
        }
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        String token = apiService.login(username, password);

        if (token == null || token.isBlank()) {
            redirectAttributes.addAttribute("error", "true");
            return "redirect:/login";
        }

        session.setAttribute("token", token);
        session.setAttribute("username", username);
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
