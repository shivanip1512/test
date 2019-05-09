#include "precompiled.h"

#include "ccfeeder.h"
#include "ccid.h"
#include "database_util.h"
#include "ccsubstationbusstore.h"
#include "capcontroller.h"
#include "msg_signal.h"
#include "msg_lmcontrolhistory.h"
#include "tbl_pt_alarm.h"
#include "Exceptions.h"

using Cti::CapControl::PointResponse;
using Cti::CapControl::createPorterRequestMsg;
using Cti::CapControl::createBankOpenRequest;
using Cti::CapControl::createBankCloseRequest;
using Cti::CapControl::createBankFlipRequest;
using Cti::CapControl::sendCapControlOperationMessage;
using Cti::CapControl::EventLogEntry;
using Cti::CapControl::EventLogEntries;
using Cti::CapControl::deserializeFlag;
using Cti::CapControl::serializeFlag;
using Cti::CapControl::populateFlag;
using Cti::CapControl::calculateKVARSolution;
using Cti::CapControl::formatMapInfo;
using Cti::CapControl::eligibleForVoltageControl;

using namespace Cti::Messaging::CapControl;
using std::endl;
using std::string;
using std::map;

extern unsigned long _CC_DEBUG;
extern bool _IGNORE_NOT_NORMAL_FLAG;
extern unsigned long _SEND_TRIES;
extern bool _USE_FLIP_FLAG;
extern unsigned long _POINT_AGE;
extern unsigned long _SCAN_WAIT_EXPIRE;
extern bool _RETRY_FAILED_BANKS;
extern unsigned long _MAX_KVAR;
extern unsigned long _MAX_KVAR_TIMEOUT;
extern unsigned long _LIKEDAY_OVERRIDE_TIMEOUT;
extern bool _RATE_OF_CHANGE;
extern unsigned long _RATE_OF_CHANGE_DEPTH;
extern long _MAXOPS_ALARM_CATID;
extern bool _RETRY_ADJUST_LAST_OP_TIME;
extern unsigned long _REFUSAL_TIMEOUT;
extern bool _USE_PHASE_INDICATORS;

DEFINE_COLLECTABLE( CtiCCFeeder, CTICCFEEDER_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCFeeder::CtiCCFeeder( StrategyManager * strategyManager )
    :   Conductor( strategyManager ),
        _displayorder( 0 ),
        _lastcapbankcontrolleddeviceid( 0 ),
        _busoptimizedvarcategory( 1 ),
        _busoptimizedvaroffset( 0 ),
        _peakTimeFlag( false ),
        _verificationFlag( false ),
        _performingVerificationFlag( false ),
        _verificationDoneFlag( false ),
        _preOperationMonitorPointScanFlag( false ),
        _operationSentWaitFlag( false ),
        _postOperationMonitorPointScanFlag( false ),
        _waitForReCloseDelayFlag( false ),
        _maxDailyOpsHitFlag( false ),
        _ovUvDisabledFlag( false ),
        _correctionNeededNoBankAvailFlag( false ),
        _lastVerificationMsgSentSuccessful( false ),
        _currentVerificationCapBankId( -1 ),
        _retryIndex( 0 ),
        _likeDayControlFlag( false ),
        _porterRetFailFlag( false )
{
}

CtiCCFeeder::CtiCCFeeder(Cti::RowReader& rdr, StrategyManager * strategyManager)
    :   Conductor( rdr, strategyManager ),
        _displayorder( 0 ),
        _lastcapbankcontrolleddeviceid( 0 ),
        _busoptimizedvarcategory( 1 ),
        _busoptimizedvaroffset( 0 ),
        _peakTimeFlag( false ),
        _verificationFlag( false ),
        _performingVerificationFlag( false ),
        _verificationDoneFlag( false ),
        _preOperationMonitorPointScanFlag( false ),
        _operationSentWaitFlag( false ),
        _postOperationMonitorPointScanFlag( false ),
        _waitForReCloseDelayFlag( false ),
        _maxDailyOpsHitFlag( false ),
        _ovUvDisabledFlag( false ),
        _correctionNeededNoBankAvailFlag( false ),
        _lastVerificationMsgSentSuccessful( false ),
        _currentVerificationCapBankId( -1 ),
        _retryIndex( 0 ),
        _likeDayControlFlag( false ),
        _porterRetFailFlag( false )
{
    _originalParent.setPAOId( getPaoId() );

    if ( hasDynamicData( rdr["AdditionalFlags"] ) )
    {
        setDynamicData( rdr );
    }

    if ( ! rdr["OriginalParentId"].isNull() )
    {
        _originalParent.restore( rdr );
    }

    // we hit max ops and were disabled, but re-enabled the feeder manually

    if ( getMaxDailyOpsHitFlag() && ! getDisableFlag() )
    {
        setMaxDailyOpsHitFlag( false );
    }
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCFeeder::~CtiCCFeeder()
{
    try
    {
        delete_container(_cccapbanks);
        _cccapbanks.clear();
    }
    catch (...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

CtiCCOriginalParent& CtiCCFeeder::getOriginalParent()
{
    return _originalParent;
}

const CtiCCOriginalParent& CtiCCFeeder::getOriginalParent() const
{
    return _originalParent;
}

/*---------------------------------------------------------------------------
    getDisplayOrder

    Returns the display order of the feeder
---------------------------------------------------------------------------*/
float CtiCCFeeder::getDisplayOrder() const
{
    return _displayorder;
}

long CtiCCFeeder::getRetryIndex() const
{
    return _retryIndex;
}

/*---------------------------------------------------------------------------
    getLastCapBankControlledDeviceId

    Returns the device id of the last cap bank controlled of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getLastCapBankControlledDeviceId() const
{
    return _lastcapbankcontrolleddeviceid;
}

CtiCCCapBankPtr CtiCCFeeder::getLastCapBankControlledDevice()
{
    CtiCCCapBankPtr bank = CtiCCSubstationBusStore::getInstance()->findCapBankByPAObjectID(_lastcapbankcontrolleddeviceid);

    return bank;
}

/*---------------------------------------------------------------------------
    getBusOptimizedVarCategory

    Returns the bus optimized var category of the feeder
---------------------------------------------------------------------------*/
long CtiCCFeeder::getBusOptimizedVarCategory() const
{
    return _busoptimizedvarcategory;
}

/*---------------------------------------------------------------------------
    getBusOptimizedVarOffset

    Returns the bus optimized var offset of the feeder
---------------------------------------------------------------------------*/
double CtiCCFeeder::getBusOptimizedVarOffset() const
{
    return _busoptimizedvaroffset;
}

/*---------------------------------------------------------------------------
    getPeakTimeFlag

    Returns the PeakTimeFlag of the feeder
---------------------------------------------------------------------------*/
bool CtiCCFeeder::getPeakTimeFlag() const
{
    return _peakTimeFlag;
}

bool CtiCCFeeder::getPorterRetFailFlag() const
{
    return _porterRetFailFlag;
}

/*---------------------------------------------------------------------------
    getCCCapBanks

    Returns the list of cap banks in the feeder
---------------------------------------------------------------------------*/
CtiCCCapBank_SVector& CtiCCFeeder::getCCCapBanks()
{
    return _cccapbanks;
}

const CtiCCCapBank_SVector& CtiCCFeeder::getCCCapBanks() const
{
    return _cccapbanks;
}



std::list<int> CtiCCFeeder::getAllCapBankIds()
{
    std::list<int> ids;
    for each (const CtiCCCapBankPtr c in _cccapbanks)
    {
        ids.push_back(c->getPaoId());
    }
    return ids;
}

std::vector<CtiCCCapBankPtr> CtiCCFeeder::getAllSwitchedCapBanks( )
{
    std::vector<CtiCCCapBankPtr> banks;
    for each (const CtiCCCapBankPtr c in _cccapbanks)
    {
        if( ciStringEqual(c->getOperationalState(), CtiCCCapBank::SwitchedOperationalState) )
        {
            banks.push_back(c);
        }
    }
    return banks;
}

std::vector<CtiCCCapBankPtr> CtiCCFeeder::getAllCapBanks( )
{
    std::vector<CtiCCCapBankPtr> banks;
    banks.assign(_cccapbanks.begin(), _cccapbanks.end());
    return banks;
}

/*---------------------------------------------------------------------------
    setDisplayOrder

    Sets the display order of the feeder
---------------------------------------------------------------------------*/
void CtiCCFeeder::setDisplayOrder(float order)
{
    _displayorder = order;
}

/*---------------------------------------------------------------------------
    setLastCapBankControlledDeviceId

    Sets the device id of the last cap bank controlled in the feeder
---------------------------------------------------------------------------*/
void CtiCCFeeder::setLastCapBankControlledDeviceId(long lastcapbank)
{
    updateDynamicValue( _lastcapbankcontrolleddeviceid, lastcapbank );
}

// These 2 functions are here just to unit test the sort comparator for bus optimized feeder
void CtiCCFeeder::setBusOptimizedVarCategory(const long varcategory)
{
    _busoptimizedvarcategory = varcategory;
}
void CtiCCFeeder::setBusOptimizedVarOffset(const double varoffset)
{
    _busoptimizedvaroffset = varoffset;
}

/*---------------------------------------------------------------------------
    setParentPeakTimeFlag

    Sets the ParentPeakTimeFlag in the feeder
---------------------------------------------------------------------------*/
void CtiCCFeeder::setPeakTimeFlag( bool peakTimeFlag )
{
    if ( updateDynamicValue( _peakTimeFlag, peakTimeFlag ) )
    {
        CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
        if ( auto sub = store->findSubBusByPAObjectID( getParentId() ) )
        {
            sub->setBusUpdatedFlag( true );
        }
    }
}

void CtiCCFeeder::setPorterRetFailFlag(bool flag)
{
    updateDynamicValue( _porterRetFailFlag, flag );
}

CtiCCCapBank* CtiCCFeeder::findCapBankToChangeVars(double kvarSolution,  CtiMultiMsg_vec& pointChanges, double leadLevel, double lagLevel, double currentVarValue,
                                                   bool checkLimits)
{
    using namespace Cti::CapControl;

    CtiCCCapBankPtr returnCapBank = NULL;
    CtiTime currentTime = CtiTime();
    BankOperationType solution;
    bool endDayFlag = false;
    std::vector<CtiCCCapBankPtr> banks;

    if (kvarSolution == 0.0)
    {
        return NULL;
    }
    else if (kvarSolution < 0.0)
    {
        solution = BankOperation_Close;
        // Sort according to CloseOrder.
        CtiCCCapBank_SCloseVector closeCaps;
        for (int i = 0; i < _cccapbanks.size(); i++)
        {
            closeCaps.insert(_cccapbanks[i]);
        }
        banks = closeCaps.get_container();
    }
    else
    {
        solution = BankOperation_Open;
        // Sort according to TripOrder..
        CtiCCCapBank_STripVector tripCaps;
        for (int i = 0; i < _cccapbanks.size(); i++)
        {
            tripCaps.insert(_cccapbanks[i]);
        }
        banks = tripCaps.get_container();
    }

    for (int i = 0; i < banks.size(); i++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)banks[i];

        if (currentCapBank->getIgnoreFlag() &&
            currentTime >= CtiTime(currentCapBank->getLastStatusChangeTime().seconds() + (_REFUSAL_TIMEOUT * 60)))
        {
            currentCapBank->setIgnoreFlag(false);
        }

        long controlStatus = currentCapBank->getControlStatus();
        bool correctControlStatus;

        if (solution == BankOperation_Close)
        {
            correctControlStatus = (controlStatus == CtiCCCapBank::Open || controlStatus == CtiCCCapBank::OpenQuestionable || controlStatus == CtiCCCapBank::OpenPending);
        }
        else
        {
            correctControlStatus = (controlStatus == CtiCCCapBank::Close || controlStatus == CtiCCCapBank::CloseQuestionable || controlStatus == CtiCCCapBank::ClosePending);
        }

        if (!currentCapBank->getDisableFlag() && !currentCapBank->getControlInhibitFlag() &&
            ciStringEqual(currentCapBank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState) &&
            correctControlStatus && !currentCapBank->getIgnoreFlag())
        {
            //Before anything, make sure this bank will not push past a threshold.
            if (checkLimits && !((leadLevel == 0) && (lagLevel == 0) && (currentVarValue == 0)))
            {
                int bankSize = currentCapBank->getBankSize();
                if (solution == BankOperation_Close)
                {
                    int newValue = currentVarValue - bankSize;
                    if ((newValue <= leadLevel))
                    {
                        //This would cause another operation, so try a different bank.

                        CTILOG_INFO(dout, "CapBank skipped because it was too large for the lead/lag range. Name: " << currentCapBank->getPaoName() << " Id: " << currentCapBank->getPaoId() << " size: " << currentCapBank->getBankSize());

                        break;
                    }
                }
                else
                {
                    int newValue = currentVarValue + bankSize;
                    if ((newValue >= lagLevel))
                    {
                        //This would cause another operation, so try a different bank.

                        CTILOG_INFO(dout, "CapBank skipped because it was too large for the lead/lag range. Name: " << currentCapBank->getPaoName() << " Id: " << currentCapBank->getPaoId() << " size: " << currentCapBank->getBankSize());

                        break;
                    }
                }
            }

            // Check max daily ops.
            if (currentCapBank->getMaxDailyOps() > 0 &&
                !currentCapBank->getMaxDailyOpsHitFlag() &&
                currentCapBank->getCurrentDailyOperations() >= currentCapBank->getMaxDailyOps())
            {
                currentCapBank->setMaxDailyOpsHitFlag(true);
                string text = string("CapBank Exceeded Max Daily Operations");
                string additional = "CapBank: " + currentCapBank->getPaoName() + formatMapInfo( currentCapBank );

                if (currentCapBank->getOperationAnalogPointId() > 0)
                {
                    CtiSignalMsg* pSig = new CtiSignalMsg(currentCapBank->getOperationAnalogPointId(),5,text,additional,CapControlLogType, _MAXOPS_ALARM_CATID, "cap control",
                                                                        TAG_ACTIVE_ALARM /*tags*/, 0 /*pri*/, 0 /*millis*/, currentCapBank->getCurrentDailyOperations() );
                    pSig->setCondition(CtiTablePointAlarming::highReasonability);
                    pointChanges.push_back(pSig);
                }

                // We should disable bank if the flag says so
                if (currentCapBank->getMaxOpsDisableFlag())
                {
                    CtiCCSubstationBusStore::getInstance()->UpdatePaoDisableFlagInDB(currentCapBank, true);

                    text = string("CapBank Disabled");
                    if (currentCapBank->getOperationAnalogPointId() > 0)
                    {
                        CtiSignalMsg* pSig = new CtiSignalMsg(currentCapBank->getOperationAnalogPointId(),5,text,additional,CapControlLogType, _MAXOPS_ALARM_CATID, "cap control",
                                                              TAG_ACTIVE_ALARM /*tags*/, 0 /*pri*/, 0 /*millis*/, currentCapBank->getCurrentDailyOperations() );
                        pSig->setCondition(CtiTablePointAlarming::highReasonability);
                        pointChanges.push_back(pSig);
                    }

                    // write to the event log
                    {
                        long    stationID, areaID, specialAreaID;

                        {
                            CtiCCSubstationBusStore * store = CtiCCSubstationBusStore::getInstance();
                            CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

                            store->getFeederParentInfo( this, specialAreaID, areaID, stationID );
                        }

                        CtiCapController::submitEventLogEntry(
                            EventLogEntry( 0,
                                           currentCapBank->getStatusPointId() > 0
                                                ? currentCapBank->getStatusPointId()
                                                : SYS_PID_CAPCONTROL,
                                           specialAreaID, areaID, stationID,
                                           getParentId(),
                                           getPaoId(),
                                           capControlDisable,
                                           getEventSequence(),
                                           0,
                                           "CapBank Disabled - Exceeded Max Daily Operations",
                                           Cti::CapControl::SystemUser ) );
                    }

                    if (!getStrategy()->getEndDaySettings().compare("Trip") && (solution == BankOperation_Open))
                    {
                        // We need this to return this bank (since we disabled it).
                        endDayFlag = true;
                    }
                    else if (!getStrategy()->getEndDaySettings().compare("Close") && (solution == BankOperation_Close))
                    {
                        // We need this to return this bank (since we disabled it).
                        endDayFlag = true;
                    }
                    else
                    {
                        // Skip this the break statement, lets try the next bank since we disabled this one.
                        continue;
                    }
                }
            }

            if (!currentCapBank->getDisableFlag() || endDayFlag)
            {
                returnCapBank = currentCapBank;
            }

            break;
        }
    }

    if (returnCapBank == NULL && _RETRY_FAILED_BANKS)
    {
        for (int i = 0; i < banks.size(); i++)
        {
            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)banks[i];

            if (!currentCapBank->getDisableFlag() && !currentCapBank->getControlInhibitFlag() &&
                ciStringEqual(currentCapBank->getOperationalState(), CtiCCCapBank::SwitchedOperationalState) &&
                (currentCapBank->getControlStatus() == CtiCCCapBank::CloseFail ||
                 currentCapBank->getControlStatus() == CtiCCCapBank::OpenFail))
            {
                if( solution == BankOperation_Close && !currentCapBank->getRetryCloseFailedFlag() )
                {
                    currentCapBank->setRetryCloseFailedFlag(true);
                    return currentCapBank;
                }
                if( solution == BankOperation_Open && !currentCapBank->getRetryOpenFailedFlag() )
                {
                    currentCapBank->setRetryOpenFailedFlag(true);
                    return currentCapBank;
                }
            }
        }
    }

    return returnCapBank;
}

bool CtiCCFeeder::checkForMaxKvar( long bankId, long bankSize )
{
    //check to make sure we will not over run the max kvar cparm.
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    return store->addKVAROperation(bankId, bankSize);
}

bool CtiCCFeeder::removeMaxKvar( long bankId )
{
    //check to make sure we will not over run the max kvar cparm.
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    return store->removeKVAROperation(bankId);
}

/*---------------------------------------------------------------------------
    createIncreaseVarRequest

    Creates a CtiRequestMsg to open the next cap bank to increase the
    var level for a strategy.
---------------------------------------------------------------------------*/
CtiRequestMsg* CtiCCFeeder::createIncreaseVarRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents,
                                                     string textInfo, double kvarBefore, double varAValue, double varBValue, double varCValue)
{
    if( capBank == NULL )
    {
        return 0;
    }

    //Determine if we are at max KVAR and don't create the request if we are.
    if( checkForMaxKvar(capBank->getPaoId(), capBank->getBankSize() ) == false )
    {
        CTILOG_INFO(dout, "Exceeded Max Kvar of "<< _MAX_KVAR<< ", not doing control on bank: "<< capBank->getPaoName() << ". " );
        return 0;
    }

    setLastCapBankControlledDeviceId(capBank->getPaoId());
    //capBank->setControlStatus(CtiCCCapBank::OpenPending);

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending,capBank, true);
    capBank->setControlStatusQuality(CC_Normal);
    figureEstimatedVarLoadPointValue();
    setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations()+1, pointChanges);
    capBank->setTotalOperations(capBank->getTotalOperations() + 1);
    capBank->setCurrentDailyOperations(capBank->getCurrentDailyOperations() + 1);
    setRecentlyControlledFlag(true);

    setVarValueBeforeControl(kvarBefore);

    capBank->setBeforeVarsString(createVarText(kvarBefore, 1.0));
    capBank->setAfterVarsString(" --- ");
    capBank->setPercentChangeString(" --- ");


    if( capBank->getStatusPointId() > 0 )
    {
        string additional = "Sub: " + getParentName() + " /Feeder: " + getPaoName() + formatMapInfo( this );

        pointChanges.push_back(new CtiSignalMsg(capBank->getStatusPointId(),0,textInfo,additional,CapControlLogType,SignalEvent, "cap control"));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
        pointChanges.push_back(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType, capBank->getPaoName()));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
        capBank->setLastStatusChangeTime(CtiTime());

        capBank->setActionId( CCEventActionIdGen(capBank->getStatusPointId()) + 1);
        string stateInfo = capBank->getControlStatusQualityString();
        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);

        EventLogEntry logEntry(0, capBank->getStatusPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlCommandSent, getEventSequence(),
                               capBank->getControlStatus(), textInfo, "cap control", kvarBefore, kvarBefore, 0,
                               capBank->getIpAddress(), capBank->getActionId(), stateInfo, varAValue, varBValue, varCValue);
        logEntry.setEventSubtype( StandardOperation );
        ccEvents.push_back(logEntry);
        sendCapControlOperationMessage( CapControlOperationMessage::createOpenBankMessage( capBank->getPaoId(), CtiTime() ) );
    }
    else
    {
        CTILOG_ERROR(dout, "Cap Bank: " << capBank->getPaoName()
        << " DeviceID: " << capBank->getPaoId() << " doesn't have a status point!");
    }

    if( capBank->getOperationAnalogPointId() > 0 )
    {
        pointChanges.push_back(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getTotalOperations(),NormalQuality,AnalogPointType));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        ccEvents.push_back(EventLogEntry(0, capBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlSetOperationCount, getEventSequence(), capBank->getTotalOperations(), "opCount adjustment", "cap control"));
    }

    if (capBank->getPointIdByAttribute( Attribute::ControlPoint ) > 0)
    {
        CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg ( capBank->getControlDeviceId(),
                                                                           capBank->getPointIdByAttribute( Attribute::ControlPoint ),
                                                                           capBank->getControlStatus(),
                                                                           CtiTime(), -1, 100 );
        hist->setMessagePriority( hist->getMessagePriority() + 2 );
        pointChanges.push_back( hist );
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(4);
    }

    std::unique_ptr<CtiRequestMsg> reqMsg = createBankOpenRequest(*capBank);
    reqMsg->setSOE(4);

    return reqMsg.release();
}

CtiRequestMsg* CtiCCFeeder::createIncreaseVarVerificationRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents,
                                                                 string textInfo, int controlOp, double kvarBefore, double varAValue, double varBValue, double varCValue )
{
    if( capBank == NULL )
        return 0;

    //Determine if we are at max KVAR and don't create the request if we are.
    if( checkForMaxKvar(capBank->getPaoId(), capBank->getBankSize() ) == false )
    {
        CTILOG_INFO(dout, "Exceeded Max Kvar of "<< _MAX_KVAR<< ", not doing control on bank: "<< capBank->getPaoName() << ". " );
        return 0;
    }

    if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
    {
        CTILOG_DEBUG(dout, "***VERIFICATION INFO***  CBid: "<<capBank->getPaoId()<<" vCtrlIdx: "<< capBank->getVCtrlIndex() <<"  CurrControlStatus: " << capBank->getControlStatus() << "  Control Open Sent Now ");
    }
    setLastCapBankControlledDeviceId(capBank->getPaoId());
    //capBank->setControlStatus(CtiCCCapBank::OpenPending);
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::OpenPending,
                                                                       capBank, true);
    capBank->setControlStatusQuality(CC_Normal);
    figureEstimatedVarLoadPointValue();
    setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations()+1, pointChanges);
    capBank->setTotalOperations(capBank->getTotalOperations() + 1);
    capBank->setCurrentDailyOperations(capBank->getCurrentDailyOperations() + 1);
    //setRecentlyControlledFlag(true);
    setVarValueBeforeControl(getCurrentVarLoadPointValue());

    capBank->setBeforeVarsString(createVarText(kvarBefore, 1.0));
    capBank->setAfterVarsString(" --- ");
    capBank->setPercentChangeString(" --- ");

    if( capBank->getStatusPointId() > 0 )
    {
        string additional = "Sub: " + getParentName() + " /Feeder: " + getPaoName() + formatMapInfo( this );

        pointChanges.push_back(new CtiSignalMsg(capBank->getStatusPointId(),0,textInfo,additional,CapControlLogType,SignalEvent, "cap control verification"));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
        pointChanges.push_back(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
        capBank->setLastStatusChangeTime(CtiTime());

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        capBank->setActionId( CCEventActionIdGen(capBank->getStatusPointId()) + 1);
        string stateInfo = capBank->getControlStatusQualityString();

        EventLogEntry logEntry(0, capBank->getStatusPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlCommandSent, getEventSequence(),
                               capBank->getControlStatus(), textInfo, "cap control verification", kvarBefore, kvarBefore, 0,
                               capBank->getIpAddress(), capBank->getActionId(), stateInfo, varAValue, varBValue, varCValue);
        logEntry.setEventSubtype( controlOp == 4 ? StandardFlipOperation : StandardOperation ); // I've died a little inside...
        ccEvents.push_back(logEntry);
    }
    else
    {
        CTILOG_ERROR(dout, "Cap Bank: " << capBank->getPaoName()
        << " DeviceID: " << capBank->getPaoId() << " doesn't have a status point!");
    }

    if( capBank->getOperationAnalogPointId() > 0 )
    {
        pointChanges.push_back(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getTotalOperations(),NormalQuality,AnalogPointType));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        ccEvents.push_back(EventLogEntry(0, capBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlSetOperationCount, getEventSequence(), capBank->getTotalOperations(), "opCount adjustment", "cap control verification"));
    }

    std::unique_ptr<CtiRequestMsg> reqMsg;

    if  (stringContainsIgnoreCase(capBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG )
    {
        reqMsg = createBankFlipRequest(*capBank);
    }
    else
    {
        reqMsg = createBankOpenRequest(*capBank);
    }
    reqMsg->setSOE(4);

    return reqMsg.release();
}

