#include "precompiled.h"

#include "InvalidReaderException.h"

#include "database_reader.h"

namespace Cti {
namespace Database {

InvalidReaderException::InvalidReaderException(DatabaseReader &rdr)
{
    try
    {
        _bad_sql = rdr.asString();
    }
    catch(SAException &e)
    {
        _bad_sql  = "[rdr.asString threw SAException - ";
        _bad_sql += e.ErrText().LockBuffer();
        _bad_sql += "]";

        e.ErrText().UnlockBuffer();
    }
}

const char *InvalidReaderException::what() const
{
    return _bad_sql.c_str();
}


}
}
