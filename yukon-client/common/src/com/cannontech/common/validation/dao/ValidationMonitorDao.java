package com.cannontech.common.validation.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.common.validation.model.ValidationMonitor;
import com.google.common.collect.SetMultimap;

public interface ValidationMonitorDao {

    public SetMultimap<ValidationMonitor, Integer> loadEnabledValidationMonitors();

    public ValidationMonitor getById(int validationMonitorId) throws ValidationMonitorNotFoundException;

    public Set<ValidationMonitor> getValidationMonitors();

    public void saveOrUpdate(ValidationMonitor validationMonitor);

    public List<ValidationMonitor> getAll();

    public boolean delete(int validationMonitorId);

    public boolean processorExistsWithName(String name);

}