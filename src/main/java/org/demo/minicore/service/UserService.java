
package org.demo.minicore.service;

import lombok.extern.slf4j.Slf4j;
import org.demo.minicore.entity.User;
import org.demo.minicore.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {

        User savedUser = userRepository.save(user);

        return savedUser;
    }

    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    public User getUseById(Integer id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found. Please try again"));
    }

    public User update(Integer id, User data) {

        User user = getUseById(id);

        user.setName(data.getName());
        user.setAge(data.getAge());
        user.setLocation(data.getLocation());
        user.setSalary(data.getSalary());

        return userRepository.save(user);
    }

    public void delete(Integer id) {

        User user = getUseById(id);

        userRepository.delete(user);
    }
}
