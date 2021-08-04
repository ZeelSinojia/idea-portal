package com.ideaportal.dao;

import java.sql.ResultSet;
import java.util.List;

import com.ideaportal.models.IsDeleted;
import com.ideaportal.models.Themes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import com.ideaportal.constants.IdeaPortalQueryConstants;
import com.ideaportal.dao.utils.DAOUtils;
import com.ideaportal.models.Artifacts;
import com.ideaportal.models.Ideas;
import com.ideaportal.repos.ArtifactRepository;
import com.ideaportal.repos.IdeasRepository;

import javax.persistence.Id;

@Repository
public class ProductManagerDAO 
{
	@Autowired
	IdeasRepository ideasRepository;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	ArtifactRepository artifactRepository;
	
	@Autowired
	DAOUtils utils;
	public Ideas createNewIdeaObject(Ideas ideas)
	{

		return ideasRepository.save(ideas);
	}

	public Ideas updateIdeaObject(long ideaID, Ideas ideas, List<Artifacts> list)
	{

		Ideas dbIdea= ideasRepository.findById(ideaID).orElse(null);
		if (dbIdea != null) {
			dbIdea.setArtifacts(artifactRepository.saveAll(list));
			dbIdea.setIdeaName(ideas.getIdeaName());
			dbIdea.setIdeaDescription(ideas.getIdeaDescription());
			dbIdea.setIdeaModificationDate(ideas.getIdeaModificationDate());
			dbIdea.setModifiedBy(ideas.getModifiedBy());

			dbIdea = ideasRepository.save(dbIdea);
		}
		return dbIdea;
	}

	public List<Ideas> getMyIdeasList(long userID) 
	{
		return jdbcTemplate.execute(IdeaPortalQueryConstants.GET_MY_IDEAS, (PreparedStatementCallback<List<Ideas>>) ps -> {
			ps.setLong(1, userID);

			ResultSet resultSet=ps.executeQuery();

			return utils.buildList(resultSet);
		});
	}

	public void saveArtifacts(List<Artifacts> artifactList, long ideaID) {
		Ideas ideas = ideasRepository.findById(ideaID).orElse(null);
		if (ideas != null) {
			ideas.setArtifacts(artifactRepository.saveAll(artifactList));
		}
	}

    public Ideas deleteIdea(long ideaID) {
		Ideas ideas = ideasRepository.findById(ideaID).orElse(null);

		if (ideas != null){
			ideas.setIsDeleted(IsDeleted.TRUE);
			ideasRepository.save(ideas);
		}
		return ideas;
    }
}
