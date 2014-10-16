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
* REVISION     :  $Revision: 1.7.24.1 $
* DATE         :  $Date: 2008/11/13 17:23:43 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

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

            std::ostringstream outLog;
            outLog.fill('0');

            outLog << endl << hex << setw(2) << (INT)_slaveToMaster[0] << dec <<" IDLC Frame header is valid ";
            outLog << endl << hex << setw(2) << (INT)_slaveToMaster[1] << dec <<" Reply from CCU Address "<< (INT)address;
            outLog << endl << hex << setw(2) << (INT)_slaveToMaster[2] << dec <<"   Control Word (rrrfsss0): ";
            outLog << endl << hex << setw(2) << (INT)_slaveToMaster[2] << dec <<"      Expected Sequence of Next Request (rrr) "<< (INT)rrr;
            outLog << endl << hex << setw(2) << (INT)_slaveToMaster[2] << dec <<"      Sequence of Current Frame (sss) "<< (INT)sss;
            outLog << endl << hex << setw(2) << (INT)_slaveToMaster[2] << dec <<"      This "<< (final ? "IS" : "IS NOT") <<" the final frame (f)";
            outLog << endl << hex << setw(2) << (INT)_slaveToMaster[3] << dec <<"   "<< (INT)length <<" bytes follow in this message ";

            outLog << endl << hex << setw(2) << (INT)_slaveToMaster[4] << dec <<"   SRC/DES Word (mmdddddd): ";
            outLog << endl << hex << setw(2) << (INT)_slaveToMaster[4] << dec <<"      Master Station Subprocess (mm) "<< (INT)src;
            outLog << endl << hex << setw(2) << (INT)_slaveToMaster[4] << dec <<"      CCU Subprocess (dddddd) "<< (INT)dest <<": ";

            switch(dest)
            {
            case 0:
                {
                    outLog <<"BASE STORES / IDLC CONTROL";
                    break;
                }
            case 1 :
                {
                    outLog <<"QUEUING CONTROL";
                    break;
                }
            case 2:
                {
                    outLog <<"DLC CONTROL";
                    break;
                }
            case 4:
                {
                    outLog <<"TIME SYNC";
                    break;
                }
            case 5:
                {
                    outLog <<"LOAD MANAGEMENT CONTROL";
                    break;
                }
            }

            outLog << endl << hex << setw(2) << (INT)_slaveToMaster[5] << dec <<"   CMND (Command) (1ggggttt): "<< (INT)cmnd;

            switch(cmnd)
            {
            case CMND_ACTIN:
                {
                    outLog <<"      ACTIN CMND Vol. 1 / pp. 4-41 "<< (INT)cmnd;
                    break;
                }
            case CMND_WMEMY:
                {
                    outLog <<"      WMEMY CMND Vol. 1 / pp. 4-54 "<< (INT)cmnd;
                    break;
                }
            case CMND_RMEMY:
                {
                    outLog <<"      RMEMY CMND Vol. 1 / pp. 4-55 "<< (INT)cmnd;
                    break;
                }
            case CMND_RCONT:
                {
                    outLog <<"      RCONT CMND Vol. 1 / pp. 4-57 "<< (INT)cmnd;
                    break;
                }
            case CMND_WMEMS:
                {
                    outLog <<"      WSETS CMND Vol. 1 / pp. 4-59 "<< (INT)cmnd;
                    break;
                }
            case CMND_RMEMS:
                {
                    outLog <<"      RSETS CMND Vol. 1 / pp. 4-61 "<< (INT)cmnd;
                    break;
                }
            case CMND_DTRAN:
                {
                    outLog <<"      DTRAN CMND Vol. 1 / pp. 4-83 "<< (INT)cmnd;
                    break;
                }
            case CMND_XTIME:
                {
                    outLog <<"      XTIME CMND Vol. 1 / pp. 4-91 "<< (INT)cmnd;
                    break;
                }
            case CMND_ITIME:
                {
                    outLog <<"      ITIME CMND Vol. 1 / pp. 4-65 "<< (INT)cmnd;
                    break;
                }
            case CMND_CQENS:
                {
                    outLog <<"      CQENS CMND Vol. 1 / pp. 4-68 "<< (INT)cmnd;
                    break;
                }
            case CMND_RQENS:
                {
                    outLog <<"      RQENS CMND Vol. 1 / pp. 4-69 "<< (INT)cmnd;
                    break;
                }
            case CMND_RQDIR:
                {
                    outLog <<"      RQDIR CMND Vol. 1 / pp. 4-81 "<< (INT)cmnd;
                    break;
                }
            case CMND_RCOLQ:
                {
                    outLog <<"      RCOLQ CMND Vol. 1 / pp. 4-86 "<< (INT)cmnd;
                    break;
                }
            case CMND_LGRPQ:
                {
                    outLog <<"      LGRPQ CMND Vol. 1 / pp. 4-92 "<< (INT)cmnd;
                    break;
                }
            default:
                {
                    outLog <<"      Unknown "<< (INT)cmnd;
                    break;
                }
            }

            outLog << endl << hex << setw(2) << (INT)_slaveToMaster[6]  << dec <<" through "<< hex << setw(2) << (INT)_slaveToMaster[9]  << dec <<"  STATS ";
            describeSlaveStatS(&_slaveToMaster[6], outLog);

            outLog << endl << hex << setw(2) << (INT)_slaveToMaster[10] << dec <<" through "<< hex << setw(2) << (INT)_slaveToMaster[15] << dec <<"  STATD ";
            describeSlaveStatD(&_slaveToMaster[10], outLog);

            outLog << endl << hex << setw(2) << (INT)_slaveToMaster[16] << dec <<" through "<< hex << setw(2) << (INT)_slaveToMaster[17] << dec <<"  STATP ";
            describeSlaveStatP(&_slaveToMaster[16], outLog);

            switch(cmnd)
            {
            case CMND_RCOLQ:
                {
                    describeRCOLQResponse(data, data_len, outLog);
                    break;
                }
            case CMND_LGRPQ:
            case CMND_XTIME:
                {
                    break;         // Minimal content header has been qualified.
                }
            case CMND_ACTIN:
                {
                    describeACTNResponse(outLog);
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
                    outLog << endl <<" ACH: Define rest of protocol";
                    break;
                }
            }

            CTILOG_INFO(dout, outLog);
        }
    }
    else
    {
        CTILOG_ERROR(dout, "IDLC Error");
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

            std::ostringstream outLog;
            outLog.fill('0');

            outLog << endl << hex << setw(2) << (INT)_masterToSlave[0] << dec <<" IDLC Frame header is valid ";
            outLog << endl << hex << setw(2) << (INT)_masterToSlave[1] << dec <<" Request to CCU Address "<< (INT)address;
            outLog << endl << hex << setw(2) << (INT)_masterToSlave[2] << dec <<"   Control Word (rrrfsss0):";
            outLog << endl << hex << setw(2) << (INT)_masterToSlave[2] << dec <<"      Expected Sequence of Next Request (rrr) "<< (INT)rrr;
            outLog << endl << hex << setw(2) << (INT)_masterToSlave[2] << dec <<"      Sequence of Current Frame (sss) "<< (INT)sss;
            outLog << endl << hex << setw(2) << (INT)_masterToSlave[2] << dec <<"      This "<< (final ? "IS" : "IS NOT") <<" the final frame (f)";
            outLog << endl << hex << setw(2) << (INT)_masterToSlave[3] << dec <<"   "<< (INT)length <<" bytes follow in this message ";

            outLog << endl << hex << setw(2) << (INT)_masterToSlave[4] << dec <<"   SRC/DES Word (mmdddddd):";
            outLog << endl << hex << setw(2) << (INT)_masterToSlave[4] << dec <<"      Master Station Subprocess (mm) "<< (INT)src;
            outLog << endl << hex << setw(2) << (INT)_masterToSlave[4] << dec <<"      CCU Subprocess (dddddd) "<< (INT)dest <<": ";

            switch(dest)
            {
            case 0:
                {
                    outLog <<"BASE STORES / IDLC CONTROL";
                    break;
                }
            case 1 :
                {
                    outLog <<"QUEUING CONTROL";
                    break;
                }
            case 2:
                {
                    outLog <<"DLC CONTROL";
                    break;
                }
            case 4:
                {
                    outLog <<"TIME SYNC";
                    break;
                }
            case 5:
                {
                    outLog <<"LOAD MANAGEMENT CONTROL";
                    break;
                }
            }

            outLog << endl << hex << setw(2) << (INT)_masterToSlave[5] << dec <<"   CMND (Command) (0ggggttt): "<< (INT)cmnd;

            switch(cmnd)
            {
            case CMND_ACTIN:
                {
                    outLog <<"      ACTIN CMND Vol. 1 / pp. 4-41 "<< (INT)cmnd;
                    break;
                }
            case CMND_WMEMY:
                {
                    outLog <<"      WMEMY CMND Vol. 1 / pp. 4-54 "<< (INT)cmnd;
                    break;
                }
            case CMND_RMEMY:
                {
                    outLog <<"      RMEMY CMND Vol. 1 / pp. 4-55 "<< (INT)cmnd;
                    break;
                }
            case CMND_RCONT:
                {
                    outLog <<"      RCONT CMND Vol. 1 / pp. 4-57 "<< (INT)cmnd;
                    break;
                }
            case CMND_WMEMS:
                {
                    outLog <<"      WSETS CMND Vol. 1 / pp. 4-59 "<< (INT)cmnd;
                    break;
                }
            case CMND_RMEMS:
                {
                    outLog <<"      RSETS CMND Vol. 1 / pp. 4-61 "<< (INT)cmnd;
                    break;
                }
            case CMND_DTRAN:
                {
                    outLog <<"      DTRAN CMND Vol. 1 / pp. 4-83 "<< (INT)cmnd;
                    break;
                }
            case CMND_XTIME:
                {
                    outLog <<"      XTIME CMND Vol. 1 / pp. 4-91 "<< (INT)cmnd;
                    break;
                }
            case CMND_ITIME:
                {
                    outLog <<"      ITIME CMND Vol. 1 / pp. 4-65 "<< (INT)cmnd;
                    break;
                }
            case CMND_CQENS:
                {
                    outLog <<"      CQENS CMND Vol. 1 / pp. 4-68 "<< (INT)cmnd;
                    break;
                }
            case CMND_RQENS:
                {
                    outLog <<"      RQENS CMND Vol. 1 / pp. 4-69 "<< (INT)cmnd;
                    break;
                }
            case CMND_RQDIR:
                {
                    outLog <<"      RQDIR CMND Vol. 1 / pp. 4-81 "<< (INT)cmnd;
                    break;
                }
            case CMND_RCOLQ:
                {
                    outLog <<"      RCOLQ CMND Vol. 1 / pp. 4-86 "<< (INT)cmnd;
                    break;
                }
            case CMND_LGRPQ:
                {
                    outLog <<"      LGRPQ CMND Vol. 1 / pp. 4-92 "<< (INT)cmnd;
                    break;
                }
            default:
                {
                    outLog <<"      Unknown "<< (INT)cmnd;
                    break;
                }
            }

            if(data_len > 0)
            {
                outLog << endl <<"     CMND data. Bytes 7 through "<< 7 + data_len;
                outLog << endl <<"     "<< hex;

                for(i = 0; i < data_len; i++)
                {
                    outLog <<" "<< setw(2) << (INT)data[i] ;
                }

                outLog << dec;
            }

            switch(cmnd)
            {
            case CMND_RCOLQ:
                {
                    describeRCOLQRequest(data, data_len, outLog);
                    break;
                }
            case CMND_LGRPQ:
                {
                    describeLGRPQRequest(data, data_len, outLog);
                    break;
                }
            case CMND_XTIME:
                {
                    describeXTIMERequest(data, data_len, outLog);
                    break;
                }
            case CMND_ACTIN:
                {
                    describeACTNRequest(data, data_len, outLog);
                    break;
                }
            case CMND_RCONT:
                {
                    outLog << endl;
                    outLog << endl <<"RCONT CMND: Vol. 1 / pp. 4-57 ";
                    outLog << endl <<"  Continue Extended Read from CCU";
                    break;
                }
            case CMND_DTRAN:
                {
                    describeDTRANRequest(data, data_len, outLog);
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
                    outLog << endl <<" **** ACH: Command type not yet decoded **** "<< __FILE__ <<" ("<< __LINE__ <<")";
                    break;
                }
            }

            CTILOG_INFO(dout, outLog);
        }
    }
    else
    {
        CTILOG_ERROR(dout, "IDLC Error");
    }
}


