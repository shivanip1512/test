#pragma once

#include "ccmonitorpoint.h"
#include "cctwowaycbcpoints.h"
#include "ccoriginalparent.h"
#include "cctypes.h"
#include "ctitime.h"
#include "ctidate.h"
#include "CapControlPao.h"
#include "PointResponse.h"
#include "PointResponseManager.h"
#include "pointdefs.h"
#include "cbcHeartbeatPolicy.h"
#include "EventLogEntry.h"

#include <boost/scoped_ptr.hpp>

namespace Cti {
namespace Database {
    class DatabaseConnection;
}
}

typedef enum
{
    CC_Normal = 0,
    CC_Partial = 1,
    CC_Significant = 2,
    CC_AbnormalQuality = 3,
    CC_Fail = 4,
    CC_CommFail = 5,
    CC_NoControl = 6,
    CC_UnSolicited = 7

}CtiCCControlStatusQaulity;


typedef enum
{
    CC_Remote = 0,
    CC_Local = 1,
    CC_OvUv = 2,
    CC_NeutralFault = 3,
    CC_Scheduled = 4,
    CC_Digital = 5,
    CC_Analog = 6,
    CC_Temperature = 7
} CtiCCLastControlReason;

namespace capcontrol
{
    enum CapbankOperationalStates
    {
        FIXED,
        SWITCHED,
        STANDALONE,
        UNINSTALLED
    };
}
class CtiCCCapBank : public CapControlPao
{
public:
    DECLARE_COLLECTABLE( CtiCCCapBank );

    CtiCCCapBank();
    CtiCCCapBank(Cti::RowReader& rdr);
    CtiCCCapBank(const CtiCCCapBank& cap);

    virtual ~CtiCCCapBank();

    long getParentId() const;
    bool getAlarmInhibitFlag() const;
    bool getControlInhibitFlag() const;
    long getMaxDailyOps() const;
    long getCurrentDailyOperations() const;
    bool getMaxOpsDisableFlag() const;
    const std::string& getOperationalState() const;
    const std::string& getControllerType() const;
    long getControlDeviceId() const;
    long getControlPointId() const;
    LitePoint getControlPoint() const;
    std::string getControlDeviceType() const;
    long getBankSize() const;
    const std::string& getTypeOfSwitch() const;
    const std::string& getSwitchManufacture() const;
    const std::string& getMapLocationId() const;
    long getRecloseDelay() const;
    float getControlOrder() const;
    float getTripOrder() const;
    float getCloseOrder() const;
    long getStatusPointId() const;
    long getControlStatus() const;
    long getOperationAnalogPointId() const;
    long getTotalOperations() const;
    const CtiTime& getLastStatusChangeTime() const;
    long getTagsControlStatus() const;
    bool getVerificationFlag() const;
    bool getPerformingVerificationFlag() const;
    bool getVerificationDoneFlag() const;
    bool getRetryOpenFailedFlag() const;
    bool getRetryCloseFailedFlag() const;
    bool getOvUvDisabledFlag() const;
    bool getMaxDailyOpsHitFlag() const;
    bool getControlStatusPartialFlag() const;
    bool getControlStatusSignificantFlag() const;
    bool getControlStatusAbnQualityFlag() const;
    bool getControlStatusFailFlag() const;
    bool getControlStatusCommFailFlag() const;
    bool getControlStatusNoControlFlag() const;
    bool getControlStatusUnSolicitedFlag() const;
    long getControlStatusQuality() const;
    bool getReEnableOvUvFlag() const;
    bool getLocalControlFlag() const;
    bool getControlRecentlySentFlag() const;
    bool getPorterRetFailFlag() const;

    bool getOvUvSituationFlag() const;
    long getUDPPort() const;
    const std::string& getIpAddress() const;
    long getReportedCBCLastControlReason() const;
    long getReportedCBCState() const;
    const CtiTime& getReportedCBCStateTime() const;
    bool getIgnoreFlag() const;
    long getIgnoredReason() const;
    const std::string& getBeforeVarsString() const;
    const std::string& getAfterVarsString() const;
    const std::string& getPercentChangeString() const;
    const CtiTime& getIgnoreIndicatorTimeUpdated() const;
    const CtiTime& getUnsolicitedChangeTimeUpdated() const;
    bool getUnsolicitedPendingFlag() const;
    const std::string&  getPartialPhaseInfo() const;
    long getActionId() const;


    int  getVCtrlIndex() const;
    int getAssumedOrigVerificationState() const;
    void setSelectedForVerificationFlag(bool individualVerificationFlag);
    bool isSelectedForVerification( ) const;

    std::vector <CtiCCMonitorPointPtr>& getMonitorPoint() {return _monitorPoint;};
    bool addMonitorPoint(CtiCCMonitorPointPtr monPoint);
    bool updateExistingMonitorPoint(CtiCCMonitorPointPtr monPoint);

