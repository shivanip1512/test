
#include "precompiled.h"
#include "ansi_focus_mtable_004.h"
#include "logger.h"

using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiFocusMfgTable04::CtiAnsiFocusMfgTable04( BYTE *dataBlob, DataOrder dataOrder )
{
    dataBlob += toAnsiIntParser( dataBlob, &_instantVoltage1, sizeof (unsigned short), dataOrder );
    dataBlob += toAnsiIntParser( dataBlob, &_instantVoltage2, sizeof (unsigned short), dataOrder );
    dataBlob += toAnsiIntParser( dataBlob, &_instantPower, sizeof (UINT32), dataOrder );
  
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiFocusMfgTable04::~CtiAnsiFocusMfgTable04()
{
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiFocusMfgTable04::printResult(  )
{

    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "=======================  Focus MFG Table 4  ========================" << endl;
        dout << endl << "Instant Voltage 1: " <<_instantVoltage1 << " (units of 1/8 volt) " << endl;
        dout << endl << "Instant Voltage 2: " <<_instantVoltage2 <<  " (units of 1/8 volt) " <<endl;
        dout << endl << "Instant Power    : " <<_instantPower <<  " (units of Watt) " <<endl;
    }
}

UINT16 CtiAnsiFocusMfgTable04::getInstantVoltage(int index)
{
    UINT16 retVal = _instantVoltage1;
    if (index > 0)
    {
        retVal = _instantVoltage2;
    }
    return retVal;
}
UINT16 CtiAnsiFocusMfgTable04::getInstantPower( )
{
    return _instantPower;
}




