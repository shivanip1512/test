package com.cannontech.multispeak.service.v5;

import java.util.Date;
import java.util.List;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.util.BadServerResponseException;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.enumerations.QualityDescriptionKind;
import com.cannontech.msp.beans.v5.multispeak.LoadManagementEvent;
import com.cannontech.msp.beans.v5.multispeak.SCADAAnalog;
import com.cannontech.msp.beans.v5.multispeak.SubstationLoadControlStatus;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.db.v5.MspLoadControl;

public interface MultispeakLMService {
    public String buildFdrMultispeakLMTranslation(String objectId);

    /**
     * Return the Yukon Point Quality value that maps to the MultiSpeak QualityDescription value.
     * PointQuality.NORMAL is the default value returned when no other value is found.
     */
    public PointQuality getPointQuality(QualityDescriptionKind qualityDescription);

    /**
     * Returns a PointData object for the MultiSpeak scadaAnalog object.
     */
    public PointData buildPointData(int pointId, SCADAAnalog scadaAnalog, String userName);

    /**
     * Writes a MultiSpeak scadaAnalog value to dispatch as a PointData objec t..
     */
    public ErrorObject writeAnalogPointData(SCADAAnalog scadaAnalog, LiteYukonUser liteYukonUser);

    /**
     * Returns an array of SubstationLoadControlStatus values.
     * For each defined MspLMInterfaceMappings, a SubstationLoadControlStatus object is created.
     * The values are grouped by SubstationName, and contain a list of
     * SubstationLoadControlStatusControlledItemsControlItems for each Strategy on that Substation.
     * The count for all devices in program/scenario that maps to the Substation/Strategy is included.
     * The controlled count for all devices in program/scenario that maps to the Substation/Strategy is
     * included.
     * * NOTE: The controlled count is only the number of devices that were active, not opted out, for the
     * date range supplied. We currently do not have a way to get an accurate control count without
     * 2way load control switches.
     */
    public List<SubstationLoadControlStatus> getActiveLoadControlStatus() throws ConnectionException, NotFoundException;

    /**
     * Build a Yukon MspLoadControl object from the loadManagementEvent.
     * If a startDate is not supplied, now will be used.
     * If a duration is not supplied, no stop time will be calculated (null will be used).
     * The substation name and strategy name values are used to lookup and return the
     * corresponding MspLMInterfaceMapping values. If combination not found, ErrorObject is returned for each
     * occurrence.
     */
    public List<ErrorObject> buildMspLoadControl(LoadManagementEvent loadManagementEvent,
            MspLoadControl mspLoadControl, MultispeakVendor vendor);

    /**
     * Start (ControlEventType.INITIATE) or Stop (ControlEventType.RESTORE) control for the "controllable"
     * paobjectId for the MspLoadControl object.
     * Returns an ErrorObject for control operations that failed.
     */
    public ErrorObject control(MspLoadControl mspLoadControl, LiteYukonUser liteYukonUser);

    /**
     * Start control for the programName and date range provided.
     * If the startTime is null or less than "now", Start Now will be used.
     * If the stopTime is null, then Never Stop will be used.
     * 
     * @throws NotAuthorizedException when the liteYukonUser does not have access to the program
     * @throws NotFoundException when the programName is not found in Yukon
     * @throws TimeoutException when the control operation times out.
     * @throws BadServerResponseException
     */
    public ProgramStatus startControlByProgramName(String programName, Date startTime, Date stopTime,
            LiteYukonUser liteYukonUser) throws NotAuthorizedException, NotFoundException, TimeoutException,
            BadServerResponseException;

    /**
     * Stop control for the programName and date range provided.
     * If the stopTime is null, then Stop Now will be used
     * 
     * @throws NotAuthorizedException when the liteYukonUser does not have access to the program
     * @throws NotFoundException when the programName is not found in Yukon
     * @throws TimeoutException when the control operation times out.
     * @throws BadServerResponseException
     */
    public ProgramStatus stopControlByProgramName(String programName, Date stopTime, LiteYukonUser liteYukonUser)
            throws NotAuthorizedException, NotFoundException, TimeoutException, BadServerResponseException;

    /**
     * Start control for the scenarioName and date range provided.
     * If the startTime is null or less than "now", Start Now will be used.
     * If the stopTime is null, then Never Stop will be used.
     * 
     * @throws NotAuthorizedException when the liteYukonUser does not have access to one of the programs for
     *         the scenario
     * @throws NotFoundException when the scenarioName is not found in Yukon
     * @throws TimeoutException when the control operation times out.
     * @throws BadServerResponseException
     */
    public ScenarioStatus startControlByControlScenario(String scenarioName, Date startTime, Date stopTime,
            LiteYukonUser liteYukonUser) throws NotAuthorizedException, NotFoundException, TimeoutException,
            BadServerResponseException;

    /**
     * Stop control for the scenarioName and date range provided. If the stopTime is null, then Stop Now will
     * be used.
     * 
     * @throws NotAuthorizedException when the liteYukonUser does not have access to the program
     * @throws NotFoundException when the programName is not found in Yukon
     * @throws TimeoutException when the control operation times out.
     * @throws BadServerResponseException
     */
    public ScenarioStatus stopControlByControlScenario(String scenarioName, Date stopTime, LiteYukonUser liteYukonUser)
            throws NotAuthorizedException, NotFoundException, TimeoutException, BadServerResponseException;
}
