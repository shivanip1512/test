
/*-----------------------------------------------------------------------------*
*
* File:   tbl_lmg_sa205
*
* Date:   2/10/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/02/17 15:08:03 $
*
* HISTORY      :
* $Log: tbl_lmg_sa205.cpp,v $
* Revision 1.1  2004/02/17 15:08:03  cplender
* New files for GRE/SA support
*
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#include "tbl_lmg_sa205.h"

CtiTableSA205LoadGroup::CtiTableSA205LoadGroup(const CtiTableSA205LoadGroup &aRef)
{
}

CtiTableSA205LoadGroup::~CtiTableSA205LoadGroup()
{
}

CtiTableSA205LoadGroup& CtiTableSA205LoadGroup::=(const CtiTableSA205LoadGroup &aRef)
{
    return ;
}

LONG CtiTableSA205LoadGroup::getLmGroupId()
{
    return ;
}

RWCString CtiTableSA205LoadGroup::getOperationalAddress()
{
    return ;
}

int CtiTableSA205LoadGroup::getAddressUsage()
{
    return ;
}

CtiTableSA205LoadGroup& CtiTableSA205LoadGroup::setLmGroupId(LONG newVal)
{
    return ;
}

CtiTableSA205LoadGroup& CtiTableSA205LoadGroup::setRouteId(LONG newVal)
{
    return ;
}

CtiTableSA205LoadGroup& CtiTableSA205LoadGroup::setOperationalAddress(RWCString newVal)
{
    return ;
}

CtiTableSA205LoadGroup& CtiTableSA205LoadGroup::setFunction(int newVal)
{
    return ;
}

CtiTableSA205LoadGroup& CtiTableSA205LoadGroup::setAddressUsage(int newVal)
{
    return ;
}

RWCString CtiTableSA205LoadGroup::getTableName()
{
    return ;
}

void CtiTableSA205LoadGroup::getSQL(RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector)
{
}

void CtiTableSA205LoadGroup::DecodeDatabaseReader(RWDBReader &rdr)
{
}

RWDBStatus CtiTableSA205LoadGroup::Restore()
{
    return ;
}

RWDBStatus CtiTableSA205LoadGroup::Insert()
{
    return ;
}

RWDBStatus CtiTableSA205LoadGroup::Update()
{
    return ;
}

RWDBStatus CtiTableSA205LoadGroup::Delete()
{
    return ;
}






