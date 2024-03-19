package com.mysite.easytree.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MuscleController {

	@PostMapping(value = "/muscle/calc")
	public String muscleCalc() {
		// => 이 함수에서는 최종적으로 newick format 내용과 정렬결과 html파일만 반환받으면 된다.
		
		
		// 1. 파라미터로 전달된 값들을 서비스 함수 매개변수로 전달한 후 createFile (운영체제가 리눅스 환경이면 권한 바꾸는 로직도 추가)
		
		// 2. excuteTool로 파일 생성
		
		// 3. 정렬 완료 후 newick format 만든 후 파일 내용 반환
		
		// 4. 결과 html파일 생성 후 파일 내용 반환
		
		
		// 5. 이 과정 속에서 생겼던 파일들 삭제
		
		// 6. 반환된 파일 내용들 model에 저장
		
		return "result";
	}
}
