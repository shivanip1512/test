/*-----------------------------------------------------------------------------*
*
* File:   prot_fpcbc
*
* Date:   6/15/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/prot_fpcbc.cpp-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2005/02/10 23:23:57 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <windows.h>
#include <iomanip>
#include <iostream>
using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore


#include "cmdparse.h"
#include "dllbase.h"
#include "devicetypes.h"
#include "logger.h"
#include "master.h"
#include "msg_pcrequest.h"
#include "prot_fpcbc.h"
#include "utility.h"


INT CtiProtocolFisherPierceCBC::parseRequest(CtiCommandParser  &parse, const FPSTRUCT &aFPSt)
{
    INT status = NORMAL;

    switch(parse.getCommand())
    {
    case ControlRequest:
        {
            assembleControl(parse, aFPSt);
            break;
        }
    case PutConfigRequest:
        {
            assemblePutConfig(parse, aFPSt);
            break;
        }
    case PutStatusRequest:
        {
            assemblePutStatus(parse, aFPSt);
            break;
        }
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Unsupported command on fisher pierce route Command = " << parse.getCommand() << endl;
            }

            status = CtiInvalidRequest;

            break;
        }
    }

    return status;
}

void CtiProtocolFisherPierceCBC::advanceAndPrime(const FPSTRUCT &fTemp)
{
    FPSTRUCT *newfpst = CTIDBG_new FPSTRUCT;
    *newfpst = fTemp;
    _fst.insert(newfpst);
    _last = _fst.entries() - 1;

    return;
}

INT CtiProtocolFisherPierceCBC::assemblePutConfig(CtiCommandParser  &parse, const FPSTRUCT &aFPSt)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return NoMethod;
}

INT CtiProtocolFisherPierceCBC::assemblePutStatus(CtiCommandParser  &parse, const FPSTRUCT &aFPSt)
{
    INT   i, iNum;
    INT   status = NORMAL;
    BOOL  firstOneDone = FALSE;
    BYTE  config[6];


    primeFPStruct(aFPSt);  // Get a CTIDBG_new one in the system

    /*
     * This should be the original with only the addressing copied into it,
     * we will use this for all others...
     */
    FPSTRUCT     FPStTemplate = getFPStruct(_last);

    if((iNum = parse.getiValue("ovuv")) != INT_MIN)
    {
        if(firstOneDone)
        {
            advanceAndPrime(FPStTemplate);    // Get a CTIDBG_new one in the list that looks like the original in terms of addressing
        }

        /*
         *  This is a ENABLE opreration if iNum != 0.
         */

        if(iNum == 0)  // Disable
        {
            _fst[_last]->u.PCC.F[0] = '0';
            _fst[_last]->u.PCC.F[1] = '7';
        }
        else if(iNum == 1)
        {
            _fst[_last]->u.PCC.F[0] = '0';
            _fst[_last]->u.PCC.F[1] = '6';
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        firstOneDone = TRUE;
    }

    if(!firstOneDone)
    {
        // Oh my, this one failed.... we should get rid of the 'prime' FPSTRUCT since it was never
        // modified
        _fst.clearAndDestroy();
    }

    return status;
}

INT CtiProtocolFisherPierceCBC::assembleControl(CtiCommandParser  &parse, const FPSTRUCT &aFPSt)
{
    INT   status = NORMAL;

    UINT  CtlReq = CMD_FLAG_CTL_ALIASMASK & parse.getFlags();


    if(CtlReq == CMD_FLAG_CTL_OPEN)
    {
        primeFPStruct(aFPSt);  // Get a CTIDBG_new one in the system
        capacitorControlCommand(TRUE);
    }
    else if(CtlReq == CMD_FLAG_CTL_CLOSE)
    {
        primeFPStruct(aFPSt);  // Get a CTIDBG_new one in the system
        capacitorControlCommand(FALSE);
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Incomplete code... for " << parse.getCommand() << " " << __FILE__ << " " << __LINE__<< endl;
    }

    return status;
}

INT CtiProtocolFisherPierceCBC::primeFPStruct(const FPSTRUCT &FPstTemplate)
{
    INT      status = NORMAL;

    FPSTRUCT  *fpst = CTIDBG_new FPSTRUCT;

    if(fpst != NULL)
    {
        memcpy((void*)fpst, &FPstTemplate, sizeof(FPSTRUCT));

        _fst.insert( fpst );
        _last = _fst.entries() - 1;      // Which one are we working on?
    }
    else
    {
        status = MEMORY;
    }

    return status;
}

/*-------------------------------------------------------------------------*
* Constructs a message buffer for a capacitor control
* command.  The argument trips control if non-zero, or closes
* it if zero.
*-------------------------------------------------------------------------*/
INT CtiProtocolFisherPierceCBC::capacitorControlCommand(BOOL Trip)
{
    if(Trip)      // Then trip/open it
    {
        _fst[_last]->u.PCC.F[0] = '0';
        _fst[_last]->u.PCC.F[1] = '2';
    }
    else      // Then close it
    {
        _fst[_last]->u.PCC.F[0] = '0';
        _fst[_last]->u.PCC.F[1] = '3';
    }

    return NORMAL;
}

FPSTRUCT CtiProtocolFisherPierceCBC::getFPStruct(INT pos) const
{
    return *_fst[pos];
}
FPSTRUCT& CtiProtocolFisherPierceCBC::getFPStruct(INT pos)
{
    return *_fst[pos];
}
