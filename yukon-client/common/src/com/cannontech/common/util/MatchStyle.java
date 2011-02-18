package com.cannontech.common.util;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * These Enum names appear in both XML files and in database rows and should never be changed.
 */
public enum MatchStyle {
	none {
		@Override
		public boolean matches(Set<?> a, Set<?> b) {
			return Sets.intersection(a, b).isEmpty();
		}
	},
	some {
		@Override
		public boolean matches(Set<?> a, Set<?> b) {
			return !Sets.intersection(a, b).isEmpty();
		}
	},
	any {
	    @Override
	    public boolean matches(Set<?> a, Set<?> b) {
	        return b.isEmpty() || !Sets.intersection(a, b).isEmpty();
	    }
	},
	notany {
	    @Override
	    public boolean matches(Set<?> a, Set<?> b) {
	        return !b.isEmpty() && Sets.intersection(a, b).isEmpty();
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
	},
	empty {
	    @Override
	    public boolean matches(Set<?> a, Set<?> b) {
	        return a.isEmpty();
	    }
	},
	notempty {
	    @Override
	    public boolean matches(Set<?> a, Set<?> b) {
	        return !a.isEmpty();
	    }
	};

	public abstract boolean matches(Set<?> a, Set<?> b);
}