/**
 * Enum RenderType
 * <p>
 * RenderType is responsible of providing a type for graph style rendering as required by HighCharts.
 * http://www.highcharts.com/
 * 
 * The types defined are:
 * <ul>
 * <li>line -> simple render
 * <li>line shapes -> line and shape render
 * <li>line area -> line and area contour render
 * <li>step -> decorates the line render by stepping the delta
 * <li>step shape -> decorates the line render by stepping the delta then applying a shape render
 * <li>step area -> decorates the line render by stepping the delta then applying a contour area render
 * <li>step area shapes -> decorates the line render by stepping the delta then applying a contour area && shape render
 * <li>bar -> bar render
 * <li>bar 3d -> applies a 2 1/2 D render of the bar. 
 * </ul>
 * 
 * 
 * 
 * TODO: There are non graphs defined, and they are used to primarily shunt the response default to the the graph for
 * now until it is determined whether or not we will be using them in future. TBD. 
 * 
 * @author      Thomas Red-Cloud
 * @email       ThomasRedCloud@Eaton.com
 * @version     %I%, %G%
 * @since       1.0
 */
package com.cannontech.web.tools.trends.data;

import java.util.Set;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableBiMap.Builder;

public enum RenderType {
    LINE(0),
    LINE_SHAPES(1),
    LINE_AREA(2),
    LINE_AREA_SHAPES(3),
    STEP(4),
    STEP_SHAPES(5),
    STEP_AREA(6),
    STEP_AREA_SHAPES(7),
    BAR(8),
    BAR_3D(9);
    
    /**
     * The <code>Set<RenderType></code> representation lines family
     */
    private final static Set<RenderType> lines = ImmutableSet.of(LINE, LINE_AREA, LINE_AREA_SHAPES, LINE_SHAPES);

    /**
     * The <code>Set<RenderType></code> representation of steps family
     */
    private final static Set<RenderType> steps = ImmutableSet.of(STEP, STEP_AREA, STEP_AREA_SHAPES, STEP_SHAPES);

    /**
     * The <code>Set<RenderType></code> representation of bars family
     */
    private final static Set<RenderType> bars = ImmutableSet.of(BAR, BAR_3D);

    /**
     * The <code>BitMap<Integer, RenderType></code> provides an iterator to match what is in the datasource to actual enumerations
     */
    private final static BiMap<Integer, RenderType> idMap;

    /**
     * The <code>Builder<Integer, RenderType></code> is the builder object of the list. 
     */
    private final static Builder<Integer, RenderType> b = new ImmutableBiMap.Builder<>();

    /**
     * pre-step to instantiation. 
     */
    static {
        for (RenderType type : values())
            b.put(type.id, type);
        idMap = b.build();
    }
    /**
     * The {@link int} representation from the datasource 
     */
    private int id;

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
}
