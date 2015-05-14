#include "precompiled.h"

#include "port_serial.h"

void CtiPortSerial::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);

    CtiLockGuard<CtiMutex> guard(_classMutex);
    _tblPortSettings.DecodeDatabaseReader(rdr);       // get the base class handled
    _tblPortTimings.DecodeDatabaseReader(rdr);       // get the base class handled
}


CtiPort &CtiPortSerial::setBaudRate(INT baudRate)
{
    if(baudRate)
        _tblPortSettings.setBaudRate(baudRate);

    return *this;
}


ULONG CtiPortSerial::getDelay(int Offset) const
{
    return _tblPortTimings.getDelay(Offset);
}

CtiPort& CtiPortSerial::setDelay(int Offset, int D)
{
    _tblPortTimings.setDelay(Offset, D);
    return *this;
}

