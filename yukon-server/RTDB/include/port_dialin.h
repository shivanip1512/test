
#pragma warning( disable : 4786)
#ifndef __PORT_DIALIN_H__
#define __PORT_DIALIN_H__

/*-----------------------------------------------------------------------------*
*
* File:   port_dialin
*
* Class:  CtiPortDialin
* Date:   11/13/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/12/12 17:06:42 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <windows.h>
#include <iostream>
using namespace std;

#include <rw\cstring.h>

#include "dsm2.h"
#include "port_base.h"
#include "port_modem.h"
#include "tbl_port_dialup.h"


class CtiPortDialin
{
protected:

    CtiPort             *_superPort;           // This is how we know who owns us...

    BOOL                _shouldDisconnect;
    RWCString           _dialedUpNumber;
    CtiTablePortDialup  _tblPortDialup;


    CtiHayesModem       _modem;

private:

public:

    CtiPortDialin();
    CtiPortDialin(const CtiPortDialin& aRef);
    virtual ~CtiPortDialin();
    CtiPortDialin& operator=(const CtiPortDialin& aRef);


    CtiHayesModem& getModem();

    CtiPortDialin& setSuperPort(CtiPort *port);

    RWCString getDialedUpNumber() const;
    RWCString& getDialedUpNumber();
    CtiPortDialin& setDialedUpNumber(const RWCString &str);

    /*-----------------------------------------------------*
     * Used to establish a connection to the remote Device
     *-----------------------------------------------------*/

    INT modemReset(USHORT Trace, BOOL dcdTest = TRUE);
    INT modemSetup(USHORT Trace, BOOL dcdTest = TRUE);
    INT modemHangup(USHORT Trace, BOOL dcdTest = TRUE);
    INT modemConnect(PCHAR Message, USHORT Trace, BOOL dcdTest = TRUE);

    CtiTablePortDialup         getTablePortDialup() const;
    CtiTablePortDialup&        getTablePortDialup();
    CtiPortDialin&            setTablePortDialup(const CtiTablePortDialup& aRef);

    void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    void DecodeDatabaseReader(RWDBReader &rdr);


    RWCString  getModemInit() const;
    RWCString& getModemInit();

    BOOL shouldDisconnect() const;
    void setShouldDisconnect(BOOL b = TRUE);

    INT  connectToDevice(CtiDevice *Device, INT trace);
    INT  disconnect(CtiDevice *Device, INT trace);

    INT reset(INT trace);
    INT setup(INT trace);
    INT close(INT trace);

    INT waitForResponse(PULONG ResponseSize, PCHAR Response, ULONG Timeout, PCHAR ExpectedResponse);
    static BOOL validModemResponse(PCHAR Response);

};
#endif // #ifndef __PORT_DIALIN_H__
