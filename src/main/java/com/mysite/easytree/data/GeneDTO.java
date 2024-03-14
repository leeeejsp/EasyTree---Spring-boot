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
	
	public GeneDTO(
			String ncbiCode, 
			String dnaSequence,
			String fastaTitle, 
			ScientificName name){
		this.ncbiCode = ncbiCode;
		this.dnaSequence = dnaSequence;
		this.fastaTitle = fastaTitle;
		this.name = name;
	}
	
	public GeneDTO(){}
	
}