void CtiProtocol711::describeRCOLQRequest(const BYTE *data, INT len, std::ostringstream &outLog) const
{
    outLog << endl;
    outLog << endl <<"RCOLQ CMND: Vol. 1 / pp. 4-86 ";
    outLog << endl << hex << setw(2) << (INT)data[0] << dec <<"  RLEN (reply length RLEN + 14 returned from CCU "<< (INT)data[0];
}

void CtiProtocol711::describeRCOLQResponse(const BYTE *data, INT len, std::ostringstream &outLog) const
{
    INT byte_offset;
    INT setl = data[0];
    INT ensta = data[5] >> 4;

    INT nfunc = (INT)data[10];

    BYTE ts[2];
    INT L[3] = {0, 0, 0};

    char oldfill = outLog.fill('0');

    outLog << endl;
    outLog << endl <<"RCOLQ CMND: Vol. 1 / pp. 4-86 ";
    outLog << endl << hex << setw(2) << (INT)data[0] << dec <<"  SETL (set length) "<< setl;

    if(setl > 0)
    {
        outLog << endl << hex << setw(2) << (INT)data[1] << dec <<" through "<< hex << setw(2) << (INT)data[4] << dec <<"  QENID ";
        outLog << endl << hex << setw(2) << (INT)data[1] << dec <<" & "<< hex << setw(2) << (INT)data[2] <<"  Porter (internal TrxInfo queue entry) "<< MAKEUSHORT(data[2], data[1]);
        outLog << endl << hex << setw(2) << (INT)data[3] << dec <<" & "<< hex << setw(2) << (INT)data[4] <<"  OUTMESS->Sequence "<< MAKEUSHORT(data[4], data[3]);
        outLog << endl << hex << setw(2) << (INT)data[5] << dec <<" ENSTA: ";

        switch(ensta)
        {
        case 12:
            {
                outLog <<"  REQUEST REJECT: - UNDEF. (Completed)  ";
                break;
            }
        case 13:
            {
                outLog <<"  REQUEST REJECT: - UNDEF. (Completed)  ";
                break;
            }
        case 14:
            {
                outLog <<"  REQUEST REJECT: - FUNCTION ERROR. (Completed)  ";
                break;
            }
        case 15:
            {
                outLog <<"  ACTION COMPLETE:  ";
                break;
            }
        default:
            {
                outLog <<"  UNDEFINED COMPLETION STATE ";
                break;
            }
        }

        outLog << endl << hex << setw(2) << (INT)data[6] <<" through "<< setw(2) << (INT)data[8] << dec <<"  STIME 1st (8 hr period of week) 2-3 (second into period)";

        if(data[9] == 255)
        {
            outLog << endl << hex << setw(2) << (INT)data[9] << dec <<"  ROUTE defined by request ";
        }
        else
        {
            outLog << endl << hex << setw(2) << (INT)data[9] << dec <<"  ROUTE ";
        }

        outLog << endl << hex << setw(2) << nfunc << dec <<"  NFUNC - number of functions in this request  ";
        outLog.fill(oldfill);
    }
}

