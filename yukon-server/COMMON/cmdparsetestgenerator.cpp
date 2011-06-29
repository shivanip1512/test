#include "yukon.h"

#include "cmdparse.h"
#include "numstr.h"

#include "test_cmdparse_input.h"

#include <iostream>
#include <fstream>

using namespace std;

int main(int argc, char **argv)
{
    char *file = "..\\common\\include\\test_cmdparse_output.h";

    try
    {
        ofstream outputfile(file);

        string parse_asString;

        const size_t test_size = sizeof(inputString) / sizeof(inputString[0]);

        for( int i = 0; i < test_size; i++ )
        {
            CtiCommandParser parse(inputString[i]);

            cout << "Processing \"" << inputString[i] << "\"" << Cti::endl;

            parse_asString += "\"";
            parse_asString += parse.asString();
            parse_asString += "\"";

            if( (i + 1) < test_size )
            {
                parse_asString += ",\n";
            }
        }

        outputfile << "#pragma once" << Cti::endl;
        outputfile << Cti::endl;
        outputfile << "#include \"test_cmdparse_input.h\"" << Cti::endl;
        outputfile << Cti::endl;

        outputfile << "std::string parse_asString[] = {" << Cti::endl;
        outputfile << parse_asString << Cti::endl;
        outputfile << "};" << Cti::endl;

        outputfile.close();
    }
    catch(RWxmsg &msg)
    {
        cout << "Exception: ";
        cout << msg.why() << Cti::endl;
    }

    return 0;
}
