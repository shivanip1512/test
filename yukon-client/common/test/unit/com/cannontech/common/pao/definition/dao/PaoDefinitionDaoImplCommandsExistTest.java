package com.cannontech.common.pao.definition.dao;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.model.CommandDefinition;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class PaoDefinitionDaoImplCommandsExistTest {

    public static Set<PaoType> exclusivePaoTypes;
    private PaoDefinitionDao paoDefinitionDao;

    @BeforeEach
    public void setup() {
        paoDefinitionDao = PaoDefinitionDaoImplTest.getTestPaoDefinitionDao();

    }

    /**
     * Test which look for all paos having the readable attributes that do not have command identified for
     * them.
     * 
     * @throws Exception
     */
    @Test
    public void testValidateCommandExistsForEveryReadableAttributes() throws Exception {
        if (paoDefinitionDao != null) {
            Set<PaoDefinition> paoDefinitions = paoDefinitionDao.getAllPaoDefinitions();
            for (PaoDefinition paoDefinition : paoDefinitions) {
                PaoType paoType = paoDefinition.getType();

                // exclude paoTypes that do not support commands to read attribute data
                if (!exclusivePaoTypes.contains(paoType)) {

                    // exclude paos that do not support command requests
                    if (paoDefinitionDao.isTagSupported(paoType, PaoTag.PORTER_COMMAND_REQUESTS)) {

                        Set<AttributeDefinition> attributeDefinitions = paoDefinitionDao.getDefinedAttributes(paoType);
                        for (AttributeDefinition attributeDefinition : attributeDefinitions) {
                            BuiltInAttribute attribute = attributeDefinition.getAttribute();

                            // include only those attributes that are on-demand readable
                            if (BuiltInAttribute.getReadableAttributes().contains(attribute)) {
                                Set<CommandDefinition> allPossibleCommands =
                                    paoDefinitionDao.getCommandsThatAffectPoints(paoType,
                                        ImmutableSet.of(attributeDefinition.getPointTemplate().getPointIdentifier()));

                                // Display list of all those that have missing commands for readable
                                // attributes
                                if (allPossibleCommands.isEmpty()) {
                                    System.out.println(paoType + " == " + attribute + "; "
                                        + attributeDefinition.getPointTemplate());

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Method populates set of exclusive paoTypes that do not support commands to read attribute data
     */
    @BeforeEach
    public void setExlusivePaoList() {
        exclusivePaoTypes = Sets.newHashSet(PaoType.MCT440_2131B, PaoType.MCT440_2132B, PaoType.MCT440_2133B);
        exclusivePaoTypes.addAll(PaoType.getRfMeterTypes());
        exclusivePaoTypes.addAll(PaoType.getRfLcrTypes());
        exclusivePaoTypes.addAll(PaoType.getRfGatewayTypes());
    }

}
