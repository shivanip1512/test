package com.cannontech.yukon.api.amr.endpoint;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
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

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.model.PaoData;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.service.PaoSelectionService;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.StateDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.yukon.api.amr.endpoint.helper.ArchivedValuesResponseData;
import com.cannontech.yukon.api.amr.endpoint.helper.PointElement;
import com.cannontech.yukon.api.amr.endpoint.helper.PointSelector;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector;
import com.cannontech.yukon.api.amr.endpoint.helper.PointValueSelector.SelectorType;
import com.cannontech.yukon.api.amr.endpoint.helper.ResponseDescriptor;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Endpoint
public class ArchivedValuesRequestEndpoint {
    private Logger log = YukonLogManager.getLogger(ArchivedValuesRequestEndpoint.class);

    private RawPointHistoryDao rawPointHistoryDao;
    private PointDao pointDao;
    private AttributeService attributeService;
    private StateDao stateDao;
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
            log.debug("looking up PAOs");
            Map<PaoIdentifier, PaoData> paoDataByPaoIdentifier =
                paoSelectionService.selectPaoIdentifiersAndGetData(paosNode, requestedPaoFields);
            responseData.setPaoDataByPaoIdentifier(paoDataByPaoIdentifier);
            boolean flatten =
                requestTemplate.evaluateAsBoolean("/y:archivedValuesRequest/y:response/@flatten", false);
            responseData.setFlatten(flatten);

            log.debug("building PAO Elements");
            buildPaoElements(responseData, response);

