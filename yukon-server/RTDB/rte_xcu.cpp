#include "precompiled.h"

#include "dsm2.h"
#include "expresscom.h"
#include "rte_xcu.h"
#include "master.h"
#include "cmdparse.h"
#include "dllbase.h"
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
#include "cparms.h"

#include "prot_versacom.h"
#include "prot_fpcbc.h"
#include "prot_sa305.h"
#include "prot_sa3rdparty.h"
#include "prot_lmi.h"
using namespace std;

#define RDS_MAX_EXPRESSCOM_LENGTH   125

CtiRouteXCU::CtiRouteXCU()
{
}

void CtiRouteXCU::setDevicePointer(CtiDeviceSPtr p)
{
    _transmitterDevice = p;
}

std::string CtiRouteXCU::toString() const
{
    Cti::FormattedList itemList;
    itemList <<"CtiRouteXCU";

    return (Inherited::toString() += itemList.toString());
}

void CtiRouteXCU::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }

    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
}

YukonError_t CtiRouteXCU::ExecuteRequest(CtiRequestMsg        *pReq,
                                         CtiCommandParser     &parse,
                                         OUTMESS             *&OutMessage,
                                         list< CtiMessage* >  &vgList,
                                         list< CtiMessage* >  &retList,
                                         list< OUTMESS* >     &outList)
{
    if( ! _transmitterDevice )      // This is the pointer which refers this rte to its transmitter device.
    {
        CTILOG_ERROR(dout, "Route "<< getName() <<" has no associated transmitter device");

        return ClientErrors::NoTransmitterForRoute;
    }

    try
    {
        if( const YukonError_t inhibitedStatus = _transmitterDevice->checkForInhibitedDevice(retList, OutMessage) )
        {
            return inhibitedStatus;
        }

        // ALL Routes MUST do this, since they are the final gasp before the trxmitting device
        OutMessage->Request.CheckSum = _transmitterDevice->getUniqueIdentifier();

        enablePrefix(false);        // Most protocols will not want this on.

        if(parse.getiValue("type") == ProtocolExpresscomType)
        {
            enablePrefix(true);
            return assembleExpresscomRequest(pReq, parse, OutMessage, vgList, retList, outList);
        }
        else if(parse.getiValue("type") == ProtocolSA305Type)
        {
            return assembleSA305Request(pReq, parse, OutMessage, vgList, retList, outList);
        }
        else if(parse.getiValue("type") == ProtocolSA205Type)
        {
            return assembleSA105205Request(pReq, parse, OutMessage, vgList, retList, outList);
        }
        else if(parse.getiValue("type") == ProtocolSA105Type)
        {
            return assembleSA105205Request(pReq, parse, OutMessage, vgList, retList, outList);
        }
        else if(parse.getiValue("type") == ProtocolSADigitalType)
        {
            return assembleSASimpleRequest(pReq, parse, OutMessage, vgList, retList, outList);
        }
        else if(parse.getiValue("type") == ProtocolGolayType)
        {
            return assembleSASimpleRequest(pReq, parse, OutMessage, vgList, retList, outList);
        }
        else if( parse.getiValue("type") == ProtocolEmetconType )
        {
            return ClientErrors::Abnormal;
        }
        else if(OutMessage->EventCode & VERSACOM)
        {
            enablePrefix(true);
            return assembleVersacomRequest(pReq, parse, OutMessage, vgList, retList, outList);
        }
        else if(OutMessage->EventCode & FISHERPIERCE)
        {
            return assembleFisherPierceRequest(pReq, parse, OutMessage, vgList, retList, outList);
        }
        else if(OutMessage->EventCode & RIPPLE)
        {
            return assembleRippleRequest(pReq, parse, OutMessage, vgList, retList, outList);
        }

        return ClientErrors::BadRoute;
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }

    return ClientErrors::None;
}


