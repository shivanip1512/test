
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
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2003/12/31 21:04:04 $
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
}

//=====================================================================================================================
//=====================================================================================================================

CtiDeviceMarkV::~CtiDeviceMarkV()
{
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
                 _transdataProtocol.setCommand( CtiTransdataApplication::General, true );  //the bool is temp to force LP collection
                                                                  //it will be seperable later
              }
            break;

           case  ScanRateLoadProfile:
              {
                 _transdataProtocol.setCommand( CtiTransdataApplication::LoadProfile, false ); 
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
      
      if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
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
   

   if( _transdataProtocol.getCommand() == CtiTransdataApplication::General )
   {
      memcpy( &_llp, InMessage->Buffer.InMessage, sizeof( _llp ));
      setLastLPTime( RWTime( _llp.lastLP ));
      memcpy( InMessage->Buffer.InMessage, InMessage->Buffer.InMessage + sizeof( _llp ), InMessage->InLength-sizeof( _llp ));

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

   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " ----Scanner Message Process----" << endl;
   }

   if( isScanPending() )
   {
      for( index = 0; index < transVector.size() - 1; index++ )
      {
         switch( transVector[index]->getID() )
         {

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
      
      if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " ----Scanner Message Inserted----" << endl;
      }
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
   BYTE                             *_storage = NULL; //change this back to storage
   int                              index;
   int                              numEnabledChannels = 0;
   bool                             firstLoop = true;

   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " ----Process Dispatch Message In Progress----" << endl;
   }

   if( _transdataProtocol.getDidProcess() )
   {
      _storage = new BYTE[30000];
      _transdataProtocol.retreiveData( _storage );

      lp = ( CtiTransdataTracker::mark_v_lp *)_storage;
      _llp.lastLP = lp->meterTime;
      RWTime mTime( lp->meterTime );

      if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " --------Dispatch will see this time ->" << mTime << endl;
      }

      for( index = 0; index < lp->numLpRecs; )
      {
         if( lp->enabledChannels[0] == true )
         {
            pPoint = getDevicePointOffsetTypeEqual( CH1_OFFSET + LOAD_PROFILE, AnalogPointType );

            if( pPoint != NULL )
            {
               pData = new CtiPointDataMsg();
               pData->setId( pPoint->getID() );
               pData->setValue( correctValue( lp->lpData[index] ) );
               pData->setQuality( checkQuality( lp->lpFormat[1], correctValue( lp->lpData[index] ) ));             //just for now
               pData->setTags( TAG_POINT_LOAD_PROFILE_DATA );
               pData->setMessageTime( mTime );
               pData->setType( pPoint->getType() );

               msgMulti->getData().insert( pData );

               if( firstLoop )
               {
                  //stick this pData into something for scanner
                  pData = new CtiPointDataMsg();
                  pData->setId( pPoint->getID() );
                  pData->setValue( correctValue( lp->lpData[index] ) );
                  pData->setQuality( checkQuality( lp->lpFormat[1], correctValue( lp->lpData[index] ) ));             //just for now
                  pData->setMessageTime( mTime );
                  pData->setType( pPoint->getType() );

                  msgMulti->getData().insert( pData );
               }

               index += 2;
            }
         }
         
         if( lp->enabledChannels[1] == true )
         {
            pPoint = getDevicePointOffsetTypeEqual( CH2_OFFSET + LOAD_PROFILE, AnalogPointType );

            if( pPoint != NULL )
            {
               pData = new CtiPointDataMsg();
               pData->setId( pPoint->getID() );
               pData->setValue( correctValue( lp->lpData[index] ) );
               pData->setQuality( checkQuality( lp->lpFormat[1], correctValue( lp->lpData[index] ) ));             //just for now
               pData->setTags( TAG_POINT_LOAD_PROFILE_DATA );
               pData->setMessageTime( mTime );
               pData->setType( pPoint->getType() );

               msgMulti->getData().insert( pData );

               if( firstLoop )
               {
                  //stick this pData into something for scanner
                  pData = new CtiPointDataMsg();
                  pData->setId( pPoint->getID() );
                  pData->setValue( correctValue( lp->lpData[index] ) );
                  pData->setQuality( checkQuality( lp->lpFormat[1], correctValue( lp->lpData[index] ) ));             //just for now
                  pData->setMessageTime( mTime );
                  pData->setType( pPoint->getType() );

                  msgMulti->getData().insert( pData );
               }

               index += 2;
            }
         }

         if( lp->enabledChannels[2] == true )
         {
            pPoint = getDevicePointOffsetTypeEqual( CH3_OFFSET + LOAD_PROFILE, AnalogPointType );

            if( pPoint != NULL )
            {
               pData = new CtiPointDataMsg();
               pData->setId( pPoint->getID() );
               pData->setValue( correctValue( lp->lpData[index] ) );
               pData->setQuality( checkQuality( lp->lpFormat[1], correctValue( lp->lpData[index] ) ));             //just for now
               pData->setTags( TAG_POINT_LOAD_PROFILE_DATA );
               pData->setMessageTime( mTime );
               pData->setType( pPoint->getType() );

               msgMulti->getData().insert( pData );

               if( firstLoop )
               {
                  //stick this pData into something for scanner
                  pData = new CtiPointDataMsg();
                  pData->setId( pPoint->getID() );
                  pData->setValue( correctValue( lp->lpData[index] ) );
                  pData->setQuality( checkQuality( lp->lpFormat[1], correctValue( lp->lpData[index] ) ));             //just for now
                  pData->setMessageTime( mTime );
                  pData->setType( pPoint->getType() );

                  msgMulti->getData().insert( pData );
               }

               index += 2;
            }
         }

         if( lp->enabledChannels[3] == true )
         {
            pPoint = getDevicePointOffsetTypeEqual( CH4_OFFSET + LOAD_PROFILE, AnalogPointType );

            if( pPoint != NULL )
            {
               pData = new CtiPointDataMsg();
               pData->setId( pPoint->getID() );
               pData->setValue( correctValue( lp->lpData[index] ) );
               pData->setQuality( checkQuality( lp->lpFormat[1], correctValue( lp->lpData[index] ) ));             //just for now
               pData->setTags( TAG_POINT_LOAD_PROFILE_DATA );
               pData->setMessageTime( mTime );
               pData->setType( pPoint->getType() );

               msgMulti->getData().insert( pData );

               if( firstLoop )
               {
                  //stick this pData into something for scanner
                  pData = new CtiPointDataMsg();
                  pData->setId( pPoint->getID() );
                  pData->setValue( correctValue( lp->lpData[index] ) );
                  pData->setQuality( checkQuality( lp->lpFormat[1], correctValue( lp->lpData[index] ) ));             //just for now
                  pData->setMessageTime( mTime );
                  pData->setType( pPoint->getType() );

                  msgMulti->getData().insert( pData );
               }

               index += 2;
            }
         }
         
         if( firstLoop )
         {
            firstLoop = false;
         }

         //decrement the time to the interval previous to the current one...
         mTime -= lp->lpFormat[0] * 60; 
      }

      conn.WriteConnQue( msgMulti );
      
      if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " ----Dispatch Message Inserted----" << endl;
      }
   }
   else
   {
      if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " ----No Data For Dispatch Message----" << endl;
      }
   }
}

