package com.dduongdev.hotel.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Hotel Booking System");
        model.addAttribute("view", "index :: content");
        return "fragments/layout";
    }
}
