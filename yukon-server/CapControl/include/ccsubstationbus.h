#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>
#include <rw/thr/mutex.h>
#include <rw/thr/recursiv.h>

#include "dbaccess.h"
#include "connection.h"
#include "types.h"
#include "observe.h"
#include "ccfeeder.h"
#include "cccapbank.h"
#include "msg_pcrequest.h"
#include "msg_cmd.h"
#include "StrategyManager.h"
#include "TimeOfDayStrategy.h"
#include "ccmonitorpoint.h"
#include "Controllable.h"
#include "sorted_vector.h"
#include "PointResponse.h"
#include "mgr_paosched.h"

namespace Cti {
namespace Database {
    class DatabaseConnection;
}
}

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


class CtiCCSubstationBus : public RWCollectable, public Controllable
{

public:

RWDECLARE_COLLECTABLE( CtiCCSubstationBus )

    CtiCCSubstationBus();
    CtiCCSubstationBus(StrategyManager * strategyManager);
    CtiCCSubstationBus(Cti::RowReader& rdr, StrategyManager * strategyManager);
    CtiCCSubstationBus(const CtiCCSubstationBus& bus);

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
    const string& getMapLocationId() const;
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

    const string& getSolution() const;
    double getTargetVarValue() const;
    const string& getParentControlUnits() const;
    const string& getParentName() const;
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
    std::list<int> getCCFeederIds();
    void deleteCCFeeder(long feederId);

    CtiCCSubstationBus& setParentId(long parentId);

// OK!
    long getControlSendRetries() const;

    CtiCCSubstationBus& setCurrentVarLoadPointId(long currentvarid);
    CtiCCSubstationBus& setCurrentVarLoadPointValue(double currentvarval, CtiTime timestamp);
    CtiCCSubstationBus& setCurrentWattLoadPointId(long currentwattid);
    CtiCCSubstationBus& setCurrentWattLoadPointValue(double currentwattval);
    CtiCCSubstationBus& setCurrentVoltLoadPointId(long currentvoltid);
    CtiCCSubstationBus& setCurrentVoltLoadPointValue(double currentvoltval);
    CtiCCSubstationBus& setMapLocationId(const string& maplocation);
    CtiCCSubstationBus& setDecimalPlaces(long places);
    CtiCCSubstationBus& figureNextCheckTime();
    CtiCCSubstationBus& setNewPointDataReceivedFlag(bool newpointdatareceived);
    CtiCCSubstationBus& setBusUpdatedFlag(bool busupdated);
    CtiCCSubstationBus& setLastCurrentVarPointUpdateTime(const CtiTime& lastpointupdate);
    CtiCCSubstationBus& setEstimatedVarLoadPointId(long estimatedvarid);
    CtiCCSubstationBus& setEstimatedVarLoadPointValue(double estimatedvarval);
    CtiCCSubstationBus& setDailyOperationsAnalogPointId(long opanalogpointid);
    CtiCCSubstationBus& setPowerFactorPointId(long pfpointid);
    CtiCCSubstationBus& setEstimatedPowerFactorPointId(long epfpointid);
    CtiCCSubstationBus& setCurrentDailyOperations(long operations);
    CtiCCSubstationBus& setCurrentDailyOperationsAndSendMsg(long operations, CtiMultiMsg_vec& pointChanges);
    CtiCCSubstationBus& setPeakTimeFlag(bool peaktime);
    CtiCCSubstationBus& setRecentlyControlledFlag(bool recentlycontrolled);
    CtiCCSubstationBus& setLastOperationTime(const CtiTime& lastoperation);
    CtiCCSubstationBus& setLastVerificationCheck(const CtiTime& checkTime);
    CtiCCSubstationBus& setVarValueBeforeControl(double oldvarval, long originalParentId = 0);
    CtiCCSubstationBus& setLastFeederControlledPAOId(long lastfeederpao);
    CtiCCSubstationBus& setLastFeederControlled(long lastfeederpao);
    CtiCCSubstationBus& setLastFeederControlledPosition(long lastfeederposition);
    CtiCCSubstationBus& setPowerFactorValue(double pfval);
    CtiCCSubstationBus& setKVARSolution(double solution);
    CtiCCSubstationBus& setEstimatedPowerFactorValue(double epfval);
    CtiCCSubstationBus& setCurrentVarPointQuality(long cvpq);
    CtiCCSubstationBus& setCurrentWattPointQuality(long cwpq);
    CtiCCSubstationBus& setCurrentVoltPointQuality(long cvpq);
    CtiCCSubstationBus& setWaiveControlFlag(bool waive);
    CtiCCSubstationBus& setOverlappingVerificationFlag( bool overlapFlag);
    CtiCCSubstationBus& setPreOperationMonitorPointScanFlag( bool flag);
    CtiCCSubstationBus& setOperationSentWaitFlag( bool flag);
    CtiCCSubstationBus& setPostOperationMonitorPointScanFlag( bool flag);
    CtiCCSubstationBus& setAltDualSubId(long altDualSubId);
    CtiCCSubstationBus& setAltSubControlValue(double controlValue);
    CtiCCSubstationBus& setSwitchOverPointId(long pointId);
    CtiCCSubstationBus& setSwitchOverStatus(bool status);
    CtiCCSubstationBus& setPrimaryBusFlag(bool status);
    CtiCCSubstationBus& setDualBusEnable(bool flag);
    CtiCCSubstationBus& setEventSequence(long eventSeq);
    CtiCCSubstationBus& setReEnableBusFlag(bool flag);
    CtiCCSubstationBus& setMultiMonitorFlag(bool flag);
    CtiCCSubstationBus& setWaitForReCloseDelayFlag(bool flag);
    CtiCCSubstationBus& setWaitToFinishRegularControlFlag(bool flag);
    CtiCCSubstationBus& setMaxDailyOpsHitFlag(bool flag);
    CtiCCSubstationBus& setOvUvDisabledFlag(bool flag);
    CtiCCSubstationBus& setCorrectionNeededNoBankAvailFlag(bool flag);
    CtiCCSubstationBus& setLikeDayControlFlag(bool flag);
    CtiCCSubstationBus& setVoltReductionFlag(bool flag);
    CtiCCSubstationBus& setVoltReductionControlId(long pointid);
    CtiCCSubstationBus& setDisableBusPointId(long pointid);
    CtiCCSubstationBus& setSendMoreTimeControlledCommandsFlag(bool flag);

