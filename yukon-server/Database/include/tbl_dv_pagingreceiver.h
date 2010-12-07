

#pragma once

#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDevicePagingReceiver : public CtiMemDBObject
{
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

    CtiTableDevicePagingReceiver(const CtiTableDevicePagingReceiver& aRef);

    virtual ~CtiTableDevicePagingReceiver();

    CtiTableDevicePagingReceiver& operator=(const CtiTableDevicePagingReceiver& aRef);

    float CtiTableDevicePagingReceiver::getFrequency() const;
    float CtiTableDevicePagingReceiver::getCapcode(int codeNumber) const;

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    LONG getDeviceID() const;

    static string getTableName();
};

