#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   prot_ansi
*
* Date:   6/13/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/PROTOCOL/prot_ansi.cpp-arc  $
* REVISION     :  $Revision: 1.25 $
* DATE         :  $Date: 2008/08/13 22:40:57 $
*    History:
      $Log: prot_ansi.cpp,v $
      Revision 1.25  2008/08/13 22:40:57  jrichter
      YUK-6310
      Sentinel dial up meter reads causing exceptions when scanner reads in future or year(S) old lastLpTime dates.

      Revision 1.24  2008/04/25 21:45:14  mfisher
      YUK-5743 isTransactionComplete() changes not propagated to all protocols
      changed isTransactionComplete() to const

      Revision 1.23  2007/03/15 17:46:35  jrichter
      Last Interval Quadrant KVar readings reporting back correctly from present value table 28.

      Revision 1.22  2006/05/03 17:19:33  jrichter
      BUG FIX:  correct DST adjustment for columbia flags.  added check for _nbrFullBlocks > 0 so it wouldn't set lastLPTime to 2036

      Revision 1.21  2006/04/14 16:31:03  jrichter
      BUG FIX: DST adjustment

      Revision 1.20  2006/04/06 17:00:30  jrichter
      BUG FIX:  memory leak in porter...cleared out stdTablesAvailable/mfgTablesAvailable list.  since, prot_ansi object was not being destructed...it kept adding each time through connecting to device.  hopefully this is the root of all sentinel evil.

      Revision 1.19  2006/03/31 16:16:01  jrichter
      BUG FIX & ENHANCEMENT:  fixed a memory leak (multiple allocations of lpBlocks, but only one deallocation), added quality retrieval.

      Revision 1.18  2006/01/05 23:42:00  jrichter
      BUG FIX:  Corrected LastLPTime update situation where there are 0 complete LP blocks so it didn't insert a bogus 2036 date for lastLPTime.

      Revision 1.17  2005/12/20 17:19:55  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.16  2005/12/12 20:34:28  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.14.2.2  2005/12/12 19:50:39  jrichter
      BUGS&ENHANCEMENTS: sync up with 31branch.  added device name to table debug, update lp data with any valid data received back from device even if it is not complete, report demand reset time for frozen values that are not initialized

      Revision 1.14.2.1  2005/11/03 23:54:19  jrichter
      fixed handling of DST changeover.

      Revision 1.14  2005/09/29 21:18:24  jrichter
      Merged latest 3.1 changes to head.

      Revision 1.12.2.3  2005/08/12 19:54:03  jliu
      Date Time Replaced

      Revision 1.12.2.2  2005/07/27 19:28:00  alauinger
      merged from the head 20050720


      Revision 1.12.2.1  2005/07/14 22:27:01  jliu
      RWCStringRemoved

      Revision 1.13  2005/06/16 19:17:59  jrichter
      Sync ANSI code with 3.1 branch!

      Revision 1.12  2005/03/14 21:44:16  jrichter
      updated with present value regs, batterylife info, corrected quals, multipliers/offsets, corrected single precision float define, modifed for commander commands, added demand reset

      Revision 1.11  2005/02/10 23:23:57  alauinger
      Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build

      Revision 1.10  2005/01/25 18:33:51  jrichter
      added present value tables for kv2 and sentinel for voltage, current, freq, pf, etc..meter info

      Revision 1.9  2005/01/03 23:07:14  jrichter
      checking into 3.1, for use at columbia to test sentinel

      Revision 1.8  2004/12/10 21:58:40  jrichter
      Good point to check in for ANSI.  Sentinel/KV2 working at columbia, duke, whe.

      Revision 1.7  2004/09/30 21:37:17  jrichter
      Ansi protocol checkpoint.  Good point to check in as a base point.

      Revision 1.6  2004/04/22 21:12:53  dsutton
      Last known revision DLS

      Revision 1.5  2003/04/25 15:12:29  dsutton
      This is now base protocol class for every ansi type meter

*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "guard.h"
#include "configparms.h"
#include "logger.h"
#include "pointdefs.h"
#include "prot_ansi.h"
#include "utility.h"
#include "ctitime.h"
#include "ctidate.h"



const CHAR * CtiProtocolANSI::METER_TIME_TOLERANCE = "PORTER_SENTINEL_TIME_TOLERANCE";
//const CHAR * CtiProtocolANSI::ANSI_DEBUGLEVEL = "ANSI_DEBUGLEVEL";
//========================================================================================================================================
//=========================================================================================================================================

CtiProtocolANSI::CtiProtocolANSI()
{
   _tables = NULL;
   _header = NULL;
   _billingTable = NULL;
   _tableZeroZero = NULL;
   _tableZeroOne = NULL;
   _tableZeroEight = NULL;
   _tableOneZero = NULL;
   _tableOneOne = NULL;
   _tableOneTwo = NULL;
   _tableOneThree = NULL;
   _tableOneFour = NULL;
   _tableOneFive = NULL;
   _tableOneSix = NULL;
   _tableTwoOne = NULL;
   _tableTwoTwo = NULL;
   _tableTwoThree = NULL;
   _tableTwoSeven = NULL;
   _tableTwoEight = NULL;
   _tableThreeOne = NULL;
   _tableThreeTwo = NULL;
   _tableThreeThree = NULL;
   _tableFiveOne = NULL;
   _tableFiveTwo = NULL;
   _tableSixOne = NULL;
   _tableSixTwo = NULL;
   _tableSixThree = NULL;
   _tableSixFour = NULL;
   //_tableFiveFive = NULL;
   _frozenRegTable = NULL;

   _validFlag = false;
   _entireTableFlag = false;
   _nbrLPDataBlksWanted = 0;
   _nbrLPDataBlkIntvlsWanted = 0;
   _nbrFirstLPDataBlkIntvlsWanted = 0;
   _nbrLastLPDataBlkIntvlsWanted = 0;
   _timeSinceLastLPTime = 0;

   _seqNbr = 0;

   _lpNbrLoadProfileChannels = 0;
   _lpNbrIntvlsLastBlock = 0;
   _lpNbrValidBlks = 0;
   _lpLastBlockIndex = 0;
   _lpNbrIntvlsPerBlock = 0;
   _lpNbrBlksSet = 0;
   _lpMaxIntervalTime = 0;
   _lpStartBlockIndex = 0;
   _lpBlockSize = 0;
   _lpOffset = 0;
   _lpNbrFullBlocks = 0;
   _lpLastBlockSize = 0;
   _stdTblsAvailable.push_back(0);

   _lpValues = NULL;
   _lpTimes = NULL;
    _lpQuality = NULL;

   _currentTableNotAvailableFlag = false;
   _requestingBatteryLifeFlag = false;
   _invalidLastLoadProfileTime = false;
   _forceProcessDispatchMsg = false;


}

//=========================================================================================================================================
//FIXME - I'm very broken....
//=========================================================================================================================================

void CtiProtocolANSI::destroyMe( void )
{

    {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << CtiTime() << " Ansi destroy started----" << endl;
   }

   //let's check to see if we have valid pointers to delete (for scanners sake)
   if( _header != NULL )
   {
      delete _header;
      _header=NULL;
   }

   if( _tables != NULL )
   {
      delete []_tables;
      _tables=NULL;
   }

   if( _tableZeroZero != NULL )
   {
      delete _tableZeroZero;
      _tableZeroZero = NULL;
   }

   if( _tableZeroOne != NULL )
   {
      delete _tableZeroOne;
      _tableZeroOne = NULL;
   }

   if( _tableZeroEight != NULL )
   {
      delete _tableZeroEight;
      _tableZeroEight = NULL;
   }

   if( _tableOneZero != NULL )
   {
      delete _tableOneZero;
      _tableOneZero = NULL;
   }

   if( _tableOneOne != NULL )
   {
      delete _tableOneOne;
      _tableOneOne = NULL;
   }

   if( _tableOneTwo != NULL )
   {
      delete _tableOneTwo;
      _tableOneTwo = NULL;
   }

   if( _tableOneThree != NULL )
   {
      delete _tableOneThree;
      _tableOneThree = NULL;
   }

   if( _tableOneFour != NULL )
   {
      delete _tableOneFour;
      _tableOneFour = NULL;
   }

   if( _tableOneFive != NULL )
   {
      delete _tableOneFive;
      _tableOneFive = NULL;
   }

   if( _tableOneSix != NULL )
   {
      delete _tableOneSix;
      _tableOneSix = NULL;
   }

   if( _tableTwoOne != NULL )
   {
      delete _tableTwoOne;
      _tableTwoOne = NULL;
   }

   if( _tableTwoTwo != NULL )
   {
      delete _tableTwoTwo;
      _tableTwoTwo = NULL;
   }

   if( _tableTwoThree != NULL )
   {
      delete _tableTwoThree;
      _tableTwoThree = NULL;
   }
   if( _tableTwoSeven != NULL )
   {
      delete _tableTwoSeven;
      _tableTwoSeven = NULL;
   }
   if( _tableTwoEight != NULL )
   {
      delete _tableTwoEight;
      _tableTwoEight = NULL;
   }
   if( _tableThreeOne != NULL )
   {
      delete _tableThreeOne;
      _tableThreeOne = NULL;
   }
   if( _tableThreeTwo != NULL )
   {
      delete _tableThreeTwo;
      _tableThreeTwo = NULL;
   }
   if( _tableThreeThree != NULL )
   {
      delete _tableThreeThree;
      _tableThreeThree = NULL;
   }
   if( _tableFiveOne != NULL )
   {
      delete _tableFiveOne;
      _tableFiveOne = NULL;
   }

   if( _tableFiveTwo != NULL )
   {
      delete _tableFiveTwo;
      _tableFiveTwo = NULL;
   }
   if( _tableSixOne != NULL )
   {
      delete _tableSixOne;
      _tableSixOne = NULL;
   }
   if( _tableSixTwo != NULL )
   {
      delete _tableSixTwo;
      _tableSixTwo = NULL;
   }
   if( _tableSixThree != NULL )
   {
      delete _tableSixThree;
      _tableSixThree = NULL;
   }
   if( _tableSixFour != NULL )
   {
      delete _tableSixFour;
      _tableSixFour = NULL;
   }
   if( _tableZeroEight != NULL )
   {
      delete _tableZeroEight;
      _tableZeroEight = NULL;
   }

   if (_frozenRegTable != NULL)
   {
       delete _frozenRegTable;
       _frozenRegTable = NULL;
   }

   if( _billingTable != NULL )
   {
      delete _billingTable;
      _billingTable = NULL;
   }

   if( _lpValues != NULL )
   {
      delete []_lpValues;
      _lpValues = NULL;
   }

   if( _lpTimes != NULL )
   {
      delete []_lpTimes;
      _lpTimes = NULL;
   }
   if ( _lpQuality != NULL )
   {
       delete []_lpQuality;
       _lpQuality = NULL;
   }

   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << CtiTime() << " ----Ansi destroy finished" << endl;
   }

}


//=========================================================================================================================================
//=========================================================================================================================================

CtiProtocolANSI::~CtiProtocolANSI()
{
   destroyMe();
}

//=========================================================================================================================================
//gets us back to our original state and cleans stuff up
//=========================================================================================================================================

void CtiProtocolANSI::reinitialize( void )
{
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << CtiTime() << " Ansi reinit started----" << endl;
   }
   try
   {

       getApplicationLayer().reinitialize();
       destroyManufacturerTables();
       destroyMe();

       _lpNbrLoadProfileChannels = 0;
       _lpNbrIntvlsLastBlock = 0;
       _lpNbrValidBlks = 0;
       _lpLastBlockIndex = 0;
       _lpNbrIntvlsPerBlock = 0;
       _lpNbrBlksSet = 0;
       _lpMaxIntervalTime = 0;
       _lpStartBlockIndex = 0;
       _lpBlockSize = 0;
       _lpOffset = 0;
       _lpNbrFullBlocks = 0;
       _lpLastBlockSize = 0;

       if (!_stdTblsAvailable.empty())
       {
           _stdTblsAvailable.clear();
           _stdTblsAvailable.push_back(0);
       }
       if (!_mfgTblsAvailable.empty())
       {
           _mfgTblsAvailable.clear();
       }

    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << CtiTime() << " ----Ansi reinit finished" << endl;
   }

}

//=========================================================================================================================================
//porter side:
//we take the outmessage that we assembled above on the scanner side and start it on it's journey through the layers of the ansi protocol
//=========================================================================================================================================

