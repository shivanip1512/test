#pragma once

#include "CcuIDLC.h"
#include "types.h"
#include "ctitime.h"
#include "PlcTransmitter.h"
#include "EmetconWords.h"

#include "CommInterface.h"
#include "portlogger.h"

#include "ctidate.h"

#include <boost/scoped_ptr.hpp>

#include <queue>
#include <vector>
#include <set>

namespace Cti {
namespace Simulator {

class Ccu721 : public CcuIDLC
{
public:

    Ccu721(unsigned char address, int strategy);

    virtual bool handleRequest(Comms &comms, Logger &logger);
    static bool validateCommand(SocketComms &socket_interface);

private:

    int _address;
    int _strategy;
    int _expected_sequence;
    unsigned _next_seq;
    bool _bufferFrozen;

    std::string _ccu721InTag;
    std::string _ccu721OutTag;

    CtiTime _timesynced_at, _timesynced_to;

    enum IDLCSizes
    {
        LoadBufferMaxSlots = 8,
        DWordBytesLength   = 7,
        CWordBytesLength   = 7,
        Idlc_HeaderLength  = 3,
        Info_HeaderLength  = 2,
        Idlc_CrcLength     = 2
        // CCU_ReplyLength   = 14  // Refer to Section 2 EMETCON Protocols, 4-86, pdf page 123
    };

    enum KlondikeCommandCodes
    {
        Klondike_Dtran =              0x01,
        Klondike_CheckStatus =        0x11,
        Klondike_ClearBuffers =       0x12,
        Klondike_LoadBuffer =         0x13,
        Klondike_FreezeBuffer =       0x14,
        Klondike_ThawBuffer =         0x15,
        Klondike_SpyBuffer =          0x16,
        Klondike_ReadBuffer =         0x17,
        Klondike_TimeSync =           0x21,
        Klondike_WriteRouteTable =    0x31,
        Klondike_ReadRouteTable =     0x32,
        Klondike_ReadRouteTableOpen = 0x33,
        Klondike_ClearRouteTable =    0x34,
        Klondike_ReadMemory =         0x41,
        Klondike_WriteMemory =        0x42,
        Klondike_AckNoData =          0x80,
        Klondike_AckData =            0x81,
        Klondike_NAK =                0xc1,

        Klondike_CommandInvalid =  0xff
    };

    enum HdlcLinkCommandOctets
    {
        HdlcLink_Sarm = 0x1f,
        HdlcLink_Rej  = 0x19,
        HdlcLink_Srej = 0x1d,
        HdlcLink_Ud   = 0x13,

        HdlcLink_RejectMask = 0x1f,

        HdlcLink_Ua   = 0x73
    };

    enum IdlcLinkCommands
    {
        IdlcLink_GeneralRequest,
        IdlcLink_GeneralReply,
        IdlcLink_BroadcastRequest,
        IdlcLink_RejectWithRestart,
        IdlcLink_RetransmitRequest,
        IdlcLink_ResetRequest,
        IdlcLink_ResetAcknowledge,

        IdlcLink_Invalid
    };

    enum StatusDescriptions
    {
        Status_TimeSyncRequired     = 0x01,
        Status_RespBufHasData       = 0x100,
        Status_RespBufMarkedData    = 0x200,
        Status_RespBufFull          = 0x400,
        Status_TransBufHasData      = 0x800,
        Status_TransBufFull         = 0x1000,
        Status_TransBufFrozen       = 0x2000,
        Status_PLCTransDtran        = 0x4000,
        Status_PLCTransBuf          = 0x8000
    };

    struct queue_entry
    {
        queue_entry() :
            priority(0),
            sequence(0),
            dlc_length(0)
        {};

        unsigned priority;
        unsigned sequence;
        unsigned dlc_length;