CtiRequestMsg* CtiCCFeeder::createDecreaseVarVerificationRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents,
                                                                 string textInfo, int controlOp, double kvarBefore, double varAValue, double varBValue, double varCValue )
{
    if( capBank == NULL )
        return 0;

    //Determine if we are at max KVAR and don't create the request if we are.
    if( checkForMaxKvar(capBank->getPaoId(), capBank->getBankSize() ) == false )
    {
        CTILOG_INFO(dout, "Exceeded Max Kvar of "<< _MAX_KVAR<< ", not doing control on bank: "<< capBank->getPaoName() << ". " );
        return 0;
    }

    if (_CC_DEBUG & CC_DEBUG_VERIFICATION)
    {
        CTILOG_DEBUG(dout, "***VERIFICATION INFO***  CBid: "<<capBank->getPaoId()<<" vCtrlIdx: "<< capBank->getVCtrlIndex() <<"  CurrControlStatus: " << capBank->getControlStatus() << "  Control Close Sent Now ");
    }
    setLastCapBankControlledDeviceId(capBank->getPaoId());
    //capBank->setControlStatus(CtiCCCapBank::ClosePending);
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending,
                                                                       capBank, true);
    capBank->setControlStatusQuality(CC_Normal);
    figureEstimatedVarLoadPointValue();
    setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations()+1, pointChanges);
    capBank->setTotalOperations(capBank->getTotalOperations() + 1);
    capBank->setCurrentDailyOperations(capBank->getCurrentDailyOperations() + 1);

    setVarValueBeforeControl(getCurrentVarLoadPointValue());

    capBank->setBeforeVarsString(createVarText(kvarBefore, 1.0));
    capBank->setAfterVarsString(" --- ");
    capBank->setPercentChangeString(" --- ");

    if( capBank->getStatusPointId() > 0 )
    {
        string additional = "Sub: " + getParentName() + " /Feeder: " + getPaoName() + formatMapInfo( this );

        pointChanges.push_back(new CtiSignalMsg(capBank->getStatusPointId(),0,textInfo,additional,CapControlLogType,SignalEvent, "cap control verification"));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
        pointChanges.push_back(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
        capBank->setLastStatusChangeTime(CtiTime());

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        capBank->setActionId( CCEventActionIdGen(capBank->getStatusPointId()) + 1);
        string stateInfo = capBank->getControlStatusQualityString();

        EventLogEntry logEntry(0, capBank->getStatusPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlCommandSent, getEventSequence(),
                               capBank->getControlStatus(), textInfo, "cap control verification", kvarBefore, kvarBefore, 0,
                               capBank->getIpAddress(), capBank->getActionId(), stateInfo, varAValue, varBValue, varCValue);
        logEntry.setEventSubtype( controlOp == 4 ? StandardFlipOperation : StandardOperation ); // I've died a little inside...
        ccEvents.push_back(logEntry);
    }
    else
    {
        CTILOG_WARN(dout, "Cap Bank: " << capBank->getPaoName()
        << " DeviceID: " << capBank->getPaoId() << " doesn't have a status point!" );
    }

    if( capBank->getOperationAnalogPointId() > 0 )
    {
        pointChanges.push_back(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getTotalOperations(),NormalQuality,AnalogPointType));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        ccEvents.push_back(EventLogEntry(0, capBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlSetOperationCount, getEventSequence(), capBank->getTotalOperations(), "opCount adjustment", "cap control verification"));
    }

    std::unique_ptr<CtiRequestMsg> reqMsg;

    if  (stringContainsIgnoreCase(capBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG )
    {
        reqMsg = createBankFlipRequest(*capBank);
    }
    else
    {
        reqMsg = createBankCloseRequest(*capBank);
    }
    reqMsg->setSOE(4);

    return reqMsg.release();
}


/*---------------------------------------------------------------------------
    createDecreaseVarRequest

    Creates a CtiRequestMsg to close the next cap bank to decrease the
    var level for a strategy.
---------------------------------------------------------------------------*/


CtiRequestMsg* CtiCCFeeder::createDecreaseVarRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents,
                                                     string textInfo, double kvarBefore, double varAValue, double varBValue, double varCValue)
{
    if( capBank == NULL )
    {
        return 0;
    }

    //Determine if we are at max KVAR and don't create the request if we are.
    if( checkForMaxKvar(capBank->getPaoId(), capBank->getBankSize() ) == false )
    {
        CTILOG_INFO(dout, "Exceeded Max Kvar of "<< _MAX_KVAR<< ", not doing control on bank: "<< capBank->getPaoName() << ". " );
        return 0;
    }

    setLastCapBankControlledDeviceId(capBank->getPaoId());
    //capBank->setControlStatus(CtiCCCapBank::ClosePending);
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::ClosePending,
                                                                       capBank, true);
    capBank->setControlStatusQuality(CC_Normal);
    figureEstimatedVarLoadPointValue();
    setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations()+1, pointChanges);
    capBank->setTotalOperations(capBank->getTotalOperations() + 1);
    capBank->setCurrentDailyOperations(capBank->getCurrentDailyOperations() + 1);
    setRecentlyControlledFlag(true);
    //setVarValueBeforeControl(getCurrentVarLoadPointValue());
    setVarValueBeforeControl(kvarBefore);

    capBank->setBeforeVarsString(createVarText(kvarBefore, 1.0));
    capBank->setAfterVarsString(" --- ");
    capBank->setPercentChangeString(" --- ");

    if( capBank->getStatusPointId() > 0 )
    {
        string additional = "Sub: " + getParentName() + " /Feeder: " + getPaoName() + formatMapInfo( this );

        pointChanges.push_back(new CtiSignalMsg(capBank->getStatusPointId(),0,textInfo,additional,CapControlLogType,SignalEvent, "cap control"));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
        pointChanges.push_back(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType, capBank->getPaoName()));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
        capBank->setLastStatusChangeTime(CtiTime());

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        capBank->setActionId( CCEventActionIdGen(capBank->getStatusPointId()) + 1);
        string stateInfo = capBank->getControlStatusQualityString();

        EventLogEntry logEntry(0, capBank->getStatusPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlCommandSent, getEventSequence(),
                               capBank->getControlStatus(), textInfo, "cap control", kvarBefore, kvarBefore, 0,
                               capBank->getIpAddress(), capBank->getActionId(), stateInfo, varAValue, varBValue, varCValue);
        logEntry.setEventSubtype( StandardOperation );
        ccEvents.push_back(logEntry);

        sendCapControlOperationMessage( CapControlOperationMessage::createCloseBankMessage( capBank->getPaoId(), CtiTime() ) );
    }
    else
    {
        CTILOG_WARN(dout, "Cap Bank: " << capBank->getPaoName()
        << " DeviceID: " << capBank->getPaoId() << " doesn't have a status point!" );
    }

    if( capBank->getOperationAnalogPointId() > 0 )
    {
        pointChanges.push_back(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getTotalOperations(),NormalQuality,AnalogPointType));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        ccEvents.push_back(EventLogEntry(0, capBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlSetOperationCount, getEventSequence(), capBank->getTotalOperations(), "opCount adjustment", "cap control"));
    }

    if (capBank->getPointIdByAttribute( Attribute::ControlPoint ) > 0)
    {
        CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg ( capBank->getControlDeviceId(),
                                                                           capBank->getPointIdByAttribute( Attribute::ControlPoint ),
                                                                           capBank->getControlStatus(),
                                                                           CtiTime(), -1, 100 );
        hist->setMessagePriority( hist->getMessagePriority() + 2 );
        pointChanges.push_back( hist );
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(4);
    }

    std::unique_ptr<CtiRequestMsg> reqMsg = createBankCloseRequest(*capBank);
    reqMsg->setSOE(4);

    return reqMsg.release();
}

/*---------------------------------------------------------------------------
    createDecreaseVarRequest

    Creates a CtiRequestMsg to close the next cap bank to decrease the
    var level for a strategy.
---------------------------------------------------------------------------*/
CtiRequestMsg* CtiCCFeeder::createForcedVarRequest(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, int action, string typeOfControl)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    if( capBank == NULL )
        return 0;

    //Determine if we are at max KVAR and don't create the request if we are.
    if( checkForMaxKvar(capBank->getPaoId(), capBank->getBankSize() ) == false )
    {
        CTILOG_INFO(dout, "Exceeded Max Kvar of "<< _MAX_KVAR<< ", not doing control on bank: "<< capBank->getPaoName() << ". " );
        return 0;
    }

    setLastCapBankControlledDeviceId(capBank->getPaoId());
    string textInfo = "";
    if (action == CtiCCCapBank::Close ||
        action == CtiCCCapBank::ClosePending ||
        action == CtiCCCapBank::CloseQuestionable ||
        action == CtiCCCapBank::CloseFail )
    {
        store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::Close,
                                                                       capBank, true);
        textInfo += "Close Sent, ";
        textInfo += typeOfControl;
    }
    else
    {
        store->setControlStatusAndIncrementOpCount(pointChanges, CtiCCCapBank::Open,
                                                                       capBank, true);
        textInfo += "Open Sent, ";
        textInfo += typeOfControl;
    }
    capBank->setControlStatusQuality(CC_AbnormalQuality);
    setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations()+1, pointChanges);
    capBank->setTotalOperations(capBank->getTotalOperations() + 1);
    capBank->setCurrentDailyOperations(capBank->getCurrentDailyOperations() + 1);


    capBank->setBeforeVarsString(textInfo);
    capBank->setAfterVarsString(" --- ");
    capBank->setPercentChangeString(" --- ");

    if( capBank->getStatusPointId() > 0 )
    {
        string additional = "Sub: " + getParentName() + " /Feeder: " + getPaoName() + formatMapInfo( this );

        pointChanges.push_back(new CtiSignalMsg(capBank->getStatusPointId(),0,textInfo,additional,CapControlLogType,SignalEvent, "cap control"));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);
        pointChanges.push_back(new CtiPointDataMsg(capBank->getStatusPointId(),capBank->getControlStatus(),NormalQuality,StatusPointType, capBank->getPaoName()));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(2);
        capBank->setLastStatusChangeTime(CtiTime());

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        capBank->setActionId( CCEventActionIdGen(capBank->getStatusPointId()) + 1);
        string stateInfo = capBank->getControlStatusQualityString();

        EventLogEntry logEntry(0, capBank->getStatusPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlCommandSent, getEventSequence(),
                               capBank->getControlStatus(), textInfo, "cap control", getCurrentVarLoadPointValue(), getCurrentVarLoadPointValue(),
                               0, capBank->getIpAddress(), capBank->getActionId(), stateInfo);
        logEntry.setEventSubtype( StandardOperation );
        ccEvents.push_back(logEntry);
    }
    else
    {
        CTILOG_WARN(dout, "Cap Bank: " << capBank->getPaoName()
        << " DeviceID: " << capBank->getPaoId() << " doesn't have a status point!" );
    }

    if( capBank->getOperationAnalogPointId() > 0 )
    {
        pointChanges.push_back(new CtiPointDataMsg(capBank->getOperationAnalogPointId(),capBank->getTotalOperations(),NormalQuality,AnalogPointType));
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(3);

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        ccEvents.push_back(EventLogEntry(0, capBank->getOperationAnalogPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlSetOperationCount, getEventSequence(), capBank->getTotalOperations(), "opCount adjustment", "cap control"));
    }

    if (capBank->getPointIdByAttribute( Attribute::ControlPoint ) > 0)
    {
        CtiLMControlHistoryMsg *hist = CTIDBG_new CtiLMControlHistoryMsg ( capBank->getControlDeviceId(),
                                                                           capBank->getPointIdByAttribute( Attribute::ControlPoint ),
                                                                           capBank->getControlStatus(),
                                                                           CtiTime(), -1, 100 );
        hist->setMessagePriority( hist->getMessagePriority() + 2 );
        pointChanges.push_back( hist );
        ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(4);
    }

    std::unique_ptr<CtiRequestMsg> reqMsg;

    if (capBank->getControlStatus() == CtiCCCapBank::Close )
    {
        reqMsg = createBankCloseRequest(*capBank);
    }
    else
    {
        reqMsg = createBankOpenRequest(*capBank);
    }

    reqMsg->setSOE(4);

    return reqMsg.release();
}

void CtiCCFeeder::createForcedVarConfirmation(CtiCCCapBank* capBank, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, string typeOfControl)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    string textInfo = "Var: ";
    textInfo += typeOfControl;
    textInfo += ", ";
    textInfo += capBank->getControlStatusText();

    capBank->setControlRecentlySentFlag(true);
    long stationId, areaId, spAreaId;
    store->getFeederParentInfo(this, spAreaId, areaId, stationId);
    string stateInfo = capBank->getControlStatusQualityString();
    ccEvents.push_back(EventLogEntry(0, capBank->getStatusPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capBankStateUpdate, getEventSequence(), capBank->getControlStatus(), textInfo, "cap control", getCurrentVarLoadPointValue(), getCurrentVarLoadPointValue(), 0, capBank->getIpAddress(), capBank->getActionId(), stateInfo));

}


/*---------------------------------------------------------------------------
    figureEstimatedVarLoadPointValue

    Figures out the estimated var point value according to the states
    of the individual cap banks in the feeder
---------------------------------------------------------------------------*/
void CtiCCFeeder::figureEstimatedVarLoadPointValue()
{
    if( getCurrentVarLoadPointId() > 0 )
    {
        double tempValue;
        if( getRecentlyControlledFlag() || getPerformingVerificationFlag())
            tempValue = getVarValueBeforeControl();
        else
            tempValue = getCurrentVarLoadPointValue();

        for(long i=0;i<_cccapbanks.size();i++)
        {
            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
            if( currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
                currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable )
            {
                tempValue = tempValue + currentCapBank->getBankSize();
            }
        }

        setEstimatedVarLoadPointValue(tempValue);
    }
    else
    {
        setEstimatedVarLoadPointValue(0.0);
    }
}

/*---------------------------------------------------------------------------
    isPeakTime

    Returns a boolean if it is peak time it also sets the peak time flag.
---------------------------------------------------------------------------*/
bool CtiCCFeeder::isPeakTime(const CtiTime& currentDateTime)
{
    setPeakTimeFlag( getStrategy()->isPeakTime( currentDateTime ) );

    return getPeakTimeFlag();
}

bool CtiCCFeeder::isControlPoint(long pointid)
{
    if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
    {
        if ( getStrategy()->getUnitType() == ControlStrategy::Volts &&
             getCurrentVoltLoadPointId() == pointid )
        {
            return true;
        }
        else if ( getStrategy()->getUnitType() == ControlStrategy::KVar &&
                  getCurrentVarLoadPointId() == pointid )
        {
            return true;
        }
        else if ( getStrategy()->getUnitType() == ControlStrategy::PFactorKWKVar &&
                  ( getCurrentVarLoadPointId() == pointid || getCurrentWattLoadPointId() == pointid ) )
        {
            return true;
        }
    }
    return false;
}

void CtiCCFeeder::updateIntegrationVPoint(const CtiTime &currentDateTime, const CtiTime &nextCheckTime)
{
    double controlVvalue = 0;

    if( getDisableFlag() )
    {
        return;
    }

    if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
    {
        if ( getStrategy()->getUnitType() == ControlStrategy::Volts )
        {
            controlVvalue = getCurrentVoltLoadPointValue();
        }
        else if ( getStrategy()->getUnitType() == ControlStrategy::KVar ||
                  getStrategy()->getUnitType() == ControlStrategy::PFactorKWKVar )
        {
            controlVvalue = getCurrentVarLoadPointValue();
        }
        else
        {
            //integration not implemented.
            controlVvalue = 0;
            CTILOG_INFO(dout, "**DEBUG** integration not implemented for this controlUnit method ");
        }

        if (getStrategy()->getControlInterval() > 0)
        {
            if (nextCheckTime - getStrategy()->getIntegratePeriod() <= currentDateTime)
            {
                if (nextCheckTime > currentDateTime)
                {


                    if (getIVCount() == 0)
                    {
                        setIVControlTot( controlVvalue );
                    }
                    else
                        setIVControlTot( getIVControlTot() + controlVvalue );

                    setIVCount( getIVCount() + 1 );
                }

            }
            else
            {
                setIVControlTot( controlVvalue );
                setIVCount( 1 );
            }
        }
        else
        {
            setIVControlTot( controlVvalue );
            setIVCount( 1 );
        }
    }
    if( _CC_DEBUG & CC_DEBUG_INTEGRATED )
    {
        CTILOG_DEBUG(dout, getPaoName() <<": iVControlTot = " <<getIVControlTot() <<" iVCount = "<<getIVCount());
    }
}

void CtiCCFeeder::updateIntegrationWPoint(const CtiTime &currentDateTime, const CtiTime &nextCheckTime)
{
    double controlWvalue = 0;

    if( getDisableFlag() )
    {
        return;
    }

    if ( getStrategy()->getMethodType() == ControlStrategy::IndividualFeeder )
    {
        controlWvalue = getCurrentWattLoadPointValue();
        if (getStrategy()->getControlInterval() > 0)
        {
            if (nextCheckTime - getStrategy()->getIntegratePeriod() <= currentDateTime)
            {

                if (getIWCount() == 0)
                {
                    setIWControlTot( controlWvalue );
                }
                else
                    setIWControlTot( getIWControlTot() + controlWvalue );

                setIWCount( getIWCount() + 1 );
            }
            else
            {
                setIWControlTot( controlWvalue );
                setIWCount( 1 );
            }
        }
        else
        {
            setIWControlTot( controlWvalue );
            setIWCount( 1 );
        }
    }
    if( _CC_DEBUG & CC_DEBUG_INTEGRATED )
    {
        CTILOG_DEBUG(dout, getPaoName() <<":  iWControlTot = " <<getIWControlTot() <<" iWCount = "<<getIWCount());
    }
}

void CtiCCFeeder::orderBanksOnFeeder()
{
    CtiCCCapBank_SVector displayCaps;
    CtiCCCapBank_SCloseVector closeCaps;
    CtiCCCapBank_STripVector tripCaps;
    int i=0;
    CtiCCCapBank* currentCapBank = NULL;
    for (i = 0; i < _cccapbanks.size(); i++)
    {
        displayCaps.insert(_cccapbanks[i]);
        closeCaps.insert(_cccapbanks[i]);
        tripCaps.insert(_cccapbanks[i]);
    }

    for(i=0;i<displayCaps.size();i++)
    {
        currentCapBank = (CtiCCCapBank*)displayCaps[i];
        currentCapBank->setControlOrder(i + 1);
    }

    for(i=0;i<closeCaps.size();i++)
    {
        currentCapBank = (CtiCCCapBank*)closeCaps[i];
        currentCapBank->setCloseOrder(i + 1);
    }

    for(i=0;i<tripCaps.size();i++)
    {
        currentCapBank = (CtiCCCapBank*)tripCaps[i];
        currentCapBank->setTripOrder(i + 1);
    }

}

void CtiCCFeeder::figureAndSetTargetVarValue(const string& controlMethod, const string& controlUnits, bool peakTimeFlag)
{

    if( ciStringEqual(controlMethod, ControlStrategy::IndividualFeederControlMethod ))
    {
        string feederControlUnits = controlUnits;
        if (!ciStringEqual(getStrategy()->getStrategyName(),"(none)"))
        {
            feederControlUnits = getStrategy()->getControlUnits();
        }
        if (ciStringEqual(feederControlUnits, ControlStrategy::PFactorKWKVarControlUnit) ||
            ciStringEqual(feederControlUnits, ControlStrategy::PFactorKWKQControlUnit ))
        {
            double setpoint = (peakTimeFlag?getStrategy()->getPeakPFSetPoint():getStrategy()->getOffPeakPFSetPoint());
            setKVARSolution(calculateKVARSolution(feederControlUnits, setpoint, getCurrentVarLoadPointValue(), getCurrentWattLoadPointValue(), *this));
            setTargetVarValue( getKVARSolution() + getCurrentVarLoadPointValue());
        }
        else
        {

            double lagLevel = (peakTimeFlag?getStrategy()->getPeakLag():getStrategy()->getOffPeakLag());
            double leadLevel = (peakTimeFlag?getStrategy()->getPeakLead():getStrategy()->getOffPeakLead());
            double setpoint = (lagLevel + leadLevel)/2;
            setKVARSolution( calculateKVARSolution(feederControlUnits, setpoint, getCurrentVarLoadPointValue(), getCurrentWattLoadPointValue(), *this));
            if( ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit) )
            {
                setTargetVarValue( getKVARSolution() + getCurrentVoltLoadPointValue());
            }
            else
            {
                setTargetVarValue( getKVARSolution() + getCurrentVarLoadPointValue());
            }
        }
    }
    else
    {
        setTargetVarValue(0);
    }
}

