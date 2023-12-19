/* 
 * 최초작성자 : 송민권
 * 최초작성일 : 2021.05.17.
 * 최종변경일 : 2022.05.16.
 * 목적 : 로그 파일 삭제
 * 개정이력 :
 * 	송민권, 2022.05.16, 최신화 및 주석 작성
*/
package com.batch.util;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class RemoveLogFile {
	
	/**
	 * <pre>
	 * 목적 : 로그 삭제 배치를 실행
	 * 매개변수 : 없음
	 * 반환값 : 없음
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
//	@Scheduled(cron = "0 0 0 * * *")
	public void run() throws IOException, InterruptedException {
		/*
		String[] command = {CustomProperties.getProperty("log.file.delete.shell")};
		RunCommand.runCommand(command);
		*/
	}
	
}
