
#pragma once
#include "dev_meter.h"
#include "dlldefs.h"
#include "dev_ansi.h"
#include "prot_ansi_focus.h"
#include "prot_ansi_sentinel.h"
#include "dsm2.h"
#include "types.h"
#include "dllyukon.h"

class IM_EX_DEVDB CtiDeviceFocus : public CtiDeviceAnsi
{

public:

    typedef CtiDeviceAnsi Inherited;

   CtiDeviceFocus();
   virtual ~CtiDeviceFocus();
   virtual Cti::Protocols::Ansi::CtiProtocolANSI& getANSIProtocol( void );
   virtual unsigned long updateLastLpTime();

   int buildScannerTableRequest (BYTE *ptr, UINT flags);
   int buildCommanderTableRequest (BYTE *ptr, UINT flags);
   int buildSingleTableRequest(BYTE *ptr, UINT tableId);


private:
   CtiProtocolANSI_focus   _stdAnsiProtocol;

};



