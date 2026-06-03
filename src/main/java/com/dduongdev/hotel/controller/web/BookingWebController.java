package com.dduongdev.hotel.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bookings")
public class BookingWebController {

    @GetMapping("/my")
    public String myBookingsPage(Model model) {
        model.addAttribute("title", "My Bookings - Hotel Booking");
        model.addAttribute("view", "bookings/my :: content");
        model.addAttribute("extraScript", "bookings/my-script :: script");
        return "fragments/layout";
    }
}
