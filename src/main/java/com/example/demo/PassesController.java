package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ch.qos.logback.core.model.Model;

@org.springframework.stereotype.Controller
@RequestMapping("/passes")
public class PassesController {

    @GetMapping
    public String showPassesPage(@RequestParam("gameId") Long gameId, org.springframework.ui.Model model) {
        model.addAttribute("gameId", gameId); // Pass gameId to Thymeleaf
        return "passes"; // Load passes.html
    }
}
