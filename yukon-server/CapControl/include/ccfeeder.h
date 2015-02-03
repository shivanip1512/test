#pragma once

#include "Controllable.h"
#include "ccOperationStats.h"
#include "ccConfirmationStats.h"
#include "ccOriginalParent.h"
#include "regression.h"
#include "cccapbank.h"
#include "EventLogEntry.h"
#include "sorted_vector.h"

//For Sorted Vector, the vector will use this to determine position in the vector.
struct CtiCCCapBank_less
{
    bool operator()( const CtiCCCapBankPtr & _X , const CtiCCCapBankPtr & _Y)
        { return ( _X->getControlOrder() < _Y->getControlOrder() ); }
};
struct CtiCCCapBank_lessClose
{
    bool operator()( const CtiCCCapBankPtr & _X , const CtiCCCapBankPtr & _Y)
        { return ( _X->getCloseOrder() < _Y->getCloseOrder() ); }
};
struct CtiCCCapBank_lessTrip
{
    bool operator()( const CtiCCCapBankPtr & _X , const CtiCCCapBankPtr & _Y)
        { return ( _X->getTripOrder() < _Y->getTripOrder() ); }
};
//Typedef for Sanity using sorted vectors
typedef codeproject::sorted_vector<CtiCCCapBankPtr,false,CtiCCCapBank_less> CtiCCCapBank_SVector;
typedef codeproject::sorted_vector<CtiCCCapBankPtr,false,CtiCCCapBank_lessClose> CtiCCCapBank_SCloseVector;
typedef codeproject::sorted_vector<CtiCCCapBankPtr,false,CtiCCCapBank_lessTrip> CtiCCCapBank_STripVector;


class CtiCCFeeder : public Controllable
{
public:
    DECLARE_COLLECTABLE( CtiCCFeeder );

public:
    CtiCCFeeder();
    CtiCCFeeder(StrategyManager * strategyManager);
    CtiCCFeeder(Cti::RowReader& rdr, StrategyManager * strategyManager);
    CtiCCFeeder(const CtiCCFeeder& feeder);

    virtual ~CtiCCFeeder();

    long getParentId() const;

    long getCurrentVarLoadPointId() const;
    double getCurrentVarLoadPointValue() const;
    long getCurrentWattLoadPointId() const;
    double getCurrentWattLoadPointValue() const;
    long getCurrentVoltLoadPointId() const;
    double getCurrentVoltLoadPointValue() const;
    const std::string& getMapLocationId() const;
    float getDisplayOrder() const;
    bool getNewPointDataReceivedFlag() const;
    const CtiTime& getLastCurrentVarPointUpdateTime() const;
    long getEstimatedVarLoadPointId() const;
    double getEstimatedVarLoadPointValue() const;
    long getDailyOperationsAnalogPointId() const;
    long getPowerFactorPointId() const;
    long getEstimatedPowerFactorPointId() const;
    long getCurrentDailyOperations() const;
    bool getRecentlyControlledFlag() const;
    const CtiTime& getLastOperationTime() const;
    double getVarValueBeforeControl() const;
    long getLastCapBankControlledDeviceId() const;
    long getBusOptimizedVarCategory() const;
    double getBusOptimizedVarOffset() const;
    double getPowerFactorValue() const;
    double getKVARSolution() const;
    double getEstimatedPowerFactorValue() const;
    long getCurrentVarPointQuality() const;
    long getCurrentWattPointQuality() const;
    long getCurrentVoltPointQuality() const;
    bool getWaiveControlFlag() const;
    const std::string& getParentControlUnits() const;
    const std::string& getParentName() const;
    long getDecimalPlaces() const;
    bool getPeakTimeFlag() const;
    bool getPorterRetFailFlag() const;
    long getEventSequence() const;
    bool getMultiMonitorFlag() const;
    bool getVerificationFlag() const;
    bool getPerformingVerificationFlag() const;
    bool getVerificationDoneFlag() const;
    bool getPreOperationMonitorPointScanFlag() const;
    bool getOperationSentWaitFlag() const;
    bool getPostOperationMonitorPointScanFlag() const;
    bool getWaitForReCloseDelayFlag() const;
    bool getMaxDailyOpsHitFlag() const;
    bool getOvUvDisabledFlag() const;
    bool getCorrectionNeededNoBankAvailFlag() const;
    bool getLikeDayControlFlag() const;
    bool getLastVerificationMsgSentSuccessfulFlag() const;
    long getCurrentVerificationCapBankId() const;
    long getCurrentVerificationCapBankOrigState() const;
    double getTargetVarValue() const;
    const std::string& getSolution() const;
    double getIVControlTot() const;
    long getIVCount() const;
    double getIWControlTot() const;
    long getIWCount() const;
    double getIVControl() const;
    double getIWControl() const;
    bool getUsePhaseData() const;
    long getPhaseBId() const;
    long getPhaseCId() const;
    Cti::CapControl::PointIdVector getCurrentVarLoadPoints() const;
    bool getTotalizedControlFlag() const;
    double getPhaseAValue() const;
    double getPhaseBValue() const;
    double getPhaseCValue() const;
    double getPhaseAValueBeforeControl() const;
    double getPhaseBValueBeforeControl() const;
    double getPhaseCValueBeforeControl() const;
    const CtiTime& getLastVarPointTime() const;
    const CtiTime& getLastWattPointTime() const;
    const CtiTime& getLastVoltPointTime() const;
    long getRetryIndex() const;
    const CtiRegression& getRegression();
    const CtiRegression& getRegressionA();
    const CtiRegression& getRegressionB();
    const CtiRegression& getRegressionC();

