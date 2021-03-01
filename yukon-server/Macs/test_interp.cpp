#include <boost/test/unit_test.hpp>
#include <boost/assign/list_of.hpp>

#include "interp.h"

int NoOp( Tcl_Interp * )
{
    return 0;
}

struct test_CtiInterpreter : CtiInterpreter
{
    test_CtiInterpreter(std::set<std::string> commandsToEscape) :
        CtiInterpreter(NoOp, commandsToEscape)
    {
    }

    using CtiInterpreter::isEscapeCommand;
    using CtiInterpreter::escapeQuotationMarks;
};

const std::set<std::string> commandsToEscape = boost::assign::list_of
    ("jack")
    ("kate")
    ("hurley")
    ("sawyer")
    ("sayid")
    ("locke");

BOOST_AUTO_TEST_SUITE( test_interp )

BOOST_AUTO_TEST_CASE( test_isEscapeCommand )
{
    test_CtiInterpreter interp(commandsToEscape);

    BOOST_REQUIRE(!interp.isEscapeCommand(""));
    BOOST_REQUIRE(!interp.isEscapeCommand(" "));
    BOOST_REQUIRE(!interp.isEscapeCommand("\t"));
    BOOST_REQUIRE( interp.isEscapeCommand("jack is a surgeon"));
    BOOST_REQUIRE(!interp.isEscapeCommand("Jack is a surgeon"));      // case-sensitive
    BOOST_REQUIRE( interp.isEscapeCommand(" kate is a thief"));
    BOOST_REQUIRE(!interp.isEscapeCommand(" Kate is a thief"));        // case-sensitive
    BOOST_REQUIRE( interp.isEscapeCommand("\thurley is a millionaire"));
    BOOST_REQUIRE(!interp.isEscapeCommand("\tHurley is a millionaire"));// case-sensitive
    BOOST_REQUIRE( interp.isEscapeCommand(" \tsawyer is a con artist"));
    BOOST_REQUIRE(!interp.isEscapeCommand(" \tSawyer is a con artist")); // case-sensitive
    BOOST_REQUIRE( interp.isEscapeCommand("sayid is a soldier"));
    BOOST_REQUIRE(!interp.isEscapeCommand("Sayid is a soldier"));     // case-sensitive
    BOOST_REQUIRE( interp.isEscapeCommand("locke is a salesman"));
    BOOST_REQUIRE(!interp.isEscapeCommand("Locke is a salesman"));    // case-sensitive

    BOOST_REQUIRE(!interp.isEscapeCommand("ben is a liar"));          // ben isn't on the list.
    BOOST_REQUIRE(!interp.isEscapeCommand("ben lied to jack"));       // jack is present, but not the first word.
}

BOOST_AUTO_TEST_CASE( test_escapeQuotes )
{
    test_CtiInterpreter interp(commandsToEscape);

    const std::vector<std::string> expected = boost::assign::list_of
        ("jack said, \\\"Last week most of us were strangers.\\\"") // jack is okay, escape it
        ("locke said something about \\\\\"The Island.\\\\\"")      // locke is okay, even with already-escaped quotes, escape it
        ("Sawyer called her \"Freckles.\"")                         // wrong case, don't escape
        ("he caught kate reading his letter to \"Sawyer.\"")        // kate isn't at the start of the string, and sawyer is the wrong case, don't escape
        ("ben said, \"You have thin doors.\"")                      // ben isn't a keyword, don't escape
        ("ben wondered about multiline commands.\n"
         "locke said, \\\"My command is to escape.\\\"")
        ("sawyer ate a burrito and exclaimed, \\\"Ole!\\\"\r\n"
         "Sayid ate macaroons and mumbled, \"Coconut.\"");

    const std::vector<std::string> input = boost::assign::list_of
        ("jack said, \"Last week most of us were strangers.\"")
        ("locke said something about \\\"The Island.\\\"")
        ("Sawyer called her \"Freckles.\"")
        ("he caught kate reading his letter to \"Sawyer.\"")
        ("ben said, \"You have thin doors.\"")
        ("ben wondered about multiline commands.\n"
         "locke said, \"My command is to escape.\"")
        ("sawyer ate a burrito and exclaimed, \"Ole!\"\r\n"
         "Sayid ate macaroons and mumbled, \"Coconut.\"");

    std::vector<std::string> output;

    for each( std::string inputString in input )
    {
        output.push_back(interp.escapeQuotationMarks(inputString));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(expected.begin(), expected.end(), output.begin(), output.end());
}

BOOST_AUTO_TEST_SUITE_END()
