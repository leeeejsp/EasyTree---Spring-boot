package com.mysite.easytree.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
	public List<Gene> getGeneList(){
		return this.geneRepository.findAll();
	}
	
	// 목록보기 페이징 적용
	public Page<Gene> getGeneList(int page){
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("registerDay")); // registerDay컬럼 기준으로 내림차순
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); // 페이지 당 10개씩 보여주기
		return this.geneRepository.findAll(pageable);
	}
	
	//수정
	
	
	//삭제
}
