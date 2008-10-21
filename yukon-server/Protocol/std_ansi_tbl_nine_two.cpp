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
#include "yukon.h"

#include "logger.h"
#include "std_ansi_tbl_92.h"

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTable92::CtiAnsiTable92( int bitRate, int nbrSetupStrings, int setupStringLength )
{

}




CtiAnsiTable92::CtiAnsiTable92( BYTE *dataBlob, int bitRate, int nbrSetupStrings, int setupStringLength )
{

    _bitRate = bitRate;
    _nbrSetupStrings = nbrSetupStrings;
    _setupStringLength = setupStringLength;

    memcpy( (void *)&_global_parms_tbl.psem_identity, dataBlob, sizeof(UINT8));
    dataBlob += sizeof(UINT8);

    if (_bitRate == 1)
    {
        memcpy( (void *)&_global_parms_tbl.bit_rate, dataBlob, sizeof(UINT32));
        dataBlob += sizeof(UINT32);
    }
    else
       _global_parms_tbl.bit_rate = 0;


    _global_parms_tbl.modem_setup_strings = new SETUP_STRING_RCD[_nbrSetupStrings];
    for (int x = 0; x < _nbrSetupStrings; x++ )
    {
        _global_parms_tbl.modem_setup_strings[x].setup_string = new unsigned char[_setupStringLength];
        for (int xx = 0; xx < _setupStringLength; xx++)
        {
            memcpy( (void *)&_global_parms_tbl.modem_setup_strings[x].setup_string[xx], dataBlob, sizeof(unsigned char));
            dataBlob += sizeof(unsigned char);
        }
    }

 }

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTable92::~CtiAnsiTable92()
{
    int i;

    if (_global_parms_tbl.modem_setup_strings != NULL)
    {
        for (i = 0; i < _nbrSetupStrings; i++)
        {
             if (_global_parms_tbl.modem_setup_strings[i].setup_string != NULL)
             {
                 delete  []_global_parms_tbl.modem_setup_strings[i].setup_string;
                 _global_parms_tbl.modem_setup_strings[i].setup_string = NULL;
             }
        }
        delete []_global_parms_tbl.modem_setup_strings;
        _global_parms_tbl.modem_setup_strings = NULL;
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
void CtiAnsiTable92::generateResultPiece( BYTE **dataBlob )
{

}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable92::decodeResultPiece( BYTE **dataBlob )
{

}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTable92::printResult( const string& deviceName )
{
    int index, i, j;

    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "=================== "<<deviceName<<"  Std Table 92  ========================" << endl;
        dout << "  ** Global Parameters Table **" << endl;
        dout << "     PSEM Identity : " <<_global_parms_tbl.psem_identity<< endl;
    }

    if (_bitRate == 1)
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "     Bit Rate : " <<_global_parms_tbl.bit_rate<< endl;
    }

    for (int x = 0; x < _nbrSetupStrings; x++ )
    {
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << "     Modem Setup String "<<x+1<<" :";
        }
        for (int xx = 0; xx < _setupStringLength; xx++)
        {
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << " "<<_global_parms_tbl.modem_setup_strings[x].setup_string[xx];
            }
        }
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << endl;
        }
    }

}