/*---------------------------------------------------------------------------
    checkForAndProvideNeededIndividualControl


---------------------------------------------------------------------------*/
bool CtiCCFeeder::checkForAndProvideNeededIndividualControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages, bool peakTimeFlag, long decimalPlaces, const string& controlUnits, bool dailyMaxOpsHitFlag)
{
    bool returnBoolean = false;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    double lagLevel = (peakTimeFlag?getStrategy()->getPeakLag():getStrategy()->getOffPeakLag());
    double leadLevel = (peakTimeFlag?getStrategy()->getPeakLead():getStrategy()->getOffPeakLead());
    double setpoint = (lagLevel + leadLevel)/2;
    bool maxOpsDisableFlag = getStrategy()->getMaxOperationDisableFlag();
    string feederControlUnits = controlUnits;
    if (!ciStringEqual(getStrategy()->getStrategyName(),"(none)"))
    {
        feederControlUnits = getStrategy()->getControlUnits();
        maxOpsDisableFlag = getStrategy()->getMaxOperationDisableFlag();
    }
    if( ciStringEqual(feederControlUnits, ControlStrategy::PFactorKWKVarControlUnit) ||
       ciStringEqual(feederControlUnits, ControlStrategy::PFactorKWKQControlUnit) )
        setpoint = (peakTimeFlag?getStrategy()->getPeakPFSetPoint():getStrategy()->getOffPeakPFSetPoint());

    //Integration Control Point setting...
    setIWControl(getCurrentWattLoadPointValue());
    if (ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit))
        setIVControl(getCurrentVoltLoadPointValue());
    else
        setIVControl(getCurrentVarLoadPointValue());
    if (getStrategy()->getIntegrateFlag() && getStrategy()->getIntegratePeriod() > 0)
    {
        if (getIVCount() > 0)
            setIVControl(getIVControlTot() / getIVCount());
        if (getIWCount() > 0)
            setIWControl(getIWControlTot() / getIWCount());

        if( _CC_DEBUG & CC_DEBUG_INTEGRATED )
        {
            CTILOG_DEBUG(dout, getPaoName() << "  USING INTEGRATED CONTROL - iVControl=iVControlTot/iVCount ( "<<
                                   getIVControl()<<" = "<< getIVControlTot() <<" / "<<getIVCount()<<" )"<< endl
                            << getPaoName() <<" USING INTEGRATED CONTROL - iWControl=iWControlTot/iWCount ( "<<
                                   getIWControl()<<" = "<< getIWControlTot() <<" / "<<getIWCount()<<" )");
        }
     //resetting integration total...
        if (ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit))
            setIVControlTot(getCurrentVoltLoadPointValue());
        else
            setIVControlTot(getCurrentVarLoadPointValue());
        setIVCount(1);
        setIWControlTot(getCurrentWattLoadPointValue());
        setIWCount(1);
    }

    setKVARSolution( calculateKVARSolution(feederControlUnits,setpoint,getIVControl(),getIWControl(), *this));
    setTargetVarValue( getKVARSolution() + getIVControl());


    //if current var load is outside of range defined by the set point plus/minus the bandwidths
    CtiRequestMsg* request = NULL;

    //checks max daily op count, feeder disable if maxOperationDisableFlag set.
    checkMaxDailyOpCountExceeded(pointChanges);

    bool arePointsNormalQuality = ( getCurrentVarPointQuality() == NormalQuality &&
                         ( ciStringEqual(feederControlUnits,ControlStrategy::PFactorKWKVarControlUnit) ? getCurrentWattPointQuality() == NormalQuality :
                         ( ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit) ? getCurrentVoltPointQuality() == NormalQuality : getCurrentVarPointQuality()  == NormalQuality) ) );

    if( !getDisableFlag() &&
        !getWaiveControlFlag() &&
        ( !_IGNORE_NOT_NORMAL_FLAG || arePointsNormalQuality ) &&
        ( currentDateTime.seconds() >= getLastOperationTime().seconds() + getStrategy()->getControlDelayTime() ) )
    {
        if( (ciStringEqual(feederControlUnits, ControlStrategy::KVarControlUnit) &&
             getCurrentVarLoadPointId() > 0) ||
            (ciStringEqual(feederControlUnits, ControlStrategy::VoltsControlUnit) &&
             getCurrentVarLoadPointId() > 0 && getCurrentVoltLoadPointId() > 0 ) )
        {
            if( (ciStringEqual(feederControlUnits,ControlStrategy::KVarControlUnit) &&
                (getIVControl() > lagLevel || getIVControl() < leadLevel )) ||
                (ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit) &&
                (getIVControl() < lagLevel || getIVControl() > leadLevel) ) )
            {

                try
                {
                    if( ( ciStringEqual(feederControlUnits,ControlStrategy::KVarControlUnit) &&
                          lagLevel < getIVControl() ) ||
                        ( ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit) &&
                          lagLevel > getIVControl() ) )
                    {
                        //if( _CC_DEBUG )
                        if( ciStringEqual(feederControlUnits, ControlStrategy::KVarControlUnit) )
                        {
                            CTILOG_INFO(dout, "Attempting to Decrease Var level in feeder: " << getPaoName().data());
                        }
                        else
                        {
                            setKVARSolution(-1);
                            CTILOG_INFO(dout, "Attempting to Increase Volt level in feeder: " << getPaoName().data());
                        }

                        CtiCCCapBank* capBank = findCapBankToChangeVars(getKVARSolution(), pointChanges, leadLevel, lagLevel, getCurrentVarLoadPointValue(),!(ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit)));

                        if( capBank != NULL &&
                            capBank->getRecloseDelay() > 0 &&
                            currentDateTime.seconds() < capBank->getLastStatusChangeTime().seconds() + capBank->getRecloseDelay() )
                        {
                            CTILOG_INFO(dout, "Can Not Close Cap Bank: " << capBank->getPaoName() << " because it has not passed its reclose delay.");
                            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                            {
                                Cti::FormattedList list;

                                list.add("Last Status Change Time") << capBank->getLastStatusChangeTime();
                                list.add("Reclose Delay")           << capBank->getRecloseDelay();
                                list.add("Current Date Time")       << currentDateTime;

                                CTILOG_DEBUG(dout, list);
                            }
                        }
                        else
                        {
                            //double controlValue = (ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                            string text = createTextString(ControlStrategy::IndividualFeederControlMethod, CtiCCCapBank::Close, getIVControl(), getCurrentVarLoadPointValue());
                            request = createDecreaseVarRequest(capBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());

                            if( request == NULL )
                            {
                                if(  ciStringEqual(feederControlUnits,ControlStrategy::KVarControlUnit) )
                                    createCannotControlBankText("Decrease Var", "Close", ccEvents);
                                else
                                    createCannotControlBankText("Increase Volt", "Close", ccEvents);
                            }
                            else
                            {
                                setCorrectionNeededNoBankAvailFlag(false);

                            }
                        }
                    }
                    else if (( ciStringEqual(feederControlUnits,ControlStrategy::KVarControlUnit) &&
                          getIVControl() < leadLevel ) ||
                        ( ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit) &&
                          getIVControl() > leadLevel ) )
                    {
                        //if( _CC_DEBUG )
                        if( ciStringEqual(feederControlUnits,ControlStrategy::KVarControlUnit) )
                        {
                            CTILOG_INFO(dout, "Attempting to Increase Var level in feeder: " << getPaoName().data());
                        }
                        else
                        {
                            setKVARSolution(1);
                            CTILOG_INFO(dout, "Attempting to Decrease Volt level in feeder: " << getPaoName().data());
                        }

                        CtiCCCapBank* capBank = findCapBankToChangeVars(getKVARSolution(), pointChanges, leadLevel, lagLevel, getCurrentVarLoadPointValue(), !(ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit)));

                        //double controlValue = (ciStringEqual(feederControlUnits,ControlStrategy::VoltsControlUnit) ? getCurrentVoltLoadPointValue() : getCurrentVarLoadPointValue());
                        string text = createTextString(ControlStrategy::IndividualFeederControlMethod, CtiCCCapBank::Open, getIVControl(), getCurrentVarLoadPointValue());
                        request = createIncreaseVarRequest(capBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());

                        if( request == NULL )
                        {
                            if(  ciStringEqual(feederControlUnits,ControlStrategy::KVarControlUnit) )
                                    createCannotControlBankText("Increase Var", "Open", ccEvents);
                                else
                                    createCannotControlBankText("Decrease Volt", "Open", ccEvents);
                        }
                        else
                        {
                            setCorrectionNeededNoBankAvailFlag(false);

                        }

                    }
                    else
                    {
                        if (_CC_DEBUG & CC_DEBUG_EXTENDED)
                        {
                            CTILOG_DEBUG(dout, "Max Daily Ops Hit. Control Inhibited on: " << getPaoName());
                        }
                    }

                    if( request != NULL )
                    {
                        pilMessages.push_back(request);
                        setLastOperationTime(currentDateTime);
                        if( getEstimatedVarLoadPointId() > 0 )
                        {
                            pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                        }
                        returnBoolean = true;
                    }
                }
                catch(...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
                }
            }
        }
        else if( (ciStringEqual(feederControlUnits,ControlStrategy::PFactorKWKVarControlUnit) ||
                 ciStringEqual(feederControlUnits,ControlStrategy::PFactorKWKQControlUnit) )
                 && getCurrentVarLoadPointId() > 0 && getCurrentWattLoadPointId() > 0 )
        {
            if( getKVARSolution() < 0 &&
                !(dailyMaxOpsHitFlag && maxOpsDisableFlag) &&
                !(getMaxDailyOpsHitFlag() && maxOpsDisableFlag) )
            {
                //if( _CC_DEBUG )
                {
                    CTILOG_DEBUG(dout, "Attempting to Decrease Var level in feeder: " << getPaoName());
                }

                CtiCCCapBank* capBank = findCapBankToChangeVars(getKVARSolution(), pointChanges, leadLevel, lagLevel, getCurrentVarLoadPointValue(), ciStringEqual(feederControlUnits,ControlStrategy::KVarControlUnit));
                if( capBank != NULL )
                {
                    if( capBank->getRecloseDelay() > 0 &&
                        currentDateTime.seconds() < capBank->getLastStatusChangeTime().seconds() + capBank->getRecloseDelay() )
                    {
                        CTILOG_INFO(dout, "Can Not Close Cap Bank: " << capBank->getPaoName() << " because it has not passed its reclose delay.");
                        if( _CC_DEBUG & CC_DEBUG_EXTENDED )
                        {
                            Cti::FormattedList list;

                            list.add("Last Status Change Time") << capBank->getLastStatusChangeTime();
                            list.add("Reclose Delay")           << capBank->getRecloseDelay();
                            list.add("Current Date Time")       << currentDateTime;

                            CTILOG_DEBUG(dout, list);
                        }
                    }
                    else
                    {
                        double adjustedBankKVARReduction = (lagLevel/100.0)*((double)capBank->getBankSize());
                        if( adjustedBankKVARReduction <= (-1.0*getKVARSolution()) )
                        {
                            string text = createTextString(ControlStrategy::IndividualFeederControlMethod, CtiCCCapBank::Close, getIVControl(), getCurrentVarLoadPointValue());
                            request = createDecreaseVarRequest(capBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
                        }
                        else
                        {//cap bank too big
                            CTILOG_INFO(dout, "Cap Bank: " << capBank->getPaoName() << ", KVAR size too large to switch");
                        }
                    }
                }

                if( capBank == NULL && request == NULL )
                {
                    createCannotControlBankText("Decrease Var", "Close", ccEvents);
                }
                else
                {
                    setCorrectionNeededNoBankAvailFlag(false);

                }
            }
            else if( getKVARSolution() > 0 )
            {
                //if( _CC_DEBUG )
                {
                    CTILOG_DEBUG(dout, "Attempting to Increase Var level in feeder: " << getPaoName());
                }

                CtiCCCapBank* capBank = findCapBankToChangeVars(getKVARSolution(), pointChanges, leadLevel, lagLevel, getCurrentVarLoadPointValue(), ciStringEqual(feederControlUnits,ControlStrategy::KVarControlUnit));
                if( capBank != NULL )
                {
                    double adjustedBankKVARIncrease = -(leadLevel/100.0) * capBank->getBankSize();
                    if( adjustedBankKVARIncrease <= getKVARSolution() )
                    {
                        string text = createTextString(ControlStrategy::IndividualFeederControlMethod, CtiCCCapBank::Open, getIVControl(), getCurrentVarLoadPointValue());
                        request = createIncreaseVarRequest(capBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
                    }
                    else
                    {//cap bank too big
                        CTILOG_INFO(dout, "Cap Bank: " << capBank->getPaoName() << ", KVAR size too large to switch");
                    }
                }

                if( capBank == NULL && request == NULL )
                {
                    createCannotControlBankText("Increase Var", "Open", ccEvents);
                }
                else
                {
                    setCorrectionNeededNoBankAvailFlag(false);

                }
            }
            else
            {
                if (_CC_DEBUG & CC_DEBUG_EXTENDED)
                {
                    CTILOG_DEBUG(dout, "Max Daily Ops Hit. Control Inhibited on: " << getPaoName());
                }

            }
            if( request != NULL )
            {
                pilMessages.push_back(request);
                setLastOperationTime(currentDateTime);
                if( getEstimatedVarLoadPointId() > 0 )
                {
                    pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                }
                returnBoolean = true;
            }
        }
        else
        {
            CTILOG_INFO(dout, "Invalid control units: " << feederControlUnits << ", in feeder: " << getPaoName());
        }
    }
    else
    {
        if ( _IGNORE_NOT_NORMAL_FLAG && !arePointsNormalQuality  )
        {
            Cti::StreamBuffer s;

            s << "Control Inhibited on Feeder: "<<getPaoName()<< " by Abnormal Point Quality";

            if( _CC_DEBUG & CC_DEBUG_EXTENDED )
            {
                Cti::FormattedTable table;

                table.setHorizontalBorders(Cti::FormattedTable::Borders_Outside_Bottom, 0);
                table.setVerticalBorders(Cti::FormattedTable::Borders_Inside);

                table.setCell(0, 0) << "Type";
                table.setCell(0, 1) << "ID";
                table.setCell(0, 2) << "Quality";

                table.setCell(1, 0) << "Var";
                table.setCell(1, 1) << getCurrentVarLoadPointId();
                table.setCell(1, 2) << getCurrentVarPointQuality();

                table.setCell(2, 0) << "Watt";
                table.setCell(2, 1) << getCurrentWattLoadPointId();
                table.setCell(2, 2) << getCurrentWattPointQuality();

                table.setCell(3, 0) << "Volt";
                table.setCell(3, 1) << getCurrentVoltLoadPointId();
                table.setCell(3, 2) << getCurrentVoltPointQuality();

                s << table;
            }

            CTILOG_INFO(dout, s);
        }
    }

    return returnBoolean;
}

bool CtiCCFeeder::capBankControlStatusUpdate(CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, long minConfirmPercent, long failurePercent,
                                             double varValueBeforeControl, double currentVarLoadPointValue, long currentVarPointQuality,
                                             double varAValue, double varBValue, double varCValue, const CtiRegression& reg)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    bool returnBoolean = false;
    bool found = false;
    double change = 0;
    double ratio = 0;
    double varValueBC = varValueBeforeControl;

    string text = "";
    string additional = "Sub: " + getParentName() + " /Feeder: " + getPaoName() + formatMapInfo( this );

    for(long i=0;i<_cccapbanks.size();i++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
        if( currentCapBank->getPaoId() == getLastCapBankControlledDeviceId() )
        {
            returnBoolean = true;
            if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
            {
                removeMaxKvar(currentCapBank->getPaoId());
                if( !_IGNORE_NOT_NORMAL_FLAG || currentVarPointQuality == NormalQuality )
                {
                    if( _RATE_OF_CHANGE && reg.depthMet() )
                    {
                        //This will only be called if we intend to do rate of change and the regression depth is met.
                        varValueBC = reg.regression( CtiTime().seconds() );

                        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                        {
                            CTILOG_DEBUG(dout, "Rate of Change Value: " << varValueBC);
                        }

                        // is estimated within Percent of currentVar?
                        change = currentVarLoadPointValue - varValueBC;
                    }
                    else
                    {
                        change = currentVarLoadPointValue - varValueBC;
                    }
                    if( _RATE_OF_CHANGE && !reg.depthMet() )
                    {
                        CTILOG_INFO(dout, "Rate of Change Depth not met: " << reg.getCurDepth() << " / " << reg.getRegDepth());
                    }

                    if( change < 0 )
                    {
                        CTILOG_INFO(dout, ""<< currentCapBank->getPaoName() <<": Var change in wrong direction?");
                    }

                    ratio = change/currentCapBank->getBankSize();
                    if( ratio < minConfirmPercent*.01 )
                    {
                        if( ratio < failurePercent*.01 && failurePercent != 0 && minConfirmPercent != 0 )
                        {
                            //currentCapBank->setControlStatus(CtiCCCapBank::OpenFail);
                            store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenFail, currentCapBank);
                            if (currentCapBank->getControlStatusQuality() != CC_CommFail)
                            {
                                currentCapBank->setControlStatusQuality(CC_Fail);
                                CTILOG_INFO(dout, "CapBank Control Status: OpenFail: "<<currentCapBank->getPaoName());
                            }
                        }
                        else if( minConfirmPercent != 0 )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                            currentCapBank->setControlStatusQuality(CC_Significant);
                            CTILOG_INFO(dout, "CapBank Control Status: OpenQuestionable: "<<currentCapBank->getPaoName());
                        }
                        else
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::Open);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            CTILOG_INFO(dout, "CapBank Control Status: Open: "<<currentCapBank->getPaoName());
                        }
                    }
                    else
                    {
                        currentCapBank->setControlStatus(CtiCCCapBank::Open);
                        currentCapBank->setControlStatusQuality(CC_Normal);
                        CTILOG_INFO(dout, "CapBank Control Status: Open: "<<currentCapBank->getPaoName());
                    }
                    text = createControlStatusUpdateText(currentCapBank->getControlStatusText(), currentVarLoadPointValue,ratio);

                    currentCapBank->setBeforeVarsString(createVarText(varValueBC, 1.0));
                    currentCapBank->setAfterVarsString(createVarText(currentVarLoadPointValue, 1.0));
                    currentCapBank->setPercentChangeString(createVarText(ratio, 100.0));

                }
                else
                {
                    char tempchar[80];
                    currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                    text = "Var: ";
                    text += CtiNumStr(currentVarLoadPointValue, getDecimalPlaces()).toString();
                    text += "- Non Normal Var Quality = ";
                    _ltoa(currentVarPointQuality,tempchar,10);
                    text += tempchar;
                    text += ", OpenQuestionable";

                    currentCapBank->setBeforeVarsString(createVarText(varValueBC, 1.0));
                    currentCapBank->setAfterVarsString(createVarText(currentVarLoadPointValue, 1.0));
                    currentCapBank->setPercentChangeString(createVarText(ratio, 100.0));
                    currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                }
            }
            else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
            {
                removeMaxKvar(currentCapBank->getPaoId());
                if( !_IGNORE_NOT_NORMAL_FLAG || currentVarPointQuality == NormalQuality )
                {
                    if( _RATE_OF_CHANGE && reg.depthMet() )
                    {
                        //This will only be called if we intend to do rate of change and the regression depth is met.
                        varValueBC = reg.regression( CtiTime().seconds() );

                        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                        {
                            CTILOG_DEBUG(dout, "Rate of Change Value: " << varValueBC);
                        }
                        // is estimated within Percent of currentVar?
                        change = varValueBC - currentVarLoadPointValue;
                    }
                    else
                    {
                        change = varValueBC - currentVarLoadPointValue;
                    }
                    if( _RATE_OF_CHANGE && !reg.depthMet() )
                    {
                        CTILOG_INFO(dout, "Rate of Change Depth not met: " << reg.getCurDepth() << " / " << reg.getRegDepth());
                    }

                    if( change < 0 )
                    {
                        CTILOG_INFO(dout, ""<< currentCapBank->getPaoName() <<":Var change in wrong direction?");
                    }

                    ratio = change/currentCapBank->getBankSize();
                    if( ratio < minConfirmPercent*.01 )
                    {
                        if( ratio < failurePercent*.01 && failurePercent != 0 && minConfirmPercent != 0 )
                        {
                            //currentCapBank->setControlStatus(CtiCCCapBank::CloseFail);
                            store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::CloseFail, currentCapBank);
                            if (currentCapBank->getControlStatusQuality() != CC_CommFail)
                            {
                                currentCapBank->setControlStatusQuality(CC_Fail);
                            }
                            CTILOG_INFO(dout, "CapBank Control Status: CloseFail: "<<currentCapBank->getPaoName());
                        }
                        else if( minConfirmPercent != 0 )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                            currentCapBank->setControlStatusQuality(CC_Significant);
                            CTILOG_INFO(dout, "CapBank Control Status: CloseQuestionable: "<<currentCapBank->getPaoName());
                        }
                        else
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::Close);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                            CTILOG_INFO(dout, "CapBank Control Status: Close: "<<currentCapBank->getPaoName());
                        }
                    }
                    else
                    {
                        currentCapBank->setControlStatus(CtiCCCapBank::Close);
                        currentCapBank->setControlStatusQuality(CC_Normal);
                        CTILOG_INFO(dout, "CapBank Control Status: Close: "<<currentCapBank->getPaoName());
                    }

                    text = createControlStatusUpdateText(currentCapBank->getControlStatusText(), currentVarLoadPointValue, ratio);

                    currentCapBank->setBeforeVarsString(createVarText(varValueBC, 1.0));
                    currentCapBank->setAfterVarsString(createVarText(currentVarLoadPointValue, 1.0));
                    currentCapBank->setPercentChangeString(createVarText(ratio, 100.0));
                }
                else
                {
                    char tempchar[80];
                    currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                    text = "Var: ";
                    text += CtiNumStr(currentVarLoadPointValue, getDecimalPlaces()).toString();
                    text += "- Non Normal Var Quality = ";
                    _ltoa(currentVarPointQuality,tempchar,10);
                    text += ", CloseQuestionable";

                    currentCapBank->setBeforeVarsString(createVarText(varValueBC, 1.0));
                    currentCapBank->setAfterVarsString(createVarText(currentVarLoadPointValue, 1.0));
                    currentCapBank->setPercentChangeString(createVarText(ratio, 100.0));
                    currentCapBank->setControlStatusQuality(CC_AbnormalQuality);

                }

            }
            else
            {
                returnBoolean = false;
                CTILOG_INFO(dout, "Last Cap Bank ("<< currentCapBank->getPaoName() <<") controlled not in pending status");
                text += "Var: ";
                text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                text += " - control was not pending, " ;
                text += currentCapBank->getControlStatusText();
                currentCapBank->setControlStatusQuality(CC_Fail);
            }

            if ( (currentCapBank->getRetryOpenFailedFlag() || currentCapBank->getRetryCloseFailedFlag() )  &&
                (currentCapBank->getControlStatus() != CtiCCCapBank::CloseFail && currentCapBank->getControlStatus() != CtiCCCapBank::OpenFail))
            {
                currentCapBank->setRetryOpenFailedFlag(false);
                currentCapBank->setRetryCloseFailedFlag(false);
            }

            if( currentCapBank->getStatusPointId() > 0 )
            {
                CtiCCSubstationBusPtr sub = store->findSubBusByPAObjectID(getParentId());
                if( sub != NULL )
                {
                   sub->createStatusUpdateMessages(pointChanges, ccEvents, currentCapBank, this, text, additional, false,
                                                                  varValueBC, currentVarLoadPointValue, change,  varAValue, varBValue, varCValue);
                }
            }
            else
            {
                CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                << " DeviceID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );
            }

            found = true;
            currentCapBank->setControlRecentlySentFlag(false);
            currentCapBank->setIgnoreFlag(false);
            break;
        }
    }

    if (found == false)
    {
        CTILOG_WARN(dout, "Last Cap Bank controlled NOT FOUND");
        returnBoolean = false;
    }

    setRetryIndex(0);
    setRecentlyControlledFlag(false);

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    capBankControlStatusUpdate

    Returns a boolean if the current day of the week can be a peak day
