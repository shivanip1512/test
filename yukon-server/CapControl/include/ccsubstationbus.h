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
using boost::shared_ptr;

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
#include "ControlStrategy.h"
#include "TimeOfDayStrategy.h"
#include "ccmonitorpoint.h"

typedef std::vector<CtiCCFeederPtr> CtiFeeder_vec;
//For Sorted Vector, the vector will use this to determine position in the vector.
struct CtiFeeder_less
{
    bool operator()( const CtiCCFeeder* _X , const CtiCCFeeder *_Y)
        { return ( _X->getDisplayOrder() < _Y->getDisplayOrder() ); }
};
//Typedef for Sanity using sorted vectors
typedef codeproject::sorted_vector<CtiCCFeeder*,false,CtiFeeder_less> CtiFeeder_SVector;

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
    CtiCCSubstationBus(RWDBReader& rdr, StrategyPtr strategy);
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
    LONG getCurrentVarLoadPointId() const;
    DOUBLE getCurrentVarLoadPointValue() const;
    DOUBLE getRawCurrentVarLoadPointValue() const;
    LONG getCurrentWattLoadPointId() const;
    DOUBLE getCurrentWattLoadPointValue() const;
    DOUBLE getRawCurrentWattLoadPointValue() const;
    LONG getCurrentVoltLoadPointId() const;
    DOUBLE getCurrentVoltLoadPointValue() const;
    const string& getMapLocationId() const;
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
    BOOL getPrimaryBusFlag() const;
    BOOL getDualBusEnable() const;
    LONG getEventSequence() const;
    BOOL getReEnableBusFlag() const;
    BOOL getMultiMonitorFlag() const;
    BOOL getWaitForReCloseDelayFlag() const;
    BOOL getWaitToFinishRegularControlFlag() const;
    BOOL getMaxDailyOpsHitFlag() const;
    BOOL getOvUvDisabledFlag() const;
    BOOL getCorrectionNeededNoBankAvailFlag() const;
    BOOL getLikeDayControlFlag() const;
    BOOL getVoltReductionFlag() const;
    LONG getVoltReductionControlId() const;
    BOOL getSendMoreTimeControlledCommandsFlag() const;


    const string& getSolution() const;
    DOUBLE getTargetVarValue() const;
    const string& getParentControlUnits() const;
    const string& getParentName() const;
    LONG getDisplayOrder() const;
    DOUBLE getIVControlTot() const;
    LONG getIVCount() const;
    DOUBLE getIWControlTot() const;
    LONG getIWCount() const;
    DOUBLE getIVControl() const;
    DOUBLE getIWControl() const;
    BOOL getUsePhaseData() const;
    LONG getPhaseBId() const;
    LONG getPhaseCId() const;
    BOOL getTotalizedControlFlag() const;
    DOUBLE getPhaseAValue() const;
    DOUBLE getPhaseBValue() const;
    DOUBLE getPhaseCValue() const;
    DOUBLE getPhaseAValueBeforeControl() const;
    DOUBLE getPhaseBValueBeforeControl() const;
    DOUBLE getPhaseCValueBeforeControl() const;
    const CtiTime& getLastWattPointTime() const;
    const CtiTime& getLastVoltPointTime() const;

    const CtiRegression& getRegression();
    const CtiRegression& getRegressionA();
    const CtiRegression& getRegressionB();
    const CtiRegression& getRegressionC();

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

