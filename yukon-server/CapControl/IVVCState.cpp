#include "yukon.h"
#include "IVVCState.h"

IVVCState::IVVCState() :
    _state(IVVC_WAIT),
    _scannedRequest(false),
    _controlledBankId(0)
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

const GroupRequestPtr& IVVCState::getGroupRequest()
{
    return _groupRequest;
}

void IVVCState::setGroupRequest(const GroupRequestPtr& groupRequest)
{
    _groupRequest.reset();
    _groupRequest = groupRequest;
}

int IVVCState::getControlledBankId()
{
    return _controlledBankId;
}

void IVVCState::setControlledBankId(int bankId)
{
    _controlledBankId = bankId;
}

const CtiTime& IVVCState::getLastTapOpTime()
{
    return _lastTapOpTime;
}


void IVVCState::setLastTapOpTime(const CtiTime& opTime)
{
    _lastTapOpTime = opTime;
}

