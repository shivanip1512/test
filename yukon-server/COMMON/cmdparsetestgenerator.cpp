#include "cmdparse.h"

#include "test_cmdparse_input.h"

#include <iostream>
#include <fstream>

int main(int argc, char **argv)
{
    std::ofstream outputfile("..\\common\\include\\test_cmdparse_output.h");

    outputfile << "#pragma once\n\n";
    outputfile << "#include <string>\n\n";
    outputfile << "std::string parse_asString[] = {\n";

    unsigned counter = 0;

    for each( const std::string &inputString in inputStrings )
    {
        std::cout << "Processing \"" << inputString << "\"" << std::endl;

        if( counter && counter % 10 == 0 )
        {
            outputfile << "//  " << counter << "\n";
        }

        outputfile << "\"";
        outputfile << CtiCommandParser(inputString).asString();
        outputfile << "\",\n";

        counter++;
    }

    outputfile << "};";

    outputfile.close();
}
