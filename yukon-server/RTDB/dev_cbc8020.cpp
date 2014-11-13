#include "precompiled.h"

#include "dev_cbc8020.h"

#include "pt_status.h"

#include <boost/optional.hpp>
using namespace std;
namespace Cti {
namespace Devices {

/**
 * This function will iterate over the <code>points</code>
 * vector, searching for the two firmware points. It will set
 * aside the first major or minor firmware revision points it
 * comes across until it finds its complement point and, as
 * soon as it finds the second point, it combines the
 * information from the major and minor revisions into a new
 * CtiPointDataMsg object and pushes it
 * onto the <code>points</code> vector and returns.
 *
 * If a major and minor point aren't both encountered in the
 * iteration, this function will effectively do nothing and the
 * vector of messages will remain untouched. If the function
 * does find both a major and minor, the size of the vector will
 * increase by one and all major and minor revision messages
 * previously in the vector will remain there untouched.
 */
void Cbc8020Device::combineFirmwarePoints( Protocols::Interface::pointlist_t &points )
{
    CtiPointDataMsg *major = 0, *minor = 0;
    Protocols::Interface::pointlist_t::iterator itr, end = points.end();

    // We need to check if the firmware analog points are present, then consolidate
    // them into single points for processing.
    for( itr = points.begin(); itr != end; itr++ )
    {
        CtiPointDataMsg *pt_msg = *itr;

        if( pt_msg && pt_msg->getType() == AnalogPointType )
        {
            if( pt_msg->getId() == PointOffset_FirmwareRevisionMajor )
            {
                major = pt_msg;
            }
            if( pt_msg->getId() == PointOffset_FirmwareRevisionMinor )
            {
                minor = pt_msg;
            }
        }

        if( major && minor )
        {
            /**
             * We've encountered both a major and minor revision message.
             * Use them to create the single revision data point message and
             * store it.
             */

            /*
                incoming data:
                    'major' : 8-bit value -- upper 4-bits == major_version
                                             lower 4-bits == minor_version
                    'minor' : 8-bit value -- revision

                outgoing data format:
                    a SIXBIT (http://nemesis.lonestar.org/reference/telecom/codes/sixbit.html)
                    encoded string packed inside a long long.  The point data message holds a
                    double which limits us to 8 (6-bit) encoded characters inside the 52-bit mantissa.
                    The string format is "major_version.minor_version.revision".  A string that is
                    too long will be truncated and the last character replaced by '#' to
                    denote that condition. eg: "10.11.123" --> "10.11.1#".
            */

            int major_minor = static_cast<int>( major->getValue() );
            int revision    = static_cast<int>( minor->getValue() );

            char buffer[16];

            int messageLength = _snprintf_s( buffer, 16, 15, "%d.%d.%d",
                                             ( major_minor >> 4 ) & 0x0f,
                                             major_minor & 0x0f,
                                             revision & 0x0ff );

            if ( messageLength > 8 )
            {
                buffer[7] = '#';
                buffer[8] = 0;

                messageLength = 8;
            }

            long long encodedValue = 0;

            for ( int i = 0; i < messageLength; ++i )
            {
                encodedValue <<= 6;
                encodedValue |= ( ( buffer[i] - ' ' ) & 0x03f );
            }

            CtiPointDataMsg *pt_msg = new CtiPointDataMsg(PointOffset_FirmwareRevision,
                                                          static_cast<double>( encodedValue ),
                                                          NormalQuality,
                                                          AnalogPointType);
            points.push_back(pt_msg);

            return;
        }
    }
}

void Cbc8020Device::processPoints( Protocols::Interface::pointlist_t &points )
{
    combineFirmwarePoints(points);

    //  do the final processing
    DnpDevice::processPoints(points);
}

YukonError_t Cbc8020Device::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t nRet = ClientErrors::NoMethod;
    bool didExecute = false;

    if( parse.getCommand() == PutConfigRequest )
    {
        if(parse.isKeyValid("ovuv"))
        {
            string cmd = parse.getiValue("ovuv") == 0 ? " open " : " close "; //0 = disable, 1 = enable
            pReq->setCommandString("control " +  cmd + " offset 14");
            parse = CtiCommandParser (pReq->CommandString());
        }
    }
    if( parse.getCommand() == ControlRequest )
    {
        const int pointid = parse.getiValue("point");

        if( pointid > 0 )
        {
            //  select by raw pointid
            CtiPointSPtr point = getDevicePointByID(pointid);

            if ( ! point )
            {
                std::string errorMessage = "The specified point is not on device " + getName();
                returnErrorMessage(ClientErrors::PointLookupFailed, OutMessage, retList, errorMessage);

                return ClientErrors::PointLookupFailed;
            }

            if( point->isStatus() )
            {
                CtiPointStatusSPtr pStatus = boost::static_pointer_cast<CtiPointStatus>(point);

                if( const boost::optional<CtiTablePointStatusControl> controlParameters = pStatus->getControlParameters() )
                {
                    if( controlParameters->getControlOffset() > 0 )
                    {
                        parse = CtiCommandParser(pReq->CommandString() + " offset " + CtiNumStr(controlParameters->getControlOffset()));
                    }
                }
            }
        }
    }

    nRet = Inherited::ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList);

    return nRet;
}

}
}
