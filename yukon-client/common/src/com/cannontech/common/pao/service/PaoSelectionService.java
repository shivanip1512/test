package com.cannontech.common.pao.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom2.Element;
import org.w3c.dom.Node;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PaoData;
import com.cannontech.common.pao.definition.model.PaoData.OptionalField;
import com.google.common.collect.ImmutableSet;

public interface PaoSelectionService {
    public enum PaoSelectorType {
        ADDRESS("carrierAddress", OptionalField.CARRIER_ADDRESS),
        DEVICE_GROUP("deviceGroup", null),
        METER_NUMBER("meterNumber", OptionalField.METER_NUMBER),
        PAO_NAME("paoName", null),
        PAO_ID("paoId", null),
        ;

        private final String elementName;
        private final Set<OptionalField> matchingOptionalFieldSet;

        private PaoSelectorType(String elementName, OptionalField matchingField) {
            this.elementName = elementName;
            if (matchingField == null) {
                matchingOptionalFieldSet = ImmutableSet.of();
            } else {
                matchingOptionalFieldSet = ImmutableSet.of(matchingField);
            }
        }

        public String getElementName() {
            return elementName;
        }

        public Set<OptionalField> getMatchingOptionalFieldSet() {
            return matchingOptionalFieldSet;
        }
    }

    public class PaoSelectionData {
        private final Map<PaoIdentifier, PaoData> paoDataById;
        private final Map<PaoSelectorType, List<String>> lookupFailures;
        private final int numLookupFailures;

        public PaoSelectionData(Map<PaoIdentifier, PaoData> paoDataById,
                                Map<PaoSelectorType, List<String>> lookupFailures,
                                int numLookupFailures) {
            this.paoDataById = paoDataById;
            this.lookupFailures = lookupFailures;
            this.numLookupFailures = numLookupFailures;
        }

        public Map<PaoIdentifier, PaoData> getPaoDataById() {
            return paoDataById;
        }

        public Map<PaoSelectorType, List<String>> getLookupFailures() {
            return lookupFailures;
        }

        public int getNumLookupFailures() {
            return numLookupFailures;
        }
    }

    /**
     * Find PAOs matching children of the given paoCollectionNode and data with fields listed
     * in responseFields populated.
     * 
     * See the complex type "PaoCollection" in PaoTypes.xsd for a description of what is required
     * for the paoCollectionNode Node.
     *  
     * @param responseFields List of optional fields to populate.  If you don't want any data,
     * use {@link #selectPaoIdentifiers(Node) instead.  Use null to populate PaoData with
     * only values used in the request.
     * @return Map of PaoIdentifier -> PaoData with PaoData filled in with all fields listed
     *         in responseFields.
     */
    public Map<PaoIdentifier, PaoData> selectPaoIdentifiersAndGetData(Node paoCollectionNode,
                                                                      Set<OptionalField> responseFields);

    /**
     * Find all PAOs defined in the given pao collection node. The PaoData instances in this
     * method will be populated only with required fields and the selector fields. For example,
     * all devices selected by meter number will include meter number but no other required fields.
     */
    public PaoSelectionData selectPaoIdentifiersByType(Node paoCollectionNode);

    /**
     * Look up the requested data for the given PAOs.
     */
    public <T extends YukonPao> Map<T, PaoData> lookupPaoData(Iterable<T> paos, Set<OptionalField> requestedFields);

    /**
     * If there are any lookup errors, add a "lookupError" node as a child of the given parent.
     * This method does nothing if there are no lookup errors.
     */
    public void addLookupErrorsNode(PaoSelectionData paoData, Element parent);
}
