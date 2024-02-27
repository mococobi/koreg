/* 
 * 최초작성자 : 송민권
 * 최초작성일 : 2021.05.17.
 * 최종변경일 : 2022.05.16.
 * 목적 : 프로퍼티 관련 처리
 * 개정이력 :
 * 	송민권, 2022.05.16, 최신화 및 주석 작성
*/
package com.mococo.web.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Application Scope에서 사용할 porperties를 singleton으로 관리
 * @author mococo
 *
 */
final public class CustomProperties {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LogManager.getLogger(CustomProperties.class);
	
	/**
	 * DFT_PTH
	 */
	private static final String DFT_PTH = "/properties/";
	
	/**
	 * DFT_NAM
	 */
	private static final String DFT_NAM = "app-" + getHostIp() + ".xml";
	
	/**
	 * DFT_RTN
	 */
	private static final String DFT_RTN = "";
	
	/**
	 * holder
	 */
	private static PropertiesHolder<CustomProperties> holder = new PropertiesHolder<>(
		new CustomProperties(),
		new PropertiesHolder.AbstractPropertiesLoader() {
			@Override
			public Properties load() {
				logger.debug("=> config file : [{}][{}]", DFT_PTH, DFT_NAM);
				return PropertiesHolder.loadFromXml(DFT_PTH + DFT_NAM);  
			}			
		},
		DFT_RTN
	);
	
	
	/**
	 * getHostIp
	 * @return
	 */
	public static String getHostIp() {
		String result = null;
		try {
			result = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			logger.error("!!! error", e);
		}
		
		return result;
	}
	
	
	private CustomProperties() {
		
	}
	
	
	/**
	 * <pre>
	 * 목적 : 프로퍼티 정보 가지고 옴
	 * 매개변수 : 없음
	 * 반환값 : java.lang.String
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	public static String getProperty(final String pid) { 
		return holder.getString(pid); 
	}
	
}