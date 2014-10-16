#include <boost/test/unit_test.hpp>

#include "string_util.h"

BOOST_AUTO_TEST_SUITE( test_string_formatter )

using Cti::StringFormatter;
using Cti::FormattedTable;
using Cti::FormattedList;

BOOST_AUTO_TEST_CASE(test_align)
{
    /// align (Left/Right/Center) ///

    {
        const std::string str = StringFormatter().width(10).left().format_copy("tEsT");
        const std::string exp = "tEsT      ";

        BOOST_CHECK_EQUAL(str, exp);
    }

    {
        const std::string str = StringFormatter().width(10).right().format_copy("tEsT");
        const std::string exp = "      tEsT";

        BOOST_CHECK_EQUAL(str, exp);
    }

    {
        const std::string str = StringFormatter().width(10).center().format_copy("tEsT");
        const std::string exp = "   tEsT   ";

        BOOST_CHECK_EQUAL(str, exp);
    }

    /// align with fill (Left/Right/Center) ///

    {
        const std::string str = StringFormatter().width(10).left().fill('$').format_copy("tEsT");
        const std::string exp = "tEsT$$$$$$";

        BOOST_CHECK_EQUAL(str, exp);
    }

    {
        const std::string str = StringFormatter().width(10).right().fill('$').format_copy("tEsT");
        const std::string exp = "$$$$$$tEsT";

        BOOST_CHECK_EQUAL(str, exp);
    }

    {
        const std::string str = StringFormatter().width(10).center().fill('$').format_copy("tEsT");
        const std::string exp = "$$$tEsT$$$";

        BOOST_CHECK_EQUAL(str, exp);
    }


    /// align no truncate - default (Left/Right/Center) ///

    {
        const std::string str = StringFormatter().width(5).left().format_copy("helLO worLd");
        const std::string exp = "helLO worLd";

        BOOST_CHECK_EQUAL(str, exp);
    }

    {
        const std::string str = StringFormatter().width(5).right().format_copy("helLO worLd");
        const std::string exp = "helLO worLd";

        BOOST_CHECK_EQUAL(str, exp);
    }

    {
        const std::string str = StringFormatter().width(5).center().format_copy("helLO worLd");
        const std::string exp = "helLO worLd";

        BOOST_CHECK_EQUAL(str, exp);
    }

    ///  align truncate (Left/Right/Center) ///

    {
        const std::string str = StringFormatter().width(5).left().truncate().format_copy("helLO worLd");
        const std::string exp = "helLO";

        BOOST_CHECK_EQUAL(str, exp);
    }

    {
        const std::string str = StringFormatter().width(5).right().truncate().format_copy("helLO worLd");
        const std::string exp = "worLd";

        BOOST_CHECK_EQUAL(str, exp);
    }

    {
        const std::string str = StringFormatter().width(5).center().truncate().format_copy("helLO worLd");
        const std::string exp = "LO wo";

        BOOST_CHECK_EQUAL(str, exp);
    }
}

