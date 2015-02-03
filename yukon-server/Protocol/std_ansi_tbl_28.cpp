#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_28.h"

using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTable28::CtiAnsiTable28( BYTE *dataBlob, UINT8 nbrPresentDemands, UINT8 nbrPresentValues,
                                bool timeRemainingFlag, int format1, int format2, int timefmt, DataOrder dataOrder )
{
    int bytes, offset;
    offset = 0;
    _nbrPresentDemands = nbrPresentDemands;
    _nbrPresentValues = nbrPresentValues;
    _timeRemainingFlag = timeRemainingFlag;
    _format1 = format1;
    _format2 = format2;
    _timefmt = timefmt;
    _dataOrder = dataOrder;

    _presentDemand = new PRESENT_DEMAND_RCD[_nbrPresentDemands];
    for (int i = 0; i < _nbrPresentDemands; i++)
    {
        if (_timeRemainingFlag)
        {
             bytes = toTime( dataBlob, _presentDemand[i].timeRemaining, _timefmt );
            dataBlob += bytes;
            offset += bytes;
        }
        else
            _presentDemand[i].timeRemaining = 0;
        bytes = toDoubleParser( dataBlob, _presentDemand[i].demandValue, _format2, _dataOrder );
        dataBlob += bytes;
        offset += bytes;

    }
    _presentValue = new double[_nbrPresentValues];
    for (int i = 0; i < _nbrPresentValues; i++)
    {
        bytes = toDoubleParser( dataBlob, _presentValue[i], _format1, _dataOrder );
        dataBlob += bytes;
        offset += bytes;
    }

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable28::~CtiAnsiTable28()
{
    if (_presentDemand != NULL)
    {
        delete []_presentDemand;
        _presentDemand = NULL;
    }
    if (_presentValue != NULL)
    {
        delete []_presentValue;
        _presentValue = NULL;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable28& CtiAnsiTable28::operator=(const CtiAnsiTable28& aRef)
{
   if(this != &aRef)
   {
   }

   return *this;
}



//=========================================================================================================================================
//=========================================================================================================================================

double CtiAnsiTable28::getPresentDemand(int index)
{
    if (_presentDemand != NULL)
    {
        return (double )_presentDemand[index].demandValue;
    }
    else
        return -1;
}

double CtiAnsiTable28::getPresentValue(int index)
{
    if (_presentDemand != NULL)
    {
        return (double )_presentValue[index];
    }
    else
        return -1;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable28::printResult( const string& deviceName )
{
    Cti::FormattedList itemList;

    Cti::StreamBufferSink& demands = itemList.add("Demand Data");
    for( int index = 0; index < _nbrPresentDemands; index++ )
    {
        demands << _presentDemand[index].demandValue <<"  ";
    }

    Cti::StreamBufferSink& values = itemList.add("Value Data");
    for( int index = 0; index < _nbrPresentValues; index++ )
    {
        values << _presentValue[index] <<"  ";
    }

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 28") <<
            endl <<"** Present Register Data **"<<
            itemList
            );
}
