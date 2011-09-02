#pragma once

#include "dlldefs.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceRTC
{
public:

    enum
    {
        NoLBT = 0,
        WaitForNextSlot = 1,
        WaitForFreqClear = 2,
        OverrideAfterSlot = 3
    };

protected:

    LONG    _deviceID;
    int     _rtcAddress;
    bool    _responseBit;       // This will likely always be true.
    int     _lbtMode;           // This is 0-3: As defined in CtiRTCLBTMode_t.

private:

public:

    CtiTableDeviceRTC();
    virtual ~CtiTableDeviceRTC();

    LONG getDeviceID() const;

    int  getRTCAddress() const;
    bool getResponseBit() const;
    int  getLBTMode() const;

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    static std::string getTableName();
};
