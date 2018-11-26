package com.cannontech.amr.rfn.service.pointmapping;

import java.io.InputStream;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.cannontech.amr.rfn.message.read.ChannelData;
import com.cannontech.amr.rfn.message.read.DatedChannelData;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.dao.RfnPointMappingDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.util.MatchStyle;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
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
        
        private final String baseUnitOfMeasure;
        private final Set<String> baseUnitOfMeasureModifiers;

        public CachedPointKey(PaoType paoType, 
                              String unitOfMeasure,
                              Set<String> unitOfMeasureModifiers,
                              String baseUnitOfMeasure,
                              Set<String> baseUnitOfMeasureModifiers) {
                    this.paoType = paoType;
                    this.unitOfMeasure = unitOfMeasure;
                    this.unitOfMeasureModifiers = unitOfMeasureModifiers;
                    this.baseUnitOfMeasure = baseUnitOfMeasure;
                    this.baseUnitOfMeasureModifiers = baseUnitOfMeasureModifiers;
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
        
        public String getBaseUnitOfMeasure() {
            return baseUnitOfMeasure;
        }
        
        public Set<String> getBaseUnitOfMeasureModifiers() {
            return baseUnitOfMeasureModifiers;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result =
                prime * result + ((baseUnitOfMeasure == null) ? 0 : baseUnitOfMeasure.hashCode());
            result =
                prime
                        * result
                        + ((baseUnitOfMeasureModifiers == null) ? 0 : baseUnitOfMeasureModifiers
                            .hashCode());
            result = prime * result + ((paoType == null) ? 0 : paoType.hashCode());
            result = prime * result + ((unitOfMeasure == null) ? 0 : unitOfMeasure.hashCode());
            result =
                prime
                        * result
                        + ((unitOfMeasureModifiers == null) ? 0 : unitOfMeasureModifiers.hashCode());
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
            if (baseUnitOfMeasure == null) {
                if (other.baseUnitOfMeasure != null)
                    return false;
            } else if (!baseUnitOfMeasure.equals(other.baseUnitOfMeasure))
                return false;
            if (baseUnitOfMeasureModifiers == null) {
                if (other.baseUnitOfMeasureModifiers != null)
                    return false;
            } else if (!baseUnitOfMeasureModifiers.equals(other.baseUnitOfMeasureModifiers))
                return false;
            if (paoType != other.paoType)
                return false;
            if (unitOfMeasure == null) {
                if (other.unitOfMeasure != null)
                    return false;
            } else if (!unitOfMeasure.equals(other.unitOfMeasure))
                return false;
            if (unitOfMeasureModifiers == null) {
                if (other.unitOfMeasureModifiers != null)
                    return false;
            } else if (!unitOfMeasureModifiers.equals(other.unitOfMeasureModifiers))
                return false;
            return true;
        }

    }

    private static final Logger log = YukonLogManager.getLogger(UnitOfMeasureToPointMappingParser.class);
    private ImmutableMultimap<PaoType, PointMapper> pointMapperMap;
    private LoadingCache<CachedPointKey, CachedPointValue> computingCache;
    private final NullCachedPointValue nullCachedPointValue = new NullCachedPointValue();
    
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private RfnPointMappingDao rfnPointMappingDao;

    @PostConstruct
    public void initialize() throws Exception {
        try (InputStream xmlFile = rfnPointMappingDao.getPointMappingFile()) {
            pointMapperMap = parse(xmlFile);
        }

        computingCache = CacheBuilder.newBuilder().concurrencyLevel(6).expireAfterWrite(5, TimeUnit.MINUTES)
        .build(new CacheLoader<CachedPointKey, CachedPointValue>() {
                @Override
                public CachedPointValue load(CachedPointKey key) {
                    return locatePointValueHandler(key);
                }
        });
    }
    
    @Override
    public <T extends ChannelData> PointValueHandler findMatch(YukonPao pao, T channelData) {
        String baseUnitOfMeasure = null;
        Set<String> baseUnitOfMeasureModifiers = null;
        
        if (channelData instanceof DatedChannelData) {
            DatedChannelData dated = (DatedChannelData) channelData;
            // 'base' uom and modifiers are for coincidental measurements
            if (dated.getBaseChannelData() != null) {
                baseUnitOfMeasure = dated.getBaseChannelData().getUnitOfMeasure();
                baseUnitOfMeasureModifiers = dated.getBaseChannelData().getUnitOfMeasureModifiers();
            }
        }
        
        CachedPointKey key = new CachedPointKey(pao.getPaoIdentifier().getPaoType(), 
                                                channelData.getUnitOfMeasure(), 
                                                channelData.getUnitOfMeasureModifiers(),
                                                baseUnitOfMeasure, 
                                                baseUnitOfMeasureModifiers);

        // Attempts to get the value, or load it if necessary. getUnchecked is only appropriate
        // since the cache loader is not expected to throw a checked exception, getUnchecked will
        // actually handle checked exceptions and re-throw them as unchecked exceptions.
        CachedPointValue value = computingCache.getUnchecked(key);

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
        String baseUnitOfMeasure = cachedPointKey.getBaseUnitOfMeasure();
        Set<String> unitOfMeasureModifiers = cachedPointKey.getUnitOfMeasureModifiers();
        Set<String> baseUnitOfMeasureModifiers = cachedPointKey.getBaseUnitOfMeasureModifiers();
        // Allowing baseUnitOfMeasureModifiers to come in null allows the xml to not require a <baseModifiers> tag
        if (baseUnitOfMeasureModifiers == null) {
            baseUnitOfMeasureModifiers = Sets.newHashSet();
        }
        Iterable<PointMapper> pointMappers = pointMapperMap.get(paoType);
        
        for (PointMapper pointMapper : pointMappers) {
            String uom = pointMapper.getUom();
            String baseUom = pointMapper.getBaseUom();
            if (!StringUtils.equals(uom, unitOfMeasure)) continue;
            
            Set<String> modifiers = Sets.newHashSet(unitOfMeasureModifiers);
            
            double prefixMultiplier = 1;
            if (pointMapper.isSiPrefixParsing()) {
                prefixMultiplier = processAndRemoveSiPrefix(modifiers);
            }
            
            Iterable<ModifiersMatcher> matchers = pointMapper.getModifiersMatchers();
            if (!checkModifiersForMatch(modifiers, matchers)) {
                continue;
            }
            
            // Check base UOM and base modifiers in case of coincidental measurement points.
            if (baseUnitOfMeasure == null) {
                if (baseUom != null) {
                    continue;
                }
            } else {
                if (!baseUnitOfMeasure.equalsIgnoreCase(baseUom)) {
                    continue;
                }
                Set<String> baseModifiers = Sets.newHashSet(baseUnitOfMeasureModifiers);
                Iterable<ModifiersMatcher> baseMatchers = pointMapper.getBaseModifiersMatchers();
                if (!checkModifiersForMatch(baseModifiers, baseMatchers)) {
                    continue;
                }
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
        
        log.debug(String.format("unable to find match on %s for %s %s", paoType, unitOfMeasure, unitOfMeasureModifiers));
        return nullCachedPointValue;
    }
    
    public boolean checkModifiersForMatch(Set<String> modifiers, Iterable<ModifiersMatcher> matchers) {
        for (ModifiersMatcher modifiersMatcher : matchers) {
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

    private ImmutableMultimap<PaoType, PointMapper> parse(InputStream inputStream) throws Exception {
        Builder<PaoType, PointMapper> builder = ImmutableMultimap.builder();

        SAXBuilder saxBuilder = new SAXBuilder();
        Document configDoc = saxBuilder.build(inputStream);

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
    
    static PointMapper createPointMapper(Element pointElement) {
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
        
        // Normal modifier sets
        Iterable<Element> modifiersElements = getElementChildren(pointElement, "modifiers");
        List<ModifiersMatcher> modifiersMatcherList = Lists.newArrayList();
        for (Element modifiersElement : modifiersElements) {
            ModifiersMatcher modifiersMatcher = getModifiersMatcher(modifiersElement);
            modifiersMatcherList.add(modifiersMatcher);
        }
        
        // 'Base' modifier sets, used for tying coincidental measurements to the channel data that spawned them.
        // Example: for the Var measurement taken at the Peak Demand event, the base UOM and base modifiers would be
        // those of the Peak Demand channel data.
        Iterable<Element> baseModifiersElements = getElementChildren(pointElement, "baseModifiers");
        List<ModifiersMatcher> baseModifiersMatcherList = null;
        String baseUom = pointElement.getChildTextTrim("baseUom"); // will be null if not present
        if (!Iterables.isEmpty(baseModifiersElements)) {
            baseModifiersMatcherList = Lists.newArrayList();
            for (Element baseModifiersElement : baseModifiersElements) {
                ModifiersMatcher baseModifiersMatcher = getModifiersMatcher(baseModifiersElement);
                baseModifiersMatcherList.add(baseModifiersMatcher);
            }
        }
        
        PointMapper pointMapper = new PointMapper(name, 
                                                  uom, 
                                                  multiplier, 
                                                  siPrefixParsing, 
                                                  modifiersMatcherList, 
                                                  baseUom, 
                                                  baseModifiersMatcherList);
        return pointMapper;
    }
    
    private static ModifiersMatcher getModifiersMatcher(Element modifiersElement) {
        String matchStyleStr = modifiersElement.getAttributeValue("match");
        MatchStyle matchStyle = MatchStyle.valueOf(matchStyleStr);
        Iterable<Element> modifierElements = getElementChildren(modifiersElement, "modifier");
        Set<String> modifierSet = Sets.newHashSet();
        for (Element modifierElement : modifierElements) {
            String uomModifier = modifierElement.getTextTrim();
            modifierSet.add(uomModifier);
        }
        return new ModifiersMatcher(matchStyle, modifierSet);
    }

    public static Iterable<Element> getElementChildren(Element element, String name) {
        return Iterables.filter(element.getChildren(name), Element.class);
    }
    
    @ManagedOperation
    public void flushCache() {
        computingCache.invalidateAll();
    }
    
    @ManagedAttribute
    public long getCacheSize() {
        return computingCache.size();
    }
    
    @Override
    public Multimap<PaoType, PointMapper> getPointMapper() {
        return pointMapperMap;
    }

}