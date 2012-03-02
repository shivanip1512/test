#pragma once

#include "PlcTransmitter.h"
#include "portlogger.h"
#include "emetconwords.h"

#include "CommInterface.h"

namespace Cti {
namespace Simulator {

class Ccu710 : public PlcTransmitter
{
private:

    struct request_t;
    struct reply_t;

    std::string _ccu710InTag;
    std::string _ccu710OutTag;

public:

    Ccu710(int address, int strategy);

    static bool    addressAvailable(Comms &comms);
    static error_t peekAddress     (Comms &comms, unsigned &address);

    bool handleRequest(Comms &comms, Logger &logger);

    template<class T>
    class holder
    {
    private:
        T _held;
    public:
        holder(T held) : _held(held) { };
        holder() { };

        const T &operator *() const { return  _held; };
        const T *operator->() const { return &_held; };
    };

    typedef holder<request_t> request_holder;
    typedef holder<reply_t>   reply_holder;

    error_t readRequest(CommsIn &comms, request_holder &external_request_holder) const;
    error_t processRequest(const request_holder &request, reply_holder &external_reply_holder, Logger &logger);

private:

    enum Functions
    {
        Function_FeederOperation,
        Function_RetransmitToMaster,
        Function_Loopback,
        Function_RetransmitToRemote,
        Function_ReadCcuIdentification,
        Function_LineMonitor,
        Function_DownloadTime,

        Function_Invalid
    };

    enum FunctionCharacter
    {
        FunctionBit_FeederOperation = 0x04,

        FunctionBits_RetransmitToMaster    = 0x08,
        FunctionBits_Loopback              = 0x10,
        FunctionBits_RetransmitToRemote    = 0x18,
        FunctionBits_ReadCcuIdentification = 0x20,
        FunctionBits_LineMonitor           = 0x28,
        FunctionBits_DownloadTime          = 0x30,

        FunctionBits_Mask                  = 0x34
    };

    enum ReplyControls
    {
        ReplyControl_Acknowledge,
        ReplyControl_NoAcknowledgeSignal,
        ReplyControl_NoAcknowledgeBch,
        ReplyControl_NoAcknowledgeDropout,
        ReplyControl_NoAcknowledgeWait,
        ReplyControl_StartOfDlcReply
    };

    enum ReplyControlCharacters
    {
        ReplyCharacter_Ack  = 0x40,
        ReplyCharacter_NakS = 0x30,
        ReplyCharacter_NakB = 0x38,
        ReplyCharacter_NakD = 0x34,
        ReplyCharacter_NakW = 0x3c,
        ReplyCharacter_Stx  = 0x02
    };

    struct feeder_operation_t
    {
        feeder_operation_t() :
            bus(0),
            amp(0),
            repeater_count(0),
            length(0)
        {};

        unsigned bus;
        unsigned amp;
        unsigned repeater_count;
        unsigned length;

        words_t words;
    };

    struct request_t
    {
        request_t() : address(0) {};

        bytes message;

        unsigned address;

        Functions function;

        feeder_operation_t feeder_operation;

        bytes loopback_response;

        std::string description;
    };

    struct reply_t
    {
        bytes message;

        std::string description;
    };

    enum Strategies
    {
        BAD_D_WORD = 1,
        GARBAGE    = 4
    };

    enum WordTypes
    {
        DEFAULT    = 0,
        INPUT      = 0,
        RESETREQ   = 1,
        RESETACK   = 2,
        GENREQ     = 3,
        GENREP     = 4,
        X_WORD     = 36,
        FEEDEROP   = 21,
        PING       = 22,
        FUNCREAD   = 41,
        READ       = 42,
        WRITE      = 43,
        READREP1   = 44,
        READREP2   = 45,
        READREP3   = 46,
    };

    int _address;
    int _strategy;

    static bool isExtendedAddress(unsigned char address_byte);
    static unsigned char makeReplyControl(unsigned address, ReplyControls reply_control);

    error_t readRequest(CommsIn &comms, request_t &request) const;
    error_t extractFeederOperation(const bytes &feeder_op_buf, feeder_operation_t &feeder_operation) const;

    std::string describeRequest(const request_t &request) const;

    error_t processRequest(const request_t &request, reply_t &reply, Logger &logger);
    error_t validateFeederOperation(const feeder_operation_t &feeder_operation, unsigned &words_expected) const;

    std::string describeReply(const reply_t &reply) const;

    error_t sendReply(CommsOut &comms, const reply_t &reply, Logger &logger) const;

protected:

    static error_t extractAddress(const bytes &buf, unsigned &address);
};

}
}

