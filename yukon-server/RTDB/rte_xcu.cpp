/*-----------------------------------------------------------------------------*
*
* File:   rte_xcu
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/rte_xcu.cpp-arc  $
* REVISION     :  $Revision: 1.47 $
* DATE         :  $Date: 2005/07/11 13:58:49 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <iostream>
#include <iomanip>
using namespace std;

#include "desolvers.h"
#include "dsm2.h"
#include "expresscom.h"
#include "rte_xcu.h"
#include "master.h"
#include "cmdparse.h"
#include "ctibase.h"
#include "dev_remote.h"
#include "dev_tap.h"
#include "dev_snpp.h"
#include "dev_tnpp.h"
#include "dev_pagingreceiver.h"
#include "dev_lcu.h"
#include "dev_wctp.h"
#include "msg_pcrequest.h"
#include "msg_signal.h"
#include "porter.h"
#include "logger.h"
#include "numstr.h"

#include "prot_versacom.h"
#include "prot_fpcbc.h"
#include "prot_sa305.h"
#include "prot_sa3rdparty.h"

static INT NoQueing = FALSE;

CtiRouteXCU::CtiRouteXCU()
{
}

CtiRouteXCU::CtiRouteXCU(const CtiRouteXCU& aRef)
{
    *this = aRef;
}

CtiRouteXCU::~CtiRouteXCU()
{
}

CtiRouteXCU& CtiRouteXCU::operator=(const CtiRouteXCU& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
    }
    return *this;
}

void CtiRouteXCU::resetDevicePointer()
{
    _transmitterDevice.reset();
}

CtiRouteXCU&  CtiRouteXCU::setDevicePointer(CtiDeviceSPtr p)
{
    _transmitterDevice = p;
    return *this;
}

void CtiRouteXCU::DumpData()
{
    Inherited::DumpData();
}

void CtiRouteXCU::DecodeDatabaseReader(RWDBReader &rdr)
{
    INT iTemp;
    RWDBNullIndicator isNull;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout); dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
}

void CtiRouteXCU::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
}


INT CtiRouteXCU::ExecuteRequest(CtiRequestMsg               *pReq,
                                CtiCommandParser            &parse,
                                OUTMESS                     *&OutMessage,
                                RWTPtrSlist< CtiMessage >   &vgList,
                                RWTPtrSlist< CtiMessage >   &retList,
                                RWTPtrSlist< OUTMESS >      &outList)
{
    INT      status = NORMAL;
    ULONG    BytesWritten;

    try
    {
        if(_transmitterDevice)      // This is the pointer which refers this rte to its transmitter device.
        {
            if((status = _transmitterDevice->checkForInhibitedDevice(retList, OutMessage)) != DEVICEINHIBITED)
            {
                // ALL Routes MUST do this, since they are the final gasp before the trxmitting device
                OutMessage->Request.CheckSum = _transmitterDevice->getUniqueIdentifier();

                enablePrefix(false);        // Most protocols will not want this on.

                if(parse.getiValue("type") == ProtocolExpresscomType)
                {
                    enablePrefix(true);
                    status = assembleExpresscomRequest(pReq, parse, OutMessage, vgList, retList, outList);
                }
                else if(parse.getiValue("type") == ProtocolSA305Type)
                {
                    status = assembleSA305Request(pReq, parse, OutMessage, vgList, retList, outList);
                }
                else if(parse.getiValue("type") == ProtocolSA205Type)
                {
                    status = assembleSA105205Request(pReq, parse, OutMessage, vgList, retList, outList);
                }
                else if(parse.getiValue("type") == ProtocolSA105Type)
                {
                    status = assembleSA105205Request(pReq, parse, OutMessage, vgList, retList, outList);
                }
                else if(parse.getiValue("type") == ProtocolSADigitalType)
                {
                    status = assembleSASimpleRequest(pReq, parse, OutMessage, vgList, retList, outList);
                }
                else if(parse.getiValue("type") == ProtocolGolayType)
                {
                    status = assembleSASimpleRequest(pReq, parse, OutMessage, vgList, retList, outList);
                }
                else if(OutMessage->EventCode & VERSACOM)
                {
                    enablePrefix(true);
                    status = assembleVersacomRequest(pReq, parse, OutMessage, vgList, retList, outList);
                }
                else if(OutMessage->EventCode & FISHERPIERCE)
                {
                    status = assembleFisherPierceRequest(pReq, parse, OutMessage, vgList, retList, outList);
                }
                else if(OutMessage->EventCode & RIPPLE)
                {
                    status = assembleRippleRequest(pReq, parse, OutMessage, vgList, retList, outList);
                }
                else
                {
                    status = BADROUTE;
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " ERROR: Route " << getName() << " has no associated transmitter device" << endl;
            }
            status = -1;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return status;
}


INT CtiRouteXCU::assembleVersacomRequest(CtiRequestMsg               *pReq,
                                         CtiCommandParser            &parse,
                                         OUTMESS                     *&OutMessage,
                                         RWTPtrSlist< CtiMessage >   &vgList,
                                         RWTPtrSlist< CtiMessage >   &retList,
                                         RWTPtrSlist< OUTMESS >      &outList)
{
    INT            status = NORMAL;
    bool           xmore = true;
    RWCString      resultString;
    RWCString      byteString;
    ULONG          i, j;
    USHORT         Length;
    VSTRUCT        VSt;

    /*
     * Addressing variables SHALL have been assigned at an earlier level!
     */

    OutMessage->DeviceID = _transmitterDevice->getID();
    OutMessage->Port     = _transmitterDevice->getPortID();
    OutMessage->Remote   = _transmitterDevice->getAddress();
    OutMessage->TimeOut  = 2;
    OutMessage->InLength = -1;

    if(!OutMessage->Retry)  OutMessage->Retry = 2;

    CtiProtocolVersacom  Versacom(_transmitterDevice->getType());
    Versacom.parseRequest(parse, OutMessage->Buffer.VSt);                       // Pick out the CommandType and parameters

    for(j = 0; j < Versacom.entries(); j++)
    {
        OUTMESS *NewOutMessage = CTIDBG_new OUTMESS( *OutMessage );  // Create and copy

        if(NewOutMessage != NULL)
        {
            VSt = Versacom.getVStruct(j);                      // Copy in the structure

            switch(_transmitterDevice->getType())
            {
            case TYPE_SNPP:
            case TYPE_WCTP:
            case TYPE_TAPTERM:
            case TYPE_TNPP:
                {
                    CtiDeviceTapPagingTerminal *TapDev = (CtiDeviceTapPagingTerminal *)(_transmitterDevice.get());

                    /* Calculate the length */
                    Length                              = VSt.Nibbles +  2;

                    NewOutMessage->TimeOut              = 2;
                    NewOutMessage->InLength             = -1;
                    NewOutMessage->OutLength            = Length;
                    NewOutMessage->Buffer.TAPSt.Length  = Length;

                    /* Build the message */
                    NewOutMessage->Buffer.TAPSt.Message[0] = 'h';

                    for(i = 0; i < VSt.Nibbles; i++)
                    {
                        if(i % 2)
                        {
                            sprintf (&NewOutMessage->Buffer.TAPSt.Message[i + 1], "%1x", VSt.Message[i / 2] & 0x0f);
                        }
                        else
                        {
                            sprintf (&NewOutMessage->Buffer.TAPSt.Message[i + 1], "%1x", (VSt.Message[i / 2] >> 4) & 0x0f);
                        }
                    }

                    NewOutMessage->Buffer.TAPSt.Message[i + 1] = 'g';

                    for(i = 0; i < NewOutMessage->OutLength; i++)
                    {
                        byteString += (char)NewOutMessage->Buffer.TAPSt.Message[i];
                    }
                    byteString += "\n";

                    /* Now add it to the collection of outbound messages */
                    outList.insert( NewOutMessage );

                    break;
                }
            case TYPE_TCU5500:
            case TYPE_TCU5000:
                {
                    /************** VersaCommSend **************/

                    /* This is a mastercom device */
                    /* Load up all the goodies */
                    /* Calculate the length */
                    Length = (VSt.Nibbles + 1) / 2;

                    NewOutMessage->MessageFlags |= MSGFLG_APPLY_EXCLUSION_LOGIC;           // 051903 CGP.  Are all these OMs excludable (ie susceptible to crosstalk)??
                    NewOutMessage->OutLength = MASTERLENGTH + Length;

                    /* Build MasterComm header */
                    if((status = MasterHeader (NewOutMessage->Buffer.OutMessage + PREIDLEN, NewOutMessage->Remote, MASTERSEND, Length)) != NORMAL)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " " << __FILE__ << " (" << __LINE__ << ") Error: " << status << endl;
                        }

                        delete NewOutMessage;
                    }
                    else
                    {
                        /* Copy message into buffer */
                        memcpy (NewOutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH, VSt.Message, Length);

                        /* Now add it to the collection of outbound messages */
                        outList.insert( NewOutMessage );
                    }
                    break;
                }
            case TYPE_LCU415:
            case TYPE_LCU415ER:
            case TYPE_LCU415LG:
            case TYPE_LCUT3026:
                {
                    break;
                }
            default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << "  Cannot send versacom to TYPE:" << /*desolveDeviceType(*/_transmitterDevice->getType()/*)*/ << endl;
                    }

                    break;
                }
            }
        }
    }

    if(Versacom.entries() > 0)
    {
        resultString = CtiNumStr(Versacom.entries()) + " Versacom commands sent on route " + getName() + "\n" + byteString;
    }
    else
    {
        xmore = false;
        resultString = "Route " + getName() + " did not transmit Versacom commands";

        RWCString desc, actn;

        desc = "Route: " + getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route";

        vgList.insert(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
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

INT CtiRouteXCU::assembleRippleRequest(CtiRequestMsg               *pReq,
                                       CtiCommandParser            &parse,
                                       OUTMESS                     *&OutMessage,
                                       RWTPtrSlist< CtiMessage >   &vgList,
                                       RWTPtrSlist< CtiMessage >   &retList,
                                       RWTPtrSlist< OUTMESS >      &outList)
{
    INT            status = NORMAL;
    bool           xmore = true;
    RWCString      resultString;
    ULONG          i, j;
    USHORT         Length;

    UINT           bookkeeping = 0;

    /* The _transmitterDevice below is the transmitter device, which is an LCU in this case! */
    CtiDeviceLCU   *lcu = (CtiDeviceLCU*)(_transmitterDevice.get());

    OutMessage->DeviceID    = lcu->getID();
    OutMessage->Port        = lcu->getPortID();
    OutMessage->Remote      = lcu->getAddress();
    OutMessage->TimeOut     = 2;
    OutMessage->InLength    = -1;
    OutMessage->EventCode    |= RIPPLE | ENCODED;
    OutMessage->MessageFlags |= MSGFLG_APPLY_EXCLUSION_LOGIC;           // 051903 CGP.  Are all these OMs excludable (ie susceptible to crosstalk)??

    lcu->lcuControl( OutMessage );        // This will return NULL or a CTIDBG_new OUTMESS

    bookkeeping |= 0x00000002;

    if( bookkeeping & 0x00000001 && bookkeeping & 0x00000002 )
    {
        resultString = "Control \"" + parse.getCommandStr() + "\" (+ stage) sent on route " + getName();
    }
    else if( bookkeeping & 0x00000002 )
    {
        resultString = "Control \"" + parse.getCommandStr() + "\" sent on route " + getName();
    }
    else
    {
        xmore = false;
        resultString = "Route " + getName() + " did not transmit control";

        RWCString desc, actn;

        desc = "Route: " + getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route";

        vgList.insert(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
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

    if(OutMessage)
    {
        outList.insert( OutMessage );         // Insert a copy someone else can clean up.
        OutMessage = NULL;
    }

    return status;
}


INT CtiRouteXCU::assembleFisherPierceRequest(CtiRequestMsg               *pReq,
                                             CtiCommandParser            &parse,
                                             OUTMESS                     *&OutMessage,
                                             RWTPtrSlist< CtiMessage >   &vgList,
                                             RWTPtrSlist< CtiMessage >   &retList,
                                             RWTPtrSlist< OUTMESS >      &outList)
{
    INT            status = NORMAL;
    bool           xmore = true;
    RWCString      resultString;
    ULONG          i, j;
    USHORT         Length;
    FPSTRUCT       FPSt;

    /*
     * Addressing variables SHALL have been assigned at an earlier level!
     */

    OutMessage->DeviceID = _transmitterDevice->getID();
    OutMessage->Port     = _transmitterDevice->getPortID();
    OutMessage->Remote   = _transmitterDevice->getAddress();
    OutMessage->TimeOut  = 2;
    OutMessage->InLength = -1;

    FPPCCST *PCC = &(OutMessage->Buffer.FPSt.u.PCC);

    PCC->PRE[0] = '*';                        /* Select Paging Terminal header */
    memcpy(PCC->UID  , "001"     , 3);        /* Utility ID.... 001 is CP&L */
    memcpy(PCC->VID  , "001"     , 3);        /* Vendor ID      001 is FP   */
    memcpy(PCC->D    , "01"      , 2);        /* _transmitterDevice ID      01 is Capacitor Control */
    memcpy(PCC->VALUE, "000000"  , 6);        /* This is set on the calling side */

    // memcpy(PCC->GRP  , "0000"    , 4);     /* Group Addressing ... 0000 is Individual. This is set by the grp object. object*/
    // memcpy(PCC->ADDRS, "0000000" , 7);     /* Address        This is set by the dev_cbc object */
    // memcpy(PCC->F    , "01"      , 2);     /* Function       This is set in protocol object */

    PCC->POST[0] = '*';                       /* Select Paging Terminal trailer */

    CtiProtocolFisherPierceCBC  FisherPierce;
    FisherPierce.parseRequest(parse, OutMessage->Buffer.FPSt);

    for(j = 0; j < FisherPierce.entries(); j++)
    {
        OUTMESS *NewOutMessage = CTIDBG_new OUTMESS( *OutMessage );  // Create and copy

        if(NewOutMessage != NULL)
        {
            FPSt = FisherPierce.getFPStruct(j);                      // Copy in the structure

            switch(_transmitterDevice->getType())
            {
            case TYPE_WCTP:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    // FALL THROUGH ???? Probably true....
                }
            case TYPE_TAPTERM:
                {
                    CtiDeviceTapPagingTerminal *TapDev = (CtiDeviceTapPagingTerminal *)(_transmitterDevice.get());

                    /* Calculate the length */
                    Length                              = 29;

                    NewOutMessage->TimeOut              = 2;
                    NewOutMessage->InLength             = -1;
                    NewOutMessage->OutLength            = Length;
                    NewOutMessage->Buffer.TAPSt.Length  = Length;

                    /* Build the message */
                    memcpy(NewOutMessage->Buffer.TAPSt.Message, FPSt.u.Message, 29);

                    if(getDebugLevel() & 0x00000002 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " FP-CBC TAP Message:  ";
                        for(i = 0; i < Length; i++)
                        {
                            dout << NewOutMessage->Buffer.TAPSt.Message[i];
                        }
                        dout << endl;
                    }

                    /* Now add it to the collection of outbound messages */
                    outList.insert( NewOutMessage );

                    break;
                }
            default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    break;
                }
            }
        }
    }

    if(FisherPierce.entries() > 0)
    {
        resultString = CtiNumStr(FisherPierce.entries()) + " FisherPierce commands sent on route " + getName();
    }
    else
    {
        xmore = false;
        resultString = "Route " + getName() + " did not transmit FisherPierce commands";

        RWCString   desc, actn;

        desc = "Route: " + getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route";

        vgList.insert(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
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

INT CtiRouteXCU::assembleExpresscomRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT            status = NORMAL;
    bool           xmore = true;
    RWCString      resultString;
    RWCString      byteString;
    ULONG          i, j;
    USHORT         Length;

    /*
     * Addressing variables SHALL have been assigned at an earlier level!
     */

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID, RWCString(OutMessage->Request.CommandStr), resultString, status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered());

    OutMessage->DeviceID = _transmitterDevice->getID();
    OutMessage->Port     = _transmitterDevice->getPortID();
    OutMessage->Remote   = _transmitterDevice->getAddress();
    OutMessage->TimeOut  = 2;
    OutMessage->InLength = -1;

    if(!OutMessage->Retry)  OutMessage->Retry = 2;

    CtiProtocolExpresscom  xcom;
    xcom.parseAddressing(parse);                    // The parse holds all the addressing for the group.
    xcom.parseRequest(parse, *OutMessage);          // OutMessage->Buffer.TAPSt has been filled with xcom.entries() messages.

    if(xcom.entries() > 0)
    {
        OutMessage->EventCode |= ENCODED;               // Make the OM be ignored by porter...

        switch(_transmitterDevice->getType())
        {
        case TYPE_SNPP:
        case TYPE_WCTP:
        case TYPE_TAPTERM:
        case TYPE_TNPP:
            {
                CtiDeviceTapPagingTerminal *TapDev = (CtiDeviceTapPagingTerminal *)(_transmitterDevice.get());

                OutMessage->OutLength            = xcom.messageSize() * 2 +  2;
                OutMessage->Buffer.TAPSt.Length  = xcom.messageSize() * 2 +  2;

                // BEGIN serialpatch here
                j = 0;
                RWCString serialpatch;

                if(parse.getiValue("xcprefix", FALSE))
                {
                    serialpatch = parse.getsValue("xcprefixstr");
                }

                if(!serialpatch.isNull() && (xcom.getByte(0) == 0))
                {
                    for(j = 0; j <= serialpatch.length(); j++)
                    {
                        OutMessage->Buffer.TAPSt.Message[j] = serialpatch.data()[j];
                    }

                    OutMessage->OutLength            += serialpatch.length();
                    OutMessage->Buffer.TAPSt.Length  += serialpatch.length();

                    j = serialpatch.length();
                }
                // END serialpatch

                /* Build the message */
                OutMessage->Buffer.TAPSt.Message[j] = xcom.getStartByte();

                for(i = 0; i < xcom.messageSize() * 2; i++)
                {
                    BYTE curByte = xcom.getByte(i / 2);
                    if(i % 2)
                    {
                        sprintf(&OutMessage->Buffer.TAPSt.Message[i + 1 + j], "%1x", curByte & 0x0f);
                    }
                    else
                    {
                        sprintf(&OutMessage->Buffer.TAPSt.Message[i + 1 + j], "%1x", (curByte >> 4) & 0x0f);
                    }
                }
                OutMessage->Buffer.TAPSt.Message[i + 1 + j] = xcom.getStopByte();

                for(i = 0; i < OutMessage->OutLength; i++)
                {
                    byteString += (char)OutMessage->Buffer.TAPSt.Message[i];
                }


                /* Now add it to the collection of outbound messages */
                outList.insert( OutMessage );
                OutMessage = 0; // It has been used, don't let it be deleted!


                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << "  Cannot send expresscom to TYPE:" << /*desolveDeviceType(*/_transmitterDevice->getType()/*)*/ << endl;
                }

                break;
            }
        }

        resultString = CtiNumStr(xcom.entries()) + " Expresscom commands (" + CtiNumStr(xcom.messageSize()) + " bytes) sent on route " + getName() + "\n" + byteString;
    }
    else
    {
        xmore = false;
        resultString = "Route " + getName() + " did not transmit Expresscom commands";

        RWCString desc, actn;

        desc = "Route: " + getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route";

        vgList.insert(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
    }


    retReturn->setResultString( resultString );

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

INT CtiRouteXCU::assembleSA305Request(CtiRequestMsg *pReq,
                                      CtiCommandParser &parse,
                                      OUTMESS *&OutMessage,
                                      RWTPtrSlist< CtiMessage >   &vgList,
                                      RWTPtrSlist< CtiMessage >   &retList,
                                      RWTPtrSlist< OUTMESS >      &outList)
{
    INT            status = NORMAL;
    bool           xmore = true;
    RWCString      resultString;
    RWCString      byteString;
    ULONG          i, j;
    USHORT         Length;
    VSTRUCT        VSt;

    /*
     * Addressing variables SHALL have been assigned at an earlier level!
     */

    OutMessage->DeviceID = _transmitterDevice->getID();
    OutMessage->Port     = _transmitterDevice->getPortID();
    OutMessage->Remote   = _transmitterDevice->getAddress();
    OutMessage->TimeOut  = 2;
    OutMessage->InLength = -1;

    CtiProtocolSA305 prot305;

    prot305.setTransmitterType(_transmitterDevice->getType());
    prot305.setTransmitterAddress(_transmitterDevice->getAddress());
    prot305.parseCommand(parse, *OutMessage);

    if(prot305.messageReady())
    {
        OUTMESS *NewOutMessage = CTIDBG_new OUTMESS( *OutMessage );  // Create and copy

        if(NewOutMessage != NULL)
        {
            switch(_transmitterDevice->getType())
            {
            case TYPE_WCTP:
            case TYPE_TAPTERM:
                {
                    CtiDeviceTapPagingTerminal *TapDev = (CtiDeviceTapPagingTerminal *)(_transmitterDevice.get());

                    /* Build the message */
                    Length = prot305.buildMessage(CtiProtocolSA305::ModeNumericPage, NewOutMessage->Buffer.TAPSt.Message);

                    NewOutMessage->OutLength            = Length;
                    NewOutMessage->Buffer.TAPSt.Length  = Length;
                    NewOutMessage->TimeOut              = 2;
                    NewOutMessage->InLength             = -1;

                    for(i = 0; i < NewOutMessage->OutLength; i++)
                    {
                        byteString += (char)NewOutMessage->Buffer.TAPSt.Message[i];
                    }
                    byteString += "\n";
                    byteString += prot305.getBitString() + "\n";

                    /* Now add it to the collection of outbound messages */
                    outList.insert( NewOutMessage );
                    NewOutMessage = 0;

                    break;
                }
            case TYPE_RTC:
                {
                    NewOutMessage->EventCode = RESULT | ENCODED;
                    NewOutMessage->Buffer.SASt._groupType = SA305;

                    NewOutMessage->Buffer.SASt._bufferLen = prot305.buildMessage(CtiProtocolSA305::ModeHex, (char*)(NewOutMessage->Buffer.SASt._buffer));
                    NewOutMessage->OutLength = NewOutMessage->Buffer.SASt._bufferLen;

                    memcpy(NewOutMessage->Buffer.SASt._code305, (char*)(NewOutMessage->Buffer.SASt._buffer) ,NewOutMessage->Buffer.SASt._bufferLen);
                    NewOutMessage->Buffer.SASt._code305[NewOutMessage->Buffer.SASt._bufferLen + 1] = '\0';
                    strncpy(NewOutMessage->Request.CommandStr, parse.getCommandStr() ,COMMAND_STR_SIZE);

                    for(i = 0; i < NewOutMessage->OutLength; i++)
                    {
                        byteString += CtiNumStr(NewOutMessage->Buffer.SASt._buffer[i]).hex().zpad(2) + " ";
                    }
                    byteString += "\n" + prot305.getBitString() + "\n";

                    outList.insert( NewOutMessage );
                    NewOutMessage = 0;

                    resultString = " Command successfully sent on route " + getName() + "\n" + byteString;

                    break;
                }
            case TYPE_SERIESVLMIRTU:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << "  Cannot send SA305 to TYPE:" << _transmitterDevice->getType() << endl;
                    }

                    break;
                }
            }
        }

        if(NewOutMessage)
        {
            delete NewOutMessage;
        }
    }

    if(prot305.messageReady())
    {
        resultString = " Command successfully sent on route " + getName() + "\n" + byteString;
    }
    else
    {
        xmore = false;
        resultString = "Route " + getName() + " did not transmit commands.  Syntax may be in error.";

        RWCString desc, actn;

        desc = "Route: " + getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route";

        vgList.insert(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
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

INT CtiRouteXCU::assembleSA105205Request(CtiRequestMsg *pReq,
                                         CtiCommandParser &parse,
                                         OUTMESS *&OutMessage,
                                         RWTPtrSlist< CtiMessage >   &vgList,
                                         RWTPtrSlist< CtiMessage >   &retList,
                                         RWTPtrSlist< OUTMESS >      &outList)
{
    INT            status = NORMAL;
    bool           xmore = true;
    RWCString      resultString;
    RWCString      byteString;
    ULONG          i, j;

    OutMessage->DeviceID = _transmitterDevice->getID();
    OutMessage->Port     = _transmitterDevice->getPortID();
    OutMessage->TimeOut  = 2;
    OutMessage->InLength = -1;


    switch(_transmitterDevice->getType())
    {
    case TYPE_RTC:
        {
            CtiProtocolSA3rdParty prot;

            prot.setTransmitterAddress(_transmitterDevice->getAddress());
            prot.parseCommand(parse);

            if(prot.messageReady())
            {
                OutMessage->EventCode = RESULT | ENCODED;

                OutMessage->Buffer.SASt = prot.getSAData();
                OutMessage->OutLength = prot.getSABufferLen();

                CtiOutMessage *NewOutMessage = CTIDBG_new OUTMESS( *OutMessage );
                strncpy(NewOutMessage->Request.CommandStr, parse.getCommandStr() ,COMMAND_STR_SIZE);

                outList.insert( NewOutMessage );

                prot.copyMessage(byteString);
                resultString = " Command successfully sent on route " + getName() + "\n" + byteString;
            }
            break;
        }
    case TYPE_WCTP:
    case TYPE_TAPTERM:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << RWTime() << "  Cannot send SA PROTOCOLS to TYPE: " << _transmitterDevice->getType() << endl;
            }

            break;
        }
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << RWTime() << "  Cannot send to TYPE:" << _transmitterDevice->getType() << endl;
            }

            break;
        }
    }

    if( resultString.isNull() )
    {
        xmore = false;
        resultString = "Route " + getName() + " did not transmit commands";

        RWCString desc, actn;

        desc = "Route: " + getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route";

        vgList.insert(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
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

INT CtiRouteXCU::assembleSASimpleRequest(CtiRequestMsg *pReq,
                                         CtiCommandParser &parse,
                                         OUTMESS *&OutMessage,
                                         RWTPtrSlist< CtiMessage >   &vgList,
                                         RWTPtrSlist< CtiMessage >   &retList,
                                         RWTPtrSlist< OUTMESS >      &outList)
{
    INT            status = NORMAL;
    bool           xmore = true;
    RWCString      resultString;
    RWCString      byteString;
    ULONG          i, j;

    OutMessage->DeviceID = _transmitterDevice->getID();
    OutMessage->Port     = _transmitterDevice->getPortID();
    OutMessage->TimeOut  = 2;
    OutMessage->InLength = -1;

    switch(_transmitterDevice->getType())
    {
    case TYPE_SERIESVLMIRTU:
        {
            OutMessage->EventCode = RESULT | ENCODED;
            OutMessage->Buffer.SASt._groupType = GOLAY;
            strncpy(OutMessage->Buffer.SASt._codeSimple, parse.getsValue("sa_codesimple").data(), 7);
            outList.insert( CTIDBG_new OUTMESS( *OutMessage ) );

            resultString = " Command successfully sent on route " + getName() + "\n" + byteString;

            break;
        }
    case TYPE_RTC:
        {
            CtiProtocolSA3rdParty prot;

            prot.setTransmitterAddress(_transmitterDevice->getAddress());
            prot.parseCommand(parse);

            if(prot.messageReady())
            {
                OutMessage->EventCode = RESULT | ENCODED;

                OutMessage->Buffer.SASt = prot.getSAData();
                OutMessage->OutLength = prot.getSABufferLen();

                outList.insert( CTIDBG_new OUTMESS( *OutMessage ) );

                prot.copyMessage(byteString);
                resultString = " Command successfully sent on route " + getName() + "\n" + byteString;
            }
            break;
        }
    case TYPE_WCTP:
    case TYPE_TAPTERM:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << RWTime() << "  Cannot send SA PROTOCOLS to TYPE:" << _transmitterDevice->getType() << endl;
            }

            break;
        }
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << RWTime() << "  Cannot send SA PROTOCOLS to TYPE:" << _transmitterDevice->getType() << endl;
            }

            break;
        }
    }

    if( resultString.isNull() )
    {
        xmore = false;
        resultString = "Route " + getName() + " did not transmit commands";

        RWCString desc, actn;

        desc = "Route: " + getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route";

        vgList.insert(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
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


void CtiRouteXCU::enablePrefix(bool enable)
{
    if(_transmitterDevice)      // This is the pointer which refers this rte to its transmitter device.
    {
        CtiDeviceWctpTerminal *WctpDev = 0;
        CtiDeviceTapPagingTerminal *TapDev = 0;

        switch(_transmitterDevice->getType())
        {
        case TYPE_WCTP:
            {
                WctpDev = (CtiDeviceWctpTerminal *)(_transmitterDevice.get());
                WctpDev->setAllowPrefix(enable);
                break;
            }
        case TYPE_TAPTERM:
            {
                TapDev = (CtiDeviceTapPagingTerminal *)(_transmitterDevice.get());
                TapDev->setAllowPrefix(enable);
                break;
            }
        }
    }
    return;
}

LONG CtiRouteXCU::getTrxDeviceID() const
{
    LONG id = 0;

    if(_transmitterDevice)
    {
        id = _transmitterDevice->getID();
    }

    return id;
}

