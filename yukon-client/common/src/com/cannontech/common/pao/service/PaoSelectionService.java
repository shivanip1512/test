package com.cannontech.common.pao.service;

import java.util.Map;
import java.util.Set;

import org.jdom.Element;
import org.w3c.dom.Node;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.definition.model.PaoData;
import com.cannontech.common.pao.definition.model.PaoData.OptionalField;
import com.google.common.collect.ImmutableSet;


public interface PaoSelectionService {
    public enum PaoSelectorType {
        ADDRESS,
        DEVICEGROUP,
        METERNUMBER,
        PAONAME,
        PAOID;
    }

    public class PaoSelector {
        private PaoSelectorType type;
        private Element element;

        public PaoSelector(PaoSelectorType type, Element element) {
            this.element = element;
            this.type = type;
        }

        public PaoSelectorType getType() {
            return type;
        }

        public Element getElement() {
            return element;
        }
    }

    /**
     * Return a list of PaoIdentifiers selected by the children of the given paoListNode.
     * See the complex type "PaoCollection" in ArchivedValuesRequest.xsd for a description of
     * what is required for the paoListNode Node.
     */
    public Set<PaoIdentifier> selectPaoIdentifiers(Node paoListNode);

    /**
     * This method works like {@link #selectPaoIdentifiers(Node)} but will also return the
     * requested data, reducing the need to reread data from the database we already had.
     *  
     * @param paoListNode Node representing
     * @param responseFields List of optional fields to populate.
     * @return Map of PoaIdentifier -> PaoData with PaoData filled in with all fields listed
     *         in responseFields.
     */
    public Map<PaoIdentifier, PaoData> selectPaoIdentifiersAndGetData(Node paoListNode,
                                                                      ImmutableSet<OptionalField> responseFields);
}
