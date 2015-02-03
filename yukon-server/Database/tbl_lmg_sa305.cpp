#include "precompiled.h"

#include "logger.h"
#include "tbl_lmg_sa305.h"

using std::string;
using std::endl;

CtiTableSA305LoadGroup::CtiTableSA305LoadGroup() :
    _lmGroupId(0),
    _routeId(0),
    _utility(0),
    _group(0),
    _division(0),
    _substation(0),
    _individual(0),
    _rateFamily(0),
    _rateMember(0),
    _hierarchy(0),
    _function(0)
{
}

CtiTableSA305LoadGroup::~CtiTableSA305LoadGroup()
{
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

string CtiTableSA305LoadGroup::getAddressUsage() const          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
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
CtiTableSA305LoadGroup& CtiTableSA305LoadGroup::setAddressUsage(string newVal)          // bitmask for functions to operate upon bit 0 is function 1.  Bit 3 is function 4.
{
    _addressUsage = newVal;
    return *this;
}

string CtiTableSA305LoadGroup::getTableName()
{
    return string("lmgroupsa305");
}

void CtiTableSA305LoadGroup::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string tmpStr;

    rdr["groupid"           ] >> _lmGroupId;
    rdr["routeid"           ] >> _routeId;
    rdr["addressusage"      ] >> _addressUsage;
    rdr["utilityaddress"    ] >> _utility;
    rdr["groupaddress"      ] >> _group;
    rdr["divisionaddress"   ] >> _division;
    rdr["substationaddress" ] >> _substation;
    rdr["individualaddress" ] >> tmpStr;
    _individual = atoi(tmpStr.c_str());

    rdr["ratefamily"        ] >> _rateFamily;
    rdr["ratemember"        ] >> _rateMember;
    rdr["ratehierarchy"     ] >> _hierarchy;
    rdr["loadnumber"        ] >> tmpStr;       // _function;

    _function &= 0x0000000F;

    if(tmpStr.find("1")!=string::npos)       _function |= 0x00000001;
    else                            _function &= ~0x00000001;

    if(tmpStr.find("2")!=string::npos)       _function |= 0x00000002;
    else                            _function &= ~0x00000002;

    if(tmpStr.find("3")!=string::npos)       _function |= 0x00000004;
    else                            _function &= ~0x00000004;

    if(tmpStr.find("4")!=string::npos)       _function |= 0x00000008;
    else                            _function &= ~0x00000008;

}

