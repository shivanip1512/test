package com.cannontech.common.trend.model;

import java.util.Set;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableBiMap.Builder;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.database.db.graph.GraphRenderers;

public enum RenderType implements DisplayableEnum {
    LINE(GraphRenderers.LINE),
    LINE_SHAPES(GraphRenderers.LINE_SHAPES),
    LINE_AREA(GraphRenderers.LINE_AREA),
    LINE_AREA_SHAPES(GraphRenderers.LINE_AREA_SHAPES),
    STEP(GraphRenderers.STEP),
    STEP_SHAPES(GraphRenderers.STEP_SHAPES),
    STEP_AREA(GraphRenderers.STEP_AREA),
    STEP_AREA_SHAPES(GraphRenderers.STEP_AREA_SHAPES),
    BAR(GraphRenderers.BAR),
    BAR_3D(GraphRenderers.BAR_3D);
    
   
    private final static Set<RenderType> lines = ImmutableSet.of(LINE, LINE_AREA, LINE_AREA_SHAPES, LINE_SHAPES);
    private final static Set<RenderType> steps = ImmutableSet.of(STEP, STEP_AREA, STEP_AREA_SHAPES, STEP_SHAPES);
    private final static Set<RenderType> bars = ImmutableSet.of(BAR, BAR_3D);
    private final static BiMap<Integer, RenderType> idMap;
    private final static Builder<Integer, RenderType> b = new ImmutableBiMap.Builder<>();
    private final static Set<RenderType> webSupportedTypes = ImmutableSet.of(LINE, STEP, BAR);
    private int id;
    private String baseKey = "yukon.web.modules.tools.trend.style.";
    /**
     * pre-step to instantiation. 
     */
    static {
        for (RenderType type : values())
            b.put(type.id, type);
        idMap = b.build();
    }



    /**
     * Class constructor
     * serializes each enumerable with a specific value to coincide with datasource entry
     * <p>
     * @param {@link int} in hex, the value contained in datasource
     * @return void
     */
    private RenderType(int id) {
        this.id = id;
    }
    /**
     * id getter for enumerable instance
     * <p>
     * @return {@link int}
     */
    public int getId() {
        return id;
    }
    /**
     * determines if this is line render
     * @return {@link boolean}
     */
    public boolean isLine() {
        return lines.contains(this);
    }
    
    /**
     * determines if this is step render
     * @return {@link boolean}
     */
    public boolean isStep() {
        return steps.contains(this);
    }
    
    /**
     * determines if this is bar render
     * @return {@link boolean}
     */
    public boolean isBar() {
        return bars.contains(this);
    }

    /**
     * retrieves the enumerable RenderType mapped by datasource number.
     * @return {@link boolean}
     */
    public static RenderType getForId(int id) {
        return idMap.get(id);
    }
    public static Set<RenderType> getWebSupportedTypes() {
        return webSupportedTypes;
    }
    
    @Override
    public String getFormatKey() {
        return baseKey + name();
    }
}
