#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h>

#include "msg_cmd.h"
#include "msg_pdata.h"
#include "ccmonitorpoint.h"
#include "cctwowaycbcpoints.h"
#include "ccoperationstats.h"
#include "ccConfirmationStats.h"
#include "ccoriginalparent.h"
#include "dbaccess.h"
#include "observe.h"
#include "cctypes.h"
#include "ctitime.h"
#include "ctidate.h"
#include "CapControlPao.h"

#include "PointResponse.h"
#include "PointResponseManager.h"

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

typedef enum
{
    Close = 0,
    Open = 1
} BankOperation;

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
class CtiCCCapBank : public RWCollectable, public CapControlPao
{

public:

  RWDECLARE_COLLECTABLE( CtiCCCapBank )

    CtiCCCapBank();
    CtiCCCapBank(Cti::RowReader& rdr);
    CtiCCCapBank(const CtiCCCapBank& cap);

    virtual ~CtiCCCapBank();

    LONG getParentId() const;
    BOOL getAlarmInhibitFlag() const;
    BOOL getControlInhibitFlag() const;
    LONG getMaxDailyOps() const;
    LONG getCurrentDailyOperations() const;
    BOOL getMaxOpsDisableFlag() const;
    const std::string& getOperationalState() const;
    const std::string& getControllerType() const;
    LONG getControlDeviceId() const;
    LONG getControlPointId() const;
    const std::string& getControlDeviceType() const;
    LONG getBankSize() const;
    const std::string& getTypeOfSwitch() const;
    const std::string& getSwitchManufacture() const;
    const std::string& getMapLocationId() const;
    LONG getRecloseDelay() const;
    FLOAT getControlOrder() const;
    FLOAT getTripOrder() const;
    FLOAT getCloseOrder() const;
    LONG getStatusPointId() const;
    LONG getControlStatus() const;
    LONG getOperationAnalogPointId() const;
    LONG getTotalOperations() const;
    const CtiTime& getLastStatusChangeTime() const;
    LONG getTagsControlStatus() const;
    BOOL getVerificationFlag() const;
    BOOL getPerformingVerificationFlag() const;
    BOOL getVerificationDoneFlag() const;
    BOOL getRetryOpenFailedFlag() const;
    BOOL getRetryCloseFailedFlag() const;
    BOOL getOvUvDisabledFlag() const;
    BOOL getMaxDailyOpsHitFlag() const;
    BOOL getControlStatusPartialFlag() const;
    BOOL getControlStatusSignificantFlag() const;
    BOOL getControlStatusAbnQualityFlag() const;
    BOOL getControlStatusFailFlag() const;
    BOOL getControlStatusCommFailFlag() const;
    BOOL getControlStatusNoControlFlag() const;
    BOOL getControlStatusUnSolicitedFlag() const;
    LONG getControlStatusQuality() const;
    BOOL getReEnableOvUvFlag() const;
    BOOL getLocalControlFlag() const;
    BOOL getControlRecentlySentFlag() const;
    BOOL getPorterRetFailFlag() const;

    BOOL getOvUvSituationFlag() const;
    LONG getUDPPort() const;
    const std::string& getIpAddress() const;
    LONG getReportedCBCLastControlReason() const;
    LONG getReportedCBCState() const;
    const CtiTime& getReportedCBCStateTime() const;
    BOOL getIgnoreFlag() const;
    LONG getIgnoredReason() const;
    const std::string& getBeforeVarsString() const;
    const std::string& getAfterVarsString() const;
    const std::string& getPercentChangeString() const;
    BOOL getSendAllCommandFlag() const;
    const CtiTime& getIgnoreReasonTimeUpdated() const;
    const CtiTime& getIgnoreIndicatorTimeUpdated() const;
    const CtiTime& getUnsolicitedChangeTimeUpdated() const;
    BOOL getUnsolicitedPendingFlag() const;
    const std::string&  getPartialPhaseInfo() const;
    LONG getActionId() const;