---------------------------------------------------------------------------*/
bool CtiCCFeeder::capBankControlPerPhaseStatusUpdate(CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, long minConfirmPercent,
                                                     long failurePercent, long currentVarPointQuality, double varAValueBeforeControl,
                                                     double varBValueBeforeControl, double varCValueBeforeControl,
                                                     double varAValue, double varBValue, double varCValue,
                                                     const CtiRegression& regA, const CtiRegression& regB, const CtiRegression& regC)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    bool returnBoolean = false;
    bool found = false;
    double change = 0;
    double changeA = 0;
    double changeB = 0;
    double changeC = 0;
    double ratioA = 0;
    double ratioB = 0;
    double ratioC = 0;
    double varValueAbc = varAValueBeforeControl;
    double varValueBbc = varBValueBeforeControl;
    double varValueCbc = varCValueBeforeControl;

    string text = "";
    string additional = "Sub: " + getParentName() + " /Feeder: " + getPaoName() + formatMapInfo( this );

    for(long i=0;i<_cccapbanks.size();i++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
        if( currentCapBank->getPaoId() == getLastCapBankControlledDeviceId() )
        {
            returnBoolean = true;
            if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
            {
                if( !_IGNORE_NOT_NORMAL_FLAG || currentVarPointQuality == NormalQuality )
                {
                    if( _RATE_OF_CHANGE && getRegressionA().depthMet() && getRegressionB().depthMet() && getRegressionC().depthMet() )
                    {
                        CtiTime timeNow;
                        //This will only be called if we intend to do rate of change and the regression depth is met.
                        varValueAbc = regA.regression( timeNow.seconds() );
                        varValueBbc = regB.regression( timeNow.seconds() );
                        varValueCbc = regC.regression( timeNow.seconds() );

                        int size =  currentCapBank->getBankSize()/3;
                        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                        {
                            Cti::FormattedList list;

                            list << "Rate of Change";
                            list.add("Phase A") << varValueAbc;
                            list.add("Phase B") << varValueBbc;
                            list.add("Phase C") << varValueCbc;

                            CTILOG_DEBUG(dout, list);
                        }
                    }
                    changeA = varAValue - varValueAbc;
                    changeB = varBValue - varValueBbc;
                    changeC = varCValue - varValueCbc;

                    if( _RATE_OF_CHANGE && (!regA.depthMet() || !regB.depthMet() || !regC.depthMet()) )
                    {
                        Cti::FormattedList list;

                        list << "Rate of Change Depth not met.";
                        list.add("Phase A") << regA.getCurDepth() << " / " << regA.getRegDepth();
                        list.add("Phase B") << regB.getCurDepth() << " / " << regB.getRegDepth();
                        list.add("Phase C") << regC.getCurDepth() << " / " << regC.getRegDepth();

                        CTILOG_INFO(dout, list);
                    }

                    if( changeA < 0 ||  changeB < 0 ||  changeC < 0)
                    {
                        Cti::FormattedList list;

                        list << currentCapBank->getPaoName() <<":Var change in wrong direction?";
                        list.add("changeA") << changeA;
                        list.add("changeB") << changeB;
                        list.add("changeC") << changeC;

                        CTILOG_WARN(dout, list);
                    }
                    ratioA = changeA/(currentCapBank->getBankSize() /3);
                    ratioB = changeB/(currentCapBank->getBankSize() /3);
                    ratioC = changeC/(currentCapBank->getBankSize() /3);
                    if( !areAllPhasesSuccess(ratioA, ratioB, ratioC, minConfirmPercent) )
                    {
                        if( shouldCapBankBeFailed(ratioA, ratioB, ratioC, failurePercent) &&
                            failurePercent != 0 && minConfirmPercent != 0 )
                        {
                            //currentCapBank->setControlStatus(CtiCCCapBank::OpenFail);
                            store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenFail, currentCapBank);
                            if (currentCapBank->getControlStatusQuality() != CC_CommFail)
                                currentCapBank->setControlStatusQuality(CC_Fail);
                        }
                        else if( minConfirmPercent != 0 )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                            if (areAllPhasesQuestionable(ratioA, ratioB, ratioC, minConfirmPercent, failurePercent))
                            {
                                currentCapBank->setControlStatusQuality(CC_Significant);
                            }
                            else
                            {

                                currentCapBank->setControlStatusQuality(CC_Partial);
                            }
                        }
                        else
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::Open);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                        }
                    }
                    else
                    {
                        currentCapBank->setControlStatus(CtiCCCapBank::Open);
                        currentCapBank->setControlStatusQuality(CC_Normal);
                    }

                    currentCapBank->setPartialPhaseInfo(getPhaseIndicatorString(currentCapBank->getControlStatus(), ratioA, ratioB, ratioC, minConfirmPercent, failurePercent));
                    text = createPhaseControlStatusUpdateText(currentCapBank->getControlStatusText(), varAValue,
                                                          varBValue, varCValue, ratioA, ratioB, ratioC);

                    currentCapBank->setBeforeVarsString(createPhaseVarText(varValueAbc, varValueBbc, varValueCbc,1.0));
                    currentCapBank->setAfterVarsString(createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                    currentCapBank->setPercentChangeString(createPhaseRatioText(ratioA, ratioB, ratioC,100.0));
                }
                else
                {
                    char tempchar[80];
                    currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                    text = "Var: ";
                    text += CtiNumStr(varAValue, getDecimalPlaces()).toString();
                    text += ":";
                    text += CtiNumStr(varBValue, getDecimalPlaces()).toString();
                    text += ":";
                    text += CtiNumStr(varCValue, getDecimalPlaces()).toString();

                    text += "- Non Normal Var Quality = ";
                    _ltoa(currentVarPointQuality,tempchar,10);
                    text += tempchar;
                    text += ", OpenQuestionable";
                    currentCapBank->setControlStatusQuality(CC_AbnormalQuality);
                }
            }
            else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
            {
                if( !_IGNORE_NOT_NORMAL_FLAG || currentVarPointQuality == NormalQuality )
                {
                    if( _RATE_OF_CHANGE && getRegressionA().depthMet() && getRegressionB().depthMet() && getRegressionC().depthMet() )
                    {
                        CtiTime timeNow;
                        //This will only be called if we intend to do rate of change and the regression depth is met.
                        varValueAbc = regA.regression( timeNow.seconds() );
                        varValueBbc = regB.regression( timeNow.seconds() );
                        varValueCbc = regC.regression( timeNow.seconds() );

                        int size =  currentCapBank->getBankSize()/3;

                        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                        {
                            Cti::FormattedList list;

                            list << "Rate of Change";
                            list.add("Phase A") << varValueAbc;
                            list.add("Phase B") << varValueBbc;
                            list.add("Phase C") << varValueCbc;

                            CTILOG_DEBUG(dout, list);
                        }
                    }
                    changeA = (varValueAbc) - varAValue;
                    changeB = (varValueBbc) - varBValue;
                    changeC = (varValueCbc) - varCValue;

                    if( _RATE_OF_CHANGE && (!regA.depthMet() || !regB.depthMet() || !regC.depthMet()) )
                    {
                        Cti::FormattedList list;

                        list << "Rate of Change Depth not met.";
                        list.add("Phase A") << regA.getCurDepth() << " / " << regA.getRegDepth();
                        list.add("Phase B") << regB.getCurDepth() << " / " << regB.getRegDepth();
                        list.add("Phase C") << regC.getCurDepth() << " / " << regC.getRegDepth();
                    }

                    if( changeA < 0 ||  changeB < 0 ||  changeC < 0)
                    {
                        CTILOG_WARN(dout, currentCapBank->getPaoName() <<":Var change in wrong direction?");
                    }
                    ratioA = changeA/(currentCapBank->getBankSize() /3);
                    ratioB = changeB/(currentCapBank->getBankSize() /3);
                    ratioC = changeC/(currentCapBank->getBankSize() /3);

                    if(  !areAllPhasesSuccess(ratioA, ratioB, ratioC, minConfirmPercent)   )
                    {
                        if(  shouldCapBankBeFailed(ratioA, ratioB, ratioC, failurePercent) &&
                             failurePercent != 0 && minConfirmPercent != 0 )
                        {
                            store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::CloseFail,
                                                                       currentCapBank);
                            if (currentCapBank->getControlStatusQuality() != CC_CommFail)
                                currentCapBank->setControlStatusQuality(CC_Fail);
                        }
                        else if( minConfirmPercent != 0 )
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                            if (areAllPhasesQuestionable(ratioA, ratioB, ratioC, minConfirmPercent, failurePercent))
                            {
                                currentCapBank->setControlStatusQuality(CC_Significant);
                            }
                            else
                            {
                                currentCapBank->setControlStatusQuality(CC_Partial);
                            }
                        }
                        else
                        {
                            currentCapBank->setControlStatus(CtiCCCapBank::Close);
                            currentCapBank->setControlStatusQuality(CC_Normal);
                        }
                    }
                    else
                    {
                        currentCapBank->setControlStatus(CtiCCCapBank::Close);
                        currentCapBank->setControlStatusQuality(CC_Normal);
                    }
                    currentCapBank->setPartialPhaseInfo(getPhaseIndicatorString(currentCapBank->getControlStatus(), ratioA, ratioB, ratioC, minConfirmPercent, failurePercent));
                    text = createPhaseControlStatusUpdateText(currentCapBank->getControlStatusText(), varAValue,
                                                          varBValue, varCValue, ratioA, ratioB, ratioC);

                    currentCapBank->setBeforeVarsString(createPhaseVarText(varValueAbc, varValueBbc, varValueCbc,1.0) );
                    currentCapBank->setAfterVarsString(createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                    currentCapBank->setPercentChangeString(createPhaseRatioText(ratioA, ratioB, ratioC,100.0));
                }
                else
                {
                    char tempchar[80];
                    currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                    text = "Var: ";
                    text += CtiNumStr(varAValue, getDecimalPlaces()).toString();
                    text += ":";
                    text += CtiNumStr(varBValue, getDecimalPlaces()).toString();
                    text += ":";
                    text += CtiNumStr(varCValue, getDecimalPlaces()).toString();
                    text += "- Non Normal Var Quality = ";
                    _ltoa(currentVarPointQuality,tempchar,10);
                    text += tempchar;
                    text += ", CloseQuestionable";
                    currentCapBank->setControlStatusQuality(CC_AbnormalQuality);
                }

            }
            else
            {
                returnBoolean = false;
                CTILOG_INFO(dout, "Last Cap Bank ("<< currentCapBank->getPaoName() <<") controlled not in pending status");
                text += "Var: ";
                text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                text += " - control was not pending, " ;
                text += currentCapBank->getControlStatusText();

            }

            if ( (currentCapBank->getRetryOpenFailedFlag() || currentCapBank->getRetryCloseFailedFlag() )  &&
                (currentCapBank->getControlStatus() != CtiCCCapBank::CloseFail && currentCapBank->getControlStatus() != CtiCCCapBank::OpenFail))
            {
                currentCapBank->setRetryOpenFailedFlag(false);
                currentCapBank->setRetryCloseFailedFlag(false);
            }


            if( currentCapBank->getStatusPointId() > 0 )
            {
                CtiCCSubstationBusPtr sub = store->findSubBusByPAObjectID(getParentId());
                if( sub != NULL )
                {
                   sub->createStatusUpdateMessages(pointChanges, ccEvents, currentCapBank, this, text, additional, false,
                                                   varValueAbc+varValueBbc+varValueCbc, varAValue+varBValue+varCValue, changeA+changeB+changeC,
                                                   varAValue, varBValue, varCValue);
                }

            }
            else
            {
                CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                << " DeviceID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );
            }
            found = true;

            currentCapBank->setIgnoreFlag(false);
            currentCapBank->setControlRecentlySentFlag(false);
            break;
        }
    }
    if (found == false)
    {
        CTILOG_WARN(dout, "Last Cap Bank controlled NOT FOUND");
    }

    setRecentlyControlledFlag(false);
    setRetryIndex(0);

    return returnBoolean;
}

bool CtiCCFeeder::capBankVerificationStatusUpdate(CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, long minConfirmPercent, long failurePercent,
                                                  double varAValue, double varBValue, double varCValue)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    bool returnBoolean = false;
    bool foundCap = false;
    double change = 0;
    double ratio = 0;
    string text = "";
    string additional = "";
    bool assumedWrongFlag = false;
    bool vResult = false; //fail

    CtiCCCapBank* currentCapBank = NULL;

    if (getUsePhaseData() && !getTotalizedControlFlag())
    {
        returnBoolean = capBankVerificationPerPhaseStatusUpdate(pointChanges, ccEvents, minConfirmPercent, failurePercent);
    }
    else
    {
        for(int j=0;j<_cccapbanks.size();j++)
        {
           currentCapBank = (CtiCCCapBank*)_cccapbanks[j];
           if (currentCapBank->getPaoId() == getCurrentVerificationCapBankId())
           {

               if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
               {
                   if( !_IGNORE_NOT_NORMAL_FLAG || getCurrentVarPointQuality() == NormalQuality )
                   {
                        if( _RATE_OF_CHANGE && getRegression().depthMet() )
                        {
                            //This will only be called if we intend to do rate of change and the regression depth is met.
                            double varValueReg = getRegression().regression( CtiTime().seconds() );

                            if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                            {
                                CTILOG_DEBUG(dout, "Rate of Change Value: " << varValueReg);
                            }
                            // is estimated within Percent of currentVar?
                            change = getCurrentVarLoadPointValue() - varValueReg;
                        }
                        else
                        {
                           change = getCurrentVarLoadPointValue() - getVarValueBeforeControl();
                        }
                        if( _RATE_OF_CHANGE && !getRegression().depthMet() )
                        {
                            CTILOG_INFO(dout, "Rate of Change Depth not met: " << getRegression().getCurDepth() << " / " << getRegression().getRegDepth());
                        }
                        if( change < 0 )
                        {
                            CTILOG_INFO(dout, ""<< currentCapBank->getPaoName() <<":Var change in wrong direction?");
                            if (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG &&
                               currentCapBank->getVCtrlIndex() == 1)
                            {
                               currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Open);
                               setCurrentVerificationCapBankState(CtiCCCapBank::Open);
                               currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);

                               assumedWrongFlag = true;
                               change = 0 - change;
                            }
                        }

                        ratio = change/currentCapBank->getBankSize();
                        if( ratio < minConfirmPercent*.01 )
                        {
                           if( ratio < failurePercent*.01 && failurePercent != 0 && minConfirmPercent != 0 )
                           {
                               if (!assumedWrongFlag)
                                   store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenFail, currentCapBank);
                               else
                                   store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::CloseFail, currentCapBank);

                               additional = string("Feeder: ");
                               additional += getPaoName();
                               if (currentCapBank->getControlStatusQuality() != CC_CommFail)
                                   currentCapBank->setControlStatusQuality(CC_Fail);

                           }
                           else if( minConfirmPercent != 0 )
                           {
                               if (!assumedWrongFlag)
                                   currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                               else
                                   currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                               additional = string("Feeder: ");
                               additional += getPaoName();
                               currentCapBank->setControlStatusQuality(CC_Significant);
                           }
                           else
                           {
                               if (!assumedWrongFlag)
                                   currentCapBank->setControlStatus(CtiCCCapBank::Open);
                               else
                                   currentCapBank->setControlStatus(CtiCCCapBank::Close);

                               additional = string("Feeder: ");
                               additional += getPaoName();
                               currentCapBank->setControlStatusQuality(CC_Normal);
                               vResult = true;
                           }
                        }
                        else
                        {
                           if (!assumedWrongFlag)
                               currentCapBank->setControlStatus(CtiCCCapBank::Open);
                           else
                               currentCapBank->setControlStatus(CtiCCCapBank::Close);

                           additional = string("Feeder: ");
                           additional += getPaoName();
                           currentCapBank->setControlStatusQuality(CC_Normal);
                           vResult = true;
                        }
                        text = createControlStatusUpdateText(currentCapBank->getControlStatusText(), getCurrentVarLoadPointValue(),ratio);

                        currentCapBank->setBeforeVarsString(createVarText(getVarValueBeforeControl(), 1.0));
                        currentCapBank->setAfterVarsString(createVarText(getCurrentVarLoadPointValue(), 1.0));
                        currentCapBank->setPercentChangeString(createVarText(ratio, 100.0));

                   }
                   else
                   {
                       char tempchar[80];
                       currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                       text = "Var: ";
                       text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                       text += "- Non Normal Var Quality = ";
                       _ltoa(getCurrentVarPointQuality(),tempchar,10);
                       text += tempchar;
                       text += ", OpenQuestionable";
                       additional = string("Feeder: ");
                       additional += getPaoName();

                       currentCapBank->setBeforeVarsString(createVarText(getVarValueBeforeControl(), 1.0));
                       currentCapBank->setAfterVarsString(createVarText(getCurrentVarLoadPointValue(), 1.0));
                       currentCapBank->setPercentChangeString(createVarText(ratio, 100.0));
                       currentCapBank->setControlStatusQuality(CC_AbnormalQuality);
                   }
               }
               else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
               {
                   if( !_IGNORE_NOT_NORMAL_FLAG || getCurrentVarPointQuality() == NormalQuality )
                   {
                        if( _RATE_OF_CHANGE && getRegression().depthMet() )
                        {
                            //This will only be called if we intend to do rate of change and the regression depth is met.
                            double varValueReg = getRegression().regression( CtiTime().seconds() );
                            CTILOG_INFO(dout, "Rate of Change Value: " << varValueReg);
                            // is estimated within Percent of currentVar?
                            change = varValueReg - getCurrentVarLoadPointValue();
                        }
                        else
                        {
                            change = getVarValueBeforeControl() - getCurrentVarLoadPointValue();
                        }
                        if( _RATE_OF_CHANGE && !getRegression().depthMet() )
                        {
                            CTILOG_INFO(dout, "Rate of Change Depth not met: " << getRegression().getCurDepth() << " / " << getRegression().getRegDepth());
                        }
                        if( change < 0 )
                        {
                            CTILOG_INFO(dout, ""<< currentCapBank->getPaoName() <<":Var change in wrong direction?");
                            if (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG &&
                               currentCapBank->getVCtrlIndex() == 1)
                            {
                               currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Close);
                               setCurrentVerificationCapBankState(CtiCCCapBank::Close);
                               currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                               assumedWrongFlag = true;
                               change = 0 - change;
                            }
                        }
                        ratio = change/currentCapBank->getBankSize();
                        if( ratio < minConfirmPercent*.01 )
                        {
                           if( ratio < failurePercent*.01 && failurePercent != 0 && minConfirmPercent != 0 )
                           {
                               if (!assumedWrongFlag)
                                   store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::CloseFail, currentCapBank);
                               else
                                   store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenFail, currentCapBank);

                               additional = string("Feeder: ");
                               additional += getPaoName();
                               if (currentCapBank->getControlStatusQuality() != CC_CommFail)
                                   currentCapBank->setControlStatusQuality(CC_Fail);
                           }
                           else if( minConfirmPercent != 0 )
                           {
                               if (!assumedWrongFlag)
                                   currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                               else
                                   currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);

                               additional = string("Feeder: ");
                               additional += getPaoName();
                               currentCapBank->setControlStatusQuality(CC_Significant);
                           }
                           else
                           {
                               if (!assumedWrongFlag)
                                   currentCapBank->setControlStatus(CtiCCCapBank::Close);
                               else
                                   currentCapBank->setControlStatus(CtiCCCapBank::Open);

                               additional = string("Feeder: ");
                               additional += getPaoName();
                               currentCapBank->setControlStatusQuality(CC_Normal);
                               vResult = true;
                           }
                        }
                        else
                        {
                           if (!assumedWrongFlag)
                               currentCapBank->setControlStatus(CtiCCCapBank::Close);
                           else
                               currentCapBank->setControlStatus(CtiCCCapBank::Open);
                           additional = string("Feeder: ");
                           additional += getPaoName();
                           currentCapBank->setControlStatusQuality(CC_Normal);
                           vResult = true;
                        }
                        text = createControlStatusUpdateText(currentCapBank->getControlStatusText(), getCurrentVarLoadPointValue(),ratio);

                        currentCapBank->setBeforeVarsString(createVarText(getVarValueBeforeControl(), 1.0));
                        currentCapBank->setAfterVarsString(createVarText(getCurrentVarLoadPointValue(), 1.0));
                        currentCapBank->setPercentChangeString(createVarText(ratio, 100.0));
                   }
                   else
                   {
                       char tempchar[80];
                       currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                       text = "Var: ";
                       text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                       text += "- Non Normal Var Quality = ";
                       _ltoa(getCurrentVarPointQuality(),tempchar,10);
                       text += tempchar;
                       text += ", CloseQuestionable";
                       additional = string("Feeder: ");
                       additional += getPaoName();


                       currentCapBank->setBeforeVarsString(createVarText(getVarValueBeforeControl(), 1.0));
                       currentCapBank->setAfterVarsString(createVarText(getCurrentVarLoadPointValue(), 1.0));
                       currentCapBank->setPercentChangeString(createVarText(ratio, 100.0));

                       currentCapBank->setControlStatusQuality(CC_AbnormalQuality);
                   }
               }
               else
               {
                   CTILOG_INFO(dout, "Last Cap Bank ("<< currentCapBank->getPaoName() <<") controlled not in pending status");
                   if( currentCapBank->getPerformingVerificationFlag() )
                   {
                       text += "Var: ";
                       text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                       text += " - control was not pending, " ;
                       text += currentCapBank->getControlStatusText();
                   }

               }
               if ( (currentCapBank->getRetryOpenFailedFlag() || currentCapBank->getRetryCloseFailedFlag() )  &&
                    (currentCapBank->getControlStatus() != CtiCCCapBank::CloseFail && currentCapBank->getControlStatus() != CtiCCCapBank::OpenFail))
                {
                    currentCapBank->setRetryOpenFailedFlag(false);
                    currentCapBank->setRetryCloseFailedFlag(false);
                }


               if( currentCapBank->getStatusPointId() > 0 )
               {
                   if (currentCapBank->getPorterRetFailFlag() && currentCapBank->getIgnoreFlag())
                   {
                         currentCapBank->setVCtrlIndex(5);
                         currentCapBank->setVerificationDoneFlag(true);
                         currentCapBank->setIgnoreFlag(false);
                   }
                   else
                   {
                       CtiCCSubstationBusPtr sub = store->findSubBusByPAObjectID(getParentId());
                       if( sub != NULL )
                       {
                          sub->createStatusUpdateMessages(pointChanges, ccEvents, currentCapBank, this, text, additional, true,
                                                          getVarValueBeforeControl(), getCurrentVarLoadPointValue(), change,
                                                          varAValue, varBValue, varCValue);
                       }

                   }
                   currentCapBank->setPorterRetFailFlag(false);
               }
               else
               {
                   CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                   << " DeviceID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );
               }

               if (currentCapBank->updateVerificationState())
               {
                   returnBoolean = true;
                   currentCapBank->setPerformingVerificationFlag(false);
                   return returnBoolean;
               }

               currentCapBank->setIgnoreFlag(false);
               currentCapBank->setControlRecentlySentFlag(false);
               foundCap = true;
               break;
           }
        }

        if (foundCap == false)
        {
            CTILOG_WARN(dout, "Last Verification Cap Bank controlled NOT FOUND");
            returnBoolean = true;
        }
    }

    setRetryIndex(0);
    return returnBoolean;
}

