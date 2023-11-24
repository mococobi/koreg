package com.mococo.microstrategy.sdk.prompt.servlet.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.web.objects.WebIServerSession;
import com.mococo.microstrategy.sdk.exception.SdkRuntimeException;
import com.mococo.microstrategy.sdk.prompt.PromptCharger;
import com.mococo.microstrategy.sdk.prompt.config.MstrSessionProvider;
import com.mococo.microstrategy.sdk.prompt.servlet.RequestHandler;
import com.mococo.microstrategy.sdk.prompt.servlet.ServiceId;
import com.mococo.microstrategy.sdk.prompt.vo.PromptElement;
import com.mococo.microstrategy.sdk.util.MstrUtil;

@ServiceId(id = "mstr.prompt.subelements")
public class PromptRequestHandler implements RequestHandler<List<PromptElement>> {
    private static final Logger logger = LoggerFactory.getLogger(PromptRequestHandler.class);

    @Override
    public List<PromptElement> GetResponse(MstrSessionProvider provider, Map<String, Object> param) {
        logger.debug("==> param: [{}]", param);

        String objectId = (String) param.get("objectId");

        if (StringUtils.isEmpty(objectId)) {
            throw new SdkRuntimeException("No objectId found.");
        }

        List<PromptElement> list = null;
        WebIServerSession session = null;
        try {
            session = provider.getSession();
            // parentLevel, parentSelectedElementId 가 param에 포함되어야 함
            list = PromptCharger.getClientResponse(session, session.getProjectID(), objectId, param);

            logger.debug("==> list: [{}]", list);
        } catch (Exception e) {
            logger.error("!!! error", e);
        } finally {
            MstrUtil.closeISession(session);
        }

        return list;
    }

}
