#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   tbl_lmg_sa305
*
* Date:   1/30/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
*
* HISTORY      :
* $Log: tbl_lmg_sa305.cpp,v $
* Revision 1.3  2005/02/10 23:23:48  alauinger
* Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build
*
* Revision 1.2  2004/03/18 19:46:44  cplender
* Added code to support the SA305 protocol and load group
*
* Revision 1.1  2004/02/17 15:08:03  cplender
* New files for GRE/SA support
*
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include "logger.h"
#include "tbl_lmg_sa305.h"

CtiTableSA305LoadGroup::CtiTableSA305LoadGroup()
{
}
CtiTableSA305LoadGroup::CtiTableSA305LoadGroup(const CtiTableSA305LoadGroup& aRef)
{
    *this = aRef;
}

CtiTableSA305LoadGroup::~CtiTableSA305LoadGroup()
{
}

CtiTableSA305LoadGroup& CtiTableSA305LoadGroup::operator=(const CtiTableSA305LoadGroup& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return *this;
}

LONG CtiTableSA305LoadGroup::getLmGroupId() const
{
    return _lmGroupId;
}

LONG CtiTableSA305LoadGroup::getRouteId() const
{
    return _routeId;
}

int CtiTableSA305LoadGroup::getUtility() const           // 4 bit address
{
    return _utility & 0x0000000F;
}

int CtiTableSA305LoadGroup::getGroup() const             // 6 bit address
{
    return _group & 0x0000003F;
}

int CtiTableSA305LoadGroup::getDivision() const          // 6 bit address
{
    return _division & 0x0000003F;
}

int CtiTableSA305LoadGroup::getSubstation() const        // 10 bit address
{
    return _substation & 0x000003FF;
}
int CtiTableSA305LoadGroup::getIndividual() const        // 22 bit serial number.  4173802 = 3FAFEA is an all call.
{
    return _individual & 0x003FFFFF;
}
int CtiTableSA305LoadGroup::getRateFamily() const        // 3 bits
{
    return _rateFamily & 0x00000007;
}
int CtiTableSA305LoadGroup::getRateMember() const        // 4 bits
{
    return _rateMember & 0x0000000F;
}
int CtiTableSA305LoadGroup::getHierarchy() const         // 1 bit
{
    return _hierarchy & 0x00000001;
}

int CtiTableSA305LoadGroup::getFunction() const          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
{
    return _function;
}

RWCString CtiTableSA305LoadGroup::getAddressUsage() const          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
{
    return _addressUsage;
}

CtiTableSA305LoadGroup& CtiTableSA305LoadGroup::setLmGroupId(LONG newVal)
{
    _lmGroupId = newVal;
    return *this;
}
CtiTableSA305LoadGroup& CtiTableSA305LoadGroup::setRouteId(LONG newVal)
{
    _routeId = newVal;
    return *this;
}
CtiTableSA305LoadGroup& CtiTableSA305LoadGroup::setUtility(int newVal)           // 4 bit address
{
    _utility = newVal;
    return *this;
}
CtiTableSA305LoadGroup& CtiTableSA305LoadGroup::setGroup(int newVal)             // 6 bit address
{
    _group = newVal;
    return *this;
}
CtiTableSA305LoadGroup& CtiTableSA305LoadGroup::setDivision(int newVal)          // 6 bit address
{
    _division = newVal;
    return *this;
}
CtiTableSA305LoadGroup& CtiTableSA305LoadGroup::setSubstation(int newVal)        // 10 bit address
{
    _substation = newVal;
    return *this;
}
CtiTableSA305LoadGroup& CtiTableSA305LoadGroup::setIndividual(int newVal)        // 22 bit serial number.  4173802 = 3FAFEA is an all call.
{
    _individual = newVal;
    return *this;
}
CtiTableSA305LoadGroup& CtiTableSA305LoadGroup::setRateFamily(int newVal)        // 3 bits
{
    _rateFamily = newVal;
    return *this;
}
CtiTableSA305LoadGroup& CtiTableSA305LoadGroup::setRateMember(int newVal)        // 4 bits
{
    _rateMember = newVal;
    return *this;
}
CtiTableSA305LoadGroup& CtiTableSA305LoadGroup::setHierarchy(int newVal)         // 1 bit
{
    _hierarchy = newVal;
    return *this;
}
CtiTableSA305LoadGroup& CtiTableSA305LoadGroup::setFunction(int newVal)          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
{
    _function = newVal;
    return *this;
}
CtiTableSA305LoadGroup& CtiTableSA305LoadGroup::setAddressUsage(RWCString newVal)          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
{
    _addressUsage = newVal;
    return *this;
}

RWCString CtiTableSA305LoadGroup::getTableName()
{
    return RWCString("lmgroupsa305");
}

void CtiTableSA305LoadGroup::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable devTbl = db.table(getTableName() );

    selector <<
    devTbl["groupid"           ] <<
    devTbl["routeid"           ] <<
    devTbl["addressusage"      ] <<
    devTbl["utilityaddress"    ] <<
    devTbl["groupaddress"      ] <<
    devTbl["divisionaddress"   ] <<
    devTbl["substationaddress" ] <<
    devTbl["individualaddress" ] <<
    devTbl["ratefamily"        ] <<
    devTbl["ratemember"        ] <<
    devTbl["ratehierarchy"     ] <<
    devTbl["loadnumber"        ] ;

    selector.from(devTbl);

    selector.where( keyTable["paobjectid"] == devTbl["groupid"] && selector.where() );
}
void CtiTableSA305LoadGroup::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWCString rwsTemp;

    rdr["groupid"           ] >> _lmGroupId;
    rdr["routeid"           ] >> _routeId;
    rdr["addressusage"      ] >> _addressUsage;
    rdr["utilityaddress"    ] >> _utility;
    rdr["groupaddress"      ] >> _group;
    rdr["divisionaddress"   ] >> _division;
    rdr["substationaddress" ] >> _substation;
    rdr["individualaddress" ] >> rwsTemp;
    _individual = atoi(rwsTemp.data());

    rdr["ratefamily"        ] >> _rateFamily;
    rdr["ratemember"        ] >> _rateMember;
    rdr["ratehierarchy"     ] >> _hierarchy;
    rdr["loadnumber"        ] >> rwsTemp;       // _function;

    _function &= 0x0000000F;

    if(rwsTemp.contains("1"))       _function |= 0x00000001;
    else                            _function &= ~0x00000001;

    if(rwsTemp.contains("2"))       _function |= 0x00000002;
    else                            _function &= ~0x00000002;

    if(rwsTemp.contains("3"))       _function |= 0x00000004;
    else                            _function &= ~0x00000004;

    if(rwsTemp.contains("4"))       _function |= 0x00000008;
    else                            _function &= ~0x00000008;

}

RWDBStatus CtiTableSA305LoadGroup::Restore()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
}

RWDBStatus CtiTableSA305LoadGroup::Insert()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return RWDBStatus::notSupported;
}

RWDBStatus CtiTableSA305LoadGroup::Update()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return RWDBStatus::notSupported;
}

RWDBStatus CtiTableSA305LoadGroup::Delete()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return RWDBStatus::notSupported;
}