bool CtiCCFeeder::capBankVerificationPerPhaseStatusUpdate(CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, long minConfirmPercent, long failPercent)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    bool returnBoolean = false;
    bool foundCap = false;
    double change = 0;
    double ratio = 0;
    string text = "";
    string additional = "";
    double changeA = 0;
    double changeB = 0;
    double changeC = 0;
    double ratioA = 0;
    double ratioB = 0;
    double ratioC = 0;
    double varAValue = getPhaseAValue();
    double varBValue = getPhaseBValue();
    double varCValue = getPhaseCValue();
    double varValueAbc = getPhaseAValueBeforeControl();
    double varValueBbc = getPhaseBValueBeforeControl();
    double varValueCbc = getPhaseCValueBeforeControl();

    bool assumedWrongFlag = false;


    bool vResult = false; //fail

    CtiCCCapBank* currentCapBank = NULL;

    for(int j=0;j<_cccapbanks.size();j++)
    {
       currentCapBank = (CtiCCCapBank*)_cccapbanks[j];
       if (currentCapBank->getPaoId() == getCurrentVerificationCapBankId())
       {

           if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
           {
               if( !_IGNORE_NOT_NORMAL_FLAG || getCurrentVarPointQuality() == NormalQuality )
               {
                    if( _RATE_OF_CHANGE && getRegressionA().depthMet() &&
                        getRegressionB().depthMet() && getRegressionC().depthMet() )
                    {

                        CtiTime timeNow;
                        varValueAbc = getRegressionA().regression( timeNow.seconds() );
                        varValueBbc = getRegressionB().regression( timeNow.seconds() );
                        varValueCbc = getRegressionC().regression( timeNow.seconds() );
                        int size =  currentCapBank->getBankSize()/3;
                        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                        {
                            Cti::FormattedList list;

                            list << "Rate of Change";
                            list.add("Phase A") << varValueAbc;
                            list.add("Phase B") << varValueBbc;
                            list.add("Phase C") << varValueCbc;

                            CTILOG_DEBUG(dout, list);
                        }
                    }
                    changeA = varAValue - varValueAbc;
                    changeB = varBValue - varValueBbc;
                    changeC = varCValue - varValueCbc;


                    if( changeA < 0 ||  changeB < 0 ||  changeC < 0)
                    {
                        CTILOG_INFO(dout, ""<< currentCapBank->getPaoName() <<":Var change in wrong direction?");
                        if (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG &&
                           currentCapBank->getVCtrlIndex() == 1)
                        {
                           currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Open);
                           setCurrentVerificationCapBankState(CtiCCCapBank::Open);
                           currentCapBank->setControlStatus(CtiCCCapBank::ClosePending);

                           assumedWrongFlag = true;
                           changeA = 0 - changeA;
                           changeB = 0 - changeB;
                           changeC = 0 - changeC;
                        }
                    }

                    ratioA = changeA/(currentCapBank->getBankSize() /3);
                    ratioB = changeB/(currentCapBank->getBankSize() /3);
                    ratioC = changeC/(currentCapBank->getBankSize() /3);
                    if( !areAllPhasesSuccess(ratioA, ratioB, ratioC, minConfirmPercent)  )
                    {
                        if( shouldCapBankBeFailed(ratioA, ratioB, ratioC, failPercent) &&
                             failPercent != 0 && minConfirmPercent != 0 )
                        {
                           if (!assumedWrongFlag)
                               store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenFail, currentCapBank);
                           else
                               store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::CloseFail, currentCapBank);

                           additional = string("Feeder: ");
                           additional += getPaoName();
                           if (currentCapBank->getControlStatusQuality() != CC_CommFail)
                               currentCapBank->setControlStatusQuality(CC_Fail);

                       }
                       else if( minConfirmPercent != 0 )
                       {
                           if (!assumedWrongFlag)
                               currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                           else
                               currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                           additional = string("Feeder: ");
                           additional += getPaoName();
                           if (areAllPhasesQuestionable(ratioA, ratioB, ratioC, minConfirmPercent, failPercent))
                           {
                               currentCapBank->setControlStatusQuality(CC_Significant);
                           }
                           else
                           {
                               currentCapBank->setControlStatusQuality(CC_Partial);
                           }
                       }
                       else
                       {
                           if (!assumedWrongFlag)
                               currentCapBank->setControlStatus(CtiCCCapBank::Open);
                           else
                               currentCapBank->setControlStatus(CtiCCCapBank::Close);

                           additional = string("Feeder: ");
                           additional += getPaoName();
                           currentCapBank->setControlStatusQuality(CC_Normal);
                           vResult = true;
                       }
                    }
                    else
                    {
                       if (!assumedWrongFlag)
                           currentCapBank->setControlStatus(CtiCCCapBank::Open);
                       else
                           currentCapBank->setControlStatus(CtiCCCapBank::Close);

                       additional = string("Feeder: ");
                       additional += getPaoName();
                       currentCapBank->setControlStatusQuality(CC_Normal);
                       vResult = true;
                    }
                    currentCapBank->setPartialPhaseInfo(getPhaseIndicatorString(currentCapBank->getControlStatus(), ratioA, ratioB, ratioC, minConfirmPercent, failPercent));
                    text = createPhaseControlStatusUpdateText(currentCapBank->getControlStatusText(), varAValue,
                                                          varBValue, varCValue, ratioA, ratioB, ratioC);

                    currentCapBank->setBeforeVarsString(createPhaseVarText(varValueAbc,varValueBbc,varValueCbc,1.0));
                    currentCapBank->setAfterVarsString(createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                    currentCapBank->setPercentChangeString(createPhaseRatioText(ratioA, ratioB, ratioC,100.0));


               }
               else
               {
                   char tempchar[80];
                   currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);
                   text = "Var: ";
                   text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                   text += "- Non Normal Var Quality = ";
                   _ltoa(getCurrentVarPointQuality(),tempchar,10);
                   text += tempchar;
                   text += ", OpenQuestionable";
                   additional = string("Feeder: ");
                   additional += getPaoName();

                   currentCapBank->setBeforeVarsString(createPhaseVarText(varValueAbc,varValueBbc,varValueCbc,1.0));
                   currentCapBank->setAfterVarsString(createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                   currentCapBank->setPercentChangeString(createPhaseRatioText(ratioA, ratioB, ratioC,100.0));
                   currentCapBank->setControlStatusQuality(CC_AbnormalQuality);
               }
           }
           else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
           {
               if( !_IGNORE_NOT_NORMAL_FLAG || getCurrentVarPointQuality() == NormalQuality )
               {
                    if( _RATE_OF_CHANGE && getRegressionA().depthMet() &&
                        getRegressionB().depthMet() && getRegressionC().depthMet() )
                    {

                        CtiTime timeNow;
                        varValueAbc = getRegressionA().regression( timeNow.seconds() );
                        varValueBbc = getRegressionB().regression( timeNow.seconds() );
                        varValueCbc = getRegressionC().regression( timeNow.seconds() );
                        int size =  currentCapBank->getBankSize()/3;
                        if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                        {
                            Cti::FormattedList list;

                            list << "Rate of Change";
                            list.add("Phase A") << varValueAbc;
                            list.add("Phase B") << varValueBbc;
                            list.add("Phase C") << varValueCbc;

                            CTILOG_DEBUG(dout, list);
                        }
                    }
                    changeA = varValueAbc - varAValue;
                    changeB = varValueBbc - varBValue;
                    changeC = varValueCbc - varCValue;

                    if( changeA < 0 ||  changeB < 0 ||  changeC < 0)
                    {
                        CTILOG_INFO(dout, ""<< currentCapBank->getPaoName() <<":Var change in wrong direction?");
                        if (stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && _USE_FLIP_FLAG &&
                           currentCapBank->getVCtrlIndex() == 1)
                        {
                           currentCapBank->setAssumedOrigVerificationState(CtiCCCapBank::Close);
                           setCurrentVerificationCapBankState(CtiCCCapBank::Close);
                           currentCapBank->setControlStatus(CtiCCCapBank::OpenPending);
                           assumedWrongFlag = true;
                           changeA = 0 - changeA;
                           changeB = 0 - changeB;
                           changeC = 0 - changeC;

                        }
                    }
                    ratioA = changeA/(currentCapBank->getBankSize() /3);
                    ratioB = changeB/(currentCapBank->getBankSize() /3);
                    ratioC = changeC/(currentCapBank->getBankSize() /3);
                    if( !areAllPhasesSuccess(ratioA, ratioB, ratioC, minConfirmPercent)  )
                    {

                        if( shouldCapBankBeFailed(ratioA, ratioB, ratioC, failPercent) &&
                             failPercent != 0 && minConfirmPercent != 0 )
                        {
                           if (!assumedWrongFlag)
                               store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::CloseFail, currentCapBank);
                           else
                               store->setControlStatusAndIncrementFailCount(pointChanges, CtiCCCapBank::OpenFail, currentCapBank);

                           additional = string("Feeder: ");
                           additional += getPaoName();
                           if (currentCapBank->getControlStatusQuality() != CC_CommFail)
                               currentCapBank->setControlStatusQuality(CC_Fail);
                       }
                       else if( minConfirmPercent != 0 )
                       {
                           if (!assumedWrongFlag)
                               currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                           else
                               currentCapBank->setControlStatus(CtiCCCapBank::OpenQuestionable);

                           additional = string("Feeder: ");
                           additional += getPaoName();
                           if (areAllPhasesQuestionable(ratioA, ratioB, ratioC, minConfirmPercent, failPercent))
                           {
                               currentCapBank->setControlStatusQuality(CC_Significant);
                           }
                           else
                           {
                               currentCapBank->setControlStatusQuality(CC_Partial);
                           }
                       }
                       else
                       {
                           if (!assumedWrongFlag)
                               currentCapBank->setControlStatus(CtiCCCapBank::Close);
                           else
                               currentCapBank->setControlStatus(CtiCCCapBank::Open);

                           additional = string("Feeder: ");
                           additional += getPaoName();
                           currentCapBank->setControlStatusQuality(CC_Normal);
                           vResult = true;
                       }
                    }
                    else
                    {
                       if (!assumedWrongFlag)
                           currentCapBank->setControlStatus(CtiCCCapBank::Close);
                       else
                           currentCapBank->setControlStatus(CtiCCCapBank::Open);
                       additional = string("Feeder: ");
                       additional += getPaoName();
                       currentCapBank->setControlStatusQuality(CC_Normal);
                       vResult = true;
                    }
                    currentCapBank->setPartialPhaseInfo(getPhaseIndicatorString(currentCapBank->getControlStatus(), ratioA, ratioB, ratioC, minConfirmPercent, failPercent));
                    text = createPhaseControlStatusUpdateText(currentCapBank->getControlStatusText(), varAValue,
                                                   varBValue, varCValue, ratioA, ratioB, ratioC);

                    currentCapBank->setBeforeVarsString(createPhaseVarText(varValueAbc,varValueBbc,varValueCbc,1.0));
                    currentCapBank->setAfterVarsString(createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                    currentCapBank->setPercentChangeString(createPhaseRatioText(ratioA, ratioB, ratioC,100.0));

               }
               else
               {
                   char tempchar[80];
                   currentCapBank->setControlStatus(CtiCCCapBank::CloseQuestionable);
                    text = "Var: ";
                   text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
                   text += "- Non Normal Var Quality = ";
                   _ltoa(getCurrentVarPointQuality(),tempchar,10);
                   text += tempchar;
                   text += ", CloseQuestionable";
                   additional = string("Feeder: ");
                   additional += getPaoName();

                   currentCapBank->setBeforeVarsString(createPhaseVarText(varValueAbc,varValueBbc,varValueCbc,1.0));
                   currentCapBank->setAfterVarsString(createPhaseVarText(varAValue, varBValue, varCValue,1.0));
                   currentCapBank->setPercentChangeString(createPhaseRatioText(ratioA, ratioB, ratioC,100.0));

                   currentCapBank->setControlStatusQuality(CC_AbnormalQuality);
               }
           }
           else
           {
               CTILOG_INFO(dout, "Last Cap Bank ("<< currentCapBank->getPaoName() <<") controlled not in pending status");
               text += "Var: ";
               text += CtiNumStr(getCurrentVarLoadPointValue(), getDecimalPlaces()).toString();
               text += " - control was not pending, " ;
               text += currentCapBank->getControlStatusText();

           }
           if ( (currentCapBank->getRetryOpenFailedFlag() || currentCapBank->getRetryCloseFailedFlag() )  &&
               (currentCapBank->getControlStatus() != CtiCCCapBank::CloseFail && currentCapBank->getControlStatus() != CtiCCCapBank::OpenFail))
           {
               currentCapBank->setRetryOpenFailedFlag(false);
               currentCapBank->setRetryCloseFailedFlag(false);
           }


           if( currentCapBank->getStatusPointId() > 0 )
           {
               if (currentCapBank->getPorterRetFailFlag() && currentCapBank->getIgnoreFlag())
               {
                     currentCapBank->setVCtrlIndex(5);
                     currentCapBank->setVerificationDoneFlag(true);
                     currentCapBank->setIgnoreFlag(false);
               }
               else
               {
                   CtiCCSubstationBusPtr sub = store->findSubBusByPAObjectID(getParentId());
                   if( sub != NULL )
                   {
                      sub->createStatusUpdateMessages(pointChanges, ccEvents, currentCapBank, this, text, additional, true,
                                                      varValueAbc+varValueBbc+varValueCbc, varAValue+varBValue+varCValue, changeA+changeB+changeC,
                                                      varAValue, varBValue, varCValue);
                   }
               }
               currentCapBank->setPorterRetFailFlag(false);
           }
           else
           {
               CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
               << " DeviceID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );
           }

           if (currentCapBank->updateVerificationState())
           {
               returnBoolean = true;
               currentCapBank->setPerformingVerificationFlag(false);
               return returnBoolean;
           }

           currentCapBank->setIgnoreFlag(false);
           currentCapBank->setControlRecentlySentFlag(false);
           foundCap = true;
           break;
       }
    }

    if (foundCap == false)
    {
        CTILOG_WARN(dout, "Last Verification Cap Bank controlled NOT FOUND");
        returnBoolean = true;
    }

    setRetryIndex(0);
    return returnBoolean;
}


bool CtiCCFeeder::startVerificationOnCapBank(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages)
{
    //get CapBank to perform verification on...subbus stores, currentCapBankToVerifyId

    CtiRequestMsg* request = NULL;
    bool retVal = true;

    for(long j=0;j<_cccapbanks.size();j++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[j];
        if( currentCapBank->getPaoId() == _currentVerificationCapBankId )
        {
            currentCapBank->initVerificationControlStatus();
            currentCapBank->setAssumedOrigVerificationState(getCurrentVerificationCapBankOrigState());
            currentCapBank->setPreviousVerificationControlStatus(-1);
            currentCapBank->setVCtrlIndex(1); //1st control sent
            setPerformingVerificationFlag(true);

            setCurrentVerificationCapBankState(currentCapBank->getAssumedOrigVerificationState());
            currentCapBank->setPerformingVerificationFlag(true);
            if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Open)
            {
                request = createCapBankVerificationControl(currentDateTime, pointChanges, ccEvents, pilMessages, currentCapBank,  CtiCCCapBank::Close);
            }
            else if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Close)
            {
                request = createCapBankVerificationControl(currentDateTime, pointChanges, ccEvents, pilMessages, currentCapBank,  CtiCCCapBank::Open);
            }
            if( request != NULL )
            {
                pilMessages.push_back(request);
                setLastOperationTime(currentDateTime);

                setLastVerificationMsgSentSuccessfulFlag(true);
                setLastCapBankControlledDeviceId( currentCapBank->getPaoId());
                setLastOperationTime(currentDateTime);
                if ( getStrategy()->getUnitType() == ControlStrategy::IntegratedVoltVar )
                {
                    updatePointResponsePreOpValues( currentCapBank );
                }
                setVarValueBeforeControl(getCurrentVarLoadPointValue());
                setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                figureEstimatedVarLoadPointValue();
                if( getEstimatedVarLoadPointId() > 0 )
                {
                    pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                }

            }
            else
            {
                retVal = false;
                setLastVerificationMsgSentSuccessfulFlag(false);
            }

            return retVal;
        }
    }
    return retVal;
}

CtiRequestMsg*  CtiCCFeeder::createCapBankVerificationControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents,
                                      CtiMultiMsg_vec& pilMessages, CtiCCCapBank* currentCapBank, int control)
{

    CtiRequestMsg* request = NULL;
    if( control == CtiCCCapBank::Close && currentCapBank->getRecloseDelay() > 0 &&
        currentDateTime.seconds() < currentCapBank->getLastStatusChangeTime().seconds() + currentCapBank->getRecloseDelay() )
    {
        CTILOG_INFO(dout, "Can Not Close Cap Bank: " << currentCapBank->getPaoName() << " yet...because it has not passed its reclose delay.");
        if( _CC_DEBUG & CC_DEBUG_EXTENDED )
        {
            Cti::FormattedList list;

            list.add("Last Status Change Time") << currentCapBank->getLastStatusChangeTime();
            list.add("Reclose Delay")           << currentCapBank->getRecloseDelay();
            list.add("Current Date Time")       << currentDateTime;

            CTILOG_DEBUG(dout, list);
        }

        setWaitForReCloseDelayFlag(true);
    }
    else
    {
        //check capbank reclose delay here...
        double controlValue = ( getStrategy()->getUnitType() == ControlStrategy::Volts )
                                   ? getCurrentVoltLoadPointValue()
                                   : getCurrentVarLoadPointValue();

        string text = createTextString(getStrategy()->getControlMethod(), control, controlValue, getCurrentVarLoadPointValue()) ;
        bool flipFlag = _USE_FLIP_FLAG && stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701");

        /*
            I'm pretty sure the following block is in error - should be calling createTextString(...) with the updated value of 'control' instead of above (like in the bus version...)
        */
        if( control == CtiCCCapBank::Open)
        {
            control = (flipFlag ? 4 : CtiCCCapBank::Open);
            request = createIncreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, control, getCurrentVarLoadPointValue(),
                                                       getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
        }
        else
        {
            control = (flipFlag ? 4 : CtiCCCapBank::Close);
            request = createDecreaseVarVerificationRequest(currentCapBank, pointChanges, ccEvents, text, control, getCurrentVarLoadPointValue(),
                                                           getPhaseAValue(), getPhaseBValue(), getPhaseCValue());
        }
    }
    return request;
}




bool CtiCCFeeder::sendNextCapBankVerificationControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages)
{
    bool retVal = false;
    CtiRequestMsg* request = NULL;
    for(long j=0;j<_cccapbanks.size();j++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[j];
        if( currentCapBank->getPaoId() == _currentVerificationCapBankId )
        {
            if (currentCapBank->getVCtrlIndex() == 1 || currentCapBank->getVCtrlIndex() == 3)
            {
                if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Open)
                {
                    request = createCapBankVerificationControl(currentDateTime, pointChanges, ccEvents, pilMessages, currentCapBank,  CtiCCCapBank::Close);
                }
                else if (getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Close)
                {
                    request = createCapBankVerificationControl(currentDateTime, pointChanges, ccEvents, pilMessages, currentCapBank,  CtiCCCapBank::Open);
                }
            }
            else if (currentCapBank->getVCtrlIndex() == 2)
            {
                //(getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Open) ||
                //(getCurrentVerificationCapBankOrigState() == CtiCCCapBank::Close)
                request = createCapBankVerificationControl(currentDateTime, pointChanges, ccEvents, pilMessages, currentCapBank, getCurrentVerificationCapBankOrigState());
            }
            else if (currentCapBank->getVCtrlIndex() == 5 || currentCapBank->getVCtrlIndex() == 0)
            {
                request = NULL;
                currentCapBank->setVCtrlIndex(5);
                setLastVerificationMsgSentSuccessfulFlag(true);
                return true;
            }

            if( request != NULL )
            {
                retVal = true;
                pilMessages.push_back(request);
                setLastCapBankControlledDeviceId( currentCapBank->getPaoId());
                setLastOperationTime(currentDateTime);
                setVarValueBeforeControl(getCurrentVarLoadPointValue());
                if ( getStrategy()->getUnitType() == ControlStrategy::IntegratedVoltVar )
                {
                    updatePointResponsePreOpValues( currentCapBank );
                }
                setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                setLastVerificationMsgSentSuccessfulFlag(true);
                setWaitForReCloseDelayFlag(false);
                figureEstimatedVarLoadPointValue();
                if( getEstimatedVarLoadPointId() > 0 )
                {
                    pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
                }

                return retVal;
            }
            else
            {
                setLastVerificationMsgSentSuccessfulFlag(false);
            }

        }
    }




    return retVal;
}



bool CtiCCFeeder::areThereMoreCapBanksToVerify()
{

    getNextCapBankToVerify();
    if (getCurrentVerificationCapBankId() != -1 )//&& !getDisableFlag())
    {
        setPerformingVerificationFlag(true);

        return true;
    }
    else
    {
        CtiCCCapBank_SVector& ccCapBanks = getCCCapBanks();
        for(long j=0;j<ccCapBanks.size();j++)
        {
            CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
            //currentCapBank->setVerificationFlag(false);
            currentCapBank->setPerformingVerificationFlag(false);
            currentCapBank->setVerificationDoneFlag(true);
        }

        setPerformingVerificationFlag(false);
        setVerificationDoneFlag(true);
        //setBusUpdatedFlag(true);
        return false;
    }
}


void CtiCCFeeder::getNextCapBankToVerify()
{
    _currentVerificationCapBankId = -1;

    CtiCCCapBank_SVector& ccCapBanks = getCCCapBanks();
    for(long j=0;j<ccCapBanks.size();j++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)ccCapBanks[j];
        if( currentCapBank->getVerificationFlag() && !currentCapBank->getVerificationDoneFlag() )
        {
            _currentVerificationCapBankId = currentCapBank->getPaoId();
            return;
        }
    }
    setVerificationDoneFlag(true);
}



/*---------------------------------------------------------------------------
    fillOutBusOptimizedInfo

    Sets _busoptimizedvarcategory with one of three different integers
    0 (below current set point minus bandwidth),
    1 (within bandwidth around current set point), or
    2 (above current set point plus bandwidth)

    Also sets the _busoptimizedvaroffset within the current
    _busoptimizedvarcategory
---------------------------------------------------------------------------*/
void CtiCCFeeder::fillOutBusOptimizedInfo(bool peakTimeFlag)
{
    setPeakTimeFlag(peakTimeFlag);
    double lagLevel = (peakTimeFlag?getStrategy()->getPeakLag():getStrategy()->getOffPeakLag());
    double leadLevel = (peakTimeFlag?getStrategy()->getPeakLead():getStrategy()->getOffPeakLead());
    double setpoint = (lagLevel + leadLevel)/2;

    //if current var load is less than the set point minus the bandwidth
    if( getCurrentVarLoadPointValue() < leadLevel )
    {
        _busoptimizedvarcategory = 0;
        _busoptimizedvaroffset = getCurrentVarLoadPointValue() - leadLevel;
    }
    //if current var load is within the range defined by the set point plus/minus the bandwidth
    else if( (getCurrentVarLoadPointValue() > leadLevel) &&
             (getCurrentVarLoadPointValue() < lagLevel) )
    {
        _busoptimizedvarcategory = 1;
        _busoptimizedvaroffset = getCurrentVarLoadPointValue() - setpoint;
    }
    //if current var load is more than the set point plus the bandwidth
    else if( getCurrentVarLoadPointValue() > lagLevel )
    {
        _busoptimizedvarcategory = 2;
        _busoptimizedvaroffset = getCurrentVarLoadPointValue() - lagLevel;
    }
}

/*---------------------------------------------------------------------------
    isAlreadyControlled

    Returns a boolean if the last cap bank controlled expected var changes
    are reflected in the current var level before the max confirm time

---------------------------------------------------------------------------*/
bool CtiCCFeeder::isAlreadyControlled(long minConfirmPercent, long currentVarPointQuality,
                                         double varAValueBeforeControl, double varBValueBeforeControl,
                                         double varCValueBeforeControl, double varAValue, double varBValue,
                                         double varCValue, double varValueBeforeControl, double currentVarLoadPointValue,
                                         const CtiRegression& reg, const CtiRegression& regA, const CtiRegression& regB, const CtiRegression& regC,
                                         bool usePhaseData, bool useTotalizedControl)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    bool returnBoolean = false;
    bool found = false;

    double timeSeconds = CtiTime().seconds();
    if( _IGNORE_NOT_NORMAL_FLAG && currentVarPointQuality != NormalQuality )
    {
        return false;
    }
    if( minConfirmPercent <= 0 )
    {
        return false;
    }

    double oldVarValue = varValueBeforeControl;
    double newVarValue = currentVarLoadPointValue;
    long bankId = getLastCapBankControlledDeviceId();

    CtiCCCapBankPtr currentCapBank = store->findCapBankByPAObjectID(bankId);

    if (currentCapBank != NULL)
    {
        found = true;
    }
    else
    {
        // Check all other banks on this feeder for a pending state...

        // Not adding return statements to keep the functionality the same.
        // In the error case of multiple pending banks, the last one in the list will be determining the return variable.
        for (int i=0; i < _cccapbanks.size(); ++i)
        {
            CtiCCCapBank* bank = (CtiCCCapBank*)_cccapbanks[i];
            if (bank->getControlStatus() == CtiCCCapBank::OpenPending ||
                bank->getControlStatus() == CtiCCCapBank::ClosePending )
            {
                if (ciStringEqual(bank->getOperationalState(),CtiCCCapBank::SwitchedOperationalState))
                {
                    bankId = bank->getPaoId();
                    setLastCapBankControlledDeviceId(bankId);
                    currentCapBank = bank;
                    found = true;
                }
            }
        }
    }

    if (found == true)
    {
        if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending ||
            currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
        {
            if (usePhaseData && !useTotalizedControl)
            {
                double ratioA;
                double ratioB;
                double ratioC;
                int banksize = currentCapBank->getBankSize() / 3;

                if( checkForRateOfChange(reg,regA,regB,regC) )
                {
                    ratioA = fabs((varAValue - (regA.regression(timeSeconds))) / banksize);
                    ratioB = fabs((varBValue - (regB.regression(timeSeconds))) / banksize);
                    ratioC = fabs((varCValue - (regC.regression(timeSeconds))) / banksize);
                }
                else
                {
                    ratioA = fabs((varAValue - varAValueBeforeControl) / banksize);
                    ratioB = fabs((varBValue - varBValueBeforeControl) / banksize);
                    ratioC = fabs((varCValue - varCValueBeforeControl) / banksize);
                }

                if( areAllPhasesSuccess(ratioA, ratioB, ratioC, minConfirmPercent) )
                {
                    return true;
                }
            }
            else
            {
                double change;
                double ratio;
                int banksize = currentCapBank->getBankSize();

                if( checkForRateOfChange(reg,regA,regB,regC) )
                {
                    if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                    {
                        CTILOG_DEBUG(dout, "Regression Value: " << reg.regression(timeSeconds));
                    }
                    change = fabs(reg.regression(timeSeconds) - newVarValue);
                }
                else
                {
                    change = fabs(newVarValue - oldVarValue);
                }

                ratio = change/banksize;
                if(_CC_DEBUG & CC_DEBUG_RATE_OF_CHANGE)
                {
                    CTILOG_DEBUG(dout, "Change Value: " << change << " Ratio Value: " << ratio << " ?>= Min Confirm: " << minConfirmPercent);
                }
                if( ratio >= minConfirmPercent*.01 )
                {
                    return true;
                }
            }
        }
        else
        {
            CTILOG_WARN(dout, "Last Cap Bank: " << bankId << " controlled not in pending status");
            return true;
        }
    }
    else
    {
        CTILOG_WARN(dout, "Last Cap Bank controlled NOT FOUND");
        return true;
    }

    return false;
}