    CtiCCCapBank_SVector& getCCCapBanks();
    const CtiCCCapBank_SVector& getCCCapBanks() const;
    std::list<int> getAllCapBankIds();
    std::vector<CtiCCCapBankPtr> getAllCapBanks();
        std::vector<CtiCCCapBankPtr> getAllSwitchedCapBanks();
    void deleteCCCapBank(long capBankId);

    CtiCCFeeder& setParentId(long parentId);
    CtiCCFeeder& setCurrentVarLoadPointId(long currentvarid);
    CtiCCFeeder& setCurrentVarLoadPointValue(double currentvarval, CtiTime timestamp);
    CtiCCFeeder& setCurrentWattLoadPointId(long currentwattid);
    CtiCCFeeder& setCurrentWattLoadPointValue(double currentwattval);
    CtiCCFeeder& setCurrentVoltLoadPointId(long currentvoltid);
    CtiCCFeeder& setCurrentVoltLoadPointValue(double currentvoltval);
    CtiCCFeeder& setMapLocationId(const std::string& maplocation);
    CtiCCFeeder& setDisplayOrder(float order);
    CtiCCFeeder& setNewPointDataReceivedFlag(bool newpointdatareceived);
    CtiCCFeeder& setLastCurrentVarPointUpdateTime(const CtiTime& lastpointupdate);
    CtiCCFeeder& setEstimatedVarLoadPointId(long estimatedvarid);
    CtiCCFeeder& setEstimatedVarLoadPointValue(double estimatedvarval);
    CtiCCFeeder& setDailyOperationsAnalogPointId(long opspointid);
    CtiCCFeeder& setPowerFactorPointId(long pfpointid);
    CtiCCFeeder& setEstimatedPowerFactorPointId(long epfpointid);
    CtiCCFeeder& setCurrentDailyOperationsAndSendMsg(long operations, CtiMultiMsg_vec& pointChanges);
    CtiCCFeeder& setCurrentDailyOperations(long operations);
    CtiCCFeeder& setRecentlyControlledFlag(bool recentlycontrolled);
    CtiCCFeeder& setLastOperationTime(const CtiTime& lastoperation);
    CtiCCFeeder& setVarValueBeforeControl(double oldvarval);
    CtiCCFeeder& setLastCapBankControlledDeviceId(long lastcapbank);
    void setBusOptimizedVarCategory(const long varcategory);
    void setBusOptimizedVarOffset(const double varoffset);
    CtiCCFeeder& setPowerFactorValue(double pfval);
    CtiCCFeeder& setKVARSolution(double solution);
    CtiCCFeeder& setEstimatedPowerFactorValue(double epfval);
    CtiCCFeeder& setCurrentVarPointQuality(long cvpq);
    CtiCCFeeder& setCurrentWattPointQuality(long cwpq);
    CtiCCFeeder& setCurrentVoltPointQuality(long cvpq);
    CtiCCFeeder& setWaiveControlFlag(bool waive);
    CtiCCFeeder& setParentControlUnits(const std::string& parentControlUnits);
    CtiCCFeeder& setParentName(const std::string& parentName);
    CtiCCFeeder& setDecimalPlaces(long decimalPlaces);
    CtiCCFeeder& setPeakTimeFlag(bool peakTimeFlag);
    CtiCCFeeder& setPorterRetFailFlag(bool flag);
    CtiCCFeeder& setEventSequence(long eventSeq);
    CtiCCFeeder& setMultiMonitorFlag(bool flag);
    CtiCCFeeder& setVerificationFlag(bool verificationFlag);
    CtiCCFeeder& setPerformingVerificationFlag(bool performingVerificationFlag);
    CtiCCFeeder& setVerificationDoneFlag(bool verificationDoneFlag);
    CtiCCFeeder& setPreOperationMonitorPointScanFlag( bool flag);
    CtiCCFeeder& setOperationSentWaitFlag( bool flag);
    CtiCCFeeder& setPostOperationMonitorPointScanFlag( bool flag);
    CtiCCFeeder& setWaitForReCloseDelayFlag(bool flag);
    CtiCCFeeder& setMaxDailyOpsHitFlag(bool flag);
    CtiCCFeeder& setOvUvDisabledFlag(bool flag);
    CtiCCFeeder& setCorrectionNeededNoBankAvailFlag(bool flag);
    CtiCCFeeder& setLikeDayControlFlag(bool flag);
    CtiCCFeeder& setLastVerificationMsgSentSuccessfulFlag(bool flag);
    CtiCCFeeder& setCurrentVerificationCapBankId(long capBankId);
    CtiCCFeeder& setCurrentVerificationCapBankState(long status);
    CtiCCFeeder& setTargetVarValue(double value);
    CtiCCFeeder& setSolution(const std::string& text);
    CtiCCFeeder& setIVControlTot(double value);
    CtiCCFeeder& setIVCount(long value);
    CtiCCFeeder& setIWControlTot(double value);
    CtiCCFeeder& setIWCount(long value);
    CtiCCFeeder& setIVControl(double value);
    CtiCCFeeder& setIWControl(double value);
    CtiCCFeeder& setUsePhaseData(bool flag);
    CtiCCFeeder& setPhaseBId(long pointid);
    CtiCCFeeder& setPhaseCId(long pointid);
    CtiCCFeeder& setPhaseAValue(double value, CtiTime timestamp);
    CtiCCFeeder& setPhaseBValue(double value, CtiTime timestamp);
    CtiCCFeeder& setPhaseCValue(double value, CtiTime timestamp);
    CtiCCFeeder& setTotalizedControlFlag(bool flag);
    CtiCCFeeder& setPhaseAValueBeforeControl(double value);
    CtiCCFeeder& setPhaseBValueBeforeControl(double value);
    CtiCCFeeder& setPhaseCValueBeforeControl(double value);
    CtiCCFeeder& setLastWattPointTime(const CtiTime& lastpointupdate);
    CtiCCFeeder& setLastVoltPointTime(const CtiTime& lastpointupdate);
    CtiCCFeeder& setRetryIndex(long value);

