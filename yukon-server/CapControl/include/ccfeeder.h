/*---------------------------------------------------------------------------
        Filename:  ccfeeder.h
        
        Programmer:  Josh Wolberg
        
        Description:    Header file for CtiCCFeeder
                        CtiCCFeeder maintains the state and handles
                        the persistence of strategies for Cap Control.                             

        Initial Date:  8/27/2001
        
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/

#ifndef CTICCFEEDERIMPL_H
#define CTICCFEEDERIMPL_H

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/db/db.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h> 
#include <rw/sortvec.h> 
#include <list>

#include "dbaccess.h"
#include "connection.h"
#include "types.h"
#include "observe.h"
#include "ccmonitorpoint.h"
#include "cccapbank.h"
#include "msg_pcrequest.h"
#include "msg_cmd.h"
#include "ccstrategy.h"
#include "sorted_vector.h"


//For Sorted Vector, the vector will use this to determine position in the vector.
struct CtiCCCapBank_less 
{
    bool operator()( const CtiCCCapBank* _X , const CtiCCCapBank *_Y)
        { return ( _X->getControlOrder() < _Y->getControlOrder() ); }
};
struct CtiCCCapBank_lessClose 
{
    bool operator()( const CtiCCCapBank* _X , const CtiCCCapBank *_Y)
        { return ( _X->getCloseOrder() < _Y->getCloseOrder() ); }
};
struct CtiCCCapBank_lessTrip 
{
    bool operator()( const CtiCCCapBank* _X , const CtiCCCapBank *_Y)
        { return ( _X->getTripOrder() < _Y->getTripOrder() ); }
};
//Typedef for Sanity using sorted vectors
typedef codeproject::sorted_vector<CtiCCCapBank*,false,CtiCCCapBank_less> CtiCCCapBank_SVector;
typedef codeproject::sorted_vector<CtiCCCapBank*,false,CtiCCCapBank_lessClose> CtiCCCapBank_SCloseVector;
typedef codeproject::sorted_vector<CtiCCCapBank*,false,CtiCCCapBank_lessTrip> CtiCCCapBank_STripVector;


template<class T>
struct FeederVARComparison
{
	bool operator()(const T& x, const T& y) const
	{
        bool returnBoolean = false;

        // if the feeders are in different var categories,
        // we don't need the offset to determine which feeder should be first
		if( x.getBusOptimizedVarCategory() != y.getBusOptimizedVarCategory() )
        {
            returnBoolean = x.getBusOptimizedVarCategory() > y.getBusOptimizedVarCategory();
        }
        // if the feeders are in the same var categories,
        // we need to figure out which has the lower var offset
        else
        {
            returnBoolean = x.getBusOptimizedVarOffset() > y.getBusOptimizedVarOffset();
        }

        return returnBoolean;
	}
};

class CtiCCFeeder : public RWCollectable
{

public:

RWDECLARE_COLLECTABLE( CtiCCFeeder )

    CtiCCFeeder();
    CtiCCFeeder(RWDBReader& rdr);
    CtiCCFeeder(const CtiCCFeeder& feeder);

    virtual ~CtiCCFeeder();

    LONG getPAOId() const;
    const string& getPAOCategory() const;
    const string& getPAOClass() const;
    const string& getPAOName() const;
    const string& getPAOType() const;
    const string& getPAODescription() const;
    BOOL getDisableFlag() const;
    LONG getParentId() const;
    LONG getStrategyId() const;
    const string& getStrategyName() const;
    const string& getControlMethod() const;
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
    LONG getDisplayOrder() const;
    BOOL getNewPointDataReceivedFlag() const;
    const CtiTime& getLastCurrentVarPointUpdateTime() const;
    LONG getEstimatedVarLoadPointId() const;
    DOUBLE getEstimatedVarLoadPointValue() const;
    LONG getDailyOperationsAnalogPointId() const;
    LONG getPowerFactorPointId() const;
    LONG getEstimatedPowerFactorPointId() const;
    LONG getCurrentDailyOperations() const;
    BOOL getRecentlyControlledFlag() const;
    const CtiTime& getLastOperationTime() const;
    DOUBLE getVarValueBeforeControl() const;
    LONG getLastCapBankControlledDeviceId() const;
    LONG getBusOptimizedVarCategory() const;
    DOUBLE getBusOptimizedVarOffset() const;
    DOUBLE getPowerFactorValue() const;
    DOUBLE getKVARSolution() const;
    DOUBLE getEstimatedPowerFactorValue() const;
    LONG getCurrentVarPointQuality() const;
    LONG getCurrentWattPointQuality() const;
    LONG getCurrentVoltPointQuality() const;
    BOOL getWaiveControlFlag() const;
    const string& getParentControlUnits() const;
    const string& getParentName() const;
    LONG getDecimalPlaces() const;
    BOOL getPeakTimeFlag() const;
    BOOL getPorterRetFailFlag() const;
    LONG getEventSequence() const;
    BOOL getMultiMonitorFlag() const;
    BOOL getVerificationFlag() const;
    BOOL getPerformingVerificationFlag() const;
    BOOL getVerificationDoneFlag() const;
    BOOL getPreOperationMonitorPointScanFlag() const;
    BOOL getOperationSentWaitFlag() const;
    BOOL getPostOperationMonitorPointScanFlag() const;
    BOOL getWaitForReCloseDelayFlag() const;
    BOOL getMaxDailyOpsHitFlag() const;
    BOOL getOvUvDisabledFlag() const;
    BOOL getCorrectionNeededNoBankAvailFlag() const;
    BOOL getLikeDayControlFlag() const;
    LONG getCurrentVerificationCapBankId() const;
    LONG getCurrentVerificationCapBankOrigState() const;
    DOUBLE getTargetVarValue() const;
    const string& getSolution() const;
    BOOL getIntegrateFlag() const;
    LONG getIntegratePeriod() const;
    BOOL getLikeDayFallBack() const;
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
    const CtiTime& getLastVarPointTime() const;
    const CtiTime& getLastWattPointTime() const;
    const CtiTime& getLastVoltPointTime() const;


    CtiCCCapBank_SVector& getCCCapBanks();
    void deleteCCCapBank(long capBankId);


    CtiCCFeeder& setPAOId(LONG id);
    CtiCCFeeder& setPAOCategory(const string& category);
    CtiCCFeeder& setPAOClass(const string& pclass);
    CtiCCFeeder& setPAOName(const string& name);
    CtiCCFeeder& setPAOType(const string& type);
    CtiCCFeeder& setPAODescription(const string& description);
    CtiCCFeeder& setDisableFlag(BOOL disable);
    CtiCCFeeder& setParentId(LONG parentId);
    CtiCCFeeder& setStrategyId(LONG strategyId);
    CtiCCFeeder& setStrategyName(const string& strategyName);
    CtiCCFeeder& setControlMethod(const string& method);
    CtiCCFeeder& setMaxDailyOperation(LONG max);
    CtiCCFeeder& setMaxOperationDisableFlag(BOOL maxopdisable);
    CtiCCFeeder& setPeakLag(DOUBLE peak);
    CtiCCFeeder& setOffPeakLag(DOUBLE offpeak);
    CtiCCFeeder& setPeakLead(DOUBLE peak);
    CtiCCFeeder& setOffPeakLead(DOUBLE offpeak);

    CtiCCFeeder& setPeakVARLag(DOUBLE peak);
    CtiCCFeeder& setOffPeakVARLag(DOUBLE offpeak);
    CtiCCFeeder& setPeakVARLead(DOUBLE peak);
    CtiCCFeeder& setOffPeakVARLead(DOUBLE offpeak);
    CtiCCFeeder& setPeakPFSetPoint(DOUBLE peak);
    CtiCCFeeder& setOffPeakPFSetPoint(DOUBLE offpeak);

    CtiCCFeeder& setPeakStartTime(LONG starttime);
    CtiCCFeeder& setPeakStopTime(LONG stoptime);
    CtiCCFeeder& setCurrentVarLoadPointId(LONG currentvarid);
    CtiCCFeeder& setCurrentVarLoadPointValue(DOUBLE currentvarval);
    CtiCCFeeder& setCurrentWattLoadPointId(LONG currentwattid);
    CtiCCFeeder& setCurrentWattLoadPointValue(DOUBLE currentwattval);
    CtiCCFeeder& setCurrentVoltLoadPointId(LONG currentvoltid);
    CtiCCFeeder& setCurrentVoltLoadPointValue(DOUBLE currentvoltval);
    CtiCCFeeder& setControlInterval(LONG interval);
    CtiCCFeeder& setMaxConfirmTime(LONG confirm);
    CtiCCFeeder& setMinConfirmPercent(LONG confirm);
    CtiCCFeeder& setFailurePercent(LONG failure);
    CtiCCFeeder& setDaysOfWeek(const string& days);
    CtiCCFeeder& setMapLocationId(const string& maplocation);
    CtiCCFeeder& setControlUnits(const string& contunit);
    CtiCCFeeder& setControlDelayTime(LONG delay);
    CtiCCFeeder& setControlSendRetries(LONG retries);
    CtiCCFeeder& setDisplayOrder(LONG order);
    CtiCCFeeder& setNewPointDataReceivedFlag(BOOL newpointdatareceived);
    CtiCCFeeder& setLastCurrentVarPointUpdateTime(const CtiTime& lastpointupdate);
    CtiCCFeeder& setEstimatedVarLoadPointId(LONG estimatedvarid);
    CtiCCFeeder& setEstimatedVarLoadPointValue(DOUBLE estimatedvarval);
    CtiCCFeeder& setDailyOperationsAnalogPointId(LONG opspointid);
    CtiCCFeeder& setPowerFactorPointId(LONG pfpointid);
    CtiCCFeeder& setEstimatedPowerFactorPointId(LONG epfpointid);
    CtiCCFeeder& setCurrentDailyOperations(LONG operations);
    CtiCCFeeder& setRecentlyControlledFlag(BOOL recentlycontrolled);
    CtiCCFeeder& setLastOperationTime(const CtiTime& lastoperation);
    CtiCCFeeder& setVarValueBeforeControl(DOUBLE oldvarval);
    CtiCCFeeder& setLastCapBankControlledDeviceId(LONG lastcapbank);
    //don't think we want public setters for these
    //CtiCCFeeder& setBusOptimizedVarCategory(LONG varcategory);
    //CtiCCFeeder& setBusOptimizedVarOffset(DOUBLE varoffset);
    CtiCCFeeder& setPowerFactorValue(DOUBLE pfval);
    CtiCCFeeder& setKVARSolution(DOUBLE solution);
    CtiCCFeeder& setEstimatedPowerFactorValue(DOUBLE epfval);
    CtiCCFeeder& setCurrentVarPointQuality(LONG cvpq);
    CtiCCFeeder& setCurrentWattPointQuality(LONG cwpq);
    CtiCCFeeder& setCurrentVoltPointQuality(LONG cvpq);
    CtiCCFeeder& setWaiveControlFlag(BOOL waive);
    CtiCCFeeder& setParentControlUnits(const string& parentControlUnits);
    CtiCCFeeder& setParentName(const string& parentName);
    CtiCCFeeder& setDecimalPlaces(LONG decimalPlaces);
    CtiCCFeeder& setPeakTimeFlag(BOOL peakTimeFlag);
    CtiCCFeeder& setPorterRetFailFlag(BOOL flag);
    CtiCCFeeder& setEventSequence(LONG eventSeq);
    CtiCCFeeder& setMultiMonitorFlag(BOOL flag);
    CtiCCFeeder& setVerificationFlag(BOOL verificationFlag);
    CtiCCFeeder& setPerformingVerificationFlag(BOOL performingVerificationFlag);
    CtiCCFeeder& setVerificationDoneFlag(BOOL verificationDoneFlag);
    CtiCCFeeder& setPreOperationMonitorPointScanFlag( BOOL flag);
    CtiCCFeeder& setOperationSentWaitFlag( BOOL flag);
    CtiCCFeeder& setPostOperationMonitorPointScanFlag( BOOL flag);
    CtiCCFeeder& setWaitForReCloseDelayFlag(BOOL flag);
    CtiCCFeeder& setMaxDailyOpsHitFlag(BOOL flag);
    CtiCCFeeder& setOvUvDisabledFlag(BOOL flag);
    CtiCCFeeder& setCorrectionNeededNoBankAvailFlag(BOOL flag);
    CtiCCFeeder& setLikeDayControlFlag(BOOL flag);
    CtiCCFeeder& setCurrentVerificationCapBankId(LONG capBankId);
    CtiCCFeeder& setCurrentVerificationCapBankState(LONG status);
    CtiCCFeeder& setTargetVarValue(DOUBLE value);
    CtiCCFeeder& setSolution(const string& text);
    CtiCCFeeder& setIntegrateFlag(BOOL flag);
    CtiCCFeeder& setIntegratePeriod(LONG period);
    CtiCCFeeder& setLikeDayFallBack(BOOL flag);
    CtiCCFeeder& setIVControlTot(DOUBLE value);
    CtiCCFeeder& setIVCount(LONG value);
    CtiCCFeeder& setIWControlTot(DOUBLE value);
    CtiCCFeeder& setIWCount(LONG value);
    CtiCCFeeder& setIVControl(DOUBLE value);
    CtiCCFeeder& setIWControl(DOUBLE value);
    CtiCCFeeder& setUsePhaseData(BOOL flag);
    CtiCCFeeder& setPhaseBId(LONG pointid);
    CtiCCFeeder& setPhaseCId(LONG pointid);
    CtiCCFeeder& setPhaseAValue(DOUBLE value);
    CtiCCFeeder& setPhaseBValue(DOUBLE value);
    CtiCCFeeder& setPhaseCValue(DOUBLE value);    
    CtiCCFeeder& setPhaseAValueBeforeControl(DOUBLE value);
    CtiCCFeeder& setPhaseBValueBeforeControl(DOUBLE value);
    CtiCCFeeder& setPhaseCValueBeforeControl(DOUBLE value);
    CtiCCFeeder& setLastWattPointTime(const CtiTime& lastpointupdate);
    CtiCCFeeder& setLastVoltPointTime(const CtiTime& lastpointupdate);

    CtiCCCapBank* findCapBankToChangeVars(DOUBLE kvarSolution);
    CtiRequestMsg* createIncreaseVarRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, string textInfo, DOUBLE kvarBefore);
    CtiRequestMsg* createDecreaseVarRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, string textInfo, DOUBLE kvarBefore);
    BOOL capBankControlStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, LONG minConfirmPercent, LONG failurePercent, DOUBLE varValueBeforeControl, DOUBLE currentVarLoadPointValue, LONG currentVarPointQuality);
    BOOL capBankControlPerPhaseStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, LONG minConfirmPercent, 
                                                     LONG failurePercent, //DOUBLE varValueBeforeControl, DOUBLE currentVarLoadPointValue, 
                                                     LONG currentVarPointQuality, DOUBLE varAValueBeforeControl, DOUBLE varBValueBeforeControl,
                                                     DOUBLE varCValueBeforeControl, DOUBLE varAValue, DOUBLE varBValue, DOUBLE varCValue) ;
    string createPhaseControlStatusUpdateText(const int capControlStatus, DOUBLE varAValue,DOUBLE varBValue, DOUBLE varCValue, 
                                                  DOUBLE ratioA, DOUBLE ratioB, DOUBLE ratioC);
    string createControlStatusUpdateText(const int capControlStatus, DOUBLE varAValue, DOUBLE ratioA);
    string createPhaseVarText(DOUBLE aValue,DOUBLE bValue, DOUBLE cValue, FLOAT multiplier);
    string createVarText(DOUBLE aValue, FLOAT multiplier);
    BOOL isPeakDay();
    BOOL isPastMaxConfirmTime(const CtiTime& currentDateTime, LONG maxConfirmTime, LONG feederRetries);
    BOOL checkForAndProvideNeededIndividualControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages, 
                                                   BOOL peakTimeFlag, LONG decimalPlaces, const string& controlUnits,
                                                   BOOL dailyMaxOpsHitFlag);
    void checkForAndReorderFeeder();
    DOUBLE figureCurrentSetPoint(const CtiTime& currentDateTime);
    BOOL isPeakTime(const CtiTime& currentDateTime);
    BOOL isControlPoint(LONG pointid);
    void updateIntegrationVPoint(const CtiTime &currentDateTime, const CtiTime &nextCheckTime);
    void updateIntegrationWPoint(const CtiTime &currentDateTime, const CtiTime &nextCheckTime);
    CtiCCFeeder& figureEstimatedVarLoadPointValue();
    BOOL isAlreadyControlled(LONG minConfirmPercent);
    void fillOutBusOptimizedInfo(BOOL peakTimeFlag);
    BOOL attemptToResendControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages, LONG maxConfirmTime);
    BOOL checkForAndPerformVerificationSendRetry(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages, LONG maxConfirmTime, LONG sendRetries);
    BOOL checkMaxDailyOpCountExceeded();
    BOOL voltControlBankSelectProcess(CtiCCMonitorPoint* point, CtiMultiMsg_vec &pointChanges, CtiMultiMsg_vec &ccEvents, CtiMultiMsg_vec& pilMessages);
    void updatePointResponsePreOpValues(CtiCCCapBank* capBank);
    void updatePointResponseDeltas();
    BOOL areAllMonitorPointsNewEnough(const CtiTime& currentDateTime);
    BOOL isScanFlagSet();
    ULONG getMonitorPointScanTime();
    BOOL scanAllMonitorPoints();
    void analyzeMultiVoltFeeder(const CtiTime& currentDateTime, LONG minConfirmPercent, LONG failurePercent, LONG maxConfirmTime, LONG sendRetries, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    BOOL areAllMonitorPointsInVoltageRange(CtiCCMonitorPoint* oorPoint);
    BOOL areOtherMonitorPointResponsesOk(LONG mPointID, CtiCCCapBank* potentialCap, int action);

    string createTextString(const string& controlMethod, int control, DOUBLE controlValue, DOUBLE monitorValue);

    CtiRequestMsg* createIncreaseVarVerificationRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, string textInfo, DOUBLE kvarBefore);
    CtiRequestMsg* createDecreaseVarVerificationRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, string textInfo, DOUBLE kvarBefore);
    CtiCCFeeder& startVerificationOnCapBank(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    BOOL sendNextCapBankVerificationControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);

    std::list <LONG>* getPointIds() {return &_pointIds;};

    BOOL isVerificationAlreadyControlled(LONG minConfirmPercent);
    BOOL areThereMoreCapBanksToVerify();
    CtiCCFeeder& getNextCapBankToVerify();

    BOOL capBankVerificationStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, LONG minConfirmPercent, LONG failurePercent, DOUBLE varValueBeforeControl, DOUBLE currentVarLoadPointValue, LONG currentVarPointQuality);
    CtiCCFeeder& addAllFeederPointsToMsg(CtiCommandMsg *pointAddMsg);
    CtiCCCapBank* getMonitorPointParentBank(CtiCCMonitorPoint* point);
    
    BOOL isDirty() const;
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime);

    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCFeeder& operator=(const CtiCCFeeder& right);

    bool  operator==(const CtiCCFeeder& right) const;
    bool  operator!=(const CtiCCFeeder& right) const;

    CtiCCFeeder* replicate() const;


    void setDynamicData(RWDBReader& rdr);
    void setStrategyValues(CtiCCStrategyPtr strategy);

    vector <CtiCCMonitorPointPtr>& getMultipleMonitorPoints() {return _multipleMonitorPoints;};


    //Possible states
    /*static const string Enabled;
    static const string Disabled;*/

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
    LONG _parentId; //subBusId
    LONG _strategyId;
    BOOL _multiMonitorFlag;

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

    DOUBLE _peakVARlag;  
    DOUBLE _offpkVARlag; 
    DOUBLE _peakVARlead; 
    DOUBLE _offpkVARlead;
    DOUBLE _peakpfsetpoint; 
    DOUBLE _offpkpfsetpoint;
    
    LONG _displayorder;
    BOOL _newpointdatareceivedflag;
    CtiTime _lastcurrentvarpointupdatetime;
    LONG _estimatedvarloadpointid;
    DOUBLE _estimatedvarloadpointvalue;
    LONG _dailyoperationsanalogpointid;
    LONG _powerfactorpointid;
    LONG _estimatedpowerfactorpointid;
    LONG _currentdailyoperations;
    BOOL _recentlycontrolledflag;
    CtiTime _lastoperationtime;
    DOUBLE _varvaluebeforecontrol;
    LONG _lastcapbankcontrolleddeviceid;
    LONG _busoptimizedvarcategory;
    DOUBLE _busoptimizedvaroffset;
    DOUBLE _powerfactorvalue;
    DOUBLE _kvarsolution;
    DOUBLE _estimatedpowerfactorvalue;
    LONG _currentvarpointquality;
    LONG _currentwattpointquality;
    LONG _currentvoltpointquality;
    BOOL _waivecontrolflag;
    BOOL _integrateflag;
    LONG _integrateperiod;
    BOOL _likedayfallback;

    string _parentControlUnits;
    string _parentName;
    LONG _decimalPlaces;
    BOOL _peakTimeFlag;

    CtiCCCapBank_SVector _cccapbanks;

    //verification info
    string _additionalFlags;

    BOOL _verificationFlag;
    BOOL _performingVerificationFlag;
    BOOL _verificationDoneFlag;
    BOOL _preOperationMonitorPointScanFlag;
    BOOL _operationSentWaitFlag;
    BOOL _postOperationMonitorPointScanFlag;
    BOOL _porterRetFailFlag;
    BOOL _waitForReCloseDelayFlag;
    BOOL _maxDailyOpsHitFlag;
    BOOL _ovUvDisabledFlag;
    BOOL _correctionNeededNoBankAvailFlag;
    BOOL _likeDayControlFlag;

    LONG   _eventSeq;

    LONG _currentVerificationCapBankId;
    LONG _currentCapBankToVerifyAssumedOrigState;

    DOUBLE _targetvarvalue;
    string _solution;

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

    CtiTime _lastWattPointTime;
    CtiTime _lastVoltPointTime;


    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    void restore(RWDBReader& rdr);
    string doubleToString(DOUBLE doubleVal, LONG decimalPlaces);

    std::list <long> _pointIds;
    vector <CtiCCMonitorPointPtr> _multipleMonitorPoints;
};


//typedef shared_ptr<CtiCCFeeder> CtiCCFeederPtr;
typedef CtiCCFeeder* CtiCCFeederPtr;
#endif
