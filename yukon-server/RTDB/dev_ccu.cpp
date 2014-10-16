#include "precompiled.h"

#include "cmdparse.h"
#include "dsm2.h"
#include "porter.h"

#include "pt_base.h"
#include "master.h"

#include "dev_ccu.h"
#include "connection.h"

#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_multi.h"
#include "prot_711.h"
#include "utility.h"

using namespace std;

CtiDeviceCCU::CtiDeviceCCU() :
    _tsLastCheck(0UL),
    _tsPos(0)
{
    int i;

    for( i = 0; i < (TimeSyncToggles * 2); i++ )
    {
        _tsAlgStatus[i] = 0;
    }

    for( i = 0; i < AlgorithmCount; i++ )
    {
        _algorithmCommandTime[i] = 0;
    }
}

//  checks if we've reset this algorithm recently - we can only do so every AlgorithmRepeatInterval seconds
bool CtiDeviceCCU::checkAlgorithmReset(int alg)
{
    bool retval = false;

    if( alg >= 0 && alg < AlgorithmCount )
    {
        if( (::time(0) - _algorithmCommandTime[alg]) > AlgorithmRepeatInterval )
        {
            retval = true;
            _algorithmCommandTime[alg] = ::time(0);
        }
    }

    return retval;
}

bool CtiDeviceCCU::checkForTimeSyncLoop(int status)
{
    bool reset   = false,
         ts_loop = false;

    if( (_tsLastCheck.seconds() + 60) < CtiTime::now().seconds() )
    {
        reset = true;
    }
    else
    {
        _tsAlgStatus[_tsPos] = status;

        _tsPos  = (_tsPos + 1) % (TimeSyncToggles * 2);

        ts_loop = true;

        for( int i = 0; ts_loop && (i < (TimeSyncToggles * 2 - 1)); i++ )
        {
            //  if any of the states are not toggles, exit
            if( !((_tsAlgStatus[i] == ALGO_ENABLED) && (_tsAlgStatus[i+1] != ALGO_ENABLED)) &&
                !((_tsAlgStatus[i] != ALGO_ENABLED) && (_tsAlgStatus[i+1] == ALGO_ENABLED)) )
            {
                ts_loop = false;
            }
        }
    }

    if( ts_loop || reset )
    {
        for( int i = 0; i < (TimeSyncToggles * 2); i++ )
        {
            _tsAlgStatus[i] = 0;
        }

        _tsPos = 0;
    }

    _tsLastCheck = CtiTime::now();

    return ts_loop;
}


YukonError_t CtiDeviceCCU::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  CtiMessageList &vgList,CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    // Get a loop done maybe?
    if( OutMessage )
    {
        CCULoop(OutMessage);
        outList.push_back( OutMessage );
        OutMessage = NULL;
    }

    return ClientErrors::None;
}

YukonError_t CtiDeviceCCU::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,  INT ScanPriority)
{
    return( GeneralScan(pReq, parse, OutMessage, vgList, retList, outList, ScanPriority) );
}


YukonError_t CtiDeviceCCU::ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    /* Clear the Scan Pending flag, if neccesary it will be reset */
    resetScanFlag(ScanRateGeneral);

    switch( InMessage.Sequence )
    {
        case Command_Loop:
        {
            char temp[10];
            string cmd(InMessage.Return.CommandStr);


            CtiReturnMsg   *pLoop = CTIDBG_new CtiReturnMsg(getID(),
                                                            cmd,
                                                            string(getName() + " / successful ping"),
                                                            InMessage.ErrorCode,
                                                            InMessage.Return.RouteID,
                                                            InMessage.Return.RetryMacroOffset,
                                                            InMessage.Return.Attempt,
                                                            InMessage.Return.GrpMsgID,
                                                            InMessage.Return.UserID);


            if( pLoop != NULL )
            {
                retList.push_back(pLoop);
            }
            break;
        }

        case Command_Reset:
        {
            CtiReturnMsg   *pLoop = CTIDBG_new CtiReturnMsg(getID(),
                                                            InMessage.Return.CommandStr,
                                                            string(getName() + " / reset submitted"),
                                                            InMessage.ErrorCode,
                                                            InMessage.Return.RouteID,
                                                            InMessage.Return.RetryMacroOffset,
                                                            InMessage.Return.Attempt,
                                                            InMessage.Return.GrpMsgID,
                                                            InMessage.Return.UserID);


            if( pLoop != NULL )
            {
                retList.push_back(pLoop);
            }
            break;
        }

        default:
        {
            CTILOG_ERROR(dout, "Unexpected InMessage.Sequence = "<< InMessage.Sequence);
        }
    }


    return ClientErrors::None;
}

unsigned CtiDeviceCCU::queuedWorkCount() const
{
    long workCount = 0;
    long priority = 0;
    if( getType() == TYPE_CCU711 && _trxInfo != NULL )
    {
        CtiTransmitter711Info *p711Info =  (CtiTransmitter711Info *)_trxInfo;
        workCount = p711Info->QueueHandle->Elements;
    }

    return workCount;
}

