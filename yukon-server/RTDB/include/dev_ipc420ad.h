#pragma once
#include "dlldefs.h"
#include "dev_focus.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Ipc420ADDevice : public CtiDeviceFocus 
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    Ipc420ADDevice(const Ipc420ADDevice&);
    Ipc420ADDevice& operator=(const Ipc420ADDevice&);

public:

   typedef CtiDeviceFocus Inherited;

   Ipc420ADDevice();
   virtual ~Ipc420ADDevice();
  
   int buildScannerTableRequest (BYTE *ptr, UINT flags);
   int buildCommanderTableRequest (BYTE *ptr, UINT flags);

private:
   int getScannerTables(Cti::Protocols::Ansi::ANSI_TABLE_WANTS* table );
   int getCommanderTables(Cti::Protocols::Ansi::ANSI_TABLE_WANTS* table );

};

}
}