int CtiProtocolANSI::recvOutbound( OUTMESS *OutMessage )
{
    try
    {
        // set our table index to zero
       _index = 0;
       buildWantedTableList (OutMessage->Buffer.OutMessage);

       // init the application layer
       if( _header->numTablesRequested )
       {
           // prime the application layer with the first table request
           getApplicationLayer().init();
           getApplicationLayer().initializeTableRequest (_tables[_index].tableID,
                                                _tables[_index].tableOffset,
                                                _tables[_index].bytesExpected,
                                                _tables[_index].type,
                                                _tables[_index].operation);
       }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

   return( _header->numTablesRequested );   //just a val
}

// needed on both sides

void CtiProtocolANSI::buildWantedTableList( BYTE *aPtr )
{
    try
    {

        BYTE *bufptr = aPtr;
        int sizeOfPswd = 20;

        if( _header != NULL )
        {
            delete _header;
            _header = NULL;
        }
        _header = CTIDBG_new WANTS_HEADER;

        if( _header != NULL )
        {

           memcpy( ( void *)_header, bufptr, sizeof( WANTS_HEADER ) );
           bufptr += sizeof( WANTS_HEADER );

           getApplicationLayer().setPassword(bufptr);
           bufptr += sizeOfPswd;

           if (_tables != NULL)
           {
               delete _tables;
               _tables = NULL;
           }
           _tables = CTIDBG_new ANSI_TABLE_WANTS[_header->numTablesRequested];

           if( _tables != NULL )
           {
              for( int x = 0; x < _header->numTablesRequested; x++ )
              {
                 memcpy( ( void *)&_tables[x], bufptr, sizeof( ANSI_TABLE_WANTS ));
                 bufptr += sizeof( ANSI_TABLE_WANTS );
              }
           }
           //memcpy ((void *)_scanOperation, bufptr, sizeof(BYTE));
           _scanOperation = ( CtiProtocolANSI::ANSI_SCAN_OPERATION ) *bufptr;
           bufptr += 1;


           memcpy ((void *)&_parseFlags, bufptr, sizeof(UINT));
           bufptr += sizeof(UINT);

        }
        setAnsiDeviceType();
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
}

//=========================================================================================================================================
//=========================================================================================================================================

CtiANSIApplication &CtiProtocolANSI::getApplicationLayer( void )
{
   return _appLayer;
}

//=========================================================================================================================================
//porter side:
//pass our xfer message to whomever can make it look ansi-like
//=========================================================================================================================================

bool CtiProtocolANSI::generate( CtiXfer &xfer )
{
    if (!_currentTableNotAvailableFlag)
    {
        return (getApplicationLayer().generate( xfer ));
    }
    else
        return false;
}

//=========================================================================================================================================
//here's where we'll decide what we got back from the port
//=========================================================================================================================================

bool CtiProtocolANSI::decode( CtiXfer &xfer, int status )
{
    try
    {

        bool  done = false;

        done = getApplicationLayer().decode( xfer, status );

        if( getApplicationLayer().isTableComplete() || getApplicationLayer().getPartialProcessLPDataFlag())
        {
            if ((_tables[_index].type == ANSI_TABLE_TYPE_STANDARD && isStdTableAvailableInMeter(_tables[_index].tableID)) ||
                (_tables[_index].type == ANSI_TABLE_TYPE_MANUFACTURER && isMfgTableAvailableInMeter(_tables[_index].tableID - 0x0800)))
            {
                if (_tables[_index].operation == ANSI_OPERATION_READ)
                {
                    convertToTable();
                }
                else if (_requestingBatteryLifeFlag)
                {
                    _tables[_index].tableID = 2050;
                    _tables[_index].tableOffset = 0;
                    _tables[_index].type = ANSI_TABLE_TYPE_MANUFACTURER;
                    _tables[_index].operation = ANSI_OPERATION_READ;
                    updateBytesExpected ();
                    getApplicationLayer().initializeTableRequest (_tables[_index].tableID,
                                                                  _tables[_index].tableOffset,
                                                                  _tables[_index].bytesExpected,
                                                                  _tables[_index].type,
                                                                  _tables[_index].operation);
                    _requestingBatteryLifeFlag = false;
                    return true;

                }
                else
                {
                    _tables[_index].tableID = 8;
                    _tables[_index].tableOffset = 0;
                    _tables[_index].type = ANSI_TABLE_TYPE_STANDARD;
                    _tables[_index].operation = ANSI_OPERATION_READ;
                    updateBytesExpected ();
                    getApplicationLayer().initializeTableRequest (_tables[_index].tableID,
                                                                  _tables[_index].tableOffset,
                                                                  _tables[_index].bytesExpected,
                                                                  _tables[_index].type,
                                                                  _tables[_index].operation);
                    return true;
                }

                if (_scanOperation == ANSI_SCAN_OPERATION::demandReset && _tables[_index].tableID == 1) //1 = demand reset
                {
                     int i = proc09RemoteReset(1);
                     if (i)
                     {
                         return true;
                     }
                }

               if (_tables[_index].tableID == 22)
               {
                   int julie = snapshotData();
                   if (julie < 0)
                   {
                       return true;
                   }
               }
               if (_tables[_index].tableID == 28)
               {
                   int battery = batteryLifeData();
                   if (battery < 0)
                   {
                       _requestingBatteryLifeFlag = true;
                       return true;
                   }
               }

               if (_tables[_index].tableID == 63  && _tableSixThree != NULL)
               {
                   _lpNbrIntvlsLastBlock = _tableSixThree->getNbrValidIntvls(1);
                   _lpNbrValidBlks = _tableSixThree->getNbrValidBlocks(1);
                   _lpLastBlockIndex = _tableSixThree->getLastBlkElmt(1);
                   _lpNbrIntvlsPerBlock = _tableSixOne->getNbrBlkIntsSet(1);
                   _lpMaxIntervalTime = _tableSixOne->getMaxIntTimeSet(1);
                   _lpNbrBlksSet = _tableSixOne->getNbrBlksSet(1);

                   if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )
                   {
                           CtiLockGuard< CtiLogger > doubt_guard( dout );
                           dout <<  "  ** DEBUG **** _lpNbrIntvlsLastBlock  " <<_lpNbrIntvlsLastBlock << endl;
                           dout <<  "  ** DEBUG **** _lpLastBlockIndex  " <<_lpLastBlockIndex << endl;
                   }
                   _lpBlockSize = getSizeOfLPDataBlock(1);
                   _lpLastBlockSize =  getSizeOfLastLPDataBlock(1);
                   getApplicationLayer().setLPBlockSize(_lpBlockSize);

                   if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )
                   {
                           CtiLockGuard< CtiLogger > doubt_guard( dout );
                           dout <<  "  ** DEBUG **** _lpBlockSize  " <<_lpBlockSize << endl;
                           dout <<  "  ** DEBUG **** _lpLastBlockSize  " <<_lpLastBlockSize << endl;
                   }
                   //Fall Back DST adjustment
                   if (_tableFiveTwo != NULL)
                   {
                       CtiTime llp(_header->lastLoadProfileTime);
                       CtiDate dte = llp.date();
                       CtiTime endLLP = CtiTime().endDST( dte.year() );

                       CtiTime now = CtiTime();
                       CtiDate dtf = now.date();
                       CtiTime end = CtiTime().endDST(dtf.year());

                       CtiTime begin = CtiTime().beginDST(dtf.year());

                       CtiTime beginLLP = CtiTime().beginDST( dte.year() );


                       if (!_tableFiveTwo->adjustTimeForDST() &&
                          ( (llp < endLLP  ) &&
                            (now > end) ) )
                       {
                           _header->lastLoadProfileTime -= 3600;
                           {
                                   CtiLockGuard< CtiLogger > doubt_guard( dout );
                                   dout <<  "  ** DEBUG **** Last Load Profile Time Adjusted to: " <<CtiTime(_header->lastLoadProfileTime) << endl;
                           }
                       }
                       //Spring Ahead DST adjustment
                       /*if (_tableFiveTwo->adjustTimeForDST() )
                       {
                           _header->lastLoadProfileTime += 3600;
                           {
                                   CtiLockGuard< CtiLogger > doubt_guard( dout );
                                   dout <<  "  ** DEBUG **** Last Load Profile Time Adjusted to: " <<CtiTime(_header->lastLoadProfileTime) << endl;
                           }
                       }  */
                   }
                   if ((_lpStartBlockIndex = calculateLPDataBlockStartIndex(_header->lastLoadProfileTime)) < 0)
                   {
                       return true;
                   }
                   else
                   {
                       _lpNbrFullBlocks = _lpLastBlockIndex - _lpStartBlockIndex;
                       _lpOffset = _lpStartBlockIndex * _lpBlockSize;
                   }
               }
           }
           // anything else to do
           if ((_index+1) < _header->numTablesRequested)
           {
               _index++;
               updateBytesExpected ();

               if (!_currentTableNotAvailableFlag)
               {

                   // bad way to do this but if we're getting a manufacturers table, add the offset
                   // NOTE: this may be specific to the kv2 !!!!!
                   if (_tables[_index].type == ANSI_TABLE_TYPE_MANUFACTURER)
                   {
                     _tables[_index].tableID += 0x0800;
                   }

                   if (_tables[_index].tableID == 15 || _tables[_index].tableID == 23 || _tables[_index].tableID == 25)
                   {
                       getApplicationLayer().setLPDataMode( true, _tables[_index].bytesExpected );
                   }

                   if (_tables[_index].tableID >= 64 && _tables[_index].tableID <= 67)
                   {
                       getApplicationLayer().setLPDataMode( true, _tableSixOne->getLPMemoryLength() );

                       _tables[_index].tableOffset = _lpOffset;

                   }
                   else
                   {
                       _tables[_index].tableOffset = 0;
                   }
                   getApplicationLayer().initializeTableRequest (_tables[_index].tableID,
                                                        _tables[_index].tableOffset,
                                                        _tables[_index].bytesExpected,
                                                        _tables[_index].type,
                                                        _tables[_index].operation);
               }
               else
               {
                   _currentTableNotAvailableFlag = false;
               }

           }
           else
           {
               if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )//DEBUGLEVEL_LUDICROUS )
               {
                   if (_tableZeroZero != NULL)
                       _tableZeroZero->printResult(getAnsiDeviceName());
                   if (_tableZeroOne != NULL)
                       _tableZeroOne->printResult(getAnsiDeviceName());
                   if (_tableOneOne != NULL)
                       _tableOneOne->printResult(getAnsiDeviceName());
                   if (_tableOneTwo != NULL)
                       _tableOneTwo->printResult(getAnsiDeviceName());
                   if (_tableOneThree != NULL)
                       _tableOneThree->printResult(getAnsiDeviceName());
                   if (_tableOneFour != NULL)
                       _tableOneFour->printResult(getAnsiDeviceName());
                   if (_tableOneFive != NULL)
                       _tableOneFive->printResult(getAnsiDeviceName());
                   if (_tableOneSix != NULL)
                       _tableOneSix->printResult(getAnsiDeviceName());
                   if (_tableTwoOne != NULL)
                       _tableTwoOne->printResult(getAnsiDeviceName());
                   if (_tableTwoTwo != NULL)
                       _tableTwoTwo->printResult(getAnsiDeviceName());
                   if (_tableTwoThree != NULL)
                       _tableTwoThree->printResult(getAnsiDeviceName());
                   if (_frozenRegTable != NULL)
                       _frozenRegTable->printResult(getAnsiDeviceName());
                   if (_tableTwoSeven != NULL)
                       _tableTwoSeven->printResult(getAnsiDeviceName());
                   if (_tableTwoEight != NULL)
                       _tableTwoEight->printResult(getAnsiDeviceName());
                   if (_tableThreeOne != NULL)
                       _tableThreeOne->printResult(getAnsiDeviceName());
                   if (_tableThreeTwo != NULL)
                       _tableThreeTwo->printResult(getAnsiDeviceName());
                   if (_tableThreeThree != NULL)
                       _tableThreeThree->printResult(getAnsiDeviceName());
                   if (_tableFiveOne != NULL)
                       _tableFiveOne->printResult(getAnsiDeviceName());
                   if (_tableFiveTwo != NULL)
                       _tableFiveTwo->printResult(getAnsiDeviceName());
                   if (_tableSixOne != NULL)
                       _tableSixOne->printResult(getAnsiDeviceName());
                   if (_tableSixTwo != NULL)
                       _tableSixTwo->printResult(getAnsiDeviceName());
                   if (_tableSixThree != NULL)
                       _tableSixThree->printResult(getAnsiDeviceName());
                   if (_tableSixFour != NULL)
                       _tableSixFour->printResult(getAnsiDeviceName());
                   if (_tableZeroEight != NULL)
                       _tableZeroEight->printResult(getAnsiDeviceName());

               }

               // done with tables, do the termination etc
               getApplicationLayer().terminateSession();
           }
        }

        return( done ); //just a val
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return false;

}

