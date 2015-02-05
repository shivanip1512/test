#pragma once
#include "dlldefs.h"
#include "dev_focus.h"
#include "prot_ansi.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Ipc410ALDevice : public CtiDeviceFocus
{
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

