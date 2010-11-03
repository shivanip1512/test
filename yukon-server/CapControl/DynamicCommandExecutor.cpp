#include "yukon.h"

#include "DynamicCommandExecutor.h"

#include "logger.h"
#include "ctitime.h"
#include "ccid.h"
#include "cccapbank.h"
#include "ccsubstationbusstore.h"


extern ULONG _CC_DEBUG;

namespace Cti {
namespace CapControl {

DynamicCommandExecutor::DynamicCommandExecutor(DynamicCommand* dynamicCommand) : _dynamicCommand(dynamicCommand)
{

}

void DynamicCommandExecutor::execute()
{
    DynamicCommand::CommandType commandType = _dynamicCommand->getCommandType();

    switch (commandType)
    {
        case DynamicCommand::DELTA:
        {
            executePointResponseDeltaUpdate();
            break;
        }
        case DynamicCommand::UNDEFINED:
        default:
        {
            if (_CC_DEBUG & CC_DEBUG_EXTENDED)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - DynamicCommandExecutor: UNDEFINED COMMAND: Command Type: " << commandType << std::endl;
            }
            break;
        }
    }

    return;
}

bool DynamicCommandExecutor::executePointResponseDeltaUpdate()
{
    if ( ! validatePointResponseDeltaUpdate())
    {
        if (_CC_DEBUG & CC_DEBUG_EXTENDED)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - DynamicCommandExecutor: EXCEPTION: Point Response Update improperly formated." << std::endl;
        }
        return false;
    }

    long bankId,pointId,staticDelta;
    double newDelta;

    //already tested that these are in the command.
    _dynamicCommand->getParameter(DynamicCommand::DEVICE_ID,bankId);
    _dynamicCommand->getParameter(DynamicCommand::POINT_ID,pointId);
    _dynamicCommand->getParameter(DynamicCommand::POINT_RESPONSE_DELTA,newDelta);
    _dynamicCommand->getParameter(DynamicCommand::POINT_RESPONSE_STATIC_DELTA,staticDelta);

    CtiCCSubstationBusStore* store = CtiCCSubstationBusStore::getInstance();
    {
        RWRecursiveLock<RWMutexLock>::LockGuard  guard(store->getMux());

        CtiCCCapBankPtr bankPtr = store->getCapBankByPaoId(bankId);
        if (bankPtr == NULL)
        {
            if (_CC_DEBUG & CC_DEBUG_EXTENDED)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - DynamicCommandExecutor: EXCEPTION: Point Response Update failed. Invalid CapBank Id" << std::endl;
            }
            return false;
        }

        try
        {
            bankPtr->handlePointResponseDeltaChange(pointId,newDelta,(bool)staticDelta);
        }
        catch (NotFoundException& e)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - DynamicCommandExecutor: EXCEPTION: Point Response Update failed. Not Found: " << pointId << std::endl;

            return false;
        }

        bankPtr->dumpDynamicPointResponseData();
    }

    if (_CC_DEBUG & CC_DEBUG_EXTENDED)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - DynamicCommandExecutor: Point Response Update successful." << std::endl;
    }
    return true;
}

bool DynamicCommandExecutor::validatePointResponseDeltaUpdate()
{
    long bankId,pointId,staticDelta;
    double newDelta;

    //Check to make sure we have the bankId pointId and delta value
    if (_dynamicCommand->getParameter(DynamicCommand::DEVICE_ID,bankId) &&
        _dynamicCommand->getParameter(DynamicCommand::POINT_ID,pointId) &&
        _dynamicCommand->getParameter(DynamicCommand::POINT_RESPONSE_DELTA,newDelta) &&
        _dynamicCommand->getParameter(DynamicCommand::POINT_RESPONSE_STATIC_DELTA,staticDelta))
    {
        return true;
    }
    return false;
}

}
}
