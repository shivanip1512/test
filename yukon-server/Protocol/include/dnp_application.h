/*-----------------------------------------------------------------------------*
*
* File:   dnp_application
*
* Namespace: CtiDNP
* Class:     Application
* Date:   5/7/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.15 $
* DATE         :  $Date: 2005/03/10 21:15:40 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DNP_APPLICATION_H__
#define __DNP_APPLICATION_H__
#pragma warning( disable : 4786)


#include <queue>

using namespace std;

#include "message.h"

#include "dnp_objects.h"
#include "dnp_transport.h"

namespace Cti       {
namespace Protocol  {
namespace DNP       {

class Application
{
public:
    enum FunctionCode;

    enum
    {
        BufferSize = 2048
    };

    typedef queue< const ObjectBlock * > object_block_queue;

private:
    struct control_header
    {
        unsigned char seq         : 4;
        unsigned char unsolicited : 1;
        unsigned char app_confirm : 1;
        unsigned char final       : 1;
        unsigned char first       : 1;
    };

    struct indications
    {
        unsigned short all_stations : 1;
        unsigned short class_1      : 1;
        unsigned short class_2      : 1;
        unsigned short class_3      : 1;
        unsigned short need_time    : 1;
        unsigned short local        : 1;
        unsigned short dev_trouble  : 1;
        unsigned short restart      : 1;
        unsigned short bad_function : 1;
        unsigned short obj_unknown  : 1;
        unsigned short out_of_range : 1;
        unsigned short buf_overflow : 1;
        unsigned short already_exec : 1;
        unsigned short bad_config   : 1;
        unsigned short reserved     : 2;
    };

    short _dstAddr, _srcAddr;

#pragma pack( push, 1 )

    struct request_t
    {
        control_header ctrl;
        unsigned char  func_code;
        unsigned char  buf[BufferSize];
    } _request;

    struct response_t
    {
        control_header ctrl;
        unsigned char  func_code;
        indications    ind;
        unsigned char  buf[BufferSize];
    } _response;

    struct acknowledge_t
    {
        control_header ctrl;
        unsigned char  func_code;
    } _acknowledge;

#pragma pack( pop )

    FunctionCode _request_function;
    int _seqno;
    int _request_buf_len,
        _response_buf_len;

    enum ApplicationIOState
    {
        Uninitialized = 0,
        Output,
        Input,
        OutputAck,
        Complete,
        Failed
    } _ioState, _retryState;

    int _comm_errors;

    bool _final_frame_received;

    object_block_queue _out_object_blocks,
                       _in_object_blocks;

    void reset( void );
    void generateAck( acknowledge_t *app_packet, unsigned char seq);

    void processInput( void );
    void eraseOutboundObjectBlocks( void );
    void eraseInboundObjectBlocks( void );

protected:
    Transport _transport;

    enum
    {
        ReqHeaderSize = 2,
        RspHeaderSize = 4,
        CommRetries   = 3
    };


public:
    Application();

    Application(const Application &aRef);

    virtual ~Application();

    Application &operator=(const Application &aRef);


    //  initialization functions
    void setAddresses( unsigned short dstAddr, unsigned short srcAddr );
    void setOptions( int options );
    void resetLink( void );

    void setCommand( FunctionCode fc );
    void addObjectBlock( const ObjectBlock *obj );
    void initForOutput( void );

    //  comm functions
    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    //  checking completion
    bool isTransactionComplete( void ) const;
    bool errorCondition( void )        const;
    bool isOneWay( void )              const;

    //  post-completion processing
    void getObjects( object_block_queue &object_queue );

    enum FunctionCode
    {
        RequestConfirm            = 0x00,
        RequestRead               = 0x01,
        RequestWrite              = 0x02,
        RequestSelect             = 0x03,
        RequestOperate            = 0x04,
        RequestDirectOp           = 0x05,
        RequestDirectOpNoAck      = 0x06,
        RequestImmedFreeze        = 0x07,
        RequestImmedFreezeNoAck   = 0x08,
        RequestFreezeClr          = 0x09,
        RequestFreezeClrNoAck     = 0x0a,
        RequestFreezeWTime        = 0x0b,
        RequestFreezeWTimeNoAck   = 0x0c,
        RequestColdRestart        = 0x0d,
        RequestWarmRestart        = 0x0e,
        RequestInitDataDefault    = 0x0f,
        RequestInitApplication    = 0x10,
        RequestStartApplication   = 0x11,
        RequestStopApplication    = 0x12,
        RequestSaveConfig         = 0x13,
        RequestEnableUnsolicited  = 0x14,
        RequestDisableUnsolicited = 0x15,
        RequestAssignClass        = 0x16,
        RequestDelayMeasurement   = 0x17,
        RequestRecordCurrentTime  = 0x18,

        ResponseConfirm     = 0x00,
        ResponseResponse    = 0x81,
        ResponseUnsolicited = 0x82
    };
};

}
}
}

#endif // #ifndef __DNP_APPLICATION_H__
