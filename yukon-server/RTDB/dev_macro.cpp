/*-----------------------------------------------------------------------------*
*
* File:   dev_macro
*
* Date:   10/16/2001
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.22 $
* DATE         :  $Date: 2008/10/28 19:21:42 $
*
* Copyright (c) 1999-2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "row_reader.h"

#include "dsm2.h"
#include "devicetypes.h"
#include "message.h"
#include "msg_pcrequest.h"
#include "msg_pdata.h"
#include "dllbase.h"
#include "dev_macro.h"
#include "dev_grp_ripple.h"
#include "logger.h"
#include "hashkey.h"
#include "pt_numeric.h"
#include "numstr.h"
#include "rte_base.h"

#include <list>

using std::list;
using namespace std;

CtiDeviceMacro::CtiDeviceMacro( )  {  }

CtiDeviceMacro::CtiDeviceMacro( const CtiDeviceMacro &aRef )
{
    *this = aRef;
}

CtiDeviceMacro::~CtiDeviceMacro( )  {  }


CtiDeviceMacro &CtiDeviceMacro::operator=( const CtiDeviceMacro &aRef )
{
    if( this != &aRef )
    {
        Inherited::operator=( aRef );

        //  copy their pointer list, but tag it unclean
        _deviceList.clear( );
        _deviceList.insert( _deviceList.begin( ), aRef._deviceList.begin( ), aRef._deviceList.end( ) );
    }

    return *this;
}


void CtiDeviceMacro::DumpData( )
{
    Inherited::DumpData( );

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << "Macro device, id " << getID( ) << endl;

        for( int i = 0; i < _deviceList.size( ); i++ )
            dout << "id of subdevice " << i+1 << ": " << _deviceList[i]->getID() << endl;
    }
}


void CtiDeviceMacro::clearDeviceList( void )
{
    CtiLockGuard<CtiMutex> guard(_deviceListMux);
    _deviceList.clear( );
}

CtiDeviceMacro &CtiDeviceMacro::addDevice( CtiDeviceSPtr toAdd )
{
    CtiLockGuard<CtiMutex> guard(_deviceListMux);

    if( toAdd )
        _deviceList.push_back( toAdd );

    return *this;
}

string CtiDeviceMacro::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                     "YP.disableflag, DV.deviceid, DV.alarminhibit, DV.controlinhibit "
                                   "FROM YukonPAObject YP, Device DV "
                                   "WHERE upper (YP.paoclass) = 'GROUP' AND upper (YP.type) = 'MACRO GROUP' "
                                     "AND YP.paobjectid = DV.deviceid";

    return sqlCore;
}

void CtiDeviceMacro::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel( ) & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}


INT CtiDeviceMacro::ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    CtiLockGuard<CtiMutex> guard(_deviceListMux);
    INT nRet = NORMAL;

    if( !_deviceList.empty( ) )
    {
        analyzeWhiteRabbits(pReq, parse, OutMessage, vgList, retList, outList);

        // Normal operations here

        set< LONG > executedRouteSet;
        deviceIter_t devIter(_deviceList.begin( ));
        size_t vglistsize = vgList.size();
        size_t newvglistsize = 0;

        const CtiCommandParser origparse = parse;

        for( ; devIter != _deviceList.end( ); devIter++)
        {
            CtiDeviceSPtr &pBase = *devIter;
            OUTMESS *pOutMessage = CTIDBG_new OUTMESS(*OutMessage);           // Create a personalized copy for this sub-device.

            if(pOutMessage)
            {
                if( executeOnSubGroupRoute(pBase, executedRouteSet) )
                {
                    pBase->ExecuteRequest( pReq, parse, pOutMessage, vgList, retList, outList );
                }
                else
                {
                    pBase->initTrxID( OutMessage->TrxID, parse, vgList );      // Must init the non-participants so they count too.
                }

                // It is our job to clean up after any Execute chain which does not consume the new message.
                if(pOutMessage)
                {
                    delete pOutMessage;
                    pOutMessage = 0;
                }
            }
            else
            {
                nRet = MEMORY;
            }

            parse = origparse;      // Reassign parse in case the last device added some clutter.
        }

        newvglistsize = vgList.size();

        CtiPointSPtr pPoint;

        if (pPoint = getDeviceControlPointOffsetEqual(GRP_CONTROL_STATUS))
        {
            if(parse.getCommand() == ControlRequest)
                reportControlStart( parse.getControlled(), parse.getiValue("control_interval"), parse.getiValue("control_reduction", 100), vgList, removeCommandDynamicText(parse.getCommandStr()) );


            if(newvglistsize > vglistsize)
            {
                /*
                 *  We claim all generated messages as having come from the device macro and not their causal device object here.
                 */
                //Switching from index 'for' loop to iterator while loop. Back to front
                //for(int i = newvglistsize; i > vglistsize; i-- )
                std::list< CtiMessage* >::reverse_iterator itr = vgList.rbegin();
                while( itr != vgList.rend() )
                {
                    CtiMessage *&pMsg = *itr;

                    if(pMsg->isA() == MSG_SIGNAL)
                    {
                        CtiSignalMsg *pSig = (CtiSignalMsg *)pMsg;
                        pSig->setId( pPoint->getID() );
                        pSig->setText( getDescription(parse) );
                    }
                    ++itr;
                }
            }
        }
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "No devices in macro device \'" << getName( ) << "\'" << endl;
    }

    return nRet;
}



/*
 * Look for any Ripple Groups and combine their bit patterns on on top of the other.
 *
 */
