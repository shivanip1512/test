package com.cannontech.common.i18n;

import java.text.Collator;
import java.util.Comparator;

import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Ordering;

public class CollationUtils {
    public static Ordering<String> getCaseInsensitiveOrdering(YukonUserContext userContext) {
        final Collator collator = Collator.getInstance(userContext.getLocale());
        collator.setStrength(Collator.PRIMARY);
        // Collator does not implement Comparator<String>, so this is a workaround
        Ordering<String> ordering = Ordering.from(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return collator.compare(o1, o2);
            }
        });
        return ordering;
    }
}
