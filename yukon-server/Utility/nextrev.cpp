
#include <windows.h>
#include <iostream>
#include <vector>
using namespace std;

#include <stdio.h>
#include <string.h>

#include <io.h>
#include <sys/stat.h>

#include <rw/ctoken.h>
#include <rw\re.h>
#include <rw/rwfile.h>
#include <rw/cstring.h>


int main(int argc, char **argv)
{
    INT i;
    INT flag = 0;

    INT majorRevisionVal = 0;
    INT minorRevisionVal = 0;
    INT buildRevisionVal = 0;



    if(argc < 2)
    {
        cout << "What log file file please?" << endl;
        return -1;
    }

    FILE *fp;

    char temp[128];


    RWCString tstr;
    RWCString filename(argv[1]);

    {
        cout << endl << "Opening " << filename << " for processing" << endl << endl;
        fp = fopen(filename.data(), "rt");

        if(fp != NULL)
        {
            while( fgets(temp, 127, fp))
            {
                temp[ strlen(temp) - 1 ] = '\0';

                RWCString str(temp);

                str = str.strip( RWCString::both, ' ' );
                str = str.strip( RWCString::both, '\t' );

                if(!str.match("BR").isNull())
                {
                    RWCTokenizer next(str);
                    RWCString token = next(":");

                    if(!(str = token.match("_[0-9]_[0-9]+(_[0-9]+)?")).isNull())
                    {
                        str = str.strip(RWCString::both, '_');

                        RWCTokenizer next(str);
                        RWCString majorRevision = next("_");
                        RWCString minorRevision = next("_ \0");
                        RWCString buildRevision = next(" \0");

                        majorRevision = majorRevision.strip(RWCString::both, '_');
                        minorRevision = minorRevision.strip(RWCString::both, '_');
                        buildRevision = buildRevision.strip(RWCString::both, '_');

                        if( majorRevisionVal < atoi(majorRevision.data()) )
                        {
                            majorRevisionVal = atoi(majorRevision.data());
                            minorRevisionVal = atoi(minorRevision.data());
                            buildRevisionVal = atoi(buildRevision.data());
                        }
                        else if( majorRevisionVal == atoi(majorRevision.data()) &&
                                 minorRevisionVal < atoi(minorRevision.data()) )
                        {
                            minorRevisionVal = atoi(minorRevision.data());
                            buildRevisionVal = atoi(buildRevision.data());
                        }
                        else if( majorRevisionVal == atoi(majorRevision.data()) &&
                                 minorRevisionVal == atoi(minorRevision.data()) &&
                                 buildRevisionVal < atoi(buildRevision.data()) )
                        {
                            buildRevisionVal = atoi(buildRevision.data());
                        }
                    }
                }
            }

            cout << endl << " The largest existing revision is " << majorRevisionVal << "." << minorRevisionVal << "." << buildRevisionVal << endl;

            fclose(fp);
        }
        else
        {
            cout << " Couldn't find the file " << filename << endl;
        }
    }

    return(0);
}

