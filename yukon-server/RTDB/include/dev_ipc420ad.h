#pragma once
#include "dlldefs.h"
#include "dev_focus.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB Ipc420ADDevice : public CtiDeviceFocus
{
public:

   typedef CtiDeviceFocus Inherited;

   Ipc420ADDevice();

   void buildScannerTableRequest  (BYTE *ptr, UINT flags) override;
   void buildCommanderTableRequest(BYTE *ptr, UINT flags) override;

private:
   int getScannerTables(Cti::Protocols::Ansi::ANSI_TABLE_WANTS* table );
   int getCommanderTables(Cti::Protocols::Ansi::ANSI_TABLE_WANTS* table );

};

}
}

