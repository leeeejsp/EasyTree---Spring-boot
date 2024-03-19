package com.mysite.easytree.service;

public interface GeneAnalyticsToolService {
	
	// 맨 처음 사용자가 선택한 데이터 기반으로 파일을 만드는 메소드
	void createFile();
	
	// 유전자 분석 tool 실행하기
	void executeTool();
	
}
