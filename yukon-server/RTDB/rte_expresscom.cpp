#include "precompiled.h"


#include <iostream>
#include <iomanip>

#include "cmdparse.h"
#include "dsm2.h"
#include "logger.h"
#include "porter.h"
#include "rte_expresscom.h"
#include "expresscom.h"
#include "numstr.h"

using std::endl;
using std::list;

CtiRouteExpresscom::CtiRouteExpresscom()
{}

std::string CtiRouteExpresscom::toString() const
{
    Cti::FormattedList itemList;
    itemList <<"CtiRouteExpresscom";
    itemList << Versacom;

    return (Inherited::toString() += itemList.toString());
}

void CtiRouteExpresscom::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    INT iTemp;

    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }
}

void CtiRouteExpresscom::DecodeVersacomDatabaseReader(Cti::RowReader &rdr)
{
    LONG                    RID;

    if(getType() == RouteTypeExpresscom)   // Just make darn sure.
    {
        if( getDebugLevel() & DEBUGLEVEL_DATABASE )
        {
            CTILOG_DEBUG(dout, "Decoding DB reader");
        }

        Versacom.DecodeDatabaseReader(rdr);
    }
}



YukonError_t CtiRouteExpresscom::ExecuteRequest(CtiRequestMsg                  *pReq,
                                                CtiCommandParser               &parse,
                                                OUTMESS                        *&OutMessage,
                                                list< CtiMessage* >      &vgList,
                                                list< CtiMessage* >      &retList,
                                                list< OUTMESS* >         &outList)
{
#define ABUFSIZE 40
#define A_WORD_LEN 7

    YukonError_t status = ClientErrors::None;
    bool      xmore = true;
    std::string resultString;
    BYTE      ABuf[ABUFSIZE];
    ULONG     BytesWritten;

    /*
     *  Wow, I have versacom data and I know what to do with it....
     */
    if(_transmitterDevice)      // This is the pointer which refers this rte to its transmitter device.
    {
        if((status = _transmitterDevice->checkForInhibitedDevice(retList, OutMessage)) != ClientErrors::DeviceInhibited)
        {
            // ALL Routes MUST do this, since they are the final gasp before the trxmitting device
            OutMessage->Request.CheckSum = _transmitterDevice->getUniqueIdentifier();

            if(OutMessage->EventCode & AWORD)      // This may have come from a emetcon group device...
            {
                /* I am a Duke Power type command */
                switch(parse.getCommand())
                {
                case ControlRequest:
                    {
                        OutMessage->EventCode |= ENCODED;
                        /*
                         *  Get us aligned with the transmitter of choice.
                         */

                        OutMessage->DeviceID             = _transmitterDevice->getID();      // This is the TCU information
                        OutMessage->Port                 = _transmitterDevice->getPortID();
                        OutMessage->Remote               = _transmitterDevice->getAddress();


                        // This is the CCU address, which is fixed at 1 for Duke
                        OutMessage->Buffer.ASt.Remote             = 1;                    // This is the CCU 710 info (default for Duke only)
                        OutMessage->Buffer.ASt.DlcRoute.Stages    = 0;                    // This is the CCU 710 info (default for Duke only)
                        OutMessage->Buffer.ASt.DlcRoute.RepFixed  = 31;                   // This is the CCU 710 info (default for Duke only)
                        OutMessage->Buffer.ASt.DlcRoute.RepVar    = 7;                    // This is the CCU 710 info (default for Duke only)

                        OutMessage->Buffer.ASt.DlcRoute.Amp       = Versacom.getAmp();
                        OutMessage->Buffer.ASt.DlcRoute.Bus       = Versacom.getBus();

                        /* build preamble message */
                        memset(&ABuf, 0, ABUFSIZE * sizeof(BYTE));
                        APreamble (ABuf, OutMessage->Buffer.ASt);
                        A_Word(ABuf + 3, OutMessage->Buffer.ASt, TRUE);  // Duke needs/wants double a word

                        //So we have our A word, lets build our expresscom message!
                        int buffLoc = 1;//start at 1 (skip addressing byte)
                        int addressing = 0;
                        int spid = Versacom.getSPID();

                        if( spid > 0 )
                        {
                            addressing |= 0x80;//SPID is present, it is not a unique address
                            OutMessage->Buffer.OutMessage[buffLoc++] = spid >> 8;
                            OutMessage->Buffer.OutMessage[buffLoc++] = spid;

                            int geo = Versacom.getGeo();
                            int sub = Versacom.getSubstation();

                            if( geo > 0 )
                            {
                                addressing |= 0x40;
                                OutMessage->Buffer.OutMessage[buffLoc++] = geo >> 8;
                                OutMessage->Buffer.OutMessage[buffLoc++] = geo;
                            }

                            if( sub > 0 )
                            {
                                addressing |= 0x20;
                                OutMessage->Buffer.OutMessage[buffLoc++] = sub >> 8;
                                OutMessage->Buffer.OutMessage[buffLoc++] = sub;
                            }
                        }
                        else
                        {
                            UINT individual = Versacom.getIndividual();
                            OutMessage->Buffer.OutMessage[buffLoc++] = individual >> 24;
                            OutMessage->Buffer.OutMessage[buffLoc++] = individual >> 16;
                            OutMessage->Buffer.OutMessage[buffLoc++] = individual >>  8;
                            OutMessage->Buffer.OutMessage[buffLoc++] = individual >>  0;
                        }

                        OutMessage->Buffer.OutMessage[0] = addressing;
                        OutMessage->Buffer.OutMessage[buffLoc++] = 0x1D;//Data Message type! (yes I should probably enumerate this)
                        OutMessage->Buffer.OutMessage[buffLoc++] = 0x00;//Binary, use port 0 (Default)
                        OutMessage->Buffer.OutMessage[buffLoc++] = A_WORD_LEN;

                        for( int i = 0; i<A_WORD_LEN; i++ )
                        {
                            OutMessage->Buffer.OutMessage[buffLoc++] = ABuf[i];
                        }

                        OutMessage->OutLength = buffLoc;

                        OUTMESS *NewOutMessage = CTIDBG_new OUTMESS( *OutMessage );  // Create and copy

                        if(NewOutMessage != NULL)
                        {
                            outList.push_back(NewOutMessage);
                            NewOutMessage = 0;
                        }
                        else
                        {
                            status = ClientErrors::MemoryAccess;
                        }

                        resultString = "AWord control sent via versacom on route: " + getName();

                        break;
                    }
                default:
                    {
                        resultString = "Unsupported command on AWord/Expresscom route: " + getName();

                        CTILOG_ERROR(dout, resultString);

                        break;
                    }
                }
            }
            else
            {
                resultString = "Expresscom routes do not support non-AWord commands (yet) Rte: " + getName();

                CTILOG_ERROR(dout, resultString);
            }
        }
    }
    else
    {
        resultString = " ERROR: Route " + getName() + " has no associated transmitter device";

        CTILOG_ERROR(dout, resultString);

        status = ClientErrors::NoTransmitterForRoute;
    }

    if( status )
    {
        xmore = false;
        resultString = "Route " + getName() + " did not transmit Versacom/AWord commands";

        const std::string desc = "Route: " + getName();
        const std::string actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route";

        vgList.push_back(CTIDBG_new CtiSignalMsg(SYS_PID_SYSTEM, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
    }


    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID,
                                                      OutMessage->Request.CommandStr,
                                                      resultString,
                                                      status,
                                                      OutMessage->Request.RouteID,
                                                      OutMessage->Request.RetryMacroOffset,
                                                      OutMessage->Request.Attempt,
                                                      OutMessage->Request.GrpMsgID,
                                                      OutMessage->Request.UserID,
                                                      OutMessage->Request.SOE,
                                                      CtiMultiMsg_vec());

    if(retReturn)
    {
        if(parse.isTwoWay()) retReturn->setExpectMore(xmore);
        retList.push_back(retReturn);
    }
    else
    {
        delete retReturn;
    }

    if(OutMessage != NULL)
    {
        delete OutMessage;       // Get rid of the memory which got us here.
        OutMessage = NULL;
    }

    return status;
}
