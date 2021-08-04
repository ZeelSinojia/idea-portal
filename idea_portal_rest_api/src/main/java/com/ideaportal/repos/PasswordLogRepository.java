package com.ideaportal.repos;

import com.ideaportal.models.PasswordLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordLogRepository extends JpaRepository<PasswordLog, Long> {
}
