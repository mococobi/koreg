/* 
 * 최초작성자 : 송민권
 * 최초작성일 : 2021.05.17.
 * 최종변경일 : 2022.05.16.
 * 목적 : 톰캣 DB 연결 관련 처리
 * 개정이력 :
 * 	송민권, 2022.05.16, 최신화 및 주석 작성
*/
package com.mococo.web.util;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TomcatDataSourceFactory extends BasicDataSourceFactory {
	private static final Logger LOGGER = LogManager.getLogger(TomcatDataSourceFactory.class);
	
	/**
	 * <pre>	
	 * 목적 : 톰캣 DB 정보 연결을 위한 인스턴스 설정
	 * 매개변수 : 
	 * 	Object obj
	 * 	Name name
	 * 	Context nameCtx
	 * 	Hashtable enviroment
	 * 반환값 : java.lang.Object
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable enviroment) throws Exception {
		if(obj instanceof Reference) {
//			setUrl((Reference)obj);
//			setUsername((Reference)obj);
			setPassword((Reference)obj);
		}
		
		return super.getObjectInstance(obj, name, nameCtx, enviroment);
	}
	
	
	/**
	 * <pre>
	 * 목적 : 톰캣 DB 정보 연결을 위한 패스워드 설정
	 * 매개변수 : 
	 * 	Reference ref
	 * 반환값 : 없음
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	private void setPassword(Reference ref) throws Exception {
		findDecryptAndReplace("password", ref);
	}
	
	
	/**
	 * <pre>
	 * 목적 : 연결 정보 설정
	 * 매개변수 : 
	 * 	String refType
	 * 	Reference ref
	 * 반환값 : 없음
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	private void findDecryptAndReplace(String refType, Reference ref) throws Exception {
		int idx = find(refType, ref);
		String decrypted = "";
		//decrypted = EncryptUtil.decrypt(ref.get(idx).getContent().toString());
		
		String tempNullableVar = (String) ref.get(idx).getContent();
		if(tempNullableVar != null) {
		    if(EncryptUtil.decrypt(tempNullableVar.toString()) != null) {
		        decrypted = EncryptUtil.decrypt(tempNullableVar.toString());
		    }
		}
		
		replace(idx, refType, decrypted, ref);
		
	}
	
	
	/**
	 * <pre>
	 * 목적 : 연결 정보 변경
	 * 매개변수 : 
	 * 	int idx
	 * 	String refType
	 * 	String decrypted
	 * 	Reference ref
	 * 반환값 : 없음
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	private void replace(int idx, String refType, String decrypted, Reference ref) throws Exception {
		ref.remove(idx);
		ref.add(idx, new StringRefAddr(refType, decrypted));
	}
	
	
	/**
	 * <pre>
	 * 목적 : 연결 정보 조회
	 * 매개변수 : 
	 * 	String refType
	 * 	Reference ref
	 * 반환값 : java.lang.Integer
	 * 개정이력 : 송민권, 2022.05.16, 최신화 및 주석 작성
	 * </pre>
	 */
	private int find(String refType, Reference ref) throws Exception {
		Enumeration enu = ref.getAll();
		for(int i=0; enu.hasMoreElements(); i++) {
			RefAddr addr = (RefAddr) enu.nextElement();
			if(addr.getType().compareTo(refType) == 0) {
				return i;
			}
		}
		throw new Exception(refType + "  " + refType.toString());
	}

}