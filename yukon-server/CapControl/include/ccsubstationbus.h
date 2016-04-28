#pragma once

#include "Controllable.h"
#include "regression.h"
#include "ccfeeder.h"
#include "TimeOfDayStrategy.h"
#include "EventLogEntry.h"


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


class CtiCCSubstationBus : public Controllable
{
public:
    DECLARE_COLLECTABLE( CtiCCSubstationBus );

    CtiCCSubstationBus( StrategyManager * strategyManager = nullptr );
    CtiCCSubstationBus( Cti::RowReader & rdr, StrategyManager * strategyManager );

    virtual ~CtiCCSubstationBus();

    long getParentId() const;

    Cti::CapControl::PointIdVector getCurrentVarLoadPoints() const;
    long getCurrentVarLoadPointId() const;
    double getCurrentVarLoadPointValue() const;
    double getRawCurrentVarLoadPointValue() const;
    double getTotalizedVarLoadPointValue() const;
    long getCurrentWattLoadPointId() const;
    double getCurrentWattLoadPointValue() const;
    double getRawCurrentWattLoadPointValue() const;
    long getCurrentVoltLoadPointId() const;
    double getCurrentVoltLoadPointValue() const;
    const std::string& getMapLocationId() const;
    long getDecimalPlaces() const;
    const CtiTime& getNextCheckTime() const;
    bool getNewPointDataReceivedFlag() const;
    bool getBusUpdatedFlag() const;
    const CtiTime& getLastCurrentVarPointUpdateTime() const;
    long getEstimatedVarLoadPointId() const;
    double getEstimatedVarLoadPointValue() const;
    long getDailyOperationsAnalogPointId() const;
    long getPowerFactorPointId() const;
    long getEstimatedPowerFactorPointId() const;
    long getCurrentDailyOperations() const;
    bool getPeakTimeFlag() const;
    bool getRecentlyControlledFlag() const;
    const CtiTime& getLastOperationTime() const;
    double getVarValueBeforeControl() const;
    long getLastFeederControlledPAOId() const;
    long getLastFeederControlledPosition() const;
    double getPowerFactorValue() const;
    double getKVARSolution() const;
    double getEstimatedPowerFactorValue() const;
    long getCurrentVarPointQuality() const;
    long getCurrentWattPointQuality() const;
    long getCurrentVoltPointQuality() const;
    bool getWaiveControlFlag() const;
    bool getVerificationFlag() const;
    bool getPerformingVerificationFlag() const;
    bool getVerificationDoneFlag() const;
    long getCurrentVerificationFeederId() const;
    long getCurrentVerificationCapBankId() const;
    long getCurrentVerificationCapBankOrigState() const;
    bool getOverlappingVerificationFlag() const;
    bool getPreOperationMonitorPointScanFlag() const;
    bool getOperationSentWaitFlag() const;
    bool getPostOperationMonitorPointScanFlag() const;
    long getAltDualSubId() const;
    double getAltSubControlValue() const;
    void getAllAltSubValues(double &volt, double &var, double &watt);
    long getSwitchOverPointId() const;
    bool getSwitchOverStatus() const;
    bool getPrimaryBusFlag() const;
    bool getDualBusEnable() const;
    long getEventSequence() const;
    bool getReEnableBusFlag() const;
    bool getMultiMonitorFlag() const;
    bool getWaitForReCloseDelayFlag() const;
    bool getWaitToFinishRegularControlFlag() const;
    bool getMaxDailyOpsHitFlag() const;
    bool getOvUvDisabledFlag() const;
    bool getCorrectionNeededNoBankAvailFlag() const;
    bool getLikeDayControlFlag() const;
    bool getVoltReductionFlag() const;
    long getVoltReductionControlId() const;
    long getDisableBusPointId() const;
    bool getSendMoreTimeControlledCommandsFlag() const;

    const std::string& getSolution() const;
    double getTargetVarValue() const;
    const std::string& getParentControlUnits() const;
    const std::string& getParentName() const;
    long getDisplayOrder() const;
    double getIVControlTot() const;
    long getIVCount() const;
    double getIWControlTot() const;
    long getIWCount() const;
    double getIVControl() const;
    double getIWControl() const;
    bool getUsePhaseData() const;
    long getPhaseBId() const;
    long getPhaseCId() const;
    bool getTotalizedControlFlag() const;
    double getPhaseAValue() const;
    double getPhaseBValue() const;
    double getPhaseCValue() const;
    double getPhaseAValueBeforeControl() const;
    double getPhaseBValueBeforeControl() const;
    double getPhaseCValueBeforeControl() const;
    const CtiTime& getLastWattPointTime() const;
    const CtiTime& getLastVoltPointTime() const;
    long getCommsStatePointId() const;

