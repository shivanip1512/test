package com.cannontech.common.util;

import java.util.Set;

import com.google.common.collect.Sets;

public enum MatchStyle {
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
	},
	notall {
		@Override
		public boolean matches(Set<?> a, Set<?> b) {
			return !a.containsAll(b);
		}
	},
	subset {
		@Override
		public boolean matches(Set<?> a, Set<?> b) {
			return b.containsAll(a);
		}
	},
	notsubset {
		@Override
		public boolean matches(Set<?> a, Set<?> b) {
			return !b.containsAll(a);
		}
	},
	equal {
		@Override
		public boolean matches(Set<?> a, Set<?> b) {
			return a.equals(b);
		}
	},
	notequal {
		@Override
		public boolean matches(Set<?> a, Set<?> b) {
			return !a.equals(b);
		}
	};

	public abstract boolean matches(Set<?> a, Set<?> b);
}