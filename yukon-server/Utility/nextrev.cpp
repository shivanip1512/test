
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


void usage()
{
    cout << endl;
    cout << "Arg 1: cvs log file -l filename" << endl;
    cout << "  Output from (cvs rlog projfile > projfile.log)?" << endl << endl;
    cout << "                    -B report BRANCHES only" << endl;
    cout << "                    -T report TAGS only" << endl;
    cout << "increment type or revision base number  " << endl;
    cout << "                    -M[:MajorBaseNum]  (Major Rev)" << endl;
    cout << "                    -m[:MinorBaseNum]  (Minor Rev)" << endl;
    cout << "                    -b                 (Build Rev)" << endl << endl;
    cout << "                    -t                 (Base all decisions on TG labels (notBR))" << endl << endl;
    cout << "output type (program exit code)" << endl;
    cout << "                    -R (Major Rev)" << endl;
    cout << "                    -r (Minor Rev)" << endl;
    cout << "                    -v (Build Rev)" << endl << endl;
    cout << endl;
    cout << "                    -V Verbose Mode" << endl << endl;
    cout << endl << "e.g.  cvs rlog -r yukon-server\\Makefile > c:\\temp\\tags.log & nextrev -l c:\\temp\\tags.log  -M:2 -m:34 -b -V" << endl;
    cout << "  - Would return the next buildrevision on the 2.34 build branch." << endl << endl;
}

