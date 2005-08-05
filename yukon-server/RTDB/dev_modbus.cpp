/*-----------------------------------------------------------------------------*
*
* File:   dev_modbus
*
* Date:   7/19/2005
*
* Author: Jess Otteson
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/08/05 20:01:42 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include <map>
#include <string>
using namespace std;

#include <windows.h>

#include "dsm2.h"
#include "porter.h"

#include "pt_base.h"
#include "pt_numeric.h"
#include "pt_status.h"
#include "pt_accum.h"
#include "master.h"
#include "dllyukon.h"

#include "pointtypes.h"
#include "mgr_route.h"
#include "mgr_point.h"
#include "msg_cmd.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_lmcontrolhistory.h"
#include "cmdparse.h"
#include "dev_modbus.h"
#include "prot_modbus.h"
#include "device.h"
#include "logger.h"
#include "numstr.h"
#include "cparms.h"

namespace Cti       {
namespace Device    {

Modbus::Modbus(void)
{
}

Modbus::Modbus(const Modbus &aRef)
{
   *this = aRef;
}

Modbus::~Modbus() {}

Modbus &Modbus::operator=(const Modbus &aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);
   }
   return *this;
}

INT Modbus::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan general");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** GeneralScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pReq->setCommandString("scan general");
    pReq->setMessagePriority(ScanPriority);

    status = ExecuteRequest(pReq, newParse, OutMessage, vgList, retList, outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}


INT Modbus::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList,RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)
{
    INT status = NORMAL;
    CtiCommandParser newParse("scan integrity");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** IntegrityScan for \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    pReq->setCommandString("scan integrity");
    pReq->setMessagePriority(ScanPriority);

    status = ExecuteRequest(pReq, newParse, OutMessage, vgList, retList, outList);

    if(OutMessage)
    {
        delete OutMessage;
        OutMessage = 0;
    }

    return status;
}


INT Modbus::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT nRet = NoMethod;

    Protocol::Modbus::Command command = Protocol::Modbus::Command_Error;
    Protocol::Modbus::output_point controlout;
    pseudo_info p_i = {false, -1, -1};

    switch( parse.getCommand() )
    {


        case ScanRequest:
        {
            switch( parse.getiValue("scantype") )
            {
                case ScanRateGeneral:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - General scan not defined for Modbus device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    break;
                }

                case ScanRateAccum:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - Accumulator scanrates not defined for Modbus device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }

                    break;
                }

                case ScanRateIntegrity:
                {
                    command = Protocol::Modbus::Command::Command_ScanALL;

                    break;
                }
            }

            break;
        }
        case PutValueRequest:
        case PutConfigRequest:
        case GetConfigRequest:
        case ControlRequest:
        case GetValueRequest:
        case GetStatusRequest:
        case PutStatusRequest:
        case LoopbackRequest:
        default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - command type \"" << parse.getCommand() << "\" not found **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    if( command != Protocol::Modbus::Command_Invalid )
    {
        _pil_info.protocol_command   = command;
 //       _pil_info.protocol_parameter = controlout;
        _pil_info.pseudo_info        = p_i;
        _pil_info.user               = pReq->getUser();

        OutMessage->Port     = getPortID();
        OutMessage->DeviceID = getID();
        OutMessage->TargetID = getID();
        EstablishOutMessagePriority(OutMessage, pReq->getMessagePriority());

        sendCommRequest(OutMessage, outList);

        nRet = NoError;
    }
    else
    {
        delete OutMessage;
        OutMessage = NULL;
    }

    return nRet;
}


Protocol::Interface *Modbus::getProtocol()
{
    return &_modbus;
}


int Modbus::generate(CtiXfer &xfer)
{
    return _modbus.generate(xfer);
}


int Modbus::decode(CtiXfer &xfer, int status)
{
    int retval = NoError;

    retval = _modbus.decode(xfer, status);

    if( _modbus.isTransactionComplete() )
    {
        const Protocol::Modbus::output_point &op = _porter_info.protocol_parameter;
    }

    return retval;
}


int Modbus::sendCommRequest( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList )
{
    int retVal = NoError;

    //  write the outmess_header
    outmess_header *om_buf = reinterpret_cast<outmess_header *>(OutMessage->Buffer.OutMessage);
    om_buf->command     = _pil_info.protocol_command;
    om_buf->parameter   = _pil_info.protocol_parameter;
    om_buf->pseudo_info = _pil_info.pseudo_info;

    char *buf = reinterpret_cast<char *>(OutMessage->Buffer.OutMessage) + sizeof(outmess_header);
    strncpy(buf, _pil_info.user.data(), 127);
    buf[127] = 0;  //  max of 128, just because i feel like it

    if( OutMessage )
    {
        //  assign all of the standard OM bits
        OutMessage->OutLength    = sizeof(om_buf) + strlen(buf) + 1;  //  plus null
        OutMessage->Destination  = _modbus_address.getSlaveAddress();
        OutMessage->EventCode    = RESULT;
        OutMessage->Retry        = Protocol::Modbus::Retries_Default;

        outList.append(OutMessage);
        OutMessage = 0;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - invalid OutMessage in DNPInterface::sendCommRequest() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        retVal = MemoryError;
    }

    return retVal;
}


int Modbus::recvCommRequest( OUTMESS *OutMessage )
{
    int retVal = NoError;

    if( OutMessage )
    {
        outmess_header *om_buf = reinterpret_cast<outmess_header *>(OutMessage->Buffer.OutMessage);

        //  we keep a copy for the decode later on
        _porter_info.protocol_command   = om_buf->command;
        _porter_info.protocol_parameter = om_buf->parameter;
        _porter_info.pseudo_info        = om_buf->pseudo_info;

        char *buf = reinterpret_cast<char *>(OutMessage->Buffer.OutMessage) + sizeof(outmess_header);
        buf[127] = 0;             //  make sure it's null-terminated somewhere before we do this:
        _porter_info.user = buf;  //  ooo, how daring

        _modbus.setAddresses(OutMessage->Destination);
        _modbus.clearPoints();
        _modbus.setCommand(_porter_info.protocol_command);

        CtiPoint *PointRecord;

        switch(_porter_info.protocol_command)
        {
            case Protocol::Modbus::Command_ScanALL :
                {
                    try
                    {
                        if(_pointMgr == NULL)      // Attached via the dev_base object.
                        {
                            RefreshDevicePoints(  );
                        }
                
                        if(_pointMgr != NULL)
                        {
                            LockGuard guard(monitor());
                
                            /* Walk the point in memory db to see what the point range is */
                            CtiRTDB<CtiPoint>::CtiRTDBIterator   itr_pt(_pointMgr->getMap());

                            while(++itr_pt)
                            {
                                PointRecord = itr_pt.value();
                
                                switch(PointRecord->getType())
                                {
                                case StatusPointType:
                                    {
                                        CtiPointStatus *StatusPoint = (CtiPointStatus *)PointRecord;
                
                                        if(StatusPoint->getPointOffset()>0)
                                        {
                                            _modbus.addStatusPoint(StatusPoint->getPointOffset());
                                        }
                                        break;
                                    }
                                case AnalogPointType:
                                    {
                                        CtiPointStatus *StatusPoint = (CtiPointStatus *)PointRecord;
                
                                        if(StatusPoint->getPointOffset()>0)
                                        {
                                            _modbus.addAnalogPoint(StatusPoint->getPointOffset());
                                        }
                                        break;
                                    }
                                default:
                                    {
                                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                                        dout << RWTime() << " **** Checkpoint - An unexpected point (PID: "<<PointRecord->getPointID()<<") was seen " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    }
                    catch (...)
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** Checkpoint - An exception was thrown " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        _modbus.clearPoints();
                        retVal = UnknownError;
						break;
                    }
                }
               
            default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - invalid command in Modbus::recvCommRequest() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                
            }
        }

    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint - invalid OutMessage in Modbus::recvCommResult() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        retVal = MemoryError;
    }

    return retVal;
}


