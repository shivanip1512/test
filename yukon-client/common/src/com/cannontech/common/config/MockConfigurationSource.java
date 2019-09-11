package com.cannontech.common.config;

import java.util.Optional;

import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadablePeriod;

public class MockConfigurationSource implements ConfigurationSource {

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getBoolean(MasterConfigBoolean key, boolean defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getBoolean(MasterConfigBoolean developmentMode) {
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
    public String getRequiredString(MasterConfigString key) throws UnknownKeyException {
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

    @Override
    public String getString(MasterConfigString key, String defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Duration getDuration(String key, ReadableDuration defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Period getPeriod(String key, ReadablePeriod defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Double getDouble(MasterConfigDouble key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getString(MasterConfigString key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getInteger(MasterConfigInteger key, int defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getRequiredBoolean(MasterConfigBoolean key) throws UnknownKeyException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<String> getOptionalString(MasterConfigString key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLicenseEnabled(MasterConfigLicenseKey key) {
        throw new UnsupportedOperationException();
    }
}