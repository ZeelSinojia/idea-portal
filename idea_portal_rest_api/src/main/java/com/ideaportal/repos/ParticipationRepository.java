package com.ideaportal.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ideaportal.models.ParticipationResponses;

public interface ParticipationRepository extends JpaRepository<ParticipationResponses, Long>
{

}