//=========================================================================================================================================
//we've gotten table data back from the device, so we'll paste it into the appropriate table structure
//we may decode it here, we may send it somewhere else....
//=========================================================================================================================================
void CtiProtocolANSI::convertToTable(  )
{
    try
    {

        // if its manufactured, send it to the child class
        if (_tables[_index].type == ANSI_TABLE_TYPE_MANUFACTURER)
        {
            if (isMfgTableAvailableInMeter(_tables[_index].tableID - 0x0800))
            {
            }
            else
            {

            }
            convertToManufacturerTable (getApplicationLayer().getCurrentTable(),
                                        _tables[_index].bytesExpected,
                                        _tables[_index].tableID);

        }
        else
        {
            if (isStdTableAvailableInMeter(_tables[_index].tableID))
            {


                switch( _tables[_index].tableID )
                {
                case 0:
                   {
                       if (_tableZeroZero != NULL)
                       {
                           delete _tableZeroZero;
                           _tableZeroZero = NULL;
                       }
                      _tableZeroZero = new CtiAnsiTableZeroZero( getApplicationLayer().getCurrentTable() );
                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )//DEBUGLEVEL_LUDICROUS )
                      {
                          _tableZeroZero->printResult(getAnsiDeviceName());
                      }

                      setTablesAvailable(_tableZeroZero->getStdTblsUsed(), _tableZeroZero->getDimStdTblsUsed(),
                                         _tableZeroZero->getMfgTblsUsed(), _tableZeroZero->getDimMfgTblsUsed());
                   }
                   break;

                case 1:
                   {
                       if (_tableZeroOne != NULL)
                       {
                           delete _tableZeroOne;
                           _tableZeroOne = NULL;
                       }
                      _tableZeroOne = new CtiAnsiTableZeroOne( getApplicationLayer().getCurrentTable(),
                                                               _tableZeroZero->getRawMfgSerialNumberFlag(),
                                                               _tableZeroZero->getRawIdFormat() );
                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )//DEBUGLEVEL_LUDICROUS )
                      {
                          _tableZeroOne->printResult(getAnsiDeviceName());
                      }

                      if ((int)getApplicationLayer().getAnsiDeviceType() == 2) //sentinel
                      {
                          getApplicationLayer().setFWVersionNumber(_tableZeroOne->getFWVersionNumber());
                      }
                   }
                   break;
               case 8:
                   {
                       if (_tableZeroEight != NULL)
                       {
                           delete _tableZeroEight;
                           _tableZeroEight = NULL;
                       }
                      _tableZeroEight = new CtiAnsiTableZeroEight( getApplicationLayer().getCurrentTable());
                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )//DEBUGLEVEL_LUDICROUS )
                      {
                          _tableZeroEight->printResult(getAnsiDeviceName());
                      }


                      if (_scanOperation == ANSI_SCAN_OPERATION::demandReset)
                      {
                          getApplicationLayer().setWriteSeqNbr( 0 );
                      }
                      else
                      {
                          _lpStartBlockIndex = _tableZeroEight->getLPOffset();
                          _lpNbrFullBlocks = _lpLastBlockIndex - _lpStartBlockIndex;
                          if( _tableSixThree != NULL && 
                              _lpStartBlockIndex >= 255)
                          {
                              _lpStartBlockIndex = 0;
                              _lpOffset = 0;
                              if( _tableSixThree->getNbrValidIntvls(1) < _tableSixThree->getNbrValidBlocks(1) )
                                  _lpNbrFullBlocks = _tableSixThree->getNbrValidBlocks(1) -1;
                              else
                                  _lpNbrFullBlocks = _tableSixThree->getNbrValidBlocks(1);
                          }
                          else
                              _lpOffset = _lpStartBlockIndex * _lpBlockSize;
                          
                      }

                   }
                   break;

                case 10:
                   {
                       if (_tableOneZero != NULL)
                       {
                           delete _tableOneZero;
                           _tableOneZero = NULL;
                       }
                      _tableOneZero = new CtiAnsiTableOneZero( getApplicationLayer().getCurrentTable() );
                     // _tableOneZero->printResult(getAnsiDeviceName());
                   }
                   break;

                case 11:
                   {
                       if (_tableOneOne != NULL)
                       {
                           delete _tableOneOne;
                           _tableOneOne = NULL;
                       }
                      _tableOneOne = new CtiAnsiTableOneOne( getApplicationLayer().getCurrentTable() );
                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )//DEBUGLEVEL_LUDICROUS )
                      {
                          _tableOneOne->printResult(getAnsiDeviceName());
                      }
                   }
                     break;

                case 12:
                   {
                       if (_tableOneTwo != NULL)
                       {
                           delete _tableOneTwo;
                           _tableOneTwo = NULL;
                       }
                      _tableOneTwo = new CtiAnsiTableOneTwo( getApplicationLayer().getCurrentTable(),
                                                             _tableOneOne->getNumberUOMEntries() );
                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )//DEBUGLEVEL_LUDICROUS )
                      {
                          _tableOneTwo->printResult(getAnsiDeviceName());
                      }
                   }
                   break;

                case 13:
                   {
                       if (_tableOneThree != NULL)
                       {
                           delete _tableOneThree;
                           _tableOneThree = NULL;
                       }
                      _tableOneThree = new CtiAnsiTableOneThree( getApplicationLayer().getCurrentTable(),
                                                                 _tableOneOne->getNumberDemandControlEntries(),
                                                                 _tableOneOne->getRawPFExcludeFlag(),
                                                                         _tableOneOne->getRawSlidingDemandFlag(),
                                                                         _tableOneOne->getRawResetExcludeFlag() );
                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                          _tableOneThree->printResult(getAnsiDeviceName());
                      }
                   }
                   break;

                case 14:
                   {
                       if (_tableOneFour != NULL)
                       {
                           delete _tableOneFour;
                           _tableOneFour = NULL;
                       }
                      _tableOneFour = new CtiAnsiTableOneFour( getApplicationLayer().getCurrentTable(),
                                                               _tableOneOne->getDataControlLength(),
                                                               _tableOneOne->getNumberDataControlEntries());
                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                          _tableOneFour->printResult(getAnsiDeviceName());
                      }
                   }
                   break;

                case 15:
                   {
                       if (_tableOneFive != NULL)
                       {
                           delete _tableOneFive;
                           _tableOneFive = NULL;
                       }
                      _tableOneFive = new CtiAnsiTableOneFive( getApplicationLayer().getCurrentTable(),
                                                               _tableOneOne->getRawConstantsSelector(),
                                                               _tableOneOne->getNumberConstantsEntries(),
                                                               _tableOneOne->getRawNoOffsetFlag(),
                                                               _tableOneOne->getRawSetOnePresentFlag(),
                                                               _tableOneOne->getRawSetTwoPresentFlag(),
                                                               _tableZeroZero->getRawNIFormat1(),
                                                               _tableZeroZero->getRawNIFormat2() );

                      getApplicationLayer().setLPDataMode( false, 0 );
                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                          _tableOneFive->printResult(getAnsiDeviceName());
                      }
                   }
                   break;

                case 16:
                   {
                       if (_tableOneSix != NULL)
                       {
                           delete _tableOneSix;
                           _tableOneSix = NULL;
                       }
                      _tableOneSix = new CtiAnsiTableOneSix( getApplicationLayer().getCurrentTable(),
                                                             _tableOneOne->getNumberSources() );
                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                          _tableOneSix->printResult(getAnsiDeviceName());
                      }
                   }
                   break;

                case 21:
                   {
                       if (_tableTwoOne != NULL)
                       {
                           delete _tableTwoOne;
                           _tableTwoOne = NULL;
                       }
                      _tableTwoOne = new CtiAnsiTableTwoOne( getApplicationLayer().getCurrentTable() );
                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                          _tableTwoOne->printResult(getAnsiDeviceName());
                      }
                   }
                   break;

                case 22:
                   {
                       if (_tableTwoTwo != NULL)
                       {
                           delete _tableTwoTwo;
                           _tableTwoTwo = NULL;
                       }
                      _tableTwoTwo = new CtiAnsiTableTwoTwo( getApplicationLayer().getCurrentTable(),
                                                             _tableTwoOne->getNumberSummations(),
                                                             _tableTwoOne->getNumberDemands(),
                                                             _tableTwoOne->getCoinValues() );
                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                          _tableTwoTwo->printResult(getAnsiDeviceName());
                      }
                   }
                   break;

                case 23:
                   {
                       if (_tableTwoThree != NULL)
                       {
                           delete _tableTwoThree;
                           _tableTwoThree = NULL;
                       }
                       _tableTwoThree = new CtiAnsiTableTwoThree( getApplicationLayer().getCurrentTable(),
                                                                 _tableTwoOne->getOccur(),
                                                                 _tableTwoOne->getNumberSummations(),
                                                                 _tableTwoOne->getNumberDemands(),
                                                                 _tableTwoOne->getCoinValues(),
                                                                 _tableTwoOne->getTiers(),
                                                                 _tableTwoOne->getDemandResetCtrFlag(),
                                                                 _tableTwoOne->getTimeDateFieldFlag(),
                                                                 _tableTwoOne->getCumDemandFlag(),
                                                                 _tableTwoOne->getContCumDemandFlag(),
                                                                 _tableZeroZero->getRawNIFormat1(),
                                                                 _tableZeroZero->getRawNIFormat2(),
                                                                 _tableZeroZero->getRawTimeFormat(),
                                                                  23 );

                       getApplicationLayer().setLPDataMode( false, 0 );
                       if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                       {
                           _tableTwoThree->printResult(getAnsiDeviceName());
                       }
                   }
                   break;
                case 25:
                    {
                        if (_frozenRegTable != NULL)
                        {
                           delete _frozenRegTable;
                           _frozenRegTable = NULL;
                        }

                        /*if (_tableTwoOne->getTimeDateFieldFlag())
                        {
                            memcpy( (void *)&_frozenEndDateTime, getApplicationLayer().getCurrentTable(), sizeOfSTimeDate());
                            getApplicationLayer().getCurrentTable() += sizeOfSTimeDate();
                        }
                        if (_tableTwoOne->getSeasonInfoFieldFlag())
                        {
                            memcpy( (void *)&_frozenSeason, getApplicationLayer().getCurrentTable(), sizeof(unsigned char));
                            getApplicationLayer().getCurrentTable() += sizeof (unsigned char);
                        }  */
                        _frozenRegTable = new CtiAnsiTableTwoFive (  getApplicationLayer().getCurrentTable(),
                                                                          _tableTwoOne->getOccur(),
                                                                          _tableTwoOne->getNumberSummations(),
                                                                          _tableTwoOne->getNumberDemands(),
                                                                          _tableTwoOne->getCoinValues(),
                                                                          _tableTwoOne->getTiers(),
                                                                          _tableTwoOne->getDemandResetCtrFlag(),
                                                                          _tableTwoOne->getTimeDateFieldFlag(),
                                                                          _tableTwoOne->getCumDemandFlag(),
                                                                          _tableTwoOne->getContCumDemandFlag(),
                                                                          _tableZeroZero->getRawNIFormat1(),
                                                                          _tableZeroZero->getRawNIFormat2(),
                                                                          _tableZeroZero->getRawTimeFormat(),
                                                                          _tableTwoOne->getSeasonInfoFieldFlag() );
                        getApplicationLayer().setLPDataMode( false, 0 );
                        if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                        {
                            _frozenRegTable->printResult(getAnsiDeviceName());
                        }
                    }
                    break;
                case 27:
                   {
                       if (_tableTwoSeven != NULL)
                       {
                           delete _tableTwoSeven;
                           _tableTwoSeven = NULL;
                       }
                       _tableTwoSeven = new CtiAnsiTableTwoSeven( getApplicationLayer().getCurrentTable(),
                                                                 _tableTwoOne->getNbrPresentDemands(),
                                                                 _tableTwoOne->getNbrPresentValues());
                       if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                           _tableTwoSeven->printResult(getAnsiDeviceName());
                      }
                   }
                   break;
                case 28:
                   {
                       if (_tableTwoEight != NULL)
                       {
                           delete _tableTwoEight;
                           _tableTwoEight = NULL;
                       }
                       _tableTwoEight = new CtiAnsiTableTwoEight( getApplicationLayer().getCurrentTable(),
                                                                 _tableTwoOne->getNbrPresentDemands(),
                                                                 _tableTwoOne->getNbrPresentValues(),
                                                                 _tableTwoOne->getTimeRemainingFlag(),
                                                                 _tableZeroZero->getRawNIFormat1(),
                                                                 _tableZeroZero->getRawNIFormat2(),
                                                                 _tableZeroZero->getRawTimeFormat() );

                       if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                           _tableTwoEight->printResult(getAnsiDeviceName());
                      }
                   }
                   break;
                   case 31:
                   {
                       if (_tableThreeOne != NULL)
                       {
                           delete _tableThreeOne;
                           _tableThreeOne = NULL;
                       }
                       _tableThreeOne = new CtiAnsiTableThreeOne( getApplicationLayer().getCurrentTable());

                       if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                            _tableThreeOne->printResult(getAnsiDeviceName());
                      }
                   }
                   break;
                   case 32:
                   {
                       if (_tableThreeTwo != NULL)
                       {
                           delete _tableThreeTwo;
                           _tableThreeTwo = NULL;
                       }
                       _tableThreeTwo = new CtiAnsiTableThreeTwo( getApplicationLayer().getCurrentTable(),
                                                                 _tableThreeOne->getNbrDispSources(),
                                                                  _tableThreeOne->getWidthDispSources() );

                       if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                           _tableThreeTwo->printResult(getAnsiDeviceName());
                      }
                   }
                   break;
                   case 33:
                   {
                       if (_tableThreeThree != NULL)
                       {
                           delete _tableThreeThree;
                           _tableThreeThree = NULL;
                       }
                       _tableThreeThree = new CtiAnsiTableThreeThree( getApplicationLayer().getCurrentTable(),
                                                                  _tableThreeOne->getNbrPriDispLists(),
                                                                      _tableThreeOne->getNbrPriDispListItems());

                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {

                          _tableThreeThree->printResult(getAnsiDeviceName());
                      }
                   }
                   break;
                case 51:
                   {
                       if (_tableFiveOne != NULL)
                       {
                           delete _tableFiveOne;
                           _tableFiveOne = NULL;
                       }
                      _tableFiveOne = new CtiAnsiTableFiveOne( getApplicationLayer().getCurrentTable() );
                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                          _tableFiveOne->printResult(getAnsiDeviceName());
                      }
                   }
                   break;
                case 52:
                   {
                       if (_tableFiveTwo != NULL)
                       {
                           delete _tableFiveTwo;
                           _tableFiveTwo = NULL;
                       }

                      _tableFiveTwo = new CtiAnsiTableFiveTwo( getApplicationLayer().getCurrentTable(), _tableZeroZero->getRawTimeFormat() );
                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                          _tableFiveTwo->printResult(getAnsiDeviceName());
                      }
                   }
                   break;

                /*case 55:
                   {
                      _tableFiveFive = new CtiAnsiTableFiveFive( getApplicationLayer().getCurrentTable() );
                  if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )//DEBUGLEVEL_LUDICROUS )
                      {
                          _tableFiveFive->printResult(getAnsiDeviceName());
                      }
                   }

                   break; */
                case 61:
                   {
                       if (_tableSixOne != NULL)
                       {
                           delete _tableSixOne;
                           _tableSixOne = NULL;
                       }

                      _tableSixOne = new CtiAnsiTableSixOne( getApplicationLayer().getCurrentTable(), _tableZeroZero->getStdTblsUsed(), _tableZeroZero->getDimStdTblsUsed() );
                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                          _tableSixOne->printResult(getAnsiDeviceName());
                      }

                      _lpNbrLoadProfileChannels = _tableSixOne->getNbrChansSet(1);

                   }

                   break;
                case 62:
                   {
                      if (_tableSixTwo != NULL)
                       {
                           delete _tableSixTwo;
                           _tableSixTwo = NULL;
                       }
                      _tableSixTwo = new CtiAnsiTableSixTwo( getApplicationLayer().getCurrentTable(), _tableSixOne->getLPDataSetUsedFlags(), _tableSixOne->getLPDataSetInfo(),
                                                             _tableSixOne->getLPScalarDivisorFlag(1), _tableSixOne->getLPScalarDivisorFlag(2), _tableSixOne->getLPScalarDivisorFlag(3),
                                                             _tableSixOne->getLPScalarDivisorFlag(4),  _tableZeroZero->getRawStdRevisionNo() );
                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                          _tableSixTwo->printResult(getAnsiDeviceName());
                      }
                   }

                   break;
               case 63:
                   {
                      if (_tableSixThree != NULL)
                       {
                           delete _tableSixThree;
                           _tableSixThree = NULL;
                       }
                      _tableSixThree = new CtiAnsiTableSixThree( getApplicationLayer().getCurrentTable(), _tableSixOne->getLPDataSetUsedFlags());
                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                          _tableSixThree->printResult(getAnsiDeviceName());
                      }
                   }
                   break;
                case 64:
                {
                    if (_tableSixFour != NULL)
                    {
                        delete _tableSixFour;
                        _tableSixFour = NULL;
                    }

                    UINT16 validIntvls = _tableSixThree->getNbrValidIntvls(1);
                    if (getApplicationLayer().getPartialProcessLPDataFlag())
                    {

                        _lpNbrFullBlocks = (getApplicationLayer().getLPByteCount() % _lpBlockSize) - 1;
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " ####### LP Byte Count = " <<getApplicationLayer().getLPByteCount() << endl;
                            dout << CtiTime() << " ####### LP Block Size = " <<_lpBlockSize << endl;
                        }

                        _lpNbrFullBlocks = (getApplicationLayer().getLPByteCount() / _lpBlockSize) - 1;
                        {
                            CtiLockGuard<CtiLogger> logger_guard(dout);
                            dout << CtiTime() << " ####### LP Nbr Full Blocks = " <<_lpNbrFullBlocks << endl;
                        }
                        _lpNbrIntvlsLastBlock = 0;//_tableSixOne->getNbrBlkIntsSet(1);
                        validIntvls = _lpNbrIntvlsLastBlock;
                        getApplicationLayer().setPartialProcessLPDataFlag(false);
                        if (_lpNbrFullBlocks > 0)
                            _forceProcessDispatchMsg = true;
                    }

                    int meterHour = 0;
                    if (_tableFiveTwo != NULL)
                    {
                        meterHour = _tableFiveTwo->getClkCldrHour();
                    }

                    _tableSixFour = new CtiAnsiTableSixFour( getApplicationLayer().getCurrentTable(), _lpNbrFullBlocks + 1,
                                                           _tableSixOne->getNbrChansSet(1), _tableSixOne->getClosureStatusFlag(),
                                                           _tableSixOne->getSimpleIntStatusFlag(), _tableSixOne->getNbrBlkIntsSet(1),
                                                           _tableSixOne->getBlkEndReadFlag(), _tableSixOne->getBlkEndPulseFlag(),
                                                           _tableSixOne->getExtendedIntStatusFlag(), _tableSixOne->getMaxIntTimeSet(1),
                                                           _tableSixTwo->getIntervalFmtCde(1), validIntvls,
                                                           _tableZeroZero->getRawNIFormat1(), _tableZeroZero->getRawNIFormat2(),
                                                           _tableZeroZero->getRawTimeFormat(), meterHour );

                    getApplicationLayer().setLPDataMode( false, 0 );

                    if (_invalidLastLoadProfileTime)
                    {
                        _header->lastLoadProfileTime = _tableSixFour->getLPDemandTime(0,0);
                    }
                }

                   break;
                default:
                    break;
                }
            }
            else
            {

            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return;
}