void CtiProtocol711::describeACTNRequest(const BYTE *data, INT len, std::ostringstream &outLog) const
{
    INT i;

    char oldfill = outLog.fill('0');

    outLog << endl;
    outLog << endl <<"ACTIN CMND: Vol. 1 / pp. 4-41 ";

    for(i = 0; i < len; i++)
    {
        outLog << endl << hex << setw(2) << (INT)data[i] << dec <<"  ACTN:  "<< (INT)data[i];

        switch(data[i])
        {
        case 0:
            {
                outLog <<"  No-Op  "<< (INT)data[i];
                break;
            }
        case 1:
            {
                outLog <<"  CLRAL - Init. process control stores  "<< (INT)data[i];
                break;
            }
        case 2:
            {
                outLog <<"  CLRDY - Init. process dynamic control stores  "<< (INT)data[i];
                break;
            }
        case 3:
            {
                outLog <<"  START - Start process.  "<< (INT)data[i];
                break;
            }
        case 4:
            {
                outLog <<"  HOPRO - Halt orderly, process  "<< (INT)data[i];
                break;
            }
        case 5:
            {
                outLog <<"  ENPRO - Enable execution of process  "<< (INT)data[i];
                break;
            }
        case 6:
            {
                outLog <<"  DSPRO - Disable execution of process  "<< (INT)data[i];
                break;
            }
        case 7:
            {
                outLog <<"  CJOUR - Clear journal  "<< (INT)data[i];
                break;
            }
        default:
            {
                outLog <<"  ACTIN code not yet defined  "<< (INT)data[i];
                break;
            }
        }

        outLog.fill(oldfill);
    }
}

