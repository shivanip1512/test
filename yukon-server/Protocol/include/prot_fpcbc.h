#pragma once

#include "cmdparse.h"
#include "dsm2.h"
#include "dllbase.h"
#include "dlldefs.h"

#include <list>
#include "utility.h"


class IM_EX_PROT CtiProtocolFisherPierceCBC : private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiProtocolFisherPierceCBC(const CtiProtocolFisherPierceCBC&);
    CtiProtocolFisherPierceCBC& operator=(const CtiProtocolFisherPierceCBC&);

protected:

   INT      _last;

   std::list< FPSTRUCT* >  _fst;

public:

    CtiProtocolFisherPierceCBC() {}
    virtual ~CtiProtocolFisherPierceCBC() {}

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
