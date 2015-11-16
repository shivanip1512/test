package com.cannontech.web.stars.dr.consumer.model;

import com.cannontech.database.db.graph.GraphRenderers;

/**
 * Enum which represents the different types of graph views
 */
public enum GraphViewType {
	DEFAULT(GraphRenderers.DEFAULT), 
	SUMMARY(GraphRenderers.SUMMARY), 
	TABULAR(GraphRenderers.TABULAR), 
	LINE(GraphRenderers.LINE),
	LINE_SHAPES(GraphRenderers.LINE_SHAPES),
	LINE_AREA(GraphRenderers.LINE_AREA),
	LINE_AREA_SHAPES(GraphRenderers.LINE_AREA_SHAPES),
	STEP(GraphRenderers.STEP),
	STEP_SHAPES(GraphRenderers.STEP_SHAPES),
	STEP_AREA(GraphRenderers.STEP_AREA),
	STEP_AREA_SHAPES(GraphRenderers.STEP_AREA_SHAPES),
	BAR(GraphRenderers.BAR),
	BAR_3D(GraphRenderers.BAR_3D),
	DONT_CHANGE(GraphRenderers.DONT_CHANGE);

	private Integer type = null;

	private GraphViewType(Integer type) {
		this.type = type;
	}

	public Integer getType() {
		return type;
	}

	/**
	 * Overloaded method to get the enum value for a view type
	 * 
	 * @param type - View type to get enum for
	 * @return Enum value
	 */
	public static GraphViewType valueOf(int type) {

		GraphViewType[] values = GraphViewType.values();
		for (GraphViewType graphView : values) {
			int viewType = graphView.getType();
			if (type == viewType) {
				return graphView;
			}
		}

		throw new IllegalArgumentException(
				"No GraphViewType found for view type: " + type);
	}
}
