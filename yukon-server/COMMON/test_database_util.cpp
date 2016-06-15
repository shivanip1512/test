#include <boost/test/unit_test.hpp>

#include "database_util.h"

BOOST_AUTO_TEST_SUITE( test_database_util )

BOOST_AUTO_TEST_CASE(test_createIdSqlClause)
{
    using Cti::Database::createIdSqlClause;
    BOOST_CHECK_EQUAL(createIdSqlClause(123, "YukonPAObject", "paObjectId"),
                      "YukonPAObject.paObjectId = 123");

    const Cti::Database::id_set idSet{ 123, 456, 789 };

    BOOST_CHECK_EQUAL(createIdSqlClause(idSet, "YukonPAObject", "paObjectId"),
                      "YukonPAObject.paObjectId IN (123,456,789)");
}

BOOST_AUTO_TEST_CASE(test_createIdEqualClause)
{
    using Cti::Database::createIdEqualClause;

    BOOST_CHECK_EQUAL(createIdEqualClause("YukonPAObject", "paObjectId"),
                      "YukonPAObject.paObjectId = ?");
}

BOOST_AUTO_TEST_CASE(test_createIdInClause)
{
    using Cti::Database::createIdInClause;

    BOOST_CHECK_EQUAL(createIdInClause("YukonPAObject", "paObjectId", 3),
                      "YukonPAObject.paObjectId IN (?,?,?)");

    BOOST_CHECK_EQUAL(createIdInClause("YukonPAObject", "paObjectId", 1),
                      "YukonPAObject.paObjectId = ?");

    BOOST_CHECK_EQUAL(createIdInClause("YukonPAObject", "paObjectId", 0),
                      "YukonPAObject.paObjectId IN ()");
}

BOOST_AUTO_TEST_SUITE_END()
