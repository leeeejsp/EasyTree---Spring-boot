package com.mysite.easytree.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mysite.easytree.Repository.GeneRepository;
import com.mysite.easytree.Repository.ScientificNameRepository;
import com.mysite.easytree.data.GeneDTO;
import com.mysite.easytree.entity.Gene;
import com.mysite.easytree.entity.ScientificName;
import com.mysite.easytree.exception.DataNotFoundException;
import com.mysite.easytree.mapper.GeneMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeneService {

	private final GeneRepository geneRepository;
	private final ScientificNameRepository scientificNameRepository;
	private final ScientificNameService scientificNameService;
	
	//생성
	public void createGene(GeneDTO geneDto) {
		Gene gene = new Gene();
		gene.setNcbiCode(geneDto.getNcbiCode());
		gene.setFastaTitle(geneDto.getFastaTitle());
		gene.setDnaSequence(geneDto.getDnaSequence());
		gene.setRegisterDay(LocalDateTime.now());
		
		ScientificName name = geneDto.getName();
		if(checkScientificName(name)) {
			gene.setName(name);
		}else {
			//학명이 등록되어 있지 않다면 새로 등록
			scientificNameService.createName(name);
		}
		
		this.geneRepository.save(gene);
	}
	
	public void createGene(String ncbiCode, String fastaTitle, String dnaSequence, String name) {
		Gene gene = new Gene();
		gene.setNcbiCode(ncbiCode);
		gene.setFastaTitle(fastaTitle);
		gene.setDnaSequence(dnaSequence);
		gene.setRegisterDay(LocalDateTime.now());
		if(!checkScientificName(name)) {
			//학명이 등록되어 있지 않다면 새로 등록
			scientificNameService.createName(name);
		}
		gene.setName(scientificNameRepository.findByName(name).get());
		this.geneRepository.save(gene);
	}
	
	//읽기
	public Gene getGene(String ncbiCode) {
		Optional<Gene> _gene = this.geneRepository.findByNcbiCode(ncbiCode);
		if(_gene.isPresent()) {
			Gene gene = _gene.get();
			return gene;
		}else {
			throw new DataNotFoundException("Data is not found");
		}
	}
	
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
	
	//수정 -> id를 기반으로 수정
	public void updateGene(int id, String ncbiCode, String fastaTitle, String dnaSequence, String name) {
		Optional<Gene> _gene = this.geneRepository.findById(id);
		if(!_gene.isPresent()) {
			throw new DataNotFoundException("존재하지 않는 데이터입니다.");
		}
		//학명이 없는 경우
		if(!checkScientificName(name)) {
			this.scientificNameService.createName(name);
		}
		Gene gene = _gene.get();
		gene.setNcbiCode(ncbiCode);
		gene.setFastaTitle(fastaTitle);
		gene.setDnaSequence(dnaSequence);
		gene.setName(this.scientificNameRepository.findByName(name).get());
		this.geneRepository.save(gene);
	}
	
	
	//삭제
	public void deleteGene(String ncbiCode) {
		Optional<Gene> _gene = this.geneRepository.findByNcbiCode(ncbiCode);
		if(!_gene.isPresent()) {
			throw new DataNotFoundException("존재하지 않는 데이터입니다.");
		}
		Gene gene = _gene.get();
		this.geneRepository.delete(gene);
	}
	
	
	//새로 등록하려고 하는 학명이 학명테이블에 있는지 확인
	private boolean checkScientificName(ScientificName name) {
		Optional<ScientificName> _name = this.scientificNameRepository.findByName(name.toString());
		if(_name.isPresent())
			return true;
		return false;
	}
	
	private boolean checkScientificName(String name) {
		Optional<ScientificName> _name = this.scientificNameRepository.findByName(name);
		if(_name.isPresent())
			return true;
		return false;
	}
	
	//ncbi코드 중복 확인
	public boolean checkNcbiCode(String ncbiCode) {
		return this.geneRepository.existsByNcbiCode(ncbiCode);
	}

	@Autowired
	GeneMapper geneMapper;
	
	public Gene selectByNcbiCode(String ncbiCode) {
		Gene gene =  this.geneMapper.selectByNcbiCode(ncbiCode);
		System.out.println(gene);
		return gene;
	}
	
}
