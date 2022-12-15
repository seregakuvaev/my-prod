package com.boots.controller;

import com.boots.entity.Role;
import com.boots.entity.User;
import com.boots.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    private final UserService userService;

    @Autowired
    public RestController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/admin/getuser/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getMyData(@PathVariable long id) {
        return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/getuser/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getUser(@PathVariable long id) {
        return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/getallusers", method = RequestMethod.GET)
    public List<User> getAllData() {
        return userService.allUsers();
    }

    @RequestMapping(value = "/admin/saveuser", method = RequestMethod.POST)
    public void createUser(@RequestBody Map<String, String> userData) {
        User user = new User();
        user.setFirstname(userData.get("firstname"));
        user.setLastname(userData.get("lastname"));
        user.setAge(Integer.parseInt(userData.get("age")));
        user.setUsername(userData.get("username"));
        user.setPassword(userData.get("password"));
        String role = userData.get("role");
        if (Objects.equals(role, "ROLE_ADMIN")) {
            userService.saveUser(user, 2);
        } else {
            userService.saveUser(user, 1);
        }
    }

    @RequestMapping(value = "/admin/updateuser/{id}", method = RequestMethod.PUT)
    public void updateUser(@PathVariable long id, @RequestBody Map<String, String> userData) {
        Set<Role> roleSet = new HashSet<Role>();
        if (Objects.equals(userData.get("role"), "ROLE_USER")) {
            roleSet.add(new Role(1L, "ROLE_USER"));
        } else {
            roleSet.add(new Role(2L, "ROLE_ADMIN"));
        }
        userService.updateUser(id, userData.get("firstname"), userData.get("lastname"),
                Integer.parseInt(userData.get("age")), userData.get("username"), userData.get("password"), roleSet);
    }

    @RequestMapping(value = "/admin/deleteuser/{id}", method = RequestMethod.DELETE)
    public void deleteMyData(@PathVariable long id) {
        userService.deleteUser(id);
    }
}
