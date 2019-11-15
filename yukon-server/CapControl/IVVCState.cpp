#include "precompiled.h"

#include "IVVCState.h"
#include <array>


Cti::CapControl::Phase  KeepAliveHelper::getCurrentPhase()
{
    static const std::array<Cti::CapControl::Phase, 4>  phases
    { 
        Cti::CapControl::Phase_Poly, 
        Cti::CapControl::Phase_A,
        Cti::CapControl::Phase_B, 
        Cti::CapControl::Phase_C 
    };

    phaseIndex = ( phaseIndex + 1 ) % phases.size();

    return phases[ phaseIndex ];
}

bool IVVCState::isIvvcOnline() const
{
    return ! _commsLost && powerFlow.valid;
}

IVVCState::IVVCState() :
    _state(IVVC_WAIT),
    _scannedRequest(false),
    _controlledId(-1),
    _paoId(0),
    _showVarCheckFailMsg(true),
    _showSubbusDisableMsg(true),
    _showNoRegulatorAttachedMsg(true),
    _showNoZonesOnBusMsg(true),
    _commsLost(false),
    _commsRetryCount(0),
    _firstPass(true),
    showZoneRegulatorConfigMsg(true)
{
}

IVVCState::State IVVCState::getState()
{
    return _state;
}

void IVVCState::setState(IVVCState::State state)
{
    _state = state;
}

bool IVVCState::isScannedRequest()
{
    return _scannedRequest;
}

void IVVCState::setScannedRequest(bool scannedRequest)
{
    _scannedRequest = scannedRequest;
}

const CtiTime& IVVCState::getTimeStamp()
{
    return _timeStamp;
}

void IVVCState::setTimeStamp(const CtiTime& time)
{
    _timeStamp = time;
}

const CtiTime& IVVCState::getNextControlTime()
{
    return _nextControlTime;
}

void IVVCState::setNextControlTime(const CtiTime& time)
{
    _nextControlTime = time;
}

const PointDataRequestPtr& IVVCState::getGroupRequest()
{
    return _groupRequest;
}

void IVVCState::setGroupRequest(const PointDataRequestPtr& groupRequest)
{
    _groupRequest.reset();
    _groupRequest = groupRequest;
}


const CtiTime& IVVCState::getLastTapOpTime()
{
    return _lastTapOpTime;
}


void IVVCState::setLastTapOpTime(const CtiTime& opTime)
{
    _lastTapOpTime = opTime;
}


void IVVCState::setControlledBankId(long bankId)
{
    _controlledId = bankId;
}


long IVVCState::getControlledBankId() const
{
    return _controlledId;
}

void IVVCState::setPaoId(long paoId)
{
    _paoId = paoId;
}


long IVVCState::getPaoId() const
{
    return _paoId;
}

bool IVVCState::isFirstPass()
{
    return _firstPass;
}

void IVVCState::setFirstPass(bool firstPass)
{
    _firstPass = firstPass;
}

void IVVCState::setShowVarCheckMsg(const bool flag)
{
    _showVarCheckFailMsg = flag;
}


bool IVVCState::isShowVarCheckMsg() const
{
    return _showVarCheckFailMsg;
}


void IVVCState::setShowBusDisableMsg(const bool flag)
{
    _showSubbusDisableMsg = flag;
}


bool IVVCState::isShowBusDisableMsg() const
{
    return _showSubbusDisableMsg;
}

void IVVCState::setShowNoRegulatorAttachedMsg(const bool flag)
{
    _showNoRegulatorAttachedMsg = flag;
}

bool IVVCState::isShowNoRegulatorAttachedMsg() const
{
    return _showNoRegulatorAttachedMsg;
}

void IVVCState::setReportedControllers(const std::set<long>& reportedControllers)
{
    _reportedControllers = reportedControllers;
}

const std::set<long>& IVVCState::getReportedControllers()
{
    return _reportedControllers;
}

void IVVCState::setShowNoZonesOnBusMsg(const bool flag)
{
    _showNoZonesOnBusMsg = flag;
}

bool IVVCState::isShowNoZonesOnBusMsg() const
{
    return _showNoZonesOnBusMsg;
}

void IVVCState::setConsecutiveCapBankOps(const unsigned ops)
{
    _consecutiveCapBankOps = ops;
}

const unsigned IVVCState::getConsecutiveCapBankOps() const
{
    return _consecutiveCapBankOps;
}

bool IVVCState::isCommsLost() const
{
    return _commsLost;
}

void IVVCState::setCommsLost(const bool flag)
{
    _commsLost = flag;
}

void IVVCState::setCommsRetryCount(const unsigned long retryCount)
{
    _commsRetryCount = retryCount;
}
unsigned long IVVCState::getCommsRetryCount() const
{
    return _commsRetryCount;
}

bool IVVCState::hasDmvTestState()
{
    return static_cast<bool>(_dmvTestData);
}

void IVVCState::setDmvTestState( std::unique_ptr<DmvTestData> testData )
{
    _dmvTestData = std::move( testData );
}

void IVVCState::deleteDmvState()
{
    _dmvTestData.reset();
}

DmvTestData & IVVCState::getDmvTestData()
{
    return *_dmvTestData;
}
