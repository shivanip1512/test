#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#define BCD                unsigned char

#pragma pack( push, 1)

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable53 : public CtiAnsiTableBase
{
protected:

   TIME_OFFSET_RCD      time_offset_table;

private:


public:

   CtiAnsiTable53( BYTE *dataBlob, int timefmat );

   virtual ~CtiAnsiTable53();

   CtiAnsiTable53& operator=(const CtiAnsiTable53& aRef);
   void printResult( const std::string& deviceName);

};