    CtiCCSubstationBus& setAllAltSubValues(double volt, double var, double watt);
    CtiCCSubstationBus& setSolution(const string& text);
    CtiCCSubstationBus& setTargetVarValue(double value);
    CtiCCSubstationBus& setParentControlUnits(const string& parentControlUnits);
    CtiCCSubstationBus& setParentName(const string& parentName);
    CtiCCSubstationBus& setDisplayOrder(long displayOrder);
    CtiCCSubstationBus& setIVControlTot(double value);
    CtiCCSubstationBus& setIVCount(long value);
    CtiCCSubstationBus& setIWControlTot(double value);
    CtiCCSubstationBus& setIWCount(long value);
    CtiCCSubstationBus& setIVControl(double value);
    CtiCCSubstationBus& setIWControl(double value);
    CtiCCSubstationBus& setUsePhaseData(bool flag);
    CtiCCSubstationBus& setPhaseBId(long pointid);
    CtiCCSubstationBus& setPhaseCId(long pointid);
    CtiCCSubstationBus& setTotalizedControlFlag(bool flag);
    CtiCCSubstationBus& setPhaseAValue(double value, CtiTime time);
    CtiCCSubstationBus& setPhaseBValue(double value, CtiTime time);
    CtiCCSubstationBus& setPhaseCValue(double value, CtiTime time);
    CtiCCSubstationBus& setPhaseAValueBeforeControl(double value);
    CtiCCSubstationBus& setPhaseBValueBeforeControl(double value);
    CtiCCSubstationBus& setPhaseCValueBeforeControl(double value);
    CtiCCSubstationBus& setLastWattPointTime(const CtiTime& lastpointupdate);
    CtiCCSubstationBus& setLastVoltPointTime(const CtiTime& lastpointupdate);
    CtiCCSubstationBus& setCommsStatePointId(long newId);