int main(int argc, char **argv)
{
    INT errorCode = -1;
    INT i;
    INT flag = 0;
    INT majorRevisionVal = -1;
    INT minorRevisionVal = -1;
    INT buildRevisionVal = -1;

    bool versionsFromTags = false;
    bool verbose = false;
    bool incrementMajor = false;
    bool incrementMinor = false;
    bool incrementBuild = false;
    bool reportBranchesOnly = false;
    bool reportTagsOnly = false;
    bool returnMajor = false;
    bool returnMinor = false;
    bool returnBuild = false;

    FILE *fp;

    char temp[128];

    RWCString tstr;
    RWCString filename;
    INT gMajorRevision = -1;
    INT gMinorRevision = -1;


    if(GetEnvironmentVariable("YUKON_MAJOR_REVISION", temp, 128) > 0)
    {
       gMajorRevision   = atoi(temp);
       majorRevisionVal = gMajorRevision;
    }

    if(GetEnvironmentVariable("YUKON_MINOR_REVISION", temp, 128) > 0)
    {
       gMinorRevision   = atoi(temp);
       minorRevisionVal = gMinorRevision;
    }



    for(i = 1; i < argc; i++)
    {
        if(argv[i][0] == '-')
        {
            switch(argv[i][1])
            {
            case 'l':
                {
                    filename = RWCString(argv[i+1]);
                    i++; // Hop over two positions here!
                    break;
                }
            case 'B':
                {
                    reportBranchesOnly = true;
                    break;
                }
            case 'T':
                {
                    reportTagsOnly = true;
                    break;
                }
            case 'M':
                {
                    if(argv[i][2] != ':')
                    {
                        incrementMajor = true;
                    }
                    else
                    {
                        gMajorRevision   = atoi((char*)(&argv[i][3]));
                        majorRevisionVal = gMajorRevision;
                    }
                    break;
                }
            case 'm':
                {
                    if(argv[i][2] != ':')
                    {
                        incrementMinor = true;
                    }
                    else
                    {
                        gMinorRevision   = atoi((char*)(&argv[i][3]));
                        minorRevisionVal = gMinorRevision;
                    }
                    break;
                }
            case 'b':
                {
                    incrementBuild = true;
                    break;
                }
            case 't':
                {
                    versionsFromTags = true;
                    break;
                }
            case 'R':
                {
                    returnMajor = true;
                    returnMinor = false;
                    returnBuild = false;
                    break;
                }
            case 'r':
                {
                    returnMinor = true;
                    returnMajor = false;
                    returnBuild = false;
                    break;
                }
            case 'v':
                {
                    returnBuild = true;
                    returnMajor = false;
                    returnMinor = false;
                    break;
                }
            case 'V':
                {
                    verbose = true;
                    break;
                }
            default:
                {
                    cout << "Bad argument" << endl;
                    usage();
                    break;
                }
            }
        }
    }

    if(!filename.isNull())
    {
        // cout << endl << "Opening " << filename << " for processing" << endl << endl;
        fp = fopen(filename.data(), "rt");

        if(fp != NULL)
        {
            RWCRExpr keyre("BR");

            if(versionsFromTags)
            {
                keyre = RWCRExpr("TG");
            }

            if(reportTagsOnly || reportBranchesOnly) cout << endl;
            while( fgets(temp, 127, fp))
            {
                temp[ strlen(temp) - 1 ] = '\0';

                RWCString str(temp);

                str = str.strip( RWCString::both, ' ' );
                str = str.strip( RWCString::both, '\t' );

                if(reportTagsOnly && !str.match("TG").isNull())
                {
                    RWCTokenizer next(str);
                    RWCString token = next(":");
                    cout << "    " << token << endl;
                }
                else if(!str.match(keyre).isNull())     // Might be looking for BR's or TG's based upon -t
                {
                    RWCTokenizer next(str);
                    RWCString token = next(":");

                    if(reportBranchesOnly)
                    {
                        cout << "    " << str << endl;
                    }

                    //cout <<  " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << token << endl;

                    if(!(str = token.match("[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]_[0-9]_[0-9]+(_[0-9]+)?_[0-9][0-9][0-9][0-9]?")).isNull())
                    {
                        str = str.strip(RWCString::both, '_');

                        RWCTokenizer next(str);
                        RWCString datestr = next("_");
                        RWCString majorRevision = next("_");
                        RWCString minorRevision = next("_");
                        RWCString buildRevision = next("_");
                        RWCString timestr = next(" \0");

                        majorRevision = majorRevision.strip(RWCString::both, '_');
                        minorRevision = minorRevision.strip(RWCString::both, '_');
                        buildRevision = buildRevision.strip(RWCString::both, '_');

                        //cout <<  " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << majorRevision << "." << minorRevision << "." << buildRevision << endl;
                        if(gMajorRevision > 0)
                        {
                            if(gMinorRevision >= 0)
                            {
                                // Looking for the largest build with these major and minor revisions.
                                if( majorRevisionVal == atoi(majorRevision.data()) &&
                                    minorRevisionVal == atoi(minorRevision.data()) &&
                                    buildRevisionVal < atoi(buildRevision.data()) )
                                {
                                    buildRevisionVal = atoi(buildRevision.data());
                                    //cout <<  " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << majorRevision << "." << minorRevision << "." << buildRevision << endl;
                                }
                            }
                            else
                            {
                                // Looking for the largest build with this major revision.
                                if( majorRevisionVal == atoi(majorRevision.data()) &&
                                    minorRevisionVal < atoi(minorRevision.data()) )
                                {
                                    minorRevisionVal = atoi(minorRevision.data());
                                    buildRevisionVal = atoi(buildRevision.data());
                                    //cout <<  " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << majorRevision << "." << minorRevision << "." << buildRevision << endl;
                                }
                                else if( majorRevisionVal == atoi(majorRevision.data()) &&
                                         minorRevisionVal == atoi(minorRevision.data()) &&
                                         buildRevisionVal < atoi(buildRevision.data()) )
                                {
                                    buildRevisionVal = atoi(buildRevision.data());
                                    //cout <<  " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << majorRevision << "." << minorRevision << "." << buildRevision << endl;
                                }
                            }
                        }
                        else
                        {
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
                    else if(!(str = token.match("[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]_[0-9][0-9][0-9][0-9]?_[0-9]_[0-9]+(_[0-9]+)?")).isNull())
                    {
                        str = str.strip(RWCString::both, '_');

                        RWCTokenizer next(str);
                        RWCString datestr = next("_");
                        RWCString timestr = next("_");
                        RWCString majorRevision = next("_");
                        RWCString minorRevision = next("_ \0");
                        RWCString buildRevision = next(" \0");

                        majorRevision = majorRevision.strip(RWCString::both, '_');
                        minorRevision = minorRevision.strip(RWCString::both, '_');
                        buildRevision = buildRevision.strip(RWCString::both, '_');

                        //cout <<  " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << majorRevision << "." << minorRevision << "." << buildRevision << endl;
                        if(gMajorRevision > 0)
                        {
                            if(gMinorRevision >= 0)
                            {
                                // Looking for the largest build with these major and minor revisions.
                                if( majorRevisionVal == atoi(majorRevision.data()) &&
                                    minorRevisionVal == atoi(minorRevision.data()) &&
                                    buildRevisionVal < atoi(buildRevision.data()) )
                                {
                                    buildRevisionVal = atoi(buildRevision.data());
                                }
                            }
                            else
                            {
                                // Looking for the largest build with this major revision.
                                if( majorRevisionVal == atoi(majorRevision.data()) &&
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
                        else
                        {
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
            }

            // cout << endl << " CURRENT REVISION " << majorRevisionVal << "." << minorRevisionVal << "." << buildRevisionVal << endl;

            fclose(fp);
        }
        else
        {
            cout << " Couldn't find the file " << filename << endl;
        }
    }
    else
    {
        usage();
    }

    if(incrementBuild)
    {
        errorCode = ++buildRevisionVal;
    }

    if(incrementMinor)
    {
        errorCode = ++minorRevisionVal;
        buildRevisionVal = 0;
    }

    if(incrementMajor)
    {
        errorCode = ++majorRevisionVal;
        minorRevisionVal = 0;
        buildRevisionVal = 0;
    }

    if(returnBuild)
    {
        errorCode = buildRevisionVal;
    }
    else if(returnMinor)
    {
        errorCode = minorRevisionVal;
    }

    else if(returnMajor)
    {
        errorCode = majorRevisionVal;
    }

    if(verbose)
    {
        cout << endl;
        cout << "MAJORREVISION:   " << majorRevisionVal << endl;
        cout << "MINORREVISION:   " << minorRevisionVal << endl;
        cout << "BUILDLEVEL:      " << buildRevisionVal << endl;
    }

    return errorCode;
}

