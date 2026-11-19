
package org.demo.minicore.batch.processor;

import org.demo.minicore.batch.model.CsvRecords;import org.demo.minicore.entity.User;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class UserProcessor implements ItemProcessor<CsvRecords, User> {

    @Override
    public User process(CsvRecords record) {

        // Validate CSV record
        if (record.getId() == null || record.getId() <= 0) {
            throw new IllegalArgumentException("Invalid User ID");
        }

        if (record.getName() == null || record.getName().isBlank()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }

        if (record.getAge() <= 0) {
            throw new IllegalArgumentException("Invalid User age");
        }

        if (record.getLocation() == null || record.getLocation().isBlank()) {
            throw new IllegalArgumentException("User location cannot be empty");
        }

        if (record.getSalary() < 0) {
            throw new IllegalArgumentException("Invalid User salary");
        }

        // Convert CSV DTO into User entity
        User user = new User();

        user.setId(record.getId());
        user.setName(record.getName());
        user.setAge(record.getAge());
        user.setLocation(record.getLocation());
        user.setSalary(record.getSalary());

        return user;
    }
}
