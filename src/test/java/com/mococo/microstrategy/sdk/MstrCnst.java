package com.mococo.microstrategy.sdk;

import com.mococo.web.util.CustomProperties;
import com.mococo.web.util.EncryptUtil;

public class MstrCnst {
	
	public static final String SERVER = CustomProperties.getProperty("mstr.server.name");
	public static final String PROJECT = CustomProperties.getProperty("mstr.default.project.name");
	public static final int PORT = Integer.parseInt(CustomProperties.getProperty("mstr.server.port"));
	public static final int LOCALE = Integer.parseInt(CustomProperties.getProperty("mstr.session.locale"));
	public static final String USERID = CustomProperties.getProperty("mstr.admin.user.id");
	public static final String PWD = EncryptUtil.decrypt(CustomProperties.getProperty("mstr.admin.user.pwd"));
	
	public MstrCnst() { }
}
