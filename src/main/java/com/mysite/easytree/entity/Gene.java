package com.mysite.easytree.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
@Entity
public class Gene {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	private String ncbiCode;
	
	@Column(columnDefinition = "TEXT")
	private String dnaSequence;
	
	private String fastaTitle;
	
	private LocalDateTime registerDay;
	
	@ManyToOne
	private ScientificName name;
}
