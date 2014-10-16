#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <utility>

#include "porter.h"

class IM_EX_PROT CtiProtocol711 : private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiProtocol711(const CtiProtocol711&);
    CtiProtocol711& operator=(const CtiProtocol711&);

protected:

   BYTE  _masterToSlave[261];
   BYTE  _slaveToMaster[261];

public:
   CtiProtocol711() {}
   virtual ~CtiProtocol711() {}

   CtiProtocol711& setMasterRequest(const BYTE *ptr);
   CtiProtocol711& setSlaveResponse(const BYTE *ptr);

   void describeSlaveResponse() const;
   void describeMasterRequest() const;
   void describeACTNRequest(const BYTE *data, INT len, std::ostringstream &outLog) const;
   void describeDTRANRequest(const BYTE *data, INT len, std::ostringstream &outLog) const;
   void describeLGRPQRequest(const BYTE *data, INT len, std::ostringstream &outLog) const;
   void describeRCOLQRequest(const BYTE *data, INT len, std::ostringstream &outLog) const;
   void describeXTIMERequest(const BYTE *data, INT len, std::ostringstream &outLog) const;
   void describeSlaveStatS(const BYTE *stat, std::ostringstream &outLog) const;
   void describeSlaveStatD(const BYTE *stat, std::ostringstream &outLog) const;
   void describeSlaveStatP(const BYTE *stat, std::ostringstream &outLog) const;

   void describeRCOLQResponse(const BYTE *data, INT len, std::ostringstream &outLog) const;
   void describeACTNResponse(std::ostringstream &outLog) const;
};