int Modbus::sendCommResult(INMESS *InMessage)
{
    int retval = NoError;

    char *buf;
    int offset;
    string result_string;
    Protocol::Modbus::stringlist_t strings;
    Protocol::Modbus::stringlist_t::iterator itr;

    buf = reinterpret_cast<char *>(InMessage->Buffer.InMessage);
    offset = 0;

    _modbus.getInboundStrings(strings);

    //  this needs to be smarter and send the device name and point data elements seperately...
    for( itr = strings.begin(); itr != strings.end(); itr++ )
    {
        result_string += getName().data();
        result_string += " / ";
        result_string += *(*itr);
        result_string += "\n";
    }

    while( !strings.empty() )
    {
        delete strings.back();

        strings.pop_back();
    }

    //  ... as does this
    for( itr = _string_results.begin(); itr != _string_results.end(); itr++ )
    {
        result_string += *(*itr);
        result_string += "\n";
    }

    while( !_string_results.empty() )
    {
        delete _string_results.back();

        _string_results.pop_back();
    }

    if( result_string.size() >= sizeof(InMessage->Buffer.InMessage) )
    {
        //  make sure we complain about it so we know the magnitude of the problem when people bring it up...
        //    one possible alternative is to send multple InMessages across with the string data - although,
        //    considering that the largest message I saw was on the order of 60k, sending 15 InMessages is not very appealing
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Info - result_string.size = " << result_string.size() << " for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        string cropped("\n---cropped---");

        //  erase the end chunk so we can append the "cropped" string in
        result_string.erase(sizeof(InMessage->Buffer.InMessage) - cropped.size() - 1, result_string.size());
        result_string += cropped;
    }

    InMessage->InLength = result_string.size() + 1;

    //  make sure we don't overrun the buffer, even though we just checked above
    strncpy(buf, result_string.c_str(), sizeof(InMessage->Buffer.InMessage) - 1);
    //  and mark the end with a null, again, just to be sure
    InMessage->Buffer.InMessage[sizeof(InMessage->Buffer.InMessage) - 1] = 0;

    return retval;
}


