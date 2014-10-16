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
    Cti::FormattedList itemList;

    itemList.add("VH IH FH Constant") << _vh_ih_fh_const.asString();
    itemList.add("Number of Relays")  << _nbr_of_relays;
    itemList.add("X.Y Format")        <<"Q"<< _x_dot_y_format.xBits <<"."<< _x_dot_y_format.yBits;
    itemList.add("DSP Sample Period") << _dsp_sample_period.numerator <<" / "<< _dsp_sample_period.denominator <<" = "<< getDSPSamplePeriod();

    CTILOG_INFO(dout,
            endl << formatTableName("Focus MFG Table 24") <<
            itemList
            );
}

float CtiAnsiFocusMfgTable24::getDSPSamplePeriod()
{
    return (float)_dsp_sample_period.numerator / (float)_dsp_sample_period.denominator;
}

float CtiAnsiFocusMfgTable24::getVhIhFhConstantsRcd()
{
    return _vh_ih_fh_const.asValue();
}
