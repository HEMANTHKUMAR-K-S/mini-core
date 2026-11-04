package org.demo.minicore.controller;


import org.demo.minicore.entity.User;
import org.demo.minicore.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("create")
    public User createUser(@RequestBody User user){
    return userService.createUser(user);
    }

    @GetMapping("/get")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/get/{id}")
    public User getUserById(@PathVariable Integer id){
        return userService.getUseById(id);
    }

    @PutMapping("/update/{id}")
    public User update(@PathVariable Integer id,@RequestBody User user){
        return  userService.update(id,user);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Integer id){
        userService.delete(id);
    }
}
