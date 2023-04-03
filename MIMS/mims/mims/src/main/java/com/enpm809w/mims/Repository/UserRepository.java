package com.enpm809w.mims.Repository;

import com.enpm809w.mims.Common.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    public User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.verificationCode = ?1")
    public User findByVerificationCode(String code);

    @Modifying
    @Query("UPDATE User u SET u.failedAttempt = ?1 WHERE u.email = ?2")
    public void updateFailedAttempts(int failAttempts, String email);
}
