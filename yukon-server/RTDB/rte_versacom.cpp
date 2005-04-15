/*-----------------------------------------------------------------------------*
*
* File:   rte_versacom
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/rte_versacom.cpp-arc  $
* REVISION     :  $Revision: 1.15 $
* DATE         :  $Date: 2005/04/15 19:04:10 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <iostream>
#include <iomanip>
using namespace std;

#include "cmdparse.h"
#include "dsm2.h"
#include "logger.h"
#include "porter.h"
#include "rte_versacom.h"
#include "prot_versacom.h"
#include "numstr.h"

CtiRouteVersacom::CtiRouteVersacom() {}

CtiRouteVersacom::CtiRouteVersacom(const CtiRouteVersacom& aRef)
{
    *this = aRef;
}

CtiRouteVersacom::~CtiRouteVersacom() {}

CtiRouteVersacom& CtiRouteVersacom::operator=(const CtiRouteVersacom& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
    }
    return *this;
}

void CtiRouteVersacom::DumpData()
{
    Inherited::DumpData();
    Versacom.DumpData();
}


void CtiRouteVersacom::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
}

void CtiRouteVersacom::DecodeDatabaseReader(RWDBReader &rdr)
{
    INT iTemp;
    RWDBNullIndicator isNull;

    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    // Versacom specific data is picked up in the VersacomDecode
}

void CtiRouteVersacom::DecodeVersacomDatabaseReader(RWDBReader &rdr)
{
    LONG                    RID;

    if(getType() == VersacomRouteType)   // Just make darn sure.
    {
        if( getDebugLevel() & DEBUGLEVEL_DATABASE )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        Versacom.DecodeDatabaseReader(rdr);
    }
}



INT CtiRouteVersacom::ExecuteRequest(CtiRequestMsg                  *pReq,
                                     CtiCommandParser               &parse,
                                     OUTMESS                        *&OutMessage,
                                     RWTPtrSlist< CtiMessage >      &vgList,
                                     RWTPtrSlist< CtiMessage >      &retList,
                                     RWTPtrSlist< OUTMESS >         &outList)
{
#define ABUFSIZE 40

    INT       status = NORMAL;
    bool      xmore = true;
    RWCString resultString;
    BYTE      ABuf[ABUFSIZE];
    ULONG     BytesWritten;

    BASEDLL_IMPORT extern CTINEXUS PorterNexus;

    /*
     *  Wow, I have versacom data and I know what to do with it....
     */
    if(_transmitterDevice)      // This is the pointer which refers this rte to its transmitter device.
    {
        if((status = _transmitterDevice->checkForInhibitedDevice(retList, OutMessage)) != DEVICEINHIBITED)
        {
            // ALL Routes MUST do this, since they are the final gasp before the trxmitting device
            OutMessage->Request.CheckSum = _transmitterDevice->getUniqueIdentifier();

            OutMessage->EventCode |= VERSACOM;

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
                        OutMessage->Buffer.ASt.DlcRoute.Feeder    = Versacom.getBus();

                        /* build preamble message */
                        memset(&ABuf, 0, ABUFSIZE * sizeof(BYTE));
                        APreamble (ABuf, OutMessage->Buffer.ASt);
                        A_Word(ABuf + 3, OutMessage->Buffer.ASt, TRUE);  // Duke needs/wants double a word

                        /*
                         *  I now have the ABuf I need to stuff in a versacom word.
                         *  AWord is safely in the ABuf, blank the VSt!
                         */
                        memset(&OutMessage->Buffer.VSt, 0, sizeof(VSTRUCT));

                        OutMessage->Buffer.VSt.DlcRoute.Amp      = Versacom.getAmp();
                        OutMessage->Buffer.VSt.DlcRoute.Feeder   = Versacom.getBus();
                        OutMessage->Buffer.VSt.DlcRoute.RepFixed = 31;
                        OutMessage->Buffer.VSt.DlcRoute.RepVar   = 7;

                        // Addressing is always fully defined and elements are omitted only if zero in the nibble builder
                        OutMessage->Buffer.VSt.UtilityID         = Versacom.getUtilityID();
                        OutMessage->Buffer.VSt.Section           = Versacom.getSection();
                        OutMessage->Buffer.VSt.Class             = Versacom.getClass();
                        OutMessage->Buffer.VSt.Division          = Versacom.getDivision();
                        OutMessage->Buffer.VSt.Address           = 0L;

                        OutMessage->Buffer.VSt.CommandType       = EXDATA;           // Extended VDATA CONTROL
    #if 0
                        if(parse.getFlags() & CMD_FLAG_TESTMODE)
                        {
                            OutMessage->Buffer.VSt.CommandType   = VDATA;            // VDATA CONTROL
                        }
    #endif

                        OutMessage->Buffer.VSt.VData.DataType    = 0;
                        OutMessage->Buffer.VSt.VData.DataLength  = 7;

                        // Copy in the AWord from the buffer!
                        memcpy(OutMessage->Buffer.VSt.VData.Data, ABuf, OutMessage->Buffer.VSt.VData.DataLength);

                        /*
                         * Use this method to manipulate the Versacom protocol
                         * OutMessage comes back fully ready to go out the port!
                         */
                        status = assembleVersacomRequest(pReq, parse, OutMessage, vgList, retList, outList);

                        resultString = "AWord control sent via versacom on route: " + getName();

                        break;
                    }
                default:
                    {
                        {
                            resultString = "Unsupported command on AWord/Versacom route: " + getName();
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " " << resultString << endl;
                            }
                        }
                        break;
                    }
                }
            }
            else
            {
                resultString = "Versacom routes do not support non-AWord commands (yet) Rte: " + getName();
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " " << resultString << endl;
                }
            }
        }
    }
    else
    {
        resultString = " ERROR: Route " + getName() + " has no associated transmitter device";
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " " << resultString << endl;
        }
        status = -1;
    }

    if(status != NORMAL)
    {
        xmore = false;
        resultString = "Route " + getName() + " did not transmit Versacom/AWord commands";

        RWCString   desc, actn;

        desc = "Route: " + getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route";

        vgList.insert(CTIDBG_new CtiSignalMsg(SYS_PID_SYSTEM, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
    }


    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID, RWCString(OutMessage->Request.CommandStr), resultString, status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered());

    if(retReturn)
    {
        if(parse.isTwoWay()) retReturn->setExpectMore(xmore);
        retList.insert(retReturn);
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

INT CtiRouteVersacom::assembleVersacomRequest(CtiRequestMsg                  *pReq,
                                              CtiCommandParser               &parse,
                                              OUTMESS                        *OutMessage,
                                              RWTPtrSlist< CtiMessage >      &vgList,
                                              RWTPtrSlist< CtiMessage >      &retList,
                                              RWTPtrSlist< OUTMESS >         &outList)
{
    INT            status = NORMAL;
    bool           xmore = true;
    ULONG          i, j;
    USHORT         Length;
    RWCString      resultString;

    VSTRUCT        VSt;

    CtiProtocolVersacom  Versacom(_transmitterDevice->getType());

    if(OutMessage->EventCode & ENCODED)
    {
        // Someone else did all the parsing and is just needs building
        // Prime the Protocol device with the vstruct, and call the update routine
        if((status = Versacom.primeAndAppend(OutMessage->Buffer.VSt)) == NORMAL)
        {
            status = Versacom.updateVersacomMessage();
        }
    }
    else
    {
        status = Versacom.parseRequest(parse, OutMessage->Buffer.VSt);  // Pick out the CommandType and parameters
    }

    /*
     *  Now we get fully qualified VSTRUCT(s) which has/have valid message(s)
     */

    /* This is a mastercomm device */
    /* Load up all the goodies */

    if(status == NORMAL)
    {
        OutMessage->TimeOut   = 2;
        OutMessage->InLength  = -1;

        for(j = 0; j < Versacom.entries(); j++)
        {
            OUTMESS *NewOutMessage = CTIDBG_new OUTMESS( *OutMessage );  // Create and copy

            if(NewOutMessage != NULL)
            {
                VSt = Versacom.getVStruct(j);           // Copy in the structure

                /************** VersaCommSend **************/
                /* Calculate the length */
                Length = (VSt.Nibbles + 1) / 2;

                NewOutMessage->OutLength = MASTERLENGTH + Length;

                /* Build MasterComm header */
                if((status = MasterHeader (NewOutMessage->Buffer.OutMessage + PREIDLEN, NewOutMessage->Remote, MASTERSEND, Length)) != NORMAL)
                {
                    cout << RWTime() << " " << __FILE__ << " (" << __LINE__ << ") Error: " << status << endl;
                    delete NewOutMessage;
                }
                else
                {
                    /* Copy message into buffer */
                    memcpy (NewOutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH, VSt.Message, Length);
                    outList.insert( NewOutMessage );
                }
            }
            else
            {
                status = MEMORY;
            }
        }
    }

    if(Versacom.entries() > 0)
    {
        resultString = CtiNumStr(Versacom.entries()) + " Versacom commands sent on route " + getName();
    }
    else
    {
        xmore = false;
        resultString = "Route " + getName() + " did not transmit Versacom commands";
    }

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID, RWCString(OutMessage->Request.CommandStr), resultString, status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered());

    if(retReturn)
    {
        if(parse.isTwoWay()) retReturn->setExpectMore(xmore);
        retList.insert(retReturn);
    }
    else
    {
        delete retReturn;
    }

    return status;
}
