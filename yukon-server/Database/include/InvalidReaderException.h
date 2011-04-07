#pragma once

#include "dlldefs.h"

namespace Cti {
namespace Database {

class DatabaseReader;

class IM_EX_CTIYUKONDB InvalidReaderException : std::exception
{
public:

    InvalidReaderException(DatabaseReader &rdr);

    virtual const char *__CLR_OR_THIS_CALL what() const;

private:

    std::string _bad_sql;
};

}
}


