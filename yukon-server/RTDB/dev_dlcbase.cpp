
#pragma warning( disable : 4786)

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
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2002/11/15 14:08:11 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "devicetypes.h"
#include "dev_dlcbase.h"
#include "pt_base.h"
#include "dsm2.h"
#include "utility.h"

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

    if( getDebugLevel() & 0x0800 )
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


INT CtiDeviceDLCBase::retMsgHandler( RWCString commandStr, CtiReturnMsg *retMsg, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList )
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

            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Wrong Address:  " << getName() << " " << InMessage->Buffer.DSt.Address << endl;
                dout << " MCT Address " << getAddress() << " != " << InMessage->Buffer.DSt.Address << endl;
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
                                  InMessage->EventCode & 0x7fff,
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
                    case CtiProtocolEmetcon::Scan_General:
                    {
                        pMsg->insert( -1 );             //  This is the dispatch token and is unimplemented at this time
                        pMsg->insert(OP_DEVICEID);      //  This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                        pMsg->insert(getID());          //  The id (device or point which failed)
                        pMsg->insert(ScanRateGeneral);  //  defined in yukon.h
                        pMsg->insert(InMessage->EventCode);

                        break;
                    }

                    case CtiProtocolEmetcon::Scan_Accum:
                    {
                        pMsg->insert( -1 );             //  This is the dispatch token and is unimplemented at this time
                        pMsg->insert(OP_DEVICEID);      //  This device failed.  OP_POINTID indicates a point fail situation.  defined in msg_cmd.h
                        pMsg->insert(getID());          //  The id (device or point which failed)
                        pMsg->insert(ScanRateAccum);
                        pMsg->insert(InMessage->EventCode);

                        break;
                    }

                    case CtiProtocolEmetcon::Scan_Integrity:
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

            resultString = getName() + " / operation failed";

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
        if(Route->getType() == MacroRouteType)
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

