package com.mococo.web.util;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FileUtil
 * @author mococo
 *
 */
public class FileUtil {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LogManager.getLogger(FileUtil.class);
	
	
    /**
     * FileUtil
     */
    public FileUtil() {
    	logger.debug("FileUtil");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("FileUtil");
    }
	
	
	/**
	 * 폴더 체크 및 생성
	 * @param folderPath
	 * @throws Exception
	 */
	public static void folderCheckAndCreate(final String folderPath) {
		//폴더 다중 생성
		/*
		final String[] folderPathList = folderPath.split("/");
		String checkfolderPath = "";
		for(final String checkPath : folderPathList) {
			checkfolderPath = checkfolderPath.concat("/" + checkPath);
			final File Folder = new File(checkfolderPath);
		}
		*/
		
		
		//폴더 단일 생성
		final File Folder = new File(folderPath);
		if (!Folder.exists()) {
			Folder.mkdir(); // 폴더 생성
			logger.debug("신규 폴더 생성 [{}]", folderPath);
		}
	}

}