BOOST_AUTO_TEST_CASE(test_string_table_no_borders)
{
    FormattedTable table;

    table.setCell(0, 0) << "column 0 row 0";
    table.setCell(0, 1) << "column 1 row 0";
    table.setCell(0, 2) << "column 2 row 0";
    table.setCell(0, 3) << "column 3 row 0";
    table.setCell(0, 4) << "column 4 row 0";

    table.setCell(1, 0) << "column 0 row 1 ";
    table.setCell(1, 1) << "column 1 row 1 ";
    table.setCell(1, 2) << "column 2 row 1 ";
    table.setCell(1, 3) << "column 3 row 1 ";
    table.setCell(1, 4) << "column 4 row 1 A";

    table.setCell(2, 0) << "column 0 row 2 ";
    table.setCell(2, 1) << "column 1 row 2 ";
    table.setCell(2, 2) << "column 2 row 2 ";
    table.setCell(2, 3) << "column 3 row 2 B";
    table.setCell(2, 4) << "column 4 row 2 BB";

    table.setCell(3, 0) << "column 0 row 3 ";
    table.setCell(3, 1) << "column 1 row 3 ";
    table.setCell(3, 2) << "column 2 row 3 C";
    table.setCell(3, 3) << "column 3 row 3 CC";
    table.setCell(3, 4) << "column 4 row 3 CCC";

    {
        const std::string exp =
                "\ncolumn 0 row 0  column 1 row 0  column 2 row 0    column 3 row 0     column 4 row 0    "
                "\ncolumn 0 row 1  column 1 row 1  column 2 row 1    column 3 row 1     column 4 row 1 A  "
                "\ncolumn 0 row 2  column 1 row 2  column 2 row 2    column 3 row 2 B   column 4 row 2 BB "
                "\ncolumn 0 row 3  column 1 row 3  column 2 row 3 C  column 3 row 3 CC  column 4 row 3 CCC";

        const std::string act = table.toString();

        BOOST_CHECK_EQUAL(act, exp);
    }


    table.setCell(0, 4, FormattedTable::Top_Right);
    table.setCell(1, 4, FormattedTable::Top_Right);
    table.setCell(2, 4, FormattedTable::Top_Right);
    table.setCell(3, 4, FormattedTable::Top_Right);

    {
        const std::string exp =
                "\ncolumn 0 row 0  column 1 row 0  column 2 row 0    column 3 row 0         column 4 row 0"
                "\ncolumn 0 row 1  column 1 row 1  column 2 row 1    column 3 row 1       column 4 row 1 A"
                "\ncolumn 0 row 2  column 1 row 2  column 2 row 2    column 3 row 2 B    column 4 row 2 BB"
                "\ncolumn 0 row 3  column 1 row 3  column 2 row 3 C  column 3 row 3 CC  column 4 row 3 CCC";

        const std::string act = table.toString();

        BOOST_CHECK_EQUAL(act, exp);
    }


    table.setCell(0, 0) << "this is a" << std::endl
                        << "multi-row" << std::endl
                        << "with"      << std::endl
                        << "4-lines";

    {
        const std::string exp =
                "\nthis is a       column 1 row 0  column 2 row 0    column 3 row 0         column 4 row 0"
                "\nmulti-row                                                                              "
                "\nwith                                                                                   "
                "\n4-lines                                                                                "
                "\ncolumn 0 row 1  column 1 row 1  column 2 row 1    column 3 row 1       column 4 row 1 A"
                "\ncolumn 0 row 2  column 1 row 2  column 2 row 2    column 3 row 2 B    column 4 row 2 BB"
                "\ncolumn 0 row 3  column 1 row 3  column 2 row 3 C  column 3 row 3 CC  column 4 row 3 CCC";

        const std::string act = table.toString();

        BOOST_CHECK_EQUAL(act, exp);
    }

    table.setCell(0, 1, FormattedTable::Middle_Left);
    table.setCell(0, 2, FormattedTable::Middle_Left);
    table.setCell(0, 3, FormattedTable::Middle_Left);
    table.setCell(0, 4, FormattedTable::Middle_Left);

    {
        const std::string exp =
                "\nthis is a                                                                              "
                "\nmulti-row       column 1 row 0  column 2 row 0    column 3 row 0     column 4 row 0    "
                "\nwith                                                                                   "
                "\n4-lines                                                                                "
                "\ncolumn 0 row 1  column 1 row 1  column 2 row 1    column 3 row 1       column 4 row 1 A"
                "\ncolumn 0 row 2  column 1 row 2  column 2 row 2    column 3 row 2 B    column 4 row 2 BB"
                "\ncolumn 0 row 3  column 1 row 3  column 2 row 3 C  column 3 row 3 CC  column 4 row 3 CCC";

        const std::string act = table.toString();

        BOOST_CHECK_EQUAL(act, exp);
    }

    table.setCell(0, 3, FormattedTable::Bottom_Right);

    {
        const std::string exp =
                "\nthis is a                                                                              "
                "\nmulti-row       column 1 row 0  column 2 row 0                       column 4 row 0    "
                "\nwith                                                                                   "
                "\n4-lines                                              column 3 row 0                    "
                "\ncolumn 0 row 1  column 1 row 1  column 2 row 1    column 3 row 1       column 4 row 1 A"
                "\ncolumn 0 row 2  column 1 row 2  column 2 row 2    column 3 row 2 B    column 4 row 2 BB"
                "\ncolumn 0 row 3  column 1 row 3  column 2 row 3 C  column 3 row 3 CC  column 4 row 3 CCC";

        const std::string act = table.toString();

        BOOST_CHECK_EQUAL(act, exp);
    }
}


