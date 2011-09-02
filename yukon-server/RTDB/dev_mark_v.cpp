#include "precompiled.h"
#include "cparms.h"
#include "porter.h"
#include "logger.h"
#include "dev_mark_v.h"
#include "utility.h"

using std::string;
using std::endl;
using std::list;
using std::vector;

//=====================================================================================================================
//=====================================================================================================================

CtiDeviceMarkV::CtiDeviceMarkV()
{
    memset(&_llp, 0, sizeof(_llp));
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
                                     list< CtiMessage* > &vgList,
                                     list< CtiMessage* > &retList,
                                     list< OUTMESS* >    &outList,
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

      if( getDebugLevel() & DEBUGLEVEL_ACTIVITY_INFO )
      {
         CtiTime p( getLastLPTime().seconds() );
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << CtiTime() << " ----Stored LPTime For " << getName() << "----" << p << endl;
      }

      if( useScanFlags() )
          OutMessage->Buffer.DUPReq.LP_Time = getLastLPTime().seconds();
      else
          OutMessage->Buffer.DUPReq.LP_Time = 0;

      OutMessage->TimeOut   = 2;
      OutMessage->EventCode = RESULT | ENCODED;
      OutMessage->Sequence  = 0;
      OutMessage->Retry     = 3;

      ptr = ( CtiProtocolTransdata::mkv *)OutMessage->Buffer.OutMessage;

      ptr->command = _transdataProtocol.getCommand();
      ptr->getLP = _transdataProtocol.getAction();

      outList.push_back( OutMessage );
      OutMessage = NULL;                //we used it, don't delete it

      if( getDebugLevel() & DEBUGLEVEL_FACTORY )
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << CtiTime() << " ----Execution In Progress For " << getName() << "----" << endl;
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
                                 list< CtiMessage* >  &vgList,
                                 list< CtiMessage* >  &retList,
                                 list< OUTMESS* >     &outList,
                                 INT                        ScanPriority)
{
   INT status = NORMAL;
   CtiCommandParser newParse( "scan general" );

   pReq->setCommandString( "scan general" );

   status = ExecuteRequest( pReq, newParse, OutMessage, vgList, retList, outList );

   return NoError;
}

//=====================================================================================================================
//=====================================================================================================================

