#include "precompiled.h"

#include "dev_cbc7020.h"

#include "numstr.h"

using namespace std;

namespace Cti {
namespace Devices {

const char *Cbc7020Device::PutConfigPart_all             = "all";
const char *Cbc7020Device::PutConfigPart_comms_lost      = "commslost";
const char *Cbc7020Device::PutConfigPart_control_times   = "controltimes";
const char *Cbc7020Device::PutConfigPart_data_logging    = "logging";
const char *Cbc7020Device::PutConfigPart_dnp             = "dnp";
const char *Cbc7020Device::PutConfigPart_fault_detection = "faultdetection";
const char *Cbc7020Device::PutConfigPart_neutral_current = "neutralcurrent";
const char *Cbc7020Device::PutConfigPart_time_temp_1     = "timeandtemp1";
const char *Cbc7020Device::PutConfigPart_time_temp_2     = "timeandtemp2";
const char *Cbc7020Device::PutConfigPart_udp             = "udp";
const char *Cbc7020Device::PutConfigPart_voltage         = "voltage";

const Cbc7020Device::ConfigPartsList Cbc7020Device::_config_parts = Cbc7020Device::initConfigParts();

Cbc7020Device::ConfigPartsList Cbc7020Device::initConfigParts()
{
    Cbc7020Device::ConfigPartsList tempList;
    tempList.push_back(Cbc7020Device::PutConfigPart_all);
    tempList.push_back(Cbc7020Device::PutConfigPart_comms_lost);
    tempList.push_back(Cbc7020Device::PutConfigPart_control_times);
    tempList.push_back(Cbc7020Device::PutConfigPart_data_logging);
    tempList.push_back(Cbc7020Device::PutConfigPart_dnp);
    tempList.push_back(Cbc7020Device::PutConfigPart_fault_detection);
    tempList.push_back(Cbc7020Device::PutConfigPart_neutral_current);
    tempList.push_back(Cbc7020Device::PutConfigPart_time_temp_1);
    tempList.push_back(Cbc7020Device::PutConfigPart_time_temp_2);
    tempList.push_back(Cbc7020Device::PutConfigPart_udp);
    tempList.push_back(Cbc7020Device::PutConfigPart_voltage);

    return tempList;
}

YukonError_t Cbc7020Device::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t nRet = ClientErrors::NoMethod;
    bool didExecute = false;

    //  if it's a control open/close request without an offset
    if( (parse.getCommand() == ControlRequest) && !(parse.getFlags() & CMD_FLAG_OFFSET) &&
        (parse.getFlags() & CMD_FLAG_CTL_OPEN || parse.getFlags() & CMD_FLAG_CTL_CLOSE) )
    {
        if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
        {
            pReq->setCommandString("control open offset 1");
        }
        else // if( parse.getFlags() & CMD_FLAG_CTL_CLOSE ) - implied because of the above if condition
        {
            pReq->setCommandString("control close offset 1");
        }

        CtiCommandParser new_parse(pReq->CommandString());

        //  NOTE the new parser I'm passing in - i've already touched the pReq string, so
        //    i need to seal the deal with a new parse
        nRet = Inherited::ExecuteRequest(pReq, new_parse, OutMessage, vgList, retList, outList);
    }
    /*else if( parse.getCommand() == PutConfigRequest && parse.isKeyValid("install") )
    {
        nRet = executePutConfig( pReq, parse, OutMessage, vgList, retList, outList );
    }*/
    else
    {
        nRet = Inherited::ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList);
    }

    return nRet;
}

void Cbc7020Device::processFirmwarePoint( Protocols::Interface::pointlist_t &points )
{
    Protocols::Interface::pointlist_t::iterator pt_itr, last_pos;

    last_pos    = points.end();

    //  we need to find any status values for offsets 1 and 2
    for( pt_itr = points.begin(); pt_itr != last_pos; pt_itr++ )
    {
        CtiPointDataMsg *pt_msg = *pt_itr;

        if( pt_msg &&
            pt_msg->getType() == AnalogPointType &&
            pt_msg->getId()   == PointOffset_FirmwareRevision )
        {
            /*
                incoming data:
                    'pt_msg' : 16-bit value -- upper 8-bits == minor_version
                                            -- lower 8-bits == major_version

                outgoing data format:
                    a SIXBIT (http://nemesis.lonestar.org/reference/telecom/codes/sixbit.html)
                    encoded string packed inside a long long.  The point data message holds a
                    double which limits us to 8 (6-bit) encoded characters inside the 52-bit mantissa.
                    The string format is "major_version.minor_version".
                    The major version is represented by a single capital alphabet letter.  The
                    valid range is 1 to 26 mapping to 'A' to 'Z' (ie: '@' + major_version).  The minor version
                    is the raw 8 bit number.  Since max length of the string is 5 chars, no
                    overflow of the 8 char limit is possible.

                    Example: 0x030D --> M.3     (13th letter of alphabet . minor)

                    An error in the range of the major value (0 or greater than 26) will report
                    0.0 as the firmware revision.
            */
            unsigned int value = pt_msg->getValue();

            int minor = ( value >> 8 ) & 0x0ff;
            int major = value & 0x0ff;

            if ( major == 0 || major > 26 )
            {
                major = '0';
                minor = 0;
            }
            else
            {
                major += '@';
            }

            char buffer[16];

            int messageLength = _snprintf_s( buffer, 16, 15, "%c.%d", major, minor );

            long long encodedValue = 0;

            for ( int i = 0; i < messageLength; ++i )
            {
                encodedValue <<= 6;
                encodedValue |= ( ( buffer[i] - ' ' ) & 0x03f );
            }

            pt_msg->setValue( static_cast<double>( encodedValue ) );
        }
    }
}

//  This overrides the processPoints function in dev_dnp, but calls it afterward to do the real processing
void Cbc7020Device::processPoints( Protocols::Interface::pointlist_t &points )
{
    // preprocess any existing firmware point
    processFirmwarePoint(points);

    //  do the final processing
    Inherited::processPoints(points);
}

YukonError_t Cbc7020Device::sendPutValueAnalog(int outputPt, double value, CtiRequestMsg *pReq, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    string tempStr = "putvalue analog " + CtiNumStr(outputPt) + " " + CtiNumStr(value);
    OUTMESS *tempOutMess = CTIDBG_new OUTMESS(*OutMessage);
    strncpy(tempOutMess->Request.CommandStr, tempStr.c_str(), COMMAND_STR_SIZE );
    CtiCommandParser parseSingle(tempOutMess->Request.CommandStr);
    return Inherited::ExecuteRequest(pReq, parseSingle, tempOutMess, vgList, retList, outList);
}

}
}

