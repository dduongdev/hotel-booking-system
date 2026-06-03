package com.dduongdev.hotel.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthWebController {

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("title", "Login - Hotel Booking");
        model.addAttribute("view", "auth/login :: content");
        model.addAttribute("extraScript", "auth/login-script :: content");
        return "fragments/layout";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("title", "Register - Hotel Booking");
        model.addAttribute("view", "auth/register :: content");
        return "fragments/layout";
    }
}
