package com.cannontech.amr.rfn.service.pointmapping;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.util.MatchStyle;
import com.cannontech.common.util.ModifiersMatcher;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Sets.SetView;

@ManagedResource
public class UnitOfMeasureToPointMappingParser implements UnitOfMeasureToPointMapper {
    
    public static class CachedPointValue {
        public PointIdentifier pointIdentifier;
        public double multiplier;
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            long temp;
            temp = Double.doubleToLongBits(multiplier);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            result = prime * result + pointIdentifier.hashCode();
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
            CachedPointValue other = (CachedPointValue) obj;
            if (Double.doubleToLongBits(multiplier) != Double.doubleToLongBits(other.multiplier))
                return false;
            if (!pointIdentifier.equals(other.pointIdentifier))
                return false;
            return true;
        }
    }
    
    public static class NullCachedPointValue extends CachedPointValue {
        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            return true;
        }
    }

    public static class CachedPointKey {

        private final PaoType paoType;
        private final String unitOfMeasure;
        private final Set<String> unitOfMeasureModifiers;

        public CachedPointKey(PaoType paoType, String unitOfMeasure,
                Set<String> unitOfMeasureModifiers) {
                    this.paoType = paoType;
                    this.unitOfMeasure = unitOfMeasure;
                    this.unitOfMeasureModifiers = unitOfMeasureModifiers;
        }

        public PaoType getPaoType() {
            return paoType;
        }

        public String getUnitOfMeasure() {
            return unitOfMeasure;
        }

        public Set<String> getUnitOfMeasureModifiers() {
            return unitOfMeasureModifiers;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + paoType.hashCode();
            result = prime * result + unitOfMeasure.hashCode();
            result = prime * result + unitOfMeasureModifiers.hashCode();
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
            CachedPointKey other = (CachedPointKey) obj;
            if (!paoType.equals(other.paoType))
                return false;
            if (!unitOfMeasure.equals(other.unitOfMeasure))
                return false;
            if (!unitOfMeasureModifiers.equals(other.unitOfMeasureModifiers))
                return false;
            return true;
        }

    }

    private PaoDefinitionDao paoDefinitionDao;
    private static final Logger log = YukonLogManager.getLogger(UnitOfMeasureToPointMappingParser.class);
    private ImmutableMultimap<PaoType, PointMapper> pointMapperMap;
    private ConcurrentMap<CachedPointKey, CachedPointValue> computingCache;
    private final NullCachedPointValue nullCachedPointValue = new NullCachedPointValue();
    private ConfigurationSource configurationSource;
    private ResourceLoader resourceLoader;
    
    @PostConstruct
    public void initialize() throws Exception {
        String pointMappingUrl = 
            configurationSource.getString("RFN_METER_POINT_MAPPING",
                                          "classpath:com/cannontech/amr/rfn/service/pointmapping/rfnPointMapping.xml");
        Resource xmlFile = resourceLoader.getResource(pointMappingUrl);
        pointMapperMap = parse(xmlFile);
        
        computingCache = new MapMaker()
            .concurrencyLevel(6)
            .expiration(5, TimeUnit.MINUTES)
            .makeComputingMap(new Function<CachedPointKey, CachedPointValue>() {

                @Override
                public CachedPointValue apply(CachedPointKey from) {
                    return locatePointValueHandler(from);
                }
        });
    }
    
    public PointValueHandler findMatch(YukonPao pao, String unitOfMeasure, Set<String> unitOfMeasureModifiers) {

        CachedPointKey key = new CachedPointKey(pao.getPaoIdentifier().getPaoType(), unitOfMeasure, unitOfMeasureModifiers);

        CachedPointValue value = computingCache.get(key);

        if (value == nullCachedPointValue) {
            return null;
        }
        PointValueHandler pointValueHandler = buildResponse(pao, value);

        return pointValueHandler;
    }

    private PointValueHandler buildResponse(final YukonPao pao, final CachedPointValue value) {
        final PaoPointIdentifier finalPaoPointIdentifier = new PaoPointIdentifier(pao.getPaoIdentifier(), value.pointIdentifier);
        return new PointValueHandler() {

            @Override
            public PaoPointIdentifier getPaoPointIdentifier() {
                return finalPaoPointIdentifier;
            }

            @Override
            public double convert(double rawValue) {
                return rawValue * value.multiplier;
            }
            
            @Override
            public String toString() {
                return String.format("%s with a %.1e multiplier", finalPaoPointIdentifier, value.multiplier);
            }
        };   
    }

    private CachedPointValue locatePointValueHandler(CachedPointKey cachedPointKey) {
        PaoType paoType = cachedPointKey.getPaoType();
        String unitOfMeasure = cachedPointKey.getUnitOfMeasure();
        Set<String> unitOfMeasureModifiers = cachedPointKey.getUnitOfMeasureModifiers();
        Iterable<PointMapper> pointMappers = pointMapperMap.get(paoType);
        for (PointMapper pointMapper : pointMappers) {
            String uom = pointMapper.getUom();
            if (!uom.equals(unitOfMeasure)) continue;
            
            Set<String> modifiers = Sets.newHashSet(unitOfMeasureModifiers);
            
            double prefixMultiplier = 1;
            if (pointMapper.isSiPrefixParsing()) {
                prefixMultiplier = processAndRemoveSiPrefix(modifiers);
            }
            
            if (!checkModifiersForMatch(modifiers, pointMapper)) {
                continue;
            }
            
            // if we get here, all matchers must have matched, now we can build our answer
            
            PointIdentifier pointIdentifier = null;
            Set<PointTemplate> pointTemplates = paoDefinitionDao.getAllPointTemplates(paoType);
            for (PointTemplate pointTemplate : pointTemplates) {
                if (pointTemplate.getName().equals(pointMapper.getName())) {
                    pointIdentifier = pointTemplate.getPointIdentifier();
                }
            }

            if (pointIdentifier == null) {
                log.debug("no matching point called '" + pointMapper.getName() + "' for " + paoType);
                continue;
            }
            
            CachedPointValue cachedPointValue = new CachedPointValue();
            cachedPointValue.multiplier = prefixMultiplier * pointMapper.getMultiplier();
            cachedPointValue.pointIdentifier = pointIdentifier;
            return cachedPointValue;

        }
        
        LogHelper.debug(log, "unable to find match on %s for %s %s", paoType, unitOfMeasure, unitOfMeasureModifiers);
        return nullCachedPointValue;
    }
    
    public boolean checkModifiersForMatch(Set<String> modifiers, PointMapper pointMapper) {
        Iterable<ModifiersMatcher> modifiersMatchers = pointMapper.getModifiersMatchers();
        for (ModifiersMatcher modifiersMatcher : modifiersMatchers) {
            boolean match = modifiersMatcher.getStyle().matches(modifiers, modifiersMatcher.getModifiers());
            if (!match) return false;
            modifiers.removeAll(modifiersMatcher.getModifiers()); // I think this is a good idea
        }
        return true;
    }
    
    private double processAndRemoveSiPrefix(Set<String> modifiers) {
        SetView<String> intersection = Sets.intersection(modifiers, SiPrefix.getAllspellings().keySet());
        if (intersection.isEmpty()) {
            return 1;
        } else if (intersection.size() > 1) {
            throw new IllegalArgumentException("multiple SI Prefixes encountered");
        } else {
            String modifier = Iterables.getOnlyElement(intersection);
            modifiers.remove(modifier); // don't want this guy to mess up further processing
            SiPrefix siPrefix = SiPrefix.getAllspellings().get(modifier);
            return siPrefix.getFactor();
        }
    }

    public ImmutableMultimap<PaoType, PointMapper> parse(Resource resource) throws Exception {
        
        Builder<PaoType, PointMapper> builder = ImmutableMultimap.builder();
        
        SAXBuilder saxBuilder = new SAXBuilder();
        Document configDoc = saxBuilder.build(resource.getInputStream());
        
        Element rootElement = configDoc.getRootElement();
        Iterable<Element> elementChildren = getElementChildren(rootElement, "pointGroup");
        for (Element rfnDeviceElement : elementChildren) {
            // get pao types
            Set<PaoType> paoTypes = EnumSet.noneOf(PaoType.class);
            Iterable<Element> paoTypeElements = getElementChildren(rfnDeviceElement, "paoType");
            for (Element paoTypeElement : paoTypeElements) {
                String paoTypeStr = paoTypeElement.getAttributeValue("value");
                PaoType paoType = PaoType.valueOf(paoTypeStr);
                paoTypes.add(paoType);
            }
            
            Iterable<Element> pointElements = getElementChildren(rfnDeviceElement, "point");
            for (Element pointElement : pointElements) {
                PointMapper pointMapper = createPointMapper(pointElement);
                for (PaoType paoType : paoTypes) {
                    builder.put(paoType, pointMapper);
                }
            }
        }
        
        return builder.build();
    }
    
    private PointMapper createPointMapper(Element pointElement) {
        String name = pointElement.getAttributeValue("name");
        String uom = pointElement.getChildTextTrim("uom");
        Element multiplierElement = pointElement.getChild("multiplier");
        double multiplier = 1.0;
        if (multiplierElement != null) {
            String multiplierStr = multiplierElement.getAttributeValue("value", "1.0");
            multiplier = Double.parseDouble(multiplierStr);
        }
        boolean siPrefixParsing = false; // if the element doesn't exist, default to false
        Element siPrefixElement = pointElement.getChild("parseSiPrefix");
        if (siPrefixElement != null) {
            // if the element does exist, default to true 
            String valueStr = siPrefixElement.getAttributeValue("value", Boolean.toString(true));
            siPrefixParsing = Boolean.parseBoolean(valueStr);
        }
        Iterable<Element> modifiersElements = getElementChildren(pointElement, "modifiers");
        List<ModifiersMatcher> modifiersMatcherList = Lists.newArrayList();
        for (Element modifiersElement : modifiersElements) {
            String matchStyleStr = modifiersElement.getAttributeValue("match");
            MatchStyle matchStyle = MatchStyle.valueOf(matchStyleStr);
            Iterable<Element> modifierElements = getElementChildren(modifiersElement, "modifier");
            Set<String> modifierSet = Sets.newHashSet();
            for (Element modifierElement : modifierElements) {
                String uomModifier = modifierElement.getTextTrim();
                modifierSet.add(uomModifier);
            }
            ModifiersMatcher modifiersMatcher = new ModifiersMatcher(matchStyle, modifierSet);
            modifiersMatcherList.add(modifiersMatcher);
        }
        
        PointMapper pointMapper = new PointMapper(name, uom, multiplier, siPrefixParsing, modifiersMatcherList);
        return pointMapper;
    }

    public static Iterable<Element> getElementChildren(Element element, String name) {
        return Iterables.filter(element.getChildren(name), Element.class);
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    @ManagedOperation
    public void flushCache() {
        computingCache.clear();
    }
    
    @ManagedAttribute
    public int getCacheSize() {
        return computingCache.size();
    }

}
