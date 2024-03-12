package com.mysite.easytree.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.easytree.entity.Gene;

public interface GeneRepository extends JpaRepository<Gene, Integer>{
	Page<Gene> findAll(Pageable pageable);
}
