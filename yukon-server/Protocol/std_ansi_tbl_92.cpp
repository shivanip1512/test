/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_92
*
* Date:   05/21/2004
*
* Author: Julie Richter
*

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_92.h"

using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable92::CtiAnsiTable92( BYTE *dataBlob, int bitRate, int nbrSetupStrings, int setupStringLength )
{

    _bitRate = bitRate;
    _nbrSetupStrings = nbrSetupStrings;
    _setupStringLength = setupStringLength;

    dataBlob += toAnsiIntParser(dataBlob, &_globalParmsTbl.psem_identity, sizeof(UINT8));

    if (_bitRate == 1)
    {
        dataBlob += toAnsiIntParser(dataBlob, &_globalParmsTbl.bit_rate, sizeof(UINT32)); 
    }
    else
       _globalParmsTbl.bit_rate = 0;


    _globalParmsTbl.modem_setup_strings = new SETUP_STRING_RCD[_nbrSetupStrings];
    for (int x = 0; x < _nbrSetupStrings; x++ )
    {
        _globalParmsTbl.modem_setup_strings[x].setup_string = new unsigned char[_setupStringLength];
        for (int xx = 0; xx < _setupStringLength; xx++)
        {
            dataBlob += toAnsiIntParser(dataBlob, &_globalParmsTbl.modem_setup_strings[x].setup_string[xx], sizeof(unsigned char));
        }
    }

 }

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTable92::~CtiAnsiTable92()
{
    int i;

    if (_globalParmsTbl.modem_setup_strings != NULL)
    {
        for (i = 0; i < _nbrSetupStrings; i++)
        {
             if (_globalParmsTbl.modem_setup_strings[i].setup_string != NULL)
             {
                 delete  []_globalParmsTbl.modem_setup_strings[i].setup_string;
                 _globalParmsTbl.modem_setup_strings[i].setup_string = NULL;
             }
        }
        delete []_globalParmsTbl.modem_setup_strings;
        _globalParmsTbl.modem_setup_strings = NULL;
    }
}
//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable92& CtiAnsiTable92::operator=(const CtiAnsiTable92& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}


//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable92::printResult( const string& deviceName )
{
    Cti::FormattedList itemList;

    itemList.add("PSEM Identity") << _globalParmsTbl.psem_identity;

    if( _bitRate == 1 )
    {
        itemList.add("Bit Rate") << _globalParmsTbl.bit_rate;
    }

    for(int index = 0; index < _nbrSetupStrings; index++)
    {
        Cti::StreamBufferSink& setup_string = itemList.add("Modem Setup String "+ boost::lexical_cast<string>(index+1))
        for (int offset = 0; offset < _setupStringLength; offset++)
        {
            setup_string << _globalParmsTbl.modem_setup_strings[index].setup_string[offset] <<"  ";
        }
    }

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 92") <<
            endl <<"** Global Parameters Table **"<<
            itemList;
            );
}
