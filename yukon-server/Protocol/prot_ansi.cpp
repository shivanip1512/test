
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
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2004/09/30 21:37:17 $
*    History: 
      $Log: prot_ansi.cpp,v $
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
   _tableFiveOne = NULL; 
   _tableFiveTwo = NULL;
   _tableSixOne = NULL;
   _tableSixTwo = NULL;
   _tableSixThree = NULL;
   _tableSixFour = NULL;
   //_tableFiveFive = NULL;

   _validFlag = false;
   _entireTableFlag = false;
   _nbrLPDataBlksWanted = 0;
   _nbrLPDataBlkIntvlsWanted = 0;
   _nbrFirstLPDataBlkIntvlsWanted = 0;
   _nbrLastLPDataBlkIntvlsWanted = 0;
   _timeSinceLastLPTime = 0;

   _seqNbr = 0;

   _writeProcedureInProgressFlag = false;
   _previewTable64InProgressFlag = false;
   _lpNbrLoadProfileChannels = 0;  
   _lpNbrIntvlsLastBlock = 0;      
   _lpLastBlockIndex = 0;          
   _lpStartBlockIndex = 0;         
   _lpBlockSize = 0;               
   _lpOffset = 0;                  
   _lpNbrFullBlocks = 0;           
   _lpLastBlockSize = 0; 

   

   _lpValues = NULL;
   _lpTimes = NULL;


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
   /*if( _tableZeroEight != NULL )
   {
      delete _tableZeroEight;
      _tableZeroEight = NULL;
   }  */


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

   _writeProcedureInProgressFlag = false;
   _previewTable64InProgressFlag = false;
   _lpNbrLoadProfileChannels = 0;  
   _lpNbrIntvlsLastBlock = 0;      
   _lpLastBlockIndex = 0;          
   _lpStartBlockIndex = 0;         
   _lpBlockSize = 0;               
   _lpOffset = 0;                  
   _lpNbrFullBlocks = 0;           
   _lpLastBlockSize = 0; 

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

    _header = CTIDBG_new WANTS_HEADER;

    if( _header != NULL )
    {
       memcpy( ( void *)_header, bufptr, sizeof( WANTS_HEADER ) );
       bufptr += sizeof( WANTS_HEADER );

       _tables = CTIDBG_new ANSI_TABLE_WANTS[_header->numTablesRequested];

       if( _tables != NULL )
       {
          for( int x = 0; x < _header->numTablesRequested; x++ )
          {
             memcpy( ( void *)&_tables[x], bufptr, sizeof( ANSI_TABLE_WANTS ));
             bufptr += sizeof( ANSI_TABLE_WANTS );
          }
       }
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
   return (getApplicationLayer().generate( xfer ));
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

      if (_tables[_index].tableID == 63)
      {

          _lpNbrIntvlsLastBlock = _tableSixThree->getNbrValidIntvls(1);
          _lpLastBlockIndex = _tableSixThree->getLastBlkElmt(1);          

          //int julie = UpdateLastLPReadBlksProcedure(1, 3, 0x0002);
         //int julie=proc09RemoteReset(1);  
          calculateLPDataBlockStartIndex(_header->lastLoadProfileTime);
          _lpBlockSize = getSizeOfLPDataBlock(1);
          //_lpBlockSize = calculateLPDataBlockSize(_lpNbrLoadProfileChannels);
          _lpLastBlockSize = calculateLPLastDataBlockSize(_lpNbrLoadProfileChannels,_lpNbrIntvlsLastBlock);
      } 
      // anything else to do 
      else if ((_index+1) < _header->numTablesRequested)
      {
          _index++;
          updateBytesExpected ();

          // bad way to do this but if we're getting a manufacturers table, add the offset
          // NOTE: this may be specific to the kv2 !!!!!
          if (_tables[_index].type == ANSI_TABLE_TYPE_MANUFACTURER)
          {
            _tables[_index].tableID += 0x0800;
          }
          /*if (_tables[_index].tableID == 64 && _previewTable64 == true) 
          {
              _tables[_index].tableOffset = _tableSixThree->getLastBlkElmt(1) * getSizeOfLPDataBlock(1);
          }
          else */if (_tables[_index].tableID >= 64 && _tables[_index].tableID <= 67) 
          {
              {
                  CtiLockGuard< CtiLogger > doubt_guard( dout );
                  dout << endl << "  ** JULIE ** LAST LP TIME " <<RWTime(_header->lastLoadProfileTime)<< endl;
              }
              getApplicationLayer().setLPDataMode( true, _tableSixOne->getLPMemoryLength() );

              _tables[_index].tableOffset = _lpOffset;
              //_tables[_index].tableOffset = getLPDataBlkOffset(1) * getSizeOfLPDataBlock(1);
              {
                  CtiLockGuard< CtiLogger > doubt_guard( dout );
                  dout << endl << "  ** JULIE ** since LAST LP TIME " <<RWTime(_timeSinceLastLPTime)<< endl;
                  dout <<  "  ** JULIE ** _tables[_index].tableOffset " <<_tables[_index].tableOffset<< endl;
              }
          }
          getApplicationLayer().initializeTableRequest (_tables[_index].tableID,
                                               _tables[_index].tableOffset,
                                               _tables[_index].bytesExpected,
                                               _tables[_index].type,
                                               _tables[_index].operation);
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
        convertToManufacturerTable (getApplicationLayer().getCurrentTable(),
                                    _tables[_index].bytesExpected,
                                    _tables[_index].tableID);
    }
    else
    {
        switch( _tables[_index].tableID )
        {
        case 0:
           {
              _tableZeroZero = new CtiAnsiTableZeroZero( getApplicationLayer().getCurrentTable() );
              _tableZeroZero->printResult();
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
              setWriteProcedureInProgress(false);

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
            if (_previewTable64InProgressFlag) 
            {
                _tableSixFour = new CtiAnsiTableSixFour( getApplicationLayer().getCurrentTable(), 1 /*last block*/,
                                                   _tableSixOne->getNbrChansSet(1), _tableSixOne->getClosureStatusFlag(), 
                                                   _tableSixOne->getSimpleIntStatusFlag(), _tableSixOne->getNbrBlkIntsSet(1),
                                                   _tableSixOne->getBlkEndReadFlag(), _tableSixOne->getBlkEndPulseFlag(),
                                                   _tableSixOne->getExtendedIntStatusFlag(), _tableSixOne->getMaxIntTimeSet(1),
                                                   _tableSixTwo->getIntervalFmtCde(1), _tableSixThree->getNbrValidIntvls(1),
                                                   _tableZeroZero->getRawNIFormat1(), _tableZeroZero->getRawNIFormat2(), 
                                                   _tableZeroZero->getRawTimeFormat() );

                break;
            }
            if( _tableSixFour != NULL )
            {
               delete _tableSixFour;
               _tableSixFour = NULL;
            }
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
}

// only used for standard tables possibly larger than one data packet size
void CtiProtocolANSI::updateBytesExpected( )
{
    // if its manufactured, send it to the child class
    if (_tables[_index].type == ANSI_TABLE_TYPE_MANUFACTURER)
    {
//        convertToManufacturerTable (getApplicationLayer().getCurrentTable(),
//                                    _tables[_index].bytesExpected,
//                                    _tables[_index].tableID);
    }
    else
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
                   demandsRecSize += (_tableTwoOne->getOccur() * sizeof (STIME_DATE));
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
                
                /*if (_previewTable64 == true) 
                {
                    _tables[_index].bytesExpected += getSizeOfLPDataBlock(1);
                }
                else
                { 
                    //ULONG tempTime = (RWTime().seconds() - RWTime(_header->lastLoadProfileTime).seconds());
                    _timeSinceLastLPTime = (RWTime().seconds() - RWTime(_header->lastLoadProfileTime).seconds());
                    _nbrLPDataBlksWanted = getTotalWantedLPDataBlocks(1, _timeSinceLastLPTime);
                    _nbrLPDataBlkIntvlsWanted = getTotalWantedLPBlockInts(1, _timeSinceLastLPTime);
                    _nbrFirstLPDataBlkIntvlsWanted = getFirstNbrWantedLPBlockInts(1, _timeSinceLastLPTime);
                    _nbrLastLPDataBlkIntvlsWanted = getLastNbrWantedLPBlockInts(1, _timeSinceLastLPTime);

                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " ****JULIE***:  totalWantedDataBlks " <<_nbrLPDataBlksWanted<< endl;
                        dout << RWTime() << " ****JULIE***:  LPDataBlkIntvlsWanted " <<_nbrLPDataBlkIntvlsWanted<< endl;
                        dout << RWTime() << " ****JULIE***:  nbrFirstLPDataBlkIntvlsWanted " <<_nbrFirstLPDataBlkIntvlsWanted<< endl;
                        dout << RWTime() << " ****JULIE***:  nbrLastLPDataBlkIntvlsWanted " <<_nbrLastLPDataBlkIntvlsWanted<< endl;
                    }
                    _tables[_index].bytesExpected += _nbrLPDataBlksWanted * getSizeOfLPDataBlock(1);
                    //_tables[_index].bytesExpected += (_tableSixThree->getNbrValidBlocks(1) * getSizeOfLPDataBlock(1))-
                    //                                ((_tableSixOne->getNbrBlkIntsSet(1) - _tableSixThree->getNbrValidIntvls(1)) * getSizeOfLPIntSetRcd(1)) ;
                } */
               // _tables[_index].bytesExpected += getSizeOfLPDataBlock(1) * _tableSixThree->getNbrUnreadBlks(1);

            }
            break;
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

//=========================================================================================================================================
//=========================================================================================================================================

bool CtiProtocolANSI::isTransactionComplete( void )
{
    // trying to decide if we are done with our attempts
   return (getApplicationLayer().isReadComplete() || getApplicationLayer().isReadFailed());
}

//=========================================================================================================================================
//=========================================================================================================================================

/*
  processBillingTables();

  if( getTotalBillingSize() < sizeof( InMessage->Buffer.InMessage ))
  {
//         memcpy( InMessage->Buffer.InMessage, _outBuff, getTotalBillingSize());

     {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "!!!  data copied to InMessage  !!!" << endl;
     }
  }
  else
  {
     CtiLockGuard<CtiLogger> doubt_guard(dout);
     dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
     dout << "!!!  data > 4k!  !!!" << endl;
  }
   return( 1 );    //don't know what we should use...
}
 */

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
        BYTE *ptr = InMessage->Buffer.InMessage;


        // loop through and add appropriately
        for( int x = 0; x < _header->numTablesRequested; x++ )
        {
            // if its manufactured, send it to the child class
            if (_tables[x].type == ANSI_TABLE_TYPE_MANUFACTURER)
            {
    //            convertToManufacturerTable (getApplicationLayer().getCurrentTable(),
    //                                        _tables[_index].bytesExpected,
    //                                        _tables[_index].tableID);
            }
            else
            {
                switch( _tables[x].tableID )
                {
                    case 0:
                        {
                            // place the appropriate pieces of each table into the inMessagebuffer
                            _tableZeroZero->generateResultPiece (&ptr);


                            break;
                        }
                    case 1:
                        {
                            // place the appropriate pieces of each table into the inMessagebuffer
                            _tableZeroOne->generateResultPiece ( &ptr ); 
                            break;
                        }
                case 11:
                   {
                      _tableOneOne->generateResultPiece(&ptr);
                        break;
                   }
                    
                case 12:
                   {
                       _tableOneTwo->generateResultPiece(&ptr);
                       break;
                   }
                   
                case 13:
                   {
                      _tableOneThree->generateResultPiece(&ptr);
                      break;
                   }
                case 14:
                   {
                      _tableOneFour->generateResultPiece(&ptr);
                      break;
                   }
                case 15:
                    {
                        _tableOneFive->generateResultPiece(&ptr);
                        break;
                    }
                case 16:
                    {
                        _tableOneSix->generateResultPiece(&ptr);
                        break;
                    }
                case 21:
                    {
                        _tableTwoOne->generateResultPiece(&ptr);
                        break;
                    } 
                case 22:
                    {
                        _tableTwoTwo->generateResultPiece(&ptr);
                        break;
                    }
                case 23:
                {
                    _tableTwoThree->generateResultPiece(&ptr);
                    break;
                }
                case 51:
                {
                    _tableFiveOne->generateResultPiece(&ptr);
                    break;
                }
                default:
                    break;
                }
            }
        }
    }
    return ret;
}

void CtiProtocolANSI::receiveCommResult( INMESS *InMessage )
{
    BYTE *ptr = InMessage->Buffer.InMessage;
    bool success = true;
    int julieTest = 47;
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << " RWCString(InMessage->Return.CommandStr) "<<RWCString(InMessage->Return.CommandStr)<<endl;
    }
    //memcpy(ptr, &success, sizeof (bool));
    //memcpy(ptr + sizeof(bool), &julieTest, sizeof(int));

    // loop through and add appropriately
   for( int x = 0; x < _header->numTablesRequested; x++ )
    {
        // if its manufactured, send it to the child class
        if (_tables[x].type == ANSI_TABLE_TYPE_MANUFACTURER)
        {
//            convertToManufacturerTable (getApplicationLayer().getCurrentTable(),
//                                        _tables[_index].bytesExpected,
//                                        _tables[_index].tableID);
        }
        else
        {
            switch( _tables[x].tableID )
            {
               /* case 0:
                    {
                        // place the appropriate pieces of each table into the inMessagebuffer
                        _tableZeroZero = new CtiAnsiTableZeroZero();
                        _tableZeroZero->decodeResultPiece (&ptr);
                        break;
                    }
                case 1:
                    {
                        // place the appropriate pieces of each table into the inMessagebuffer
                        _tableZeroOne = new CtiAnsiTableZeroOne(
                                                        _tableZeroZero->getRawMfgSerialNumberFlag(), 
                                                        _tableZeroZero->getRawIdFormat() );
                        _tableZeroOne->decodeResultPiece (&ptr);
                        break;
                    }
                case 11:
                   {
                      _tableOneOne = new CtiAnsiTableOneOne( );
                      _tableOneOne->decodeResultPiece (&ptr);
                      break;
                   }
                case 12:
                   {
                      _tableOneTwo = new CtiAnsiTableOneTwo( _tableOneOne->getNumberUOMEntries() );
                      _tableOneTwo->decodeResultPiece (&ptr);
                      break;
                   }
                case 13:
                   {
                      _tableOneThree = new CtiAnsiTableOneThree( _tableOneOne->getNumberDemandControlEntries(),  
                                                                 _tableOneOne->getRawPFExcludeFlag(), 
                                                                 _tableOneOne->getRawSlidingDemandFlag(),
                                                                 _tableOneOne->getRawResetExcludeFlag() );
                      _tableOneThree->decodeResultPiece (&ptr);
                      break;
                   }
                 
                case 14:
                   {
                    _tableOneFour = new CtiAnsiTableOneFour( _tableOneOne->getDataControlLength(),
                                                             _tableOneOne->getNumberDataControlEntries() );
                    _tableOneFour->decodeResultPiece (&ptr);
                    break;
                   }
                   
            case 15:
                {
                    _tableOneFive = new CtiAnsiTableOneFive(_tableOneOne->getRawConstantsSelector(), 
                                                       _tableOneOne->getNumberConstantsEntries(),
                                                       _tableOneOne->getRawNoOffsetFlag(),
                                                       _tableOneOne->getRawSetOnePresentFlag(),
                                                       _tableOneOne->getRawSetTwoPresentFlag(),
                                                       _tableZeroZero->getRawNIFormat1(),
                                                       _tableZeroZero->getRawNIFormat2() );
                    _tableOneFive->decodeResultPiece (&ptr);

                    break;
                }
            case 16:
               {
                  _tableOneSix = new CtiAnsiTableOneSix( _tableOneOne->getNumberSources() );
                  _tableOneSix->decodeResultPiece (&ptr);
                  break;
               }
            case 21:
           {
              _tableTwoOne = new CtiAnsiTableTwoOne();
              _tableTwoOne->decodeResultPiece (&ptr);
              break;
           }
            case 22:
            {
               _tableTwoTwo = new CtiAnsiTableTwoTwo(_tableTwoOne->getNumberSummations(), 
                                                         _tableTwoOne->getNumberDemands(),
                                                         _tableTwoOne->getCoinValues() );
               _tableTwoTwo->decodeResultPiece (&ptr);
               break;
            }
            case 23:
            {
               _tableTwoThree = new CtiAnsiTableTwoThree(_tableTwoOne->getOccur(), 
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
               _tableTwoThree->decodeResultPiece (&ptr);
               break;
            }
            case 51:
            {
                _tableFiveOne = new CtiAnsiTableFiveOne();
                _tableFiveOne->decodeResultPiece (&ptr);
                break;
            }

                

                
                default:
                    break;
         */

/*    
    
           
    
            case 15:
               {
                  _tableOneFive = new CtiAnsiTableOneFive( getApplicationLayer().getCurrentTable(), 
                                                           _tableOneOne->getConstantsSelector(), 
                                                           _tableOneOne->getNumberConstantsEntries(),
                                                           _tableOneOne->getNoOffsetFlag(),
                                                           _tableOneOne->getSetOnePresentFlag(),
                                                           _tableOneOne->getSetTwoPresentFlag(),
                                                           _tableZeroZero->getNiFormatOne(),
                                                           _tableZeroZero->getNiFormatTwo() );
               }
               break;
    
            case 16:
               {
                  _tableOneSix = new CtiAnsiTableOneSix( getApplicationLayer().getCurrentTable(), 
                                                         _tableOneOne->getNumberSources() );
               }
               break;
    
            case 21:
               {
                  _tableTwoOne = new CtiAnsiTableTwoOne( getApplicationLayer().getCurrentTable() );
               }
               break;
    
            case 22:
               {
                  _tableTwoTwo = new CtiAnsiTableTwoTwo( getApplicationLayer().getCurrentTable(), 
                                                         _tableTwoOne->getNumberSummations(), 
                                                         _tableTwoOne->getNumberDemands(),
                                                         _tableTwoOne->getCoinValues() );
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
                                                             _tableZeroZero->getNiFormatOne(), 
                                                             _tableZeroZero->getNiFormatTwo(), 
                                                             _tableZeroZero->getTmFormat() );
               }
               break;
    
            case 52:
               {
                  _tableFiveTwo = new CtiAnsiTableFiveTwo( getApplicationLayer().getCurrentTable() );
    
               }
               break;
            }
*/
            }
        }
    }
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << RWTime::now() << " ==============================================" << endl;
        dout << RWTime::now() << " ==========The KV2 responded with data=========" << endl;
        dout << RWTime::now() << " ==============================================" << endl;
    }
       /* _tableZeroZero->printResult();
        _tableZeroOne->printResult();
        _tableOneOne->printResult();
        _tableOneTwo->printResult();
        _tableOneThree->printResult();
        _tableOneFour->printResult();
        _tableOneFive->printResult();
        _tableOneSix->printResult();
        _tableTwoOne->printResult();
        _tableTwoTwo->printResult();
        _tableTwoThree->printResult();
        _tableFiveOne->printResult();
      */

          
    {
        CtiLockGuard< CtiLogger > doubt_guard( dout );
        dout << RWTime::now() << " ==============================================" << endl;
        dout << RWTime::now() << " ================= Complete ===================" << endl;
        dout << RWTime::now() << " ==============================================" << endl;
    }

}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiProtocolANSI::recvInbound( INMESS *InMessage )
{
   BYTE *ptr = InMessage->Buffer.InMessage;

   _billingTable = new CtiAnsiBillingTable( InMessage->Buffer.InMessage );

   //a heavy-handed way to do this
   ptr += sizeof( int )*17 + sizeof( bool )*4;

   _tableTwoThree = new CtiAnsiTableTwoThree( InMessage->Buffer.InMessage, _billingTable->getNumOccurs(), _billingTable->getNumSummations(),
                                              _billingTable->getNumDemands(), _billingTable->getNumCoins(), _billingTable->getNumTiers(),
                                              _billingTable->getDemandResetFlag(), _billingTable->getTimeDateFlag(),
                                              _billingTable->getCumDemandFlag(), _billingTable->getContCumDemandFlag(),
                                              _billingTable->getNiFormat1(), _billingTable->getNiFormat2(), _billingTable->getTmFormat() );

   //check to make sure this size is correct!
   ptr += _tableTwoThree->getTotSize();

   _tableTwoTwo = new CtiAnsiTableTwoTwo( InMessage->Buffer.InMessage, _billingTable->getNumSummations(), _billingTable->getNumDemands(),
                                                _billingTable->getNumCoins() );

   ptr += _tableTwoTwo->getTotalTableSize();

   _tableOneTwo = new CtiAnsiTableOneTwo( InMessage->Buffer.InMessage, _billingTable->getNumUOMEntries() );

//   ptr += _tableOneTwo->getTableSize();

   decipherInMessage();

   return( 1 );
}

