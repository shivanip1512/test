#pragma once
#include "dlldefs.h"
#include "dev_focus.h"
#include "prot_ansi.h"

class IM_EX_DEVDB Ipc410ALDevice : public CtiDeviceFocus 
{

public:

   typedef CtiDeviceFocus Inherited;

   Ipc410ALDevice();
   virtual ~Ipc410ALDevice();

   int buildScannerTableRequest (BYTE *ptr, UINT flags);
   int buildCommanderTableRequest (BYTE *ptr, UINT flags);

private:
   int getTables(Cti::Protocols::Ansi::ANSI_TABLE_WANTS *table);


};



