package com.enpm809w.mims.Repository;

import com.enpm809w.mims.Common.Insurance;
import com.enpm809w.mims.Common.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, Long> {
    List<Insurance> findByUserId(Long userId, Pageable pageable);
}
