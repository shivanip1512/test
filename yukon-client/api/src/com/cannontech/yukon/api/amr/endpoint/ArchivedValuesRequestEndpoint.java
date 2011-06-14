package com.cannontech.yukon.api.amr.endpoint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jdom.Element;
import org.jdom.Namespace;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.xml.validation.XmlValidationException;
import org.springframework.xml.xpath.XPathException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoData;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.service.PaoSelectionService;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;
import com.cannontech.yukon.api.amr.endpoint.helper.ArchivedValuesResponseData;
import com.cannontech.yukon.api.amr.endpoint.helper.ArchivedValuesResponseData.PointData;
import com.cannontech.yukon.api.amr.endpoint.helper.ArchivedValuesResponseData.ValueSet;
import com.cannontech.yukon.api.amr.endpoint.helper.PointElement;
import com.cannontech.yukon.api.amr.endpoint.helper.PointSelector;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector.SelectorType;
import com.cannontech.yukon.api.amr.endpoint.helper.ResponseDescriptor;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Endpoint
public class ArchivedValuesRequestEndpoint {
    private UnitMeasureDao unitMeasureDao;
    private RawPointHistoryDao rawPointHistoryDao;
    private PointDao pointDao;
    private AttributeService attributeService;
    private StateDao stateDao;
    private DynamicDataSource dynamicDataSource;
    private PaoSelectionService paoSelectionService;

    private final static Namespace ns = YukonXml.getYukonNamespace();

    private Map<PointSelector.Type, HistorySelector> historySelectorMap = Maps.newHashMap();

    public ArchivedValuesRequestEndpoint() {
        historySelectorMap.put(PointSelector.Type.ATTRIBUTE, new AttributeHistorySelector());
        historySelectorMap.put(PointSelector.Type.POINT_NAME, new PointNameHistorySelector());
        historySelectorMap.put(PointSelector.Type.TYPE_AND_OFFSET,
                               new PointTypeOffsetHistorySelector());
        historySelectorMap.put(PointSelector.Type.DEFAULT_POINT_NAME,
                               new DefaultPointNameHistorySelector());
    }

    @PayloadRoot(namespace = "http://yukon.cannontech.com/api", localPart = "archivedValuesRequest")
    public Element invoke(Element archivedValuesRequest, LiteYukonUser user) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(archivedValuesRequest,
                                                  XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        Element response = new Element("archivedValuesResponse", ns);

        // The "response" node contains a child for each type of data to include in the output.
        // For example, if a "paoType" node is included as a child of "response", the response
        // should include the paoType.  If not, this data should be left out of the response.
        ArchivedValuesResponseData responseData = new ArchivedValuesResponseData();
        try {
            SimpleXPathTemplate requestTemplate =
                YukonXml.getXPathTemplateForElement(archivedValuesRequest);

            responseData.setResponseFields(findResponseTypes(requestTemplate));
            Node paosNode = requestTemplate.evaluateAsNode("/y:archivedValuesRequest/y:paos");
            List<Node> pointNodes = requestTemplate.evaluateAsNodeList("/y:archivedValuesRequest/y:point");

            Set<ResponseDescriptor> responseFields = responseData.getResponseFields();
            ImmutableSet<PaoData.OptionalField> requestedPaoFields = getOptionalPaoFields(responseFields);
            Map<PaoIdentifier, PaoData> paoDataByPaoId =
                paoSelectionService.selectPaoIdentifiersAndGetData(paosNode, requestedPaoFields);
            responseData.setPaoDataByPaoId(paoDataByPaoId);
            boolean flatten =
                requestTemplate.evaluateAsBoolean("/y:archivedValuesRequest/y:response/@flatten", false);

            for (Node pointNode : pointNodes) {
                SimpleXPathTemplate pointNodeTemplate = YukonXml.getXPathTemplateForNode(pointNode);
                gatherPointData(pointNodeTemplate, paoDataByPaoId.keySet(), responseFields,
                                responseData);
            }

            if (responseFields.contains(ResponseDescriptor.POINT_NAME)
                    || responseFields.contains(ResponseDescriptor.POINT_TYPE)
                    || responseFields.contains(ResponseDescriptor.UNIT_OF_MEASURE)) {
                populateLitePoints(responseData);
            }

            if (flatten) {
                buildFlattenedResponse(response, responseData);
            } else {
                buildResponse(response, responseData);
            }
        } catch (XmlValidationException xmle) {
            Element error = XMLFailureGenerator.generateFailure(response, xmle, "InvalidResponseType",
                                                                xmle.getMessage());
            response.addContent(error);
        } catch (XPathException xpe) {
            Element error = XMLFailureGenerator.generateFailure(response, xpe, "XmlParseError",
                                                                xpe.getMessage());
            response.addContent(error);
        } catch (DOMException dome) {
            Element error = XMLFailureGenerator.generateFailure(response, dome, "XmlParseError",
                                                                dome.getMessage());
            response.addContent(error);
        } catch (Exception exception) {
            Element error = XMLFailureGenerator.generateFailure(response, exception, "OtherError",
                                                                exception.getMessage());
            response.addContent(error);
        }

        return response;
    }

