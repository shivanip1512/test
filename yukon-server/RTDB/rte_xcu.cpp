#include "precompiled.h"


#include <iostream>
#include <iomanip>
#include <cctype>

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

void CtiRouteXCU::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    INT iTemp;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
}

INT CtiRouteXCU::ExecuteRequest(CtiRequestMsg               *pReq,
                                CtiCommandParser            &parse,
                                OUTMESS                     *&OutMessage,
                                list< CtiMessage* >   &vgList,
                                list< CtiMessage* >   &retList,
                                list< OUTMESS* >      &outList)
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
                else if( parse.getiValue("type") == ProtocolEmetconType )
                {
                    status = !NORMAL;
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
                dout << CtiTime() << " ERROR: Route " << getName() << " has no associated transmitter device" << endl;
            }
            status = -1;
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return status;
}


INT CtiRouteXCU::assembleVersacomRequest(CtiRequestMsg               *pReq,
                                         CtiCommandParser            &parse,
                                         OUTMESS                     *&OutMessage,
                                         list< CtiMessage* >   &vgList,
                                         list< CtiMessage* >   &retList,
                                         list< OUTMESS* >      &outList)
{
    INT            status = NORMAL;
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
                    if((status = MasterHeader (NewOutMessage->Buffer.OutMessage + PREIDLEN, NewOutMessage->Remote, MASTERSEND, Length)) != NORMAL)
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " " << __FILE__ << " (" << __LINE__ << ") Error: " << status << endl;
                        }

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
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << "  Cannot send versacom to TYPE:" << /*desolveDeviceType(*/_transmitterDevice->getType()/*)*/ << endl;
                    }

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

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID, string(OutMessage->Request.CommandStr), resultString, status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());

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

INT CtiRouteXCU::assembleRippleRequest(CtiRequestMsg               *pReq,
                                       CtiCommandParser            &parse,
                                       OUTMESS                     *&OutMessage,
                                       list< CtiMessage* >   &vgList,
                                       list< CtiMessage* >   &retList,
                                       list< OUTMESS* >      &outList)
{
    INT            status = NORMAL;
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

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID, string(OutMessage->Request.CommandStr), resultString, status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());

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


INT CtiRouteXCU::assembleFisherPierceRequest(CtiRequestMsg               *pReq,
                                             CtiCommandParser            &parse,
                                             OUTMESS                     *&OutMessage,
                                             list< CtiMessage* >   &vgList,
                                             list< CtiMessage* >   &retList,
                                             list< OUTMESS* >      &outList)
{
    INT            status = NORMAL;
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
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
                    ::memcpy(NewOutMessage->Buffer.TAPSt.Message, FPSt.u.Message, 29);

                    if(getDebugLevel() & 0x00000002 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " FP-CBC TAP Message:  ";
                        for(i = 0; i < Length; i++)
                        {
                            dout << NewOutMessage->Buffer.TAPSt.Message[i];
                        }
                        dout << endl;
                    }

                    /* Now add it to the collection of outbound messages */
                    outList.push_back( NewOutMessage );

                    break;
                }
            default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

        string   desc, actn;

        desc = "Route: " + getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route";

        vgList.push_back(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
    }

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID, string(OutMessage->Request.CommandStr), resultString, status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());

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

