package com.cannontech.web.dev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.rfn.service.pointmapping.ModifiersMatcher;
import com.cannontech.amr.rfn.service.pointmapping.PointMapper;
import com.cannontech.amr.rfn.service.pointmapping.UnitOfMeasureToPointMapper;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.web.security.annotation.CheckCparm;
import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

@Controller
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class PointMappingDecipherController {

    @Autowired private UnitOfMeasureToPointMapper unitOfMeasureToPointMapper;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    @RequestMapping("/rfn/decipher/view")
    public String view(ModelMap model) {

        model.addAttribute("dataToMatch", new DataToMatch());
        return "rfn/pointMappingDecipher.jsp";
    }

    @RequestMapping("/rfn/decipher/find")
    public String find(@ModelAttribute DataToMatch dataToMatch, ModelMap model) {

        if (StringUtils.isEmpty(dataToMatch.getUom())) {
            return "rfn/pointMappingDecipher.jsp";
        }
        // split by , and remove empty spaces
        Set<String> unitOfMeasureModifiers = Sets.newHashSet(dataToMatch.getModifiers().split("\\s*,\\s*"));

        Iterator<String> it = unitOfMeasureModifiers.iterator();
        while (it.hasNext()) {
            String modifier = it.next().toLowerCase();
            if (modifier.toLowerCase().contains("coincident")) {
                it.remove();
            }
        }
        Set<String> baseUnitOfMeasureModifiers = Sets.newHashSet(dataToMatch.getBaseModifiers().split("\\s*,\\s*"));;
        Set<MatchedMapping> mappings = getDecipherMappings(dataToMatch.getUom(), unitOfMeasureModifiers,
            dataToMatch.getBaseUom(), baseUnitOfMeasureModifiers);
        List<MatchedMapping> sortedMappings = new ArrayList<MatchedMapping>(mappings);
        Collections.sort(sortedMappings);
        model.addAttribute("mappings", sortedMappings);

        return "rfn/pointMappingDecipher.jsp";
    }

    /**
     * Matches user-entered data to rfnPointMapping.xml and paoDefinition.xml and returns the result.
     */
    private Set<MatchedMapping> getDecipherMappings(String unitOfMeasure, Set<String> unitOfMeasureModifiers,
            String baseUnitOfMeasure, Set<String> baseUnitOfMeasureModifiers) {

        Set<MatchedMapping> mappings = new HashSet<>();
        Multimap<PaoType, PointMapper> pointMapperMap = unitOfMeasureToPointMapper.getPointMapper();

        for (PaoType paoType : PaoType.getRfMeterTypes()) {
            Set<PointTemplate> templates = paoDefinitionDao.getAllPointTemplates(paoType);
            for (PointMapper pointMapper : pointMapperMap.get(paoType)) {
                if (StringUtils.isEmpty(baseUnitOfMeasure) && StringUtils.isEmpty(pointMapper.getBaseUom())) {
                    if (StringUtils.equalsIgnoreCase(unitOfMeasure, pointMapper.getUom())
                        && checkModifiersForMatch(unitOfMeasureModifiers, pointMapper.getModifiersMatchers())) {
                        MatchedMapping mapping = getMatchedMapping(paoType, templates, pointMapper.getName());
                        mappings.add(mapping);
                        break;
                    }
                }
                if (!StringUtils.isEmpty(baseUnitOfMeasure) && !StringUtils.isEmpty(pointMapper.getBaseUom())) {
                    if (StringUtils.equalsIgnoreCase(unitOfMeasure, pointMapper.getUom())
                        && checkModifiersForMatch(unitOfMeasureModifiers, pointMapper.getModifiersMatchers())
                        && StringUtils.equalsIgnoreCase(baseUnitOfMeasure, pointMapper.getBaseUom())
                        && checkModifiersForMatch(baseUnitOfMeasureModifiers, pointMapper.getBaseModifiersMatchers())) {
                        MatchedMapping mapping = getMatchedMapping(paoType, templates, pointMapper.getName());
                        mappings.add(mapping);
                        break;
                    }
                }
            }
        }
        return mappings;
    }

    /**
     * Attempts to find a point in paoDefinition.xml by point name.
     * If the point was only found in rfnPointMapping.xml, the point name and Pao Type will be returned, if the point
     * was found in paoDefinition.xml point type, offset, multiplier, attributes will be returned.
     */
    private MatchedMapping getMatchedMapping(PaoType paoType, Set<PointTemplate> pointTemplates, String pointName) {
        MatchedMapping mapping = new MatchedMapping(paoType, pointName);
        for (PointTemplate template : pointTemplates) {
            if (template.getName().equals(pointName)) {
                mapping.template = template;
                PaoTypePointIdentifier paoTypePointIdentifier =
                    PaoTypePointIdentifier.of(paoType, template.getPointIdentifier());
                Set<BuiltInAttribute> attributes =
                    paoDefinitionDao.findAttributeForPaoTypeAndPoint(paoTypePointIdentifier);
                mapping.attributes.addAll(attributes);
                break;
            }
        }
        return mapping;
    }

    public static class DataToMatch {
        private String uom;
        private String modifiers;
        private String baseUom;
        private String baseModifiers;

        public String getUom() {
            return uom;
        }

        public void setUom(String uom) {
            this.uom = uom;
        }

        public String getModifiers() {
            return modifiers;
        }

        public void setModifiers(String modifiers) {
            this.modifiers = modifiers;
        }

        public String getBaseUom() {
            return baseUom;
        }

        public void setBaseUom(String baseUom) {
            this.baseUom = baseUom;
        }

        public String getBaseModifiers() {
            return baseModifiers;
        }

        public void setBaseModifiers(String baseModifiers) {
            this.baseModifiers = baseModifiers;
        }
    }

    public static class MatchedMapping implements Comparable<MatchedMapping>{

        private PaoType paoType;
        private String pointName;
        private PointTemplate template;
        private Set<BuiltInAttribute> attributes = new TreeSet<>();

        public MatchedMapping(PaoType paoType, String pointName) {
            super();
            this.paoType = paoType;
            this.pointName = pointName;
        }

        public PaoType getPaoType() {
            return paoType;
        }

        public String getPointName() {
            return pointName;
        }

        public PointTemplate getTemplate() {
            return template;
        }

        public String getAttributes() {
            return Joiner.on(",").join(attributes);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((paoType == null) ? 0 : paoType.hashCode());
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
            MatchedMapping other = (MatchedMapping) obj;
            if (paoType != other.paoType)
                return false;
            return true;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (template != null) {
                sb.append("[" + paoType + "]");
                sb.append("[name=" + pointName);
                sb.append(" type=" + template.getPointType());
                sb.append(" offset=" + template.getOffset());
                sb.append(" multiplier=" + template.getMultiplier() + "]");
                if (!attributes.isEmpty()) {
                    sb.append(attributes);
                }
            } else {
                sb.append("[" + paoType + "]");
                sb.append("[name=" + pointName + "]");
            }

            return sb.toString();
        }

        @Override
        public int compareTo(MatchedMapping o) {
            return this.paoType.compareTo(o.getPaoType());
        }
    }

    /**
     * Checks modifiers for a match.
     */
    private boolean checkModifiersForMatch(Set<String> modifiers, Iterable<ModifiersMatcher> matchers) {
        for (ModifiersMatcher matcher : matchers) {
            Set<String> matcherModifiers = new HashSet<String>(matcher.getModifiers());
            if (modifiers.size() == matcherModifiers.size()) {
                matcherModifiers.removeAll(modifiers);
                if (matcherModifiers.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }
}
