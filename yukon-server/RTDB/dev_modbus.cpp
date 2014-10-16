#include "precompiled.h"

#include <map>
#include <string>

#include "dsm2.h"
#include "porter.h"

#include "pt_base.h"
#include "pt_numeric.h"
#include "pt_status.h"
#include "pt_analog.h"
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
#include "logger.h"
#include "numstr.h"
#include "cparms.h"

using Cti::Protocols::ModbusProtocol;
using std::string;
using std::endl;
using std::list;

namespace Cti {
namespace Devices {

ModbusDevice::ModbusDevice(void)
{
}

YukonError_t ModbusDevice::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  CtiMessageList &vgList,CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;
    CtiCommandParser newParse("scan general");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CTILOG_DEBUG(dout, "GeneralScan for \"" << getName() << "\"");
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


YukonError_t ModbusDevice::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  CtiMessageList &vgList,CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;
    CtiCommandParser newParse("scan integrity");

    if( getDebugLevel() & DEBUGLEVEL_SCANTYPES )
    {
        CTILOG_DEBUG(dout, "IntegrityScan for \"" << getName() << "\"");
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


YukonError_t ModbusDevice::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t nRet = ClientErrors::NoMethod;

    ModbusProtocol::Command command = ModbusProtocol::Command_Error;
    ModbusProtocol::output_point controlout;
    pseudo_info p_i = {false, -1, -1};

    switch( parse.getCommand() )
    {


        case ScanRequest:
        {
            switch( parse.getiValue("scantype") )
            {
                case ScanRateGeneral:
                {
                    CTILOG_ERROR(dout, "General scan not defined for Modbus device \"" << getName() << "\"");

                    break;
                }

                case ScanRateAccum:
                {
                    CTILOG_ERROR(dout, "Accumulator scanrates not defined for Modbus device \"" << getName() << "\"");

                    break;
                }

                case ScanRateIntegrity:
                {
                    command = ModbusProtocol::Command_ScanALL;

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
            CTILOG_ERROR(dout, "command type \"" << parse.getCommand() << "\" not found");
        }
    }

    if( command != ModbusProtocol::Command_Invalid )
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

        nRet = ClientErrors::None;
    }
    else
    {
        delete OutMessage;
        OutMessage = NULL;
    }

    return nRet;
}


Protocol::Interface *ModbusDevice::getProtocol()
{
    return &_modbus;
}


YukonError_t ModbusDevice::generate(CtiXfer &xfer)
{
    return _modbus.generate(xfer);
}


YukonError_t ModbusDevice::decode(CtiXfer &xfer, YukonError_t status)
{
    YukonError_t retval = _modbus.decode(xfer, status);

    if( _modbus.isTransactionComplete() )
    {
        const ModbusProtocol::output_point &op = _porter_info.protocol_parameter;
    }

    return retval;
}


YukonError_t ModbusDevice::sendCommRequest( OUTMESS *&OutMessage, OutMessageList &outList )
{
    YukonError_t retVal = ClientErrors::None;

    if( OutMessage )
    {
        //  write the outmess_header
        outmess_header *om_buf = reinterpret_cast<outmess_header *>(OutMessage->Buffer.OutMessage);
        om_buf->command     = _pil_info.protocol_command;
        om_buf->parameter   = _pil_info.protocol_parameter;
        om_buf->pseudo_info = _pil_info.pseudo_info;

        char *buf = reinterpret_cast<char *>(OutMessage->Buffer.OutMessage) + sizeof(outmess_header);
        strncpy(buf, _pil_info.user.data(), 127);
        buf[127] = 0;  //  max of 128, just because i feel like it

        //  assign all of the standard OM bits
        OutMessage->OutLength    = sizeof(om_buf) + strlen(buf) + 1;  //  plus null
        OutMessage->Destination  = _modbus_address.getSlaveAddress();
        OutMessage->EventCode    = RESULT;
        OutMessage->Retry        = ModbusProtocol::Retries_Default;

        outList.push_back(OutMessage);
        OutMessage = 0;
    }
    else
    {
        CTILOG_ERROR(dout, "NULL OutMessage");
        retVal = ClientErrors::Memory;
    }

    return retVal;
}


YukonError_t ModbusDevice::recvCommRequest( OUTMESS *OutMessage )
{
    YukonError_t retVal = ClientErrors::None;

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

        switch(_porter_info.protocol_command)
        {
            case ModbusProtocol::Command_ScanALL:
            {
                try
                {
                    std::vector<CtiPointManager::ptr_type> points;

                    getDevicePoints(points);

                    if( points.empty() )
                    {
                        return ClientErrors::NoPointsOnDevice;
                    }

                    std::vector<CtiPointManager::ptr_type>::iterator itr;

                    for( itr = points.begin(); itr != points.end(); itr++ )
                    {
                        CtiPointManager::ptr_type PointRecord = *itr;

                        switch(PointRecord->getType())
                        {
                            case StatusPointType:
                            {
                                CtiPointStatusSPtr StatusPoint = boost::static_pointer_cast<CtiPointStatus>(PointRecord);

                                if(StatusPoint->getPointOffset()>0)
                                {
                                    _modbus.addStatusPoint(StatusPoint->getPointOffset());
                                }
                                break;
                            }
                            case AnalogPointType:
                            {
                                CtiPointAnalogSPtr AnalogPoint = boost::static_pointer_cast<CtiPointAnalog>(PointRecord);

                                if(AnalogPoint->getPointOffset()>0)
                                {
                                    _modbus.addAnalogPoint(AnalogPoint->getPointOffset());
                                }
                                break;
                            }
                            default:
                            {
                                CTILOG_ERROR(dout, "An unexpected point (PID: "<< PointRecord->getPointID() <<") was seen");

                                break;
                            }
                        }
                    }
                    break;
                }
                catch (...)
                {
                    CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);

                    _modbus.clearPoints();
                    retVal = ClientErrors::Unknown;

                    break;
                }
            }

            default:
            {
                CTILOG_ERROR(dout, "Invalid command = "<< _porter_info.protocol_command);
            }
        }

    }
    else
    {
        CTILOG_ERROR(dout, "NULL OutMessage");

        retVal = ClientErrors::Memory;
    }

