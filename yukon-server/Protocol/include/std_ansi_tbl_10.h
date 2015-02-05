#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

class IM_EX_PROT CtiAnsiTable10 : public CtiAnsiTableBase
{
public:

   int getNumberUOMEntries( void );

   CtiAnsiTable10( BYTE *dataBlob );
   virtual ~CtiAnsiTable10();
   CtiAnsiTable10& operator=(const CtiAnsiTable10& aRef);

};
