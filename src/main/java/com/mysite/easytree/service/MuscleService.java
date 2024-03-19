package com.mysite.easytree.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.mysite.easytree.Repository.GeneRepository;

public class MuscleService implements GeneAnalyticsToolService{

	private long startTime;
	private long endTime;
	private String inputFileName;
	private String inputFileContent;
	private String alignmentFileName;
	private String treeFileName;
	private String htmlFileName;
	private String path;

	@Autowired
	private GeneRepository geneRepository;
	
	@Override
	public void createFile() {
		setMusclePath();
		
		
	}

	@Override
	public void executeTool() {
		// TODO Auto-generated method stub
		
	}

	//유전자 분석 tool위치
	private void setMusclePath() {
		String osName = System.getProperty("os.name").toLowerCase();
		if(osName.contains("win")) {
			// 윈도우에서 실행할 경우
			path = "C:\\Users\\이종섭\\Documents\\workspace-spring-tool-suite-4-4.19.1.RELEASE\\EasyTree\\src\\main\\resources\\static\\muscle\\";
		}
		else {
			// 서버에 배포했을때(우분투) 실행할 경우
			// 진짜로 배포할 시 손 좀 봐야함
			path = "";
		}
	}
}
