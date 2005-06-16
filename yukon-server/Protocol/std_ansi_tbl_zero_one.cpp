#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   std_ansi_tbl_zero_one
*
* Date:   9/16/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/std_ansi_tbl_zero_one.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/06/16 19:17:59 $
*    History: 
      $Log: std_ansi_tbl_zero_one.cpp,v $
      Revision 1.5  2005/06/16 19:17:59  jrichter
      Sync ANSI code with 3.1 branch!

      Revision 1.4  2005/02/10 23:23:58  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.3  2003/04/25 15:09:54  dsutton
      Standard ansi tables all inherit from a base table

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "logger.h"
#include "std_ansi_tbl_zero_one.h"

//=========================================================================================================================================
//=========================================================================================================================================
CtiAnsiTableZeroOne::CtiAnsiTableZeroOne( bool sn_flag, bool id_form )
{
    _serialNumberFlag = sn_flag;
    _idForm = id_form;
}

CtiAnsiTableZeroOne::CtiAnsiTableZeroOne( BYTE *dataBlob, bool sn_flag, bool id_form )
{
    _serialNumberFlag = sn_flag;
    _idForm = id_form;

   memcpy( (void *)&_manufacturer, dataBlob, 4 * sizeof( unsigned char ));
   dataBlob += 4 * sizeof( unsigned char );

   memcpy( (void *)&_ed_model, dataBlob, 8 * sizeof( unsigned char ));
   dataBlob += 8 * sizeof( unsigned char );

   memcpy( (void *)&_hw_version_number, dataBlob, sizeof( unsigned char ));
   dataBlob += sizeof( unsigned char );

   memcpy( (void *)&_hw_revision_number, dataBlob, sizeof( unsigned char ));
   dataBlob += sizeof( unsigned char );

   memcpy( (void *)&_fw_version_number, dataBlob, sizeof( unsigned char ));
   dataBlob += sizeof( unsigned char );

   memcpy( (void *)&_fw_revision_number, dataBlob, sizeof( unsigned char ));
   dataBlob += sizeof( unsigned char );

   if( _serialNumberFlag == false )
   {
      if( _idForm == false )
      {
         memcpy( (void *)&_mfg_serial_number, dataBlob, 16 * sizeof( char ));
      }
      else
      {
         memcpy( (void *)&_mfg_serial_number, dataBlob, 8 * sizeof( BCD ));
      }
   }
   else
   {
      memcpy( (void *)&_mfg_serial_number, dataBlob, sizeof( UINT64 ));
   }

}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableZeroOne::~CtiAnsiTableZeroOne()
{

}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableZeroOne::printResult(  )
{
    int integer;
    RWCString string1,string2;
    bool flag;

    /**************************************************************
    * its been discovered that if a method goes wrong while having the logger locked
    * unpleasant consquences may happen (application lockup for instance)  Because
    * of this, we make ugly printout calls so we aren't locking the logger at the time
    * of the method call
    ***************************************************************
    */
    string2 = getRawManufacturer();
    string1 = getResolvedManufacturer();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << endl << "=======================  Std Table 1 =========================" << endl;
        dout << "   Manufacturer: " << string1 << " (" << string2 <<")" << endl;
    }

    string2 = getRawModel();
    string1 = getResolvedModel();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Model: " << string1 << " (" << string2 <<")" << endl;
    }

    string2 = getRawSerialNumber();
    string1 = getResolvedSerialNumber();
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   Model: " << string1 << " (" << string2 <<")" << endl;
    }

    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << "   FW Version: " << getFWVersionNumber() << endl;
        dout << "   FW Revision: " << getFWRevisionNumber() << endl;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiAnsiTableZeroOne& CtiAnsiTableZeroOne::operator=(const CtiAnsiTableZeroOne& aRef)
{
   if(this != &aRef)
   {
   }
   return *this;

}
//=========================================================================================================================================
//=========================================================================================================================================
RWCString CtiAnsiTableZeroOne::getRawManufacturer( void )
{
    CHAR temp[5];
    memset (temp,'\0',5);
    memcpy (temp, _manufacturer, 4);
   return RWCString (temp);
}
//=========================================================================================================================================
//=========================================================================================================================================
RWCString CtiAnsiTableZeroOne::getResolvedManufacturer( void )
{
    CHAR temp[5];
    memset (temp,'\0',5);
    memcpy (temp, _manufacturer, 4);
   return RWCString (temp);
}
//=========================================================================================================================================
//=========================================================================================================================================
RWCString CtiAnsiTableZeroOne::getRawModel( void )
{
    CHAR temp[9];
    memset (temp,'\0',9);
    memcpy (temp, _ed_model, 8);
    return RWCString (temp);
}
//=========================================================================================================================================
//=========================================================================================================================================
RWCString CtiAnsiTableZeroOne::getResolvedModel( void )
{
    // need to take data format into account !!
    CHAR temp[9];
    memset (temp,'\0',9);
    memcpy (temp, _ed_model, 8);
    return RWCString (temp);
}