BOOST_AUTO_TEST_CASE(test_string_table_borders)
{
    FormattedTable table;

    table.setCell(0, 0) << "column 0 row 0";
    table.setCell(0, 1) << "column 1 row 0";
    table.setCell(0, 2) << "column 2 row 0";
    table.setCell(0, 3) << "column 3 row 0";
    table.setCell(0, 4) << "column 4 row 0";

    table.setCell(1, 0) << "column 0 row 1 ";
    table.setCell(1, 1) << "column 1 row 1 ";
    table.setCell(1, 2) << "column 2 row 1 ";
    table.setCell(1, 3) << "column 3 row 1 ";
    table.setCell(1, 4) << "column 4 row 1 A";

    table.setCell(2, 0) << "column 0 row 2 ";
    table.setCell(2, 1) << "column 1 row 2 ";
    table.setCell(2, 2) << "column 2 row 2 ";
    table.setCell(2, 3) << "column 3 row 2 B";
    table.setCell(2, 4) << "column 4 row 2 BB";

    table.setCell(3, 0) << "column 0 row 3 ";
    table.setCell(3, 1) << "column 1 row 3 ";
    table.setCell(3, 2) << "column 2 row 3 C";
    table.setCell(3, 3) << "column 3 row 3 CC";
    table.setCell(3, 4) << "column 4 row 3 CCC";

    table.setHorizontalBorders(FormattedTable::Borders_Outside, 0); // show both border on row 0

    {
        const std::string exp =
                "\n---------------------------------------------------------------------------------------"
                "\ncolumn 0 row 0  column 1 row 0  column 2 row 0    column 3 row 0     column 4 row 0    "
                "\n---------------------------------------------------------------------------------------"
                "\ncolumn 0 row 1  column 1 row 1  column 2 row 1    column 3 row 1     column 4 row 1 A  "
                "\ncolumn 0 row 2  column 1 row 2  column 2 row 2    column 3 row 2 B   column 4 row 2 BB "
                "\ncolumn 0 row 3  column 1 row 3  column 2 row 3 C  column 3 row 3 CC  column 4 row 3 CCC";

        const std::string act = table.toString();

        BOOST_CHECK_EQUAL(act, exp);
    }

    table.setHorizontalBorders(FormattedTable::Borders_Outside_Bottom, 3); // show the border at the bottom of row 3

    {
        const std::string exp =
                "\n---------------------------------------------------------------------------------------"
                "\ncolumn 0 row 0  column 1 row 0  column 2 row 0    column 3 row 0     column 4 row 0    "
                "\n---------------------------------------------------------------------------------------"
                "\ncolumn 0 row 1  column 1 row 1  column 2 row 1    column 3 row 1     column 4 row 1 A  "
                "\ncolumn 0 row 2  column 1 row 2  column 2 row 2    column 3 row 2 B   column 4 row 2 BB "
                "\ncolumn 0 row 3  column 1 row 3  column 2 row 3 C  column 3 row 3 CC  column 4 row 3 CCC"
                "\n---------------------------------------------------------------------------------------";

        const std::string act = table.toString();

        BOOST_CHECK_EQUAL(act, exp);
    }

    table.setVerticalBorders(FormattedTable::Borders_All); // show all vertical borders

    {
        const std::string exp =
                "\n+----------------+----------------+------------------+-------------------+--------------------+"
                "\n| column 0 row 0 | column 1 row 0 | column 2 row 0   | column 3 row 0    | column 4 row 0     |"
                "\n+----------------+----------------+------------------+-------------------+--------------------+"
                "\n| column 0 row 1 | column 1 row 1 | column 2 row 1   | column 3 row 1    | column 4 row 1 A   |"
                "\n| column 0 row 2 | column 1 row 2 | column 2 row 2   | column 3 row 2 B  | column 4 row 2 BB  |"
                "\n| column 0 row 3 | column 1 row 3 | column 2 row 3 C | column 3 row 3 CC | column 4 row 3 CCC |"
                "\n+----------------+----------------+------------------+-------------------+--------------------+";

        const std::string act = table.toString();

        BOOST_CHECK_EQUAL(act, exp);
    }

    table.setVerticalBorders(FormattedTable::Borders_Outside); // show outside vertical border only

    {
        const std::string exp =
                "\n+-----------------------------------------------------------------------------------------+"
                "\n| column 0 row 0  column 1 row 0  column 2 row 0    column 3 row 0     column 4 row 0     |"
                "\n+-----------------------------------------------------------------------------------------+"
                "\n| column 0 row 1  column 1 row 1  column 2 row 1    column 3 row 1     column 4 row 1 A   |"
                "\n| column 0 row 2  column 1 row 2  column 2 row 2    column 3 row 2 B   column 4 row 2 BB  |"
                "\n| column 0 row 3  column 1 row 3  column 2 row 3 C  column 3 row 3 CC  column 4 row 3 CCC |"
                "\n+-----------------------------------------------------------------------------------------+";

        const std::string act = table.toString();

        BOOST_CHECK_EQUAL(act, exp);
    }

    table.setVerticalBorders(FormattedTable::Borders_Inside, FormattedTable::Range(2,3)); // show border between column 2 and 3

    {
        const std::string exp =
                "\n+--------------------------------------------------+---------------------------------------+"
                "\n| column 0 row 0  column 1 row 0  column 2 row 0   | column 3 row 0     column 4 row 0     |"
                "\n+--------------------------------------------------+---------------------------------------+"
                "\n| column 0 row 1  column 1 row 1  column 2 row 1   | column 3 row 1     column 4 row 1 A   |"
                "\n| column 0 row 2  column 1 row 2  column 2 row 2   | column 3 row 2 B   column 4 row 2 BB  |"
                "\n| column 0 row 3  column 1 row 3  column 2 row 3 C | column 3 row 3 CC  column 4 row 3 CCC |"
                "\n+--------------------------------------------------+---------------------------------------+";

        const std::string act = table.toString();

        BOOST_CHECK_EQUAL(act, exp);
    }
}