void Modbus::sendDispatchResults(CtiConnection &vg_connection)
{
    CtiReturnMsg                *vgMsg;
    CtiPointDataMsg             *pt_msg;
    CtiPointBase                *point;
    CtiPointNumeric             *pNumeric;
    RWCString                    resultString;
    RWTime                       Now;

    Protocol::Interface::pointlist_t points;
    Protocol::Interface::pointlist_t::iterator itr;

    vgMsg  = CTIDBG_new CtiReturnMsg(getID());  //  , InMessage->Return.CommandStr

    double tmpValue;

    _modbus.getInboundPoints(points);

    //  do any device-dependent work on the points (CBC 6510, for example)
    processPoints(points);

    //  then toss them into the return msg
    for( itr = points.begin(); itr != points.end(); itr++ )
    {
        pt_msg = *itr;

        if( pt_msg )
        {
            _string_results.push_back(CTIDBG_new string(pt_msg->getString()));

            vgMsg->PointData().append(pt_msg);
        }
    }

    points.erase(points.begin(), points.end());

    //  now send the pseudos related to the control point
    //    note:  points are the domain of the device, not the protocol,
    //           so i have to handle pseudo points here, in the device code
/*    switch( _porter_info.protocol_command )
    {
        case Protocol::Modbus::Command_SetDigitalOut_Direct:
        case Protocol::Modbus::Command_SetDigitalOut_SBO_Select:  //  presumably this will transition...  we need to verify this...
        case Protocol::Modbus::Command_SetDigitalOut_SBO_Operate:
        {
            if( _porter_info.pseudo_info.is_pseudo  )  //  ... for example, make sure the control was successful
           {
                CtiPointDataMsg *msg = CTIDBG_new CtiPointDataMsg(_porter_info.pseudo_info.pointid,
                                                                  _porter_info.pseudo_info.state,
                                                                  NormalQuality,
                                                                  StatusPointType,
                                                                  "This point has been controlled");
                msg->setUser(_porter_info.user.data());
                vgMsg->PointData().append(msg);
            }

            break;
        }
    }*/

    vg_connection.WriteConnQue(vgMsg);
}


void Modbus::processPoints( Protocol::Interface::pointlist_t &points )
{
    Protocol::Interface::pointlist_t::iterator itr;
    CtiPointDataMsg *msg;
    CtiPoint *point;
    RWCString resultString;


    for( itr = points.begin(); itr != points.end(); itr++ )
    {
        msg = *itr;

        //  !!! msg->getId() is actually returning the offset !!!  because only the offset and type are known in the protocol object
        if( msg && (point = getDevicePointOffsetTypeEqual(msg->getId(), msg->getType())) )
        {
            //  NOTE:  we had to retrieve the actual pointid by offset+type (see above), so we assign the actual id now
            msg->setId(point->getID());//assigns actual id to point in pointlist!!

            if( point->isNumeric() )
            {
                CtiPointNumeric *pNumeric = (CtiPointNumeric *)point;

                msg->setValue(pNumeric->computeValueForUOM(msg->getValue()));

                resultString  = getName() + " / " + point->getName() + ": " + CtiNumStr(msg->getValue(), ((CtiPointNumeric *)point)->getPointUnits().getDecimalPlaces());
                resultString += " @ " + msg->getTime().asString();
            }
            else if( point->isStatus() )
            {
                resultString  = getName() + " / " + point->getName() + ": " + ResolveStateName(((CtiPointStatus *)point)->getStateGroupID(), msg->getValue());
                resultString += " @ " + msg->getTime().asString();
            }
            else
            {
                resultString = "";
            }

            msg->setString(resultString);
        }
        else
        {
            delete *itr;

            *itr = 0;
        }
    }
}


