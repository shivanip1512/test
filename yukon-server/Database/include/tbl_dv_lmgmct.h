/*-----------------------------------------------------------------------------*
*
* File:   tbl_dv_lmgmct
*
* Class:  CtiTableLMGroupMCT
* Date:   5/23/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/12/20 17:16:08 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)
#ifndef __TBL_DV_LMGMCT_H__
#define __TBL_DV_LMGMCT_H__

#include <string>

#include "dlldefs.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableLMGroupMCT
{
    enum AddressLevels;

private:
    unsigned long _address;

    AddressLevels _addressLevel;

    unsigned int _relays;

    long _routeID, _deviceID, _mctUniqueAddress;

protected:

public:

    CtiTableLMGroupMCT();
    CtiTableLMGroupMCT( const CtiTableLMGroupMCT &aRef );

    virtual ~CtiTableLMGroupMCT();

    CtiTableLMGroupMCT &operator=( const CtiTableLMGroupMCT &aRef );

    enum AddressLevels
    {
        Addr_Bronze,
        Addr_Lead,
        Addr_Unique,
        Addr_Invalid
    };

    static std::string getTableName();

    unsigned int getRelays();
    unsigned long getAddress() const;
    AddressLevels getAddressLevel() const;
    long getRouteID() const;
    long getMCTUniqueAddress() const;

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};

#endif // #ifndef __TBL_DV_LMGMCT_H__
