/*-----------------------------------------------------------------------------*
*
* File:   tbl_lmg_golay
*
* Date:   2/4/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:16:06 $
*
* HISTORY      :
* $Log: tbl_lmg_golay.cpp,v $
* Revision 1.4  2005/12/20 17:16:06  tspar
* Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex
*
* Revision 1.3.2.1  2005/07/12 21:08:32  jliu
* rpStringWithoutCmpParser
*
* Revision 1.3  2005/02/17 19:02:57  mfisher
* Removed space before CVS comment header, moved #include "yukon.h" after CVS header
*
* Revision 1.2  2005/02/10 23:23:48  alauinger
* Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build
*
* Revision 1.1  2004/02/17 15:08:03  cplender
* New files for GRE/SA support
*
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "tbl_lmg_golay.h"

using std::string;

CtiTableGolayGroup::CtiTableGolayGroup() :
    _lmGroupId(0),
    _routeId(0),
    _nominalTimeout(0),
    _virtualTimeout(0)
{
}

CtiTableGolayGroup::CtiTableGolayGroup(const CtiTableGolayGroup &aRef) :
    _lmGroupId(0),
    _routeId(0),
    _nominalTimeout(0),
    _virtualTimeout(0)
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

string CtiTableGolayGroup::getOperationalAddress()
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

CtiTableGolayGroup& CtiTableGolayGroup::setOperationalAddress(string newVal)
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

string CtiTableGolayGroup::getTableName()
{
    return string("LMGroupSA205Or105");
}

void CtiTableGolayGroup::DecodeDatabaseReader(Cti::RowReader &rdr)
{
}