            log.debug("processing points");
            for (Node pointNode : pointNodes) {
                buildResponseForPoint(pointNode, responseData, response);
            }
        } catch (XmlValidationException xmle) {
            log.error("XML validation error", xmle);
            Element error = XMLFailureGenerator.generateFailure(response, xmle, "InvalidResponseType",
                                                                xmle.getMessage());
            response.addContent(error);
        } catch (XPathException xpe) {
            log.error("XML validation error", xpe);
            Element error = XMLFailureGenerator.generateFailure(response, xpe, "XmlParseError",
                                                                xpe.getMessage());
            response.addContent(error);
        } catch (DOMException dome) {
            log.error("XML validation error", dome);
            Element error = XMLFailureGenerator.generateFailure(response, dome, "XmlParseError",
                                                                dome.getMessage());
            response.addContent(error);
        } catch (Exception exception) {
            log.error("other error", exception);
            Element error = XMLFailureGenerator.generateFailure(response, exception, "OtherError",
                                                                exception.getMessage());
            response.addContent(error);
        }

        log.debug("returning response");
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

    private void buildPaoElements(ArchivedValuesResponseData responseData, Element response) {
        if (responseData.isFlatten()) {
            // No PAO elements to build--we'll only have value elements.
            return;
        }

        for (PaoData paoData : responseData.getAllPaoData()) {
            Element paoElement = createPaoElement(paoData, responseData);
            responseData.addPaoElement(paoData.getPaoIdentifier(), paoElement);
            response.addContent(paoElement);
        }
    }

    private void buildResponseForPoint(Node pointNode, ArchivedValuesResponseData responseData,
                                       Element response) {
        SimpleXPathTemplate pointNodeTemplate = YukonXml.getXPathTemplateForNode(pointNode);
        Set<ResponseDescriptor> responseFields = responseData.getResponseFields();
        Set<PaoIdentifier> paoIdentifiers = responseData.getPaoIdentifiers();

        PointSelector pointSelector = new PointSelector(pointNodeTemplate);
        LogHelper.debug(log, "building response for point %s for %d PAOs",
                        pointSelector, paoIdentifiers.size());

        // handle timeframe selectors
        List<Node> pointValueNodes = pointNodeTemplate.evaluateAsNodeList("*[position()>1]");
        List<PointValueSelector> pointValueSelectors = Lists.newArrayList();
        for (Node pointValueNode : pointValueNodes) {
            pointValueSelectors.add(PointValueSelector.fromNode(pointValueNode));
        }

        PointElement inputPointElement = new PointElement(pointSelector, pointValueSelectors);

        HistorySelector historySelector = historySelectorMap.get(pointSelector.getType());
        Map<PaoIdentifier, PointInfo> pointInfoById = null;
        if (responseFields.contains(ResponseDescriptor.POINT_NAME)
                || responseFields.contains(ResponseDescriptor.POINT_TYPE)
                || responseFields.contains(ResponseDescriptor.UNIT_OF_MEASURE)
                || responseFields.contains(ResponseDescriptor.STATUS_TEXT)) {
            pointInfoById = historySelector.getPointInfo(pointSelector, paoIdentifiers);
        }

        Map<PaoIdentifier, Element> pointElementsByPaoIdentifier = Maps.newHashMap();
        for (PointValueSelector valueSelector : inputPointElement.getPointValueSelectors()) {
            LogHelper.debug(log, "  working on %s", valueSelector);
            ListMultimap<PaoIdentifier, PointValueQualityHolder> valueMap = null;
            if (valueSelector.getValueSelectorType() == SelectorType.SNAPSHOT) {
                valueMap =
                    historySelector.getPointSnapshotMap(paoIdentifiers, pointSelector, valueSelector);
            }
            else {
                valueMap =
                    historySelector.getPointValueMap(paoIdentifiers, pointSelector, valueSelector);
            }

            String valueLabel = valueSelector.getLabel();
            for (PaoIdentifier paoIdentifier : paoIdentifiers) {
                LogHelper.debug(log, "    pao %s", paoIdentifier);
                PointInfo pointInfo = pointInfoById == null ? null : pointInfoById.get(paoIdentifier);
                if (pointInfo == null) {
                    // This can happen if an invalid attribute was requested.  An error is already
                    // generated in AttributeHistorySelector.getPointInfo.  In this case, however,
                    // we don't want to create a point element since there's nothing to put in it.
                    continue;
                }
                Element paoElement = responseData.getPaoElement(paoIdentifier);
                Element pointElement = null;
                if (!responseData.isFlatten()) {
                    pointElement = pointElementsByPaoIdentifier.get(paoIdentifier);
                    if (pointElement == null) {
                        pointElement = createPointElement(pointInfo, pointSelector, paoIdentifier, responseData);
                        paoElement.addContent(pointElement);
                        pointElementsByPaoIdentifier.put(paoIdentifier, pointElement);
                    }
                }

                PaoData paoData = responseData.getPaoData(paoIdentifier);
                Map<Integer, LiteState> statesForStateGroupId = null;
                if (pointInfo != null) {
                    statesForStateGroupId =
                        getStatesForGroupId(pointInfo.getStateGroupId(), responseData);
                }
                List<PointValueQualityHolder> values = valueMap.get(paoIdentifier);
                List<Element> valueElementList = Lists.newArrayList();
                for (PointValueQualityHolder value : values) {
                    LogHelper.debug(log, "      %tc/%f", value.getPointDataTimeStamp(),
                                    value.getValue());
                    if (responseData.isFlatten()) {
                        Element valueElement =
                            buildFlatValue(value, responseFields, valueLabel, paoData, pointInfo,
                                           statesForStateGroupId, pointSelector);
                        response.addContent(valueElement);
                    } else {
                        Element valueElement = createValueElement(value, responseFields, pointSelector,
                                                                  statesForStateGroupId);
                        valueElementList.add(valueElement);
                    }
                }
                if (!responseData.isFlatten()) {
                    Element elementToAdd = null;
                    if (valueElementList.isEmpty()) {
                        elementToAdd = new Element("nullValue", ns);
                    } else if (valueElementList.size() == 1) {
                        elementToAdd = valueElementList.get(0);
                    } else {
                        elementToAdd = new Element("list", ns);
                        for (Element valueElement : valueElementList) {
                            elementToAdd.addContent(valueElement);
                        }
                    }

                    if (valueLabel != null) {
                        elementToAdd.setAttribute("label", valueLabel);
                    }

                    pointElement.addContent(elementToAdd);
                }
            }
        }
    }

    private Element buildFlatValue(PointValueQualityHolder value,
                                   Set<ResponseDescriptor> responseFields, String label,
                                   PaoData paoData, PointInfo pointInfo,
                                   Map<Integer, LiteState> statesForStateGroupId,
                                   PointSelector pointSelector) {
        Element valueElement = createValueElement(value, responseFields,
                                                  pointSelector,
                                                  statesForStateGroupId);

        addPaoAttributes(valueElement, paoData, responseFields);
        addPointData(valueElement, pointInfo, pointSelector, paoData.getPaoIdentifier(), responseFields);

        if (label != null) {
            valueElement.setAttribute("label", label);
        }

        return valueElement;
    }

    private Element createPaoElement(PaoData paoData, ArchivedValuesResponseData responseData) {
        Element paoElement = new Element("pao", ns);

        Set<ResponseDescriptor> responseFields = responseData.getResponseFields();
        addPaoAttributes(paoElement, paoData, responseFields);

        return paoElement;
    }

    private void addPaoAttributes(Element element, PaoData paoData,
                                  Set<ResponseDescriptor> responseFields) {
        PaoIdentifier paoIdentifier = paoData.getPaoIdentifier();
        if (responseFields.contains(ResponseDescriptor.PAO_TYPE)) {
            element.setAttribute("paoType", paoIdentifier.getPaoType().getDbString());
        }

        if (responseFields.contains(ResponseDescriptor.PAO_ID)) {
            element.setAttribute("paoId", Integer.toString(paoIdentifier.getPaoId()));
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

    private Element createPointElement(PointInfo pointInfo, PointSelector pointSelector,
                                       PaoIdentifier paoIdentifier,
                                       ArchivedValuesResponseData responseData) {
        Element pointElement = new Element("point", ns);

        Set<ResponseDescriptor> responseFields = responseData.getResponseFields();
        addPointData(pointElement, pointInfo, pointSelector, paoIdentifier, responseFields);

        return pointElement;
    }

    private Map<Integer, LiteState> getStatesForGroupId(Integer stateGroupId,
                                                        ArchivedValuesResponseData responseData) {
        Map<Integer, Map<Integer, LiteState>> statesByGroupIdAndRawState =
            responseData.getStatesByGroupIdAndRawState();
        synchronized (statesByGroupIdAndRawState) {
            Map<Integer, LiteState> retVal = statesByGroupIdAndRawState.get(stateGroupId);
            if (retVal == null) {
                retVal = Maps.newHashMap();
                LiteState[] liteStates = stateDao.getLiteStates(stateGroupId);
                for (LiteState liteState : liteStates) {
                    retVal.put(liteState.getStateRawState(), liteState);
                }
            }
            return retVal;
        }
    }

    private void addPointData(Element element, PointInfo pointInfo, PointSelector pointSelector,
                              PaoIdentifier paoIdentifier, Set<ResponseDescriptor> responseFields) {
        if (responseFields.contains(ResponseDescriptor.POINT_NAME)
                || responseFields.contains(ResponseDescriptor.POINT_TYPE)
                || responseFields.contains(ResponseDescriptor.UNIT_OF_MEASURE)) {
            if (pointInfo == null) {
                throw new NullPointerException("could not find pointInfo for point "
                                               + pointSelector + " and pao " + paoIdentifier);
            }

            if (responseFields.contains(ResponseDescriptor.POINT_NAME)) {
                element.setAttribute("pointName", pointInfo.getName());
            }

            if (responseFields.contains(ResponseDescriptor.POINT_TYPE)) {
                element.setAttribute("pointType", pointInfo.getType()
                    .getPointTypeString());
            }

            if (responseFields.contains(ResponseDescriptor.UNIT_OF_MEASURE)) {
                element.setAttribute("uofm", pointInfo.getUnitOfMeasure());
            }
        }
    }

    private Element createValueElement(PointValueQualityHolder value,
                                       Set<ResponseDescriptor> responseFields,
                                       PointSelector pointSelector,
                                       Map<Integer, LiteState> statesForStateGroupId) {
        Element valueElement = XmlUtils.createDoubleElement("value", ns, value.getValue());
        DateTime timeStamp = new DateTime(value.getPointDataTimeStamp());
        DateTimeFormatter dtf = ISODateTimeFormat.dateTime();
        valueElement.setAttribute("timestamp", dtf.print(timeStamp));

        if (responseFields.contains(ResponseDescriptor.QUALITY)) {
            String quality = value.getPointQuality().getDescription();
            valueElement.setAttribute("quality", quality);
        }
        if (responseFields.contains(ResponseDescriptor.STATUS_TEXT)) {
            int stateValue = (int) value.getValue();
            LiteState liteState = statesForStateGroupId.get(stateValue);
            if (liteState == null) {
                log.error("could not find state for value " + stateValue + " and point "
                          + pointSelector);
            } else {
                valueElement.setAttribute("statusText", liteState.getStateText());
            }
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

        public Map<PaoIdentifier, PointInfo> getPointInfo(PointSelector pointSelector, Set<PaoIdentifier> paos);
    }

    private class AttributeHistorySelector implements HistorySelector {
        @Override
        public ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointValueMap(Set<PaoIdentifier> paos,
                                                                                     PointSelector pointSelector,
                                                                                     PointValueSelector selector) {
            String name = pointSelector.getName();
            Attribute attribute = attributeService.resolveAttributeName(name);
            Date startDate = null;
            if (selector.getStartDate() != null) {
                startDate = selector.getStartDate().toDate();
            }
            Date stopDate = null;
            if (selector.getStopDate() != null) {
                stopDate = selector.getStopDate().toDate();
            }
            if (selector.getNumberOfRows() != null) {
                return rawPointHistoryDao.getLimitedAttributeData(paos, attribute, startDate,
                                                                  stopDate,
                                                                  selector.getNumberOfRows(),
                                                                  false, selector.getClusivity(),
                                                                  selector.getOrder());
            } else {
                return rawPointHistoryDao.getAttributeData(paos, attribute, startDate, stopDate,
                                                           false, selector.getClusivity(), 
                                                           selector.getOrder());
            }
        }

        @Override
        public ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointSnapshotMap(Set<PaoIdentifier> paos,
                                                                                        PointSelector pointSelector,
                                                                                        PointValueSelector selector) {
            /*
            String name = pointSelector.getName();
            Attribute attribute = attributeService.resolveAttributeName(name);
            ListMultimap<PaoIdentifier, PointValueQualityHolder> resultMap = ArrayListMultimap.create();
            Map<PaoIdentifier, LitePoint> paoPointMap = attributeService.getPointsForAttribute(paos, attribute);
            
            
            
            for (PaoIdentifier paoIdentifier : paoPointMap.keySet()) {
                LitePoint point = paoPointMap.get(paoIdentifier);
                PointValueQualityHolder pointValue = dynamicDataSource.getPointValue(point.getLiteID());
                resultMap.put(paoIdentifier, pointValue);
            }*/

            return null;
        }

        @Override
        public Map<PaoIdentifier, PointInfo> getPointInfo(PointSelector pointSelector,
                                                          Set<PaoIdentifier> paos) {
            Attribute attribute = attributeService.resolveAttributeName(pointSelector.getName());
            List<PaoPointIdentifier> paoPointIdentifiers = Lists.newArrayList();
            for (PaoIdentifier pao : paos) {
                try {
                    PaoPointIdentifier paoPointIdentifierForAttribute =
                            attributeService.getPaoPointIdentifierForAttribute(pao, attribute);
                    paoPointIdentifiers.add(paoPointIdentifierForAttribute);
                } catch (IllegalUseOfAttribute isoa) {
                    log.error("attribute " + attribute + " is not valid for pao " + pao);
                }
            }
            Map<PaoIdentifier, PointInfo> retVal = Maps.newHashMap();
            Map<PaoPointIdentifier, PointInfo> pointInfoById =
                pointDao.getPointInfoById(paoPointIdentifiers);
            for (Entry<PaoPointIdentifier, PointInfo> entry : pointInfoById.entrySet()) {
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
            Date startDate = null;
            if (selector.getStartDate() != null) {
                startDate = selector.getStartDate().toDate();
            }
            Date stopDate = null;
            if (selector.getStopDate() != null) {
                stopDate = selector.getStopDate().toDate();
            }
            if (selector.getNumberOfRows() != null) {
                return rawPointHistoryDao.getLimitedDataByPointName(paos, name, startDate, 
                                                                    stopDate,
                                                                    selector.getNumberOfRows(), 
                                                                    selector.getClusivity(), 
                                                                    selector.getOrder());
            } else {
                return rawPointHistoryDao.getDataByPointName(paos, name, startDate, stopDate, 
                                                             selector.getClusivity(), 
                                                             selector.getOrder());
            }
        }

        @Override
        public ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointSnapshotMap(Set<PaoIdentifier> paos,
                                                                                        PointSelector pointSelector,
                                                                                        PointValueSelector selector) {
            return null;   
        }

        @Override
        public Map<PaoIdentifier, PointInfo> getPointInfo(PointSelector pointSelector,
                                                          Set<PaoIdentifier> paos) {
            return pointDao.getPointInfoByPointName(paos, pointSelector.getName());
        }
    }

    private class PointTypeOffsetHistorySelector implements HistorySelector {
        @Override
        public ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointValueMap(Set<PaoIdentifier> paos,
                                                                                     PointSelector pointSelector,
                                                                                     PointValueSelector selector) {
            PointType type = pointSelector.getPointType();
            int offset = pointSelector.getOffset();
            Date startDate = null;
            if (selector.getStartDate() != null) {
                startDate = selector.getStartDate().toDate();
            }
            Date stopDate = null;
            if (selector.getStopDate() != null) {
                stopDate = selector.getStopDate().toDate();
            }
            if (selector.getNumberOfRows() != null) {
                return rawPointHistoryDao.getLimitedDataByTypeAndOffset(paos, type, offset,
                                                                        startDate, stopDate,
                                                                        selector.getNumberOfRows(),
                                                                        selector.getClusivity(),
                                                                        selector.getOrder());
            } else {
                return rawPointHistoryDao.getDataByTypeAndOffset(paos, type, offset, startDate,
                                                                 stopDate, selector.getClusivity(),
                                                                 selector.getOrder());
            }
        }

        @Override
        public ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointSnapshotMap(Set<PaoIdentifier> paos,
                                                                                        PointSelector pointSelector,
                                                                                        PointValueSelector selector) {
            return null;   
        }

        @Override
        public Map<PaoIdentifier, PointInfo> getPointInfo(PointSelector pointSelector,
                                                          Set<PaoIdentifier> paos) {
            return pointDao.getPointInfoByPointIdentifier(paos, pointSelector.getPointIdentifier());
        }
    }

    private class DefaultPointNameHistorySelector implements HistorySelector {
        @Override
        public ListMultimap<PaoIdentifier, PointValueQualityHolder> getPointValueMap(Set<PaoIdentifier> paos,
                                                                                     PointSelector pointSelector,
                                                                                     PointValueSelector selector) {
            String defaultName = pointSelector.getName();
            Date startDate = null;
            if (selector.getStartDate() != null) {
                startDate = selector.getStartDate().toDate();
            }
            Date stopDate = null;
            if (selector.getStopDate() != null) {
                stopDate = selector.getStopDate().toDate();
            }
            if (selector.getNumberOfRows() != null) {
                return rawPointHistoryDao.getLimitedDataByDefaultPointName(paos, defaultName,
                                                                           startDate, stopDate,
                                                                           selector.getNumberOfRows(),
                                                                           selector.getClusivity(),
                                                                           selector.getOrder());
            } else {
                return rawPointHistoryDao.getDataByDefaultPointName(paos, defaultName, startDate,
                                                                    stopDate,
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
        public Map<PaoIdentifier, PointInfo> getPointInfo(PointSelector pointSelector,
                                                          Set<PaoIdentifier> paos) {
            return pointDao.getPointInfoByDefaultName(paos, pointSelector.getName());
        }
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
    public void setPaoSelectionService(PaoSelectionService paoSelectionService) {
        this.paoSelectionService = paoSelectionService;
    }
}
