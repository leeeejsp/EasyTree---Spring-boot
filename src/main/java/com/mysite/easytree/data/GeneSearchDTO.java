package com.mysite.easytree.data;

import java.time.LocalDateTime;

import com.mysite.easytree.entity.ScientificName;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@Data
public class GeneSearchDTO {

	private Long id;
	private String ncbiCode;
	private String fastaTitle;
	private String name;
	private LocalDateTime registerDay;
}
