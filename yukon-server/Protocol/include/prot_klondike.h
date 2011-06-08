#pragma once

#include "prot_wrap.h"
#include "prot_idlc.h"
//#include "dnp_datalink.h"  //  DNP should be reimplemented as a wrapper protocol

#include "critical_section.h"

#include <queue>
#include <functional>
#include <set>

namespace Cti {
namespace Protocols {

class IM_EX_PROT KlondikeProtocol : public Cti::Protocol::Interface
{
public:

    typedef std::vector<unsigned char> byte_buffer_t;

    enum Errors;
    enum Command;
    enum PLCProtocols;
    //enum ProtocolWrap;

    struct queue_result_t
    {
        void *requester;
        Errors error;
        unsigned long timestamp;
        byte_buffer_t message;

        queue_result_t(void *requester_, Errors error_, unsigned long timestamp_, const byte_buffer_t &message_) :
            requester(requester_),
            error    (error_),
            timestamp(timestamp_),
            message  (message_)
        {
        };

        queue_result_t() :
            requester(0),
            error(Error_None),
            timestamp(0)
        {
        };
    };

private:

    enum CommandCode;

    struct command_state_map_t : public std::map<Command, std::vector<CommandCode> >
    {
        command_state_map_t();
    };

    static const command_state_map_t _command_states;
    friend class command_state_map_t;  //  so it can see our private enums

    struct command_state_t
    {
        Command     command;
        CommandCode command_code;
        unsigned    state;

    } _current_command;

    bool nextCommandState();
    bool commandStateValid();

    unsigned short _masterAddress,
                   _slaveAddress;

    byte_buffer_t _raw_command;

    Errors _error;

    int _wrap_errors;
    int _read_toggle;

    struct queue_entry_t
    {
        unsigned priority;
        PLCProtocols protocol;
        unsigned char dlc_parms;
        unsigned char stages;
        byte_buffer_t outbound;
        unsigned resubmissions;
        void *requester;

        queue_entry_t() :
            protocol(PLCProtocol_Invalid),
            priority(0),
            dlc_parms(0),
            stages(0),
            resubmissions(0),
            requester(0)
        { }

        queue_entry_t(const byte_buffer_t &outbound_, unsigned priority_, unsigned char dlc_parms_, unsigned char stages_, void *requester_) :
            protocol (PLCProtocol_Emetcon),
            outbound (outbound_),
            priority (priority_),
            dlc_parms(dlc_parms_),
            stages   (stages_),
            resubmissions(0),
            requester(requester_)
        { }

        bool operator>(const queue_entry_t &rhs) const   {  return priority > rhs.priority;  };
    };

    typedef std::multiset<queue_entry_t, std::greater<queue_entry_t> > local_work_t;
    typedef std::map<unsigned, local_work_t::iterator>                 pending_work_t;
    typedef std::map<unsigned, queue_entry_t>                          remote_work_t;

    mutable CtiCriticalSection _sync;
    typedef CtiLockGuard<CtiCriticalSection> sync_guard_t;

    local_work_t   _waiting_requests;
    pending_work_t _pending_requests;
    remote_work_t  _remote_requests;

    struct queue_response_t
    {
        unsigned short sequence;
        unsigned long  timestamp;
        unsigned short signal_strength;
        unsigned char  result;
        byte_buffer_t  message;

        queue_response_t(byte_buffer_t::const_iterator &buf, const byte_buffer_t::const_iterator &end)
        {
            if( std::distance(buf, end) < 10 )
            {
                throw std::range_error("Not enough bytes to restore queue entry");
            }

            sequence         = *buf++;
            sequence        |= *buf++ <<  8;

            timestamp        = *buf++;
            timestamp       |= *buf++ <<  8;
            timestamp       |= *buf++ << 16;
            timestamp       |= *buf++ << 24;

            signal_strength  = *buf++;
            signal_strength |= *buf++ <<  8;

            result           = *buf++;

            unsigned char message_length = *buf++;

            if( std::distance(buf, end) < message_length )
            {
                throw std::range_error("Not enough bytes to restore queue entry's message");
            }

            message.assign(buf, buf + message_length);

            buf += message_length;
        }
    };

    std::vector<queue_result_t> _plc_results;

