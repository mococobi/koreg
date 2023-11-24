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

/**
 * MSTR 계층프롬프트의 API를 이용한 프롬프트 목록 DAO
 * 
 * @author hyoungilpark
 *
 */
public class MstrExpressionPromptApiDao extends MstrPromptDao<WebExpressionPrompt>
        implements ClientResponse<Map<String, Object>, List<PromptElement>> {
    private static final Logger logger = LoggerFactory.getLogger(MstrExpressionPromptApiDao.class);
    private final WebIServerSession session;

    public MstrExpressionPromptApiDao(WebIServerSession session, WebExpressionPrompt prompt) {
        super(prompt);

        this.session = session;

        if (((WebExpressionPrompt) prompt)
                .getExpressionType() == EnumDSSXMLExpressionType.DssXmlFilterAttributeIDQual) {
            throw new SdkRuntimeException("EnumDSSXMLExpressionType.DssXmlFilterAttributeIDQual type not supported.");
        }

        try {
            prompt.populate();
        } catch (WebObjectsException e) {
            throw new SdkRuntimeException(e);
        }
    }

    @Override
    public List<PromptElement> getDefaultAnswers() {
        WebExpression defaultAnswers = getPrompt().getDefaultAnswer();
        List<PromptElement> elementList = new ArrayList<PromptElement>();

        for (int i = 0; defaultAnswers != null && i < defaultAnswers.getRootNode().getChildCount(); i++) {
            int expressionType = defaultAnswers.getRootNode().getChild(i).getExpressionType();

            /*
             * DssXmlExpressionAggMetric 19 Specifies an aggregate metric qualification
             * DssXmlExpressionBanding 20 Specifies a banding qualification
             * DssXmlExpressionCanceledPrompt 24 DssXmlExpressionElementList 25
             * DssXmlExpressionElementSingle 26 DssXmlExpressionGeneric 1 Specifies a
             * generic expression type. DssXmlExpressionMDXSAPVariable 22 Specifies an
             * expression prompt supporting SAP Variables DssXmlExpressionReserved 0
             * DssXmlExpressionSQLQueryQual 23 DssXmlFilterAllAttributeQual 16 Specifies an
             * all attribute qualification DssXmlFilterAttributeDESCQual 18 Specifies an
             * attribute description qualification DssXmlFilterAttributeIDQual 17 Specifies
             * an attribute ID qualification DssXmlFilterBranchQual 14 Specifies a branch
             * qualification. DssXmlFilterEmbedQual 13 Specifies an embedded qualification.
             * DssXmlFilterJointFormQual 4 Specifies a joint form qualification
             * DssXmlFilterJointListFormQual 8 Specifies a joint list qualification
             * involving attribute forms DssXmlFilterJointListQual 7 Specifies a joint list
             * qualification DssXmlFilterListFormQual 6 Specifies a list qualification
             * involving an attribute form (example: Customer(Last Name) In ("Jacobson",
             * "Jones") DssXmlFilterListQual 5 Specifies a list qualification.
             * DssXmlFilterMetricExpression 12 Specifies a metric expression qualification
             * (example: M1 > M2). DssXmlFilterMultiBaseFormQual 3 Specifies a multy base
             * form qualification DssXmlFilterMultiMetricQual 11 Specifies a qualification
             * on multiple metrics. DssXmlFilterRelationshipQual 15 Specifies a relationship
             * qualification DssXmlFilterReportQual 21 Specifies a filter report
             * qualification DssXmlFilterSingleBaseFormExpression 9 Specifies a single base
             * form expression qualification. DssXmlFilterSingleBaseFormQual 2 Specifies a
             * single base form qualification (example: Customer(Last Name) Like "C*").
             * DssXmlFilterSingleMetricQual 10 Specifies a single metric qualification
             * (example: Sales > 100).
             */
            logger.debug("==> ExpressionType: [{}]", expressionType);

            // TODO: EnumDSSXMLExpressionType.DssXmlFilterListQual 만 수용한 이유 확인
            // TODO: 다른 형식의 ExpressionType 수용 부분 추가
            if (expressionType != EnumDSSXMLExpressionType.DssXmlFilterListQual) {
                continue;
            }

            // .getChild(0)은 WebShortcutNode, .getChild(1)은 WebElementsObjectNode
            WebElementsObjectNode node = (WebElementsObjectNode) defaultAnswers.getRootNode().getChild(i).getChild(1);

            for (Enumeration<WebElement> e = node.getElements().elements(); e.hasMoreElements();) {
                WebElement element = e.nextElement();
                elementList.add(new PromptElement(element.getID(), element.getDisplayName()));
            }
        }

        return elementList;
    }

    public static List<WebDimensionAttribute> getDimensionAttributeList(WebPrompt paramPrompt) {
        List<WebDimensionAttribute> attributeList = new ArrayList<WebDimensionAttribute>();

        WebExpressionPrompt prompt = (WebExpressionPrompt) paramPrompt;
        WebDisplayUnits units;
        try {
            units = prompt.getDisplayHelper().getAvailableDisplayUnits();

            for (Enumeration<WebDimension> e = units.elements(); e.hasMoreElements();) {
                WebDimension dimension = e.nextElement();
                logger.debug("*** dimension.displayName: [{}]", dimension.getDisplayName());

                dimension.populate();
                for (Enumeration<WebDimensionAttribute> e1 = dimension.getTopLevelAttributes().elements(); e1
                        .hasMoreElements();) {
                    WebDimensionAttribute dimensionAttribute = e1.nextElement();

                    if (dimensionAttribute.getParents().size() != 0) {
                        continue;
                    }

                    logger.debug("*** top attribute -> id: [{}], displayName: [{}]", dimensionAttribute.getID(),
                            dimensionAttribute.getDisplayName());

                    while (dimensionAttribute != null) {
                        attributeList.add(dimensionAttribute);

                        if (dimensionAttribute.getChildren().isEmpty()) {
                            dimensionAttribute = null;
                        } else {
                            dimensionAttribute = (WebDimensionAttribute) dimensionAttribute.getChildren().item(0);
                        }
                    }
                    break;
                }
            }
        } catch (WebObjectsException e) {
            throw new SdkRuntimeException(e);
        }

        return attributeList;
    }

    private List<PromptElement> getTopElements() {
        List<WebDimensionAttribute> dimensionAttributeList = getDimensionAttributeList(getPrompt());
        WebElements elements;

        List<PromptElement> elementList = new ArrayList<PromptElement>();
        try {
            elements = dimensionAttributeList.get(0).getAttribute().getElementSource().getElements();

            for (Enumeration<WebElement> e = elements.elements(); e.hasMoreElements();) {
                WebElement e1 = e.nextElement();
                elementList.add(new PromptElement(e1.getID(), e1.getDisplayName(), e1.getElementID()));
            }
        } catch (WebObjectsException e) {
            throw new SdkRuntimeException(e);
        }

        return elementList;
    }

    @Override
    public List<PromptElement> getSuggestedAnswers() {
        return getTopElements();
    }

    public static WebElement getElement(WebDimensionAttribute paramDimensionAttribute, String selectedElemId) {
        try {
            WebElements elements = paramDimensionAttribute.getAttribute().getElementSource().getElements();

            for (int i = 0; i < elements.size(); i++) {
                if (StringUtils.equals(elements.get(i).getID(), selectedElemId)) {
                    return elements.get(i);
                }
            }
        } catch (WebObjectsException e) {
            throw new SdkRuntimeException(e);
        }

        return null;
    }

    public List<PromptElement> getFilteredElements(WebDimensionAttribute dimensionAttribute, String attributeId,
            String filterElementId) throws WebObjectsException, IllegalArgumentException {
        WebAttribute attribute = dimensionAttribute.getAttribute();
        WebFilter filter = attribute.getElementSource().getFilter();
        WebExpression expression = filter.getExpression();
        expression.clear();

        WebObjectSource source = session.getFactory().getObjectSource();

        WebOperatorNode root = (WebOperatorNode) expression.getRootNode();
        if (filter != null && !StringUtils.equals(filter.getDisplayName().trim(), "")) {
            WebObjectInfo shortcut = source.getObject(filter.getID(), EnumDSSXMLObjectTypes.DssXmlTypeAttribute);
            expression.createShortcutNode(shortcut, root);
        }

        WebOperatorNode inlistNode = expression.createOperatorNode(EnumDSSXMLExpressionType.DssXmlFilterListQual,
                EnumDSSXMLFunction.DssXmlFunctionIn, root);
        WebAttribute filterAttribute = (WebAttribute) source.getObject(attributeId,
                EnumDSSXMLObjectTypes.DssXmlTypeAttribute);
        expression.createShortcutNode(filterAttribute, inlistNode);
        WebElementsObjectNode objectNode = expression.createElementsObjectNode(filterAttribute, inlistNode);
        WebElements filterElements = objectNode.getElements();
        filterElements.add(filterElementId);

        WebElements elements = attribute.getElementSource().getElements();
        List<PromptElement> elementList = new ArrayList<PromptElement>();
        for (int i = 0; i < elements.size(); i++) {
            WebElement element = elements.get(i);
            elementList.add(new PromptElement(element.getID(), element.getDisplayName(), element.getElementID()));
        }

        return elementList;
    }

    public List<PromptElement> getLevelElements(int parentLevel, String parentSelectedElementId)
            throws WebObjectsException, IllegalArgumentException {
        List<WebDimensionAttribute> attributeList = getDimensionAttributeList(getPrompt());
        WebDimensionAttribute parentDimensionAttribute = (WebDimensionAttribute) attributeList.get(parentLevel);
        WebDimensionAttribute childDimensionAttribute = (WebDimensionAttribute) parentDimensionAttribute.getChildren()
                .item(0);

        logger.debug("==> parentDimensionAttribute: [{}]", parentDimensionAttribute);
        logger.debug("==> childDimensionAttribute: [{}]", childDimensionAttribute);

        WebElement parentElement = getElement(parentDimensionAttribute, parentSelectedElementId);

        if (parentElement == null) {
            return null;
        }

        List<PromptElement> elements = getFilteredElements(childDimensionAttribute,
                parentDimensionAttribute.getAttribute().getID(), parentSelectedElementId);

        return elements;
    }

    @Override
    public List<PromptElement> getSuggestedAnswers(int parentLevel, String parentSelectedElementId) {
        List<PromptElement> elementList = null;
        try {
            elementList = getLevelElements(parentLevel, parentSelectedElementId);
        } catch (WebObjectsException e) {
            throw new SdkRuntimeException(e);
        }

        return elementList;
    }

    @Override
    public List<PromptElement> getClientResponse(Map<String, Object> param) {
        String parentLevelStr = (String) param.get("parentLevel");
        String parentSelectedElementId = (String) param.get("parentSelectedElementId");

        return getSuggestedAnswers(Integer.parseInt(parentLevelStr), parentSelectedElementId);
    }

}
