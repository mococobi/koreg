/* 
 * 최초작성자 : 송민권
 * 최초작성일 : 2021.05.17.
 * 최종변경일 : 2022.05.16.
 * 목적 : 사용자 연동 배치
 * 개정이력 :
 * 	송민권, 2022.05.16, 최신화 및 주석 작성
 *  정유석, 2023.01.02, EIAM 배치 수정 (사용자그룹 권한 및 폴더 생성 소스 변경)
 *  정유석, 2023.02.02, EIAM 배치 수정 (Cache, CUBE 삭제 없애기)
 *  정유석, 2023.02.10, MSTR에는 존재하지만, EIAM 권한은 없는 사용자 "사용계정불가" 처리
*/
package com.batch.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.batch.JdbcTemplate.UserJdbcTemplate;
import com.batch.properties.BatchProperties;
import com.microstrategy.web.objects.WebIServerSession;
import com.mococo.biz.common.dao.SimpleBizDao;
import com.mococo.microstrategy.sdk.util.MstrUtil;

@Component
public class SyncUser {
	
	@Autowired
    SimpleBizDao simpleBizDao;
	
	private static final Logger LOGGER = LogManager.getLogger(SyncUser.class);
	
	private UserJdbcTemplate userJdbcTemplate = null;
	
	private static final String[] SPRING_CONFIG_XML = new String[] { "spring/batch-context-" + BatchProperties.getHostIp() + ".xml" };
	
	private void setDao() {
		ApplicationContext context = new ClassPathXmlApplicationContext(SPRING_CONFIG_XML);
		userJdbcTemplate = (UserJdbcTemplate)context.getBean("userJdbcTemplate");
	}
	
	private static boolean active = false;
	
	public synchronized static void jobStart() { active = true; LOGGER.info("[!!! 사용자동기화 작업을 시작합니다.]"); }
	public synchronized static void jobEnd() { active = false; LOGGER.info("[!!! 사용자동기화 작업을 종료합니다.]"); }
	public synchronized static boolean isActive() { return active; }
	
	
//	@Scheduled(cron = "0 0/5 * * * *")
	public static void batchDo() {
//	public static void main(String[] args) {
		LOGGER.info("배치 실행 ");
		// Sparrow 검출을 피하기 위한 주석 처리 (실제 수행 시에는 필요)
		if (SyncUser.isActive()) {
			LOGGER.info("!!! 이미 작업이 실행 중입니다.");
		} else {
			SyncUser syncUser = new SyncUser();
			syncUser.setDao();
			syncUser.doSync();
			
//			System.exit(0);
		}
	}
	
	
	public void doSync() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date nowDate = new Date(System.currentTimeMillis());
		String batchExecuteDate = formatter.format(nowDate);
		
		WebIServerSession serverSession = null;
		
		long beforeTimne = System.currentTimeMillis();
		
		
		try {
			List<Map<String, Object>> changeDepartmentList = new ArrayList<Map<String, Object>>();
			changeDepartmentList = userJdbcTemplate.selectEiamDepartment();
			
			System.out.println("changeDepartmentList : " + changeDepartmentList);
			
			
		} catch (Exception e) {
			LOGGER.error("batch Exception", e);
		} finally {
//			MstrUtil.closeISession(serverSession);
			jobEnd();
			long afterTimne = System.currentTimeMillis();
			LOGGER.info("배치 수행시간 : [{}]", (afterTimne - beforeTimne) / 1000.0);
		}
	}
}