    void figureAndSetTargetVarValue(const std::string& controlMethod, const std::string& controlUnits, bool peakTimeFlag);
    CtiCCCapBankPtr getLastCapBankControlledDevice();
    CtiCCCapBankPtr findCapBankToChangeVars(double kvarSolution, CtiMultiMsg_vec& pointChanges,
                                          double leadLevel = 0, double lagLevel = 0, double currentVarValue = 0,
                                          bool checkLimits = true);
    bool checkForMaxKvar( long, long );
    bool removeMaxKvar( long bankId );
    CtiRequestMsg* createIncreaseVarRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents,
                                            std::string textInfo, double kvarBefore, double varAValue, double varBValue, double varCValue);
    CtiRequestMsg* createDecreaseVarRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents,
                                            std::string textInfo, double kvarBefore, double varAValue, double varBValue, double varCValue);
    CtiRequestMsg* createForcedVarRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, int action, std::string typeOfControl);
    void createForcedVarConfirmation(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, std::string typeOfControl);
    bool capBankControlStatusUpdate(CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, long minConfirmPercent, long failurePercent,
                                    double varValueBeforeControl, double currentVarLoadPointValue, long currentVarPointQuality,
                                    double varAValue, double varBValue, double varCValue, const CtiRegression& reg);
    bool capBankControlPerPhaseStatusUpdate(CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, long minConfirmPercent,
                                                     long failurePercent, //double varValueBeforeControl, double currentVarLoadPointValue,
                                                     long currentVarPointQuality, double varAValueBeforeControl, double varBValueBeforeControl,
                                                     double varCValueBeforeControl, double varAValue, double varBValue, double varCValue,
                                            const CtiRegression& regA, const CtiRegression& regB, const CtiRegression& regC);
    std::string createPhaseControlStatusUpdateText(std::string capControlStatus, double varAValue,double varBValue, double varCValue,
                                                  double ratioA, double ratioB, double ratioC);
    std::string createControlStatusUpdateText(std::string capControlStatus, double varAValue, double ratioA);
    std::string createPhaseVarText(double aValue,double bValue, double cValue, float multiplier);
    std::string createPhaseRatioText(double aValue,double bValue, double cValue, float multiplier);
    std::string createVarText(double aValue, float multiplier);
    bool isPeakDay();
    bool isPastMaxConfirmTime(const CtiTime& currentDateTime, long maxConfirmTime, long feederRetries);
    bool checkForAndProvideNeededIndividualControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages,
                                                   bool peakTimeFlag, long decimalPlaces, const std::string& controlUnits,
                                                   bool dailyMaxOpsHitFlag);
    bool checkForAndProvideNeededFallBackControl(const CtiTime& currentDateTime,
                            CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    void orderBanksOnFeeder();
    double figureCurrentSetPoint(const CtiTime& currentDateTime);
    bool isPeakTime(const CtiTime& currentDateTime);
    bool isControlPoint(long pointid);
    void updateIntegrationVPoint(const CtiTime &currentDateTime, const CtiTime &nextCheckTime);
    void updateIntegrationWPoint(const CtiTime &currentDateTime, const CtiTime &nextCheckTime);
    CtiCCFeeder& figureEstimatedVarLoadPointValue();
    bool isAlreadyControlled(long minConfirmPercent,
                             long currentVarPointQuality, double varAValueBeforeControl,
                             double varBValueBeforeControl, double varCValueBeforeControl,
                             double varAValue, double varBValue, double varCValue,
                             double varValueBeforeControl, double currentVarLoadPointValue,
                             const CtiRegression& reg, const CtiRegression& regA, const CtiRegression& regB,
                             const CtiRegression& regC,  bool usePhaseData, bool useTotalizedControl);

    void fillOutBusOptimizedInfo(bool peakTimeFlag);
    bool attemptToResendControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages, long maxConfirmTime);
    bool checkForAndPerformVerificationSendRetry(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages, long maxConfirmTime, long sendRetries);
    bool checkMaxDailyOpCountExceeded(CtiMultiMsg_vec &pointChanges);
    bool voltControlBankSelectProcess(const CtiCCMonitorPoint & point, CtiMultiMsg_vec &pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    void updatePointResponsePreOpValues(CtiCCCapBank* capBank);
    void updatePointResponseDeltas();
    bool areAllMonitorPointsNewEnough(const CtiTime& currentDateTime);
    bool isScanFlagSet();
    unsigned long getMonitorPointScanTime();
    bool scanAllMonitorPoints();
    void analyzeMultiVoltFeeder(const CtiTime& currentDateTime, long minConfirmPercent, long failurePercent, long maxConfirmTime, long sendRetries, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    bool areAllMonitorPointsInVoltageRange(CtiCCMonitorPointPtr & oorPoint);
    bool areOtherMonitorPointResponsesOk(long mPointID, CtiCCCapBank* potentialCap, int action);
    double computeRegression( CtiTime time );
    std::string createTextString(const std::string& controlMethod, int control, double controlValue, double monitorValue);
    void createCannotControlBankText(std::string text, std::string commandString, Cti::CapControl::EventLogEntries &ccEvents);
    void resetVerificationFlags();

    CtiRequestMsg* createIncreaseVarVerificationRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents,
                                                        std::string textInfo, int controlOp, double kvarBefore, double varAValue, double varBValue, double varCValue);
    CtiRequestMsg* createDecreaseVarVerificationRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents,
                                                        std::string textInfo, int controlOp, double kvarBefore, double varAValue, double varBValue, double varCValue);
    bool startVerificationOnCapBank(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    bool sendNextCapBankVerificationControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    CtiRequestMsg*  createCapBankVerificationControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents,
                                          CtiMultiMsg_vec& pilMessages, CtiCCCapBank* currentCapBank, int control);

    bool isVerificationAlreadyControlled(long minConfirmPercent, long quality, double varAValueBeforeControl,
                             double varBValueBeforeControl, double varCValueBeforeControl,
                             double varAValue, double varBValue, double varCValue, double oldVarValue, double newVarValue,
                             bool usePhaseData, bool useTotalizedControl);



    bool areThereMoreCapBanksToVerify();
    CtiCCFeeder& getNextCapBankToVerify();

    bool capBankVerificationStatusUpdate(CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, long minConfirmPercent,
                                         long failurePercent, double varAValue, double varBValue, double varCValue);
    bool capBankVerificationPerPhaseStatusUpdate(CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents,
                                                 long minConfirmPercent, long failPercent);
    CtiCCFeeder& addAllFeederPointsToMsg(std::set<long>& pointAddMsg);
    CtiCCCapBank* getMonitorPointParentBank(const CtiCCMonitorPoint & point);

    bool isDataOldAndFallBackNecessary(std::string controlUnits);

    CtiCCOperationStats& getOperationStats();
    CtiCCConfirmationStats& getConfirmationStats();
    CtiCCOriginalParent& getOriginalParent();
    const CtiCCOriginalParent& getOriginalParent() const;

    bool isDirty() const;
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    CtiCCFeeder& operator=(const CtiCCFeeder& right);

    CtiCCFeeder* replicate() const;

    void setDynamicData(Cti::RowReader& rdr);

    std::vector <CtiCCMonitorPointPtr>& getMultipleMonitorPoints() {return _multipleMonitorPoints;};

    bool areAllPhasesSuccess(double ratioA, double ratioB, double ratioC, double confirmPercent);
    bool areAllPhasesQuestionable(double ratioA, double ratioB, double ratioC, double confirmPercent, double failPercent);
    bool shouldCapBankBeFailed(double ratioA, double ratioB, double ratioC, double failPercent);
    bool isAnyPhaseFail(double ratioA, double ratioB, double ratioC, double failPercent, long &numFailedPhases);
    bool isResponseQuestionable(double ratio, double confirmPercent, double failPercent);
    bool isResponseFail(double ratio, double failPercent);
    bool isResponseSuccess(double ratio, double confirmPercent);
    std::string getPhaseIndicatorString(long capState, double ratioA, double ratioB, double ratioC, double confirmPercent, double failPercent);
    std::string getQuestionablePhasesString(double ratioA, double ratioB, double ratioC, double confirmPercent, double failPercent);
    std::string getFailedPhasesString(double ratioA, double ratioB, double ratioC, double confirmPercent, double failPercent);

