#pragma once

#include <string>
#include <map>

#include "ctibase.h"
#include "dlldefs.h"
#include "dbmemobject.h"
#include "pointdefs.h"
#include "yukon.h"
#include "database_connection.h"
#include "rwutil.h"

using std::map;
using std::string;

class IM_EX_CTIYUKONDB CtiTableStaticPaoInfo : public CtiMemDBObject
{
public:

    enum PaoInfoKeys
    {
        //  this ordering can change without adverse effects - the strings are what the DB keys on
        Key_Invalid   =  -1,

        Key_IP_Address = 1000,
        Key_IP_Port,
        //  make sure to add any new enum values to the string map
    };

protected:

    static const string _key_ip_address;
    static const string _key_ip_port;

    typedef map<PaoInfoKeys,             const string *> key_map_t;

    static const key_map_t   _key_map;

    static key_map_t   init_key_map();

    long             _entry_id;
    long             _pao_id;

    PaoInfoKeys   _key;
    string _value;

    static const string _empty_string;

private:

public:

    typedef CtiMemDBObject Inherited;

    CtiTableStaticPaoInfo();
    CtiTableStaticPaoInfo(const CtiTableStaticPaoInfo &aRef);
    CtiTableStaticPaoInfo(long paoid, PaoInfoKeys k);  //  owner doesn't matter until the new row gets written to the DB

    virtual ~CtiTableStaticPaoInfo();

    CtiTableStaticPaoInfo& operator=(const CtiTableStaticPaoInfo &aRef);
    bool                    operator<(const CtiTableStaticPaoInfo &rhs) const;  //  this is for the set in dev_base

    bool hasRow() const;

    static string getSQLCoreStatement(CtiApplication_t _app_id);

    void DecodeDatabaseReader(Cti::RowReader& rdr);

    long             getPaoID()       const;
    long             getEntryID()     const;
    PaoInfoKeys      getKey()         const;
    string           getValue()       const;

    void             getValue(int           &destination) const;
    void             getValue(long          &destination) const;
    void             getValue(unsigned long &destination) const;
    void             getValue(double        &destination) const;
    void             getValue(string        &destination) const;

    virtual void dump();
};
