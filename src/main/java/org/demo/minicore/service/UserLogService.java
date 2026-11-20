package org.demo.minicore.service;


import org.demo.minicore.entity.UserLogs;
import org.demo.minicore.repository.UserLogRepo;
import org.springframework.stereotype.Service;

@Service
public class UserLogService {



    private final UserLogRepo userLogRepo;

    public UserLogService(UserLogRepo userLogRepo) {
        this.userLogRepo = userLogRepo;
    }

    public void saveLogs(String username,String status){

        UserLogs userLogs=new UserLogs();

        userLogs.setUsername(username);
        userLogs.setStatus(status);

        userLogRepo.save(userLogs);

    }
}