    void reOrderFeederDisplayOrders();
    void figureAndSetTargetVarValue();
    void figureAndSetPowerFactorByFeederValues();
    double getSetPoint();
    bool isPastMaxConfirmTime(const CtiTime& currentDateTime);
    long getLastFeederControlledSendRetries() const;
    void analyzeMultiVoltBus(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    void analyzeMultiVoltBus1(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    bool performActionMultiPointBus(const CtiTime& currentDateTime);
    bool isVarCheckNeeded(const CtiTime& currentDateTime);
    bool isConfirmCheckNeeded();
    bool capBankControlStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents);
    double figureCurrentSetPoint(const CtiTime& currentDateTime);
    bool isPeakDay(const CtiTime& currentDateTime);
    bool isPeakTime(const CtiTime& currentDateTime);
    bool isControlPoint(long pointid);
    void updateIntegrationVPoint(const CtiTime &currentDateTime);
    void updateIntegrationWPoint(const CtiTime &currentDateTime);
    void clearOutNewPointReceivedFlags();
    bool maxOperationsHitDisableBus();
    void checkForMaxDailyOpsHit();


    int getNumOfBanksInState(std::set<int> s);
    CtiCCCapBankPtr CtiCCSubstationBus::getPendingCapBank( );
    std::vector<CtiCCCapBankPtr> getAllCapBanks( );
    std::vector<CtiCCCapBankPtr> getAllSwitchedCapBanks( );
    CtiCCSubstationBus& checkForAndProvideNeededControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    CtiCCSubstationBus& checkForAndProvideNeededFallBackControl(const CtiTime& currentDateTime,
                        CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    CtiCCSubstationBus& checkForAndProvideNeededTimeOfDayControl(const CtiTime& currentDateTime,
                            CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    void regularSubstationBusControl(double lagLevel, double leadLevel, const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    void optimizedSubstationBusControl(double lagLevel, double leadLevel, const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    CtiCCSubstationBus& figureEstimatedVarLoadPointValue();
    bool isAlreadyControlled();
    double calculatePowerFactor(double kvar, double kw);
    double convertKQToKVAR(double kq, double kw);
    double convertKVARToKQ(double kvar, double kw);
    static double calculateKVARSolution(const string& controlUnits, double setPoint, double varValue, double wattValue);
    bool checkForAndPerformSendRetry(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    bool checkForAndPerformVerificationSendRetry(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
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
    bool areAllMonitorPointsInVoltageRange(CtiCCMonitorPointPtr oorPoint);
    CtiCCCapBank* getMonitorPointParentBankAndFeeder(CtiCCMonitorPointPtr point, CtiCCFeeder* feed);
    bool voltControlBankSelectProcess(CtiCCMonitorPointPtr point, CtiMultiMsg_vec &pointChanges, CtiMultiMsg_vec &ccEvents, CtiMultiMsg_vec &pilMessages);
    bool areOtherMonitorPointResponsesOk(long mPointID, CtiCCCapBank* potentialCap, int action);
    bool analyzeBusForVarImprovement(CtiMultiMsg_vec &pointChanges, CtiMultiMsg_vec &ccEvents, CtiMultiMsg_vec &pilMessages);

    int getAlterateBusIdForPrimary() const;
    bool isBusPerformingVerification();
    bool isBusReadyToStartVerification();
    bool isBusVerificationAlreadyStarted();
    bool isVerificationPastMaxConfirmTime(const CtiTime& currentDateTime);
    bool areThereMoreCapBanksToVerify(CtiMultiMsg_vec& ccEvents);
    CtiCCSubstationBus& startVerificationOnCapBank(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    bool isVerificationAlreadyControlled();
    CtiCCSubstationBus& analyzeVerificationByFeeder(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages, CtiMultiMsg_vec& capMessages);
    void setCapBanksToVerifyFlags(int verificationStrategy, CtiMultiMsg_vec& ccEvents);
    bool isBankSelectedByVerificationStrategy(int verificationStrategy, CtiCCCapBankPtr currentCapBank);
    CtiCCSubstationBus& recompileCapBanksToVerifyList();
    CtiCCSubstationBus& getNextCapBankToVerify(CtiMultiMsg_vec& ccEvents);
    bool sendNextCapBankVerificationControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    CtiCCSubstationBus& setVerificationFlag(bool verificationFlag);
    CtiCCSubstationBus& setPerformingVerificationFlag(bool performingVerificationFlag);
    CtiCCSubstationBus& setVerificationDoneFlag(bool verificationDoneFlag);
    CtiCCSubstationBus& setCurrentVerificationFeederId(long feederId);
    CtiCCSubstationBus& setCurrentVerificationCapBankId(long capBankId);
    CtiCCSubstationBus& setCurrentVerificationCapBankState(long status);

    CtiCCSubstationBus& checkAndUpdateRecentlyControlledFlag();

    CtiCCSubstationBus& addAllSubPointsToMsg(std::set<long>& pointAddMsg);
    CtiCCSubstationBus& verifyControlledStatusFlags();
    long getNextTODStartTime();

    CtiCCSubstationBus& setVerificationAlreadyStartedFlag(bool verificationFlag);
    void setVerificationStrategy( CtiPAOScheduleManager::VerificationStrategy verificationStrategy);
    CtiPAOScheduleManager::VerificationStrategy getVerificationStrategy(void) const;
    void setVerificationDisableOvUvFlag(bool flag);
    bool getVerificationDisableOvUvFlag(void) const;
    string getVerificationString();
    void setCapBankInactivityTime(long capBankToVerifyInactivityTime);
    long getCapBankInactivityTime(void) const;

    bool capBankVerificationStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents);
    bool capBankVerificationPerPhaseStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents);
    void createStatusUpdateMessages(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiCCCapBankPtr capBank,
                                    CtiCCFeederPtr feeder, string text, string additional, bool verifyFlag,
                                    double before, double after, double change, double phaseA, double phaseB, double phaseC);
    void createCannotControlBankText(string text, string commandString, CtiMultiMsg_vec& ccEvents);
    void performDataOldAndFallBackNecessaryCheck();

    bool addMonitorPoint(long pointId, CtiCCMonitorPointPtr monPoint);
    bool updateExistingMonitorPoint(CtiCCMonitorPointPtr monPoint);

    const map <long, CtiCCMonitorPointPtr>& getAllMonitorPoints();
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

    CtiCCOperationStats& getOperationStats();
    CtiCCConfirmationStats& getConfirmationStats();

    //Members inherited from RWCollectable
    void saveGuts(RWvostream& ) const;

    CtiCCSubstationBus& operator=(const CtiCCSubstationBus& right);

    CtiCCSubstationBus* replicate() const;

    //static int PeakState;
    //static int OffPeakState;

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
    string _maplocationid;

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

    string _additionalFlags;
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
    string _solution;  //text field to be added to messaging indicating status/thinking
    string _parentControlUnits;
    string _parentName;
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

    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;

    //don't stream
    bool _insertDynamicDataFlag;
    bool _dirty;

    void restore(Cti::RowReader& rdr);
    string doubleToString(double doubleVal);

    std::vector <CtiCCMonitorPointPtr> _multipleMonitorPoints;

    bool performDataOldAndFallBackNecessaryCheckOnFeeders();

    bool checkForRateOfChange(const CtiRegression& reg, const CtiRegression& regA, const CtiRegression& regB, const CtiRegression& regC);
    CtiRegression regression;
    CtiRegression regressionA;
    CtiRegression regressionB;
    CtiRegression regressionC;

    map <long, CtiCCMonitorPointPtr> _monitorPoints;
    map <Cti::CapControl::PointResponseKey, Cti::CapControl::PointResponsePtr> _pointResponses;
};

typedef CtiCCSubstationBus* CtiCCSubstationBusPtr;

typedef std::set<CtiCCSubstationBusPtr> CtiCCSubstationBus_set;
typedef std::vector<CtiCCSubstationBusPtr> CtiCCSubstationBus_vec;
