package com.cannontech.common.util;

import com.google.common.collect.ImmutableSet;

public class ModifiersMatcher {
	private MatchStyle style;
	private ImmutableSet<String> modifiers;

	public ModifiersMatcher(MatchStyle style, Iterable<String> modifiers) {
		this.style = style;
		this.modifiers = ImmutableSet.copyOf(modifiers);
	}

	public MatchStyle getStyle() {
		return style;
	}

	public ImmutableSet<String> getModifiers() {
		return modifiers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s(%s)", style, modifiers);
	}
}