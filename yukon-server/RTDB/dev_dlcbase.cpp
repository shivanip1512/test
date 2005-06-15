/*-----------------------------------------------------------------------------*
*
* File:   dev_dlcbase
*
* Date:   1/29/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_dlcbase.cpp-arc  $
* REVISION     :  $Revision: 1.26 $
* DATE         :  $Date: 2005/06/15 19:23:17 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include "dev_dlcbase.h"
#include "dev_mct.h"  //  for ARM commands
#include "cparms.h"
#include "devicetypes.h"
#include "msg_cmd.h"
#include "pt_base.h"
#include "dsm2.h"
#include "utility.h"
#include "porter.h"
#include "numstr.h"

using Cti::Protocol::Emetcon;


unsigned int CtiDeviceDLCBase::_lpRetryMultiplier = 0;
unsigned int CtiDeviceDLCBase::_lpRetryMinimum    = 0;
unsigned int CtiDeviceDLCBase::_lpRetryMaximum    = 0;

CtiDeviceDLCBase::CtiDeviceDLCBase()   {}

CtiDeviceDLCBase::CtiDeviceDLCBase(const CtiDeviceDLCBase& aRef)
{
    *this = aRef;
}

CtiDeviceDLCBase::~CtiDeviceDLCBase() {}

CtiDeviceDLCBase& CtiDeviceDLCBase::operator=(const CtiDeviceDLCBase& aRef)
{
    int i;

    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        LockGuard guard(monitor());

        DeviceRoutes = aRef.getDeviceRoute();
    }
    return *this;
}

CtiTableDeviceRoute  CtiDeviceDLCBase::getDeviceRoute() const
{
    return DeviceRoutes;
}
CtiTableDeviceRoute& CtiDeviceDLCBase::getDeviceRoute()
{
    LockGuard guard(monitor());
    return DeviceRoutes;
}

CtiDeviceDLCBase& CtiDeviceDLCBase::setDeviceRoute(const CtiTableDeviceRoute& aRoute)
{
    LockGuard guard(monitor());
    DeviceRoutes = aRoute;
    return *this;
}

CtiTableDeviceCarrier  CtiDeviceDLCBase::getCarrierSettings() const
{
    return CarrierSettings;
}

CtiTableDeviceCarrier& CtiDeviceDLCBase::getCarrierSettings()
{
    LockGuard guard(monitor());
    return CarrierSettings;
}

CtiDeviceDLCBase& CtiDeviceDLCBase::setCarrierSettings( const CtiTableDeviceCarrier & aCarrierSettings )
{
    LockGuard guard(monitor());
    CarrierSettings = aCarrierSettings;
    return *this;
}


void CtiDeviceDLCBase::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableDeviceCarrier::getSQL(db, keyTable, selector);
    CtiTableDeviceRoute::getSQL(db, keyTable, selector);        //  leftOuter'd.
}


void CtiDeviceDLCBase::DecodeDatabaseReader(RWDBReader &rdr)
{
    INT iTemp;
    RWDBNullIndicator isNull;

    Inherited::DecodeDatabaseReader(rdr);       //  get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    LockGuard guard(monitor());
    CarrierSettings.DecodeDatabaseReader(rdr);
    DeviceRoutes.DecodeDatabaseReader(rdr);
}

LONG CtiDeviceDLCBase::getAddress() const   {   return CarrierSettings.getAddress();    }
LONG CtiDeviceDLCBase::getRouteID() const   {   return DeviceRoutes.getRouteID();       }   //  From CtiTableDeviceRoute


INT CtiDeviceDLCBase::retMsgHandler( RWCString commandStr, int status, CtiReturnMsg *retMsg, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, bool expectMore )
{
    CtiReturnMsg *tmpVGRetMsg = NULL;
    RWOrdered subMsgs;
    CtiCommandParser parse(commandStr);
    int retVal;

    //  this function replaced the following commented block of code everywhere it's called:
    //    (except the MCT31x;  it had some special error-handling code.  the replaced code
    //     is still there, commented out)

    /*
      if(ReturnMsg != NULL)
      {
         if(!(ReturnMsg->ResultString().isNull()) || ReturnMsg->getData().entries() > 0)
         {
            retList.append( ReturnMsg );
         }
         else
         {
            delete ReturnMsg;
         }
      }
     */

    retVal = FALSE;

    if( retMsg != NULL)
    {
        if(!(retMsg->ResultString().isNull()) || retMsg->PointData().entries() > 0)
        {
            //  if it's an update command

            if( parse.isKeyValid("flag") && (parse.getFlags( ) & CMD_FLAG_UPDATE) )
            {
                //  make a copy for VanGogh
                tmpVGRetMsg = (CtiReturnMsg *)retMsg->replicateMessage( );

                subMsgs = tmpVGRetMsg->PointData( );

                //  iterate through the points in the retMsg and set the pointdatas to "MUST ARCHIVE"
                for( int i = 0; i < subMsgs.entries( ); i++ )
                {
                    if( (subMsgs[i])->isA( ) == MSG_POINTDATA )
                        ((CtiPointDataMsg *)(subMsgs[i]))->setTags( TAG_POINT_MUST_ARCHIVE );
                }

                vgList.append( tmpVGRetMsg );
            }
            else
            {
                //  Check for any "Must Archive" points and send them to Dispatch

                subMsgs = retMsg->getData( );

                CtiPointDataMsg *tmpMsg;

                for( int i = 0; i < subMsgs.entries( ); i++ )
                {
                    if( (subMsgs[i])->isA( ) == MSG_POINTDATA &&
                        (((CtiPointDataMsg *)(subMsgs[i]))->getTags( ) & TAG_POINT_MUST_ARCHIVE) )
                    {
                        //  only allocate this object if you need to
                        if( tmpVGRetMsg == NULL )
                        {
                            tmpVGRetMsg = CTIDBG_new CtiReturnMsg(*((CtiReturnMsg *)retMsg));
                        }

                        tmpMsg = CTIDBG_new CtiPointDataMsg( *((CtiPointDataMsg *)(subMsgs[i])) );

                        tmpVGRetMsg->PointData().append(tmpMsg);
                    }
                }

                if( tmpVGRetMsg != NULL )
                {
                    vgList.append( tmpVGRetMsg );
                }
            }

            retMsg->setStatus(status);

            if( expectMore )
            {
                retMsg->setExpectMore();
            }

            retList.append( retMsg );

            retVal = TRUE;
        }
        else
        {
            delete retMsg;
        }
    }

    return retVal;
}



