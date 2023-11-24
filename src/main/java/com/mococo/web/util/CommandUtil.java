/* 
 * 최초작성자 : 송민권
 * 최초작성일 : 2021.05.17.
 * 최종변경일 : 2022.05.16.
 * 목적 : 커맨드 실행 관련 처리
 * 개정이력 :
 * 	송민권, 2022.05.16, 최신화 및 주석 작성
*/
package com.mococo.web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CommandUtil {
	private static final Logger LOGGER = LogManager.getLogger(CommandUtil.class);
	
	
	public static class RunResult {
		private Map<String, Object> result = new HashMap<String, Object>();
		
		
		/**
		 * <pre>
		 * 목적 : EXIT 코드 설정
		 * 매개변수 : 
		 * 	int exitCode
		 * 반환값 : 없음
		 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
		 * </pre>
		 */
		public void setExitCode(int exitCode) { 
			result.put("exitCode", exitCode); 
		}
		
		/**
		 * <pre>
		 * 목적 : EXIT 코드 반환
		 * 매개변수 : 없음
		 * 반환값 : java.lang.Integer
		 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
		 * </pre>
		 */
		public int getExitCode() { 
			return (Integer)result.get("exitCode"); 
		}
		
		
		/**
		 * <pre>
		 * 목적 : 출력 결과 설정
		 * 매개변수 : 
		 * 	String output
		 * 반환값 : 없음
		 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
		 * </pre>
		 */
		public void setOutput(String output) { 
			result.put("output", output); 
		}
		
		
		/**
		 * <pre>
		 * 목적 : 출력 결과 반환
		 * 매개변수 : 없음
		 * 반환값 : java.lang.String
		 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
		 * </pre>
		 */
		public String getOutput() { 
			return (String)result.get("output"); 
		}
		
		
		/**
		 * <pre>
		 * 목적 : EXIT 코드, 출력 결과 설정
		 * 매개변수 : 
		 * 	int exitCode
		 * 	String output
		 * 반환값 : 없음
		 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
		 * </pre>
		 */
		public void setResult(int exitCode, String output) {
			setExitCode(exitCode);
			setOutput(output);
		}
	}
	
	
	/**
	 * <pre>
	 * 목적 : 명령어 및 파라미터를 문자열 배열로 구성하여 전달 커맨드 실행
	 * 매개변수 : 
	 * 	String[] command
	 * 반환값 : com.mococo.web.util.RunCommand.RunResult
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	public static final RunResult runCommand(String[] command) throws IOException, InterruptedException {
		
		LOGGER.info("=> runCommand - size : [{}]", command.length);
		
		for(int i=0; i<command.length; i++) {
			LOGGER.info("=> runCommand 파라미터 정보 {} data : [{}]", i, command[i]);
		}
		
		RunResult result = new RunResult();
		
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		Process process = builder.start();
		
		//상황에 맞추어 변경
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
//		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "EUC-KR"));
		StringBuffer buffer = new StringBuffer();
		
		String line = null;
		while((line = reader.readLine()) != null) {
			LOGGER.info("=> line : {}", line);
		}
		
		process.getOutputStream().close();
		int code = process.waitFor();
		
		result.setResult(code, buffer.toString());
		LOGGER.info("=> runCommand End : [{}]", code);
		
		return result;
	}
}
