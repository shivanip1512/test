
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_mark_v
*
* Date:   7/16/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2003/12/09 17:55:26 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "yukon.h"
#include "porter.h"
#include "logger.h"
#include "dev_mark_v.h"
#include "utility.h"

//=====================================================================================================================
//=====================================================================================================================

CtiDeviceMarkV::CtiDeviceMarkV()
{
//   _storage = NULL;
}

//=====================================================================================================================
//=====================================================================================================================

CtiDeviceMarkV::~CtiDeviceMarkV()
{
   /*
   if( _storage )
   {
      delete [] _storage;
      _storage = NULL;
   } */
}

//=====================================================================================================================
//=====================================================================================================================

INT CtiDeviceMarkV::ExecuteRequest( CtiRequestMsg             *pReq, 
                                     CtiCommandParser          &parse, 
                                     OUTMESS                   *&OutMessage, 
                                     RWTPtrSlist< CtiMessage > &vgList, 
                                     RWTPtrSlist< CtiMessage > &retList, 
                                     RWTPtrSlist< OUTMESS >    &outList,
                                     INT                       ScanPriority )
{
   CtiProtocolTransdata::mkv   *ptr = NULL;

   switch( parse.getCommand() )
   {
       case ScanRequest:
       {
           switch( parse.getiValue( "scantype" ) )
           {
            case ScanRateGeneral:
              {
                 _transdataProtocol.setCommand( GENERAL, true );  //the bool is temp to force LP collection
                                                                  //it will be seperable later
              }
            break;

           case  ScanRateLoadProfile:
              {
                 _transdataProtocol.setCommand( LOADPROFILE, false ); 
              }
              break;
           }
       }
       break;
   }
   
   if( OutMessage != NULL )
   {
      setCurrentCommand( CmdScanData );
      EstablishOutMessagePriority( OutMessage, ScanPriority );

      // Load all the other stuff that is needed
      OutMessage->DeviceID  = getID();
      OutMessage->TargetID  = getID();
      OutMessage->Port      = getPortID();
      OutMessage->Remote    = getAddress();
      OutMessage->Buffer.DUPReq.LP_Time = getLastLPTime().seconds();
      OutMessage->TimeOut   = 2;
      OutMessage->EventCode = RESULT | ENCODED;
      OutMessage->Sequence  = 0;
      OutMessage->Retry     = 3;

      ptr = ( CtiProtocolTransdata::mkv *)OutMessage->Buffer.OutMessage;
      
      ptr->command = _transdataProtocol.getCommand();
      ptr->getLP = _transdataProtocol.getAction();

      outList.insert( OutMessage );
      
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " ----Execution In Progress----" << endl;
      }
   }
   else
   {
      return MEMORY;
   }

   return( 1 );
}

//=====================================================================================================================
//=====================================================================================================================

INT CtiDeviceMarkV::GeneralScan( CtiRequestMsg              *pReq,
                                 CtiCommandParser           &parse,
                                 OUTMESS                    *&OutMessage,
                                 RWTPtrSlist< CtiMessage >  &vgList,
                                 RWTPtrSlist< CtiMessage >  &retList,
                                 RWTPtrSlist< OUTMESS >     &outList,
                                 INT                        ScanPriority)
{
   INT status = NORMAL;
   CtiCommandParser newParse( "scan general" );

   ULONG lastLPTime = getLastLPTime().seconds();
   pReq->setCommandString( "scan general" );
   
   status = ExecuteRequest( pReq, newParse, OutMessage, vgList, retList, outList );

   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " General Scan" << endl;
   }

   return NoError;
}

//=====================================================================================================================
//=====================================================================================================================

INT CtiDeviceMarkV::LoadProfileScan( CtiRequestMsg              *pReq,
                                     CtiCommandParser           &parse,
                                     OUTMESS                    *&OutMessage,
                                     RWTPtrSlist< CtiMessage >  &vgList,
                                     RWTPtrSlist< CtiMessage >  &retList,
                                     RWTPtrSlist< OUTMESS >     &outList,
                                     INT                        ScanPriority)
{
   INT status = NORMAL;
   CtiCommandParser newParse( "scan loadprofile" );
   
   pReq->setCommandString( "scan loadprofile" );
   
   status = ExecuteRequest( pReq, newParse, OutMessage, vgList, retList, outList );

   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " Load Profile Scan" << endl;
   }

   return NoError;
}

//=====================================================================================================================
//=====================================================================================================================

