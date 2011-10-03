package com.cannontech.capcontrol.creation.service;

import java.util.List;

import com.cannontech.capcontrol.creation.model.CbcImportData;
import com.cannontech.capcontrol.creation.model.CbcImportResult;
import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.capcontrol.creation.model.HierarchyImportResult;

public interface CapControlImportService {

    /**
     * Attempts to create a CBC object out of user specified data. 
     * @param cbcImportData the import data specified by the user that will be used
     * to create the CBC.
     * @param results a list of results for which the outcome of the import of the 
     * current CBC will be added.
     * @throws NotFoundException if the user specified a CapBank name as a parent that
     * does not exist in the database.
     * @throws IllegalArgumentException if the PaoTemplate used to create the device is
     * missing required Fields objects. This exception is an unrecoverable Yukon error
     * (created by failing to code the creation of the object's fields map with all 
     * required fields) and is not an exception that should be caught. There is nothing 
     * the end user can do to fix this problem.
     */
	public void createCbc(CbcImportData cbcImportData, List<CbcImportResult> results);
	
	/**
	 * Attempts to create a CBC from a template using user specified data.
	 * @param cbcImportData the import data specified by the user that will be used
     * to create the CBC.
	 * @param results a list of results for which the outcome of the import of the 
     * current CBC will be added.
     * @throws NotFoundException if the user specified a CapBank name as a parent that
     * does not exist in the database.
     * @throws IllegalArgumentException if the PaoTemplate used to create the device is
     * missing required Fields objects. This exception is an unrecoverable Yukon error
     * (created by failing to code the creation of the object's fields map with all 
     * required fields) and is not an exception that should be caught. There is nothing 
     * the end user can do to fix this problem.
	 */
	public void createCbcFromTemplate(CbcImportData cbcImportData, List<CbcImportResult> results);
	
	/**
	 * Attempts to update a previously created CBC object using user specified data.
	 * @param cbcImportData the import data specified by the user that will be used
     * to update the CBC.
     * @param results a list of results for which the outcome of the import of the 
     * current CBC will be added.
     * @throws NotFoundException if the user specified a CapBank name as a parent that
     * does not exist in the database.
     * @throws IllegalArgumentException if the PaoTemplate used to create the device is
     * missing required Fields objects. This exception is an unrecoverable Yukon error
     * (created by failing to code the creation of the object's fields map with all 
     * required fields) and is not an exception that should be caught. There is nothing 
     * the end user can do to fix this problem.
	 */
	public void updateCbc(CbcImportData cbcImportData, List<CbcImportResult> results);
	
	/**
	 * Attempts to remove a previously created CBC object using user specified data. 
	 * @param cbcImportData the import data specified by the user that will be used
	 * to remove the CBC.
	 * @param results a list of results for which the outcome of the import of the 
     * current CBC will be added.
     * @throws IllegalArgumentException if the PaoTemplate used to create the device is
     * missing required Fields objects. This exception is an unrecoverable Yukon error
     * (created by failing to code the creation of the object's fields map with all 
     * required fields) and is not an exception that should be caught. There is nothing 
     * the end user can do to fix this problem.
	 */
	public void removeCbc(CbcImportData cbcImportData, List<CbcImportResult> results);
	
	/**
	 * Attempts to create a Cap Control hierarchy object using user specified data.
	 * @param hierarchyImportData the import data specified by the user that will be used
	 * to create the hierarchy object.
	 * @param results a list of results for which the outcome of the import of the 
     * current hierarchy object will be added.
     * @throws NotFoundException if the user specified a parent name that does not exist 
     * in the database.
     * @throws IllegalArgumentException if the PaoTemplate used to create the object is
     * missing required Fields objects. This exception is an unrecoverable Yukon error
     * (created by failing to code the creation of the object's fields map with all 
     * required fields) and is not an exception that should be caught. There is nothing 
     * the end user can do to fix this problem.
	 */
	public void createHierarchyObject(HierarchyImportData hierarchyImportData, List<HierarchyImportResult> results);
	
	/**
	 * Attempts to update a previously created Cap Control hierarchy object using user
	 * specified data.
	 * @param hierarchyImportData the import data specified by the user that will be used
     * to update the hierarchy object.
     * @param results a list of results for which the outcome of the import of the 
     * current hierarchy object will be added.
     * @throws NotFoundException if the user specified a parent name that does not exist 
     * in the database.
     * @throws IllegalArgumentException if the PaoTemplate used to create the object is
     * missing required Fields objects. This exception is an unrecoverable Yukon error
     * (created by failing to code the creation of the object's fields map with all 
     * required fields) and is not an exception that should be caught. There is nothing 
     * the end user can do to fix this problem.
	 */
	public void updateHierarchyObject(HierarchyImportData hierarchyImportData, List<HierarchyImportResult> results);
	
	/**
     * Attempts to remove a previously created Cap Control hierarchy object using user
     * specified data.
     * @param hierarchyImportData the import data specified by the user that will be used
     * to remove the hierarchy object.
     * @param results a list of results for which the outcome of the import of the 
     * current hierarchy object will be added.
     * @throws IllegalArgumentException if the PaoTemplate used to create the object is
     * missing required Fields objects. This exception is an unrecoverable Yukon error
     * (created by failing to code the creation of the object's fields map with all 
     * required fields) and is not an exception that should be caught. There is nothing 
     * the end user can do to fix this problem.
     */
	public void removeHierarchyObject(HierarchyImportData hierarchyImportData, List<HierarchyImportResult> results);
}
