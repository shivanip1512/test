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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2002/07/25 20:53:21 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dlldefs.h"
#include "pointtypes.h"

#include "dnp_application.h"
#include "dnp_objects.h"
#include "dnp_object_binaryoutput.h"
#include "xfer.h"

class IM_EX_PROT CtiProtocolDNP
{
    enum   DNPCommand;
    struct dnp_output_point;

private:

    CtiDNPApplication _appLayer;
    unsigned short    _masterAddress, _slaveAddress;
    DNPCommand        _currentCommand;

public:

    CtiProtocolDNP();
    CtiProtocolDNP(int address);
    CtiProtocolDNP(const CtiProtocolDNP &aRef);

    virtual ~CtiProtocolDNP();

    CtiProtocolDNP &operator=(const CtiProtocolDNP &aRef);

    void setMasterAddress( unsigned short address );
    void setSlaveAddress( unsigned short address );

    void setCommand( DNPCommand command, dnp_output_point *points = NULL, int numPoints = 0 );

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    bool isTransactionComplete( void );

    int commOut( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
    int commIn ( INMESS *InMessage,   RWTPtrSlist< OUTMESS > &outList );

    int sendOutbound( OUTMESS *&OutMessage );
    int recvOutbound( OUTMESS  *OutMessage );

    int sendInbound( INMESS *InMessage );
    int recvInbound( INMESS *InMessage );

    bool hasInboundPoints( void );
    void getInboundPoints( RWTPtrSlist< CtiPointDataMsg > &pointList );

    enum DNPOutputPointType
    {
        AnalogOutput,
        DigitalOutput
    };

    struct dnp_output_point
    {
        union
        {
            struct dnp_ao_point
            {
                double value;
            } aout;

            struct dnp_do_point
            {
                CtiDNPBinaryOutputControl::ControlCode control;
                CtiDNPBinaryOutputControl::TripClose trip_close;
                unsigned long on_time;
                unsigned long off_time;
                bool queue;
                bool clear;
                unsigned char count;
            } dout;
        };

        unsigned long offset;

        DNPOutputPointType type;
    };

    enum DNPCommand
    {
        DNP_Invalid = 0,
        DNP_Class0Read,
        DNP_Class1Read,
        DNP_Class2Read,
        DNP_Class3Read,
        DNP_Class123Read,
        DNP_SetAnalogOut,
        DNP_SetDigitalOut
    };

    enum
    {
        DefaultYukonDNPMasterAddress =    5,
        DefaultSlaveAddress          =    1,
        MaxAppLayerSize              = 2048
    };
};

#endif // #ifndef __PROT_DNP_H__
