
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_cbc6510
*
* Date:   5/22/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_cbc.cpp-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/05/30 15:12:21 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>

#include "dsm2.h"
#include "porter.h"

#include "pt_base.h"
#include "master.h"

#include "pointtypes.h"
#include "mgr_route.h"
#include "mgr_point.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "cmdparse.h"
#include "dev_cbc6510.h"
#include "device.h"
#include "yukon.h"
#include "logger.h"
#include "numstr.h"
#include "cparms.h"


int CtiDeviceCBC6510::_cbcTries;

CtiDeviceCBC6510::CtiDeviceCBC6510() {}

CtiDeviceCBC6510::CtiDeviceCBC6510(const CtiDeviceCBC6510 &aRef)
{
   *this = aRef;
}

CtiDeviceCBC6510::~CtiDeviceCBC6510() {}

CtiDeviceCBC6510 &CtiDeviceCBC6510::operator=(const CtiDeviceCBC6510 &aRef)
{
   if(this != &aRef)
   {
      Inherited::operator=(aRef);

      _cbc = aRef.getCBC();
   }
   return *this;
}


int CtiDeviceCBC6510::getCBCRetries( void )
{
    //  check if it's been initialized
    if( _cbcTries <= 0 )
    {
        RWCString retryStr = gConfigParms.getValueAsString("CBC_RETRIES");
        int            tmp = atol( retryStr.data() );

        if( tmp > 0  )
        {
            _cbcTries = tmp + 1;
        }
        else
        {
            //  default to 3 attempts (2 retries)
            _cbcTries = 3;

            if( getDebugLevel() & 0x00000001 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "CBC_RETRIES cparm not found - defaulting CBC retry count to 2 (3 attempts)" << endl;
            }
        }
    }

    return _cbcTries - 1;
}


INT CtiDeviceCBC6510::ExecuteRequest(CtiRequestMsg              *pReq,
                                     CtiCommandParser           &parse,
                                     OUTMESS                   *&OutMessage,
                                     RWTPtrSlist< CtiMessage >  &vgList,
                                     RWTPtrSlist< CtiMessage >  &retList,
                                     RWTPtrSlist< OUTMESS >     &outList)
{
    INT nRet = NoMethod;

    switch( parse.getCommand() )
    {
        case ControlRequest:
            {
                int offset = (parse.getFlags() & CMD_FLAG_CTL_OPEN) ? 2 : 1;

                CtiProtocolDNP::XferPoint controlout;
                controlout.type   = StatusOutputPointType;
                controlout.offset = offset;
                controlout.value  = 1;

                _dnp.setCommand(CtiProtocolDNP::DNP_SetDigitalOut, &controlout, 1);

                break;
            }

        case ScanRequest:
            {
                _dnp.setCommand(CtiProtocolDNP::DNP_Class0Read);

                break;
            }

        case GetValueRequest:
        case PutValueRequest:
        case GetStatusRequest:
            {

            }
        case PutStatusRequest:
        case GetConfigRequest:
        case PutConfigRequest:
        case LoopbackRequest:
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
    }

    _dnp.commOut( OutMessage, outList );

    return nRet;
}


INT CtiDeviceCBC6510::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist<CtiMessage>&vgList, RWTPtrSlist<CtiMessage>&retList, RWTPtrSlist<OUTMESS>&outList)
{
    INT ErrReturn = InMessage->EventCode & 0x3fff;

    _dnp.commIn(InMessage, outList);

    if( _dnp.hasPoints() )
    {
        _dnp.sendPoints(vgList, retList);
    }

    return ErrReturn;
}

INT CtiDeviceCBC6510::ErrorDecode(INMESS *InMessage, RWTime& Now, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
{
    INT retCode = NORMAL;

    CtiCommandParser  parse(InMessage->Return.CommandStr);
    CtiReturnMsg     *pPIL = new CtiReturnMsg(getID(),
                                              RWCString(InMessage->Return.CommandStr),
                                              RWCString(),
                                              InMessage->EventCode & 0x7fff,
                                              InMessage->Return.RouteID,
                                              InMessage->Return.MacroOffset,
                                              InMessage->Return.Attempt,
                                              InMessage->Return.TrxID,
                                              InMessage->Return.UserID);
    CtiPointDataMsg  *commFailed;
    CtiPointBase     *commPoint;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Error decode for device " << getName() << " in progress " << endl;
    }

    if( pPIL != NULL )
    {
        //  insert "Sky is falling" messages into pPIL here

        // send the whole mess to dispatch
        if (pPIL->PointData().entries() > 0)
        {
            retList.insert( pPIL );
        }
        else
        {
            delete pPIL;
        }
        pPIL = NULL;
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return retCode;
}



/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
RWCString CtiDeviceCBC6510::getDescription(const CtiCommandParser &parse) const
{
   RWCString tmp;

   tmp = "CBC Device: " + getName() + " SN: " + CtiNumStr(_cbc.getSerial());

   return tmp;
}


CtiTableDeviceCBC   CtiDeviceCBC6510::getCBC() const    { return _cbc; }
CtiTableDeviceCBC  &CtiDeviceCBC6510::getCBC()          { return _cbc; }

CtiDeviceCBC6510   &CtiDeviceCBC6510::setCBC(const CtiTableDeviceCBC &aRef)
{
   _cbc = aRef;
   return *this;
}

void CtiDeviceCBC6510::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
   Inherited::getSQL(db, keyTable, selector);
   CtiTableDeviceCBC::getSQL(db, keyTable, selector);
}

void CtiDeviceCBC6510::DecodeDatabaseReader(RWDBReader &rdr)
{
   Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

   if(getDebugLevel() & 0x0800) cout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;

   _cbc.DecodeDatabaseReader(rdr);
}