    void setParentId(long parentId);
    void setAlarmInhibitFlag(bool alarminhibit);
    void setControlInhibitFlag(bool controlinhibit);
    void setMaxDailyOperation(long maxdailyops);
    void setCurrentDailyOperations(long operations);
    void setMaxOpsDisableFlag(bool maxopsdisable);
    void setDeviceClass(const std::string& deviceclass);
    void setOperationalState(const std::string& operational);
    void setOperationalState(int value);
    void setControllerType(const std::string& controllertype);
    void setBankSize(long size);
    void setTypeOfSwitch(const std::string& switchtype);
    void setSwitchManufacture(const std::string& manufacture);
    void setMapLocationId(const std::string& maplocation);
    void setRecloseDelay(long reclose);
    void setControlOrder(float order);
    void setTripOrder(float order);
    void setCloseOrder(float order);
    void setStatusPointId(long statuspoint);
    void setControlStatus(long status);
    void setOperationAnalogPointId(long operationpoint);
    void setTotalOperations(long operations);
    void setTotalOperationsAndSendMsg(long operations, CtiMultiMsg_vec& pointChanges);
    void setLastStatusChangeTime(const CtiTime& laststatuschangetime);
    void setTagsControlStatus(long tags);

    void setVerificationFlag(bool verificationFlag);
    void setPerformingVerificationFlag(bool performingVerificationFlag);
    void setVerificationDoneFlag(bool verificationDoneFlag);
    void setRetryOpenFailedFlag(bool retryOpenFailedFlag);
    void setRetryCloseFailedFlag(bool retryCloseFailedFlag);
    void setOvUvDisabledFlag(bool ovUvDisabledFlag);
    void setMaxDailyOpsHitFlag(bool flag);
    void setControlStatusPartialFlag(bool flag);
    void setControlStatusSignificantFlag(bool flag);
    void setControlStatusAbnQualityFlag(bool flag);
    void setControlStatusFailFlag(bool flag);
    void setControlStatusCommFailFlag(bool flag);
    void setControlStatusNoControlFlag(bool flag);
    void setControlStatusUnSolicitedFlag(bool flag);
    void setControlStatusQuality(CtiCCControlStatusQaulity quality, std::string partialPhaseInfo = "(none)");
    void setReEnableOvUvFlag(bool flag);
    void setLocalControlFlag(bool localControlFlag);
    void setControlRecentlySentFlag(bool flag);
    void setPorterRetFailFlag(bool flag);

    void setOvUvSituationFlag(bool ovUvSituationFlag);
    void setUDPPort(long value);
    void setIpAddress(unsigned long value);
    void setReportedCBCLastControlReason(long value);
    void setReportedCBCState(long value);
    void setReportedCBCStateTime(const CtiTime& timestamp);
    void setIgnoreFlag(bool flag);
    void setIgnoredReason(long value);
    void setBeforeVarsString(const std::string& before);
    void setAfterVarsString(const std::string& after);
    void setPercentChangeString(const std::string& percent);
    void setIgnoreIndicatorTimeUpdated(const CtiTime& timestamp);
    void setUnsolicitedChangeTimeUpdated(const CtiTime& timestamp);
    void setUnsolicitedPendingFlag(bool flag);
    void setPartialPhaseInfo(const std::string& info);
    void setActionId(long actionId);

    void setVCtrlIndex(int vCtrlIndex);
    void setAssumedOrigVerificationState(int assumedOrigCapBankPos);
    void setPreviousVerificationControlStatus(long status);
    bool updateVerificationState(void);

    std::string getControlStatusQualityString();
    void initVerificationControlStatus();
    void addAllCapBankPointsToMsg(std::set<long>& pointAddMsg);
    std::string getControlStatusText() const;
    bool isPendingStatus();
    bool isFailedStatus();
    bool isQuestionableStatus();
    bool isFailedOrQuestionableStatus();
    bool isControlDeviceTwoWay() const;
    long getPointIdByAttribute( const Attribute & attribute );

    Cti::CapControl::PointResponse getPointResponse(const CtiCCMonitorPoint & point);
    std::vector<Cti::CapControl::PointResponse> getPointResponses();
    void addPointResponse(Cti::CapControl::PointResponse pointResponse);

    bool handlePointResponseDeltaChange(long pointId, double newDelta, bool staticDelta);
    bool updatePointResponseDelta(const CtiCCMonitorPoint & point, double maxDelta);
    bool updatePointResponsePreOpValue(long pointId, double value);

    bool isExpresscom();

    CtiCCOriginalParent& getOriginalParent();
    const CtiCCOriginalParent& getOriginalParent() const;

    CtiCCCapBank* replicate() const;
    virtual int compareTo(const CtiCCCapBank* right) const;

    CtiCCTwoWayPoints & getTwoWayPoints();
    const CtiCCTwoWayPoints & getTwoWayPoints() const;

    bool isDirty() const;
    void setDirty(const bool flag);
    bool getInsertDynamicDataFlag() const;
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    bool checkMaxDailyOpCountExceeded( CtiMultiMsg_vec & pointChanges, Cti::CapControl::EventLogEntries & events );