//=========================================================================================================================================
//this is where we'll grab what we want from our newly rebuilt tables on the scanner side and see if we can get real numbers and stuff out
//of them
//FIXME - I'm only doing the billing stuff here and that ain't good enough
//=========================================================================================================================================

void CtiProtocolANSI::decipherInMessage( void )
{
   int         index;
//   BYTEINT32   de;
   int      bytes;

   //all crappy test code
   double   demandActualVal;
   int      crossRef;
   int      type;
   long     temp;

   for( index = 0; index < _billingTable->getNumDemands(); index++ )
   {
      demandActualVal = _tableTwoThree->getTotDataBlock().demands[index].demand[0];


   }
}

//=========================================================================================================================================
//deciphering the tables we got with billing data
//these are the full tables 00,01,11,12,13,14,15,15,16,21,22,23,52
//we will yank out what stuff we need from most of the tables, stick them in a new struct and pass it and the full table 23 (perhaps
//others) to the scanner side..
//=========================================================================================================================================

void CtiProtocolANSI::processBillingTables( void )
{
/*
   BYTE *tracker = _outBuff;
   int  bytes = 0;
   int  offSet = 0;

   _billingTable = new CtiAnsiBillingTable( _tableTwoTwo->getDemandSelectSize(), _tableTwoThree->getTotSize(), _tableTwoOne->getNumberSummations(),
                                            _tableTwoOne->getNumberDemands(), _tableTwoOne->getCoinValues(), _tableTwoOne->getOccur(),
                                            _tableOneOne->getNumberUOMEntries(), _tableOneOne->getNumberDemandControlEntries(),
                                            _tableOneOne->getDataControlLength(), _tableOneOne->getNumberDataControlEntries(),
                                            _tableOneOne->getNumberConstantsEntries(),_tableZeroZero->getTmFormat(), _tableZeroZero->getIntFormat(),
                                            _tableZeroZero->getNiFormatOne(), _tableZeroZero->getNiFormatTwo(), _tableTwoOne->getTiers(),
                                            _tableTwoOne->getDemandResetCtrFlag(), _tableTwoOne->getTimeDateFieldFlag(),
                                            _tableTwoOne->getCumDemandFlag(), _tableTwoOne->getContCumDemandFlag() );

   //don't be moron! don't increment your main pointer or MicroSquish will bitch later!
   if( tracker != NULL )
   {
      offSet = _billingTable->copyDataOut( tracker + bytes );
      bytes += offSet;

      offSet = _tableTwoThree->copyTotDataBlock( tracker + bytes );
      bytes += offSet;

      offSet = _tableTwoTwo->copyDemandSelect( tracker + bytes );
      bytes += offSet;

      offSet = _tableOneTwo->copyUOMStuff( tracker + bytes );
      bytes += offSet;
   }
*/   
}

