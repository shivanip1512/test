/*-----------------------------------------------------------------------------*
*
* File:   dev_foreignporter
*
* Date:   2006-sep-21
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_710.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2006/09/23 13:06:47 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <strstrea.h>

#include "cparms.h"
#include "devicetypes.h"
#include "numstr.h"

#include "dev_foreignporter.h"

#include "dev_grp_emetcon.h"

namespace Cti    {
namespace Device {

ForeignPorter::ForeignPorter()  { }


ForeignPorter::~ForeignPorter() { }


bool ForeignPorter::isTransactionComplete()
{
    return _msg_sent;
}


int ForeignPorter::decode(CtiXfer &xfer, int status)
{
    int retVal = NoError;

    if( !status )
    {
        _msg_sent = true;

        if( xfer.getInCountActual() )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Device \"" << getName() << "\" received (" << xfer.getInCountActual() << ") bytes from foreign Porter connection **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }

        delete [] _in_buf;
        _in_buf = 0;
    }
    else
    {
        if( ++_errors > MaxErrors )
        {
            retVal = status;
        }
    }

    return retVal;
}


int ForeignPorter::generate(CtiXfer &xfer)
{
    xfer.setOutBuffer(_out_buf);
    xfer.setOutCount(_out_count);

    xfer.setNonBlockingReads(true);
    xfer.setInCountExpected(0);
    xfer.setInCountActual(&_in_count);

    return NoError;
}


int ForeignPorter::recvCommRequest(OUTMESS *OutMessage)
{
    std::basic_stringstream<char> str_buf;
    RWpostream rw_ostream(str_buf);

    _msg_sent = false;
    _errors   = 0;

    string cmd = "control emetcon ";

    switch( OutMessage->Buffer.ASt.Function )
    {
        case A_SCRAM:
        case A_SHED_A:
        case A_SHED_B:
        case A_SHED_C:
        case A_SHED_D:
        {
            cmd += "shed ";

            switch( OutMessage->Buffer.ASt.Time )
            {
                case TIME_7_5:  cmd += "7m ";   break;
                case TIME_15:   cmd += "15m ";  break;
                case TIME_30:   cmd += "30m ";  break;
                case TIME_60:   cmd += "60m ";  break;
            }

            break;
        }

        case A_LATCH_OPEN:  cmd += "open ";     break;

        case A_LATCH_CLOSE: cmd += "close ";    break;

        case A_RESTORE:     cmd += "restore ";  break;
    }

    switch( OutMessage->Buffer.ASt.Function )
    {
        case A_SHED_A:      cmd += "relay 1 ";  break;
        case A_SHED_B:      cmd += "relay 2 ";  break;
        case A_SHED_C:      cmd += "relay 3 ";  break;
        case A_SHED_D:      cmd += "relay 4 ";  break;
        case A_SCRAM:       cmd += "relay 1,2,3,4 ";  break;
    }

    if( OutMessage->Buffer.ASt.Group < 60 )
    {
        cmd += " silver " + CtiNumStr(OutMessage->Buffer.ASt.Group + 1);
    }
    else
    {
        cmd += " gold " + CtiNumStr(OutMessage->Buffer.ASt.Group - 59);
    }

    CtiRequestMsg msg(SYS_DID_SYSTEM, cmd, 0, 0, gConfigParms.getValueAsInt("FOREIGN_PORTER_ROUTEID", 0));

    rw_ostream << msg;

    string &str = str_buf.str();
    _out_count = str.size();
    _out_buf = CTIDBG_new unsigned char[_out_count];
    memcpy(_out_buf, str.c_str(), _out_count);

    _in_buf = CTIDBG_new unsigned char[MaxInbound];

    return NoError;
}


}
}

