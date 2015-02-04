/**
 * MR_EASoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface MR_EASoap_PortType extends java.rmi.Remote {

    /**
     * EA Pings URL of MR to see if it is alive. Returns errorObject(s)
     * as necessary to communicate application status.(Required)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException;

    /**
     * EA requests list of methods supported by MR. (Required)
     */
    public java.lang.String[] getMethods() throws java.rmi.RemoteException;

    /**
     * The client requests from the server a list of names of domains
     * supported by the server.  This method is used, along with the GetDomainMembers
     * method to enable systems to exchange information about application-specific
     * or installation-specific lists of information, such as the lists of
     * counties for this installation or the list of serviceStatusCodes used
     * by the server. (Optional)
     */
    public java.lang.String[] getDomainNames() throws java.rmi.RemoteException;

    /**
     * The client requests from the server the members of a specific
     * domain of information, identified by the domainName parameter, which
     * are supported by the server.  This method is used, along with the
     * GetDomainNames method to enable systems to exchange information about
     * application-specific or installation-specific lists of information,
     * such as the lists of counties for this installation or the list of
     * serviceStatusCodes used by the server. (Optional)
     */
    public com.cannontech.multispeak.deploy.service.DomainMember[] getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException;

    /**
     * Returns all meters that have AMR.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getAMRSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all meters that support AMR and that have been modified
     * since the specified sessionID.  The calling parameter previousSessionID
     * should carry the session identifier for the last session of data that
     * the client has successfully received.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getModifiedAMRMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns Reading Data for All Meters Given a Date Range. The
     * calling parameter lastReceived is included so that large sets of data
     * can be returned in manageable blocks.  lastReceived should carry an
     * empty string the first time in a session that this method is invoked.
     * When multiple calls to this method are required to obtain all of the
     * data, the lastReceived should carry in subsequent calls the objectID
     * of the data instance noted by the server as being the lastSent. (Recommended)
     */
    public com.cannontech.multispeak.deploy.service.MeterRead[] getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all required Reading Data for a given MeterNo and Date
     * Range.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.MeterRead[] getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Returns Most Recent Meter Reading Data for all AMR capable
     * meters.  The calling parameter lastReceived is included so that large
     * sets of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.MeterRead[] getLatestReadings(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns Most Recent Meter Reading Data for a given MeterNo.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the requested Reading Data for All Meters given Unit
     * Of Measure(UOM) and Date Range. The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent. (Optional)
     */
    public com.cannontech.multispeak.deploy.service.MeterRead[] getReadingsByUOMAndDate(java.lang.String uomData, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns History Log Data for a given MeterNo and Date Range.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.HistoryLog[] getHistoryLogByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Returns History Log Data for a all Meters Given a Date Range.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.HistoryLog[] getHistoryLogsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns History Log Data for a given MeterNo, eventCode and
     * Date Range.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.HistoryLog[] getHistoryLogsByMeterNoAndEventCode(java.lang.String meterNo, com.cannontech.multispeak.deploy.service.EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Returns History Log Data for a all Meters Given the eventCode
     * and a Date Range.  The calling parameter lastReceived is included
     * so that large sets of data can be returned in manageable blocks. 
     * lastReceived should carry an empty string the first time in a session
     * that this method is invoked.  When multiple calls to this method are
     * required to obtain all of the data, the lastReceived should carry
     * in subsequent calls the objectID of the data instance noted by the
     * server as being the lastSent.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.HistoryLog[] getHistoryLogsByDateAndEventCode(com.cannontech.multispeak.deploy.service.EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the most recent reading data for a given meterNo and
     * reading type.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock getLatestReadingByMeterNoAndType(java.lang.String meterNo, java.lang.String readingType) throws java.rmi.RemoteException;

    /**
     * Returns the most recent reading data for a given reading type.The
     * calling parameter lastReceived is included so that large sets of data
     * can be returned in manageable blocks.  lastReceived should carry an
     * empty string the first time in a session that this method is invoked.
     * When multiple calls to this method are required to obtain all of the
     * data, the lastReceived should carry in subsequent calls the objectID
     * of the data instance noted by the server as being the lastSent.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getLatestReadingByType(java.lang.String readingType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns readings all meters given the date range and reading
     * type desired.  The calling parameter lastReceived is included so that
     * large sets of data can be returned in manageable blocks.  lastReceived
     * should carry an empty string the first time in a session that this
     * method is invoked.  When multiple calls to this method are required
     * to obtain all of the data, the lastReceived should carry in subsequent
     * calls the objectID of the data instance noted by the server as being
     * the lastSent.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getReadingsByDateAndType(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String readingType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all reading types supported by the AMR system.(Recommended)
     */
    public java.lang.String[] getSupportedReadingTypes() throws java.rmi.RemoteException;

    /**
     * Returns readings for a given meter for a specific date range
     * and reading type desired.  The calling parameter lastReceived is included
     * so that large sets of data can be returned in manageable blocks. 
     * lastReceived should carry an empty string the first time in a session
     * that this method is invoked.  When multiple calls to this method are
     * required to obtain all of the data, the lastReceived should carry
     * in subsequent calls the objectID of the data instance noted by the
     * server as being the lastSent.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getReadingsByMeterNoAndType(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String readingType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Request MR to perform a meter reading for specific meter numbers
     * and reading type. MR returns information about failed transactions
     * using an array of errorObjects.  The MR subsequently returns the data
     * collected by publishing formattedBlocks to the EA at the URL specified
     * in the responseURL parameter.The transactionID parameter is supplied
     * to link the returned formattedBlock(s) with this Initiate request.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateMeterReadByMeterNoAndType(java.lang.String meterNo, java.lang.String responseURL, java.lang.String readingType, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * EA requests MR to to perform a meter reading for all meters
     * down line of the circuit element supplied using the calling parameters
     * objectName and nounType and containing the phasing supplied in the
     * calling parameter phaseCode. The MR subsequently returns the data
     * collected by publishing formattedBlocks to the EA at the URL specified
     * in the responseURL parameter.  The transactionID parameter is supplied
     * to link the returned formattedBlock(s) with this Initiate request.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateMeterReadByObject(java.lang.String objectName, java.lang.String nounType, com.cannontech.multispeak.deploy.service.PhaseCd phaseCode, java.lang.String responseURL, java.lang.String transactionID) throws java.rmi.RemoteException;
}
