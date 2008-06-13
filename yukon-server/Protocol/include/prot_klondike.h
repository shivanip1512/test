/*-----------------------------------------------------------------------------*
*
* File:   prot_klondike
*
* Namespace: Cti::Protocol
* Class:     Klondike
* Date:      2006-aug-08
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2008/06/13 13:39:49 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_KLONDIKE_H__
#define __PROT_KLONDIKE_H__
#pragma warning( disable : 4786)


#include <queue>
#include <set>
#include <functional>

#include "dlldefs.h"
#include "pointtypes.h"
#include "rte_ccu.h"  //  so we can add knowledge of CCU routes

#include "prot_wrap.h"
#include "prot_idlc.h"
//#include "dnp_datalink.h"  //  DNP should be reimplemented as a wrapper protocol


namespace Cti       {
namespace Protocol  {

template <class Element, class Compare=less<Element> >
struct ordered_pair_less : public std::binary_function<std::pair<Element, unsigned>,
                                                       std::pair<Element, unsigned>, bool>
{
    bool operator()(const std::pair<Element, unsigned> &lhs,
                    const std::pair<Element, unsigned> &rhs) const
    {
        bool less = Compare()(lhs.first, rhs.first);

        //  if the .first elements are equal, then compare the ordering element
        if( !less && !Compare()(rhs.first, lhs.first) )
        {
            less = lhs.second < rhs.second;
        }

        return less;
    }
};


template <class Element, class Compare=less<Element> >
class fifo_multiset : public std::set<std::pair<Element, unsigned>, ordered_pair_less<Element, Compare> >
{
    //  we cannot exhaust this count - 10 requests every second would
    //     take 13+ years to hit 4 billion.  We can't talk that fast.
    unsigned count;

    typedef std::set<std::pair<Element, unsigned>, ordered_pair_less<Element, Compare> > Inherited;

public:

    class iterator : public Inherited::iterator
    {
        typedef Inherited::iterator set_itr;

    public:

        iterator(set_itr itr) : set_itr(itr) { }
        iterator() : set_itr() { }

        operator bool()      const  {  return (*(static_cast<const set_itr *>(this)))->first;  /*return *this;*/  }
        Element operator->() const  {  return (*(static_cast<const set_itr *>(this)))->first;  /*return *this;*/  }
        Element operator*()  const  {  return (*(static_cast<const set_itr *>(this)))->first;  }
    };

    iterator begin() const  {  return iterator((static_cast<const Inherited *>(this))->begin());  }
    iterator end()   const  {  return iterator((static_cast<const Inherited *>(this))->end());    }

    Inherited::_Pairib insert(Element element)
    {
        if( empty() )  count = 0;  //  if we ever get a breather, reset the FIFO count

        return Inherited::insert(std::make_pair(element, ++count));
    }
};


struct outmessage_ptr_less : public std::binary_function<OUTMESS *, OUTMESS *, bool>
{
    bool operator()(const OUTMESS *lhs, const OUTMESS *rhs)
    {
        return (lhs && rhs)?(lhs->Priority < rhs->Priority):(rhs);
    }
};


class IM_EX_PROT Klondike : public Interface
{
public:

    typedef std::pair<const OUTMESS *, INMESS *> result_pair_t;
    typedef std::queue<result_pair_t>            result_queue_t;

    enum Errors;
    enum Command;
    //enum ProtocolWrap;

private:

    enum CommandCode;

    struct command_state_map_t : public map<Command, vector<CommandCode> >
    {
        command_state_map_t();
    };

    static const command_state_map_t _command_states;
    friend class command_state_map_t;  //  so it can see our private enum

    struct command_state_t
    {
        Command     command;
        CommandCode command_code;
        unsigned    state;

    } _current_command;

    bool nextCommandState();

    unsigned short _masterAddress,
                   _slaveAddress;

    void refreshMCTTimeSync(BSTRUCT &bst);

    OUTMESS *_dtran_outmess;  //  the outbound information for a direct transmission
    unsigned char _d_words[DWORDLEN * 3];  //  the response (if any) from a direct transmission
    unsigned char _response_length;

    int _protocol_errors;
    int _read_toggle;

    typedef fifo_multiset<const OUTMESS *, outmessage_ptr_less> local_work_t;
    typedef std::map<unsigned, local_work_t::iterator>          pending_work_t;
    typedef std::map<unsigned, const OUTMESS *>                 remote_work_t;