/*---------------------------------------------------------------------------
    isPastMaxConfirmTime

    Returns a boolean if the last control is past the maximum confirm time.
---------------------------------------------------------------------------*/
bool CtiCCFeeder::isPastMaxConfirmTime(const CtiTime& currentDateTime, long maxConfirmTime, long feederRetries)
{
    bool returnBoolean = false;

    if (getStrategy()->getUnitType() != ControlStrategy::None && getStrategy()->getControlSendRetries() > feederRetries)
    {
        feederRetries = getStrategy()->getControlSendRetries();
    }

    if( ((getLastOperationTime().seconds() + ((maxConfirmTime/_SEND_TRIES) * (_retryIndex + 1))) <= currentDateTime.seconds()) ||
        ((getLastOperationTime().seconds() + ((maxConfirmTime/(feederRetries+1)) * (_retryIndex + 1))) <= currentDateTime.seconds()) )
    {
        returnBoolean = true;
    }

    return returnBoolean;
}

/*---------------------------------------------------------------------------
    isAlreadyControlled

    Returns a boolean if the last cap bank controlled expected var changes
    are reflected in the current var level before the max confirm time
---------------------------------------------------------------------------*/
bool CtiCCFeeder::isVerificationAlreadyControlled(long minConfirmPercent, long quality, double varAValueBeforeControl,
                             double varBValueBeforeControl, double varCValueBeforeControl,
                             double varAValue, double varBValue, double varCValue,
                             double oldVarValue, double newVarValue,  bool usePhaseData, bool useTotalizedControl)
{
    bool returnBoolean = false;
    bool found = false;

    if (_porterRetFailFlag == true)
    {
        _porterRetFailFlag = false;
        return true;
    }
    else if( !_IGNORE_NOT_NORMAL_FLAG || quality == NormalQuality )
    {
        if( minConfirmPercent > 0 )
        {
            CtiCCCapBankPtr currentCapBank = getLastCapBankControlledDevice();
            if( currentCapBank != NULL &&
                currentCapBank->getPaoId() == getLastCapBankControlledDeviceId() &&
                currentCapBank->getPerformingVerificationFlag() &&
                currentCapBank->getVCtrlIndex() > 0)
                {
                    double change;
                    if( currentCapBank->isPendingStatus())
                    {
                        if ( usePhaseData && !useTotalizedControl )
                        {
                            double ratioA;
                            double ratioB;
                            double ratioC;
                            int banksize = currentCapBank->getBankSize() / 3;

                            ratioA = fabs((varAValue - varAValueBeforeControl) / banksize);
                            ratioB = fabs((varBValue - varBValueBeforeControl) / banksize);
                            ratioC = fabs((varCValue - varCValueBeforeControl) / banksize);

                            if( areAllPhasesSuccess(ratioA, ratioB, ratioC, minConfirmPercent) )
                            {
                                returnBoolean = true;
                            }
                            else
                            {
                                returnBoolean = false;
                            }
                        }
                        else
                        {
                            change = fabs(newVarValue - oldVarValue);
                            double ratio = fabs(change/currentCapBank->getBankSize());
                            if( ratio >= minConfirmPercent*.01 )
                            {
                                returnBoolean = true;
                            }
                            else
                            {
                                returnBoolean = false;
                            }
                        }
                        found = true;
                    }
                    else if (currentCapBank->getPorterRetFailFlag())
                    {
                        returnBoolean = true;
                        found = true;
                    }
                    else
                    {
                        CTILOG_INFO(dout, "Last Cap Bank: "<<getLastCapBankControlledDeviceId()<<" controlled not in pending status");
                        returnBoolean = false;
                    }

                }


            // Check all other banks on this feeder for a pending state...
            if (!found)
            {
                for(long i=0;i<_cccapbanks.size();i++)
                {
                    currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
                    if (currentCapBank->isPendingStatus() && currentCapBank->getVCtrlIndex() > 0)
                    {
                        if ( usePhaseData && !useTotalizedControl )
                        {
                            double ratioA;
                            double ratioB;
                            double ratioC;
                            int banksize = currentCapBank->getBankSize() / 3;

                            ratioA = fabs((varAValue - varAValueBeforeControl) / banksize);
                            ratioB = fabs((varBValue - varBValueBeforeControl) / banksize);
                            ratioC = fabs((varCValue - varCValueBeforeControl) / banksize);

                            if( areAllPhasesSuccess(ratioA, ratioB, ratioC, minConfirmPercent) )
                            {
                                returnBoolean = true;
                            }
                            else
                            {
                                returnBoolean = false;
                            }
                            found = true;

                        }
                        else
                        {
                            double change = newVarValue - oldVarValue;
                            double ratio = fabs(change/currentCapBank->getBankSize());
                            if( ratio >= minConfirmPercent*.01 )
                            {
                                returnBoolean = true;
                            }
                            else
                            {
                                returnBoolean = false;
                            }
                        }
                        found = true;
                    }
                    else if (currentCapBank->getPorterRetFailFlag())
                    {
                        returnBoolean = true;
                        found = true;
                    }
                }
            }
            if (found == false)
            {
                CTILOG_WARN(dout, "Last Cap Bank controlled NOT FOUND");
                returnBoolean = true;
            }
        }
    }

    return returnBoolean;
}


/*---------------------------------------------------------------------------
    attemptToResendControl

    Returns a .
---------------------------------------------------------------------------*/
bool CtiCCFeeder::attemptToResendControl(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages, long maxConfirmTime)
{
    bool returnBoolean = false;
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();


    for(long i=0;i<_cccapbanks.size();i++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
        if( currentCapBank->getPaoId() == getLastCapBankControlledDeviceId() )
        {
            if (!(_USE_FLIP_FLAG && stringContainsIgnoreCase(currentCapBank->getControlDeviceType(),"CBC 701") && getVerificationFlag()) )
            {

                if( currentDateTime.seconds() < currentCapBank->getLastStatusChangeTime().seconds() + maxConfirmTime )
                {
                    if( currentCapBank->getControlStatus() == CtiCCCapBank::OpenPending )
                    {
                        figureEstimatedVarLoadPointValue();
                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            string text = string("Resending Open");
                            string additional = "Sub: " + getParentName() + " /Feeder: " + getPaoName() + formatMapInfo( this );

                            pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent, "cap control"));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);

                            long stationId, areaId, spAreaId;
                            store->getFeederParentInfo(this, spAreaId, areaId, stationId);
                            ccEvents.push_back(EventLogEntry(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlCommandRetrySent, getEventSequence(), currentCapBank->getControlStatus(), text, "cap control"));
                        }
                        else
                        {
                            CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                            << " DeviceID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );
                        }

                        pilMessages.push_back(
                           createBankOpenRequest(*currentCapBank).release());

                        if (_RETRY_ADJUST_LAST_OP_TIME)
                        {
                            setLastOperationTime(currentDateTime);
                            currentCapBank->setLastStatusChangeTime(currentDateTime);
                        }
                        returnBoolean = true;
                    }
                    else if( currentCapBank->getControlStatus() == CtiCCCapBank::ClosePending )
                    {
                        figureEstimatedVarLoadPointValue();
                        if( currentCapBank->getStatusPointId() > 0 )
                        {
                            string text = string("Resending Close");
                            string additional = "Sub: " + getParentName() + " /Feeder: " + getPaoName() + formatMapInfo( this );

                            pointChanges.push_back(new CtiSignalMsg(currentCapBank->getStatusPointId(),0,text,additional,CapControlLogType,SignalEvent, "cap control"));
                            ((CtiPointDataMsg*)pointChanges[pointChanges.size()-1])->setSOE(1);

                            long stationId, areaId, spAreaId;
                            store->getFeederParentInfo(this, spAreaId, areaId, stationId);
                            ccEvents.push_back(EventLogEntry(0, currentCapBank->getStatusPointId(), spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlCommandRetrySent, getEventSequence(), currentCapBank->getControlStatus(), text, "cap control"));
                        }
                        else
                        {
                            CTILOG_WARN(dout, "Cap Bank: " << currentCapBank->getPaoName()
                            << " DeviceID: " << currentCapBank->getPaoId() << " doesn't have a status point!" );
                        }

                        pilMessages.push_back(
                           createBankCloseRequest(*currentCapBank).release());

                        if (_RETRY_ADJUST_LAST_OP_TIME)
                        {
                            setLastOperationTime(currentDateTime);
                            currentCapBank->setLastStatusChangeTime(currentDateTime);
                        }
                        returnBoolean = true;
                    }
                    else if( _CC_DEBUG && CC_DEBUG_EXTENDED )
                    {
                        CTILOG_DEBUG(dout, "Cannot Resend Control for Cap Bank: "<< currentCapBank->getPaoName() <<", Not Pending");
                    }
                }
                else if( _CC_DEBUG && CC_DEBUG_EXTENDED )
                {
                    CTILOG_DEBUG(dout, "Cannot Resend Control for Cap Bank: "<< currentCapBank->getPaoName() <<", Past Confirm Time");
                }
                break;
            }
        }
    }

    return returnBoolean;
}

bool CtiCCFeeder::checkForAndPerformVerificationSendRetry(const CtiTime& currentDateTime, CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages, long maxConfirmTime, long sendRetries)
{
   bool returnBoolean = false;
   if (getVerificationFlag() && getPerformingVerificationFlag() &&
       isPastMaxConfirmTime(currentDateTime,maxConfirmTime,sendRetries) &&
       attemptToResendControl(currentDateTime, pointChanges, ccEvents, pilMessages, maxConfirmTime) )
   {
       if (_RETRY_ADJUST_LAST_OP_TIME)
           setLastOperationTime(currentDateTime);
       setRetryIndex(getRetryIndex() + 1);
       returnBoolean = true;
   }
   return returnBoolean;
}

void CtiCCFeeder::setVerificationFlag(bool verificationFlag)
{
    if( verificationFlag )
    {
        setVerificationDoneFlag(false);
    }

    updateDynamicValue( _verificationFlag, verificationFlag );
}

void CtiCCFeeder::setPerformingVerificationFlag(bool performingVerificationFlag)
{
    updateDynamicValue( _performingVerificationFlag, performingVerificationFlag );
}
void CtiCCFeeder::setVerificationDoneFlag(bool verificationDoneFlag)
{
    updateDynamicValue( _verificationDoneFlag, verificationDoneFlag );
}

void CtiCCFeeder::setPreOperationMonitorPointScanFlag( bool flag)
{
    updateDynamicValue( _preOperationMonitorPointScanFlag, flag );
}

void CtiCCFeeder::setOperationSentWaitFlag( bool flag)
{
    updateDynamicValue( _operationSentWaitFlag, flag );
}

void CtiCCFeeder::setPostOperationMonitorPointScanFlag( bool flag)
{
    updateDynamicValue( _postOperationMonitorPointScanFlag, flag );
}

void CtiCCFeeder::setWaitForReCloseDelayFlag(bool flag)
{
    updateDynamicValue( _waitForReCloseDelayFlag, flag );
}

void CtiCCFeeder::setMaxDailyOpsHitFlag(bool flag)
{
    updateDynamicValue( _maxDailyOpsHitFlag, flag );
}

void CtiCCFeeder::setOvUvDisabledFlag(bool flag)
{
    updateDynamicValue( _ovUvDisabledFlag, flag );
}

void CtiCCFeeder::setCorrectionNeededNoBankAvailFlag(bool flag)
{
    updateDynamicValue( _correctionNeededNoBankAvailFlag, flag );
}

void CtiCCFeeder::setLikeDayControlFlag(bool flag)
{
    updateDynamicValue( _likeDayControlFlag, flag );
}
void CtiCCFeeder::setLastVerificationMsgSentSuccessfulFlag(bool flag)
{
    updateDynamicValue( _lastVerificationMsgSentSuccessful, flag );
}

void CtiCCFeeder::setCurrentVerificationCapBankId(long capBankId)
{
    updateDynamicValue( _currentVerificationCapBankId, capBankId );
}

void CtiCCFeeder::setRetryIndex(long value)
{
    updateDynamicValue( _retryIndex, value );
}

bool CtiCCFeeder::getVerificationFlag() const
{
    return _verificationFlag;
}

bool CtiCCFeeder::getPerformingVerificationFlag() const
{
    return _performingVerificationFlag;
}

bool CtiCCFeeder::getVerificationDoneFlag() const
{
    return _verificationDoneFlag;
}

bool CtiCCFeeder::getPreOperationMonitorPointScanFlag() const
{
    return _preOperationMonitorPointScanFlag;
}
bool CtiCCFeeder::getOperationSentWaitFlag() const
{
    return _operationSentWaitFlag;
}
bool CtiCCFeeder::getPostOperationMonitorPointScanFlag() const
{
    return _postOperationMonitorPointScanFlag;
}

bool CtiCCFeeder::getWaitForReCloseDelayFlag() const
{
    return _waitForReCloseDelayFlag;
}
bool CtiCCFeeder::getMaxDailyOpsHitFlag() const
{
    return _maxDailyOpsHitFlag;
}

bool CtiCCFeeder::getOvUvDisabledFlag() const
{
    return _ovUvDisabledFlag;
}

bool CtiCCFeeder::getCorrectionNeededNoBankAvailFlag() const
{
    return _correctionNeededNoBankAvailFlag;

}
bool CtiCCFeeder::getLikeDayControlFlag() const
{
    return _likeDayControlFlag;
}

bool CtiCCFeeder::getLastVerificationMsgSentSuccessfulFlag() const
{
    return _lastVerificationMsgSentSuccessful;
}



long CtiCCFeeder::getCurrentVerificationCapBankId() const
{
    return _currentVerificationCapBankId;
}

bool CtiCCFeeder::voltControlBankSelectProcess(const CtiCCMonitorPoint & point, CtiMultiMsg_vec &pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages)
{
    bool retVal = false;
    CtiCCCapBank* bestBank = NULL;

    CtiRequestMsg* request = NULL;
   //Check for undervoltage condition first.
   try
   {
        StrategyManager::SharedPtr  strategy;
        {
            /*
                I'm not sure how to handle this... the calling code for this function checks the bus strategy
                    so I'm imitating this here.  If there is no strategy assigned to the bus, it defaults over
                    to the feeder assigned strategy.  Normally this won't be an issue because the feeder
                    strategy is cascaded down from the bus, but unintended things may happen with a differing
                    strategy attached to the feeder.
            */
            CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
            CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

            if ( CtiCCSubstationBusPtr bus = store->findSubBusByPAObjectID( getParentId() ) )
            {
                strategy = bus->getStrategy();
            }

            if ( strategy->getUnitType() == ControlStrategy::None ) 
            {
                strategy = getStrategy();
            }
        }

        CtiTime Now;

        const bool   peakTime   = isPeakTime( Now );
        const double upperLimit = strategy->getUpperVoltLimit( peakTime );
        const double lowerLimit = strategy->getLowerVoltLimit( peakTime );

        double upperBound = upperLimit;
        double lowerBound = lowerLimit;

        if ( point.getOverrideStrategy() )
        {
            upperBound = point.getUpperBandwidth();
            lowerBound = point.getLowerBandwidth();
        }

       if ( point.getValue() < lowerBound)
       {
            CtiCCCapBank* parentBank = NULL;

            //1.  First check this point's parent bank to see if we can close it.
            parentBank = getMonitorPointParentBank(point);
            if (parentBank != NULL)
            {
                if ( parentBank->isSwitched() &&
                        ! parentBank->getDisableFlag() &&
                        ( parentBank->getControlStatus() == CtiCCCapBank::Open ||
                          parentBank->getControlStatus() == CtiCCCapBank::OpenQuestionable ) )
                {
                    try
                    {
                        PointResponse pResponse = parentBank->getPointResponse(point);

                        const double value = point.getValue() + pResponse.getDelta();

                        if ( ( lowerBound <= value && value <= upperBound )
                                || value < upperBound )
                        {
                            CTILOG_INFO(dout, "Attempting to Increase Voltage on Feeder: "<<getPaoName()<<" CapBank: "<<parentBank->getPaoName());

                            if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                            {
                                CTILOG_DEBUG(dout, "MULTIVOLT: MonitorPoint->bankID/pointID: "<<point.getDeviceId()<<"/"<<point.getPointId()<<" Parent CapBank: "<<parentBank->getPaoName() <<" selected to Close");
                            }

                            //Check other monitor point responses using this potential capbank
                            if ( ! parentBank->checkMaxDailyOpCountExceeded( pointChanges, ccEvents ) &&
                                 areOtherMonitorPointResponsesOk(point.getPointId(), parentBank, CtiCCCapBank::Close))
                            {
                                double controlValue = ( getStrategy()->getUnitType() != ControlStrategy::Volts )
                                                           ? getCurrentVoltLoadPointValue()
                                                           : getCurrentVarLoadPointValue();

                                string text = createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Close, controlValue, getCurrentVarLoadPointValue());
                                request = createDecreaseVarRequest(parentBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());

                                updatePointResponsePreOpValues(parentBank);
                                bestBank = parentBank;
                            }
                            else
                            {
                                parentBank = NULL;
                            }
                        }
                        else
                        {
                            parentBank = NULL;
                        }
                    }
                    catch (NotFoundException& e)
                    {
                        CTILOG_WARN(dout, "PointResponse not found. CapBank: "<< parentBank->getPaoName() <<  " pointId: " << point.getPointId());
                    }
                }
                else
                {
                    parentBank = NULL;
                }
            }

            //2.  If parent bank won't work, start checking other banks...
            if (parentBank == NULL || request == NULL)
            {
                for ( CtiCCCapBankPtr currentCapBank : _cccapbanks )
                {
                    if ( currentCapBank->isSwitched() &&
                            ! currentCapBank->getDisableFlag() &&
                            ( currentCapBank->getControlStatus() == CtiCCCapBank::Open ||
                              currentCapBank->getControlStatus() == CtiCCCapBank::OpenQuestionable ) )
                    {
                        if (point.getDeviceId() != currentCapBank->getPaoId())
                        {
                            try
                            {
                                PointResponse pResponse = currentCapBank->getPointResponse(point);

                                const double value = point.getValue() + pResponse.getDelta();

                                if ( ( lowerBound <= value && value <= upperBound )
                                        || value < upperBound
                                        || pResponse.getDelta() == 0 )
                                {
                                    CTILOG_INFO(dout, "Attempting to Increase Voltage on Feeder: "<<getPaoName()<<" CapBank: "<<currentCapBank->getPaoName());

                                    if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                    {
                                        CTILOG_DEBUG(dout, "MULTIVOLT: MonitorPoint->bankID/pointID: "<<point.getDeviceId()<<"/"<<point.getPointId()<<" CapBank: "<<currentCapBank->getPaoName() <<" selected to Close");
                                    }
                                    //Check other monitor point responses using this potential capbank
                                    if ( ! currentCapBank->checkMaxDailyOpCountExceeded( pointChanges, ccEvents ) &&
                                         areOtherMonitorPointResponsesOk(point.getPointId(), currentCapBank, CtiCCCapBank::Close))
                                    {
                                        double controlValue = ( getStrategy()->getUnitType() != ControlStrategy::Volts )
                                                                   ? getCurrentVoltLoadPointValue()
                                                                   : getCurrentVarLoadPointValue();

                                        string text = createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Close, controlValue, getCurrentVarLoadPointValue());
                                        request = createDecreaseVarRequest(currentCapBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());

                                        updatePointResponsePreOpValues(currentCapBank);
                                        bestBank = currentCapBank;
                                    }
                                }
                            }
                            catch (NotFoundException& e)
                            {
                                CTILOG_WARN(dout, "PointResponse not found. CapBank: "<< currentCapBank->getPaoName() <<  " pointId: " << point.getPointId());
                            }
                        }
                        if (request != NULL)
                        {
                            bestBank = currentCapBank;
                            break;
                        }
                    }
                }
            }
            if (request == NULL)
            {
                CTILOG_WARN(dout, "No Banks Available to Close on Feeder: "<<getPaoName());
            }
        }
        else if ( point.getValue() > upperBound )
        {
            //1.  First check this point's parent bank to see if we can open it.
            CtiCCCapBankPtr parentBank = getMonitorPointParentBank(point);
            if (parentBank != NULL)
            {
                if ( parentBank->isSwitched() &&
                        ! parentBank->getDisableFlag() &&
                        ( parentBank->getControlStatus() == CtiCCCapBank::Close ||
                          parentBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ) )
                {
                    try
                    {
                        PointResponse pResponse = parentBank->getPointResponse(point);

                        const double value = point.getValue() - pResponse.getDelta();

                        if ( ( lowerBound <= value && value <= upperBound )
                                || value > lowerBound )
                        {
                            CTILOG_INFO(dout, "Attempting to Decrease Voltage on Feeder: "<<getPaoName()<<" CapBank: "<<parentBank->getPaoName());

                            if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                            {
                                CTILOG_DEBUG(dout, "MULTIVOLT: MonitorPoint->bankID/pointID: "<<point.getDeviceId()<<"/"<<point.getPointId()<<" Parent CapBank: "<<parentBank->getPaoName() <<" selected to Open");
                            }
                            //Check other monitor point responses using this potential capbank
                            if ( ! parentBank->checkMaxDailyOpCountExceeded( pointChanges, ccEvents ) &&
                                 areOtherMonitorPointResponsesOk(point.getPointId(), parentBank, CtiCCCapBank::Open))
                            {
                                double controlValue = ( getStrategy()->getUnitType() != ControlStrategy::Volts )
                                                           ? getCurrentVoltLoadPointValue()
                                                           : getCurrentVarLoadPointValue();

                                string text = createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Open, controlValue, getCurrentVarLoadPointValue());
                                request = createIncreaseVarRequest(parentBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());

                                updatePointResponsePreOpValues(parentBank);
                                bestBank = parentBank;
                            }
                            else
                            {
                                parentBank = NULL;
                            }
                        }
                        else
                        {
                            parentBank = NULL;
                        }
                    }
                    catch (NotFoundException& e)
                    {
                        CTILOG_WARN(dout, "PointResponse not found. CapBank: "<< parentBank->getPaoName() <<  " pointId: " << point.getPointId());
                    }
                }
                else
                {
                    parentBank = NULL;
                }
            }

            //2.  If parent bank won't work, start checking other banks...
            if (parentBank == NULL || request == NULL)
            {
                for ( CtiCCCapBankPtr currentCapBank : _cccapbanks )
                {
                    if ( currentCapBank->isSwitched() &&
                            ! currentCapBank->getDisableFlag() &&
                            ( currentCapBank->getControlStatus() == CtiCCCapBank::Close ||
                              currentCapBank->getControlStatus() == CtiCCCapBank::CloseQuestionable ) )
                    {
                        if (point.getDeviceId() != currentCapBank->getPaoId())
                        {
                            try
                            {
                                PointResponse pResponse = currentCapBank->getPointResponse(point);

                                const double value = point.getValue() - pResponse.getDelta();

                                if ( ( lowerBound <= value && value <= upperBound )
                                        || value > lowerBound
                                        || pResponse.getDelta() == 0 )
                                {
                                    CTILOG_INFO(dout, "Attempting to Decrease Voltage on Feeder: "<<getPaoName()<<" CapBank: "<<currentCapBank->getPaoName());

                                    if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                                    {
                                        CTILOG_DEBUG(dout, "MULTIVOLT: MonitorPoint->bankID/pointID: "<<point.getDeviceId()<<"/"<<point.getPointId()<<" CapBank: "<<currentCapBank->getPaoName() <<" selected to Open");
                                    }
                                    //Check other monitor point responses using this potential capbank
                                    if ( ! currentCapBank->checkMaxDailyOpCountExceeded( pointChanges, ccEvents ) &&
                                         areOtherMonitorPointResponsesOk(point.getPointId(), currentCapBank, CtiCCCapBank::Open))
                                    {
                                        double controlValue = ( getStrategy()->getUnitType() != ControlStrategy::Volts )
                                                                   ? getCurrentVoltLoadPointValue()
                                                                   : getCurrentVarLoadPointValue();

                                        string text = createTextString(getStrategy()->getControlMethod(), CtiCCCapBank::Open, controlValue, getCurrentVarLoadPointValue());
                                        request = createIncreaseVarRequest(currentCapBank, pointChanges, ccEvents, text, getCurrentVarLoadPointValue(), getPhaseAValue(), getPhaseBValue(), getPhaseCValue());

                                        updatePointResponsePreOpValues(currentCapBank);
                                        bestBank = currentCapBank;
                                    }
                                }
                            }
                            catch (NotFoundException& e)
                            {
                                CTILOG_WARN(dout, "PointResponse not found. CapBank: "<< currentCapBank->getPaoName() <<  " pointId: " << point.getPointId());
                            }
                        }
                        if (request != NULL)
                        {
                            bestBank = currentCapBank;
                            break;
                        }
                    }
                }
            }
            //3.  If there are no banks avail which put UpperBW > mp->value > LowerBW...just settle for bestFit..
            if (request == NULL)
            {
                CTILOG_INFO(dout, "MULTIVOLT: No Banks Available to Open on Feeder: "<<getPaoName());
            }
       }


        if( request != NULL )
        {
            pilMessages.push_back(request);
            setOperationSentWaitFlag(true);
            setLastCapBankControlledDeviceId( bestBank->getPaoId());
            setVarValueBeforeControl(getCurrentVarLoadPointValue());
            figureEstimatedVarLoadPointValue();
            if( getEstimatedVarLoadPointId() > 0 )
            {
                pointChanges.push_back(new CtiPointDataMsg(getEstimatedVarLoadPointId(),getEstimatedVarLoadPointValue(),NormalQuality,AnalogPointType));
            }

            retVal = true;
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    return retVal;

}