INT Modbus::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;
    RWTPtrSlist<CtiPointDataMsg> dnpPoints;

    CtiReturnMsg *retMsg;

    if( !ErrReturn )
    {
        string result_string;

        //  safety first
        if( InMessage->InLength > sizeof(InMessage->Buffer.InMessage) )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint InMessage->InLength > sizeof(InMessage->Buffer.InMessage) for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }

            InMessage->InLength = sizeof(InMessage->Buffer.InMessage);
        }
        InMessage->Buffer.InMessage[InMessage->InLength - 1] = 0;

        result_string.assign(reinterpret_cast<char *>(InMessage->Buffer.InMessage), InMessage->InLength);

        retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                         RWCString(InMessage->Return.CommandStr),
                                         result_string.data(),
                                         InMessage->EventCode & 0x7fff,
                                         InMessage->Return.RouteID,
                                         InMessage->Return.MacroOffset,
                                         InMessage->Return.Attempt,
                                         InMessage->Return.TrxID,
                                         InMessage->Return.UserID);

        retList.append(retMsg);
    }
    else
    {
        char error_str[80];
        RWCString resultString;

        GetErrorString(ErrReturn, error_str);

        resultString = getName() + " / operation failed \"" + error_str + "\" (" + RWCString(CtiNumStr(ErrReturn).xhex().zpad(2)) + ")";

        retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                         RWCString(InMessage->Return.CommandStr),
                                         resultString,
                                         InMessage->EventCode & 0x7fff,
                                         InMessage->Return.RouteID,
                                         InMessage->Return.MacroOffset,
                                         InMessage->Return.Attempt,
                                         InMessage->Return.TrxID,
                                         InMessage->Return.UserID);

        retList.append(retMsg);
    }

    return ErrReturn;
}


INT Modbus::ErrorDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
{
    INT retCode = NORMAL;

    CtiCommandParser  parse(InMessage->Return.CommandStr);
    CtiReturnMsg     *pPIL = CTIDBG_new CtiReturnMsg(getID(),
                                              RWCString(InMessage->Return.CommandStr),
                                              RWCString(),
                                              InMessage->EventCode & 0x7fff,
                                              InMessage->Return.RouteID,
                                              InMessage->Return.MacroOffset,
                                              InMessage->Return.Attempt,
                                              InMessage->Return.TrxID,
                                              InMessage->Return.UserID);
    CtiPointDataMsg  *commFailed;
    CtiPointBase     *commPoint;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Error decode for device " << getName() << " in progress " << endl;
    }

    if( pPIL != NULL )
    {
        CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

        if(pMsg != NULL)
        {
            pMsg->insert( -1 );             // This is the dispatch token and is unimplemented at this time
            pMsg->insert(OP_DEVICEID);      // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
            pMsg->insert(getID());          // The id (device or point which failed)
            pMsg->insert(ScanRateInvalid);  // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

            if(InMessage->EventCode != 0)
            {
                pMsg->insert(InMessage->EventCode);
            }
            else
            {
                pMsg->insert(GeneralScanAborted);
            }

            retList.insert( pMsg );
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return retCode;
}



/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
RWCString Modbus::getDescription(const CtiCommandParser &parse) const
{
   return getName();
}


void Modbus::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   Inherited::getSQL(db, keyTable, selector);
   CtiTableDeviceAddress::getSQL(db, keyTable, selector);
}

void Modbus::DecodeDatabaseReader(RWDBReader &rdr)
{
   Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
   _modbus_address.DecodeDatabaseReader(rdr);

   if( getDebugLevel() & DEBUGLEVEL_DATABASE )
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   _modbus.setAddresses(_modbus_address.getSlaveAddress());
}

}
}

