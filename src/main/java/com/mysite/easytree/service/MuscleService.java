package com.mysite.easytree.service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mysite.easytree.Repository.GeneRepository;
import com.mysite.easytree.entity.Gene;
import com.mysite.easytree.exception.DataNotFoundException;
import com.mysite.easytree.tool.Muscle;

@Service
public class MuscleService implements GeneAnalyticsToolService{

	

	@Autowired
	private GeneRepository geneRepository;
	
	


	
	
	@Override
	public Muscle executeTool(String ncbiCodes, String dnaSequence, String kindOfTree) {
		Muscle muscle = new Muscle();
		
		// 현재 프로그램이 실행되고 있는 os가 무엇인지 먼저 파악 및 
		setMuscleOS(muscle);
		
		// ncbiCodes, dnaSequence전처리
		preprocessingData(muscle, ncbiCodes, dnaSequence);
		
		// 트리 방식 설정
		muscle.setKindOfTree(kindOfTree);
		
		// 1. 파일 생성
		try {
			createFile(muscle);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 2. 생성한 파일 기반으로 muscle실행
		RunMuscleForAlignment(muscle);
		
		// 3. 정렬된 파일 기반으로 newick format파일 생성
		RunMuscleForTree(muscle);
		
		// 4. 정렬된 파일 기반으로 html파일 생성

		// 5. muscle 객체 반환
		
		return muscle;
	}

	//유전자 분석 tool위치
	private void setMuscleOS(Muscle muscle) {
		String osName = System.getProperty("os.name").toLowerCase();
		if(osName.contains("win")) {
			// 윈도우에서 실행할 경우
			muscle.setOsName("window");
		}
		else {
			// 윈도우가 아닌 환경에서 프로그램이 실행될 경우
			muscle.setOsName("notWindow");
			muscle.setPath("");
		}
	}
	
	// ncbiCodes, dnaSequence전처리
	private void preprocessingData(Muscle muscle, String ncbiCodes, String dnaSequence) {
		muscle.setNcbiCodes(ncbiCodes.split(","));
		
		muscle.setDnaSequence(
		dnaSequence.replaceAll("\\(","\\[")
					.replaceAll("\\)","\\]")
					.replaceAll(",","_")
					.replaceAll(";","")
					.replaceAll("'","")
					.replaceAll("<br>","\r\n") + "\r\n");
		
	}
	
	// 넘겨받은 데이터를 기반으로 파일생성
	private void createFile(Muscle muscle) throws IOException {
		String fileName = LocalDateTime.now()
								.toString()
								.replaceAll(":", "_")
								.replaceAll(" ", "_");
		
		// 파일 이름 설정하기
		muscle.setInputFileName(muscle.getPath() + fileName + "_input.txt");
		muscle.setAlignmentFileName(muscle.getPath() + fileName + "_align.txt");
		muscle.setTreeFileName(muscle.getPath() + fileName + "_tree.txt");
		muscle.setHtmlFileName(muscle.getPath() + fileName + "_html.txt");
		
		// 하나의 inputFileContent 만들기
		getFasta(muscle);
		
		// 하나의 inputFile 만들기
		FileWriter fin = null;
		try {
			fin = new FileWriter(muscle.getInputFileName());
			fin.write(muscle.getInputFileContent());
	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fin.close();
		}
		
	}
	
	// 사용자가 선택한 데이터와 직접 입력한 데이터를 하나의 내용으로 합친다.
	private void getFasta(Muscle muscle) {
		String[] selectedCodes = muscle.getNcbiCodes();
		StringBuilder selectedContent = new StringBuilder();
		for(String ncbiCode : selectedCodes) {
			Optional<Gene> _gene = geneRepository.findByNcbiCode(ncbiCode);
			if(_gene.isEmpty()) {
				throw new DataNotFoundException("존재하지 않는 ncbiCode가 있습니다.");
			}
			Gene gene = _gene.get();
			selectedContent.append(
					gene.getDnaSequence()
					.replaceAll("\\(","\\[")
					.replaceAll("\\)","\\]")
					.replaceAll(",","_")
					.replaceAll(";","")
					.replaceAll("'","")
					.replaceAll("<br>","\r\n") + "\r\n"
					);
		}
		muscle.setInputFileContent(selectedContent.toString() + muscle.getDnaSequence());
	}
	
	// inputFile기반으로 muscle실행시켜서 alignmentFile만들기
	private void RunMuscleForAlignment(Muscle muscle) {
		if(muscle.getOsName().equals("window")) {
			RunMuscleForAlignmentInWindow(muscle);
		}else {
			RunMuscleForAlignmentInNotWindow(muscle);
		}
	}
	
	private void RunMuscleForAlignmentInWindow(Muscle muscle) {
		String promptCommandLine = muscle.getPath() + "muscle5 -align " + muscle.getInputFileName() + " -output " + muscle.getAlignmentFileName();
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(promptCommandLine);
			
			//행(hang)이 걸려서 무한 대기하는 것을 방지하기 위해 stream을 닫아준 후 waitFor을 쓴다.
			p.getErrorStream().close();
			p.getInputStream().close();
			p.getOutputStream().close();
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void RunMuscleForAlignmentInNotWindow(Muscle muscle) {
		String promptCommandLine = muscle.getPath() + "muscle5 -align " + muscle.getInputFileName() + " -output " + muscle.getAlignmentFileName();
		ProcessBuilder p;
		try {
			p = new ProcessBuilder(new String[] {"/bin/sh", "-c", promptCommandLine});
			Process process = p.start();
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	// alignmentFile기반으로 muscle실행시켜서 treeFile만들기
	private void RunMuscleForTree(Muscle muscle) {
		if(muscle.getOsName().equals("window")) {
			RunMuscleForTreeInWindow(muscle);
		}else {
			RunMuscleForTreeInNotWindow(muscle);
		}
	}
	
	private void RunMuscleForTreeInWindow(Muscle muscle) {
		String promptCommandLine = null;
		Process p = null;
		if (muscle.getKindOfTree().equals("maximumLikelihood")) {
			promptCommandLine = muscle.getPath() + "muscle3 -in " + muscle.getAlignmentFileName() + " -tree1 " + muscle.getTreeFileName() + " -maxiters 1";
			try {
				p = Runtime.getRuntime().exec(promptCommandLine);
				
				//행(hang)이 걸려서 무한 대기하는 것을 방지하기 위해 stream을 닫아준 후 waitFor을 쓴다.
				p.getErrorStream().close();
				p.getInputStream().close();
				p.getOutputStream().close();
				p.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(muscle.getKindOfTree().equals("neighborJoining")) {
			promptCommandLine = muscle.getPath() + "muscle3 -in " + muscle.getAlignmentFileName() + " -tree1 " + muscle.getTreeFileName() + " -cluster neighborjoining";
			try {
				p = Runtime.getRuntime().exec(promptCommandLine);
				
				//행(hang)이 걸려서 무한 대기하는 것을 방지하기 위해 stream을 닫아준 후 waitFor을 쓴다.
				p.getErrorStream().close();
				p.getInputStream().close();
				p.getOutputStream().close();
				p.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			//UPGMA일때
			promptCommandLine = muscle.getPath() + "muscle3 -maketree -in " + muscle.getAlignmentFileName() + " -out " + muscle.getTreeFileName();
			try {
				p = Runtime.getRuntime().exec(promptCommandLine);
				
				//행(hang)이 걸려서 무한 대기하는 것을 방지하기 위해 stream을 닫아준 후 waitFor을 쓴다.
				p.getErrorStream().close();
				p.getInputStream().close();
				p.getOutputStream().close();
				p.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void RunMuscleForTreeInNotWindow(Muscle muscle) {
		String promptCommandLine = null;
		ProcessBuilder p;
		if (muscle.getKindOfTree().equals("maximumLikelihood")) {
			promptCommandLine = muscle.getPath() + "muscle3 -in " + muscle.getAlignmentFileName() + " -tree1 " + muscle.getTreeFileName() + " -maxiters 1";
			try {
				p = new ProcessBuilder(new String[] {"/bin/sh", "-c", promptCommandLine});
				Process process = p.start();
				process.waitFor(); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (muscle.getKindOfTree().equals("neighborJoining")) {
			promptCommandLine = muscle.getPath() + "muscle3 -maketree -in " + muscle.getAlignmentFileName() + " -out " + muscle.getTreeFileName() + " -cluster neighborjoining";
			try {
				p = new ProcessBuilder(new String[] {"/bin/sh", "-c", promptCommandLine});
				Process process = p.start();
				process.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else { 
			//UPGMA일때
			promptCommandLine = muscle.getPath() + "muscle3 -maketree -in " + muscle.getAlignmentFileName() + " -out " + muscle.getTreeFileName();
			try {
				p = new ProcessBuilder(new String[] {"/bin/sh", "-c", promptCommandLine});
				Process process = p.start();
				process.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
}
