package com.cannontech.stars.dr.optout.model;

/**
 * Enum to represent whether an opt out counts or not
 */
public enum OptOutCounts {

	COUNT{
		@Override
		public boolean isCounts() {
			return true;
		}}, 
	DONT_COUNT {
		@Override
		public boolean isCounts() {
			return false;
		}
	};

	public static OptOutCounts valueOf(boolean counts) {
		if (counts) {
			return COUNT;
		} else {
			return DONT_COUNT;
		}
	}
	
	public abstract boolean isCounts();

}
