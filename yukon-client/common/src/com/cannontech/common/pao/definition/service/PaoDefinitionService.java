package com.cannontech.common.pao.definition.service;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.database.data.point.PointBase;
import com.google.common.collect.ListMultimap;

/**
 * Class which provides functionality for manipulating paos based on their
 * definition
 */
public interface PaoDefinitionService {

	public static class PointTemplateTransferPair {
        public PointIdentifier oldDefinitionTemplate;
        public PointTemplate newDefinitionTemplate;
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((newDefinitionTemplate == null) ? 0
                    : newDefinitionTemplate.hashCode());
            result = prime * result + ((oldDefinitionTemplate == null) ? 0
                    : oldDefinitionTemplate.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            PointTemplateTransferPair other = (PointTemplateTransferPair) obj;
            if (newDefinitionTemplate == null) {
                if (other.newDefinitionTemplate != null)
                    return false;
            } else if (!newDefinitionTemplate.equals(other.newDefinitionTemplate))
                return false;
            if (oldDefinitionTemplate == null) {
                if (other.oldDefinitionTemplate != null)
                    return false;
            } else if (!oldDefinitionTemplate.equals(other.oldDefinitionTemplate))
                return false;
            return true;
        }
        @Override
        public String toString() {
            ToStringBuilder b = new ToStringBuilder(this);
            b.append("oldDefinitionTemplate", oldDefinitionTemplate);
            b.append("newDefinitionTemplate", newDefinitionTemplate);
            return b.toString();
        }
        

    }

    /**
     * Method to create all of the default points for the given pao. NOTE:
     * this will create the points in memory ONLY - the default points will NOT
     * be persisted
     * @param pao - Pao to create points for
     * @return A list of the default points for the pao (returns a new copy
     *         each time the method is called)
     */
    public List<PointBase> createDefaultPointsForPao(YukonPao pao);

    /**
     * Method to create all of the points for the given pao. NOTE: this will
     * create the points in memory ONLY - the points will NOT be persisted
     * @param pao - Pao to create points for
     * @return A list of all the points for the pao (returns a new copy each
     *         time the method is called)
     */
    public List<PointBase> createAllPointsForPao(YukonPao pao);

    /**
     * Method to get a map of pao display groups and their associated pao
     * types
     * @return An immutable map with key: display group name, value: list of
     *         pao display
     */
    public ListMultimap<String, PaoDefinition> getPaoDisplayGroupMap();
    
    /**
     * Method to get a map of pao display groups and their associated creatable pao
     * types
     * @return An immutable map with key: display group name, value: list of creatable
     *         pao display
     */
    public ListMultimap<String, PaoDefinition> getCreatablePaoDisplayGroupMap();

    /**
     * Method used to determine if a pao can have its type changed
     * @param pao - Pao to change
     * @return True if the pao's type can be changed
     */
    public boolean isPaoTypeChangeable(YukonPao pao);

    /**
     * Method used to determine if a pao with passed in PaoType can be changed
     * @param paoType - PaoType to change
     * @return True if the PaoType can be changed
     */
    public boolean isPaoTypeChangeable(PaoType paoType);

    /**
     * Method to get a set of pao definitions for paos that the given
     * pao can be changed into
     * @param pao - Pao to change
     * @return A set of pao definitions that the given pao can change into
     *         (returns a new copy each time the method is called)
     */
    public Set<PaoDefinition> getChangeablePaos(YukonPao pao);
    
    /**
     * Method to get a set of pao definitions that the given
     * paoType can be changed into
     * @param paoType - PaoType to change
     * @return A set of pao definitions that the given paoType can change into
     *         (returns a new copy each time the method is called)
     */
    public Set<PaoDefinition> getChangeablePaos(PaoType paoType);
    
    /**
     * Method to get a set of point templates that will be added to the given
     * pao if its type is changed to the given pao definition
     * @param pao - Pao to change type
     * @param paoDefinition - Definition of type to change to
     * @return Set of points that will be added to the pao (returns a new
     *         copy each time the method is called)
     */
    public Set<PointTemplate> getPointTemplatesToAdd(YukonPao pao,
            PaoDefinition paoDefinition);

    /**
     * Method to get a set of points that will be removed from the given pao
     * if its type is changed to the given pao definition
     * @param pao - Pao to change type
     * @param paoDefinition - Definition of type to change to
     * @return Set of points that will be removed from the pao (returns a new
     *         copy each time the method is called)
     */
    public Set<PointIdentifier> getPointTemplatesToRemove(YukonPao pao,
            PaoDefinition paoDefinition);
    
    /**
     * Method to get a set of points that will be transfered from the given
     * pao type to the new pao type if its type is changed to the given
     * pao definition
     * @param pao - Pao to change type
     * @param paoDefinition - Definition of type to change to
     * @return Set of point templates that will be transfered from the pao
     *         (returns a new copy each time the method is called)
     */
    public Set<PointTemplateTransferPair> getPointTemplatesToTransfer(YukonPao pao,
            PaoDefinition paoDefinition);
}