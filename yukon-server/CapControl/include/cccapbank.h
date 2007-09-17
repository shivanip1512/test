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
#include "dbaccess.h"
#include "observe.h"
#include "ctitime.h"
#include "ctidate.h"

                
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
    LONG getTripOrder() const;
    LONG getCloseOrder() const;
    LONG getStatusPointId() const;
    LONG getControlStatus() const;
    LONG getOperationAnalogPointId() const;
    LONG getTotalOperations() const;
    const CtiTime& getLastStatusChangeTime() const;
    LONG getTagsControlStatus() const;
    LONG getOriginalFeederId() const;
    LONG getOriginalSwitchingOrder() const;
    BOOL getVerificationFlag() const;
    BOOL getPerformingVerificationFlag() const;
    BOOL getVerificationDoneFlag() const;
    BOOL getRetryOpenFailedFlag() const;
    BOOL getRetryCloseFailedFlag() const;
    BOOL getOvUvDisabledFlag() const;
    BOOL getMaxDailyOpsHitFlag() const;
    LONG getUDPPort() const;
    const string& getIpAddress() const;
    LONG getReportedCBCState() const;
    const CtiTime& getReportedCBCStateTime() const;
    BOOL getIgnoreFlag() const;
    LONG getIgnoredReason() const;


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
    CtiCCCapBank& setTripOrder(LONG order);
    CtiCCCapBank& setCloseOrder(LONG order);
    CtiCCCapBank& setStatusPointId(LONG statuspoint);
    CtiCCCapBank& setControlStatus(LONG status);
    CtiCCCapBank& setOperationAnalogPointId(LONG operationpoint);
    CtiCCCapBank& setTotalOperations(LONG operations);
    CtiCCCapBank& setLastStatusChangeTime(const CtiTime& laststatuschangetime);
    CtiCCCapBank& setTagsControlStatus(LONG tags);
    CtiCCCapBank& setOriginalFeederId(LONG origfeeder);
    CtiCCCapBank& setOriginalSwitchingOrder(LONG origorder);

    CtiCCCapBank& setVerificationFlag(BOOL verificationFlag);
    CtiCCCapBank& setPerformingVerificationFlag(BOOL performingVerificationFlag);
    CtiCCCapBank& setVerificationDoneFlag(BOOL verificationDoneFlag);
    CtiCCCapBank& setRetryOpenFailedFlag(BOOL retryOpenFailedFlag);
    CtiCCCapBank& setRetryCloseFailedFlag(BOOL retryCloseFailedFlag);
    CtiCCCapBank& setOvUvDisabledFlag(BOOL ovUvDisabledFlag);
    CtiCCCapBank& setMaxDailyOpsHitFlag(BOOL flag);
    CtiCCCapBank& setUDPPort(LONG value);
    CtiCCCapBank& setIpAddress(ULONG value);
    CtiCCCapBank& setReportedCBCState(LONG value);
    CtiCCCapBank& setReportedCBCStateTime(const CtiTime& timestamp);
    CtiCCCapBank& setIgnoreFlag(BOOL flag);
    CtiCCCapBank& setIgnoredReason(LONG value);

    CtiCCCapBank& setVCtrlIndex(int vCtrlIndex);
    CtiCCCapBank& setAssumedOrigVerificationState(int assumedOrigCapBankPos);
    CtiCCCapBank& setPreviousVerificationControlStatus(LONG status);
    BOOL updateVerificationState(void);
    CtiCCCapBank& updatePointResponseDeltas(CtiCCMonitorPoint* point);

    //int getAssumedOrigVerificationState();
    CtiCCCapBank& initVerificationControlStatus();
    CtiCCCapBank& addAllCapBankPointsToMsg(CtiCommandMsg *pointAddMsg);

    CtiCCPointResponse* getPointResponse(CtiCCMonitorPoint* point);

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
    static int Open;
    static int Close;
    static int OpenQuestionable;
    static int CloseQuestionable;
    static int OpenFail;
    static int CloseFail;
    static int OpenPending;
    static int ClosePending;

    

        
private:

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
    LONG _triporder;
    LONG _closeorder;
    LONG _statuspointid;
    LONG _controlstatus;
    LONG _operationanalogpointid;
    LONG _totaloperations;
    CtiTime _laststatuschangetime;
    LONG _tagscontrolstatus;
    LONG _originalfeederid;
    LONG _originalswitchingorder;


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
    CtiCCTwoWayPoints *_twoWayPoints;
    string _ipAddress;
    LONG _udpPortNumber;
    LONG _reportedCBCState;
    CtiTime _reportedCBCStateTime;

    BOOL _ignoreFlag;
    LONG _ignoreReason;
    
    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    void restore(RWDBReader& rdr);
    CtiCCCapBank();

    std::list <LONG> _pointIds;

    std::vector <CtiCCMonitorPoint*>  _monitorPoint; //normally this is just one, but if display order is > 1 then we have more
                                                // than one monitor point attached to a capbank!!!
    std::vector <CtiCCPointResponse*> _pointResponses;
};

//typedef shared_ptr<CtiCCCapBank> CtiCCCapBankPtr;
typedef CtiCCCapBank* CtiCCCapBankPtr;
#endif
