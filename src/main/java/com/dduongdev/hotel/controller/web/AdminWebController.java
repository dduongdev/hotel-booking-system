package com.dduongdev.hotel.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminWebController {

    @GetMapping("/dashboard")
    public String dashboardPage(Model model) {
        model.addAttribute("title", "Dashboard - Hotel Booking Manager");
        model.addAttribute("view", "admin/dashboard :: content");
        model.addAttribute("extraScript", "admin/dashboard-script :: script");
        return "fragments/layout";
    }

    @GetMapping("/bookings")
    public String bookingsPage(Model model) {
        model.addAttribute("title", "Manage Bookings - Hotel Booking");
        model.addAttribute("view", "admin/bookings :: content");
        model.addAttribute("extraScript", "admin/bookings-script :: script");
        return "fragments/layout";
    }

    @GetMapping("/rooms")
    public String roomsPage(Model model) {
        model.addAttribute("title", "Manage Rooms - Hotel Booking");
        model.addAttribute("view", "admin/rooms :: content");
        model.addAttribute("extraScript", "admin/rooms-script :: script");
        return "fragments/layout";
    }

    @GetMapping("/room-types")
    public String roomTypesPage(Model model) {
        model.addAttribute("title", "Manage Room Types - Hotel Booking");
        model.addAttribute("view", "admin/room-types :: content");
        model.addAttribute("extraScript", "admin/room-types-script :: script");
        return "fragments/layout";
    }
}
