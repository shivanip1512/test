#pragma once

#include <queue>

#include "dnp_objects.h"
#include "dnp_transport.h"
#include "dnp_configuration.h"

namespace Cti::Protocols::DNP {

class ApplicationLayer
{
public:
    enum FunctionCode;

    enum
    {
        BufferSize = 16384
    };

    using object_block_queue = std::deque<ObjectBlockPtr>;

private:

    const DNP::config_data* _config;

#pragma pack( push, 1 )

    struct control_header
    {
        unsigned char seq         : 4;
        unsigned char unsolicited : 1;
        unsigned char app_confirm : 1;
        unsigned char final       : 1;
        unsigned char first       : 1;
    };

    union indications
    {
        struct
        {
            unsigned short broadcast             : 1;
            unsigned short class_1_events        : 1;
            unsigned short class_2_events        : 1;
            unsigned short class_3_events        : 1;
            unsigned short need_time             : 1;
            unsigned short local_control         : 1;
            unsigned short device_trouble        : 1;
            unsigned short device_restart        : 1;
            unsigned short no_func_code_support  : 1;
            unsigned short object_unknown        : 1;
            unsigned short parameter_error       : 1;
            unsigned short event_buffer_overflow : 1;
            unsigned short already_executing     : 1;
            unsigned short config_corrupt        : 1;
            unsigned short reserved : 2;
        };

        unsigned short raw;
    };

    struct request_t
    {
        control_header ctrl;
        unsigned char  func_code;
        unsigned char  buf[BufferSize];
        unsigned       buf_len;
    } _request;

    struct response_t
    {
        control_header ctrl;
        unsigned char  func_code;
        indications    ind;
        unsigned char  buf[BufferSize];
        unsigned       buf_len;
    } _response;

    struct acknowledge_t
    {
        control_header ctrl;
        unsigned char  func_code;
    } _acknowledge;

#pragma pack( pop )

    FunctionCode _request_function;
    int _seqno;

    enum IoState
    {
        Uninitialized = 0,

        //  DNP master states
        SendRequest,
        RecvResponse,
        SendConfirm,

        RecvUnsolicited,

        //  DNP master states
        SendUnexpectedConfirm,

        //  DNP slave state
        SendFirstResponse,
        SendResponse,

        //  DNP loopback (short-circuit to Datalink layer)
        Loopback,

        Complete

    } _appState;

    YukonError_t _errorCondition;

    unsigned _comm_errors;

    std::optional<indications> _iin;

    object_block_queue _out_object_blocks,
                       _in_object_blocks;

    void generateAck( acknowledge_t *app_packet, const control_header ctrl );

    void processResponse( void );

    void eraseOutboundObjectBlocks( void );
    void eraseInboundObjectBlocks( void );

public:
    ApplicationLayer();
    ApplicationLayer(const ApplicationLayer &) = delete;
    ApplicationLayer &operator=(const ApplicationLayer &) = delete;

    //  initialization functions
    void setConfigData( const config_data* config );

    void setLoopback();

    void setCommand( FunctionCode fc );
    void setCommand( FunctionCode fc, ObjectBlockPtr obj );
    void setCommand( FunctionCode fc, std::vector<ObjectBlockPtr> objs );

    void initForOutput( void );

    void initForSlaveOutput( void );
    void setInternalIndications_FunctionCodeUnsupported();
    void setSequenceNumber(int seqNbr);
    void completeSlave( void );
    
    void initUnsolicited( void );

    //  comm functions
    YukonError_t generate( TransportLayer &transport );
    YukonError_t decode  ( TransportLayer &transport );

    //  checking completion
    bool isTransactionComplete( void ) const;
    YukonError_t errorCondition( void ) const;
    bool isOneWay( void )              const;

    //  post-completion processing
    void getObjects( object_block_queue &object_queue );
    bool hasInternalIndications() const;
    std::string getInternalIndications( void ) const;
    YukonError_t getIINErrorCode() const;

    bool hasDeviceRestarted() const;
    bool needsTime() const;

    IM_EX_PROT static std::vector<std::unique_ptr<ObjectBlock>> restoreObjectBlocks(const unsigned char *buf, const unsigned len);

    enum
    {
        ReqHeaderSize = 2,
        RspHeaderSize = 4
    };

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

