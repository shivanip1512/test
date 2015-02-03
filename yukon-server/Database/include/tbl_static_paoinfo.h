#pragma once

#include "dbmemobject.h"

#include <string>
#include <map>

namespace Cti {
    class RowReader;
}

class IM_EX_CTIYUKONDB CtiTableStaticPaoInfo : public CtiMemDBObject, public Cti::Loggable
{
public:

    enum PaoInfoKeys
    {
        //  this ordering can change without adverse effects - the strings are what the DB keys on
        Key_Invalid   =  -1,

        Key_RDS_IP_Address = 1000,
        Key_RDS_IP_Port,
        Key_RDS_Site_Address,
        Key_RDS_Encoder_Address,
        Key_RDS_Transmit_Speed,
        Key_RDS_Group_Type,
        Key_CPS_One_Way_Encryption_Key,
        Key_RDS_SPID,
        Key_RDS_AID_Repeat_Period,

        //  make sure to add any new enum values to the string map
    };

protected:

    static const std::string _key_rds_ip_address;
    static const std::string _key_rds_ip_port;
    static const std::string _key_rds_site_address;
    static const std::string _key_rds_encoder_address;
    static const std::string _key_rds_transmit_speed;
    static const std::string _key_rds_group_type;
    static const std::string _key_cps_one_way_encryption_key;
    static const std::string _key_rds_spid;
    static const std::string _key_rds_aid_repeat_period;

    typedef std::map<PaoInfoKeys,const std::string *> key_map_t;

    static const key_map_t   _key_map;

    static key_map_t   init_key_map();

    long             _entry_id;
    long             _pao_id;

    PaoInfoKeys   _key;
    std::string   _value;

    static const std::string _empty_string;

private:

public:

    typedef CtiMemDBObject Inherited;

    CtiTableStaticPaoInfo();
    CtiTableStaticPaoInfo(const CtiTableStaticPaoInfo &aRef);
    CtiTableStaticPaoInfo(long paoid, PaoInfoKeys k);  //  owner doesn't matter until the new row gets written to the DB

    CtiTableStaticPaoInfo& operator=(const CtiTableStaticPaoInfo &aRef);

    static std::string getSQLCoreStatement();

    void DecodeDatabaseReader(Cti::RowReader& rdr);

    long             getPaoID()       const;
    long             getEntryID()     const;
    PaoInfoKeys      getKey()         const;
    std::string      getValue()       const;

    void             getValue(int           &destination) const;
    void             getValue(long          &destination) const;
    void             getValue(unsigned long &destination) const;
    void             getValue(double        &destination) const;
    void             getValue(std::string   &destination) const;

    virtual std::string toString() const override;
};
