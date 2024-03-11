package com.mysite.easytree.data;

import java.time.LocalDateTime;

import com.mysite.easytree.entity.ScientificName;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GeneDTO {

	private int id;
	private String ncbiCode;
	private String dnaSequence;
	private String fastaTitle;
	private LocalDateTime registerDay;
	private ScientificName name;
	
	GeneDTO(int id, 
			String ncbiCode, 
			String dnaSequence,
			String fastaTitle, 
			LocalDateTime registerDay, 
			ScientificName name){
		this.id = id;
		this.ncbiCode = ncbiCode;
		this.dnaSequence = dnaSequence;
		this.fastaTitle = fastaTitle;
		this.registerDay = registerDay;
		this.name = name;
	}
	
	public GeneDTO(){}
	
}
