
#pragma warning( disable : 4786)
#ifndef __PROT_FPCBC_H__
#define __PROT_FPCBC_H__

/*-----------------------------------------------------------------------------*
*
* File:   prot_fpcbc
*
* Class:  CtiProtocolFisherPierceCBC
* Date:   6/15/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/INCLUDE/prot_fpcbc.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/11/15 14:08:08 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "cmdparse.h"
#include "dsm2.h"
#include "dllbase.h"
#include "dlldefs.h"


class IM_EX_PROT CtiProtocolFisherPierceCBC
{
protected:

   INT      _last;

   RWTPtrSlist< FPSTRUCT >  _fst;


private:

public:
    CtiProtocolFisherPierceCBC() {}

    CtiProtocolFisherPierceCBC(const CtiProtocolFisherPierceCBC& aRef)
    {
        *this = aRef;
    }

    virtual ~CtiProtocolFisherPierceCBC() {}

    CtiProtocolFisherPierceCBC& operator=(const CtiProtocolFisherPierceCBC& aRef)
    {
       if(this != &aRef)
       {
          _fst.clearAndDestroy();

          for( int i = 0; i < aRef.entries(); i++ )
          {
             FPSTRUCT *Fst = CTIDBG_new FPSTRUCT;
             memcpy((void*)Fst, &aRef.getFPStruct(i), sizeof(FPSTRUCT));

             _fst.insert( Fst );
          }
       }
       return *this;
    }

    INT   entries() const
    {
       return _fst.entries();
    }

    FPSTRUCT                getFPStruct(INT pos = 0) const;
    FPSTRUCT&               getFPStruct(INT pos = 0);

    void advanceAndPrime(const FPSTRUCT &fTemp);
    INT primeFPStruct(const FPSTRUCT &FPstTemplate);

    INT parseRequest(CtiCommandParser &parse, const FPSTRUCT &aFPSt);

    INT assemblePutConfig(CtiCommandParser  &parse, const FPSTRUCT &aFPSt);
    INT assemblePutStatus(CtiCommandParser  &parse, const FPSTRUCT &aFPSt);
    INT assembleControl(CtiCommandParser  &parse, const FPSTRUCT &aFPSt);


    /*-------------------------------------------------------------------------*
     * Constructs a message buffer for a capacitor control
     * command.  The argument trips control if non-zero, or closes
     * it if zero.
     *-------------------------------------------------------------------------*/
    INT capacitorControlCommand(BOOL Trip);


};
#endif // #ifndef __PROT_FPCBC_H__
