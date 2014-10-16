/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_91
*
* Date:   05/21/2004
*
* Author: Julie Richter
*

* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "logger.h"
#include "std_ansi_tbl_91.h"

using std::string;
using std::endl;

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTable91::CtiAnsiTable91( BYTE *dataBlob )
{

    dataBlob += toAnsiIntParser(dataBlob, &_telephoneTbl, sizeof( TELEPHONE_RCD ));
}

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTable91::~CtiAnsiTable91()
{

}
//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTable91& CtiAnsiTable91::operator=(const CtiAnsiTable91& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable91::printResult( const string& deviceName )
{
    Cti::FormattedList itemList;

    itemList.add("Answer Flag")         << (bool)_telephoneTbl.telephoneFlags.answer_flag;
    itemList.add("S Anchor Date Flag")  << (bool)_telephoneTbl.telephoneFlags.s_anchor_date_flag;
    itemList.add("OffHook Detect Flag") << (bool)_telephoneTbl.telephoneFlags.offhook_detect_flag;
    itemList.add("Bit Rate")            << getBitRate();
    itemList.add("ID in Purpose")       << (bool)_telephoneTbl.telephoneFlags.id_in_purpose;
    itemList.add("No Lockout Parm")     << (bool)_telephoneTbl.telephoneFlags.no_lockout_parm;

    CTILOG_INFO(dout,
            endl << formatTableName(deviceName +" Std Table 91") <<
            endl <<"** Actual Telephone Table **"<< 
            itemList
            );
}

int CtiAnsiTable91::getBitRate()
{
    return  (int) _telephoneTbl.telephoneFlags.bit_rate;
}

int CtiAnsiTable91::getNbrSetupStrings()
{
    return  (int) _telephoneTbl.nbr_setup_strings;
}

int CtiAnsiTable91::getSetupStringLength()
{
    return  (int) _telephoneTbl.setup_string_length;
}
