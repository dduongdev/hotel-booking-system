package com.dduongdev.hotel.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/room-types")
public class RoomTypeWebController {

    @GetMapping
    public String listPage(Model model) {
        model.addAttribute("title", "Room Types - Hotel Booking");
        model.addAttribute("view", "room-types/list :: content");
        model.addAttribute("extraScript", "room-types/script :: script");
        return "fragments/layout";
    }

    @GetMapping("/availability")
    public String availabilityPage(Model model) {
        model.addAttribute("title", "Check Availability - Hotel Booking");
        model.addAttribute("view", "room-types/availability :: content");
        model.addAttribute("extraScript", "room-types/availability-script :: script");
        return "fragments/layout";
    }
}
