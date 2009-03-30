#pragma once

#include "ccu710.h"
#include "ctitime.h"
#include "fifo_multiset.h"
#include "PlcTransmitter.h"

#include "CommInterface.h"
#include "portlogger.h"

#include <boost/scoped_ptr.hpp>

#include <queue>
#include <vector>

namespace Cti {
namespace Simulator {

class Ccu711 : public PlcTransmitter
{
public:

    Ccu711(unsigned char address, int strategy);

    static bool    addressAvailable(Comms &comms);
    static error_t peekAddress     (Comms &comms, unsigned &address);

    enum
    {
        Hdlc_FramingFlag = 0x7e
    };

    bool handleRequest(Comms &comms, PortLogger &logger);

private:

    enum Miscellaneous
    {
        Idlc_HeaderLength = 3,
        Info_HeaderLength = 3,
        Idlc_CrcLength    = 2
    };

    enum HdlcLinkCommandOctets
    {
        HdlcLink_Sarm = 0x1f,
        HdlcLink_Rej  = 0x19,
        HdlcLink_Srej = 0x1d,
        HdlcLink_Ud   = 0x13,

        HdlcLink_RejectMask = 0x1f,

        HdlcLink_Ua   = 0x73,
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

    enum CommandOctets
    {
        Command_Actin = 0x00,
        Command_WSets = 0x09,
        Command_DTran = 0x26,
        Command_RColQ = 0x27,
        Command_Xtime = 0x2a,
        Command_LGrpQ = 0x2b,

        Command_Invalid = 0xff
    };

    struct idlc_header
    {
        idlc_header() : control_command(IdlcLink_Invalid) { };

        int address;

        IdlcLinkCommands control_command;

        int control_sequence;
        int control_sequence_expected;
    };

    struct queue_entry
    {
        queue_entry() {};

        unsigned entry_id;
        unsigned priority;

        struct request_info
        {
            request_info() : word_type(EmetconWord::WordType_Invalid) {};

            CtiTime arrival;

            unsigned address;
            unsigned bus;
            unsigned repeater_fixed;
            unsigned repeater_variable;
            unsigned repeater_count;
            unsigned function_code;
            unsigned length;

            EmetconWord::WordTypes word_type;

            bool write;
            bool function;

            bytes data;

            EmetconWordB b_word;
            vector<EmetconWordC> c_words;

        } request;

        unsigned xmit_duration;

        struct result_info
        {
            result_info() : completion_status(CompletionStatus_NoAttempt) {};

            CtiTime completion_time;

            bytes data;

            words_t as_words;

            enum CompletionStatuses
            {
                //  see Section 2 EMETCON Protocols, 4-71 to 4-72, pdf pages 106-107
                CompletionStatus_NoAttempt,
                CompletionStatus_Successful,
                CompletionStatus_RouteFailure,
                CompletionStatus_TransponderFailure

            } completion_status;

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

    struct request_info
    {
        request_info() : command(Command_Invalid) { };

        CommandOctets command;

        int reply_length;

        struct xtime_info
        {
            unsigned year, day, day_of_week, period, seconds;

            CtiTime timesync;

        } xtime;

        struct lgrpq_info
        {
            std::vector< queue_entry > request_group;

        } lgrpq;

        struct dtran_info
        {
            bytes message;

            Ccu710::request_holder request;

        } dtran;
    };

    struct idlc_request
    {
        idlc_header  header;
        request_info info;

        bytes message;

        std::string description;
    };

    struct status_info
    {
        status_info();  //  default values assigned here

        struct stats_info
        {
            bool power;
            bool faultc;
            bool deadmn;
            bool coldst;
            bool nsadj;
            bool algflt;
            bool reqack;
            bool broadc;
            bool battry;
            bool jouren;
            bool jourov;
            bool badtim;

            enum AlgorithmStatus
            {
                AlgSt_Inactive         = 0,
                AlgSt_EnabledAndHalted = 1,
                AlgSt_Running          = 2,
                AlgSt_Suspended        = 3,  //  Intentionally duplicated, as per Section 2 EMETCON Protocols 4-37, pdf page 68.
                AlgSt_StepCompleted    = 3   //

            } alg_st[7];

        } stats;

        struct statd_info
        {
            bool hdrmck;
            bool battst;
            bool dlcflt;
            bool cplpw;
            unsigned readyn;
            unsigned ncsets;
            unsigned ncocts;

        } statd;

        struct
        {
//  TODO-P3: do we want statp for the individual ddddd algorithms?
//        refer to Section 2 EMETCON Protocols.pdf, page 70 (4-39)
        } statp;

    } _status;

    struct reply_info
    {
        CommandOctets command;

        status_info status;

        unsigned reply_length;

        std::vector< queue_entry > collected_queue_entries;

        struct dtran_info
        {
            Ccu710::reply_holder reply;
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

    CtiTime _timesynced_at, _timesynced_to;

    Ccu710 _ccu710;

    int _address;
    int _strategy;
    int _expected_sequence;

    struct queue_info
    {
        typedef fifo_multiset<const queue_entry, queue_entry::pending_less>   pending_set;
        typedef fifo_multiset<const queue_entry, queue_entry::completed_less> completed_set;

        pending_set   pending;

        completed_set completed;
        completed_set returned;

        CtiTime last_transmit;

    } _queue;

    void processQueue(PortLogger &logger);

    error_t extractData(const words_t &reply_words, byte_appender &output);

    static unsigned queue_request_dlc_time(const queue_entry::request_info &request);

    error_t readRequest(Comms &comms, idlc_request &request) const;

    error_t extractIdlcHeader       (const bytes &message, idlc_header &header) const;

    error_t extractRequestInfo      (const bytes &message, request_info &info) const;
    error_t extractRequestInfo_DTran(const bytes &message, request_info &info) const;
    error_t extractRequestInfo_RColQ(const bytes &message, request_info &info) const;
    error_t extractRequestInfo_XTime(const bytes &message, request_info &info) const;
    error_t extractRequestInfo_LGrpQ(const bytes &message, request_info &info) const;

    error_t extractQueueEntry       (const bytes &message, const int index, const int setl, queue_entry &queue_entry) const;

    error_t validateCrc             (const bytes &message) const;

    string describeRequest       (const idlc_request &request) const;
    string describeGeneralRequest(const request_info &info)    const;

    error_t processRequest       (const idlc_request &request, idlc_reply &reply);
    error_t processGeneralRequest(const idlc_request &request, idlc_reply &reply);

    string describeReply         (const idlc_reply   &reply)      const;
    string describeGeneralReply  (const reply_info   &reply_info) const;
    string describeStatuses      (const status_info  &statuses)   const;

    error_t sendReply(Comms &comms, const idlc_reply &reply) const;

    error_t writeIdlcHeader (const idlc_header &header,  byte_appender &out_itr) const;
    error_t writeReplyInfo  (const reply_info  &info,    byte_appender &out_itr) const;
    error_t writeReplyStatus(const status_info &status,  byte_appender &out_itr) const;
    error_t writeIdlcCrc    (const bytes     &message, byte_appender &out_itr) const;
};

}
}

