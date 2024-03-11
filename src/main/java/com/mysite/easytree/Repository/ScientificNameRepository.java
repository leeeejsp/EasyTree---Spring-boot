package com.mysite.easytree.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mysite.easytree.entity.ScientificName;

public interface ScientificNameRepository extends JpaRepository<ScientificName, Integer>{
	Optional<ScientificName> findByName(String name);
}