    private Set<ResponseDescriptor> findResponseTypes(SimpleXPathTemplate template)
            throws XmlValidationException, XPathException {
        List<Node> responseTypeNodes =
            template.evaluateAsNodeList("/y:archivedValuesRequest/y:response/*");

        Set<ResponseDescriptor> responseTypes = new HashSet<ResponseDescriptor>();
        for (Node node : responseTypeNodes) {
            ResponseDescriptor rt = ResponseDescriptor.getByElementName(node.getNodeName());
            if (rt == null) {
                throw new XmlValidationException("invalid response type " + node.getNodeName());
            }
            responseTypes.add(rt);
        }

        return responseTypes;
    }

    private ImmutableSet<PaoData.OptionalField> getOptionalPaoFields(Set<ResponseDescriptor> allRequestedFields) {
        ImmutableSet.Builder<PaoData.OptionalField> builder = ImmutableSet.builder();
        for (ResponseDescriptor requestedField : allRequestedFields) {
            if (requestedField.getPaoDataField() != null) {
                builder.add(requestedField.getPaoDataField());
            }
        }
        return builder.build();
    }

    /**
     * This method handles creating point elements for a single &lt;point&gt; tag.
     */
    private void gatherPointData(SimpleXPathTemplate pointNodeTemplate, Set<PaoIdentifier> paoIds,
                                 Set<ResponseDescriptor> responseFields,
                                 ArchivedValuesResponseData responseData) {
        PointElement inputPointElement = parsePointElement(pointNodeTemplate);
        PointSelector pointSelector = inputPointElement.getPointSelector();
        responseData.addPointSelector(pointSelector);

        HistorySelector historySelector = historySelectorMap.get(pointSelector.getType());
        Map<PaoIdentifier, PointData> pointDataByPaoId = Maps.newHashMap();
        for (PointValueSelector valueSelector : inputPointElement.getPointValueSelectors()) {
            ListMultimap<PaoIdentifier, PointValueQualityHolder> valueMap = null;
            if (valueSelector.getValueSelectorType() == SelectorType.SNAPSHOT) {
                valueMap =
                    historySelector.getPointSnapshotMap(paoIds, pointSelector, valueSelector);
            }
            else {
                valueMap =
                    historySelector.getPointValueMap(paoIds, pointSelector, valueSelector);
            }

            for (PaoIdentifier paoId : valueMap.keySet()) {
                PointData pointData = pointDataByPaoId.get(paoId);
                if (pointData == null) {
                    pointData = new PointData(paoId, pointSelector);
                    pointDataByPaoId.put(paoId, pointData);
                }
                List<PointValueQualityHolder> values = valueMap.get(paoId);
                ValueSet valueSet = new ValueSet(valueSelector, values);
                pointData.addValueSet(valueSet);
            }
        }

        responseData.addPointData(pointDataByPaoId.values());
    }

