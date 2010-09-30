#include "yukon.h"
#include "IVVCState.h"

IVVCState::IVVCState() :
    _state(IVVC_WAIT),
    _scannedRequest(false),
    _controlledId(-1),
    _paoId(0),
    _commsRetryCount(0),
    _showVarCheckFailMsg(true),
    _showSubbusDisableMsg(true),
    _showLtcAutoModeMsg(true),
    _showNoLtcAttachedMsg(true),
    _remoteMode(false),
    _commsLost(false),
    _firstPass(true)
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

bool IVVCState::isRemoteMode()
{
    return _remoteMode;
}

void IVVCState::setRemoteMode(bool remoteMode)
{
    _remoteMode = remoteMode;
}

bool IVVCState::isFirstPass()
{
    return _firstPass;
}

void IVVCState::setFirstPass(bool firstPass)
{
    _firstPass = firstPass;
}

CtiTime IVVCState::getNextHeartbeatTime()
{
    return _nextHeartbeat;
}

void IVVCState::setNextHeartbeatTime(const CtiTime& time)
{
    _nextHeartbeat = time;
}


void IVVCState::setCommsRetryCount(const unsigned long retryCount)
{
    _commsRetryCount = retryCount;
}


unsigned long IVVCState::getCommsRetryCount() const
{
    return _commsRetryCount;
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


void IVVCState::setShowLtcAutoModeMsg(const bool flag)
{
    _showLtcAutoModeMsg = flag;
}


bool IVVCState::isShowLtcAutoModeMsg() const
{
    return _showLtcAutoModeMsg;
}

void IVVCState::setShowNoLtcAttachedMsg(const bool flag)
{
    _showNoLtcAttachedMsg = flag;
}

bool IVVCState::isShowNoLtcAttachedMsg() const
{
    return _showNoLtcAttachedMsg;
}

void IVVCState::setReportedControllers(const std::set<long>& reportedControllers)
{
    _reportedControllers = reportedControllers;
}

const std::set<long>& IVVCState::getReportedControllers()
{
    return _reportedControllers;
}

bool IVVCState::isCommsLost() const
{
    return _commsLost;
}

void IVVCState::setCommsLost(const bool flag)
{
    _commsLost = flag;
}

void IVVCState::setConsecutiveCapBankOps(const unsigned ops)
{
    _consecutiveCapBankOps = ops;
}

const unsigned IVVCState::getConsecutiveCapBankOps() const
{
    return _consecutiveCapBankOps;
}

