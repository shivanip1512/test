package com.cannontech.web.taglib;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableList.Builder;

public class MessageScopeHelper {
    private static final String scopeAttributeName = "com.cannontech.web.scopeStack";
    private static final Logger log = YukonLogManager.getLogger(MessageScopeHelper.class);

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
            ScopeHolder scopeHolder = createScope(searchPaths);
            if (scopeHolder == null) {
                return;
            }
            pushScope(scopeHolder);
        }

        public static ScopeHolder createScope(String... searchPaths) {
            if (searchPaths.length == 0) {
                return null;
            }
            ScopeHolder scopeHolder = new BasicScopeHolder(searchPaths);
            return scopeHolder;
        }

        public void pushScope(ScopeHolder scopeHolder) {
            scopeStack.push(scopeHolder);
        }

        public void popScope() {
            scopeStack.pop();
        }
        
        public void popMatchingScope(ScopeHolder scopeHolder) {
            if (scopeHolder == null) {
                return;
            }
            scopeStack.removeFirstOccurrence(scopeHolder);
        }
        
        public MessageSourceResolvable generateResolvable(String key, Object ... arguments) {
            if (key.startsWith("yukon.")) {
                return new YukonMessageSourceResolvable(key, arguments);
            }
            MessageSourceResolvable messageSourceResolvable;
            try {
                List<String> codes = getFullKeys(key, "yukon.web.");
                messageSourceResolvable = YukonMessageSourceResolvable.createMultipleCodesWithArguments(codes, arguments);
                return messageSourceResolvable;
            } catch (RuntimeException e) {
                log.warn("unable to generate resolvable for '" + key + "' in: " + this.toString());
                throw e;
            }
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
         * @param suffix Usually suffix will be the final part of every result, in this case should always begin with a period,
         *              but this will also repsect a suffix of 'yukon.*' as legitemate also. 
         */
        private void collectKeys(List<String> result, List<ScopeHolder> scopes, String defaultPrefix, String suffix) {
            if (!suffix.startsWith(".")) {
                if (!suffix.startsWith("yukon.")) {
                    result.add(defaultPrefix + suffix);
                } else {
                    result.add(suffix);
                }
                return;
            }
            if (scopes.isEmpty()) {
                throw new IllegalArgumentException("ran out of scopes before finding absolute key for '" + suffix + "'");
            }
            
            ScopeHolder currentScope = scopes.get(0);
            List<ScopeHolder> remainingScopes = scopes.subList(1, scopes.size());
            List<String> searchPaths = currentScope.getSearchPaths();
            for (String path : searchPaths) {
                String newSuffix = path + suffix;
                collectKeys(result, remainingScopes, defaultPrefix, newSuffix);
            }
        }
        
        @Override
        public String toString() {
            return ObjectUtils.toString(scopeStack);
        }

    }

    public static interface ScopeHolder {
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

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((searchPaths == null) ? 0 : searchPaths.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ScopeHolderBase other = (ScopeHolderBase) obj;
            if (searchPaths == null) {
                if (other.searchPaths != null)
                    return false;
            } else if (!searchPaths.equals(other.searchPaths))
                return false;
            return true;
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
