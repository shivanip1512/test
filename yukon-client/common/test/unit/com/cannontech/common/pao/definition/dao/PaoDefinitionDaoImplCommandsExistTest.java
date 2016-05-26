package com.cannontech.common.pao.definition.dao;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.UrlResource;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.config.dao.DeviceDefinitionDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.model.CommandDefinition;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

/**
 * PaoDefinitionDaoImplCommandsExistTest class provides all paos which do not have commands for all their
 * readable attributes
 *
 */
public class PaoDefinitionDaoImplCommandsExistTest {
    public static Set<PaoType> exclusivePaoTypes;
    private PaoDefinitionDao paoDefinitionDao = null;

    /**
     * Test which validates all paos having the readable attributes that do not have
     * command identified for them
     * 
     * @throws Exception
     */
    @Test
    public void testValidateCommandExistsForEveryReadableAttributes() throws Exception {
        paoDefinitionDao =
            PaoDefinitionDaoImplCommandsExistTest.getTestPaoDefinitionDao(new MockEmptyDeviceDefinitionDao());
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

    public static PaoDefinitionDao getTestPaoDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) throws Exception {
        PaoDefinitionDaoImpl dao = new PaoDefinitionDaoImpl();
        ReflectionTestUtils.setField(dao, "deviceDefinitionDao", deviceDefinitionDao);

        // Use paoDefinition.xml for testing
        ClassLoader classLoader = dao.getClass().getClassLoader();
        URL inputResource = classLoader.getResource("com/cannontech/common/pao/definition/dao/paoDefinition.xml");
        dao.setInputFile(new UrlResource(inputResource));

        URL schemaResource = classLoader.getResource("com/cannontech/common/pao/definition/dao/paoDefinition.xsd");
        dao.setSchemaFile(new UrlResource(schemaResource));

        ReflectionTestUtils.setField(dao, "stateGroupDao", new MockStateGroupDao());
        ReflectionTestUtils.setField(dao, "unitMeasureDao", new MockUnitMeasureDao());
        dao.initialize();

        return dao;
    }

    private static class MockStateGroupDao implements StateGroupDao {

        @Override
        public LiteState findLiteState(int stateGroupID, int rawState) {
            return null;
        }

        @Override
        public LiteStateGroup getStateGroup(int stateGroupID) {
            return null;
        }

        @Override
        public LiteStateGroup getStateGroup(String stateGroupName) {
            // Basically allow any stateGroupName to pass. We aren't really using this for the command for
            // attribute check.
            LiteStateGroup liteStateGroup = new LiteStateGroup(0, stateGroupName);
            // we need this state to pass the intialState needs of paoDefinition.xml
            liteStateGroup.getStatesList().add(new LiteState(1, "Decommissioned", 7, 6, 0));
            return liteStateGroup;
        }

        @Override
        public List<LiteState> getLiteStates(int stateGroupID) {
            return null;
        }

        @Override
        public List<LiteStateGroup> getAllStateGroups() {
            return null;
        }

    }

    private static class MockUnitMeasureDao implements UnitMeasureDao {
        @Override
        public List<LiteUnitMeasure> getLiteUnitMeasures() {
            return null;
        }

        @Override
        public LiteUnitMeasure getLiteUnitMeasureByPointID(int pointID) {
            return null;
        }

        @Override
        public LiteUnitMeasure getLiteUnitMeasure(int uomid) {
            return null;
        }

        @Override
        public LiteUnitMeasure getLiteUnitMeasure(String uomName) {

            if (uomName.equals("measure0")) {
                return new LiteUnitMeasure(0, uomName, 0, uomName);
            } else if (uomName.equals("measure1")) {
                return new LiteUnitMeasure(1, uomName, 0, uomName);
            } else { // see if we can load from enum
                try {
                    UnitOfMeasure unitOfMeasure = UnitOfMeasure.getForLongName(uomName);
                    return new LiteUnitMeasure(unitOfMeasure.getId(), unitOfMeasure.getAbbreviation(), 0,
                        unitOfMeasure.getLongName());
                } catch (NotFoundException e) {
                    throw new IllegalArgumentException("Unit of measure doesn't exist: " + uomName);
                }
            }
        }

        @Override
        public Table<Integer, PointIdentifier, LiteUnitMeasure> getLiteUnitMeasureByPaoIdAndPoint(
                List<? extends YukonPao> paos) {
            return null;
        }

    }

    public static class MockEmptyDeviceDefinitionDao implements DeviceDefinitionDao {
        @Override
        public long getCustomFileSize() {
            return 0;
        }

        @Override
        public InputStream findCustomDeviceDefinitions() {
            return null;
        }
    }

    /**
     * Method populates set of exclusive paoTypes that do not support commands to read attribute data
     */
    @Before
    public void setExlusivePaoList() {
        exclusivePaoTypes = Sets.newHashSet(PaoType.MCT440_2131B, PaoType.MCT440_2132B, PaoType.MCT440_2133B);
        exclusivePaoTypes.addAll(PaoType.getRfMeterTypes());
        exclusivePaoTypes.addAll(PaoType.getRfLcrTypes());
        exclusivePaoTypes.addAll(PaoType.getRfGatewayTypes());
    }
}