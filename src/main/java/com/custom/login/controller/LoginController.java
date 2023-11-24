package com.custom.login.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.custom.log.service.LogService;
import com.custom.login.service.LoginService;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.admin.users.WebUser;
import com.mococo.biz.exception.BizException;
import com.mococo.microstrategy.sdk.util.MstrUserUtil;
import com.mococo.microstrategy.sdk.util.MstrUtil;
import com.mococo.web.util.ControllerUtil;
import com.mococo.web.util.CustomProperties;
import com.mococo.web.util.HttpUtil;

@Controller
@RequestMapping("/login/*")
public class LoginController {
	
    final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    
    private static String RSA_WEB_KEY = "_RSA_SF_KEY_"; // 개인키 session key
    private static String RSA_INSTANCE = "RSA"; // rsa transformation
    
    @Autowired
    LoginService loginService;
    
    @Autowired
    LogService logService;
    
    
    /**
     * 로그인 화면 이동
     * @return
     */
    @RequestMapping(value = "/login/loginUserView.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView loginView(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("/login/loginUser");
        
        return view;
    }
    
    
    /**
     * 로그인 RSA 공개키, 개인키 생성
     * 
     * @param request
     */
    @RequestMapping(value = "/login/createLoginKey.json", method = { RequestMethod.POST })
    @ResponseBody
    public Map<String, Object> initRSA(HttpServletRequest request, HttpServletResponse response) {
        
        Map<String, Object> rstMap = new HashMap<String, Object>();
        
        HttpSession session = request.getSession();
        try {
            KeyPairGenerator generator;
            
            generator = KeyPairGenerator.getInstance(LoginController.RSA_INSTANCE);
            generator.initialize(1024);
            
            KeyPair keyPair = generator.genKeyPair();
            KeyFactory keyFactory = KeyFactory.getInstance(LoginController.RSA_INSTANCE);
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            
            session.setAttribute(LoginController.RSA_WEB_KEY, privateKey); // session에 RSA 개인키를 세션에 저장
            
            RSAPublicKeySpec publicSpec = (RSAPublicKeySpec) keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
            String publicKeyModulus = publicSpec.getModulus().toString(16);
            String publicKeyExponent = publicSpec.getPublicExponent().toString(16);
            
            rstMap.put("RSAModulus", publicKeyModulus);
            rstMap.put("RSAExponent", publicKeyExponent);
            
        } catch (Exception e) {
            LOGGER.error("initRsa error!! : {}", e.getMessage());
            throw new BizException("error");
        }
        
        return rstMap;
    }
    
    
    /**
     * 로그인 RSA 복호화
     * @param privateKey
     * @param securedValue
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     */
    private String decryptRsa(PrivateKey privateKey, String securedValue) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        Cipher cipher = Cipher.getInstance(LoginController.RSA_INSTANCE);
        byte[] encryptedBytes = hexToByteArray(securedValue);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        String decryptedValue = new String(decryptedBytes, "utf-8"); // 문자 인코딩 주의.
        return decryptedValue;
    }
    
    
    /**
     * 16진 문자열을 byte 배열로 변환
     * @param hex
     * @return
     */
    private static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() % 2 != 0) { return new byte[] {}; }
        
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            byte value = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
            bytes[(int) Math.floor(i / 2)] = value;
        }
        return bytes;
    }
    
    
	/**
	 * MSTR 사용자 로그인 - 기본 인증
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 */
	@RequestMapping(value = "/login/loginUser.do", method = {RequestMethod.POST})
	@ResponseBody
	public ModelAndView loginUser(HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
		ModelAndView view = new ModelAndView("/login/loginUser");
		
		HttpSession session = request.getSession(false);
		WebIServerSession isession = null;
		String userId = (String)request.getParameter("encAcntID");
		String userPwd = (String)request.getParameter("encAcntPW");
		
		try {
			// 복호화
            PrivateKey privateKey = (PrivateKey) session.getAttribute(LoginController.RSA_WEB_KEY);
            userId = decryptRsa(privateKey, userId);
            userPwd = decryptRsa(privateKey, userPwd);
			
			MstrUtil.cleanOtherUserMstrSession(request.getSession(), userId);
			
			isession = MstrUtil.connectSession(
					CustomProperties.getProperty("mstr.server.name"),
					CustomProperties.getProperty("mstr.default.project"),
					userId,
					userPwd
			);
			
			//로그인 프로세스 처리(데이터 및 로그 기록시)
			loginSessionProcess(request, isession, view, userId);
			
			//정상 로그인 - 메인 화면 이동
			view.setViewName("redirect:/app/main/mainView.do");
			
		} catch (BizException e) {
			LOGGER.debug("=> 요청 사용자 : [{}]", userId);
			LOGGER.error("!!! loginUser BizException", e);
			view.addObject("errorMessage", e.getMessage());
		} catch(WebObjectsException e) {
			LOGGER.debug("=> 요청 사용자 : [{}]", userId);
			LOGGER.error("!!! loginUser WebObjectsException", e);
			view.addObject("errorMessage", e.getMessage());
		} catch (Exception e) {
			LOGGER.debug("=> 요청 사용자 : [{}]", userId);
			LOGGER.error("!!! loginUser Exception", e);
			view.addObject("errorMessage", e.getMessage());
		} finally {
			MstrUtil.closeISession(isession);
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
	@RequestMapping(value = "/login/loginTrust.do", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public ModelAndView loginUserTrust(HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> params) {
		ModelAndView view = new ModelAndView("/login/loginUser");
		
		WebIServerSession isession = null;
		String userId = (String)request.getParameter("userId");
		
		try {
			MstrUtil.cleanOtherUserMstrSession(request.getSession(), userId);
			
			isession = MstrUtil.connectTrustSession(
				CustomProperties.getProperty("mstr.server.name"),
				CustomProperties.getProperty("mstr.default.project"),
				userId,
				CustomProperties.getProperty("mstr.trust.token")
			);
			
			//로그인 프로세스 처리(데이터 및 로그 기록시)
			loginSessionProcess(request, isession, view, userId);
			
			//정상 로그인 - 메인 화면 이동
			view.setViewName("redirect:/app/main/mainView.do");
			
		} catch (BizException e) {
			LOGGER.debug("=> 요청 사용자 : [{}]", userId);
			LOGGER.error("!!! loginUser BizException", e);
			view.addObject("errorMessage", e.getMessage());
		} catch(WebObjectsException e) {
			LOGGER.debug("=> 요청 사용자 : [{}]", userId);
			LOGGER.error("!!! loginUser WebObjectsException", e);
			view.addObject("errorMessage", e.getMessage());
		} catch (Exception e) {
			LOGGER.debug("=> 요청 사용자 : [{}]", userId);
			LOGGER.error("!!! loginUser Exception", e);
			view.addObject("errorMessage", e.getMessage());
		} finally {
			MstrUtil.closeISession(isession);
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
	private Map<String, Object> loginSessionProcess(HttpServletRequest request, WebIServerSession isession, ModelAndView view, String userId) throws WebObjectsException {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		WebUser user = (WebUser) isession.getUserInfo();
		
		if (user == null) {
			//MSTR 사용자가 없을 경우
			resultMap = ControllerUtil.getFailMap("error.no.user.found");
			resultMap.put("userId", userId);
			
			request.getSession().setAttribute("mstrUserIdAttr", "");
			request.getSession().setAttribute("mstrUserNameAttr", "");
			request.getSession().setAttribute("mstrGroupIdMapAttr", "");
		} else {
			//정상 로그인 처리
			
			//MSTR 그룹 확인
			Map<String, String> groupIdMap = new HashMap<String, String>();
			String userIp = HttpUtil.getClientIP(request);
			
			if(isession != null) {
				groupIdMap = MstrUserUtil.applyIsImpttTerminal(isession, user, userIp);
			}
			
			//사용자 정보 세션 저장
			request.getSession().setAttribute("mstrUserIdAttr", user.getAbbreviation());
			request.getSession().setAttribute("mstrUserNameAttr", user.getDisplayName());
			request.getSession().setAttribute("mstrGroupIdMapAttr", groupIdMap);
			request.getSession().setAttribute("mstrUserIpAttr", userIp);
			
//			resultMap.put("mstrUserIdAttr", request.getSession().getAttribute("mstrUserIdAttr"));
//			resultMap.put("mstrUserNameAttr", request.getSession().getAttribute("mstrUserNameAttr"));
//			resultMap.put("mstrGroupIdMapAttr", request.getSession().getAttribute("mstrGroupIdMapAttr"));
			
			//포탈 로그 기록(로그인)
			logService.addPortalLog(request, "PORTAL", "PORTAL", "LOGIN", null);
			
			LOGGER.info("MSTR 사용자 로그인 [{}][{}][{}]", request.getSession().getAttribute("mstrUserIdAttr"), request.getSession().getAttribute("mstrUserNameAttr"), request.getSession().getAttribute("mstrGroupIdMapAttr"));
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
	@RequestMapping(value = "/login/logoutUser.do", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public ModelAndView logoutUser(HttpServletRequest request, final HttpServletResponse response, @RequestParam final Map<String, Object> param) {
		ModelAndView view = new ModelAndView("/login/loginUser");
		
		//포탈 로그 기록(로그아웃)
		
		//MSTR 세션 제거
		MstrUtil.cleanMstrSession(request.getSession());
		
		//포탈 세션 제거
		request.getSession().setAttribute("mstrUserIdAttr", "");
		request.getSession().setAttribute("mstrUserNameAttr", "");
		request.getSession().setAttribute("mstrGroupIdMapAttr", "");
		
		return view;
	}
	
}