bool CtiDeviceMacro::coalesceRippleGroups( CtiRequestMsg *pReq, CtiCommandParser &parse, BYTE *RippleMessage )
{
    bool bRippled = false;
    bool bAllRipple = true;

    deviceIter_t devIter(_deviceList.begin());

    for( ; devIter != _deviceList.end( ); devIter++)
    {
        CtiDeviceSPtr &pBase = *devIter;
        if(pBase->getType() != TYPE_LMGROUP_RIPPLE)                 // We need to look for a RIPPLE GROUP
        {
            // There is a NON-RIPPLE group in here! we do nothing!
            bAllRipple = false;
        }
    }

    if(bAllRipple)
    {
        bRippled = true;
        ::memset(RippleMessage, 0, 7);                // Ripple messages are 7 bytes.

        for( devIter = _deviceList.begin(); devIter != _deviceList.end( ); devIter++)
        {
            CtiDeviceSPtr &pBase = *devIter;
            CtiDeviceGroupRipple &Grp =  *((CtiDeviceGroupRipple*)(pBase.get()));
            Grp.contributeToBitPattern(RippleMessage, parse.isControlled());
            Grp.setShed(parse.getControlled());
        }

        if(getDebugLevel() & DEBUGLEVEL_RIPPLE)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << "  Ripple message has been bit-packed ";

            char of = dout .fill('0');
            int i, j;
            for(i = 0; i < 7; i++)
            {
                dout << hex << setw(2) << (int)RippleMessage[i] << " ";
            }

            dout << dec << endl;
            dout .fill(of);
        }
    }

    return bRippled;
}


/*
 * This method examines a set and a group to determine if the route has already been submitted for execution.
 * we only execute on any given route ONE time.
 *
 * This method may fail is the device route is a macro.
 */
bool CtiDeviceMacro::executeOnSubGroupRoute( const CtiDeviceSPtr &pBase, set< LONG > &executedRouteSet )
{
    bool bExecute = true;           // The default case is, yes, do the execute.

    // This block handles the Ripple Bit packing scheme used at EREPC.
    if(gCoalesceRippleBits)
    {
        if(pBase->getType() == TYPE_LMGROUP_RIPPLE)                 // We need to look for a RIPPLE GROUP
        {
            // OK, we only execute on a route ONE time.  Use the set to indicate a collision!
            CtiDeviceGroupRipple &Grp =  *((CtiDeviceGroupRipple*)(pBase.get()));

            LONG rID = Grp.getRouteID();

            pair< set< LONG >::iterator, bool >  findpair = executedRouteSet.insert( rID );

            if( findpair.second == false )      // Already exists in the list, do not send on this route again!
            {
                bExecute = false;
            }
        }
    }

    return bExecute;
}


INT CtiDeviceMacro::initTrxID( int trx, CtiCommandParser &parse, list< CtiMessage* >  &vgList)
{
    CtiPointSPtr pPoint;

    setResponsesOnTrxID(0);
    setTrxID(trx);

    if (pPoint = getDevicePointOffsetTypeEqual(1, AnalogPointType))
    {
        // We have a point match here...
        DOUBLE val = 0.0;
        string resString;

        if (pPoint->isNumeric())
        {
            val = boost::static_pointer_cast< CtiPointNumeric >(pPoint)->computeValueForUOM( (DOUBLE)0.0 );
        }

        resString = getName() + " / " + pPoint->getName();
        resString += " = " + CtiNumStr(val);

        //create a new data message
        CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), val, NormalQuality, pPoint->getType(), resString);

        if (pData != NULL)
        {
            vgList.push_back( pData );
        }
    }
    return NORMAL;
}

INT CtiDeviceMacro::processTrxID( int trx,  list< CtiMessage* >  &vgList)
{
    CtiPointSPtr pPoint;
    INT cnt = getResponsesOnTrxID();

    if( trx == getCurrentTrxID() )                          // Only process it if we are still on this trx id.  We have not sent another!
    {
        {
            CtiLockGuard<CtiMutex> guard(_deviceListMux);

            if( !_deviceList.empty( ) )
            {
                deviceIter_t devIter(_deviceList.begin( ));

                for( ; devIter != _deviceList.end( ); )
                {
                    CtiDeviceSPtr &pBase = *(devIter++);
                    pBase->processTrxID(trx, vgList);
                }
            }
        }

        // If we as a macro group have a TRX ID, It too will be incremented for this one!
        setResponsesOnTrxID(++cnt);
        if (pPoint = getDevicePointOffsetTypeEqual(1, AnalogPointType))
        {
            // We have a point match here...
            DOUBLE val = 0.0;
            if (pPoint->isNumeric())
            {
                val = boost::static_pointer_cast< CtiPointNumeric >(pPoint)->computeValueForUOM( (DOUBLE)cnt );
            }

            //create a new data message
            string s= getName() + " / " + pPoint->getName();
            s += " = " + CtiNumStr(val);
            CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), val, NormalQuality, pPoint->getType(), s);
            if (pData != NULL)
            {
                vgList.push_back( pData );
            }
        }
    }

    return cnt;
}


INT CtiDeviceMacro::analyzeWhiteRabbits( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NORMAL;

    // Do we need to process Ripple Bit Packing?
    if(gCoalesceRippleBits)
    {
        bool bCoalesced = coalesceRippleGroups( pReq, parse, OutMessage->Buffer.RSt.Message );

        if( bCoalesced )
        {
            setOutMessageLMGID( OutMessage->DeviceIDofLMGroup);     // This is the LM Group which started this mess
            setOutMessageTrxID( OutMessage->TrxID );                // This is the LM Group which started this mess
            initTrxID( OutMessage->TrxID, parse, vgList );
        }
    }

    return status;
}


string CtiDeviceMacro::getDescription(const CtiCommandParser & parse) const
{
    return string("Macro Group: " + getName());
}


