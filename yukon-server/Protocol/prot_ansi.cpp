
#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2005/01/25 18:33:51 $
*    History: 
      $Log: prot_ansi.cpp,v $
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

#include <rw/cstring.h>

#include "guard.h"
#include "logger.h"
#include "pointdefs.h"
#include "prot_ansi.h"


//=========================================================================================================================================
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
   _tableFiveOne = NULL; 
   _tableFiveTwo = NULL;
   _tableSixOne = NULL;
   _tableSixTwo = NULL;
   _tableSixThree = NULL;
   _tableSixFour = NULL;
   //_tableFiveFive = NULL;

   _validFlag = false;
   _entireTableFlag = false;
   //_clearMfgTables = false;
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

   _currentTableNotAvailableFlag = false;

}

//=========================================================================================================================================
//FIXME - I'm very broken....
//=========================================================================================================================================

void CtiProtocolANSI::destroyMe( void )
{
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " Ansi destroy started----" << endl;
   }

   //let's check to see if we have valid pointers to delete (for scanners sake)
   if( _header != NULL )
   {
      delete _header;
      _header=NULL;
   }

   if( _tables != NULL )
   {
      delete _tables;
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


   if( _billingTable != NULL )
   {
      delete _billingTable;
      _billingTable = NULL;
   }

   /*if( _lpValues != NULL )
   {
      delete []_lpValues;
      _lpValues = NULL;
   }

   if( _lpTimes != NULL )
   {
      delete []_lpTimes;
      _lpTimes = NULL;
   }
      */
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " ----Ansi destroy finished" << endl;
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
      dout << RWTime() << " Ansi reinit started----" << endl;
   }

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
   //_clearMfgTables = false;
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " ----Ansi reinit finished" << endl;
   }

}
/*
//=========================================================================================================================================
//scanner side:
//we want to grab the tables that are needed for a general scan of the ansi meter (kv2) and stick them in the outmessage, then we'll send
//the list to porter
//=========================================================================================================================================

void CtiProtocolANSI::getGeneralScanTables( BYTE *ptrToOutmessageBuffer )
{
   _tableList[0] = 0;
   _tableList[1] = 1;

   getTables( ptrToOutmessageBuffer, 2 );

   memset( _tableList, NULL, sizeof( _tableList ) );
}

//=========================================================================================================================================
//this is just a test method at this point, but it could be a getBillingTables() or getGeneralScanTables() just by adjusting the ID's
//requested...
//=========================================================================================================================================

void CtiProtocolANSI::getBillingTables( BYTE *ptrToOutmessageBuffer )
{
   //these are the ansi-table ID's that we want
   _tableList[0] = 0;
   _tableList[1] = 1;
   _tableList[2] = 11;
   _tableList[3] = 12;
   _tableList[4] = 13;
   _tableList[5] = 14;
   _tableList[6] = 15;
   _tableList[7] = 16;
   _tableList[8] = 21;
   _tableList[9] = 22;
   _tableList[10] = 23;
   _tableList[11] = 52;

   getTables( ptrToOutmessageBuffer, 12 );

   memset( _tableList, NULL, sizeof( _tableList ) );
}

//=========================================================================================================================================
//a general request builder that we can pass any number of table ids that we want
//
// NOTE: not sure if we'll be able to get tables with ID larger than 255 if we use a byte array (_tableList) to hold the ids on the way in
//............hmmmmmmmmmmmmmmm...
//=========================================================================================================================================

void CtiProtocolANSI::getTables( BYTE *ptrToOutmessageBuffer, int numTables )
{
   int   index;
   int   temp = 0;

   _tables = new ANSI_TABLE_WANTS[numTables];

   for( index = 0; index < numTables; index++ )
   {
      _tables[index].tableID = _tableList[index];
      _tables[index].tableOffset = 0;
//      _tables[index].bytesExpected = getTableSize( _tableList[index] );
      _tables[index].bytesExpected = 0;
   }

   temp = sizeof( ANSI_TABLE_WANTS ) * index;

   if( ptrToOutmessageBuffer != NULL )
      memcpy( ptrToOutmessageBuffer, _tables, temp );

//   _numberOfTablesToGet = numTables;
}
*/
//=========================================================================================================================================
//porter side:
//we take the outmessage that we assembled above on the scanner side and start it on it's journey through the layers of the ansi protocol
//=========================================================================================================================================

