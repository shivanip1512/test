#include <boost/test/unit_test.hpp>

#include "numstr.h"

using namespace std;

BOOST_AUTO_TEST_SUITE( test_numstr )

//  for integer types
template <class T>
void numstr_test_helper(T value, int padding, const string &str_base, const string &str_base_spad, const string &str_base_zpad,
                                              const string &str_hex,  const string &str_hex_spad,  const string &str_hexstr_zpad,
                                              const string &str_xhex, const string &str_xhex_spad, const string &str_xhex_zpad )
{
    const string &str_exp = str_base, &str_exp_spad = str_base_spad, &str_exp_zpad = str_base_zpad;


    CtiNumStr numstr(value);

    //  test without formats
                          BOOST_CHECK_EQUAL(numstr.toString(), str_base);
    numstr.spad(padding); BOOST_CHECK_EQUAL(numstr.toString(), str_base_spad);
    numstr.zpad(padding); BOOST_CHECK_EQUAL(numstr.toString(), str_base_zpad);

    //  reset padding
    numstr.spad(0);

    //  test exp format
    numstr.exp();         BOOST_CHECK_EQUAL(numstr.toString(), str_exp);
    numstr.spad(padding); BOOST_CHECK_EQUAL(numstr.toString(), str_exp_spad);
    numstr.zpad(padding); BOOST_CHECK_EQUAL(numstr.toString(), str_exp_zpad);

    //  reset padding
    numstr.spad(0);

    //  test hex format
    numstr.hex();         BOOST_CHECK_EQUAL(numstr.toString(), str_hex);
    numstr.spad(padding); BOOST_CHECK_EQUAL(numstr.toString(), str_hex_spad);
    numstr.zpad(padding); BOOST_CHECK_EQUAL(numstr.toString(), str_hexstr_zpad);

    //  reset padding
    numstr.spad(0);

    //  test xhex format
    numstr.xhex();        BOOST_CHECK_EQUAL(numstr.toString(), str_xhex);
    numstr.spad(padding); BOOST_CHECK_EQUAL(numstr.toString(), str_xhex_spad);
    numstr.zpad(padding); BOOST_CHECK_EQUAL(numstr.toString(), str_xhex_zpad);
}



//  for floating-point types
template <class T>
void numstr_test_helper(T value, int precision, int padding, const string &base, const string &base_spad, const string &base_zpad,
                                                             const string &exp,  const string &exp_spad,  const string &exp_zpad )
{
    const string  &hex = base,  &hex_spad = base_spad,  &hex_zpad = base_zpad,
                 &xhex = base, &xhex_spad = base_spad, &xhex_zpad = base_zpad;

    BOOST_TEST_MESSAGE("value="+to_string(value)+", precision=" + to_string(precision)+", padding=" + to_string(padding));
    
    CtiNumStr numstr(value, precision);

    //  test without formats
                          BOOST_CHECK_EQUAL(numstr.toString(), base);
    numstr.spad(padding); BOOST_CHECK_EQUAL(numstr.toString(), base_spad);
    numstr.zpad(padding); BOOST_CHECK_EQUAL(numstr.toString(), base_zpad);

    //  reset padding
    numstr.spad(0);

    //  test exp format
    numstr.exp();         BOOST_CHECK_EQUAL(numstr.toString(), exp);
    numstr.spad(padding); BOOST_CHECK_EQUAL(numstr.toString(), exp_spad);
    numstr.zpad(padding); BOOST_CHECK_EQUAL(numstr.toString(), exp_zpad);

    //  reset padding
    numstr.spad(0);

    //  test hex format
    numstr.hex();         BOOST_CHECK_EQUAL(numstr.toString(), hex);
    numstr.spad(padding); BOOST_CHECK_EQUAL(numstr.toString(), hex_spad);
    numstr.zpad(padding); BOOST_CHECK_EQUAL(numstr.toString(), hex_zpad);

    //  reset padding
    numstr.spad(0);

    //  test xhex format
    numstr.xhex();        BOOST_CHECK_EQUAL(numstr.toString(), xhex);
    numstr.spad(padding); BOOST_CHECK_EQUAL(numstr.toString(), xhex_spad);
    numstr.zpad(padding); BOOST_CHECK_EQUAL(numstr.toString(), xhex_zpad);
}



