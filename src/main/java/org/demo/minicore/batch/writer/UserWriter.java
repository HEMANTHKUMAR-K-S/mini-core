package org.demo.minicore.batch.writer;

import lombok.extern.slf4j.Slf4j;
import org.demo.minicore.entity.User;
import org.demo.minicore.repository.UserRepository;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class UserWriter implements ItemWriter<User> {


    private final UserRepository userRepository;

    public UserWriter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public void write(Chunk<? extends User> chunk) throws Exception {


        userRepository.saveAll(chunk.getItems());

       log.info("inserted Succssfully :"+chunk.size()+" user Into data base");
    }
}