    private PointElement parsePointElement(SimpleXPathTemplate pointNodeTemplate) {
        PointSelector pointSelector = new PointSelector(pointNodeTemplate);

        // handle timeframe selectors
        List<Node> pointValueNodes = pointNodeTemplate.evaluateAsNodeList("*[position()>1]");
        List<PointValueSelector> pointValueSelectors = Lists.newArrayList();
        for (Node pointValueNode : pointValueNodes) {
            pointValueSelectors.add(PointValueSelector.fromNode(pointValueNode));
        }

        return new PointElement(pointSelector, pointValueSelectors);
    }

    private void populateLitePoints(ArchivedValuesResponseData responseData) {
        Set<PaoIdentifier> paoIds = responseData.getPaoIds();
        for (PointSelector pointSelector : responseData.getPointSelectors()) {
            HistorySelector historySelector = historySelectorMap.get(pointSelector.getType());
            Map<PaoIdentifier, LitePoint> litePointsById =
                historySelector.getLitePoints(pointSelector, paoIds);
            responseData.updateLitePoints(pointSelector, litePointsById);
        }
    }

    private void buildFlattenedResponse(Element response, ArchivedValuesResponseData responseData) {
        Set<ResponseDescriptor> responseFields = responseData.getResponseFields();

        for (Entry<PaoIdentifier, PaoData> entry : responseData.getPaoDataByPaoId().entrySet()) {
            PaoIdentifier paoId = entry.getKey();
            PaoData paoData = entry.getValue();

            for (PointData pointData : responseData.getPointDataByPaoId(paoId)) {
                // For each point element, there is a list of lists of values.
                for (ValueSet valueSet : pointData.getValueSets()) {
                    for (PointValueQualityHolder value : valueSet.getValues()) {
                        Element valueElement = createValueElement(value, responseFields);

                        addPaoAttributes(valueElement, paoData, responseFields);
                        addPointData(valueElement, pointData, responseFields);

                        if (valueSet.getSelector().getLabel() != null) {
                            valueElement.setAttribute("label", valueSet.getSelector().getLabel());
                        }
                        response.addContent(valueElement);
                    }
                }
            }
        }
    }

    private void buildResponse(Element response, ArchivedValuesResponseData responseData) {
        for (PaoData paoData : responseData.getPaoDataByPaoId().values()) {
            Element paoElement = createPaoElement(paoData, responseData);
            response.addContent(paoElement);
        }
    }

    private Element createPaoElement(PaoData paoData, ArchivedValuesResponseData responseData) {
        Element paoElement = new Element("pao", ns);

        Set<ResponseDescriptor> responseFields = responseData.getResponseFields();
        addPaoAttributes(paoElement, paoData, responseFields);

        for (PointData pointData : responseData.getPointDataByPaoId(paoData.getPaoId())) {
            Element pointElement = createPointElement(pointData, responseData);
            paoElement.addContent(pointElement);
        }

        return paoElement;
    }

    private void addPaoAttributes(Element element, PaoData paoData,
                                  Set<ResponseDescriptor> responseFields) {
        PaoIdentifier paoId = paoData.getPaoId();
        if (responseFields.contains(ResponseDescriptor.PAO_TYPE)) {
            element.setAttribute("paoType", paoId.getPaoType().getDbString());
        }

        if (responseFields.contains(ResponseDescriptor.PAO_ID)) {
            element.setAttribute("paoId", Integer.toString(paoId.getPaoId()));
        }

        if (responseFields.contains(ResponseDescriptor.NAME)) {
            element.setAttribute("name", paoData.getName());
        }

        if (responseFields.contains(ResponseDescriptor.CARRIER_ADDRESS)) {
            element.setAttribute("carrierAddress", Integer.toString(paoData.getCarrierAddress()));
        }

        if (responseFields.contains(ResponseDescriptor.ENABLED)) {
            element.setAttribute("enabled", paoData.isEnabled() ? "true" : "false");
        }

        if (responseFields.contains(ResponseDescriptor.METER_NUMBER)) {
            String meterNumber = paoData.getMeterNumber();
            if (meterNumber != null) {
                element.setAttribute("meterNumber", meterNumber);
            }
        }
    }

