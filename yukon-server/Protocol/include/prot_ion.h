#pragma warning( disable : 4786)
#ifndef __PROT_ION_H__
#define __PROT_ION_H__

/*-----------------------------------------------------------------------------*
*
* File:   prot_ion
*
* Class:  CtiProtocolDNP
* Date:   5/6/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/12/21 02:38:57 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dlldefs.h"
#include "pointtypes.h"

#include "prot_base.h"

#include "ion_rootclasses.h"
//#include "ion_classtypes.h"
#include "ion_value_datastream.h"
#include "ion_net_application.h"


class IM_EX_PROT CtiProtocolION : public  CtiProtocolBase
{
    enum   IONCommand;
    struct ion_output_point;

private:

    CtiIONApplicationLayer _appLayer;

    CtiIONDataStream       _dsBuf;

    unsigned short _srcID, _dstID;

    struct ion_protocol_command_struct
    {
        IONCommand command;
    } _currentCommand;

protected:

    int commOut( OUTMESS *&OutMessage );
    int commIn ( INMESS   *InMessage  );

    enum
    {
        IONFeatureManagerHandle = 2
    };

    enum IONStates
    {
        IONStateUninitialized,
        IONStateInit,
        IONStateRequestFeatureManagerInfo,
        IONStateReceiveFeatureManagerInfo,
        IONStateRequestManagerInfo,
        IONStateReceiveManagerInfo,
        IONStateRequestModuleInfo,
        IONStateReceiveModuleInfo,
        IONStateRequestRegisterInfo,
        IONStateReceiveRegisterInfo,
        IONStateRequestData,
        IONStateReceiveData,
        IONStateComplete,
        IONStateAbort
    } _ionState;

public:

    CtiProtocolION();
    CtiProtocolION(const CtiProtocolION &aRef);

    virtual ~CtiProtocolION();

    CtiProtocolION &operator=(const CtiProtocolION &aRef);

    void setAddresses( unsigned short masterAddress, unsigned short slaveAddress );

    void setCommand( IONCommand command, ion_output_point *points = NULL, int numPoints = 0 );

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    bool isTransactionComplete( void );

    int sendCommRequest( OUTMESS *&OutMessage, RWTPtrSlist< OUTMESS > &outList );
    int recvCommResult ( INMESS   *InMessage,  RWTPtrSlist< OUTMESS > &outList );

    int recvCommRequest( OUTMESS *OutMessage );
    int sendCommResult ( INMESS  *InMessage  );

    bool hasInboundPoints( void );
    void getInboundPoints( RWTPtrSlist< CtiPointDataMsg > &pointList );

    /*
    enum IONOutputPointType
    {
        AnalogOutput,
        DigitalOutput
    };

    struct ion_output_point
    {
        union
        {
            struct ion_ao_point
            {
                double value;
            } aout;

            struct ion_do_point
            {
                CtiIONBinaryOutputControl::ControlCode control;
                CtiIONBinaryOutputControl::TripClose trip_close;
                unsigned long on_time;
                unsigned long off_time;
                bool queue;
                bool clear;
                unsigned char count;
            } dout;
        };

        unsigned long offset;

        IONOutputPointType type;
    };
    */

    enum IONCommand
    {
        ION_Invalid = 0,
        ION_ExceptionScan,
        ION_IntegrityScan,
        ION_ScanLoadProfile,
        ION_SetAnalogOut,
        ION_SetDigitalOut
    };

    enum
    {
        DefaultYukonIONMasterAddress =    5,
        DefaultSlaveAddress          =    1,
        MaxAppLayerSize              = 2048
    };
};

#endif // #ifndef __PROT_ION_H__