    const CtiRegression& getRegression();
    const CtiRegression& getRegressionA();
    const CtiRegression& getRegressionB();
    const CtiRegression& getRegressionC();

    CtiFeeder_vec& getCCFeeders();
    const CtiFeeder_vec& getCCFeeders() const;
    std::list<int> getCCFeederIds();
    void deleteCCFeeder(long feederId);

    void setParentId(long parentId);

    long getControlSendRetries() const;

    void setCurrentVarLoadPointId(long currentvarid);
    void setCurrentVarLoadPointValue(double currentvarval, CtiTime timestamp);
    void setCurrentWattLoadPointId(long currentwattid);
    void setCurrentWattLoadPointValue(double currentwattval);
    void setCurrentVoltLoadPointId(long currentvoltid);
    void setCurrentVoltLoadPointValue(double currentvoltval);
    void setMapLocationId(const std::string& maplocation);
    void setDecimalPlaces(long places);
    void figureNextCheckTime();
    void setNewPointDataReceivedFlag(bool newpointdatareceived);
    void setBusUpdatedFlag(bool busupdated);
    void setLastCurrentVarPointUpdateTime(const CtiTime& lastpointupdate);
    void setEstimatedVarLoadPointId(long estimatedvarid);
    void setEstimatedVarLoadPointValue(double estimatedvarval);
    void setDailyOperationsAnalogPointId(long opanalogpointid);
    void setPowerFactorPointId(long pfpointid);
    void setEstimatedPowerFactorPointId(long epfpointid);
    void setCurrentDailyOperations(long operations);
    void setCurrentDailyOperationsAndSendMsg(long operations, CtiMultiMsg_vec& pointChanges);
    void setPeakTimeFlag(bool peaktime);
    void setRecentlyControlledFlag(bool recentlycontrolled);
    void setLastOperationTime(const CtiTime& lastoperation);
    void setLastVerificationCheck(const CtiTime& checkTime);
    void setVarValueBeforeControl(double oldvarval, long originalParentId = 0);
    void setLastFeederControlledPAOId(long lastfeederpao);
    void setLastFeederControlled(long lastfeederpao);
    void setLastFeederControlledPosition(long lastfeederposition);
    void setPowerFactorValue(double pfval);
    void setKVARSolution(double solution);
    void setEstimatedPowerFactorValue(double epfval);
    void setCurrentVarPointQuality(long cvpq);
    void setCurrentWattPointQuality(long cwpq);
    void setCurrentVoltPointQuality(long cvpq);
    void setWaiveControlFlag(bool waive);
    void setOverlappingVerificationFlag( bool overlapFlag);
    void setPreOperationMonitorPointScanFlag( bool flag);
    void setOperationSentWaitFlag( bool flag);
    void setPostOperationMonitorPointScanFlag( bool flag);
    void setAltDualSubId(long altDualSubId);
    void setAltSubControlValue(double controlValue);
    void setSwitchOverPointId(long pointId);
    void setSwitchOverStatus(bool status);
    void setPrimaryBusFlag(bool status);
    void setDualBusEnable(bool flag);
    void setEventSequence(long eventSeq);
    void setReEnableBusFlag(bool flag);
    void setMultiMonitorFlag(bool flag);
    void setWaitForReCloseDelayFlag(bool flag);
    void setWaitToFinishRegularControlFlag(bool flag);
    void setMaxDailyOpsHitFlag(bool flag);
    void setOvUvDisabledFlag(bool flag);
    void setCorrectionNeededNoBankAvailFlag(bool flag);
    void setLikeDayControlFlag(bool flag);
    void setVoltReductionFlag(bool flag);
    void setVoltReductionControlId(long pointid);
    void setDisableBusPointId(long pointid);
    void setSendMoreTimeControlledCommandsFlag(bool flag);