YukonError_t CtiRouteXCU::assembleVersacomRequest(CtiRequestMsg               *pReq,
                                                  CtiCommandParser            &parse,
                                                  OUTMESS                     *&OutMessage,
                                                  list< CtiMessage* >   &vgList,
                                                  list< CtiMessage* >   &retList,
                                                  list< OUTMESS* >      &outList)
{
    YukonError_t   status = ClientErrors::None;
    bool           xmore = true;
    string      resultString;
    string      byteString;
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
        ::memset((void*)NewOutMessage->Buffer.OutMessage, 0, sizeof(NewOutMessage->Buffer.OutMessage));

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
                            ::sprintf (&NewOutMessage->Buffer.TAPSt.Message[i + 1], "%1x", VSt.Message[i / 2] & 0x0f);
                        }
                        else
                        {
                            ::sprintf (&NewOutMessage->Buffer.TAPSt.Message[i + 1], "%1x", (VSt.Message[i / 2] >> 4) & 0x0f);
                        }
                    }

                    NewOutMessage->Buffer.TAPSt.Message[i + 1] = 'g';
                    NewOutMessage->Buffer.TAPSt.Message[i + 2] = '\0';

                    for(i = 0; i < NewOutMessage->OutLength; i++)
                    {
                        byteString += (char)NewOutMessage->Buffer.TAPSt.Message[i];
                    }
                    byteString += "\n";

                    /* Now add it to the collection of outbound messages */
                    outList.push_back( NewOutMessage );

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

                    NewOutMessage->MessageFlags |= MessageFlag_ApplyExclusionLogic;           // 051903 CGP.  Are all these OMs excludable (ie susceptible to crosstalk)??
                    NewOutMessage->OutLength = MASTERLENGTH + Length;

                    /* Build MasterComm header */
                    if( status = MasterHeader (NewOutMessage->Buffer.OutMessage + PREIDLEN, NewOutMessage->Remote, MASTERSEND, Length) )
                    {
                        CTILOG_ERROR(dout, "MasterHeader Returned Error: "<< status);

                        delete NewOutMessage;
                    }
                    else
                    {
                        /* Copy message into buffer */
                        ::memcpy (NewOutMessage->Buffer.OutMessage + PREIDLEN + MASTERLENGTH, VSt.Message, Length);

                        /* Now add it to the collection of outbound messages */
                        outList.push_back( NewOutMessage );
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
                    CTILOG_ERROR(dout, "Cannot send versacom to TYPE: "<< _transmitterDevice->getType());

                    break;
                }
            }
        }
    }

    if(Versacom.entries() > 0)
    {
        resultString = CtiNumStr(Versacom.entries()) + " Versacom commands sent on route " + getName() + " \n" + byteString;
    }
    else
    {
        xmore = false;
        resultString = "Route " + getName() + " did not transmit Versacom commands";

        string desc, actn;

        desc = "Route: " + getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route";

        vgList.push_back(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
    }

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID,
                                                      string(OutMessage->Request.CommandStr),
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

    return status;
}

YukonError_t CtiRouteXCU::assembleRippleRequest(CtiRequestMsg               *pReq,
                                                CtiCommandParser            &parse,
                                                OUTMESS                     *&OutMessage,
                                                list< CtiMessage* >   &vgList,
                                                list< CtiMessage* >   &retList,
                                                list< OUTMESS* >      &outList)
{
    YukonError_t   status = ClientErrors::None;
    bool           xmore = true;
    string      resultString;
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
    OutMessage->MessageFlags |= MessageFlag_ApplyExclusionLogic;           // 051903 CGP.  Are all these OMs excludable (ie susceptible to crosstalk)??

    lcu->lcuControl( OutMessage );        // This will return NULL or a new OUTMESS

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

        string desc, actn;

        desc = "Route: " + getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route";

        vgList.push_back(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
    }

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID,
                                                      string(OutMessage->Request.CommandStr),
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

    if(OutMessage)
    {
        outList.push_back( OutMessage );         // Insert a copy someone else can clean up.
        OutMessage = NULL;
    }

    return status;
}


