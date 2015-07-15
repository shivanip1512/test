package com.cannontech.web.editor.point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.cannontech.database.data.fdr.FDRInterface;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.fdr.FDRTranslation;
import com.cannontech.web.util.JSFParamUtil;

public class PointFDREntry {
    
    private PointBase pointBase = null;
    private FDRTranslationEntry fdrTranslationEntry = null;
    private List<SelectItem> fdrInterfaceSel = new ArrayList<>();

    // <InterfaceType, Directions>
    private Map<String, List<SelectItem>> fdrDirectionsMap = new HashMap<>();

    // <InterfaceType, FDRInterface>
    private Map<String, FDRInterface> fdrInterfacesMap = new HashMap<>();

    private int selectedRowNum = -1;
    private FDRInterface selectedInterface = null;
    private String selectedTranslation = null;

    public PointFDREntry(PointBase pBase) {

        if (pBase == null)
            throw new IllegalArgumentException("PointFDREntry can not be created with a NULL PointBase reference");

        loadInterfaceData();

        pointBase = pBase;
    }

    private PointBase getPointBase() {
        return pointBase;
    }

    /**
     * Shows the current editor for the given translation string
     */
    public String showTranslation() {

        int rowNum = new Integer(JSFParamUtil.getJSFReqParam("rowNumber"));
        String interfaceName = JSFParamUtil.getJSFReqParam("fdrInterface");

        // set our data structures up
        setSelectedRowNum(rowNum);
        setSelectedInterface(getFdrInterfacesMap().get(interfaceName));

        fdrTranslationEntry = new FDRTranslationEntry(
            getPointBase().getPointFDRList().get(rowNum), getSelectedInterface());

        // keep us on our same page
        return null;
    }

    /**
     * Shows the current editor for the given translation string
     */
    public void interfaceChange(ValueChangeEvent ev) {

        Integer rowNum = (Integer) JSFParamUtil.getChildElemValue(ev.getComponent(), "rowNumber");
        String interfaceName = (String) ev.getNewValue();

        // set up some values as to edit this translation
        setSelectedRowNum(-1);
        setSelectedInterface(null);
        // (FDRInterface)getFdrInterfacesMap().get(interfaceName) );

        // update the db object with the change
        if (rowNum >= 0 && rowNum <= getPointBase().getPointFDRList().size()) {
            
            Point point = getPointBase().getPoint();

            getPointBase().getPointFDRList().set(
                rowNum.intValue(),
                FDRInterface.createDefaultTranslation(getFdrInterfacesMap().get(interfaceName),
                    point.getPointID(), point.getPointType()));

        }
    }

    /**
     * Remove the selected translation from our table
     */
    public String deleteTranslation() {

        int rowNum = new Integer(JSFParamUtil.getJSFReqParam("rowNumber"));

        // be sure we have a valid row number
        if (rowNum >= 0 && rowNum <= getPointBase().getPointFDRList().size()) {
            getPointBase().getPointFDRList().remove(rowNum);
        }

        // we must clear out any editing action
        setSelectedRowNum(-1);
        setSelectedInterface(null);

        // keep us on our same page
        return null;
    }

    /**
     * Add a new default translation entry to our table
     */
    public String addTranslation() {

        FDRTranslation trans = FDRTranslation.createTranslation(getPointBase().getPoint().getPointID());

        getPointBase().getPointFDRList().add(trans);

        // we must clear out any editing action
        setSelectedRowNum(-1);
        setSelectedInterface(null);

        // keep us on our same page
        return null;
    }

    /**
     * Loads all of our FDRInterface data structures into memory
     */
    private void loadInterfaceData() {

        FDRInterface[] fdrInterfaces = FDRInterface.getALLFDRInterfaces();

        fdrDirectionsMap = new HashMap<>();
        fdrInterfacesMap = new HashMap<>();
        fdrInterfaceSel = new ArrayList<>();

        for (FDRInterface fdrIface : fdrInterfaces) {
            fdrInterfaceSel.add(new SelectItem(fdrIface.getFdrInterface().getInterfaceName(),
                                               fdrIface.getFdrInterface().getInterfaceName()));
            
            // mapping used to map InterfaceName to the FDRInterface instance
            getFdrInterfacesMap().put(fdrIface.getFdrInterface().getInterfaceName(), fdrIface);
            
            // build our directions mapping for this interface
            String[] dirsStrs = fdrIface.getFdrInterface().getAllDirections();
            
            List<SelectItem> dirItems = new ArrayList<>();
            
            for (String dirString : dirsStrs) {
                dirItems.add(new SelectItem(dirString, dirString));
            }
            
            // map of InterfaceType to SelectItms[]
            fdrDirectionsMap.put(fdrIface.getFdrInterface().getInterfaceName(), dirItems);
        }

    }

    /**
     * Returns the number of translations we have in our list
     */
    public int getFDRTransSize() {
        return getPointBase().getPointFDRList().size();
    }

    /**
     * Returns all FDR interfaces from the DB
     */
    public List<SelectItem> getFDRInterfaces() {
        return fdrInterfaceSel;
    }

    /**
     * Gets all the possible direction for a FDRInterface
     */
    public Map<String, List<SelectItem>> getFDRDirectionsMap() {

        return fdrDirectionsMap;
    }

    public Map<String, FDRInterface> getFdrInterfacesMap() {
        return fdrInterfacesMap;
    }

    public int getSelectedRowNum() {
        return selectedRowNum;
    }

    public void setSelectedRowNum(int i) {
        selectedRowNum = i;
    }

    private FDRInterface getSelectedInterface() {
        return selectedInterface;
    }

    private void setSelectedInterface(FDRInterface interface1) {
        selectedInterface = interface1;
    }

    public FDRTranslationEntry getFdrTranslationEntry() {
        return fdrTranslationEntry;
    }

    public String getSelectedTranslation() {
        return selectedTranslation;
    }

    public void setSelectedTranslation(String string) {
        selectedTranslation = string;
    }

}
