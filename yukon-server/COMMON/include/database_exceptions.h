#pragma once

namespace Cti {
namespace Database {

class DBException : public std::exception
{
    const std::string description;

public:

    DBException(const std::string& desc)
    :   description(desc)
    {}

    virtual ~DBException()
    {}

    const char* what() const
    {
        return description.c_str();
    }
};

class PrimaryKeyViolationException : public DBException
{
public:

    PrimaryKeyViolationException(const std::string& desc)
    :   DBException(std::string("Primary Key Violation: ") + desc)
    {}

    ~PrimaryKeyViolationException()
    {}
};

class ForeignKeyViolationException : public DBException
{
public:

    ForeignKeyViolationException(const std::string& desc)
    :   DBException(std::string("Foreign Key Violation: ") + desc)
    {}

    ~ForeignKeyViolationException()
    {}
};

}
} // Namespace Cti::Database
