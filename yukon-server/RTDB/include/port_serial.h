/*-----------------------------------------------------------------------------*
*
* File:   port_serial
*
* Class:  CtiPortSerial
* Date:   3/3/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PORT_SERIAL_H__
#define __PORT_SERIAL_H__
#pragma warning( disable : 4786)

#include "port_base.h"
#include "tbl_port_settings.h"
#include "tbl_port_timing.h"

class CtiPortSerial : public CtiPort
{
protected:

    CtiTablePortSettings _tblPortSettings;
    CtiTablePortTimings _tblPortTimings;

private:

public:

    typedef CtiPort Inherited;

    CtiPortSerial() {}

    CtiPortSerial(const CtiPortSerial& aRef)
    {
        *this = aRef;
    }

    virtual ~CtiPortSerial() {}

    CtiPortSerial& operator=(const CtiPortSerial& aRef);

    const CtiTablePortSettings& getTablePortSettings() const
    {
        return _tblPortSettings;
    }
    const CtiTablePortTimings& getTablePortTimings() const
    {
        return _tblPortTimings;
    }

    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector) const;
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

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



#endif // #ifndef __PORT_SERIAL_H__
