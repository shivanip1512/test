
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
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2004/04/22 21:12:53 $
*    History: 
      $Log: prot_ansi.cpp,v $
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
   _tableFiveTwo = NULL;
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

   if( _tableFiveTwo != NULL )
   {
      delete _tableFiveTwo;
      _tableFiveTwo = NULL;
   }

   if( _billingTable != NULL )
   {
      delete _billingTable;
      _billingTable = NULL;
   }

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
      convertToTable();

      // anything else to do
      if ((_index+1) < _header->numTablesRequested)
      {
          _index++;
          updateBytesExpected ();

          // bad way to do this but if we're getting a manufacturers table, add the offset
          // NOTE: this may be specific to the kv2 !!!!!
          if (_tables[_index].type == ANSI_TABLE_TYPE_MANUFACTURER)
          {
            _tables[_index].tableID += 0x0800;
          }
          getApplicationLayer().initializeTableRequest (_tables[_index].tableID,
                                               _tables[_index].tableOffset,
                                               _tables[_index].bytesExpected,
                                               _tables[_index].type,
                                               _tables[_index].operation);
      }
      else
      {
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
           }
           break;

        case 1:
           {
              _tableZeroOne = new CtiAnsiTableZeroOne( getApplicationLayer().getCurrentTable(), 
                                                       _tableZeroZero->getRawMfgSerialNumberFlag(), 
                                                       _tableZeroZero->getRawIdFormat() );
           }
           break;

        case 10:
           {
              _tableOneZero = new CtiAnsiTableOneZero( getApplicationLayer().getCurrentTable() );
           }
           break;

        case 11:
           {
              _tableOneOne = new CtiAnsiTableOneOne( getApplicationLayer().getCurrentTable() );
           }
             break;

        case 12:
           {
              _tableOneTwo = new CtiAnsiTableOneTwo( getApplicationLayer().getCurrentTable(), 
                                                     _tableOneOne->getNumberUOMEntries() );
           }
           break;

        case 13:
           {
              _tableOneThree = new CtiAnsiTableOneThree( getApplicationLayer().getCurrentTable(), 
                                                         _tableOneOne->getNumberDemandControlEntries(), 
                                                         _tableOneOne->getRawPFExcludeFlag(),
                                                                 _tableOneOne->getRawSlidingDemandFlag(),
                                                                 _tableOneOne->getRawResetExcludeFlag() );
           }
           break;

        case 14:
           {
              _tableOneFour = new CtiAnsiTableOneFour( getApplicationLayer().getCurrentTable(), 
                                                       _tableOneOne->getDataControlLength(), 
                                                       _tableOneOne->getNumberDataControlEntries());
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
                                                         _tableZeroZero->getRawNIFormat1(), 
                                                         _tableZeroZero->getRawNIFormat2(), 
                                                         _tableZeroZero->getRawTimeFormat() );
           }
           break;

        case 52:
           {
              _tableFiveTwo = new CtiAnsiTableFiveTwo( getApplicationLayer().getCurrentTable() );

           }
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
               _tables[_index].bytesExpected = sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat1());

               if (!_tableOneOne->getRawNoOffsetFlag())
               {
                   _tables[_index].bytesExpected += sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat1());
               }
               if (_tableOneOne->getRawSetOnePresentFlag())
               {
                    _tables[_index].bytesExpected += 1;
                    _tables[_index].bytesExpected += ( 2 * sizeOfNonIntegerFormat (_tableZeroZero->getRawNIFormat1()));
               }
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
        }
    }
    {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << RWTime() << "DEBUG:  Table " << _tables[_index].tableID << " expected bytes " << _tables[_index].bytesExpected << endl;
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
/*
    // if the read failed, don't do any of this and tell the device it 
    if (getApplicationLayer().isReadFailed())
    {
        ret=!NORMAL;
    }
    else
    {
        // how this works will need to change once we are doing load profile also

        for (i  = 1;
            i <= OFFSET_HIGHEST_CURRENT_OFFSET;
            i ++)
        {
            // grab the point based on device and offset
            if ((pNumericPoint = (CtiPointNumeric*)getDevicePointOffsetTypeEqual(i, AnalogPointType)) != NULL)
            {

                switch (aOffset)
                {
                    case OFFSET_TOTAL_KWH:
                        {
                            for (int x=0; x < _tableTwoOne->getNumberSummations(); x++)
                            {
                                SOURCE_LINK_BFLD table16 = _tableOneSix->getSourceLink(_tableTwoTwo->getSummationSelect()[x]);

                                // we have to have this set of we don't know what the data represents
                                if (table16.uom_entry_flag)
                                {
                                    if (_tableOneTwo->isCorrectData(_tableTwoTwo->getSummationSelect()[x],CtiAnsiTableOneTwo::uom_watts))
                                    {

                                    }
                                }
                            }
                        }
                    default:
                        break;
                }
            }
        }
    }
*/    
    return ret;
}

#if 0
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
                    default:
                        break;

                case 12:
                   {
                      _tableOneTwo->generateResultPiece(&ptr);
                   }
                   break;
/*

                case 13:
                   {
                      _tableOneThree = new CtiAnsiTableOneThree( getApplicationLayer().getCurrentTable(), 
                                                                 _tableOneOne->getNumberDemandControlEntries(), 
                                                                 _tableOneOne->getPFExcludeFlag(),
                                                                         _tableOneOne->getSlidingDemandFlag(),
                                                                         _tableOneOne->getResetExcludeFlag() );
                   }
                   break;

                case 14:
                   {
                      _tableOneFour = new CtiAnsiTableOneFour( getApplicationLayer().getCurrentTable(), 
                                                               _tableOneOne->getDataControlLength(), 
                                                               _tableOneOne->getNumberDataControlEntries());
                   }
                   break;

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
    }
    return ret;
}
#endif

void CtiProtocolANSI::receiveCommResult( INMESS *InMessage )
{
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

                default:
                    break;
    

/*    
    
            c2
            ase 12:
               {
                  _tableOneTwo = new CtiAnsiTableOneTwo( getApplicationLayer().getCurrentTable(), 
                                                         _tableOneOne->getNumberUOMEntries() );
               }
               break;
    
            case 13:
               {
                  _tableOneThree = new CtiAnsiTableOneThree( getApplicationLayer().getCurrentTable(), 
                                                             _tableOneOne->getNumberDemandControlEntries(), 
                                                             _tableOneOne->getPFExcludeFlag(),
                                                                     _tableOneOne->getSlidingDemandFlag(),
                                                                     _tableOneOne->getResetExcludeFlag() );
               }
               break;
    
            case 14:
               {
                  _tableOneFour = new CtiAnsiTableOneFour( getApplicationLayer().getCurrentTable(), 
                                                           _tableOneOne->getDataControlLength(), 
                                                           _tableOneOne->getNumberDataControlEntries());
               }
               break;
    
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
        _tableZeroZero->printResult();
        _tableZeroOne->printResult();
        _tableOneOne->printResult();
        _tableOneTwo->printResult();
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