    queue_entry_t _dtran_queue_entry;
    byte_buffer_t _dtran_result;
    unsigned      _dtran_in_expected;

    bool _loading_device_queue,
         _reading_device_queue;

    unsigned _device_queue_sequence,
             _device_queue_entries_available;

#pragma pack( push, 1 )
    union device_status
    {
        struct
        {
            unsigned short response_buffer_has_unmarked_data : 1;
            unsigned short response_buffer_has_marked_data   : 1;
            unsigned short response_buffer_full              : 1;
            unsigned short transmit_buffer_has_data          : 1;
            unsigned short transmit_buffer_full              : 1;
            unsigned short transmit_buffer_frozen            : 1;
            unsigned short plc_transmitting_dtran_message    : 1;
            unsigned short plc_transmitting_buffer_message   : 1;
            unsigned short time_sync_required                : 1;
            unsigned short broadcast_in_progress             : 1;
            unsigned short reserved                          : 6;
        };
        unsigned char  as_bytes[2];
        unsigned short as_ushort;
    } _device_status;
#pragma pack( pop )

    void processResponse(const byte_buffer_t &inbound);
    void processFailed();

    enum CommandCode
    {
        CommandCode_Null = 0,

        CommandCode_DirectMessageRequest = 0x01,

        CommandCode_CheckStatus          = 0x11,

        CommandCode_WaitingQueueClear    = 0x12,
        CommandCode_WaitingQueueWrite    = 0x13,
        CommandCode_WaitingQueueFreeze   = 0x14,
        CommandCode_WaitingQueueThaw     = 0x15,
        CommandCode_WaitingQueueSpy      = 0x16,
        CommandCode_ReplyQueueRead       = 0x17,

        CommandCode_TimeSyncCCU          = 0x21,

        CommandCode_RoutingTableWrite                 = 0x31,
        CommandCode_RoutingTableRead                  = 0x32,
        CommandCode_RoutingTableRequestAvailableSlots = 0x33,
        CommandCode_RoutingTableClear                 = 0x34,

        CommandCode_ConfigurationMemoryRead  = 0x41,
        CommandCode_ConfigurationMemoryWrite = 0x42,

        CommandCode_ACK_NoData           = 0x80,
        CommandCode_ACK_Data             = 0x81,
        CommandCode_NAK                  = 0xc1,
    };

    enum NAK_Codes
    {
        NAK_DirectTransmission_DTranBusy = 0x00,
        NAK_DirectTransmission_NoRoutes,
        NAK_DirectTransmission_InvalidSequence,
        NAK_DirectTransmission_InvalidBus,
        NAK_DirectTransmission_BusDisabled,
        NAK_DirectTransmission_InvalidDLCType,
        NAK_DirectTransmission_InvalidMessageLength,
        NAK_DirectTransmission_TransmitterOverheating,

        NAK_LoadBuffer_QueueEntries = 0x00,
        NAK_LoadBuffer_NoRoutes,
        NAK_LoadBuffer_InvalidSequence,
        NAK_LoadBuffer_InvalidBus,
        NAK_LoadBuffer_BusDisabled,
        NAK_LoadBuffer_InvalidDLCType,
        NAK_LoadBuffer_InvalidMessageLength,
        NAK_LoadBuffer_QueueFull,

        NAK_ReadACKReplyBuffer_InvalidRTI = 0x00,
        NAK_ReadACKReplyBuffer_InvalidRTIReset,
        NAK_ReadACKReplyBuffer_InvalidBufferData,

        NAK_TimeSyncCCU_TimeSyncFailed = 0x00,

        NAK_Memory_InvalidAddress = 0x00,
        NAK_Memory_AccessFailed,
        NAK_Memory_InvalidType,
        NAK_Memory_InvalidNumberOfBytes
    };

    enum MiscNumeric
    {
        BlockReadFlag_ReadToggleId  = 0x01,
        BlockReadFlag_AckOnlyNoData = 0x02,

        WrapErrorsMaximum      =   1,
        WrapLengthMaximum      = 255,
        QueueWriteBasePriority =  11,
        QueueReadBasePriority  =  11,
        QueueEntryHeaderLength =   4,  //  CCU-721 Master Station Interface section 2.3.4.4: bytes 5-8 (assume bus is always nonzero)
        RoutesMaximum          =  32,
        QueueEntryResubmissionsMaximum = 3
    };