// only used for standard tables possibly larger than one data packet size
void CtiProtocolANSI::updateBytesExpected( )
{
    try
    {
        // if its manufactured, send it to the child class
        if (_tables[_index].type == ANSI_TABLE_TYPE_MANUFACTURER)
        {
            if (isMfgTableAvailableInMeter((_tables[_index].tableID) - 0x0800))
            {
                _currentTableNotAvailableFlag = false;
                switch( (_tables[_index].tableID - 0x800) )
                {
                    case 0:
                    {
                        _tables[_index].bytesExpected = 59;
                        break;
                    }
                /*case 1:
                {
                    _tables[_index].bytesExpected = 59;
                    break;
                } */
                case 2:
                {
                    _tables[_index].bytesExpected = 20;
                    break;
                }
                    case 70:
                    {
                        _tables[_index].bytesExpected = 46;
                        break;
                    }
                    case 110:
                    {
                        _tables[_index].bytesExpected = 166;
                        break;
                    }

                default:
                        break;
                }

            }
            else
            {

            }
    //        convertToManufacturerTable (getApplicationLayer().getCurrentTable(),
    //                                    _tables[_index].bytesExpected,
    //                                    _tables[_index].tableID);
        }
        else
        {
            if(isStdTableAvailableInMeter(_tables[_index].tableID))
            {

                _currentTableNotAvailableFlag = false;

                switch( _tables[_index].tableID )
                {
                   case 0:
                   {
                       _tables[_index].bytesExpected = 30;
                   }
                   break;
                   case 1:
                   {
                       _tables[_index].bytesExpected = 24;
                   }
                   break;
                   case 8:
                   {
                       _tables[_index].bytesExpected = 5;
                   }
                   break;
                   case 11:
                   {
                       _tables[_index].bytesExpected = 8;
                   }
                   break;
                   case 12:
                   {
                       _tables[_index].bytesExpected = 4 * _tableOneOne->getNumberUOMEntries();
                   }
                   break;

                case 13:
                   {
                       _tables[_index].bytesExpected = 0;
                       if (_tableOneOne->getRawResetExcludeFlag())
                       {
                           _tables[_index].bytesExpected += 1;
                       }
                       if (_tableOneOne->getRawPFExcludeFlag())
                       {
                           _tables[_index].bytesExpected += 3;
                       }
                       // add array values
                       _tables[_index].bytesExpected += 2 * _tableOneOne->getNumberDemandControlEntries();
                   }
                   break;

                case 14:
                    {
                       _tables[_index].bytesExpected = _tableOneOne->getDataControlLength() *
                                                       _tableOneOne->getNumberDataControlEntries();
                    }
                   break;

                case 15:
                   {
                       // NOTE: worrying about electrical only
                       //MULTIPLIER
                       _tables[_index].bytesExpected = sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat1());
                       //OFFSET
                       if (!_tableOneOne->getRawNoOffsetFlag())
                       {
                           _tables[_index].bytesExpected += sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat1());
                       }
                       //SET1_CONSTANTS
                       if (_tableOneOne->getRawSetOnePresentFlag())
                       {
                            _tables[_index].bytesExpected += 1;
                            _tables[_index].bytesExpected += ( 2 * sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat1()));
                       }
                       //SET2_CONSTANTS
                       if (_tableOneOne->getRawSetTwoPresentFlag())
                       {
                            _tables[_index].bytesExpected += 1;
                            _tables[_index].bytesExpected += ( 2 * sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat1())) ;
                       }
                       _tables[_index].bytesExpected *= _tableOneOne->getNumberConstantsEntries();
                   }
                   break;

                case 16:
                   {
                        _tables[_index].bytesExpected = _tableOneOne->getNumberSources();
                   }
                   break;
                case 20:
                case 21:
                   {
                       _tables[_index].bytesExpected = 10;
                   }
                   break;
                case 22:
                   {
                       _tables[_index].bytesExpected = (_tableTwoOne->getNumberSummations() +
                                                        _tableTwoOne->getNumberDemands() +
                                                        (2 * _tableTwoOne->getCoinValues()) +
                                                        ((int)_tableTwoOne->getNumberDemands() + 7)/ 8);
                   }
                   break;

                case 23:
                   {
                       // get the size of a demands record first
                       int demandsRecSize = 0;
                       if (_tableTwoOne->getTimeDateFieldFlag())
                       {
                           demandsRecSize += (_tableTwoOne->getOccur() * sizeOfSTimeDate());
                       }

                       if (_tableTwoOne->getCumDemandFlag())
                       {
                           demandsRecSize += sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat1());
                       }

                       if (_tableTwoOne->getContCumDemandFlag())
                       {
                           demandsRecSize += sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat1());
                       }

                       demandsRecSize += (_tableTwoOne->getOccur()  *
                                          sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat2()));

                       int coinRecSize = (_tableTwoOne->getOccur()  *
                                          sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat2()));

                       _tables[_index].bytesExpected = _tableTwoOne->getNumberSummations() *
                                                       sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat1());
                       _tables[_index].bytesExpected += _tableTwoOne->getNumberDemands() * demandsRecSize;
                       _tables[_index].bytesExpected += _tableTwoOne->getCoinValues() * coinRecSize;


                       _tables[_index].bytesExpected += (_tableTwoOne->getTiers() * _tables[_index].bytesExpected);


                        if (_tableTwoOne->getDemandResetCtrFlag())
                        {
                             _tables[_index].bytesExpected += 1;
                        }
                   }
                   break;
                case 25:
                    {
                        // get the size of a demands record first
                       int demandsRecSize = 0;
                       int registerInfoSize = 0;
                       if (_tableTwoOne->getTimeDateFieldFlag())
                       {
                           demandsRecSize += (_tableTwoOne->getOccur() * sizeOfSTimeDate());
                           registerInfoSize += sizeOfSTimeDate();
                       }
                       if (_tableTwoOne->getSeasonInfoFieldFlag())
                       {
                           registerInfoSize += 1;
                       }
                       if (_tableTwoOne->getCumDemandFlag())
                       {
                           demandsRecSize += sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat1());
                       }

                       if (_tableTwoOne->getContCumDemandFlag())
                       {
                           demandsRecSize += sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat1());
                       }

                       demandsRecSize += (_tableTwoOne->getOccur()  *
                                          sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat2()));

                       int coinRecSize = (_tableTwoOne->getOccur()  *
                                          sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat2()));

                       _tables[_index].bytesExpected = _tableTwoOne->getNumberSummations() *
                                                       sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat1());
                       _tables[_index].bytesExpected += _tableTwoOne->getNumberDemands() * demandsRecSize;
                       _tables[_index].bytesExpected += _tableTwoOne->getCoinValues() * coinRecSize;


                       _tables[_index].bytesExpected += (_tableTwoOne->getTiers() * _tables[_index].bytesExpected);


                        if (_tableTwoOne->getDemandResetCtrFlag())
                        {
                             _tables[_index].bytesExpected += 1;
                        }
                        _tables[_index].bytesExpected += registerInfoSize;

                    }
                    break;
                case 27:
                   {
                       _tables[_index].bytesExpected += (_tableTwoOne->getNbrPresentDemands() +
                                                         _tableTwoOne->getNbrPresentValues());
                   }
                   break;
                case 28:
                    {
                        _tables[_index].bytesExpected += ( _tableTwoOne->getNbrPresentDemands() * ( sizeOfNonIntegerFormat(_tableZeroZero->getRawNIFormat2()) + 4 ))
                                                          + ( _tableTwoOne->getNbrPresentValues() * sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat1()) );

                    }
                    break;
                case 31:
                    {
                         _tables[_index].bytesExpected = 10;
                    }
                    break;
                case 32:
                    {
                         _tables[_index].bytesExpected += (_tableThreeOne->getNbrDispSources() * ( _tableThreeOne->getWidthDispSources() * 1));
                    }
                    break;
                case 33:
                    {
                        _tables[_index].bytesExpected += ((_tableThreeOne->getNbrPriDispLists() * 3) + (_tableThreeOne->getNbrPriDispListItems() * 2));
                    }
                    break;
                case 34:
                    {
                        _tables[_index].bytesExpected += ((_tableThreeOne->getNbrSecDispLists() * 3) + (_tableThreeOne->getNbrSecDispListItems() * 2));
                    }
                    break;
                case 51:
                    {
                        _tables[_index].bytesExpected = 9;
                    }
                    break;
                case 52:
                    {     _tables[_index].bytesExpected = 7;
                        // _tables[_index].bytesExpected = sizeof (LTIME_DATE) + 1; //LTIME_DATE + TIME_DATE_QUAL_BFLD
                    }
                    break;
                case 61:
                    {
                       // if (useScanFlags())
                        {
                            _tables[_index].bytesExpected = 7; //LP_MEMORY_LEN + LP_FLAGS + LP_FMATS

                            int lpTbl[] = {64, 65, 66, 67};
                            unsigned char *stdTblsUsed = _tableZeroZero->getStdTblsUsed();

                            if (_tableZeroZero->getDimStdTblsUsed() > 8)
                            {
                                int x, y, yy;
                                x = 0;
                                while (x < 4)
                                {
                                    y = 1;
                                    for (yy = 0; yy < lpTbl[x]%8; yy++)
                                    {
                                        y = y*2;
                                    }
                                    if (stdTblsUsed[(lpTbl[x]/8)] & y)
                                    {
                                        _tables[_index].bytesExpected += 6;   //6 bytes per data set used
                                    }
                                    x++;
                                }
                            }
                        }
                       // else
                        {
                      //      _currentTableNotAvailableFlag = true;
                        }
                    }
                    break;
                case 62:
                    {
                       // if (useScanFlags())
                        {
                            bool * dataSetUsedFlags = _tableSixOne->getLPDataSetUsedFlags();
                            LP_DATA_SET *lp_data_set_info = _tableSixOne->getLPDataSetInfo();
                            _tables[_index].bytesExpected = 0;

                            for (int x = 0; x < 4; x++)
                            {
                                if (dataSetUsedFlags[x])
                                {
                                    _tables[_index].bytesExpected += (lp_data_set_info[x].nbr_chns_set * 3);
                                    _tables[_index].bytesExpected += 1;
                                    if (_tableSixOne->getLPScalarDivisorFlag(x+1))
                                    {
                                        //Scalers Set and Divisors Set
                                        _tables[_index].bytesExpected += 2 * (lp_data_set_info[x].nbr_chns_set * 2);
                                    }
                                }
                            }
                        }
                       // else
                        {
                       //     _currentTableNotAvailableFlag = true;
                        }
                    }
                    break;
                case 63:
                    {
                        bool * dataSetUsedFlags = _tableSixOne->getLPDataSetUsedFlags();
                        _tables[_index].bytesExpected = 0;

                        for (int x = 0; x < 4; x++)
                        {
                            if (dataSetUsedFlags[x])
                            {
                                _tables[_index].bytesExpected += 13;
                            }
                        }

                    }
                    break;
                case 64:
                    {
                        _tables[_index].bytesExpected = (_lpNbrFullBlocks * _lpBlockSize) + _lpLastBlockSize;

                    }
                    break;
                 }
            }
            else
            {
                _currentTableNotAvailableFlag = true;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout <<CtiTime() <<  "  **Table " << _tables[_index].tableID << " NOT present in meter -- 0 bytes expected" << endl;
                }

            }

        }
        if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )
        {
           CtiLockGuard<CtiLogger> doubt_guard(dout);
           dout << CtiTime() << "  **Table " << _tables[_index].tableID << " expected bytes " << (int)_tables[_index].bytesExpected << endl;
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

}

int CtiProtocolANSI::sizeOfNonIntegerFormat( int aFormat )
{
    int retVal=0;

   switch( aFormat )
   {
       case ANSI_NI_FORMAT_FLOAT64:
           retVal = 8;
           break;
       case ANSI_NI_FORMAT_FLOAT32:
           retVal = 4;
            break;
       case ANSI_NI_FORMAT_ARRAY12_CHAR:
           retVal = 12;
           break;
       case ANSI_NI_FORMAT_ARRAY6_CHAR:
           retVal = 6;
           break;
       case ANSI_NI_FORMAT_INT32_IMPLIED:
           retVal = 4;
           break;
       case ANSI_NI_FORMAT_ARRAY6_BCD:
           retVal = 6;
           break;
       case ANSI_NI_FORMAT_ARRAY4_BCD:
           retVal = 4;
           break;
       case ANSI_NI_FORMAT_INT24:
           retVal = 3;
           break;
       case ANSI_NI_FORMAT_INT32:
           retVal = 4;
           break;
       case ANSI_NI_FORMAT_INT40:
           retVal = 5;
           break;
       case ANSI_NI_FORMAT_INT48:
           retVal = 6;
           break;
       case ANSI_NI_FORMAT_INT64:
           retVal = 8;
           break;
       case ANSI_NI_FORMAT_ARRAY8_BCD:
           retVal = 8;
           break;
       case ANSI_NI_FORMAT_ARRAY21_CHAR:
           retVal = 21;
           break;
   }
   return( retVal );
}

int CtiProtocolANSI::sizeOfSTimeDate( void )
{
    int retVal=0;
   if (_tableZeroZero != NULL)
   {
       switch (_tableZeroZero->getRawTimeFormat())
       {
           case 1/*CASE1*/:
           {
               retVal = 3;
               break;
           }
           case 2/*CASE2*/:
           {
               retVal = 5;
               break;
           }
           case 3/*CASE3*/:
           case 4/*CASE4*/:
           {
               retVal = 4;
               break;
           }
           default:
               break;
       }
   }
   return retVal;
}
int CtiProtocolANSI::sizeOfLTimeDate( void )
{
    int retVal=0;
   if (_tableZeroZero != NULL)
   {
       switch (_tableZeroZero->getRawTimeFormat())
       {
           case 1/*CASE1*/:
           {
               retVal = 3;
               break;
           }
           case 2/*CASE2*/:
           {
               retVal = 6;
               break;
           }
           case 3/*CASE3*/:
           {
               retVal = 5;
               break;
           }
           case 4/*CASE4*/:
           {
               retVal = 4;
               break;
           }
           default:
               break;
       }
   }
   return retVal;
}


