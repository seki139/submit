package com.example.demo;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TextFileRepository extends JpaRepository<TextFileEntity, Long> {
	@Query("SELECT t FROM TextFileEntity t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<TextFileEntity> searchByName(@Param("name") String name);
}