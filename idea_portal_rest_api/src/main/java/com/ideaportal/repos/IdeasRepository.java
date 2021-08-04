package com.ideaportal.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ideaportal.models.Ideas;

public interface IdeasRepository extends JpaRepository<Ideas, Long>
{

}
