package com.custom.login.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.custom.admin.service.AdminService;
import com.custom.log.service.LogService;
import com.microstrategy.utils.serialization.EnumWebPersistableState;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.admin.users.WebUser;
import com.mococo.biz.exception.BizException;
import com.mococo.microstrategy.sdk.esm.vo.MstrUser;
import com.mococo.microstrategy.sdk.util.MstrUserUtil;
import com.mococo.microstrategy.sdk.util.MstrUtil;
import com.mococo.web.util.ControllerUtil;
import com.mococo.web.util.CustomProperties;
import com.mococo.web.util.HttpUtil;
import com.mococo.web.util.LoginRsaUtil;
import com.mococo.web.util.PortalCodeUtil;

/**
 * LoginController
 * @author mococo
 *
 */
@Controller
@RequestMapping("/login/*")
public class LoginController {
	
	/**
	 * 로그
	 */
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    /**
     * logService
     */
    /* default */ @Autowired /* default */ LogService logService;
    
    /**
     * adminService
     */
    /* default */ @Autowired /* default */ AdminService adminService;
    
    
    /**
     * LoginController
     */
    public LoginController() {
    	logger.debug("LoginController");
    }
    
    
    /**
     * 세션 테스트 화면 이동
     * @return
     */
    @GetMapping("/login/sessionTest.do")
    public ModelAndView sessionTestGet(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("login/sessionTest");
    }
    
    
    /**
     * SSO 로그인 화면 이동
     * @return
     */
    @GetMapping("/login/ssoUserView.do")
    public ModelAndView ssoUserViewGet(HttpServletRequest request, HttpServletResponse response) {
    	final ModelAndView view = new ModelAndView("login/ssoUser");
    	
    	switch (CustomProperties.getProperty("portal.application.file.name")) {
			case "Gcgf":
				view.setViewName("login/ssoUserGcgf");
				break;
			case "Koreg":
				view.setViewName("login/ssoUserKoreg");
				break;
			default:
				break;
		}
        
        return view;
    }
    
    
    /**
     * SSO 로그인 화면 이동
     * @return
     */
    @PostMapping("/login/ssoUserView.do")
    public ModelAndView ssoUserViewPost(HttpServletRequest request, HttpServletResponse response) {
    	final ModelAndView view = new ModelAndView("login/ssoUser");
    	
    	switch (CustomProperties.getProperty("portal.application.file.name")) {
			case "Gcgf":
				view.setViewName("login/ssoUserGcgf");
				break;
			case "Koreg":
				view.setViewName("login/ssoUserKoreg");
				break;
			default:
				break;
		}
        
        return view;
    }
    
    
    /**
     * SSO EIS 로그인 화면 이동
     * @return
     */
    @GetMapping("/login/ssoUserEisView.do")
    public ModelAndView ssoUserEisViewGet(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("login/ssoUserEisKoreg");
    }
    
    
    /**
     * SSO EIS 로그인 화면 이동
     * @return
     */
    @PostMapping("/login/ssoUserEisView.do")
    public ModelAndView ssoUserEisViewPost(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("login/ssoUserEisKoreg");
    }
    
    
    /**
     * 로그인 화면 이동
     * @return
     */
    @GetMapping("/login/loginUserView.do")
    public ModelAndView loginUserViewGet(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView(PortalCodeUtil.loginLoginUser);
    }
    
    
    /**
     * 로그인 화면 이동
     * @return
     */
    @PostMapping("/login/loginUserView.do")
    public ModelAndView loginUserViewPost(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView(PortalCodeUtil.loginLoginUser);
    }
    
    
    /**
     * EIS 로그인 화면 이동
     * @return
     */
    @GetMapping("/login/loginUserEisView.do")
    public ModelAndView loginUserEisViewGet(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("login/loginUserEis");
    }
    
    
    /**
     * EIS 로그인 화면 이동
     * @return
     */
    @PostMapping("/login/loginUserEisView.do")
    public ModelAndView loginUserEisViewPost(HttpServletRequest request, HttpServletResponse response) {
        return new ModelAndView("login/loginUserEis");
    }
    
    
    /**
     * 로그인 RSA 공개키, 개인키 생성
     * 
     * @param request
     */
    @PostMapping("/login/createLoginKey.json")
    @ResponseBody
    public Map<String, Object> initRSA(final HttpServletRequest request, final HttpServletResponse response) {
        
    	final Map<String, Object> rstMap = new ConcurrentHashMap<>();
        
    	final HttpSession session = request.getSession();
        try {
            KeyPairGenerator generator;
            
            generator = KeyPairGenerator.getInstance(LoginRsaUtil.rsaInstance);
            generator.initialize(2048);
            
            final KeyPair keyPair = generator.genKeyPair();
            final KeyFactory keyFactory = KeyFactory.getInstance(LoginRsaUtil.rsaInstance);
            final PublicKey publicKey = keyPair.getPublic();
            final PrivateKey privateKey = keyPair.getPrivate();
            
            session.setAttribute(LoginRsaUtil.rsaWebKey, privateKey); // session에 RSA 개인키를 세션에 저장
            
            final RSAPublicKeySpec publicSpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
            final String publicKeyModulus = publicSpec.getModulus().toString(16);
            final String publicKeyExponent = publicSpec.getPublicExponent().toString(16);
            
            rstMap.put("RSAModulus", publicKeyModulus);
            rstMap.put("RSAExponent", publicKeyExponent);
            
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
        	logger.error("initRsa error!! ", e);
            throw new BizException(e);
        }
        
        return rstMap;
    }
    
    
	/**
	 * MSTR 사용자 로그인 - 기본 인증
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 */
    @PostMapping({"/login/loginUser.do", "/login/loginUserEis.do"})
	@ResponseBody
	public ModelAndView loginUser(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	final ModelAndView view = new ModelAndView(PortalCodeUtil.loginLoginUser);
		
		final HttpSession httpSession = request.getSession(false);
		WebIServerSession session = null;
		String userId = params.get("encAcntID").toString();
		String userPwd = params.get("encAcntPW").toString();
		final String screenId = params.get("screenId").toString();
		
		try {
			// 복호화
			final PrivateKey privateKey = (PrivateKey) httpSession.getAttribute(LoginRsaUtil.rsaWebKey);
			
            userId = LoginRsaUtil.decryptRsa(privateKey, userId);
            userPwd = LoginRsaUtil.decryptRsa(privateKey, userPwd);
			
			MstrUtil.cleanOtherUserMstrSession(request.getSession(), userId);
			
			final Map<String, Object> connData = new ConcurrentHashMap<>();
			connData.put("server", CustomProperties.getProperty("mstr.server.name"));
			connData.put("project", CustomProperties.getProperty("mstr.default.project.name"));
			connData.put("port", Integer.parseInt(CustomProperties.getProperty("mstr.server.port")));
			connData.put("localeNum", Integer.parseInt(CustomProperties.getProperty("mstr.session.locale")));
			connData.put("uid", userId);
			connData.put("pwd", userPwd);
			session = MstrUtil.connectStandardSession(connData);
			
			//로그인 프로세스 처리(데이터 및 로그 기록시)
			loginSessionProcess(request, response, session, userId, screenId);
			
			//정상 로그인 - 메인 화면 이동
			if(PortalCodeUtil.EIS.equals(screenId)) {
				view.setViewName("redirect:/app/main/mainEisView.do");
			} else {
				view.setViewName("redirect:/app/main/mainView.do");
			}
			
		} catch (BizException | WebObjectsException | BadSqlGrammarException | SQLException 
				| NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException 
				| IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			logger.error("!!! loginUser Exception", e);
			view.addObject(PortalCodeUtil.errorMessage, e.getMessage());
		} finally {
			MstrUtil.closeISession(session);
		}
		
		return view;
	}
	
	
	/**
	 * MSTR 사용자 로그인 - 신뢰된 인증
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 */
    @PostMapping("/login/loginTrust.do")
	@ResponseBody
	public ModelAndView loginUserTrust(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
//    	logger.debug("params : [{}]", params.toString().replaceAll("[\r\n]",""));
    	final ModelAndView view = new ModelAndView(PortalCodeUtil.loginLoginUser);
		
		WebIServerSession session = null;
		final String userId = params.get("encAcntID").toString();
		final String screenId = params.get("screenId").toString();
		
		try {
			MstrUtil.cleanOtherUserMstrSession(request.getSession(), userId);
			
			final Map<String, Object> connData = new ConcurrentHashMap<>();
			connData.put("server", CustomProperties.getProperty("mstr.server.name"));
			connData.put("project", CustomProperties.getProperty("mstr.default.project.name"));
			connData.put("port", Integer.parseInt(CustomProperties.getProperty("mstr.server.port")));
			connData.put("localeNum", Integer.parseInt(CustomProperties.getProperty("mstr.session.locale")));
			connData.put("uid", userId);
			connData.put("trustToken", CustomProperties.getProperty("mstr.trust.token"));
			session = MstrUtil.connectStandardSession(connData);
			
			//로그인 프로세스 처리(데이터 및 로그 기록시)
			loginSessionProcess(request, response, session, userId, screenId);
			
			//정상 로그인 - 메인 화면 이동
			if(PortalCodeUtil.EIS.equals(screenId)) {
				view.setViewName("redirect:/app/main/mainEisView.do");
			} else {
				view.setViewName("redirect:/app/main/mainView.do");
			}
			
		} catch (BizException | WebObjectsException | BadSqlGrammarException | SQLException e) {
//			logger.debug("=> 요청 사용자 : [{}]", userId.replaceAll("[\r\n]",""));
			logger.error("!!! loginUser Exception", e);
			view.addObject(PortalCodeUtil.errorMessage, e.getMessage());
		} finally {
			MstrUtil.closeISession(session);
		}
		
		return view;
	}
	
	
	/**
	 * 로그인 프로세스 처리
	 * @param request
	 * @param isession
	 * @param userId
	 * @return
	 * @throws WebObjectsException
	 */
	private Map<String, Object> loginSessionProcess(final HttpServletRequest request, final HttpServletResponse response, final WebIServerSession session, final String userId, final String screenId) throws WebObjectsException, SQLException {
		Map<String, Object> resultMap = new ConcurrentHashMap<>();
		final WebUser user = (WebUser) session.getUserInfo();
		final HttpSession httpSession = request.getSession();
		
		if (user == null) {
			//MSTR 사용자가 없을 경우
			resultMap = ControllerUtil.getFailMap("error.no.user.found");
			resultMap.put("userId", userId);
			
			httpSession.setAttribute("mstr-user-vo", null);
			
			httpSession.setAttribute("mstrUserIdAttr", "");
			httpSession.setAttribute("mstrUserNameAttr", "");
			httpSession.setAttribute("mstrGroupIdMapAttr", "");
		} else {
			//정상 로그인 처리
			
			//MSTR 그룹 확인
			Map<String, String> groupIdMap = new ConcurrentHashMap<>();
			final String userIp = HttpUtil.getClientIP(request);
			
			if(session != null) {
				groupIdMap = MstrUserUtil.applyIsImpttTerminal(session, user, userIp);
			}
			
			//사용자 정보 세션 저장
			final MstrUser mstrUser = new MstrUser(user.getAbbreviation());
            mstrUser.setClientIp(userIp);
            mstrUser.setServer(session.getServerName());
            mstrUser.setPort(session.getServerPort());
            mstrUser.setProject(session.getProjectName());
            mstrUser.setProjectSession(session.getProjectName(), session.saveState(EnumWebPersistableState.MAXIMAL_STATE_INFO));
            httpSession.setAttribute("mstr-user-vo", mstrUser);
            httpSession.setAttribute("portal-screen-id", screenId);
            
            httpSession.setAttribute("mstrUserIdAttr", user.getAbbreviation());
            httpSession.setAttribute("mstrUserNameAttr", user.getDisplayName());
            httpSession.setAttribute("mstrGroupIdMapAttr", groupIdMap);
			
			//포탈 관리자 정보
            final Map<String, Object> params = new ConcurrentHashMap<>();
			final List<Map<String, Object>> authListMap = adminService.adminAuthList(request, response, params);
			final List<String> authList = new ArrayList<>();
			for(final Map<String, Object> authMap : authListMap) {
				authList.add(authMap.get("ADM_CD").toString());
			}
			httpSession.setAttribute("PORTAL_AUTH", authList);
			
			//포탈 로그 기록(로그인)
			logService.addPortalLog(request, screenId, screenId, "LOGIN", null);
			final String userInfoId = user.getAbbreviation().replaceAll("[\r\n]","");
			final String userInfoNm = user.getDisplayName().replaceAll("[\r\n]","");
			final String userInfoGroup = groupIdMap.toString().replaceAll("[\r\n]","");
			
			logger.info("MSTR 사용자 로그인 [{}][{}][{}]", userInfoId, userInfoNm, userInfoGroup);
		}
		
		return resultMap;
	}
	
	
	/**
	 * MSTR 사용자 로그아웃
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 */
	@PostMapping("/login/logoutUser.do")
	@ResponseBody
	public ModelAndView logoutUser(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> param) throws SQLException {
		final ModelAndView view = new ModelAndView(PortalCodeUtil.loginLoginUser);
		final HttpSession httpSession = request.getSession();
		final String screenId = (String)httpSession.getAttribute("portal-screen-id");
		
		if(PortalCodeUtil.EIS.equals(screenId)) {
			view.setViewName("login/loginUserEis");
		} else {
			view.setViewName(PortalCodeUtil.loginLoginUser);
		}
		
		//포탈 로그 기록(로그아웃)
		if(HttpUtil.getLoginUserId(request) != null) {
			//MSTR 세션 제거
			logService.addPortalLog(request, screenId, screenId, "LOGOUT", null);
		}
		
		//MSTR 세션 제거
		MstrUtil.cleanMstrSession(httpSession);
		
		//포탈 세션 제거
		httpSession.setAttribute("mstr-user-vo", null);
		httpSession.setAttribute("portal-screen-id", "");
		
		httpSession.setAttribute("mstrUserIdAttr", "");
		httpSession.setAttribute("mstrUserNameAttr", "");
		httpSession.setAttribute("mstrGroupIdMapAttr", "");
		
		return view;
	}
	
}