BOOST_AUTO_TEST_CASE(test_string_table_digits)
{
    FormattedTable table;

    table.setCell(0, 0) << 1.0f;
    table.setCell(0, 1) << 4.5f;
    table.setCell(0, 2) << 2.5999999f;
    table.setCell(0, 3) << 4.69999981f;
    table.setCell(0, 4) << 9.80000019f;

    table.setCell(1, 0) << -1;
    table.setCell(1, 1) << -2;
    table.setCell(1, 2) << 0xAB;
    table.setCell(1, 3) << (unsigned char)4;
    table.setCell(1, 4) << (unsigned)5;

    table.setCell(2, 0) << true;
    table.setCell(2, 1) << true;
    table.setCell(2, 2) << false;
    table.setCell(2, 3) << false;
    table.setCell(2, 4) << false;

    {
        const std::string exp =
                "\n1     4.5   2.5999999  4.69999981  9.80000019"
                "\n-1    -2    171        4           5         "
                "\ntrue  true  false      false       false     ";

        const std::string act = table.toString();

        BOOST_CHECK_EQUAL(act, exp);
    }
}

BOOST_AUTO_TEST_CASE(test_string_list_text)
{
    Cti::FormattedList itemList;

    itemList.add("element0")      << "value1";
    itemList.add("element1")      << "value2";
    itemList.add("Element2 ABCD") << "value 44";
    itemList.add("element 3")     << "Test";
    itemList.add("element 6")     << "hello world!";

    {
        const std::string exp =
                "\nelement0      : value1"
                "\nelement1      : value2"
                "\nElement2 ABCD : value 44"
                "\nelement 3     : Test"
                "\nelement 6     : hello world!";

        const std::string act = itemList.toString();

        BOOST_CHECK_EQUAL(act, exp);
    }
}