//=====================================================================================================================
//=====================================================================================================================

int CtiDeviceMarkV::sendCommResult( INMESS *InMessage )
{
   CtiProtocolTransdata::llp  *lLP = NULL;
   
   lLP = &_llp;

   //insert lastlptime struct into inmess
   memcpy( InMessage->Buffer.InMessage, lLP, sizeof( _llp ) );
   _transdataProtocol.sendCommResult( InMessage );
              
   return( 1 );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiDeviceMarkV::DecodeDatabaseReader( RWDBReader &rdr )
{
   Inherited::DecodeDatabaseReader( rdr );       // get the base class handled

   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
   }

   getProtocol().injectData( getIED().getPassword() );
}

//=====================================================================================================================
//flip our bytes into the correct order to get a value
//=====================================================================================================================

int CtiDeviceMarkV::correctValue( CtiTransdataTracker::lpRecord rec )
{
   BYTEUSHORT temp;
   
   temp.ch[0] = rec.rec[1];
   temp.ch[1] = rec.rec[0];

   return( temp.sh );
}

//=====================================================================================================================
//transdata qualities
// 0 = end of interval
// 6 = end of interval with power outage
// 8 = entire interval occured during outage
// a = end of interval during demand deferral
// 2 = interval time correction
// e = end of interval with diagnostics alarm
// c = loss of potential alarm
// 4 = harmonic distortion alarm
//=====================================================================================================================

int CtiDeviceMarkV::checkQuality( int yyMap, int lpValue )
{
   int quality = NormalQuality;

   if( yyMap == Type_II )
   {
      //the top 3 bits tell us what the quality is
      quality = ( lpValue & 0xe000 );

      switch( quality )
      {
      case 0x0:
         quality = NormalQuality;
         break;

      case 0x6:
         quality = PowerfailQuality;
         break;

      case 0x8:
         quality = PowerfailQuality;
         break;

      case 0xa:
         quality = AbnormalQuality;
         break;

      case 0x2:
         quality = PartialIntervalQuality;
         break;

      case 0xe:
      case 0xc:
      case 0x4:
         quality = QuestionableQuality;

      default:
         quality = AbnormalQuality;
         break;
      }
   }

   return( quality );
}

