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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

/**
 * MstrCubeUtil
 * @author mococo
 *
 */
public class MstrCubeUtil {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LogManager.getLogger(MstrCubeUtil.class);
	
	
    /**
     * MstrCubeUtil
     */
    public MstrCubeUtil() {
    	logger.debug("MstrCubeUtil");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("MstrCubeUtil");
    }
	
	
	/**
	 * MSTR 큐브를 삭제
	 * @param adminSession
	 * @param deleteCubeId
	 * @return
	 * @throws WebObjectsAdminException
	 * @throws MonitorManipulationException
	 */
	public static List<Map<String, Object>> deleteCube(final WebObjectsFactory factory, final String deleteCubeId) throws WebObjectsAdminException, MonitorManipulationException {
		final List<Map<String, Object>> rtnList = new ArrayList<>();
		
		//create a WebObjectsFactory instance
//		final WebObjectsFactory factory = adminSession.getFactory();
		
		//get the cache source object for CUBE caches
		final CacheSource cubeCS = (CacheSource) factory.getMonitorSource(EnumWebMonitorType.WebMonitorTypeCubeCache);
		cubeCS.setLevel(EnumDSSXMLLevelFlags.DssXmlDetailLevel);
		
		//Obtain the cache manipulator object for reports
		final CacheManipulator cubeCM = cubeCS.getManipulator();
		final CacheResults cubeResults = cubeCS.getCaches();
		final int cubeCachecount = cubeResults.getCount();
		logger.info("Total Cube caches [{}]", cubeCachecount);
		
		for(int j=0; j<cubeResults.size(); j++) {
			//Caches are group on Project level, so get the cache collection for each project
			final Caches result = cubeResults.get(j);
			
			if(result.getProjectName().equals(CustomProperties.getProperty("mstr.default.project.name"))) {
				for(int i=0; i<result.getCount(); i++) {
					final CubeCache cache = (CubeCache) result.get(i);
					
					if(cache.getCacheSourceID().equals(deleteCubeId)) {
						rtnList.add(makeCubeMap(cache));
						
						final MonitorFilter filter = cubeCM.newMonitorFilter();
						filter.add(EnumDSSXMLCubeInfo.DssXmlCubeInfoCubeDefId, EnumDSSXMLMonitorFilterOperator.DssXmlEqual, cache.getCacheSourceID());
						cubeCM.alter(result.getProjectDSSID(), EnumDSSXMLCubeAdminAction.DeleteCube, filter);
						
					}
				}
				cubeCM.submit();
			}
		}
		
		logger.info("Delete Cube [{}]", rtnList);
		return rtnList;
	}
	
	
	private static Map<String, Object> makeCubeMap(final CubeCache cache) {
		final Map<String, Object> deleteCubeMap = new ConcurrentHashMap<>();
		
		deleteCubeMap.put("name", cache.getCacheSourceName());
		deleteCubeMap.put("id", cache.getCacheSourceID());
		deleteCubeMap.put("instanceId", cache.getID());
		
		return deleteCubeMap;
	}
}
