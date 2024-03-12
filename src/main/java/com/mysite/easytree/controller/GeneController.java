package com.mysite.easytree.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.easytree.entity.Gene;
import com.mysite.easytree.service.GeneService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class GeneController {

	private final GeneService geneService;
	
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
}