    int  getVCtrlIndex() const;
    int getAssumedOrigVerificationState() const;

    std::vector <CtiCCMonitorPointPtr>& getMonitorPoint() {return _monitorPoint;};

    CtiCCCapBank& setParentId(LONG parentId);
    CtiCCCapBank& setAlarmInhibitFlag(BOOL alarminhibit);
    CtiCCCapBank& setControlInhibitFlag(BOOL controlinhibit);
    CtiCCCapBank& setMaxDailyOperation(LONG maxdailyops);
    CtiCCCapBank& setCurrentDailyOperations(LONG operations);
    CtiCCCapBank& setMaxOpsDisableFlag(BOOL maxopsdisable);
    CtiCCCapBank& setDeviceClass(const std::string& deviceclass);
    CtiCCCapBank& setOperationalState(const std::string& operational);
    CtiCCCapBank& setOperationalState(int value);
    CtiCCCapBank& setControllerType(const std::string& controllertype);
    CtiCCCapBank& setControlDeviceId(LONG controldevice);
    CtiCCCapBank& setControlPointId(LONG controlpoint);
    CtiCCCapBank& setControlDeviceType(const std::string& controlDeviceType);
    CtiCCCapBank& setBankSize(LONG size);
    CtiCCCapBank& setTypeOfSwitch(const std::string& switchtype);
    CtiCCCapBank& setSwitchManufacture(const std::string& manufacture);
    CtiCCCapBank& setMapLocationId(const std::string& maplocation);
    CtiCCCapBank& setRecloseDelay(LONG reclose);
    CtiCCCapBank& setControlOrder(FLOAT order);
    CtiCCCapBank& setTripOrder(FLOAT order);
    CtiCCCapBank& setCloseOrder(FLOAT order);
    CtiCCCapBank& setStatusPointId(LONG statuspoint);
    CtiCCCapBank& setControlStatus(LONG status);
    CtiCCCapBank& setOperationAnalogPointId(LONG operationpoint);
    CtiCCCapBank& setTotalOperations(LONG operations);
    CtiCCCapBank& setTotalOperationsAndSendMsg(LONG operations, CtiMultiMsg_vec& pointChanges);
    CtiCCCapBank& setLastStatusChangeTime(const CtiTime& laststatuschangetime);
    CtiCCCapBank& setTagsControlStatus(LONG tags);

    CtiCCCapBank& setVerificationFlag(bool verificationFlag);
    CtiCCCapBank& setPerformingVerificationFlag(BOOL performingVerificationFlag);
    CtiCCCapBank& setVerificationDoneFlag(BOOL verificationDoneFlag);
    CtiCCCapBank& setRetryOpenFailedFlag(BOOL retryOpenFailedFlag);
    CtiCCCapBank& setRetryCloseFailedFlag(BOOL retryCloseFailedFlag);
    CtiCCCapBank& setOvUvDisabledFlag(BOOL ovUvDisabledFlag);
    CtiCCCapBank& setMaxDailyOpsHitFlag(BOOL flag);
    CtiCCCapBank& setControlStatusPartialFlag(BOOL flag);
    CtiCCCapBank& setControlStatusSignificantFlag(BOOL flag);
    CtiCCCapBank& setControlStatusAbnQualityFlag(BOOL flag);
    CtiCCCapBank& setControlStatusFailFlag(BOOL flag);
    CtiCCCapBank& setControlStatusCommFailFlag(BOOL flag);
    CtiCCCapBank& setControlStatusNoControlFlag(BOOL flag);
    CtiCCCapBank& setControlStatusUnSolicitedFlag(BOOL flag);
    CtiCCCapBank& setControlStatusQuality(CtiCCControlStatusQaulity quality, std::string partialPhaseInfo = "(none)");
    CtiCCCapBank& setReEnableOvUvFlag(BOOL flag);
    CtiCCCapBank& setLocalControlFlag(BOOL localControlFlag);
    CtiCCCapBank& setControlRecentlySentFlag(BOOL flag);
    CtiCCCapBank& setPorterRetFailFlag(BOOL flag);

