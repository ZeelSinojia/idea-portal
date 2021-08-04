package com.ideaportal.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ideaportal.models.Comments;

public interface CommentsRepository extends JpaRepository<Comments, Long>
{

}