BOOST_AUTO_TEST_CASE(test_char)
{
    char c;
    unsigned char uc;

    numstr_test_helper(c =    0, 8,    "0", "       0",   "00000000",
                                       "0", "       0",   "00000000",
                                     "0x0", "     0x0", "0x00000000");

    numstr_test_helper(c =    1, 8,    "1", "       1",   "00000001",
                                       "1", "       1",   "00000001",
                                     "0x1", "     0x1", "0x00000001");

    numstr_test_helper(c =   15, 8,   "15", "      15",   "00000015",
                                       "f", "       f",   "0000000f",
                                     "0xf", "     0xf", "0x0000000f");

    numstr_test_helper(c =   16, 8,   "16", "      16",   "00000016",
                                      "10", "      10",   "00000010",
                                    "0x10", "    0x10", "0x00000010");

    numstr_test_helper(c =  127, 8,  "127", "     127",   "00000127",
                                      "7f", "      7f",   "0000007f",
                                    "0x7f", "    0x7f", "0x0000007f");

    numstr_test_helper(c =   -1, 8,   "-1", "      -1",   "-0000001",
                                      "ff", "      ff",   "000000ff",
                                    "0xff", "    0xff", "0x000000ff");

    numstr_test_helper(c =  -15, 8,  "-15", "     -15",   "-0000015",
                                      "f1", "      f1",   "000000f1",
                                    "0xf1", "    0xf1", "0x000000f1");

    numstr_test_helper(c =  -16, 8,  "-16", "     -16",   "-0000016",
                                      "f0", "      f0",   "000000f0",
                                    "0xf0", "    0xf0", "0x000000f0");

    numstr_test_helper(c = -120, 8, "-120", "    -120",   "-0000120",
                                      "88", "      88",   "00000088",
                                    "0x88", "    0x88", "0x00000088");

    numstr_test_helper(c = -128, 8, "-128", "    -128",   "-0000128",
                                      "80", "      80",   "00000080",
                                    "0x80", "    0x80", "0x00000080");

    numstr_test_helper(uc =   0, 8,    "0", "       0",   "00000000",
                                       "0", "       0",   "00000000",
                                     "0x0", "     0x0", "0x00000000");

    numstr_test_helper(uc =   1, 8,    "1", "       1",   "00000001",
                                       "1", "       1",   "00000001",
                                     "0x1", "     0x1", "0x00000001");

    numstr_test_helper(uc =  15, 8,   "15", "      15",   "00000015",
                                       "f", "       f",   "0000000f",
                                     "0xf", "     0xf", "0x0000000f");

    numstr_test_helper(uc =  16, 8,   "16", "      16",   "00000016",
                                      "10", "      10",   "00000010",
                                    "0x10", "    0x10", "0x00000010");

    numstr_test_helper(uc = 127, 8,  "127", "     127",   "00000127",
                                      "7f", "      7f",   "0000007f",
                                    "0x7f", "    0x7f", "0x0000007f");

    numstr_test_helper(uc = 128, 8,  "128", "     128",   "00000128",
                                      "80", "      80",   "00000080",
                                    "0x80", "    0x80", "0x00000080");

    numstr_test_helper(uc = 255, 8,  "255", "     255",   "00000255",
                                      "ff", "      ff",   "000000ff",
                                    "0xff", "    0xff", "0x000000ff");
}


