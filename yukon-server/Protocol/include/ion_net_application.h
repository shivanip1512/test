#pragma warning( disable : 4786 )

#ifndef __ION_NET_APPLICATION_H__
#define __ION_NET_APPLICATION_H__

/*-----------------------------------------------------------------------------*
 *
 * File:    ion_net_application.h
 *
 * Classes: CtiIONApplicationLayer, CtiIONNetworkLayer, CtiIONDatalinkLayer
 * Date:    07/06/2001
 *
 * Author:  Matthew Fisher
 *
 *          ION pseudo-ISO network layer classes for the ION serial protocol
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include <vector>
#include "ctitypes.h"
#include "dlldefs.h"
#include "xfer.h"

//#include "ion_rootclasses.h"
//#include "ion_valuebasictypes.h"
#include "ion_net_network.h"


//  necessary to preserve byte alignment;  makes for easy memcpy initialization and serialization
#pragma pack(push, ion_packing, 1)


class IM_EX_PROT CtiIONApplicationLayer : public CtiIONSerializable
{
private:

    //  note that we have no provision for the timesync format...
    struct _app_layer_request_struct
    {
        struct _app_layer_request_header
        {
            unsigned char servicereserved1 : 8;   //  MSB ordering, but bits are stored low-to-high, i.e.

            unsigned char service          : 4;   //  <-- low nibble
            unsigned char servicereserved0 : 4;   //  <-- high nibble

            unsigned short pid;  //  <--  this may break, MSB vs. Intel ordering.
            unsigned char  freq;
            unsigned char  priority;

            struct _length_bytes
            {
                unsigned char byte1    :  7;  //
                unsigned char reserved :  1;  //  <-- high bit of this char

                unsigned char byte0    :  8;  //
            } length;
        } header;
        unsigned char *IONData;
    };

    //  note that we have no provision for the timesync format...
    struct _app_layer_reply_struct
    {
        struct _app_layer_reply_header
        {
            unsigned char servicereserved1 : 8;   //  MSB ordering, but bits are stored low-to-high, i.e.

            unsigned char service          : 4;   //  <-- low nibble
            unsigned char servicereserved0 : 4;   //  <-- high nibble

            unsigned char  status;
            unsigned short pid;  //  <--  this may break, MSB vs. Intel ordering.
            unsigned char  freq;
            unsigned char  priority;

            struct _length_bytes
            {
                unsigned char byte1    :  7;  //
                unsigned char reserved :  1;  //  <-- high bit of this char

                unsigned char byte0    :  8;  //
            } length;
        } header;
        unsigned char *IONData;
    };

    _app_layer_request_struct _appOut;
    _app_layer_reply_struct   _appIn;

    void initOutPacketReserved( void );
    void initInPacketReserved ( void );

    void freeOutPacketMemory( void );
    void freeInPacketMemory ( void );

    enum ApplicationIOState
    {
        Uninitialized = 0,
        Output,
        Input,
        Complete,
        Failed
    } _ioState;

    CtiIONNetworkLayer _networkLayer;

protected:

public:

    CtiIONApplicationLayer( );
    ~CtiIONApplicationLayer( );

    void setAddresses( unsigned short srcID, unsigned short dstID );

    void setToOutput( const CtiIONSerializable &payload );
    void setToInput( void );

    void putSerialized( unsigned char *buf ) const;
    unsigned int getSerializedLength( void ) const;

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    bool isTransactionComplete( void );
    bool errorCondition( void );

    void putPayload( unsigned char *buf ) const;
    unsigned int getPayloadLength( void ) const;

};

#pragma pack(pop, ion_packing)

#endif  //  #ifndef __ION_NET_APPLICATION_H__

