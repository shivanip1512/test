#pragma warning( disable : 4786)
#ifndef __DEV_GRP_MACRO_H__
#define __DEV_GRP_MACRO_H__

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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/04/17 14:54:37 $
*
* Copyright (c) 1999-2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

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

    typedef vector< CtiDeviceBase * >           deviceVec_t;
    typedef vector< CtiDeviceBase * >::iterator deviceIter_t;

    deviceVec_t _deviceList;

    CtiMutex _deviceListMux;

private:

    INT analyzeWhiteRabbits( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    bool coalesceRippleGroups( CtiRequestMsg *pReq, CtiCommandParser &parse, BYTE *RippleMessage );
    bool executeOnSubGroupRoute( const CtiDeviceBase *&pBase, set< LONG > &executedRouteSet );

public:

    typedef CtiDeviceBase Inherited;

    CtiDeviceMacro( );
    CtiDeviceMacro( const CtiDeviceMacro &aRef );
    ~CtiDeviceMacro( );

    CtiDeviceMacro &operator=( const CtiDeviceMacro &aRef );
    virtual void DumpData( void );

    void clearDeviceList( void );
    CtiDeviceMacro &addDevice( CtiDeviceBase *toAdd );

    void DecodeDatabaseReader( RWDBReader &rdr );
    static void getSQL( RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector );
    virtual INT ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    virtual INT processTrxID( int trx, RWTPtrSlist< CtiMessage >  &vgList );
    virtual INT initTrxID( int trx, CtiCommandParser &parse, RWTPtrSlist< CtiMessage >  &vgList );
    virtual RWCString getDescription(const CtiCommandParser & parse) const;
};

#endif // #ifndef __DEV_GRP_MACRO_H__
