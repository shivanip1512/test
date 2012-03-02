#pragma once
#include "dlldefs.h"
#include "dev_focus.h"

class IM_EX_DEVDB Ipc420ADDevice : public CtiDeviceFocus 
{

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