INT CtiDeviceDLCBase::decodeCheckErrorReturn(INMESS *InMessage, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    CtiCommandParser parse(InMessage->Return.CommandStr);

    INT ErrReturn = InMessage->EventCode & 0x3fff;

    CtiReturnMsg    *retMsg;
    RWCString        resultString;

    CtiCommandMsg   *pMsg;


    if(!ErrReturn)
    {
        //  verify we heard back from the correct device (only if we heard it)
        //
        //    Note:  The returned address from the device is only the lower 13 bits,
        //           which means we would not have to mask of the Dst address, but for some
        //           reason when the reading is queued into the CCU711 we get the whole address
        //           returned.  So by comparing only 13 bit for both it will not break in
        //           either case.

        if((getAddress() & 0x1fff) != (InMessage->Buffer.DSt.Address & 0x1fff) && (InMessage->Buffer.DSt.Length))  //  make sure it's not just an ACK
        {
            //  Address did not match, so it's a comm error
            ErrReturn = WRONGADDRESS;
            InMessage->EventCode = WRONGADDRESS;

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint - Wrong DLC Address: \"" << getName() << "\" ";
                dout << "(" << getAddress() << ") != (" << InMessage->Buffer.DSt.Address << ") **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    }

    //  ACH: update performace stats for device and route... ?

    //  check for communication failure
    if(ErrReturn)
    {
        retMsg = CTIDBG_new CtiReturnMsg(getID(),
                                         RWCString(InMessage->Return.CommandStr),
                                         RWCString(),
                                         ErrReturn,
                                         InMessage->Return.RouteID,
                                         InMessage->Return.MacroOffset,
                                         InMessage->Return.Attempt,
                                         InMessage->Return.TrxID,
                                         InMessage->Return.UserID);

        if( retMsg != NULL )
        {
            //  send a Device Failed/Point Failed message to dispatch, if applicable
            pMsg = CTIDBG_new CtiCommandMsg(CtiCommandMsg::UpdateFailed);

            if( pMsg != NULL )
            {
                switch( InMessage->Sequence )
                {
                    case Emetcon::Scan_General:
                    {
                        pMsg->insert( -1 );             //  This is the dispatch token and is unimplemented at this time
                        pMsg->insert(OP_DEVICEID);      //  This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                        pMsg->insert(getID());          //  The id (device or point which failed)
                        pMsg->insert(ScanRateGeneral);  //  defined in yukon.h
                        pMsg->insert(InMessage->EventCode);

                        break;
                    }

                    case Emetcon::Scan_Accum:
                    {
                        pMsg->insert( -1 );             //  This is the dispatch token and is unimplemented at this time
                        pMsg->insert(OP_DEVICEID);      //  This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                        pMsg->insert(getID());          //  The id (device or point which failed)
                        pMsg->insert(ScanRateAccum);
                        pMsg->insert(InMessage->EventCode);

                        break;
                    }

                    case Emetcon::Scan_Integrity:
                    {
                        pMsg->insert( -1 );             //  This is the dispatch token and is unimplemented at this time
                        pMsg->insert(OP_DEVICEID);      //  This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                        pMsg->insert(getID());          //  The id (device or point which failed)
                        pMsg->insert(ScanRateIntegrity);
                        pMsg->insert(InMessage->EventCode);

                        break;
                    }

                    default:
                    {
                        delete pMsg;
                        pMsg = NULL;

                        break;
                    }
                }

                if( pMsg != NULL )
                {
                    retMsg->insert(pMsg);
                }
            }

            char error_str[80];

            GetErrorString(ErrReturn, error_str);

            resultString = getName() + " / operation failed \"" + error_str + "\" (" + RWCString(CtiNumStr(ErrReturn).xhex().zpad(2)) + ")";

            retMsg->setResultString(resultString);

            retList.append(retMsg);
        }

        //  Find the next route and resubmit request to porter
        //    ACH:  if no more routes exist, plug the value points... ?

        if( InMessage->Return.MacroOffset > 0 )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  We should be filling out an OutMessage here if there is a MacroOffset > 0 specified! " << endl;
            }

            OUTMESS *NewOutMessage = CTIDBG_new OUTMESS;

            if(NewOutMessage)
            {
                InEchoToOut( InMessage, NewOutMessage);
                outList.insert( NewOutMessage );
            }
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  We should be filling out an OutMessage here if there is a MacroOffset > 0 specified! " << endl;
        }
    }
    else
    {
        //  ACH:  Log the communication success on this route... ?
    }

    return ErrReturn;
}


