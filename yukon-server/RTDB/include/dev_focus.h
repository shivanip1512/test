
#pragma once
#include "dev_meter.h"
#include "dlldefs.h"
#include "dev_ansi.h"
#include "prot_ansi.h"
#include "prot_ansi_focus.h"
#include "prot_ansi_sentinel.h"
#include "dsm2.h"
#include "types.h"
#include "dllyukon.h"

namespace Cti {
namespace Devices {

class IM_EX_DEVDB CtiDeviceFocus : public CtiDeviceAnsi
{
public:

    typedef CtiDeviceAnsi Inherited;

    CtiDeviceFocus();

   virtual Cti::Protocols::Ansi::CtiProtocolANSI& getANSIProtocol( void );
   virtual unsigned long updateLastLpTime();

   void buildScannerTableRequest  (BYTE *ptr, UINT flags) override;
   void buildCommanderTableRequest(BYTE *ptr, UINT flags) override;
   void buildSingleTableRequest(BYTE *ptr, UINT tableId) override;
   int buildTableRequest (BYTE *aMsg, Cti::Protocols::Ansi::ANSI_TABLE_WANTS* table,
                          WANTS_HEADER  header, BYTE scanOperation, UINT flags);

private:
   int getScannerTables(Cti::Protocols::Ansi::ANSI_TABLE_WANTS* table );
   int getCommanderTables(Cti::Protocols::Ansi::ANSI_TABLE_WANTS* table );
   CtiProtocolANSI_focus   _stdAnsiProtocol;

};

}
}

