package com.mococo.microstrategy.sdk.prompt.servlet.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.web.objects.WebIServerSession;
import com.mococo.microstrategy.sdk.exception.SdkRuntimeException;
import com.mococo.microstrategy.sdk.prompt.ReportCharger;
import com.mococo.microstrategy.sdk.prompt.config.MstrSessionProvider;
import com.mococo.microstrategy.sdk.prompt.servlet.RequestHandler;
import com.mococo.microstrategy.sdk.prompt.servlet.ServiceId;
import com.mococo.microstrategy.sdk.prompt.vo.Report;
import com.mococo.microstrategy.sdk.util.MstrUtil;

@ServiceId(id = "mstr.report.info")
public class ReportRequestHandler implements RequestHandler<Report> {
    private static final Logger logger = LoggerFactory.getLogger(ReportRequestHandler.class);

    @Override
    public Report GetResponse(MstrSessionProvider provider, Map<String, Object> param) {
        logger.debug("==> param: [{}]", param);

        String objectId = (String) param.get("objectId");
        Integer objectType = (Integer) param.get("objectType");

        if (StringUtils.isEmpty(objectId) || objectType == null) {
            throw new SdkRuntimeException("no objectId, objectType found.");
        }

        Report report = null;
        WebIServerSession session = null;
        try {
            session = provider.getSession();
            report = ReportCharger.chargeObject(session, objectType, objectId);

            logger.debug("==> report: [{}]", report);
        } catch (Exception e) {
            logger.error("!!! error", e);
        } finally {
            MstrUtil.closeISession(session);
        }

        return report;
    }

}