int CtiProtocolANSI::recvOutbound( OUTMESS *OutMessage )
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

   return( _header->numTablesRequested );   //just a val
}

// needed on both sides
void CtiProtocolANSI::buildWantedTableList( BYTE *aPtr )
{
    BYTE *bufptr = aPtr;
    int sizeOfPswd = 20;

    _header = CTIDBG_new WANTS_HEADER;

    if( _header != NULL )
    {

       memcpy( ( void *)_header, bufptr, sizeof( WANTS_HEADER ) );
       bufptr += sizeof( WANTS_HEADER );

       getApplicationLayer().setPassword(bufptr);
       bufptr += sizeOfPswd;

       _tables = CTIDBG_new ANSI_TABLE_WANTS[_header->numTablesRequested];

       if( _tables != NULL )
       {
          for( int x = 0; x < _header->numTablesRequested; x++ )
          {
             memcpy( ( void *)&_tables[x], bufptr, sizeof( ANSI_TABLE_WANTS ));
             bufptr += sizeof( ANSI_TABLE_WANTS );
          }
       }

       _scanOperation = ( CtiProtocolANSI::ANSI_SCAN_OPERATION ) *bufptr;
       bufptr += 1;
    }
    setAnsiDeviceType();
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
   bool  done = false;

   done = getApplicationLayer().decode( xfer, status );

   if( getApplicationLayer().isTableComplete())
   {
       if ((_tables[_index].type == ANSI_TABLE_TYPE_STANDARD && isStdTableAvailableInMeter(_tables[_index].tableID)) ||
           (_tables[_index].type == ANSI_TABLE_TYPE_MANUFACTURER && isMfgTableAvailableInMeter(_tables[_index].tableID - 0x0800)))
       {
           if (_tables[_index].operation == ANSI_OPERATION_READ) 
           {
               convertToTable();
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

           if (_scanOperation == demandReset && _tables[_index].tableID == 1) //1 = demand reset
           {
                int i = proc09RemoteReset(1);
                if (i)
                {
                    return true;
                }
           }

          if (/*_tables[_index].tableID == 62  || */_tables[_index].tableID == 22)
          {
              int julie = snapshotData();
              if (julie < 0)
              {
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

              if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
              {
                      CtiLockGuard< CtiLogger > doubt_guard( dout );
                      dout <<  "  ** DEBUG **** _lpNbrIntvlsLastBlock  " <<_lpNbrIntvlsLastBlock << endl;
                      dout <<  "  ** DEBUG **** _lpLastBlockIndex  " <<_lpLastBlockIndex << endl;
              }
              _lpBlockSize = getSizeOfLPDataBlock(1);
              _lpLastBlockSize = calculateLPLastDataBlockSize(_lpNbrLoadProfileChannels,_lpNbrIntvlsLastBlock);

              if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
              {
                      CtiLockGuard< CtiLogger > doubt_guard( dout );
                      dout <<  "  ** DEBUG **** _lpBlockSize  " <<_lpBlockSize << endl;
                      dout <<  "  ** DEBUG **** _lpLastBlockSize  " <<_lpLastBlockSize << endl;
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

              if (_tables[_index].tableID == 15)
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
          if (_tableZeroZero != NULL) 
              _tableZeroZero->printResult();
          if (_tableZeroOne != NULL)
              _tableZeroOne->printResult();
          if (_tableOneOne != NULL)
              _tableOneOne->printResult();
          if (_tableOneTwo != NULL)
              _tableOneTwo->printResult();
          if (_tableOneThree != NULL)
              _tableOneThree->printResult();
          if (_tableOneFour != NULL)
              _tableOneFour->printResult();
          if (_tableOneFive != NULL)
              _tableOneFive->printResult();
          if (_tableOneSix != NULL)
              _tableOneSix->printResult();
          if (_tableTwoOne != NULL)
              _tableTwoOne->printResult();
          if (_tableTwoTwo != NULL)
              _tableTwoTwo->printResult();
          if (_tableTwoThree != NULL)
              _tableTwoThree->printResult();
          if (_tableTwoSeven != NULL)
              _tableTwoSeven->printResult();
          if (_tableTwoEight != NULL)
              _tableTwoEight->printResult();
          if (_tableFiveOne != NULL)
              _tableFiveOne->printResult();
          if (_tableFiveTwo != NULL)
              _tableFiveTwo->printResult();
          if (_tableSixOne != NULL)
              _tableSixOne->printResult();
          if (_tableSixTwo != NULL)
              _tableSixTwo->printResult();
          if (_tableSixThree != NULL)
              _tableSixThree->printResult();
          if (_tableSixFour != NULL)
              _tableSixFour->printResult();
          if (_tableZeroEight != NULL)
              _tableZeroEight->printResult();


          // done with tables, do the termination etc
          getApplicationLayer().terminateSession();
      }
   }
   
   return( done ); //just a val
}

//=========================================================================================================================================
//we've gotten table data back from the device, so we'll paste it into the appropriate table structure
//we may decode it here, we may send it somewhere else....
//=========================================================================================================================================
void CtiProtocolANSI::convertToTable(  )
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
                  _tableZeroZero = new CtiAnsiTableZeroZero( getApplicationLayer().getCurrentTable() );
                  _tableZeroZero->printResult();

                  setTablesAvailable(_tableZeroZero->getStdTblsUsed(), _tableZeroZero->getDimStdTblsUsed(),
                                     _tableZeroZero->getMfgTblsUsed(), _tableZeroZero->getDimMfgTblsUsed());
               }
               break;

            case 1:
               {
                  _tableZeroOne = new CtiAnsiTableZeroOne( getApplicationLayer().getCurrentTable(), 
                                                           _tableZeroZero->getRawMfgSerialNumberFlag(), 
                                                           _tableZeroZero->getRawIdFormat() );
                  _tableZeroOne->printResult();
               }
               break;
           case 8:
               {
                  _tableZeroEight = new CtiAnsiTableZeroEight( getApplicationLayer().getCurrentTable());
                  _tableZeroEight->printResult();

                  _lpStartBlockIndex = _tableZeroEight->getLPOffset();         
                  _lpOffset = _lpStartBlockIndex * _lpBlockSize;                  
                  _lpNbrFullBlocks = _lpLastBlockIndex - _lpStartBlockIndex; 

               }
               break;

            case 10:
               {
                  _tableOneZero = new CtiAnsiTableOneZero( getApplicationLayer().getCurrentTable() );
                 // _tableOneZero->printResult();
               }
               break;

            case 11:
               {
                  _tableOneOne = new CtiAnsiTableOneOne( getApplicationLayer().getCurrentTable() );
                  _tableOneOne->printResult();
               }
                 break;

            case 12:
               {
                  _tableOneTwo = new CtiAnsiTableOneTwo( getApplicationLayer().getCurrentTable(), 
                                                         _tableOneOne->getNumberUOMEntries() );
                  _tableOneTwo->printResult();
               }
               break;

            case 13:
               {
                  _tableOneThree = new CtiAnsiTableOneThree( getApplicationLayer().getCurrentTable(), 
                                                             _tableOneOne->getNumberDemandControlEntries(), 
                                                             _tableOneOne->getRawPFExcludeFlag(),
                                                                     _tableOneOne->getRawSlidingDemandFlag(),
                                                                     _tableOneOne->getRawResetExcludeFlag() );
                  _tableOneThree->printResult();
               }
               break;

            case 14:
               {
                  _tableOneFour = new CtiAnsiTableOneFour( getApplicationLayer().getCurrentTable(), 
                                                           _tableOneOne->getDataControlLength(), 
                                                           _tableOneOne->getNumberDataControlEntries());
                  _tableOneFour->printResult();
               }
               break;

            case 15:
               {
                  _tableOneFive = new CtiAnsiTableOneFive( getApplicationLayer().getCurrentTable(), 
                                                           _tableOneOne->getRawConstantsSelector(), 
                                                           _tableOneOne->getNumberConstantsEntries(),
                                                           _tableOneOne->getRawNoOffsetFlag(),
                                                           _tableOneOne->getRawSetOnePresentFlag(),
                                                           _tableOneOne->getRawSetTwoPresentFlag(),
                                                           _tableZeroZero->getRawNIFormat1(),
                                                           _tableZeroZero->getRawNIFormat2() );

                  getApplicationLayer().setLPDataMode( false, 0 );
                   _tableOneFive->printResult();
               }
               break;

            case 16:
               {
                  _tableOneSix = new CtiAnsiTableOneSix( getApplicationLayer().getCurrentTable(), 
                                                         _tableOneOne->getNumberSources() );
                  _tableOneSix->printResult();
               }
               break;

            case 21:
               {
                  _tableTwoOne = new CtiAnsiTableTwoOne( getApplicationLayer().getCurrentTable() );
                  _tableTwoOne->printResult();
               }
               break;

            case 22:
               {
                  _tableTwoTwo = new CtiAnsiTableTwoTwo( getApplicationLayer().getCurrentTable(), 
                                                         _tableTwoOne->getNumberSummations(), 
                                                         _tableTwoOne->getNumberDemands(),
                                                         _tableTwoOne->getCoinValues() );
                  _tableTwoTwo->printResult();
               }
               break;

            case 23:
               {
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
                                                             _tableZeroZero->getRawTimeFormat() );
                  _tableTwoThree->printResult();
               }
               break;
            case 27:
               {
                   _tableTwoSeven = new CtiAnsiTableTwoSeven( getApplicationLayer().getCurrentTable(),
                                                             _tableTwoOne->getNbrPresentDemands(), 
                                                             _tableTwoOne->getNbrPresentValues());
                  _tableTwoSeven->printResult();
               }
               break;
            case 28:
               {
                   _tableTwoEight = new CtiAnsiTableTwoEight( getApplicationLayer().getCurrentTable(),
                                                             _tableTwoOne->getNbrPresentDemands(), 
                                                             _tableTwoOne->getNbrPresentValues(),
                                                             _tableTwoOne->getTimeRemainingFlag(), 
                                                             _tableZeroZero->getRawNIFormat1(), 
                                                             _tableZeroZero->getRawNIFormat2(),
                                                             _tableZeroZero->getRawTimeFormat() );

                  _tableTwoEight->printResult();
               }
               break;
            case 51:
               {
                  _tableFiveOne = new CtiAnsiTableFiveOne( getApplicationLayer().getCurrentTable() );
                  _tableFiveOne->printResult();
               }
               break;
            case 52:
               {
                  _tableFiveTwo = new CtiAnsiTableFiveTwo( getApplicationLayer().getCurrentTable(), _tableZeroZero->getRawTimeFormat() );
                  _tableFiveTwo->printResult();
               }
               break;

            /*case 55:
               {
                  _tableFiveFive = new CtiAnsiTableFiveFive( getApplicationLayer().getCurrentTable() );
                  _tableFiveFive->printResult();
               }

               break; */
            case 61:
               {
                  _tableSixOne = new CtiAnsiTableSixOne( getApplicationLayer().getCurrentTable(), _tableZeroZero->getStdTblsUsed(), _tableZeroZero->getDimStdTblsUsed() );
                  _tableSixOne->printResult();

                  _lpNbrLoadProfileChannels = _tableSixOne->getNbrChansSet(1);

               }

               break;
            case 62:
               {

                  _tableSixTwo = new CtiAnsiTableSixTwo( getApplicationLayer().getCurrentTable(), _tableSixOne->getLPDataSetUsedFlags(), _tableSixOne->getLPDataSetInfo(),
                                                         _tableSixOne->getLPScalarDivisorFlag(1), _tableSixOne->getLPScalarDivisorFlag(2), _tableSixOne->getLPScalarDivisorFlag(3),
                                                         _tableSixOne->getLPScalarDivisorFlag(4),  _tableZeroZero->getRawStdRevisionNo() );
                  _tableSixTwo->printResult(); 
               }
                         
               break;
           case 63:
               {

                  _tableSixThree = new CtiAnsiTableSixThree( getApplicationLayer().getCurrentTable(), _tableSixOne->getLPDataSetUsedFlags());
                  _tableSixThree->printResult(); 
               }
               break;
            case 64:
            {
                _tableSixFour = new CtiAnsiTableSixFour( getApplicationLayer().getCurrentTable(), _lpNbrFullBlocks + 1,
                                                       _tableSixOne->getNbrChansSet(1), _tableSixOne->getClosureStatusFlag(), 
                                                       _tableSixOne->getSimpleIntStatusFlag(), _tableSixOne->getNbrBlkIntsSet(1),
                                                       _tableSixOne->getBlkEndReadFlag(), _tableSixOne->getBlkEndPulseFlag(),
                                                       _tableSixOne->getExtendedIntStatusFlag(), _tableSixOne->getMaxIntTimeSet(1),
                                                       _tableSixTwo->getIntervalFmtCde(1), _tableSixThree->getNbrValidIntvls(1),
                                                       _tableZeroZero->getRawNIFormat1(), _tableZeroZero->getRawNIFormat2(), 
                                                       _tableZeroZero->getRawTimeFormat() );
                
                getApplicationLayer().setLPDataMode( false, 0 );
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

// only used for standard tables possibly larger than one data packet size
void CtiProtocolANSI::updateBytesExpected( )
{
    // if its manufactured, send it to the child class
    if (_tables[_index].type == ANSI_TABLE_TYPE_MANUFACTURER)
    {
        if (isMfgTableAvailableInMeter(_tables[_index].tableID))
        {
            switch( _tables[_index].tableID )
            {
                case 0:
                {
                    _tables[_index].bytesExpected = 59; 
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
                                                    (int)_tableTwoOne->getNumberDemands() / 8);
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
                break;
            case 62:
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
                dout <<  "DEBUG:  Table " << _tables[_index].tableID << " NOT PRESENT IN METER -- 0 BYTES EXPECTED" << endl;    
            }

        }
        
    }
    {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << RWTime() << "DEBUG:  Table " << _tables[_index].tableID << " expected bytes " << (int)_tables[_index].bytesExpected << endl;
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

bool CtiProtocolANSI::isTransactionComplete( void )
{
    // trying to decide if we are done with our attempts
   return (getApplicationLayer().isReadComplete() || getApplicationLayer().isReadFailed());
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
        dout << RWTime::now() << " ==============================================" << endl;
        dout << RWTime::now() << " ==========The KV2 responded with data=========" << endl;
        dout << RWTime::now() << " ==============================================" << endl;
    }
       
          
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << RWTime::now() << " ==============================================" << endl;
        dout << RWTime::now() << " ================= Complete ===================" << endl;
        dout << RWTime::now() << " ==============================================" << endl;
    }

}

////////////////////////////////////////////////////////////////////////////////////
// Demand - KW, KVAR, KVA, etc...
////////////////////////////////////////////////////////////////////////////////////
bool CtiProtocolANSI::retreiveDemand( int offset, double *value )
{
    bool success = false;
    unsigned char * demandSelect;
    int ansiOffset;

    ansiOffset = getUnitsOffsetMapping(offset);
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
                        *value = ((_tableTwoThree->getDemandValue(x) * 
                               _tableOneFive->getElecMultiplier((demandSelect[x]%20))) / 1000000000);
                    }
                    else
                    {
                        *value = (_tableTwoThree->getDemandValue(x)  / 1000000000);
                    }
                    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                    {
                        CtiLockGuard< CtiLogger > doubt_guard( dout );
                        dout << " *value =   "<<*value<<endl;
                    }
                }
                else
                {
                    *value = _tableTwoThree->getDemandValue(x);
                }
                break;
            }

        }
    }
    demandSelect = NULL;
    return success;
}
////////////////////////////////////////////////////////////////////////////////////
// Summations = Energy - KWH, KVARH, KVAH, etc...
////////////////////////////////////////////////////////////////////////////////////
bool CtiProtocolANSI::retreiveSummation( int offset, double *value )
{
    bool success = false;
    unsigned char* summationSelect;
    int ansiOffset;

    /* Watts = 0, Vars = 1, VA = 2, etc */
    ansiOffset = getUnitsOffsetMapping(offset);


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
                                        *value = ((_tableTwoThree->getSummationsValue(x) * 
                                               _tableOneFive->getElecMultiplier(summationSelect[x])) / 1000000000);
                                    }
                                    else
                                    {    
                                        *value = (_tableTwoThree->getSummationsValue(x) / 1000000000);
                                    }
                                    success = true;
                                    if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                                    {
                                        CtiLockGuard< CtiLogger > doubt_guard( dout );
                                        dout << " *value =   "<<*value<<endl;
                                    }
                                }
                                else
                                {
                                    *value = _tableTwoThree->getSummationsValue(x);
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
        success = retreiveKV2PresentValue(offset, value);
        if (success)
        {
            if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
            {
                CtiLockGuard< CtiLogger > doubt_guard( dout );
                dout << " *value =   "<<*value<<endl;
            }
        }
    }
    else
    {
        /* Watts = 0, Vars = 1, VA = 2, Volts = 8, Current = 12, etc */
        ansiOffset = getUnitsOffsetMapping(offset);


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
                                        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
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
    presentValueSelect = NULL;
    return success;
}

