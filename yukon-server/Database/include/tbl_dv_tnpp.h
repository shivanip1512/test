#pragma once

#include <limits.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/monitor.h>

#include <string>
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "yukon.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceTnpp : public CtiMemDBObject
{
protected:

   LONG                 _deviceID;
  // INT                  _originAddress;   //computers address?
   unsigned short       _inertia;
   int                  _destinationAddress; //The tnpp devices address
   int                  _originAddress;
   string            _identifierFormat;
   string            _pagerProtocol;
   string            _dataFormat;
   string            _channel;
   string            _zone;
   string            _functionCode;
   string            _pagerID;

public:

    CtiTableDeviceTnpp();

    CtiTableDeviceTnpp(const CtiTableDeviceTnpp& aRef);

    virtual ~CtiTableDeviceTnpp();

    CtiTableDeviceTnpp& operator=(const CtiTableDeviceTnpp& aRef);

    LONG getDeviceID() const;
    unsigned short getInertia() const;
    int getDestinationAddress() const; //The tnpp devices address
    int getOriginAddress() const; //The tnpp devices address
    string getIdentifierFormat();
    string getPagerProtocol();
    string getPagerDataFormat();
    string getChannel();
    string getZone();
    string getFunctionCode();
    string getPagerID();

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    static string getTableName();
};

