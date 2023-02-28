package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/signup")
public class SignupController {
    private UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String getSignupPage(Model model) {
        return "signup";
    }

    @PostMapping()
    public String signup(@ModelAttribute User user, Model model) {

        if (userService.usernameExists(user.getUsername())) {
            model.addAttribute("error", true);
            model.addAttribute("errorMsg", "Username already exists");
            return "signup";
        }

        int rowsAdded = userService.createUser(user);
        if (rowsAdded < 0) {
            model.addAttribute("errorMsg", "Error, try again");
            return "signup";
        }
        model.addAttribute("success", true);
        return "login";
    }
}
