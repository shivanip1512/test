
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
    Cti::FormattedList itemList;

    itemList.add("Instant Voltage 1") << _instantVoltage1 <<" (units of 1/8 volt)";
    itemList.add("Instant Voltage 2") << _instantVoltage2 <<" (units of 1/8 volt)";
    itemList.add("Instant Power")     << _instantPower    <<" (units of Watt)";

    CTILOG_INFO(dout,
            endl << formatTableName("Focus MFG Table 4") <<
            itemList
            );
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




