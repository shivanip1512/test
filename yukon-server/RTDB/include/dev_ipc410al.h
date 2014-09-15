#pragma once
#include "dlldefs.h"
#include "dev_focus.h"
#include "prot_ansi.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Ipc410ALDevice : public CtiDeviceFocus
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    Ipc410ALDevice(const Ipc410ALDevice&);
    Ipc410ALDevice& operator=(const Ipc410ALDevice&);

public:

   typedef CtiDeviceFocus Inherited;

   Ipc410ALDevice();

   void buildScannerTableRequest  (BYTE *ptr, UINT flags) override;
   void buildCommanderTableRequest(BYTE *ptr, UINT flags) override;

private:
   int getTables(Cti::Protocols::Ansi::ANSI_TABLE_WANTS *table);


};

}
}

