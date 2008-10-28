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
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
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
using std::set;
using std::vector;

class IM_EX_DEVDB CtiDeviceMacro : public CtiDeviceGroupBase // 2004/1/4 CGP // : public CtiDeviceBase
{
private:

    typedef CtiDeviceGroupBase Inherited;

    INT analyzeWhiteRabbits( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    bool coalesceRippleGroups( CtiRequestMsg *pReq, CtiCommandParser &parse, BYTE *RippleMessage );
    bool executeOnSubGroupRoute( const CtiDeviceSPtr &pBase, set< LONG > &executedRouteSet );

protected:

    typedef std::vector< CtiDeviceSPtr >           deviceVec_t;
    typedef std::vector< CtiDeviceSPtr >::iterator deviceIter_t;

    deviceVec_t _deviceList;

    CtiMutex _deviceListMux;

public:

    CtiDeviceMacro( );
    CtiDeviceMacro( const CtiDeviceMacro &aRef );
    ~CtiDeviceMacro( );

    CtiDeviceMacro &operator=( const CtiDeviceMacro &aRef );
    virtual void DumpData( void );

    void clearDeviceList( void );
    CtiDeviceMacro &addDevice( CtiDeviceSPtr toAdd );

    virtual LONG getRouteID() { return 0L; }

    void DecodeDatabaseReader( RWDBReader &rdr );
    virtual void getSQL( RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector ) const;
    virtual INT ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    virtual INT processTrxID( int trx, list< CtiMessage* >  &vgList );
    virtual INT initTrxID( int trx, CtiCommandParser &parse, list< CtiMessage* >  &vgList );
    virtual string getDescription(const CtiCommandParser & parse) const;
};

#endif // #ifndef __DEV_GRP_MACRO_H__
