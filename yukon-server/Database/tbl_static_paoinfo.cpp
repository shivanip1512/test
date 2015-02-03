#include "precompiled.h"

#include "dbaccess.h"
#include "logger.h"
#include "numstr.h"
#include "tbl_static_paoinfo.h"
#include "database_reader.h"
#include "database_writer.h"

using namespace std;


const string CtiTableStaticPaoInfo::_empty_string = "(empty)";

//  !!!  these strings MUST NOT CHANGE - this is what the DB keys on  !!!
const string CtiTableStaticPaoInfo::_key_rds_ip_address      = "RDS_TRANSMITTER_IP_ADDRESS";
const string CtiTableStaticPaoInfo::_key_rds_ip_port         = "RDS_TRANSMITTER_IP_PORT";
const string CtiTableStaticPaoInfo::_key_rds_site_address    = "RDS_TRANSMITTER_SITE_ADDRESS";
const string CtiTableStaticPaoInfo::_key_rds_encoder_address = "RDS_TRANSMITTER_ENCODER_ADDRESS";
const string CtiTableStaticPaoInfo::_key_rds_transmit_speed  = "RDS_TRANSMITTER_TRANSMIT_SPEED";
const string CtiTableStaticPaoInfo::_key_rds_group_type      = "RDS_TRANSMITTER_GROUP_TYPE";
const string CtiTableStaticPaoInfo::_key_cps_one_way_encryption_key = "CPS_ONE_WAY_ENCRYPTION_KEY";
const string CtiTableStaticPaoInfo::_key_rds_spid            = "RDS_TRANSMITTER_SPID";
const string CtiTableStaticPaoInfo::_key_rds_aid_repeat_period  = "RDS_TRANSMITTER_AID_REPEAT_PERIOD";

const CtiTableStaticPaoInfo::key_map_t   CtiTableStaticPaoInfo::_key_map   = CtiTableStaticPaoInfo::init_key_map();

CtiTableStaticPaoInfo::key_map_t CtiTableStaticPaoInfo::init_key_map()
{
    key_map_t retval;

    retval.insert(make_pair(Key_RDS_IP_Address,      &_key_rds_ip_address));
    retval.insert(make_pair(Key_RDS_IP_Port,         &_key_rds_ip_port));
    retval.insert(make_pair(Key_RDS_Site_Address,    &_key_rds_site_address));
    retval.insert(make_pair(Key_RDS_Encoder_Address, &_key_rds_encoder_address));
    retval.insert(make_pair(Key_RDS_Transmit_Speed,  &_key_rds_transmit_speed));
    retval.insert(make_pair(Key_RDS_Group_Type,      &_key_rds_group_type));
    retval.insert(make_pair(Key_CPS_One_Way_Encryption_Key, &_key_cps_one_way_encryption_key));
    retval.insert(make_pair(Key_RDS_SPID,            &_key_rds_spid));
    retval.insert(make_pair(Key_RDS_AID_Repeat_Period,  &_key_rds_aid_repeat_period));

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


string CtiTableStaticPaoInfo::getSQLCoreStatement()
{
    static const string sqlStart =  "SELECT SPI.staticpaoinfoid, SPI.paobjectid, SPI.infokey, SPI.value "
                                    "FROM StaticPaoInfo SPI "
                                    "WHERE SPI.value IS NOT NULL";

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


std::string CtiTableStaticPaoInfo::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableStaticPaoInfo";
    itemList.add("getEntryID()") << getEntryID();
    itemList.add("getPaoID()")   << getPaoID();
    itemList.add("getKey()")     << getKey();
    itemList.add("getValue()")   << getValue();

    return itemList.toString();
}

