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
* REVISION     :  $Revision: 1.27 $
* DATE         :  $Date: 2008/10/21 16:30:31 $
*    History:
      $Log: prot_ansi.cpp,v $
      Revision 1.27  2008/10/21 16:30:31  mfisher
      YUK-6615 ANSI table class names and filenames are difficult to read
      Renamed classes and filenames

      Revision 1.26  2008/08/14 18:17:20  jrichter
      YUK-6310
      Sentinel dial up meter reads causing exceptions when scanner reads in future or year(S) old lastLpTime dates.
      -don't check to see if map is empty before  clear.  who cares.

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
#include "precompiled.h"


#include "guard.h"
#include "configparms.h"
#include "logger.h"
#include "pointdefs.h"
#include "prot_ansi.h"
#include "utility.h"
#include "ctitime.h"
#include "ctidate.h"

using std::endl;
using std::string;
using std::list;

using namespace Cti::Protocols::Ansi;
const CHAR * CtiProtocolANSI::METER_TIME_TOLERANCE = "PORTER_SENTINEL_TIME_TOLERANCE";
//const CHAR * CtiProtocolANSI::ANSI_DEBUGLEVEL = "ANSI_DEBUGLEVEL";
//========================================================================================================================================
//=========================================================================================================================================

CtiProtocolANSI::CtiProtocolANSI()
{
   _index = 0;
   _bytesInGot = 0;
   _tables = NULL;
   _header = NULL;
   _billingTable = NULL;
   _table00 = NULL;
   _table01 = NULL;
   _table08 = NULL;
   _table10 = NULL;
   _table11 = NULL;
   _table12 = NULL;
   _table13 = NULL;
   _table14 = NULL;
   _table15 = NULL;
   _table16 = NULL;
   _table21 = NULL;
   _table22 = NULL;
   _table23 = NULL;
   _table27 = NULL;
   _table28 = NULL;
   _table31 = NULL;
   _table32 = NULL;
   _table33 = NULL;
   _table51 = NULL;
   _table52 = NULL;
   _table61 = NULL;
   _table62 = NULL;
   _table63 = NULL;
   _table64 = NULL;
   //_table55 = NULL;
   _frozenRegTable = NULL;

   _validFlag = false;
   _previewTable64 = false;
   _entireTableFlag = false;
   _clearMfgTables = false;
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
   _ansiAbortOperation = FALSE;

   _stdTblsAvailable.push_back(0);

   _lpValues = NULL;
   _lpTimes = NULL;
    _lpQuality = NULL;

   _currentTableNotAvailableFlag = false;
   _requestingBatteryLifeFlag = false;
   _invalidLastLoadProfileTime = false;
   _forceProcessDispatchMsg = false;

   _scanOperation = generalScan;
   _parseFlags = 0;

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

   if( _table00 != NULL )
   {
      delete _table00;
      _table00 = NULL;
   }

   if( _table01 != NULL )
   {
      delete _table01;
      _table01 = NULL;
   }

   if( _table08 != NULL )
   {
      delete _table08;
      _table08 = NULL;
   }

   if( _table10 != NULL )
   {
      delete _table10;
      _table10 = NULL;
   }

   if( _table11 != NULL )
   {
      delete _table11;
      _table11 = NULL;
   }

   if( _table12 != NULL )
   {
      delete _table12;
      _table12 = NULL;
   }

   if( _table13 != NULL )
   {
      delete _table13;
      _table13 = NULL;
   }

   if( _table14 != NULL )
   {
      delete _table14;
      _table14 = NULL;
   }

   if( _table15 != NULL )
   {
      delete _table15;
      _table15 = NULL;
   }

   if( _table16 != NULL )
   {
      delete _table16;
      _table16 = NULL;
   }

   if( _table21 != NULL )
   {
      delete _table21;
      _table21 = NULL;
   }

   if( _table22 != NULL )
   {
      delete _table22;
      _table22 = NULL;
   }

   if( _table23 != NULL )
   {
      delete _table23;
      _table23 = NULL;
   }
   if( _table27 != NULL )
   {
      delete _table27;
      _table27 = NULL;
   }
   if( _table28 != NULL )
   {
      delete _table28;
      _table28 = NULL;
   }
   if( _table31 != NULL )
   {
      delete _table31;
      _table31 = NULL;
   }
   if( _table32 != NULL )
   {
      delete _table32;
      _table32 = NULL;
   }
   if( _table33 != NULL )
   {
      delete _table33;
      _table33 = NULL;
   }
   if( _table51 != NULL )
   {
      delete _table51;
      _table51 = NULL;
   }

   if( _table52 != NULL )
   {
      delete _table52;
      _table52 = NULL;
   }
   if( _table61 != NULL )
   {
      delete _table61;
      _table61 = NULL;
   }
   if( _table62 != NULL )
   {
      delete _table62;
      _table62 = NULL;
   }
   if( _table63 != NULL )
   {
      delete _table63;
      _table63 = NULL;
   }
   if( _table64 != NULL )
   {
      delete _table64;
      _table64 = NULL;
   }
   if( _table08 != NULL )
   {
      delete _table08;
      _table08 = NULL;
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

       _ansiAbortOperation = FALSE;

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

    return( getScanOperation() == loopBack ? 1 : _header->numTablesRequested );   //just a val
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
                if (_tables[_index].operation != ANSI_OPERATION_READ)
                {
                    if (handleWriteOperations())
                    {
                        return true;
                    }
                }
                else
                {
                    convertToTable();
                }

                if (createWriteOperations())
                {
                    return true;
                }


               try
               {
                   if (_tables[_index].tableID == LoadProfileStatus  && _table63 != NULL)
                   {
                       if ( !setLoadProfileVariables() )
                           return true;
                   }
               }
               catch( ... )
               {
                   CtiLockGuard<CtiLogger> logger_guard(dout);
                   dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;

                   _ansiAbortOperation = TRUE;
                   _index = _header->numTablesRequested - 1;
                   getApplicationLayer().terminateSession();
                   return true;
               }
           }
           // anything else to do
           if ((_index+1) < _header->numTablesRequested)
           {
               _index++;
               updateBytesExpected ();

               if (!_currentTableNotAvailableFlag)
               {
                   prepareApplicationLayer();
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
                   printResults();
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

        _ansiAbortOperation = TRUE;
        _index = _header->numTablesRequested - 1;
        getApplicationLayer().terminateSession();

    }
    return false;

}

void CtiProtocolANSI::prepareApplicationLayer()
{

    if (_tables[_index].type == ANSI_TABLE_TYPE_MANUFACTURER)
    {
      _tables[_index].tableID += 0x0800;
    }

    if (//getApplicationLayer().getAnsiDeviceType() != CtiANSIApplication::focus &&
       (_tables[_index].tableID == Constants ||
        _tables[_index].tableID == CurrentRegisterData ||
        _tables[_index].tableID == FrozenRegisterData))
    {
        getApplicationLayer().setLPDataMode( true, _tables[_index].bytesExpected );
    }

    if (_tables[_index].tableID >= LoadProfileDataSet1 && _tables[_index].tableID <= LoadProfileDataSet4)
    {
        getApplicationLayer().setLPDataMode( true, _table61->getLPMemoryLength() );
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

void CtiProtocolANSI::printResults()
{
    if (_table00 != NULL)
        _table00->printResult(getAnsiDeviceName());
    if (_table01 != NULL)
        _table01->printResult(getAnsiDeviceName());
    if (_table11 != NULL)
        _table11->printResult(getAnsiDeviceName());
    if (_table12 != NULL)
        _table12->printResult(getAnsiDeviceName());
    if (_table13 != NULL)
        _table13->printResult(getAnsiDeviceName());
    if (_table14 != NULL)
        _table14->printResult(getAnsiDeviceName());
    if (_table15 != NULL)
        _table15->printResult(getAnsiDeviceName());
    if (_table16 != NULL)
        _table16->printResult(getAnsiDeviceName());
    if (_table21 != NULL)
        _table21->printResult(getAnsiDeviceName());
    if (_table22 != NULL)
        _table22->printResult(getAnsiDeviceName());
    if (_table23 != NULL)
        _table23->printResult(getAnsiDeviceName());
    if (_frozenRegTable != NULL)
        _frozenRegTable->printResult(getAnsiDeviceName());
    if (_table27 != NULL)
        _table27->printResult(getAnsiDeviceName());
    if (_table28 != NULL)
        _table28->printResult(getAnsiDeviceName());
    if (_table31 != NULL)
        _table31->printResult(getAnsiDeviceName());
    if (_table32 != NULL)
        _table32->printResult(getAnsiDeviceName());
    if (_table33 != NULL)
        _table33->printResult(getAnsiDeviceName());
    if (_table51 != NULL)
        _table51->printResult(getAnsiDeviceName());
    if (_table52 != NULL)
        _table52->printResult(getAnsiDeviceName());
    if (_table61 != NULL)
        _table61->printResult(getAnsiDeviceName());
    if (_table62 != NULL)
        _table62->printResult(getAnsiDeviceName());
    if (_table63 != NULL)
        _table63->printResult(getAnsiDeviceName());
    if (_table64 != NULL)
        _table64->printResult(getAnsiDeviceName());
    if (_table08 != NULL)
        _table08->printResult(getAnsiDeviceName());

    return;
}

bool CtiProtocolANSI::handleWriteOperations()
{

    if (_requestingBatteryLifeFlag)
    {
        _tables[_index].tableID = Sentinel_BatteryLifeResponse;
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
    else if (_tables[_index].tableID == Focus_SetLpReadControl)
    {
        return false;
    }
    else
    {
        _tables[_index].tableID = ProcedureResponse;
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
}

bool CtiProtocolANSI::createWriteOperations()
{

     if (_scanOperation == CtiProtocolANSI::demandReset && _tables[_index].tableID == GeneralManufacturerIdentification) //1 = demand reset
     {
          if (proc09RemoteReset(1))
          {
              return true;
          }
     }
     if (_tables[_index].tableID == DataSelection && snapshotData())
     {
         return true;
     }
     if (_tables[_index].tableID == PresentRegisterData && batteryLifeData())
     {
         _requestingBatteryLifeFlag = true;
         return true;
     }
     return false;
}

void CtiProtocolANSI::setLoadProfileFullBlocks(long fullBlocks)
{
    _lpNbrFullBlocks =  fullBlocks;
}

bool CtiProtocolANSI::setLoadProfileVariables()
{
    bool retVal = true;
   _lpNbrIntvlsLastBlock = _table63->getNbrValidIntvls(1);
   _lpNbrValidBlks = _table63->getNbrValidBlocks(1);
   _lpLastBlockIndex = _table63->getLastBlkElmt(1);
   _lpNbrIntvlsPerBlock = _table61->getNbrBlkIntsSet(1);
   _lpMaxIntervalTime = _table61->getMaxIntTimeSet(1);
   _lpNbrBlksSet = _table61->getNbrBlksSet(1);

   if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )
   {
           CtiLockGuard< CtiLogger > doubt_guard( dout );
           dout <<  CtiTime() << " " << getApplicationLayer().getAnsiDeviceName() << " ** _lpNbrIntvlsLastBlock  " <<_lpNbrIntvlsLastBlock << endl;
           dout <<  CtiTime() << " " << getApplicationLayer().getAnsiDeviceName() << " ** _lpLastBlockIndex  " <<_lpLastBlockIndex << endl;
   }
   _lpBlockSize = getSizeOfLPDataBlock(1);
   _lpLastBlockSize =  getSizeOfLastLPDataBlock(1);
   getApplicationLayer().setLPBlockSize(_lpBlockSize);

   if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )
   {
           CtiLockGuard< CtiLogger > doubt_guard( dout );
           dout <<  CtiTime() << " " << getApplicationLayer().getAnsiDeviceName() << " ** _lpBlockSize  " <<_lpBlockSize << endl;
           dout <<  CtiTime() << " " << getApplicationLayer().getAnsiDeviceName() << " ** _lpLastBlockSize  " <<_lpLastBlockSize << endl;
   }

   if ((_lpStartBlockIndex = calculateLPDataBlockStartIndex(_header->lastLoadProfileTime)) < 0)
   {
       retVal = false;
   }
   else
   {
       _lpNbrFullBlocks =  _lpLastBlockIndex - _lpStartBlockIndex;
       _lpOffset = _lpStartBlockIndex * _lpBlockSize;
       if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )
       {
           CtiLockGuard< CtiLogger > doubt_guard( dout );
           dout <<  CtiTime() << " " << getApplicationLayer().getAnsiDeviceName() << " ** Nbr of Full Blocks  " <<_lpNbrFullBlocks << endl;
       }
   }
   return retVal;
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
                case Configuration:
                   {
                       if (_table00 != NULL)
                       {
                           delete _table00;
                           _table00 = NULL;
                       }
                      _table00 = new CtiAnsiTable00( getApplicationLayer().getCurrentTable() );
                      if( _table00 != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )//DEBUGLEVEL_LUDICROUS )
                      {
                          _table00->printResult(getAnsiDeviceName());
                      }

                      setTablesAvailable(_table00->getStdTblsUsed(), _table00->getDimStdTblsUsed(),
                                         _table00->getMfgTblsUsed(), _table00->getDimMfgTblsUsed());
                   }
                   break;

                case GeneralManufacturerIdentification:
                   {
                       if (_table01 != NULL)
                       {
                           delete _table01;
                           _table01 = NULL;
                       }
                      _table01 = new CtiAnsiTable01( getApplicationLayer().getCurrentTable(),
                                                               _table00->getRawMfgSerialNumberFlag(),
                                                               _table00->getRawIdFormat() );
                      if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )//DEBUGLEVEL_LUDICROUS )
                      {
                          _table01->printResult(getAnsiDeviceName());
                      }

                      if ((int)getApplicationLayer().getAnsiDeviceType() == CtiANSIApplication::sentinel) //sentinel
                      {
                          getApplicationLayer().setFWVersionNumber(_table01->getFWVersionNumber());
                      }
                   }
                   break;
               case ProcedureResponse:
                   {
                       if (_table08 != NULL)
                       {
                           delete _table08;
                           _table08 = NULL;
                       }
                       _table08 = new CtiAnsiTable08( getApplicationLayer().getCurrentTable());
                       if( _table08 != NULL )
                       {
                           if(  _table08 != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )//DEBUGLEVEL_LUDICROUS )
                           {
                               _table08->printResult(getAnsiDeviceName());
                           }


                           if (_scanOperation == CtiProtocolANSI::demandReset)
                           {
                               getApplicationLayer().setWriteSeqNbr( 0 );
                           }
                           else
                           {
                               _lpStartBlockIndex = _table08->getLPOffset();
                               _lpNbrFullBlocks = _lpLastBlockIndex - _lpStartBlockIndex;

                               if( _table63 != NULL &&
                                   _lpStartBlockIndex >= 255)
                               {
                                   _lpStartBlockIndex = 0;
                                   _lpOffset = 0;
                                   if( _table63->getNbrValidIntvls(1) < _table63->getNbrValidBlocks(1) )
                                   {
                                       _lpNbrFullBlocks = _table63->getNbrValidBlocks(1) -1;
                                   }
                                   else
                                   {
                                       _lpNbrFullBlocks = _table63->getNbrValidBlocks(1);
                                   }
                               }
                               else
                               {
                                   _lpOffset = _lpStartBlockIndex * _lpBlockSize;
                               }

                           }
                       }
                   }
                   break;

                case DataSource:
                   {
                       if (_table10 != NULL)
                       {
                           delete _table10;
                           _table10 = NULL;
                       }
                      _table10 = new CtiAnsiTable10( getApplicationLayer().getCurrentTable() );
                   }
                   break;

                case ActualSourcesLimiting:
                   {
                       if (_table11 != NULL)
                       {
                           delete _table11;
                           _table11 = NULL;
                       }
                       _table11 = new CtiAnsiTable11( getApplicationLayer().getCurrentTable() );
                       if(  _table11 != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )//DEBUGLEVEL_LUDICROUS )
                       {
                           _table11->printResult(getAnsiDeviceName());
                       }
                   }
                   break;

                case UnitOfMeasureEntry:
                   {
                       if (_table12 != NULL)
                       {
                           delete _table12;
                           _table12 = NULL;
                       }
                       _table12 = new CtiAnsiTable12( getApplicationLayer().getCurrentTable(),
                                                             _table11->getNumberUOMEntries() );
                       if( _table12 != NULL &&  getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )//DEBUGLEVEL_LUDICROUS )
                       {
                           _table12->printResult(getAnsiDeviceName());
                       }
                   }
                   break;

                case DemandControl:
                   {
                       if (_table13 != NULL)
                       {
                           delete _table13;
                           _table13 = NULL;
                       }
                       if( _table11 != NULL )
                       {
                           _table13 = new CtiAnsiTable13( getApplicationLayer().getCurrentTable(),
                                                                  _table11->getNumberDemandControlEntries(),
                                                                  _table11->getRawPFExcludeFlag(),
                                                                          _table11->getRawSlidingDemandFlag(),
                                                                          _table11->getRawResetExcludeFlag() );
                           if( _table13 != NULL &&  getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {
                               _table13->printResult(getAnsiDeviceName());
                           }
                       }
                   }
                   break;

                case DataControl:
                   {
                       if (_table14 != NULL)
                       {
                           delete _table14;
                           _table14 = NULL;
                       }
                       if( _table11 != NULL )
                       {
                           _table14 = new CtiAnsiTable14( getApplicationLayer().getCurrentTable(),
                                                               _table11->getDataControlLength(),
                                                               _table11->getNumberDataControlEntries());
                           if( _table14 != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {
                               _table14->printResult(getAnsiDeviceName());
                           }
                       }
                   }
                   break;

                case Constants:
                   {
                       if (_table15 != NULL)
                       {
                           delete _table15;
                           _table15 = NULL;
                       }
                       if( _table11 != NULL && _table00 != NULL )
                       {
                           _table15 = new CtiAnsiTable15( getApplicationLayer().getCurrentTable(),
                                                               _table11->getRawConstantsSelector(),
                                                               _table11->getNumberConstantsEntries(),
                                                               _table11->getRawNoOffsetFlag(),
                                                               _table11->getRawSetOnePresentFlag(),
                                                               _table11->getRawSetTwoPresentFlag(),
                                                               _table00->getRawNIFormat1(),
                                                               _table00->getRawNIFormat2(),
                                                               (_table00->getRawDataOrder() == 0) );

                           getApplicationLayer().setLPDataMode( false, 0 );
                           if( _table15 != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {
                               _table15->printResult(getAnsiDeviceName());
                           }
                       }
                   }
                   break;

                case SourceDefinition:
                   {
                       if (_table16 != NULL)
                       {
                           delete _table16;
                           _table16 = NULL;
                       }
                       if( _table11 != NULL )
                       {
                           _table16 = new CtiAnsiTable16( getApplicationLayer().getCurrentTable(),
                                                             _table11->getNumberSources() );
                           if( _table16 != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {
                               _table16->printResult(getAnsiDeviceName());
                           }
                       }
                   }
                   break;

                case ActualRegister:
                   {
                       if (_table21 != NULL)
                       {
                           delete _table21;
                           _table21 = NULL;
                       }
                      _table21 = new CtiAnsiTable21( getApplicationLayer().getCurrentTable() );
                      if( _table21 != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                          _table21->printResult(getAnsiDeviceName());
                      }
                   }
                   break;

                case DataSelection:
                   {
                       if (_table22 != NULL)
                       {
                           delete _table22;
                           _table22 = NULL;
                       }
                       if( _table21 != NULL)
                       {
                            _table22 = new CtiAnsiTable22( getApplicationLayer().getCurrentTable(),
                                                             _table21->getNumberSummations(),
                                                             _table21->getNumberDemands(),
                                                             _table21->getCoinValues() );
                            if( _table22 != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                            {
                                _table22->printResult(getAnsiDeviceName());
                            }
                       }
                   }
                   break;

                case CurrentRegisterData:
                   {
                       if (_table23 != NULL)
                       {
                           delete _table23;
                           _table23 = NULL;
                       }
                       if ( _table21 != NULL && _table00 != NULL)
                       {
                           _table23 = new CtiAnsiTable23( getApplicationLayer().getCurrentTable(),
                                                                 _table21->getOccur(),
                                                                 _table21->getNumberSummations(),
                                                                 _table21->getNumberDemands(),
                                                                 _table21->getCoinValues(),
                                                                 _table21->getTiers(),
                                                                 _table21->getDemandResetCtrFlag(),
                                                                 _table21->getTimeDateFieldFlag(),
                                                                 _table21->getCumDemandFlag(),
                                                                 _table21->getContCumDemandFlag(),
                                                                 _table00->getRawNIFormat1(),
                                                                 _table00->getRawNIFormat2(),
                                                                 _table00->getRawTimeFormat(),
                                                                  23,
                                                                (_table00->getRawDataOrder() == 0));

                           getApplicationLayer().setLPDataMode( false, 0 );
                           if( _table23 != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {
                               _table23->printResult(getAnsiDeviceName());
                           }
                       }
                   }
                   break;
                case FrozenRegisterData:
                    {
                        if (_frozenRegTable != NULL)
                        {
                           delete _frozenRegTable;
                           _frozenRegTable = NULL;
                        }

                        if ( _table21 != NULL && _table00 != NULL)
                        {
                            _frozenRegTable = new CtiAnsiTable25 (  getApplicationLayer().getCurrentTable(),
                                                                          _table21->getOccur(),
                                                                          _table21->getNumberSummations(),
                                                                          _table21->getNumberDemands(),
                                                                          _table21->getCoinValues(),
                                                                          _table21->getTiers(),
                                                                          _table21->getDemandResetCtrFlag(),
                                                                          _table21->getTimeDateFieldFlag(),
                                                                          _table21->getCumDemandFlag(),
                                                                          _table21->getContCumDemandFlag(),
                                                                          _table00->getRawNIFormat1(),
                                                                          _table00->getRawNIFormat2(),
                                                                          _table00->getRawTimeFormat(),
                                                                          _table21->getSeasonInfoFieldFlag() );
                            getApplicationLayer().setLPDataMode( false, 0 );
                            if( _frozenRegTable != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                            {
                                _frozenRegTable->printResult(getAnsiDeviceName());
                            }
                        }
                    }
                    break;
                case PresentRegisterSelection:
                   {
                       if (_table27 != NULL)
                       {
                           delete _table27;
                           _table27 = NULL;
                       }
                       if ( _table21 != NULL )
                       {
                       _table27 = new CtiAnsiTable27( getApplicationLayer().getCurrentTable(),
                                                                 _table21->getNbrPresentDemands(),
                                                                 _table21->getNbrPresentValues());
                           if( _table27 != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                           _table27->printResult(getAnsiDeviceName());
                      }
                       }
                   }
                   break;
                case PresentRegisterData:
                   {
                       if (_table28 != NULL)
                       {
                           delete _table28;
                           _table28 = NULL;
                       }
                       if ( _table21 != NULL && _table00 != NULL)
                       {
                       _table28 = new CtiAnsiTable28( getApplicationLayer().getCurrentTable(),
                                                                 _table21->getNbrPresentDemands(),
                                                                 _table21->getNbrPresentValues(),
                                                                 _table21->getTimeRemainingFlag(),
                                                                 _table00->getRawNIFormat1(),
                                                                 _table00->getRawNIFormat2(),
                                                                 _table00->getRawTimeFormat(),
                                                                (_table00->getRawDataOrder() == 0)  );

                           if( _table28 != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                           _table28->printResult(getAnsiDeviceName());
                      }
                       }
                   }
                   break;
                   case ActualDisplay:
                   {
                       if (_table31 != NULL)
                       {
                           delete _table31;
                           _table31 = NULL;
                       }
                       _table31 = new CtiAnsiTable31( getApplicationLayer().getCurrentTable());

                       if( _table31 != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                      {
                            _table31->printResult(getAnsiDeviceName());
                      }
                   }
                   break;
                   case DisplaySource:
                   {
                       if (_table32 != NULL)
                       {
                           delete _table32;
                           _table32 = NULL;
                       }
                       if( _table31 != NULL )
                       {
                           _table32 = new CtiAnsiTable32( getApplicationLayer().getCurrentTable(),
                                                                 _table31->getNbrDispSources(),
                                                                  _table31->getWidthDispSources() );

                           if( _table32 != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {
                                _table32->printResult(getAnsiDeviceName());
                           }
                       }
                   }
                   break;
                   case PrimaryDisplayList:
                   {
                       if (_table33 != NULL)
                       {
                           delete _table33;
                           _table33 = NULL;
                       }
                       if( _table31 != NULL )
                       {
                           _table33 = new CtiAnsiTable33( getApplicationLayer().getCurrentTable(),
                                                                  _table31->getNbrPriDispLists(),
                                                                      _table31->getNbrPriDispListItems());

                           if( _table33 != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {

                               _table33->printResult(getAnsiDeviceName());
                           }
                      }
                   }
                   break;
                case ActualTimeAndTOU:
                   {
                       if (_table51 != NULL)
                       {
                           delete _table51;
                           _table51 = NULL;
                       }
                       _table51 = new CtiAnsiTable51( getApplicationLayer().getCurrentTable() );
                       if( _table51 != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                       {
                           _table51->printResult(getAnsiDeviceName());
                       }
                   }
                   break;
                case Clock:
                   {
                       if (_table52 != NULL)
                       {
                           delete _table52;
                           _table52 = NULL;
                       }

                       if( _table00 != NULL )
                       {
                           _table52 = new CtiAnsiTable52( getApplicationLayer().getCurrentTable(), _table00->getRawTimeFormat() );
                           if( _table52 != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                           {
                               _table52->printResult(getAnsiDeviceName());
                           }
                       }
                   }
                   break;
                case ActualLoadProfile:
                   {
                       if (_table61 != NULL)
                       {
                           delete _table61;
                           _table61 = NULL;
                       }
                       if( _table00 != NULL )
                       {
                           _table61 = new CtiAnsiTable61( getApplicationLayer().getCurrentTable(),
                                                          _table00->getStdTblsUsed(),
                                                          _table00->getDimStdTblsUsed(),
                                                          (_table00->getRawDataOrder() == 0)  );
                           if( _table61 != NULL )
                           {
                              if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                              {
                                  _table61->printResult(getAnsiDeviceName());
                              }

                              _lpNbrLoadProfileChannels = _table61->getNbrChansSet(1);
                           }
                       }
                   }
                   break;
                case LoadProfileControl:
                   {
                      if (_table62 != NULL)
                      {
                          delete _table62;
                          _table62 = NULL;
                      }
                      if( _table00 != NULL && _table61 != NULL )
                      {
                          _table62 = new CtiAnsiTable62( getApplicationLayer().getCurrentTable(), _table61->getLPDataSetUsedFlags(), _table61->getLPDataSetInfo(),
                                                             _table61->getLPScalarDivisorFlag(1), _table61->getLPScalarDivisorFlag(2), _table61->getLPScalarDivisorFlag(3),
                                                             _table61->getLPScalarDivisorFlag(4),  _table00->getRawStdRevisionNo() );
                          if( _table62 != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                          {
                              _table62->printResult(getAnsiDeviceName());
                          }
                      }
                   }

                   break;
               case LoadProfileStatus:
                   {
                      if (_table63 != NULL)
                       {
                           delete _table63;
                           _table63 = NULL;
                       }
                      if( _table61 != NULL )
                      {
                          _table63 = new CtiAnsiTable63( getApplicationLayer().getCurrentTable(), _table61->getLPDataSetUsedFlags(), (_table00->getRawDataOrder() == 0));
                          if( _table63 != NULL && getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_DATA_INFO) )
                          {
                              _table63->printResult(getAnsiDeviceName());
                          }
                      }

                   }
                   break;
                case LoadProfileDataSet1:
                {
                    if (_table64 != NULL)
                    {
                        delete _table64;
                        _table64 = NULL;
                    }
                    UINT16 validIntvls = 0;
                    if( _table63 != NULL )
                    {

                        validIntvls = _table63->getNbrValidIntvls(1);
                        if (getApplicationLayer().getPartialProcessLPDataFlag())
                        {
                            _lpNbrFullBlocks = (getApplicationLayer().getLPByteCount() / _lpBlockSize) - 1;
                            {
                                CtiLockGuard<CtiLogger> logger_guard(dout);
                                dout << CtiTime() << " ####### LP Byte Count = " <<getApplicationLayer().getLPByteCount() << endl;
                                dout << CtiTime() << " ####### LP Block Size = " <<_lpBlockSize << endl;
                                dout << CtiTime() << " ####### LP Nbr Full Blocks = " <<_lpNbrFullBlocks << endl;
                            }
                            _lpNbrIntvlsLastBlock = 0;//_table61->getNbrBlkIntsSet(1);
                            validIntvls = _lpNbrIntvlsLastBlock;
                            getApplicationLayer().setPartialProcessLPDataFlag(false);
                            if (_lpNbrFullBlocks > 0)
                                _forceProcessDispatchMsg = true;
                        }
                    }

                    int meterHour = 0;
                    bool timeZoneApplied = false;
                    if (_table52 != NULL)
                    {
                        meterHour = _table52->getClkCldrHour();
                        timeZoneApplied = _table52->getTimeZoneAppliedFlag();
                    }

                    if( _table00 != NULL && _table62 != NULL && _table61 != NULL )
                    {
                        _table64 = new CtiAnsiTable64( getApplicationLayer().getCurrentTable(), _lpNbrFullBlocks + 1,
                                                               _table61->getNbrChansSet(1), _table61->getClosureStatusFlag(),
                                                               _table61->getSimpleIntStatusFlag(), _table61->getNbrBlkIntsSet(1),
                                                               _table61->getBlkEndReadFlag(), _table61->getBlkEndPulseFlag(),
                                                               _table61->getExtendedIntStatusFlag(), _table61->getMaxIntTimeSet(1),
                                                               _table62->getIntervalFmtCde(1), validIntvls,
                                                               _table00->getRawNIFormat1(), _table00->getRawNIFormat2(),
                                                               _table00->getRawTimeFormat(), meterHour, timeZoneApplied,
                                                               (_table00->getRawDataOrder() == 0), _table63->isDataBlockOrderDecreasing(1),
                                                                _table63->isDataBlockOrderDecreasing(1));

                        getApplicationLayer().setLPDataMode( false, 0 );

                        if (_invalidLastLoadProfileTime && _table64 != NULL)
                        {
                            _header->lastLoadProfileTime = _table64->getLPDemandTime(0,0, isDataBlockOrderDecreasing());
                        }
                    }
                }

                   break;
                default:
                    break;
                }
            }

        }
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;

        _ansiAbortOperation = TRUE;
        _index = _header->numTablesRequested - 1;
        getApplicationLayer().terminateSession();
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
                updateMfgBytesExpected();
            }
        }
        else
        {
            if(isStdTableAvailableInMeter(_tables[_index].tableID))
            {

                _currentTableNotAvailableFlag = false;

                switch( _tables[_index].tableID )
                {
                   case Configuration:
                   {
                       _tables[_index].bytesExpected = 30;
                   }
                   break;
                   case GeneralManufacturerIdentification:
                   {
                       _tables[_index].bytesExpected = 24;
                   }
                   break;
                   case ProcedureResponse:
                   {
                       _tables[_index].bytesExpected = 5;
                   }
                   break;
                   case ActualSourcesLimiting:
                   {
                       _tables[_index].bytesExpected = 8;
                   }
                   break;
                   case UnitOfMeasureEntry:
                   {
                       _tables[_index].bytesExpected = 4 * _table11->getNumberUOMEntries();
                   }
                   break;

                case DemandControl:
                   {
                       _tables[_index].bytesExpected = 0;
                       if (_table11->getRawResetExcludeFlag())
                       {
                           _tables[_index].bytesExpected += 1;
                       }
                       if (_table11->getRawPFExcludeFlag())
                       {
                           _tables[_index].bytesExpected += 3;
                       }
                       // add array values
                       _tables[_index].bytesExpected += 2 * _table11->getNumberDemandControlEntries();
                   }
                   break;

                case DataControl:
                    {
                       _tables[_index].bytesExpected = _table11->getDataControlLength() *
                                                       _table11->getNumberDataControlEntries();
                    }
                   break;

                case Constants:
                   {
                       // NOTE: worrying about electrical only
                       //MULTIPLIER
                       _tables[_index].bytesExpected = sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
                       //OFFSET
                       if (!_table11->getRawNoOffsetFlag())
                       {
                           _tables[_index].bytesExpected += sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
                       }
                       //SET1_CONSTANTS
                       if (_table11->getRawSetOnePresentFlag())
                       {
                            _tables[_index].bytesExpected += 1;
                            _tables[_index].bytesExpected += ( 2 * sizeOfNonIntegerFormat (_table00->getRawNIFormat1()));
                       }
                       //SET2_CONSTANTS
                       if (_table11->getRawSetTwoPresentFlag())
                       {
                            _tables[_index].bytesExpected += 1;
                            _tables[_index].bytesExpected += ( 2 * sizeOfNonIntegerFormat (_table00->getRawNIFormat1())) ;
                       }
                       _tables[_index].bytesExpected *= _table11->getNumberConstantsEntries();
                   }
                   break;

                case SourceDefinition:
                   {
                        _tables[_index].bytesExpected = _table11->getNumberSources();
                   }
                   break;
                case Register:
                case ActualRegister:
                   {
                       _tables[_index].bytesExpected = 10;
                   }
                   break;
                case DataSelection:
                   {
                       _tables[_index].bytesExpected = (_table21->getNumberSummations() +
                                                        _table21->getNumberDemands() +
                                                        (2 * _table21->getCoinValues()) +
                                                        ((int)_table21->getNumberDemands() + 7)/ 8);
                   }
                   break;

                case CurrentRegisterData:
                   {
                       // get the size of a demands record first
                       int demandsRecSize = 0;
                       if (_table21->getTimeDateFieldFlag())
                       {
                           demandsRecSize += (_table21->getOccur() * sizeOfSTimeDate());
                       }

                       if (_table21->getCumDemandFlag())
                       {
                           demandsRecSize += sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
                       }

                       if (_table21->getContCumDemandFlag())
                       {
                           demandsRecSize += sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
                       }

                       demandsRecSize += (_table21->getOccur()  *
                                          sizeOfNonIntegerFormat (_table00->getRawNIFormat2()));

                       int coinRecSize = (_table21->getOccur()  *
                                          sizeOfNonIntegerFormat (_table00->getRawNIFormat2()));

                       _tables[_index].bytesExpected = _table21->getNumberSummations() *
                                                       sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
                       _tables[_index].bytesExpected += _table21->getNumberDemands() * demandsRecSize;
                       _tables[_index].bytesExpected += _table21->getCoinValues() * coinRecSize;


                       _tables[_index].bytesExpected += (_table21->getTiers() * _tables[_index].bytesExpected);


                        if (_table21->getDemandResetCtrFlag())
                        {
                             _tables[_index].bytesExpected += 1;
                        }
                   }
                   break;
                case FrozenRegisterData:
                    {
                        // get the size of a demands record first
                       int demandsRecSize = 0;
                       int registerInfoSize = 0;
                       if (_table21->getTimeDateFieldFlag())
                       {
                           demandsRecSize += (_table21->getOccur() * sizeOfSTimeDate());
                           registerInfoSize += sizeOfSTimeDate();
                       }
                       if (_table21->getSeasonInfoFieldFlag())
                       {
                           registerInfoSize += 1;
                       }
                       if (_table21->getCumDemandFlag())
                       {
                           demandsRecSize += sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
                       }

                       if (_table21->getContCumDemandFlag())
                       {
                           demandsRecSize += sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
                       }

                       demandsRecSize += (_table21->getOccur()  *
                                          sizeOfNonIntegerFormat (_table00->getRawNIFormat2()));

                       int coinRecSize = (_table21->getOccur()  *
                                          sizeOfNonIntegerFormat (_table00->getRawNIFormat2()));

                       _tables[_index].bytesExpected = _table21->getNumberSummations() *
                                                       sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
                       _tables[_index].bytesExpected += _table21->getNumberDemands() * demandsRecSize;
                       _tables[_index].bytesExpected += _table21->getCoinValues() * coinRecSize;


                       _tables[_index].bytesExpected += (_table21->getTiers() * _tables[_index].bytesExpected);


                        if (_table21->getDemandResetCtrFlag())
                        {
                             _tables[_index].bytesExpected += 1;
                        }
                        _tables[_index].bytesExpected += registerInfoSize;

                    }
                    break;
                case PresentRegisterSelection:
                   {
                       _tables[_index].bytesExpected += (_table21->getNbrPresentDemands() +
                                                         _table21->getNbrPresentValues());
                   }
                   break;
                case PresentRegisterData:
                    {
                        if (_table27 != NULL)
                        {
                            _tables[_index].bytesExpected += ( _table21->getNbrPresentDemands() * ( sizeOfNonIntegerFormat(_table00->getRawNIFormat2()) + 4 ))
                                                          + ( _table21->getNbrPresentValues() * sizeOfNonIntegerFormat (_table00->getRawNIFormat1()) );
                        }
                        else
                        {
                            _tables[_index].bytesExpected = 0;
                        }

                    }
                    break;
                case ActualDisplay:
                    {
                         _tables[_index].bytesExpected = 10;
                    }
                    break;
                case DisplaySource:
                    {
                         _tables[_index].bytesExpected += (_table31->getNbrDispSources() * ( _table31->getWidthDispSources() * 1));
                    }
                    break;
                case PrimaryDisplayList:
                    {
                        _tables[_index].bytesExpected += ((_table31->getNbrPriDispLists() * 3) + (_table31->getNbrPriDispListItems() * 2));
                    }
                    break;
                case SecondaryDisplayList:
                    {
                        _tables[_index].bytesExpected += ((_table31->getNbrSecDispLists() * 3) + (_table31->getNbrSecDispListItems() * 2));
                    }
                    break;
                case ActualTimeAndTOU:
                    {
                        _tables[_index].bytesExpected = 9;
                    }
                    break;
                case Clock:
                    {     _tables[_index].bytesExpected = 7;
                        // _tables[_index].bytesExpected = sizeof (LTIME_DATE) + 1; //LTIME_DATE + TIME_DATE_QUAL_BFLD
                    }
                    break;
                case ActualLoadProfile:
                    {
                       // if (useScanFlags())
                        {
                            _tables[_index].bytesExpected = 7; //LP_MEMORY_LEN + LP_FLAGS + LP_FMATS

                            int lpTbl[] = {64, 65, 66, 67};
                            unsigned char *stdTblsUsed = _table00->getStdTblsUsed();

                            if (_table00->getDimStdTblsUsed() > 8)
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
                case LoadProfileControl:
                    {
                       // if (useScanFlags())
                        {
                            bool * dataSetUsedFlags = _table61->getLPDataSetUsedFlags();
                            LP_DATA_SET *lp_data_set_info = _table61->getLPDataSetInfo();
                            _tables[_index].bytesExpected = 0;

                            for (int x = 0; x < 4; x++)
                            {
                                if (dataSetUsedFlags[x])
                                {
                                    _tables[_index].bytesExpected += (lp_data_set_info[x].nbr_chns_set * 3);
                                    _tables[_index].bytesExpected += 1;
                                    if (_table61->getLPScalarDivisorFlag(x+1))
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
                case LoadProfileStatus:
                    {
                        bool * dataSetUsedFlags = _table61->getLPDataSetUsedFlags();
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
                case LoadProfileDataSet1:
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
        _ansiAbortOperation = TRUE;
    }

}
void CtiProtocolANSI::updateMfgBytesExpected()
{
    switch( (_tables[_index].tableID - 0x800) )
    {
        case 0:
        {
            _tables[_index].bytesExpected = 59;
            break;
        }
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
   if (_table00 != NULL)
   {
       switch (_table00->getRawTimeFormat())
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
   if (_table00 != NULL)
   {
       switch (_table00->getRawTimeFormat())
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
bool CtiProtocolANSI::retreiveDemand( int offset, double *value, double *timestamp )
{
    try
    {

        bool success = false;
        if( _ansiAbortOperation )
        {
            return success;
        }
        unsigned char * demandSelect;
        int ansiOffset;
        int ansiTOURate;
        int ansiDeviceType = (int) getApplicationLayer().getAnsiDeviceType();

        ansiOffset = getUnitsOffsetMapping(offset);
        ansiTOURate = getRateOffsetMapping(offset);

        demandSelect = _table22->getDemandSelect();
        for (int x = 0; x < _table21->getNumberDemands(); x++)
        {
            if ((int) demandSelect[x] != 255)
            {
                if ((_table12->getRawTimeBase(demandSelect[x]) == 4 ||
                     _table12->getRawTimeBase(demandSelect[x]) == 2 ) &&
                    _table12->getRawIDCode(demandSelect[x]) == ansiOffset )
                {
                    success = true;
                    double multiplier = 1;
                    if (_table15 != NULL)
                    {
                        multiplier *= _table15->getElecMultiplier((demandSelect[x]%20));
                    }
                    if(ansiDeviceType == CtiANSIApplication::sentinel || ansiDeviceType == CtiANSIApplication::focus)
                    {
                         // 2 = sentinel
                         multiplier *= (_table12->getResolvedMultiplier(demandSelect[x])/1000);
                    }
                    else
                    {
                        multiplier /= 1000000000;
                    }

                    // will bring back value in KW/KVAR ...
                    *value = _table23->getDemandValue(x, ansiTOURate) * multiplier ;
                    *timestamp = _table23->getDemandEventTime( x, ansiTOURate );
                    if (_table52 != NULL)
                    {
                        *timestamp = _table52->adjustTimeZoneAndDST(*timestamp);
                    }


                    if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )//DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard< CtiLogger > doubt_guard( dout );
                        dout << " *value =   "<<*value<<endl;
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
        if( _ansiAbortOperation )
        {
            return success;
        }
        unsigned char* summationSelect;
        int ansiOffset;
        int ansiTOURate;
        int ansiDeviceType = (int) getApplicationLayer().getAnsiDeviceType();

        /* Watts = 0, Vars = 1, VA = 2, etc */
        ansiOffset = getUnitsOffsetMapping(offset);
        ansiTOURate = getRateOffsetMapping(offset);


        if (_table22 != NULL)
        {
            /* returns pointer to list of summation Selects */
            summationSelect = _table22->getSummationSelect();

            if (_table21 != NULL)
            {
                for (int x = 0; x < _table21->getNumberSummations(); x++)
                {
                    if ((int) summationSelect[x] != 255)
                    {
                        if (_table12 != NULL)
                        {
                            if ((_table12->getRawTimeBase(summationSelect[x]) == 0  ||
                                _table12->getRawTimeBase(summationSelect[x]) == 3 ||
                                _table12->getRawTimeBase(summationSelect[x]) == 2 )&&
                                _table12->getRawIDCode(summationSelect[x]) == ansiOffset)
                            {
                                double multiplier = 1;
                                if (_table16 != NULL  && _table23 != NULL)
                                {
                                    if (_table16->getConstantsFlag(summationSelect[x]) &&
                                        !_table16->getConstToBeAppliedFlag(summationSelect[x]))
                                    {
                                        if (_table15 != NULL)
                                        {
                                            multiplier *= _table15->getElecMultiplier(summationSelect[x]);
                                        }
                                    }
                                    if(ansiDeviceType == CtiANSIApplication::kv2 || ansiDeviceType == CtiANSIApplication::kv)
                                    {
                                        multiplier /= 1000000000;
                                    }
                                }
                                if(ansiDeviceType == CtiANSIApplication::sentinel || ansiDeviceType == CtiANSIApplication::focus)
                                {
                                    multiplier *= (_table12->getResolvedMultiplier(summationSelect[x]) / 1000);
                                }
                                *value = _table23->getSummationsValue(x, ansiTOURate) * multiplier;
                                success = true;
                                if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )//DEBUGLEVEL_LUDICROUS )
                                {
                                    CtiLockGuard< CtiLogger > doubt_guard( dout );
                                    dout << " *value =   "<<*value<<endl;
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
bool CtiProtocolANSI::retreiveFrozenDemand( int offset, double *value, double *timestamp )
{
    bool success = false;
    if( _ansiAbortOperation )
    {
        return success;
    }

    try
    {
        unsigned char * demandSelect;
        int ansiOffset;
        int ansiTOURate;
        int ansiDeviceType = (int) getApplicationLayer().getAnsiDeviceType();

        ansiOffset = getUnitsOffsetMapping(offset);
        ansiTOURate = getRateOffsetMapping(offset);

        demandSelect = _table22->getDemandSelect();
        for (int x = 0; x < _table21->getNumberDemands(); x++)
        {
            if ((int) demandSelect[x] != 255)
            {
                if (_table12->getRawTimeBase(demandSelect[x]) == 4 &&
                    _table12->getRawIDCode(demandSelect[x]) == ansiOffset)
                {
                    success = true;
                    double multiplier = 1;
                    if (_table16->getDemandCtrlFlag(demandSelect[x]) )
                    {
                        if (_table15 != NULL)
                        {
                            multiplier *= _table15->getElecMultiplier((demandSelect[x]%20));
                        }
                        if(ansiDeviceType == CtiANSIApplication::kv2 || ansiDeviceType == CtiANSIApplication::kv)
                        {
                            multiplier /= 1000000000;
                        }
                    }
                    if(ansiDeviceType == CtiANSIApplication::sentinel || ansiDeviceType == CtiANSIApplication::focus)
                    {
                        // will bring back value in KW/KVAR ...
                        multiplier *= (_table12->getResolvedMultiplier(demandSelect[x])/1000);

                    }

                    *value = _frozenRegTable->getDemandResetDataTable()->getDemandValue(x, ansiTOURate) * multiplier;
                    *timestamp = _frozenRegTable->getDemandResetDataTable()->getDemandEventTime( x, ansiTOURate );
                    if (_table52 != NULL)
                    {
                        *timestamp = _table52->adjustTimeZoneAndDST(*timestamp);
                    }
                    if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )//DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard< CtiLogger > doubt_guard( dout );
                        dout << " *value =   "<<*value<<endl;
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
bool CtiProtocolANSI::retreiveFrozenSummation( int offset, double *value, double *timestamp)
{
    bool success = false;
    if( _ansiAbortOperation )
    {
        return success;
    }

    unsigned char* summationSelect;
    int ansiOffset;
    int ansiTOURate;
    int ansiDeviceType = (int) getApplicationLayer().getAnsiDeviceType();

    /* Watts = 0, Vars = 1, VA = 2, etc */
    ansiOffset = getUnitsOffsetMapping(offset);
    ansiTOURate = getRateOffsetMapping(offset);

    try
    {
        if (_table22 != NULL)
        {
            /* returns pointer to list of summation Selects */
            summationSelect = _table22->getSummationSelect();

            if (_table21 != NULL)
            {
                for (int x = 0; x < _table21->getNumberSummations(); x++)
                {
                    if ((int) summationSelect[x] != 255)
                    {
                        if (_table12 != NULL)
                        {
                            if (_table12->getRawTimeBase(summationSelect[x]) == 0 &&
                                _table12->getRawIDCode(summationSelect[x]) == ansiOffset)
                            {
                                double multiplier = 1;
                                if (_table16 != NULL  && _table23 != NULL)
                                {
                                    if (_table16->getConstantsFlag(summationSelect[x]) &&
                                        !_table16->getConstToBeAppliedFlag(summationSelect[x]))
                                    {
                                        if (_table15 != NULL)
                                        {
                                            multiplier *= _table15->getElecMultiplier(summationSelect[x]);
                                        }
                                    }
                                    if(ansiDeviceType == CtiANSIApplication::kv2 || ansiDeviceType == CtiANSIApplication::kv)
                                    {
                                        multiplier /= 1000000000;
                                    }
                                }
                                if(ansiDeviceType == CtiANSIApplication::sentinel || ansiDeviceType == CtiANSIApplication::focus)
                                {
                                    multiplier *= (_table12->getResolvedMultiplier(summationSelect[x])/1000);
                                }
                                success = true;

                                *value = _frozenRegTable->getDemandResetDataTable()->getSummationsValue(x, ansiTOURate) * multiplier;
                                *timestamp = _frozenRegTable->getEndDateTime();

                                if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )//DEBUGLEVEL_LUDICROUS )
                                {
                                    CtiLockGuard< CtiLogger > doubt_guard( dout );
                                    dout << " *value =   "<<*value<<endl;
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
    if( _ansiAbortOperation )
    {
        return success;
    }
    unsigned char* presentValueSelect;
    int ansiOffset;
    int ansiDeviceType = (int) getApplicationLayer().getAnsiDeviceType();

    if (ansiDeviceType == CtiANSIApplication::kv2) //if 1, kv2 gets info from mfg tbl 110
    {
        try
        {
            success = retreiveMfgPresentValue(offset, value);
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
            if (_table27 != NULL)
            {
                /* returns pointer to list of present Value Selects */
                presentValueSelect = (unsigned char*)_table27->getValueSelect();

                if (_table21 != NULL && presentValueSelect != NULL)
                {
                    for (int x = 0; x < _table21->getNbrPresentValues(); x++)
                    {
                        if ((int) presentValueSelect[x] != 255)
                        {
                            if (_table12 != NULL)
                            {
                                if (_table12->getRawTimeBase(presentValueSelect[x]) == 1 &&
                                    _table12->getSegmentation(presentValueSelect[x]) == getSegmentationOffsetMapping(offset) &&
                                    _table12->getRawIDCode(presentValueSelect[x]) == ansiOffset)
                                {
                                    if (_table16 != NULL  && _table28 != NULL)
                                    {
                                        double multiplier = 1;
                                        if (_table16->getConstantsFlag(presentValueSelect[x]) &&
                                            !_table16->getConstToBeAppliedFlag(presentValueSelect[x]))
                                        {
                                            if (_table15 != NULL)
                                            {
                                                multiplier *= _table15->getElecMultiplier(presentValueSelect[x]); /*/ 1000000000*/
                                            }
                                        }
                                        *value = _table28->getPresentValue(x) * multiplier;
                                        success = true;
                                        if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )//DEBUGLEVEL_LUDICROUS )
                                        {
                                            CtiLockGuard< CtiLogger > doubt_guard( dout );
                                            dout << " *value =   "<<*value<<endl;
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
    if( _ansiAbortOperation )
    {
        return success;
    }
    unsigned char* presentDemandSelect;
    int ansiOffset;
    int ansiQuadrant;
    int ansiDeviceType = (int) getApplicationLayer().getAnsiDeviceType();

    if (ansiDeviceType == CtiANSIApplication::kv2) //if 1, kv2 gets info from mfg tbl 110
    {
        try
        {
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
            if (_table27 != NULL)
            {
                /* returns pointer to list of present Demand Selects */
                presentDemandSelect = _table27->getDemandSelect();

                if (_table21 != NULL && presentDemandSelect != NULL)
                {
                    for (int x = 0; x < _table21->getNbrPresentDemands(); x++)
                    {
                        if ((int) presentDemandSelect[x] != 255)
                        {
                            if (_table12 != NULL)
                            {
                                if (_table12->getRawTimeBase(presentDemandSelect[x]) == 4 &&
                                    _table12->getRawIDCode(presentDemandSelect[x]) == ansiOffset &&
                                    _table12->getQuadrantAccountabilityFlag(ansiQuadrant, presentDemandSelect[x]) )
                                {
                                    if (_table16 != NULL  && _table28 != NULL)
                                    {
                                        double multiplier = 1;
                                        if (_table16->getConstantsFlag(presentDemandSelect[x]) &&
                                            !_table16->getConstToBeAppliedFlag(presentDemandSelect[x]))
                                        {
                                            if (_table15 != NULL)
                                            {
                                                multiplier *= _table15->getElecMultiplier(presentDemandSelect[x]); /*/ 1000000000*/
                                            }
                                        }
                                        *value = _table28->getPresentDemand(x) * multiplier;
                                        success = true;
                                        if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )//DEBUGLEVEL_LUDICROUS )
                                        {
                                            CtiLockGuard< CtiLogger > doubt_guard( dout );
                                            dout << " *value =   "<<*value<<endl;
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

    if (ansiDeviceType != CtiANSIApplication::sentinel) //if 0,1, kv,kv2 not supported
    {
        return success;
    }
    else if( _ansiAbortOperation )
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
    if( _ansiAbortOperation )
    {
        return success;
    }

    ULONG value;

    string   str;
    int tempDiff;

    //tempStr = getValueAsString(METER_TIME_TOLERANCE);
    if( !(str = gConfigParms.getValueAsString(METER_TIME_TOLERANCE)).empty() )
        tempDiff = atoi(str.c_str());
    else
        tempDiff = 600;


    if (_table52 != NULL)
    {
        try
        {
            value = _table52->getMeterServerTimeDifference();
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
    if( _ansiAbortOperation )
    {
        return success;
    }

    UINT8* lpDemandSelect = NULL;
    int ansiOffset;

    /* Watts = 0, Vars = 1, VA = 2, etc */
    ansiOffset = getUnitsOffsetMapping(offset);

    try
    {

        if (_table62 != NULL)
        {
            /* returns pointer to list of LP Demand Selects from either dataSet 1,2,3,or 4*/
            lpDemandSelect = _table62->getLPDemandSelect(dataSet);
            if (_table61 != NULL)
            {
                for (int x = 0; x < _table61->getNbrChansSet(dataSet); x++)
                {
                    if ((int) lpDemandSelect[x] != 255)
                    {

                        if (_table12 != NULL)
                        {

                           if ((_table12->getRawTimeBase(lpDemandSelect[x]) == 3 ||
                                _table12->getRawTimeBase(lpDemandSelect[x]) == 5 ||
                                 _table12->getRawTimeBase(lpDemandSelect[x]) == 1 ||
                                 _table12->getRawTimeBase(lpDemandSelect[x]) == 0) &&
                                _table12->getRawIDCode(lpDemandSelect[x]) == ansiOffset)
                            {

                                if (_table64 != NULL &&
                                    (_lpNbrFullBlocks > 0 || _lpNbrIntvlsLastBlock > 0))
                                {
                                    success = true;
                                    /*if (!_table16->getConstantsFlag(lpDemandSelect[x]) &&
                                        !_table16->getConstToBeAppliedFlag(lpDemandSelect[x]))
                                    { */
                                        switch (dataSet)
                                        {
                                            case 1:
                                                {
                                                    int intvlsPerBlk = _table61->getNbrBlkIntsSet(dataSet);
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
                                                        if (_table62->getNoMultiplierFlag(dataSet) && _table15 != NULL) //no_multiplier_flag == true (constants need to be applied)
                                                        {
                                                            _lpValues[y] = ((_table64->getLPDemandValue ( x, blkIndex, intvlIndex ) *
                                                                           (_table15->getElecMultiplier((lpDemandSelect[x]%20)))) /
                                                                            (_table12->getResolvedMultiplier(lpDemandSelect[x]) * 1000) *
                                                                            (60 / _table61->getMaxIntTimeSet(dataSet)) ) ;
                                                            _lpTimes[y] = _table64->getLPDemandTime (blkIndex, intvlIndex,_table63->isDataBlockOrderDecreasing(dataSet));
                                                            if (_table52 != NULL)
                                                            {
                                                                _lpTimes[y] = _table52->adjustTimeZoneAndDST(_lpTimes[y]);

                                                            }
                                                            if (_table64->getPowerFailFlag(blkIndex, intvlIndex))
                                                                _lpQuality[y] = PowerfailQuality; //powerFailQuality
                                                            else
                                                                _lpQuality[y] = translateAnsiQualityToYukon(_table64->getExtendedIntervalStatus(x, blkIndex, intvlIndex));

                                                            if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )//DEBUGLEVEL_LUDICROUS )
                                                            {
                                                                CtiLockGuard< CtiLogger > doubt_guard( dout );
                                                                dout << endl <<"    **lpTime:  " << CtiTime(_lpTimes[y]) << "  lpValue: "<<_lpValues[y]<< "  lpQuality: "<<(int)_lpQuality[y];
                                                            }
                                                        }
                                                        else
                                                        {
                                                            _lpValues[y] = ( _table64->getLPDemandValue ( x, blkIndex, intvlIndex ) /
                                                                           (_table12->getResolvedMultiplier(lpDemandSelect[x]) * 1000) *
                                                                             (60 / _table61->getMaxIntTimeSet(dataSet)) ) ;
                                                            _lpTimes[y] = _table64->getLPDemandTime (blkIndex, intvlIndex, _table63->isDataBlockOrderDecreasing(dataSet));
                                                            if (_table52 != NULL)
                                                            {
                                                                _lpTimes[y] = _table52->adjustTimeZoneAndDST(_lpTimes[y]);

                                                            }
                                                            if (_table64->getPowerFailFlag(blkIndex, intvlIndex))
                                                                _lpQuality[y] = PowerfailQuality; //powerFailQuality
                                                            else
                                                                _lpQuality[y] = translateAnsiQualityToYukon(_table64->getExtendedIntervalStatus(x, blkIndex, intvlIndex));


                                                            if( getApplicationLayer().getANSIDebugLevel(DEBUGLEVEL_ACTIVITY_INFO) )//DEBUGLEVEL_LUDICROUS )
                                                            {
                                                                CtiLockGuard< CtiLogger > doubt_guard( dout );
                                                                dout << endl <<"    **lpTime:  " << CtiTime(_lpTimes[y]) << "  lpValue: "<<_lpValues[y]<< "  lpQuality: "<<(int)_lpQuality[y]<<endl;
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
                                                    value[y] = _table65->getLPDemandValue ( x, 1, 1 );
                                                }
                                                break;
                                            case 3:
                                                {
                                                    value[y] = _table66->getLPDemandValue ( x, 1, 1 );
                                                }
                                                break;
                                            case 4:
                                                {
                                                    value[y] = _table67->getLPDemandValue ( x, 1, 1 );
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
        int nbrChnsSet = _table61->getNbrChansSet(dataSetNbr);
        int nbrBlkIntsSet = _table63->getNbrValidIntvls(dataSetNbr);


        sizeOfLpBlkDatRcd += sizeOfSTimeDate() +
                          (nbrChnsSet * getSizeOfLPReadingsRcd());

        if (_table61->getClosureStatusFlag())
        {
            sizeOfLpBlkDatRcd += (nbrChnsSet * 2); //uint16 closure_status_bfld
        }
        if (_table61->getSimpleIntStatusFlag())
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
        int nbrChnsSet = _table61->getNbrChansSet(dataSetNbr);
        int nbrBlkIntsSet = _table61->getNbrBlkIntsSet(dataSetNbr);


        sizeOfLpBlkDatRcd += sizeOfSTimeDate() +
                          (nbrChnsSet * getSizeOfLPReadingsRcd());

        if (_table61->getClosureStatusFlag())
        {
            sizeOfLpBlkDatRcd += (nbrChnsSet * 2); //uint16 closure_status_bfld
        }
        if (_table61->getSimpleIntStatusFlag())
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

        if (_table61->getBlkEndReadFlag())
        {
            sizeOfReadingsRcd += sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
        }
        if (_table61->getBlkEndPulseFlag())
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
        int nbrChnsSet = _table61->getNbrChansSet(dataSetNbr);

        if (_table61->getExtendedIntStatusFlag())
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
        switch (_table62->getIntervalFmtCde(dataSetNbr))
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
                sizeOfIntFmtRcd += sizeOfNonIntegerFormat (_table00->getRawNIFormat1());
                break;
            }
            case 128:
            {
                sizeOfIntFmtRcd += sizeOfNonIntegerFormat (_table00->getRawNIFormat2());

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

        _tables[_index].tableID = ProcedureInitiate;
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
bool CtiProtocolANSI::isDataBlockOrderDecreasing()
{
    if( _table63 != NULL )
    {
        return _table63->isDataBlockOrderDecreasing(1);
    }
    return false;
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
    if (_table64 != NULL)
        return _forceProcessDispatchMsg;
    else if (_table23 != NULL || _table52 != NULL)
        return true;
    else
        return false;
}

bool CtiProtocolANSI::isTimeUninitialized(double timestamp)
{
    CtiTime t1 = CtiTime(timestamp);
    CtiTime t2 = CtiTime(CtiDate(1,1,2000));
    if ( t1.seconds() == t2.seconds() )
        return true;
    else
        return false;
}



 void CtiProtocolANSI::destroyManufacturerTables( void )
 {
     return;
 }
 void CtiProtocolANSI::convertToManufacturerTable( BYTE *data, BYTE numBytes, short aTableID )
 {
     return;
 }

bool CtiProtocolANSI::snapshotData()
{
    return false;
}

bool CtiProtocolANSI::batteryLifeData()
{
    return false;
}

int CtiProtocolANSI::getGoodBatteryReading()
{
    return 0;
}
int CtiProtocolANSI::getCurrentBatteryReading()
{
    return 0;
}
int CtiProtocolANSI::getDaysOnBatteryReading()
{
   return 0;
}
bool CtiProtocolANSI::retreiveMfgPresentValue( int offset, double *value )
{
    return false;
}


int CtiProtocolANSI::calculateLPDataBlockStartIndex(ULONG lastLPTime)
{
    int nbrIntervals = 0;
    int nbrValidInts = getNbrValidIntvls();
    int nbrIntsPerBlock = getNbrIntervalsPerBlock();
    int nbrBlksSet = getNbrValidBlks();
    CtiTime currentTime;


    currentTime.now();
    nbrIntervals =  (int)(abs((long)(currentTime.seconds() - lastLPTime))/60) / getMaxIntervalTime();
    if (nbrIntervals > (((nbrBlksSet-1) * nbrIntsPerBlock) + nbrValidInts))
    {
        nbrIntervals = (((nbrBlksSet-1) * nbrIntsPerBlock) + nbrValidInts);
    }
    if (isDataBlockOrderDecreasing())
    {
        return 0;
    }
    if (nbrIntervals <= nbrValidInts)
    {
        // lastBlockIndex;
        return getLastBlockIndex();
    }
    else if ((nbrIntervals - nbrValidInts) > nbrIntsPerBlock)
    {
        return getLastBlockIndex() - ((nbrIntervals - nbrValidInts) / nbrIntsPerBlock);
    }
    else //(nbrIntervals - nbrValidInts) <= nbrIntsPerBlock
    {
        // lastBlockIndex -  1;
        return getLastBlockIndex() - 1;
    }

}
