package com.cannontech.services.validation.dao;

import com.cannontech.services.validation.model.AnalysisDescription;
import com.google.common.collect.SetMultimap;

public interface AnalysisDescriptionDao {

    public SetMultimap<AnalysisDescription, Integer> loadAnalysisDescriptions();

}