#pragma once

#include <rw/collect.h>
#include <rw/vstream.h>

#include "dbaccess.h"
#include "connection.h"
#include "types.h"
#include "observe.h"
#include "ccmonitorpoint.h"
#include "ccoriginalparent.h"
#include "cccapbank.h"
#include "cctypes.h"
#include "msg_pcrequest.h"
#include "msg_cmd.h"
#include "StrategyManager.h"
#include "sorted_vector.h"
#include "regression.h"
#include "Controllable.h"

namespace Cti {
namespace Database {
    class DatabaseConnection;
}
}

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

class CtiCCFeeder : public RWCollectable, public Controllable
{

public:

RWDECLARE_COLLECTABLE( CtiCCFeeder )

    CtiCCFeeder();
    CtiCCFeeder(StrategyManager * strategyManager);
    CtiCCFeeder(Cti::RowReader& rdr, StrategyManager * strategyManager);
    CtiCCFeeder(const CtiCCFeeder& feeder);

    virtual ~CtiCCFeeder();

    LONG getParentId() const;

    LONG getCurrentVarLoadPointId() const;
    DOUBLE getCurrentVarLoadPointValue() const;
    LONG getCurrentWattLoadPointId() const;
    DOUBLE getCurrentWattLoadPointValue() const;
    LONG getCurrentVoltLoadPointId() const;
    DOUBLE getCurrentVoltLoadPointValue() const;
    const string& getMapLocationId() const;
    float getDisplayOrder() const;
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
    bool getLikeDayControlFlag() const;
    BOOL getLastVerificationMsgSentSuccessfulFlag() const;
    LONG getCurrentVerificationCapBankId() const;
    LONG getCurrentVerificationCapBankOrigState() const;
    DOUBLE getTargetVarValue() const;
    const string& getSolution() const;
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
    const CtiTime& getLastVarPointTime() const;
    const CtiTime& getLastWattPointTime() const;
    const CtiTime& getLastVoltPointTime() const;
    LONG getRetryIndex() const;
    const CtiRegression& getRegression();
    const CtiRegression& getRegressionA();
    const CtiRegression& getRegressionB();
    const CtiRegression& getRegressionC();

    CtiCCCapBank_SVector& getCCCapBanks();
    std::list<int> getAllCapBankIds();
    std::vector<CtiCCCapBankPtr> getAllCapBanks();
        std::vector<CtiCCCapBankPtr> getAllSwitchedCapBanks();
    void deleteCCCapBank(long capBankId);

