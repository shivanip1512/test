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

class IM_EX_CTIYUKONDB CtiTableDeviceTnpp : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableDeviceTnpp(const CtiTableDeviceTnpp&);
    CtiTableDeviceTnpp& operator=(const CtiTableDeviceTnpp&);

protected:

   LONG                 _deviceID;
  // INT                  _originAddress;   //computers address?
   unsigned short       _inertia;
   int                  _destinationAddress; //The tnpp devices address
   int                  _originAddress;
   std::string            _identifierFormat;
   std::string            _pagerProtocol;
   std::string            _dataFormat;
   std::string            _channel;
   std::string            _zone;
   std::string            _functionCode;
   std::string            _pagerID;

public:

    CtiTableDeviceTnpp();
    virtual ~CtiTableDeviceTnpp();

    LONG getDeviceID() const;
    unsigned short getInertia() const;
    int getDestinationAddress() const; //The tnpp devices address
    int getOriginAddress() const; //The tnpp devices address
    std::string getIdentifierFormat();
    std::string getPagerProtocol();
    std::string getPagerDataFormat();
    std::string getChannel();
    std::string getZone();
    std::string getFunctionCode();
    std::string getPagerID();

    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    static std::string getTableName();
};