//=========================================================================================================================================
//=========================================================================================================================================
RWCString CtiAnsiTableZeroOne::getRawSerialNumber( void )
{
    RWCString ret;

    if( _serialNumberFlag == false )
    {
       if( _idForm == false )
       {
           CHAR temp[9];
           memset (temp,'\0',9);
           memcpy (temp, _mfg_serial_number.bcd_sn, 8);
           ret=RWCString (temp);
       }
       else
       {
           CHAR temp[17];
           memset (temp,'\0',17);
           memcpy (temp, _mfg_serial_number.char_sn, 16);
           ret=RWCString (temp);
       }
    }
    else
    {
        CHAR temp[8];
        ret=RWCString (_ui64toa(_mfg_serial_number.ll_sn,temp,10));
    }

   return RWCString(ret);
}
//=========================================================================================================================================
//=========================================================================================================================================
RWCString CtiAnsiTableZeroOne::getResolvedSerialNumber( void )
{
    RWCString ret;

    if( _serialNumberFlag == false )
    {
       if( _idForm == false )
       {
           CHAR temp[9];
           memset (temp,'\0',9);
           for (int x=0; x < 8; x++)
           {
               temp[x] = _mfg_serial_number.bcd_sn[x];
           }
           ret=RWCString (temp);
       }
       else
       {
           CHAR temp[17];
           memset (temp,'\0',17);
           for (int x=0; x < 16; x++)
           {
               temp[x] = _mfg_serial_number.char_sn[x];
           }
           ret=RWCString (temp);
       }
    }
    else
    {
        BYTEUINT64 temp;
        BYTEUINT64 sn;

        temp.u64=_mfg_serial_number.ll_sn;
        for (int x=0; x < 8; x++)
        {
            sn.ch[x] = temp.ch[x];
        }
        CHAR strTmp[8];
        ret=RWCString (_ui64toa(sn.u64,strTmp,10));
    }

   return RWCString(ret);
}

//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableZeroOne::generateResultPiece( BYTE **dataBlob )
{
    memcpy(*dataBlob, (void *)&_manufacturer, sizeof( _manufacturer ));
    *dataBlob += sizeof( _manufacturer );
    memcpy(*dataBlob, (void *)&_ed_model, sizeof(_ed_model));
    *dataBlob += sizeof( _ed_model );

    if( _serialNumberFlag == false )
    {
       if( _idForm == false )
       {
          memcpy(*dataBlob, (void *)&_mfg_serial_number, 16 * sizeof( char ));
          *dataBlob += 16;
       }
       else
       {
          memcpy(*dataBlob, (void *)&_mfg_serial_number, 8 * sizeof( BCD ));
          *dataBlob += 8;
       }
    }
    else
    {
       memcpy( *dataBlob, (void *)&_mfg_serial_number, sizeof( UINT64 ));
       *dataBlob += sizeof (UINT64);
    }
}
//=========================================================================================================================================
//=========================================================================================================================================
void CtiAnsiTableZeroOne::decodeResultPiece( BYTE **dataBlob )
{
    memcpy( (void *)&_manufacturer, *dataBlob, 4 * sizeof( unsigned char ));
    *dataBlob += 4 * sizeof( unsigned char );

    memcpy( (void *)&_ed_model, *dataBlob, 8 * sizeof( unsigned char ));
    *dataBlob += 8 * sizeof( unsigned char );


    if( _serialNumberFlag == false )
    {
       if( _idForm == false )
       {
          memcpy( (void *)&_mfg_serial_number, *dataBlob, 16 * sizeof( char ));
          *dataBlob += 16;
       }
       else
       {
          memcpy( (void *)&_mfg_serial_number, *dataBlob, 8 * sizeof( BCD ));
          *dataBlob += 8;
       }
    }
    else
    {
       memcpy( (void *)&_mfg_serial_number, *dataBlob, sizeof( UINT64 ));
       *dataBlob += sizeof (UINT64);
    }
}


int CtiAnsiTableZeroOne::getFWVersionNumber()
{
    return (int)_fw_version_number;
}
int CtiAnsiTableZeroOne::getFWRevisionNumber()
{
    return (int) _fw_revision_number;
}


