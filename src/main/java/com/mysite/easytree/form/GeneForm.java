package com.mysite.easytree.form;

import com.mysite.easytree.entity.ScientificName;
import com.mysite.easytree.service.GeneService;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter @Setter
public class GeneForm {
	

	@NotBlank(message = "ncbi코드는 필수 항목입니다.")
	private String ncbiCode;
	
	@NotBlank(message = "dna서열은 필수 항목입니다.")
	private String dnaSequence;
	
	@NotBlank(message = "fastaTitle은 필수 항목입니다.")
	private String fastaTitle;
	
	@NotBlank(message = "학명은 필수 항목입니다.")
	private String name;
	
}
