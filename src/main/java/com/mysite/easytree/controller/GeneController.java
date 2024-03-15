package com.mysite.easytree.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.easytree.Repository.GeneRepository;
import com.mysite.easytree.entity.Gene;
import com.mysite.easytree.exception.DataNotFoundException;
import com.mysite.easytree.form.GeneForm;
import com.mysite.easytree.service.GeneService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class GeneController {

	private final GeneService geneService;
	private final GeneRepository geneRepository;
	

	@GetMapping(value = "/list")
	public String geneList(Model model,
			@RequestParam(value = "page", defaultValue = "0") int page) {
//		List<Gene> list = this.geneService.getGeneList();
		Page<Gene> paging = this.geneService.getGeneList(page);
		model.addAttribute("paging",paging);
		int startPage = (page+1) - ((page+1) - 1) % 10;
		model.addAttribute("startPage", startPage);
		return "list";
	}
	
	@GetMapping(value = "/list/{ncbiCode}")
	public String geneDetail(Model model,
			@PathVariable("ncbiCode") String ncbiCode) {
		//ncbi코드 없는 경우 처리해야함
		if(!this.geneService.checkNcbiCode(ncbiCode)) {
			String message = "존재하지 않는 데이터입니다.";
			model.addAttribute("message", message);
			return "error/404";
		}
		Gene gene = this.geneService.getGene(ncbiCode);
		gene.setDnaSequence(gene.getDnaSequence().replaceAll("<br>", "\r\n"));
		model.addAttribute("gene", gene);
		return "detail";
	}
	
	@GetMapping(value = "/create")
	public String geneCreate(GeneForm geneForm) {
		return "form/gene_form";
	}
	
	@PostMapping(value = "/create")
	public String geneCreate(@Valid GeneForm geneForm,
			BindingResult bindingResult, Model model) {
		if(bindingResult.hasErrors()) {
			return "form/gene_form";
		}
		if(geneService.checkNcbiCode(geneForm.getNcbiCode())) { // NCBI코드가 이미 존재하는 경우
			String message = "NCBI코드가 이미 존재합니다.";
			model.addAttribute("message", message);
			return "form/gene_form";
		}
		this.geneService.createGene(
				geneForm.getNcbiCode(),
				geneForm.getFastaTitle(),
				dnaSequenceReplace(geneForm.getDnaSequence()),
				geneForm.getName()
				);
		return "redirect:/list";
	}
	
	@GetMapping(value = "/list/{ncbiCode}/update")
	public String geneUpdate(GeneForm geneForm, 
			@PathVariable("ncbiCode") String ncbiCode,
			Model model) {
		Optional<Gene> _gene = this.geneRepository.findByNcbiCode(ncbiCode);
		if(!_gene.isPresent()) {
			String message = "NCBI코드가 존재하지 않습니다.";
			model.addAttribute("message", message);
			return "error/404";
		}
		Gene gene = _gene.get();
		geneForm.setNcbiCode(gene.getNcbiCode());
		geneForm.setFastaTitle(gene.getFastaTitle());
		geneForm.setDnaSequence(dnaSequenceReplaceForUpdate(gene.getDnaSequence()));
		geneForm.setName(gene.getName().toString());
		return "form/gene_form";
	}
	
	@PostMapping(value = "/list/{ncbiCode}/update")
	public String geneUpdate(@Valid GeneForm geneForm,
			BindingResult bindingResult,
			@PathVariable("ncbiCode") String ncbiCode,
			Model model) {
		if(bindingResult.hasErrors()) {
			return "form/gene_form";
		}
		//ncbi코드가 존재하는 경우
		if(geneService.checkNcbiCode(geneForm.getNcbiCode())) { // NCBI코드가 이미 존재하는 경우 = id가 다른 경우
			if(this.geneService.getGene(ncbiCode).getId() != 
					this.geneService.getGene(geneForm.getNcbiCode()).getId()) {
				String message = "NCBI코드가 이미 존재합니다.";
				model.addAttribute("message", message);
				return "form/gene_form";
			}
		}
		this.geneService.updateGene(this.geneService.getGene(ncbiCode).getId(),
									ncbiCode, 
									geneForm.getFastaTitle(), 
									geneForm.getDnaSequence(), 
									geneForm.getName());
		return String.format("redirect:/list/%s", ncbiCode);
	}
	
	private String dnaSequenceReplace(String dnaSequence) {
		return dnaSequence.replaceAll("\\(","\\[")
		.replaceAll("\\)","\\]")
		.replaceAll(",","_")
		.replaceAll(";","")
		.replaceAll("'","")
		.replaceAll("\r\n", "<br>");
	}
	
	private String dnaSequenceReplaceForUpdate(String dnaSequence) {
		return dnaSequence.replaceAll("<br>", "\r\n");
	}
}
