package com.boots.controller;

import com.boots.entity.Role;
import com.boots.entity.User;
import com.boots.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

@Controller
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String userList(Model model) {
        model.addAttribute("allUsers", userService.allUsers());
        return "admin";
    }

    @PostMapping("/admin")
    public String deleteUser(@RequestParam Long userId,
                             @RequestParam String action) {
        if (action.equals("delete")) {
            userService.deleteUser(userId);
        }
        return "redirect:/admin";
    }

    @PostMapping("/update")
    public String updateUser(@RequestParam(name = "userId") String userId,
                             @RequestParam(name = "Username") String username,
                             @RequestParam(name = "Roles") String roles,
                             @RequestParam(name = "Password") String password) {
        Set<Role> roleSet = new HashSet<Role> ();
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
