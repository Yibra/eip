package com.fsg.fsgdata.eiprestlet.repositories;

import com.fsg.fsgdata.eiprestlet.entities.ResourceSql;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceSqlRepository extends JpaRepository<ResourceSql, Long> {
    List<ResourceSql> findByResource(String resource);
}
