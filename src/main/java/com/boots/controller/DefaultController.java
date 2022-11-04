package com.boots.controller;

import com.boots.entity.Role;
import com.boots.entity.User;
import com.boots.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute("userForm") @Valid User userForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "registration";
        }
        if (!userService.saveUser(userForm, 1)){
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            return "registration";
        }
        return "redirect:/";
    }

    @GetMapping("/admin")
    public String userList(Model model) {
        model.addAttribute("allUsers", userService.allUsers());
        return "admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam(name = "userId") Long userId) {
        userService.deleteUser(userId);
        return "redirect:/admin";
    }

    @PostMapping("/update")
    public String updateUser(@RequestParam(name = "userId") String userId,
                             @RequestParam(name = "Username") String username,
                             @RequestParam(name = "Roles") String roles,
                             @RequestParam(name = "Password") String password) {
        Set<Role> roleSet = new HashSet<Role>();
        if (Objects.equals(roles, "ROLE_USER")){
            roleSet.add(new Role(1L, "ROLE_USER"));
        }else {
            roleSet.add(new Role(2L, "ROLE_ADMIN"));
        }
        userService.updateUser(Long.valueOf(userId), username, roleSet, password);
        return "redirect:/admin";
    }

    @GetMapping("/add")
    public String addUser(@RequestParam(name = "addUsername") String userName,
                          @RequestParam(name = "addPassword") String password,
                          @RequestParam(name = "addRole") String role) {
        User user = new User();
        user.setUsername(userName);
        user.setPassword(password);
        if (Objects.equals(role, "ROLE_ADMIN")){
            userService.saveUser(user, 2);
        } else {
            userService.saveUser(user, 1);
        }
        return "redirect:/admin";
    }
}