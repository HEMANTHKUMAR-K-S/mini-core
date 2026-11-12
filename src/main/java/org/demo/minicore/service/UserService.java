package org.demo.minicore.service;

import org.demo.minicore.entity.User;
import org.demo.minicore.repository.UserRepository;
import org.demo.minicore.rmq.ConsumerClient;
import org.demo.minicore.rmq.rmqServices;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class UserService {

    private final UserRepository userRepository;

    private final rmqServices rmqServices;

    private final ConsumerClient consumerClient;

    public UserService(UserRepository userRepository,rmqServices rmqServices,ConsumerClient consumerClient) {

        this.userRepository = userRepository;
        this.rmqServices=rmqServices;
        this.consumerClient=consumerClient;

    }

    public User createUser(User user){
       User savedUser= userRepository.save(user);

       rmqServices.createUser(savedUser.getName());

       return savedUser;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUseById(Integer id){
        return userRepository.findById(id).orElseThrow(()-> new RuntimeException("user not found please try again"));
    }

    public User update(Integer id,User data){

       User user = getUseById(id);


       String oldUsername=user.getName();

       String newUsername=data.getName();



       if (!(oldUsername.equals(newUsername))){


           rmqServices.createUser(newUsername);

           try {

               // Step 2: Start the new consumer
               String startResponse =
                       consumerClient.startConsumer(newUsername);

               System.out.println(startResponse);

               // Step 3: Stop the old consumer
               String stopResponse =
                       consumerClient.stopConsumer(oldUsername);

               System.out.println(stopResponse);

               // Step 4: Delete the old queue
               rmqServices.deleteUser(oldUsername);

           } catch (Exception e) {

               // Clean up the new queue if the workflow fails
               rmqServices.deleteUser(newUsername);

               throw new RuntimeException(
                       "Username update failed. Old username retained.",
                       e
               );
           }

        }
       user.setName(newUsername);
       user.setAge(data.getAge());
       user.setLocation(data.getLocation());
       user.setSalary(data.getSalary());
        return userRepository.save(user);
    }

    public void delete(Integer id){

        User user=getUseById(id);
        userRepository.delete(user);
        rmqServices.deleteUser(user.getName());

    }
}
