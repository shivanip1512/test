package com.cannontech.common.pao.definition.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.pao.service.PointCreationService;
import com.cannontech.common.util.MapUtil;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;
import com.google.common.base.Predicate;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;

/**
 * Implementation class for PaoDefinitionService
 */
public class PaoDefinitionServiceImpl implements PaoDefinitionService {

    private PaoDefinitionDao paoDefinitionDao = null;
    private PointCreationService pointCreationService;
    private PointDao pointDao = null;

    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }

    @Autowired
    public void setPointCreationService(PointCreationService pointCreationService) {
		this.pointCreationService = pointCreationService;
	}
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
		this.pointDao = pointDao;
	}
    
    public List<PointBase> createDefaultPointsForPao(YukonPao pao) {

        List<PointBase> pointList = new ArrayList<PointBase>();
        Set<PointTemplate> pointTemplates = paoDefinitionDao.getInitPointTemplates(pao.getPaoIdentifier().getPaoType());
        for (PointTemplate template : pointTemplates) {
            pointList.add(pointCreationService.createPoint(pao.getPaoIdentifier().getPaoId(), template));
        }

        return pointList;
    }

    public List<PointBase> createAllPointsForPao(YukonPao pao) {

        List<PointBase> pointList = new ArrayList<PointBase>();
        Set<PointTemplate> pointTemplates = paoDefinitionDao.getAllPointTemplates(pao.getPaoIdentifier().getPaoType());
        for (PointTemplate template : pointTemplates) {
            pointList.add(pointCreationService.createPoint(pao.getPaoIdentifier().getPaoId(), template));
        }

        return pointList;
    }

    public ListMultimap<String, PaoDefinition> getPaoDisplayGroupMap() {
        return paoDefinitionDao.getPaoDisplayGroupMap();
    }

    private ListMultimap<String, PaoDefinition> getPaoDisplayGroupMap(Predicate<PaoDefinition> predicate) {
        ListMultimap<String, PaoDefinition> paoDisplayGroupMap = paoDefinitionDao.getPaoDisplayGroupMap();
        return MapUtil.filterLinkedListMultimap(paoDisplayGroupMap, predicate); 
    }

    @Override
    public ListMultimap<String, PaoDefinition> getCreatablePaoDisplayGroupMap() {
        return getPaoDisplayGroupMap(new Predicate<PaoDefinition>() {
            public boolean apply(PaoDefinition input) {
                return input.isCreatable();
            }
        });
    }

    public boolean isPaoTypeChangeable(YukonPao pao) {
        PaoDefinition paoDefinition = paoDefinitionDao.getPaoDefinition(pao.getPaoIdentifier().getPaoType());
        return paoDefinition.isChangeable();
    }

    public Set<PaoDefinition> getChangeablePaos(YukonPao pao) {

        // Make sure this pao can be changed
        if (!this.isPaoTypeChangeable(pao)) {
            return Collections.emptySet();
        }

        PaoDefinition paoDefinition = paoDefinitionDao.getPaoDefinition(pao.getPaoIdentifier().getPaoType());

        // Get all of the paos in the pao's change group
        Set<PaoDefinition> paos = paoDefinitionDao.getPaosThatPaoCanChangeTo(paoDefinition);
        return paos;
    }

    public Set<PointTemplate> getPointTemplatesToAdd(YukonPao pao,
            PaoDefinition newDefinition) {
		this.validateChange(pao, newDefinition);
		// points to add are points that are defined for newDefinition minus points being
		// transfered (i.e. minus new points from getPointTemplatesForTransfer)
		
		// for this method, templates are being compared on the new definition and therefore
		// should be compared by the identifier (although including name won't hurt, because
		// everything is from the same definition)
		
		Set<PointTemplate> existingTemplates = paoDefinitionDao.getInitPointTemplates(newDefinition);
		HashSet<PointTemplate> result = new HashSet<PointTemplate>(existingTemplates);
		
		Iterable<PointTemplateTransferPair> pointTemplatesToTransfer = getPointTemplatesToTransfer(pao, newDefinition);
		for (PointTemplateTransferPair pointTemplateTransferPair : pointTemplatesToTransfer) {
			result.remove(pointTemplateTransferPair.newDefinitionTemplate);
		}
		
		return result;
	}

    public Set<PointIdentifier> getPointTemplatesToRemove(YukonPao pao,
            PaoDefinition newDefinition) {
		this.validateChange(pao, newDefinition);
		// points to remove are points that exist on pao (AND are defined) minus points being
		// transfered (i.e. minus old points from getPointTemplatesForTransfer)
		
		// for this method, templates are being compared on the new definition and therefore
		// should be compared by the identifier (although including name won't hurt, because
		// everything is from the same definition)
		
		HashSet<PointIdentifier> result = new HashSet<PointIdentifier>();
		Set<PointTemplate> existingPointTemplates = getExistingPointTemplates(pao);
		for (PointTemplate pointTemplate : existingPointTemplates) {
			result.add(pointTemplate.getPointIdentifier());
		}
		
		Iterable<PointTemplateTransferPair> pointTemplatesToTransfer = getPointTemplatesToTransfer(pao, newDefinition);
		for (PointTemplateTransferPair pointTemplateTransferPair : pointTemplatesToTransfer) {
			result.remove(pointTemplateTransferPair.oldDefinitionTemplate);
		}
		
		return result;
	}

    public Set<PointTemplateTransferPair> getPointTemplatesToTransfer(YukonPao pao,
            PaoDefinition newDefinition) {

		this.validateChange(pao, newDefinition);
		
		Set<PointTemplate> existingTemplates = this.getExistingPointTemplates(pao);
		Set<PointTemplate> supportedTemplates = paoDefinitionDao.getAllPointTemplates(newDefinition);
		
		// Form pairs of points by comparing names
		Set<PointTemplateTransferPair> result = Sets.newHashSet();
		for (PointTemplate oldTemplate : existingTemplates) {
			for (PointTemplate newTemplate : supportedTemplates) {
				// here's the big check that determines what points are the same
				// note we're comparing the names of the names of the templates
				// so that any changes to the point's name in the DB are ignored
				if (oldTemplate.getName().equals(newTemplate.getName())) {
					PointTemplateTransferPair pair = new PointTemplateTransferPair();
					pair.oldDefinitionTemplate = oldTemplate.getPointIdentifier();
					pair.newDefinitionTemplate = newTemplate;
					result.add(pair);
				}
			}
		}
		
		return result;
	}

    /**
     * Helper method to determine if the pao can be changed into the new
     * definition type
     * @param pao - Pao to change
     * @param newDefinition - Definition of type to change to
     * @return True if the pao can be changed into the given definition type
     */
    private void validateChange(YukonPao pao, PaoDefinition newDefinition) {

        PaoDefinition paoDefinition = paoDefinitionDao.getPaoDefinition(pao.getPaoIdentifier().getPaoType());

        if (paoDefinition.getChangeGroup() == null
                || !paoDefinition.getChangeGroup().equals(newDefinition.getChangeGroup())) {

            throw new IllegalArgumentException(pao + " cannot be changed into a "
                    + newDefinition.getDisplayName());
        }

    }

    /**
     * Helper method to get the list of point templates that correspond to
     * litePoints that exist for the given pao
     * @param pao - Pao to get pointTemplates for
     * @return A set of existing point templates (returns a new copy each time
     *         the method is called)
     */
    private Set<PointTemplate> getExistingPointTemplates(YukonPao pao) {

        Set<PointTemplate> templates = new HashSet<PointTemplate>();
    	List<LitePoint> existingPoints = pointDao.getLitePointsByPaObjectId(pao.getPaoIdentifier().getPaoIdentifier().getPaoId());
    	Set<PointTemplate> existingTemplates = paoDefinitionDao.getAllPointTemplates(pao.getPaoIdentifier().getPaoType());
    	
    	for (LitePoint litePoint : existingPoints) {
    	    PointIdentifier pointIdentifier = PointIdentifier.createPointIdentifier(litePoint);
			for (PointTemplate template : existingTemplates) {
				if (pointIdentifier.equals(template.getPointIdentifier()))
					templates.add(template);
			}
		}
        return templates;
    }
}