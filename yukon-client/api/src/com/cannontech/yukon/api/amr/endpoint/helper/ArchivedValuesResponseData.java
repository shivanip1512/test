package com.cannontech.yukon.api.amr.endpoint.helper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.definition.model.PaoData;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.point.PointInfo;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * Stores data gathered for generating an ArchivedValuesResponse (flattened or not).
 */
public class ArchivedValuesResponseData {
    public static class ValueSet {
        private PointValueSelector selector;
        private List<PointValueQualityHolder> values;

        public ValueSet(PointValueSelector selector, List<PointValueQualityHolder> values) {
            this.selector = selector;
            this.values = values;
        }

        public PointValueSelector getSelector() {
            return selector;
        }

        public List<PointValueQualityHolder> getValues() {
            return values;
        }
    }

    public static class PointData {
        private PaoIdentifier paoId;
        private PointSelector pointSelector;
        private PointInfo pointInfo;
        private List<ValueSet> valueSets = Lists.newArrayList();

        public PointData(PaoIdentifier paoId, PointSelector pointSelector) {
            this.paoId = paoId;
            this.pointSelector = pointSelector;
        }

        public PaoIdentifier getPaoId() {
            return paoId;
        }

        public PointSelector getPointSelector() {
            return pointSelector;
        }

        public PointInfo getPointInfo() {
            return pointInfo;
        }

        public List<ValueSet> getValueSets() {
            return valueSets;
        }

        public void addValueSet(ValueSet valueSet) {
            valueSets.add(valueSet);
        }
    }

    private Set<ResponseDescriptor> responseFields;
    private Map<PaoIdentifier, PaoData> paoDataByPaoId;
    private List<PointSelector> pointSelectors = Lists.newArrayList();
    private Multimap<PaoIdentifier, PointData> pointDataByPaoId = HashMultimap.create();
    private Map<PointSelector, Map<PaoIdentifier, PointData>> pointDataBySelectorAndPaoId =
        Maps.newHashMap();
    private Map<Integer, Map<Integer, LiteState>> statesByGroupIdAndRawState = Maps.newHashMap();

    public Set<PaoIdentifier> getPaoIds() {
        return paoDataByPaoId.keySet();
    }

    public Set<ResponseDescriptor> getResponseFields() {
        return responseFields;
    }

    public void setResponseFields(Set<ResponseDescriptor> responseFields) {
        this.responseFields = responseFields;
    }

    public Map<PaoIdentifier, PaoData> getPaoDataByPaoId() {
        return paoDataByPaoId;
    }

    public void setPaoDataByPaoId(Map<PaoIdentifier, PaoData> paoDataByPaoId) {
        this.paoDataByPaoId = paoDataByPaoId;
    }

    public Collection<PointData> getPointDataByPaoId(PaoIdentifier paoId) {
        return pointDataByPaoId.get(paoId);
    }

    public List<PointSelector> getPointSelectors() {
        return pointSelectors;
    }

    public void addPointSelector(PointSelector pointSelector) {
        pointSelectors.add(pointSelector);
    }

    public void addPointData(Iterable<PointData> pointDataSet) {
        for (PointData pointData : pointDataSet) {
            pointDataByPaoId.put(pointData.paoId, pointData);
            Map<PaoIdentifier, PointData> byPaoId =
                pointDataBySelectorAndPaoId.get(pointData.pointSelector);
            if (byPaoId == null) {
                byPaoId = Maps.newHashMap();
                pointDataBySelectorAndPaoId.put(pointData.pointSelector, byPaoId);
            }
            byPaoId.put(pointData.paoId, pointData);
        }
    }

    public void updateLitePoints(PointSelector pointSelector,
                                 Map<PaoIdentifier, PointInfo> litePoints) {
        Map<PaoIdentifier, PointData> pointDataByPaoId =
            pointDataBySelectorAndPaoId.get(pointSelector);
        if (pointDataByPaoId == null) {
            // There were no points of the given type on any of the PAOs specified.
            return;
        }

        for (Entry<PaoIdentifier, PointInfo> entry : litePoints.entrySet()) {
            PaoIdentifier paoId = entry.getKey();
            PointInfo pointInfo = entry.getValue();
            PointData pointData = pointDataByPaoId.get(paoId);
            // null just means there is no point for this paoId/pointSelector combination
            if (pointData != null) {
                pointData.pointInfo = pointInfo;
            }
        }
    }

    public Map<Integer, Map<Integer, LiteState>> getStatesByGroupIdAndRawState() {
        return statesByGroupIdAndRawState;
    }
}
