#include "boost_test_helpers.h"

Cti::Test::use_in_unit_tests_only test_tag;

using Cti::Test::PreventDatabaseConnections;

BOOST_GLOBAL_FIXTURE( PreventDatabaseConnections );