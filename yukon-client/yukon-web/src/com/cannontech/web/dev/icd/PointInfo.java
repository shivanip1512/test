package com.cannontech.web.dev.icd;

import java.util.HashMap;
import java.util.Map;

public class PointInfo {
    private NameScale nameScale;
    private Map<PointDefinition, NameScale> coincidentPoints;
    public PointInfo(NameScale nameScale, Map<PointDefinition, NameScale> coincidentPoints) {
        this.nameScale = nameScale;
        this.coincidentPoints = coincidentPoints;
    }
    public PointInfo(NameScale nameScale) {
        this(nameScale, new HashMap<>());
    }
    public NameScale getNameScale() {
        return nameScale;
    }
    public Map<PointDefinition, NameScale> getCoincidentPoints() {
        return coincidentPoints;
    }
    public void addCoincidentPoint(PointDefinition pd, NameScale ns) {
        coincidentPoints.put(pd, ns);
    }
}