YukonError_t CtiRouteXCU::assembleFisherPierceRequest(CtiRequestMsg               *pReq,
                                                      CtiCommandParser            &parse,
                                                      OUTMESS                     *&OutMessage,
                                                      list< CtiMessage* >   &vgList,
                                                      list< CtiMessage* >   &retList,
                                                      list< OUTMESS* >      &outList)
{
    YukonError_t   status = ClientErrors::None;
    bool           xmore = true;
    string      resultString;
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
    ::memcpy(PCC->UID  , "001"     , 3);        /* Utility ID.... 001 is CP&L */
    ::memcpy(PCC->VID  , "001"     , 3);        /* Vendor ID      001 is FP   */
    ::memcpy(PCC->D    , "01"      , 2);        /* _transmitterDevice ID      01 is Capacitor Control */
    ::memcpy(PCC->VALUE, "000000"  , 6);        /* This is set on the calling side */

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
                    CTILOG_WARN(dout, "Transmitter Device Type is TYPE_WCTP"); // FIXME: is this unexpected?

                    // FALL THROUGH ???? Probably true....
                }
            case TYPE_TAPTERM:
                {
                    /* Calculate the length */
                    Length                              = 29;

                    NewOutMessage->TimeOut              = 2;
                    NewOutMessage->InLength             = -1;
                    NewOutMessage->OutLength            = Length;
                    NewOutMessage->Buffer.TAPSt.Length  = Length;

                    /* Build the message */
                    ::memcpy(NewOutMessage->Buffer.TAPSt.Message, FPSt.u.Message, 29);

                    if(getDebugLevel() & 0x00000002 )
                    {
                        std::ostringstream oss;

                        oss <<"FP-CBC TAP Message:"<< hex << setfill('0');

                        for(i = 0; i < Length; i++)
                        {
                            oss <<" "<< setw(2) << NewOutMessage->Buffer.TAPSt.Message[i];
                        }

                        CTILOG_DEBUG(dout, oss);
                    }

                    /* Now add it to the collection of outbound messages */
                    outList.push_back( NewOutMessage );

                    break;
                }
            default:
                {
                    CTILOG_ERROR(dout, "Invalid Transmitter Type ("<< _transmitterDevice->getType() <<")");

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

        string   desc, actn;

        desc = "Route: " + getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route";

        vgList.push_back(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
    }

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID,
                                                      string(OutMessage->Request.CommandStr),
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

    return status;
}