//=========================================================================================================================================
//=========================================================================================================================================

bool CtiProtocolANSI::isTransactionComplete( void ) const
{
    // trying to decide if we are done with our attempts
   return _appLayer.isReadComplete() || _appLayer.isReadFailed();
}

bool CtiProtocolANSI::isTransactionFailed( void )
{
    // trying to decide if we are done with our attempts
   return (getApplicationLayer().isReadFailed());
}


int CtiProtocolANSI::sendCommResult( INMESS *InMessage  )
{
    int ret = NORMAL;

    // if the read failed, don't do any of this and tell the device it
    if (getApplicationLayer().isReadFailed())
    {
        ret=!NORMAL;
    }
    else
    {
        // check if we were successful
       // BYTE *ptr = InMessage->Buffer.InMessage;
       /* unsigned long *lastLPTime;
        lastLPTime = 0;
        *lastLPTime =  getlastLoadProfileTime();
        */
        unsigned long lastLPTime = getlastLoadProfileTime();
        memcpy( InMessage->Buffer.InMessage, (void *)&lastLPTime, sizeof (unsigned long) );
        InMessage->InLength = sizeof (unsigned long);
        InMessage->EventCode = NORMAL;

        unsigned long *temp;
        temp = (unsigned long *)InMessage->Buffer.InMessage;
    }
    return ret;
}

void CtiProtocolANSI::receiveCommResult( INMESS *InMessage )
{
    BYTE *ptr = InMessage->Buffer.InMessage;
    unsigned long *lastLpTime;
    lastLpTime = (unsigned long *)ptr;

    bool success = true;
    // if its manufactured, send it to the child class
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << CtiTime::now() << " ==============================================" << endl;
        dout << CtiTime::now() << " ==========The KV2 responded with data=========" << endl;
        dout << CtiTime::now() << " ==============================================" << endl;
    }


    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << CtiTime::now() << " ==============================================" << endl;
        dout << CtiTime::now() << " ================= Complete ===================" << endl;
        dout << CtiTime::now() << " ==============================================" << endl;
    }

}