//=========================================================================================================================================
//deciphering the tables we got with load-profile data
//=========================================================================================================================================

void CtiProtocolANSI::processLoadProfileTables( void )
{

}

//=========================================================================================================================================
//=========================================================================================================================================

int CtiProtocolANSI::getTotalBillingSize( void )
{
   return _billingTable->getTableSize()+_tableTwoTwo->getDemandSelectSize()+_tableTwoThree->getTotSize();
}

//=========================================================================================================================================
//=========================================================================================================================================

void CtiProtocolANSI::retreiveKWHValue( int *value )
{
    BYTE *dataBlob;
   // DATA_BLK_RCD *temp;
    double temp = 8;
    int offset;
    dataBlob = NULL;
        
    //_tableTwoThree->retrieveDemandsRecord( dataBlob, _tableTwoThree->getTotDataBlock(), offset );
    //temp = (DATA_BLK_RCD *)dataBlob;
     // memcpy(value, (void *)&temp->demands->demand[0], sizeof(int));
    temp = _tableTwoThree->getDemandValue(0);
    *value = (int ) temp;
    //*value = (int)temp->demands->demand[0];
       // dataBlob = NULL;
     //   temp = NULL;
      //
    //lue = _tableTwoThree->getTotDataBlock();
   //return _tableTwoThree;
}

