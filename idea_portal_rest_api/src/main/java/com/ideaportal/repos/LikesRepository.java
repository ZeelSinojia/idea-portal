package com.ideaportal.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ideaportal.models.Likes;

public interface LikesRepository extends JpaRepository<Likes, Long>
{

}
