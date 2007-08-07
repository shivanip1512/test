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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2007/08/07 21:05:11 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_KLONDIKE_H__
#define __PROT_KLONDIKE_H__
#pragma warning( disable : 4786)


#include <list>

#include "dlldefs.h"
#include "pointtypes.h"

#include "prot_base.h"

#include "dnp_datalink.h"
#include "prot_idlc.h"

namespace Cti       {
namespace Protocol  {

class IM_EX_PROT Klondike : public Interface
{
    enum Command;
    enum Wrap;

private:

    IDLC          _idlc_wrap;
    DNP::Datalink _dnp_wrap;

    unsigned short _masterAddress,
                   _slaveAddress;

    Command _command;

    int _comm_errors;

    enum MiscNumeric
    {
        CommErrorMaximum = 2
    };

    Wrap _protocol_wrap;

    int  wrap_generate(CtiXfer &xfer);
    int  wrap_decode  (CtiXfer &xfer, int status);

    bool wrap_isTransactionComplete();

    Klondike(const Klondike &aRef);
    Klondike &operator=(const Klondike &aRef);

protected:

public:

    Klondike();
    virtual ~Klondike();

    void setAddresses(unsigned short slaveAddress, unsigned short masterAddress);
    void setWrap(Wrap w);

    bool setCommand(Command command );

    int generate(CtiXfer &xfer);
    int decode  (CtiXfer &xfer, int status);

    bool isTransactionComplete(void);

    enum Wrap
    {
        Wrap_IDLC,
        Wrap_DNP,
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
