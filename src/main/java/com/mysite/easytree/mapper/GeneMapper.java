package com.mysite.easytree.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.mysite.easytree.entity.Gene;

@Mapper
public interface GeneMapper {
	Gene selectByNcbiCode(String ncbiCode);
}
