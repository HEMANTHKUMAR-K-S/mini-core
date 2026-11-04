package org.demo.minicore.service;

import org.demo.minicore.entity.User;
import org.demo.minicore.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user){
        return userRepository.save(user);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUseById(Integer id){
        return userRepository.findById(id).orElseThrow(()-> new RuntimeException("user not found please try again"));
    }

    public User update(Integer id,User data){

       User user = getUseById(id);
       user.setName(data.getName());
       user.setAge(data.getAge());
       user.setLocation(data.getLocation());
       user.setSalary(data.getSalary());
        return userRepository.save(user);
    }

    public void delete(Integer id){
        userRepository.delete(getUseById(id));
    }
}