    return retVal;
}


YukonError_t ModbusDevice::sendCommResult(INMESS &InMessage)
{
    char *buf;
    int offset;
    string result_string;
    ModbusProtocol::stringlist_t strings;
    ModbusProtocol::stringlist_t::iterator itr;

    buf = reinterpret_cast<char *>(InMessage.Buffer.InMessage);
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

    if( result_string.size() >= sizeof(InMessage.Buffer.InMessage) )
    {
        //  make sure we complain about it so we know the magnitude of the problem when people bring it up...
        //    one possible alternative is to send multple InMessages across with the string data - although,
        //    considering that the largest message I saw was on the order of 60k, sending 15 InMessages is not very appealing
        CTILOG_WARN(dout, "result_string.size = " << result_string.size() << " for device \"" << getName() << "\" will be cropped");

        string cropped("\n---cropped---");

        //  erase the end chunk so we can append the "cropped" string in
        result_string.erase(sizeof(InMessage.Buffer.InMessage) - cropped.size() - 1, result_string.size());
        result_string += cropped;
    }

    InMessage.InLength = result_string.size() + 1;

    //  make sure we don't overrun the buffer, even though we just checked above
    strncpy(buf, result_string.c_str(), sizeof(InMessage.Buffer.InMessage) - 1);
    //  and mark the end with a null, again, just to be sure
    InMessage.Buffer.InMessage[sizeof(InMessage.Buffer.InMessage) - 1] = 0;

    return ClientErrors::None;
}


void ModbusDevice::sendDispatchResults(CtiConnection &vg_connection)
{
    CtiReturnMsg                *vgMsg;
    CtiPointDataMsg             *pt_msg;
    CtiPointSPtr                point;
    CtiPointNumericSPtr         pNumeric;
    string                    resultString;
    CtiTime                       Now;

    Protocol::Interface::pointlist_t points;
    Protocol::Interface::pointlist_t::iterator itr;

    vgMsg  = CTIDBG_new CtiReturnMsg(getID());  //  , InMessage.Return.CommandStr

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

            vgMsg->PointData().push_back(pt_msg);
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


void ModbusDevice::processPoints( Protocol::Interface::pointlist_t &points )
{
    Protocol::Interface::pointlist_t::iterator itr;
    CtiPointDataMsg *msg;
    CtiPointSPtr point;
    string resultString;


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
                CtiPointNumericSPtr pNumeric = boost::static_pointer_cast<CtiPointNumeric>(point);

                msg->setValue(pNumeric->computeValueForUOM(msg->getValue()));

                resultString  = getName() + " / " + point->getName() + ": " + CtiNumStr(msg->getValue(), boost::static_pointer_cast<CtiPointNumeric>(point)->getPointUnits().getDecimalPlaces());
                resultString += " @ " + msg->getTime().asString();
            }
            else if( point->isStatus() )
            {
                resultString  = getName() + " / " + point->getName() + ": " + ResolveStateName(boost::static_pointer_cast<CtiPointStatus>(point)->getStateGroupID(), msg->getValue());
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


YukonError_t ModbusDevice::ResultDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    const YukonError_t ErrReturn = InMessage.ErrorCode;

    CtiReturnMsg *retMsg;

    if( !ErrReturn )
    {
        string result_string;

        unsigned long length = InMessage.InLength;
        //  safety first
        if( InMessage.InLength > sizeof(InMessage.Buffer.InMessage) )
        {
            CTILOG_ERROR(dout, "InMessage.InLength > sizeof(InMessage.Buffer.InMessage) for device \""<< getName() <<"\" ("<< InMessage.InLength <<" > "<< sizeof(InMessage.Buffer.InMessage) <<")");

            length = sizeof(InMessage.Buffer.InMessage);
        }

        result_string.assign(InMessage.Buffer.InMessage,
                             InMessage.Buffer.InMessage + length);

        retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                         string(InMessage.Return.CommandStr),
                                         result_string.data(),
                                         InMessage.ErrorCode,
                                         InMessage.Return.RouteID,
                                         InMessage.Return.RetryMacroOffset,
                                         InMessage.Return.Attempt,
                                         InMessage.Return.GrpMsgID,
                                         InMessage.Return.UserID);

        retList.push_back(retMsg);
    }
    else
    {
        string resultString;

        const string error_str = GetErrorString(ErrReturn);

        resultString = getName() + " / operation failed \"" + error_str + "\" (" + string(CtiNumStr(ErrReturn).xhex().zpad(2)) + ")";

        retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                         string(InMessage.Return.CommandStr),
                                         resultString,
                                         InMessage.ErrorCode,
                                         InMessage.Return.RouteID,
                                         InMessage.Return.RetryMacroOffset,
                                         InMessage.Return.Attempt,
                                         InMessage.Return.GrpMsgID,
                                         InMessage.Return.UserID);

        retList.push_back(retMsg);
    }

