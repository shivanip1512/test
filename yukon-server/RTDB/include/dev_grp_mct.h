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
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2008/10/28 19:21:43 $
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

    typedef CtiDeviceGroupBase Inherited;

    CtiTableLMGroupMCT _lmGroupMCT;

protected:

    enum Commands
    {
        Command_Restore  = 0x00,

        Command_Shed_07m = 0x00,
        Command_Shed_15m = 0x10,
        Command_Shed_30m = 0x20,
        Command_Shed_60m = 0x30,
    };

    enum BaseAddresses
    {
        BaseAddress_LeadLoad  = 4182016,  //  3FD000
        BaseAddress_LeadMeter = 4186112,  //  3FE000
        BaseAddress_Bronze    = 4190208,  //  3FF000
    };

public:

    CtiDeviceGroupMCT();
    CtiDeviceGroupMCT( const CtiDeviceGroupMCT &aRef );
    virtual ~CtiDeviceGroupMCT();

    CtiDeviceGroupMCT& operator=( const CtiDeviceGroupMCT &aRef );

    virtual LONG getRouteID();
    virtual LONG getAddress() const;
    virtual string getDescription( const CtiCommandParser &parse ) const;
    virtual void getSQL( RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector ) const;
    virtual void DecodeDatabaseReader( RWDBReader &rdr );
    virtual INT ExecuteRequest( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );
    virtual INT executeControl( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList );

};

#endif // #ifndef __DEV_GRP_MCT_H__
