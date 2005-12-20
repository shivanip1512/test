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

#include <list>
using std::list;

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h>  
#include <list> 

#include "dbaccess.h"
#include "connection.h"
#include "types.h"
#include "observe.h"
#include "ccfeeder.h"
#include "cccapbank.h"
#include "msg_pcrequest.h"
#include "ccstrategy.h"

#define ALLBANKS 0
#define FAILEDBANKS 1
#define QUESTIONABLEBANKS 2
#define FAILEDANDQUESTIONABLEBANKS 3
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
    const string& getPAOCategory() const;
    const string& getPAOClass() const;
    const string& getPAOName() const;
    const string& getPAOType() const;
    const string& getPAODescription() const;
    BOOL getDisableFlag() const;
    LONG getParentId() const;
    LONG getStrategyId() const;
    const string& getControlMethod() const;
    const string& getStrategyName() const;
    LONG getMaxDailyOperation() const;
    BOOL getMaxOperationDisableFlag() const;
    DOUBLE getPeakLag() const;
    DOUBLE getOffPeakLag() const;
    DOUBLE getPeakLead() const;
    DOUBLE getOffPeakLead() const;
    LONG getPeakStartTime() const;
    LONG getPeakStopTime() const;
    LONG getCurrentVarLoadPointId() const;
    DOUBLE getCurrentVarLoadPointValue() const;
    LONG getCurrentWattLoadPointId() const;
    DOUBLE getCurrentWattLoadPointValue() const;
    LONG getCurrentVoltLoadPointId() const;
    DOUBLE getCurrentVoltLoadPointValue() const;
    LONG getControlInterval() const;
    LONG getMaxConfirmTime() const;
    LONG getMinConfirmPercent() const;
    LONG getFailurePercent() const;
    const string& getDaysOfWeek() const;
    const string& getMapLocationId() const;
    const string& getControlUnits() const;
    LONG getControlDelayTime() const;
    LONG getControlSendRetries() const;
    LONG getDecimalPlaces() const;
    const CtiTime& getNextCheckTime() const;
    BOOL getNewPointDataReceivedFlag() const;
    BOOL getBusUpdatedFlag() const;
    const CtiTime& getLastCurrentVarPointUpdateTime() const;
    LONG getEstimatedVarLoadPointId() const;
    DOUBLE getEstimatedVarLoadPointValue() const;
    LONG getDailyOperationsAnalogPointId() const;
    LONG getPowerFactorPointId() const;
    LONG getEstimatedPowerFactorPointId() const;
    LONG getCurrentDailyOperations() const;
    BOOL getPeakTimeFlag() const;
    BOOL getRecentlyControlledFlag() const;
    const CtiTime& getLastOperationTime() const;
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
    void deleteCCFeeder(long feederId);

    CtiCCSubstationBus& setPAOId(LONG id);
    CtiCCSubstationBus& setPAOCategory(const string& category);
    CtiCCSubstationBus& setPAOClass(const string& pclass);
    CtiCCSubstationBus& setPAOName(const string& name);
    CtiCCSubstationBus& setPAOType(const string& type);
    CtiCCSubstationBus& setPAODescription(const string& description);
    CtiCCSubstationBus& setDisableFlag(BOOL disable);
    CtiCCSubstationBus& setParentId(LONG parentId);
    CtiCCSubstationBus& setStrategyId(LONG strategyId);
    CtiCCSubstationBus& setStrategyName(const string& strategyName);
    CtiCCSubstationBus& setControlMethod(const string& method);
    CtiCCSubstationBus& setMaxDailyOperation(LONG max);
    CtiCCSubstationBus& setMaxOperationDisableFlag(BOOL maxopdisable);
    CtiCCSubstationBus& setPeakLag(DOUBLE peak);
    CtiCCSubstationBus& setOffPeakLag(DOUBLE offpeak);
    CtiCCSubstationBus& setPeakLead(DOUBLE peak);
    CtiCCSubstationBus& setOffPeakLead(DOUBLE offpeak);
    CtiCCSubstationBus& setPeakStartTime(LONG starttime);
    CtiCCSubstationBus& setPeakStopTime(LONG stoptime);
    CtiCCSubstationBus& setCurrentVarLoadPointId(LONG currentvarid);
    CtiCCSubstationBus& setCurrentVarLoadPointValue(DOUBLE currentvarval);
    CtiCCSubstationBus& setCurrentWattLoadPointId(LONG currentwattid);
    CtiCCSubstationBus& setCurrentWattLoadPointValue(DOUBLE currentwattval);
    CtiCCSubstationBus& setCurrentVoltLoadPointId(LONG currentvoltid);
    CtiCCSubstationBus& setCurrentVoltLoadPointValue(DOUBLE currentvoltval);
    CtiCCSubstationBus& setControlInterval(LONG interval);
    CtiCCSubstationBus& setMaxConfirmTime(LONG confirm);
    CtiCCSubstationBus& setMinConfirmPercent(LONG confirm);
    CtiCCSubstationBus& setFailurePercent(LONG failure);
    CtiCCSubstationBus& setDaysOfWeek(const string& days);
    CtiCCSubstationBus& setMapLocationId(const string& maplocation);
    CtiCCSubstationBus& setControlUnits(const string& contunit);
    CtiCCSubstationBus& setControlDelayTime(LONG delay);
    CtiCCSubstationBus& setControlSendRetries(LONG retries);
    CtiCCSubstationBus& setDecimalPlaces(LONG places);
    CtiCCSubstationBus& figureNextCheckTime();
    CtiCCSubstationBus& setNewPointDataReceivedFlag(BOOL newpointdatareceived);
    CtiCCSubstationBus& setBusUpdatedFlag(BOOL busupdated);
    CtiCCSubstationBus& setLastCurrentVarPointUpdateTime(const CtiTime& lastpointupdate);
    CtiCCSubstationBus& setEstimatedVarLoadPointId(LONG estimatedvarid);
    CtiCCSubstationBus& setEstimatedVarLoadPointValue(DOUBLE estimatedvarval);
    CtiCCSubstationBus& setDailyOperationsAnalogPointId(LONG opanalogpointid);
    CtiCCSubstationBus& setPowerFactorPointId(LONG pfpointid);
    CtiCCSubstationBus& setEstimatedPowerFactorPointId(LONG epfpointid);
    CtiCCSubstationBus& setCurrentDailyOperations(LONG operations);
    CtiCCSubstationBus& setPeakTimeFlag(LONG peaktime);
    CtiCCSubstationBus& setRecentlyControlledFlag(BOOL recentlycontrolled);
    CtiCCSubstationBus& setLastOperationTime(const CtiTime& lastoperation);
    CtiCCSubstationBus& setVarValueBeforeControl(DOUBLE oldvarval);
    CtiCCSubstationBus& setLastFeederControlledPAOId(LONG lastfeederpao);
    CtiCCSubstationBus& setLastFeederControlledPosition(LONG lastfeederposition);
    CtiCCSubstationBus& setPowerFactorValue(DOUBLE pfval);
    CtiCCSubstationBus& setKVARSolution(DOUBLE solution);
    CtiCCSubstationBus& setEstimatedPowerFactorValue(DOUBLE epfval);
    CtiCCSubstationBus& setCurrentVarPointQuality(LONG cvpq);
    CtiCCSubstationBus& setWaiveControlFlag(BOOL waive);
    CtiCCSubstationBus& setOverlappingVerificationFlag( BOOL overlapFlag);

    BOOL isPastMaxConfirmTime(const CtiTime& currentDateTime);
    BOOL isVarCheckNeeded(const CtiTime& currentDateTime);
    BOOL isConfirmCheckNeeded();
    BOOL capBankControlStatusUpdate(RWOrdered& pointChanges);
    DOUBLE figureCurrentSetPoint(const CtiTime& currentDateTime);
    BOOL isPeakDay();
    BOOL isPeakTime(const CtiTime& currentDateTime);
    void clearOutNewPointReceivedFlags();
    CtiCCSubstationBus& checkForAndProvideNeededControl(const CtiTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages);
    void regularSubstationBusControl(DOUBLE lagLevel, DOUBLE leadLevel, const CtiTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages);
    void optimizedSubstationBusControl(DOUBLE lagLevel, DOUBLE leadLevel, const CtiTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages);
    CtiCCSubstationBus& figureEstimatedVarLoadPointValue();
    BOOL isAlreadyControlled();
    DOUBLE calculatePowerFactor(DOUBLE kvar, DOUBLE kw);
    DOUBLE convertKQToKVAR(DOUBLE kq, DOUBLE kw);
    DOUBLE convertKVARToKQ(DOUBLE kvar, DOUBLE kw);
    static DOUBLE calculateKVARSolution(const string& controlUnits, DOUBLE setPoint, DOUBLE varValue, DOUBLE wattValue);
    BOOL checkForAndPerformSendRetry(const CtiTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages);

    BOOL isBusPerformingVerification();
    BOOL isBusReadyToStartVerification();
    BOOL isBusVerificationAlreadyStarted();
    BOOL isVerificationPastMaxConfirmTime(const CtiTime& currentDateTime);
    BOOL capBankVerificationDone(RWOrdered& pointChanges);
    BOOL areThereMoreCapBanksToVerify();
    //CtiCCSubstationBus& checkForAndProvideNeededVerificationControl();
    CtiCCSubstationBus& startVerificationOnCapBank(const CtiTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages);
    BOOL isVerificationAlreadyControlled();
    CtiCCSubstationBus& setCapBanksToVerifyFlags(int verificationStrategy);
    CtiCCSubstationBus& recompileCapBanksToVerifyList();
    CtiCCSubstationBus& getNextCapBankToVerify();
    CtiCCSubstationBus& sendNextCapBankVerificationControl(const CtiTime& currentDateTime, RWOrdered& pointChanges, RWOrdered& pilMessages);
    CtiCCSubstationBus& setVerificationFlag(BOOL verificationFlag);
    CtiCCSubstationBus& setPerformingVerificationFlag(BOOL performingVerificationFlag);
    CtiCCSubstationBus& setVerificationDoneFlag(BOOL verificationDoneFlag);
    CtiCCSubstationBus& setCurrentVerificationFeederId(LONG feederId);
    CtiCCSubstationBus& setCurrentVerificationCapBankId(LONG capBankId);
    CtiCCSubstationBus& setCurrentVerificationCapBankState(LONG status);

    list <LONG>* getPointIds() {return &_pointIds;};

    CtiCCSubstationBus& setVerificationAlreadyStartedFlag(BOOL verificationFlag);
    list <LONG> getVerificationCapBankList();
    void setVerificationStrategy(int verificationStrategy);
    int getVerificationStrategy(void) const;
    const string& getVerificationCommand();
    CtiCCSubstationBus& setVerificationCommand(string verCommand);
    void setCapBankInactivityTime(LONG capBankToVerifyInactivityTime);
    LONG getCapBankInactivityTime(void) const;

    BOOL capBankVerificationStatusUpdate(RWOrdered& pointChanges);


    BOOL isDirty() const;
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime);
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
    static const string SubstationBusControlMethod;
    static const string IndividualFeederControlMethod;
    static const string BusOptimizedFeederControlMethod;
    static const string ManualOnlyControlMethod;

    static const string KVARControlUnits;
    static const string VoltControlUnits;
    static const string PF_BY_KVARControlUnits;
    static const string PF_BY_KQControlUnits;
    //static int PeakState;
    //static int OffPeakState;


    private:

    LONG _paoid;
    string _paocategory;
    string _paoclass;
    string _paoname;
    string _paotype;
    string _paodescription;
    BOOL _disableflag;
    LONG _parentId;
      LONG _strategyId;
      string _strategyName;
      string _controlmethod;
      LONG _maxdailyoperation;
      BOOL _maxoperationdisableflag;
      LONG _peakstarttime;
      LONG _peakstoptime;
    LONG _currentvarloadpointid;
    DOUBLE _currentvarloadpointvalue;
    LONG _currentwattloadpointid;
    DOUBLE _currentwattloadpointvalue;
    LONG _currentvoltloadpointid;
    DOUBLE _currentvoltloadpointvalue;
    
      LONG _controlinterval;
      LONG _maxconfirmtime;
      LONG _minconfirmpercent;
      LONG _failurepercent;
      string _daysofweek;
      string _maplocationid;
      string _controlunits;
      LONG _controldelaytime;
      LONG _controlsendretries;
      DOUBLE _peaklag;
      DOUBLE _offpklag;
      DOUBLE _peaklead;
      DOUBLE _offpklead;

    LONG _decimalplaces;
    CtiTime _nextchecktime;
    BOOL _newpointdatareceivedflag;
    BOOL _busupdatedflag;
    CtiTime _lastcurrentvarpointupdatetime;
    LONG _estimatedvarloadpointid;
    DOUBLE _estimatedvarloadpointvalue;
    LONG _dailyoperationsanalogpointid;
    LONG _powerfactorpointid;
    LONG _estimatedpowerfactorpointid;
    LONG _currentdailyoperations;       //daily operations...
    BOOL _peaktimeflag;
    BOOL _recentlycontrolledflag;
    CtiTime _lastoperationtime;
    DOUBLE _varvaluebeforecontrol;
    LONG _lastfeedercontrolledpaoid;
    LONG _lastfeedercontrolledposition;
    DOUBLE _powerfactorvalue;
    DOUBLE _kvarsolution;
    DOUBLE _estimatedpowerfactorvalue;
    LONG _currentvarpointquality;
    BOOL _waivecontrolflag;

    string _additionalFlags;
    LONG _currentVerificationCapBankId;
    LONG _currentVerificationFeederId; 
    RWOrdered _ccfeeders;

    BOOL _verificationFlag;
    BOOL _performingVerificationFlag;
    BOOL _verificationDoneFlag;
    BOOL _overlappingSchedulesVerificationFlag;

    LONG _currentCapBankToVerifyAssumedOrigState;
    int _verificationStrategy;
    LONG _capBankToVerifyInactivityTime;

    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    void restore(RWDBReader& rdr);
    void restoreSubstationBusTableValues(RWDBReader& rdr);

    string doubleToString(DOUBLE doubleVal);
    list <long> _pointIds;
};


//typedef shared_ptr<CtiCCSubstationBus> CtiCCSubstationBusPtr;
typedef CtiCCSubstationBus* CtiCCSubstationBusPtr;
#endif
