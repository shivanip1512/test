package com.cannontech.amr.rfn.service.pointmapping.icd;

import java.util.HashMap;
import java.util.Map;

public class NameScaleCoincidents {
    private NameScale nameScale;
    private Map<PointDefinition, NameScale> coincidentPoints;
    public NameScaleCoincidents(NameScale nameScale, Map<PointDefinition, NameScale> coincidentPoints) {
        this.nameScale = nameScale;
        this.coincidentPoints = coincidentPoints;
    }
    public NameScaleCoincidents(NameScale nameScale) {
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