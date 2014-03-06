package com.cannontech.web.capcontrol;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.util.ServletUtil;

/**
 * Generates a URL for a graph with the given cache and item ID
 */
public class CBCWebUtils
{
    public final static String TYPE_PF = "PF";
    public final static String TYPE_VARWATTS = "VW";

    public final static String TYPE_ORPH_SUBS = "__cti_oSubBuses__";
    public final static String TYPE_ORPH_SUBSTATIONS = "__cti_oSubstations__";
    public final static String TYPE_ORPH_FEEDERS = "__cti_oFeeders__";
    public final static String TYPE_ORPH_BANKS = "__cti_oBanks__";
    public final static String TYPE_ORPH_CBCS = "__cti_oCBCs__";
    public final static String TYPE_ORPH_REGULATORS = "__cti_oRegulators__";

    private static final String GRAPH_30_DAY_URL = "/servlet/GraphGenerator?action=EncodeGraph";

    /**
     * Creates a URL that will generate a graph for the give FEEDER
     * or SUBUBUS id
     */
    public static synchronized String genGraphURL(int theId, CapControlCache theCache,
                                                  String period, String type) {
        if (theCache == null) {
            return null;
        }

        String retURL = GRAPH_30_DAY_URL;
        if (period == null) {
            retURL += "&period=" + ServletUtil.PREVTHIRTYDAYS;
        } else {
            retURL += "&period=" + period;
        }

        String res = null;
        if (theCache.isSubBus(theId)) {
            res = _createSubBusGraphURL(theCache.getSubBus(new Integer(theId)), type);
            retURL = (res == null ? null : retURL + res);
        } else if (theCache.isFeeder(theId)) {
            res = _createFeederGraphURL(theCache.getFeeder(new Integer(theId)), type);
            retURL = (res == null ? null : retURL + res);
        }

        return retURL;
    }

    public static synchronized String genGraphURL(int theId, CapControlCache theCache, String type) {
        return genGraphURL(theId, theCache, ServletUtil.PREVTHIRTYDAYS, type);
    }

    /**
     * Creates a URL for the given SubBus's points
     * 
     */
    private static synchronized String _createSubBusGraphURL(SubBus subBus, String type) {
        String temp = "";

        if (TYPE_PF.equals(type))
        {
            temp += _getPointStr(subBus.getPowerFactorPointId());
            temp += _getPointStr(subBus.getEstimatedPowerFactorPointId());
        }
        else
        {
            temp += _getPointStr(subBus.getCurrentVarLoadPointID());
            temp += _getPointStr(subBus.getCurrentWattLoadPointID());
            temp += _getPointStr(subBus.getEstimatedVarLoadPointID());
        }

        if (temp.length() > 0)
            return "&pointid=" + temp.substring(1);
        else
            return null;
    }

    /**
     * Creates a URL for the given feeders points
     * 
     */
    private static synchronized String _createFeederGraphURL(Feeder feeder, String type) {
        String temp = "";
        if (TYPE_PF.equals(type)) {
            temp += _getPointStr(feeder.getPowerFactorPointID());
            temp += _getPointStr(feeder.getEstimatedPowerFactorPointID());
        } else {
            temp += _getPointStr(feeder.getCurrentVarLoadPointID());
            temp += _getPointStr(feeder.getCurrentWattLoadPointID());
            temp += _getPointStr(feeder.getEstimatedVarLoadPointID());
        }

        if (temp.length() > 0) {
            return "&pointid=" + temp.substring(1);
        } else {
            return null;
        }
    }

    /**
     * Decides if a given point id is valid or not.
     */
    private static boolean _isPointIDValid(Integer ptID)
    {
        return ptID != null && ptID.intValue() != CtiUtilities.NONE_ZERO_ID;
    }

    /**
     * Returns the ptID as a string with a preceding comma
     */
    private static String _getPointStr(Integer ptID)
    {
        if (_isPointIDValid(ptID))
            return "," + ptID;
        else
            return "";
    }
}
