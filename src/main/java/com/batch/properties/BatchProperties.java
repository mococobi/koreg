/* 
 * 최초작성자 : 송민권
 * 최초작성일 : 2021.05.17.
 * 최종변경일 : 2022.05.16.
 * 목적 : 사용자 배치 프로퍼티 정보
 * 개정이력 :
 * 	송민권, 2022.05.16, 최신화 및 주석 작성
*/
package com.batch.properties;

import java.util.Properties;

import com.mococo.web.util.CustomProperties;
import com.mococo.web.util.PropertiesHolder;

/**
 * Batch Application Scope에서 사용할 porperties를 singleton으로 관리
 * @author mococo
 *
 */
final public class BatchProperties {
	
	/**
	 * 배치 파일 위치
	 */
	private static final String DFT_PTH = "/com/batch/properties/";
	
	/**
	 * 배치 파일 명
	 */
	private static final String DFT_NAM = "batch-sql-" + CustomProperties.getProperty("portal.application.file.name") + ".xml";
	
	/**
	 * holder
	 */
	private static PropertiesHolder<BatchProperties> holder = new PropertiesHolder<>(
		new BatchProperties(),
		new PropertiesHolder.AbstractPropertiesLoader() {
			@Override
			public Properties load() {
				return PropertiesHolder.loadFromXml(DFT_PTH + DFT_NAM);  
			}			
		},
		""
	);
	
	
	/**
	 * BatchProperties
	 */
	private BatchProperties() { 
		
	}
	
	
	/**
	 * 쿼리 실행
	 * @param pid
	 * @return
	 */
	public static String getProperty(final String pid) { 
		return holder.getString(pid); 
	}
	
}
