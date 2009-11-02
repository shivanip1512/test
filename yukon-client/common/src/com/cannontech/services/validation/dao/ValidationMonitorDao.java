package com.cannontech.services.validation.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.services.validation.model.ValidationMonitor;
import com.google.common.collect.SetMultimap;

public interface ValidationMonitorDao {

    public SetMultimap<ValidationMonitor, Integer> loadAnalysisDescriptions();

    public ValidationMonitor getById(int validationMonitorId) throws ValidationMonitorNotFoundException;

    public Set<ValidationMonitor> getValidationMonitors();

    public void saveOrUpdate(ValidationMonitor validationMonitor);

    public List<ValidationMonitor> getAll();

    public boolean delete(int validationMonitorId);

    public boolean processorExistsWithName(String name);

}