INT CtiDeviceMarkV::ResultDecode( INMESS                    *InMessage,
                                  RWTime                    &TimeNow,
                                  RWTPtrSlist< CtiMessage > &vgList,
                                  RWTPtrSlist< CtiMessage > &retList,
                                  RWTPtrSlist< OUTMESS >    &outList)
{
   vector<CtiTransdataData *>    transVector;
   

   if( _transdataProtocol.getCommand() == GENERAL )
   {
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Decode Billing" << endl;
      }
      
      transVector = _transdataProtocol.resultDecode( InMessage );

      if( transVector.size() )
      {
         decodeResultScan( InMessage,TimeNow, vgList, retList, transVector );

         for( int count = 0; count < transVector.size() - 1; count++ )    //matt say use iterator!
         {
            delete transVector[count];
         }
      }
   }
   else //LOADPROFILE
   {
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " Decode LoadProfile" << endl;
      }
   }

   return( NORMAL );
}

//=====================================================================================================================
//=====================================================================================================================

INT CtiDeviceMarkV::ErrorDecode( INMESS                     *InMessage,
                                 RWTime                     &TimeNow,
                                 RWTPtrSlist< CtiMessage >  &vgList,
                                 RWTPtrSlist< CtiMessage >  &retList,
                                 RWTPtrSlist< OUTMESS >     &outList)
{
   return( 1 );
}

//=====================================================================================================================
// we pop open the vector will our data that has been broken into logical parts (peak, usage, times)
// we then look at the type each chunk is and associate it with our pointdefs list, yank the data out, fill our 
// point data messages and stick those into the retlist for shipping back to ... dispatch?
//=====================================================================================================================

int CtiDeviceMarkV::decodeResultScan( INMESS                    *InMessage,
                                      RWTime                    &TimeNow,
                                      RWTPtrSlist< CtiMessage > &vgList,
                                      RWTPtrSlist< CtiMessage > &retList,
                                      vector<CtiTransdataData *> transVector)
{
   CtiPointDataMsg   *pData = NULL;
   CtiPointBase      *pPoint = NULL;
   int               index;
   CtiReturnMsg      *pPIL = CTIDBG_new CtiReturnMsg( getID(),
//                                                       RWCString(InMessage->Return.CommandStr),
                                                       RWCString("ooowhat?"),
                                                       RWCString(),
                                                       InMessage->EventCode & 0x7fff,
                                                       InMessage->Return.RouteID,
                                                       InMessage->Return.MacroOffset,
                                                       InMessage->Return.Attempt,
                                                       InMessage->Return.TrxID,
                                                       InMessage->Return.UserID);

   if( isScanPending() )
   {
      for( index = 0; index < transVector.size() - 1; index++ )
      {
         switch( transVector[index]->getID() )
         {

            //setLastLPTime (lastIntervalTS.seconds());  //lp thing

         case 5:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH1_OFFSET + TOTAL_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;
         
         case 6:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH1_OFFSET + CURRENT_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;
         
         case 7:  //peak demand
            {
               pPoint = getDevicePointOffsetTypeEqual( CH1_OFFSET + PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 8, 300 );
               }
            }
            break;
/*         
         case 8:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH1_OFFSET + PEAK_TIME, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index );
               }
            }
            break;

         case 300:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH1_OFFSET + PEAK_DATE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index );
               }
            }
            break;
*/         
         case 10:                                
            {
               pPoint = getDevicePointOffsetTypeEqual( CH1_OFFSET + PREVIOUS_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;
         
         case 13:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH2_OFFSET + TOTAL_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;
         
         case 14:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH2_OFFSET + CURRENT_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;

         case 15:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH2_OFFSET + PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 16, 301 );
               }
            }
            break;
/*
         case 16:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH2_OFFSET + PEAK_TIME, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index );
               }
            }
            break;

         case 301:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH2_OFFSET + PEAK_DATE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index );
               }
            }
            break;
*/
         case 18:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH2_OFFSET + PREVIOUS_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;

         case 21:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH3_OFFSET + TOTAL_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;

         case 22:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH3_OFFSET + CURRENT_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;

         case 23:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH3_OFFSET + PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 24, 302 );
               }
            }
            break;
/*
         case 24:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH3_OFFSET + PEAK_TIME, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index );
               }
            }
            break;

         case 302:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH3_OFFSET + PEAK_DATE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index );
               }
            }
            break;
*/
         case 26:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH3_OFFSET + PREVIOUS_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;


         case 29:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH4_OFFSET + TOTAL_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;

         case 30:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH4_OFFSET + CURRENT_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;

         case 31:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH4_OFFSET + PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 32, 303 );
               }
            }
            break;
