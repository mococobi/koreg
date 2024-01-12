package com.mococo.web.util;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUtil {

	private static final Logger LOGGER = LogManager.getLogger(FileUtil.class);

	//폴더 체크 및 생성
	public static void folderCheckAndCreate(String folderPath) throws Exception {
		
		String[] folderPathList = folderPath.split("/");
		String checkfolderPath = "";
		
		for(int i=0; i<folderPathList.length; i++) {
			if(!folderPathList[i].equals("")) {
				checkfolderPath += "/" + folderPathList[i];
				File Folder = new File(checkfolderPath);

				// 해당 디렉토리가 없을경우 디렉토리를 생성합니다.
				if (!Folder.exists()) {
					Folder.mkdir(); // 폴더 생성합니다.
					LOGGER.debug("신규 폴더 생성 [{}]", checkfolderPath);
				}
			}
		}
	}

}
