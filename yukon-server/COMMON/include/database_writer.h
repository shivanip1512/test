#pragma once

#include "ctitime.h"
#include "database_connection.h"
#include "row_writer.h"
#include "dlldefs.h"
#include <string>
#include <SQLAPI.h>

namespace Cti {
namespace Database {

class IM_EX_CTIBASE DatabaseWriter : public RowWriter
{
private:
    SACommand _command;

public:

    DatabaseWriter(DatabaseConnection &conn, const std::string &command = "");
    ~DatabaseWriter();

    RowWriter &setCommandText(const std::string &command);
    bool execute();
    void executeWithDatabaseException();

    void reset();  //  Reset the insert position so the command can be reused.

    RowWriter &operator<<(const SpecialValues operand);
    RowWriter &operator<<(const bool operand);
    RowWriter &operator<<(const short operand);
    RowWriter &operator<<(const unsigned short operand);
    RowWriter &operator<<(const long operand);
    RowWriter &operator<<(const INT operand);
    RowWriter &operator<<(const UINT operand);
    RowWriter &operator<<(const unsigned long operand);
    RowWriter &operator<<(const long long operand);
    RowWriter &operator<<(const double operand);
    RowWriter &operator<<(const float operand);
    RowWriter &operator<<(const CtiTime &operand);
    RowWriter &operator<<(const boost::posix_time::ptime &operand);
    RowWriter &operator<<(const std::string &operand);
    RowWriter &operator<<(const char *operand);

    std::string asString();

    long rowsAffected();
};

}
}// Namespace Cti::Database