/*
         case 32:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH4_OFFSET + PEAK_TIME, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index );
               }
            }
            break;

         case 303:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH4_OFFSET + PEAK_DATE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index );
               }
            }
            break;
*/
         case 34:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH4_OFFSET + PREVIOUS_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;



         case 105:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH1_OFFSET + RATEA_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;
         
         case 129:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH1_OFFSET + RATEB_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;
         
         case 153:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH1_OFFSET + RATEC_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;
         
         case 177:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH1_OFFSET + RATED_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;
                 
         case 109:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH2_OFFSET + RATEA_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;
         
         case 133:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH2_OFFSET + RATEB_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;
         
         case 157:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH2_OFFSET + RATEC_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;
         
         case 181:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH2_OFFSET + RATED_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;

         case 113:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH3_OFFSET + RATEA_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;

         case 137:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH3_OFFSET + RATEB_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;

         case 161:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH3_OFFSET + RATEC_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;

         case 185:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH3_OFFSET + RATED_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;


         case 117:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH4_OFFSET + RATEA_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;

         case 141:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH4_OFFSET + RATEB_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;

         case 165:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH4_OFFSET + RATEC_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;

         case 189:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH4_OFFSET + RATED_USAGE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 0, 0 );
               }
            }
            break;


         case 106:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH1_OFFSET + RATEA_PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 107, 315 );
               }
            }
            break;

         case 130:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH1_OFFSET + RATEB_PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 131, 316 );
               }
            }
            break;

         case 154:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH1_OFFSET + RATEC_PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 155, 317 );
               }
            }
            break;

         case 178:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH1_OFFSET + RATED_PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 179, 318 );
               }
            }
            break;


         case 110:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH2_OFFSET + RATEA_PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 111, 323 );
               }
            }
            break;

         case 134:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH2_OFFSET + RATEB_PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 135, 324 );
               }
            }
            break;

         case 158:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH2_OFFSET + RATEC_PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 159, 325 );
               }
            }
            break;

         case 182:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH2_OFFSET + RATED_PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 183, 326 );
               }
            }
            break;


         case 114:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH3_OFFSET + RATEA_PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 115, 331 );
               }
            }
            break;

         case 138:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH3_OFFSET + RATEB_PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 139, 332 );
               }
            }
            break;

         case 162:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH3_OFFSET + RATEC_PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 163, 333 );
               }
            }
            break;

         case 186:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH3_OFFSET + RATED_PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 187, 334 );
               }
            }
            break;


         case 118:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH4_OFFSET + RATEA_PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 119, 339 );
               }
            }
            break;

         case 142:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH4_OFFSET + RATEB_PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 143, 340 );
               }
            }
            break;

         case 166:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH4_OFFSET + RATEC_PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 167, 341 );
               }
            }
            break;

         case 190:
            {
               pPoint = getDevicePointOffsetTypeEqual( CH4_OFFSET + RATED_PEAK_DEMAND, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = fillPDMsg( transVector, pPoint, index, 191, 342 );
               }
            }
            break;
         }

         if( pData != NULL )
         {
            pPIL->PointData().insert( pData );
            pData = NULL;
         }
      }

      retList.insert( pPIL );
   }

   resetScanPending();

   return( 1 );   //bs
}

//=====================================================================================================================
//=====================================================================================================================

CtiPointDataMsg* CtiDeviceMarkV::fillPDMsg( vector<CtiTransdataData *> transVector, 
                                            CtiPointBase *point, 
                                            int index,
                                            int timeID,
                                            int dateID )
{
   CtiPointDataMsg   *pData = NULL;
   CtiPointNumeric   *pNumericPoint = NULL;

   pNumericPoint = ( CtiPointNumeric *)point;

   if( pNumericPoint != NULL )
   {
      pData = new CtiPointDataMsg();
      pData->setId( pNumericPoint->getPointID() );
      pData->setType( AnalogPointType ); 
      pData->setQuality( NormalQuality );
      pData->setValue( transVector[index]->getReading() );

      if( timeID != 0 )
      {
         pData->setTime( getMsgTime( timeID, dateID, transVector ));
      }
   }

   return( pData );
}

//=====================================================================================================================
//=====================================================================================================================

