package com.mysite.easytree.service;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

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
		
		// 현재 프로그램이 실행되고 있는 os가 무엇인지 먼저 파악 및 작업 시간 측정 시작
		start(muscle);
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
		RunMuscleForHTML(muscle);
		
		// 5. muscle 객체 반환하기 전 각각의 fileContent를 저장하고 있어야함
		readMuscleFiles(muscle);
		end(muscle);
		
		// 6. 만들었던 파일들 삭제
		deleteMuscleFiles(muscle);
		System.out.println(muscle.getInsertIntoJS());
		
		return muscle;
	}
	
	// 작업 시간 측정 메소드 start, end
	private void start(Muscle muscle) {
		long startTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기
		muscle.setStartTime(startTime);
	}
	
	private void end(Muscle muscle) {
		long endTime = System.currentTimeMillis();
		muscle.setEndTime(endTime);
		muscle.setWaitingTime(endTime - muscle.getStartTime());
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
	
	// alignmentFile기반으로 html파일을 만드려고함
	private void RunMuscleForHTML (Muscle muscle) {
		if(muscle.getOsName().equals("window")) {
			RunMuscleForHTMLInWindow(muscle);
		}else {
			RunMuscleForHTMLInNotWindow(muscle);
		}
	}
	
	private void RunMuscleForHTMLInWindow (Muscle muscle) {
		String promptCommandLine = muscle.getPath() + "muscle3 -in " + muscle.getAlignmentFileName() + " -htmlout " + muscle.getHtmlFileName();
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
	
	private void RunMuscleForHTMLInNotWindow (Muscle muscle) {
		String promptCommandLine = muscle.getPath() + "muscle3 -in " + muscle.getAlignmentFileName() + " -htmlout " + muscle.getHtmlFileName();
		try {
			Process p1 = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", promptCommandLine});
			p1.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void readMuscleFiles(Muscle muscle) {
		// newick format file 읽기
		readMuscleTreeFile(muscle);
		
		// 다중시퀀스 정렬 html file 읽기
		readMuscleHTMLFile(muscle);
	}
	
	private void readMuscleTreeFile(Muscle muscle) {
		String treeFileName = muscle.getTreeFileName();
		try {
			File file = new File(treeFileName);
			FileReader fr = new FileReader(file); 
			Scanner scan = new Scanner(fr);
			StringBuilder newickContent = new StringBuilder();
			while(scan.hasNextLine()) {
				newickContent.append(scan.nextLine() + "<br>");
			}
			scan.close();
			muscle.setTreeFileContent(newickContent.toString());
			muscle.setInsertIntoJS(newickContent.toString().replaceAll("<br>",""));
			muscle.setDownloadNewick("'" + newickContent.toString().replaceAll("<br>","") + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void readMuscleHTMLFile(Muscle muscle) {
		String htmlFileName = muscle.getHtmlFileName();
		try {
			File file = new File(htmlFileName);
			FileReader fr = new FileReader(file); 
			Scanner scan = new Scanner(fr);
			String content = "";
			while(scan.hasNextLine()) {
				content += scan.nextLine() + "<br>";
			}
			scan.close();
			muscle.setHtmlFileContent(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 유전자 분석을 위해 만들었던 파일들 삭제 
	private void deleteMuscleFiles(Muscle muscle) {
		String inputFileName = muscle.getInputFileName();
		String alignmentFileName = muscle.getAlignmentFileName();
		String treeFileName = muscle.getTreeFileName();
		String htmlFileName = muscle.getHtmlFileName();
		
		try {
			File inputFile = new File(inputFileName);
			if(inputFile.exists()) {
				inputFile.delete();
			}
			
			File alignmentFile = new File(alignmentFileName);
			if(alignmentFile.exists()) {
				alignmentFile.delete();
			}
			
			File treeFile = new File(treeFileName);
			if(treeFile.exists()) {
				treeFile.delete();
			}
			File htmlFile = new File(htmlFileName);
			if(htmlFile.exists()) {
				htmlFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
