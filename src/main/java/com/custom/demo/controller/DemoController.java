package com.custom.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.custom.demo.service.DemoService;
import com.mococo.biz.exception.BizException;
import com.mococo.web.util.ParamUtil;

@Controller
@RequestMapping("/demo/*")
public class DemoController {
    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    @Autowired
    private DemoService service;

    @RequestMapping(value = "/test.json", method = { RequestMethod.GET, RequestMethod.POST })
    public List<Map<String, Object>> test(@RequestParam Map<String, Object> param) {
        Map<String, Object> map = null;

        logger.debug("=> param: [{}]", param);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (param != null) {
            for (String key : param.keySet()) {
                map = new HashMap<String, Object>();
                map.put(key + "-1", param.get(key) + "-2");
                list.add(map);
            }
        }

        logger.debug("=> list: [{}]", list);

        return list;
    }

    @RequestMapping(value = "/list.json", method = { RequestMethod.GET, RequestMethod.POST })
    public List<Map<String, Object>> list(@RequestParam Map<String, Object> param) {
        Map<String, Object> map = null;

        logger.debug("=> param: [{}]", param);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (param != null) {
            for (String key : param.keySet()) {
                map = new HashMap<String, Object>();
                map.put(key + "-1", param.get(key) + "-2");
                list.add(map);
            }
        }

        logger.debug("=> list: [{}]", list);

        return list;
    }

    @RequestMapping(value = "/demo.do", method = { RequestMethod.GET, RequestMethod.POST })
    public String demo() {
        return "demo/demo";
    }

    @RequestMapping(value = "/jsonParam.json", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public Map<String, Object> jsonParam(@RequestBody Map<String, Object> param) {
        logger.debug("=> param: [{}]", param);

        List<Map<String, Object>> list = (List<Map<String, Object>>) param.get("data");
        for (Map<String, Object> map : list) {
            logger.debug("=> row: [{}], number-value to BigDecimal: [{}]", map,
                    ParamUtil.getBigDecimal(map.get("number-value")));
            map.put("server-response", "server-value of " + map.get("string-value"));
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("server-data", list);

        return map;
    }

    @RequestMapping(value = "/error.json", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    public Map<String, Object> errorJson() {
        if (true) {
            throw new BizException("err.download");
        }

        return new HashMap<String, Object>();
    }
    
}