YukonError_t CtiRouteXCU::assembleExpresscomRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    YukonError_t   status = ClientErrors::None;
    bool           xmore = true;
    bool           isAscii = true;
    string         resultString;
    string         byteString;
    ULONG          i, j;

    /*
     * Addressing variables SHALL have been assigned at an earlier level!
     */

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID,
                                                      string(OutMessage->Request.CommandStr),
                                                      resultString,
                                                      status,
                                                      OutMessage->Request.RouteID,
                                                      OutMessage->Request.RetryMacroOffset,
                                                      OutMessage->Request.Attempt,
                                                      OutMessage->Request.GrpMsgID,
                                                      OutMessage->Request.UserID,
                                                      OutMessage->Request.SOE,
                                                      CtiMultiMsg_vec());

    //  the incoming device ID is the LM group that initiated this request, but we must save it from being overwritten
    int saveDevId = OutMessage->DeviceID;
    OutMessage->DeviceID = _transmitterDevice->getID();
    OutMessage->Port     = _transmitterDevice->getPortID();
    OutMessage->Remote   = _transmitterDevice->getAddress();
    OutMessage->TimeOut  = 2;
    OutMessage->InLength = -1;

    if(!OutMessage->Retry)  OutMessage->Retry = 2;

    CtiProtocolExpresscom  xcom;
    xcom.parseAddressing(parse);                    // The parse holds all the addressing for the group.
    status = xcom.parseRequest(parse);
    xcom.setUseASCII(true);

    // by default the CRC is used
    if( !gConfigParms.isTrue("YUKON_USE_EXPRESSCOM_CRC", true) )
    {
        xcom.setUseCRC(false);
    }

    const bool usingEncryption = ( _encryptionKey.size() == 16 );   // reject malformed keys

    if ( usingEncryption )
    {
        xcom.setUseCRC(false);
        xcom.setUseASCII(false);
        isAscii = false;
    }

    if(!status  && xcom.entries() > 0)
    {
        OutMessage->EventCode |= ENCODED;               // Make the OM be ignored by porter...

        switch(_transmitterDevice->getType())
        {
        case TYPE_RDS:
            {
                xcom.setUseASCII(false);
                isAscii = false;
                //RDS uniquely uses ascii, then falls through and does the rest identically to paging!
                // Possibly a ToDo, RDS may need to break apart messages to reduce the max message length.
                // This message length problem is similar to what is done for CCU expresscom messaging.

                if ( usingEncryption )
                {
                    if( xcom.messageSize() > RDS_MAX_EXPRESSCOM_LENGTH - 10 )   // encryption adds 10 bytes - no header and footer
                    {
                        xmore = false;
                        status = ClientErrors::BadRange;
                        resultString = "Message length was too large for an encrypted expresscom message in RDS. Length: " + CtiNumStr(xcom.messageSize() + 10) + " Maximum: " + CtiNumStr(RDS_MAX_EXPRESSCOM_LENGTH);
                        break;
                    }
                }
                else
                {
                    if ( xcom.messageSize() > RDS_MAX_EXPRESSCOM_LENGTH - 2 )   // header and footer byte
                    {
                        xmore = false;
                        status = ClientErrors::BadRange;
                        resultString = "Message length was too large for an expresscom message in RDS. Length: " + CtiNumStr(xcom.messageSize() + 2) + " Maximum: " + CtiNumStr(RDS_MAX_EXPRESSCOM_LENGTH);
                        break;
                    }
                }
            }
        case TYPE_SNPP:
        case TYPE_WCTP:
        case TYPE_TAPTERM:
        case TYPE_TNPP:
            {
                // BEGIN serialpatch here
                j = 0;
                string serialpatch;

                if(parse.getiValue("xcprefix", FALSE))
                {
                    serialpatch = parse.getsValue("xcprefixstr");
                }

                if(!serialpatch.empty() && (xcom.getByte(0) == 0))
                {
                    for(j = 0; j <= serialpatch.length(); j++)
                    {
                        OutMessage->Buffer.TAPSt.Message[j] = serialpatch[j];
                    }
                    j = serialpatch.length();
                }
                // END serialpatch

                // add the start byte if we are not RDS
                if ( _transmitterDevice->getType() != TYPE_RDS )
                {
                    OutMessage->Buffer.TAPSt.Message[j++] = ( usingEncryption ? 'w' : xcom.getStartByte() );
                }

                int messageStartIndex = j;
                int messageLength     = xcom.messageSize();

                // copy the message
                int curByte;
                for(i = 0; i < xcom.messageSize(); i++)
                {
                    curByte = xcom.getByte(i);
                    OutMessage->Buffer.TAPSt.Message[i + j] = curByte;
                }
                j += i;

                // add the stop byte if we are not RDS
                if ( _transmitterDevice->getType() != TYPE_RDS )
                {
                    OutMessage->Buffer.TAPSt.Message[j++] = ( usingEncryption ? 'x' : xcom.getStopByte() );
                }

                OutMessage->Buffer.TAPSt.Length = OutMessage->OutLength = j;

                OutMessage->Buffer.TAPSt.Message[j] = '\0';

                // create the string for the resultString
                for(i = 0; i < OutMessage->OutLength; i++)
                {
                    if ( isAscii || i < messageStartIndex || i >= ( messageStartIndex + messageLength ) )
                    {
                        byteString += (char)OutMessage->Buffer.TAPSt.Message[i];
                    }
                    else
                    {
                        byteString += CtiNumStr((unsigned char)OutMessage->Buffer.TAPSt.Message[i]).hex(2);
                    }
                }

                if ( usingEncryption )
                {
                    // set this flag so the transmitter knows to encrypt...

                    OutMessage->MessageFlags |= MessageFlag_EncryptionRequired;

                    // copy in our start/stop byte indexes

                    OutMessage->Buffer.TAPSt.Message[j++] = messageStartIndex;
                    OutMessage->Buffer.TAPSt.Message[j++] = messageLength;

                    // copy in the key

                    for (i = 0; i < 16; i++)
                    {
                        OutMessage->Buffer.TAPSt.Message[j + i] = _encryptionKey[i];
                    }
                    j += 16;

                    OutMessage->Buffer.TAPSt.Length = OutMessage->OutLength = j;
                }

                /* Now add it to the collection of outbound messages */
                outList.push_back( OutMessage );
                OutMessage = 0; // It has been used, don't let it be deleted!

                break;
            }
        default:
            {
                CTILOG_ERROR(dout, "Cannot send expresscom to TYPE: "<< _transmitterDevice->getType());

                break;
            }
        }

        if(status == ClientErrors::None)
        {
            resultString = CtiNumStr(xcom.entries()) + string(" Expresscom commands (") + CtiNumStr(xcom.messageSize()) + " bytes) sent on route " + getName();

            if(!gConfigParms.isTrue("HIDE_PROTOCOL"))
            {
                resultString += " \n" + byteString;
            }
        }
    }
    else
    {
        status = ClientErrors::BadParameter;
        xmore = false;
        resultString = "Route " + getName() + " did not transmit Expresscom commands. Error " + CtiNumStr(status) + " - " + CtiError::GetErrorString(status);

        string desc, actn;

        desc = "Route: " + getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route. Error " + CtiNumStr(status) + " - " + CtiError::GetErrorString(status);

        vgList.push_back(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
    }

    if(retReturn)
    {
        retReturn->setResultString( resultString );
        retReturn->setStatus( status );
        if(parse.isTwoWay()) retReturn->setExpectMore(xmore);
        retList.push_back(retReturn);
    }
    else
    {
        delete retReturn;
    }

    return status;
}

