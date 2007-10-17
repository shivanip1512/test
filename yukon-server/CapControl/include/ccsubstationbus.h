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
#include <vector>

#include "dbaccess.h"
#include "connection.h"
#include "types.h"
#include "observe.h"
#include "ccfeeder.h"
#include "cccapbank.h"
#include "msg_pcrequest.h"
#include "msg_cmd.h"
#include "ccstrategy.h"
#include "ccmonitorpoint.h"

typedef std::vector<CtiCCFeeder*> CtiFeeder_vec;

enum CtiCCMultiBusState 
{
    IDLE = 0,
    NEW_MULTI_POINT_DATA_RECEIVED,
    PRE_OP_SCAN_PENDING,
    EVALUATE_SUB,
    SELECT_BANK,
    OPERATION_SENT_WAIT,
    POST_OP_SCAN_PENDING,
    RECORD_ADAPTIVE_VOLTAGE,
};
              
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
    DOUBLE getPeakVARLag() const;
    DOUBLE getOffPeakVARLag() const;
    DOUBLE getPeakVARLead() const;
    DOUBLE getOffPeakVARLead() const;
    DOUBLE getPeakPFSetPoint() const;
    DOUBLE getOffPeakPFSetPoint() const;
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
    LONG getCurrentWattPointQuality() const;
    LONG getCurrentVoltPointQuality() const;
    BOOL getWaiveControlFlag() const;
    BOOL getVerificationFlag() const;
    BOOL getPerformingVerificationFlag() const;
    BOOL getVerificationDoneFlag() const;
    LONG getCurrentVerificationFeederId() const;
    LONG getCurrentVerificationCapBankId() const;
    LONG getCurrentVerificationCapBankOrigState() const;
    BOOL getOverlappingVerificationFlag() const;
    BOOL getPreOperationMonitorPointScanFlag() const;
    BOOL getOperationSentWaitFlag() const;
    BOOL getPostOperationMonitorPointScanFlag() const;
    LONG getAltDualSubId() const;
    DOUBLE getAltSubControlValue() const;
    void getAllAltSubValues(DOUBLE &volt, DOUBLE &var, DOUBLE &watt);
    LONG getSwitchOverPointId() const;        
    BOOL getSwitchOverStatus() const;          
    BOOL getDualBusEnable() const;
    LONG getEventSequence() const;
    BOOL getReEnableBusFlag() const;
    BOOL getMultiMonitorFlag() const;
    BOOL getWaitForReCloseDelayFlag() const;
    BOOL getWaitToFinishRegularControlFlag() const;
    BOOL getMaxDailyOpsHitFlag() const;
    BOOL getOvUvDisabledFlag() const;
    const string& getSolution() const;
    DOUBLE getTargetVarValue() const;
    const string& getParentControlUnits() const;
    const string& getParentName() const;
    LONG getDisplayOrder() const;  
    BOOL getIntegrateFlag() const;
    LONG getIntegratePeriod() const;
    DOUBLE getIVControlTot() const;
    LONG getIVCount() const;
    DOUBLE getIWControlTot() const;
    LONG getIWCount() const;
    DOUBLE getIVControl() const;
    DOUBLE getIWControl() const;
    BOOL getUsePhaseData() const;
    LONG getPhaseBId() const;
    LONG getPhaseCId() const;
    DOUBLE getPhaseAValue() const;
    DOUBLE getPhaseBValue() const;
    DOUBLE getPhaseCValue() const;
    DOUBLE getPhaseAValueBeforeControl() const;
    DOUBLE getPhaseBValueBeforeControl() const;
    DOUBLE getPhaseCValueBeforeControl() const;

    CtiFeeder_vec& getCCFeeders();
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
    CtiCCSubstationBus& setPeakVARLag(DOUBLE peak);
    CtiCCSubstationBus& setOffPeakVARLag(DOUBLE offpeak);
    CtiCCSubstationBus& setPeakVARLead(DOUBLE peak);
    CtiCCSubstationBus& setOffPeakVARLead(DOUBLE offpeak);
    CtiCCSubstationBus& setPeakPFSetPoint(DOUBLE peak);
    CtiCCSubstationBus& setOffPeakPFSetPoint(DOUBLE offpeak);
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
    CtiCCSubstationBus& setLastVerificationCheck(const CtiTime& checkTime);
    CtiCCSubstationBus& setVarValueBeforeControl(DOUBLE oldvarval);
    CtiCCSubstationBus& setLastFeederControlledPAOId(LONG lastfeederpao);
    CtiCCSubstationBus& setLastFeederControlledPosition(LONG lastfeederposition);
    CtiCCSubstationBus& setPowerFactorValue(DOUBLE pfval);
    CtiCCSubstationBus& setKVARSolution(DOUBLE solution);
    CtiCCSubstationBus& setEstimatedPowerFactorValue(DOUBLE epfval);
    CtiCCSubstationBus& setCurrentVarPointQuality(LONG cvpq);
    CtiCCSubstationBus& setCurrentWattPointQuality(LONG cwpq);
    CtiCCSubstationBus& setCurrentVoltPointQuality(LONG cvpq);
    CtiCCSubstationBus& setWaiveControlFlag(BOOL waive);
    CtiCCSubstationBus& setOverlappingVerificationFlag( BOOL overlapFlag);
    CtiCCSubstationBus& setPreOperationMonitorPointScanFlag( BOOL flag);
    CtiCCSubstationBus& setOperationSentWaitFlag( BOOL flag);
    CtiCCSubstationBus& setPostOperationMonitorPointScanFlag( BOOL flag);
    CtiCCSubstationBus& setAltDualSubId(LONG altDualSubId);
    CtiCCSubstationBus& setAltSubControlValue(DOUBLE controlValue);
    CtiCCSubstationBus& setSwitchOverPointId(LONG pointId);
    CtiCCSubstationBus& setSwitchOverStatus(BOOL status);
    CtiCCSubstationBus& setDualBusEnable(BOOL flag);
    CtiCCSubstationBus& setEventSequence(LONG eventSeq);
    CtiCCSubstationBus& setReEnableBusFlag(BOOL flag);
    CtiCCSubstationBus& setMultiMonitorFlag(BOOL flag);
    CtiCCSubstationBus& setWaitForReCloseDelayFlag(BOOL flag);
    CtiCCSubstationBus& setWaitToFinishRegularControlFlag(BOOL flag);
    CtiCCSubstationBus& setMaxDailyOpsHitFlag(BOOL flag);
    CtiCCSubstationBus& setOvUvDisabledFlag(BOOL flag);
    CtiCCSubstationBus& setAllAltSubValues(DOUBLE volt, DOUBLE var, DOUBLE watt);
    CtiCCSubstationBus& setSolution(const string& text);
    CtiCCSubstationBus& setTargetVarValue(DOUBLE value);
    CtiCCSubstationBus& setParentControlUnits(const string& parentControlUnits);
    CtiCCSubstationBus& setParentName(const string& parentName);
    CtiCCSubstationBus& setDisplayOrder(LONG displayOrder);
    CtiCCSubstationBus& setIntegrateFlag(BOOL flag);
    CtiCCSubstationBus& setIntegratePeriod(LONG period);
    CtiCCSubstationBus& setIVControlTot(DOUBLE value);
    CtiCCSubstationBus& setIVCount(LONG value);
    CtiCCSubstationBus& setIWControlTot(DOUBLE value);
    CtiCCSubstationBus& setIWCount(LONG value);
    CtiCCSubstationBus& setIVControl(DOUBLE value);
    CtiCCSubstationBus& setIWControl(DOUBLE value);
    CtiCCSubstationBus& setUsePhaseData(BOOL flag);
    CtiCCSubstationBus& setPhaseBId(LONG pointid);
    CtiCCSubstationBus& setPhaseCId(LONG pointid);
    CtiCCSubstationBus& setPhaseAValue(DOUBLE value);
    CtiCCSubstationBus& setPhaseBValue(DOUBLE value);
    CtiCCSubstationBus& setPhaseCValue(DOUBLE value);
    CtiCCSubstationBus& setPhaseAValueBeforeControl(DOUBLE value);
    CtiCCSubstationBus& setPhaseBValueBeforeControl(DOUBLE value);
    CtiCCSubstationBus& setPhaseCValueBeforeControl(DOUBLE value);


    BOOL isPastMaxConfirmTime(const CtiTime& currentDateTime);
    LONG getLastFeederControlledSendRetries() const;
    void analyzeMultiVoltBus(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    void analyzeMultiVoltBus1(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    BOOL performActionMultiPointBus(const CtiTime& currentDateTime);
    BOOL isVarCheckNeeded(const CtiTime& currentDateTime);
    BOOL isConfirmCheckNeeded();
    BOOL capBankControlStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents);
    DOUBLE figureCurrentSetPoint(const CtiTime& currentDateTime);
    BOOL isPeakDay(const CtiTime& currentDateTime);
    BOOL isPeakTime(const CtiTime& currentDateTime);
    BOOL isControlPoint(LONG pointid);
    void updateIntegrationVPoint(const CtiTime &currentDateTime);
    void updateIntegrationWPoint(const CtiTime &currentDateTime);
    void clearOutNewPointReceivedFlags();
    CtiCCSubstationBus& checkForAndProvideNeededControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    void regularSubstationBusControl(DOUBLE lagLevel, DOUBLE leadLevel, const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    void optimizedSubstationBusControl(DOUBLE lagLevel, DOUBLE leadLevel, const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    CtiCCSubstationBus& figureEstimatedVarLoadPointValue();
    BOOL isAlreadyControlled();
    DOUBLE calculatePowerFactor(DOUBLE kvar, DOUBLE kw);
    DOUBLE convertKQToKVAR(DOUBLE kq, DOUBLE kw);
    DOUBLE convertKVARToKQ(DOUBLE kvar, DOUBLE kw);
    static DOUBLE calculateKVARSolution(const string& controlUnits, DOUBLE setPoint, DOUBLE varValue, DOUBLE wattValue);
    BOOL checkForAndPerformSendRetry(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    BOOL checkForAndPerformVerificationSendRetry(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    void voltControlProcess();
    void updatePointResponsePreOpValues(CtiCCCapBank* capBank);
    void updatePointResponseDeltas();
    BOOL areAllMonitorPointsNewEnough(const CtiTime& currentDateTime);
    BOOL isScanFlagSet();
    ULONG getMonitorPointScanTime();
    BOOL scanAllMonitorPoints();
    BOOL isBusAnalysisNeeded(const CtiTime& currentDateTime);
    BOOL isMultiVoltBusAnalysisNeeded(const CtiTime& currentDateTime);
    BOOL areAllMonitorPointsInVoltageRange(CtiCCMonitorPoint* oorPoint);
    CtiCCCapBank* getMonitorPointParentBankAndFeeder(CtiCCMonitorPoint* point, CtiCCFeeder* feed);
    BOOL voltControlBankSelectProcess(CtiCCMonitorPoint* point, CtiMultiMsg_vec &pointChanges, CtiMultiMsg_vec &ccEvents, CtiMultiMsg_vec &pilMessages);
    BOOL areOtherMonitorPointResponsesOk(LONG mPointID, CtiCCCapBank* potentialCap, int action);
    BOOL analyzeBusForVarImprovement(CtiCCMonitorPoint* point, CtiMultiMsg_vec &pointChanges, CtiMultiMsg_vec &ccEvents, CtiMultiMsg_vec &pilMessages);

    BOOL isBusPerformingVerification();
    BOOL isBusReadyToStartVerification();
    BOOL isBusVerificationAlreadyStarted();
    BOOL isVerificationPastMaxConfirmTime(const CtiTime& currentDateTime);
    BOOL capBankVerificationDone(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents);
    BOOL areThereMoreCapBanksToVerify(CtiMultiMsg_vec& ccEvents);
    //CtiCCSubstationBus& checkForAndProvideNeededVerificationControl();
    CtiCCSubstationBus& startVerificationOnCapBank(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    BOOL isVerificationAlreadyControlled();
    CtiCCSubstationBus& analyzeVerificationByFeeder(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages, CtiMultiMsg_vec& capMessages);
    CtiCCSubstationBus& setCapBanksToVerifyFlags(int verificationStrategy, CtiMultiMsg_vec& ccEvents);
    CtiCCSubstationBus& recompileCapBanksToVerifyList();
    CtiCCSubstationBus& getNextCapBankToVerify(CtiMultiMsg_vec& ccEvents);
    BOOL sendNextCapBankVerificationControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    CtiCCSubstationBus& setVerificationFlag(BOOL verificationFlag);
    CtiCCSubstationBus& setPerformingVerificationFlag(BOOL performingVerificationFlag);
    CtiCCSubstationBus& setVerificationDoneFlag(BOOL verificationDoneFlag);
    CtiCCSubstationBus& setCurrentVerificationFeederId(LONG feederId);
    CtiCCSubstationBus& setCurrentVerificationCapBankId(LONG capBankId);
    CtiCCSubstationBus& setCurrentVerificationCapBankState(LONG status);

    CtiCCSubstationBus& addAllSubPointsToMsg(CtiCommandMsg *pointAddMsg);


    list <LONG>* getPointIds() {return &_pointIds;};

    CtiCCSubstationBus& setVerificationAlreadyStartedFlag(BOOL verificationFlag);
    void setVerificationStrategy(int verificationStrategy);
    int getVerificationStrategy(void) const;
    string getVerificationString();
    //const string& getVerificationCommand();
    //CtiCCSubstationBus& setVerificationCommand(string verCommand);
    void setCapBankInactivityTime(LONG capBankToVerifyInactivityTime);
    LONG getCapBankInactivityTime(void) const;

    BOOL capBankVerificationStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents);
    


    BOOL isDirty() const;
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime);
    void setDynamicData(RWDBReader& rdr);
    void setStrategyValues(CtiCCStrategyPtr strategy);

    vector <CtiCCMonitorPointPtr>& getMultipleMonitorPoints() {return _multipleMonitorPoints;};

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
    static const string MultiVoltControlUnits;
    static const string MultiVoltVarControlUnits;
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
    LONG   _altDualSubId;
    DOUBLE _altSubControlValue;
    LONG   _switchOverPointId;
    BOOL   _switchOverStatus;
    BOOL   _dualBusEnable;
    LONG   _eventSeq;
    BOOL   _multiMonitorFlag;
    
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
      DOUBLE _peakVARlag;
      DOUBLE _offpkVARlag;
      DOUBLE _peakVARlead;
      DOUBLE _offpkVARlead;
      DOUBLE _peakpfsetpoint;
      DOUBLE _offpkpfsetpoint;

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
    LONG _currentwattpointquality;
    LONG _currentvoltpointquality;
    BOOL _waivecontrolflag;
    BOOL _integrateflag;
    LONG _integrateperiod;

    string _additionalFlags;
    LONG _currentVerificationCapBankId;
    LONG _currentVerificationFeederId; 
    std:: vector <CtiCCFeeder*> _ccfeeders;

    BOOL _verificationFlag;
    BOOL _performingVerificationFlag;
    BOOL _verificationDoneFlag;
    BOOL _overlappingSchedulesVerificationFlag;
    BOOL _preOperationMonitorPointScanFlag;
    BOOL _operationSentWaitFlag;
    BOOL _postOperationMonitorPointScanFlag;
    BOOL _reEnableBusFlag;
    BOOL _waitForReCloseDelayFlag;
    BOOL _waitToFinishRegularControlFlag;
    BOOL _maxDailyOpsHitFlag;
    BOOL _ovUvDisabledFlag;

    LONG _currentCapBankToVerifyAssumedOrigState;
    int _verificationStrategy;
    LONG _capBankToVerifyInactivityTime;

    DOUBLE _targetvarvalue;
    string _solution;  //text field to be added to messaging indicating status/thinking
    string _parentControlUnits;
    string _parentName;
    LONG _displayOrder;
    

    DOUBLE _altSubVoltVal;
    DOUBLE _altSubVarVal;
    DOUBLE _altSubWattVal;
    CtiTime _lastVerificationCheck;

    DOUBLE _iVControlTot;
    LONG  _iVCount;
    DOUBLE _iWControlTot;
    LONG  _iWCount;

    DOUBLE _iVControl;
    DOUBLE _iWControl;

    BOOL _usePhaseData;
    LONG _phaseBid;
    LONG _phaseCid;

    DOUBLE _phaseAvalue;
    DOUBLE _phaseBvalue;
    DOUBLE _phaseCvalue;
    DOUBLE _phaseAvalueBeforeControl;
    DOUBLE _phaseBvalueBeforeControl;
    DOUBLE _phaseCvalueBeforeControl;


    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    void restore(RWDBReader& rdr);
    string doubleToString(DOUBLE doubleVal);


    std::list <long> _pointIds;
    //vector <long> _multipleMonitorPoints;
    std::vector <CtiCCMonitorPointPtr> _multipleMonitorPoints;


    std::map <long, CtiCCMonitorPointPtr> _mpid_mp_map;
    std::map <long, CtiCCPointResponsePtr> _cbid_prmap_map;


};


//typedef shared_ptr<CtiCCSubstationBus> CtiCCSubstationBusPtr;
typedef CtiCCSubstationBus* CtiCCSubstationBusPtr;
#endif
