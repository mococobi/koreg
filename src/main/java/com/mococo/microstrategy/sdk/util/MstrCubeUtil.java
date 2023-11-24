/* 
 * 최초작성자 : 송민권
 * 최초작성일 : 2021.05.17.
 * 최종변경일 : 2022.05.16.
 * 목적 : MSTR 큐브 관련 처리
 * 개정이력 :
 * 	송민권, 2022.05.16, 최신화 및 주석 작성
*/
package com.mococo.microstrategy.sdk.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectsFactory;
import com.microstrategy.web.objects.admin.WebObjectsAdminException;
import com.microstrategy.web.objects.admin.monitors.CacheManipulator;
import com.microstrategy.web.objects.admin.monitors.CacheResults;
import com.microstrategy.web.objects.admin.monitors.CacheSource;
import com.microstrategy.web.objects.admin.monitors.Caches;
import com.microstrategy.web.objects.admin.monitors.CubeCache;
import com.microstrategy.web.objects.admin.monitors.EnumDSSXMLCubeAdminAction;
import com.microstrategy.web.objects.admin.monitors.EnumWebMonitorType;
import com.microstrategy.web.objects.admin.monitors.MonitorFilter;
import com.microstrategy.web.objects.admin.monitors.MonitorManipulationException;
import com.microstrategy.webapi.EnumDSSXMLCubeInfo;
import com.microstrategy.webapi.EnumDSSXMLLevelFlags;
import com.microstrategy.webapi.EnumDSSXMLMonitorFilterOperator;
import com.mococo.web.util.CustomProperties;


public class MstrCubeUtil {
	private static final Logger LOGGER = LogManager.getLogger(MstrCubeUtil.class);
	
	
	/**
	 * <pre>
	 * 목적 : MSTR 큐브를 삭제
	 * 매개변수 : 
	 * 	WebIServerSession adminSession
	 * 	String deleteCubeId
	 * 반환값 : java.util.List : List&lt;Map&lt;String, Object&gt;&gt;
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	public static List<Map<String, Object>> deleteCube(WebIServerSession adminSession, String deleteCubeId) throws WebObjectsAdminException, MonitorManipulationException {
		List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
		
		//create a WebObjectsFactory instance
		WebObjectsFactory factory = adminSession.getFactory();
		
		//get the cache source object for CUBE caches
		CacheSource cubeCS = (CacheSource) factory.getMonitorSource(EnumWebMonitorType.WebMonitorTypeCubeCache);
		cubeCS.setLevel(EnumDSSXMLLevelFlags.DssXmlDetailLevel);
		
		//Obtain the cache manipulator object for reports
		CacheManipulator cubeCM = cubeCS.getManipulator();
		
		CacheResults cubeResults;
		cubeResults = cubeCS.getCaches();
		int cubeCachecount = cubeResults.getCount();
		LOGGER.info("Total Cube caches [{}]", cubeCachecount);
		
		for(int j=0; j<cubeResults.size(); j++) {
			//Caches are group on Project level, so get the cache collection for each project
			Caches result = cubeResults.get(j);
			
			if(result.getProjectName().equalsIgnoreCase(CustomProperties.getProperty("mstr.default.project"))) {
				for(int i=0; i<result.getCount(); i++) {
					CubeCache cache = (CubeCache) result.get(i);
					
					if(cache.getCacheSourceID().equals(deleteCubeId)) {
						Map<String, Object> deleteCubeMap = new HashMap<String, Object>();
						deleteCubeMap.put("name", cache.getCacheSourceName());
						deleteCubeMap.put("id", cache.getCacheSourceID());
						deleteCubeMap.put("instanceId", cache.getID());
						rtnList.add(deleteCubeMap);
						
						MonitorFilter filter = cubeCM.newMonitorFilter();
						filter.add(EnumDSSXMLCubeInfo.DssXmlCubeInfoCubeDefId, EnumDSSXMLMonitorFilterOperator.DssXmlEqual, cache.getCacheSourceID());
						cubeCM.alter(result.getProjectDSSID(), EnumDSSXMLCubeAdminAction.DeleteCube, filter);
						
					}
				}
				cubeCM.submit();
			}
		}
		
		LOGGER.info("Delete Cube [{}]", rtnList);
		return rtnList;
	}
}
