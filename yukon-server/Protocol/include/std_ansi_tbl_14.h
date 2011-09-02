#pragma once

#include "dlldefs.h"
#include "types.h"
#include "std_ansi_tbl_base.h"

#pragma pack( push, 1)

#pragma pack( pop )

class IM_EX_PROT CtiAnsiTable14 : public CtiAnsiTableBase
{
protected:

   struct DATA_RCD
   {
      unsigned char     *source_id;
   };

   struct DATA_CONTROL_RCD
   {
      DATA_RCD          *data_rcd;
   };

   DATA_CONTROL_RCD  _data_control_record;


private:

   int         _controlLength;
   int         _controlEntries;

public:

   CtiAnsiTable14( int dataCtrlLen, int numDataCtrlEntries );
   CtiAnsiTable14( BYTE *dataBlob, int dataCtrlLen, int numDataCtrlEntries );
   virtual ~CtiAnsiTable14();
   CtiAnsiTable14& operator=(const CtiAnsiTable14& aRef);
   void printResult( const std::string& deviceName );

   void decodeResultPiece( BYTE **dataBlob );
   void generateResultPiece( BYTE **dataBlob );


};