bool CtiCCFeeder::areOtherMonitorPointResponsesOk(long mPointID, CtiCCCapBank* potentialCap, int action)
{
    bool retVal = true;

    //action = 0 --> open
    //action = 1 --> close

    StrategyManager::SharedPtr  strategy;
    {
        /*
            I'm not sure how to handle this... the calling code for this function checks the bus strategy
                so I'm imitating this here.  If there is no strategy assigned to the bus, it defaults over
                to the feeder assigned strategy.  Normally this won't be an issue because the feeder
                strategy is cascaded down from the bus, but unintended things may happen with a differing
                strategy attached to the feeder.
        */
        CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
        CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

        if ( CtiCCSubstationBusPtr bus = store->findSubBusByPAObjectID( getParentId() ) )
        {
            strategy = bus->getStrategy();
        }

        if ( strategy->getUnitType() == ControlStrategy::None ) 
        {
            strategy = getStrategy();
        }
    }

    CtiTime Now;

    const bool   peakTime   = isPeakTime( Now );
    const double upperLimit = strategy->getUpperVoltLimit( peakTime );
    const double lowerLimit = strategy->getLowerVoltLimit( peakTime );

    for ( const CtiCCMonitorPointPtr otherPoint : _multipleMonitorPoints )
    {
        CtiCCCapBankPtr otherBank = getMonitorPointParentBank( *otherPoint );

        if ( otherBank &&
             eligibleForVoltageControl( *otherBank ) &&
              ( otherPoint->getPointId() != mPointID ) )
        {
            for ( const PointResponse & pResponse : potentialCap->getPointResponses() )
            {
                if (otherPoint->getPointId() == pResponse.getPointId())
                {
                    if (action) //CLOSE
                    {
                        const double upperBound =
                            otherPoint->getOverrideStrategy()
                                ? otherPoint->getUpperBandwidth()
                                : upperLimit;

                        if ( pResponse.getDelta() != 0
                                && otherPoint->getValue() + pResponse.getDelta() > upperBound )
                        {
                            CTILOG_INFO( dout,
                                         "OPERATION CANCELLED: Other Monitor Point Voltages will be overly affected on Feeder: "
                                            << getPaoName() << " CapBank: " << potentialCap->getPaoName()
                                            << " MULTIVOLT: otherPoint: " << otherPoint->getPointId() << " " << otherPoint->getDeviceId()
                                            << " Value: " << otherPoint->getValue() << " Delta: " << pResponse.getDelta()
                                            << " pResponse: " << pResponse.getPointId() << " " << pResponse.getDeviceId() );
                            retVal = false;
                            break;
                        }
                        else
                        {
                            retVal = true;
                        }
                    }
                    else // OPEN
                    {
                        const double lowerBound =
                            otherPoint->getOverrideStrategy()
                                ? otherPoint->getLowerBandwidth()
                                : lowerLimit;

                        if ( pResponse.getDelta() != 0
                                && otherPoint->getValue() - pResponse.getDelta() < lowerBound )
                        {
                            CTILOG_INFO( dout,
                                         "Attention - Other Monitor Point Voltages will be overly affected on Feeder: "
                                            << getPaoName() << " CapBank: " << potentialCap->getPaoName()
                                            << " MULTIVOLT: otherPoint: " << otherPoint->getPointId() << " " << otherPoint->getDeviceId()
                                            << " Value: " << otherPoint->getValue() << " Delta: " << pResponse.getDelta()
                                            << " pResponse: " << pResponse.getPointId() << " " << pResponse.getDeviceId() );
                        }
                        else
                        {
                            retVal = true;
                        }
                    }
                }
            }
            if (retVal == false)
            {
                break;
            }
        }
    }

    return retVal;
}