RWTime CtiDeviceMarkV::getMsgTime( int timeID, int dateID, vector<CtiTransdataData *> transVector )
{
   RWTime   tTime;
   int      index;

   for( index = 0; index < transVector.size() - 1; index++ )
   {
      if( transVector[index]->getID() == timeID )
      {
         RWDate aDate( transVector[index]->getDay(),
                       transVector[index]->getMonth(),
                       transVector[index]->getYear() );

         RWTime aTime( aDate, 
                       transVector[index]->getHour(),
                       transVector[index]->getMinute(),
                       transVector[index]->getSecond(),
                       RWZone::local() );

         tTime = aTime;
      }
   }

   return( tTime );
}

//=====================================================================================================================
//=====================================================================================================================

CtiProtocolTransdata & CtiDeviceMarkV::getProtocol( void )
{
   return _transdataProtocol;
}

//=====================================================================================================================
//=====================================================================================================================

void CtiDeviceMarkV::processDispatchReturnMessage( CtiConnection &conn )
{
   CtiTransdataTracker::mark_v_lp   *lp = NULL;
   CtiMultiMsg                      *msgMulti = new CtiMultiMsg;
   CtiPointDataMsg                  *pData = NULL;
   CtiPointBase                     *pPoint = NULL;
   BYTE                             *storage = NULL;
   int                              index;
   int                              numEnabledChannels = 0;

   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " ----Process Dispatch Message In Progress----" << endl;
   }

   storage = new BYTE[10000];
   _transdataProtocol.retreiveData( storage );
   lp = ( CtiTransdataTracker::mark_v_lp *)storage;

   RWTime mTime( lp->meterTime );

   for( index = 0; index < 8; index++ )
   {
      if( lp->enabledChannels[index] == true )
         numEnabledChannels++;
   }

   //
   //the meter hands us the lp data in order of youngest to oldest
   //
   for( index = 0; index < lp->numLpRecs; index += numEnabledChannels )
   {
      for( int x = 7; x >= 0; x-- )
      {
         if( lp->enabledChannels[x] == true )
         {
            pPoint = getDevicePointOffsetTypeEqual( CH1_OFFSET + LOAD_PROFILE, AnalogPointType );

            if( pPoint != NULL )
            {
               pData = new CtiPointDataMsg();
               pData->setId( pPoint->getID() );
               pData->setValue( lp->lpData[index].value );
               pData->setQuality( NormalQuality );             //just for now
               pData->setTags( TAG_POINT_LOAD_PROFILE_DATA );
               pData->setMessageTime( mTime );

               index += 2; //lp data is 2 bytes per
               
               msgMulti->getData().insert( pData );
            }
         }
      }

      //decrement the time to the interval previous to the current one...
      mTime -= lp->lpFormat[0] * 60; 
   }

   conn.WriteConnQue( msgMulti );
}

//=====================================================================================================================
//=====================================================================================================================
/*
void CtiDeviceMarkV::DecodeDatabaseReader( RWDBReader &rdr )
{
   Inherited::DecodeDatabaseReader( rdr );       // get the base class handled

   if( getDebugLevel() & 0x0800 )
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   getProtocol().injectData( getIED().getPassword() );
}
*/

/*
INT CtiDeviceMarkV::GeneralScan( CtiRequestMsg              *pReq,
                                 CtiCommandParser           &parse,
                                 OUTMESS                    *&OutMessage,
                                 RWTPtrSlist< CtiMessage >  &vgList,
                                 RWTPtrSlist< CtiMessage >  &retList,
                                 RWTPtrSlist< OUTMESS >     &outList,
                                 INT                        ScanPriority)
{
   INT status = NORMAL;
   CtiCommandParser newParse("scan general");

   ULONG lastLPTime = getLastLPTime().seconds();

   if( OutMessage != NULL )
   {
      setCurrentCommand( CmdScanData );
      EstablishOutMessagePriority( OutMessage, ScanPriority );

      // Load all the other stuff that is needed
      OutMessage->DeviceID  = getID();
      OutMessage->TargetID  = getID();
      OutMessage->Port      = getPortID();
      OutMessage->Remote    = getAddress();
      OutMessage->Buffer.DUPReq.LP_Time = getLastLPTime().seconds();
//      OutMessage->Buffer.DUPReq.LastFileTime = getLastLPTime().seconds();
      OutMessage->TimeOut   = 2;
      OutMessage->EventCode = RESULT | ENCODED;
      OutMessage->Sequence  = 0;
      OutMessage->Retry     = 3;

      pReq->setCommandString("scan general");
      
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " General Scan" << endl;
      }

//      outList.insert( OutMessage );
   }
   else
   {
      return MEMORY;
   }
   return NoError;
}

*/
