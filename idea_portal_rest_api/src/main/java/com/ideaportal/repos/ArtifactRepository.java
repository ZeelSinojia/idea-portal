package com.ideaportal.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ideaportal.models.Artifacts;

public interface ArtifactRepository extends JpaRepository<Artifacts, Long>
{

}