    return ErrReturn;
}


YukonError_t ModbusDevice::ErrorDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &retList)
{
    CTILOG_INFO(dout, "ErrorDecode for device "<< getName() <<" in progress");

    CtiCommandMsg *pMsg = new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

    pMsg->insert( -1 );             // This is the dispatch token and is unimplemented at this time
    pMsg->insert(CtiCommandMsg::OP_DEVICEID);      // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
    pMsg->insert(getID());          // The id (device or point which failed)
    pMsg->insert(ScanRateInvalid);  // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

    pMsg->insert(
            InMessage.ErrorCode
                ? InMessage.ErrorCode
                : ClientErrors::GeneralScanAborted);

    retList.push_back( pMsg );

    return ClientErrors::None;
}



/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
string ModbusDevice::getDescription(const CtiCommandParser &parse) const
{
   return getName();
}

string ModbusDevice::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                     "DV.deviceid, DV.alarminhibit, DV.controlinhibit, CS.portid, DUS.phonenumber, "
                                     "DUS.minconnecttime, DUS.maxconnecttime, DUS.linesettings, DUS.baudrate, "
                                     "AD.masteraddress, AD.slaveaddress, AD.postcommwait "
                                   "FROM Device DV, DeviceAddress AD, DeviceDirectCommSettings CS, YukonPAObject YP "
                                     "LEFT OUTER JOIN DeviceDialupSettings DUS ON YP.paobjectid = DUS.deviceid "
                                   "WHERE YP.paobjectid = AD.deviceid AND YP.paobjectid = DV.deviceid AND "
                                     "YP.paobjectid = CS.deviceid";

    return sqlCore;
}

void ModbusDevice::DecodeDatabaseReader(Cti::RowReader &rdr)
{
   Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
   _modbus_address.DecodeDatabaseReader(rdr);

   if( getDebugLevel() & DEBUGLEVEL_DATABASE )
   {
       CTILOG_DEBUG(dout, "Decoding DB reader");
   }

   _modbus.setAddresses(_modbus_address.getSlaveAddress());
}

}
}

