/*-----------------------------------------------------------------------------*
*
* File:   prot_711
*
* Date:   4/27/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/prot_711.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/02/10 23:23:57 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <iostream>
#include <iomanip>
using namespace std;

#include "cticalls.h"
#include "prot_711.h"
#include "logger.h"
#include "dllbase.h"

CtiProtocol711& CtiProtocol711::setMasterRequest(const BYTE *ptr)
{
    if(ptr[0] == 0x7e)
    {
        memcpy( _masterToSlave, ptr, 4 + ptr[3] );
    }
    return *this;
}

CtiProtocol711& CtiProtocol711::setSlaveResponse(const BYTE *ptr)
{
    if(ptr[0] == 0x7e)
    {
        memcpy( _slaveToMaster, ptr, 4 + ptr[3] );
    }
    return *this;
}

void CtiProtocol711::describeSlaveResponse() const
{
    BYTE flag = _slaveToMaster[0];
    BYTE address = _slaveToMaster[1] >> 1;
    BYTE control = _slaveToMaster[2];
    BYTE rrr = _slaveToMaster[2] >> 5;
    BYTE sss = (_slaveToMaster[2] >> 1) & 0x07;
    BYTE final = (_slaveToMaster[2] & 0x10) >> 4;
    BYTE length = _slaveToMaster[3];
    BYTE src  = _slaveToMaster[4] >> 6;
    BYTE dest = _slaveToMaster[4] & 0x3f;

    if(flag == 0x7e)
    {
        if(_slaveToMaster[2] & 0x01)
        {

        }
        else                             // General Reply
        {
            BYTE cmnd =  _slaveToMaster[5] & 0x7F;
            BYTE stats[4];
            BYTE statd[6];
            BYTE statp[2];

            const BYTE *data = &_slaveToMaster[18];

            INT  data_len = length - 14;        // 14 addnl header bytes!

            memcpy(stats, &_slaveToMaster[6], 4);
            memcpy(statd, &_slaveToMaster[10], 6);
            memcpy(statp, &_slaveToMaster[16], 2);

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                char oldfill = dout.fill('0');

                dout << hex << setw(2) << (INT)_slaveToMaster[0] << dec << " IDLC Frame header is valid " << endl;
                dout << hex << setw(2) << (INT)_slaveToMaster[1] << dec << " Reply from CCU Address " << (INT)address << endl;
                dout << hex << setw(2) << (INT)_slaveToMaster[2] << dec << "   Control Word (rrrfsss0): "  << endl;
                dout << hex << setw(2) << (INT)_slaveToMaster[2] << dec << "      Expected Sequence of Next Request (rrr) " << (INT)rrr << endl;
                dout << hex << setw(2) << (INT)_slaveToMaster[2] << dec << "      Sequence of Current Frame (sss) " << (INT)sss << endl;
                dout << hex << setw(2) << (INT)_slaveToMaster[2] << dec << "      This " << (final ? "IS" : "IS NOT") << " the final frame (f)" << endl;
                dout << hex << setw(2) << (INT)_slaveToMaster[3] << dec << "   " << (INT)length << " bytes follow in this message " << endl;

                dout << hex << setw(2) << (INT)_slaveToMaster[4] << dec << "   SRC/DES Word (mmdddddd): " << endl;
                dout << hex << setw(2) << (INT)_slaveToMaster[4] << dec << "      Master Station Subprocess (mm) " << (INT)src << endl;
                dout << hex << setw(2) << (INT)_slaveToMaster[4] << dec << "      CCU Subprocess (dddddd) " << (INT)dest << ": ";

                switch(dest)
                {
                case 0:
                    {
                        dout << "BASE STORES / IDLC CONTROL" << endl;
                        break;
                    }
                case 1 :
                    {
                        dout << "QUEUING CONTROL" << endl;
                        break;
                    }
                case 2:
                    {
                        dout << "DLC CONTROL" << endl;
                        break;
                    }
                case 4:
                    {
                        dout << "TIME SYNC" << endl;
                        break;
                    }
                case 5:
                    {
                        dout << "LOAD MANAGEMENT CONTROL" << endl;
                        break;
                    }
                }

                dout << hex << setw(2) << (INT)_slaveToMaster[5] << dec << "   CMND (Command) (1ggggttt): " << (INT)cmnd;

                switch(cmnd)
                {
                case CMND_ACTIN:
                    {
                        dout << "      ACTIN CMND Vol. 1 / pp. 4-41 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_WMEMY:
                    {
                        dout << "      WMEMY CMND Vol. 1 / pp. 4-54 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_RMEMY:
                    {
                        dout << "      RMEMY CMND Vol. 1 / pp. 4-55 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_RCONT:
                    {
                        dout << "      RCONT CMND Vol. 1 / pp. 4-57 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_WMEMS:
                    {
                        dout << "      WSETS CMND Vol. 1 / pp. 4-59 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_RMEMS:
                    {
                        dout << "      RSETS CMND Vol. 1 / pp. 4-61 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_DTRAN:
                    {
                        dout << "      DTRAN CMND Vol. 1 / pp. 4-83 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_XTIME:
                    {
                        dout << "      XTIME CMND Vol. 1 / pp. 4-91 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_ITIME:
                    {
                        dout << "      ITIME CMND Vol. 1 / pp. 4-65 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_CQENS:
                    {
                        dout << "      CQENS CMND Vol. 1 / pp. 4-68 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_RQENS:
                    {
                        dout << "      RQENS CMND Vol. 1 / pp. 4-69 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_RQDIR:
                    {
                        dout << "      RQDIR CMND Vol. 1 / pp. 4-81 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_RCOLQ:
                    {
                        dout << "      RCOLQ CMND Vol. 1 / pp. 4-86 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_LGRPQ:
                    {
                        dout << "      LGRPQ CMND Vol. 1 / pp. 4-92 " << (INT)cmnd << endl;
                        break;
                    }
                default:
                    {
                        dout << "      Unknown " << (INT)cmnd << endl;
                        break;
                    }
                }
#if 1
                dout << hex << setw(2) << (INT)_slaveToMaster[6] << dec<< " through " << hex << setw(2) << (INT)_slaveToMaster[9] << dec << "  STATS " << endl;
                describeSlaveStatS(&_slaveToMaster[6]);
                dout << hex << setw(2) << (INT)_slaveToMaster[10] << dec<< " through " << hex << setw(2) << (INT)_slaveToMaster[15] << dec << "  STATD " << endl;
                describeSlaveStatD(&_slaveToMaster[10]);
                dout << hex << setw(2) << (INT)_slaveToMaster[16] << dec<< " through " << hex << setw(2) << (INT)_slaveToMaster[17] << dec << "  STATP " << endl;
                describeSlaveStatP(&_slaveToMaster[16]);
#endif
                dout.fill(oldfill);
            }

            switch(cmnd)
            {
            case CMND_RCOLQ:
                {
                    describeRCOLQResponse(data, data_len);
                    break;
                }
            case CMND_LGRPQ:
            case CMND_XTIME:
                {
                    break;         // Minimal content header has been qualified.
                }
            case CMND_ACTIN:
                {
                    describeACTNResponse();
                    break;
                }
            case CMND_WMEMY:
            case CMND_RMEMY:
            case CMND_WMEMS:
            case CMND_RMEMS:
            case CMND_DTRAN:
            case CMND_ITIME:
            case CMND_CQENS:
            case CMND_RQENS:
            case CMND_RQDIR:
            default:
                {
                    dout << " ACH: Define rest of protocol" << endl;
                    break;
                }
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " IDLC Error " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}
void CtiProtocol711::describeMasterRequest() const
{
    INT i;

    BYTE flag      = _masterToSlave[0];
    BYTE address   = _masterToSlave[1] >> 1;
    BYTE control   = _masterToSlave[2];
    BYTE rrr       = (control >> 5);
    BYTE sss       = (control >> 1) & 0x07;
    BYTE final     = (control & 0x10) >> 4;
    BYTE length    = _masterToSlave[3];
    BYTE src       = _masterToSlave[4] >> 6;
    BYTE dest      = _masterToSlave[4] & 0x3f;

    char oldfill = dout.fill('0');

    if(flag == 0x7e)
    {
        if(control & 0x01)               // Some other specific request
        {

        }
        else                             // General Request
        {
            BYTE cmnd =  _masterToSlave[5] & 0x7f;
            const BYTE *data = &_masterToSlave[6];
            INT  data_len = length - 2;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << hex << setw(2) << (INT)_masterToSlave[0] << dec << " IDLC Frame header is valid " << endl;
                dout << hex << setw(2) << (INT)_masterToSlave[1] << dec << " Request to CCU Address " << (INT)address << endl;
                dout << hex << setw(2) << (INT)_masterToSlave[2] << dec << "   Control Word (rrrfsss0):"  << endl;
                dout << hex << setw(2) << (INT)_masterToSlave[2] << dec << "      Expected Sequence of Next Request (rrr) " << (INT)rrr << endl;
                dout << hex << setw(2) << (INT)_masterToSlave[2] << dec << "      Sequence of Current Frame (sss) " << (INT)sss << endl;
                dout << hex << setw(2) << (INT)_masterToSlave[2] << dec << "      This " << (final ? "IS" : "IS NOT") << " the final frame (f)" << endl;
                dout << hex << setw(2) << (INT)_masterToSlave[3] << dec << "   " << (INT)length << " bytes follow in this message " << endl;

                dout << hex << setw(2) << (INT)_masterToSlave[4] << dec << "   SRC/DES Word (mmdddddd):" << endl;
                dout << hex << setw(2) << (INT)_masterToSlave[4] << dec << "      Master Station Subprocess (mm) " << (INT)src << endl;
                dout << hex << setw(2) << (INT)_masterToSlave[4] << dec << "      CCU Subprocess (dddddd) " << (INT)dest << ": ";

                switch(dest)
                {
                case 0:
                    {
                        dout << "BASE STORES / IDLC CONTROL" << endl;
                        break;
                    }
                case 1 :
                    {
                        dout << "QUEUING CONTROL" << endl;
                        break;
                    }
                case 2:
                    {
                        dout << "DLC CONTROL" << endl;
                        break;
                    }
                case 4:
                    {
                        dout << "TIME SYNC" << endl;
                        break;
                    }
                case 5:
                    {
                        dout << "LOAD MANAGEMENT CONTROL" << endl;
                        break;
                    }
                }

                dout << hex << setw(2) << (INT)_masterToSlave[5] << dec << "   CMND (Command) (0ggggttt): " << (INT)cmnd;

                switch(cmnd)
                {
                case CMND_ACTIN:
                    {
                        dout << "      ACTIN CMND Vol. 1 / pp. 4-41 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_WMEMY:
                    {
                        dout << "      WMEMY CMND Vol. 1 / pp. 4-54 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_RMEMY:
                    {
                        dout << "      RMEMY CMND Vol. 1 / pp. 4-55 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_RCONT:
                    {
                        dout << "      RCONT CMND Vol. 1 / pp. 4-57 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_WMEMS:
                    {
                        dout << "      WSETS CMND Vol. 1 / pp. 4-59 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_RMEMS:
                    {
                        dout << "      RSETS CMND Vol. 1 / pp. 4-61 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_DTRAN:
                    {
                        dout << "      DTRAN CMND Vol. 1 / pp. 4-83 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_XTIME:
                    {
                        dout << "      XTIME CMND Vol. 1 / pp. 4-91 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_ITIME:
                    {
                        dout << "      ITIME CMND Vol. 1 / pp. 4-65 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_CQENS:
                    {
                        dout << "      CQENS CMND Vol. 1 / pp. 4-68 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_RQENS:
                    {
                        dout << "      RQENS CMND Vol. 1 / pp. 4-69 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_RQDIR:
                    {
                        dout << "      RQDIR CMND Vol. 1 / pp. 4-81 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_RCOLQ:
                    {
                        dout << "      RCOLQ CMND Vol. 1 / pp. 4-86 " << (INT)cmnd << endl;
                        break;
                    }
                case CMND_LGRPQ:
                    {
                        dout << "      LGRPQ CMND Vol. 1 / pp. 4-92 " << (INT)cmnd << endl;
                        break;
                    }
                default:
                    {
                        dout << "      Unknown " << (INT)cmnd << endl;
                        break;
                    }
                }

                if(data_len > 0)
                {
                    dout << "     CMND data. Bytes " << 7 << " through " << 7 + data_len << endl;
                    dout << "      ";

                    for(i = 0; i < data_len; i++)
                    {
                        dout << hex << setw(2) << (INT)data[i] << " ";
                    }
                    dout << dec << endl;
                }
            }

            switch(cmnd)
            {
            case CMND_RCOLQ:
                {
                    describeRCOLQRequest(data, data_len);
                    break;
                }
            case CMND_LGRPQ:
                {
                    describeLGRPQRequest(data, data_len);
                    break;
                }
            case CMND_XTIME:
                {
                    describeXTIMERequest(data, data_len);
                    break;
                }
            case CMND_ACTIN:
                {
                    describeACTNRequest(data, data_len);
                    break;
                }
            case CMND_RCONT:
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << endl << "RCONT CMND: Vol. 1 / pp. 4-57 " << endl;
                    dout << "  Continue Extended Read from CCU" << endl;

                    break;
                }
            case CMND_DTRAN:
                {
                    describeDTRANRequest(data, data_len);
                    break;
                }
            case CMND_WMEMY:
            case CMND_RMEMY:
            case CMND_WMEMS:
            case CMND_RMEMS:
            case CMND_ITIME:
            case CMND_CQENS:
            case CMND_RQENS:
            case CMND_RQDIR:
            default:
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** ACH: Command type not yet decoded **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                    break;
                }
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " IDLC Error " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    dout.fill(oldfill);

}


void CtiProtocol711::describeRCOLQRequest(const BYTE *data, INT len) const
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << "RCOLQ CMND: Vol. 1 / pp. 4-86 " << endl;

        dout << hex << setw(2) << (INT)data[0] << dec << "  RLEN (reply length RLEN + 14 returned from CCU " << (INT)data[0] << endl;
    }
}

void CtiProtocol711::describeRCOLQResponse(const BYTE *data, INT len) const
{
    INT byte_offset;
    INT setl = data[0];
    INT ensta = data[5] >> 4;

    INT nfunc = (INT)data[10];

    BYTE ts[2];
    INT L[3] = {0, 0, 0};

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        char oldfill = dout.fill('0');

        dout << endl << "RCOLQ CMND: Vol. 1 / pp. 4-86 " << endl;

        dout << hex << setw(2) << (INT)data[0] << dec << "  SETL (set length) " << setl << endl;

        if(setl > 0)
        {
            dout << hex << setw(2) << (INT)data[1] << dec<< " through " << hex << setw(2) << (INT)data[4] << dec << "  QENID " << endl;
            dout << hex << setw(2) << (INT)data[1] << dec << " & " << hex << setw(2) << (INT)data[2] << "  Porter (internal TrxInfo queue entry) " << MAKEUSHORT(data[2], data[1]) << endl;
            dout << hex << setw(2) << (INT)data[3] << dec << " & " << hex << setw(2) << (INT)data[4] << "  OUTMESS->Sequence " << MAKEUSHORT(data[4], data[3]) << endl;
            dout << hex << setw(2) << (INT)data[5] << dec << " ENSTA: ";

            switch(ensta)
            {
            case 12:
                {
                    dout << "  REQUEST REJECT: - UNDEF. (Completed)  " << endl;
                    break;
                }
            case 13:
                {
                    dout << "  REQUEST REJECT: - UNDEF. (Completed)  " << endl;
                    break;
                }
            case 14:
                {
                    dout << "  REQUEST REJECT: - FUNCTION ERROR. (Completed)  " << endl;
                    break;
                }
            case 15:
                {
                    dout << "  ACTION COMPLETE:  " << endl;
                    break;
                }
            default:
                {
                    dout << "  UNDEFINED COMPLETION STATE " << endl;
                    break;
                }
            }

            dout << hex << setw(2) << (INT)data[6] << " through " << setw(2) << (INT)data[8] << dec << "  STIME 1st (8 hr period of week) 2-3 (second into period)" << endl;

            if(data[9] == 255)
            {
                dout << hex << setw(2) << (INT)data[9] << dec << "  ROUTE defined by request " << endl;
            }
            else
            {
                dout << hex << setw(2) << (INT)data[9] << dec << "  ROUTE " << endl;
            }
            dout << hex << setw(2) << nfunc << dec << "  NFUNC - number of functions in this request  " << endl;

#if 0
            for(byte_offset = 0; byte_offset < nfunc; byte_offset += 3)
            {
                L[byte_offset] = (INT)data[13 +byte_offset];

                dout << hex << setw(2) << (INT)data[11 + byte_offset] << " & " << setw(2) << (INT)data[12 + byte_offset] << dec << "  S" << byte_offset+1 << " - status of communication " << byte_offset+1 << endl;
                dout << hex << setw(2) << (INT)data[13 +byte_offset] << dec << "  L" << byte_offset+1 << " - data length of communication " << byte_offset+1 << endl;
            }

            byte_offset += 11;

            memcpy(ts, &data[byte_offset],  2);
            dout << hex << setw(2) << (INT)data[byte_offset] << " & " << setw(2) << (INT)data[1 + byte_offset] << dec << "  TS - transponder status "  << endl;

            byte_offset += 2;
            INT data_max = byte_offset + L[0];


            for( ; byte_offset < data_max; byte_offset++)
            {
                dout << hex << setw(2) << (INT)data[byte_offset] << " ";
            }
            dout << dec << endl;
#endif

            dout.fill(oldfill);
        }
    }
}

void CtiProtocol711::describeACTNRequest(const BYTE *data, INT len) const
{
    INT i;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        char oldfill = dout.fill('0');

        dout << endl << "ACTIN CMND: Vol. 1 / pp. 4-41 " << endl;


        for(i = 0; i < len; i++)
        {
            dout << hex << setw(2) << (INT)data[i] << dec << "  ACTN:  " << (INT)data[i];

            switch(data[i])
            {
            case 0:
                {
                    dout << "  No-Op  " << (INT)data[i] << endl;
                    break;
                }
            case 1:
                {
                    dout << "  CLRAL - Init. process control stores  " << (INT)data[i] << endl;
                    break;
                }
            case 2:
                {
                    dout << "  CLRDY - Init. process dynamic control stores  " << (INT)data[i] << endl;
                    break;
                }
            case 3:
                {
                    dout << "  START - Start process.  " << (INT)data[i] << endl;
                    break;
                }
            case 4:
                {
                    dout << "  HOPRO - Halt orderly, process  " << (INT)data[i] << endl;
                    break;
                }
            case 5:
                {
                    dout << "  ENPRO - Enable execution of process  " << (INT)data[i] << endl;
                    break;
                }
            case 6:
                {
                    dout << "  DSPRO - Disable execution of process  " << (INT)data[i] << endl;
                    break;
                }
            case 7:
                {
                    dout << "  CJOUR - Clear journal  " << (INT)data[i] << endl;
                    break;
                }
            default:
                {
                    dout << "  ACTIN code not yet defined  " << (INT)data[i] << endl;
                    break;
                }
            }

            dout.fill(oldfill);
        }
    }
}

void CtiProtocol711::describeACTNResponse() const
{
}

void CtiProtocol711::describeDTRANRequest(const BYTE *data, INT len) const
{
    INT i;

    {
        INT rlen = data[0];
        INT loa = data[1] & 0x03;
        INT feeder = ((data[1] & 0x31) >> 3) + 1;
        INT amp = ((data[2] & 0x40) ? 2 : 1);
        INT rtf = data[2] & 0x07;
        INT ctf = data[3] & 0x3f;




        CtiLockGuard<CtiLogger> doubt_guard(dout);
        char oldfill = dout.fill('0');

        dout << endl << "DTRAN CMND: Vol. 1 / pp. 4-83 " << endl;

        dout << "  RLEN  " << rlen << " reply length expected " << endl;
        dout << "  PREAMBLE: " << endl;
        dout << "    Feeder            " << feeder << endl;
        dout << "    Amp               " << amp << endl;
        dout << "    Stages to Follow  " << rtf << endl;
        dout << "    Number of chars   " << ctf << endl;

        dout << "    ";

        for(i = 4; i < ctf + 4; i++)
        {
            dout << hex << setw(2) << (INT)data[i] << " ";
        }

        dout << endl;

        INT wordt = (data[4] >> 4) & 0x0e;

        switch(wordt)
        {
        case 10: // b-word;
            {
                BYTE var = (data[4] & 0x0e) >> 1;
                BYTE fix = ((data[4] & 0x01) << 4) | ((data[5] & 0xf0) >> 4);

                INT address = data[5] & 0x0f;
                address = (address << 8) | data[6];
                address = (address << 8) | data[7];
                address = (address << 2) | (data[8] >> 6);

                INT words = (data[8] & 0x30) >> 4;
                INT func = ((data[8] & 0x0f) << 4) | (data[9] >> 4);
                INT io = (data[9] & 0x0c) >> 2;


                dout << "  BWORD: " << endl;
                dout << "    Variable bits    " << hex << setw(2) << (INT)var << endl;
                dout << "    Fixed    bits    " << hex << setw(2) << (INT)fix << endl;
                dout << "    Address          " << dec << address << endl;
                dout << "    Words to follow  " << words << endl;
                dout << "    Function/Address " << hex << setw(2) << func << endl;
                dout << "    IO               " << hex << setw(2) << io << endl;

                break;
            }
        case 8:  // a-word
        default:
            {
                break;
            }
        }




        dout.fill(oldfill);
    }
}

void CtiProtocol711::describeLGRPQRequest(const BYTE *data, INT len) const
{
    INT      i;
    BYTE     setl = data[0];

    bool     shortformat = ( data[1] & 0x80 ? true : false );
    BYTE     qenid[4];
    BYTE     priority = data[5];

    BYTE     feeder;
    BYTE     vvv;
    BYTE     fffff;
    BYTE     RRR;
    BYTE     nfunc;

    char oldfill = dout.fill('0');

    memcpy(qenid, &data[1], 4);

    if(shortformat)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** ACH: short format does not compute **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    else  // long format LGRPQ
    {
        ULONG dlcaddr = MAKEULONG( MAKEUSHORT(data[8], data[7]), (USHORT)data[6] );

        if(data[9] & 0x80)
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** ACH: does not compute **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
        else
        {
            feeder = (data[9] & 0x07) + 1;
            vvv = data[10] >> 5;
            fffff = data[10] & 0x1f;
            RRR = data[11] & 0x07;
            nfunc = data[12];

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << endl << "LGRPQ CMND: Vol. 1 / pp. 4-92 " << endl;

                dout << hex << setw(2) << (INT)data[0] << dec << "  SETL (set length) " << (INT)setl << endl;
                dout << hex << setw(2) << (INT)data[1] << dec << (shortformat ? "  Short Format " : "  Long Format") << endl;
                dout << hex << setw(2) << (INT)data[1] << dec<< " through " << hex << setw(2) << (INT)data[4] << dec << "  QENID " << endl;
                dout << hex << setw(2) << (INT)data[1] << dec << " and " << hex << setw(2) << (INT)data[2] << "  Porter (internal TrxInfo queue entry) " << MAKEUSHORT(data[2], data[1]) << endl;
                dout << hex << setw(2) << (INT)data[3] << dec << " and " << hex << setw(2) << (INT)data[4] << "  OUTMESS->Sequence " << MAKEUSHORT(data[4], data[3]) << endl;
                dout << hex << setw(2) << (INT)data[5] << dec << "  Priority (0 - 15 max) " << (INT)priority << endl;
                dout << hex << setw(2) << (INT)data[6] << dec << " through " << hex << (INT)data[8] << dec << "  DLC remote address " << (INT)dlcaddr << endl;
                dout << hex << setw(2) << (INT)data[9] << dec << "  Feeder " << (INT)feeder << endl;
                dout << hex << setw(2) << (INT)data[10] << dec << "  Variable Bits " << (INT)vvv << endl;
                dout << hex << setw(2) << (INT)data[10] << dec << "  Fixed Bits " << (INT)fffff << endl;
                dout << hex << setw(2) << (INT)data[11] << dec << "  Repeaters to follow " << (INT)RRR << endl;
                dout << hex << setw(2) << (INT)data[12] << dec << "  NFUNC: number of functions to follow " << (INT)nfunc << endl;
            }
        }

        if(0 < nfunc && nfunc <= 3 )
        {
            BYTE dlcword;
            BYTE dblrpt;
            BYTE io;
            BYTE dlclen;
            BYTE bwordcmd;

            INT nfunc_offset = 13;
            INT dlcaddr;

            for(i = 0; i < nfunc; i++)
            {
                dlcword = data[nfunc_offset] >> 6;
                dblrpt = (data[nfunc_offset] & 0x20) >> 5;
                bwordcmd = data[nfunc_offset+2];
                io =  (data[nfunc_offset] & 0x18) >> 3;
                dlclen = data[nfunc_offset + 3];

                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << hex << setw(2) << (INT)data[nfunc_offset] << " through " << setw(2) << (INT)data[nfunc_offset + 4] << dec << "  DLC COMMAND: " << endl;

                    switch(dlcword)
                    {
                    case 0x00:
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** ACH: UNUSED/ERROR! **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            break;
                        }
                    case 0x02:
                        {
                            dout << hex << setw(2) << (INT)data[nfunc_offset] << dec << " bits 7-6 indicate a BWORD COMMAND " << endl;
                            break;
                        }
                    case 0x03:
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** ACH: Word type GWORD **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    case 0x01:
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** ACH: Word type AWORD **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                    default:
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** ACH: Word type not yet decoded! **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                            break;
                        }
                    }

                    dout << hex << setw(2) << (INT)data[nfunc_offset] << dec << " bit 5 indicates command " << (dblrpt ? "WILL" : "WILL NOT") << " be double'd" << endl;
                    dout << hex << setw(2) << (INT)data[nfunc_offset] << dec << " bits 4-3 indicate io type ";

                    switch(io)
                    {
                    case 0:
                        {
                            dout << "WRITE" << endl;
                            break;
                        }
                    case 1:
                        {
                            dout << "READ" << endl;
                            break;
                        }
                    case 2:
                        {
                            dout << "FUNCTION WRITE" << endl;
                            break;
                        }
                    case 3:
                        {
                            dout << "FUNCTION READ" << endl;
                            break;
                        }
                    }
                }

                switch(dlcword)
                {
                case 0x00:
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** ACH: UNUSED/ERROR! **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        break;
                    }
                case 0x02:
                    {
                        dout << hex << setw(2) << (INT)data[nfunc_offset+2] << " BWORD FUNCTION " << (INT)bwordcmd << dec << endl;
                        dout << hex << setw(2) << (INT)data[nfunc_offset+3] << dec << " BWORD LENGTH " << (INT)dlclen << endl;
                        break;
                    }
                case 0x03:
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** ACH: Word type GWORD **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                case 0x01:
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " **** ACH: Word type AWORD **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                default:
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** ACH: Word type not yet decoded! **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        break;
                    }
                }

                nfunc_offset += 4;
            }

            if(len > nfunc_offset + 1)
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << hex << setw(2) << (INT)data[nfunc_offset] << dec << " to end.  Data to be written to the DLC device" << endl;
                }
            }
        }
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** ACH: Unknown CTL function Vol. 1 / pp. 4-96 **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    dout.fill(oldfill);
}

void CtiProtocol711::describeXTIMERequest(const BYTE *data, INT len) const
{
    INT year = (INT)MAKEUSHORT(data[1], data[0]);
    INT juln = (INT)MAKEUSHORT(data[3], data[2]);
    INT dow  = data[4];
    INT perd = data[5];
    INT sec  = (INT)MAKEUSHORT(data[7], data[6]);

    INT period = (INT)perd * (8 * 3600);

    INT tsec = period + sec;

    INT hour = (tsec / 3600);
    INT minute = (tsec - (hour * 3600)) / 60;
    INT second = (tsec - (hour * 3600) - (minute * 60));

    CHAR day[10];

    switch(dow)
    {
    case 1:
        {
            strcpy(day, "Sunday");
            break;
        }
    case 2:
        {
            strcpy(day, "Monday");
            break;
        }
    case 3:
        {
            strcpy(day, "Tuesday");
            break;
        }
    case 4:
        {
            strcpy(day, "Wednesday");
            break;
        }
    case 5:
        {
            strcpy(day, "Thursday");
            break;
        }
    case 6:
        {
            strcpy(day, "Friday");
            break;
        }
    case 7:
        {
            strcpy(day, "Saturday");
            break;
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << endl << "XTIME CMND: Vol. 1 / pp. 4-92 " << endl;
        dout << hex << setw(2) << (INT)data[0] << " & " << setw(2) << (INT)data[1] << dec << "  YEAR (year) " << (INT)year << endl;
        dout << hex << setw(2) << (INT)data[2] << " & " << setw(2) << (INT)data[3] << dec << "  JULN (julian day-of-year number) " << (INT)juln << endl;
        dout << hex << setw(2) << (INT)data[4] << dec << "  DOW (day-of-week number. 1 = Sun.): " << (INT)day << endl;
        dout << hex << setw(2) << (INT)data[5] << dec << "  PERD (period of day). Seconds since midnight: " << period << endl;
        dout << hex << setw(2) << (INT)data[2] << " & " << setw(2) << (INT)data[3] << dec << "  SEC (seconds into period) " << (INT)sec << endl;

        dout << dec << "        Time set to " << setw(2) << hour << ":" << setw(2) << minute << ":" << setw(2) << second << endl;
    }

}

void CtiProtocol711::describeSlaveStatS(const BYTE *stat) const
{
    dout << "  STATS:" << endl;
    dout << "    AC Power                  " << (stat[0] & 0x01 ? "outage detected": "good / outage not detected") << endl;
    dout << "    Slave fault               " << (stat[0] & 0x02 ? "detected": "not detected") << endl;
    dout << "    Deadman circuit           " << (stat[0] & 0x04 ? "activated": "inactive") << endl;
    dout << "    Cold start                " << (stat[0] & 0x08 ? "detected": "not detected") << endl;
    dout << "    Sequence adjust           " << (stat[0] & 0x10 ? "detected": "not detected") << endl;
    dout << "    Alg. fault                " << (stat[0] & 0x20 ? "detected": "not detected") << endl;
    dout << "    Sequence adjust           " << (stat[0] & 0x10 ? "detected": "not detected") << endl;
}
void CtiProtocol711::describeSlaveStatD(const BYTE *stat) const
{
    dout << "  STATD Not yet decoded" << endl;
}
void CtiProtocol711::describeSlaveStatP(const BYTE *stat) const
{
    dout << "  STATP Not yet decoded" << endl;
}
