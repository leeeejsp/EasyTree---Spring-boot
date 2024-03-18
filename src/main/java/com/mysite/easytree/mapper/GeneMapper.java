package com.mysite.easytree.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mysite.easytree.data.GeneSearchDTO;

@Mapper
public interface GeneMapper {
	
	// 검색용 쿼리
	List<GeneSearchDTO> selectByNcbiCodeorFastaTitleorName(@Param("ncbiCode") String ncbiCode, 
			@Param("fastaTitle") String fastaTitle, 
			@Param("name") String name,
			@Param("offset") long offset,
			@Param("pageSize") int pageSize);
	
	// 검색 결과 레코드 개수
	int countGene(@Param("ncbiCode") String ncbiCode, 
			@Param("fastaTitle") String fastaTitle, 
			@Param("name") String name);
}
