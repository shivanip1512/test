
/*-----------------------------------------------------------------------------*
*
* File:   tbl_lmg_golay
*
* Date:   2/4/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/02/17 15:08:03 $
*
* HISTORY      :
* $Log: tbl_lmg_golay.cpp,v $
* Revision 1.1  2004/02/17 15:08:03  cplender
* New files for GRE/SA support
*
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include "tbl_lmg_golay.h"

CtiTableGolayGroup::CtiTableGolayGroup()
{
}

CtiTableGolayGroup::CtiTableGolayGroup(const CtiTableGolayGroup &aRef)
{
}

CtiTableGolayGroup::~CtiTableGolayGroup()
{
}

CtiTableGolayGroup& CtiTableGolayGroup::operator=(const CtiTableGolayGroup &aRef)
{
    return ;
}

LONG CtiTableGolayGroup::getLmGroupId()
{
    return ;
}

LONG CtiTableGolayGroup::getRouteId()
{
    return ;
}

RWCString CtiTableGolayGroup::getOperationalAddress()
{
    return ;
}

int CtiTableGolayGroup::getFunction()
{
    return ;
}

int CtiTableGolayGroup::getNominalTimeout()
{
    return ;
}

int CtiTableGolayGroup::getVirtualTimeout()
{
    return ;
}

CtiTableGolayGroup& CtiTableGolayGroup::setLmGroupId(LONG newVal)
{
    return ;
}

CtiTableGolayGroup& CtiTableGolayGroup::setRouteId(LONG newVal)
{
    return ;
}

CtiTableGolayGroup& CtiTableGolayGroup::setOperationalAddress(RWCString newVal)
{
    return ;
}

CtiTableGolayGroup& CtiTableGolayGroup::setFunction(int newVal)
{
    return ;
}

CtiTableGolayGroup& CtiTableGolayGroup::setNominalTimeout(int newVal)
{
    return ;
}

CtiTableGolayGroup& CtiTableGolayGroup::setVirtualTimeout(int newVal)
{
    return ;
}

RWCString CtiTableGolayGroup::getTableName()
{
    return RWCString("LMGroupSA205Or105");
}

void CtiTableGolayGroup::getSQL(RWDBDatabase &db, RWDBTable &keyTable, RWDBSelector &selector)
{
}

void CtiTableGolayGroup::DecodeDatabaseReader(RWDBReader &rdr)
{
}

RWDBStatus CtiTableGolayGroup::Restore()
{
    return ;
}

RWDBStatus CtiTableGolayGroup::Insert()
{
    return ;
}

RWDBStatus CtiTableGolayGroup::Update()
{
    return ;
}

RWDBStatus CtiTableGolayGroup::Delete()
{
    return ;
}