BOOST_AUTO_TEST_CASE(test_short)
{
    short s;
    unsigned short us;

    numstr_test_helper(s =      0, 8,      "0", "       0",   "00000000",
                                           "0", "       0",   "00000000",
                                         "0x0", "     0x0", "0x00000000");

    numstr_test_helper(s =      1, 8,      "1", "       1",   "00000001",
                                           "1", "       1",   "00000001",
                                         "0x1", "     0x1", "0x00000001");

    numstr_test_helper(s =     15, 8,     "15", "      15",   "00000015",
                                           "f", "       f",   "0000000f",
                                         "0xf", "     0xf", "0x0000000f");

    numstr_test_helper(s =     16, 8,     "16", "      16",   "00000016",
                                          "10", "      10",   "00000010",
                                        "0x10", "    0x10", "0x00000010");

    numstr_test_helper(s =    255, 8,    "255", "     255",   "00000255",
                                          "ff", "      ff",   "000000ff",
                                        "0xff", "    0xff", "0x000000ff");

    numstr_test_helper(s =    256, 8,    "256", "     256",   "00000256",
                                         "100", "     100",   "00000100",
                                       "0x100", "   0x100", "0x00000100");

    numstr_test_helper(s =  12000, 8,  "12000", "   12000",   "00012000",
                                        "2ee0", "    2ee0",   "00002ee0",
                                      "0x2ee0", "  0x2ee0", "0x00002ee0");

    numstr_test_helper(s =  32767, 8,  "32767", "   32767",   "00032767",
                                        "7fff", "    7fff",   "00007fff",
                                      "0x7fff", "  0x7fff", "0x00007fff");

    numstr_test_helper(s = -32768, 8, "-32768", "  -32768",   "-0032768",
                                        "8000", "    8000",   "00008000",
                                      "0x8000", "  0x8000", "0x00008000");

    numstr_test_helper(s = -12000, 8, "-12000", "  -12000",   "-0012000",
                                        "d120", "    d120",   "0000d120",
                                      "0xd120", "  0xd120", "0x0000d120");

    numstr_test_helper(s =   -256, 8,   "-256", "    -256",   "-0000256",
                                        "ff00", "    ff00",   "0000ff00",
                                      "0xff00", "  0xff00", "0x0000ff00");

    numstr_test_helper(s =   -255, 8,   "-255", "    -255",   "-0000255",
                                        "ff01", "    ff01",   "0000ff01",
                                      "0xff01", "  0xff01", "0x0000ff01");

    numstr_test_helper(s =    -15, 8,    "-15", "     -15",   "-0000015",
                                        "fff1", "    fff1",   "0000fff1",
                                      "0xfff1", "  0xfff1", "0x0000fff1");

    numstr_test_helper(s =    -16, 8,    "-16", "     -16",   "-0000016",
                                        "fff0", "    fff0",   "0000fff0",
                                      "0xfff0", "  0xfff0", "0x0000fff0");

    numstr_test_helper(s =     -1, 8,     "-1", "      -1",   "-0000001",
                                        "ffff", "    ffff",   "0000ffff",
                                      "0xffff", "  0xffff", "0x0000ffff");

    numstr_test_helper(us =     0, 8,      "0", "       0",   "00000000",
                                           "0", "       0",   "00000000",
                                         "0x0", "     0x0", "0x00000000");

    numstr_test_helper(us =     1, 8,      "1", "       1",   "00000001",
                                           "1", "       1",   "00000001",
                                         "0x1", "     0x1", "0x00000001");

    numstr_test_helper(us =   255, 8,    "255", "     255",   "00000255",
                                          "ff", "      ff",   "000000ff",
                                        "0xff", "    0xff", "0x000000ff");

    numstr_test_helper(us =   256, 8,    "256", "     256",   "00000256",
                                         "100", "     100",   "00000100",
                                       "0x100", "   0x100", "0x00000100");

    numstr_test_helper(us = 12000, 8,  "12000", "   12000",   "00012000",
                                        "2ee0", "    2ee0",   "00002ee0",
                                      "0x2ee0", "  0x2ee0", "0x00002ee0");

    numstr_test_helper(us = 32767, 8,  "32767", "   32767",   "00032767",
                                        "7fff", "    7fff",   "00007fff",
                                      "0x7fff", "  0x7fff", "0x00007fff");

    numstr_test_helper(us = 32768, 8,  "32768", "   32768",   "00032768",
                                        "8000", "    8000",   "00008000",
                                      "0x8000", "  0x8000", "0x00008000");

    numstr_test_helper(us = 65535, 8,  "65535", "   65535",   "00065535",
                                        "ffff", "    ffff",   "0000ffff",
                                      "0xffff", "  0xffff", "0x0000ffff");
}


