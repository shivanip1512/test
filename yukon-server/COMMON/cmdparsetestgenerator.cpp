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
#include "test_cmdparse_input.h"

using namespace std;  // get the STL into our namespace for use.  Do NOT use iostream.h anymore

void main(int argc, char **argv)
{
    char* file = "..\\common\\include\\test_cmdparse_output.h";
    try
    {   
        ofstream outputfile(file);
        outputfile << "#ifndef _TESTCMDPARSE_OUTPUT_" << endl << "#define _TESTCMDPARSE_OUTPUT_" << endl
             << "#include \"test_cmdparse_input.h\"" << endl << "std::string  outputString[TEST_SIZE]= {" << endl;
        for(int i=0; i<TEST_SIZE; i++){
            string cmd = inputString[i];
            cout << cmd << endl;
            CtiCommandParser  parse(cmd);
            ///parse.parse();
            string outp = parse.asString();
            outputfile << "\"" << outp << "\"";
            if(i!=TEST_SIZE-1){
                outputfile << ",";
            }
            outputfile << endl;
        }
        outputfile << "};" << endl << "#endif" << endl;
        outputfile.close();
    }
    catch(RWxmsg &msg)
    {
        cout << "Exception: ";
        cout << msg.why() << endl;
    }
    exit(0);
}
