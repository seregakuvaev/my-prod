package com.boots.controller;

import com.boots.entity.Role;
import com.boots.entity.User;
import com.boots.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
                             @RequestParam(name = "Roles") String roles,
                             @ModelAttribute("user") User user) {
        User lastUser = userService.findUserById(Long.valueOf(userId));
        Set<Role> roleSet = new HashSet<Role> ();
        System.out.println(roles);
        System.out.println(roles.equals("1"));
        if (Objects.equals(roles, "1")){
            roleSet.add(new Role(1L, "ROLE_USER"));
        }else {
            roleSet.add(new Role(2L, "ROLE_ADMIN"));
        }
            userService.updateUser(Long.valueOf(userId), user.getUsername(), roleSet);
        return "redirect:/admin";
    }
}
