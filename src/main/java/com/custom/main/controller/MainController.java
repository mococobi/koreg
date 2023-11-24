package com.custom.main.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.custom.main.service.MainService;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mococo.biz.exception.BizException;
import com.mococo.microstrategy.sdk.esm.vo.MstrUser;
import com.mococo.web.util.JsonUtil;

@Controller
@RequestMapping("/main/*")
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    
    @Autowired
    MainService mainService;
    
    /**
     * 메인 화면 이동
     * @return
     */
    @RequestMapping(value = "/main/mainView.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView loginView() {
        ModelAndView view = new ModelAndView("/main/main");
        
        return view;
    }
    
    @RequestMapping(value = "/category.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView mainCategoryView(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("mainCategory");
        LOGGER.info("call main category page..");
        
        HttpSession httpSession = request.getSession(false);
        MstrUser mstrUser;
//        String sMstrUid = null;
        if (httpSession != null) {
            mstrUser = (MstrUser) httpSession.getAttribute("mstr-user-vo");
            //sMstrUid = (String) httpSession.getAttribute("uid");
//            sMstrUid = mstrUser.getId();
        } else {
            LOGGER.error("Session not found. MstrUid undefined!!");
            // 세션 오류 - 로그인 페이지로
            throw new BizException("noSession");
        }
        // 사업장 목록
        List<Map<String, Object>> lmCorpFolder = mainService.getCorpFolder(mstrUser);
        // 분류 폴더 목록 : 분류 폴더 별 ObjectID
        // 분류 폴더 목록 : 분류 폴더 별 권한 여부 
        
        // Set Return Values
        try {
            view.addObject("corpList", JsonUtil.<Map<String, Object>>toJsonString(lmCorpFolder));
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return view;
    }
    
    @RequestMapping(value = "/equipmentproduct.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView equipmentProductView(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("Equipment/mainEquipmentProduct");
        LOGGER.info("call main MSTR Report page..");
        
        String sEquipmentProductFolderObjectId = (String) request.getParameter("fldObjId");
        HttpSession httpSession = request.getSession(false);
        MstrUser mstrUser;
//        String sMstrUid = null;
        if (httpSession != null) {
            mstrUser = (MstrUser) httpSession.getAttribute("mstr-user-vo");
            //sMstrUid = (String) httpSession.getAttribute("uid");
//            sMstrUid = mstrUser.getId();
        } else {
            LOGGER.error("Session not found. MstrUid undefined!!");
            // 세션 오류 - 로그인 페이지로
            throw new BizException("noSession");
        }
        // 메뉴 목록(폴더 & 보고서)
        List<Map<String, Object>> lmEquipmentProductFolder = mainService.getEquipmentProductFolder(mstrUser, sEquipmentProductFolderObjectId);
        LOGGER.debug("EquipmentProductFolder : [{}], Result: [{}]", sEquipmentProductFolderObjectId, lmEquipmentProductFolder);
        // Set Return Values
        try {
            view.addObject("contentList", JsonUtil.<Map<String, Object>>toJsonString(lmEquipmentProductFolder));
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return view;
    }
    
    @RequestMapping(value = "/energysaving.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView energySavingView(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("Energy/mainEnergySaving");
        LOGGER.info("call main MSTR Report page..");
        
        String sEnergySavingFolderObjectId = (String) request.getParameter("fldObjId");
        HttpSession httpSession = request.getSession(false);
        MstrUser mstrUser;
//        String sMstrUid = null;
        if (httpSession != null) {
            mstrUser = (MstrUser) httpSession.getAttribute("mstr-user-vo");
            //sMstrUid = (String) httpSession.getAttribute("uid");
//            sMstrUid = mstrUser.getId();
        } else {
            LOGGER.error("Session not found. MstrUid undefined!!");
            // 세션 오류 - 로그인 페이지로
            throw new BizException("noSession");
        }
        // 메뉴 목록(폴더 & 보고서)
        List<Map<String, Object>> lmEnergySavingFolder = mainService.getEquipmentProductFolder(mstrUser, sEnergySavingFolderObjectId);
        LOGGER.debug("EquipmentProductFolder : [{}], Result: [{}]", sEnergySavingFolderObjectId, lmEnergySavingFolder);
        // Set Return Values
        try {
            view.addObject("contentList", JsonUtil.<Map<String, Object>>toJsonString(lmEnergySavingFolder));
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return view;
    }
    
    @RequestMapping(value = "/tabtest.do", method = { RequestMethod.GET, RequestMethod.POST })
    public ModelAndView testEquipmentProductView(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("Equipment/tabtest");
        LOGGER.info("call main test page..");
        
        // 메뉴 목록(폴더 & 보고서)
        
        return view;
    }
}
