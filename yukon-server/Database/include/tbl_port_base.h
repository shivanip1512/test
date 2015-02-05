#pragma once

#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"
#include "loggable.h"


class IM_EX_CTIYUKONDB CtiTablePortBase : public CtiMemDBObject, private boost::noncopyable, public Cti::Loggable
{
    UINT _protocol;              // 32 protocols.
    bool _alarmInhibit;

    std::string _sharedPortType;
    INT _sharedSocketNumber;

public:

    typedef CtiMemDBObject Inherited;

    CtiTablePortBase();
    virtual ~CtiTablePortBase();

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
    virtual std::string toString() const override;
};
