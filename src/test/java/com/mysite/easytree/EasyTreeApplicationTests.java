package com.mysite.easytree;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mysite.easytree.Repository.GeneRepository;
import com.mysite.easytree.Repository.ScientificNameRepository;
import com.mysite.easytree.data.GeneDTO;
import com.mysite.easytree.entity.ScientificName;
import com.mysite.easytree.service.GeneService;
import com.mysite.easytree.service.ScientificNameService;

@SpringBootTest
class EasyTreeApplicationTests {

	@Autowired
	private ScientificNameService scientificNameService;
	
	@Autowired
	private ScientificNameRepository scientificNameRepository;
	
	@Autowired
	private GeneService geneService;
	
	@Autowired
	private GeneRepository geneRepository;
	
	
	//학명 생성 테스트
//	@Test
//	void nameCreate() {
//		String name = "Pinus Densiflora";
//		this.scientificNameService.createName(name);
//	}
	
	// 유전자 등록 테스트
	@Test
	void createGene() {
		
		for(int i=0; i<10; i++) {
			String name = "Pinus Densiflora";
			String dnaSequence = "abcdabcd";
			String fastaTitle = "testTitle";
			String ncbiCode = "testNcbiCode" + i;
			
			Optional<ScientificName> sciName = this.scientificNameRepository.findByName(name);
			if(sciName.isPresent()) {
				GeneDTO geneDto = new GeneDTO();
				ScientificName scientificName = sciName.get();
				geneDto.setNcbiCode(ncbiCode);;
				geneDto.setDnaSequence(dnaSequence);
				geneDto.setFastaTitle(fastaTitle);
				geneDto.setRegisterDay(LocalDateTime.now());
				geneDto.setName(scientificName);
				this.geneService.createGene(geneDto);
			} 
		}
	}
	
	// gene 테이블에서 학명꺼내기 테스트
//	@Test
//	void getScientificName() {
//		String name = "Pinus Densiflora";
//		int id = 1;
//		Optional<Gene> _gene = this.geneRepository.findById(id);
//		if(_gene.isPresent()) {
//			Gene gene = _gene.get();
//			assertEquals(gene.getName().toString(), name);
//		}
//	}
	
	

}