int CtiDeviceDLCBase::executeOnDLCRoute( CtiRequestMsg              *pReq,
                                         CtiCommandParser           &parse,
                                         OUTMESS                   *&OutMessage,
                                         RWTPtrSlist< OUTMESS >     &tmpOutList,
                                         RWTPtrSlist< CtiMessage >  &vgList,
                                         RWTPtrSlist< CtiMessage >  &retList,
                                         RWTPtrSlist< OUTMESS >     &outList,
                                         bool                        result )
{
    int nRet = NoError;

    CtiRouteSPtr Route;

    RWCString resultString;
    long      routeID;

    CtiReturnMsg* pRet = 0;

    while( !tmpOutList.isEmpty() )
    {
        OUTMESS *pOut = tmpOutList.get();

        if( pReq->RouteId() )
        {
            pOut->Request.RouteID = pReq->RouteId();
        }
        else
        {
            pOut->Request.RouteID = getRouteID();
        }

        EstablishOutMessagePriority( pOut, MAXPRIORITY - 4 );

        if( (Route = CtiDeviceBase::getRoute( pOut->Request.RouteID )) )
        {
            pOut->TargetID  = getID();

            if( result )
            {
                pOut->EventCode = BWORD | WAIT | RESULT;
            }
            else
            {
                pOut->EventCode = BWORD | WAIT;
            }

            if( parse.isKeyValid("noqueue") )
            {
                pOut->EventCode |= DTRAN;
                //  pOut->EventCode &= ~QUEUED;
            }

            pOut->Buffer.BSt.Address      = getAddress();            // The DLC address of the device
            pOut->Buffer.BSt.DeviceType   = getType();
            pOut->Buffer.BSt.SSpec        = 0;  // 2003-08-22 mskf - implement this at the dev_mct level if necessary - getSSpec();

            /*
             * OK, these are the items we are about to set out to perform..  Any additional signals will
             * be added into the list upon completion of the Execute!
             */
            if(parse.getActionItems().entries())
            {
                for(size_t offset = offset; offset < parse.getActionItems().entries(); offset++)
                {
                    RWCString actn = parse.getActionItems()[offset];
                    RWCString desc = getDescription(parse);

                    vgList.insert(CTIDBG_new CtiSignalMsg(SYS_PID_SYSTEM, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
                }
            }

            //  this should eventually be implemented to do some sort of if/switch on the ARM flags, instead of having
            //    multiple if/else statements
            /*while( pOut->Buffer.BSt.IO & (Q_ARML | Q_ARMC | Q_ARMS) )
            {*/
                //  send an ARMC for commands that require it...  also, make sure we won't generate an infinite loop
                if( (pOut->Buffer.BSt.IO & Q_ARMC) && !parse.isKeyValid("armc") )
                {
                    pOut->Buffer.BSt.IO &= ~Q_ARMC;

                    CtiRequestMsg *arm_req = CTIDBG_new CtiRequestMsg(*pReq);

                    if( arm_req )
                    {
                        if( parse.isKeyValid("noqueue") )
                        {
                            arm_req->setCommandString("putconfig emetcon armc noqueue");
                        }
                        else
                        {
                            arm_req->setCommandString("putconfig emetcon armc");
                        }

                        arm_req->setMessagePriority(pReq->getMessagePriority());

                        CtiCommandParser arm_parse(arm_req->CommandString());

                        if( CtiDeviceBase::ExecuteRequest(arm_req, arm_parse, vgList, retList, outList, pOut) )
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - error sending ARMC to device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }

                        delete arm_req;
                    }
                }
                if( (pOut->Buffer.BSt.IO & Q_ARML) && !parse.isKeyValid("arml") )
                {
                    pOut->Buffer.BSt.IO &= ~Q_ARML;

                    CtiRequestMsg *arm_req = CTIDBG_new CtiRequestMsg(*pReq);

                    if( arm_req )
                    {
                        if( parse.isKeyValid("noqueue") )
                        {
                            arm_req->setCommandString("putconfig emetcon arml noqueue");
                        }
                        else
                        {
                            arm_req->setCommandString("putconfig emetcon arml");
                        }

                        arm_req->setMessagePriority(pReq->getMessagePriority());
                        arm_req->setConnectionHandle(0);

                        CtiCommandParser arm_parse(arm_req->CommandString());

                        if( CtiDeviceBase::ExecuteRequest(arm_req, arm_parse, vgList, retList, outList, pOut) )
                        {
                            {
                                CtiLockGuard<CtiLogger> doubt_guard(dout);
                                dout << RWTime() << " **** Checkpoint - error sending ARML to device \"" << getName() << "\" **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            }
                        }

                        delete arm_req;
                    }
                }
            /*}*/

            /*
             *  Form up the reply here since the ExecuteRequest funciton will consume the
             *  OutMessage.
             */
            pRet = CTIDBG_new CtiReturnMsg(getID(), RWCString(pOut->Request.CommandStr), Route->getName(), nRet, pOut->Request.RouteID, pOut->Request.MacroOffset, pOut->Request.Attempt, pOut->Request.TrxID, pOut->Request.UserID, pOut->Request.SOE, RWOrdered());
            // Start the control request on its route(s)
            if( (nRet = Route->ExecuteRequest(pReq, parse, pOut, vgList, retList, outList)) )
            {
                resultString = "ERROR " + CtiNumStr(nRet) + " performing command on route " + Route->getName().data() + "\n" + FormatError(nRet);
                pRet->setResultString(resultString);
                pRet->setStatus( nRet );
            }
            else
            {
                delete pRet;
                pRet = 0;
            }
        }
        else if( getRouteManager() == 0 )       // If there is no route manager, we need porter to do the route work!
        {
            // Tell the porter side to complete the assembly of the message.
            pOut->Request.BuildIt = TRUE;
            strncpy(pOut->Request.CommandStr, pReq->CommandString(), COMMAND_STR_SIZE);

            outList.insert( pOut );       // May porter have mercy.
            pOut = 0;
        }
        else
        {
            nRet = BADROUTE;

            resultString = "ERROR: Route or Route Transmitter not available for device " + getName();

            pRet = CTIDBG_new CtiReturnMsg(getID(),
                                           RWCString(pOut->Request.CommandStr),
                                           resultString,
                                           nRet,
                                           pOut->Request.RouteID,
                                           pOut->Request.MacroOffset,
                                           pOut->Request.Attempt,
                                           pOut->Request.TrxID,
                                           pOut->Request.UserID,
                                           pOut->Request.SOE,
                                           RWOrdered());
        }

        if(pRet)
        {
            retList.insert( pRet );
        }

        if( pOut )
        {
            delete pOut;
        }
    }

    return nRet;
}



bool CtiDeviceDLCBase::processAdditionalRoutes( INMESS *InMessage ) const
{
    bool bret = false;

    if(InMessage->Return.MacroOffset != 0)
    {
        CtiRouteSPtr Route;

        if( (Route = CtiDeviceBase::getRoute( InMessage->Return.RouteID )) )    // This is "this's" route
        {
            bret = Route->processAdditionalRoutes(InMessage);
        }
        else
        {
            bret = true;        // Presume the existence of MacroOffset != 0 indicates a GO status!
        }
    }
    return bret;
}


inline ULONG CtiDeviceDLCBase::selectInitialMacroRouteOffset(LONG routeid) const
{
    ULONG offset = 0;

    CtiRouteSPtr Route;

    if(routeid > 0 && (Route = CtiDeviceBase::getRoute( routeid )) )    // This is "this's" route
    {
        if(Route->getType() == RouteTypeMacro)
        {
            offset = 1;
        }
    }
    else
    {
        offset = 0;
    }

    return offset;
}


unsigned int CtiDeviceDLCBase::getLPRetryRate( unsigned int interval )
{
    unsigned int retVal;

    //  check if it's been initialized
    if( _lpRetryMultiplier == 0 )
    {
        _lpRetryMultiplier = gConfigParms.getValueAsInt("DLC_LP_RETRY_MULTIPLIER", DefaultLPRetryMultiplier);

        if( _lpRetryMultiplier < DefaultLPRetryMultiplier )
        {
            _lpRetryMultiplier = DefaultLPRetryMultiplier;
        }
    }

    //  check if it's been initialized
    if( _lpRetryMinimum == 0 )
    {
        _lpRetryMinimum = gConfigParms.getValueAsInt("DLC_LP_RETRY_MINIMUM", DefaultLPRetryMinimum);

        if( _lpRetryMinimum < DefaultLPRetryMinimum )
        {
            _lpRetryMinimum = DefaultLPRetryMinimum;
        }
    }

    //  check if it's been initialized
    if( _lpRetryMaximum == 0 )
    {
        _lpRetryMaximum = gConfigParms.getValueAsInt("DLC_LP_RETRY_MAXIMUM", DefaultLPRetryMaximum);

        if( _lpRetryMaximum > DefaultLPRetryMaximum )
        {
            _lpRetryMaximum = DefaultLPRetryMaximum;
        }
    }

    if( _lpRetryMinimum > _lpRetryMaximum )
    {
        unsigned int tmp;
        tmp             = _lpRetryMinimum;
        _lpRetryMinimum = _lpRetryMaximum;
        _lpRetryMaximum = tmp;
    }

    retVal = interval * _lpRetryMultiplier;

    if( retVal > _lpRetryMaximum )
    {
        retVal = _lpRetryMaximum;
    }
    else if( retVal < _lpRetryMinimum )
    {
        retVal = _lpRetryMinimum;
    }

    return retVal;
}

