package com.ideaportal.models;

import com.ideaportal.repos.ParticipantRoleRepository;
import com.ideaportal.repos.RolesRepository;
import com.ideaportal.repos.ThemesCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class Data {
    @Autowired
    RolesRepository rolesRepository;
    @Autowired
    ParticipantRoleRepository participantRoleRepository;
    @Autowired
    ThemesCategoryRepository themesCategoryRepository;

    @EventListener
    public void appReady(ApplicationReadyEvent event){
        rolesRepository.save(new Roles(1, "Client Partner"));
        rolesRepository.save(new Roles(2, "Product Manager"));
        rolesRepository.save(new Roles(3, "Participant"));

        participantRoleRepository.save(new ParticipantRoles(1, "Front-End Engineer"));
        participantRoleRepository.save(new ParticipantRoles(2, "Back-End Engineer"));
        participantRoleRepository.save(new ParticipantRoles(3, "Full Stack Engineer"));
        participantRoleRepository.save(new ParticipantRoles(4, "Software Engineer in Test (QA Engineer)"));
        participantRoleRepository.save(new ParticipantRoles(5, "DevOps Engineer"));
        participantRoleRepository.save(new ParticipantRoles(6, "Security Engineer"));
        participantRoleRepository.save(new ParticipantRoles(7, "Engineering Manager"));
        participantRoleRepository.save(new ParticipantRoles(8, "Product Manager"));
        participantRoleRepository.save(new ParticipantRoles(9, "Business Account Manager"));
        participantRoleRepository.save(new ParticipantRoles(10, "Procurement Manager"));

        themesCategoryRepository.save(new ThemesCategory(1, "Insurance"));
        themesCategoryRepository.save(new ThemesCategory(2, "E-Commerce"));
        themesCategoryRepository.save(new ThemesCategory(3, "Banking"));
        themesCategoryRepository.save(new ThemesCategory(4, "Booking Portal"));
        themesCategoryRepository.save(new ThemesCategory(5, "Mobility and Personal Communications Networks"));
        themesCategoryRepository.save(new ThemesCategory(6, "Healthcare"));
    }
}
