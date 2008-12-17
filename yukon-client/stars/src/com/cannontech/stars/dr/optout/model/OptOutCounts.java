package com.cannontech.stars.dr.optout.model;

/**
 * Enum to represent whether an opt out counts or not
 */
public enum OptOutCounts {

	COUNT, DONT_COUNT;

	public static OptOutCounts valueOf(boolean counts) {
		if (counts) {
			return COUNT;
		} else {
			return DONT_COUNT;
		}
	}

}