INT CtiDeviceMarkV::LoadProfileScan( CtiRequestMsg              *pReq,
                                     CtiCommandParser           &parse,
                                     OUTMESS                    *&OutMessage,
                                     list< CtiMessage* >  &vgList,
                                     list< CtiMessage* >  &retList,
                                     list< OUTMESS* >     &outList,
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
                                  CtiTime                    &TimeNow,
                                  list< CtiMessage* > &vgList,
                                  list< CtiMessage* > &retList,
                                  list< OUTMESS* >    &outList)
{
   vector<CtiTransdataData *> transVector;
   INT                        retCode = NOTNORMAL;

   try
   {
      if( _transdataProtocol.getCommand() == CtiTransdataApplication::General )
      {
         transVector = _transdataProtocol.resultDecode( InMessage );

         if( transVector.size() )
         {
            setLastLPTime( _transdataProtocol.getLastLoadProfileTime() );
            decodeResultScan( InMessage, TimeNow, vgList, retList, transVector );

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
   catch(...)
   {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        //what the hell happened?
   }

   return( retCode );
}

//=====================================================================================================================
//at the moment, all we do is fail in general, without setting points 'non-updated' or anything fancy
//=====================================================================================================================

INT CtiDeviceMarkV::ErrorDecode( const INMESS        &InMessage,
                                 const CtiTime        TimeNow,
                                 list< CtiMessage* > &retList)
{
   INT retCode = NORMAL;

   CtiCommandParser parse( InMessage.Return.CommandStr );

   if( getDebugLevel() & DEBUGLEVEL_FACTORY )
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << CtiTime() << " ----Error Decode For " << getName() << " In Progress----" << endl;
   }

   CtiCommandMsg *pMsg = CTIDBG_new CtiCommandMsg( CtiCommandMsg::UpdateFailed );

   if( pMsg != NULL )
   {
      pMsg->insert( -1 );               // This is the dispatch token and is unimplemented at this time
      pMsg->insert( CtiCommandMsg::OP_DEVICEID );      // This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
      pMsg->insert( getID() );          // The id (device or point which failed)
      pMsg->insert( ScanRateInvalid );  // One of ScanRateGeneral,ScanRateAccum,ScanRateStatus,ScanRateIntegrity, or if unknown -> ScanRateInvalid defined in yukon.h

      if( InMessage.EventCode != 0 )
      {
         pMsg->insert( InMessage.EventCode );
      }
      else
      {
         pMsg->insert( GeneralScanAborted );
      }

      retList.push_back( pMsg );
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
                                      CtiTime                    &TimeNow,
                                      list< CtiMessage* > &vgList,
                                      list< CtiMessage* > &retList,
                                      vector<CtiTransdataData *> transVector)
{
   CtiPointDataMsg   *pData = NULL;
   CtiPointSPtr      pPoint;
   int               index;
   int               offset = 0;
   int               time_id;
   int               date_id;
   CtiReturnMsg      *pPIL = CTIDBG_new CtiReturnMsg( getID(),
                                                       string(InMessage->Return.CommandStr),
                                                       string(),
                                                       InMessage->EventCode & 0x7fff,
                                                       InMessage->Return.RouteID,
                                                       InMessage->Return.MacroOffset,
                                                       InMessage->Return.Attempt,
                                                       InMessage->Return.GrpMsgID,
                                                       InMessage->Return.UserID);

   if( getDebugLevel() & DEBUGLEVEL_FACTORY )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << CtiTime() << " ----Scanner Message Process For " << getName() << "----" << endl;
   }

   if( isScanFlagSet(ScanRateGeneral) )
   {
      for( index = 0; index < transVector.size() - 1; index++ )
      {
         time_id = 0;
         date_id = 0;

         switch( transVector[index]->getID() )
         {
         case 5:
            {
               offset = CH1_OFFSET + TOTAL_USAGE;
            }
            break;

         case 6:
            {
               offset = CH1_OFFSET + CURRENT_DEMAND;
            }
            break;

         case 7:  //peak demand
            {
               offset = CH1_OFFSET + PEAK_DEMAND;
               time_id = 8;
               date_id = 300;
            }
            break;

         case 10:
            {
               offset = CH1_OFFSET + PREVIOUS_DEMAND;
            }
            break;

         case 13:
            {
               offset = CH2_OFFSET + TOTAL_USAGE;
            }
            break;

         case 14:
            {
               offset = CH2_OFFSET + CURRENT_DEMAND;
            }
            break;

         case 15:
            {
               offset = CH2_OFFSET + PEAK_DEMAND;
               time_id = 16;
               date_id = 301;
            }
            break;

         case 18:
            {
               offset = CH2_OFFSET + PREVIOUS_DEMAND;
            }
            break;

         case 21:
            {
               offset = CH3_OFFSET + TOTAL_USAGE;
            }
            break;

         case 22:
            {
               offset = CH3_OFFSET + CURRENT_DEMAND;
            }
            break;

         case 23:
            {
               offset = CH3_OFFSET + PEAK_DEMAND;
               time_id = 24;
               date_id = 302;
            }
            break;

         case 26:
            {
               offset = CH3_OFFSET + PREVIOUS_DEMAND;
            }
            break;


         case 29:
            {
               offset = CH4_OFFSET + TOTAL_USAGE;
            }
            break;

         case 30:
            {
               offset = CH4_OFFSET + CURRENT_DEMAND;
            }
            break;

         case 31:
            {
               offset = CH4_OFFSET + PEAK_DEMAND;
               time_id = 32;
               date_id = 303;
            }
            break;

         case 34:
            {
               offset = CH4_OFFSET + PREVIOUS_DEMAND;
            }
            break;

         case 105:
            {
               offset = CH1_OFFSET + RATEA_USAGE;
            }
            break;

         case 129:
            {
               offset = CH1_OFFSET + RATEB_USAGE;
            }
            break;

         case 153:
            {
               offset = CH1_OFFSET + RATEC_USAGE;
            }
            break;

         case 177:
            {
               offset = CH1_OFFSET + RATED_USAGE;
            }
            break;

         case 109:
            {
               offset = CH2_OFFSET + RATEA_USAGE;
            }
            break;

         case 133:
            {
               offset = CH2_OFFSET + RATEB_USAGE;
            }
            break;

         case 157:
            {
               offset = CH2_OFFSET + RATEC_USAGE;
            }
            break;

         case 181:
            {
               offset = CH2_OFFSET + RATED_USAGE;
            }
            break;

         case 113:
            {
               offset = CH3_OFFSET + RATEA_USAGE;
            }
            break;

         case 137:
            {
               offset = CH3_OFFSET + RATEB_USAGE;
            }
            break;

         case 161:
            {
               offset = CH3_OFFSET + RATEC_USAGE;
            }
            break;

         case 185:
            {
               offset = CH3_OFFSET + RATED_USAGE;
            }
            break;


         case 117:
            {
               offset = CH4_OFFSET + RATEA_USAGE;
            }
            break;

         case 141:
            {
               offset = CH4_OFFSET + RATEB_USAGE;
            }
            break;

         case 165:
            {
               offset = CH4_OFFSET + RATEC_USAGE;
            }
            break;

         case 189:
            {
               offset = CH4_OFFSET + RATED_USAGE;
            }
            break;


         case 106:
            {
               offset = CH1_OFFSET + RATEA_PEAK_DEMAND;
               time_id = 107;
               date_id = 315;
            }
            break;

         case 130:
            {
               offset = CH1_OFFSET + RATEB_PEAK_DEMAND;
               time_id = 131;
               date_id = 316;
            }
            break;

         case 154:
            {
               offset = CH1_OFFSET + RATEC_PEAK_DEMAND;
               time_id = 155;
               date_id = 317;
            }
            break;

         case 178:
            {
               offset = CH1_OFFSET + RATED_PEAK_DEMAND;
               time_id = 179;
               date_id = 318;
            }
            break;


         case 110:
            {
               offset = CH2_OFFSET + RATEA_PEAK_DEMAND;
               time_id = 111;
               date_id = 323;
            }
            break;

         case 134:
            {
               offset = CH2_OFFSET + RATEB_PEAK_DEMAND;
               time_id = 135;
               date_id = 324;
            }
            break;

         case 158:
            {
               offset = CH2_OFFSET + RATEC_PEAK_DEMAND;
               time_id = 159;
               date_id = 325;
            }
            break;

         case 182:
            {
               offset = CH2_OFFSET + RATED_PEAK_DEMAND;
               time_id = 183;
               date_id = 326;
            }
            break;


         case 114:
            {
               offset = CH3_OFFSET + RATEA_PEAK_DEMAND;
               time_id = 115;
               date_id = 331;
            }
            break;

         case 138:
            {
               offset = CH3_OFFSET + RATEB_PEAK_DEMAND;
               time_id = 139;
               date_id = 332;
            }
            break;

         case 162:
            {
               offset = CH3_OFFSET + RATEC_PEAK_DEMAND;
               time_id = 163;
               date_id = 333;
            }
            break;

         case 186:
            {
               offset = CH3_OFFSET + RATED_PEAK_DEMAND;
               time_id = 187;
               date_id = 334;
            }
            break;


         case 118:
            {
               offset = CH4_OFFSET + RATEA_PEAK_DEMAND;
               time_id = 119;
               date_id = 339;
            }
            break;

         case 142:
            {
               offset = CH4_OFFSET + RATEB_PEAK_DEMAND;
               time_id = 143;
               date_id = 340;
            }
            break;

         case 166:
            {
               offset = CH4_OFFSET + RATEC_PEAK_DEMAND;
               time_id = 167;
               date_id = 341;
            }
            break;

         case 190:
            {
               offset = CH4_OFFSET + RATED_PEAK_DEMAND;
               time_id = 191;
               date_id = 342;
            }
            break;

         case 505:
            {
               offset = OFFSET_INSTANTANEOUS_PHASE_A_VOLTAGE;
               time_id = 191;
               date_id = 342;
            }
            break;

         case 506:
            {
               offset = OFFSET_INSTANTANEOUS_PHASE_B_VOLTAGE;
               time_id = 191;
               date_id = 342;
            }
            break;

         case 507:
            {
               offset = OFFSET_INSTANTANEOUS_PHASE_C_VOLTAGE;
               time_id = 191;
               date_id = 342;
            }
            break;

         case 509:
            {
               offset = OFFSET_INSTANTANEOUS_PHASE_A_CURRENT;
               time_id = 191;
               date_id = 342;
            }
            break;

         case 510:
            {
               offset = OFFSET_INSTANTANEOUS_PHASE_B_CURRENT;
               time_id = 191;
               date_id = 342;
            }
            break;

         case 511:
            {
               offset = OFFSET_INSTANTANEOUS_PHASE_C_CURRENT;
               time_id = 191;
               date_id = 342;
            }
            break;
         }

         pPoint = getDevicePointOffsetTypeEqual( offset, AnalogPointType );

         if( pPoint )
         {
            pData = fillPDMsg( transVector, pPoint, index, time_id, date_id );
            pPoint.reset();

            if( pData != NULL )
            {
               pPIL->PointData().push_back( pData );
               pData = NULL;
            }
         }
      }

      retList.push_back( pPIL );
      pPIL = NULL;

      if( getDebugLevel() & DEBUGLEVEL_ACTIVITY_INFO )
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << CtiTime() << " ----Scanner Message Inserted For " << getName() << "----" << endl;
      }
   }

   resetScanFlag(ScanRateGeneral);

   return( 1 );   //bs
}

//=====================================================================================================================
//=====================================================================================================================

CtiPointDataMsg* CtiDeviceMarkV::fillPDMsg( vector<CtiTransdataData *> transVector,
                                            CtiPointSPtr point,
                                            int index,
                                            int timeID,
                                            int dateID )
{
   CtiPointDataMsg   *pData = NULL;
   CtiPointNumericSPtr   pNumericPoint;

   if( point->isNumeric() )
   {
      pNumericPoint = boost::static_pointer_cast<CtiPointNumeric>(point);

      if( pNumericPoint )
      {
         pData = CTIDBG_new CtiPointDataMsg();
         pData->setId( pNumericPoint->getPointID() );
         pData->setType( AnalogPointType );
         pData->setQuality( NormalQuality );
         pData->setValue( transVector[index]->getReading() );

         if( timeID != 0 )
         {
             CtiTime msgTime = getMsgTime( timeID, dateID, transVector );
             if(getLastLPTime() <= msgTime && msgTime <= msgTime.now() + 86400)
             {
                 pData->setTime( msgTime );
             }
             else
             {
                 // This decode has popped the rails.  DO NOT ALLOW IT TO GET IN THE DB!
                 delete pData;
                 pData = 0;
             }
         }

         pNumericPoint.reset();
      }
   }

   return( pData );
}

//=====================================================================================================================
//=====================================================================================================================

CtiTime CtiDeviceMarkV::getMsgTime( int timeID, int dateID, vector<CtiTransdataData *> transVector )
{
   CtiTime   tTime;
   int      index;

   for( index = 0; index < transVector.size() - 1; index++ )
   {
      if( transVector[index]->getID() == timeID )
      {
         CtiDate aDate( transVector[index]->getDay(),
                       transVector[index]->getMonth(),
                       transVector[index]->getYear() );

         CtiTime aTime( aDate,
                       transVector[index]->getHour(),
                       transVector[index]->getMinute(),
                       transVector[index]->getSecond() );

         tTime = aTime;
      }
   }

   return( tTime );
}

//=====================================================================================================================
//=====================================================================================================================

CtiProtocolTransdata & CtiDeviceMarkV::getTransdataProtocol( void )
{
   return _transdataProtocol;
}

//=====================================================================================================================
//=====================================================================================================================

void CtiDeviceMarkV::processDispatchReturnMessage( CtiReturnMsg *msgPtr )
{
   CtiTransdataTracker::mark_v_lp   *lp = NULL;
   CtiPointDataMsg                  *pData = NULL;
   CtiPointSPtr                     pPoint;
   BYTE                             *storage = NULL;
   int                              index;
   int                              numEnabledChannels = 0;
   int                              val = 0;
   int                              qual = 0;

   if( getDebugLevel() & DEBUGLEVEL_FACTORY )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << CtiTime() << " ----Process Dispatch Message In Progress For " << getName() << "----" << endl;
   }

   if( _transdataProtocol.getDidProcess() )
   {
      storage = CTIDBG_new BYTE[50000];
      _transdataProtocol.retreiveData( storage );

      //move this down a layer
      lp = ( CtiTransdataTracker::mark_v_lp *)storage;

      CtiTime mTime( lp->meterTime );

      if( mTime > (CtiTime::now() + 86400) ||
          mTime < (CtiTime::now() - 86400) )
      {
          {
             CtiLockGuard<CtiLogger> doubt_guard(dout);
             dout << CtiTime() << " **** Checkpoint - meter time = (" << mTime << ") in CtiDeviceMarkV::processDispatchReturnMessage() for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
          }
      }
      else if( lp->lpFormat[0] > 60 || lp->lpFormat[0] < 0 )
      {
          {
             CtiLockGuard<CtiLogger> doubt_guard(dout);
             dout << CtiTime() << " **** Checkpoint - lp->lpFormat[0] = (" << lp->lpFormat[0] << ") in CtiDeviceMarkV::processDispatchReturnMessage() for device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
          }
      }
      else
      {
          _llp.lastLP = lp->meterTime;

          CtiMultiMsg *msgMulti = CTIDBG_new CtiMultiMsg;

          for( index = 0; index < lp->numLpRecs; )
          {
              for( int x = 3; x >= 0; x-- )      // the 3 here should be a define, reps # of channels
                {
                if( lp->enabledChannels[x] )
                {
                   pPoint = getDevicePointOffsetTypeEqual( getChannelOffset( x ) + LOAD_PROFILE, AnalogPointType );

                   if( pPoint )
                   {
                      pData = CTIDBG_new CtiPointDataMsg();
                      val = 0;
                      qual = 0;

                      pData->setId( pPoint->getID() );

                      correctValue( lp->lpData[index], lp->lpFormat[1], val, qual );

                      CtiPointNumericSPtr pTemp;
                      pTemp = boost::static_pointer_cast<CtiPointNumeric>(pPoint);

                      pData->setValue( pTemp->computeValueForUOM( val ) );
                      pData->setQuality( qual );
                      pData->setTags( TAG_POINT_LOAD_PROFILE_DATA );
                      pData->setTime( mTime );
                      pData->setType( pPoint->getType() );

                      if( getDebugLevel() & DEBUGLEVEL_ACTIVITY_INFO )
                      {
                         CtiLockGuard<CtiLogger> doubt_guard(dout);
                         dout << mTime << " " << pTemp->computeValueForUOM( val ) << endl;
                      }

                      msgMulti->getData().push_back( pData );

                      pTemp.reset();
                      pData = NULL;
                      pPoint.reset();
                   }

                   index++;
                }
              }

              //decrement the time to the interval previous to the current one...
              CtiTime tempTime( mTime.seconds() - ( lp->lpFormat[0] * 60 ) );
              mTime = tempTime;
          }

          msgPtr->insert( msgMulti );
          msgMulti = NULL;
      }

      if( lp )
      {
         lp = NULL;
      }

      if( storage )
      {
         delete [] storage;
         storage = NULL;
      }

      if( getDebugLevel() & DEBUGLEVEL_FACTORY )
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << CtiTime() << " ----Dispatch Message Inserted For " << getName() << "----" << endl;
      }
   }
   else
   {
      if( getDebugLevel() & DEBUGLEVEL_FACTORY )
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << CtiTime() << " ----No Data For Dispatch Message For " << getName() << "----" << endl;
      }
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
      memcpy( InMessage->Buffer.InMessage, lLP, sizeof( CtiProtocolTransdata::llp ) );

      _transdataProtocol.sendCommResult( InMessage );

      lLP = NULL;
   }

   return NoError;
}