INT CtiRouteXCU::assembleExpresscomRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT            status = NORMAL;
    bool           xmore = true;
    bool           isAscii = true;
    string      resultString;
    string      byteString;
    ULONG          i, j;
    USHORT         Length;

    /*
     * Addressing variables SHALL have been assigned at an earlier level!
     */

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID, string(OutMessage->Request.CommandStr), resultString, status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());

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

    const bool usingEncryption = hasStaticInfo( CtiTableStaticPaoInfo::Key_CPS_One_Way_Encryption_Key );

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
                        status = BADRANGE;
                        resultString = "Message length was too large for an encrypted expresscom message in RDS. Length: " + CtiNumStr(xcom.messageSize() + 10) + " Maximum: " + CtiNumStr(RDS_MAX_EXPRESSCOM_LENGTH);
                        break;
                    }
                }
                else
                {
                    if ( xcom.messageSize() > RDS_MAX_EXPRESSCOM_LENGTH - 2 )   // header and footer byte
                    {
                        xmore = false;
                        status = BADRANGE;
                        resultString = "Message length was too large for an expresscom message in RDS. Length: " + CtiNumStr(xcom.messageSize() + 2) + " Maximum: " + CtiNumStr(RDS_MAX_EXPRESSCOM_LENGTH);
                        break;
                    }
                }
            }
        case TYPE_SNPP:
        case TYPE_WCTP:
        case TYPE_TAPTERM:
            {
                if ( usingEncryption )
                {
                    isAscii = false;
                    xcom.setUseASCII(false);

                    OutMessage->OutLength            = xcom.messageSize();
                    OutMessage->Buffer.TAPSt.Length  = xcom.messageSize();
                }
                else
                {
                    OutMessage->OutLength            = xcom.messageSize() +  2;
                    OutMessage->Buffer.TAPSt.Length  = xcom.messageSize() +  2;
                }

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

                    OutMessage->OutLength            += serialpatch.length();
                    OutMessage->Buffer.TAPSt.Length  += serialpatch.length();

                    j = serialpatch.length();
                }
                // END serialpatch

                /* Build the message */
                if ( ! usingEncryption )
                {
                    OutMessage->Buffer.TAPSt.Message[j] = xcom.getStartByte();
                    ++j;
                }

                int curByte;
                for(i = 0; i < xcom.messageSize(); i++)
                {
                    curByte = xcom.getByte(i);
                    OutMessage->Buffer.TAPSt.Message[i + j] = curByte;
                }
                j += i;

                if ( ! usingEncryption )
                {
                    OutMessage->Buffer.TAPSt.Message[j] = xcom.getStopByte();
                    ++j;
                }
                OutMessage->Buffer.TAPSt.Message[j] = '\0';

                for(i = 0; i < OutMessage->OutLength; i++)
                {
                    if(isAscii)
                    {
                        byteString += (char)OutMessage->Buffer.TAPSt.Message[i];
                    }
                    else
                    {
                        if( ! usingEncryption && ( i == 0 || i == (OutMessage->OutLength - 1) ) )
                        {
                            byteString += (char)OutMessage->Buffer.TAPSt.Message[i];
                        }
                        else
                        {
                            byteString += CtiNumStr((unsigned char)OutMessage->Buffer.TAPSt.Message[i]).hex(2);
                        }
                    }
                }

                if ( usingEncryption )
                {
                    // add password and key and adjust lengths

                    std::string password = gConfigParms.getValueAsString("ONE_WAY_ENCRYPT_PASSWORD");
                    if ( password.length() == 0 )
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " - Missing One-Way Encryption Password" << endl;
                    }

                    char * endOfMsg = OutMessage->Buffer.TAPSt.Message + OutMessage->OutLength;

                    // truncate password to a max of 32 bytes

                    int truncatedPasswordLen = std::min( password.length(), 32u );

                    for ( int i = 0; i < truncatedPasswordLen; ++i )
                    {
                        *endOfMsg++ = password[i];
                    }
                    *endOfMsg++ = truncatedPasswordLen;

                    std::string key;
                    getStaticInfo( CtiTableStaticPaoInfo::Key_CPS_One_Way_Encryption_Key, key );

                    if ( key.length() == 40 )   // btw - this is enforced in the client
                    {
                        // ok - parse it
                        for ( int key_i = 0; key_i < 40; key_i += 2 )
                        {
                            char ascii_pair[3] = { key[ key_i ], key[ key_i + 1 ], 0 };

                            *endOfMsg++ = std::strtoul( ascii_pair, 0, 16 );
                        }
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " - One-Way Encryption Key - invalid length" << endl;
                    }

                    OutMessage->OutLength            += password.length() + 1 + 20;
                    OutMessage->Buffer.TAPSt.Length  =  OutMessage->OutLength;

                    // set this flag so the transmitter knows to encrypt...

                    OutMessage->MessageFlags |= MessageFlag_EncryptionRequired;
                }

                /* Now add it to the collection of outbound messages */
                outList.push_back( OutMessage );
                OutMessage = 0; // It has been used, don't let it be deleted!


                break;
            }
        case TYPE_TNPP:
            {
                OutMessage->OutLength            = xcom.messageSize() +  2;
                OutMessage->Buffer.TAPSt.Length  = xcom.messageSize() +  2;

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

                    OutMessage->OutLength            += serialpatch.length();
                    OutMessage->Buffer.TAPSt.Length  += serialpatch.length();

                    j = serialpatch.length();
                }
                // END serialpatch

                /* Build the message */
                OutMessage->Buffer.TAPSt.Message[j] = xcom.getStartByte();

                int curByte;
                for(i = 0; i < xcom.messageSize(); i++)
                {
                    curByte = xcom.getByte(i);
                    OutMessage->Buffer.TAPSt.Message[i + 1 + j] = curByte;
                }
                OutMessage->Buffer.TAPSt.Message[i + 1 + j] = xcom.getStopByte();
                OutMessage->Buffer.TAPSt.Message[i + 2 + j] = '\0';

                for(i = 0; i < OutMessage->OutLength; i++)
                {
                    if(isAscii)
                    {
                        byteString += (char)OutMessage->Buffer.TAPSt.Message[i];
                    }
                    else
                    {
                        if(i == 0 || i == (OutMessage->OutLength - 1))
                        {
                            byteString += (char)OutMessage->Buffer.TAPSt.Message[i];
                        }
                        else
                        {
                            byteString += CtiNumStr((unsigned char)OutMessage->Buffer.TAPSt.Message[i]).hex(2);
                        }
                    }
                }


                /* Now add it to the collection of outbound messages */
                outList.push_back( OutMessage );
                OutMessage = 0; // It has been used, don't let it be deleted!


                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << "  Cannot send expresscom to TYPE:" << /*desolveDeviceType(*/_transmitterDevice->getType()/*)*/ << endl;
                }

                break;
            }
        }

        if(status == NORMAL)
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
        status = BADPARAM;
        xmore = false;
        resultString = "Route " + getName() + " did not transmit Expresscom commands. Error " + CtiNumStr(status) + " - " + FormatError(status);

        string desc, actn;

        desc = "Route: " + getName();
        actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route. Error " + CtiNumStr(status) + " - " + FormatError(status);

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

