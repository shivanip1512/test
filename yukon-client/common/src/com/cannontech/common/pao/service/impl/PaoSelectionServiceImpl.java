package com.cannontech.common.pao.service.impl;

import static com.cannontech.common.pao.service.PaoSelectionService.PaoSelectorType.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Node;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.dao.PaoSelectionDao;
import com.cannontech.common.pao.definition.model.PaoData;
import com.cannontech.common.pao.definition.model.PaoData.OptionalField;
import com.cannontech.common.pao.service.PaoSelectionService;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.PaoDao;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PaoSelectionServiceImpl implements PaoSelectionService {
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoSelectionDao paoSelectionDao;

    private Namespace ns = YukonXml.getYukonNamespace();
    private final ImmutableMap<PaoSelectorType, PaoSelector> paoSelectors;

    public PaoSelectionServiceImpl() {
        Builder<PaoSelectorType, PaoSelector> builder = ImmutableMap.builder();

        builder.put(DEVICE_GROUP, new ByDeviceGroupNamesSelector());
        builder.put(METER_NUMBER, new ByMeterNumbersSelector());
        builder.put(ADDRESS, new ByCarrierAddressSelector());
        builder.put(PAO_NAME, new ByPaoNamesSelector());
        builder.put(PAO_ID, new ByPaoIdSelector());

        paoSelectors = builder.build();
    }

    @Override
    public Map<PaoIdentifier, PaoData> selectPaoIdentifiersAndGetData(Node paoCollectionNode,
                                                                      Set<OptionalField> responseFields) {
        return selectAndGetData(paoCollectionNode, responseFields, null);
    }

    @Override
    public PaoSelectionData selectPaoIdentifiersByType(Node paoCollectionNode) {
        Map<PaoSelectorType, List<String>> lookupFailures = Maps.newHashMap();
        Map<PaoIdentifier, PaoData> dataById = selectAndGetData(paoCollectionNode, null,
                                                                lookupFailures);

        int numLookupFailures = 0;
        for (List<String> failures : lookupFailures.values()) {
            numLookupFailures += failures.size();
        }
        PaoSelectionData retVal = new PaoSelectionData(dataById, lookupFailures, numLookupFailures);
        return retVal;
    }

    private Map<PaoIdentifier, PaoData> selectAndGetData(Node paoCollectionNode,
                                                         Set<OptionalField> responseFieldsIn,
                                                         Map<PaoSelectorType, List<String>> lookupFailures) {
        SimpleXPathTemplate paoListTemplate = YukonXml.getXPathTemplateForNode(paoCollectionNode);

        Map<PaoIdentifier, PaoData> retVal = Maps.newHashMap();

        for (PaoSelectorType type : paoSelectors.keySet()) {
            List<Node> nodeList = paoListTemplate.evaluateAsNodeList(ns.getPrefix() + ":"
                    + type.getElementName());
            Set<OptionalField> responseFields = responseFieldsIn == null
                    ? type.getMatchingOptionalFieldSet() : responseFieldsIn;
            List<String> lookupFailuresForType = null;
            if (lookupFailures != null) {
                lookupFailuresForType = Lists.newArrayList();
                lookupFailures.put(type, lookupFailuresForType);
            }
            paoSelectors.get(type).selectPaos(nodeList, responseFields, retVal,
                                              lookupFailuresForType);
        }

        return retVal;
    }

    @Override
    public <T extends YukonPao> Map<T, PaoData> lookupPaoData(Iterable<T> paos, Set<OptionalField> requestedFields) {
        Map<T, PaoData> paoDataByPao = new HashMap<>();
        for (T pao : paos) {
            PaoData paoData = new PaoData(requestedFields, pao.getPaoIdentifier());
            paoDataByPao.put(pao, paoData);
        }
        addNeededData(paoDataByPao.values(), requestedFields, null);
        return paoDataByPao;
    }

    @Override
    public void addLookupErrorsNode(PaoSelectionData paoData, Element parent) {
        if (paoData.getNumLookupFailures() == 0) {
            return;
        }

        Element lookupErrorElem = new Element("lookupError", ns);
        for (Map.Entry<PaoSelectorType, List<String>> entry : paoData.getLookupFailures().entrySet()) {
            PaoSelectorType selectorType = entry.getKey();
            List<String> lookupFailures = entry.getValue();
            if (!lookupFailures.isEmpty()) {
                for (String lookupFailure : lookupFailures) {
                    Element paoElement = new Element(selectorType.getElementName(), ns);
                    paoElement.setAttribute("value", lookupFailure);
                    lookupErrorElem.addContent(paoElement);
                }
            }
        }
        parent.addContent(lookupErrorElem);
    }

    private void addNeededData(Iterable<PaoData> paosNeedingData, Set<OptionalField> responseFields,
                               OptionalField alreadyFulfilled) {
        Set<OptionalField> neededFields = Sets.newEnumSet(responseFields, OptionalField.class);
        if (alreadyFulfilled != null) {
            neededFields.remove(alreadyFulfilled);
        }
        paoSelectionDao.addNeededData(paosNeedingData, neededFields);
    }

    private abstract class PaoSelector {
        /**
         * Look up PAOs and get data.
         * 
         * @param lookupFailures If this is not null, it should be populated with a list of items
         *            for which no PAO was found. Use null to avoid extra work if this is not
         *            needed.
         */
        public abstract void selectPaos(List<Node> nodes, Set<OptionalField> responseFields,
                                        Map<PaoIdentifier, PaoData> into, List<String> lookupFailures);
    }

    private class ByCarrierAddressSelector extends PaoSelector {
        @Override
        public void selectPaos(List<Node> nodes, Set<OptionalField> responseFields,
                               Map<PaoIdentifier, PaoData> into, List<String> lookupFailures) {
            Set<Integer> carrierAddresses = Sets.newHashSet();
            for (Node node : nodes) {
                String carrierAddressStr =
                        YukonXml.getXPathTemplateForNode(node).evaluateAsString("@value");
                try {
                    Integer carrierAddress = new Integer(carrierAddressStr);
                    carrierAddresses.add(carrierAddress);
                } catch (NumberFormatException nfe) {
                    if (lookupFailures != null) {
                        lookupFailures.add(carrierAddressStr);
                    }
                }
            }

            Map<Integer, PaoIdentifier> paoIdsByCarrierAddress =
                paoDao.findPaoIdentifiersByCarrierAddress(carrierAddresses);
            List<PaoData> newPaos = Lists.newArrayListWithCapacity(paoIdsByCarrierAddress.size());
            for (Map.Entry<Integer, PaoIdentifier> entry : paoIdsByCarrierAddress.entrySet()) {
                Integer carrierAddress = entry.getKey();
                carrierAddresses.remove(carrierAddress);
                PaoIdentifier paoIdentifier = entry.getValue();

                PaoData paoData = new PaoData(responseFields, paoIdentifier);
                paoData.setCarrierAddress(carrierAddress);
                into.put(paoIdentifier, paoData);
                newPaos.add(paoData);
            }

            if (lookupFailures != null && !carrierAddresses.isEmpty()) {
                Iterable<String> failedAddressStrs =
                        Iterables.transform(carrierAddresses, Functions.toStringFunction());
                for (String failedLookup : failedAddressStrs) {
                    lookupFailures.add(failedLookup);
                }
            }

            addNeededData(newPaos, responseFields, OptionalField.CARRIER_ADDRESS);
        }
    }

    private class ByDeviceGroupNamesSelector extends PaoSelector {
        @Override
        public void selectPaos(List<Node> nodes, Set<OptionalField> responseFields,
                               Map<PaoIdentifier, PaoData> into, List<String> lookupFailures) {
            Set<String> groupNames = Sets.newHashSet();

            for (Node node : nodes) {
                String groupName = YukonXml.getXPathTemplateForNode(node).evaluateAsString("@value");
                groupNames.add(groupName);
            }

            Set<DeviceGroup> groups = Sets.newHashSet();
            Set<String> myFailures = Sets.newHashSet();
            for (String groupName : groupNames) {
                DeviceGroup group = deviceGroupService.findGroupName(groupName);
                if (group == null) {
                    if (lookupFailures != null) {
                        lookupFailures.add(groupName);
                        myFailures.add(groupName);
                    }
                } else {
                    groups.add(group);
                }
            }
            if (lookupFailures != null) {
                groupNames.removeAll(myFailures);
            }
            Set<SimpleDevice> devices = deviceGroupService.getDevices(groups);

            List<PaoData> newPaos = Lists.newArrayList();
            for (SimpleDevice device : devices) {
                PaoIdentifier paoIdentifier = device.getPaoIdentifier();
                PaoData paoData = new PaoData(responseFields, paoIdentifier);
                into.put(paoIdentifier, paoData);
                newPaos.add(paoData);
            }

            addNeededData(newPaos, responseFields, null);
        }
    }

    private class ByMeterNumbersSelector extends PaoSelector {
        @Override
        public void selectPaos(List<Node> nodes, Set<OptionalField> responseFields,
                               Map<PaoIdentifier, PaoData> into, List<String> lookupFailures) {
            Set<String> meterNumbers = Sets.newHashSet();
            for (Node node : nodes) {
                meterNumbers.add(YukonXml.getXPathTemplateForNode(node).evaluateAsString("@value"));
            }

            List<PaoData> newPaos = Lists.newArrayList();
            Map<String, PaoIdentifier> paoIdsByMeterNumber =
                paoDao.findPaoIdentifiersByMeterNumber(meterNumbers);
            for (Map.Entry<String, PaoIdentifier> entry : paoIdsByMeterNumber.entrySet()) {
                String meterNumber = entry.getKey();
                PaoIdentifier paoIdentifier = entry.getValue();

                if (lookupFailures != null) {
                    meterNumbers.remove(meterNumber);
                }

                PaoData paoData = new PaoData(responseFields, paoIdentifier);
                paoData.setMeterNumber(meterNumber);
                into.put(paoIdentifier, paoData);
                newPaos.add(paoData);
            }

            if (lookupFailures != null) {
                lookupFailures.addAll(meterNumbers);
            }
            addNeededData(newPaos, responseFields, OptionalField.METER_NUMBER);
        }
    }

    private class ByPaoNamesSelector extends PaoSelector {
        @Override
        public void selectPaos(List<Node> nodes, Set<OptionalField> responseFields,
                               Map<PaoIdentifier, PaoData> into, List<String> lookupFailures) {
            Set<String> paoNames = Sets.newHashSet();
            for (Node node : nodes) {
                paoNames.add(YukonXml.getXPathTemplateForNode(node).evaluateAsString("@value"));
            }

            List<PaoData> newPaos = Lists.newArrayList();
            Map<String, PaoIdentifier> paoIdsByName = paoDao.findPaoIdentifiersByName(paoNames);
            for (Map.Entry<String, PaoIdentifier> entry : paoIdsByName.entrySet()) {
                String paoName = entry.getKey();
                PaoIdentifier paoIdentifier = entry.getValue();

                if (lookupFailures != null) {
                    paoNames.remove(paoName);
                }

                PaoData paoData = new PaoData(responseFields, paoIdentifier);
                paoData.setName(paoName);
                into.put(paoIdentifier, paoData);
                newPaos.add(paoData);
            }


            if (lookupFailures != null) {
                lookupFailures.addAll(paoNames);
            }
            addNeededData(newPaos, responseFields, OptionalField.NAME);
        }
    }

    private class ByPaoIdSelector extends PaoSelector {
        @Override
        public void selectPaos(List<Node> nodes, Set<OptionalField> responseFields,
                               Map<PaoIdentifier, PaoData> into, List<String> lookupFailures) {
            Set<Integer> paoIds = Sets.newHashSet();
            for (Node node : nodes) {
                String paoIdStr =
                        YukonXml.getXPathTemplateForNode(node).evaluateAsString("@value");
                try {
                    Integer paoId = new Integer(paoIdStr);
                    paoIds.add(paoId);
                } catch (NumberFormatException nfe) {
                    if (lookupFailures != null) {
                        lookupFailures.add(paoIdStr);
                    }
                }
            }

            List<PaoData> newPaos = Lists.newArrayList();
            List<PaoIdentifier> paoIdentifiers = paoDao.getPaoIdentifiersForPaoIds(paoIds);
            for (PaoIdentifier paoIdentifier : paoIdentifiers) {
                if (lookupFailures != null) {
                    paoIds.remove(paoIdentifier.getPaoId());
                }
                PaoData paoData = new PaoData(responseFields, paoIdentifier);
                into.put(paoIdentifier, paoData);
                newPaos.add(paoData);
            }

            if (lookupFailures != null && !paoIds.isEmpty()) {
                Iterable<String> failedAddressStrs =
                        Iterables.transform(paoIds, Functions.toStringFunction());
                for (String failedLookup : failedAddressStrs) {
                    lookupFailures.add(failedLookup);
                }
            }

            addNeededData(newPaos, responseFields, null);
        }
    }
}