void CtiProtocolANSI::retreiveKVARHValue( int *value )
{
    BYTE *dataBlob;
   // DATA_BLK_RCD *temp;
    double temp = 8;
    int offset;
    dataBlob = NULL;
        
    //_tableTwoThree->retrieveDemandsRecord( dataBlob, _tableTwoThree->getTotDataBlock(), offset );
    //temp = (DATA_BLK_RCD *)dataBlob;
     // memcpy(value, (void *)&temp->demands->demand[0], sizeof(int));
    temp = _tableTwoThree->getSummationsValue(0);
    *value = (int ) temp;
    //*value = (int)temp->demands->demand[0];
       // dataBlob = NULL;
     //   temp = NULL;
      //
    //lue = _tableTwoThree->getTotDataBlock();
   //return _tableTwoThree;
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
                    *value = ((_tableTwoThree->getDemandValue(x) * 
                               _tableOneFive->getElecMultiplier((demandSelect[x]%20))) / 1000000000);
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

    /* returns pointer to list of summation Selects */
    summationSelect = _tableTwoTwo->getSummationSelect();
    for (int x = 0; x < _tableTwoOne->getNumberSummations(); x++) 
    {
        if ((int) summationSelect[x] != 255) 
        {
            if (_tableOneTwo->getRawTimeBase(summationSelect[x]) == 0 && 
                _tableOneTwo->getRawIDCode(summationSelect[x]) == ansiOffset) 
            {
                success = true;
                if (_tableOneSix->getConstantsFlag(summationSelect[x]) && 
                    !_tableOneSix->getConstToBeAppliedFlag(summationSelect[x]))
                {
                    *value = ((_tableTwoThree->getSummationsValue(x) * 
                               _tableOneFive->getElecMultiplier(summationSelect[x])) / 1000000000);
                    {
                        CtiLockGuard< CtiLogger > doubt_guard( dout );
                        dout << " *value =   "<<*value<<endl;
                    }
                }
                else
                {
                    *value = _tableTwoThree->getSummationsValue(x);
                }
                break;
            } 
        }
    }
    summationSelect = NULL;
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

    /* returns pointer to list of LP Demand Selects from either dataSet 1,2,3,or 4*/
    lpDemandSelect = _tableSixTwo->getLPDemandSelect(dataSet);
    for (int x = 0; x < _tableSixOne->getNbrChansSet(dataSet); x++) 
    {
        {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << " (int) lpDemandSelect[x] =   "<<(int) lpDemandSelect[x]<<endl;
            dout << " ansiOffset =   "<<ansiOffset<<endl;
        }

        if ((int) lpDemandSelect[x] != 255) 
        {

            {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << " _tableOneTwo->getRawTimeBase(lpDemandSelect[x]) =   "<<_tableOneTwo->getRawTimeBase(lpDemandSelect[x])<<endl;
            dout << " _tableOneTwo->getRawIDCode(lpDemandSelect[x]) =   "<<_tableOneTwo->getRawIDCode(lpDemandSelect[x])<<endl;
        }
            if ((_tableOneTwo->getRawTimeBase(lpDemandSelect[x]) == 5 ||
                 _tableOneTwo->getRawTimeBase(lpDemandSelect[x]) == 0) && 
                _tableOneTwo->getRawIDCode(lpDemandSelect[x]) == ansiOffset) 
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
                                _lpValues = new double[totalIntvls];
                                _lpTimes = new ULONG[totalIntvls];
                                //int intvlIndex = (intvlsPerBlk - _nbrFirstLPDataBlkIntvlsWanted)% intvlsPerBlk;
                                int intvlIndex = 0;
                                for (int y = 0; y < totalIntvls; y++) 
                                {
                                    _lpValues[y] = _tableSixFour->getLPDemandValue ( x, blkIndex, intvlIndex );
                                    _lpTimes[y] = _tableSixFour->getLPDemandTime (blkIndex, intvlIndex);
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
                    /*{
                        CtiLockGuard< CtiLogger > doubt_guard( dout );
                        dout << " *value =   "<<*lpalue<<endl;
                    } */
                /*}
                else
                {
                    {
                        CtiLockGuard< CtiLogger > doubt_guard( dout );
                        dout << " ***JULIE***  OOPPSssss!!!"<<endl;
                    }
                    *_lpValues = _tableSixFour->getLPDemandValue ( x, 1, 1 );
                }                 break; */

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


    /*sizeOfLpBlkDatRcd += sizeof(STIME_DATE) +
                      (nbrChnsSet * getSizeOfLPReadingsRcd());
    */
    sizeOfLpBlkDatRcd += 4 +
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
    {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " ****JULIE***:  sizeOfLpBlkDatRcd " <<sizeOfLpBlkDatRcd<< endl;
    }
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
unsigned short CtiProtocolANSI::getTotalWantedLPBlockInts(int dataSetNbr, ULONG timeSinceLastLP)
{
    int nbrBlkIntsNeeded = 0;
    int maxIntTime = _tableSixOne->getMaxIntTimeSet(dataSetNbr);   //in minutes
    RWTime timeSpan = RWTime(timeSinceLastLP);
    if (timeSpan.seconds() > (maxIntTime * 60)) 
    {
        nbrBlkIntsNeeded = timeSpan.seconds()/(maxIntTime*60);
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " ****JULIE***:  nbrBlkIntsNeeded " <<nbrBlkIntsNeeded<< endl;
        }

        if (nbrBlkIntsNeeded > (_tableSixOne->getNbrBlksSet(dataSetNbr) * _tableSixOne->getNbrBlkIntsSet(dataSetNbr))) 
        {
            nbrBlkIntsNeeded = (_tableSixThree->getNbrValidBlocks(dataSetNbr) * 
                               _tableSixOne->getNbrBlkIntsSet(dataSetNbr)) -
                               (_tableSixOne->getNbrBlkIntsSet(dataSetNbr) - 
                                _tableSixThree->getNbrValidIntvls(dataSetNbr));
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " ****JULIE***:  nbrBlkIntsNeeded " <<nbrBlkIntsNeeded<< endl;
                dout << RWTime() << " ****JULIE***:  getNbrValidBlocks " <<_tableSixThree->getNbrValidBlocks(dataSetNbr)<< endl;
                dout << RWTime() << " ****JULIE***:  getNbrBlkIntsSet " <<_tableSixOne->getNbrBlkIntsSet(dataSetNbr)<< endl;
                dout << RWTime() << " ****JULIE***:  getNbrValidIntvls " << _tableSixThree->getNbrValidIntvls(dataSetNbr)<< endl;
            }
        }
    }
    return nbrBlkIntsNeeded;
}

int CtiProtocolANSI::getLastNbrWantedLPBlockInts(int dataSetNbr, ULONG timeSinceLastLP)
{
    unsigned short totalInts = getTotalWantedLPBlockInts(dataSetNbr,timeSinceLastLP);
    int nbrValidInts = _tableSixThree->getNbrValidIntvls(dataSetNbr);

    if (nbrValidInts <= totalInts) 
    {
        return nbrValidInts;
    }
    else
    {
        return (int) totalInts;
    }
}

int CtiProtocolANSI::getFirstNbrWantedLPBlockInts(int dataSetNbr, ULONG timeSinceLastLP)
{
    return (getTotalWantedLPBlockInts(dataSetNbr,timeSinceLastLP) - 
            getLastNbrWantedLPBlockInts(dataSetNbr, timeSinceLastLP)) %
            _tableSixOne->getNbrBlkIntsSet(dataSetNbr);
}

int CtiProtocolANSI::getTotalWantedLPDataBlocks(int dataSetNbr, ULONG timeSinceLastLP)
{
    int totalBlocks = getTotalWantedLPBlockInts(dataSetNbr,timeSinceLastLP)/_tableSixOne->getNbrBlkIntsSet(dataSetNbr);
    if (getFirstNbrWantedLPBlockInts(dataSetNbr,timeSinceLastLP)) 
    {
        totalBlocks += 1;
    }
    if (getLastNbrWantedLPBlockInts(dataSetNbr, timeSinceLastLP) != _tableSixOne->getNbrBlkIntsSet(dataSetNbr)) 
    {
        totalBlocks += 1;
    }
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " ****JULIE***:  totalBlocks " <<totalBlocks<< endl;
    }

    return totalBlocks;
}

int CtiProtocolANSI::getEndLPDataBlockSet(int dataSetNbr, ULONG timeSinceLastLP)
{   
    return getStartLPDataBlockSet(dataSetNbr,timeSinceLastLP) + getTotalWantedLPDataBlocks(dataSetNbr,timeSinceLastLP);
}
int CtiProtocolANSI::getStartLPDataBlockSet(int dataSetNbr, ULONG timeSinceLastLP)
{
    int nbrValidBlocks = _tableSixThree->getNbrValidBlocks(dataSetNbr);
    int totalDataBlocks = getTotalWantedLPDataBlocks(dataSetNbr,timeSinceLastLP);

    if (totalDataBlocks >= nbrValidBlocks) 
    {
        return 0;
    }
    else
    {
        return nbrValidBlocks - totalDataBlocks;
    }
}

int CtiProtocolANSI::getLPDataBlkOffset(int dataSetNbr)
{
    return _tableSixThree->getNbrValidBlocks(dataSetNbr) -  _tableSixThree->getNbrUnreadBlks(dataSetNbr);
}

int CtiProtocolANSI::UpdateLastLPReadBlksProcedure(int dataSetNbr, UINT8 tblList, UINT16 nbrEntriesRead)
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
    reqData.proc.tbl_proc_nbr = 16;
    reqData.proc.std_vs_mfg_flag = 0;
    reqData.proc.selector = 3;
    UINT16 *test;
    test = new UINT16;
    test = (UINT16*)&reqData.proc;
    {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << " (UINT16) reqData.proc =   "<<*test<<endl;
    }


    getApplicationLayer().setProcBfld( reqData.proc );
    _seqNbr++;
    reqData.seq_nbr = _seqNbr;
    getApplicationLayer().setWriteSeqNbr( reqData.seq_nbr );
    
    reqData.u.p5.tbl_list = tblList;
    reqData.u.p5.entries_read = nbrEntriesRead;
    {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << " reqData.u.p5.tbl_list "<<(int)reqData.u.p5.tbl_list <<"  ( UINT16) reqData.u.p5.entries_read =   "<<(int)( UINT16) reqData.u.p5.entries_read<<endl;
    }

    BYTE *newPtr, *ptr;
    ptr = new BYTE[3];
    newPtr = ptr;
    *ptr = tblList;
    ptr++;
    *ptr = (reqData.u.p5.entries_read & 0xff00)/0x100; //high byte
    ptr++;
    *ptr = (reqData.u.p5.entries_read & 0x00ff);       //low byte

    {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << " (UINT16) newPtr =   "<<(int)*newPtr<<"  "<<(int)*(newPtr+1)<<"  "<<(int)*(newPtr+2)<<endl;
    }
    getApplicationLayer().populateParmPtr(newPtr, 3) ;
      
    return 1;


}


