package com.cannontech.amr.porterResponseMonitor.model;

import java.util.Set;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.Sets;

public enum PorterResponseMonitorMatchStyle implements DisplayableEnum {

	// not sure if you can just do something like MatchStyle.any
	// so doing this for now:

	none {
		@Override
		public boolean matches(Set<?> a, Set<?> b) {
			return Sets.intersection(a, b).isEmpty();
		}
	},
	any {
		@Override
		public boolean matches(Set<?> a, Set<?> b) {
			return !Sets.intersection(a, b).isEmpty();
		}
	},
	all {
		@Override
		public boolean matches(Set<?> a, Set<?> b) {
			return a.containsAll(b);
		}
	};

	public abstract boolean matches(Set<?> a, Set<?> b);

	@Override
	public String getFormatKey() {
		return keyPrefix + name();
	}

	private final static String keyPrefix = "yukon.web.modules.amr.porterResponseMonitor.matchStyle.";
}