    CtiCCFeeder& setParentId(LONG parentId);
    CtiCCFeeder& setCurrentVarLoadPointId(LONG currentvarid);
    CtiCCFeeder& setCurrentVarLoadPointValue(DOUBLE currentvarval, CtiTime timestamp);
    CtiCCFeeder& setCurrentWattLoadPointId(LONG currentwattid);
    CtiCCFeeder& setCurrentWattLoadPointValue(DOUBLE currentwattval);
    CtiCCFeeder& setCurrentVoltLoadPointId(LONG currentvoltid);
    CtiCCFeeder& setCurrentVoltLoadPointValue(DOUBLE currentvoltval);
    CtiCCFeeder& setMapLocationId(const string& maplocation);
    CtiCCFeeder& setDisplayOrder(float order);
    CtiCCFeeder& setNewPointDataReceivedFlag(BOOL newpointdatareceived);
    CtiCCFeeder& setLastCurrentVarPointUpdateTime(const CtiTime& lastpointupdate);
    CtiCCFeeder& setEstimatedVarLoadPointId(LONG estimatedvarid);
    CtiCCFeeder& setEstimatedVarLoadPointValue(DOUBLE estimatedvarval);
    CtiCCFeeder& setDailyOperationsAnalogPointId(LONG opspointid);
    CtiCCFeeder& setPowerFactorPointId(LONG pfpointid);
    CtiCCFeeder& setEstimatedPowerFactorPointId(LONG epfpointid);
    CtiCCFeeder& setCurrentDailyOperationsAndSendMsg(LONG operations, CtiMultiMsg_vec& pointChanges);
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
    CtiCCFeeder& setLastVerificationMsgSentSuccessfulFlag(BOOL flag);
    CtiCCFeeder& setCurrentVerificationCapBankId(LONG capBankId);
    CtiCCFeeder& setCurrentVerificationCapBankState(LONG status);
    CtiCCFeeder& setTargetVarValue(DOUBLE value);
    CtiCCFeeder& setSolution(const string& text);
    CtiCCFeeder& setIVControlTot(DOUBLE value);
    CtiCCFeeder& setIVCount(LONG value);
    CtiCCFeeder& setIWControlTot(DOUBLE value);
    CtiCCFeeder& setIWCount(LONG value);
    CtiCCFeeder& setIVControl(DOUBLE value);
    CtiCCFeeder& setIWControl(DOUBLE value);
    CtiCCFeeder& setUsePhaseData(BOOL flag);
    CtiCCFeeder& setPhaseBId(LONG pointid);
    CtiCCFeeder& setPhaseCId(LONG pointid);
    CtiCCFeeder& setPhaseAValue(DOUBLE value, CtiTime timestamp);
    CtiCCFeeder& setPhaseBValue(DOUBLE value, CtiTime timestamp);
    CtiCCFeeder& setPhaseCValue(DOUBLE value, CtiTime timestamp);
    CtiCCFeeder& setTotalizedControlFlag(BOOL flag);
    CtiCCFeeder& setPhaseAValueBeforeControl(DOUBLE value);
    CtiCCFeeder& setPhaseBValueBeforeControl(DOUBLE value);
    CtiCCFeeder& setPhaseCValueBeforeControl(DOUBLE value);
    CtiCCFeeder& setLastWattPointTime(const CtiTime& lastpointupdate);
    CtiCCFeeder& setLastVoltPointTime(const CtiTime& lastpointupdate);
    CtiCCFeeder& setRetryIndex(LONG value);

