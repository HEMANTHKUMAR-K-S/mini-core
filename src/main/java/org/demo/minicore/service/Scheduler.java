package org.demo.minicore.service;


import org.demo.minicore.entity.Analytics;
import org.demo.minicore.entity.User;
import org.demo.minicore.repository.AnalyticsRepository;
import org.demo.minicore.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Scheduler {

private final UserRepository userRepository;
private final AnalyticsRepository analyticsRepository;

    public Scheduler(UserRepository userRepository, AnalyticsRepository analyticsRepository) {
        this.userRepository = userRepository;
        this.analyticsRepository = analyticsRepository;
    }

//    @Scheduled(fixedDelayString = "${app.run.intervl}")
//    @Scheduled(fixedRateString = "${app.run.interval}")
@Scheduled(fixedDelayString = "${app.run.interval}")
    public void scheduling(){

       List<User> users = userRepository.findAll();
//       System.out.println(users);

//       for(User user:users){
//           System.out.println(user.getName());
//       }

      int count=users.size();

       double avgSal=users.stream().mapToDouble(User::getSalary).average().orElse(0.0);

       double avgAge=users.stream().mapToDouble(User::getAge).average().orElse(0.0);

        Analytics analytics=new Analytics();

        analytics.setCount(count);
        analytics.setAvgSal(avgSal);
        analytics.setAvgAge(avgAge);

        analyticsRepository.save(analytics);

        System.out.println("count:"+count+"\n"+"AvgSal:"+avgSal+"\n"+"AvgAge:"+avgAge);

    }
}
