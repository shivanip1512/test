#pragma once

#include "Conductor.h"
#include "ccfeeder.h"
#include "TimeOfDayStrategy.h"
#include "EventLogEntry.h"

class CtiRegression;

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


class CtiCCSubstationBus : public Conductor
{
public:
    DECLARE_COLLECTABLE( CtiCCSubstationBus );

    CtiCCSubstationBus( StrategyManager * strategyManager = nullptr );
    CtiCCSubstationBus( Cti::RowReader & rdr, StrategyManager * strategyManager );

    virtual ~CtiCCSubstationBus();

    double getCurrentVarLoadPointValue() const override;

    double getTotalizedVarLoadPointValue() const;

    double getCurrentWattLoadPointValue() const override;

    double getCurrentVoltLoadPointValue() const override;

    const CtiTime& getNextCheckTime() const;
    bool getBusUpdatedFlag() const;
    bool getPeakTimeFlag() const;
    long getLastFeederControlledPAOId() const;
    long getLastFeederControlledPosition() const;
    bool getVerificationFlag() const;
    bool getPerformingVerificationFlag() const;
    bool getVerificationDoneFlag() const;
    long getCurrentVerificationFeederId() const;
    long getCurrentVerificationCapBankId() const;
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
    bool getReEnableBusFlag() const;
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
    bool getDmvTestRunning() const;

    long getDisplayOrder() const;

    bool getTotalizedControlFlag() const;

    long getCommsStatePointId() const;

    CtiFeeder_vec& getCCFeeders();
    const CtiFeeder_vec& getCCFeeders() const;
    std::list<int> getCCFeederIds();
    void deleteCCFeeder(long feederId);

    long getControlSendRetries() const;

    void figureNextCheckTime();
    void setBusUpdatedFlag(bool busupdated);

    void setEstimatedVarLoadPointValue( const double aValue ) override;

    void setPeakTimeFlag(bool peaktime);
    void setLastVerificationCheck(const CtiTime& checkTime);
    void setLastFeederControlledPAOId(long lastfeederpao);
    void setLastFeederControlled(long lastfeederpao);
    void setLastFeederControlledPosition(long lastfeederposition);
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
    void setReEnableBusFlag(bool flag);
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
    void setDmvTestRunning(bool flag);

    void setAllAltSubValues(double volt, double var, double watt);
    void setDisplayOrder(long displayOrder);
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
    std::set<long> getAllCapBankIds();
    void checkForAndProvideNeededControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    void checkForAndProvideNeededFallBackControl(const CtiTime& currentDateTime,
                        CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    void checkForAndProvideNeededTimeOfDayControl(const CtiTime& currentDateTime,
                            CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    void regularSubstationBusControl(double lagLevel, double leadLevel, const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    void optimizedSubstationBusControl(double lagLevel, double leadLevel, const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    void figureEstimatedVarLoadPointValue();
    bool isAlreadyControlled();
    bool checkForAndPerformSendRetry(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    bool checkForAndPerformVerificationSendRetry(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    void voltControlProcess();
    void updatePointResponsePreOpValues(CtiCCCapBank* capBank);
    void updatePointResponseDeltas();
    void updatePointResponseDeltas(std::set<long> pointIds);

    bool areAllMonitorPointsNewEnough(const CtiTime& currentDateTime);

    bool areCapbankMonitorPointsNewerThan( const CtiTime timestamp );
    void clearMonitorPointsScanInProgress();

    bool isScanFlagSet();
    bool scanAllMonitorPoints();
    bool isBusAnalysisNeeded(const CtiTime& currentDateTime);
    bool isMultiVoltBusAnalysisNeeded(const CtiTime& currentDateTime);
    bool areAllMonitorPointsInVoltageRange(CtiCCMonitorPointPtr & oorPoint);
    CtiCCCapBankPtr getMonitorPointParentBankAndFeeder( const CtiCCMonitorPoint & point, CtiCCFeederPtr & feeder );
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

    void checkAndUpdateRecentlyControlledFlag();

    void getSpecializedPointRegistrationIds( std::set<long> & registrationIDs ) const override;
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
    std::vector <long> getAllMonitorPointIds() const;
    std::vector <CtiCCMonitorPointPtr> getAllCapBankMonitorPoints();
    void removeAllMonitorPoints();
    CtiCCMonitorPointPtr getMonitorPoint(long pointId);
    Cti::CapControl::PointResponsePtr getPointResponse(Cti::CapControl::PointResponseKey key);
    void updatePointResponse(Cti::CapControl::PointResponseKey key, Cti::CapControl::PointResponsePtr  pResponse);
    void addDefaultPointResponses( std::set< std::pair<long, int> > &requiredPointResponses );

    std::vector<Cti::CapControl::PointResponse> getPointResponsesForDevice(const long deviceId);

    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);
    void setDynamicData(Cti::RowReader& rdr);

    std::vector <CtiCCMonitorPointPtr>& getMultipleMonitorPoints() {return _multipleMonitorPoints;};

    CtiCCCapBankPtr getMonitorPointParentBank( const CtiCCMonitorPoint & point );

    CtiCCSubstationBus* replicate() const;

    CtiCCCapBankPtr canConsiderPoint( const CtiCCMonitorPoint & point );

    // Added for serialization
    double getAltSubVoltVal() const;
    double getAltSubVarVal() const;
    double getAltSubWattVal() const;

protected:

    CtiCCSubstationBus( const CtiCCSubstationBus & bus ) = default;
    CtiCCSubstationBus & operator=( const CtiCCSubstationBus & right ) = delete;

private:

    long   _altDualSubId;
    double _altSubControlValue;
    long   _switchOverPointId;
    bool   _switchOverStatus;
    bool   _primaryBusFlag;
    bool   _dualBusEnable;

    CtiTime _nextchecktime;
    bool _busupdatedflag;
    bool _peaktimeflag;
    long _lastfeedercontrolledpaoid;
    long _lastfeedercontrolledposition;

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
    bool _dmvTestRunningFlag;

    long _voltReductionControlId;
    long _disableBusPointId;
    CtiPAOScheduleManager::VerificationStrategy _verificationStrategy;
    bool _disableOvUvVerificationFlag;
    long _capBankToVerifyInactivityTime;

    long _displayOrder;

    double _altSubVoltVal;
    double _altSubVarVal;
    double _altSubWattVal;
    CtiTime _lastVerificationCheck;

    long    _commsStatePointId;

    std::string formatFlags() const;
    bool updateDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime ) override;
    bool insertDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime ) override;

    void restore(Cti::RowReader& rdr);
    std::string doubleToString(double doubleVal);

    std::vector <CtiCCMonitorPointPtr> _multipleMonitorPoints;

    bool performDataOldAndFallBackNecessaryCheckOnFeeders();

    bool checkForRateOfChange(const CtiRegression& reg, const CtiRegression& regA, const CtiRegression& regB, const CtiRegression& regC);

    std::map <long, CtiCCMonitorPointPtr> _monitorPoints;
    std::map <Cti::CapControl::PointResponseKey, Cti::CapControl::PointResponsePtr> _pointResponses;
};

typedef CtiCCSubstationBus* CtiCCSubstationBusPtr;

typedef std::set<CtiCCSubstationBusPtr> CtiCCSubstationBus_set;
typedef std::vector<CtiCCSubstationBusPtr> CtiCCSubstationBus_vec;