    void figureAndSetTargetVarValue(const string& controlMethod, const string& controlUnits, BOOL peakTimeFlag);
    CtiCCCapBankPtr getLastCapBankControlledDevice();
    CtiCCCapBankPtr findCapBankToChangeVars(double kvarSolution, CtiMultiMsg_vec& pointChanges,
                                          double leadLevel = 0, double lagLevel = 0, double currentVarValue = 0,
                                          BOOL checkLimits = true);
    bool checkForMaxKvar( long, long );
    bool removeMaxKvar( long bankId );
    CtiRequestMsg* createIncreaseVarRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents,
                                            string textInfo, DOUBLE kvarBefore, DOUBLE varAValue, DOUBLE varBValue, DOUBLE varCValue);
    CtiRequestMsg* createDecreaseVarRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents,
                                            string textInfo, DOUBLE kvarBefore, DOUBLE varAValue, DOUBLE varBValue, DOUBLE varCValue);
    CtiRequestMsg* createForcedVarRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, int action, string typeOfControl);
    void createForcedVarConfirmation(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, string typeOfControl);
    BOOL capBankControlStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, LONG minConfirmPercent, LONG failurePercent,
                                    DOUBLE varValueBeforeControl, DOUBLE currentVarLoadPointValue, LONG currentVarPointQuality,
                                    DOUBLE varAValue, DOUBLE varBValue, DOUBLE varCValue, const CtiRegression& reg);
    BOOL capBankControlPerPhaseStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, LONG minConfirmPercent,
                                                     LONG failurePercent, //DOUBLE varValueBeforeControl, DOUBLE currentVarLoadPointValue,
                                                     LONG currentVarPointQuality, DOUBLE varAValueBeforeControl, DOUBLE varBValueBeforeControl,
                                                     DOUBLE varCValueBeforeControl, DOUBLE varAValue, DOUBLE varBValue, DOUBLE varCValue,
                                            const CtiRegression& regA, const CtiRegression& regB, const CtiRegression& regC);
    string createPhaseControlStatusUpdateText(string capControlStatus, DOUBLE varAValue,DOUBLE varBValue, DOUBLE varCValue,
                                                  DOUBLE ratioA, DOUBLE ratioB, DOUBLE ratioC);
    string createControlStatusUpdateText(string capControlStatus, DOUBLE varAValue, DOUBLE ratioA);
    string createPhaseVarText(DOUBLE aValue,DOUBLE bValue, DOUBLE cValue, FLOAT multiplier);
    string createPhaseRatioText(DOUBLE aValue,DOUBLE bValue, DOUBLE cValue, FLOAT multiplier);
    string createVarText(DOUBLE aValue, FLOAT multiplier);
    BOOL isPeakDay();
    BOOL isPastMaxConfirmTime(const CtiTime& currentDateTime, LONG maxConfirmTime, LONG feederRetries);
    BOOL checkForAndProvideNeededIndividualControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages,
                                                   BOOL peakTimeFlag, LONG decimalPlaces, const string& controlUnits,
                                                   BOOL dailyMaxOpsHitFlag);
    BOOL checkForAndProvideNeededFallBackControl(const CtiTime& currentDateTime,
                            CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    void orderBanksOnFeeder();
    DOUBLE figureCurrentSetPoint(const CtiTime& currentDateTime);
    BOOL isPeakTime(const CtiTime& currentDateTime);
    BOOL isControlPoint(LONG pointid);
    void updateIntegrationVPoint(const CtiTime &currentDateTime, const CtiTime &nextCheckTime);
    void updateIntegrationWPoint(const CtiTime &currentDateTime, const CtiTime &nextCheckTime);
    CtiCCFeeder& figureEstimatedVarLoadPointValue();
    bool isAlreadyControlled(LONG minConfirmPercent,
                             LONG currentVarPointQuality, DOUBLE varAValueBeforeControl,
                             DOUBLE varBValueBeforeControl, DOUBLE varCValueBeforeControl,
                             DOUBLE varAValue, DOUBLE varBValue, DOUBLE varCValue,
                             DOUBLE varValueBeforeControl, DOUBLE currentVarLoadPointValue,
                             const CtiRegression& reg, const CtiRegression& regA, const CtiRegression& regB,
                             const CtiRegression& regC,  BOOL usePhaseData, BOOL useTotalizedControl);

    void fillOutBusOptimizedInfo(BOOL peakTimeFlag);
    BOOL attemptToResendControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages, LONG maxConfirmTime);
    BOOL checkForAndPerformVerificationSendRetry(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages, LONG maxConfirmTime, LONG sendRetries);
    BOOL checkMaxDailyOpCountExceeded(CtiMultiMsg_vec &pointChanges);
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
    double computeRegression( CtiTime time );
    string createTextString(const string& controlMethod, int control, DOUBLE controlValue, DOUBLE monitorValue);
    void createCannotControlBankText(string text, string commandString, CtiMultiMsg_vec& ccEvents);
    void resetVerificationFlags();

    CtiRequestMsg* createIncreaseVarVerificationRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents,
                                                        string textInfo, DOUBLE kvarBefore, DOUBLE varAValue, DOUBLE varBValue, DOUBLE varCValue);
    CtiRequestMsg* createDecreaseVarVerificationRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents,
                                                        string textInfo, DOUBLE kvarBefore, DOUBLE varAValue, DOUBLE varBValue, DOUBLE varCValue);
    bool startVerificationOnCapBank(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    BOOL sendNextCapBankVerificationControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, CtiMultiMsg_vec& pilMessages);
    CtiRequestMsg*  createCapBankVerificationControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents,
                                          CtiMultiMsg_vec& pilMessages, CtiCCCapBank* currentCapBank, int control);

    BOOL isVerificationAlreadyControlled(long minConfirmPercent, long quality, DOUBLE varAValueBeforeControl,
                             DOUBLE varBValueBeforeControl, DOUBLE varCValueBeforeControl,
                             DOUBLE varAValue, DOUBLE varBValue, DOUBLE varCValue, double oldVarValue, double newVarValue,
                             BOOL usePhaseData, BOOL useTotalizedControl);



    BOOL areThereMoreCapBanksToVerify();
    CtiCCFeeder& getNextCapBankToVerify();

    BOOL capBankVerificationStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents, LONG minConfirmPercent,
                                         LONG failurePercent, DOUBLE varAValue, DOUBLE varBValue, DOUBLE varCValue);
    BOOL capBankVerificationPerPhaseStatusUpdate(CtiMultiMsg_vec& pointChanges, CtiMultiMsg_vec& ccEvents,
                                                 LONG minConfirmPercent, LONG failPercent);
    CtiCCFeeder& addAllFeederPointsToMsg(std::set<long>& pointAddMsg);
    CtiCCCapBank* getMonitorPointParentBank(CtiCCMonitorPoint* point);

    bool isDataOldAndFallBackNecessary(string controlUnits);

    CtiCCOperationStats& getOperationStats();
    CtiCCConfirmationStats& getConfirmationStats();
    CtiCCOriginalParent& getOriginalParent();

    BOOL isDirty() const;
    void dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime);

    //Members inherited from RWCollectable
    void saveGuts(RWvostream& ) const;

    CtiCCFeeder& operator=(const CtiCCFeeder& right);

    CtiCCFeeder* replicate() const;

    void setDynamicData(Cti::RowReader& rdr);

    std::vector <CtiCCMonitorPointPtr>& getMultipleMonitorPoints() {return _multipleMonitorPoints;};

    bool areAllPhasesSuccess(DOUBLE ratioA, DOUBLE ratioB, DOUBLE ratioC, DOUBLE confirmPercent);
    bool areAllPhasesQuestionable(DOUBLE ratioA, DOUBLE ratioB, DOUBLE ratioC, DOUBLE confirmPercent, DOUBLE failPercent);
    bool shouldCapBankBeFailed(DOUBLE ratioA, DOUBLE ratioB, DOUBLE ratioC, DOUBLE failPercent);
    bool isAnyPhaseFail(DOUBLE ratioA, DOUBLE ratioB, DOUBLE ratioC, DOUBLE failPercent, LONG &numFailedPhases);
    bool isResponseQuestionable(DOUBLE ratio, DOUBLE confirmPercent, DOUBLE failPercent);
    bool isResponseFail(DOUBLE ratio, DOUBLE failPercent);
    bool isResponseSuccess(DOUBLE ratio, DOUBLE confirmPercent);
    string getPhaseIndicatorString(LONG capState, DOUBLE ratioA, DOUBLE ratioB, DOUBLE ratioC, DOUBLE confirmPercent, DOUBLE failPercent);
    string getQuestionablePhasesString(DOUBLE ratioA, DOUBLE ratioB, DOUBLE ratioC, DOUBLE confirmPercent, DOUBLE failPercent);
    string getFailedPhasesString(DOUBLE ratioA, DOUBLE ratioB, DOUBLE ratioC, DOUBLE confirmPercent, DOUBLE failPercent);

