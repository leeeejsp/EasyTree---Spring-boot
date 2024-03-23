package com.mysite.easytree.tool;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter
public class Muscle implements GeneAnalyticsTool {

	// 환경 설정
	private String osName;
	private String path = "C:\\Users\\이종섭\\Documents\\workspace-spring-tool-suite-4-4.19.1.RELEASE\\EasyTree\\src\\main\\resources\\static\\muscle\\";
	
	// 사용자가 입력한 내용
	private String[] ncbiCodes;
	private String dnaSequence;
	private String kindOfTree;
	
	// 작업 시간
	private long startTime;
	private long endTime;
	private long waitingTime;
	
	// 작업 파일
	private String inputFileName;
	private String inputFileContent;
	private String alignmentFileName;
	private String alignmentFileContent;
	private String treeFileName;
	private String treeFileContent;
	private String htmlFileName;
	private String htmlFileContent;
	
}