INT CtiRouteXCU::assembleSA305Request(CtiRequestMsg *pReq,
                                      CtiCommandParser &parse,
                                      OUTMESS *&OutMessage,
                                      list< CtiMessage* >   &vgList,
                                      list< CtiMessage* >   &retList,
                                      list< OUTMESS* >      &outList)
{
    INT            status = NORMAL;
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
                    CtiDeviceTapPagingTerminal *TapDev = (CtiDeviceTapPagingTerminal *)(_transmitterDevice.get());

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
            case TYPE_SERIESVLMIRTU:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }
            default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << "  Cannot send SA305 to TYPE:" << _transmitterDevice->getType() << endl;
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

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID, string(OutMessage->Request.CommandStr), resultString, status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());

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

INT CtiRouteXCU::assembleSA105205Request(CtiRequestMsg *pReq,
                                         CtiCommandParser &parse,
                                         OUTMESS *&OutMessage,
                                         list< CtiMessage* >   &vgList,
                                         list< CtiMessage* >   &retList,
                                         list< OUTMESS* >      &outList)
{
    INT            status = NORMAL;
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
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << CtiTime() << "  Cannot send SA PROTOCOLS to TYPE: " << _transmitterDevice->getType() << endl;
            }

            break;
        }
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << CtiTime() << "  Cannot send to TYPE:" << _transmitterDevice->getType() << endl;
            }

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

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID, string(OutMessage->Request.CommandStr), resultString, status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());

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

INT CtiRouteXCU::assembleSASimpleRequest(CtiRequestMsg *pReq,
                                         CtiCommandParser &parse,
                                         OUTMESS *&OutMessage,
                                         list< CtiMessage* >   &vgList,
                                         list< CtiMessage* >   &retList,
                                         list< OUTMESS* >      &outList)
{
    INT            status = NORMAL;
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
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << CtiTime() << "  Cannot send SA PROTOCOLS to TYPE:" << _transmitterDevice->getType() << endl;
            }

            break;
        }
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << CtiTime() << "  Cannot send SA PROTOCOLS to TYPE:" << _transmitterDevice->getType() << endl;
            }

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

    CtiReturnMsg *retReturn = CTIDBG_new CtiReturnMsg(OutMessage->TargetID, string(OutMessage->Request.CommandStr), resultString, status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());

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