    local_work_t   _waiting_requests;
    pending_work_t _pending_requests;
    remote_work_t  _remote_requests;

    result_queue_t _dlc_results;

    bool _loading_device_queue,
         _reading_device_queue;

    unsigned _device_queue_sequence,
             _device_queue_entries_available;

#pragma pack( push, 1 )
    union device_status
    {
        struct
        {
            unsigned short response_buffer_has_data         : 1;
            unsigned short response_buffer_has_marked_data  : 1;
            unsigned short response_buffer_full             : 1;
            unsigned short transmit_buffer_has_data         : 1;
            unsigned short transmit_buffer_full             : 1;
            unsigned short transmit_buffer_frozen           : 1;
            unsigned short plc_transmitting_dtran_message   : 1;
            unsigned short plc_transmitting_buffer_message  : 1;
            unsigned short time_sync_required               : 1;
            unsigned short reserved                         : 7;
        };
        unsigned char  as_bytes[2];
        unsigned short as_ushort;
    } _device_status;
#pragma pack( pop )

    unsigned writeDLCMessage(unsigned char *buf, const BSTRUCT &bst);
    unsigned calcDLCMessageLength(const BSTRUCT &bst);
    void processResponse(unsigned char *inbound, unsigned char inbound_length);

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
        CommandCode_TimeSyncTransmit     = 0x22,

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

    enum NAK_Errors
    {
        NAK_DTran_DTranBusy = 0x00,
        NAK_DTran_NoRoutes,
        NAK_DTran_InvalidSEQ,
        NAK_DTran_InvalidBUS,
        NAK_DTran_BUSDisabled,
        NAK_DTran_InvalidDLCType,
    };

    int _error_code;

    //static const error_mapping_t _error_mapping;

    enum MiscNumeric
    {
        ProtocolErrorsMaximum  =   1,
        WrapLengthMaximum      = 255,
        QueueWriteBasePriority =  11,
        QueueReadBasePriority  =  11,
        QueueEntryHeaderLength =   4,
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
    static bool responseExpected(CommandCode command);

    Klondike(const Klondike &aRef);
    Klondike &operator=(const Klondike &aRef);

    Wrap *_wrap;

    IDLC _idlc_wrap;
    //DNP::Datalink _dnp_wrap;

    struct route_entry_t
    {
        unsigned char fixed;
        unsigned char variable;
        unsigned char stages;
        unsigned char bus;
        unsigned char spid;
    };

    typedef list<route_entry_t> route_list_t;

    route_list_t _routes;

protected:

public:

    Klondike();
    virtual ~Klondike();

    void setAddresses(unsigned short slaveAddress, unsigned short masterAddress);
    //void setWrap(ProtocolWrap w);

    int setCommand(const OUTMESS *const outmessage);

    Command getCommand() const;

    void getResults(result_queue_t &results);

    int generate(CtiXfer &xfer);
    int decode  (CtiXfer &xfer, int status);

    bool isTransactionComplete(void) const;

    bool errorCondition() const;
    int  errorCode();

    bool hasQueuedWork()  const;
    unsigned queuedWorkCount() const;
    bool addQueuedWork(OUTMESS *&OutMessage);

    bool hasRemoteWork()  const;
    unsigned getRemoteWorkPriority() const;
    bool isLoadingDeviceQueue() const;
    bool setLoadingDeviceQueue(bool loading);

    bool hasWaitingWork() const;
    unsigned getWaitingWorkPriority() const;
    bool isReadingDeviceQueue() const;
    bool setReadingDeviceQueue(bool loading);

    static int decodeDWords(unsigned char *input, unsigned input_length, unsigned Remote, DSTRUCT *DSt);

    void clearRoutes();
    void addRoute(const CtiRouteCCUSPtr &route);

    /*enum ProtocolWrap
    {
        ProtocolWrap_IDLC,
        ProtocolWrap_DNP,
    };*/

    enum Command
    {
        Command_Null,
        Command_Loopback,
        Command_LoadQueue,
        Command_ReadQueue,
        Command_DirectTransmission,
        Command_TimeSync,
        Command_LoadRoutes,
    };

    enum
    {
        DefaultMasterAddress = 5,
        DefaultSlaveAddress  = 1
    };
};


}
}


#endif // #ifndef __PROT_KLONDIKE_H__
