#pragma once

#include "rfn_identifier.h"
#include "row_reader.h"

#include "dlldefs.h"

namespace Cti {
namespace Database {
namespace Tables {

struct IM_EX_CTIYUKONDB RfnIdentifierTable
{
   static RfnIdentifier DecodeDatabaseReader(RowReader &rdr);

private:
   RfnIdentifierTable();
};

}
}
}

