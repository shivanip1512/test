/*---------------------------------------------------------------------------
        Filename:  cccapbank.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiCCCapBank
                        CtiCCCapBank maintains the state and handles
                        the persistence of cap banks for Cap Control.

        Initial Date:  8/30/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#ifndef CTICCCAPBANKIMPL_H
#define CTICCCAPBANKIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 
#include <list>

#include "msg_cmd.h"
#include "ccpointresponse.h"
#include "ccmonitorpoint.h"
#include "cctwowaycbcpoints.h"
#include "ccoperationstats.h"
#include "ccConfirmationStats.h"
#include "dbaccess.h"
#include "observe.h"
#include "ctitime.h"
#include "ctidate.h"

using boost::shared_ptr;

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
} CtiCCControlStatusQaulity;

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
class CtiCCCapBank : public RWCollectable
{

public:

  RWDECLARE_COLLECTABLE( CtiCCCapBank )

    CtiCCCapBank(RWDBReader& rdr);
    CtiCCCapBank(const CtiCCCapBank& cap);

    virtual ~CtiCCCapBank();

    LONG getPAOId() const;
    const string& getPAOCategory() const;
    const string& getPAOClass() const;
    const string& getPAOName() const;
    const string& getPAOType() const;
    const string& getPAODescription() const;
    BOOL getDisableFlag() const;
    LONG getParentId() const;
    BOOL getAlarmInhibitFlag() const;
    BOOL getControlInhibitFlag() const;
    LONG getMaxDailyOps() const;
    LONG getCurrentDailyOperations() const;
    BOOL getMaxOpsDisableFlag() const;
    const string& getOperationalState() const;
    const string& getControllerType() const;
    LONG getControlDeviceId() const;
    LONG getControlPointId() const;
    const string& getControlDeviceType() const;
    LONG getBankSize() const;
    const string& getTypeOfSwitch() const;
    const string& getSwitchManufacture() const;
    const string& getMapLocationId() const;
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
    LONG getOriginalFeederId() const;

    float getOriginalSwitchingOrder() const;
    float getOriginalCloseOrder() const;
    float getOriginalTripOrder() const;

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
    const string& getIpAddress() const;
    LONG getReportedCBCState() const;
    const CtiTime& getReportedCBCStateTime() const;
    BOOL getIgnoreFlag() const;
    LONG getIgnoredReason() const;
    const string& getBeforeVarsString() const;
    const string& getAfterVarsString() const;
    const string& getPercentChangeString() const;


    int  getVCtrlIndex() const;
    int getAssumedOrigVerificationState() const;

    std::list <LONG>* getPointIds() {return &_pointIds;};
    std::vector <CtiCCMonitorPointPtr>& getMonitorPoint() {return _monitorPoint;};
    std::vector <CtiCCPointResponsePtr>& getPointResponse() {return _pointResponses;};

    CtiCCCapBank& setPAOId(LONG id);
    CtiCCCapBank& setPAOCategory(const string& category);
    CtiCCCapBank& setPAOClass(const string& pclass);
    CtiCCCapBank& setPAOName(const string& name);
    CtiCCCapBank& setPAOType(const string& type);
    CtiCCCapBank& setPAODescription(const string& description);
    CtiCCCapBank& setDisableFlag(BOOL disable);
    CtiCCCapBank& setParentId(LONG parentId);
    CtiCCCapBank& setAlarmInhibitFlag(BOOL alarminhibit);
    CtiCCCapBank& setControlInhibitFlag(BOOL controlinhibit);
    CtiCCCapBank& setMaxDailyOperation(LONG maxdailyops);
    CtiCCCapBank& setCurrentDailyOperations(LONG operations);
    CtiCCCapBank& setMaxOpsDisableFlag(BOOL maxopsdisable);
    CtiCCCapBank& setDeviceClass(const string& deviceclass);
    CtiCCCapBank& setOperationalState(const string& operational);
    CtiCCCapBank& setOperationalState(int value);
    CtiCCCapBank& setControllerType(const string& controllertype);
    CtiCCCapBank& setControlDeviceId(LONG controldevice);
    CtiCCCapBank& setControlPointId(LONG controlpoint);
    CtiCCCapBank& setControlDeviceType(const string& controlDeviceType);
    CtiCCCapBank& setBankSize(LONG size);
    CtiCCCapBank& setTypeOfSwitch(const string& switchtype);
    CtiCCCapBank& setSwitchManufacture(const string& manufacture);
    CtiCCCapBank& setMapLocationId(const string& maplocation);
    CtiCCCapBank& setRecloseDelay(LONG reclose);
    CtiCCCapBank& setControlOrder(FLOAT order);
    CtiCCCapBank& setTripOrder(FLOAT order);
    CtiCCCapBank& setCloseOrder(FLOAT order);
    CtiCCCapBank& setStatusPointId(LONG statuspoint);
    CtiCCCapBank& setControlStatus(LONG status);
    CtiCCCapBank& setOperationAnalogPointId(LONG operationpoint);
    CtiCCCapBank& setTotalOperations(LONG operations);
    CtiCCCapBank& setLastStatusChangeTime(const CtiTime& laststatuschangetime);
    CtiCCCapBank& setTagsControlStatus(LONG tags);
    CtiCCCapBank& setOriginalFeederId(LONG origfeeder);
    CtiCCCapBank& setOriginalSwitchingOrder(float origorder);
    CtiCCCapBank& setOriginalCloseOrder(float origorder);
    CtiCCCapBank& setOriginalTripOrder(float origorder);

    CtiCCCapBank& setVerificationFlag(BOOL verificationFlag);
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
    CtiCCCapBank& setControlStatusQuality(CtiCCControlStatusQaulity quality);
    CtiCCCapBank& setReEnableOvUvFlag(BOOL flag);
    CtiCCCapBank& setLocalControlFlag(BOOL localControlFlag);
    CtiCCCapBank& setControlRecentlySentFlag(BOOL flag);
    CtiCCCapBank& setPorterRetFailFlag(BOOL flag);

    CtiCCCapBank& setOvUvSituationFlag(BOOL ovUvSituationFlag);
    CtiCCCapBank& setUDPPort(LONG value);
    CtiCCCapBank& setIpAddress(ULONG value);
    CtiCCCapBank& setReportedCBCState(LONG value);
    CtiCCCapBank& setReportedCBCStateTime(const CtiTime& timestamp);
    CtiCCCapBank& setIgnoreFlag(BOOL flag);
    CtiCCCapBank& setIgnoredReason(LONG value);
    CtiCCCapBank& setBeforeVarsString(const string& before);
    CtiCCCapBank& setAfterVarsString(const string& after);
    CtiCCCapBank& setPercentChangeString(const string& percent);

    CtiCCCapBank& setVCtrlIndex(int vCtrlIndex);
    CtiCCCapBank& setAssumedOrigVerificationState(int assumedOrigCapBankPos);
    CtiCCCapBank& setPreviousVerificationControlStatus(LONG status);
    BOOL updateVerificationState(void);
    CtiCCCapBank& updatePointResponseDeltas(CtiCCMonitorPoint* point);

    string getControlStatusQualityString();
    string getIgnoreReasonText() const;
    CtiCCCapBank& initVerificationControlStatus();
    CtiCCCapBank& addAllCapBankPointsToMsg(CtiCommandMsg *pointAddMsg);
    string getControlStatusText() const;

    CtiCCPointResponse* getPointResponse(CtiCCMonitorPoint* point);

    CtiCCOperationStats& getOperationStats();
    CtiCCConfirmationStats& getConfirmationStats();

    CtiCCCapBank* replicate() const;
    virtual int compareTo(const RWCollectable* right) const;

    CtiCCTwoWayPoints* getTwoWayPoints();

    BOOL isDirty() const;
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime);

    void setDynamicData(RWDBReader& rdr);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCCapBank& operator=(const CtiCCCapBank& right);

    int operator==(const CtiCCCapBank& right) const;
    int operator!=(const CtiCCCapBank& right) const;

    /* Static Members */

    //Possible operational states
    static const string SwitchedOperationalState;
    static const string FixedOperationalState;
    static const string UninstalledState;
    static const string StandAloneState;
    
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

    const string& convertOperationalState( int num );

    LONG _paoid;
    string _paocategory;
    string _paoclass;
    string _paoname;
    string _paotype;
    string _paodescription;
    BOOL _disableflag;
    LONG _parentId; //feederId
    BOOL _alarminhibitflag;
    BOOL _controlinhibitflag; 
    LONG _maxdailyops;
    LONG _currentdailyoperations;
    BOOL _maxopsdisableflag;
    string _operationalstate;
    string _controllertype;
    LONG _controldeviceid;
    LONG _controlpointid;
    string _controlDeviceType;
    LONG _banksize;
    string _typeofswitch;
    string _switchmanufacture;
    string _maplocationid;
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
    string _additionalFlags;
    LONG _verificationControlStatus;
    int _vCtrlIndex; //1,2, or 3
    BOOL _retryFlag;                 
    LONG _prevVerificationControlStatus;
    int _assumedOrigCapBankPos;
    BOOL _verificationFlag;
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
    
    CtiCCTwoWayPoints *_twoWayPoints;
    string _ipAddress;
    LONG _udpPortNumber;
    LONG _reportedCBCState;
    CtiTime _reportedCBCStateTime;

    BOOL _ignoreFlag;
    LONG _ignoreReason;
    string _sBeforeVars;
    string _sAfterVars;
    string _sPercentChange;
    LONG _controlStatusQuality;

    
    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;
    
    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    std::list <LONG> _pointIds;
    std::vector <CtiCCMonitorPoint*>  _monitorPoint; //normally this is just one, but if display order is > 1 then we have more
                                                    // than one monitor point attached to a capbank!!!
    std::vector <CtiCCPointResponse*> _pointResponses;

    CtiCCCapBank();
    void restore(RWDBReader& rdr);
};

//typedef shared_ptr<CtiCCCapBank> CtiCCCapBankPtr;
typedef CtiCCCapBank* CtiCCCapBankPtr;
#endif
