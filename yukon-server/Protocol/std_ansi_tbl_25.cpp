#include "precompiled.h"

#include "logger.h"
#include "ctitime.h"
#include "std_ansi_tbl_25.h"

using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTable25::CtiAnsiTable25( BYTE *dataBlob, int oc, int sum, int demnd, int coin, int tier, bool reset, bool time, bool cumd, bool cumcont,
                                int f1, int f2, int timeformat, bool season )
{
    int bytes;
    _dateTimeFieldFlag = time;
    _seasonInfoFieldFlag = season;

    if (_dateTimeFieldFlag)
    {
        bytes = toUint32STime( dataBlob, _endDateTime, timeformat );
        dataBlob += bytes;
    }
    if (_seasonInfoFieldFlag)
    {
        dataBlob += toAnsiIntParser(dataBlob, &_season, sizeof(unsigned char));
    }

    _prevDemandResetData = new CtiAnsiTable23( dataBlob, oc, sum, demnd, coin, tier, reset, time, cumd, cumcont,
                                               f1, f2, timeformat, 25 );
}

CtiAnsiTable25::~CtiAnsiTable25()
{
    if (_prevDemandResetData != NULL)
    {
        delete _prevDemandResetData;
        _prevDemandResetData = NULL;
    }

}
CtiAnsiTable25& CtiAnsiTable25::operator=(const CtiAnsiTable25& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}
void CtiAnsiTable25::printResult(const string& deviceName)
{
    Cti::FormattedList itemList;

    if( _dateTimeFieldFlag )
    {
        itemList.add("End Date Time") << CtiTime(_endDateTime);
    }

    if( _seasonInfoFieldFlag )
    {
        itemList.add("Season") << _season;
    }

    _prevDemandResetData->appendResult(itemList);

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 25") <<
            itemList
            );
}


CtiAnsiTable23 *CtiAnsiTable25::getDemandResetDataTable( )
{
    return _prevDemandResetData;
}

double CtiAnsiTable25::getEndDateTime()
{
    return (double)_endDateTime;
}
unsigned char CtiAnsiTable25::getSeason()
{
    return _season;
}


















