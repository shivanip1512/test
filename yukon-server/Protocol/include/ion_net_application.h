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
#ifndef __ION_NET_APPLICATION_H__
#define __ION_NET_APPLICATION_H__
#pragma warning( disable : 4786 )


#include <vector>

#include "dllbase.h"
#include "dlldefs.h"
#include "xfer.h"

#include "ion_net_network.h"


//  necessary to preserve byte alignment;  makes for easy memcpy initialization and serialization
#pragma pack(push, ion_packing, 1)


class CtiIONApplicationLayer : public CtiIONSerializable
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
    virtual ~CtiIONApplicationLayer( );

    void setAddresses( unsigned short masterAddress, unsigned short slaveAddress );

    void setToTimeSync( void );
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


class CtiIONTimeSync : public CtiIONSerializable
{
    friend class CtiIONApplicationLayer;

protected:

    unsigned long _utcSeconds;

    CtiIONTimeSync( unsigned long utcSeconds ) :
        _utcSeconds(utcSeconds)
    {
    }

    unsigned int getSerializedLength( void ) const
    {
        return 4;
    }

    void putSerialized( unsigned char *buf ) const
    {
        unsigned char *ptr = (unsigned char *)&_utcSeconds;

        buf[0] = ptr[3];
        buf[1] = ptr[2];
        buf[2] = ptr[1];
        buf[3] = ptr[0];
    }
};


#pragma pack(pop, ion_packing)

#endif  //  #ifndef __ION_NET_APPLICATION_H__

