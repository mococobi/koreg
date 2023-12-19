package com.mococo.microstrategy.sdk.prompt;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.web.objects.EnumWebPromptType;
import com.microstrategy.web.objects.WebConstantPrompt;
import com.microstrategy.web.objects.WebElementsPrompt;
import com.microstrategy.web.objects.WebExpressionPrompt;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebObjectsPrompt;
import com.microstrategy.web.objects.WebPrompt;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.mococo.microstrategy.sdk.exception.SdkRuntimeException;
import com.mococo.microstrategy.sdk.prompt.cache.CacheManager;
import com.mococo.microstrategy.sdk.prompt.cache.CacheManager.CacheObjectType;
import com.mococo.microstrategy.sdk.prompt.config.ConfigManager;
import com.mococo.microstrategy.sdk.prompt.dao.ClientResponse;
import com.mococo.microstrategy.sdk.prompt.dao.CustomPromptDao;
import com.mococo.microstrategy.sdk.prompt.dao.PromptDao;
import com.mococo.microstrategy.sdk.prompt.dao.impl.MstrConstantPromptApiDao;
import com.mococo.microstrategy.sdk.prompt.dao.impl.MstrElementsPromptApiDao;
import com.mococo.microstrategy.sdk.prompt.dao.impl.MstrExpressionPromptApiDao;
import com.mococo.microstrategy.sdk.prompt.dao.impl.MstrObjectsPromptApiDao;
import com.mococo.microstrategy.sdk.prompt.vo.ObjectConfig;
import com.mococo.microstrategy.sdk.prompt.vo.Prompt;

public class PromptCharger {
    private static final Logger logger = LoggerFactory.getLogger(PromptCharger.class);

    /*
     * [ { "objectId":"111", "exUiType":"date", "elementSource":{
     * "className":"MstrPromptDao" } }, { "objectId":"112", "exUiType":"yearMonth" }
     * ]
     */
    private static void setExProp(Prompt prompt, ObjectConfig config) {
        prompt.setExProp(config.<String>get("exUiType"), config.<String>get("exExtUiType"),
                config.<String>get("exAction"), config.<String>get("exValidation"));
    }

    private static PromptDao getPromptDao(ObjectConfig config)
            throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, WebObjectsException {
        PromptDao dao = null;

        Map<String, Object> elementSource = null;
        if (config != null) {
            elementSource = config.<Map<String, Object>>get("elementSource");
        }

        if (elementSource != null) {
            String daoClassName = (String) elementSource.get("className");

            Class<CustomPromptDao<?, ?>> clazz = (Class<CustomPromptDao<?, ?>>) PromptCharger.class.getClassLoader()
                    .loadClass(daoClassName);
            logger.debug("==> clazz.getName(): [{}]", clazz.getName());

            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            Constructor<CustomPromptDao<?, ?>> matchConstructor = null;
            for (Constructor<?> constructor : constructors) {
                // 생성될 dao의 생성자 파라미터는 결정되어 있지 않으므로, 2개의 파라미터를 받는 첫번째 생성자를 무조건 수행
                if (constructor.getParameterTypes().length == 2) {
                    matchConstructor = (Constructor<CustomPromptDao<?, ?>>) constructor;
                    break;
                }
            }

            Object param1 = elementSource.get("param1");
            Object param2 = elementSource.get("param2");

            if (matchConstructor != null) {
                dao = matchConstructor.newInstance(param1, param2);
            }
        }

        return dao;
    }

    private static PromptDao getPromptDao(WebIServerSession session, WebPrompt webPrompt, ObjectConfig config)
            throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, WebObjectsException {
        PromptDao dao = getPromptDao(config);

        if (dao == null && webPrompt != null) {
            switch (webPrompt.getPromptType()) {
            case EnumWebPromptType.WebPromptTypeConstant:
                dao = new MstrConstantPromptApiDao((WebConstantPrompt) webPrompt);
                break;
            case EnumWebPromptType.WebPromptTypeElements:
                dao = new MstrElementsPromptApiDao((WebElementsPrompt) webPrompt);
                break;
            case EnumWebPromptType.WebPromptTypeObjects:
                dao = new MstrObjectsPromptApiDao((WebObjectsPrompt) webPrompt);
                break;
            case EnumWebPromptType.WebPromptTypeExpression:
                dao = new MstrExpressionPromptApiDao(session, (WebExpressionPrompt) webPrompt);

                ClientResponse<?, ?> r = new MstrExpressionPromptApiDao(session, (WebExpressionPrompt) webPrompt);
                break;
            default:
                throw new SdkRuntimeException("Unsupported Prompt Type.");
            }
        }

        return dao;
    }

