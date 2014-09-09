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
* REVISION     :  $Revision: 1.11.4.1 $
* DATE         :  $Date: 2008/11/13 17:23:43 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include <iomanip>
#include <iostream>



#include "cmdparse.h"
#include "dllbase.h"
#include "devicetypes.h"
#include "logger.h"
#include "master.h"
#include "msg_pcrequest.h"
#include "prot_fpcbc.h"
#include "utility.h"

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

INT CtiProtocolFisherPierceCBC::parseRequest(CtiCommandParser  &parse, const FPSTRUCT &aFPSt)
{
    INT status = NoError;

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
                dout << CtiTime() << " Unsupported command on fisher pierce route Command = " << parse.getCommand() << endl;
            }

            status = ErrorInvalidRequest;

            break;
        }
    }

    return status;
}

void CtiProtocolFisherPierceCBC::advanceAndPrime(const FPSTRUCT &fTemp)
{
    FPSTRUCT *newfpst = CTIDBG_new FPSTRUCT;
    *newfpst = fTemp;
    _fst.push_back(newfpst);
    _last = _fst.size() - 1;

    return;
}

INT CtiProtocolFisherPierceCBC::assemblePutConfig(CtiCommandParser  &parse, const FPSTRUCT &aFPSt)
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return NoMethod;
}

INT CtiProtocolFisherPierceCBC::assemblePutStatus(CtiCommandParser  &parse, const FPSTRUCT &aFPSt)
{
    INT   i, iNum;
    INT   status = NoError;
    BOOL  firstOneDone = FALSE;
    BYTE  config[6];


    primeFPStruct(aFPSt);  // Get a new one in the system

    /*
     * This should be the original with only the addressing copied into it,
     * we will use this for all others...
     */
    FPSTRUCT     FPStTemplate = getFPStruct(_last);

    if((iNum = parse.getiValue("ovuv")) != INT_MIN)
    {
        if(firstOneDone)
        {
            advanceAndPrime(FPStTemplate);    // Get a new one in the list that looks like the original in terms of addressing
        }

        /*
         *  This is a ENABLE opreration if iNum != 0.
         */
        FPSTRUCT* last_ptr = _fst.back();
        if(iNum == 0)  // Disable
        {
            last_ptr->u.PCC.F[0] = '0';
            last_ptr->u.PCC.F[1] = '7';
        }
        else if(iNum == 1)
        {
            last_ptr->u.PCC.F[0] = '0';
            last_ptr->u.PCC.F[1] = '6';
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        firstOneDone = TRUE;
    }

    if(!firstOneDone)
    {
        // Oh my, this one failed.... we should get rid of the 'prime' FPSTRUCT since it was never
        // modified
        delete_container(_fst);
        _fst.clear();
    }

    return status;
}

INT CtiProtocolFisherPierceCBC::assembleControl(CtiCommandParser  &parse, const FPSTRUCT &aFPSt)
{
    INT   status = NoError;

    UINT  CtlReq = CMD_FLAG_CTL_ALIASMASK & parse.getFlags();


    if(CtlReq == CMD_FLAG_CTL_OPEN)
    {
        primeFPStruct(aFPSt);  // Get a new one in the system
        capacitorControlCommand(TRUE);
    }
    else if(CtlReq == CMD_FLAG_CTL_CLOSE)
    {
        primeFPStruct(aFPSt);  // Get a new one in the system
        capacitorControlCommand(FALSE);
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " Incomplete code... for " << parse.getCommand() << " " << __FILE__ << " " << __LINE__<< endl;
    }

    return status;
}

INT CtiProtocolFisherPierceCBC::primeFPStruct(const FPSTRUCT &FPstTemplate)
{
    INT      status = NoError;

    FPSTRUCT  *fpst = CTIDBG_new FPSTRUCT;

    if(fpst != NULL)
    {
        ::memcpy((void*)fpst, &FPstTemplate, sizeof(FPSTRUCT));

        _fst.push_back( fpst );
        _last = _fst.size() - 1;      // Which one are we working on?
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
    FPSTRUCT* last_ptr = _fst.back();
    if(Trip)      // Then trip/open it
    {
        last_ptr->u.PCC.F[0] = '0';
        last_ptr->u.PCC.F[1] = '2';
    }
    else      // Then close it
    {
        last_ptr->u.PCC.F[0] = '0';
        last_ptr->u.PCC.F[1] = '3';
    }

    return NoError;
}

FPSTRUCT CtiProtocolFisherPierceCBC::getFPStruct(INT pos) const
{   int x = 0;
    std::list<FPSTRUCT*>::const_iterator itr = _fst.begin();
    while( x != pos ){
       ++itr;++x;
    }
    return **itr;
}
FPSTRUCT& CtiProtocolFisherPierceCBC::getFPStruct(INT pos)
{
    int x = 0;
    std::list<FPSTRUCT*>::iterator itr = _fst.begin();
    while( x != pos ){
       ++itr;++x;
    }
    return **itr;
}
