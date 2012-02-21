#include <boost/test/unit_test.hpp>

#include "database_reader.h"
#include "row_reader.h"
#include "dbaccess.h"
#include "ctidate.h"
#include "utility.h"

#include "tbl_pt_property.h"

#include <iostream>
#include <sstream>
#include <algorithm>

BOOST_AUTO_TEST_SUITE( test_rtdb_sql )

BOOST_AUTO_TEST_CASE(test_sql_string)
{
    ///// VARIABLES

        std::stringstream ss;
        long pntID = 0, paoID = 0;
        std::vector<long> pointIds;

        pointIds.push_back(4);
        pointIds.push_back(8);

    /////

    ///// DECLARE DEVICE TYPE

    /////

    ///// DATABASE VARIABLES

    /////

    ///// RW SELECTOR BUILDING AND OUTPUT

        //std::cout << selector.asString() << std::endl;

    /////

    ///// DATABASE READER CONFIGURATION AND OUTPUT

        std::string sql = "SELECT JD.id, JD.phrase "
                          "FROM Jordan JD";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);

        rdr.execute();

        //std::cout << isNull << std::endl;

    /////
}

BOOST_AUTO_TEST_SUITE_END()
