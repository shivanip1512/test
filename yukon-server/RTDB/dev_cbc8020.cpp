#include "precompiled.h"

#include "dev_cbc8020.h"

#include <boost/optional.hpp>

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
void Cbc8020Device::combineFirmwarePoints( Cti::Protocol::Interface::pointlist_t &points )
{
    CtiPointDataMsg *major = 0, *minor = 0;
    Cti::Protocol::Interface::pointlist_t::iterator itr, end = points.end();

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
            double firmware = major->getValue() + (minor->getValue() / 100.0);

            CtiPointDataMsg *pt_msg = new CtiPointDataMsg(PointOffset_FirmwareRevision,
                                                          firmware,
                                                          NormalQuality,
                                                          AnalogPointType);
            points.push_back(pt_msg);

            return;
        }
    }
}

void Cbc8020Device::processPoints( Cti::Protocol::Interface::pointlist_t &points )
{
    combineFirmwarePoints(points);

    //  do the final processing
    DnpDevice::processPoints(points);
}

}
}
