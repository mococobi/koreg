package com.mococo.web.util;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class HttpServletUtil {

    public static boolean checkSession(HttpServletRequest request, HttpServletResponse response)
            throws JsonGenerationException, JsonMappingException, IOException {

        HttpSession session = request.getSession(false);

        Object mstrUser = session.getAttribute("mstr-user-vo");
        if (mstrUser == null) {
            response.sendRedirect("/MicroStrategy/plugins/esm/jsp/sso.jsp?mstrUserId=demo");
        }
//		session.getAttributeNames().asIterator()
//        .forEachRemaining(name -> logger.info("session name={}, value={}", name, session.getAttribute(name)));
        return true;

    }
}
