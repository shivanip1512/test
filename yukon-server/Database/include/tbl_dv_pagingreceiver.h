#pragma once

#include <limits.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDevicePagingReceiver : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableDevicePagingReceiver(const CtiTableDevicePagingReceiver&);
    CtiTableDevicePagingReceiver& operator=(const CtiTableDevicePagingReceiver&);

protected:

    LONG         _deviceID;
    float        _frequency;
    int          _capcode1;
    int          _capcode2;
    int          _capcode3;
    int          _capcode4;
    int          _capcode5;
    int          _capcode6;
    int          _capcode7;
    int          _capcode8;
    int          _capcode9;
    int          _capcode10;
    int          _capcode11;
    int          _capcode12;
    int          _capcode13;
    int          _capcode14;
    int          _capcode15;
    int          _capcode16;

public:

    CtiTableDevicePagingReceiver();
    virtual ~CtiTableDevicePagingReceiver();

    float CtiTableDevicePagingReceiver::getFrequency() const;
    float CtiTableDevicePagingReceiver::getCapcode(int codeNumber) const;

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    LONG getDeviceID() const;

    static std::string getTableName();
};

