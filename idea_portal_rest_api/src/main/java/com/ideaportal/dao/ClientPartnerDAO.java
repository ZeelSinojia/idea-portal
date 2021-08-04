package com.ideaportal.dao;

import com.ideaportal.constants.IdeaPortalQueryConstants;





import com.ideaportal.dao.utils.DAOUtils;

import com.ideaportal.models.*;
import com.ideaportal.repos.ArtifactRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import com.ideaportal.repos.ThemesRepository;

import java.sql.ResultSet;
import java.util.List;



@Repository
public class ClientPartnerDAO {
	@Autowired
	ThemesRepository themeRepository;

	@Autowired
	DAOUtils utils;

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	ArtifactRepository artifactRepository;
	
	public Themes saveTheme(Themes themes)
	{

		return themeRepository.save(themes);
	}

	public Themes update(long themeID, Themes themes, List<Artifacts> list)
	{
		Themes dbTheme= themeRepository.findById(themeID).orElse(null);

		if(dbTheme != null){
			dbTheme.setThemeName(themes.getThemeName());
			dbTheme.setThemeDescription(themes.getThemeDescription());
			dbTheme.setThemeModificationDate(themes.getThemeModificationDate());
			dbTheme.setModifiedBy(themes.getModifiedBy());
			dbTheme.setThemesCategory(themes.getThemesCategory());

			dbTheme.setArtifacts(artifactRepository.saveAll(list));
			dbTheme = themeRepository.save(dbTheme);
		}



		return dbTheme;
	}

	public List<ParticipationResponses> getParticipantResponsesListForIdea(long ideaID)
	{
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_ALL_PARTICIPATION_RESPONSES_FOR_IDEA, (PreparedStatementCallback<List<ParticipationResponses>>) ps -> {
			ps.setLong(1, ideaID);

			ResultSet resultSet=ps.executeQuery();

			return utils.buildParticipationResponsesList(resultSet);
		});
	}

	public List<Themes> getMyThemesList(long userID) 
	{
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_MY_THEMES, (PreparedStatementCallback<List<Themes>>) ps -> {
			ps.setLong(1, userID);

			ResultSet resultSet=ps.executeQuery();

			return utils.buildThemesList(resultSet);
		});
	}

	public void saveArtifacts(List<Artifacts> artifactList, long themeID) {
		Themes themes = themeRepository.findById(themeID).orElse(null);
		if (themes != null) {
			themes.setArtifacts(artifactRepository.saveAll(artifactList));
		}
	}

	public Themes deleteTheme(long themeID) {
		Themes themes = themeRepository.findById(themeID).orElse(null);

		if (themes != null){
			themes.setIsDeleted(IsDeleted.TRUE);
			themeRepository.save(themes);
		}
		return themes;
	}
}
