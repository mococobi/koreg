package com.mococo.microstrategy.sdk.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microstrategy.web.objects.SimpleList;
import com.microstrategy.web.objects.WebDisplayUnit;
import com.microstrategy.web.objects.WebDisplayUnitEntry;
import com.microstrategy.web.objects.WebDisplayUnits;
import com.microstrategy.web.objects.WebFolder;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebShortcut;
import com.microstrategy.web.objects.WebViewMediaSettings;
import com.microstrategy.webapi.EnumDSSXMLObjectFlags;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.microstrategy.webapi.EnumDSSXMLViewMedia;

/**
 * MstrFolderBrowseUtil
 * @author mococo
 *
 */
public class MstrFolderBrowseUtil {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LogManager.getLogger(MstrFolderBrowseUtil.class);
	
	
    /**
     * MstrFolderBrowseUtil
     */
    public MstrFolderBrowseUtil() {
    	logger.debug("MstrFolderBrowseUtil");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("MstrFolderBrowseUtil");
    }
	
	
    /**
     * StackedUnits
     * @author mococo
     *
     */
    private static class StackedUnits {
    	/**
    	 * elements
    	 */
        private final Enumeration<WebDisplayUnit> elements;
        
        /**
         * list
         */
        private final List<Map<String, Object>> list;
        
        /**
         * elements
         * @param elements
         * @param list
         */
        public StackedUnits(final Enumeration<WebDisplayUnit> elements, final List<Map<String, Object>> list) {
            this.elements = elements;
            this.list = list;
        }

        public Enumeration<WebDisplayUnit> getElements() {
            return elements;
        }

