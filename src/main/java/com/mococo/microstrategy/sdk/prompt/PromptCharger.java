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

/**
 * PromptCharger
 * @author mococo
 *
 */
public class PromptCharger {
	
	/**
	 * 로그
	 */
    private static final Logger logger = LoggerFactory.getLogger(PromptCharger.class);
    
    
    /**
     * NUM_2
     */
    private static final int NUM_2 = 2;
    
    
    /**
     * PromptCharger
     */
    public PromptCharger() {
    	logger.debug("PromptCharger");
    }
    
    
	@SuppressWarnings("unused")
	private void sample() {
    	logger.debug("PromptCharger");
    }
	
    
    /**
     * setExProp
     * [ { "objectId":"111", "exUiType":"date"
     * , "elementSource":{"className":"MstrPromptDao" } }
     * , { "objectId":"112", "exUiType":"yearMonth" }]
     */
    private static void setExProp(final Prompt prompt, final ObjectConfig config) {
        prompt.setExProp(config.<String>get("exUiType")
        		, config.<String>get("exExtUiType")
        		, config.<String>get("exAction")
        		, config.<String>get("exValidation"));
    }
    
    
    /**
     * getPromptDao
     * @param config
     * @return
     */
    private static PromptDao getPromptDao(final ObjectConfig config) throws WebObjectsException, ClassNotFoundException, InstantiationException, InvocationTargetException, IllegalAccessException {
        PromptDao dao = null;

        Map<String, Object> elementSource = null;
        if (config != null) {
            elementSource = config.<Map<String, Object>>get("elementSource");
        }

        if (elementSource != null) {
        	final String daoClassName = (String) elementSource.get("className");
        	final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            final Class<CustomPromptDao<?, ?>> clazz = (Class<CustomPromptDao<?, ?>>) loader.loadClass(daoClassName);
            
            final String logTmp1 = clazz.getName();
            logger.debug("==> clazz.getName(): [{}]", logTmp1);

            final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            Constructor<CustomPromptDao<?, ?>> matchConstructor = null;
            for (final Constructor<?> constructor : constructors) {
                // 생성될 dao의 생성자 파라미터는 결정되어 있지 않으므로, 2개의 파라미터를 받는 첫번째 생성자를 무조건 수행
                if (constructor.getParameterTypes().length == NUM_2) {
                    matchConstructor = (Constructor<CustomPromptDao<?, ?>>) constructor;
                    break;
                }
            }

            final Object param1 = elementSource.get("param1");
            final Object param2 = elementSource.get("param2");

            if (matchConstructor != null) {
                dao = matchConstructor.newInstance(param1, param2);
            }
        }

        return dao;
    }
    
    
    /**
     * getPromptDao
     * @param session
     * @param webPrompt
     * @param config
     * @return
     */
    private static PromptDao getPromptDao(final WebIServerSession session, final WebPrompt webPrompt, final ObjectConfig config) throws WebObjectsException, ClassNotFoundException, InstantiationException, InvocationTargetException, IllegalAccessException {
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

                ClientResponse<?, ?> clientR = new MstrExpressionPromptApiDao(session, (WebExpressionPrompt) webPrompt);
                break;
            default:
                throw new SdkRuntimeException("Unsupported Prompt Type.");
            }
        }

        return dao;
    }
    
    
    /**
     * chargeElement
     * @param prompt
     * @param dao
     */
    private static void chargeElement(final Prompt prompt, final PromptDao dao) {
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
            prompt.setMeaning(dao.getMeaning());
        } 
        /*
        else {
        	throw new SdkRuntimeException("No dao found.");
        }
        */
    }
    
    
    /**
     * getChargedPrompt
     * @param session
     * @param webPrompt
     * @param config
     * @return
     */
    public static final Prompt getChargedPrompt(final WebIServerSession session, final WebPrompt webPrompt, final ObjectConfig config) throws WebObjectsException, ClassNotFoundException, InstantiationException, InvocationTargetException, IllegalAccessException {
    	final Prompt prompt = new Prompt(webPrompt.getID(),
                StringUtils.defaultString(config.<String>get("exTitle"), webPrompt.getTitle()),
                webPrompt.getPromptType());

        setExProp(prompt, config);
        chargeElement(prompt, getPromptDao(session, webPrompt, config));

        return prompt;
    }
    
    
    /**
     * getChargedPrompt
     * @param config
     * @return
     */
    public static final Prompt getChargedPrompt(final ObjectConfig config) throws WebObjectsException, ClassNotFoundException, InstantiationException, InvocationTargetException, IllegalAccessException {
    	Prompt prompt = null;
        
    	if(config != null) {
    		prompt = new Prompt(config.<String>get("objectId"), config.<String>get("exTitle"));
	        prompt.setPin(config.get("pin"));
	        setExProp(prompt, config);
	        chargeElement(prompt, getPromptDao(config));
    	}
        
        return prompt;
    }
    

    /**
     * getChargedPrompt
     * @param session
     * @param webPrompt
     * @return
     */
    public static final Prompt getChargedPrompt(final WebIServerSession session, final WebPrompt webPrompt) throws WebObjectsException, ClassNotFoundException, InstantiationException, InvocationTargetException, IllegalAccessException {
    	final String cacheItemId = CacheManager.getCacheItemId(CacheObjectType.PROMPT, session.getProjectID(), webPrompt.getID());
        ObjectConfig objectConfig = CacheManager.<ObjectConfig>getCache(cacheItemId);

        if (objectConfig == null) {
            objectConfig = ConfigManager.getInstance().getObjectConfig(webPrompt, session);
            CacheManager.setCache(cacheItemId, objectConfig);
        }

        return getChargedPrompt(session, webPrompt, objectConfig);
    }

    
    /**
     * 리포트 주석에 포함된 가상 프롬프트에 대한 처리 
     * 개별 가상 프롬프트에 대한 설정은 리포트, 프롬프트 주석이 아닌 별도의 리소스(.json, DB)에 처리
     * @param project
     * @param promptId
     * @return
     */
    public static final Prompt getChargedPrompt(final String project, final String promptId) throws WebObjectsException, ClassNotFoundException, InstantiationException, InvocationTargetException, IllegalAccessException {
    	final String cacheItemId = CacheManager.getCacheItemId(CacheObjectType.PROMPT, project, promptId);
        ObjectConfig objectConfig = CacheManager.<ObjectConfig>getCache(cacheItemId);

        if (objectConfig == null) {
            objectConfig = ConfigManager.getInstance().getObjectConfig(project, promptId);
            CacheManager.setCache(cacheItemId, objectConfig);
        }

        return getChargedPrompt(objectConfig);
    }
    
    
    /**
     * getClientResponse
     * @param <A>
     * @param <B>
     * @return
     */
    public static final <A, B> B getClientResponse(final WebIServerSession session, final String project, final String promptId, final A tClase1) throws WebObjectsException, ClassNotFoundException, InstantiationException, InvocationTargetException, IllegalAccessException {
    	B rtnObj = null;
    	
    	final String cacheItemId = CacheManager.getCacheItemId(CacheObjectType.PROMPT, project, promptId);
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
        	final WebPrompt prompt = (WebPrompt) session.getFactory().getObjectSource().getObject(promptId, EnumDSSXMLObjectTypes.DssXmlTypePrompt, true);
            promptDao = getPromptDao(session, prompt, objectConfig);
        }

        if (promptDao instanceof ClientResponse) {
        	rtnObj = ((ClientResponse<A, B>) promptDao).getClientResponse(tClase1);
        }

        return rtnObj;
    }

}