    private Element createPointElement(PointData pointData, ArchivedValuesResponseData responseData) {
        Element pointElement = new Element("point", ns);

        Set<ResponseDescriptor> responseFields = responseData.getResponseFields();
        addPointData(pointElement, pointData, responseFields);

        // For each point element, there is a list of lists of values.
        for (ValueSet valueSet : pointData.getValueSets()) {
            Element valueElement = createValueElements(valueSet, responseData);
            pointElement.addContent(valueElement);
        }

        return pointElement;
    }

    private void addPointData(Element element, PointData pointData,
                              Set<ResponseDescriptor> responseFields) {
        if (responseFields.contains(ResponseDescriptor.POINT_NAME)
                || responseFields.contains(ResponseDescriptor.POINT_TYPE)
                || responseFields.contains(ResponseDescriptor.UNIT_OF_MEASURE)) {
            LitePoint litePoint = pointData.getLitePoint();
            if (litePoint == null) {
                throw new NullPointerException("could not find LitePoint for point "
                                               + pointData.getPointSelector() + " and pao "
                                               + pointData.getPaoId());
            }

            if (responseFields.contains(ResponseDescriptor.POINT_NAME)) {
                element.setAttribute("pointName", litePoint.getPointName());
            }

            if (responseFields.contains(ResponseDescriptor.POINT_TYPE)) {
                element.setAttribute("pointType", litePoint.getPointTypeEnum()
                    .getPointTypeString());
            }

            if (responseFields.contains(ResponseDescriptor.UNIT_OF_MEASURE)) {
                LiteUnitMeasure uofm = unitMeasureDao.getLiteUnitMeasure(litePoint.getUofmID());
                element.setAttribute("uofm", uofm.getUnitMeasureName());
            }
        }
    }

    private Element createValueElements(ValueSet valueSet, ArchivedValuesResponseData responseData) {
        List<Element> valueElementList = new ArrayList<Element>(valueSet.getValues().size());

        Set<ResponseDescriptor> responseFields = responseData.getResponseFields();

        for (PointValueQualityHolder value : valueSet.getValues()) {
            Element valueElement = createValueElement(value, responseFields);
            valueElementList.add(valueElement);
        }

        Element returnElement = null;
        if (valueElementList.isEmpty()) {
            returnElement = new Element("nullValue", ns);
        } else if (valueElementList.size() == 1) {
            returnElement = valueElementList.get(0);
        } else {
            returnElement = new Element("list", ns);
            for (Element valueElement : valueElementList) {
                returnElement.addContent(valueElement);
            }
        }

        if (valueSet.getSelector().getLabel() != null) {
            returnElement.setAttribute("label", valueSet.getSelector().getLabel());
        }

        return returnElement;
    }

    private Element createValueElement(PointValueQualityHolder value,
                                       Set<ResponseDescriptor> responseFields) {
        Element valueElement = XmlUtils.createDoubleElement("value", ns, value.getValue());
        DateTime timeStamp = new DateTime(value.getPointDataTimeStamp());
        DateTimeFormatter dtf = ISODateTimeFormat.dateTime();
        valueElement.setAttribute("timestamp", dtf.print(timeStamp));

        if (responseFields.contains(ResponseDescriptor.QUALITY)) {
            String quality = String.valueOf(value.getPointQuality().getQuality());
            valueElement.setAttribute("quality", quality);
        }
        if (responseFields.contains(ResponseDescriptor.STATUS_TEXT)) {
            valueElement.setAttribute("statusText", value.getPointQuality().getDescription());
        }
        return valueElement;
    }

    private interface HistorySelector {
        public ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointValueMap(Set<PaoIdentifier> paos,
                                                                                     PointSelector pointSelector,
                                                                                     PointValueSelector selector);

