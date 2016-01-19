#pragma once

#include "ccmonitorpoint.h"
#include "cctwowaycbcpoints.h"
#include "ccoperationstats.h"
#include "ccConfirmationStats.h"
#include "ccoriginalparent.h"
#include "cctypes.h"
#include "ctitime.h"
#include "ctidate.h"
#include "CapControlPao.h"
#include "PointResponse.h"
#include "PointResponseManager.h"

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

public:
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
    const std::string& getControlDeviceType() const;
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
    CtiCCCapBank& setSelectedForVerificationFlag(bool individualVerificationFlag);
    bool CtiCCCapBank::isSelectedForVerification( ) const;

    std::vector <CtiCCMonitorPointPtr>& getMonitorPoint() {return _monitorPoint;};
    bool addMonitorPoint(CtiCCMonitorPointPtr monPoint);
    bool updateExistingMonitorPoint(CtiCCMonitorPointPtr monPoint);

    CtiCCCapBank& setParentId(long parentId);
    CtiCCCapBank& setAlarmInhibitFlag(bool alarminhibit);
    CtiCCCapBank& setControlInhibitFlag(bool controlinhibit);
    CtiCCCapBank& setMaxDailyOperation(long maxdailyops);
    CtiCCCapBank& setCurrentDailyOperations(long operations);
    CtiCCCapBank& setMaxOpsDisableFlag(bool maxopsdisable);
    CtiCCCapBank& setDeviceClass(const std::string& deviceclass);
    CtiCCCapBank& setOperationalState(const std::string& operational);
    CtiCCCapBank& setOperationalState(int value);
    CtiCCCapBank& setControllerType(const std::string& controllertype);
    CtiCCCapBank& setControlDeviceId(long controldevice);
    CtiCCCapBank& setControlPointId(long controlpoint);
    CtiCCCapBank& setControlDeviceType(const std::string& controlDeviceType);
    CtiCCCapBank& setBankSize(long size);
    CtiCCCapBank& setTypeOfSwitch(const std::string& switchtype);
    CtiCCCapBank& setSwitchManufacture(const std::string& manufacture);
    CtiCCCapBank& setMapLocationId(const std::string& maplocation);
    CtiCCCapBank& setRecloseDelay(long reclose);
    CtiCCCapBank& setControlOrder(float order);
    CtiCCCapBank& setTripOrder(float order);
    CtiCCCapBank& setCloseOrder(float order);
    CtiCCCapBank& setStatusPointId(long statuspoint);
    CtiCCCapBank& setControlStatus(long status);
    CtiCCCapBank& setOperationAnalogPointId(long operationpoint);
    CtiCCCapBank& setTotalOperations(long operations);
    CtiCCCapBank& setTotalOperationsAndSendMsg(long operations, CtiMultiMsg_vec& pointChanges);
    CtiCCCapBank& setLastStatusChangeTime(const CtiTime& laststatuschangetime);
    CtiCCCapBank& setTagsControlStatus(long tags);

    CtiCCCapBank& setVerificationFlag(bool verificationFlag);
    CtiCCCapBank& setPerformingVerificationFlag(bool performingVerificationFlag);
    CtiCCCapBank& setVerificationDoneFlag(bool verificationDoneFlag);
    CtiCCCapBank& setRetryOpenFailedFlag(bool retryOpenFailedFlag);
    CtiCCCapBank& setRetryCloseFailedFlag(bool retryCloseFailedFlag);
    CtiCCCapBank& setOvUvDisabledFlag(bool ovUvDisabledFlag);
    CtiCCCapBank& setMaxDailyOpsHitFlag(bool flag);
    CtiCCCapBank& setControlStatusPartialFlag(bool flag);
    CtiCCCapBank& setControlStatusSignificantFlag(bool flag);
    CtiCCCapBank& setControlStatusAbnQualityFlag(bool flag);
    CtiCCCapBank& setControlStatusFailFlag(bool flag);
    CtiCCCapBank& setControlStatusCommFailFlag(bool flag);
    CtiCCCapBank& setControlStatusNoControlFlag(bool flag);
    CtiCCCapBank& setControlStatusUnSolicitedFlag(bool flag);
    CtiCCCapBank& setControlStatusQuality(CtiCCControlStatusQaulity quality, std::string partialPhaseInfo = "(none)");
    CtiCCCapBank& setReEnableOvUvFlag(bool flag);
    CtiCCCapBank& setLocalControlFlag(bool localControlFlag);
    CtiCCCapBank& setControlRecentlySentFlag(bool flag);
    CtiCCCapBank& setPorterRetFailFlag(bool flag);

    CtiCCCapBank& setOvUvSituationFlag(bool ovUvSituationFlag);
    CtiCCCapBank& setUDPPort(long value);
    CtiCCCapBank& setIpAddress(unsigned long value);
    CtiCCCapBank& setReportedCBCLastControlReason(long value);
    CtiCCCapBank& setReportedCBCState(long value);
    CtiCCCapBank& setReportedCBCStateTime(const CtiTime& timestamp);
    CtiCCCapBank& setIgnoreFlag(bool flag);
    CtiCCCapBank& setIgnoredReason(long value);
    CtiCCCapBank& setBeforeVarsString(const std::string& before);
    CtiCCCapBank& setAfterVarsString(const std::string& after);
    CtiCCCapBank& setPercentChangeString(const std::string& percent);
    CtiCCCapBank& setIgnoreIndicatorTimeUpdated(const CtiTime& timestamp);
    CtiCCCapBank& setUnsolicitedChangeTimeUpdated(const CtiTime& timestamp);
    CtiCCCapBank& setUnsolicitedPendingFlag(bool flag);
    CtiCCCapBank& setPartialPhaseInfo(const std::string& info);
    CtiCCCapBank& setActionId(long actionId);

    CtiCCCapBank& setVCtrlIndex(int vCtrlIndex);
    CtiCCCapBank& setAssumedOrigVerificationState(int assumedOrigCapBankPos);
    CtiCCCapBank& setPreviousVerificationControlStatus(long status);
    bool updateVerificationState(void);

    std::string getControlStatusQualityString();
    CtiCCCapBank& initVerificationControlStatus();
    CtiCCCapBank& addAllCapBankPointsToMsg(std::set<long>& pointAddMsg);
    std::string getControlStatusText() const;
    bool isPendingStatus();
    bool isFailedStatus();
    bool isQuestionableStatus();
    bool isFailedOrQuestionableStatus();
    bool isControlDeviceTwoWay();
    int getPointIdByAttribute(const PointAttribute & attribute);

    Cti::CapControl::PointResponse getPointResponse(const CtiCCMonitorPoint & point);
    std::vector<Cti::CapControl::PointResponse> getPointResponses();
    void addPointResponse(Cti::CapControl::PointResponse pointResponse);
    Cti::CapControl::PointResponseManager& getPointResponseManager();

    bool handlePointResponseDeltaChange(long pointId, double newDelta, bool staticDelta);
    bool updatePointResponseDelta(const CtiCCMonitorPoint & point);
    bool updatePointResponsePreOpValue(long pointId, double value);

    bool isExpresscom();

    CtiCCOperationStats& getOperationStats();
    CtiCCConfirmationStats& getConfirmationStats();
    CtiCCOriginalParent& getOriginalParent();
    const CtiCCOriginalParent& getOriginalParent() const;

    CtiCCCapBank* replicate() const;
    virtual int compareTo(const CtiCCCapBank* right) const;

    CtiCCTwoWayPoints & getTwoWayPoints();

    bool isDirty() const;
    void setDirty(const bool flag);
    bool getInsertDynamicDataFlag() const;
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

    long _parentId; //feederId
    bool _alarminhibitflag;
    bool _controlinhibitflag;
    long _maxdailyops;
    long _currentdailyoperations;
    bool _maxopsdisableflag;
    std::string _operationalstate;
    std::string _controllertype;
    long _controldeviceid;
    long _controlpointid;
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
    long _originalfeederid;
    float _originalswitchingorder;
    float _originalcloseorder;
    float _originaltriporder;

    //verification info
    std::string _additionalFlags;
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

    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;
    CtiCCOriginalParent _originalParent;

    long _actionId;

    //don't stream
    bool _insertDynamicDataFlag;
    bool _dirty;

    std::vector <CtiCCMonitorPointPtr>  _monitorPoint; //normally this is just one, but if display order is > 1 then we have more
                                                    // than one monitor point attached to a capbank!!!

    Cti::CapControl::PointResponseManager _pointResponseManager;

    void restore(Cti::RowReader& rdr);
};

typedef CtiCCCapBank* CtiCCCapBankPtr;

typedef std::list<CtiCCCapBankPtr> CapBankList;
