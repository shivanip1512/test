
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
* REVISION     :  $Revision: 1.15 $
* DATE         :  $Date: 2004/01/20 19:06:01 $
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

      if( getLastLPTime() > RWTime() )
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " ----WARNING: LastLPTime is incorrect!----" << endl;
      }

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
      //probably can delete this here
      ptr = NULL;
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
   INT                           retCode = NOTNORMAL;

   try
   {
      if( InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1] == 0 )
      {
         if( _transdataProtocol.getCommand() == CtiTransdataApplication::General )
         {
            memcpy( &_llp, InMessage->Buffer.DUPSt.DUPRep.Message, sizeof( _llp ));
            RWTime x( _llp.lastLP );
            setLastLPTime( x );
            memcpy( InMessage->Buffer.InMessage, InMessage->Buffer.DUPSt.DUPRep.Message + sizeof( _llp ), InMessage->InLength-sizeof( _llp ));

            transVector = _transdataProtocol.resultDecode( InMessage );

            if( transVector.size() )
            {
               decodeResultScan( InMessage,TimeNow, vgList, retList, transVector );

               for( int count = 0; count < transVector.size() - 1; count++ )    //matt say use iterator!
               {
                  delete transVector[count];
               }

               transVector.erase( transVector.begin(), transVector.end() );
            }
         }
         else 
         {
            //LOADPROFILE
         }

         retCode = NORMAL;
      }
   }
   catch(...)
   {
      //what the hell happened?
   }

   return( retCode );
}

//=====================================================================================================================
//at the moment, all we do is fail in general, without setting points 'non-updated' or anything fancy
//=====================================================================================================================

