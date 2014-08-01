#pragma once

#include "dev_base.h"


class CtiDeviceSystem : public CtiDeviceBase
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceSystem(const CtiDeviceSystem&);
    CtiDeviceSystem& operator=(const CtiDeviceSystem&);

    typedef CtiDeviceBase Inherited;

protected:

public:

   CtiDeviceSystem();
   virtual ~CtiDeviceSystem();

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              std::list< CtiMessage* >      &vgList,
                              std::list< CtiMessage* >      &retList,
                              std::list< OUTMESS* >         &outList);

   virtual std::string getDescription(const CtiCommandParser &parse) const;

   virtual INT  ProcessResult(const INMESS*, CtiTime&, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

};