////////////////////////////////////////////////////////////////////////////////////
// Demand - KW, KVAR, KVA, etc...
////////////////////////////////////////////////////////////////////////////////////
bool CtiProtocolANSI::retreiveDemand( int offset, double *value, double *time )
{
    try
    {

        bool success = false;
        unsigned char * demandSelect;
        int ansiOffset;
        int ansiTOURate;
        int ansiDeviceType = (int) getApplicationLayer().getAnsiDeviceType();

        ansiOffset = getUnitsOffsetMapping(offset);
        ansiTOURate = getRateOffsetMapping(offset);

        demandSelect = _tableTwoTwo->getDemandSelect();
        for (int x = 0; x < _tableTwoOne->getNumberDemands(); x++)
        {
            if ((int) demandSelect[x] != 255)
            {
                if (_tableOneTwo->getRawTimeBase(demandSelect[x]) == 4 &&
                    _tableOneTwo->getRawIDCode(demandSelect[x]) == ansiOffset )
                {
                    success = true;
                    if (_tableOneSix->getDemandCtrlFlag(demandSelect[x]) )
                    {
                        if (_tableOneFive != NULL)
                        {
                            if(ansiDeviceType != 2)
                            {
                                *value = ((_tableTwoThree->getDemandValue(x, ansiTOURate) *
                                   _tableOneFive->getElecMultiplier((demandSelect[x]%20))) / 1000000000);
                                *time = _tableTwoThree->getDemandEventTime( x, ansiTOURate );
                                if (_tableFiveTwo != NULL)
                                {
                                    if (_tableFiveTwo->adjustTimeForDST())
                                    {
                                        *time -= 3600;
                                    }
                                }
                            }
                            else  // 2 = sentinel
                            {
                                // will bring back value in KW/KVAR ...
                                *value = (_tableTwoThree->getDemandValue(x, ansiTOURate) *
                                           _tableOneFive->getElecMultiplier((demandSelect[x]%20)) /
                                          _tableOneTwo->getResolvedMultiplier(demandSelect[x])) / 1000;
                                *time = _tableTwoThree->getDemandEventTime( x, ansiTOURate );
                                if (_tableFiveTwo != NULL)
                                {
                                    if (_tableFiveTwo->adjustTimeForDST())
                                    {
                                        *time -= 3600;
                                    }
                                }
                            }
                        }
                        else
                        {
                            if(ansiDeviceType != 2)
                            {
                                *value = (_tableTwoThree->getDemandValue(x, ansiTOURate)  / 1000000000);
                                *time = _tableTwoThree->getDemandEventTime( x, ansiTOURate );
                                if (_tableFiveTwo != NULL)
                                {
                                    if (_tableFiveTwo->adjustTimeForDST())
                                    {
                                        *time -= 3600;
                                    }
                                }
                            }
                            else  // 2 = sentinel
                            {
                                // will bring back value in KW/KVAR ...
                                 *value = (_tableTwoThree->getDemandValue(x, ansiTOURate) /
                                           _tableOneTwo->getResolvedMultiplier(demandSelect[x])) / 1000;
                                 *time = _tableTwoThree->getDemandEventTime( x, ansiTOURate );
                                 if (_tableFiveTwo != NULL)
                                 {
                                     if (_tableFiveTwo->adjustTimeForDST())
                                     {
                                         *time -= 3600;
                                     }
                                 }
                            }

                        }
                        if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )//DEBUGLEVEL_LUDICROUS )
                        {
                            CtiLockGuard< CtiLogger > doubt_guard( dout );
                            dout << " *value =   "<<*value<<endl;
                        }
                    }
                    else
                    {
                        if(ansiDeviceType != 2)
                        {
                            *value = _tableTwoThree->getDemandValue(x, ansiTOURate);
                            *time = _tableTwoThree->getDemandEventTime( x, ansiTOURate );
                            if (_tableFiveTwo != NULL)
                            {
                                if (_tableFiveTwo->adjustTimeForDST())
                                {
                                    *time -= 3600;
                                }
                            }
                        }
                        else  // 2 = sentinel
                        {
                            // will bring back value in KW/KVAR ...
                            *value = (_tableTwoThree->getDemandValue(x, ansiTOURate) /
                                       _tableOneTwo->getResolvedMultiplier(demandSelect[x]))/1000;
                            *time = _tableTwoThree->getDemandEventTime( x, ansiTOURate );
                            if (_tableFiveTwo != NULL)
                            {
                                if (_tableFiveTwo->adjustTimeForDST() )
                                {
                                    *time -= 3600;
                                }
                            }
                        }
                    }
                    break;
                }

            }
        }
        demandSelect = NULL;

        return success;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return false;
}
////////////////////////////////////////////////////////////////////////////////////
// Summations = Energy - KWH, KVARH, KVAH, etc...
////////////////////////////////////////////////////////////////////////////////////
bool CtiProtocolANSI::retreiveSummation( int offset, double *value )
{
    try
    {
        bool success = false;
        unsigned char* summationSelect;
        int ansiOffset;
        int ansiTOURate;
        int ansiDeviceType = (int) getApplicationLayer().getAnsiDeviceType();

        /* Watts = 0, Vars = 1, VA = 2, etc */
        ansiOffset = getUnitsOffsetMapping(offset);
        ansiTOURate = getRateOffsetMapping(offset);


        if (_tableTwoTwo != NULL)
        {
            /* returns pointer to list of summation Selects */
            summationSelect = _tableTwoTwo->getSummationSelect();

            if (_tableTwoOne != NULL)
            {
                for (int x = 0; x < _tableTwoOne->getNumberSummations(); x++)
                {
                    if ((int) summationSelect[x] != 255)
                    {
                        if (_tableOneTwo != NULL)
                        {
                            if (_tableOneTwo->getRawTimeBase(summationSelect[x]) == 0 &&
                                _tableOneTwo->getRawIDCode(summationSelect[x]) == ansiOffset)
                            {
                                if (_tableOneSix != NULL  && _tableTwoThree != NULL)
                                {
                                    if (_tableOneSix->getConstantsFlag(summationSelect[x]) &&
                                        !_tableOneSix->getConstToBeAppliedFlag(summationSelect[x]))
                                    {
                                        if (_tableOneFive != NULL)
                                        {
                                            if(ansiDeviceType != 2)
                                            {
                                                *value = ((_tableTwoThree->getSummationsValue(x, ansiTOURate) *
                                                   _tableOneFive->getElecMultiplier(summationSelect[x])) / 1000000000);
                                            }
                                            else //sentinel = 2
                                            {
                                                // will bring back value in KWH/KVARH ...
                                                 *value = ((_tableTwoThree->getSummationsValue(x, ansiTOURate) *
                                                   _tableOneFive->getElecMultiplier(summationSelect[x])) /
                                                           _tableOneTwo->getResolvedMultiplier(summationSelect[x])) / 1000;
                                            }
                                        }
                                        else
                                        {
                                            if(ansiDeviceType != 2)
                                            {
                                                *value = (_tableTwoThree->getSummationsValue(x, ansiTOURate) / 1000000000);
                                            }
                                            else  // 2 = sentinel
                                            {
                                                // will bring back value in KWH/KVARH ...
                                                 *value = (_tableTwoThree->getSummationsValue(x, ansiTOURate) /
                                                           _tableOneTwo->getResolvedMultiplier(summationSelect[x])) / 1000;
                                            }
                                        }
                                        success = true;
                                        if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )//DEBUGLEVEL_LUDICROUS )
                                        {
                                            CtiLockGuard< CtiLogger > doubt_guard( dout );
                                            dout << " *value =   "<<*value<<endl;
                                        }
                                    }
                                    else
                                    {
                                        if(ansiDeviceType != 2)
                                        {
                                            *value = _tableTwoThree->getSummationsValue(x, ansiTOURate);
                                        }
                                        else  // 2 = sentinel
                                        {
                                            // will bring back value in KW/KVAR ...
                                            *value = (_tableTwoThree->getSummationsValue(x, ansiTOURate) /
                                                       _tableOneTwo->getResolvedMultiplier(summationSelect[x]))/1000;
                                        }
                                        success = true;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        summationSelect = NULL;
        return success;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return false;
}

////////////////////////////////////////////////////////////////////////////////////
// Demand - KW, KVAR, KVA, etc...
////////////////////////////////////////////////////////////////////////////////////
bool CtiProtocolANSI::retreiveFrozenDemand( int offset, double *value, double *time )
{
    bool success = false;

    try
    {
        unsigned char * demandSelect;
        int ansiOffset;
        int ansiTOURate;
        int ansiDeviceType = (int) getApplicationLayer().getAnsiDeviceType();

        ansiOffset = getUnitsOffsetMapping(offset);
        ansiTOURate = getRateOffsetMapping(offset);

        demandSelect = _tableTwoTwo->getDemandSelect();
        for (int x = 0; x < _tableTwoOne->getNumberDemands(); x++)
        {
            if ((int) demandSelect[x] != 255)
            {
                if (_tableOneTwo->getRawTimeBase(demandSelect[x]) == 4 &&
                    _tableOneTwo->getRawIDCode(demandSelect[x]) == ansiOffset)
                {
                    success = true;
                    if (_tableOneSix->getDemandCtrlFlag(demandSelect[x]) )
                    {
                        if (_tableOneFive != NULL)
                        {
                            if(ansiDeviceType != 2)
                            {
                                *value = ((_frozenRegTable->getDemandResetDataTable()->getDemandValue(x, ansiTOURate) *
                                   _tableOneFive->getElecMultiplier((demandSelect[x]%20))) / 1000000000);
                                *time = _frozenRegTable->getDemandResetDataTable()->getDemandEventTime( x, ansiTOURate );
                                if (_tableFiveTwo != NULL)
                                {
                                    if (_tableFiveTwo->adjustTimeForDST() )
                                    {
                                        *time -= 3600;
                                    }
                                }
                            }
                            else  // 2 = sentinel
                            {
                                // will bring back value in KW/KVAR ...
                                *value = (_frozenRegTable->getDemandResetDataTable()->getDemandValue(x, ansiTOURate) *
                                           _tableOneFive->getElecMultiplier((demandSelect[x]%20)) /
                                          _tableOneTwo->getResolvedMultiplier(demandSelect[x])) / 1000;
                                *time = _frozenRegTable->getDemandResetDataTable()->getDemandEventTime( x, ansiTOURate );
                                if (isTimeUninitialized(*time))
                                {
                                    *time = _frozenRegTable->getEndDateTime();
                                }
                                if (_tableFiveTwo != NULL)
                                {
                                    if (_tableFiveTwo->adjustTimeForDST() )
                                    {
                                        *time -= 3600;
                                    }
                                }
                            }
                        }
                        else
                        {
                            if(ansiDeviceType != 2)
                            {
                                *value = (_frozenRegTable->getDemandResetDataTable()->getDemandValue(x, ansiTOURate)  / 1000000000);
                                *time = _frozenRegTable->getDemandResetDataTable()->getDemandEventTime( x, ansiTOURate );
                                if (_tableFiveTwo != NULL)
                                {
                                    if (_tableFiveTwo->adjustTimeForDST() )
                                    {
                                        *time -= 3600;
                                    }
                                }
                            }
                            else  // 2 = sentinel
                            {
                                // will bring back value in KW/KVAR ...
                                 *value = (_frozenRegTable->getDemandResetDataTable()->getDemandValue(x, ansiTOURate) /
                                           _tableOneTwo->getResolvedMultiplier(demandSelect[x])) / 1000;
                                 *time = _frozenRegTable->getDemandResetDataTable()->getDemandEventTime( x, ansiTOURate );
                                 if (isTimeUninitialized(*time))
                                 {
                                     *time = _frozenRegTable->getEndDateTime();
                                 }
                                 if (_tableFiveTwo != NULL)
                                 {
                                     if (_tableFiveTwo->adjustTimeForDST() )
                                     {
                                         *time -= 3600;
                                     }
                                 }
                            }

                        }
                        if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )//DEBUGLEVEL_LUDICROUS )
                        {
                            CtiLockGuard< CtiLogger > doubt_guard( dout );
                            dout << " *value =   "<<*value<<endl;
                        }
                    }
                    else
                    {
                        if(ansiDeviceType != 2)
                        {
                            *value = _frozenRegTable->getDemandResetDataTable()->getDemandValue(x, ansiTOURate);
                            *time = _frozenRegTable->getDemandResetDataTable()->getDemandEventTime( x, ansiTOURate );
                            if (_tableFiveTwo != NULL)
                            {
                                if (_tableFiveTwo->adjustTimeForDST() )
                                {
                                    *time -= 3600;
                                }
                            }
                        }
                        else  // 2 = sentinel
                        {
                            // will bring back value in KW/KVAR ...
                            *value = (_frozenRegTable->getDemandResetDataTable()->getDemandValue(x, ansiTOURate) /
                                       _tableOneTwo->getResolvedMultiplier(demandSelect[x]))/1000;
                            *time = _frozenRegTable->getDemandResetDataTable()->getDemandEventTime( x, ansiTOURate );

                            if (isTimeUninitialized(*time))
                            {
                                *time = _frozenRegTable->getEndDateTime();
                            }
                            if (_tableFiveTwo != NULL)
                            {
                                if (_tableFiveTwo->adjustTimeForDST() )
                                {
                                    *time -= 3600;
                                }
                            }
                        }
                    }
                    break;
                }

            }
        }
        demandSelect = NULL;

    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return success;
}
////////////////////////////////////////////////////////////////////////////////////
// Summations = Energy - KWH, KVARH, KVAH, etc...
////////////////////////////////////////////////////////////////////////////////////
bool CtiProtocolANSI::retreiveFrozenSummation( int offset, double *value, double *time)
{
    bool success = false;
    unsigned char* summationSelect;
    int ansiOffset;
    int ansiTOURate;
    int ansiDeviceType = (int) getApplicationLayer().getAnsiDeviceType();

    /* Watts = 0, Vars = 1, VA = 2, etc */
    ansiOffset = getUnitsOffsetMapping(offset);
    ansiTOURate = getRateOffsetMapping(offset);

    try
    {
        if (_tableTwoTwo != NULL)
        {
            /* returns pointer to list of summation Selects */
            summationSelect = _tableTwoTwo->getSummationSelect();

            if (_tableTwoOne != NULL)
            {
                for (int x = 0; x < _tableTwoOne->getNumberSummations(); x++)
                {
                    if ((int) summationSelect[x] != 255)
                    {
                        if (_tableOneTwo != NULL)
                        {
                            if (_tableOneTwo->getRawTimeBase(summationSelect[x]) == 0 &&
                                _tableOneTwo->getRawIDCode(summationSelect[x]) == ansiOffset)
                            {
                                if (_tableOneSix != NULL  && _tableTwoThree != NULL)
                                {
                                    if (_tableOneSix->getConstantsFlag(summationSelect[x]) &&
                                        !_tableOneSix->getConstToBeAppliedFlag(summationSelect[x]))
                                    {
                                        if (_tableOneFive != NULL)
                                        {
                                            if(ansiDeviceType != 2)
                                            {
                                                *value = ((_frozenRegTable->getDemandResetDataTable()->getSummationsValue(x, ansiTOURate) *
                                                   _tableOneFive->getElecMultiplier(summationSelect[x])) / 1000000000);
                                                *time = _frozenRegTable->getEndDateTime();
                                            }
                                            else //sentinel = 2
                                            {
                                                // will bring back value in KWH/KVARH ...
                                                 *value = ((_frozenRegTable->getDemandResetDataTable()->getSummationsValue(x, ansiTOURate) *
                                                   _tableOneFive->getElecMultiplier(summationSelect[x])) /
                                                           _tableOneTwo->getResolvedMultiplier(summationSelect[x])) / 1000;
                                                 *time = _frozenRegTable->getEndDateTime();
                                            }
                                        }
                                        else
                                        {
                                            if(ansiDeviceType != 2)
                                            {
                                                *value = (_frozenRegTable->getDemandResetDataTable()->getSummationsValue(x, ansiTOURate) / 1000000000);
                                                *time = _frozenRegTable->getEndDateTime();
                                            }
                                            else  // 2 = sentinel
                                            {
                                                // will bring back value in KWH/KVARH ...
                                                 *value = (_frozenRegTable->getDemandResetDataTable()->getSummationsValue(x, ansiTOURate) /
                                                           _tableOneTwo->getResolvedMultiplier(summationSelect[x])) / 1000;
                                                 *time = _frozenRegTable->getEndDateTime();
                                            }
                                        }
                                        success = true;
                                        if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )//DEBUGLEVEL_LUDICROUS )
                                        {
                                            CtiLockGuard< CtiLogger > doubt_guard( dout );
                                            dout << " *value =   "<<*value<<endl;
                                        }
                                    }
                                    else
                                    {
                                        if(ansiDeviceType != 2)
                                        {
                                            *value = _frozenRegTable->getDemandResetDataTable()->getSummationsValue(x, ansiTOURate);
                                            *time = _frozenRegTable->getEndDateTime();
                                        }
                                        else  // 2 = sentinel
                                        {
                                            // will bring back value in KW/KVAR ...
                                            *value = (_frozenRegTable->getDemandResetDataTable()->getSummationsValue(x, ansiTOURate) /
                                                       _tableOneTwo->getResolvedMultiplier(summationSelect[x]))/1000;
                                            *time = _frozenRegTable->getEndDateTime();
                                        }
                                        success = true;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    summationSelect = NULL;
    return success;
}

////////////////////////////////////////////////////////////////////////////////////
// Present Values - volts, current, pf, etc...
////////////////////////////////////////////////////////////////////////////////////
bool CtiProtocolANSI::retreivePresentValue( int offset, double *value )
{
    bool success = false;
    unsigned char* presentValueSelect;
    int ansiOffset;
    int ansiDeviceType = (int) getApplicationLayer().getAnsiDeviceType();

    if (ansiDeviceType == 1) //if 1, kv2 gets info from mfg tbl 110
    {
        try
        {
            success = retreiveKV2PresentValue(offset, value);
            if (success)
            {
                if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
                {
                    CtiLockGuard< CtiLogger > doubt_guard( dout );
                    dout << " *value =   "<<*value<<endl;
                }
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
    else
    {
        /* Watts = 0, Vars = 1, VA = 2, Volts = 8, Current = 12, etc */
        ansiOffset = getUnitsOffsetMapping(offset);

        try
        {
            if (_tableTwoSeven != NULL)
            {
                /* returns pointer to list of present Value Selects */
                presentValueSelect = (unsigned char*)_tableTwoSeven->getValueSelect();

                if (_tableTwoOne != NULL && presentValueSelect != NULL)
                {
                    for (int x = 0; x < _tableTwoOne->getNbrPresentValues(); x++)
                    {
                        if ((int) presentValueSelect[x] != 255)
                        {
                            if (_tableOneTwo != NULL)
                            {
                                if (_tableOneTwo->getRawTimeBase(presentValueSelect[x]) == 1 &&
                                    _tableOneTwo->getSegmentation(presentValueSelect[x]) == getSegmentationOffsetMapping(offset) &&
                                    _tableOneTwo->getRawIDCode(presentValueSelect[x]) == ansiOffset)
                                {
                                    if (_tableOneSix != NULL  && _tableTwoEight != NULL)
                                    {
                                        if (_tableOneSix->getConstantsFlag(presentValueSelect[x]) &&
                                            !_tableOneSix->getConstToBeAppliedFlag(presentValueSelect[x]))
                                        {
                                            if (_tableOneFive != NULL)
                                            {
                                                *value = ((_tableTwoEight->getPresentValue(x) *
                                                       _tableOneFive->getElecMultiplier(presentValueSelect[x])) /*/ 1000000000*/);
                                            }
                                            else
                                            {
                                                *value = (_tableTwoEight->getPresentValue(x) /*/ 1000000000*/);
                                            }
                                            success = true;
                                            if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )//DEBUGLEVEL_LUDICROUS )
                                            {
                                                CtiLockGuard< CtiLogger > doubt_guard( dout );
                                                dout << " *value =   "<<*value<<endl;
                                            }
                                        }
                                        else
                                        {
                                            *value = _tableTwoEight->getPresentValue(x);
                                            success = true;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
    presentValueSelect = NULL;
    return success;
}

////////////////////////////////////////////////////////////////////////////////////
// Present Values - volts, current, pf, etc...
////////////////////////////////////////////////////////////////////////////////////
bool CtiProtocolANSI::retreivePresentDemand( int offset, double *value )
{
    bool success = false;
    unsigned char* presentDemandSelect;
    int ansiOffset;
    int ansiQuadrant;
    int ansiDeviceType = (int) getApplicationLayer().getAnsiDeviceType();

    if (ansiDeviceType == 1) //if 1, kv2 gets info from mfg tbl 110
    {
        try
        {
           /* success = retreiveKV2PresentValue(offset, value);
            if (success)
            {
                if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_LUDICROUS) )//DEBUGLEVEL_LUDICROUS )
                {
                    CtiLockGuard< CtiLogger > doubt_guard( dout );
                    dout << " *value =   "<<*value<<endl;
                }
            }*/

            {
                    CtiLockGuard< CtiLogger > doubt_guard( dout );
                    dout << " NOT IMPLEMENTED FOR KV2 YET"<<endl;
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
    else
    {
        /* Watts = 0, Vars = 1, VA = 2, Volts = 8, Current = 12, etc */
        ansiOffset = getUnitsOffsetMapping(offset);
        ansiQuadrant = getQuadrantOffsetMapping(offset);

        try
        {
            if (_tableTwoSeven != NULL)
            {
                /* returns pointer to list of present Demand Selects */
                presentDemandSelect = (unsigned char*)_tableTwoSeven->getDemandSelect();

                if (_tableTwoOne != NULL && presentDemandSelect != NULL)
                {
                    for (int x = 0; x < _tableTwoOne->getNbrPresentDemands(); x++)
                    {
                        if ((int) presentDemandSelect[x] != 255)
                        {
                            if (_tableOneTwo != NULL)
                            {
                                if (_tableOneTwo->getRawTimeBase(presentDemandSelect[x]) == 4 &&
                                    _tableOneTwo->getRawIDCode(presentDemandSelect[x]) == ansiOffset &&
                                    _tableOneTwo->getQuadrantAccountabilityFlag(ansiQuadrant, presentDemandSelect[x]) )
                                {
                                    if (_tableOneSix != NULL  && _tableTwoEight != NULL)
                                    {
                                        if (_tableOneSix->getConstantsFlag(presentDemandSelect[x]) &&
                                            !_tableOneSix->getConstToBeAppliedFlag(presentDemandSelect[x]))
                                        {
                                            if (_tableOneFive != NULL)
                                            {
                                                *value = ((_tableTwoEight->getPresentDemand(x) *
                                                       _tableOneFive->getElecMultiplier(presentDemandSelect[x])) /*/ 1000000000*/);
                                            }
                                            else
                                            {
                                                *value = (_tableTwoEight->getPresentDemand(x) /*/ 1000000000*/);
                                            }
                                            success = true;
                                            if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )//DEBUGLEVEL_LUDICROUS )
                                            {
                                                CtiLockGuard< CtiLogger > doubt_guard( dout );
                                                dout << " *value =   "<<*value<<endl;
                                            }
                                        }
                                        else
                                        {
                                            *value = _tableTwoEight->getPresentDemand(x);
                                            success = true;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }
    presentDemandSelect = NULL;
    return success;
}


////////////////////////////////////////////////////////////////////////////////////
// Battery Life - volts, current, pf, etc...
////////////////////////////////////////////////////////////////////////////////////
bool CtiProtocolANSI::retreiveBatteryLife( int offset, double *value )
{
    bool success = false;
    unsigned char* BatteryLifeSelect;
    int ansiOffset;

    int ansiDeviceType = (int) getApplicationLayer().getAnsiDeviceType();

    if (ansiDeviceType != 2) //if 0,1, kv,kv2 not supported
    {
        return success;
    }
    else
    {
        try
        {
            switch (offset)
            {
                case 180:
                {
                    *value = abs((UINT16)getGoodBatteryReading() - (UINT16)getCurrentBatteryReading());
                    success = true;
                    break;
                }
                case 181:
                {
                    *value = getDaysOnBatteryReading();
                    success = true;
                    break;
                }
                default:
                    break;
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }

    return success;
}
bool CtiProtocolANSI::retreiveMeterTimeDiffStatus( int offset, double *status )
{
    bool success = false;
    ULONG value;

    string   str;
    int tempDiff;

    //tempStr = getValueAsString(METER_TIME_TOLERANCE);
    if( !(str = gConfigParms.getValueAsString(METER_TIME_TOLERANCE)).empty() )
        tempDiff = atoi(str.c_str());
    else
        tempDiff = 600;


    if (_tableFiveTwo != NULL)
    {
        try
        {
            value = _tableFiveTwo->getMeterServerTimeDifference();
            if (value > tempDiff)
            {
                *status = STATEONE; //BAD - ALARMING
                success = true;
            }
            else
            {
                *status = STATEZERO; //GOOD
                success = true;
            }
        }
        catch(...)
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
        }
    }

    return success;
}

////////////////////////////////////////////////////////////////////////////////////
// LP Demands - KW, KVAR, KVA, etc...
////////////////////////////////////////////////////////////////////////////////////
bool CtiProtocolANSI::retreiveLPDemand( int offset, int dataSet )
{
    bool success = false;
    UINT8* lpDemandSelect = NULL;
    int ansiOffset;

    /* Watts = 0, Vars = 1, VA = 2, etc */
    ansiOffset = getUnitsOffsetMapping(offset);

    try
    {

        if (_tableSixTwo != NULL)
        {
            /* returns pointer to list of LP Demand Selects from either dataSet 1,2,3,or 4*/
            lpDemandSelect = _tableSixTwo->getLPDemandSelect(dataSet);
            if (_tableSixOne != NULL)
            {
                for (int x = 0; x < _tableSixOne->getNbrChansSet(dataSet); x++)
                {
                    if ((int) lpDemandSelect[x] != 255)
                    {

                        if (_tableOneTwo != NULL)
                        {

                           if ((_tableOneTwo->getRawTimeBase(lpDemandSelect[x]) == 5 ||
                                 _tableOneTwo->getRawTimeBase(lpDemandSelect[x]) == 0) &&
                                _tableOneTwo->getRawIDCode(lpDemandSelect[x]) == ansiOffset)
                            {

                                if (_tableSixFour != NULL && 
                                    (_lpNbrFullBlocks > 0 || _lpNbrIntvlsLastBlock > 0))
                                {
                                    success = true;
                                    /*if (!_tableOneSix->getConstantsFlag(lpDemandSelect[x]) &&
                                        !_tableOneSix->getConstToBeAppliedFlag(lpDemandSelect[x]))
                                    { */
                                        switch (dataSet)
                                        {
                                            case 1:
                                                {
                                                    int intvlsPerBlk = _tableSixOne->getNbrBlkIntsSet(dataSet);
                                                    int blkIndex = 0;
                                                    int totalIntvls = intvlsPerBlk * _lpNbrFullBlocks + _lpNbrIntvlsLastBlock;
                                                    _nbrLPDataBlkIntvlsWanted = totalIntvls;
                                                    if (_lpValues != NULL)
                                                    {
                                                        delete []_lpValues;
                                                        _lpValues = NULL;
                                                    }
                                                    _lpValues = new double[totalIntvls + 1];
                                                    if (_lpTimes != NULL)
                                                    {
                                                        delete []_lpTimes;
                                                        _lpTimes = NULL;
                                                    }
                                                    _lpTimes = new ULONG[totalIntvls +1];
                                                    if (_lpQuality != NULL)
                                                    {
                                                        delete []_lpQuality;
                                                        _lpQuality = NULL;
                                                    }
                                                    _lpQuality = new UINT8[totalIntvls +1];
                                                    int intvlIndex = 0;
                                                    for (int y = 0; y < totalIntvls; y++)
                                                    {
                                                        if (_tableSixTwo->getNoMultiplierFlag(dataSet) && _tableOneFive != NULL) //no_multiplier_flag == true (constants need to be applied)
                                                        {
                                                            _lpValues[y] = ((_tableSixFour->getLPDemandValue ( x, blkIndex, intvlIndex ) *
                                                                           (_tableOneFive->getElecMultiplier((lpDemandSelect[x]%20)))) /
                                                                            (_tableOneTwo->getResolvedMultiplier(lpDemandSelect[x]) * 1000) *
                                                                            (60 / _tableSixOne->getMaxIntTimeSet(dataSet)) ) ;
                                                            _lpTimes[y] = _tableSixFour->getLPDemandTime (blkIndex, intvlIndex);
                                                            if (_tableFiveTwo != NULL)
                                                            {
                                                                if (_tableFiveTwo->adjustTimeForDST() )
                                                                {
                                                                    if (CtiTime(_lpTimes[y]) > CtiTime().beginDST(RWDate().year()))
                                                                        _lpTimes[y] -= 3600;
                                                                    else
                                                                    {
                                                                        _lpTimes[y] -= ( 3600 * 2 );

                                                                    }
                                                                }
                                                            }
                                                            if (_tableSixFour->getPowerFailFlag(blkIndex, intvlIndex))
                                                                _lpQuality[y] = PowerfailQuality; //powerFailQuality
                                                            else
                                                                _lpQuality[y] = translateAnsiQualityToYukon(_tableSixFour->getExtendedIntervalStatus(x, blkIndex, intvlIndex));

                                                            if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )//DEBUGLEVEL_LUDICROUS )
                                                            {
                                                                CtiLockGuard< CtiLogger > doubt_guard( dout );
                                                                dout << "    **lpTime:  " << CtiTime(_lpTimes[y]) << "  lpValue: "<<_lpValues[y]<< "  lpQuality: "<<_lpQuality[y]<<endl;
                                                            }
                                                        }
                                                        else
                                                        {
                                                            _lpValues[y] = ( _tableSixFour->getLPDemandValue ( x, blkIndex, intvlIndex ) /
                                                                           (_tableOneTwo->getResolvedMultiplier(lpDemandSelect[x]) * 1000) *
                                                                             (60 / _tableSixOne->getMaxIntTimeSet(dataSet)) ) ;
                                                            _lpTimes[y] = _tableSixFour->getLPDemandTime (blkIndex, intvlIndex);
                                                            if (_tableFiveTwo != NULL)
                                                            {
                                                                if (_tableFiveTwo->adjustTimeForDST() )
                                                                {
                                                                    if (CtiTime(_lpTimes[y]) > CtiTime().beginDST(RWDate().year()))
                                                                        _lpTimes[y] -= 3600;
                                                                    else
                                                                    {
                                                                        _lpTimes[y] -= ( 3600 * 2 );
                                                                    }
                                                                }
                                                            }
                                                            if (_tableSixFour->getPowerFailFlag(blkIndex, intvlIndex))
                                                                _lpQuality[y] = PowerfailQuality; //powerFailQuality
                                                            else
                                                                _lpQuality[y] = translateAnsiQualityToYukon(_tableSixFour->getExtendedIntervalStatus(x, blkIndex, intvlIndex));


                                                            if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )//DEBUGLEVEL_LUDICROUS )
                                                            {
                                                                CtiLockGuard< CtiLogger > doubt_guard( dout );
                                                                dout << "    **lpTime:  " << CtiTime(_lpTimes[y]) << "  lpValue: "<<_lpValues[y]<< "  lpQuality: "<<_lpQuality[y]<<endl;
                                                            }
                                                        }

                                                        if (intvlIndex + 1 == intvlsPerBlk)
                                                        {
                                                            blkIndex++;
                                                        }
                                                        intvlIndex = (intvlIndex + 1) % intvlsPerBlk;
                                                    }
                                                }
                                                break;
                                            /*
                                            case 2:
                                                {
                                                    value[y] = _tableSixFive->getLPDemandValue ( x, 1, 1 );
                                                }
                                                break;
                                            case 3:
                                                {
                                                    value[y] = _tableSixSix->getLPDemandValue ( x, 1, 1 );
                                                }
                                                break;
                                            case 4:
                                                {
                                                    value[y] = _tableSixSeven->getLPDemandValue ( x, 1, 1 );
                                                }

                                                break;
                                            */
                                            default:
                                                break;
                                        }
                                }

                            }
                        }
                    }
                    if (success)
                        break;
                }
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    if (lpDemandSelect != NULL)
    {
        delete []lpDemandSelect;
        lpDemandSelect = NULL;
    }
    return success;
}
////////////////////////////////////////////////////////////////////////////////////
// LP Demands - KW, KVAR, KVA, etc...
////////////////////////////////////////////////////////////////////////////////////
double CtiProtocolANSI::getLPValue( int index)
{
    if (_lpValues[index] != NULL)
    {
        return _lpValues[index];
    }
    else
    {
        return 0;
    }
}
////////////////////////////////////////////////////////////////////////////////////
// LP Demands - KW, KVAR, KVA, etc...
////////////////////////////////////////////////////////////////////////////////////
ULONG CtiProtocolANSI::getLPTime( int index )
{
    if (_lpTimes[index] != NULL)
    {
        return _lpTimes[index];
    }
    else
    {
        return 0;
    }
}

////////////////////////////////////////////////////////////////////////////////////
// LP Qualities
////////////////////////////////////////////////////////////////////////////////////
UINT8 CtiProtocolANSI::getLPQuality( int index )
{
    if (_lpQuality[index] != NULL)
    {
        return _lpQuality[index];
    }
    else
    {
        return 0;
    }
}


int CtiProtocolANSI::getQuadrantOffsetMapping(int offset)
{
    int retVal = 300;
    switch (offset)
    {
        case OFFSET_QUADRANT1_TOTAL_KVARH:
        case OFFSET_QUADRANT1_LAST_INTERVAL_KVAR:
        {
            retVal = 1;
            break;
        }
        case OFFSET_QUADRANT2_TOTAL_KVARH:
        case OFFSET_QUADRANT2_LAST_INTERVAL_KVAR:
        {
            retVal = 2;
            break;
        }
        case OFFSET_QUADRANT3_TOTAL_KVARH:
        case OFFSET_QUADRANT3_LAST_INTERVAL_KVAR:
        {
            retVal = 3;
            break;
        }
        case OFFSET_QUADRANT4_TOTAL_KVARH:
        case OFFSET_QUADRANT4_LAST_INTERVAL_KVAR:
        {
            retVal = 4;
            break;
        }
    default:
        break;
    }
    return retVal;



}


int CtiProtocolANSI::getUnitsOffsetMapping(int offset)
{
    int retVal = 300;
    switch (offset)
    {
            case OFFSET_TOTAL_KWH:
            case OFFSET_PEAK_KW_OR_RATE_A_KW:
            case OFFSET_RATE_A_KWH:
            case OFFSET_RATE_B_KW:
            case OFFSET_RATE_B_KWH:
            case OFFSET_RATE_C_KW:
            case OFFSET_RATE_C_KWH:
            case OFFSET_RATE_D_KW:
            case OFFSET_RATE_D_KWH:
            case OFFSET_RATE_E_KW:
            case OFFSET_RATE_E_KWH:
            case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW:
            case OFFSET_LOADPROFILE_KW:
            {
                retVal = 0;
                break;
            }
            case OFFSET_TOTAL_KVARH:
            case OFFSET_PEAK_KVAR_OR_RATE_A_KVAR:
            case OFFSET_RATE_A_KVARH:
            case OFFSET_RATE_B_KVAR:
            case OFFSET_RATE_B_KVARH:
            case OFFSET_RATE_C_KVAR:
            case OFFSET_RATE_C_KVARH:
            case OFFSET_RATE_D_KVAR:
            case OFFSET_RATE_D_KVARH:
            case OFFSET_RATE_E_KVAR:
            case OFFSET_RATE_E_KVARH:
            case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR:
            case OFFSET_LOADPROFILE_KVAR:
            case OFFSET_LOADPROFILE_QUADRANT1_KVAR:
            case OFFSET_LOADPROFILE_QUADRANT2_KVAR:
            case OFFSET_LOADPROFILE_QUADRANT3_KVAR:
            case OFFSET_LOADPROFILE_QUADRANT4_KVAR:
            case OFFSET_QUADRANT1_TOTAL_KVARH:
            case OFFSET_QUADRANT1_LAST_INTERVAL_KVAR:
            case OFFSET_QUADRANT2_TOTAL_KVARH:
            case OFFSET_QUADRANT2_LAST_INTERVAL_KVAR:
            case OFFSET_QUADRANT3_TOTAL_KVARH:
            case OFFSET_QUADRANT3_LAST_INTERVAL_KVAR:
            case OFFSET_QUADRANT4_TOTAL_KVARH:
            case OFFSET_QUADRANT4_LAST_INTERVAL_KVAR:
            {
                retVal = 1;
                break;
            }
            case OFFSET_TOTAL_KVAH:
            case OFFSET_PEAK_KVA_OR_RATE_A_KVA:
            case OFFSET_RATE_A_KVAH:
            case OFFSET_RATE_B_KVA:
            case OFFSET_RATE_B_KVAH:
            case OFFSET_RATE_C_KVA:
            case OFFSET_RATE_C_KVAH:
            case OFFSET_RATE_D_KVA:
            case OFFSET_RATE_D_KVAH:
            case OFFSET_RATE_E_KVA:
            case OFFSET_RATE_E_KVAH:
            case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA:
            case OFFSET_LOADPROFILE_KVA:
            case OFFSET_LOADPROFILE_QUADRANT1_KVA:
            case OFFSET_LOADPROFILE_QUADRANT2_KVA:
            case OFFSET_LOADPROFILE_QUADRANT3_KVA:
            case OFFSET_LOADPROFILE_QUADRANT4_KVA:
            {
                retVal = 2;
                break;
            }
            case OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE:
            case OFFSET_LOADPROFILE_PHASE_A_VOLTAGE:
            case OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE:
            case OFFSET_LOADPROFILE_PHASE_B_VOLTAGE:
            case OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE:
            case OFFSET_LOADPROFILE_PHASE_C_VOLTAGE:
            {
                retVal = 8;
                break;
            }
            case OFFSET_INSTANTANEOUS_PHASE_A_CURRENT:
            case OFFSET_LOADPROFILE_PHASE_A_CURRENT:
            case OFFSET_INSTANTANEOUS_PHASE_B_CURRENT:
            case OFFSET_LOADPROFILE_PHASE_B_CURRENT:
            case OFFSET_INSTANTANEOUS_PHASE_C_CURRENT:
            case OFFSET_LOADPROFILE_PHASE_C_CURRENT:
            case OFFSET_INSTANTANEOUS_NEUTRAL_CURRENT:
            case OFFSET_LOADPROFILE_NEUTRAL_CURRENT:
            {
                retVal = 12;
                break;
            }
            case OFFSET_POWER_FACTOR:
            {
                retVal = 24;
            }
        default:
            break;
    }
    return retVal;
}

int CtiProtocolANSI::getSegmentationOffsetMapping(int offset)
{

    int retVal = 0;
    switch (offset)
    {
        case OFFSET_POWER_FACTOR:
        {
            retVal = 0;
            break;
        }
        case OFFSET_INSTANTANEOUS_NEUTRAL_CURRENT:
        case OFFSET_LOADPROFILE_NEUTRAL_CURRENT:
        {
            retVal = 4;
            break;
        }
        case OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE:
        case OFFSET_LOADPROFILE_PHASE_A_VOLTAGE:
        case OFFSET_INSTANTANEOUS_PHASE_A_CURRENT:
        case OFFSET_LOADPROFILE_PHASE_A_CURRENT:
        {
            retVal = 5;
            break;
        }
        case OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE:
        case OFFSET_LOADPROFILE_PHASE_B_VOLTAGE:
        case OFFSET_INSTANTANEOUS_PHASE_B_CURRENT:
        case OFFSET_LOADPROFILE_PHASE_B_CURRENT:
        {
            retVal = 6;
            break;
        }
        case OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE:
        case OFFSET_LOADPROFILE_PHASE_C_VOLTAGE:
        case OFFSET_INSTANTANEOUS_PHASE_C_CURRENT:
        case OFFSET_LOADPROFILE_PHASE_C_CURRENT:
        {
            retVal = 7;
            break;
        }
        default:
            break;
    }
    return retVal;
}


int CtiProtocolANSI::getRateOffsetMapping(int offset)
{
    int retVal = 300;
    switch (offset)
    {
        case OFFSET_TOTAL_KWH:
        case OFFSET_TOTAL_KVARH:
        case OFFSET_TOTAL_KVAH:
            {
                retVal = 0;
                break;
            }
        case OFFSET_PEAK_KW_OR_RATE_A_KW:
        case OFFSET_PEAK_KVAR_OR_RATE_A_KVAR:
        case OFFSET_PEAK_KVA_OR_RATE_A_KVA:
        case OFFSET_RATE_A_KWH:
        case OFFSET_RATE_A_KVARH:
        case OFFSET_RATE_A_KVAH:
            {
                retVal = 1;
                break;
            }

        case OFFSET_RATE_B_KW:
        case OFFSET_RATE_B_KWH:
        case OFFSET_RATE_B_KVAR:
        case OFFSET_RATE_B_KVARH:
        case OFFSET_RATE_B_KVA:
        case OFFSET_RATE_B_KVAH:
        {
            retVal = 2;
            break;
        }

        case OFFSET_RATE_C_KW:
        case OFFSET_RATE_C_KWH:
        case OFFSET_RATE_C_KVAR:
        case OFFSET_RATE_C_KVARH:
        case OFFSET_RATE_C_KVA:
        case OFFSET_RATE_C_KVAH:
        {
            retVal = 3;
            break;
        }

        case OFFSET_RATE_D_KW:
        case OFFSET_RATE_D_KWH:
        case OFFSET_RATE_D_KVAR:
        case OFFSET_RATE_D_KVARH:
        case OFFSET_RATE_D_KVA:
        case OFFSET_RATE_D_KVAH:
        {
            retVal = 4;
            break;
        }

        case OFFSET_RATE_E_KW:
        case OFFSET_RATE_E_KWH:
        case OFFSET_RATE_E_KVAR:
        case OFFSET_RATE_E_KVARH:
        case OFFSET_RATE_E_KVA:
        case OFFSET_RATE_E_KVAH:
        {
            retVal = 5;
            break;
        }

        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KW:
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVAR:
        case OFFSET_LAST_INTERVAL_OR_INSTANTANEOUS_KVA:

        case OFFSET_LOADPROFILE_KW:
        case OFFSET_LOADPROFILE_KVAR:
        case OFFSET_LOADPROFILE_KVA:

        case OFFSET_LOADPROFILE_QUADRANT1_KVAR:
        case OFFSET_LOADPROFILE_QUADRANT1_KVA:
        case OFFSET_LOADPROFILE_QUADRANT2_KVAR:
        case OFFSET_LOADPROFILE_QUADRANT2_KVA:
        case OFFSET_LOADPROFILE_QUADRANT3_KVAR:
        case OFFSET_LOADPROFILE_QUADRANT3_KVA:
        case OFFSET_LOADPROFILE_QUADRANT4_KVAR:
        case OFFSET_LOADPROFILE_QUADRANT4_KVA:

        case OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE:
        case OFFSET_INSTANTANEOUS_PHASE_A_CURRENT:
        case OFFSET_LOADPROFILE_PHASE_A_VOLTAGE:
        case OFFSET_LOADPROFILE_PHASE_A_CURRENT:

        case OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE:
        case OFFSET_INSTANTANEOUS_PHASE_B_CURRENT:
        case OFFSET_LOADPROFILE_PHASE_B_VOLTAGE:
        case OFFSET_LOADPROFILE_PHASE_B_CURRENT:

        case OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE:
        case OFFSET_INSTANTANEOUS_PHASE_C_CURRENT:
        case OFFSET_LOADPROFILE_PHASE_C_VOLTAGE:
        case OFFSET_LOADPROFILE_PHASE_C_CURRENT:

        case OFFSET_INSTANTANEOUS_NEUTRAL_CURRENT:
        case OFFSET_LOADPROFILE_NEUTRAL_CURRENT:
        default:
            break;
    }
    return retVal;
}

int CtiProtocolANSI::translateAnsiQualityToYukon(int ansiQuality )
{
    int retVal = NormalQuality;
    switch (ansiQuality)
    {
        case 0:
        case 5:
        {
            retVal = NormalQuality;
            break;
        }
        case 1:
        {
            retVal = OverflowQuality;
            break;
        }
        case 2:
        {
            retVal = PartialIntervalQuality;
            break;
        }
        case 3: //long interval
        case 4:  //skipped
        {
            retVal = UnknownQuality;
            break;
        }
        default:
        {
            retVal = NormalQuality;
            break;
        }
    }
    return retVal;

}


int CtiProtocolANSI::getSizeOfLastLPDataBlock(int dataSetNbr)
{
    try
    {

        int sizeOfLpBlkDatRcd = 0;
        int nbrChnsSet = _tableSixOne->getNbrChansSet(dataSetNbr);
        int nbrBlkIntsSet = _tableSixThree->getNbrValidIntvls(dataSetNbr);


        sizeOfLpBlkDatRcd += sizeOfSTimeDate() +
                          (nbrChnsSet * getSizeOfLPReadingsRcd());

        if (_tableSixOne->getClosureStatusFlag())
        {
            sizeOfLpBlkDatRcd += (nbrChnsSet * 2); //uint16 closure_status_bfld
        }
        if (_tableSixOne->getSimpleIntStatusFlag())
        {
            sizeOfLpBlkDatRcd += (nbrBlkIntsSet + 7) / 8;
        }
        sizeOfLpBlkDatRcd += nbrBlkIntsSet * getSizeOfLPIntSetRcd(dataSetNbr);

        return sizeOfLpBlkDatRcd;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return 0;
}


int CtiProtocolANSI::getSizeOfLPDataBlock(int dataSetNbr)
{
    try
    {

        int sizeOfLpBlkDatRcd = 0;
        int nbrChnsSet = _tableSixOne->getNbrChansSet(dataSetNbr);
        int nbrBlkIntsSet = _tableSixOne->getNbrBlkIntsSet(dataSetNbr);


        sizeOfLpBlkDatRcd += sizeOfSTimeDate() +
                          (nbrChnsSet * getSizeOfLPReadingsRcd());

        if (_tableSixOne->getClosureStatusFlag())
        {
            sizeOfLpBlkDatRcd += (nbrChnsSet * 2); //uint16 closure_status_bfld
        }
        if (_tableSixOne->getSimpleIntStatusFlag())
        {
            sizeOfLpBlkDatRcd += (nbrBlkIntsSet + 7) / 8;
        }
        sizeOfLpBlkDatRcd += nbrBlkIntsSet * getSizeOfLPIntSetRcd(dataSetNbr);

        return sizeOfLpBlkDatRcd;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return 0;
}

int CtiProtocolANSI::getSizeOfLPReadingsRcd()
{
    try
    {
        int sizeOfReadingsRcd = 0;

        if (_tableSixOne->getBlkEndReadFlag())
        {
            sizeOfReadingsRcd += sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat1());
        }
        if (_tableSixOne->getBlkEndPulseFlag())
        {
            sizeOfReadingsRcd += 4;
        }
        return sizeOfReadingsRcd;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return 0;
}

int CtiProtocolANSI::getSizeOfLPIntSetRcd(int dataSetNbr)
{
    try
    {
            int sizeOfIntSetRcd = 0;
        int nbrChnsSet = _tableSixOne->getNbrChansSet(dataSetNbr);

        if (_tableSixOne->getExtendedIntStatusFlag())
        {
            sizeOfIntSetRcd += (nbrChnsSet/2) + 1;

        }
        sizeOfIntSetRcd += nbrChnsSet * getSizeOfLPIntFmtRcd(dataSetNbr);

        return sizeOfIntSetRcd;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return 0;
}
int CtiProtocolANSI::getSizeOfLPIntFmtRcd(int dataSetNbr)
{
    try
    {
        int sizeOfIntFmtRcd = 0;
        switch (_tableSixTwo->getIntervalFmtCde(dataSetNbr))
        {
            case 1:
            case 8:
            {
                sizeOfIntFmtRcd += 1;
                break;
            }
            case 2:
            case 16:
            {
                sizeOfIntFmtRcd += 2;
                break;
            }
            case 4:
            case 32:
            {
                sizeOfIntFmtRcd += 4;
                break;
            }
            case 64:
            {
                sizeOfIntFmtRcd += sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat1());
                break;
            }
            case 128:
            {
                sizeOfIntFmtRcd += sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat2());

                break;
            }
        }
        return sizeOfIntFmtRcd;
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }
    return 0;
}
unsigned short CtiProtocolANSI::getTotalWantedLPBlockInts()
{
    return _nbrLPDataBlkIntvlsWanted;
}

int CtiProtocolANSI::proc09RemoteReset(UINT8 actionFlag)
{
    try
    {

        _tables[_index].tableID = 7;
        _tables[_index].tableOffset = 0;
        _tables[_index].bytesExpected = 1;
        _tables[_index].type = ANSI_TABLE_TYPE_STANDARD;
        _tables[_index].operation = ANSI_OPERATION_WRITE;

        getApplicationLayer().initializeTableRequest (_tables[_index].tableID,
                                                      _tables[_index].tableOffset,
                                                      _tables[_index].bytesExpected,
                                                      _tables[_index].type,
                                                      _tables[_index].operation);

        REQ_DATA_RCD reqData;
        reqData.proc.tbl_proc_nbr = 9;
        reqData.proc.std_vs_mfg_flag = 0;
        reqData.proc.selector = 3;
        BYTE *superTemp;

        superTemp = (BYTE *)reqData.proc.tbl_proc_nbr;
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "tbl_proc_nbr = "<<(int)reqData.proc.tbl_proc_nbr<<endl;
            dout << "std_vs_mfg_flag = " << (int)reqData.proc.std_vs_mfg_flag<<endl;
            dout << "selector = "<<(int)reqData.proc.selector<<endl;
       //     dout << "hex value : "<<(int)superTemp[0]<<" "<<(int)superTemp[1]<<endl;
        }
        getApplicationLayer().setProcBfld( reqData.proc );
        _seqNbr++;
        reqData.seq_nbr = _seqNbr;
        getApplicationLayer().setWriteSeqNbr( reqData.seq_nbr );


        reqData.u.p9.action_flag = actionFlag;
        /*BYTE *newPtr;
        newPtr = 0;
        *newPtr = reqData.u.p9.action_flag;*/
        getApplicationLayer().populateParmPtr((BYTE *)&reqData.u.p9.action_flag, 1) ;


        getApplicationLayer().setProcDataSize( sizeof(TBL_IDB_BFLD) + sizeof(reqData.seq_nbr) + 1 );

    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return 1;
}


void CtiProtocolANSI::setCurrentAnsiWantsTableValues(short tableID,int tableOffset, unsigned int bytesExpected,
                                                     BYTE  type, BYTE operation)
{
   _tables[_index].tableID = tableID;
   _tables[_index].tableOffset = tableOffset;
   _tables[_index].bytesExpected = bytesExpected;
   _tables[_index].type = type;
   _tables[_index].operation = operation;

   return;
}

int CtiProtocolANSI::getWriteSequenceNbr(void)
{
    return _seqNbr++;
}

unsigned long CtiProtocolANSI::getlastLoadProfileTime(void)
{
    return _header->lastLoadProfileTime;
}


int CtiProtocolANSI::getNbrValidIntvls()
{
    return _lpNbrIntvlsLastBlock;
}
int CtiProtocolANSI::getNbrValidBlks()
{
    return _lpNbrValidBlks;
}

int CtiProtocolANSI::getMaxIntervalTime()
{
    return _lpMaxIntervalTime;
}
int CtiProtocolANSI::getNbrBlksSet()
{
    return _lpNbrBlksSet;
}
int CtiProtocolANSI::getNbrIntervalsPerBlock()
{
    return _lpNbrIntvlsPerBlock;
}
int CtiProtocolANSI::getLastBlockIndex()
{
    return _lpLastBlockIndex;
}



void CtiProtocolANSI::setTablesAvailable(unsigned char * stdTblsUsed, int dimStdTblsUsed, unsigned char * mfgTblsUsed, int dimMfgTblsUsed)
{
    try
    {
        if (!_stdTblsAvailable.empty())
        {
            _stdTblsAvailable.clear();
        }
        if (_mfgTblsAvailable.empty())
        {
            _mfgTblsAvailable.clear();
        }

        int i;
        for (i = 0; i < dimStdTblsUsed; i++)
        {
            if (stdTblsUsed[i] & 0x01)
            {
                _stdTblsAvailable.push_back((i*8)+0);
            }
            if (stdTblsUsed[i] & 0x02)
            {
                _stdTblsAvailable.push_back((i*8)+1);
            }
            if (stdTblsUsed[i] & 0x04)
            {
                _stdTblsAvailable.push_back((i*8)+2);
            }
            if (stdTblsUsed[i] & 0x08)
            {
                _stdTblsAvailable.push_back((i*8)+3);
            }
            if (stdTblsUsed[i] & 0x10)
            {
                _stdTblsAvailable.push_back((i*8)+4);
            }
            if (stdTblsUsed[i] & 0x20)
            {
                _stdTblsAvailable.push_back((i*8)+5);
            }
            if (stdTblsUsed[i] & 0x40)
            {
                _stdTblsAvailable.push_back((i*8)+6);
            }
            if (stdTblsUsed[i] & 0x80)
            {
                _stdTblsAvailable.push_back((i*8)+7);
            }
        }

        for (i = 0; i < dimMfgTblsUsed; i++)
        {
            if ((int)mfgTblsUsed[i] & 0x01)
            {
                _mfgTblsAvailable.push_back((i*8)+0);
            }
            if (mfgTblsUsed[i] & 0x02)
            {
                _mfgTblsAvailable.push_back((i*8)+1);
            }
            if (mfgTblsUsed[i] & 0x04)
            {
                _mfgTblsAvailable.push_back((i*8)+2);
            }
            if (mfgTblsUsed[i] & 0x08)
            {
                _mfgTblsAvailable.push_back((i*8)+3);
            }
            if (mfgTblsUsed[i] & 0x10)
            {
                _mfgTblsAvailable.push_back((i*8)+4);
            }
            if (mfgTblsUsed[i] & 0x20)
            {
                _mfgTblsAvailable.push_back((i*8)+5);
            }
            if (mfgTblsUsed[i] & 0x40)
            {
                _mfgTblsAvailable.push_back((i*8)+6);
            }
            if (mfgTblsUsed[i] & 0x80)
            {
                _mfgTblsAvailable.push_back((i*8)+7);
            }
        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
    }

    return;
}

list < short > CtiProtocolANSI::getStdTblsAvailable(void)
{
    return _stdTblsAvailable;
}

list < short > CtiProtocolANSI::getMfgTblsAvailable(void)
{
    return _mfgTblsAvailable;
}

bool CtiProtocolANSI::isStdTableAvailableInMeter(short tableNbr)
{
    std::list<short>::iterator ii = _stdTblsAvailable.begin();

    do
    {
        if (*ii == tableNbr)
        {
            return true;
        }
        else
            ii++;

    }while(ii != _stdTblsAvailable.end());

    return false;
}

bool CtiProtocolANSI::isMfgTableAvailableInMeter(short tableNbr)
{
    std::list<short>::iterator ii = _mfgTblsAvailable.begin();

    do
    {
        if (*ii == tableNbr)
        {
            return true;
        }
        else
            ii++;

    }while(ii != _mfgTblsAvailable.end());

    return false;
}


int CtiProtocolANSI::getScanOperation(void)
{
    return (int)_scanOperation;
}


UINT CtiProtocolANSI::getParseFlags(void)
{
    return _parseFlags;
}

const string& CtiProtocolANSI::getAnsiDeviceName() const
{
    return _ansiDevName;
}
void CtiProtocolANSI::setAnsiDeviceName(const string& devName)
{
    getApplicationLayer().setAnsiDeviceName(devName);
    _ansiDevName = devName;
    return;
}


void CtiProtocolANSI::setLastLoadProfileTime(LONG lastLPTime)
{
    _header->lastLoadProfileTime = lastLPTime;
    return;
}

bool CtiProtocolANSI::forceProcessDispatchMsg()
{
    if (_tableSixFour != NULL)
        return _forceProcessDispatchMsg;
    else if (_tableTwoThree != NULL || _tableFiveTwo != NULL)
        return true;
    else
        return false;
}

bool CtiProtocolANSI::isTimeUninitialized(double time)
{
    CtiTime t1 = CtiTime(time);
    CtiTime t2 = CtiTime(CtiDate(1,1,2000));
    if ( t1.seconds() == t2.seconds() )
        return true;
    else
        return false;
}



