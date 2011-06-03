#include "yukon.h"

#include "dev_cbc8020.h"

#include <boost/optional.hpp>

namespace Cti {
namespace Devices {

/**
 * This function will iterate over the <code>points</code> 
 * vector, searching for the two firmware points. If it finds 
 * both, as soon as it finds the second point it will break out
 * of the iteration, calculate the firmware version, delete the 
 * first message it encountered, and update the last message it 
 * processed to contain the firmware version and point offset 
 * required to be processed.
 *  
 * If we encounter multiple major or minor revisions in the 
 * <code>points</code> vector (i.e. Major Major Minor or 
 * similar), the first one processed will be the only one that 
 * will be saved and passed on (assuming both major and minor 
 * points are eventually processed in the vector.) Any 
 * subsequent message whose type has already been encountered 
 * will be removed from the points vector. 
 *  
 * There is no guarantee on the order of the points in the 
 * vector when they are passed into this function. 
 */
void Cbc8020Device::combineFirmwarePoints( Cti::Protocol::Interface::pointlist_t &points )
{
    boost::optional<CtiPointDataMsg*> major, minor;

    // We need to check if the firmware analog points are present, then consolidate
    // them into one point for processing.
    for( int i = 0; i < points.size(); /* Increments handled manually */ )
    {
        CtiPointDataMsg *pt_msg = points[i];
        
        if( pt_msg && pt_msg->getType() == AnalogPointType )
        {
            if( pt_msg->getId() == PointOffset_FirmwareRevisionMajor )
            {
                if( !major || (*major)->getTime() < pt_msg->getTime() )
                {
                    major = pt_msg;
                }
                points.erase(points.begin() + i);
                continue;
            }
            if( pt_msg->getId() == PointOffset_FirmwareRevisionMinor )
            {
                if( !minor || (*minor)->getTime() < pt_msg->getTime() )
                {
                    minor = pt_msg;
                }
                points.erase(points.begin() + i);
                continue;
            }
        }


        /**
         * We want to increment in the general case, but not when we've 
         * encountered multiple major or minor revision messages. The 
         * continue statement in those cases will skip the 
         * incrementation, which will keep us in place in the vector 
         * since we've erased a message from the vector. 
         */
        i++;
    }

    if( major && minor )
    {
        /**
         * We just processed the second firmware point message. We have 
         * the index of the first one, lets shove all the firmare 
         * information into one value, put it into this message, and 
         * delete the first one. 
         */
        double firmware = static_cast<double>((*major)->getValue()) +
                          static_cast<double>((*minor)->getValue()) / 100.0;

        (*major)->setValue(firmware);
        points.push_back(*major);
    }
    else
    {
        if( major ) 
        {
            points.push_back(*major);
        }
        if( minor ) 
        {
            points.push_back(*minor);
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