private:

    long _parentId; //subBusId
    bool _multiMonitorFlag;

    std::string _maplocationid;
    long _currentvarloadpointid;
    double _currentvarloadpointvalue;
    long _currentwattloadpointid;
    double _currentwattloadpointvalue;
    long _currentvoltloadpointid;
    double _currentvoltloadpointvalue;

    float _displayorder;

    bool _newpointdatareceivedflag;
    CtiTime _lastcurrentvarpointupdatetime;
    long _estimatedvarloadpointid;
    double _estimatedvarloadpointvalue;
    long _dailyoperationsanalogpointid;
    long _powerfactorpointid;
    long _estimatedpowerfactorpointid;
    long _currentdailyoperations;
    bool _recentlycontrolledflag;
    CtiTime _lastoperationtime;
    double _varvaluebeforecontrol;
    long _lastcapbankcontrolleddeviceid;
    long _busoptimizedvarcategory;
    double _busoptimizedvaroffset;
    double _powerfactorvalue;
    double _kvarsolution;
    double _estimatedpowerfactorvalue;
    long _currentvarpointquality;
    long _currentwattpointquality;
    long _currentvoltpointquality;
    bool _waivecontrolflag;

    std::string _parentControlUnits;
    std::string _parentName;
    long _decimalPlaces;
    bool _peakTimeFlag;

    CtiCCCapBank_SVector _cccapbanks;

    //verification info
    std::string _additionalFlags;

    bool _verificationFlag;
    bool _performingVerificationFlag;
    bool _verificationDoneFlag;
    bool _preOperationMonitorPointScanFlag;
    bool _operationSentWaitFlag;
    bool _postOperationMonitorPointScanFlag;
    bool _porterRetFailFlag;
    bool _waitForReCloseDelayFlag;
    bool _maxDailyOpsHitFlag;
    bool _ovUvDisabledFlag;
    bool _correctionNeededNoBankAvailFlag;
    bool _likeDayControlFlag;
    bool _lastVerificationMsgSentSuccessful;

    long   _eventSeq;

    long _currentVerificationCapBankId;
    long _currentCapBankToVerifyAssumedOrigState;

    double _targetvarvalue;
    std::string _solution;

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
    long _retryIndex;

    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;
    CtiCCOriginalParent _originalParent;

    //don't stream
    bool _insertDynamicDataFlag;
    bool _dirty;

    void restore(Cti::RowReader& rdr);
    std::string doubleToString(double doubleVal, long decimalPlaces);

    std::vector <CtiCCMonitorPointPtr> _multipleMonitorPoints;

    bool checkForRateOfChange(const CtiRegression& reg, const CtiRegression& regA, const CtiRegression& regB, const CtiRegression& regC);
    CtiRegression regression;
    CtiRegression regressionA;
    CtiRegression regressionB;
    CtiRegression regressionC;
};

typedef CtiCCFeeder* CtiCCFeederPtr;

struct FeederVARComparison
{
    bool operator()(const CtiCCFeederPtr & x, const CtiCCFeederPtr & y) const
    {
        // if the feeders are in different var categories,
        // we don't need the offset to determine which feeder should be first
        if ( x->getBusOptimizedVarCategory() != y->getBusOptimizedVarCategory() )
        {
            return x->getBusOptimizedVarCategory() > y->getBusOptimizedVarCategory();
        }
        // if the feeders are in the same var categories,
        // we need to figure out which has the lower var offset
        return x->getBusOptimizedVarOffset() > y->getBusOptimizedVarOffset();
    }
};