void CtiProtocol711::describeACTNResponse(std::ostringstream &outLog) const
{
}

void CtiProtocol711::describeDTRANRequest(const BYTE *data, INT len, std::ostringstream &outlog) const
{
    INT i;

    {
        INT rlen = data[0];
        INT loa = data[1] & 0x03;
        INT feeder = ((data[1] & 0x31) >> 3) + 1;
        INT amp = ((data[2] & 0x40) ? 2 : 1);
        INT rtf = data[2] & 0x07;
        INT ctf = data[3] & 0x3f;

        char oldfill = outlog.fill('0');

        outlog << endl;
        outlog << endl <<"DTRAN CMND: Vol. 1 / pp. 4-83 ";
        outlog << endl <<"  RLEN  "<< rlen <<" reply length expected ";
        outlog << endl <<"  PREAMBLE: ";
        outlog << endl <<"    Feeder            "<< feeder;
        outlog << endl <<"    Amp               "<< amp;
        outlog << endl <<"    Stages to Follow  "<< rtf;
        outlog << endl <<"    Number of chars   "<< ctf;
        outlog << endl <<"    " ;

        for(i = 4; i < ctf + 4; i++)
        {
            outlog << hex << setw(2) << (INT)data[i] <<" ";
        }

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

                outlog << endl <<"  BWORD: ";
                outlog << endl <<"    Variable bits    "<< hex << setw(2) << (INT)var;
                outlog << endl <<"    Fixed    bits    "<< hex << setw(2) << (INT)fix;
                outlog << endl <<"    Address          "<< dec << address;
                outlog << endl <<"    Words to follow  "<< words;
                outlog << endl <<"    Function/Address "<< hex << setw(2) << func;
                outlog << endl <<"    IO               "<< hex << setw(2) << io;

                break;
            }
        case 8:  // a-word
        default:
            {
                break;
            }
        }

        outlog.fill(oldfill);
    }
}

