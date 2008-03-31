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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2008/03/31 21:17:35 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_KLONDIKE_H__
#define __PROT_KLONDIKE_H__
#pragma warning( disable : 4786)


#include <list>
#include <set>
#include <functional>

#include "dlldefs.h"
#include "pointtypes.h"

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

        operator bool()      const  {  return *this;  }
        Element operator->() const  {  return *this;  }
        Element operator*()  const  {  return (*(static_cast<const set_itr *>(this)))->first;  }
    };

    iterator begin() const  {  return iterator((static_cast<const Inherited *>(this))->begin());  }
    iterator end()   const  {  return iterator((static_cast<const Inherited *>(this))->end());    }

    Inherited::_Pairib insert(Element element)
    {
        if( empty() )  count = 0;

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
    enum Command;
    //enum ProtocolWrap;
    enum Errors;

private:

    struct command_mapping_t : public map<int, Command>
    {
        command_mapping_t();
    };
    /*
    struct error_mapping_t : public map<Errors, int>
    {
        error_mapping_t();
    };
    */
    unsigned short _masterAddress,
                   _slaveAddress;

    Command _command;
    static const command_mapping_t _command_mapping;

    BSTRUCT _dtran_bstruct;  //  the outbound information for a direct transmission
    unsigned char _d_words[DWORDLEN * 3];  //  the response (if any) from a direct transmission
    unsigned char _response_length;

    int _protocol_errors;
    int _sequence;

    typedef fifo_multiset<const OUTMESS *, outmessage_ptr_less> local_work;
    typedef std::map<unsigned, local_work::iterator>            pending_work;
    typedef std::map<unsigned, const OUTMESS *>                 remote_work;

    local_work   _waiting_requests;
    pending_work _pending_requests;
    remote_work  _remote_requests;

    bool _loading_device_queue,
         _reading_device_queue;

    unsigned _device_queue_sequence,
             _device_queue_entries_available;

    unsigned writeDLCMessage(unsigned char *buf, const BSTRUCT &bst);
    unsigned calcDLCMessageLength(const BSTRUCT &bst);
    void processResponse(unsigned char *inbound, unsigned char inbound_length);

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
        ProtocolErrorsMaximum  =   3,
        WrapLengthMaximum      = 255,
        QueueWriteBasePriority =  11,
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

    void doOutput(void);
    void doInput (CtiXfer &xfer);
    static bool response_expected(Command command);

    Klondike(const Klondike &aRef);
    Klondike &operator=(const Klondike &aRef);

    Wrap *_wrap;

    IDLC _idlc_wrap;
    //DNP::Datalink _dnp_wrap;

protected:

public:

    Klondike();
    virtual ~Klondike();

    void setAddresses(unsigned short slaveAddress, unsigned short masterAddress);
    //void setWrap(ProtocolWrap w);

    bool setCommand(int command);
    bool setCommandDirectTransmission(const BSTRUCT &BSt);

    Command getCommand() const;

    void getResultDirectTransmission(unsigned char *buf, int buf_length, unsigned char &input_length);

    int generate(CtiXfer &xfer);
    int decode  (CtiXfer &xfer, int status);

    bool isTransactionComplete(void) const;

    bool errorCondition() const;
    int  errorCode();

    bool hasQueuedWork() const;
    unsigned queuedWorkCount() const;
    bool addQueuedWork(OUTMESS *&OutMessage);

    bool     hasRemoteWork() const;
    unsigned getRemoteWorkPriority() const;
    bool isLoadingDeviceQueue() const;
    bool setLoadingDeviceQueue(bool loading);

    bool     hasWaitingWork() const;
    unsigned getWaitingWorkPriority() const;
    bool isReadingDeviceQueue() const;
    bool setReadingDeviceQueue(bool loading);

    static int decodeDWords(unsigned char *input, unsigned input_length, unsigned Remote, DSTRUCT *DSt);

    /*enum ProtocolWrap
    {
        ProtocolWrap_IDLC,
        ProtocolWrap_DNP,
    };*/

    enum Command
    {
        Command_Invalid = 0,

        Command_DirectMessageRequest = 0x01,

        Command_CheckStatus          = 0x11,

        Command_WaitingQueueClear    = 0x12,
        Command_WaitingQueueWrite    = 0x13,
        Command_WaitingQueueFreeze   = 0x14,
        Command_WaitingQueueThaw     = 0x15,
        Command_WaitingQueueRead     = 0x16,
        Command_ReplyQueueRead       = 0x17,

        Command_TimeSyncCCU          = 0x21,
        Command_TimeSyncTransmit     = 0x22,

        Command_RoutingTableWrite    = 0x31,
        Command_RoutingTableRead     = 0x32,
        Command_RoutingTableRequestAvailableSlots = 0x33,
        Command_RoutingTableClear    = 0x34,

        Command_ConfigurationMemoryRead  = 0x41,
        Command_ConfigurationMemoryWrite = 0x42,

        Command_ACK_NoData           = 0x80,
        Command_ACK_Data             = 0x81,
        Command_NAK                  = 0xc1,
    };

    enum
    {
        DefaultMasterAddress = 5,
        DefaultSlaveAddress  = 1
    };
};


}
}


#endif // #ifndef __PROT_DNP_H__
