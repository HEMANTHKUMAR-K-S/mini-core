package org.demo.minicore.repository;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.demo.minicore.entity.UserLogs;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserLogRepo extends JpaRepository<UserLogs, Long> {

    long countByStatus(String status);

    List<UserLogs> findAllByTxId(String txId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
         SELECT u FROM UserLogs u
         WHERE u.Status = :status
         AND u.txId IS NULL
         ORDER BY u.id
    """)
    List<UserLogs> findPendingRecords(
            @Param("status") String status,
            Pageable pageable
    );

    @Transactional
    @Modifying
    @Query("DELETE FROM UserLogs u WHERE u.txId = :txId")
    int deleteAllByTxId(@Param("txId") String txId);
}
