#pragma once

#include "port_base.h"
#include "tbl_port_settings.h"
#include "tbl_port_timing.h"

class CtiPortSerial : public CtiPort
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiPortSerial(const CtiPortSerial&);
    CtiPortSerial& operator=(const CtiPortSerial&);

protected:

    CtiTablePortSettings _tblPortSettings;
    CtiTablePortTimings _tblPortTimings;

public:

    typedef CtiPort Inherited;

    CtiPortSerial() {}

    const CtiTablePortSettings& getTablePortSettings() const
    {
        return _tblPortSettings;
    }
    const CtiTablePortTimings& getTablePortTimings() const
    {
        return _tblPortTimings;
    }

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    virtual CtiPort &setBaudRate(INT baudRate);
    virtual INT getBaudRate() const;
    virtual INT getBits() const;
    virtual INT getStopBits() const;
    virtual INT getParity() const;
    virtual ULONG getCDWait() const;

    virtual ULONG getDelay(int Offset) const;
    virtual CtiPort& setDelay(int Offset, int D);

};

inline INT CtiPortSerial::getBaudRate() const { return getTablePortSettings().getBaudRate();}
inline INT CtiPortSerial::getBits() const { return getTablePortSettings().getBits(); }
inline INT CtiPortSerial::getStopBits() const { return getTablePortSettings().getStopBits(); }
inline INT CtiPortSerial::getParity() const { return getTablePortSettings().getParity(); }
inline ULONG CtiPortSerial::getCDWait() const { return getTablePortSettings().getCDWait(); }
