/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_mct
*
* Class:  CtiDeviceGroupMCT
* Date:   5/23/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/06/27 20:53:57 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)
#ifndef __DEV_GRP_MCT_H__
#define __DEV_GRP_MCT_H__

#include "dev_grp.h"
#include "tbl_dv_lmgmct.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"

class CtiDeviceGroupMCT : public CtiDeviceGroupBase
{
private:

    CtiTableLMGroupMCT _lmGroupMCT;

protected:

    enum
    {
        MCTGroup_Restore       = 0x00,
        MCTGroup_Shed_Base_07m = 0x00,
        MCTGroup_Shed_Base_15m = 0x10,
        MCTGroup_Shed_Base_30m = 0x20,
        MCTGroup_Shed_Base_60m = 0x30,

        MCTGroup_BronzeAddr_Base    = 4190208,
        MCTGroup_LeadLoadAddr_Base  = 4182016,
        MCTGroup_LeadMeterAddr_Base = 4186112
    };

    typedef CtiDeviceGroupBase Inherited;

public:

    CtiDeviceGroupMCT();
    CtiDeviceGroupMCT( const CtiDeviceGroupMCT &aRef );
    virtual ~CtiDeviceGroupMCT();

    CtiDeviceGroupMCT& operator=( const CtiDeviceGroupMCT &aRef );

    virtual LONG getRouteID();
    virtual LONG getAddress() const;
    virtual RWCString getDescription( const CtiCommandParser &parse ) const;
    virtual void getSQL( RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector );
    virtual void DecodeDatabaseReader( RWDBReader &rdr );
    virtual INT ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );
    virtual INT executeControl( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList );

};

#endif // #ifndef __DEV_GRP_MCT_H__
