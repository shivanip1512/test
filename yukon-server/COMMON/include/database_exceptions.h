#pragma once

namespace Cti {
namespace Database {

class DatabaseException : public std::exception
{
    const std::string description;

public:

    DatabaseException(const std::string& desc)
    :   description(desc)
    {}

    virtual ~DatabaseException()
    {}

    const char* what() const
    {
        return description.c_str();
    }
};

class PrimaryKeyViolationException : public DatabaseException
{
public:

    PrimaryKeyViolationException(const std::string& desc)
    :   DatabaseException(std::string("Primary Key Violation: ") + desc)
    {}

    ~PrimaryKeyViolationException()
    {}
};

class ForeignKeyViolationException : public DatabaseException
{
public:

    ForeignKeyViolationException(const std::string& desc)
    :   DatabaseException(std::string("Foreign Key Violation: ") + desc)
    {}

    ~ForeignKeyViolationException()
    {}
};

}
} // Namespace Cti::Database