    private static void chargeElement(Prompt prompt, PromptDao dao) {
        if (dao != null) {
            prompt.setPin(dao.getPin());
            prompt.setMin(dao.getMin());
            prompt.setMax(dao.getMax());
            prompt.setDefaultAnswer(dao.getDefaultAnswer());
            prompt.setDefaultAnswers(dao.getDefaultAnswers());
            prompt.setSuggestedAnswers(dao.getSuggestedAnswers());
            prompt.setControlType(dao.getControlType());
            prompt.setDaoClassName(dao.getClass().getName());
            prompt.setRequired(dao.isRequired());
            prompt.setPromptType(dao.getPromptType());
            prompt.setPromptSubType(dao.getPromptSubType());
        } else {
            // throw new SdkRuntimeException("No dao found.");
        }
    }

    public static final Prompt getChargedPrompt(WebIServerSession session, WebPrompt webPrompt, ObjectConfig config)
            throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, WebObjectsException {
        Prompt prompt = new Prompt(webPrompt.getID(),
                StringUtils.defaultString(config.<String>get("exTitle"), webPrompt.getTitle()),
                webPrompt.getPromptType());

        setExProp(prompt, config);
        chargeElement(prompt, getPromptDao(session, webPrompt, config));

        return prompt;
    }

    public static final Prompt getChargedPrompt(ObjectConfig config)
            throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, WebObjectsException {
        Prompt prompt = new Prompt(config.<String>get("objectId"), config.<String>get("exTitle"));
        prompt.setPin(config.get("pin"));

        setExProp(prompt, config);
        chargeElement(prompt, getPromptDao(config));

        return prompt;
    }

    /*
     * public static final PromptDao getPromptDao(WebIServerSession session,
     * WebPrompt webPrompt) throws ClassNotFoundException, NoSuchMethodException,
     * SecurityException, InstantiationException, IllegalAccessException,
     * IllegalArgumentException, InvocationTargetException, WebObjectsException {
     * return getPromptDao(session, webPrompt,
     * ConfigManager.getInstance().getObjectConfig(webPrompt)); }
     * 
     * public static final PromptDao getPromptDao(String project, String id) throws
     * ClassNotFoundException, NoSuchMethodException, SecurityException,
     * InstantiationException, IllegalAccessException, IllegalArgumentException,
     * InvocationTargetException, WebObjectsException { return
     * getPromptDao(ConfigManager.getInstance().getObjectConfig(project, id)); }
     */

    public static final Prompt getChargedPrompt(WebIServerSession session, WebPrompt webPrompt)
            throws WebObjectsException, IllegalArgumentException, ClassNotFoundException, NoSuchMethodException,
            SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException {
        String cacheItemId = CacheManager.getCacheItemId(CacheObjectType.PROMPT, session.getProjectID(),
                webPrompt.getID());
        ObjectConfig objectConfig = CacheManager.<ObjectConfig>getCache(cacheItemId);

        if (objectConfig == null) {
            objectConfig = ConfigManager.getInstance().getObjectConfig(webPrompt, session);
            CacheManager.setCache(cacheItemId, objectConfig);
        }

        return getChargedPrompt(session, webPrompt, objectConfig);
    }

    /**
     * 리포트 주석에 포함된 가상 프롬프트에 대한 처리 개별 가상 프롬프트에 대한 설정은 리포트, 프롬프트 주석이 아닌 별도의 리소스(.json,
     * DB)에 처리
     * 
     * @param id
     * @return
     * @throws WebObjectsException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     * @throws Exception
     */
    public static final Prompt getChargedPrompt(String project, String promptId)
            throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, WebObjectsException {
        String cacheItemId = CacheManager.getCacheItemId(CacheObjectType.PROMPT, project, promptId);
        ObjectConfig objectConfig = CacheManager.<ObjectConfig>getCache(cacheItemId);

        if (objectConfig == null) {
            objectConfig = ConfigManager.getInstance().getObjectConfig(project, promptId);
            CacheManager.setCache(cacheItemId, objectConfig);
        }

        return getChargedPrompt(objectConfig);
    }

    public static final <T1, T2> T2 getClientResponse(WebIServerSession session, String project, String promptId, T1 t1)
            throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, WebObjectsException {
        String cacheItemId = CacheManager.getCacheItemId(CacheObjectType.PROMPT, project, promptId);
        ObjectConfig objectConfig = CacheManager.<ObjectConfig>getCache(cacheItemId);

        if (objectConfig == null) {
            objectConfig = ConfigManager.getInstance().getObjectConfig(project, promptId);
        }

        PromptDao promptDao = null;
        if (objectConfig != null) {
            CacheManager.setCache(cacheItemId, objectConfig);
            promptDao = getPromptDao(objectConfig);
        }

        if (promptDao == null) {
            WebPrompt prompt = (WebPrompt) session.getFactory().getObjectSource().getObject(promptId,
                    EnumDSSXMLObjectTypes.DssXmlTypePrompt, true);
            promptDao = getPromptDao(session, prompt, objectConfig);
        }

        if (promptDao != null && promptDao instanceof ClientResponse) {
            return ((ClientResponse<T1, T2>) promptDao).getClientResponse(t1);
        }

        return null;
    }

}
