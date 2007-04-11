#include "yukon.h"

#include <windows.h>
#include <iostream>
#include <vector>
#include <boost/regex.hpp>
#include <boost/tokenizer.hpp>
using namespace std;

#include <stdio.h>
#include <string.h>

#include <io.h>
#include <sys/stat.h>

#include <rw/ctoken.h>
#include <rw\re.h>
#include <rw/rwfile.h>
#include "rwutil.h"


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

    string tstr;
    string filename;
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
                    filename = string(argv[i+1]);
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

    if(!filename.empty())
    {
        // cout << endl << "Opening " << filename << " for processing" << endl << endl;
        fp = ::fopen(filename.c_str(), "rt");

        if(fp != NULL)
        {
            string keyre("BR");

            if(versionsFromTags)
            {
                keyre = ("TG");
            }

            if(reportTagsOnly || reportBranchesOnly) cout << endl;
            while( ::fgets(temp, 127, fp))
            {
                temp[ ::strlen(temp) - 1 ] = '\0';

                string str(temp);

                str = trim(str);
                str = trim(str, "\t" );

                if( reportTagsOnly && !str.find("TG")!=string::npos )
                {
                    
                    boost::char_separator<char> sep(":");
                    Boost_char_tokenizer next(str, sep);
                    Boost_char_tokenizer::iterator tok_iter = next.begin(); 
                    if( tok_iter != next.end() )
                    {
                        string token = *tok_iter;
                    }
                    cout << "    " << token << endl;
                }
                else if(!str.find(keyre)!=string::npos)     // Might be looking for BR's or TG's based upon -t
                {
                    boost::char_separator<char> sep(":");
                    Boost_char_tokenizer next(str, sep);
                    Boost_char_tokenizer::iterator tok_iter = next.begin(); 
                    if( tok_iter != next.end() )
                    {
                        string token = *tok_iter;
                    }

                    if(reportBranchesOnly)
                    {
                        cout << "    " << str << endl;
                    }

                    //cout <<  " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << token << endl;
                    boost::regex e1("[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]_[0-9]_[0-9]+(_[0-9]+)?_[0-9][0-9][0-9][0-9]?");
                    boost::match_results<std::string::const_iterator> what;
                    if(boost::regex_search(token, what, e1, boost::match_default))
                    {
                        str = string(what[0]);
                        str = trim(str, "_");

                        boost::char_separator<char> sep("_");
                        Boost_char_tokenizer next(str, sep);
                        Boost_char_tokenizer::iterator tok_iter = next.begin(); 

                        if( tok_iter != next.end() )
                        {
                            string datestr = *tok_iter++;
                        }
                        if( tok_iter != next.end() )
                        {
                            string majorRevision = *tok_iter++;
                        }
                        if( tok_iter != next.end() )
                        {
                            string minorRevision = *tok_iter++;
                        }
                        if( tok_iter != next.end() )
                        {
                            string buildRevision = *tok_iter;
                        }
                        
                        boost::char_separator<char> sep2(" \0");
                        typedef boost::token_iterator_generator<boost::char_separator<char> >::type Iter;
                        Iter beg = boost::make_token_iterator<string>(tok_iter.base(), tok_iter.end(),sep2);
                        
                        string timestr = *beg;

                        majorRevision = trim(majorRevision, "_");
                        minorRevision = trim(minorRevision, "_");
                        buildRevision = trim(buildRevision, "_");

                        //cout <<  " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << majorRevision << "." << minorRevision << "." << buildRevision << endl;
                        if(gMajorRevision > 0)
                        {
                            if(gMinorRevision >= 0)
                            {
                                // Looking for the largest build with these major and minor revisions.
                                if( majorRevisionVal == atoi(majorRevision.c_str()) &&
                                    minorRevisionVal == atoi(minorRevision.c_str()) &&
                                    buildRevisionVal < atoi(buildRevision.c_str()) )
                                {
                                    buildRevisionVal = atoi(buildRevision.c_str());
                                    //cout <<  " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << majorRevision << "." << minorRevision << "." << buildRevision << endl;
                                }
                            }
                            else
                            {
                                // Looking for the largest build with this major revision.
                                if( majorRevisionVal == atoi(majorRevision.c_str()) &&
                                    minorRevisionVal < atoi(minorRevision.c_str()) )
                                {
                                    minorRevisionVal = atoi(minorRevision.c_str());
                                    buildRevisionVal = atoi(buildRevision.c_str());
                                    //cout <<  " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << majorRevision << "." << minorRevision << "." << buildRevision << endl;
                                }
                                else if( majorRevisionVal == atoi(majorRevision.c_str()) &&
                                         minorRevisionVal == atoi(minorRevision.c_str()) &&
                                         buildRevisionVal < atoi(buildRevision.c_str()) )
                                {
                                    buildRevisionVal = atoi(buildRevision.c_str());
                                    //cout <<  " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << majorRevision << "." << minorRevision << "." << buildRevision << endl;
                                }
                            }
                        }
                        else
                        {
                            if( majorRevisionVal < atoi(majorRevision.c_str()) )
                            {
                                majorRevisionVal = atoi(majorRevision.c_str());
                                minorRevisionVal = atoi(minorRevision.c_str());
                                buildRevisionVal = atoi(buildRevision.c_str());
                            }
                            else if( majorRevisionVal == atoi(majorRevision.c_str()) &&
                                     minorRevisionVal < atoi(minorRevision.c_str()) )
                            {
                                minorRevisionVal = atoi(minorRevision.c_str());
                                buildRevisionVal = atoi(buildRevision.c_str());
                            }
                            else if( majorRevisionVal == atoi(majorRevision.c_str()) &&
                                     minorRevisionVal == atoi(minorRevision.c_str()) &&
                                     buildRevisionVal < atoi(buildRevision.c_str()) )
                            {
                                buildRevisionVal = atoi(buildRevision.c_str());
                            }
                        }
                    }
                    e1.assign("[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]_[0-9][0-9][0-9][0-9]?_[0-9]_[0-9]+(_[0-9]+)?");
                    if(boost::regex_search(token, what, e1, boost::match_default))
                    {
                        str = string(what[0]);
                        str = trim(str, "_");

                        boost::char_separator<char> sep("_");
                        Boost_char_tokenizer next(str, sep);
                        Boost_char_tokenizer::iterator tok_iter = next.begin(); 

                        if( tok_iter != next.end() )
                        {
                            string datestr = *tok_iter++;
                        }
                        if( tok_iter != next.end() )
                        {
                            string timestr = *tok_iter++;
                        }
                        if( tok_iter != next.end() )
                        {
                            string majorRevision = *tok_iter++;
                        }


                        boost::char_separator<char> sep2("_ \0");
                        typedef boost::token_iterator_generator<boost::char_separator<char> >::type Iter;
                        Iter beg = boost::make_token_iterator<string>(tok_iter.base(), tok_iter.end(),sep2);
                        Iter end;
                        if( beg != tok_iter.end() )
                        {
                            string minorRevision = *beg++;
                        }

                        boost::char_separator<char> sep3(" \0");
                        Iter beg1 = boost::make_token_iterator<string>(beg.base(), beg.end(), sep3);
                        if( beg1 != beg.end() )
                        {
                            string buildRevision = *beg1;
                        }

                        
                        majorRevision = trim(majorRevision, "_");
                        minorRevision = trim(minorRevision, "_");
                        buildRevision = trim(buildRevision, "_");


                        //cout <<  " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << majorRevision << "." << minorRevision << "." << buildRevision << endl;
                        if(gMajorRevision > 0)
                        {
                            if(gMinorRevision >= 0)
                            {
                                // Looking for the largest build with these major and minor revisions.
                                if( majorRevisionVal == atoi(majorRevision.c_str()) &&
                                    minorRevisionVal == atoi(minorRevision.c_str()) &&
                                    buildRevisionVal < atoi(buildRevision.c_str()) )
                                {
                                    buildRevisionVal = atoi(buildRevision.c_str());
                                }
                            }
                            else
                            {
                                // Looking for the largest build with this major revision.
                                if( majorRevisionVal == atoi(majorRevision.c_str()) &&
                                    minorRevisionVal < atoi(minorRevision.c_str()) )
                                {
                                    minorRevisionVal = atoi(minorRevision.c_str());
                                    buildRevisionVal = atoi(buildRevision.c_str());
                                }
                                else if( majorRevisionVal == atoi(majorRevision.c_str()) &&
                                         minorRevisionVal == atoi(minorRevision.c_str()) &&
                                         buildRevisionVal < atoi(buildRevision.c_str()) )
                                {
                                    buildRevisionVal = atoi(buildRevision.c_str());
                                }
                            }
                        }
                        else
                        {
                            if( majorRevisionVal < atoi(majorRevision.c_str()) )
                            {
                                majorRevisionVal = atoi(majorRevision.c_str());
                                minorRevisionVal = atoi(minorRevision.c_str());
                                buildRevisionVal = atoi(buildRevision.c_str());
                            }
                            else if( majorRevisionVal == atoi(majorRevision.c_str()) &&
                                     minorRevisionVal < atoi(minorRevision.c_str()) )
                            {
                                minorRevisionVal = atoi(minorRevision.c_str());
                                buildRevisionVal = atoi(buildRevision.c_str());
                            }
                            else if( majorRevisionVal == atoi(majorRevision.c_str()) &&
                                     minorRevisionVal == atoi(minorRevision.c_str()) &&
                                     buildRevisionVal < atoi(buildRevision.c_str()) )
                            {
                                buildRevisionVal = atoi(buildRevision.c_str());
                            }
                        }
                    }
                }
            }

            // cout << endl << " CURRENT REVISION " << majorRevisionVal << "." << minorRevisionVal << "." << buildRevisionVal << endl;

            ::fclose(fp);
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

