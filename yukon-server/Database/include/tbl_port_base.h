#pragma once

#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTablePortBase : public CtiMemDBObject
{

protected:

    // Original CommPort Table
    //LONG          PortID;
    //string     Description;                   // This is no longer used as the name in porter anywhere!!!
    //INT           Type;

    UINT _protocol;              // 32 protocols.
    bool _alarmInhibit;

    std::string _sharedPortType;
    INT _sharedSocketNumber;

private:

    public:

    typedef CtiMemDBObject Inherited;

    CtiTablePortBase();
    CtiTablePortBase(const CtiTablePortBase& aRef);

    virtual ~CtiTablePortBase();

    CtiTablePortBase& operator=(const CtiTablePortBase& aRef);

    INT   getProtocol() const;
    void  setProtocol(int t);


    CtiTablePortBase& setAlarmInhibit(bool b);
    bool  getAlarmInhibit() const;

    void setSharedPortType(std::string str);
    std::string getSharedPortType() const;
    INT getSharedSocketNumber() const;
    void setSharedSocketNumber(INT sockNum);

    static std::string getTableName();
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
    virtual void DumpData();
};
