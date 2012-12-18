package com.cannontech.common.validation.model;

import java.util.Date;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.core.dynamic.PointValueHolder;

public class ReviewPoint implements PointValueHolder {

	private long changeId;
	private RphTag rphTag;
	private DisplayablePao displayablePao;
	private PointValueHolder pointValue;
	
	public long getChangeId() {
		return changeId;
	}
	public void setChangeId(long changeId) {
		this.changeId = changeId;
	}
	public RphTag getRphTag() {
		return rphTag;
	}
	public void setRphTag(RphTag rphTag) {
		this.rphTag = rphTag;
	}
	public DisplayablePao getDisplayablePao() {
		return displayablePao;
	}
	public void setDisplayablePao(DisplayablePao displayablePao) {
		this.displayablePao = displayablePao;
	}
	public PointValueHolder getPointValue() {
		return pointValue;
	}
	public void setPointValue(PointValueHolder pointValue) {
		this.pointValue = pointValue;
	}
	
	@Override
	public int getId() {
		return pointValue.getId();
	}
	@Override
	public Date getPointDataTimeStamp() {
		return pointValue.getPointDataTimeStamp();
	}
	@Override
	public int getType() {
		return pointValue.getType();
	}
	@Override
	public double getValue() {
		return pointValue.getValue();
	}
}
