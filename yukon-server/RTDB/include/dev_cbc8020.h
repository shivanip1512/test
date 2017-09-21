#pragma once

#include "dev_cbc7020.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Cbc8020Device : public Cbc7020Device
{
public:

    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

protected:
    typedef Cbc7020Device Inherited;
    enum class PointOffsets : long
    {
        FirmwareRevisionMajor = 3,
        FirmwareRevisionMinor = 4,
        /**
         * Offset 9999 was chosen for the artificial Firmware Revision
         * point since negative offsets are not allowable. Analog
         * outputs start at 10000 and go up, so 9999 was a safe offset
         * choice for Firmware Revision point since analog inputs will
         * likely never reach that high and cause a conflict.
         */
        FirmwareRevision      = 9999
    };

    enum class ControlOffsets : long
    {
        ControlPoint             =  1,
        EnableControlOvuv        = 14,
        EnableControlVar         = 15,
        EnableControlTemperature = 16,
        EnableControlTime        = 23,
    };

    void processPoints( Protocols::Interface::pointlist_t &points ) override;

    struct FirmwareRevisionOffsets
    {
        long major;
        long minor;
    };

    static void combineFirmwarePoints( Protocols::Interface::pointlist_t &points, const FirmwareRevisionOffsets firmwareOffsets );

    void refreshAttributeOverrides();

private:
    
    std::map<PointOffsets, std::string> _pointOffsetOverrides;
    std::map<ControlOffsets, std::string> _controlOffsetOverrides;

    long getPointOffset  (PointOffsets defaultOffset);
    long getControlOffset(ControlOffsets defaultControlOffset);

    size_t _lastConfigId = 0;
};

}
}
