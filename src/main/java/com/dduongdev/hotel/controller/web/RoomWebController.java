package com.dduongdev.hotel.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rooms")
public class RoomWebController {

    @GetMapping
    public String listPage(Model model) {
        model.addAttribute("title", "Rooms - Hotel Booking");
        model.addAttribute("view", "rooms/list :: content");
        model.addAttribute("extraScript", "rooms/script :: script");
        return "fragments/layout";
    }
}
