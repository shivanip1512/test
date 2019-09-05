/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_32
*
* Date:   2/1/2005
*
* Author: Julie Richter
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_32.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2008/10/21 16:30:31 $

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_32.h"
#include <boost/lexical_cast.hpp>

using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable32::CtiAnsiTable32( BYTE *dataBlob, UINT16 nbrDispSources, UINT8 widthDispSources  )
{
    _nbrDispSources = nbrDispSources;
    _widthDispSources = widthDispSources;

    _displaySources = new DISP_SOURCE_DESC_RCD[_nbrDispSources];
    for (int i = 0; i < _nbrDispSources; i++)
    {
         _displaySources[i].displaySource = new UINT8[_widthDispSources];
         for (int j = 0; j < _widthDispSources; j++)
         {
             dataBlob += toAnsiIntParser(dataBlob, &_displaySources[i].displaySource[j], sizeof(UINT8));
         }
    }


}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable32::~CtiAnsiTable32()
{
    if (_displaySources != NULL)
    {
        for (int i = 0; i < _nbrDispSources; i++)
        {
            if (_displaySources[i].displaySource != NULL)
            {
                delete  []_displaySources[i].displaySource;
                _displaySources[i].displaySource = NULL;
            }
        }
        delete []_displaySources;
        _displaySources = NULL;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable32& CtiAnsiTable32::operator=(const CtiAnsiTable32& aRef)
{
   if(this != &aRef)
   {
   }

   return *this;
}



//=========================================================================================================================================
//=========================================================================================================================================

UINT8 CtiAnsiTable32::getDisplaySources(int sourceIndex, int widthIndex)
{
    if (_displaySources[sourceIndex].displaySource[widthIndex] != NULL)
    {
        return _displaySources[sourceIndex].displaySource[widthIndex];
    }
    else
        return -1;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable32::printResult( const string& deviceName )
{
    Cti::FormattedList itemList;

    for (int i = 0; i < _nbrDispSources; i++)
    {
        Cti::StreamBufferSink& values = itemList.add("Display Source["+ boost::lexical_cast<string>(i) +"]");
        for (int j = 0; j < _widthDispSources; j++)
        {
            values << _displaySources[i].displaySource[j] <<" ";
        }
    }

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 32") <<
            endl <<"** Display Source Table **"<<
            itemList
            );
}
