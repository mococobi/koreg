package com.mococo.web.filter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.admin.users.WebUser;
import com.mococo.microstrategy.sdk.util.MstrUserUtil;
import com.mococo.microstrategy.sdk.util.MstrUtil;
import com.mococo.web.util.CustomProperties;
import com.mococo.web.util.EncryptUtil;
import com.mococo.web.util.HttpUtil;


/**
 * 파일 Export시 DownloadServletFilter
 */
//@WebFilter(urlPatterns="/*")
public class DownloadServletFilter implements Filter {
	
	/**
	 * logger
	 */
	private static final Logger logger = LogManager.getLogger(DownloadServletFilter.class);
	
	/**
	 * drm_default_path
	 */
	private static final String drm_default_path = CustomProperties.getProperty("attach.base.location") + "drm/";
	
	/**
	 * 시작 호출 함수
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}
	
	
	/**
	 * 종료시 호출 함수
	 */
	@Override
	public void destroy() {
		
	}

	
	/**
	 * 내보내기 요청 가능 여부 확인
	 * @param request
	 * @param response
	 * @param response2
	 * @return
	 */
	private boolean isExportRequest(final HttpServletRequest request) {
		final String path = request.getServletPath();
		boolean result = false;
		
		if (path == null || "".equals(path)) {
			result = false;
		} else if (path.matches("^/export.*")) {
			result = true;
		} else if (path.matches("^/servlet/mstrWeb*")) {
			final String paramEvt = request.getParameter("evt");
			final String paramSrc = request.getParameter("src");
			
			if ("3012".equals(paramEvt) && "mstrWeb.3012".equals(paramSrc)) {
				// Report 내보내기 : 일반텍스트(txt), HTML
				result = true;
			} else if ("3103".equals(paramEvt) && "mstrWeb.3103".equals(paramSrc)) {
				// Dossier 내보내기 : 데이터(csv)
				result = true;
			} else if ("1024001".equals(paramEvt) && "mstrWeb.rwd.rwframe.rwb.1024001".equals(paramSrc)) {
				// Document 내보내기 : HTML 외 모두
				result = false;
			}
		}
		
		return result;
	}
	
	
	/**
	 * 파일 암호화
	 * @param input
	 * @param output
	 * @param fileName
	 * @param signUserId
	 * @param clientIpAddr
	 */
	private void encryptFile(final OutputStream output, final String fileName, final String signUserId, final String clientIpAddr) throws IOException, WebObjectsException {
		String signUserObjId = "";
		String signUserName = "";
		
		if (StringUtils.isNotEmpty(signUserId)) {
			WebIServerSession session = null;
			try {
				final Map<String, Object> connData = new ConcurrentHashMap<>();
				connData.put("server", CustomProperties.getProperty("mstr.server.name"));
				connData.put("project", CustomProperties.getProperty("mstr.default.project.name"));
				connData.put("port", Integer.parseInt(CustomProperties.getProperty("mstr.server.port")));
				connData.put("localeNum", Integer.parseInt(CustomProperties.getProperty("mstr.session.locale")));
				connData.put("uid", CustomProperties.getProperty("mstr.admin.user.id"));
				connData.put("pwd", EncryptUtil.decrypt(CustomProperties.getProperty("mstr.admin.user.pwd")));
				session = MstrUtil.connectStandardSession(connData);
				
				WebObjectSource objectSource = null;
				if(session != null) {
					objectSource = session.getFactory().getObjectSource();
				}
				
				final WebUser user = MstrUserUtil.searchUser(objectSource, signUserId);
				signUserObjId = user.getID();
				signUserName = user.getDisplayName();
				logger.debug("UserId : [{}], UserName : [{}], ObjId : [{}], IP : [{}]", signUserId, signUserName, signUserObjId, clientIpAddr);
				
			} catch (WebObjectsException e) {
				logger.error("error", e);
			} finally {
				if (session != null) {
					MstrUtil.closeISession(session);
					logger.debug("encryptFile Close ISSession..!");
				}
			}
		}
		
		String tmpFileDir = drm_default_path;
		String tmpFileName = fileName;
		
		//로컬 샘플 파일 테스트
//		tmpFileDir = "/mococo/portal/1/";
//		tmpFileName = "test.xlsx";
		
		//기본 파일
		final File rtnEncDrmFile = new File(tmpFileDir, tmpFileName);
		
		//DRM  모듈 적용(필요시) -- 시작
		//rtnEncDrmFile = DrmUtil.encDrm(downFilePath, downFileName);
		//DRM  모듈 적용(필요시) -- 종료
		
		if(rtnEncDrmFile != null) {
			try (
				InputStream fis = Files.newInputStream(Paths.get(tmpFileDir + tmpFileName));
				BufferedInputStream bis = new BufferedInputStream(fis);
			){
				final byte[] readBuffer = new byte[1024];
				int read = -1;
				
				output.flush();
				while ( (read = bis.read(readBuffer)) != -1) {
					output.write(readBuffer, 0, read);
				}
			} catch (IOException e) {
				logger.error("encryptFile IOException : ", e);
			}
		}
	}
	
	
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		final HttpServletRequest httpRequest = (HttpServletRequest) request;
//		final HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		if (isExportRequest(httpRequest)) {
			
