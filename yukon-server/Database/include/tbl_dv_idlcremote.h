#pragma once

#include "types.h"
#include "dlldefs.h"
#include "dllbase.h"
#include "dbmemobject.h"
#include "row_reader.h"

class IM_EX_CTIYUKONDB CtiTableDeviceIDLC : public CtiMemDBObject, private boost::noncopyable
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiTableDeviceIDLC(const CtiTableDeviceIDLC&);
    CtiTableDeviceIDLC& operator=(const CtiTableDeviceIDLC&);

protected:

   LONG        _deviceID;
   LONG        _address;       // IDLC Addressing, etc.
   INT         _postdelay;
   INT         _ccuAmpUseType;
   INT         _currentAmp;

   void setCCUAmpUseType( const INT aAmpUseType );  //  for unit test

private:

    static std::string getTableName();
    LONG getDeviceID() const;

public:

   CtiTableDeviceIDLC();
   virtual ~CtiTableDeviceIDLC();

   LONG getAddress() const;
   INT  getAmp();
   INT  getPostDelay() const;
   INT  getCCUAmpUseType() const;

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
};

