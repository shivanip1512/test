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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2007/09/04 16:46:18 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_KLONDIKE_H__
#define __PROT_KLONDIKE_H__
#pragma warning( disable : 4786)


#include <list>

#include "dlldefs.h"
#include "pointtypes.h"

#include "prot_wrap.h"

#include "prot_idlc.h"
//#include "dnp_datalink.h"  //  DNP should be reimplemented as a wrapper protocol


namespace Cti       {
namespace Protocol  {


class IM_EX_PROT Klondike : public Interface
{
    enum Command;
    enum ProtocolWrap;

private:

    unsigned short _masterAddress,
                   _slaveAddress;

    Command _command;

    BSTRUCT _dtran_bstruct;  //  the information needed for a direct transmission

    unsigned char _outbound[255];

    int _comm_errors;
    int _sequence;

    enum MiscNumeric
    {
        CommErrorMaximum = 2
    };

    enum IO_State
    {
        IO_Invalid,

        IO_Output,
        IO_Input,

        IO_Complete,

    } _io_state;

    void doOutput(void);

    Klondike(const Klondike &aRef);
    Klondike &operator=(const Klondike &aRef);

    Wrap *_wrap;

    IDLC          _idlc_wrap;
    //DNP::Datalink _dnp_wrap;

protected:

public:

    Klondike();
    virtual ~Klondike();

    void setAddresses(unsigned short slaveAddress, unsigned short masterAddress);
    void setWrap(ProtocolWrap w);

    bool setCommand(int command);
    void setDirectTransmissionInfo(BSTRUCT &BSt);

    int generate(CtiXfer &xfer);
    int decode  (CtiXfer &xfer, int status);

    bool isTransactionComplete(void);

    enum ProtocolWrap
    {
        ProtocolWrap_IDLC,
        ProtocolWrap_DNP,
    };

    enum Command
    {
        Command_Invalid = 0,

        Command_DirectMessageRequest,

        Command_WaitingQueueWrite,
        Command_WaitingQueueFreeze,
        Command_WaitingQueueRead,
        Command_WaitingQueueClear,
        Command_ReplyQueueRead,

        Command_TimeSyncCCU,
        Command_TimeSyncTransmit,

        Command_RoutingTableWrite,
        Command_RoutingTableRead,
        Command_RoutingTableRequestAvailableSlots,
        Command_RoutingTableClear,

        Command_ConfigurationMemoryRead,
        Command_ConfigurationMemoryWrite,

        Command_Failed,
        Command_Complete,
    };

    enum
    {
        DefaultMasterAddress =    5,
        DefaultSlaveAddress  =    1
    };
};


}
}


#endif // #ifndef __PROT_DNP_H__