			// ResponseWrapper로 실제 출력스트림을 파일로 임시저장하고, 이 파일에 암호화 처리 후 서블릿 스트림으로 출력 
			final ResponseWrapper wrapper = new ResponseWrapper((HttpServletResponse)response);
			
			try {
				chain.doFilter(request, wrapper);
			} catch (Exception e) {
				logger.error("error", e);
			} finally {
				try {
					wrapper.finish();
			
					String downFileName = "";
					String downFileExt = "";
					
					for (final String hName : wrapper.getHeaderNames()) {
						final String hValue = wrapper.getHeader(hName);
//						logger.debug("wrapper.getInfo Name : [{}], Value : [{}]", hName, hValue);
						
						if (hValue.contains("filename=")) {
							downFileName = URLDecoder.decode(hValue.split("filename=")[1], "UTF-8");
							logger.debug("downloadFileName === [{}]", downFileName);
							downFileExt = downFileName.substring(downFileName.lastIndexOf('.'), downFileName.lastIndexOf(';'));
							logger.debug("downloadFileExtension === [{}]", downFileExt);
							break;
						}
					}
					
					for (final String hName : wrapper.getHeaderNames()) {
						final String hValue = wrapper.getHeader(hName);
//						logger.debug("wrapper.getInfo Name : [{}], Value : [{}]", hName, hValue);
						if (hValue.contains("filename=")) {
							downFileName = URLDecoder.decode(hValue.split("filename=")[1], "UTF-8");
							logger.debug("downloadFileName === [{}]", downFileName);
							downFileExt = downFileName.substring(downFileName.lastIndexOf('.'), downFileName.lastIndexOf(';'));
							logger.debug("downloadFileExtension === [{}]", downFileExt);
							break;
						}
					}
					
					final String responType = response.getContentType();
					logger.debug("response2 = [{}]", responType);
					if (downFileExt.toLowerCase(Locale.getDefault()).contains("pdf") || "application/pdf".equals(responType) ) {
						// pdf 의 경우, 내보내기 시 브라우저에서 자동 Open 되면서 오류 발생. 
						// DRM 적용 및 자동 Open 방지를 위채 Content-Type 수정, 파일 다운로드로 변경
						response.setContentType("application/octet-stream");
						logger.debug("pdf response = [{}]", responType);
					} else if (downFileExt.toLowerCase(Locale.getDefault()).contains("csv") || responType.contains("application/csv") ) {
						// csv 의 경우, DRM 의 IsSupportFile 메소드 결과 false(지원하지 않는 확장자?) 를 
						// 반환하여 확장자를 엑셀 확장자로 임시 변경 설정
						downFileExt = ".xlsx";
						logger.debug("download FileExtension2 == [{}]", downFileExt);
					} else if(responType.contains("text/html")) {
						//로딩 페이지일 경우 파일 읽어서 다시 리턴
						final String wrapperFileNm = wrapper.getFileName();
						logger.debug("로딩 페이지 : [{}]", wrapperFileNm);
						final File wrapperFile = new File(wrapperFileNm);
						final byte[] fileByte = FileUtils.readFileToByteArray(wrapperFile);
						wrapperFile.delete();
						
						response.setContentLength(fileByte.length);
						response.getOutputStream().write(fileByte);
						response.flushBuffer(); // marks response as committed
					}
					
					if(!responType.contains("text/html")) {
						// DRM 적용시 사용할 User 정보를 위해 세션의 UserId 가져옴.
						final HttpSession httpSession = httpRequest.getSession(false);
						String signUserId = "";
						String clientIpAddr = "";
						if (httpSession != null) {
							signUserId = HttpUtil.getLoginUserId(httpRequest);
							clientIpAddr = HttpUtil.getClientIP(httpRequest);
							logger.debug("Download User Info [{}][{}] ", signUserId, clientIpAddr);
						} else {
							logger.debug("Download User Info : session is null");
						}
						
						final File orgfile = new File(wrapper.getFileName());
						File orgRenameFile = null;
						boolean isMoved = false;
						
						if (!"".contentEquals(downFileExt)) {
							//파일 확장자 붙여서 파일명 변경
							orgRenameFile = new File(wrapper.getFileName() + downFileExt);
							isMoved = orgfile.renameTo(orgRenameFile);
							final String orgReFileNm = orgRenameFile.getName();
							logger.debug("File Rename : [{}][{}]" , isMoved, orgReFileNm);
						}
						
						try (
//							InputStream input = new FileInputStream(isMoved ? orgRenameFile : orgfile);
							OutputStream output = response.getOutputStream();
						){
							encryptFile(output, isMoved ? orgRenameFile.getName() : orgfile.getName(), signUserId, clientIpAddr);
						} catch (Exception e) {
							logger.error("encryptFile error : ", e);
						} finally {
							//임시 파일 삭제
							if (isMoved) {
								orgRenameFile.delete();
							} else {
								orgfile.delete();
							}
						}
					}
					
				} catch (Exception e) {
					logger.error("error", e);
				}
			}
			
		} else {
			chain.doFilter(request, response);
		}
	}
	
	
	// ResponseWrapper를 통해 출력스크림이 파일로 저장되며 저장된 임시파일명을 지정하는 부분
	private static String getTempFileName() {
		final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS_", Locale.KOREA);
		final String currentTime = timeFormat.format(new Date());
		
		final String fileName = drm_default_path + currentTime + UUID.randomUUID().toString();
		logger.debug("=> fileName : [{}]", fileName);
		
		return fileName;
	}
	
	
	/**
	 * 파일 처리를 위한 EncryptServletOutputStream
	 */
	private static class EncryptServletOutputStream extends ServletOutputStream {
		
		/**
		 * servletOutputStream
		 */
		private final ServletOutputStream servletOutStream;
		
		/**
		 * encryptStream
		 */
//		private FileOutputStream encryptStream;
		private final OutputStream encryptStream;
		
		/**
		 * fileName
		 */
		private final String fileName;
		
		/**
		 * EncryptServletOutputStream
		 * @param servletOutputStream
		 * @throws IOException
		 */
		public EncryptServletOutputStream(final ServletOutputStream servletOutStream) throws IOException {
			this.servletOutStream = servletOutStream;
			fileName = getTempFileName();
			encryptStream = Files.newOutputStream(Paths.get(fileName));
		}

		public String getFileName() { return fileName; }
		
		@Override
		public boolean isReady() { return servletOutStream.isReady(); }

		@Override
		public void setWriteListener(final WriteListener writeListener) { servletOutStream.setWriteListener(writeListener); }

		@Override
		public void write(final int byte1) throws IOException { encryptStream.write(byte1); }

		@Override
		public void close() throws IOException { encryptStream.close(); }
		
		@Override
		public void flush() throws IOException { encryptStream.flush(); }
	}	
	
	
	/**
	 * 파일 처리를 위한 ResponseWrapper
	 */
	private static class ResponseWrapper extends HttpServletResponseWrapper {
		
		/**
		 * outputStream
		 */
		private EncryptServletOutputStream outputStream;
		
		/**
		 * writer
		 */
		private PrintWriter writer;
		
		/**
		 * ResponseWrapper
		 * @param response
		 */
		public ResponseWrapper(final HttpServletResponse response) {
			super(response);
		}
		
		@Override
		public synchronized ServletOutputStream getOutputStream() throws IOException {
			if (this.writer != null) {
				throw new IllegalStateException("getWriter() already called.");
			}
			if (this.outputStream == null) {
				this.outputStream = new EncryptServletOutputStream(super.getOutputStream());
			}
			
			return this.outputStream;
		}
		
		@Override
		public synchronized PrintWriter getWriter() throws IOException {
			if (this.writer == null && this.outputStream != null) {
				throw new IllegalStateException("getOutputStream() already called.");
			}
			
			if (this.writer == null) {
				this.outputStream = new EncryptServletOutputStream(super.getOutputStream());
				this.writer = new PrintWriter(new OutputStreamWriter(this.outputStream, this.getCharacterEncoding()));
			}
			
			return this.writer;
		}
		
		@Override
		public void flushBuffer() throws IOException {
			if (this.writer != null) {
				this.writer.flush();
			} else if (this.outputStream != null) {
				this.outputStream.flush();
			}
			
			super.flushBuffer();
		}
		
		@Override
		public void setContentLength(int length) { }
		
		@Override
		public void setContentLengthLong(long length) { }
		
		@Override
		public void setHeader(final String name, final String value) { 
			if (!"content-length".equalsIgnoreCase(name)) { 
				super.setHeader(name, value); 
			} 
		}
		
		@Override
		public void addHeader(final String name, final String value) { 
			if (!"content-length".equalsIgnoreCase(name)) { 
				super.setHeader(name, value); 
			} 
		}
		
		@Override
		public void setIntHeader(final String name, final int value) { 
			if (!"content-length".equalsIgnoreCase(name)) { 
				super.setIntHeader(name, value); 
			} 
		}

		@Override
		public void addIntHeader(final String name, final int value) { 
			if (!"content-length".equalsIgnoreCase(name)) { 
				super.setIntHeader(name, value); 
			} 
		}
		
		/**
		 * finish
		 * @throws IOException
		 */
		public void finish() throws IOException {
			if (writer != null) {
				writer.close();
			} else if (outputStream != null) {
				outputStream.close();
			}
		}
		
		/**
		 * getFileName
		 * @return
		 */
		public String getFileName() {
			return outputStream != null ? outputStream.getFileName() : null;
		}
	}
}