    enum IO_State
    {
        IO_Invalid,

        IO_Output,
        IO_Input,

        IO_Complete,
        IO_Failed,

    } _io_state;

    void doOutput(CommandCode command_code);
    void doInput (CommandCode command_code, CtiXfer &xfer);
    unsigned getPLCTiming(PLCProtocols protocol);

    enum PLCProtocolInfo
    {
        Emetcon_bps = 56  //  approximately 1 second per word per transmitter
    };

    static bool responseExpected(CommandCode command);

    KlondikeProtocol(const KlondikeProtocol &aRef);
    KlondikeProtocol &operator=(const KlondikeProtocol &aRef);

    Cti::Protocol::Wrap *_wrap;

    Cti::Protocol::IDLC _idlc_wrap;
    //DNP::Datalink _dnp_wrap;

    struct route_entry_t
    {
        unsigned char fixed;
        unsigned char variable;
        unsigned char stages;
        unsigned char bus;
    };

    typedef std::list<route_entry_t> route_list_t;

    route_list_t _routes;

protected:

    void setWrap(Cti::Protocol::Wrap *wrap);    //  unit test access function
    virtual long currentTime();  //  unit test access function override

public:

    KlondikeProtocol();
    virtual ~KlondikeProtocol();

    void setAddresses(unsigned short slaveAddress, unsigned short masterAddress);

    int setCommand(int command, const byte_buffer_t payload=byte_buffer_t(), unsigned in_expected=0, unsigned priority=0, unsigned char stages=0, unsigned char dlc_parms=0);

    Command getCommand() const;

    std::vector<queue_result_t> getQueuedResults();
    byte_buffer_t getDTranResult();

    int generate(CtiXfer &xfer);
    int decode  (CtiXfer &xfer, int status);

    bool isTransactionComplete(void) const;

    bool   errorCondition() const;
    Errors errorCode() const;

    // --  these functions may be called at any time by another thread, meaning that their data must be muxed
    std::string describeCurrentStatus(void) const;

    bool hasQueuedWork()  const;
    bool addQueuedWork(void *requester, const byte_buffer_t &payload, unsigned priority, unsigned char dlc_parms, unsigned char stages);
    bool removeQueuedWork(void *requester);

    bool hasRemoteWork()  const;
    unsigned getRemoteWorkPriority() const;
    bool isLoadingDeviceQueue() const;
    bool setLoadingDeviceQueue(bool loading);

    bool hasWaitingWork() const;
    unsigned getWaitingWorkPriority() const;
    bool isReadingDeviceQueue() const;
    bool setReadingDeviceQueue(bool loading);

    struct request_status
    {
        void *requester;
        unsigned queue_id;
        unsigned priority;
    };

    typedef std::vector<request_status> request_statuses;

    void getRequestStatus(request_statuses &waiting, request_statuses &pending, request_statuses &queued, request_statuses &completed) const;

    void clearRoutes();
    void addRoute(unsigned bus, unsigned fixed, unsigned variable, unsigned stages);

    enum PLCProtocols
    {
        PLCProtocol_Invalid,
        PLCProtocol_Emetcon
    };

    enum DLCParms
    {
        DLCParms_None          = 0x00,
        DLCParms_BroadcastFlag = 0x10
    };

    /*enum ProtocolWrap
    {
        ProtocolWrap_IDLC,
        ProtocolWrap_DNP,
    };*/

    enum Command
    {
        Command_Invalid,
        Command_Loopback,
        Command_LoadQueue,
        Command_ReadQueue,
        Command_DirectTransmission,
        Command_TimeSync,
        Command_TimeRead,
        Command_LoadRoutes,

        Command_Raw
    };

    enum Errors
    {
        Error_None,

        Error_NoRoutes,
        Error_DTranBusy,
        Error_InvalidSequence,
        Error_InvalidBus,
        Error_BusDisabled,
        Error_InvalidDLCType,
        Error_InvalidMessageLength,
        Error_TransmitterOverheating,
        Error_QueueFull,
        Error_QueueEntryLost,

        Error_Unknown
    };

    enum
    {
        DefaultMasterAddress = 5,
        DefaultSlaveAddress  = 1
    };
};


}
}

