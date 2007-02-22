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
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2007/02/22 22:01:45 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <strstrea.h>

#include "cparms.h"
#include "devicetypes.h"
#include "numstr.h"

#include "dev_foreignporter.h"

namespace Cti    {
namespace Device {

ForeignPorter::ForeignPorter()  { }


ForeignPorter::~ForeignPorter() { }


bool ForeignPorter::isTransactionComplete()
{
    return _complete;
}


int ForeignPorter::decode(CtiXfer &xfer, int status)
{
    int retVal = NoError;

    if( !status )
    {
        _complete = true;

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

    _complete = false;
    _errors   = 0;

    CtiCommandParser parse(OutMessage->Request.CommandStr);

    if( parse.getCommand() != ControlRequest )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - unsupported command type (" << parse.getCommand() << ") in ForeignPorter::recvCommRequest() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else
    {
        switch( parse.getiValue("type") )
        {
            default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " **** Checkpoint - unsupported protocol type (" << parse.getiValue("type") << ") in ForeignPorter::recvCommRequest() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }

                _complete = true;

                break;
            }

            case ProtocolEmetconType:
            {
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

                break;
            }

            case ProtocolExpresscomType:
            {
                int routeid;

                if( !(routeid = gConfigParms.getValueAsInt("FOREIGN_PORTER_" + CtiNumStr(getID()) + "_ROUTEID", 0)) )
                {
                    routeid = gConfigParms.getValueAsInt("FOREIGN_PORTER_ROUTEID", 0);
                }

                CtiRequestMsg msg(SYS_DID_SYSTEM, OutMessage->Request.CommandStr, 0, 0, routeid);

                rw_ostream << msg;

                string &str = str_buf.str();
                _out_count = str.size();
                _out_buf = CTIDBG_new unsigned char[_out_count];
                memcpy(_out_buf, str.c_str(), _out_count);

                break;
            }
        }
    }

    _in_buf = CTIDBG_new unsigned char[MaxInbound];

    return NoError;
}


}
}

