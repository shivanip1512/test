
#pragma warning( disable : 4786)
#ifndef __DNP_APPLICATION_H__
#define __DNP_APPLICATION_H__

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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/05/30 15:11:26 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "message.h"

#include "dnp_objects.h"
#include "dnp_transport.h"

#define DNP_APP_BUF_SIZE 2048

class CtiDNPApplication
{
protected:
    CtiDNPTransport _transport;

private:
    void reset( void );

    enum
    {
        ReqHeaderSize = 2,
        RspHeaderSize = 4
    };

#pragma pack( push, 1 )
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

    struct appBuf
    {
        union
        {
            struct _req
            {
                _dnp_app_control ctrl;
                unsigned char func_code;
                unsigned char buf[DNP_APP_BUF_SIZE - sizeof(_dnp_app_control) - 1];
            } req;
            struct _rsp
            {
                _dnp_app_control ctrl;
                unsigned char func_code;
                _dnp_app_indications ind;
                unsigned char buf[DNP_APP_BUF_SIZE - sizeof(_dnp_app_control) - 1 - sizeof(_dnp_app_indications)];
            } rsp;
        };
    } _appBuf;

    int _seqno;
    int _appbufBytesUsed;
    bool _hasPoints;
#pragma pack( pop )

public:
    enum AppFuncCode;

    CtiDNPApplication();

    CtiDNPApplication(const CtiDNPApplication &aRef);

    virtual ~CtiDNPApplication();

    CtiDNPApplication &operator=(const CtiDNPApplication &aRef);

    void setCommand( AppFuncCode func );
    void initForOutput( void );
    void initForInput ( void );
    void addPoint( dnp_point_descriptor *point );

    int commOut( OUTMESS *OutMessage, RWTPtrSlist< OUTMESS > &outList );
    int commIn ( INMESS *InMessage, RWTPtrSlist< OUTMESS > &outList );

    void processInput( void );

    bool hasPoints( void );
    void sendPoints( RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList );

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
