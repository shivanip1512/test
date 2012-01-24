/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_25
*
* Date:   7/28/2005
*
* Author: Julie Richter
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_tbl_25.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
      $Log: std_ansi_tbl_two_five.cpp,v $
      Revision 1.5  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.4  2005/12/20 17:19:57  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.3  2005/12/12 20:34:29  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.2  2005/09/29 21:18:54  jrichter
      Merged latest 3.1 changes to head.

      Revision 1.1.2.1  2005/08/01 17:08:26  jrichter
      added frozen register retreival functionality.  cleanup.


*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
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
        memcpy( (void *)&_season, dataBlob, sizeof(unsigned char));
        dataBlob += sizeof (unsigned char);
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
void CtiAnsiTable25::printResult( const string& deviceName)
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
        dout << endl << "=================== "<<deviceName<<"  Std Table 25 ========================" << endl;
    }

    if (_dateTimeFieldFlag)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "**End Date Time:  "<< CtiTime(_endDateTime).asString()<<endl;
    }
    if (_seasonInfoFieldFlag)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "**Season:  "<< (int)_season<<endl;
    }
    _prevDemandResetData->printResult(deviceName);
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


















