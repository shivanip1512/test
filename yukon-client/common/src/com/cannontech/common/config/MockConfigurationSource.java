package com.cannontech.common.config;

public class MockConfigurationSource implements ConfigurationSource {

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getInteger(String key, int defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLong(String key, long defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getRequiredInteger(String key) throws UnknownKeyException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequiredString(String key) throws UnknownKeyException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getString(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getString(String key, String defaultValue) {
        throw new UnsupportedOperationException();
    }
    
}