    CtiCCCapBank& setOvUvSituationFlag(BOOL ovUvSituationFlag);
    CtiCCCapBank& setUDPPort(LONG value);
    CtiCCCapBank& setIpAddress(ULONG value);
    CtiCCCapBank& setReportedCBCLastControlReason(LONG value);
    CtiCCCapBank& setReportedCBCState(LONG value);
    CtiCCCapBank& setReportedCBCStateTime(const CtiTime& timestamp);
    CtiCCCapBank& setIgnoreFlag(BOOL flag);
    CtiCCCapBank& setIgnoredReason(LONG value);
    CtiCCCapBank& setBeforeVarsString(const std::string& before);
    CtiCCCapBank& setAfterVarsString(const std::string& after);
    CtiCCCapBank& setPercentChangeString(const std::string& percent);
    CtiCCCapBank& setSendAllCommandFlag(BOOL flag);
    CtiCCCapBank& setIgnoreReasonCounter(INT counter);
    CtiCCCapBank& setIgnoreReasonTimeUpdated(const CtiTime& timestamp);
    CtiCCCapBank& setIgnoreIndicatorTimeUpdated(const CtiTime& timestamp);
    CtiCCCapBank& setUnsolicitedChangeTimeUpdated(const CtiTime& timestamp);
    CtiCCCapBank& setUnsolicitedPendingFlag(BOOL flag);
    CtiCCCapBank& setPartialPhaseInfo(const std::string& info);
    CtiCCCapBank& setActionId(LONG actionId);

    CtiCCCapBank& setVCtrlIndex(int vCtrlIndex);
    CtiCCCapBank& setAssumedOrigVerificationState(int assumedOrigCapBankPos);
    CtiCCCapBank& setPreviousVerificationControlStatus(LONG status);
    BOOL updateVerificationState(void);

    std::string getControlStatusQualityString();
    std::string getIgnoreReasonText() const;
    CtiCCCapBank& initVerificationControlStatus();
    CtiCCCapBank& addAllCapBankPointsToMsg(std::set<long>& pointAddMsg);
    std::string getControlStatusText() const;
    BOOL isPendingStatus();
    BOOL isFailedStatus();
    BOOL isQuestionableStatus();
    BOOL isFailedOrQuestionableStatus();
    bool isControlDeviceTwoWay();
    int getPointIdByAttribute(const PointAttribute & attribute);

    Cti::CapControl::PointResponse getPointResponse(CtiCCMonitorPoint* point);
    std::vector<Cti::CapControl::PointResponse> getPointResponses();
    void addPointResponse(Cti::CapControl::PointResponse pointResponse);
    Cti::CapControl::PointResponseManager& getPointResponseManager();

    bool handlePointResponseDeltaChange(long pointId, double newDelta, bool staticDelta);
    bool updatePointResponseDelta(CtiCCMonitorPoint* point);
    bool updatePointResponsePreOpValue(long pointId, double value);

    bool isExpresscom();

    CtiCCOperationStats& getOperationStats();
    CtiCCConfirmationStats& getConfirmationStats();
    CtiCCOriginalParent& getOriginalParent();

    CtiCCCapBank* replicate() const;
    virtual int compareTo(const RWCollectable* right) const;

    CtiCCTwoWayPoints* getTwoWayPoints();

    BOOL isDirty() const;
    void setDirty(const bool flag);
    BOOL getInsertDynamicDataFlag() const;
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

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

    //Members inherited from RWCollectable
    void saveGuts(RWvostream& ) const;

    CtiCCCapBank& operator=(const CtiCCCapBank& right);

    /* Static Members */

    //Possible operational states
    static const std::string SwitchedOperationalState;
    static const std::string FixedOperationalState;
    static const std::string UninstalledState;
    static const std::string StandAloneState;

