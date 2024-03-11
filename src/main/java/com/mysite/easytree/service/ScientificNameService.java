package com.mysite.easytree.service;

import org.springframework.stereotype.Service;

import com.mysite.easytree.Repository.ScientificNameRepository;
import com.mysite.easytree.entity.ScientificName;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScientificNameService {

	private final ScientificNameRepository scientificNameRepository;
	
	//생성
	public void createName(String name) {
		ScientificName sciName = new ScientificName();
		sciName.setName(name);
		this.scientificNameRepository.save(sciName);
	}
	
	
}
