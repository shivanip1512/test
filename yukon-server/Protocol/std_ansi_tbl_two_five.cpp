#include "yukon.h"

/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_two_five
*
* Date:   7/28/2005
*
* Author: Julie Richter
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_tbl_two_five.cpp-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2005/09/29 21:18:54 $
*    History: 
      $Log: std_ansi_tbl_two_five.cpp,v $
      Revision 1.2  2005/09/29 21:18:54  jrichter
      Merged latest 3.1 changes to head.

      Revision 1.1.2.1  2005/08/01 17:08:26  jrichter
      added frozen register retreival functionality.  cleanup.

      
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "std_ansi_tbl_two_five.h"

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTableTwoFive::CtiAnsiTableTwoFive( int oc, int sum, int demnd, int coin, int tier, bool reset, bool time, bool cumd, bool cumcont,
                         int f1, int f2, int timeformat, bool season )
{
    _prevDemandResetData = NULL;
}
CtiAnsiTableTwoFive::CtiAnsiTableTwoFive( BYTE *dataBlob, int oc, int sum, int demnd, int coin, int tier, bool reset, bool time, bool cumd, bool cumcont,
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

    _prevDemandResetData = new CtiAnsiTableTwoThree ( dataBlob, oc, sum, demnd, coin, tier, reset, time, cumd, cumcont, 
                                                      f1, f2, timeformat, 25 );
}

CtiAnsiTableTwoFive::~CtiAnsiTableTwoFive()
{ 
    if (_prevDemandResetData != NULL)
    {
        delete _prevDemandResetData;
        _prevDemandResetData = NULL;
    }

}
CtiAnsiTableTwoFive& CtiAnsiTableTwoFive::operator=(const CtiAnsiTableTwoFive& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}
void CtiAnsiTableTwoFive::printResult()
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
        dout << endl << "=======================  Std Table 25 ========================" << endl;
    }

    if (_dateTimeFieldFlag)
    {                     
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "**End Date Time:  "<< RWTime(_endDateTime).asString()<<endl;
    }
    if (_seasonInfoFieldFlag)
    {                     
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "**Season:  "<< (int)_season<<endl;
    }
    _prevDemandResetData->printResult();
}


CtiAnsiTableTwoThree *CtiAnsiTableTwoFive::getDemandResetDataTable( )
{
    return _prevDemandResetData;
}

double CtiAnsiTableTwoFive::getEndDateTime()
{
    return (double)_endDateTime;
}
unsigned char CtiAnsiTableTwoFive::getSeason()
{
    return _season;
}


















