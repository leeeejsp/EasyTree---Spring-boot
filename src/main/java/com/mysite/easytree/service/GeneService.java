package com.mysite.easytree.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.mysite.easytree.Repository.GeneRepository;
import com.mysite.easytree.data.GeneDTO;
import com.mysite.easytree.entity.Gene;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeneService {

	private final GeneRepository geneRepository;
	
	//생성
	public void createGene(GeneDTO geneDto) {
		Gene gene = new Gene();
		gene.setNcbiCode(geneDto.getNcbiCode());
		gene.setFastaTitle(geneDto.getFastaTitle());
		gene.setDnaSequence(geneDto.getDnaSequence());
		gene.setRegisterDay(LocalDateTime.now());
		gene.setName(geneDto.getName());
		this.geneRepository.save(gene);
	}
	
	//읽기
	
	
	//목록보기
	
	
	//수정
	
	
	//삭제
}