BOOST_AUTO_TEST_CASE(test_int)
{
    int i;
    unsigned int ui;

    numstr_test_helper(i =           0, 12,           "0", "           0",   "000000000000",
                                                      "0", "           0",   "000000000000",
                                                    "0x0", "         0x0", "0x000000000000");

    numstr_test_helper(i =           1, 12,           "1", "           1",   "000000000001",
                                                      "1", "           1",   "000000000001",
                                                    "0x1", "         0x1", "0x000000000001");

    numstr_test_helper(i =  2147483647, 12,  "2147483647", "  2147483647",   "002147483647",
                                               "7fffffff", "    7fffffff",   "00007fffffff",
                                             "0x7fffffff", "  0x7fffffff", "0x00007fffffff");

    numstr_test_helper(i = -2147483648, 12, "-2147483648", " -2147483648",   "-02147483648",
                                               "80000000", "    80000000",   "000080000000",
                                             "0x80000000", "  0x80000000", "0x000080000000");

    numstr_test_helper(i =          -1, 12,          "-1", "          -1",   "-00000000001",
                                               "ffffffff", "    ffffffff",   "0000ffffffff",
                                             "0xffffffff", "  0xffffffff", "0x0000ffffffff");

    numstr_test_helper(ui =           0, 12,           "0", "           0",   "000000000000",
                                                      "0", "           0",   "000000000000",
                                                    "0x0", "         0x0", "0x000000000000");

    numstr_test_helper(ui =           1, 12,           "1", "           1",   "000000000001",
                                                      "1", "           1",   "000000000001",
                                                    "0x1", "         0x1", "0x000000000001");

    numstr_test_helper(ui =  2147483647, 12,  "2147483647", "  2147483647",   "002147483647",
                                                "7fffffff", "    7fffffff",   "00007fffffff",
                                              "0x7fffffff", "  0x7fffffff", "0x00007fffffff");

    numstr_test_helper(ui =  2147483648, 12,  "2147483648", "  2147483648",   "002147483648",
                                                "80000000", "    80000000",   "000080000000",
                                              "0x80000000", "  0x80000000", "0x000080000000");

    numstr_test_helper(ui = 4294967295, 12, "4294967295", "  4294967295",   "004294967295",
                                              "ffffffff", "    ffffffff",   "0000ffffffff",
                                            "0xffffffff", "  0xffffffff", "0x0000ffffffff");

}


