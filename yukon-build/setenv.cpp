#include <iostream>
#include <fstream>
#include <string>

using namespace std;

inline string trim_right ( std::string & source , std::string t = " ")
{
    std::string str = source;
    return source = str.erase ( str.find_last_not_of ( t ) + 1 ) ;
}

inline string trim_left ( std::string & source ,std::string t = " ")
{
    std::string str = source;
    return source = str.erase ( 0 , source.find_first_not_of ( t ) ) ;
}

inline string trim ( std::string & source , std::string t = " ")
{
    std::string str = source;
    return source = trim_left ( trim_right ( str , t ) , t ) ;
}

int main( int argc, char **argv )
{
    int retval = 0;

    //  program path/to/build_version.properties
    //  argv[n+1] == property name
    //  argv[n+2] == environment variable name

    ifstream property_file;

    if( argc < 3 )
    {
        cerr << "Usage:  setenv path\\to\\build_version.properties property_name environment_name ..." << endl;

        retval = 1;
    }
    else
    {
        property_file.open(argv[1]);

        if( !property_file.good() )
        {
            cerr << "Could not open \"" << argv[1] << "\"" << endl;

            retval = 2;
        }
        else
        {
            string line, var, value;

            while( getline(property_file, line) )
            {
                string::size_type pos;

                pos = line.find_first_of(":=");

//                cerr << "line: " << line << endl;
//                cerr << "pos: " << pos << endl;

                if( pos && pos != string::npos )
                {
                    var   = trim(line.substr(0, pos - 1));

                    value = trim(line.substr(pos + 1));

//                    cerr << "var: " << var << endl;
//                    cerr << "value: " << value << endl;

                    if( value.empty() || var.empty() )
                    {
//                        cerr << "value.empty() || var.empty()" << endl;
                    }
                    else
                    {
                        for( int i = 2; i < (argc - 1); i += 2 )
                        {
                            if( !var.compare(argv[i]) )
                            {
                                cout << "set " << argv[i+1] << "=" << value << endl;
                            }
                        }
                    }
                }
            }
        }
    }

    return retval;
}