void CtiProtocol711::describeLGRPQRequest(const BYTE *data, INT len, std::ostringstream &outlog) const
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
    BYTE     nfunc = 0;

    memcpy(qenid, &data[1], 4);

    if(shortformat)
    {
        outlog << endl <<" **** ACH: short format does not compute **** "<< __FILE__ <<" ("<< __LINE__ <<")";
    }
    else  // long format LGRPQ
    {
        ULONG dlcaddr = MAKEULONG( MAKEUSHORT(data[8], data[7]), (USHORT)data[6] );

        if(data[9] & 0x80)
        {
            outlog << endl <<" **** ACH: does not compute **** "<< __FILE__ <<" ("<< __LINE__ <<")";
        }
        else
        {
            feeder = (data[9] & 0x07) + 1;
            vvv = data[10] >> 5;
            fffff = data[10] & 0x1f;
            RRR = data[11] & 0x07;
            nfunc = data[12];

            char oldfill = outlog.fill('0');
            outlog << endl;
            outlog << endl <<"LGRPQ CMND: Vol. 1 / pp. 4-92 ";
            outlog << endl << hex << setw(2) << (INT)data[0]  << dec <<"  SETL (set length) "<< (INT)setl;
            outlog << endl << hex << setw(2) << (INT)data[1]  << dec << (shortformat ? "  Short Format " : "  Long Format");
            outlog << endl << hex << setw(2) << (INT)data[1]  << dec <<" through "<< hex << setw(2) << (INT)data[4] << dec <<"  QENID ";
            outlog << endl << hex << setw(2) << (INT)data[1]  << dec <<" and "<< hex << setw(2) << (INT)data[2] <<"  Porter (internal TrxInfo queue entry) "<< MAKEUSHORT(data[2], data[1]);
            outlog << endl << hex << setw(2) << (INT)data[3]  << dec <<" and "<< hex << setw(2) << (INT)data[4] <<"  OUTMESS->Sequence "<< MAKEUSHORT(data[4], data[3]);
            outlog << endl << hex << setw(2) << (INT)data[5]  << dec <<"  Priority (0 - 15 max) "<< (INT)priority;
            outlog << endl << hex << setw(2) << (INT)data[6]  << dec <<" through "<< hex << (INT)data[8] << dec <<"  DLC remote address "<< (INT)dlcaddr;
            outlog << endl << hex << setw(2) << (INT)data[9]  << dec <<"  Feeder "<< (INT)feeder;
            outlog << endl << hex << setw(2) << (INT)data[10] << dec <<"  Variable Bits "<< (INT)vvv;
            outlog << endl << hex << setw(2) << (INT)data[10] << dec <<"  Fixed Bits "<< (INT)fffff;
            outlog << endl << hex << setw(2) << (INT)data[11] << dec <<"  Repeaters to follow "<< (INT)RRR;
            outlog << endl << hex << setw(2) << (INT)data[12] << dec <<"  NFUNC: number of functions to follow "<< (INT)nfunc;
            outlog.fill(oldfill);
        }

        if(0 < nfunc && nfunc <= 3 )
        {
            BYTE dlcword;
            BYTE dblrpt;
            BYTE io;
            BYTE dlclen;
            BYTE bwordcmd;

            INT nfunc_offset = 13;

            for(i = 0; i < nfunc; i++)
            {
                dlcword = data[nfunc_offset] >> 6;
                dblrpt = (data[nfunc_offset] & 0x20) >> 5;
                bwordcmd = data[nfunc_offset+2];
                io =  (data[nfunc_offset] & 0x18) >> 3;
                dlclen = data[nfunc_offset + 3];

                char oldfill = outlog.fill('0');
                outlog << endl << hex << setw(2) << (INT)data[nfunc_offset] <<" through "<< setw(2) << (INT)data[nfunc_offset + 4] << dec <<"  DLC COMMAND: ";

                switch(dlcword)
                {
                case 0x00:
                    {
                        outlog << endl <<" **** ACH: UNUSED/ERROR! **** "<< __FILE__ <<" ("<< __LINE__ <<")";
                        break;
                    }
                case 0x02:
                    {
                        outlog << endl << hex << setw(2) << (INT)data[nfunc_offset] << dec <<" bits 7-6 indicate a BWORD COMMAND ";
                        break;
                    }
                case 0x03:
                    {
                        outlog << endl <<" **** ACH: Word type GWORD **** "<< __FILE__ <<" ("<< __LINE__ <<")";
                    }
                case 0x01:
                    {
                        outlog << endl <<" **** ACH: Word type AWORD **** "<< __FILE__ <<" ("<< __LINE__ <<")";
                    }
                    // FIXME break missing here?
                default:
                    {
                        outlog << endl <<" **** ACH: Word type not yet decoded! **** "<< __FILE__ <<" ("<< __LINE__ <<")";
                        break;
                    }
                }

                outlog << endl << hex << setw(2) << (INT)data[nfunc_offset] << dec <<" bit 5 indicates command "<< (dblrpt ? "WILL" : "WILL NOT") <<" be double'd";
                outlog << endl << hex << setw(2) << (INT)data[nfunc_offset] << dec <<" bits 4-3 indicate io type ";

                switch(io)
                {
                case 0:
                    {
                        outlog <<"WRITE";
                        break;
                    }
                case 1:
                    {
                        outlog <<"READ";
                        break;
                    }
                case 2:
                    {
                        outlog <<"FUNCTION WRITE";
                        break;
                    }
                case 3:
                    {
                        outlog <<"FUNCTION READ";
                        break;
                    }
                }

                switch(dlcword)
                {
                case 0x00:
                    {
                        outlog << endl <<" **** ACH: UNUSED/ERROR! **** "<< __FILE__ <<" ("<< __LINE__ <<")";
                        break;
                    }
                case 0x02:
                    {
                        outlog << endl << hex << setw(2) << (INT)data[nfunc_offset+2] <<" BWORD FUNCTION "<< (INT)bwordcmd << dec;
                        outlog << endl << hex << setw(2) << (INT)data[nfunc_offset+3] << dec <<" BWORD LENGTH "<< (INT)dlclen;
                        break;
                    }
                case 0x03:
                    {
                        outlog << endl <<" **** ACH: Word type GWORD **** "<< __FILE__ <<" ("<< __LINE__ <<")";
                    }
                case 0x01:
                    {
                        outlog << endl <<" **** ACH: Word type AWORD **** "<< __FILE__ <<" ("<< __LINE__ <<")";
                    }
                default:
                    {
                        outlog << endl <<" **** ACH: Word type not yet decoded! **** "<< __FILE__ <<" ("<< __LINE__ <<")";
                        break;
                    }
                }

                outlog.fill(oldfill);

                nfunc_offset += 4;
            }

            if(len > nfunc_offset + 1)
            {
                outlog << endl << hex << setw(2) << (INT)data[nfunc_offset] << dec <<" to end.  Data to be written to the DLC device";
            }
        }
        else
        {
            outlog << endl <<" **** ACH: Unknown CTL function Vol. 1 / pp. 4-96 **** "<< __FILE__ <<" ("<< __LINE__ <<")";
        }
    }
}

