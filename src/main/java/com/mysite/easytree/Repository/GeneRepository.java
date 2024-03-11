package com.mysite.easytree.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.easytree.entity.Gene;

public interface GeneRepository extends JpaRepository<Gene, Integer>{

}
