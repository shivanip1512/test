#include "precompiled.h"

#include "DynamicCommandExecutor.h"
#include "ExecutorFactory.h"
#include "ExecVerification.h"
#include "ExecChangeOpState.h"
#include "ccexecutor.h"

using std::endl;
using Cti::CapControl::DynamicCommandExecutor;

std::auto_ptr<CtiCCExecutor> CtiCCExecutorFactory::createExecutor(const CtiMessage* message)
{
    std::auto_ptr<CtiCCExecutor> ret_val;
    long classId = message->isA();

    switch (classId)
    {
        case CTICCSUBSTATIONBUS_MSG_ID:
        case CTICCCAPBANKSTATES_MSG_ID:
        case CTICCGEOAREAS_MSG_ID:
        case CTICCSPECIALAREAS_MSG_ID:
        case CTICCSUBSTATION_MSG_ID:
        case CTICCSERVERRESPONSE_ID:
        case CTIVOLTAGEREGULATOR_MSG_ID:
            ret_val.reset(new CtiCCClientMsgExecutor((CtiMessage*)message));
            break;

        case CAP_CONTROL_COMMAND_ID:
            ret_val.reset(new CtiCCCommandExecutor((CapControlCommand*)message));
            break;

        case ITEM_COMMAND_MSG_ID:
            ret_val.reset(new CtiCCCommandExecutor((ItemCommand*)message));
            break;
        case CHANGE_OP_STATE_MSG_ID:
            ret_val.reset(new ChangeOpStateExecutor((ChangeOpState*)message));
            delete message;
            message = NULL;
            break;

        case VERIFY_INACTIVE_BANKS_MSG_ID:
            ret_val.reset(new VerificationExecutor((VerifyInactiveBanks*)message));
            delete message;
            message = NULL;
            break;
        case CTICCSUBVERIFICATIONMSG_ID:
            ret_val.reset(new VerificationExecutor((VerifyBanks*)message));
            delete message;
            message = NULL;
            break;

        case DYNAMICCOMMAND_ID:
            ret_val.reset(new DynamicCommandExecutor((DynamicCommand*)message));
            break;

        case MSG_POINTDATA:
            ret_val.reset(new CtiCCPointDataMsgExecutor((CtiPointDataMsg*)message));
            break;

        case MSG_MULTI:
            ret_val.reset(new CtiCCMultiMsgExecutor((CtiMultiMsg*)message));
            break;

        case MSG_COMMAND:
            ret_val.reset(new CtiCCForwardMsgToDispatchExecutor((CtiMessage*)message));
            break;

        case CTICCCAPBANKMOVEMSG_ID:
            ret_val.reset(new CtiCCCapBankMoveExecutor((CtiCCCapBankMoveMsg*)message));
            break;
        case CTICCOBJECTMOVEMSG_ID:
            ret_val.reset(new CtiCCFeederMoveExecutor((CtiCCObjectMoveMsg*)message));
            break;
        case CTICCSHUTDOWN_ID:
            ret_val.reset(new CtiCCShutdownExecutor());
            break;

        case DELETE_ITEM_MSG_ID:
            ret_val.reset(new DeleteItemExecutor((DeleteItem*)message));
            break;

        case SYSTEM_STATUS_MSG_ID:
            ret_val.reset(new SystemStatusExecutor((SystemStatus*)message));
            break;

        default:
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - CtiCCExecutorFactory::createExecutor - Warning unknown classId: " << classId << endl;
            ret_val.reset(new NoOpExecutor());
        }
    }

    return ret_val;
}