void CtiProtocol711::describeXTIMERequest(const BYTE *data, INT len, std::ostringstream &outLog) const
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

    outLog << endl;
    outLog << endl <<"XTIME CMND: Vol. 1 / pp. 4-92 ";
    outLog << endl << hex << setw(2) << (INT)data[0] <<" & "<< setw(2) << (INT)data[1] << dec <<"  YEAR (year) "<< (INT)year;
    outLog << endl << hex << setw(2) << (INT)data[2] <<" & "<< setw(2) << (INT)data[3] << dec <<"  JULN (julian day-of-year number) "<< (INT)juln;
    outLog << endl << hex << setw(2) << (INT)data[4] << dec <<"  DOW (day-of-week number. 1 = Sun.): "<< (INT)dow;
    outLog << endl << hex << setw(2) << (INT)data[5] << dec <<"  PERD (period of day). Seconds since midnight: "<< period;
    outLog << endl << hex << setw(2) << (INT)data[2] <<" & "<< setw(2) << (INT)data[3] << dec <<"  SEC (seconds into period) "<< (INT)sec;
    outLog << endl << dec <<"        Time set to "<< setw(2) << hour <<":"<< setw(2) << minute <<":"<< setw(2) << second;
}

// You MUST own the dout mutex to call this function
void CtiProtocol711::describeSlaveStatS(const BYTE *stat, std::ostringstream &outLog) const
{
    outLog << endl <<"  STATS:";
    outLog << endl <<"    AC Power                  "<< (stat[0] & 0x01 ? "outage detected": "good / outage not detected");
    outLog << endl <<"    Slave fault               "<< (stat[0] & 0x02 ? "detected": "not detected");
    outLog << endl <<"    Deadman circuit           "<< (stat[0] & 0x04 ? "activated": "inactive");
    outLog << endl <<"    Cold start                "<< (stat[0] & 0x08 ? "detected": "not detected");
    outLog << endl <<"    Sequence adjust           "<< (stat[0] & 0x10 ? "detected": "not detected");
    outLog << endl <<"    Alg. fault                "<< (stat[0] & 0x20 ? "detected": "not detected");
    outLog << endl <<"    Sequence adjust           "<< (stat[0] & 0x10 ? "detected": "not detected");
}

// You MUST own the dout mutex to call this function
void CtiProtocol711::describeSlaveStatD(const BYTE *stat, std::ostringstream &outLog) const
{
    outLog << endl <<"  STATD Not yet decoded";
}

// You MUST own the dout mutex to call this function
void CtiProtocol711::describeSlaveStatP(const BYTE *stat, std::ostringstream &outLog) const
{
    outLog << endl <<"  STATP Not yet decoded";
}
