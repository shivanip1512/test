#include "yukon.h"

#include "dbaccess.h"
#include "logger.h"
#include "numstr.h"
#include "tbl_static_paoinfo.h"
#include "database_reader.h"
#include "database_writer.h"

using namespace std;


const string CtiTableStaticPaoInfo::_empty_string = "(empty)";

//  !!!  these strings MUST NOT CHANGE - this is what the DB keys on  !!!
const string CtiTableStaticPaoInfo::_key_ip_address = "ip address";
const string CtiTableStaticPaoInfo::_key_ip_port    = "ip port";

const CtiTableStaticPaoInfo::key_map_t   CtiTableStaticPaoInfo::_key_map   = CtiTableStaticPaoInfo::init_key_map();

CtiTableStaticPaoInfo::key_map_t CtiTableStaticPaoInfo::init_key_map()
{
    key_map_t retval;

    retval.insert(make_pair(Key_IP_Address,     &_key_ip_address));
    retval.insert(make_pair(Key_IP_Port,        &_key_ip_port));

    return retval;
}


CtiTableStaticPaoInfo::CtiTableStaticPaoInfo() :
    _entry_id(-1),
    _pao_id(-1),
    _key(Key_Invalid),
    _value("")
{
}


CtiTableStaticPaoInfo::CtiTableStaticPaoInfo(const CtiTableStaticPaoInfo& aRef) :
    _entry_id(-1),
    _pao_id(-1),
    _key(Key_Invalid),
    _value("")
{
    *this = aRef;
}


CtiTableStaticPaoInfo::CtiTableStaticPaoInfo(long paoid, PaoInfoKeys k) :
    _entry_id(-1),
    _pao_id(paoid),
    _key(k),
    _value("")
{
}


CtiTableStaticPaoInfo::~CtiTableStaticPaoInfo()
{
}


CtiTableStaticPaoInfo& CtiTableStaticPaoInfo::operator=(const CtiTableStaticPaoInfo& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        _entry_id = aRef.getEntryID();
        _pao_id   = aRef.getPaoID();
        _key      = aRef.getKey();
        _value    = aRef.getValue();
    }

    return *this;
}


bool CtiTableStaticPaoInfo::operator<(const CtiTableStaticPaoInfo &rhs) const
{
    //  there should not be more than one of these in any device's collection of table entries, so this is safe for a total ordering
    //    it makes set-based lookups possible, as well - i didn't want to use a map in the device
    return getKey() < rhs.getKey();
}

bool CtiTableStaticPaoInfo::hasRow() const
{
    return (_entry_id > 0);
}

string CtiTableStaticPaoInfo::getSQLCoreStatement()
{
    static const string sqlStart =  "SELECT SPI.staticpaoinfoid, SPI.paobjectid, SPI.infokey, SPI.value "
                                    "FROM StaticPaoInfo SPI";

    return sqlStart;
}

void CtiTableStaticPaoInfo::DecodeDatabaseReader(Cti::RowReader& rdr)
{
    string tmp_key, tmp_value;
    long tmp_entryid, tmp_paoid;

    key_map_t::const_iterator k_itr;

    rdr["staticpaoinfoid"] >> tmp_entryid;
    rdr["paobjectid"] >> tmp_paoid;
    rdr["infokey"]    >> tmp_key;
    rdr["value"]      >> tmp_value;

    _entry_id = tmp_entryid;
    _pao_id = tmp_paoid;

    k_itr = _key_map.begin();
    while( k_itr != _key_map.end() )
    {
        if( !tmp_key.compare(*(k_itr->second)) )
        {
            _key = k_itr->first;
            k_itr = _key_map.end();
        }
        else
        {
            k_itr++;
        }
    }

    //  should we turn _empty_string into ""?
    _value = tmp_value;

    //  make sure this happens at the end, so we reset the dirty bit AFTER all of those above calls set it dirty
    resetDirty();
}


long CtiTableStaticPaoInfo::getEntryID() const
{
    return _entry_id;
}
long CtiTableStaticPaoInfo::getPaoID() const
{
    return _pao_id;
}
CtiTableStaticPaoInfo::PaoInfoKeys CtiTableStaticPaoInfo::getKey() const
{
    return _key;
}

string CtiTableStaticPaoInfo::getValue() const
{
    return _value;
}

//  these may need to become individually named get functions, if the assignment idiom doesn't work out
void CtiTableStaticPaoInfo::getValue(string &destination) const
{
    destination = _value;
}
void CtiTableStaticPaoInfo::getValue(int &destination) const
{
    destination = atoi(_value.c_str());
}

void CtiTableStaticPaoInfo::getValue(long &destination) const
{
    destination = atol(_value.c_str());
}
void CtiTableStaticPaoInfo::getValue(unsigned long &destination) const
{
    double tmp;
    getValue(tmp);

    if( tmp >= 0 )
    {
        destination = (unsigned long)tmp;
    }
    else
    {
        destination = 0UL;
    }
}
void CtiTableStaticPaoInfo::getValue(double &destination) const
{
    destination = atof(_value.c_str());
}


void CtiTableStaticPaoInfo::dump()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << "getEntryID() " << getEntryID() << endl;
        dout << "getPaoID()   " << getPaoID() << endl;
        dout << "getKey()     " << getKey() << endl;
        dout << "getValue()   " << getValue() << endl;
    }
}

