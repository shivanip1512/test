#include "yukon.h"
#include "IVVCState.h"

IVVCState::IVVCState() :
    _state(IVVC_WAIT),
    _scannedRequest(false),
    _controlledId(-1),
    _paoId(0),
    _showVarCheckFailMsg(true),
    _showSubbusDisableMsg(true),
    _showRegulatorAutoModeMsg(true),
    _showNoRegulatorAttachedMsg(true),
    _remoteMode(false),
    _cbcCommsLost(false),
    _regulatorCommsLost(false),
    _voltageCommsLost(false),
    _cbcCommsRetryCount(0),
    _regulatorCommsRetryCount(0),
    _voltageCommsRetryCount(0),
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


void IVVCState::setShowRegulatorAutoModeMsg(const bool flag)
{
    _showRegulatorAutoModeMsg = flag;
}


bool IVVCState::isShowRegulatorAutoModeMsg() const
{
    return _showRegulatorAutoModeMsg;
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



void IVVCState::setConsecutiveCapBankOps(const unsigned ops)
{
    _consecutiveCapBankOps = ops;
}

const unsigned IVVCState::getConsecutiveCapBankOps() const
{
    return _consecutiveCapBankOps;
}



/*
    Comms lost flags
*/

bool IVVCState::isCbcCommsLost() const
{
    return _cbcCommsLost;
}

void IVVCState::setCbcCommsLost(const bool flag)
{
    _cbcCommsLost = flag;
}

bool IVVCState::isRegulatorCommsLost() const
{
    return _regulatorCommsLost;
}

void IVVCState::setRegulatorCommsLost(const bool flag)
{
    _regulatorCommsLost = flag;
}

bool IVVCState::isVoltageCommsLost() const
{
    return _voltageCommsLost;
}

void IVVCState::setVoltageCommsLost(const bool flag)
{
    _voltageCommsLost = flag;
}

/*
    Comms Retry counts
*/
void IVVCState::setCbcCommsRetryCount(const unsigned long retryCount)
{
    _cbcCommsRetryCount = retryCount;
}
unsigned long IVVCState::getCbcCommsRetryCount() const
{
    return _cbcCommsRetryCount;
}

void IVVCState::setRegulatorCommsRetryCount(const unsigned long retryCount)
{
    _regulatorCommsRetryCount = retryCount;
}

unsigned long IVVCState::getRegulatorCommsRetryCount() const
{
    return _regulatorCommsRetryCount;
}

void IVVCState::setVoltageCommsRetryCount(const unsigned long retryCount)
{
    _voltageCommsRetryCount = retryCount;
}

unsigned long IVVCState::getVoltageCommsRetryCount() const
{
    return _voltageCommsRetryCount;
}

