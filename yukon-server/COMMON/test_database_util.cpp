#include <boost/test/unit_test.hpp>

#include <boost/assign/list_of.hpp>

#include "database_util.h"

BOOST_AUTO_TEST_SUITE( test_database_util )

BOOST_AUTO_TEST_CASE(test_createIdSqlClause)
{
    using Cti::Database::createIdSqlClause;
    BOOST_CHECK_EQUAL(createIdSqlClause(123, "YukonPAObject", "paObjectId"),
                      "YukonPAObject.paObjectId = 123");

    const Cti::Database::id_set idSet = boost::assign::list_of
            (123)
            (456)
            (789);

    BOOST_CHECK_EQUAL(createIdSqlClause(idSet, "YukonPAObject", "paObjectId"),
                      "YukonPAObject.paObjectId IN (123,456,789)");
}

BOOST_AUTO_TEST_SUITE_END()
