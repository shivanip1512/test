#pragma once

#include "dbaccess.h"
#include "guard.h"

class SAConnection;

namespace Cti {
namespace Database {

typedef std::set<long> id_set;
typedef id_set::const_iterator id_set_itr;

class IM_EX_CTIBASE DatabaseConnection
{
private:
    SAConnection *connection;
    
    void*  operator new   (size_t){return NULL;};
    void*  operator new[] (size_t){return NULL;};
    void   operator delete   (void *){};
    void   operator delete[] (void *){};

protected:
    operator SAConnection*();

    friend class DatabaseWriter;
    friend class DatabaseReader;

public:
    DatabaseConnection();
    
    virtual ~DatabaseConnection();

    void beginTransaction();
    bool commitTransaction();
};

}
}// Namespace Cti::Database