BOOST_AUTO_TEST_CASE(test_string_list_digits)
{
    Cti::FormattedList itemList;

    int val1           = 345;
    unsigned val2      = 554;
    float val3         = 2.5999999;
    unsigned char val4 = 123;

    std::ostringstream oss;
    oss << "this is oss";


    itemList.add("element0")      << true;
    itemList.add("element1")      << false;
    itemList.add("Element2 ABCD") << val1;
    itemList.add("element 3")     << val2;
    itemList.add("element 6")     << val3;
    itemList.add("ele 7")         << val4;
    itemList.add("ostringstream") << oss;

    {
        const std::string exp =
                "\nelement0      : true"
                "\nelement1      : false"
                "\nElement2 ABCD : 345"
                "\nelement 3     : 554"
                "\nelement 6     : 2.5999999"
                "\nele 7         : 123"
                "\nostringstream : this is oss";


        const std::string act = itemList.toString();

        BOOST_CHECK_EQUAL(act, exp);
    }
}

BOOST_AUTO_TEST_CASE(test_string_list_separator)
{
    Cti::FormattedList itemList;

    int val1           = 345;
    unsigned val2      = 554;
    float val3         = 2.5999999;
    unsigned char val4 = 123;

    itemList.add("element0")      << true;
    itemList.add("element1")      << false;

    itemList <<"** Separation 1 **";
    itemList.add("Element2 ABCD") << val1 << " and some text";
    itemList.add("element 3")     << val2;
    itemList.add("element 6")     << val3;

    itemList <<"** Separation 2 **";
    itemList.add("ele 7")         << val4;
    itemList.add("ele 8")         <<"string value";

    {
        const std::string exp =
                "\nelement0      : true"
                "\nelement1      : false"
                "\n** Separation 1 **"
                "\nElement2 ABCD : 345 and some text"
                "\nelement 3     : 554"
                "\nelement 6     : 2.5999999"
                "\n** Separation 2 **"
                "\nele 7         : 123"
                "\nele 8         : string value";

        const std::string act = itemList.toString();

        BOOST_CHECK_EQUAL(act, exp);
    }
}

BOOST_AUTO_TEST_CASE(test_string_list_multiline_values)
{
    Cti::FormattedList itemList;

    itemList.add("element0")      << "ele0 line1";

    itemList.add("element1")      << "ele1 line1\n";

    itemList.add("element2")      << "ele2 line1\n"
                                  << "ele2 line2";

    itemList.add("element3")      << "ele3 line1\n"
                                  << "ele3 line2\n"
                                  << "ele3 line3\n";

    itemList.add("element4")      << "\nele4 line2";

    itemList.add("element5 A")    << "ele5 line1" << std::endl
                                  << "ele5 line2";

    {
        const std::string exp =
            "\nelement0   : ele0 line1"
            "\nelement1   : ele1 line1"
            "\n"
            "\nelement2   : ele2 line1"
            "\n             ele2 line2"
            "\nelement3   : ele3 line1"
            "\n             ele3 line2"
            "\n             ele3 line3"
            "\n"
            "\nelement4   : "
            "\n             ele4 line2"
            "\nelement5 A : ele5 line1"
            "\n             ele5 line2";

        const std::string act = itemList.toString();

        BOOST_CHECK_EQUAL(act, exp);
    }
}

BOOST_AUTO_TEST_CASE(test_string_list_multiline_names)
{
    Cti::FormattedList itemList;

    itemList.add("element0"
               "\nelement0 - line 2")  << "ele0 line1";
    itemList.add("element1"
               "\n")                   << "ele1 line1";
    itemList.add("element2"
               "\nele")                << "ele2 line1";
    itemList.add("element3"
                "\nele - line 2"
                "\nele - line 3")      << "ele3 line1";
    itemList.add("element4")           << "ele4 line1";
    itemList.add("element5 - A")       << "ele5 line1";

    {
        const std::string exp =
            "\nelement0"
            "\nelement0 - line 2 : ele0 line1"
            "\nelement1"
            "\n                  : ele1 line1"
            "\nelement2"
            "\nele               : ele2 line1"
            "\nelement3"
            "\nele - line 2"
            "\nele - line 3      : ele3 line1"
            "\nelement4          : ele4 line1"
            "\nelement5 - A      : ele5 line1";

        const std::string act = itemList.toString();

        BOOST_CHECK_EQUAL(act, exp);
    }
}

BOOST_AUTO_TEST_SUITE_END()