YukonError_t CtiRouteXCU::assembleSA305Request(CtiRequestMsg *pReq,
                                               CtiCommandParser &parse,
                                               OUTMESS *&OutMessage,
                                               list< CtiMessage* >   &vgList,
                                               list< CtiMessage* >   &retList,
                                               list< OUTMESS* >      &outList)
{
    YukonError_t   status = ClientErrors::None;
    bool           xmore = true;
    string      resultString;
    string      byteString;
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
                    /* Build the message */
                    Length = prot305.buildNumericPageMessage(NewOutMessage->Buffer.TAPSt.Message);

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
                    outList.push_back( NewOutMessage );
                    NewOutMessage = 0;

                    break;
                }
            case TYPE_RTC:
                {
                    NewOutMessage->EventCode = RESULT | ENCODED;
                    NewOutMessage->Buffer.SASt._groupType = SA305;

                    NewOutMessage->Buffer.SASt._bufferLen = prot305.buildHexMessage((char*)(NewOutMessage->Buffer.SASt._buffer));
                    NewOutMessage->OutLength = NewOutMessage->Buffer.SASt._bufferLen;

                    ::memcpy(NewOutMessage->Buffer.SASt._code305, (char*)(NewOutMessage->Buffer.SASt._buffer) ,NewOutMessage->Buffer.SASt._bufferLen);
                    NewOutMessage->Buffer.SASt._code305[NewOutMessage->Buffer.SASt._bufferLen + 1] = '\0';
                    strncpy(NewOutMessage->Request.CommandStr, parse.getCommandStr().c_str(),COMMAND_STR_SIZE);

                    for(i = 0; i < NewOutMessage->OutLength; i++)
                    {
                        byteString += CtiNumStr(NewOutMessage->Buffer.SASt._buffer[i]).hex().zpad(2) + " ";
                    }
                    byteString += "\n" + prot305.getBitString() + "\n";

                    outList.push_back( NewOutMessage );
                    NewOutMessage = 0;

                    resultString = " Command successfully sent on route " + getName() + " \n" + byteString;

                    break;
                }
            default:
                {
                    CTILOG_ERROR(dout, "Cannot send SA305 to TYPE: "<< _transmitterDevice->getType());

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
        resultString = " Command successfully sent on route " + getName() + " \n" + byteString;
    }
    else
    {
        xmore = false;
        resultString = "Route " + getName() + " did not transmit commands.  Syntax may be in error.";

        string desc, actn;

        desc = "Route: " + getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route";

        vgList.push_back(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
    }

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID,
                                                      string(OutMessage->Request.CommandStr),
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

    return status;
}

