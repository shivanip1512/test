/*-----------------------------------------------------------------------------*
*
* File:   port_dialable
*
* Class:  CtiPortDialable
* Date:   12/16/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2004/05/05 15:31:41 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PORT_DIALABLE_H__
#define __PORT_DIALABLE_H__
#pragma warning( disable : 4786)


#include "port_base.h"
#include "port_modem.h"
#include "tbl_port_dialup.h"


class CtiPortDialable
{
protected:

    CtiPort             *_superPort;           // This is how we know who owns us...

    BOOL                _shouldDisconnect;
    RWCString           _dialedUpNumber;

    CtiHayesModem       _modem;

    CtiTablePortDialup  _tblPortDialup;


private:

public:

    CtiPortDialable()  :
        _shouldDisconnect(FALSE)
    {}
    virtual ~CtiPortDialable() {}

    CtiHayesModem& getModem();
    CtiPortDialable& setSuperPort(CtiPort *port);

    RWCString getDialedUpNumber() const;
    RWCString& getDialedUpNumber();
    CtiPortDialable& setDialedUpNumber(const RWCString &str);

    virtual BOOL shouldDisconnect() const;
    virtual void setShouldDisconnect(BOOL b = TRUE);

    virtual INT connectToDevice(CtiDeviceSPtr Device, LONG &LastDeviceId, INT trace) = 0;

    virtual INT reset(INT trace) = 0;
    virtual INT setup(INT trace) = 0;
    virtual INT close(INT trace) = 0;

    virtual INT waitForResponse(PULONG ResponseSize, PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse = NULL);
    virtual INT disconnect(CtiDeviceSPtr Device, INT trace);

    CtiTablePortDialup getTablePortDialup() const;
    CtiTablePortDialup& getTablePortDialup();
    CtiPortDialable& setTablePortDialup(const CtiTablePortDialup& aRef);

    static void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    void DecodeDatabaseReader(RWDBReader &rdr);

};

inline CtiPortDialable& CtiPortDialable::setSuperPort(CtiPort *port)
{
    _superPort = port;
    return *this;
}

inline BOOL CtiPortDialable::shouldDisconnect() const
{
    return _shouldDisconnect;
}

inline void CtiPortDialable::setShouldDisconnect(BOOL b)
{
    _shouldDisconnect = b;
}





#endif // #ifndef __PORT_DIALABLE_H__
