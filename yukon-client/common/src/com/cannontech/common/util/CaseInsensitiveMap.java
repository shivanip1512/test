package com.cannontech.common.util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class CaseInsensitiveMap<V> implements Map<String, V> {
    private HashMap<StringKey, V> delegate = new HashMap<StringKey, V>();
    
    
    private class StringKey {
        private String key;
        private String lowerCache;
        
        public StringKey(String key) {
            this.key = key;
            lowerCache = key.toLowerCase();
        }
        
        public String getKey() {
            return this.key;
        }

        @Override
        public int hashCode() {
            return lowerCache.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final StringKey other = (StringKey) obj;
            if (lowerCache == null) {
                if (other.lowerCache != null)
                    return false;
            } else if (!lowerCache.equals(other.lowerCache))
                return false;
            return true;
        }
        
        
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        StringKey theKey = new StringKey(key.toString());
        return delegate.containsKey(theKey);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<String, V>> entrySet() {
        return new AbstractSet<java.util.Map.Entry<String, V>>() {

            @Override
            public Iterator<java.util.Map.Entry<String, V>> iterator() {
                final Iterator<java.util.Map.Entry<StringKey, V>> iterator = delegate.entrySet().iterator();
                return new Iterator<java.util.Map.Entry<String, V>>() {

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public java.util.Map.Entry<String, V> next() {
                        final Entry<StringKey, V> next = iterator.next();
                        return new Map.Entry<String, V>() {

                            @Override
                            public String getKey() {
                                return next.getKey().getKey();
                            }

                            @Override
                            public V getValue() {
                                return next.getValue();
                            }

                            @Override
                            public V setValue(V value) {
                                return next.setValue(value);
                            }
                            
                        };
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                    
                };
            }

            @Override
            public int size() {
                return delegate.size();
            }
            
        };    }

    @Override
    public V get(Object key) {
        StringKey theKey =  new StringKey(key.toString());
        return delegate.get(theKey);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return new AbstractSet<String>() {

            @Override
            public Iterator<String> iterator() {
                final Iterator<StringKey> iterator = delegate.keySet().iterator();
                return new Iterator<String>() {

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public String next() {
                        return iterator.next().getKey();
                    }

                    @Override
                    public void remove() {
                        iterator.remove();
                    }
                    
                };
            }

            @Override
            public int size() {
                return delegate.size();
            }
            
        };
    }

    @Override
    public V put(String key, V value) {
        StringKey theKey = new StringKey(key);
        return delegate.put(theKey, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        HashMap<StringKey, V> temp = new HashMap<StringKey, V>();
        for (Map.Entry<? extends String, ? extends V> it : m.entrySet()) {
            StringKey theKey = new StringKey(it.getKey());
            temp.put(theKey, it.getValue());
        }
        delegate.putAll(temp);
    }

    @Override
    public V remove(Object key) {
        StringKey theKey = new StringKey(key.toString());
        return delegate.remove(theKey);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

}
