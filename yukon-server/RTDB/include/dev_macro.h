/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_macro
*
* Class:  CtiDeviceGroupMacro
* Date:   10/12/2001
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2004/05/05 15:31:41 $
*
* Copyright (c) 1999-2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_GRP_MACRO_H__
#define __DEV_GRP_MACRO_H__
#pragma warning( disable : 4786)


#include "dev_base.h"
#include "dev_grp.h"
#include "cmdparse.h"
#include "mutex.h"
#include "yukon.h"
#include <set>
#include <vector>
using namespace std;

class IM_EX_DEVDB CtiDeviceMacro : public CtiDeviceBase
{
protected:

    typedef vector< CtiDeviceSPtr >           deviceVec_t;
    typedef vector< CtiDeviceSPtr >::iterator deviceIter_t;

    deviceVec_t _deviceList;

    CtiMutex _deviceListMux;

private:

    INT analyzeWhiteRabbits( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    bool coalesceRippleGroups( CtiRequestMsg *pReq, CtiCommandParser &parse, BYTE *RippleMessage );
    bool executeOnSubGroupRoute( const CtiDeviceSPtr &pBase, set< LONG > &executedRouteSet );

public:

    typedef CtiDeviceBase Inherited;

    CtiDeviceMacro( );
    CtiDeviceMacro( const CtiDeviceMacro &aRef );
    ~CtiDeviceMacro( );

    CtiDeviceMacro &operator=( const CtiDeviceMacro &aRef );
    virtual void DumpData( void );

    void clearDeviceList( void );
    CtiDeviceMacro &addDevice( CtiDeviceSPtr toAdd );

    void DecodeDatabaseReader( RWDBReader &rdr );
    virtual void getSQL( RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector );
    virtual INT ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    virtual INT processTrxID( int trx, RWTPtrSlist< CtiMessage >  &vgList );
    virtual INT initTrxID( int trx, CtiCommandParser &parse, RWTPtrSlist< CtiMessage >  &vgList );
    virtual RWCString getDescription(const CtiCommandParser & parse) const;
};

#endif // #ifndef __DEV_GRP_MACRO_H__
