#pragma once

#include "ccu710.h"
#include "ctitime.h"
#include "PlcTransmitter.h"
#include "BehaviorCollection.h"

#include "CommInterface.h"
#include "portlogger.h"
#include "CcuIDLC.h"

#include <boost/scoped_ptr.hpp>

#include <queue>
#include <vector>
#include <set>

namespace Cti {
namespace Simulator {

class Ccu711 : public CcuIDLC
{
public:

    Ccu711(unsigned char address, int strategy);

    virtual bool handleRequest(Comms &comms, Logger &logger);
    static bool validateCommand(SocketComms &socket_interface);

private:

    enum TSBitValues
    {
        TS_AlarmEnabled = 0x0100
    };

    enum IDLCSizes
    {
        Idlc_HeaderLength = 3,
        Info_HeaderLength = 3,
        Idlc_CrcLength    = 2,
        CCU_ReplyLength   = 14  // Refer to Section 2 EMETCON Protocols, 4-86, pdf page 123
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

    struct queue_entry
    {
        queue_entry() :
            entry_id(0),
            priority(0)
        {};

        unsigned entry_id;
        unsigned priority;

        struct request_info
        {
            request_info() :
                word_type(EmetconWord::WordType_Invalid),
                address(0),
                bus(0),
                repeater_fixed(0),
                repeater_variable(0),
                repeater_count(0),
                function_code(0),
                length(0),
                write(false),
                function(false)
            {};

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
            std::vector<EmetconWordC> c_words;

        } request;

        unsigned xmit_duration;

        struct result_info
        {
            result_info() :
                completion_status(CompletionStatus_NoAttempt),
                ts_values(0)
            {};

            CtiTime completion_time;

            bytes data;
            unsigned short ts_values;

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
        request_info() :
            command(Command_Invalid),
            reply_length(0){ };

        CommandOctets command;

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
            bool dummy;
        } statp;

    } _status;

    struct reply_info
    {
        reply_info() : reply_length(0) {};

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

    std::string _ccu711InTag;
    std::string _ccu711OutTag;

    int _address;
    int _strategy;
    int _expected_sequence;

    struct queue_info
    {
        typedef std::multiset<queue_entry, queue_entry::pending_less>   pending_set;
        typedef std::multiset<queue_entry, queue_entry::completed_less> completed_set;

        pending_set   pending;

        completed_set completed;
        completed_set returned;

        CtiTime last_transmit;

    } _queue;

    void processQueue(Logger &logger);

    error_t extractInformation(const words_t &reply_words, queue_entry &entry);
    error_t extractData(const words_t &reply_words, byte_appender &output);
    error_t extractTS_Values(const words_t &reply_words, queue_entry &entry);

    // This function returns seconds.
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

    std::string describeRequest       (const idlc_request &request) const;
    std::string describeGeneralRequest(const request_info &info)    const;

    error_t processRequest       (const idlc_request &request, idlc_reply &reply, Logger &logger);
    error_t processGeneralRequest(const idlc_request &request, idlc_reply &reply, Logger &logger);

    std::string describeReply         (const idlc_reply   &reply)      const;
    std::string describeGeneralReply  (const reply_info   &reply_info) const;
    std::string describeStatuses      (const status_info  &statuses)   const;

    error_t sendReply(Comms &comms, const idlc_reply &reply, Logger &logger) const;

    error_t writeIdlcHeader (const idlc_header &header,  byte_appender &out_itr) const;
    error_t writeReplyInfo  (const reply_info  &info,    byte_appender &out_itr) const;
    error_t writeReplyStatus(const status_info &status,  byte_appender &out_itr) const;
    error_t writeIdlcCrc    (const bytes     &message, byte_appender &out_itr) const;
};

}
}

