


#include "precompiled.h"
#include "ansi_focus_mtable_024.h"
#include "logger.h"

using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiFocusMfgTable24::CtiAnsiFocusMfgTable24( BYTE *dataBlob, DataOrder dataOrder )
{
    for (int i = 0; i < 8; ++i)
    {
        dataBlob += toAnsiIntParser( dataBlob, &_vh_ih_fh_const.ch[i], sizeof(char),  dataOrder );
    }

    dataBlob += toAnsiIntParser( dataBlob, &_nbr_of_relays, sizeof(UINT8),  dataOrder );
    dataBlob += toAnsiIntParser( dataBlob, &_x_dot_y_format.xBits, sizeof(UINT8),  dataOrder );
    dataBlob += toAnsiIntParser( dataBlob, &_x_dot_y_format.yBits, sizeof(UINT8),  dataOrder );
    dataBlob += toAnsiIntParser( dataBlob, &_dsp_sample_period.numerator, sizeof(UINT16),  dataOrder );
    dataBlob += toAnsiIntParser( dataBlob, &_dsp_sample_period.denominator, sizeof(UINT16),  dataOrder );
       
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiFocusMfgTable24::~CtiAnsiFocusMfgTable24()
{
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiFocusMfgTable24::printResult(  )
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
        dout << endl << "=======================  Focus MFG Table 24  ========================" << endl;
        dout << " VH IH FH Constant : " << _vh_ih_fh_const.asString()  << endl;
        dout << " Number of Relays  : " << (int) _nbr_of_relays  << endl;
        dout << " X.Y Format        : Q" << (int) _x_dot_y_format.xBits << "." << (int) _x_dot_y_format.yBits << endl;
        dout << " DSP Sample Period : " <<  _dsp_sample_period.numerator <<" / " << _dsp_sample_period.denominator;
        dout << " = " << getDSPSamplePeriod() << endl;
    }
}

float CtiAnsiFocusMfgTable24::getDSPSamplePeriod()
{
    return (float)_dsp_sample_period.numerator / (float)_dsp_sample_period.denominator;
}

float CtiAnsiFocusMfgTable24::getVhIhFhConstantsRcd()
{
    return _vh_ih_fh_const.asValue();
}