        struct request_info
        {
            request_info() :
                word_type(EmetconWord::WordType_Invalid),
                //address(0),
                bus(0),
                //repeater_fixed(0),
                //repeater_variable(0),
                //function_code(0),
                length(0),
                write(false),
                function(false),
                broadcast(false),
                dlcType(0),
                stagesToFollow(0)
            {};

            CtiTime arrival;

            bool broadcast;
            unsigned bus;
            unsigned length;
            unsigned stagesToFollow;
            unsigned short dlcType;

            EmetconWord::WordTypes word_type;

            bool write;
            bool function;

            bytes data;

            EmetconWordB b_word;
            std::vector<EmetconWordC> c_words;

        } request;

        struct result_info
        {
            CtiTime completion_time;

            bytes data;

            words_t as_words;

        } result;

        struct pending_less
        {
            bool operator()(const queue_entry &lhs, const queue_entry &rhs) const
            {
                if( lhs.priority < rhs.priority )  return true;
                if( lhs.priority > rhs.priority )  return false;
                return lhs.request.arrival < rhs.request.arrival;
            };
        };

        struct completed_less
        {
            bool operator()(const queue_entry &lhs, const queue_entry &rhs) const
            {
                if( lhs.priority < rhs.priority )  return true;
                if( lhs.priority > rhs.priority )  return false;
                return lhs.result.completion_time < rhs.result.completion_time;
            };
        };
    };

    struct routeTable_entry
    {
        routeTable_entry() :
            routeNumber(0),
            fixedBits(0),
            variableBits(0),
            stagesToFollow(0),
            busCoupling(0)
        { };

        int routeNumber;
        int fixedBits;
        int variableBits;
        int stagesToFollow;
        int busCoupling;
    };

    struct idlc_header
    {
        idlc_header() :
            control_command(IdlcLink_Invalid),
            address(0),
            control_sequence(0),
            control_sequence_expected(0)
        { };

        int address;

        IdlcLinkCommands control_command;

        int control_sequence;
        int control_sequence_expected;
    };

    struct request_info
    {
        request_info() :
            command(Klondike_CommandInvalid),
            reply_length(0){ };

        KlondikeCommandCodes command;

        int reply_length;

        struct xtime_info
        {
            xtime_info() :
                year(0),
                day(0),
                day_of_week(0),
                period(0),
                seconds(0)
            {};

            unsigned year, day, day_of_week, period, seconds;

            CtiTime timesync;

        } xtime;

        struct routeTable_info
        {
            routeTable_info() : clearAllRoutes(false) { };

            bool clearAllRoutes;
            std::vector<int> routesToClear;
            std::vector<routeTable_entry> write_requests;

        } routeTable;

        struct readBuffer_info
        {
            readBuffer_info() : flags(0) {};

            unsigned char flags;

        } readBuffer;

        struct loadBuffer_info
        {
            std::vector< queue_entry > request_group;

        } loadBuffer;

        struct clearBuffer_info
        {
            clearBuffer_info() : clearAll(false) {};

            bool clearAll;
            std::vector<int> sequences;

        } clearBuffer;

        struct memoryAccess_info
        {
            memoryAccess_info() :
                memoryRead(true),
                type(0),
                startAddress(0),
                numBytes(0)
            { };

            bool memoryRead;
            int type;
            unsigned startAddress;
            unsigned numBytes;
            bytes data;

        } memoryAccess;

        struct dtran_info
        {
            queue_entry bookkeeperEntry;
            bytes message;

        } dtran;
    };

    struct idlc_request
    {
        idlc_header  header;
        request_info info;

        bytes message;

        std::string description;
    };

    struct reply_info
    {
        reply_info() : command(Klondike_CommandInvalid) {};

        KlondikeCommandCodes command;

        std::vector< queue_entry > collected_queue_entries;

        struct memory_info
        {
            unsigned startAddress;
            unsigned numBytes;
            int type;
            bytes data;

        } memoryAccess;

