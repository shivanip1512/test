#pragma once

#include "dlldefs.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceRTC : private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableDeviceRTC(const CtiTableDeviceRTC&);
    CtiTableDeviceRTC& operator=(const CtiTableDeviceRTC&);

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
