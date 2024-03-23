package com.mysite.easytree.service;

import com.mysite.easytree.tool.GeneAnalyticsTool;

public interface GeneAnalyticsToolService {
	
	// 유전자 분석 tool 실행하기
	GeneAnalyticsTool executeTool(String ncbiCodes, String dnaSequence, String kindOfTree);
	
}