        public List<Map<String, Object>> getList() {
            return list;
        }
    }
    
    
    @SuppressWarnings("unchecked")
    private static String getPath(final WebDisplayUnit unit) {
    	final SimpleList simpleList = ((WebObjectInfo) unit).getAncestors();

        final StringBuilder path = new StringBuilder();
        final Enumeration<WebFolder> enumFolder = simpleList.elements();
        while (enumFolder.hasMoreElements()) {
        	final WebFolder folder = enumFolder.nextElement();
            path.append(StringUtils.isEmpty(path.toString()) ? "" : "/").append(folder.getDisplayName());
        }
        return path.toString();
    }
    
    
    @SuppressWarnings("unchecked")
    private static Map<String, Object> getParents(final WebDisplayUnit unit) {
    	final SimpleList simpleList = ((WebObjectInfo) unit).getAncestors();

        final Map<String, Object> parents = new ConcurrentHashMap<>();
        final Enumeration<WebFolder> enumFolder = simpleList.elements();
        while (enumFolder.hasMoreElements()) {
        	final WebFolder folder = enumFolder.nextElement();
            parents.put("name", folder.getDisplayName());
            parents.put("id", folder.getID());

        }
        return parents;
    }
    
    
    /**
     * DisplayNameComparator
     *
     */
    public static class DisplayNameComparator implements Comparator<Object> {
    	@Override
        public int compare(final Object obj1, final Object obj2) {
            String str1 = "";
            String str2 = "";

            if (obj1 instanceof WebDisplayUnitEntry && obj2 instanceof WebDisplayUnitEntry) {
            	str1 = ((WebDisplayUnitEntry) obj1).getValue().getDisplayName();
            	str2 = ((WebDisplayUnitEntry) obj2).getValue().getDisplayName();
            }
            return str1.compareTo(str2);
        }
    }
    
    
    /**
     * 지정폴더의 하위 객체 검색
     * @param session
     * @param rootId
     * @param depth
     * @param objectTypes
     * @return
     */
    public static List<Map<String, Object>> getFolderTree(final WebIServerSession session, final String rootId, final int depth, final List<Integer> objectTypes) throws WebObjectsException {
    	final WebObjectSource source = session.getFactory().getObjectSource();
        source.setFlags(source.getFlags() | EnumDSSXMLObjectFlags.DssXmlObjectComments);
        final WebObjectInfo info = source.getObject(rootId, EnumDSSXMLObjectTypes.DssXmlTypeFolder, true);
        final WebFolder folder = (WebFolder) info;
        folder.populate();
        WebDisplayUnits childUnits = folder.getChildUnits();
        final Comparator<?> childSort = new DisplayNameComparator();

        final List<Map<String, Object>> tree = new ArrayList<>();
        
        if (childUnits != null) {
        	childUnits.sort(new DisplayNameComparator());
        	
        	int currentDepth = 1;
        	List<Map<String, Object>> currentList = tree;
        	Enumeration<WebDisplayUnit> currentElements = childUnits.elements();
        	
        	final LinkedList<StackedUnits> stack = new LinkedList<>();
        	while (true) {
        		if (!currentElements.hasMoreElements()) {
        			while (!stack.isEmpty()) {
        				final StackedUnits units = stack.pop();
        				currentElements = units.getElements();
        				currentList = units.getList();
        				currentDepth--;
        				if (currentElements.hasMoreElements()) {
        					break;
        				}
        			}
        		}
        		
        		if (stack.isEmpty() && !currentElements.hasMoreElements()) {
        			break;
        		}
        		
        		final WebDisplayUnit currentUnit = currentElements.nextElement();
        		
        		// 처리레벨이 정해져 있다면, 해당 레벨의 폴더는 표시하지 않고, 다음 sibling을 처리한다.
        		if (currentDepth > depth && depth != -1) {
        			continue;
        		}
        		if (objectTypes != null && !objectTypes.contains(currentUnit.getDisplayUnitType())) {
        			continue;
        		}
        		
        		final Map<String, Object> map = new ConcurrentHashMap<>();
        		final Map<String, Object> parents = getParents(currentUnit);
        		map.put("name", currentUnit.getDisplayName());
        		map.put("depth", currentDepth);
        		map.put("path", getPath(currentUnit));
        		map.put("parentsName", parents.get("name"));
        		map.put("parentsID", parents.get("id"));
        		if (currentUnit.getDisplayUnitType() == EnumDSSXMLObjectTypes.DssXmlTypeShortcut || currentUnit.isObjectInfo()) {
        			WebObjectInfo objectInfo;
        			if (currentUnit.getDisplayUnitType() == EnumDSSXMLObjectTypes.DssXmlTypeShortcut) {
        				final WebShortcut shortcut = (WebShortcut) source.getObject(currentUnit.getID(),
        						EnumDSSXMLObjectTypes.DssXmlTypeShortcut, true);
        				objectInfo = shortcut.getTarget();
        			} else {
        				objectInfo = (WebObjectInfo) currentUnit;
        			}
        			map.put("id", objectInfo.getID());
        			map.put("type", objectInfo.getType());
        			map.put("subType", objectInfo.getSubType());
        			final WebViewMediaSettings settings = objectInfo.getViewMediaSettings();
        			// 도큐먼트와 VI는 getType()으로 구분할 수 없어, getDefaultMode()를 이용하여 VI, 됴큐먼트를 구분
        			// map.put("isVI", (EnumDSSXMLViewMedia.DSSXmlViewMediaHTML5Dashboard &
        			// settings.getDefaultMode()) == EnumDSSXMLViewMedia.DssXmlViewMediaExportHTML);
        			map.put("isVI", (EnumDSSXMLViewMedia.DSSXmlViewMediaHTML5Dashboard
        					& settings.getDefaultMode()) == EnumDSSXMLViewMedia.DSSXmlViewMediaHTML5Dashboard);
        		} else {
        			map.put("id", currentUnit.getID());
        			map.put("type", currentUnit.getDisplayUnitType());
        		}
        		// logger.debug(map.get("path") + " ==> " + map);
        		currentList.add(map);
        		
        		if (currentUnit.getDisplayUnitType() == EnumDSSXMLObjectTypes.DssXmlTypeFolder) {
        			stack.push(new StackedUnits(currentElements, currentList));
        			((WebFolder) currentUnit).populate();
        			childUnits = currentUnit.getChildUnits();
        			childUnits.sort(childSort);
        			currentElements = childUnits.elements();
        			
        			final List<Map<String, Object>> childList = new ArrayList<>();
        			map.put("child", childList);
        			currentList = childList;
        			currentDepth++;
        		}
        	}
        }

        return tree;
    }

}