        public ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointSnapshotMap(Set<PaoIdentifier> paos,
                                                                                        PointSelector pointSelector,
                                                                                        PointValueSelector selector);

        public Map<PaoIdentifier, LitePoint> getLitePoints(PointSelector pointSelector, Set<PaoIdentifier> paos);
    }

    private class AttributeHistorySelector implements HistorySelector {
        @Override
        public ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointValueMap(Set<PaoIdentifier> paos,
                                                                                     PointSelector pointSelector,
                                                                                     PointValueSelector selector) {
            String name = pointSelector.getName();
            Attribute attribute = attributeService.resolveAttributeName(name);
            if (selector.getNumberOfRows() != null) {   
                return rawPointHistoryDao.getLimitedAttributeData(paos, attribute,
                                                                  selector.getStartDate(), 
                                                                  selector.getStopDate(), 
                                                                  selector.getNumberOfRows(), 
                                                                  false, 
                                                                  selector.getClusivity(), 
                                                                  selector.getOrder());
            } else {
                return rawPointHistoryDao.getAttributeData(paos, attribute,
                                                           selector.getStartDate(),
                                                           selector.getStopDate(), false, 
                                                           selector.getClusivity(), 
                                                           selector.getOrder());
            }
        }

        @Override
        public ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointSnapshotMap(Set<PaoIdentifier> paos,
                                                                                        PointSelector pointSelector,
                                                                                        PointValueSelector selector) {
            String name = pointSelector.getName();
            Attribute attribute = attributeService.resolveAttributeName(name);
            ListMultimap<PaoIdentifier, PointValueQualityHolder> resultMap = ArrayListMultimap.create();
           /* Map<PaoIdentifier, LitePoint> paoPointMap = attributeService.getPointsForAttribute(paos, attribute);
            
            
            
            for (PaoIdentifier paoIdentifier : paoPointMap.keySet()) {
                LitePoint point = paoPointMap.get(paoIdentifier);
                PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(point.getLiteID());
                resultMap.put(paoIdentifier, pointValue);
            }*/
            
            return resultMap;
        }

        @Override
        public Map<PaoIdentifier, LitePoint> getLitePoints(PointSelector pointSelector,
                                                           Set<PaoIdentifier> paos) {
            Attribute attribute = attributeService.resolveAttributeName(pointSelector.getName());
            List<PaoPointIdentifier> paoPointIdentifiers = Lists.newArrayList();
            for (PaoIdentifier pao : paos) {
                paoPointIdentifiers.add(attributeService.getPaoPointIdentifierForAttribute(pao, attribute));
            }
            Map<PaoIdentifier, LitePoint> retVal = Maps.newHashMap();
            Map<PaoPointIdentifier, LitePoint> litePointsById =
                pointDao.getLitePointsById(paoPointIdentifiers);
            for (Entry<PaoPointIdentifier, LitePoint> entry : litePointsById.entrySet()) {
                retVal.put(entry.getKey().getPaoIdentifier(), entry.getValue());
            }
            return retVal;
        }
    }

    private class PointNameHistorySelector implements HistorySelector {
        @Override
        public ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointValueMap(Set<PaoIdentifier> paos,
                                                                                     PointSelector pointSelector,
                                                                                     PointValueSelector selector) {
            String name = pointSelector.getName();
            if (selector.getNumberOfRows() != null) {
                return rawPointHistoryDao.getLimitedDataByPointName(paos, name, 
                                                         selector.getStartDate(), 
                                                         selector.getStopDate(), 
                                                         selector.getNumberOfRows(), 
                                                         selector.getClusivity(), 
                                                         selector.getOrder());
            } else {
                return rawPointHistoryDao.getDataByPointName(paos, name, selector.getStartDate(), 
                                                             selector.getStopDate(), 
                                                             selector.getClusivity(), 
                                                             selector.getOrder());
            }
        }

        @Override
        public ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointSnapshotMap(Set<PaoIdentifier> paos,
                                                                                        PointSelector pointSelector,
                                                                                        PointValueSelector selector) {
            String name = pointSelector.getName();
            
            
            
            return null;   
        }

