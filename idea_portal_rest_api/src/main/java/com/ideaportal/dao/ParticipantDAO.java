package com.ideaportal.dao;

import com.ideaportal.dao.utils.DAOUtils;

import com.ideaportal.models.ParticipationResponses;
import com.ideaportal.repos.ParticipationRepository;

import java.sql.Timestamp;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ParticipantDAO {

    @Autowired
    DAOUtils utils;
    @Autowired
    ParticipationRepository participationRepository;

    public ParticipationResponses saveParticipantForIdea(ParticipationResponses participationResponses)
    {
    	participationResponses.setParticipationDate(Timestamp.valueOf(LocalDateTime.now()));
        participationResponses = participationRepository.save(participationResponses);
        participationResponses = utils.buildParticipationResponseObject(participationResponses);
        return participationResponses;
    }

}