int CtiProtocolANSI::proc05UpdateLastLPBlkRead(int dataSetNbr, UINT8 tblList, UINT16 nbrEntriesRead)
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
    reqData.proc.tbl_proc_nbr = 5;
    reqData.proc.std_vs_mfg_flag = 0;
    reqData.proc.selector = 3;
    return 1;

}

int CtiProtocolANSI::proc16StartLPRecording( void )
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
    reqData.proc.tbl_proc_nbr = 16;
    reqData.proc.std_vs_mfg_flag = 0;
    reqData.proc.selector = 3;


    getApplicationLayer().setProcBfld( reqData.proc );
    _seqNbr++;
    reqData.seq_nbr = _seqNbr;
    getApplicationLayer().setWriteSeqNbr( reqData.seq_nbr );

    BYTE *newPtr;
    newPtr = 0;
    getApplicationLayer().populateParmPtr(newPtr, 0) ;

    return 1;


}

int CtiProtocolANSI::proc17StopLPRecording( void )
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
    reqData.proc.tbl_proc_nbr = 17;
    reqData.proc.std_vs_mfg_flag = 0;
    reqData.proc.selector = 3;
    return 1;

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


int CtiProtocolANSI::proc22LoadProfileStartBlock( void )
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
    reqData.proc.tbl_proc_nbr = 22;
    reqData.proc.std_vs_mfg_flag = 1;
    reqData.proc.selector = 3;   


    getApplicationLayer().setProcBfld( reqData.proc );
    _seqNbr++;
    reqData.seq_nbr = _seqNbr;
    getApplicationLayer().setWriteSeqNbr( reqData.seq_nbr );


    reqData.u.pm22.time = _header->lastLoadProfileTime - RWTime(RWDate(1,1,2000)).seconds();

    {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << " _header->lastLoadProfileTime =   "<< _header->lastLoadProfileTime<<" "<<RWTime( _header->lastLoadProfileTime)<<endl;
            dout << " RWTime(1,1,2000).seconds() =   "<<RWTime(1,1,2000).seconds()<<endl;
            dout << " time =   "<<reqData.u.pm22.time<<" "<<RWTime(reqData.u.pm22.time)<<endl;
    }
    UINT32 tempTime =  RWTime().seconds() - 60 - RWTime(RWDate(1,1,2000)).seconds();
    //getApplicationLayer().populateParmPtr((BYTE *) &reqData.u.pm22.time, 4) ;
    getApplicationLayer().populateParmPtr((BYTE *) &tempTime, 4) ;


    getApplicationLayer().setProcDataSize( sizeof(TBL_IDB_BFLD) + sizeof(reqData.seq_nbr) + 4 );

    return 1;
}



/*void CtiProtocolANSI::performDemandReset ( void )
{
    //this only sets up the tables ... doesn't do any sending.
    int success = proc09RemoteReset(1);

    //need to do port transfer in/out ... ???
    return;
}  */

void CtiProtocolANSI::setWriteProcedureInProgress(bool writeFlag)
{
    _writeProcedureInProgressFlag = writeFlag;
    return;
}

void CtiProtocolANSI::setPreviewTable64InProgress(bool previewFlag)
{
    _previewTable64InProgressFlag = previewFlag; 
    return;
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

