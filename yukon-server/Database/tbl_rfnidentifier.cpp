#include "precompiled.h"

#include "tbl_rfnidentifier.h"

namespace Cti {
namespace Database {
namespace Tables {

RfnIdentifier RfnIdentifierTable::DecodeDatabaseReader(RowReader &rdr)
{
    RfnIdentifier rfnId;

    rdr["SerialNumber"] >> rfnId.serialNumber;
    rdr["Manufacturer"] >> rfnId.manufacturer;
    rdr["Model"]        >> rfnId.model;

    return rfnId;
}


}
}
}