INT CtiDeviceMarkV::ErrorDecode( INMESS                     *InMessage,
                                 RWTime                     &TimeNow,
                                 RWTPtrSlist< CtiMessage >  &vgList,
                                 RWTPtrSlist< CtiMessage >  &retList,
                                 RWTPtrSlist< OUTMESS >     &outList)
{
   INT retCode       = NORMAL;
   CtiCommandParser  parse( InMessage->Return.CommandStr );

   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << RWTime() << " ----Error Decode For Device " << getName() << " In Progress----" << endl;
   }

   CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg( CtiCommandMsg::UpdateFailed );
   
   if( pMsg != NULL )
   {
      pMsg->insert( -1 );             // This is the dispatch token and is unimplemented at this time
      pMsg->insert( OP_DEVICEID );      // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
      pMsg->insert( getID() );          // The id (device or point which failed)
      pMsg->insert( ScanRateInvalid );  // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h
      
      if( InMessage->EventCode != 0 )
      {
         pMsg->insert( InMessage->EventCode );
      }
      else
      {
         pMsg->insert( GeneralScanAborted );
      }
      
      retList.insert( pMsg );
      pMsg = NULL;
   }

   return retCode;
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
                                                       RWCString(InMessage->Return.CommandStr),
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

         if( pPoint != NULL )
         {
            pPoint = NULL;
         }

         if( pData != NULL )
         {
            pPIL->PointData().insert( pData );
            pData = NULL;
         }
      }

      retList.insert( pPIL );
      pPIL = NULL;

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

   if( point->isNumeric() )
   {
      pNumericPoint = ( CtiPointNumeric *)point;

      if( pNumericPoint != NULL )
      {
         pData = CTIDBG_new CtiPointDataMsg();
         pData->setId( pNumericPoint->getPointID() );
         pData->setType( AnalogPointType ); 
         pData->setQuality( NormalQuality );
         pData->setValue( transVector[index]->getReading() );

         if( timeID != 0 )
         {
            pData->setTime( getMsgTime( timeID, dateID, transVector ));
         }

         pNumericPoint = NULL;
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

void CtiDeviceMarkV::processDispatchReturnMessage( CtiReturnMsg *msgPtr )
{
   CtiTransdataTracker::mark_v_lp   *lp = NULL;
   CtiMultiMsg                      *msgMulti = CTIDBG_new CtiMultiMsg; //create where needed
   CtiPointDataMsg                  *pData = NULL;
   CtiPointBase                     *pPoint = NULL;
   BYTE                             *storage = NULL; 
   int                              index;
   int                              numEnabledChannels = 0;
   int                              val = 0;
   int                              qual = 0;
   bool                             firstLoop = true;

   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " ----Process Dispatch Message In Progress----" << endl;
   }

   if( _transdataProtocol.getDidProcess() )
   {
      storage = CTIDBG_new BYTE[30000];
      _transdataProtocol.retreiveData( storage );

      lp = ( CtiTransdataTracker::mark_v_lp *)storage;
      _llp.lastLP = lp->meterTime;
      RWTime mTime( lp->meterTime );

      for( index = 0; index < lp->numLpRecs; )
      {
         for( int x = 0; x < 4; x++ )
         {
            if( lp->enabledChannels[x] == true )
            {
               pPoint = getDevicePointOffsetTypeEqual( getChannelOffset( x ) + LOAD_PROFILE, AnalogPointType );

               if( pPoint != NULL )
               {
                  pData = CTIDBG_new CtiPointDataMsg();
                  val = 0;
                  qual = 0;

                  pData->setId( pPoint->getID() );

                  correctValue( lp->lpData[index], lp->lpFormat[1], val, qual );

                  pData->setValue( val );
                  pData->setQuality( qual );
                  pData->setTags( TAG_POINT_LOAD_PROFILE_DATA );
                  pData->setMessageTime( mTime );
                  pData->setType( pPoint->getType() );

                  msgMulti->getData().insert( pData );
                  pData = NULL;

                  if( firstLoop )
                  {
                     //stick this pData into something for scanner
                     pData = CTIDBG_new CtiPointDataMsg();
                     val = 0;
                     qual = 0;
                     
                     pData->setId( pPoint->getID() );

                     correctValue( lp->lpData[index], lp->lpFormat[1], val, qual );

                     pData->setValue( val );
                     pData->setQuality( qual );
                     pData->setMessageTime( mTime );
                     
                     if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
                     {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " ----Dispatch will get this lastLP time --> " << mTime << endl;
                     }
                     
                     pData->setType( pPoint->getType() );

                     msgMulti->getData().insert( pData );
                     pData = NULL;
                  }

                  index += 2;
               
                  pPoint = NULL;
               }
            }
         }
         
         if( firstLoop )
         {
            firstLoop = false;
         }

         //decrement the time to the interval previous to the current one...
         mTime -= lp->lpFormat[0] * 60; 
      }

      msgPtr->insert( msgMulti );
      msgMulti = NULL;

      if( lp )
      {
         lp = NULL;
      }

      if( storage )
      {
         delete [] storage;
         storage = NULL;
      }

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

   if( msgMulti != NULL )
   {
      delete msgMulti;
      msgMulti = NULL;
   }
}

//=====================================================================================================================
//=====================================================================================================================

int CtiDeviceMarkV::sendCommResult( INMESS *InMessage )
{
   CtiProtocolTransdata::llp  *lLP = NULL;
   
   lLP = &_llp;

   if( lLP != NULL )
   {
      //insert lastlptime struct into inmess
      memcpy( InMessage->Buffer.DUPSt.DUPRep.Message, lLP, sizeof( _llp ) );

      //we have succeeded in communicating
      InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1] = _transdataProtocol.sendCommResult( InMessage );
   
      lLP = NULL;
   }
   
   return( 1 );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiDeviceMarkV::DecodeDatabaseReader( RWDBReader &rdr )
{
   Inherited::DecodeDatabaseReader( rdr );       // get the base class handled

   getProtocol().injectData( getIED().getPassword() );
}

//=====================================================================================================================
//flip our bytes into the correct order to get a value
//=====================================================================================================================

void CtiDeviceMarkV::correctValue( CtiTransdataTracker::lpRecord rec, int yyMap, int &value, int &quality )
{
   BYTEUSHORT temp;
   
   if(( rec.rec[0] != NULL ) && ( rec.rec[1] != 0 ))
   {
      temp.ch[0] = rec.rec[1];
      temp.ch[1] = rec.rec[0];
   }
   else
   {
      temp.sh = 0;
   }
   
   value = temp.sh;
   quality = checkQuality( yyMap, value );

   if( quality != NormalQuality )
   {
      value = value & 0x1fff;    //lop off the top 3 bits
   }
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
      case 0x0000:
         quality = NormalQuality;
         break;

      case 0x6000:
         quality = PowerfailQuality;
         break;

      case 0x8000:
         quality = PowerfailQuality;
         break;

      case 0xa000:
         quality = AbnormalQuality;
         break;

      case 0x2000:
         quality = PartialIntervalQuality;
         break;

      case 0xe000:
      case 0xc000:
      case 0x4000:
         quality = QuestionableQuality;

      default:
         quality = AbnormalQuality;
         break;
      }
   }

   return( quality );
}

//=====================================================================================================================
//=====================================================================================================================

int CtiDeviceMarkV::getChannelOffset( int index )
{
   int retVal = 0;

   switch( index )
   {
   case 0:
      retVal = CH1_OFFSET;
      break;

   case 1:
      retVal = CH2_OFFSET;
      break;

   case 2:
      retVal = CH3_OFFSET;
      break;

   case 3:
      retVal = CH4_OFFSET;
      break;
   }

   return( retVal );
}


