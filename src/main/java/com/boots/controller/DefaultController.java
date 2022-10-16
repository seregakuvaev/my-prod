package com.boots.controller;

import com.boots.entity.User;
import com.boots.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Controller
public class DefaultController {
    private final UserService userService;
    private String username;
    @Autowired
    public DefaultController(UserService userService){
        this.userService = userService;
    }
    @GetMapping("/")
    public String start() {
        return "login";
    }
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @RequestMapping("/default")
    public String defaultAfterLogin(HttpServletRequest request) {
        this.username = request.getRemoteUser();
        if (request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/admin/";
        }
        return "redirect:/news";
    }

    @GetMapping("/news")
    public String news(Model model) {
        User user = (User) userService.loadUserByUsername(username);
        model.addAttribute("user", user);
        return "news";
    }

    @GetMapping("/error")
    public String error(Model model) {
        return "error";
    }
}