bool CtiCCFeeder::areAllMonitorPointsInVoltageRange(CtiCCMonitorPointPtr & oorPoint)
{
    try
    {
        StrategyManager::SharedPtr  strategy;
        {
            /*
                I'm not sure how to handle this... the calling code for this function checks the bus strategy
                    so I'm imitating this here.  If there is no strategy assigned to the bus, it defaults over
                    to the feeder assigned strategy.  Normally this won't be an issue because the feeder
                    strategy is cascaded down from the bus, but unintended things may happen with a differing
                    strategy attached to the feeder.
            */
            CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
            CTILOCKGUARD( CtiCriticalSection, guard, store->getMux() );

            if ( CtiCCSubstationBusPtr bus = store->findSubBusByPAObjectID( getParentId() ) )
            {
                strategy = bus->getStrategy();
            }

            if ( strategy->getUnitType() == ControlStrategy::None ) 
            {
                strategy = getStrategy();
            }
        }

        CtiTime Now;

        const bool   peakTime   = isPeakTime( Now );
        const double upperLimit = strategy->getUpperVoltLimit( peakTime );
        const double lowerLimit = strategy->getLowerVoltLimit( peakTime );

        for ( CtiCCMonitorPointPtr point : _multipleMonitorPoints )
        {
            CtiCCCapBankPtr bank = getMonitorPointParentBank( *point );

            if ( bank && eligibleForVoltageControl( *bank ) )
            {
                double upperBound = upperLimit;
                double lowerBound = lowerLimit;

                if ( point->getOverrideStrategy() )
                {
                    upperBound = point->getUpperBandwidth();
                    lowerBound = point->getLowerBandwidth();
                }

                const double pointValue = point->getValue();

                if ( lowerBound <= pointValue && pointValue <= upperBound )
                {
                    if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
                    {
                        CTILOG_DEBUG( dout,
                                      "MULTIVOLT: Monitor Point: " << point->getPointId() << " on CapBank: "
                                        << point->getDeviceId() << " is inside limits.  Current value: "
                                        << pointValue
                                        << " - Limits: [" << lowerBound << ", " << upperBound << "]" ); 
                    }
                }
                else if ( pointValue < lowerBound )
                {
                    CTILOG_WARN( dout,
                                 "Monitor Point: " << point->getPointId() << " on CapBank: " << point->getDeviceId()
                                    << " is BELOW limit.  Current value: " << pointValue
                                    << " - Limits: [" << lowerBound << ", " << upperBound << "]" ); 

                    // Record the first BELOW limit point we've found, but don't return until we've determined that
                    //  there are no ABOVE limit points.
                    if ( ! oorPoint )
                    {
                        oorPoint = point;
                    }
                }
                else    // pointValue > upperBound
                {
                    CTILOG_WARN( dout,
                                 "Monitor Point: " << point->getPointId() << " on CapBank: " << point->getDeviceId()
                                    << " is ABOVE limit.  Current value: " << pointValue
                                    << " - Limits: [" << lowerBound << ", " << upperBound << "]" ); 

                    // Return the first point that we find that is ABOVE the lower limit immediately, regardless
                    //  if we've found a BELOW the limit point.
                    oorPoint = point;
                    break;
                }
            }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return ! oorPoint;
}

void CtiCCFeeder::updatePointResponsePreOpValues(CtiCCCapBank* capBank)
{
    CTILOG_INFO( dout, "Updating PreOpValue for CapBank: " << capBank->getPaoName() << " - has " << capBank->getPointResponses().size() << " point responses." );

    for ( const auto & point : _multipleMonitorPoints )
    {
        double      value = point->getValue();
        std::string ID    = point->getIdentifier();

        try
        {
            if ( 110.0 < value && value < 130.0 )
            {
                CTILOG_INFO( dout, ID << " -- PreOpValue: " << value );
            }
            else
            {
                CTILOG_WARN( dout, ID << " -- PreOpValue: " << value << " -- outside valid voltage range." );
            }

            capBank->updatePointResponsePreOpValue( point->getPointId(), value );
        }
        catch (NotFoundException& e)
        {
            CTILOG_WARN(dout, "Error Updating PreOpValue for deltas. PointId not found: " << point->getPointId());
        }
    }
}


void CtiCCFeeder::updatePointResponseDeltas()
{

    try
    {

        for(long i=0;i<_cccapbanks.size();i++)
        {
           CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[i];

           if (currentCapBank->getPaoId() == getLastCapBankControlledDeviceId())
           {
               if (_CC_DEBUG & CC_DEBUG_MULTIVOLT)
               {
                   CTILOG_DEBUG(dout, "MULTIVOLT: Updating POINT RESPONSE DELTAS for CapBank: " <<currentCapBank->getPaoName());
               }
               for (int j = 0; j < _multipleMonitorPoints.size(); j++)
               {
                    const CtiCCMonitorPoint & point = *_multipleMonitorPoints[j];
                    try
                    {
                       currentCapBank->updatePointResponseDelta(point, getStrategy()->getMaximumDeltaVoltage());
                    }
                    catch (NotFoundException& e)
                    {
                        CTILOG_WARN(dout, "Error Updating delta value. PointId not found: " << point.getPointId());
                    }
               }
               break;
           }
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

bool CtiCCFeeder::areAllMonitorPointsNewEnough(const CtiTime& currentDateTime)
{
    bool retVal = false;

    try
    {
        if ( isScanFlagSet() && currentDateTime.seconds() >= getLastOperationTime().seconds() + (_SCAN_WAIT_EXPIRE *60) )  //T1 Expired.. Force Process
        {
            for ( CtiCCMonitorPointPtr point : _multipleMonitorPoints )
            {
                point->setScanInProgress( false );
            }
            retVal = true;
        }
        else
        {
            for ( CtiCCMonitorPointPtr point : _multipleMonitorPoints )
            {
                CtiCCCapBankPtr bank = getMonitorPointParentBank( *point );

                if ( bank && eligibleForVoltageControl( *bank ) )
                {
                    if ( point->getTimeStamp() > ( getLastOperationTime() - 30 )
                            && point->getTimeStamp() >= ( currentDateTime - ( 60 * _POINT_AGE ) ) )
                    {
                        retVal = true;
                        point->setScanInProgress( false );
                    }
                    else
                    {
                        retVal = false;
                        break;
                    }
                }
            }
        }

        if ( retVal && ( _CC_DEBUG & CC_DEBUG_MULTIVOLT ) )
        {
            CTILOG_DEBUG(dout, "ALL MONITOR POINTS ARE NEW ENOUGH on Feeder: " <<getPaoName());
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
    return retVal;
}

bool CtiCCFeeder::isScanFlagSet()
{
    return (_preOperationMonitorPointScanFlag || _postOperationMonitorPointScanFlag);
}

bool CtiCCFeeder::scanAllMonitorPoints()
{
    // gah - sorted_vector -> vector
    std::vector<CtiCCCapBankPtr>    banks{ _cccapbanks.begin(), _cccapbanks.end() };
    
    return issueTwoWayScans( _multipleMonitorPoints, banks );
}

void CtiCCFeeder::getSpecializedPointRegistrationIds( std::set<long> & registrationIDs ) const
{
    Conductor::getSpecializedPointRegistrationIds( registrationIDs );
}

/*---------------------------------------------------------------------------
    dumpDynamicData

    Writes out the dynamic information for this cc feeder.
---------------------------------------------------------------------------*/
void CtiCCFeeder::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    writeDynamicData( conn, currentDateTime );

    getOriginalParent().dumpDynamicData(conn, currentDateTime);
    getOperationStats().dumpDynamicData(conn, currentDateTime);
}

std::string CtiCCFeeder::formatFlags() const
{
    std::string flags( 20, 'N' );

    flags[  0 ] = populateFlag( _verificationFlag );
    flags[  1 ] = populateFlag( _performingVerificationFlag );
    flags[  2 ] = populateFlag( _verificationDoneFlag );
    flags[  3 ] = populateFlag( _preOperationMonitorPointScanFlag );
    flags[  4 ] = populateFlag( _operationSentWaitFlag );
    flags[  5 ] = populateFlag( _postOperationMonitorPointScanFlag );
    flags[  6 ] = populateFlag( _waitForReCloseDelayFlag );
    flags[  7 ] = populateFlag( _peakTimeFlag );
    flags[  8 ] = populateFlag( _maxDailyOpsHitFlag );
    flags[  9 ] = populateFlag( _ovUvDisabledFlag );
    flags[ 10 ] = populateFlag( _correctionNeededNoBankAvailFlag );
    flags[ 11 ] = populateFlag( _likeDayControlFlag );
    flags[ 12 ] = populateFlag( _lastVerificationMsgSentSuccessful );

    return flags;
}

bool CtiCCFeeder::updateDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime )
{
    static const std::string sql =
        "UPDATE "
            "dynamicccfeeder "
        "SET "
            "currentvarpointvalue = ?, currentwattpointvalue = ?, "
            "newpointdatareceivedflag = ?, lastcurrentvarupdatetime = ?, "
            "estimatedvarpointvalue = ?, currentdailyoperations = ?, "
            "recentlycontrolledflag = ?, lastoperationtime = ?, "
            "varvaluebeforecontrol = ?, lastcapbankdeviceid = ?, "
            "busoptimizedvarcategory = ?, busoptimizedvaroffset = ?, "
            "ctitimestamp = ?, powerfactorvalue = ?, kvarsolution = ?, "
            "estimatedpfvalue = ?, currentvarpointquality = ?, waivecontrolflag = ?, "
            "additionalflags = ?, currentvoltpointvalue = ?, eventseq = ?, "
            "currverifycbid = ?, currverifycborigstate = ?, currentwattpointquality = ?, "
            "currentvoltpointquality = ?, ivcontroltot = ?, ivcount = ?, "
            "iwcontroltot = ?, iwcount = ?, phaseavalue = ?, phasebvalue = ?, "
            "phasecvalue = ?, lastwattpointtime = ?, lastvoltpointtime = ?, "
            "retryindex = ?, phaseavaluebeforecontrol = ?, "
            "phasebvaluebeforecontrol = ?, phasecvaluebeforecontrol = ? "
        "WHERE "
            "feederid = ?";

    Cti::Database::DatabaseWriter writer( conn, sql );

    writer
        << getRawCurrentVarLoadPointValue()
        << getRawCurrentWattLoadPointValue()
        << serializeFlag( getNewPointDataReceivedFlag() )
        << getLastCurrentVarPointUpdateTime()
        << getEstimatedVarLoadPointValue()
        << getCurrentDailyOperations()
        << serializeFlag( getRecentlyControlledFlag() )
        << getLastOperationTime()
        << getVarValueBeforeControl()
        << _lastcapbankcontrolleddeviceid
        << _busoptimizedvarcategory
        << _busoptimizedvaroffset
        << currentDateTime
        << getPowerFactorValue()
        << getKVARSolution()
        << getEstimatedPowerFactorValue()
        << getCurrentVarPointQuality()
        << serializeFlag( getWaiveControlFlag() )
        << formatFlags()
        << getRawCurrentVoltLoadPointValue()
        << getEventSequence()
        << _currentVerificationCapBankId
        << getCurrentVerificationCapBankOrigState()
        << getCurrentWattPointQuality()
        << getCurrentVoltPointQuality()
        << getIVControlTot()
        << getIVCount()
        << getIWControlTot()
        << getIWCount()
        << getPhaseAValue()
        << getPhaseBValue()
        << getPhaseCValue()
        << getLastWattPointTime()
        << getLastVoltPointTime()
        << _retryIndex
        << getPhaseAValueBeforeControl()
        << getPhaseBValueBeforeControl()
        << getPhaseCValueBeforeControl()
        << getPaoId();

    return Cti::Database::executeCommand( writer, CALLSITE );
}

bool CtiCCFeeder::insertDynamicData( Cti::Database::DatabaseConnection & conn, CtiTime & currentDateTime )
{
    static const std::string sql =
        "INSERT INTO "
            "dynamicccfeeder "
        "VALUES ( "
              "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
              "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
              "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
              "?, ?, ?, ?, ?, ?, ?, ?, ?)";

    CTILOG_INFO( dout, "Inserted Feeder into DynamicCCFeeder: " << getPaoName() );

    Cti::Database::DatabaseWriter writer( conn, sql );

    writer
        << getPaoId()
        << getRawCurrentVarLoadPointValue()
        << getRawCurrentWattLoadPointValue()
        << serializeFlag( getNewPointDataReceivedFlag() )
        << getLastCurrentVarPointUpdateTime()
        << getEstimatedVarLoadPointValue()
        << getCurrentDailyOperations()
        << serializeFlag( getRecentlyControlledFlag() )
        << getLastOperationTime()
        << getVarValueBeforeControl()
        << _lastcapbankcontrolleddeviceid
        << _busoptimizedvarcategory
        << _busoptimizedvaroffset
        << currentDateTime
        << getPowerFactorValue()
        << getKVARSolution()
        << getEstimatedPowerFactorValue()
        << getCurrentVarPointQuality()
        << serializeFlag( getWaiveControlFlag() )
        << formatFlags()
        << getRawCurrentVoltLoadPointValue()
        << getEventSequence()
        << _currentVerificationCapBankId
        << getCurrentVerificationCapBankOrigState()
        << getCurrentWattPointQuality()
        << getCurrentVoltPointQuality()
        << getIVControlTot()
        << getIVCount()
        << getIWControlTot()
        << getIWCount()
        << getPhaseAValue()
        << getPhaseBValue()
        << getPhaseCValue()
        << getLastWattPointTime()
        << getLastVoltPointTime()
        << _retryIndex
        << getPhaseAValueBeforeControl()
        << getPhaseBValueBeforeControl()
        << getPhaseCValueBeforeControl();

    return Cti::Database::executeCommand( writer, CALLSITE, Cti::Database::LogDebug( _CC_DEBUG & CC_DEBUG_DATABASE ) );
}

/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiCCFeeder* CtiCCFeeder::replicate() const
{
    CtiCCFeederPtr newFeeder = new CtiCCFeeder( *this );

    // The banks are currently owned by both feeders due to the shallow
    //  default copy semantics. We need to replicate the banks in the new
    //  feeder and overwrite the contents of the collection.

    std::transform( newFeeder->_cccapbanks.begin(), newFeeder->_cccapbanks.end(),
                    newFeeder->_cccapbanks.begin(),
                    [ & ]( auto bank )
                    {
                        return bank->replicate();
                    } ); 

    return newFeeder;
}

void CtiCCFeeder::setDynamicData(Cti::RowReader& rdr)
{
    std::string flags;

    rdr["LastCapBankDeviceID"]      >> _lastcapbankcontrolleddeviceid;
    rdr["BusOptimizedVarCategory"]  >> _busoptimizedvarcategory;
    rdr["BusOptimizedVarOffset"]    >> _busoptimizedvaroffset;

    rdr["AdditionalFlags"]          >> flags;

    _verificationFlag                   = deserializeFlag( flags,  0 );
    _performingVerificationFlag         = deserializeFlag( flags,  1 );
    _verificationDoneFlag               = deserializeFlag( flags,  2 );
    _preOperationMonitorPointScanFlag   = deserializeFlag( flags,  3 );
    _operationSentWaitFlag              = deserializeFlag( flags,  4 );
    _postOperationMonitorPointScanFlag  = deserializeFlag( flags,  5 );
    _waitForReCloseDelayFlag            = deserializeFlag( flags,  6 );
    _peakTimeFlag                       = deserializeFlag( flags,  7 );
    _maxDailyOpsHitFlag                 = deserializeFlag( flags,  8 );
    _ovUvDisabledFlag                   = deserializeFlag( flags,  9 );
    _correctionNeededNoBankAvailFlag    = deserializeFlag( flags, 10 );
    _likeDayControlFlag                 = deserializeFlag( flags, 11 );
    _lastVerificationMsgSentSuccessful  = deserializeFlag( flags, 12 );

    rdr["CurrVerifyCBId"]           >> _currentVerificationCapBankId;

    rdr["retryIndex"]               >> _retryIndex;
}

/*---------------------------------------------------------------------------
    doubleToString

    Returns the string representation of a double
---------------------------------------------------------------------------*/
string CtiCCFeeder::doubleToString(double doubleVal, long decimalPlaces)
{
    char tempchar[80] = "";
    string retString = string("");
    _snprintf(tempchar,80,"%.*f",decimalPlaces, doubleVal);
    retString += tempchar;

    return retString;
}

void CtiCCFeeder::deleteCCCapBank(long capBankId)
{
    CtiCCCapBank_SVector& ccCapBanks = getCCCapBanks();
    CtiCCCapBank_SVector::iterator itr = ccCapBanks.begin();
    while(itr != ccCapBanks.end())
    {
        CtiCCCapBank *capBank = *itr;
        if (capBank->getPaoId() == capBankId)
        {
            itr = getCCCapBanks().erase(itr);
            break;
        }else
            ++itr;

    }
    return;
}

bool CtiCCFeeder::checkMaxDailyOpCountExceeded(CtiMultiMsg_vec& pointChanges)
{
    bool retVal = false;
    if( getStrategy()->getMaxDailyOperation() > 0 &&
        ( getCurrentDailyOperations() == getStrategy()->getMaxDailyOperation()  ||
         (!getMaxDailyOpsHitFlag() && getCurrentDailyOperations() > getStrategy()->getMaxDailyOperation()) ) )//only send once
    {

        string text = ("Feeder Exceeded Max Daily Operations");
        string additional = "Feeder: " + getPaoName() + formatMapInfo( this );

        if (getDailyOperationsAnalogPointId() > 0 && !getMaxDailyOpsHitFlag())
        {
            CtiSignalMsg* pSig = new CtiSignalMsg(getDailyOperationsAnalogPointId(),5,text,additional,CapControlLogType, _MAXOPS_ALARM_CATID, "cap control",
                                                                                TAG_ACTIVE_ALARM /*tags*/, 0 /*pri*/, 0 /*millis*/, getCurrentDailyOperations() );
            pSig->setCondition(CtiTablePointAlarming::highReasonability);
            pointChanges.push_back(pSig);

        }
        setMaxDailyOpsHitFlag(true);

        retVal = true;
    }

    if( getStrategy()->getMaxOperationDisableFlag() && getMaxDailyOpsHitFlag() )
    {
        bool endOfDayOverride = false;

        if (getStrategy()->getEndDaySettings().compare("Trip") == 0)
        {
            endOfDayOverride = CtiCCSubstationBusStore::getInstance()->isAnyBankClosed(getPaoId(),Cti::CapControl::Feeder);
        }
        else if (getStrategy()->getEndDaySettings().compare("Close") == 0)
        {
            endOfDayOverride = CtiCCSubstationBusStore::getInstance()->isAnyBankOpen(getPaoId(),Cti::CapControl::Feeder);
        }

        if (endOfDayOverride == false)
        {
            string text = string("Feeder Disabled");
            string additional = "Feeder: " + getPaoName() + formatMapInfo( this );

            if (getDailyOperationsAnalogPointId() > 0)
            {
                CtiSignalMsg* pSig = new CtiSignalMsg(getDailyOperationsAnalogPointId(),5,text,additional,CapControlLogType, _MAXOPS_ALARM_CATID, "cap control",
                                                                                    TAG_ACTIVE_ALARM /*tags*/, 0 /*pri*/, 0 /*millis*/, getCurrentDailyOperations() );
                pSig->setCondition(CtiTablePointAlarming::highReasonability);
                pointChanges.push_back(pSig);
            }
            CtiCCSubstationBusStore::getInstance()->UpdatePaoDisableFlagInDB(this, true);
            retVal = false;
        }
    }

    return retVal;
}

string CtiCCFeeder::createPhaseVarText(double aValue,double bValue, double cValue, float multiplier)
{
    string text = ("");
    text += CtiNumStr(aValue*multiplier, 2).toString();
    text += " : ";
    text += CtiNumStr(bValue*multiplier, 2).toString();
    text += " : ";
    text += CtiNumStr(cValue*multiplier, 2).toString();
    text += " : ";
    text += CtiNumStr((aValue+bValue+cValue)*multiplier, 2).toString();
    return text;
}
string CtiCCFeeder::createPhaseRatioText(double aValue,double bValue, double cValue, float multiplier)
{
    string text = ("");
    text += CtiNumStr(aValue*multiplier, 2).toString();
    text += " : ";
    text += CtiNumStr(bValue*multiplier, 2).toString();
    text += " : ";
    text += CtiNumStr(cValue*multiplier, 2).toString();
    text += " : ";
    double totalRatio = (aValue+bValue+cValue) / 3;
    text += CtiNumStr(totalRatio*multiplier, 2).toString();
    return text;
}

string CtiCCFeeder::createVarText(double aValue,float multiplier)
{
    string text = ("");
    text += CtiNumStr(aValue*multiplier, 2).toString();
    return text;
}

string CtiCCFeeder::createPhaseControlStatusUpdateText(string capControlStatus, double varAValue,double varBValue, double varCValue,
                                                  double ratioA, double ratioB, double ratioC)
{
    string text = ("");

    text = string("Var: ");
    text += CtiNumStr(varAValue, 2).toString();
    text += "/";
    text += CtiNumStr(varBValue, 2).toString();
    text += "/";
    text += CtiNumStr(varCValue, 2).toString();
    text += "  ( ";
    text += CtiNumStr(ratioA*100.0, 2).toString();
    text += "% / ";
    text += CtiNumStr(ratioB*100.0, 2).toString();
    text += "% / ";
    text += CtiNumStr(ratioC*100.0, 2).toString();

    text += "% change), ";
    text += capControlStatus;

    return text;
}

string CtiCCFeeder::createControlStatusUpdateText(string capControlStatus, double varAValue, double ratioA)
{
    string text = ("");
    text = string("Var: ");
    text += CtiNumStr(varAValue, 2).toString();
    text += "  ( ";
    text += CtiNumStr(ratioA*100.0, 2).toString();
    text += "% change), ";
    text += capControlStatus;

    return text;
}
string CtiCCFeeder::createTextString(const string& controlMethod, int control, double controlValue, double monitorValue)//, long decimalPlaces)
{
    string text = ("");
    switch (control)
    {
        case 0:
            {
                text += "Open Sent, ";
            }
            break;
        case 1:
            {
                text += "Close Sent, ";
            }
            break;
        case 2:
            {
                text += "Resending Open, ";
            }
            break;
        case 3:
            {
                text += "Resending Close, ";
            }
            break;
        case 4:
            {
                text += "Flip Sent, ";
            }
        default:
            break;

    }
    if (ciStringEqual(getParentControlUnits(),ControlStrategy::VoltsControlUnit))
    {
        if (ciStringEqual(controlMethod,ControlStrategy::SubstationBusControlMethod))
            text += "SubBus-Volt: ";
        else if (ciStringEqual(controlMethod,ControlStrategy::BusOptimizedFeederControlMethod))
            text += "BusOp-Volt: ";
        else if (ciStringEqual(controlMethod,ControlStrategy::IndividualFeederControlMethod))
            text += "IndvFdr-Volt: ";
        else if (ciStringEqual(controlMethod,ControlStrategy::ManualOnlyControlMethod))
            text += "Manual-Volt: ";
        else if (ciStringEqual(controlMethod,ControlStrategy::TimeOfDayControlMethod))
            text += "TimeOfDay-Volt: ";
        else
            text += "No Method Defined? Volt: ";
        text += doubleToString(controlValue, getDecimalPlaces());
        text += "/Var: ";
        text += doubleToString(monitorValue, getDecimalPlaces());
    }
    else if (ciStringEqual(getParentControlUnits(), ControlStrategy::KVarControlUnit) ||
             ciStringEqual(getParentControlUnits(), ControlStrategy::PFactorKWKVarControlUnit) ||
             ciStringEqual(getParentControlUnits(), ControlStrategy::PFactorKWKQControlUnit))
    {
        if (ciStringEqual(controlMethod,ControlStrategy::SubstationBusControlMethod))
        {
            text += "SubBus-Var: ";
            text += doubleToString(controlValue, getDecimalPlaces());
            text += "/Var: ";
            CtiCCSubstationBusPtr bus = CtiCCSubstationBusStore::getInstance()->findSubBusByPAObjectID(getParentId());
            if (bus != NULL)
            {
                if (bus->getUsePhaseData())
                {
                    text += doubleToString(bus->getPhaseAValue(), getDecimalPlaces());
                    text += " : ";
                    text += doubleToString(bus->getPhaseBValue(), getDecimalPlaces());
                    text += " : ";
                    text += doubleToString(bus->getPhaseCValue(), getDecimalPlaces());
                }
                else
                    text += doubleToString(monitorValue, getDecimalPlaces());
            }
            else
                text += doubleToString(monitorValue, getDecimalPlaces());
        }

        else if (ciStringEqual(controlMethod,ControlStrategy::BusOptimizedFeederControlMethod))
        {
            text += "BusOp-Var: ";
            text += doubleToString(controlValue, getDecimalPlaces());
            text += "/Var: ";
            if (getUsePhaseData())
            {
                text += doubleToString(getPhaseAValue(), getDecimalPlaces());
                text += " : ";
                text += doubleToString(getPhaseBValue(), getDecimalPlaces());
                text += " : ";
                text += doubleToString(getPhaseCValue(), getDecimalPlaces());
            }
            else
                text += doubleToString(monitorValue, getDecimalPlaces());

        }
        else if (ciStringEqual(controlMethod,ControlStrategy::IndividualFeederControlMethod))
        {
            text += "IndvFdr-Var: ";
            text += doubleToString(controlValue, getDecimalPlaces());
            text += "/Var: ";
            if (getUsePhaseData())
            {
                text += doubleToString(getPhaseAValue(), getDecimalPlaces());
                text += " : ";
                text += doubleToString(getPhaseBValue(), getDecimalPlaces());
                text += " : ";
                text += doubleToString(getPhaseCValue(), getDecimalPlaces());
            }
            else
                text += doubleToString(monitorValue, getDecimalPlaces());
        }
        else if (ciStringEqual(controlMethod,ControlStrategy::ManualOnlyControlMethod))
        {
            text += "Manual-Var: ";
            text += doubleToString(controlValue, getDecimalPlaces());
            text += "/Var: ";
            text += doubleToString(monitorValue, getDecimalPlaces());
        }
        else if (ciStringEqual(controlMethod,ControlStrategy::TimeOfDayControlMethod))
        {
            text += "TimeOfDay-Var: ";
            text += doubleToString(controlValue, getDecimalPlaces());
            text += "/Var: ";
            text += doubleToString(monitorValue, getDecimalPlaces());
        }
        else
        {
            text += "No Method Defined? Var:";
            text += doubleToString(controlValue, getDecimalPlaces());
            text += "/Var: ";
            text += doubleToString(monitorValue, getDecimalPlaces());
        }
    }
    else if (ciStringEqual(getParentControlUnits(), ControlStrategy::MultiVoltControlUnit))
    {
        if (ciStringEqual(controlMethod, ControlStrategy::SubstationBusControlMethod))
            text += "SubBus-MultiVolt: ";
        else if (ciStringEqual(controlMethod, ControlStrategy::BusOptimizedFeederControlMethod))
            text += "BusOp-MultiVolt: ";
        else if (ciStringEqual(controlMethod, ControlStrategy::IndividualFeederControlMethod))
            text += "IndvFdr-MultiVolt: ";
        else if (ciStringEqual(controlMethod, ControlStrategy::ManualOnlyControlMethod))
            text += "Manual-MultiVolt: ";
        else if (ciStringEqual(controlMethod,ControlStrategy::TimeOfDayControlMethod))
            text += "TimeOfDay-MultiVolt: ";
        else
            text += "No Method Defined? MultiVolt: ";
        text += doubleToString(controlValue, getDecimalPlaces());
        text += "/Var: ";
        text += doubleToString(monitorValue, getDecimalPlaces());
    }
    if (ciStringEqual(getParentControlUnits(),ControlStrategy::IntegratedVoltVarControlUnit))
    {
        text += "IVVC: ";
        text += doubleToString(controlValue, getDecimalPlaces());
        text += "/Var: ";
        text += doubleToString(monitorValue, getDecimalPlaces());
    }
    else
    {
        if (ciStringEqual(controlMethod,ControlStrategy::TimeOfDayControlMethod))
        {
            text += "TimeOfDay-Var: ";
            text += doubleToString(controlValue, getDecimalPlaces());
            text += "/Var: ";
            text += doubleToString(monitorValue, getDecimalPlaces());
        }
    }
    return text;

}


CtiCCCapBank* CtiCCFeeder::getMonitorPointParentBank(const CtiCCMonitorPoint & point)
{

    for (long i = 0; i < _cccapbanks.size(); i++)
    {
        CtiCCCapBankPtr cap = (CtiCCCapBankPtr)_cccapbanks[i];
        if (point.getDeviceId() == cap->getPaoId())
        {
            return cap;
        }
    }
    return NULL;
}

bool CtiCCFeeder::checkForAndProvideNeededFallBackControl(const CtiTime& currentDateTime,
                        CtiMultiMsg_vec& pointChanges, EventLogEntries &ccEvents, CtiMultiMsg_vec& pilMessages)
{
    bool retVal = false;
    CtiRequestMsg* request = NULL;

    map <long, long> controlid_action_map;
    controlid_action_map.clear();

    CtiTime lastSendTime = getLastOperationTime();
    if (getCurrentVarLoadPointId() &&
        getLastOperationTime() < getLastCurrentVarPointUpdateTime())
    {
        lastSendTime = getLastCurrentVarPointUpdateTime();
        setLastOperationTime(currentDateTime);
    }
    int fallBackConst = 86400;
    //if (currentDateTime.tm_wday)
    {
        struct tm *ctm= new struct tm();
        currentDateTime.extract(ctm);
        if (ctm->tm_wday == 0 || ctm->tm_wday == 6)
        {
            fallBackConst = 604800;
        }
        else if (ctm->tm_wday == 1)
        {
            fallBackConst = 259200;
        }
        else
            fallBackConst = 86400;
        delete ctm;

    }
    CtiCCSubstationBusStore::getInstance()->reloadMapOfBanksToControlByLikeDay(0, getPaoId(), &controlid_action_map,lastSendTime, fallBackConst);
    std::map <long, long>::iterator iter = controlid_action_map.begin();
    while (iter != controlid_action_map.end())
    {
        {
            int capCount = 0;
            long ptId = iter->first;

            PointIdToCapBankMultiMap::iterator bankIter, end;
            if (CtiCCSubstationBusStore::getInstance()->findCapBankByPointID(iter->first, bankIter, end))
            {
                CtiCCCapBankPtr bank = bankIter->second;
                if (bank->getParentId() == getPaoId())
                {
                    request = createForcedVarRequest(bank, pointChanges, ccEvents, iter->second, "LikeDay Control");

                    if( request != NULL )
                    {
                        createForcedVarConfirmation(bank, pointChanges, ccEvents, "LikeDay Control");

                        retVal = true;
                        pilMessages.push_back(request);
                        setLastOperationTime(currentDateTime);
                        setCurrentDailyOperationsAndSendMsg(getCurrentDailyOperations() + 1, pointChanges);
                    }
                }
            }
        }
        iter = controlid_action_map.erase(iter);
    }

    return retVal;
}

bool CtiCCFeeder::isDataOldAndFallBackNecessary(string controlUnits)
{
    bool retVal = false;
    CtiTime timeNow = CtiTime();
    string feederControlUnits = controlUnits;

    if (!ciStringEqual(getStrategy()->getStrategyName(),"(none)"))
    {
        feederControlUnits = getStrategy()->getControlUnits();
    }

    if (!getDisableFlag())
    {
        if (getStrategy()->getLikeDayFallBack())
        {
            if ( ciStringEqual(feederControlUnits, ControlStrategy::VoltsControlUnit))
            {
                 if ((getCurrentVoltLoadPointId() && getCurrentVarLoadPointId()) &&
                     (timeNow > getLastVoltPointTime() + _LIKEDAY_OVERRIDE_TIMEOUT ||
                      timeNow > getLastCurrentVarPointUpdateTime() + _LIKEDAY_OVERRIDE_TIMEOUT))
                {
                     retVal = true;
                }
            }
            else if ( ciStringEqual(feederControlUnits, ControlStrategy::PFactorKWKVarControlUnit))
            {
                 if ((getCurrentWattLoadPointId() && getCurrentVarLoadPointId()) &&
                     (timeNow > getLastWattPointTime() + _LIKEDAY_OVERRIDE_TIMEOUT ||
                      timeNow > getLastCurrentVarPointUpdateTime() + _LIKEDAY_OVERRIDE_TIMEOUT))
                {
                     retVal = true;
                }
            }
            else
            {
                if (getCurrentVarLoadPointId() &&
                    timeNow > getLastCurrentVarPointUpdateTime() + _LIKEDAY_OVERRIDE_TIMEOUT)
                {
                    retVal = true;
                }
            }
        }
    }

    return retVal;
}

bool CtiCCFeeder::checkForRateOfChange(const CtiRegression& reg, const CtiRegression& regA, const CtiRegression& regB, const CtiRegression& regC)
{
    if(!_RATE_OF_CHANGE)
        return false;
    if( getUsePhaseData() && !getTotalizedControlFlag() )
    {
        if( regA.depthMet() && regB.depthMet() && regC.depthMet() )
            return true;
    }
    else
    {
        return reg.depthMet();
    }
    return false;
}

bool CtiCCFeeder::areAllPhasesSuccess(double ratioA, double ratioB, double ratioC, double confirmPercent)
{
    return ( isResponseSuccess(ratioA, confirmPercent) &&
             isResponseSuccess(ratioB, confirmPercent) &&
             isResponseSuccess(ratioC, confirmPercent) );
}
bool CtiCCFeeder::areAllPhasesQuestionable(double ratioA, double ratioB, double ratioC, double confirmPercent, double failPercent)
{
    return (isResponseQuestionable(ratioA, confirmPercent, failPercent) &&
            isResponseQuestionable(ratioB, confirmPercent, failPercent) &&
            isResponseQuestionable(ratioC, confirmPercent, failPercent) );

}
bool CtiCCFeeder::isResponseQuestionable(double ratio, double confirmPercent, double failPercent)
{
    return ( ! ( isResponseSuccess(ratio, confirmPercent) || isResponseFail(ratio, failPercent) ) );

}
bool CtiCCFeeder::isResponseFail(double ratio,double failPercent)
{
    return (ratio < failPercent * .01) ;
}
bool CtiCCFeeder::isResponseSuccess(double ratio,double confirmPercent)
{
    return (ratio >= confirmPercent * .01) ;

}
bool CtiCCFeeder::shouldCapBankBeFailed(double ratioA, double ratioB, double ratioC, double failPercent)
{
    bool retVal = false;
    long numOfFails = 0;
    retVal = isAnyPhaseFail(ratioA, ratioB, ratioC, failPercent, numOfFails);
    if( !_USE_PHASE_INDICATORS && numOfFails != 3)
    {
        //if _USE_PHASE_INDICATORS == false, all phases must be failed
        retVal = false;
    }
    return retVal;
}


bool CtiCCFeeder::isAnyPhaseFail(double ratioA, double ratioB, double ratioC, double failPercent, long &numFailedPhases)
{
    numFailedPhases = 0;

    if (isResponseFail(ratioA, failPercent))
    {
        numFailedPhases += 1;
    }
    if (isResponseFail(ratioB, failPercent))
    {
        numFailedPhases += 1;
    }
    if (isResponseFail(ratioC, failPercent))
    {
        numFailedPhases += 1;
    }

    return numFailedPhases != 0;
}

string CtiCCFeeder::getFailedPhasesString(double ratioA, double ratioB, double ratioC, double confirmPercent, double failPercent)
{
    string retStr = "";
    long numOfFail = 0;
    if( isAnyPhaseFail(ratioA, ratioB, ratioC, failPercent, numOfFail) )
    {
        if( numOfFail == 3)
        {
            retStr = "ABC";
        }
        else if( numOfFail == 2 )
        {
            if( isResponseFail(ratioA,failPercent) )
            {
                retStr += "A";
            }
            if( isResponseFail(ratioB,failPercent) )
            {
                retStr += "B";
            }
            if( isResponseFail(ratioC,failPercent) )
            {
                retStr += "C";
            }
        }
        else if( numOfFail == 1 )
        {
            if( isResponseSuccess(ratioA, confirmPercent) ||
                isResponseSuccess(ratioB, confirmPercent) ||
                isResponseSuccess(ratioC, confirmPercent) )
            {
                if( isResponseFail(ratioA,failPercent) )
                {
                    retStr += "A";
                }
                else if( isResponseFail(ratioB,failPercent) )
                {
                    retStr += "B";
                }
                else
                {
                    retStr += "C";
                }
            }
            else
            {
                if(isResponseQuestionable(ratioA, confirmPercent, failPercent))
                {
                    retStr += "A";
                }
                if(isResponseQuestionable(ratioB, confirmPercent, failPercent))
                {
                    retStr += "B";
                }
                if(isResponseQuestionable(ratioC, confirmPercent, failPercent))
                {
                    retStr += "C";
                }
            }
        }
    }
    return retStr;
}
string CtiCCFeeder::getQuestionablePhasesString(double ratioA, double ratioB, double ratioC, double confirmPercent, double failPercent)
{
    string retStr = "";
    if( isResponseQuestionable(ratioA, confirmPercent, failPercent)  )
    {
        retStr += "A";
    }
    if( isResponseQuestionable(ratioB, confirmPercent, failPercent)  )
    {
        retStr += "B";
    }
    if( isResponseQuestionable(ratioC, confirmPercent, failPercent)  )
    {
        retStr += "C";
    }
    return retStr;
}


string CtiCCFeeder::getPhaseIndicatorString(long capState, double ratioA, double ratioB, double ratioC, double confirmPercent, double failPercent)
{
    string retStr = "(none)";
    if( _USE_PHASE_INDICATORS )
    {
        if (capState == CtiCCCapBank::OpenQuestionable ||
            capState == CtiCCCapBank::CloseQuestionable )
        {
            retStr = getQuestionablePhasesString(ratioA, ratioB, ratioC, confirmPercent, failPercent);
        }
        else if (capState == CtiCCCapBank::OpenFail ||
                 capState == CtiCCCapBank::CloseFail )
        {
            retStr = getFailedPhasesString(ratioA, ratioB, ratioC, confirmPercent, failPercent);
        }
        else
        {
             retStr = "(none)";
        }
    }

    return retStr;
}

void CtiCCFeeder::createCannotControlBankText(string text, string commandString, EventLogEntries &ccEvents)
{
    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();

    if (_CC_DEBUG & CC_DEBUG_EXTENDED)
    {
        Cti::FormattedTable table;

        table.setCell(0, 0) << "CapBank";
        table.setCell(0, 1) << "ControlStatus";
        table.setCell(0, 2) << "OperationalState";
        table.setCell(0, 3) << "DisableFlag";
        table.setCell(0, 4) << "ControlInhibitFlag";

        CtiCCCapBank* currentCapBank = NULL;
        for(int i=0;i<_cccapbanks.size();i++)
        {
            currentCapBank = (CtiCCCapBank*)_cccapbanks[i];
            table.setCell(i + 1, 0) << currentCapBank->getPaoName();
            table.setCell(i + 1, 1) << currentCapBank->getControlStatus();
            table.setCell(i + 1, 2) << currentCapBank->getOperationalState();
            table.setCell(i + 1, 3) << currentCapBank->getDisableFlag();
            table.setCell(i + 1, 4) << currentCapBank->getControlInhibitFlag();
        }

        CTILOG_DEBUG(dout,
                     "Can Not "<< text << " level for feeder: " << getPaoName() << " any further.  All cap banks are already in the "<< commandString <<" state"
                     << table);
    }
    if (!getCorrectionNeededNoBankAvailFlag())
    {
        setCorrectionNeededNoBankAvailFlag(true);
        string textInfo;
        textInfo = ("Feeder: ");
        textInfo += getPaoName();
        textInfo += " Cannot ";
        textInfo += text;
        textInfo +=" Level.  No Cap Banks Available to " + commandString + ".";

        long stationId, areaId, spAreaId;
        store->getFeederParentInfo(this, spAreaId, areaId, stationId);
        ccEvents.push_back(EventLogEntry(0, SYS_PID_CAPCONTROL, spAreaId, areaId, stationId, getParentId(), getPaoId(), capControlPointOutsideOperatingLimits, getEventSequence(), -1, textInfo, "cap control", getCurrentVarLoadPointValue(), getCurrentVarLoadPointValue(), 0));
    }

}


void CtiCCFeeder::resetVerificationFlags()
{
    setVerificationFlag(false);
    setPerformingVerificationFlag(false);
    setVerificationDoneFlag(false);


    for(long k=0;k<_cccapbanks.size();k++)
    {
        CtiCCCapBank* currentCapBank = (CtiCCCapBank*)_cccapbanks[k];

        currentCapBank->setVerificationFlag(false);
        currentCapBank->setPerformingVerificationFlag(false);
        currentCapBank->setVerificationDoneFlag(false);
        //wouldn't hurt to set this.
        currentCapBank->setVCtrlIndex(0);
        currentCapBank->setSelectedForVerificationFlag(false);
    }
}

