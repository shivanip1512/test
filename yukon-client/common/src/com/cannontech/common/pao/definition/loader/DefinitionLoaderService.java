package com.cannontech.common.pao.definition.loader;

import java.util.Map;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.loader.jaxb.DeviceCategories.Category;
import com.cannontech.common.pao.definition.loader.jaxb.Point;
import com.cannontech.common.pao.definition.model.CommandDefinition;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PaoTagDefinition;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.SetMultimap;

public interface DefinitionLoaderService {
    

    /**
     * Reloads all pao definitions. This method should be used for testing only since all pao definitions are
     * loaded on
     * the start-up.
     */
    void load();

    /**
     * Applies custom pao definitions.
     */
    void override();

    Map<PaoType, Map<Attribute, AttributeDefinition>> getPaoAttributeAttrDefinitionMap();

    /**
     * Returns a map of PaoType to points
     * @param initOnly - if true, returns only points where <init>true</init>
     */
    SetMultimap<PaoType, PointTemplate> getPointTemplateMap(boolean initOnly);

    SetMultimap<PaoType, Category> getPaoCategoryMap();

    BiMap<PaoType, PaoDefinition> getPaoTypeMap();

    SetMultimap<PaoType, CommandDefinition> getPaoCommandMap();

    Map<PaoType, ImmutableBiMap<PaoTag, PaoTagDefinition>> getSupportedTagsByType();

    /**
     * Get all the point defined for system device(i.e in SYSTEM.xml file).
     */
    Map<String, Point> getSystemDevicePoints();

    /**
     * Clears loaded jaxb objects from memory
     */
    void cleanUp();
}