        @Override
        public Map<PaoIdentifier, LitePoint> getLitePoints(PointSelector pointSelector,
                                                           Set<PaoIdentifier> paos) {
            return pointDao.getLitePointsByPointName(paos, pointSelector.getName());
        }
    }

    private class PointTypeOffsetHistorySelector implements HistorySelector {
        @Override
        public ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointValueMap(Set<PaoIdentifier> paos,
                                                                                     PointSelector pointSelector,
                                                                                     PointValueSelector selector) {
            PointType type = pointSelector.getPointType();
            int offset = pointSelector.getOffset();
            if (selector.getNumberOfRows() != null) {
                return rawPointHistoryDao.getLimitedDataByTypeAndOffset(paos, type, offset, 
                                                                        selector.getStartDate(), 
                                                                        selector.getStopDate(), 
                                                                        selector.getNumberOfRows(), 
                                                                        selector.getClusivity(), 
                                                                        selector.getOrder());
            } else {
                return rawPointHistoryDao.getDataByTypeAndOffset(paos, type, offset, 
                                                                 selector.getStartDate(), 
                                                                 selector.getStopDate(), 
                                                                 selector.getClusivity(), 
                                                                 selector.getOrder());
            }
        }

        @Override
        public ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointSnapshotMap(Set<PaoIdentifier> paos,
                                                                                        PointSelector pointSelector,
                                                                                        PointValueSelector selector) {
            // TODO
            return null;   
        }

        @Override
        public Map<PaoIdentifier, LitePoint> getLitePoints(PointSelector pointSelector,
                                                           Set<PaoIdentifier> paos) {
            PointType type = pointSelector.getPointType();
            int offset = pointSelector.getOffset();
            return pointDao.getLitePointsByTypeAndOffset(paos, type, offset);
        }
    }

    private class DefaultPointNameHistorySelector implements HistorySelector {
        @Override
        public ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointValueMap(Set<PaoIdentifier> paos,
                                                                                     PointSelector pointSelector,
                                                                                     PointValueSelector selector) {
            String defaultName = pointSelector.getName();
            if (selector.getNumberOfRows() != null) {
                return rawPointHistoryDao.getLimitedDataByDefaultPointName(paos, defaultName, 
                                                                           selector.getStartDate(), 
                                                                           selector.getStopDate(), 
                                                                           selector.getNumberOfRows(), 
                                                                           selector.getClusivity(), 
                                                                           selector.getOrder());
            } else {
                return rawPointHistoryDao.getDataByDefaultPointName(paos, defaultName, 
                                                                    selector.getStartDate(), 
                                                                    selector.getStopDate(), 
                                                                    selector.getClusivity(), 
                                                                    selector.getOrder());
            }
        }

        @Override
        public ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointSnapshotMap(Set<PaoIdentifier> paos,
                                                                                        PointSelector pointSelector,
                                                                                        PointValueSelector selector) {
            String name = pointSelector.getName();
            // TODO
            return null;   
        }

        @Override
        public Map<PaoIdentifier, LitePoint> getLitePoints(PointSelector pointSelector,
                                                           Set<PaoIdentifier> paos) {
            return pointDao.getLitePointsByDefaultName(paos, pointSelector.getName());
        }
    }

    @Autowired
    public void setUnitMeasureDao(UnitMeasureDao unitMeasureDao) {
        this.unitMeasureDao = unitMeasureDao;
    }

    @Autowired
    public void setRawPointHistoryDao(RawPointHistoryDao rawPointHistoryDao) {
        this.rawPointHistoryDao = rawPointHistoryDao;
    }

    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }

    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Autowired
    public void setStateDao(StateDao stateDao) {
        this.stateDao = stateDao;
    }

    @Autowired
    public void setDynamicDataSource(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }

    @Autowired
    public void setPaoSelectionService(PaoSelectionService paoSelectionService) {
        this.paoSelectionService = paoSelectionService;
    }
}