// OK!
    LONG getControlSendRetries() const;


    CtiCCSubstationBus& setCurrentVarLoadPointId(LONG currentvarid);
    CtiCCSubstationBus& setCurrentVarLoadPointValue(DOUBLE currentvarval, CtiTime timestamp);
    CtiCCSubstationBus& setCurrentWattLoadPointId(LONG currentwattid);
    CtiCCSubstationBus& setCurrentWattLoadPointValue(DOUBLE currentwattval);
    CtiCCSubstationBus& setCurrentVoltLoadPointId(LONG currentvoltid);
    CtiCCSubstationBus& setCurrentVoltLoadPointValue(DOUBLE currentvoltval);
    CtiCCSubstationBus& setMapLocationId(const string& maplocation);
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
    CtiCCSubstationBus& setCurrentDailyOperationsAndSendMsg(LONG operations, CtiMultiMsg_vec& pointChanges);
    CtiCCSubstationBus& setPeakTimeFlag(LONG peaktime);
    CtiCCSubstationBus& setRecentlyControlledFlag(BOOL recentlycontrolled);
    CtiCCSubstationBus& setLastOperationTime(const CtiTime& lastoperation);
    CtiCCSubstationBus& setLastVerificationCheck(const CtiTime& checkTime);
    CtiCCSubstationBus& setVarValueBeforeControl(DOUBLE oldvarval);
    CtiCCSubstationBus& setLastFeederControlledPAOId(LONG lastfeederpao);
    CtiCCSubstationBus& setLastFeederControlled(LONG lastfeederpao);
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
    CtiCCSubstationBus& setPrimaryBusFlag(BOOL status);
    CtiCCSubstationBus& setDualBusEnable(BOOL flag);
    CtiCCSubstationBus& setEventSequence(LONG eventSeq);
    CtiCCSubstationBus& setReEnableBusFlag(BOOL flag);
    CtiCCSubstationBus& setMultiMonitorFlag(BOOL flag);
    CtiCCSubstationBus& setWaitForReCloseDelayFlag(BOOL flag);
    CtiCCSubstationBus& setWaitToFinishRegularControlFlag(BOOL flag);
    CtiCCSubstationBus& setMaxDailyOpsHitFlag(BOOL flag);
    CtiCCSubstationBus& setOvUvDisabledFlag(BOOL flag);
    CtiCCSubstationBus& setCorrectionNeededNoBankAvailFlag(BOOL flag);
    CtiCCSubstationBus& setLikeDayControlFlag(BOOL flag);
    CtiCCSubstationBus& setVoltReductionFlag(BOOL flag);
    CtiCCSubstationBus& setVoltReductionControlId(LONG pointid);
    CtiCCSubstationBus& setSendMoreTimeControlledCommandsFlag(BOOL flag);

    CtiCCSubstationBus& setAllAltSubValues(DOUBLE volt, DOUBLE var, DOUBLE watt);
    CtiCCSubstationBus& setSolution(const string& text);
    CtiCCSubstationBus& setTargetVarValue(DOUBLE value);
    CtiCCSubstationBus& setParentControlUnits(const string& parentControlUnits);
    CtiCCSubstationBus& setParentName(const string& parentName);
    CtiCCSubstationBus& setDisplayOrder(LONG displayOrder);
    CtiCCSubstationBus& setIVControlTot(DOUBLE value);
    CtiCCSubstationBus& setIVCount(LONG value);
    CtiCCSubstationBus& setIWControlTot(DOUBLE value);
    CtiCCSubstationBus& setIWCount(LONG value);
    CtiCCSubstationBus& setIVControl(DOUBLE value);
    CtiCCSubstationBus& setIWControl(DOUBLE value);
    CtiCCSubstationBus& setUsePhaseData(BOOL flag);
    CtiCCSubstationBus& setPhaseBId(LONG pointid);
    CtiCCSubstationBus& setPhaseCId(LONG pointid);
    CtiCCSubstationBus& setTotalizedControlFlag(BOOL flag);
    CtiCCSubstationBus& setPhaseAValue(DOUBLE value, CtiTime time);
    CtiCCSubstationBus& setPhaseBValue(DOUBLE value, CtiTime time);
    CtiCCSubstationBus& setPhaseCValue(DOUBLE value, CtiTime time);
    CtiCCSubstationBus& setPhaseAValueBeforeControl(DOUBLE value);
    CtiCCSubstationBus& setPhaseBValueBeforeControl(DOUBLE value);
    CtiCCSubstationBus& setPhaseCValueBeforeControl(DOUBLE value);
    CtiCCSubstationBus& setLastWattPointTime(const CtiTime& lastpointupdate);
    CtiCCSubstationBus& setLastVoltPointTime(const CtiTime& lastpointupdate);

    int getLtcId();
    void setLtcId(int ltcId);

    void reOrderFeederDisplayOrders();
    void figureAndSetTargetVarValue();
    void figureAndSetPowerFactorByFeederValues();
    DOUBLE getSetPoint();
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
    BOOL maxOperationsHitDisableBus();
    void checkForMaxDailyOpsHit();
    BOOL isAnyBankClosed();
    CtiCCSubstationBus& checkForAndProvideNeededControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    CtiCCSubstationBus& checkForAndProvideNeededFallBackControl(const CtiTime& currentDateTime,
                        CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    CtiCCSubstationBus& checkForAndProvideNeededTimeOfDayControl(const CtiTime& currentDateTime,
                            CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
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

    LONG getAlterateBusIdForPrimary() const;
    BOOL isBusPerformingVerification();
    BOOL isBusReadyToStartVerification();
    BOOL isBusVerificationAlreadyStarted();
    BOOL isVerificationPastMaxConfirmTime(const CtiTime& currentDateTime);
    BOOL areThereMoreCapBanksToVerify(CtiMultiMsg_vec& ccEvents);
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

    CtiCCSubstationBus& checkAndUpdateRecentlyControlledFlag();

    CtiCCSubstationBus& addAllSubPointsToMsg(std::list<int>& pointAddMsg);
    CtiCCSubstationBus& verifyControlledStatusFlags();
    LONG getNextTODStartTime();

    list <LONG>* getPointIds() {return &_pointIds;};

    CtiCCSubstationBus& setVerificationAlreadyStartedFlag(BOOL verificationFlag);
    void setVerificationStrategy(int verificationStrategy);
    int getVerificationStrategy(void) const;
    void setVerificationDisableOvUvFlag(BOOL flag);
    BOOL getVerificationDisableOvUvFlag(void) const;
    string getVerificationString();
    void setCapBankInactivityTime(LONG capBankToVerifyInactivityTime);
    LONG getCapBankInactivityTime(void) const;

    BOOL capBankVerificationStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents);
    BOOL capBankVerificationPerPhaseStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents);
    void createStatusUpdateMessages(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiCCCapBankPtr capBank,
                                    CtiCCFeederPtr feeder, string text, string additional, bool verifyFlag,
                                    DOUBLE before, DOUBLE after, DOUBLE change, DOUBLE phaseA, DOUBLE phaseB, DOUBLE phaseC);
    void createCannotControlBankText(string text, string commandString, CtiMultiMsg_vec& ccEvents);
    void performDataOldAndFallBackNecessaryCheck();

    BOOL isDirty() const;
    void dumpDynamicData();
    void dumpDynamicData(RWDBConnection& conn, CtiTime& currentDateTime);
    void setDynamicData(RWDBReader& rdr);

    std::vector <CtiCCMonitorPointPtr>& getMultipleMonitorPoints() {return _multipleMonitorPoints;};


    CtiCCOperationStats& getOperationStats();
    CtiCCConfirmationStats& getConfirmationStats();


    //Members inherited from RWCollectable
    void restoreGuts(RWvistream& );
    void saveGuts(RWvostream& ) const;

    CtiCCSubstationBus& operator=(const CtiCCSubstationBus& right);

    int operator==(const CtiCCSubstationBus& right) const;
    int operator!=(const CtiCCSubstationBus& right) const;

    CtiCCSubstationBus* replicate() const;

    //static int PeakState;
    //static int OffPeakState;


    void setStrategy(StrategyPtr strategy);
    StrategyPtr getStrategy() const;

    bool isParentOverride() const;
    void setParentOverride(const bool flag);

