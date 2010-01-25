package com.cannontech.web.taglib;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;

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

        public void pushScope(String ... searchPaths) {
            if (searchPaths.length == 0) {
                return;
            }
            ScopeHolder scopeHolder = new BasicScopeHolder(searchPaths);
            pushScope(scopeHolder);
        }

        public void pushScope(ScopeHolder scopeHolder) {
            scopeStack.push(scopeHolder);
        }

        public void popScope() {
            scopeStack.pop();
        }
        
        public MessageSourceResolvable generateResolvable(String key, Object ... arguments) {
            if (key.startsWith("yukon.")) {
                return new YukonMessageSourceResolvable(key, arguments);
            }
            MessageSourceResolvable messageSourceResolvable;
            List<String> codes = getFullKeys(key, "yukon.web.");
            messageSourceResolvable = YukonMessageSourceResolvable.createMultipleCodesWithArguments(codes, arguments);
            return messageSourceResolvable;
        }

        /**
         * Get the list of message keys that should be searched in order for the given
         * suffix and the current state of the stack of scopes.
         * @param request
         * @param suffix - e.g. "name"
         * @param defaultPrefix - e.g. "yukon.web."
         * @return
         */
        public List<String> getFullKeys(final String suffix, String defaultPrefix) {
            List<ScopeHolder> scopes = Lists.newArrayListWithExpectedSize(scopeStack.size());
            Iterables.addAll(scopes, scopeStack);
            List<String> result = Lists.newArrayListWithExpectedSize(6);
            collectKeys(result, scopes, defaultPrefix, suffix);

            return result;
        }

        /**
         * @param result
         * @param scopes
         * @param defaultPrefix prepended to every result, should always end with a period
         * @param suffix will be the final part of every result, should always begin with a period
         */
        private void collectKeys(List<String> result, List<ScopeHolder> scopes, String defaultPrefix, String suffix) {
            if (!suffix.startsWith(".")) {
                result.add(defaultPrefix + suffix);
                return;
            }
            if (scopes.isEmpty()) {
                throw new IllegalArgumentException("ran out of scopes before finding absolute key");
            }

            
            ScopeHolder currentScope = scopes.get(0);
            List<ScopeHolder> remainingScopes = scopes.subList(1, scopes.size());
            List<String> searchPaths = currentScope.getSearchPaths();
            for (String path : searchPaths) {
                String newSuffix = path + suffix;
                collectKeys(result, remainingScopes, defaultPrefix, newSuffix);
            }
        }

    }

    private static interface ScopeHolder {
        public List<String> getSearchPaths();
    }
    
    private static abstract class ScopeHolderBase implements ScopeHolder {
        protected List<String> searchPaths;
        
        public List<String> getSearchPaths() {
            return searchPaths;
        }
        
        @Override
        public String toString() {
            return getSearchPaths().toString();
        }
    }
    
    public static class BasicScopeHolder extends ScopeHolderBase {
        public BasicScopeHolder(String ... paths) {
            Builder<String> builder = ImmutableList.builder();
            for (int i = 0; i < paths.length; i++) {
                builder.add(paths[i].trim());
            }
            searchPaths = builder.build();
        }
    }
    
}
