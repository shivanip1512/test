#pragma once

#include "dev_cbc7020.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Cbc8020Device : public Cbc7020Device
{
public:

    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    static void initOffsetAttributeMaps( std::map <int, PointAttribute> &analogOffsetAttribute,
                                          std::map <int, PointAttribute> &statusOffsetAttribute,
                                          std::map <int, PointAttribute> &accumulatorOffsetAttribute);
protected:
    typedef Cbc7020Device Inherited;
    enum PointOffsets
    {
        PointOffset_FirmwareRevisionMajor = 3,
        PointOffset_FirmwareRevisionMinor = 4,
        /**
         * Offset 9999 was chosen for the artificial Firmware Revision 
         * point since negative offsets are not allowable. Analog 
         * outputs start at 10000 and go up, so 9999 was a safe offset 
         * choice for Firmware Revision point since analog inputs will 
         * likely never reach that high and cause a conflict.
         */
        PointOffset_FirmwareRevision      = 9999
    };

    virtual void processPoints( Cti::Protocol::Interface::pointlist_t &points );

    static void combineFirmwarePoints( Cti::Protocol::Interface::pointlist_t &points );

};

}
}