////////////////////////////////////////////////////////////////////////////////////
// LP Demands - KW, KVAR, KVA, etc...
////////////////////////////////////////////////////////////////////////////////////
bool CtiProtocolANSI::retreiveLPDemand( int offset, int dataSet )
{
    bool success = false;
    UINT8* lpDemandSelect;
    int ansiOffset;

    /* Watts = 0, Vars = 1, VA = 2, etc */
    ansiOffset = getUnitsOffsetMapping(offset);

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

                            if (_tableSixFour != NULL)
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
                                                _lpValues = new double[totalIntvls + 1];
                                                _lpTimes = new ULONG[totalIntvls +1];
                                                int intvlIndex = 0;
                                                for (int y = 0; y < totalIntvls; y++) 
                                                {
                                                    if (_tableSixTwo->getNoMultiplierFlag(dataSet) && _tableOneFive != NULL) //no_multiplier_flag == true (constants need to be applied)
                                                    {
                                                        _lpValues[y] = _tableSixFour->getLPDemandValue ( x, blkIndex, intvlIndex )
                                                                        * (_tableOneFive->getElecMultiplier((lpDemandSelect[x]%20)));
                                                        _lpTimes[y] = _tableSixFour->getLPDemandTime (blkIndex, intvlIndex);
                                                        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                                                        {
                                                            CtiLockGuard< CtiLogger > doubt_guard( dout );
                                                            dout << "    **lpTime:  " << RWTime(_lpTimes[y]) << "  lpValue: "<<_lpValues[y]<<endl;
                                                        }
                                                    }
                                                    else
                                                    {
                                                        _lpValues[y] = _tableSixFour->getLPDemandValue ( x, blkIndex, intvlIndex );
                                                        _lpTimes[y] = _tableSixFour->getLPDemandTime (blkIndex, intvlIndex);
                                                        if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                                                        {
                                                            CtiLockGuard< CtiLogger > doubt_guard( dout );
                                                            dout << "    **lpTime:  " << RWTime(_lpTimes[y]) << "  lpValue: "<<_lpValues[y]<<endl;
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
    lpDemandSelect = NULL;
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
            
            case OFFSET_PEAK_KW_OR_RATE_A_KW:                
            case OFFSET_PEAK_KVAR_OR_RATE_A_KVAR:            
            case OFFSET_PEAK_KVA_OR_RATE_A_KVA:              
            case OFFSET_RATE_A_KWH:
            case OFFSET_RATE_A_KVARH: 
            case OFFSET_RATE_A_KVAH:                         

            case OFFSET_RATE_B_KW:                           
            case OFFSET_RATE_B_KWH:                          
            case OFFSET_RATE_B_KVAR:                         
            case OFFSET_RATE_B_KVARH:                       
            case OFFSET_RATE_B_KVA:                          
            case OFFSET_RATE_B_KVAH:                         

            case OFFSET_RATE_C_KW:                           
            case OFFSET_RATE_C_KWH:                          
            case OFFSET_RATE_C_KVAR:                         
            case OFFSET_RATE_C_KVARH:                        
            case OFFSET_RATE_C_KVA:                          
            case OFFSET_RATE_C_KVAH:                         

            case OFFSET_RATE_D_KW:                          
            case OFFSET_RATE_D_KWH:                          
            case OFFSET_RATE_D_KVAR:                        
            case OFFSET_RATE_D_KVARH:                        
            case OFFSET_RATE_D_KVA:                          
            case OFFSET_RATE_D_KVAH:                         

            case OFFSET_RATE_E_KW:                           
            case OFFSET_RATE_E_KWH:                          
            case OFFSET_RATE_E_KVAR:                         
            case OFFSET_RATE_E_KVARH:                        
            case OFFSET_RATE_E_KVA:                          
            case OFFSET_RATE_E_KVAH:                         

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

        


int CtiProtocolANSI::getSizeOfLPDataBlock(int dataSetNbr)
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

int CtiProtocolANSI::getSizeOfLPReadingsRcd()
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

int CtiProtocolANSI::getSizeOfLPIntSetRcd(int dataSetNbr)
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
int CtiProtocolANSI::getSizeOfLPIntFmtRcd(int dataSetNbr)
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
unsigned short CtiProtocolANSI::getTotalWantedLPBlockInts()
{
    return _nbrLPDataBlkIntvlsWanted;
}

int CtiProtocolANSI::proc09RemoteReset(UINT8 actionFlag)
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
        dout << "hex value : "<<(int)superTemp[0]<<" "<<(int)superTemp[1]<<endl;
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

    return 1;


}


void CtiProtocolANSI::setCurrentAnsiWantsTableValues(int tableID,int tableOffset, unsigned short bytesExpected,
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

    return;
}

list < int > CtiProtocolANSI::getStdTblsAvailable(void)
{
    return _stdTblsAvailable;
}

list < int > CtiProtocolANSI::getMfgTblsAvailable(void)
{
    return _mfgTblsAvailable;
}

bool CtiProtocolANSI::isStdTableAvailableInMeter(int tableNbr)
{       
    list<int>::iterator ii = _stdTblsAvailable.begin();

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

bool CtiProtocolANSI::isMfgTableAvailableInMeter(int tableNbr)
{       
    list<int>::iterator ii = _mfgTblsAvailable.begin();

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

