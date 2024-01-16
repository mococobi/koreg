/* 
 * 최초작성자 : 송민권
 * 최초작성일 : 2021.05.17.
 * 최종변경일 : 2022.05.16.
 * 목적 : 사용자 배치 프로퍼티 정보
 * 개정이력 :
 * 	송민권, 2022.05.16, 최신화 및 주석 작성
*/
package com.batch.properties;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mococo.web.util.CustomProperties;
import com.mococo.web.util.PropertiesHolder;

/**
 * Batch Application Scope에서 사용할 porperties를 singleton으로 관리
 * <br/><b>History</b><br/>
 * <pre>
 * 2012. 2. 23. 최초작성
 * </pre>
 * @author 박형일
 * @version 1.0
 */
final public class BatchProperties {
	private static final Logger LOGGER = LogManager.getLogger(BatchProperties.class);
	
	private static final String DFT_PTH = "/com/batch/properties/";
	private static final String DFT_NAM = "batch-sql-" + CustomProperties.getProperty("portal.application.file.name") + ".xml";
	private static final String DFT_RTN = "";
	
	private static PropertiesHolder<BatchProperties> holder = new PropertiesHolder<BatchProperties>(
		new BatchProperties(),
		new PropertiesHolder.AbstractPropertiesLoader() {
			@Override
			public Properties load() {
				return PropertiesHolder.loadFromXml(DFT_PTH + DFT_NAM);  
			}			
		},
		BatchProperties.DFT_RTN
	);
	
	private BatchProperties() { 
		
	}
	
	
	/**
	 * <pre>
	 * 목적 : 쿼리 실행
	 * 매개변수 : 
	 * 	String pid
	 * 반환값 : java.lang.String
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	public static String getProperty(final String pid) { 
		return holder.getString(pid); 
	}
	
	
	/**
	 * <pre>
	 * 목적 : 호스트 IP 정보를 가지고 옴
	 * 매개변수 : 
	 * 	String pid
	 * 반환값 : java.lang.String
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	public static String getHostIp() {
		String result = null;
		try {
			result = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			LOGGER.error("!!! error", e);
		}
		
		return result;
	}
}