        // For future use. When the Read Routing Table command is actually implemented,
        // this will be used to store the entries that are being requested.
        //  struct routeTable_info
        //  {
        //      std::vector<routeTable_entry> entries;
        //
        //  } routeTable;

        struct loadBuffer_info
        {
            loadBuffer_info() :
                accepted(0),
                available(0)
            { };

            int accepted;
            int available;

        } loadBuffer;

        struct readBuffer_info
        {
            readBuffer_info() : flags(0) { };

            unsigned char flags;

        } readBuffer;

        struct dtran_info
        {
            unsigned sequence;
            bytes message;

        } dtran;
    };

    struct idlc_reply
    {
        idlc_header header;
        reply_info  info;

        bytes message;

        std::string description;
    };

    struct queue_info
    {
        typedef std::multiset<const queue_entry, queue_entry::pending_less>   pending_set;
        typedef std::multiset<const queue_entry, queue_entry::completed_less> completed_set;

        pending_set   pending;

        completed_set completed;
        completed_set returned;

        CtiTime last_transmit;

    } _queue;

    struct status_info
    {
        status_info() : status_bytes(0) { };

        unsigned short status_bytes;

    } _status;

    void processQueue(Logger &logger);

    // This function returns seconds.
    static unsigned queue_request_dlc_time(const queue_entry::request_info &request);

    error_t readRequest           (Comms &comms, idlc_request &request) const;

    error_t extractIdlcHeader       (const bytes &message, idlc_header &header) const;

    error_t extractRequestInfo              (const bytes &message, request_info &info) const;
    error_t extractRequestInfo_Dtran        (const bytes &command_data, request_info &info) const;
    error_t extractRequestInfo_ReadBuffer   (const bytes &command_data, request_info &info) const;
    error_t extractRequestInfo_TimeSync     (const bytes &command_data, request_info &info) const;
    error_t extractRequestInfo_ClrBuffers   (const bytes &command_data, request_info &info) const;
    error_t extractRequestInfo_LoadBuffer   (const bytes &command_data, request_info &info) const;
    error_t extractRequestInfo_WriteRtTable (const bytes &command_data, request_info &info) const;
    error_t extractRequestInfo_ClrRtTable   (const bytes &command_data, request_info &info) const;
    error_t extractRequestInfo_ReadMem      (const bytes &command_data, request_info &info) const;
    error_t extractRequestInfo_WriteMem     (const bytes &command_data, request_info &info) const;

    error_t extractQueueEntry(const bytes &command_data, int index, int nextSequence, queue_entry &a_queue_entry) const;

    error_t validateCrc     (const bytes &message) const;

    std::string describeRequest       (const idlc_request &request) const;
    std::string describeGeneralRequest(const request_info &info)    const;

    error_t processRequest        (const idlc_request &request, idlc_reply &reply, Logger &logger);
    error_t processGeneralRequest (const idlc_request &request, idlc_reply &reply, Logger &logger);
    error_t processDtranRequest   (const idlc_request &request, idlc_reply &reply, Logger &logger);

    std::string describeReply         (const idlc_reply   &reply)      const;
    std::string describeGeneralReply  (const reply_info   &reply_info) const;
    std::string describeStatuses      (const unsigned short &status)   const;

    error_t sendReply(Comms &comms, const idlc_reply &reply, Logger &logger) const;

    error_t writeIdlcHeader   (const idlc_header &header,  byte_appender &out_itr)          const;
    error_t writeReplyInfo    (const reply_info  &info,    byte_appender &out_itr)          const;
    error_t writeReplyAckInfo (const KlondikeCommandCodes &command, byte_appender &out_itr) const;
    error_t writeReplyStatus  (const unsigned short &status,  byte_appender &out_itr)       const;
    error_t writeIdlcCrc      (const bytes     &message, byte_appender &out_itr)            const;

    error_t setStatusBit  (const StatusDescriptions &statusPos);
    error_t resetStatusBit(const StatusDescriptions &statusPos);

    error_t updateStatusBytes();
};

}
}
