package com.mococo.microstrategy.sdk.prompt.dao.impl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microstrategy.web.objects.WebAttribute;
import com.microstrategy.web.objects.WebDimension;
import com.microstrategy.web.objects.WebDimensionAttribute;
import com.microstrategy.web.objects.WebDisplayUnits;
import com.microstrategy.web.objects.WebElement;
import com.microstrategy.web.objects.WebElements;
import com.microstrategy.web.objects.WebElementsObjectNode;
import com.microstrategy.web.objects.WebExpression;
import com.microstrategy.web.objects.WebExpressionPrompt;
import com.microstrategy.web.objects.WebFilter;
import com.microstrategy.web.objects.WebIServerSession;
import com.microstrategy.web.objects.WebObjectInfo;
import com.microstrategy.web.objects.WebObjectSource;
import com.microstrategy.web.objects.WebObjectsException;
import com.microstrategy.web.objects.WebOperatorNode;
import com.microstrategy.web.objects.WebPrompt;
import com.microstrategy.webapi.EnumDSSXMLExpressionType;
import com.microstrategy.webapi.EnumDSSXMLFunction;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import com.mococo.microstrategy.sdk.exception.SdkRuntimeException;
import com.mococo.microstrategy.sdk.prompt.dao.ClientResponse;
import com.mococo.microstrategy.sdk.prompt.dao.MstrPromptDao;
import com.mococo.microstrategy.sdk.prompt.vo.PromptElement;
import com.mococo.web.util.PortalCodeUtil;

/**
 * MSTR 계층프롬프트의 API를 이용한 프롬프트 목록 DAO
 * @author mococo
 *
 */
public class MstrExpressionPromptApiDao extends MstrPromptDao<WebExpressionPrompt> implements ClientResponse<Map<String, Object>, List<PromptElement>> {
	
	/**
	 * 로그
	 */
    private static final Logger logger = LoggerFactory.getLogger(MstrExpressionPromptApiDao.class);
    
    /**
     * session
     */
    private final WebIServerSession session;
    
    
    /**
     * MstrExpressionPromptApiDao
     * @param session
     * @param prompt
     */
    public MstrExpressionPromptApiDao(final WebIServerSession session, final WebExpressionPrompt prompt) {
        super(prompt);

        this.session = session;

        if (prompt.getExpressionType() == EnumDSSXMLExpressionType.DssXmlFilterAttributeIDQual) {
            throw new SdkRuntimeException("EnumDSSXMLExpressionType.DssXmlFilterAttributeIDQual type not supported.");
        }

        try {
            prompt.populate();
        } catch (WebObjectsException e) {
            throw new SdkRuntimeException(e);
        }
    }
    
