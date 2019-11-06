#pragma once

#include "database_connection.h"
#include "ctitime.h"
#include "row_reader.h"
#include "dlldefs.h"
#include <string>
#include <SQLAPI.h>

namespace Cti::Database {

class IM_EX_CTIBASE DatabaseReader : public RowReader
{
private:
    int _currentIndex; // SACommand index is 1 based
    bool _isValid;
    bool _executeCalled; // Here to warn if execute is not called

    SACommand _command;

    //  Private since no one calls it
    void executeWithDatabaseException();

public:

    DatabaseReader(Cti::Database::DatabaseConnection &conn, const std::string &command = "");
    ~DatabaseReader();

    bool setCommandText(const std::string &command);
    bool isValid();
    bool execute();
    bool executeWithRetries();

    // Checks if the current index is null.
    // For example: rdr["test"].isNull();
    bool isNull();
    bool operator()();

    RowReader &operator[](const char *columnName);
    RowReader &operator[](const std::string &columnName);
    RowReader &operator[](int columnNumber); // 0 based

private:
    operator bool()           override;
    operator short()          override;
    operator unsigned short() override;
    operator long()           override;
    operator int()            override;
    operator unsigned()       override;
    operator unsigned char()  override;
    operator unsigned long()  override;
    operator long long()      override;
    operator double()         override;
    operator float()          override;
    operator CtiTime()        override;
    operator boost_ptime()    override;
    operator std::string()    override;
    operator Bytes()          override;
    RowReader &extractChars(char *destination, unsigned count) override;

    void incrementColumnIndex() override;

public:
    // inputs for variable binding
    RowReader &operator<<(const short operand) override;
    RowReader &operator<<(const unsigned short operand) override;
    RowReader &operator<<(const long operand) override;
    RowReader &operator<<(const INT operand) override;
    RowReader &operator<<(const UINT operand) override;
    RowReader &operator<<(const unsigned long operand) override;
    RowReader &operator<<(const long long operand) override;
    RowReader &operator<<(const double operand) override;
    RowReader &operator<<(const float operand) override;
    RowReader &operator<<(const CtiTime &operand) override;
    RowReader &operator<<(const boost::posix_time::ptime &operand) override;
    RowReader &operator<<(const std::string &operand) override;
    RowReader &operator<<(const char *operand) override;
    
    template <class T>
    RowReader &operator<<(const T& container)
    {
        for( const auto& element : container )
        {
            *this << element;
        }
        //  If we're feeding in a bunch of IDs, we'll probably be reading back a bunch of rows, so prefetch at least 100 at a time.
        setPrefetch();
        return *this;
    }

    //  Used to prefetch 100 rows.  Useful if you're reading an entire rowset instead of just the first row.
    void setPrefetch();

    std::string asString();
};

}

