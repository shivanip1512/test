

#include "precompiled.h"
#include "ansi_focus_mtable_013.h"
#include "logger.h"

using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiFocusMfgTable13::CtiAnsiFocusMfgTable13( BYTE *dataBlob, int fwVersion, int fwRevision, DataOrder dataOrder )
{
    _xyFormat = Q10_6;
    if (fwVersion < 5  || (fwVersion == 5 && fwRevision <= 27))
    {
        _xyFormat = Q9_7;
    }
    for (int i = A; i < Invalid; ++i)
    {
        dataBlob += toAnsiIntParser( dataBlob, &_gyrboxPhaseInfo.phaseVoltage[i].rawValue, sizeof(unsigned short),  dataOrder );
    }
    for (int i = A; i < Invalid; ++i)
    {
        dataBlob += toAnsiIntParser( dataBlob, &_gyrboxPhaseInfo.phaseVoltageAngle[i].rawValue, sizeof(unsigned short),  dataOrder );
    }
    for (int i = A; i < Invalid; ++i)
    {
        dataBlob += toAnsiIntParser( dataBlob, &_gyrboxPhaseInfo.phaseCurrent[i].rawValue, sizeof(unsigned short),  dataOrder );
    }
    for (int i = 0; i < 3; ++i)
    {
        dataBlob += toAnsiIntParser( dataBlob, &_gyrboxPhaseInfo.reserved[i].rawValue, sizeof(unsigned short),  dataOrder );
    }
    dataBlob += toAnsiIntParser( dataBlob, &_instantMeasure.instantPower, sizeof(int),  dataOrder );
    dataBlob += toAnsiIntParser( dataBlob, &_instantMeasure.neutralCurrent.rawValue, sizeof(unsigned short),  dataOrder );
    for (int i = 0; i < 7; ++i)
    {
        dataBlob += toAnsiIntParser( dataBlob, &_instantMeasure.reserved, sizeof(unsigned short),  dataOrder );
    }
    dataBlob += toAnsiIntParser( dataBlob, &_temperature, sizeof(signed char),  dataOrder );
    dataBlob += toAnsiIntParser( dataBlob, &_unused, sizeof(unsigned char),  dataOrder );
    dataBlob += toAnsiIntParser( dataBlob, &_lineFrequency.rawValue, sizeof(unsigned short),  dataOrder );

    dataBlob += toAnsiIntParser( dataBlob, &_reverseRotationPulseCnt, sizeof(unsigned char),  dataOrder );

    dataBlob += toAnsiIntParser( dataBlob, &_loadSidePhaseAVoltage.rawValue, sizeof(unsigned short),  dataOrder );
    dataBlob += toAnsiIntParser( dataBlob, &_loadSidePhaseCVoltage.rawValue, sizeof(unsigned short),  dataOrder );
   
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiFocusMfgTable13::~CtiAnsiFocusMfgTable13()
{
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiFocusMfgTable13::printResult(  )
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
        dout << endl << "=======================  Focus MFG Table 13  ========================" << endl;
    }


    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << " Phase A Voltage: " << _gyrboxPhaseInfo.phaseVoltage[A].asValue(_xyFormat)  << endl;
        dout << endl << " Phase B Voltage: " << _gyrboxPhaseInfo.phaseVoltage[B].asValue(_xyFormat)  << endl;
        dout << endl << " Phase C Voltage: " << _gyrboxPhaseInfo.phaseVoltage[C].asValue(_xyFormat)  << endl;
        dout << endl << " Phase A Voltage Angle: " << _gyrboxPhaseInfo.phaseVoltageAngle[A].asValue(_xyFormat)  << endl;
        dout << endl << " Phase B Voltage Angle: " << _gyrboxPhaseInfo.phaseVoltageAngle[B].asValue(_xyFormat)  << endl;
        dout << endl << " Phase C Voltage Angle: " << _gyrboxPhaseInfo.phaseVoltageAngle[C].asValue(_xyFormat)  << endl;
        dout << endl << " Phase A Current: " << _gyrboxPhaseInfo.phaseCurrent[A].asValue(_xyFormat)  << endl;
        dout << endl << " Phase B Current: " << _gyrboxPhaseInfo.phaseCurrent[B].asValue(_xyFormat)  << endl;
        dout << endl << " Phase C Current: " << _gyrboxPhaseInfo.phaseCurrent[C].asValue(_xyFormat)  << endl;
        dout << endl << " Instant Power: "   << _instantMeasure.instantPower << endl;
        dout << endl << " Neutral Current: "   << _instantMeasure.neutralCurrent.asValue(_xyFormat) << endl;
        dout << endl << " Temperature: "   << _temperature << endl;
        dout << endl << " Line Frequency: "   << _lineFrequency.asValue(_xyFormat) << endl;
        dout << endl << " Reverse Rotation Pulse Count: "   << _reverseRotationPulseCnt << endl;
        dout << endl << " Load Side Phase A Voltage: "   << _loadSidePhaseAVoltage.asValue(_xyFormat) << endl;
        dout << endl << " Load Side Phase C Voltage: "   << _loadSidePhaseCVoltage.asValue(_xyFormat) << endl;

    }
}

short CtiAnsiFocusMfgTable13::getPhaseVoltage(PhaseType phase)
{
    return _gyrboxPhaseInfo.phaseVoltage[phase].asValue(_xyFormat);
}

short CtiAnsiFocusMfgTable13::getPhaseCurrent(PhaseType phase)
{
    return _gyrboxPhaseInfo.phaseCurrent[phase].asValue(_xyFormat);
}
short CtiAnsiFocusMfgTable13::getNeutralCurrent(  )
{
    return _instantMeasure.neutralCurrent.asValue(_xyFormat);
}

