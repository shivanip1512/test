package com.cannontech.web.taglib;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class MessageScopeHelper {
    private static final String scopeAttributeName = "com.cannontech.web.scopeStack";

    public static MessageScope forRequest(HttpServletRequest request) {
        Deque<ScopeHolder> scopeStack = getScopeStack(request);
        return new MessageScope(scopeStack);
    }

    private static Deque<ScopeHolder> getScopeStack(HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        Deque<ScopeHolder> stack = (Deque<ScopeHolder>) request.getAttribute(scopeAttributeName);
        if (stack == null) {
            stack = new ArrayDeque<ScopeHolder>();
            request.setAttribute(scopeAttributeName, stack);
        }

        return stack;
    }

    public static class MessageScope {
        private Deque<ScopeHolder> scopeStack;

        public MessageScope(Deque<ScopeHolder> scopeStack) {
            this.scopeStack = scopeStack;
        }

        public MessageScope() {
            this.scopeStack = new ArrayDeque<ScopeHolder>();
        }

        public void pushScope(String keyPart, boolean sticky) {
            if (StringUtils.isBlank(keyPart)) {
                return;
            }
            ScopeHolder scopeHolder = new ScopeHolder(keyPart, sticky);
            scopeStack.push(scopeHolder);
        }

        public void popScope() {
            scopeStack.pop();
        }
        
        public MessageSourceResolvable generateResolvable(String key, Object ... arguments) {
            MessageSourceResolvable messageSourceResolvable;
            if (key.startsWith(".")) {
                List<String> codes = getFullKeys(key.substring(1), "yukon.web.defaults.");
                messageSourceResolvable = YukonMessageSourceResolvable.createMultipleCodesWithArguments(codes, arguments);
            } else {
                String codes[] = new String[] {key};
                messageSourceResolvable = new YukonMessageSourceResolvable(codes, arguments);
            }
            return messageSourceResolvable;
        }

        /**
         * Get the list of message keys that should be searched in order for the given
         * suffix and the current state of the stack of scopes.
         * @param request
         * @param suffix - e.g. "name"
         * @param defaultPrefix - e.g. "yukon.web.defaults."
         * @return
         */
        public List<String> getFullKeys(final String suffix, final String defaultPrefix) {
            List<ScopeHolder> scopes = Lists.newArrayListWithExpectedSize(scopeStack.size() + 1);
            Iterators.addAll(scopes, scopeStack.descendingIterator());
            scopes.add(new ScopeHolder(suffix, true)); // it works nicely to
            // pretend the suffix is
            // a sticky scope
            String[] result = new String[scopes.size()];
            splitAndProcess(result, 0, "", scopes, defaultPrefix);

            return Arrays.asList(result);
        }

        private void splitAndProcess(String[] result, int index, String prefix,
                List<ScopeHolder> staticKeys, String defaultPrefix) {
            if (staticKeys.isEmpty()) {
                return;
            }
            if (staticKeys.size() == 1) {
                result[index] = defaultPrefix + staticKeys.get(0).scope;
                return;
            }

            // find next sticky (this code will always at least select the last
            // element)
            int nextStickyIndex = 1;
            for (; nextStickyIndex < staticKeys.size(); nextStickyIndex++) {
                if (staticKeys.get(nextStickyIndex).sticky) {
                    break;
                }
            }

            List<ScopeHolder> left = staticKeys.subList(0, nextStickyIndex);
            List<ScopeHolder> right = staticKeys.subList(nextStickyIndex, staticKeys.size());
            Function<ScopeHolder, String> nameFromScope = new Function<ScopeHolder, String>() {
                public String apply(ScopeHolder from) {
                    return from.scope;
                }
            };
            List<String> staticKeyStrings = Lists.transform(right,
                                                            nameFromScope);
            String suffix = StringUtils.join(staticKeyStrings, ".");
            
            // The following builds up the currentPrefix string one element at a
            // time and places it into the result array backwards (easiest to see in a debugger).
            StringBuilder currentPrefix = new StringBuilder(100);
            currentPrefix.append(prefix);
            for (int i = 0; i < left.size(); i++) {
                currentPrefix.append(left.get(i).scope + ".");
                result[index + left.size() - i - 1] = currentPrefix + suffix;
            }

            splitAndProcess(result, index + left.size(), defaultPrefix, right, defaultPrefix);
        }

    }

    private static class ScopeHolder {
        public ScopeHolder(String scope, boolean sticky) {
            super();
            this.scope = scope;
            this.sticky = sticky;
        }

        private String scope;
        private boolean sticky;

        @Override
        public String toString() {
            return scope + (sticky ? "!" : "");
        }
    }
}
