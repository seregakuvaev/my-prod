package com.boots.controller;

import com.boots.entity.Logs;
import com.boots.entity.Role;
import com.boots.entity.User;
import com.boots.service.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalTime;
import java.util.*;

@org.springframework.stereotype.Controller
public class Controller {

    private final UserService userService;

    @Autowired
    public Controller(UserService userService) {
        this.userService = userService;
    }

    static Logger logger = Logger.getLogger(Controller.class.getName());

    private String usernameLogged;


    // DefaultControllers

    @GetMapping("/")
    public String index() {
        return "login";
    }

    @GetMapping("/fail")
    public String failAuth() {
        logger.error("Неудачная попытка авторизации неизветсно кем!!!!!!!!!!!!!");  // ловим ошибку авторизации
        return "login";
    }

    @GetMapping("/login")
    public String start() {
        return "login";
    }

    @RequestMapping("/default")
    public String defaultAfterLogin(HttpServletRequest request) {
        this.usernameLogged = request.getRemoteUser();
        User user = (User) userService.loadUserByUsername(usernameLogged);

        logger.info("Авторизован пользователь " + usernameLogged + " c доступом " +
                user.getRoles().toString().replace("[", "").replace("]", "") + ";");

        if (request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/admin/";
        }
        return "redirect:/news";
    }

    @GetMapping("/news")
    public String news(Model model) {
        User user = (User) userService.loadUserByUsername(usernameLogged);
        model.addAttribute("user", user);
        return "news";
    }

    @GetMapping("/error")
    public String error() {
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
        if (!userService.saveUser(userForm, 1)) {
            model.addAttribute("usernameError", "Пользователь с таким именем уже существует");
            return "registration";
        }

        logger.info("Новым пользователем создан аккаунт " + userForm.getUsername() + ";"); // возможно стоит добавить поля

        userService.addLogs("New user", "Create", null,  userForm.getUsername(), "New",
                userForm.getFirstname(), userForm.getLastname(), userForm.getAge(),
                userForm.getRoles().toString().replace("[","").replace("]",""));

        return "redirect:/login";
    }

    @GetMapping("/admin")
    public String userList(Model model) {
        User user = (User) userService.loadUserByUsername(usernameLogged);
        model.addAttribute("admin", user);
        model.addAttribute("allUsers", userService.allUsers());
        return "admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam(name = "userId") Long userId) {
        User deleteduser = userService.findUserById(userId);
        userService.deleteUser(userId);
        return "redirect:/admin";
    }

    @GetMapping("/add")
    public String addUser(@RequestParam(name = "addUsername") String username,
                          @RequestParam(name = "addPassword") String password,
                          @RequestParam(name = "addRole") String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        if (Objects.equals(role, "ROLE_ADMIN")) {
            userService.saveUser(user, 2);
        } else {
            userService.saveUser(user, 1);
        }
        return "redirect:/admin";
    }


    // RestControllers


    @RequestMapping(value = "/admin/getuser/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getMyData(@PathVariable long id) {
        return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/getuser/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getUser(@PathVariable long id) {
        return new ResponseEntity<>(userService.findUserById(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/getallusers", method = RequestMethod.GET)
    @ResponseBody
    public List<User> getAllData() {
        return userService.allUsers();
    }

    @RequestMapping(value = "/admin/saveuser", method = RequestMethod.POST)
    @ResponseBody
    public void createUser(@RequestBody Map<String, String> userData) {

        logger.info(usernameLogged + " создал пользователя " +
                userData.get("username") + " с правами " + userData.get("role") +";");

        userService.addLogs(usernameLogged, "Create", null, userData.get("username"), "New",
                userData.get("firstname"), userData.get("lastname"), Integer.parseInt(userData.get("age")), userData.get("role"));

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
    @ResponseBody
    public void updateUser(@PathVariable long id, @RequestBody Map<String, String> userData) {
        Set<Role> roleSet = new HashSet<Role>();
        if (Objects.equals(userData.get("role"), "ROLE_USER")) {
            roleSet.add(new Role(1L, "ROLE_USER"));
        } else {
            roleSet.add(new Role(2L, "ROLE_ADMIN"));
        }

        User preuser = userService.findUserById(id);

        StringBuilder updateInfo = new StringBuilder();
        Logs log = new Logs();

        log.setInstigator(usernameLogged);
        log.setChange("Update");
        log.setUsername_changed(preuser.getUsername());
        log.setTime(LocalTime.now());

        if (!Objects.equals(preuser.getFirstname(), userData.get("firstname"))){
            updateInfo.append("firstname ");
            log.setFirstname(userData.get("firstname"));
        }
        if (!Objects.equals(preuser.getLastname(), userData.get("lastname"))){
            updateInfo.append("lastname ");
            log.setLastname(userData.get("lastname"));
        }
        if (!Objects.equals(preuser.getAge(), Integer.parseInt(userData.get("age")))){
            updateInfo.append("age ");
            log.setAge(Integer.parseInt(userData.get("age")));
        }
        else {
            log.setAge(-1);
        }
        if (!Objects.equals(preuser.getRoles().toString().replace("[","")
                .replace("]",""), userData.get("role"))){
            updateInfo.append("role ");
            log.setRoles(userData.get("role"));
        }
        if (userData.get("password").length() > 0){
            updateInfo.append("password ");
            log.setPassword("Updated");
        }
        if (!Objects.equals(preuser.getUsername(), userData.get("username"))){
            updateInfo.append(", логин изменен на ").append(userData.get("username"));
            log.setUsername(userData.get("username"));
        }
        updateInfo.append(";");

        userService.addLogs(log); // добавляем лог в бд

        logger.info(usernameLogged + " изменил пользователю " + preuser.getUsername() + " поля " + updateInfo);

        userService.updateUser(id, userData.get("firstname"), userData.get("lastname"),
                Integer.parseInt(userData.get("age")), userData.get("username"), userData.get("password"), roleSet);
    }

    @RequestMapping(value = "/admin/deleteuser/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteMyData(@PathVariable long id) {

        User deleteuser = userService.findUserById(id);

        userService.addLogs(usernameLogged, "Delete", deleteuser.getUsername(), deleteuser.getUsername(), "Deleted",
                deleteuser.getFirstname(), deleteuser.getLastname(), deleteuser.getAge(),
                deleteuser.getRoles().toString().replace("[", "").replace("]", ""));

        logger.info(usernameLogged + " удалил пользователя " + deleteuser.getUsername() + " с правами " +
                deleteuser.getRoles().toString().replace("[", "").replace("]", "") + ";");

        userService.deleteUser(id);
    }


}