BOOST_AUTO_TEST_CASE(test_long)
{
    long l;
    unsigned long ul;

    numstr_test_helper(l =           0, 12,           "0", "           0",   "000000000000",
                                                      "0", "           0",   "000000000000",
                                                    "0x0", "         0x0", "0x000000000000");

    numstr_test_helper(l =           1, 12,           "1", "           1",   "000000000001",
                                                      "1", "           1",   "000000000001",
                                                    "0x1", "         0x1", "0x000000000001");

    numstr_test_helper(l =  2147483647, 12,  "2147483647", "  2147483647",   "002147483647",
                                               "7fffffff", "    7fffffff",   "00007fffffff",
                                             "0x7fffffff", "  0x7fffffff", "0x00007fffffff");

    numstr_test_helper(l = -2147483648, 12, "-2147483648", " -2147483648",   "-02147483648",
                                               "80000000", "    80000000",   "000080000000",
                                             "0x80000000", "  0x80000000", "0x000080000000");

    numstr_test_helper(l =          -1, 12,          "-1", "          -1",   "-00000000001",
                                               "ffffffff", "    ffffffff",   "0000ffffffff",
                                             "0xffffffff", "  0xffffffff", "0x0000ffffffff");

    numstr_test_helper(ul =           0, 12,           "0", "           0",   "000000000000",
                                                      "0", "           0",   "000000000000",
                                                    "0x0", "         0x0", "0x000000000000");

    numstr_test_helper(ul =           1, 12,           "1", "           1",   "000000000001",
                                                      "1", "           1",   "000000000001",
                                                    "0x1", "         0x1", "0x000000000001");

    numstr_test_helper(ul =  2147483647, 12,  "2147483647", "  2147483647",   "002147483647",
                                                "7fffffff", "    7fffffff",   "00007fffffff",
                                              "0x7fffffff", "  0x7fffffff", "0x00007fffffff");

    numstr_test_helper(ul =  2147483648, 12,  "2147483648", "  2147483648",   "002147483648",
                                                "80000000", "    80000000",   "000080000000",
                                              "0x80000000", "  0x80000000", "0x000080000000");

    numstr_test_helper(ul = 4294967295, 12, "4294967295", "  4294967295",   "004294967295",
                                              "ffffffff", "    ffffffff",   "0000ffffffff",
                                            "0xffffffff", "  0xffffffff", "0x0000ffffffff");

}


