#pragma once

#include "types.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceIDLC : public CtiMemDBObject, private boost::noncopyable
{
   LONG        _deviceID;
   LONG        _address;       // IDLC Addressing, etc.
   INT         _postdelay;
   INT         _ccuAmpUseType;
   INT         _currentAmp;

   static std::string getTableName();
   LONG getDeviceID() const;

protected:

   void setCCUAmpUseType( const INT aAmpUseType );  //  for unit test

public:

   CtiTableDeviceIDLC();
   virtual ~CtiTableDeviceIDLC();

   LONG getAddress() const;
   INT  getAmp();
   INT  getPostDelay() const;
   INT  getCCUAmpUseType() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};

