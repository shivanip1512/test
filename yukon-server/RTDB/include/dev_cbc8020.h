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

    enum ControlOffsets
    {
        ControlOffset_EnableControlOvuv        = 14,
        ControlOffset_EnableControlVar         = 15,
        ControlOffset_EnableControlTemperature = 16,
        ControlOffset_EnableControlTime        = 23,
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
    
    std::string _pointNameFirmwareMajor;
    std::string _pointNameFirmwareMinor;
    std::string _pointNameEnableControlOvuv;
    std::string _pointNameEnableControlTemperature;
    std::string _pointNameEnableControlTime;
    std::string _pointNameEnableControlVar;

    long getPointOffset  (const std::string& overridePointName, long defaultOffset);
    long getControlOffset(const std::string& overridePointName, long defaultControlOffset);

    size_t _lastConfigId = 0;
};

}
}