    /**
     * Uses the passed in connection to dump the data.
    */
    void dumpDynamicPointResponseData(Cti::Database::DatabaseConnection& conn);
    /**
     * This will create its own connections to the db to update and
     * insert.
     *
     */
    void dumpDynamicPointResponseData();

    void setDynamicData(Cti::RowReader& rdr);

    CtiCCCapBank& operator=(const CtiCCCapBank& right);

    bool isSwitched() const;

    /* Static Members */

    //Possible operational states
    static const std::string SwitchedOperationalState;
    static const std::string FixedOperationalState;
    static const std::string UninstalledState;
    static const std::string StandAloneState;

    //Possible states
    static constexpr int Open               = STATEZERO;
    static constexpr int Close              = STATEONE;
    static constexpr int OpenQuestionable   = STATETWO;
    static constexpr int CloseQuestionable  = STATETHREE;
    static constexpr int OpenFail           = STATEFOUR;
    static constexpr int CloseFail          = STATEFIVE;
    static constexpr int OpenPending        = STATESIX;
    static constexpr int ClosePending       = STATESEVEN;

//private:


    void createCbc( const long ID, const std::string & Type );



    void handlePointData( const CtiPointDataMsg & message );

    bool submitHeartbeatCommands( Cti::CapControl::Policy::Actions & actions );

    void executeSendHeartbeat( const std::string & user );
    void executeStopHeartbeat( const std::string & user );

    void loadAttributes( AttributeService * service );

    struct Heartbeat
    {
        double  _period;
        long    _value;

        std::string _mode;

        std::unique_ptr<Cti::CapControl::CbcHeartbeatPolicy>    _policy;

        CtiTime _sendTime;

        Heartbeat();

        void initialize( CtiCCCapBank * bank );

        bool isTimeToSend( const CtiTime & now );
    }
    heartbeat;

    bool supportsHeartbeat() const;

private:

    bool assignSpecializedPoint( const long pointID, const long pointOffset, const CtiPointType_t pointType ) override;

    const std::string& convertOperationalState( int num );

    long _parentId; //feederId
    bool _alarminhibitflag;
    bool _controlinhibitflag;
    long _maxdailyops;
    long _currentdailyoperations;
    bool _maxopsdisableflag;
    std::string _operationalstate;
    std::string _controllertype;
    long _controldeviceid;
    std::string _controlDeviceType;
    long _banksize;
    std::string _typeofswitch;
    std::string _switchmanufacture;
    std::string _maplocationid;
    long _reclosedelay;
    float _controlorder;
    float _triporder;
    float _closeorder;
    long _statuspointid;
    long _controlstatus;
    long _operationanalogpointid;
    long _totaloperations;
    CtiTime _laststatuschangetime;
    long _tagscontrolstatus;

    //verification info
    long _verificationControlStatus;
    int _vCtrlIndex; //1,2, or 3
    bool _selectedForVerificationFlag;
    bool _retryFlag;
    long _prevVerificationControlStatus;
    int _assumedOrigCapBankPos;
    bool _verificationFlag;
    bool _performingVerificationFlag;
    bool _verificationDoneFlag;
    bool _retryOpenFailedFlag;
    bool _retryCloseFailedFlag;
    bool _ovUvDisabledFlag;
    bool _maxDailyOpsHitFlag;
    bool _controlStatusPartialFlag;
    bool _controlStatusSignificantFlag;
    bool _controlStatusAbnQualityFlag;
    bool _controlStatusFailFlag;
    bool _controlStatusCommFailFlag;
    bool _controlStatusNoControlFlag;
    bool _controlStatusUnSolicitedFlag;
    bool _ovuvSituationFlag;
    bool _reEnableOvUvFlag;
    bool _localControlFlag;
    bool _controlRecentlySentFlag;
    bool _porterRetFailFlag;
    bool _unsolicitedPendingFlag;

    boost::scoped_ptr<CtiCCTwoWayPoints>    _twoWayPoints;

    std::string _ipAddress;
    long _udpPortNumber;
    long _reportedCBCLastControlReason;
    long _reportedCBCState;
    CtiTime _reportedCBCStateTime;
    std::string _partialPhaseInfo;

    bool _ignoreFlag;
    long _ignoreReason;
    std::string _sBeforeVars;
    std::string _sAfterVars;
    std::string _sPercentChange;
    long _controlStatusQuality;

    CtiTime _ignoreIndicatorTimeUpdated;
    CtiTime _unsolicitedChangeTimeUpdated;

    CtiCCOriginalParent _originalParent;

    long _actionId;

    //don't stream
    bool _insertDynamicDataFlag;
    bool _dirty;

    std::vector <CtiCCMonitorPointPtr>  _monitorPoint; //normally this is just one, but if display order is > 1 then we have more
                                                    // than one monitor point attached to a capbank!!!

    Cti::CapControl::PointResponseManager _pointResponseManager;

    void restore(Cti::RowReader& rdr);
    void getSpecializedPointRegistrationIds( std::set<long> & registrationIDs ) const override;

};

typedef CtiCCCapBank* CtiCCCapBankPtr;

typedef std::list<CtiCCCapBankPtr> CapBankList;
