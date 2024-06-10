package com.example.demo;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImageFileRepository extends JpaRepository<ImageFileEntity, Long> {
	//CONCATは文字の結合
	//SELECT * FROM text_files WHERE LOWER(name) LIKE LOWER('%search_term%');
	@Query("SELECT i FROM ImageFileEntity i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<ImageFileEntity> searchByName(@Param("name") String name);
}