//=====================================================================================================================
//=====================================================================================================================

void CtiDeviceMarkV::DecodeDatabaseReader(Cti::RowReader &rdr)
{
   Inherited::DecodeDatabaseReader( rdr );       // get the base class handled

   getTransdataProtocol().injectData( getIED().getPassword() );
}

//=====================================================================================================================
//flip our bytes into the correct order to get a value
//=====================================================================================================================

void CtiDeviceMarkV::correctValue( CtiTransdataTracker::lpRecord rec, int yyMap, int &value, int &quality )
{
   BYTEUSHORT temp;

   temp.ch[0] = rec.rec[1];
   temp.ch[1] = rec.rec[0];

   value = temp.sh;
   quality = checkQuality( yyMap, value );
   value = value & 0x1fff;    //lop off the top 3 bits
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
      case 0xe000:
            {
                if(isDebugLudicrous())
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << CtiTime() << " " << getName() << " quality is " << string(CtiNumStr(quality).xhex().zpad(2)) << endl;
                }

                if(gConfigParms.isTrue("MARKV_PERFECT_QUALITY"))    quality = QuestionableQuality;
                else                                                quality = NormalQuality;

                break;
            }
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

#if 0       // 20060216 CGP.
      case 0xe000:
#endif
      case 0xc000:
      case 0x4000:
         quality = QuestionableQuality;
         break;
      default:
         quality = NormalQuality; // 20051014 CGP. // AbnormalQuality;
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

   return retVal;
}