BOOST_AUTO_TEST_CASE(test_float)
{
    float f;
    double d;

    //  Note - these are not exhaustive, especially considering that the digits of precision are not
    //    properly handled by CtiNumStr.

    numstr_test_helper(f = 0.0, 0, 12,                     "0",    "           0",    "000000000000",
                                                      "0e+000",    "      0e+000",    "0.000000e+00");

    numstr_test_helper(f = 0.0, 3, 12,                 "0.000",    "       0.000",    "00000000.000",
                                                   "0.000e+00",    "   0.000e+00",    "0.000000e+00");

    numstr_test_helper(f = 0.0, 6, 12,              "0.000000",    "    0.000000",    "00000.000000",
                                                "0.000000e+00",    "0.000000e+00",    "0.000000e+00");

    numstr_test_helper(f = 0.1234, 0, 12,                  "0",    "           0",    "000000000000",
                                                      "1e-001",    "      1e-001",    "1.234000e-01");

    numstr_test_helper(f = 0.1234, 3, 12,              "0.123",    "       0.123",    "00000000.123",
                                                   "1.234e-01",    "   1.234e-01",    "1.234000e-01");

    numstr_test_helper(f = 0.1234, 6, 12,           "0.123400",    "    0.123400",    "00000.123400",
                                                "1.234000e-01",    "1.234000e-01",    "1.234000e-01");

    numstr_test_helper(f = 1.0, 0, 12,                     "1",    "           1",    "000000000001",
                                                      "1e+000",    "      1e+000",    "1.000000e+00");

    numstr_test_helper(f = 1.0, 3, 12,                 "1.000",    "       1.000",    "00000001.000",
                                                   "1.000e+00",    "   1.000e+00",    "1.000000e+00");

    numstr_test_helper(f = 1.0, 6, 12,              "1.000000",    "    1.000000",    "00001.000000",
                                                "1.000000e+00",    "1.000000e+00",    "1.000000e+00");

//  these doesn't make sense for float, since it only has 7 significant digits - the exp test will fail
/*
    numstr_test_helper(f = 0.0, 6, 15,              "0.000000", "       0.000000", "00000000.000000",
                                               "0.000000e+000", "  0.000000e+000", "0.00000000e+000");

    numstr_test_helper(f = 0.1234, 6, 15,           "0.123400", "       0.123400", "00000000.123400",
                                               "1.234000e-001", "  1.234000e-001", "1.23400000e-001");

    numstr_test_helper(f = 1.0, 6, 15,              "1.000000", "       1.000000", "00000001.000000",
                                               "1.000000e+000", "  1.000000e+000", "1.00000000e+000");
*/

    numstr_test_helper(d = 0.0, 0, 12,                     "0",    "           0",    "000000000000",
                                                      "0e+000",    "      0e+000",    "0.000000e+00");

    numstr_test_helper(d = 0.0, 3, 12,                 "0.000",    "       0.000",    "00000000.000",
                                                   "0.000e+00",    "   0.000e+00",    "0.000000e+00");

    numstr_test_helper(d = 0.0, 6, 12,              "0.000000",    "    0.000000",    "00000.000000",
                                                "0.000000e+00",    "0.000000e+00",    "0.000000e+00");

    numstr_test_helper(d = 0.0, 6, 15,              "0.000000", "       0.000000", "00000000.000000",
                                                "0.000000e+00", "   0.000000e+00", "0.000000000e+00");

    numstr_test_helper(d = 0.2346, 0, 12,                  "0",    "           0",    "000000000000",
                                                      "2e-001",    "      2e-001",    "2.346000e-01");

    numstr_test_helper(d = 0.2346, 3, 12,              "0.235",    "       0.235",    "00000000.235",
                                                   "2.346e-01",    "   2.346e-01",    "2.346000e-01");

    numstr_test_helper(d = 0.2346, 6, 12,           "0.234600",    "    0.234600",    "00000.234600",
                                                "2.346000e-01",    "2.346000e-01",    "2.346000e-01");

    numstr_test_helper(d = 0.2346, 6, 15,           "0.234600", "       0.234600", "00000000.234600",
                                                "2.346000e-01", "   2.346000e-01", "2.346000000e-01");

    numstr_test_helper(d = 2.0, 0, 12,                     "2",    "           2",    "000000000002",
                                                      "2e+000",    "      2e+000",    "2.000000e+00");

    numstr_test_helper(d = 2.0, 3, 12,                 "2.000",    "       2.000",    "00000002.000",
                                                   "2.000e+00",    "   2.000e+00",    "2.000000e+00");

    numstr_test_helper(d = 2.0, 6, 12,              "2.000000",    "    2.000000",    "00002.000000",
                                                "2.000000e+00",    "2.000000e+00",    "2.000000e+00");

    numstr_test_helper(d = 2.0, 6, 15,              "2.000000", "       2.000000", "00000002.000000",
                                                "2.000000e+00", "   2.000000e+00", "2.000000000e+00");

    numstr_test_helper(d = 76389.2384, 0, 12,          "76389",    "       76389",    "000000076389",
                                                      "8e+004",    "      8e+004",    "7.638924e+04");

    numstr_test_helper(d = 76389.2384, 3, 12,      "76389.238",    "   76389.238",    "00076389.238",
                                                   "7.639e+04",    "   7.639e+04",    "7.638924e+04");

    numstr_test_helper(d = 76389.2384, 6, 12,   "76389.238400",    "76389.238400",    "76389.238400",
                                                "7.638924e+04",    "7.638924e+04",    "7.638924e+04");

}


BOOST_AUTO_TEST_CASE(test_operators)
{
    string s;
    int i;

    CtiNumStr numstr(i = 2736);

    s = "Test string " + numstr.toString() + " success";

    BOOST_CHECK_EQUAL(s, "Test string 2736 success");

    s = "Test string " + numstr + " success";

    BOOST_CHECK_EQUAL(s, "Test string 2736 success");

    s = numstr + " success";

    BOOST_CHECK_EQUAL(s, "2736 success");

    s = "Test string " + numstr;

    BOOST_CHECK_EQUAL(s, "Test string 2736");
}

BOOST_AUTO_TEST_CASE(test_limits)
{
    double d;

    //  CtiNumStr allows 15 digits of max precision
    CtiNumStr numstr(d = 3.141592653589793238, 18);

    BOOST_CHECK_EQUAL(numstr.toString(), "3.141592653589793");
}

BOOST_AUTO_TEST_SUITE_END()