private:

    LONG _parentId; //subBusId
    BOOL _multiMonitorFlag;

    string _maplocationid;
    LONG _currentvarloadpointid;
    DOUBLE _currentvarloadpointvalue;
    LONG _currentwattloadpointid;
    DOUBLE _currentwattloadpointvalue;
    LONG _currentvoltloadpointid;
    DOUBLE _currentvoltloadpointvalue;

    float _displayorder;

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
    BOOL _lastVerificationMsgSentSuccessful;

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
    BOOL _totalizedControlFlag;

    DOUBLE _phaseAvalue;
    DOUBLE _phaseBvalue;
    DOUBLE _phaseCvalue;
    DOUBLE _phaseAvalueBeforeControl;
    DOUBLE _phaseBvalueBeforeControl;
    DOUBLE _phaseCvalueBeforeControl;

    CtiTime _lastWattPointTime;
    CtiTime _lastVoltPointTime;
    LONG _retryIndex;

    CtiCCOperationStats _operationStats;
    CtiCCConfirmationStats _confirmationStats;
    CtiCCOriginalParent _originalParent;

    //don't stream
    BOOL _insertDynamicDataFlag;
    BOOL _dirty;

    void restore(Cti::RowReader& rdr);
    string doubleToString(DOUBLE doubleVal, LONG decimalPlaces);

    std::vector <CtiCCMonitorPointPtr> _multipleMonitorPoints;

    bool checkForRateOfChange(const CtiRegression& reg, const CtiRegression& regA, const CtiRegression& regB, const CtiRegression& regC);
    CtiRegression regression;
    CtiRegression regressionA;
    CtiRegression regressionB;
    CtiRegression regressionC;
};

typedef CtiCCFeeder* CtiCCFeederPtr;