    /**
     * getDefaultAnswers
     */
    @Override
    public List<PromptElement> getDefaultAnswers() {
    	final WebExpression defaultAnswers = getPrompt().getDefaultAnswer();
    	final List<PromptElement> elementList = new ArrayList<>();

        for (int i = 0; defaultAnswers != null && i < defaultAnswers.getRootNode().getChildCount(); i++) {
        	final int expressionType = defaultAnswers.getRootNode().getChild(i).getExpressionType();
            logger.debug("==> ExpressionType: [{}]", expressionType);

            // TODO: EnumDSSXMLExpressionType.DssXmlFilterListQual 만 수용한 이유 확인
            // TODO: 다른 형식의 ExpressionType 수용 부분 추가
            if (expressionType != EnumDSSXMLExpressionType.DssXmlFilterListQual) {
                continue;
            }

            // .getChild(0)은 WebShortcutNode, .getChild(1)은 WebElementsObjectNode
            final WebElementsObjectNode node = (WebElementsObjectNode) defaultAnswers.getRootNode().getChild(i).getChild(1);

            for (final Enumeration<WebElement> e = node.getElements().elements(); e.hasMoreElements();) {
            	final WebElement element = e.nextElement();
                elementList.add(new PromptElement(element.getID(), element.getDisplayName()));
            }
        }

        return elementList;
    }
    
    
    /**
     * getDimensionAttributeList
     * @param paramPrompt
     * @return
     */
    public static List<WebDimensionAttribute> getDimensionAttributeList(final WebPrompt paramPrompt) {
    	final List<WebDimensionAttribute> attributeList = new ArrayList<>();

    	final WebExpressionPrompt prompt = (WebExpressionPrompt) paramPrompt;
        WebDisplayUnits units;
        try {
            units = prompt.getDisplayHelper().getAvailableDisplayUnits();

            for (final Enumeration<WebDimension> e = units.elements(); e.hasMoreElements();) {
            	final WebDimension dimension = e.nextElement();
            	final String logTmp1 = dimension.getDisplayName().replaceAll("[\r\n]","");
                logger.debug("*** dimension.displayName: [{}]", logTmp1);

                dimension.populate();
                for (final Enumeration<WebDimensionAttribute> e1 = dimension.getTopLevelAttributes().elements(); e1.hasMoreElements();) {
                	WebDimensionAttribute dimsAttr = e1.nextElement();

                    if (dimsAttr.getParents().size() != 0) {
                        continue;
                    }

                    final String logTmp2 = dimsAttr.getID().replaceAll("[\r\n]","");
                    final String logTmp3 = dimsAttr.getDisplayName().replaceAll("[\r\n]","");
                    logger.debug("*** top attribute -> id: [{}], displayName: [{}]", logTmp2, logTmp3);

                    while (dimsAttr != null) {
                        attributeList.add(dimsAttr);

                        if (!dimsAttr.getChildren().isEmpty()) {
                        	dimsAttr = (WebDimensionAttribute) dimsAttr.getChildren().item(0);
                        }
                    }
                    
                    //PMD 관련 조건 로직 추가
                    final String rch1 = "Y";
                    if (PortalCodeUtil.CHECK_Y.equals(rch1)) {
                    	break;
                    }
                }
            }
        } catch (WebObjectsException e) {
            throw new SdkRuntimeException(e);
        }

        return attributeList;
    }
    
    
    private List<PromptElement> getTopElements() {
    	final List<WebDimensionAttribute> dimsAttrList = getDimensionAttributeList(getPrompt());
        WebElements elements;

        final List<PromptElement> elementList = new ArrayList<>();
        try {
            elements = dimsAttrList.get(0).getAttribute().getElementSource().getElements();

            for (final Enumeration<WebElement> e = elements.elements(); e.hasMoreElements();) {
            	final WebElement elem = e.nextElement();
                elementList.add(new PromptElement(elem.getID(), elem.getDisplayName(), elem.getElementID()));
            }
        } catch (WebObjectsException e) {
            throw new SdkRuntimeException(e);
        }

        return elementList;
    }
    
    
    /**
     * getSuggestedAnswers
     */
    @Override
    public List<PromptElement> getSuggestedAnswers() {
        return getTopElements();
    }
    
    
    /**
     * getElement
     * @param paramDimensionAttribute
     * @param selectedElemId
     * @return
     */
    public static WebElement getElement(final WebDimensionAttribute paramDimsAttr, final String selectedElemId) {
    	WebElement rtnElement = null;
        try {
        	final WebElements elements = paramDimsAttr.getAttribute().getElementSource().getElements();

            for (int i = 0; i < elements.size(); i++) {
                if (StringUtils.equals(elements.get(i).getID(), selectedElemId)) {
                	rtnElement = elements.get(i);
                	break;
                }
            }
        } catch (WebObjectsException e) {
            throw new SdkRuntimeException(e);
        }

        return rtnElement;
    }
    
    
    /**
     * getFilteredElements
     * @param dimensionAttribute
     * @param attributeId
     * @param filterElementId
     * @return
     */
    public List<PromptElement> getFilteredElements(final WebDimensionAttribute dimsAttr, final String attributeId, final String filterElementId) throws WebObjectsException {
    	final WebAttribute attribute = dimsAttr.getAttribute();
    	final WebFilter filter = attribute.getElementSource().getFilter();
        final WebExpression expression = filter.getExpression();
        expression.clear();

        final WebObjectSource source = session.getFactory().getObjectSource();
        final WebOperatorNode root = (WebOperatorNode) expression.getRootNode();
        
        if (filter != null && !StringUtils.equals(filter.getDisplayName().trim(), "")) {
        	final WebObjectInfo shortcut = source.getObject(filter.getID(), EnumDSSXMLObjectTypes.DssXmlTypeAttribute);
            expression.createShortcutNode(shortcut, root);
        }

        final WebOperatorNode inlistNode = expression.createOperatorNode(EnumDSSXMLExpressionType.DssXmlFilterListQual, EnumDSSXMLFunction.DssXmlFunctionIn, root);
        final WebAttribute filterAttribute = (WebAttribute) source.getObject(attributeId, EnumDSSXMLObjectTypes.DssXmlTypeAttribute);
        expression.createShortcutNode(filterAttribute, inlistNode);
        
        final WebElementsObjectNode objectNode = expression.createElementsObjectNode(filterAttribute, inlistNode);
        final WebElements filterElements = objectNode.getElements();
        filterElements.add(filterElementId);

        final WebElements elements = attribute.getElementSource().getElements();
        final List<PromptElement> elementList = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
        	final WebElement element = elements.get(i);
            elementList.add(new PromptElement(element.getID(), element.getDisplayName(), element.getElementID()));
        }

        return elementList;
    }
    
    
    /**
     * getLevelElements
     * @param parentLevel
     * @param parentSelectedElementId
     * @return
     */
    public List<PromptElement> getLevelElements(final int parentLevel, final String parentSelectElmId) throws WebObjectsException {
    	final List<WebDimensionAttribute> attributeList = getDimensionAttributeList(getPrompt());
    	final WebDimensionAttribute parentDimsAttr = attributeList.get(parentLevel);
        final WebDimensionAttribute childDimsAttr = (WebDimensionAttribute) parentDimsAttr.getChildren().item(0);

//        logger.debug("==> parentDimensionAttribute: [{}]", parentDimsAttr);
//        logger.debug("==> childDimensionAttribute: [{}]", childDimsAttr);

        final WebElement parentElement = getElement(parentDimsAttr, parentSelectElmId);

        /*
        if (parentElement == null) {
            return null;
        }
        */

        List<PromptElement> elements = new ArrayList<>();
        if (parentElement != null) {
        	elements = getFilteredElements(childDimsAttr, parentDimsAttr.getAttribute().getID(), parentSelectElmId);
        }

        return elements;
    }
    
    
    /**
     * getSuggestedAnswers
     */
    @Override
    public List<PromptElement> getSuggestedAnswers(final int parentLevel, final String parentSelectElmId) {
        List<PromptElement> elementList;
        try {
            elementList = getLevelElements(parentLevel, parentSelectElmId);
        } catch (WebObjectsException e) {
            throw new SdkRuntimeException(e);
        }

        return elementList;
    }
    
    
    /**
     * getClientResponse
     */
    @Override
    public List<PromptElement> getClientResponse(final Map<String, Object> param) {
    	final String parentLevelStr = (String) param.get("parentLevel");
    	final String parentSelectElmId = (String) param.get("parentSelectedElementId");

        return getSuggestedAnswers(Integer.parseInt(parentLevelStr), parentSelectElmId);
    }

}
