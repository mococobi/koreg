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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mococo.biz.exception.BizException;

/**
 * CommandUtil
 * @author mococo
 *
 */
public class CommandUtil {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LogManager.getLogger(CommandUtil.class);
	
	
    /**
     * CommandUtil
     */
    public CommandUtil() {
    	logger.debug("CommandUtil");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("CommandUtil");
    }
	
	
	/**
	 * RunResult
	 * @author mococo
	 *
	 */
	public static class RunResult {
		
		/**
		 * result
		 */
		private final Map<String, Object> result = new ConcurrentHashMap<>();
		
		
		/**
		 * EXIT 코드 설정
		 * @param exitCode
		 */
		public void setExitCode(final int exitCode) { 
			result.put("exitCode", exitCode); 
		}
		
		
		/**
		 * EXIT 코드 반환
		 * @return
		 */
		public int getExitCode() { 
			return (Integer)result.get("exitCode"); 
		}
		
		
		/**
		 * 출력 결과 설정
		 * @param output
		 */
		public void setOutput(final String output) { 
			result.put("output", output); 
		}
		
		
		/**
		 * 출력 결과 반환
		 * @return
		 */
		public String getOutput() { 
			return (String)result.get("output"); 
		}
		
		
		/**
		 * EXIT 코드, 출력 결과 설정
		 * @param exitCode
		 * @param output
		 */
		public void setResult(final int exitCode, final String output) {
			setExitCode(exitCode);
			setOutput(output);
		}
	}
	
	
	/**
	 * 명령어 및 파라미터를 문자열 배열로 구성하여 전달 커맨드 실행
	 * @param command
	 * @return
	 */
	public static final RunResult runCommand(final String... command) throws IOException, InterruptedException {
		
		/*
		logger.debug("=> runCommand - size : [{}]", command.length);
		for(int i=0; i<command.length; i++) {
			logger.debug("=> runCommand 파라미터 정보 [{}] data : [{}]", i, command[i]);
		}
		*/
		
		final RunResult result = new RunResult();
		
		final ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		final Process process = builder.start();
		
		//상황에 맞추어 변경
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"))) {
			final StringBuffer buffer = new StringBuffer();
			
			String line;
			while((line = reader.readLine()) != null) {
				logger.debug("=> line : {}", line);
			}
			
			//process.getOutputStream().close();
			final int code = process.waitFor();
			
			result.setResult(code, buffer.toString());
			logger.info("=> runCommand End : [{}]", code);
		} catch (IOException | InterruptedException e) {
			throw new BizException(e);
		}
		
		
		return result;
	}
}