    void setAllAltSubValues(double volt, double var, double watt);
    void setSolution(const std::string& text);
    void setTargetVarValue(double value);
    void setParentControlUnits(const std::string& parentControlUnits);
    void setParentName(const std::string& parentName);
    void setDisplayOrder(long displayOrder);
    void setIVControlTot(double value);
    void setIVCount(long value);
    void setIWControlTot(double value);
    void setIWCount(long value);
    void setIVControl(double value);
    void setIWControl(double value);
    void setUsePhaseData(bool flag);
    void setPhaseBId(long pointid);
    void setPhaseCId(long pointid);
    void setTotalizedControlFlag(bool flag);
    void setPhaseAValue(double value, CtiTime time);
    void setPhaseBValue(double value, CtiTime time);
    void setPhaseCValue(double value, CtiTime time);
    void setPhaseAValueBeforeControl(double value);
    void setPhaseBValueBeforeControl(double value);
    void setPhaseCValueBeforeControl(double value);
    void setLastWattPointTime(const CtiTime& lastpointupdate);
    void setLastVoltPointTime(const CtiTime& lastpointupdate);
    void setCommsStatePointId(long newId);

    void reOrderFeederDisplayOrders();
    void figureAndSetTargetVarValue();
    void figureAndSetPowerFactorByFeederValues();
    double getSetPoint();
    bool isPastMaxConfirmTime(const CtiTime& currentDateTime);
    long getLastFeederControlledSendRetries() const;
    void analyzeMultiVoltBus(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    void analyzeMultiVoltBus1(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    bool performActionMultiPointBus(const CtiTime& currentDateTime);
    bool isVarCheckNeeded(const CtiTime& currentDateTime);
    bool isConfirmCheckNeeded();
    bool capBankControlStatusUpdate(CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents);
    double figureCurrentSetPoint(const CtiTime& currentDateTime);
    bool isPeakTime(const CtiTime& currentDateTime);
    bool isControlPoint(long pointid);
    void updateIntegrationVPoint(const CtiTime &currentDateTime);
    void updateIntegrationWPoint(const CtiTime &currentDateTime);
    void clearOutNewPointReceivedFlags();
    bool maxOperationsHitDisableBus();
    void checkForMaxDailyOpsHit();


    int getNumOfBanksInState(std::set<int> s);
    CtiCCCapBankPtr getPendingCapBank( );
    std::vector<CtiCCCapBankPtr> getAllCapBanks( );
    std::vector<CtiCCCapBankPtr> getAllSwitchedCapBanks( );
    void checkForAndProvideNeededControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    void checkForAndProvideNeededFallBackControl(const CtiTime& currentDateTime,
                        CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    void checkForAndProvideNeededTimeOfDayControl(const CtiTime& currentDateTime,
                            CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    void regularSubstationBusControl(double lagLevel, double leadLevel, const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    void optimizedSubstationBusControl(double lagLevel, double leadLevel, const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    void figureEstimatedVarLoadPointValue();
    bool isAlreadyControlled();
    double convertKQToKVAR(double kq, double kw);
    double convertKVARToKQ(double kvar, double kw);
    static double calculateKVARSolution(const std::string& controlUnits, double setPoint, double varValue, double wattValue);
    bool checkForAndPerformSendRetry(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    bool checkForAndPerformVerificationSendRetry(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    void voltControlProcess();
    void updatePointResponsePreOpValues(CtiCCCapBank* capBank);
    void updatePointResponseDeltas();
    void updatePointResponseDeltas(std::set<long> pointIds);

    bool areAllMonitorPointsNewEnough(const CtiTime& currentDateTime);
    bool isScanFlagSet();
    unsigned long getMonitorPointScanTime();
    bool scanAllMonitorPoints();
    bool isBusAnalysisNeeded(const CtiTime& currentDateTime);
    bool isMultiVoltBusAnalysisNeeded(const CtiTime& currentDateTime);
    bool areAllMonitorPointsInVoltageRange(CtiCCMonitorPointPtr & oorPoint);
    CtiCCCapBank* getMonitorPointParentBankAndFeeder(const CtiCCMonitorPoint & point, CtiCCFeeder* feed);
    bool voltControlBankSelectProcess(const CtiCCMonitorPoint & point, CtiMultiMsg_vec &pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec &pilMessages);
    bool areOtherMonitorPointResponsesOk(long mPointID, CtiCCCapBank* potentialCap, int action);
    bool analyzeBusForVarImprovement(CtiMultiMsg_vec &pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec &pilMessages);

    int getAlterateBusIdForPrimary() const;
    bool isBusPerformingVerification();
    bool isBusReadyToStartVerification();
    bool isBusVerificationAlreadyStarted();
    bool isVerificationPastMaxConfirmTime(const CtiTime& currentDateTime);
    bool areThereMoreCapBanksToVerify(Cti::CapControl::EventLogEntries &ccEvents);
    void startVerificationOnCapBank(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    bool isVerificationAlreadyControlled();
    void analyzeVerificationByFeeder(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages, CtiMultiMsg_vec& capMessages);
    void setCapBanksToVerifyFlags(int verificationStrategy, Cti::CapControl::EventLogEntries &ccEvents);
    bool isBankSelectedByVerificationStrategy(int verificationStrategy, CtiCCCapBankPtr currentCapBank);
    void recompileCapBanksToVerifyList();
    void getNextCapBankToVerify(Cti::CapControl::EventLogEntries &ccEvents);
    bool sendNextCapBankVerificationControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    void setVerificationFlag(bool verificationFlag);
    void setPerformingVerificationFlag(bool performingVerificationFlag);
    void setVerificationDoneFlag(bool verificationDoneFlag);
    void setCurrentVerificationFeederId(long feederId);
    void setCurrentVerificationCapBankId(long capBankId);
    void setCurrentVerificationCapBankState(long status);

    void checkAndUpdateRecentlyControlledFlag();

    void addAllSubPointsToMsg(std::set<long>& pointAddMsg);
    void verifyControlledStatusFlags();
    long getNextTODStartTime();

    void setVerificationAlreadyStartedFlag(bool verificationFlag);
    void setVerificationStrategy( CtiPAOScheduleManager::VerificationStrategy verificationStrategy);
    CtiPAOScheduleManager::VerificationStrategy getVerificationStrategy(void) const;
    void setVerificationDisableOvUvFlag(bool flag);
    bool getVerificationDisableOvUvFlag(void) const;
    std::string getVerificationString();
    void setCapBankInactivityTime(long capBankToVerifyInactivityTime);
    long getCapBankInactivityTime(void) const;

    bool capBankVerificationStatusUpdate(CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents);
    bool capBankVerificationPerPhaseStatusUpdate(CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents);
    void createStatusUpdateMessages(CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiCCCapBankPtr capBank,
                                    CtiCCFeederPtr feeder, std::string text, std::string additional, bool verifyFlag,
                                    double before, double after, double change, double phaseA, double phaseB, double phaseC);
    void createCannotControlBankText(std::string text, std::string commandString, Cti::CapControl::EventLogEntries &ccEvents);
    void performDataOldAndFallBackNecessaryCheck();

    bool addMonitorPoint(long pointId, CtiCCMonitorPointPtr monPoint);
    bool updateExistingMonitorPoint(CtiCCMonitorPointPtr monPoint);

    const std::map <long, CtiCCMonitorPointPtr>& getAllMonitorPoints();
    std::vector <long> getAllMonitorPointIds();
    std::vector <CtiCCMonitorPointPtr> getAllCapBankMonitorPoints();
    void removeAllMonitorPoints();
    CtiCCMonitorPointPtr getMonitorPoint(long pointId);
    Cti::CapControl::PointResponsePtr getPointResponse(Cti::CapControl::PointResponseKey key);
    void updatePointResponse(Cti::CapControl::PointResponseKey key, Cti::CapControl::PointResponsePtr  pResponse);
    void addDefaultPointResponses( std::set< std::pair<long, int> > &requiredPointResponses );

    std::vector<Cti::CapControl::PointResponse> getPointResponsesForDevice(const long deviceId);

    bool isDirty() const;
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);
    void setDynamicData(Cti::RowReader& rdr);

    std::vector <CtiCCMonitorPointPtr>& getMultipleMonitorPoints() {return _multipleMonitorPoints;};


    CtiCCSubstationBus* replicate() const;

    // Added for serialization
    double getAltSubVoltVal() const;
    double getAltSubVarVal() const;
    double getAltSubWattVal() const;
    double getCurrentvoltloadpointvalue() const;
    double getCurrentvarloadpointvalue() const;
    double getCurrentwattloadpointvalue() const;

protected:

    CtiCCSubstationBus( const CtiCCSubstationBus & bus ) = default;
    CtiCCSubstationBus & operator=( const CtiCCSubstationBus & right ) = delete;

private:

    long _parentId;
    long _currentvarloadpointid;
    double _currentvarloadpointvalue;
    long _currentwattloadpointid;
    double _currentwattloadpointvalue;
    long _currentvoltloadpointid;
    double _currentvoltloadpointvalue;
    long   _altDualSubId;
    double _altSubControlValue;
    long   _switchOverPointId;
    bool   _switchOverStatus;
    bool   _primaryBusFlag;
    bool   _dualBusEnable;
    long   _eventSeq;
    bool   _multiMonitorFlag;
    std::string _maplocationid;

    long _decimalplaces;
    CtiTime _nextchecktime;
    bool _newpointdatareceivedflag;
    bool _busupdatedflag;
    CtiTime _lastcurrentvarpointupdatetime;
    long _estimatedvarloadpointid;
    double _estimatedvarloadpointvalue;
    long _dailyoperationsanalogpointid;
    long _powerfactorpointid;
    long _estimatedpowerfactorpointid;
    long _currentdailyoperations;       //daily operations...
    bool _peaktimeflag;
    bool _recentlycontrolledflag;
    CtiTime _lastoperationtime;
    double _varvaluebeforecontrol;
    long _lastfeedercontrolledpaoid;
    long _lastfeedercontrolledposition;
    double _powerfactorvalue;
    double _kvarsolution;
    double _estimatedpowerfactorvalue;
    long _currentvarpointquality;
    long _currentwattpointquality;
    long _currentvoltpointquality;
    bool _waivecontrolflag;

    std::string _additionalFlags;
    long _currentVerificationCapBankId;
    long _currentVerificationFeederId;
    std::vector <CtiCCFeeder*> _ccfeeders;

    int _percentToClose;
    bool _likeDayControlFlag;

    bool _verificationFlag;
    bool _performingVerificationFlag;
    bool _verificationDoneFlag;
    bool _overlappingSchedulesVerificationFlag;
    bool _preOperationMonitorPointScanFlag;
    bool _operationSentWaitFlag;
    bool _postOperationMonitorPointScanFlag;
    bool _reEnableBusFlag;
    bool _waitForReCloseDelayFlag;
    bool _waitToFinishRegularControlFlag;
    bool _maxDailyOpsHitFlag;
    bool _ovUvDisabledFlag;
    bool _correctionNeededNoBankAvailFlag;
    bool _voltReductionFlag;
    bool _sendMoreTimeControlledCommandsFlag;

    long _voltReductionControlId;
    long _disableBusPointId;
    long _currentCapBankToVerifyAssumedOrigState;
    CtiPAOScheduleManager::VerificationStrategy _verificationStrategy;
    bool _disableOvUvVerificationFlag;
    long _capBankToVerifyInactivityTime;

    double _targetvarvalue;
    std::string _solution;  //text field to be added to messaging indicating status/thinking
    std::string _parentControlUnits;
    std::string _parentName;
    long _displayOrder;

    double _altSubVoltVal;
    double _altSubVarVal;
    double _altSubWattVal;
    CtiTime _lastVerificationCheck;

    double _iVControlTot;
    long  _iVCount;
    double _iWControlTot;
    long  _iWCount;

    double _iVControl;
    double _iWControl;

    bool _usePhaseData;
    long _phaseBid;
    long _phaseCid;
    bool _totalizedControlFlag;

    double _phaseAvalue;
    double _phaseBvalue;
    double _phaseCvalue;
    double _phaseAvalueBeforeControl;
    double _phaseBvalueBeforeControl;
    double _phaseCvalueBeforeControl;

    CtiTime _lastWattPointTime;
    CtiTime _lastVoltPointTime;
    long    _commsStatePointId;

    //don't stream
    bool _insertDynamicDataFlag;
    bool _dirty;

    void restore(Cti::RowReader& rdr);
    std::string doubleToString(double doubleVal);

    std::vector <CtiCCMonitorPointPtr> _multipleMonitorPoints;

    bool performDataOldAndFallBackNecessaryCheckOnFeeders();

    bool checkForRateOfChange(const CtiRegression& reg, const CtiRegression& regA, const CtiRegression& regB, const CtiRegression& regC);
    CtiRegression regression;
    CtiRegression regressionA;
    CtiRegression regressionB;
    CtiRegression regressionC;

    std::map <long, CtiCCMonitorPointPtr> _monitorPoints;
    std::map <Cti::CapControl::PointResponseKey, Cti::CapControl::PointResponsePtr> _pointResponses;
};

typedef CtiCCSubstationBus* CtiCCSubstationBusPtr;

typedef std::set<CtiCCSubstationBusPtr> CtiCCSubstationBus_set;
typedef std::vector<CtiCCSubstationBusPtr> CtiCCSubstationBus_vec;