    //Possible states
    static const int Open;
    static const int Close;
    static const int OpenQuestionable;
    static const int CloseQuestionable;
    static const int OpenFail;
    static const int CloseFail;
    static const int OpenPending;
    static const int ClosePending;

    /*typedef enum
    {
        Normal = 0,
        Partial = 1,
        Significant = 2,
        AbnormalQuality = 3
    } CtiCCControlStatusQaulity;
    */

private:

    const std::string& convertOperationalState( int num );

    LONG _parentId; //feederId
    BOOL _alarminhibitflag;
    BOOL _controlinhibitflag;
    LONG _maxdailyops;
    LONG _currentdailyoperations;
    BOOL _maxopsdisableflag;
    std::string _operationalstate;
    std::string _controllertype;
    LONG _controldeviceid;
    LONG _controlpointid;
    std::string _controlDeviceType;
    LONG _banksize;
    std::string _typeofswitch;
    std::string _switchmanufacture;
    std::string _maplocationid;
    LONG _reclosedelay;
    FLOAT _controlorder;
    FLOAT _triporder;
    FLOAT _closeorder;
    LONG _statuspointid;
    LONG _controlstatus;
    LONG _operationanalogpointid;
    LONG _totaloperations;
    CtiTime _laststatuschangetime;
    LONG _tagscontrolstatus;
    LONG _originalfeederid;
    float _originalswitchingorder;
    float _originalcloseorder;
    float _originaltriporder;

    //verification info
    std::string _additionalFlags;
    LONG _verificationControlStatus;
    int _vCtrlIndex; //1,2, or 3
    BOOL _retryFlag;
    LONG _prevVerificationControlStatus;
    int _assumedOrigCapBankPos;
    bool _verificationFlag;
    BOOL _performingVerificationFlag;
    BOOL _verificationDoneFlag;
    BOOL _retryOpenFailedFlag;
    BOOL _retryCloseFailedFlag;
    BOOL _ovUvDisabledFlag;
    BOOL _maxDailyOpsHitFlag;
    BOOL _controlStatusPartialFlag;
    BOOL _controlStatusSignificantFlag;
    BOOL _controlStatusAbnQualityFlag;
    BOOL _controlStatusFailFlag;
    BOOL _controlStatusCommFailFlag;
    BOOL _controlStatusNoControlFlag;
    BOOL _controlStatusUnSolicitedFlag;
    BOOL _ovuvSituationFlag;
    BOOL _reEnableOvUvFlag;
    BOOL _localControlFlag;
    BOOL _controlRecentlySentFlag;
    BOOL _porterRetFailFlag;
    BOOL _unsolicitedPendingFlag;

    CtiCCTwoWayPoints* _twoWayPoints;
    std::string _ipAddress;
    LONG _udpPortNumber;
    LONG _reportedCBCLastControlReason;
    LONG _reportedCBCState;
    CtiTime _reportedCBCStateTime;
    std::string _partialPhaseInfo;

    BOOL _ignoreFlag;
    LONG _ignoreReason;
    std::string _sBeforeVars;
    std::string _sAfterVars;
    std::string _sPercentChange;
    LONG _controlStatusQuality;

    BOOL _sendAllCommandFlag;
    CtiTime _ignoreReasonTimeUpdated;
    CtiTime _ignoreIndicatorTimeUpdated;
    CtiTime _unsolicitedChangeTimeUpdated;

    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;
    CtiCCOriginalParent _originalParent;

    LONG _actionId;

    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    std::vector <CtiCCMonitorPoint*>  _monitorPoint; //normally this is just one, but if display order is > 1 then we have more
                                                    // than one monitor point attached to a capbank!!!

    Cti::CapControl::PointResponseManager _pointResponseManager;

    void restore(Cti::RowReader& rdr);
};

typedef CtiCCCapBank* CtiCCCapBankPtr;

typedef std::list<CtiCCCapBankPtr> CapBankList;
