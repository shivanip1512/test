package com.cannontech.common.validation.model;

import java.util.Date;
import java.util.Set;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.google.common.collect.Sets;

public class ReviewPoint implements PointValueHolder {

	private long changeId;
	private Set<RphTag> rphTag;
	private DisplayablePao displayablePao;
	private PointValueHolder pointValue;
	
	public long getChangeId() {
		return changeId;
	}
	public void setChangeId(long changeId) {
		this.changeId = changeId;
	}

    public Set<RphTag> getRphTag() {
        if (this.rphTag == null) {
            this.rphTag = Sets.newHashSet();
        }
        return rphTag;
    }

    public void addRphTag(RphTag rphTag) {
        if (this.rphTag == null) {
            this.rphTag = Sets.newHashSet();
        }
        this.rphTag.add(rphTag);
    }
    
    public void setRphTag(Set<RphTag> rphTag) {
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