private:

    StrategyPtr     _strategy;
    bool            _parentOverride;

    LONG _paoid;
    string _paocategory;
    string _paoclass;
    string _paoname;
    string _paotype;
    string _paodescription;
    BOOL _disableflag;
    LONG _parentId;
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
    BOOL   _primaryBusFlag;
    BOOL   _dualBusEnable;
    LONG   _eventSeq;
    BOOL   _multiMonitorFlag;
    string _maplocationid;

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

    string _additionalFlags;
    LONG _currentVerificationCapBankId;
    LONG _currentVerificationFeederId;
    std::vector <CtiCCFeeder*> _ccfeeders;

    int _percentToClose;
    BOOL _likeDayControlFlag;

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
    BOOL _correctionNeededNoBankAvailFlag;
    BOOL _voltReductionFlag;
    BOOL _sendMoreTimeControlledCommandsFlag;

    LONG _voltReductionControlId;
    LONG _currentCapBankToVerifyAssumedOrigState;
    int _verificationStrategy;
    BOOL _disableOvUvVerificationFlag;
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
    BOOL _totalizedControlFlag;

    DOUBLE _phaseAvalue;
    DOUBLE _phaseBvalue;
    DOUBLE _phaseCvalue;
    DOUBLE _phaseAvalueBeforeControl;
    DOUBLE _phaseBvalueBeforeControl;
    DOUBLE _phaseCvalueBeforeControl;

    CtiTime _lastWattPointTime;
    CtiTime _lastVoltPointTime;


    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;

    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    void restore(RWDBReader& rdr);
    string doubleToString(DOUBLE doubleVal);


    std::list <long> _pointIds;
    //vector <long> _multipleMonitorPoints;
    std::vector <CtiCCMonitorPointPtr> _multipleMonitorPoints;

    bool performDataOldAndFallBackNecessaryCheckOnFeeders();

    bool checkForRateOfChange(const CtiRegression& reg, const CtiRegression& regA, const CtiRegression& regB, const CtiRegression& regC);
    CtiRegression regression;
    CtiRegression regressionA;
    CtiRegression regressionB;
    CtiRegression regressionC;

    int _ltcId;

};


//typedef shared_ptr<CtiCCSubstationBus> CtiCCSubstationBusPtr;
typedef CtiCCSubstationBus* CtiCCSubstationBusPtr;
#endif
