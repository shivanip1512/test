/*-----------------------------------------------------------------------------*
*
* File:   dnp_application
*
* Class:  CtiDNPApplication
* Date:   5/7/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.14 $
* DATE         :  $Date: 2005/02/10 23:23:58 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DNP_APPLICATION_H__
#define __DNP_APPLICATION_H__
#pragma warning( disable : 4786)


#include <vector>

#include "dllbase.h"
#include "message.h"

#include "dnp_objects.h"
#include "dnp_transport.h"


//#define DNP_APP_BUF_SIZE 2048

class CtiDNPApplication
{
public:
    enum
    {
        ApplicationBufferSize = 2048
    };

private:
    struct _dnp_app_control
    {
        unsigned char seq         : 4;
        unsigned char unsolicited : 1;
        unsigned char app_confirm : 1;
        unsigned char final       : 1;
        unsigned char first       : 1;
    };

    struct _dnp_app_indications
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

#pragma pack( push, 1 )

    short _dstAddr, _srcAddr;
    struct appReq
    {
        _dnp_app_control ctrl;
        unsigned char func_code;
        unsigned char buf[ApplicationBufferSize/* - sizeof(_dnp_app_control) - 1*/];
    } _appReq;

    struct appRsp
    {
        _dnp_app_control ctrl;
        unsigned char func_code;
        _dnp_app_indications ind;
        unsigned char buf[ApplicationBufferSize/* - sizeof(_dnp_app_control) - 1 - sizeof(_dnp_app_indications)*/];
    } _appRsp;

    struct appAck
    {
        _dnp_app_control ctrl;
        unsigned char func_code;
    } _appAck;

#pragma pack( pop )

    int _seqno, _replyExpected;
    int _appReqBytesUsed, _appRspBytesUsed;
    bool _inHasPoints, _hasOutput;

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

    /*vector< CtiDNPObjectBlock * > _outObjectBlocks;*/
    vector< CtiDNPObjectBlock * > _inObjectBlocks;

    void reset( void );
    void generateAck( appAck *app_packet, unsigned char seq);

protected:
    CtiDNPTransport _transport;

    enum
    {
        ReqHeaderSize = 2,
        RspHeaderSize = 4,
        CommRetries   = 3
    };


public:
    enum AppFuncCode;

    CtiDNPApplication();

    CtiDNPApplication(const CtiDNPApplication &aRef);

    virtual ~CtiDNPApplication();

    CtiDNPApplication &operator=(const CtiDNPApplication &aRef);


    //  initialization functions
    void setAddresses( unsigned short dstAddr, unsigned short srcAddr );
    void setOptions( int options );

    void resetLink( void );

    void setCommand( AppFuncCode func );
    void addObjectBlock( const CtiDNPObjectBlock &obj );


    //  these six functions are for the Out/InMess Scanner/Porter/Pil interactions
    int  getLengthReq( void );
    void serializeReq( unsigned char *buf );
    void restoreReq  ( unsigned char *buf, int len );

    int  getLengthRsp( void );
    void serializeRsp( unsigned char *buf );
    void restoreRsp  ( unsigned char *buf, int len );


    //  comm functions
    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );


    //  checking completion
    bool isTransactionComplete( void );
    bool errorCondition( void );
    bool isReplyExpected( void );


    //  post-completion processing
    void processInput( void );
    void eraseOutboundObjectBlocks( void );
    void eraseInboundObjectBlocks( void );
    bool hasInboundPoints( void );
    void getInboundPoints( RWTPtrSlist< CtiPointDataMsg > &pointList );

    bool isControlResult( void )        const;
    int  getControlResultStatus( void ) const;
    long getControlResultOffset( void ) const;

    bool          hasTimeResult( void ) const;
    unsigned long getTimeResult( void ) const;


    enum AppFuncCode
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
#endif // #ifndef __DNP_APPLICATION_H__
