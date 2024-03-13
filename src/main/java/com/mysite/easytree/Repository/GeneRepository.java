package com.mysite.easytree.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.easytree.entity.Gene;

public interface GeneRepository extends JpaRepository<Gene, Integer>{
	Page<Gene> findAll(Pageable pageable);
	Optional<Gene> findByNcbiCode(String ncbiCode);
}
