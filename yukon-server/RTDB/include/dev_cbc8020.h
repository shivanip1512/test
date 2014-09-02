#pragma once

#include "dev_cbc7020.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Cbc8020Device : public Cbc7020Device
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    Cbc8020Device(const Cbc8020Device&);
    Cbc8020Device& operator=(const Cbc8020Device&);

public:
    Cbc8020Device() {};

    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

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
