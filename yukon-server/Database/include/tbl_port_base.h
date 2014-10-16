#pragma once

#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"
#include "loggable.h"


class IM_EX_CTIYUKONDB CtiTablePortBase : public CtiMemDBObject, private boost::noncopyable, public Cti::Loggable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTablePortBase(const CtiTablePortBase&);
    CtiTablePortBase& operator=(const CtiTablePortBase&);

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
