#pragma once

#include "database_connection.h"
#include "ctitime.h"
#include "row_reader.h"
#include "dlldefs.h"
#include <string>
#include <SQLAPI.h>

namespace Cti {
namespace Database {

class IM_EX_CTIBASE DatabaseReader : public RowReader
{
private:
    int _currentIndex; // SACommand index is 1 based
    bool _isValid;
    bool _executeCalled; // Here to warn if execute is not called

    SACommand _command;

public:

    DatabaseReader(Cti::Database::DatabaseConnection &conn, const std::string &command = "");
    ~DatabaseReader();

    bool setCommandText(const std::string &command);
    bool isValid();
    bool execute();
    bool executeWithRetries();
    void executeCommand();

    // Checks if the current index is null.
    // For example: rdr["test"].isNull();
    bool isNull();
    bool operator()();

    RowReader &operator[](const char *columnName);
    RowReader &operator[](const std::string &columnName);
    RowReader &operator[](int columnNumber); // 0 based

    RowReader &operator>>(bool &operand);
    RowReader &operator>>(short &operand);
    RowReader &operator>>(unsigned short &operand);
    RowReader &operator>>(long &operand);
    RowReader &operator>>(INT &operand);
    RowReader &operator>>(UINT &operand);
    RowReader &operator>>(UCHAR &operand);
    RowReader &operator>>(unsigned long &operand);
    RowReader &operator>>(long long &operand);
    RowReader &operator>>(double &operand);
    RowReader &operator>>(float &operand);
    RowReader &operator>>(CtiTime &operand);
    RowReader &operator>>(boost::posix_time::ptime &operand);
    RowReader &operator>>(std::string &operand);
    RowReader &extractChars(char *destination, unsigned count);

    // inputs for variable binding
    RowReader &operator<<(const bool operand);
    RowReader &operator<<(const short operand);
    RowReader &operator<<(const unsigned short operand);
    RowReader &operator<<(const long operand);
    RowReader &operator<<(const INT operand);
    RowReader &operator<<(const UINT operand);
    RowReader &operator<<(const unsigned long operand);
    RowReader &operator<<(const long long operand);
    RowReader &operator<<(const double operand);
    RowReader &operator<<(const float operand);
    RowReader &operator<<(const CtiTime &operand);
    RowReader &operator<<(const boost::posix_time::ptime &operand);
    RowReader &operator<<(const std::string &operand);
    RowReader &operator<<(const char *operand);
    
    template <class T>
    RowReader &operator<<(const std::vector<T>& container)
    {
        for( const auto& element : container )
        {
            *this << element;
        }
        return *this;
    }

    template <class T>
    RowReader &operator<<(const std::set<T>& container)
    {
        for( const auto& element : container )
        {
            *this << element;
        }
        return *this;
    }

    std::string asString();
};

}
}// Namespace Cti::Database
