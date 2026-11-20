package org.demo.minicore.repository;

import org.demo.minicore.entity.UserLogs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface UserLogRepo extends JpaRepository<UserLogs, Long> {

    long countByStatus(String status);

}