YukonError_t CtiRouteXCU::assembleSA105205Request(CtiRequestMsg *pReq,
                                                  CtiCommandParser &parse,
                                                  OUTMESS *&OutMessage,
                                                  list< CtiMessage* >   &vgList,
                                                  list< CtiMessage* >   &retList,
                                                  list< OUTMESS* >      &outList)
{
    YukonError_t   status = ClientErrors::None;
    bool           xmore = true;
    string      resultString;
    string      byteString;
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
                strncpy(NewOutMessage->Request.CommandStr, parse.getCommandStr().c_str() ,COMMAND_STR_SIZE);

                outList.push_back( NewOutMessage );

                prot.copyMessage(byteString);
                resultString = " Command successfully sent on route " + getName() + " \n" + byteString;
            }
            break;
        }
    case TYPE_WCTP:
    case TYPE_TAPTERM:
    default:
        {
            CTILOG_ERROR(dout, "Cannot send SA PROTOCOLS to TYPE: "<< _transmitterDevice->getType());

            break;
        }
    }

    if( resultString.empty() )
    {
        xmore = false;
        resultString = "Route " + getName() + " did not transmit commands";

        string desc, actn;

        desc = "Route: " + getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route";

        vgList.push_back(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
    }

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID,
                                                      string(OutMessage->Request.CommandStr),
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

    return status;
}

YukonError_t CtiRouteXCU::assembleSASimpleRequest(CtiRequestMsg *pReq,
                                                  CtiCommandParser &parse,
                                                  OUTMESS *&OutMessage,
                                                  list< CtiMessage* >   &vgList,
                                                  list< CtiMessage* >   &retList,
                                                  list< OUTMESS* >      &outList)
{
    YukonError_t   status = ClientErrors::None;
    bool           xmore = true;
    string      resultString;
    string      byteString;
    ULONG          i, j;

    OutMessage->DeviceID = _transmitterDevice->getID();
    OutMessage->Port     = _transmitterDevice->getPortID();
    OutMessage->TimeOut  = 2;
    OutMessage->InLength = -1;

    switch(_transmitterDevice->getType())
    {
    case TYPE_TNPP:
        {
            OutMessage->Sequence = CtiDeviceTnppPagingTerminal::TnppPublicProtocolGolay;
            string tempString = parse.getsValue("sa_codesimple").data();
            for(int z=0;z<6;z++)
            {
                OutMessage->Buffer.SASt._codeSimple[z] = tempString[z];
            }

            outList.push_back( CTIDBG_new OUTMESS( *OutMessage ) );

            resultString = " Command successfully sent on route " + getName() + " \n" + byteString;
            break;
        }
    case TYPE_SERIESVLMIRTU:
        {
            OutMessage->EventCode = RESULT | ENCODED;
            OutMessage->Buffer.SASt._groupType = GOLAY;
            strncpy(OutMessage->Buffer.SASt._codeSimple, parse.getsValue("sa_codesimple").data(), 7);
            OutMessage->Sequence = CtiProtocolLMI::Sequence_Code;  //  these are the only commands that can be queued to an LMI
            outList.push_back( CTIDBG_new OUTMESS( *OutMessage ) );

            resultString = " Command successfully sent on route " + getName() + " \n" + byteString;

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

                outList.push_back( CTIDBG_new OUTMESS( *OutMessage ) );

                prot.copyMessage(byteString);
                resultString = " Command successfully sent on route " + getName() + " \n" + byteString;
            }
            break;
        }
    case TYPE_WCTP:
    case TYPE_TAPTERM:
    default:
        {
            CTILOG_ERROR(dout, "Cannot send SA PROTOCOLS to TYPE: "<< _transmitterDevice->getType());

            break;
        }
    }

    if( resultString.empty() )
    {
        xmore = false;
        resultString = "Route " + getName() + " did not transmit commands";

        string desc, actn;

        desc = "Route: " + getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route";

        vgList.push_back(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
    }

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID,
                                                      string(OutMessage->Request.CommandStr),
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

    return status;
}


void CtiRouteXCU::enablePrefix(bool enable)
{
    if(_transmitterDevice)      // This is the pointer which refers this rte to its transmitter device.
    {
        switch(_transmitterDevice->getType())
        {
            case TYPE_WCTP:
            {
                auto &wctp = static_cast<CtiDeviceWctpTerminal &>(*_transmitterDevice);
                return wctp.setAllowPrefix(enable);
            }
            case TYPE_TAPTERM:
            {
                auto &tap = static_cast<Cti::Devices::TapPagingTerminal &>(*_transmitterDevice);
                return tap.setAllowPrefix(enable);
            }
        }
    }
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

