/*---------------------------------------------------------------------------
        Filename:  ccsubstationbus.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiCCSubstationBus
                        CtiCCSubstationBus maintains the state and handles
                        the persistence of strategies for Cap Control.                             

        Initial Date:  8/27/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#ifndef CTICCSUBSTATIONBUSIMPL_H
#define CTICCSUBSTATIONBUSIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 

#include "dbaccess.h"
#include "connection.h"
#include "types.h"
#include "observe.h"
#include "ccfeeder.h"
#include "cccapbank.h"
#include "msg_pcrequest.h"
#include "ccstrategy.h"


#define ALLBANKS 0
#define FAILEDANDQUESTIONABLEBANKS 1
#define FAILEDBANKS 2
#define QUESTIONABLEBANKS 3
#define SELECTEDFORVERIFICATIONBANKS 4
#define BANKSINACTIVEFORXTIME 5


class CtiCCSubstationBus : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiCCSubstationBus )

    CtiCCSubstationBus();
    CtiCCSubstationBus(RWDBReader& rdr);
    CtiCCSubstationBus(const CtiCCSubstationBus& bus);

    virtual ~CtiCCSubstationBus();

    LONG getPAOId() const;
    const RWCString& getPAOCategory() const;
    const RWCString& getPAOClass() const;
    const RWCString& getPAOName() const;
    const RWCString& getPAOType() const;
    const RWCString& getPAODescription() const;
    BOOL getDisableFlag() const;
    LONG getParentId() const;
    LONG getStrategyId() const;
    const RWCString& getStrategyName() const;
    const RWCString& getControlMethod() const;
    LONG getMaxDailyOperation() const;
    BOOL getMaxOperationDisableFlag() const;
    DOUBLE getPeakSetPoint() const;
    DOUBLE getOffPeakSetPoint() const;
    LONG getPeakStartTime() const;
    LONG getPeakStopTime() const;
    LONG getCurrentVarLoadPointId() const;
    DOUBLE getCurrentVarLoadPointValue() const;
    LONG getCurrentWattLoadPointId() const;
    DOUBLE getCurrentWattLoadPointValue() const;
    DOUBLE getUpperBandwidth() const;
    LONG getControlInterval() const;
    LONG getMaxConfirmTime() const;
    LONG getMinConfirmPercent() const;
    LONG getFailurePercent() const;
    const RWCString& getDaysOfWeek() const;
    const RWCString& getMapLocationId() const;
    DOUBLE getLowerBandwidth() const;
    const RWCString& getControlUnits() const;
    LONG getControlDelayTime() const;
    LONG getControlSendRetries() const;
    LONG getDecimalPlaces() const;
    const RWDBDateTime& getNextCheckTime() const;
    BOOL getNewPointDataReceivedFlag() const;
    BOOL getBusUpdatedFlag() const;
    const RWDBDateTime& getLastCurrentVarPointUpdateTime() const;
    LONG getEstimatedVarLoadPointId() const;
    DOUBLE getEstimatedVarLoadPointValue() const;
    LONG getDailyOperationsAnalogPointId() const;
    LONG getPowerFactorPointId() const;
    LONG getEstimatedPowerFactorPointId() const;
    LONG getCurrentDailyOperations() const;
    BOOL getPeakTimeFlag() const;
    BOOL getRecentlyControlledFlag() const;
    const RWDBDateTime& getLastOperationTime() const;
    DOUBLE getVarValueBeforeControl() const;
    LONG getLastFeederControlledPAOId() const;
    LONG getLastFeederControlledPosition() const;
    DOUBLE getPowerFactorValue() const;
    DOUBLE getKVARSolution() const;
    DOUBLE getEstimatedPowerFactorValue() const;
    LONG getCurrentVarPointQuality() const;
    BOOL getWaiveControlFlag() const;
    BOOL getVerificationFlag() const;
    BOOL getPerformingVerificationFlag() const;
    BOOL getVerificationDoneFlag() const;
    LONG getCurrentVerificationFeederId() const;
    LONG getCurrentVerificationCapBankId() const;
    LONG getCurrentVerificationCapBankOrigState() const;
    BOOL getOverlappingVerificationFlag() const;


    RWOrdered& getCCFeeders();

    CtiCCSubstationBus& setPAOId(LONG id);
    CtiCCSubstationBus& setPAOCategory(const RWCString& category);
    CtiCCSubstationBus& setPAOClass(const RWCString& pclass);
    CtiCCSubstationBus& setPAOName(const RWCString& name);
    CtiCCSubstationBus& setPAOType(const RWCString& type);
    CtiCCSubstationBus& setPAODescription(const RWCString& description);
    CtiCCSubstationBus& setDisableFlag(BOOL disable);
    CtiCCSubstationBus& setParentId(LONG parentId);
    CtiCCSubstationBus& setStrategyId(LONG strategyId);
    CtiCCSubstationBus& setStrategyName(const RWCString& strategyName);
    CtiCCSubstationBus& setControlMethod(const RWCString& method);
    CtiCCSubstationBus& setMaxDailyOperation(LONG max);
    CtiCCSubstationBus& setMaxOperationDisableFlag(BOOL maxopdisable);
    CtiCCSubstationBus& setPeakSetPoint(DOUBLE peak);
    CtiCCSubstationBus& setOffPeakSetPoint(DOUBLE offpeak);
    CtiCCSubstationBus& setPeakStartTime(LONG starttime);
    CtiCCSubstationBus& setPeakStopTime(LONG stoptime);
    CtiCCSubstationBus& setCurrentVarLoadPointId(LONG currentvarid);
    CtiCCSubstationBus& setCurrentVarLoadPointValue(DOUBLE currentvarval);
    CtiCCSubstationBus& setCurrentWattLoadPointId(LONG currentwattid);
    CtiCCSubstationBus& setCurrentWattLoadPointValue(DOUBLE currentwattval);
    CtiCCSubstationBus& setUpperBandwidth(DOUBLE bandwidth);
    CtiCCSubstationBus& setControlInterval(LONG interval);
    CtiCCSubstationBus& setMaxConfirmTime(LONG confirm);
    CtiCCSubstationBus& setMinConfirmPercent(LONG confirm);
    CtiCCSubstationBus& setFailurePercent(LONG failure);
    CtiCCSubstationBus& setDaysOfWeek(const RWCString& days);
    CtiCCSubstationBus& setMapLocationId(const RWCString& maplocation);
    CtiCCSubstationBus& setLowerBandwidth(DOUBLE bandwidth);
    CtiCCSubstationBus& setControlUnits(const RWCString& contunit);
    CtiCCSubstationBus& setControlDelayTime(LONG delay);
    CtiCCSubstationBus& setControlSendRetries(LONG retries);
    CtiCCSubstationBus& setDecimalPlaces(LONG places);
    CtiCCSubstationBus& figureNextCheckTime();
    CtiCCSubstationBus& setNewPointDataReceivedFlag(BOOL newpointdatareceived);
    CtiCCSubstationBus& setBusUpdatedFlag(BOOL busupdated);
    CtiCCSubstationBus& setLastCurrentVarPointUpdateTime(const RWDBDateTime& lastpointupdate);
    CtiCCSubstationBus& setEstimatedVarLoadPointId(LONG estimatedvarid);
    CtiCCSubstationBus& setEstimatedVarLoadPointValue(DOUBLE estimatedvarval);
    CtiCCSubstationBus& setDailyOperationsAnalogPointId(LONG opanalogpointid);
    CtiCCSubstationBus& setPowerFactorPointId(LONG pfpointid);
    CtiCCSubstationBus& setEstimatedPowerFactorPointId(LONG epfpointid);
    CtiCCSubstationBus& setCurrentDailyOperations(LONG operations);
    CtiCCSubstationBus& setPeakTimeFlag(LONG peaktime);
    CtiCCSubstationBus& setRecentlyControlledFlag(BOOL recentlycontrolled);
    CtiCCSubstationBus& setLastOperationTime(const RWDBDateTime& lastoperation);
    CtiCCSubstationBus& setVarValueBeforeControl(DOUBLE oldvarval);
    CtiCCSubstationBus& setLastFeederControlledPAOId(LONG lastfeederpao);
    CtiCCSubstationBus& setLastFeederControlledPosition(LONG lastfeederposition);
    CtiCCSubstationBus& setPowerFactorValue(DOUBLE pfval);
    CtiCCSubstationBus& setKVARSolution(DOUBLE solution);
    CtiCCSubstationBus& setEstimatedPowerFactorValue(DOUBLE epfval);
    CtiCCSubstationBus& setCurrentVarPointQuality(LONG cvpq);
    CtiCCSubstationBus& setWaiveControlFlag(BOOL waive);
    CtiCCSubstationBus& setOverlappingVerificationFlag( BOOL overlapFlag);

    BOOL isPastMaxConfirmTime(const RWDBDateTime& currentDateTime);
    BOOL isVarCheckNeeded(const RWDBDateTime& currentDateTime);
    BOOL isConfirmCheckNeeded();
    BOOL capBankControlStatusUpdate(RWOrdered& pointChanges);
    DOUBLE figureCurrentSetPoint(const RWDBDateTime& currentDateTime);
    BOOL isPeakDay();
    BOOL isPeakTime(const RWDBDateTime& currentDateTime);
    void clearOutNewPointReceivedFlags();
    CtiCCSubstationBus& checkForAndProvideNeededControl(const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages);
    void regularSubstationBusControl(DOUBLE setpoint, const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages);
    void optimizedSubstationBusControl(DOUBLE setpoint, const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages);
    CtiCCSubstationBus& figureEstimatedVarLoadPointValue();
    BOOL isAlreadyControlled();
    DOUBLE calculatePowerFactor(DOUBLE kvar, DOUBLE kw);
    DOUBLE convertKQToKVAR(DOUBLE kq, DOUBLE kw);
    DOUBLE convertKVARToKQ(DOUBLE kvar, DOUBLE kw);
    static DOUBLE calculateKVARSolution(const RWCString& controlUnits, DOUBLE setPoint, DOUBLE varValue, DOUBLE wattValue);
    BOOL checkForAndPerformSendRetry(const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages);

    BOOL isBusPerformingVerification();
    BOOL isBusReadyToStartVerification();
    BOOL isBusVerificationAlreadyStarted();
    BOOL isVerificationPastMaxConfirmTime(const RWDBDateTime& currentDateTime);
    BOOL capBankVerificationDone(RWOrdered& pointChanges);
    BOOL areThereMoreCapBanksToVerify();
    //CtiCCSubstationBus& checkForAndProvideNeededVerificationControl();
    CtiCCSubstationBus& startVerificationOnCapBank(const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages);
    BOOL isVerificationAlreadyControlled();
    CtiCCSubstationBus& setCapBanksToVerifyFlags(int verificationStrategy);
    CtiCCSubstationBus& recompileCapBanksToVerifyList();
    CtiCCSubstationBus& getNextCapBankToVerify();
    CtiCCSubstationBus& sendNextCapBankVerificationControl(const RWDBDateTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages);
    CtiCCSubstationBus& setVerificationFlag(BOOL verificationFlag);
    CtiCCSubstationBus& setPerformingVerificationFlag(BOOL performingVerificationFlag);
    CtiCCSubstationBus& setVerificationDoneFlag(BOOL verificationDoneFlag);
    CtiCCSubstationBus& setCurrentVerificationFeederId(LONG feederId);
    CtiCCSubstationBus& setCurrentVerificationCapBankId(LONG capBankId);
    CtiCCSubstationBus& setCurrentVerificationCapBankState(LONG status);


    CtiCCSubstationBus& setVerificationAlreadyStartedFlag(BOOL verificationFlag);
    list <LONG> getVerificationCapBankList();
    void setVerificationStrategy(int verificationStrategy);
    int getVerificationStrategy(void) const;
    void setCapBankInactivityTime(LONG capBankToVerifyInactivityTime);
    LONG getCapBankInactivityTime(void) const;

    BOOL capBankVerificationStatusUpdate(RWOrdered& pointChanges);


    BOOL isDirty() const;
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, RWDBDateTime& currentDateTime);
    void setDynamicData(RWDBReader& rdr);
    void setStrategyValues(CtiCCStrategyPtr strategy);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCSubstationBus& operator=(const CtiCCSubstationBus& right);

    int operator==(const CtiCCSubstationBus& right) const;
    int operator!=(const CtiCCSubstationBus& right) const;

    CtiCCSubstationBus* replicate() const;

    //Possible control methods
    static const RWCString SubstationBusControlMethod;
    static const RWCString IndividualFeederControlMethod;
    static const RWCString BusOptimizedFeederControlMethod;
    static const RWCString ManualOnlyControlMethod;

    static const RWCString KVARControlUnits;
    static const RWCString PF_BY_KVARControlUnits;
    static const RWCString PF_BY_KQControlUnits;
    //static int PeakState;
    //static int OffPeakState;


    private:

    LONG _paoid;
    RWCString _paocategory;
    RWCString _paoclass;
    RWCString _paoname;
    RWCString _paotype;
    RWCString _paodescription;
    BOOL _disableflag;
    LONG _parentId;
    LONG _strategyId;
    RWCString _strategyName;
    RWCString _controlmethod;
    LONG _maxdailyoperation;
    BOOL _maxoperationdisableflag;
    DOUBLE _peaksetpoint;
    DOUBLE _offpeaksetpoint;
    LONG _peakstarttime;
    LONG _peakstoptime;
    LONG _currentvarloadpointid;
    DOUBLE _currentvarloadpointvalue;
    LONG _currentwattloadpointid;
    DOUBLE _currentwattloadpointvalue;
    DOUBLE _upperbandwidth;
    LONG _controlinterval;
    LONG _maxconfirmtime;
    LONG _minconfirmpercent;
    LONG _failurepercent;
    RWCString _daysofweek;
    RWCString _maplocationid;
    DOUBLE _lowerbandwidth;
    RWCString _controlunits;
    LONG _controldelaytime;
    LONG _controlsendretries;
    LONG _decimalplaces;
    RWDBDateTime _nextchecktime;
    BOOL _newpointdatareceivedflag;
    BOOL _busupdatedflag;
    RWDBDateTime _lastcurrentvarpointupdatetime;
    LONG _estimatedvarloadpointid;
    DOUBLE _estimatedvarloadpointvalue;
    LONG _dailyoperationsanalogpointid;
    LONG _powerfactorpointid;
    LONG _estimatedpowerfactorpointid;
    LONG _currentdailyoperations;
    BOOL _peaktimeflag;
    BOOL _recentlycontrolledflag;
    RWDBDateTime _lastoperationtime;
    DOUBLE _varvaluebeforecontrol;
    LONG _lastfeedercontrolledpaoid;
    LONG _lastfeedercontrolledposition;
    DOUBLE _powerfactorvalue;
    DOUBLE _kvarsolution;
    DOUBLE _estimatedpowerfactorvalue;
    LONG _currentvarpointquality;
    BOOL _waivecontrolflag;

    RWCString _additionalFlags;
    LONG _currentVerificationCapBankId;
    LONG _currentVerificationFeederId; 
    RWOrdered _ccfeeders;

    BOOL _verificationFlag;
    BOOL _performingVerificationFlag;
    BOOL _verificationDoneFlag;
    BOOL _overlappingSchedulesVerificationFlag;

    BOOL _startVerificationFlag;
    BOOL _verificationAlreadyStartedFlag;
    //list <LONG> _verificationCapBankIds;
    LONG _currentCapBankToVerifyAssumedOrigState;
    int _verificationStrategy;
    LONG _capBankToVerifyInactivityTime;

    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    void restore(RWDBReader& rdr);
    void restoreSubstationBusTableValues(RWDBReader& rdr);
    RWCString doubleToString(DOUBLE doubleVal);
};


//typedef shared_ptr<CtiCCSubstationBus> CtiCCSubstationBusPtr;
typedef CtiCCSubstationBus* CtiCCSubstationBusPtr;
#endif
