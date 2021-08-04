package com.ideaportal.repos;

import com.ideaportal.models.ParticipantRoles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRoleRepository extends JpaRepository<ParticipantRoles, Long> {
}
