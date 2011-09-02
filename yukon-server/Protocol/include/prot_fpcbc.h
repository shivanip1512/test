#pragma once

#include "cmdparse.h"
#include "dsm2.h"
#include "dllbase.h"
#include "dlldefs.h"

#include <list>
#include "utility.h"


class IM_EX_PROT CtiProtocolFisherPierceCBC
{
protected:

   INT      _last;

   std::list< FPSTRUCT* >  _fst;


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
          delete_container(_fst);
          _fst.clear();

          for( int i = 0; i < aRef.entries(); i++ )
          {
             FPSTRUCT *Fst = CTIDBG_new FPSTRUCT;
             ::memcpy((void*)Fst, &aRef.getFPStruct(i), sizeof(FPSTRUCT));

             _fst.push_back( Fst );
          }
       }
       return *this;
    }

    INT   entries() const
    {
       return _fst.size();
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
