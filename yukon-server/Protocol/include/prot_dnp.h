#pragma warning( disable : 4786)
#ifndef __PROT_DNP_H__
#define __PROT_DNP_H__

/*-----------------------------------------------------------------------------*
*
* File:   prot_dnp
*
* Class:  CtiProtocolDNP
* Date:   5/6/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/05/30 15:11:35 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dlldefs.h"
#include "pointtypes.h"

#include "dnp_application.h"
#include "dnp_objects.h"
#include "xfer.h"

class IM_EX_PROT CtiProtocolDNP
{
    enum   DNPCommand;
    struct XferPoint;

private:

    CtiDNPApplication _appLayer;
    int               _address;
    DNPCommand        _currentCommand;

public:

    CtiProtocolDNP();
    CtiProtocolDNP(int address);
    CtiProtocolDNP(const CtiProtocolDNP &aRef);

    virtual ~CtiProtocolDNP();

    CtiProtocolDNP &operator=(const CtiProtocolDNP &aRef);

    void setCommand( DNPCommand command, XferPoint *points = NULL, int numPoints = 0 );

    void initForOutput( void );
    void initForInput ( void );

    int commOut( OUTMESS *OutMessage, RWTPtrSlist< OUTMESS > &outList );
    int commIn ( INMESS *InMessage, RWTPtrSlist< OUTMESS > &outList );

    bool hasPoints( void );
    void sendPoints( RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList );

    struct XferPoint
    {
        CtiPointType_t type;
        unsigned int offset;
        double value;
    };

    enum DNPCommand
    {
        DNP_Invalid    = 0,
        DNP_Class0Read,
        DNP_Class1Read,
        DNP_Class2Read,
        DNP_SetAnalogOut,
        DNP_SetDigitalOut
    };

    enum
    {
        MaxAppLayerSize = 2048
    };
};

#endif // #ifndef __PROT_DNP_H__
