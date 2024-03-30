package com.lms.userapi.api;

import com.lms.userapi.dto.User;
import com.lms.userapi.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/create")
    public void createUser(
            @RequestBody User user
    ){
        userService.createUser(
                user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getEmail()
        );
    }

    @PostMapping("/login")
    public Object login(
            @RequestParam String username, @RequestParam String password
    ){
        System.out.println(username);
        System.out.println(password);
        return userService.login(username, password);
    }
}
