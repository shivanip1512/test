/*
*
* File:   cmdparsetestgenerator
*
* Date:   7/22/2005
*
* to generate the test_cmdparse_output.h based on the current cmdparse.cpp and test_cmdparse_input.h
*
* should be used with test_cmdparse
*
*/
#include "yukon.h"

#include <crtdbg.h>
#include <windows.h>
#include <iostream>
#include <fstream>

#include "cmdparse.h"
#include "numstr.h"

#include "test_cmdparse_input.h"

using namespace std;

int main(int argc, char **argv)
{
    char *file = "..\\common\\include\\test_cmdparse_output.h";

    try
    {
        ofstream outputfile(file);

        string parse_asString;

        for( int i = 0; i < TEST_SIZE; i++ )
        {
            CtiCommandParser parse(inputString[i]);

            cout << "Processing \"" << inputString[i] << "\"" << endl;

            parse_asString += "\"";
            parse_asString += parse.asString();
            parse_asString += "\"";

            if( (i + 1) < TEST_SIZE )
            {
                parse_asString += ",\n";
            }
        }

        outputfile << "#ifndef _TESTCMDPARSE_OUTPUT_" << endl;
        outputfile << "#define _TESTCMDPARSE_OUTPUT_" << endl;
        outputfile << endl;
        outputfile << "#include \"test_cmdparse_input.h\"" << endl;
        outputfile << endl;

        outputfile << "std::string parse_asString[TEST_SIZE] = {" << endl;
        outputfile << parse_asString << endl;
        outputfile << "};" << endl;
        outputfile << endl;
        outputfile << "#endif" << endl;

        outputfile.close();
    }
    catch(RWxmsg &msg)
    {
        cout << "Exception: ";
        cout << msg.why() << endl;
    }

    return 0;
}