Cti::DeviceQueueInterface* CtiDeviceCCU::getDeviceQueueHandler()
{
    if( getType() == TYPE_CCU711 )
    {
        _queueHandler.set711Info((CtiTransmitter711Info *)_trxInfo);
        return &_queueHandler;
    }
    else
    {
        return NULL;
    }

}

/* Routine to decode returned CCU message and update database */
YukonError_t CtiDeviceCCU::ExecuteRequest(CtiRequestMsg     *pReq,
                                          CtiCommandParser  &parse,
                                          OUTMESS          *&OutMessage,
                                          CtiMessageList    &vgList,
                                          CtiMessageList    &retList,
                                          OutMessageList    &outList)
{
    YukonError_t nRet = ClientErrors::None;
    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*, INT ScanPriority)
     *   (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */

    switch( parse.getCommand() )
    {
        case LoopbackRequest:
        {
            CCULoop(OutMessage);
            outList.push_back( OutMessage );
            OutMessage = NULL;
            break;
        }

        case PutStatusRequest:
        {
            if( parse.getFlags() & CMD_FLAG_PS_RESET )
            {
                if( getType() == TYPE_CCU711 )
                {
                    OUTMESS *OutMTemp = CTIDBG_new OUTMESS(*OutMessage);

                    if( OutMTemp != NULL )
                    {
                        // Get a reset done maybe?
                        CCU711Reset(OutMTemp);
                        outList.push_back( OutMTemp );
                    }
                }
                else
                {
                    nRet = ClientErrors::NoMethod;
                    retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                                            string(OutMessage->Request.CommandStr),
                                                            string("Non-711 CCUs do not support this command"),
                                                            nRet,
                                                            OutMessage->Request.RouteID,
                                                            OutMessage->Request.RetryMacroOffset,
                                                            OutMessage->Request.Attempt,
                                                            OutMessage->Request.GrpMsgID,
                                                            OutMessage->Request.UserID,
                                                            OutMessage->Request.SOE,
                                                            CtiMultiMsg_vec()));
                }
            }

            break;
        }

        case ControlRequest:
        case GetStatusRequest:
        case GetValueRequest:
        case PutValueRequest:
        case GetConfigRequest:
        case PutConfigRequest:
        default:
        {
            nRet = ClientErrors::NoMethodForExecuteRequest;
            /* Set the error value in the base class. */
            // FIX FIX FIX 092999
            retList.push_back( CTIDBG_new CtiReturnMsg(getID(),
                                                    string(OutMessage->Request.CommandStr),
                                                    string("CCU Devices do not support this command (yet?)"),
                                                    nRet,
                                                    OutMessage->Request.RouteID,
                                                    OutMessage->Request.RetryMacroOffset,
                                                    OutMessage->Request.Attempt,
                                                    OutMessage->Request.GrpMsgID,
                                                    OutMessage->Request.UserID,
                                                    OutMessage->Request.SOE,
                                                    CtiMultiMsg_vec()));
            break;
        }
    }


    return nRet;
}

/* Routine to execute a loop message */
INT CtiDeviceCCU::CCULoop(OUTMESS* OutMessage)
{
    /* Build an IDLC loopback request */
    /* Load up the pieces of the structure */
    OutMessage->DeviceID    = getID();
    OutMessage->Port        = getPortID();
    OutMessage->Remote      = getAddress();
    OutMessage->EventCode   = RESULT | RCONT | ENCODED | NOWAIT;
    OutMessage->TimeOut     = 2;
    OutMessage->OutLength   = 3;
    OutMessage->InLength    = 0;
    OutMessage->Source      = 0;
    OutMessage->Destination = DEST_BASE;
    OutMessage->Command     = CMND_ACTIN;
    OutMessage->Retry       = 0;
    OutMessage->Sequence    = Command_Loop;
    OutMessage->ReturnNexus = NULL;
    OutMessage->SaveNexus   = NULL;

    OutMessage->Buffer.OutMessage[PREIDL] = NO_OP;

    return ClientErrors::None;
}


/* Routine to execute a reset on a 711 CCU */
INT CtiDeviceCCU::CCU711Reset(OUTMESS* OutMessage)
{
    //  Load up the structure
    OutMessage->DeviceID    = getID();
    OutMessage->Port        = getPortID();
    OutMessage->Remote      = getAddress();
    OutMessage->EventCode   = NOWAIT | NORESULT | ENCODED;
    OutMessage->TimeOut     = 5;
    OutMessage->OutLength   = 3;
    OutMessage->InLength    = 0;
    OutMessage->Source      = 0;
    OutMessage->Destination = DEST_BASE;
    OutMessage->Command     = CMND_ACTIN;
    OutMessage->Retry       = 0;
    OutMessage->Sequence    = Command_Reset;
    OutMessage->ReturnNexus = NULL;
    OutMessage->SaveNexus   = NULL;

    OutMessage->Buffer.OutMessage[PREIDL] = COLD;  //  16... ?

    EstablishOutMessagePriority( OutMessage, MAXPRIORITY );

    return ClientErrors::None;
}

