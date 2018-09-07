#pragma once

#include "Conductor.h"
#include "ccOriginalParent.h"
#include "cccapbank.h"
#include "EventLogEntry.h"
#include "sorted_vector.h"

class CtiRegression;

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


class CtiCCFeeder : public Conductor
{
public:
    DECLARE_COLLECTABLE( CtiCCFeeder );

    CtiCCFeeder( StrategyManager * strategyManager = nullptr );
    CtiCCFeeder(Cti::RowReader& rdr, StrategyManager * strategyManager);

    virtual ~CtiCCFeeder();

    float getDisplayOrder() const;
    long getLastCapBankControlledDeviceId() const;
    long getBusOptimizedVarCategory() const;
    double getBusOptimizedVarOffset() const;
    bool getPeakTimeFlag() const;
    bool getPorterRetFailFlag() const;
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
    long getRetryIndex() const;

    CtiCCCapBank_SVector& getCCCapBanks();
    const CtiCCCapBank_SVector& getCCCapBanks() const;
    std::list<int> getAllCapBankIds();
    std::vector<CtiCCCapBankPtr> getAllCapBanks();
        std::vector<CtiCCCapBankPtr> getAllSwitchedCapBanks();
    void deleteCCCapBank(long capBankId);

    void setDisplayOrder(float order);
    void setLastCapBankControlledDeviceId(long lastcapbank);
    void setBusOptimizedVarCategory(const long varcategory);
    void setBusOptimizedVarOffset(const double varoffset);
    void setPeakTimeFlag(bool peakTimeFlag);
    void setPorterRetFailFlag(bool flag);
    void setVerificationFlag(bool verificationFlag);
    void setPerformingVerificationFlag(bool performingVerificationFlag);
    void setVerificationDoneFlag(bool verificationDoneFlag);
    void setPreOperationMonitorPointScanFlag( bool flag);
    void setOperationSentWaitFlag( bool flag);
    void setPostOperationMonitorPointScanFlag( bool flag);
    void setWaitForReCloseDelayFlag(bool flag);
    void setMaxDailyOpsHitFlag(bool flag);
    void setOvUvDisabledFlag(bool flag);
    void setCorrectionNeededNoBankAvailFlag(bool flag);
    void setLikeDayControlFlag(bool flag);
    void setLastVerificationMsgSentSuccessfulFlag(bool flag);
    void setCurrentVerificationCapBankId(long capBankId);
    void setRetryIndex(long value);

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
    bool isPastMaxConfirmTime(const CtiTime& currentDateTime, long maxConfirmTime, long feederRetries);
    bool checkForAndProvideNeededIndividualControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages,
                                                   bool peakTimeFlag, long decimalPlaces, const std::string& controlUnits,
                                                   bool dailyMaxOpsHitFlag);
    bool checkForAndProvideNeededFallBackControl(const CtiTime& currentDateTime,
                            CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages);
    void orderBanksOnFeeder();
    bool isPeakTime(const CtiTime& currentDateTime);
    bool isControlPoint(long pointid);
    void updateIntegrationVPoint(const CtiTime &currentDateTime, const CtiTime &nextCheckTime);
    void updateIntegrationWPoint(const CtiTime &currentDateTime, const CtiTime &nextCheckTime);
    void figureEstimatedVarLoadPointValue();
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
    void getNextCapBankToVerify();

    bool capBankVerificationStatusUpdate(CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents, long minConfirmPercent,
                                         long failurePercent, double varAValue, double varBValue, double varCValue);
    bool capBankVerificationPerPhaseStatusUpdate(CtiMultiMsg_vec& pointChanges, Cti::CapControl::EventLogEntries &ccEvents,
                                                 long minConfirmPercent, long failPercent);

    void getSpecializedPointRegistrationIds( std::set<long> & registrationIDs ) const override;
    CtiCCCapBank* getMonitorPointParentBank(const CtiCCMonitorPoint & point);

    bool isDataOldAndFallBackNecessary(std::string controlUnits);

    CtiCCOriginalParent& getOriginalParent();
    const CtiCCOriginalParent& getOriginalParent() const;

    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

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

protected:

    CtiCCFeeder( const CtiCCFeeder & feeder ) = default;
    CtiCCFeeder & operator=( const CtiCCFeeder & right ) = delete;

private:

    float _displayorder;

    long _lastcapbankcontrolleddeviceid;
    long _busoptimizedvarcategory;
    double _busoptimizedvaroffset;

    bool _peakTimeFlag;

    CtiCCCapBank_SVector _cccapbanks;

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

    long _currentVerificationCapBankId;

    long _retryIndex;

    CtiCCOriginalParent _originalParent;

    std::string formatFlags() const;
    bool updateDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime ) override;
    bool insertDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime ) override;

    std::string doubleToString(double doubleVal, long decimalPlaces);

    std::vector <CtiCCMonitorPointPtr> _multipleMonitorPoints;

    bool checkForRateOfChange(const CtiRegression& reg, const CtiRegression& regA, const CtiRegression& regB, const CtiRegression& regC);
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

