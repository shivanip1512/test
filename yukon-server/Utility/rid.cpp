#include "yukon.h"
#include <windows.h>
#include <iostream>
#include <vector>
#include <boost/regex.hpp>
using namespace std;

#include <stdio.h>
#include <string.h>

#include <io.h>
#include <sys/stat.h>

#include <rw\re.h>
#include <rw/rwfile.h>
#include "rwutil.h"
#include "string_util.h"

static string gCompileBase;
static INT gMajorRevision = -1;
static INT gMinorRevision = -1;
static INT gBuildRevision = -1;
static bool gDoCheckouts = false;

class CtiPVCS
{
public:

   string filename;
   string rev;
   string date;

   CtiPVCS() :
      rev("Unknown"),
      date("Unknown")
   {}

   CtiPVCS(const CtiPVCS& aRef) :
      rev("Unknown"),
      date("Unknown")
   {
      *this = aRef;
   }
   virtual ~CtiPVCS() {}

   CtiPVCS& operator=(const CtiPVCS& aRef)
   {
      if(this != &aRef)
      {
         filename = aRef.filename;
         rev      = aRef.rev;
         date     = aRef.date;
      }
      return *this;
   }

};

class CtiDirBuild
{
public:

   string _dir;
   string _project;

   UINT _majorRevision;
   UINT _minorRevision;
   UINT _buildNumber;

   CtiDirBuild() :
      _dir("Unknown"),
      _project("Unknown"),
      _majorRevision(0),
      _minorRevision(0),
      _buildNumber(0)
   {}

   CtiDirBuild(const CtiDirBuild& aRef)  :
      _dir("Unknown"),
      _project("Unknown"),
      _majorRevision(0),
      _minorRevision(0),
      _buildNumber(0)
   {
      *this = aRef;
   }
   virtual ~CtiDirBuild() {}

   CtiDirBuild& operator=(const CtiDirBuild& aRef)
   {
      if(this != &aRef)
      {
         _dir = aRef._dir;
         _project = aRef._project;
         _majorRevision = aRef._majorRevision;
         _minorRevision = aRef._minorRevision;
         _buildNumber = aRef._buildNumber;
      }
      return *this;
   }

};

ostream& operator<<(ostream &os, const CtiDirBuild &db)
{
   os << "Directory " << db._dir << "\n" <<
      "Project   " << db._project << "\n" <<
      "Major     " << db._majorRevision << "\n" <<
      "Minor     " << db._minorRevision << "\n" <<
      "Build     " << db._buildNumber;

   return os;
}

typedef vector< CtiPVCS > CTIFILEVECTOR;
typedef vector< CtiDirBuild > CTIDIRVECTOR;


#define MAJORINCREMENT 0x00000008
#define MINORINCREMENT 0x00000004

void ProcessCID(CtiDirBuild &db, string &cidfile);


void ProcessCID(CtiDirBuild &db, string &cidfile)
{

   bool bWritable = false;

   FILE *fp;

   CtiDirBuild   &dinfo = db;

   char oldnum[10];
   char newnum[10];

   char temp[128];
   char dir[128];
   char file[128];
   char *ptr = NULL;
   vector< string > fileStrings;

   std::transform(cidfile.begin(), cidfile.end(), cidfile.begin(), ::tolower);
   boost::regex e1("([_a-z0-9.]+h)$");
   boost::match_results<std::string::const_iterator> what;
   boost::regex_search(cidfile, what, e1, boost::match_default);
   string tstr = string(what[0]);

   // cout << tstr << endl;

   if(tstr.length() == 0)
   {
      tstr = ".\\";
      cout << tstr << endl;
   }

   if(!GetFullPathName(cidfile.c_str(), 128, dir, &ptr))
   {
      cout << "It failed" << endl;
   }
   else
   {
      ::strcpy(file, ptr);
      *ptr = '\0';
   }

   dinfo._dir = cidfile;
   e1.assign(tstr);
   dinfo._dir = boost::regex_replace(dinfo._dir, e1, "", boost::match_default | boost::format_all | boost::format_first_only);

   fp = ::fopen(cidfile.c_str(), "rt");

   if(fp != NULL)
   {
      while( ::fgets(temp, 127, fp) )
      {
         temp[ ::strlen(temp) - 1 ] = '\0';

         string str(temp);
         string origstr(temp);
         std::transform(str.begin(), str.end(), str.begin(), ::tolower);

         if(!str.find("project")!=string::npos)
         {
            // store it off for the write.
            fileStrings.push_back(origstr);

            boost::regex e1("#define");
            str = boost::regex_replace(str, e1, "", boost::match_default | boost::format_all | boost::format_first_only);
            e1.assign("project");
            str = boost::regex_replace(str, e1, "", boost::match_default | boost::format_all | boost::format_first_only);
            str = trim(str);

            dinfo._project = str;
            // cout << "Project " << dinfo._project << endl;
         }
         else if(!str.find("majorrevision")!=string::npos)
         {
             boost::regex e1("[0-9]+");
             boost::match_results<std::string::const_iterator> what;

            if( boost::regex_search(str, what, e1, boost::match_default) )
            {
                tstr = string(what[0]);
               dinfo._majorRevision = ::atoi(tstr.c_str());
            }

            if(gMajorRevision > 0 && dinfo._majorRevision != gMajorRevision)
            {
               // We need to generate a new buildnumber define!
               ::sprintf(oldnum, "%d", dinfo._majorRevision);

               dinfo._majorRevision = gMajorRevision;
               ::sprintf(newnum, "%d", dinfo._majorRevision);

               e1.assign(oldnum);
               origstr = boost::regex_replace(origstr, e1, newnum, boost::match_default | boost::format_all | boost::format_first_only);

            }
            else
            {
               gMajorRevision = -1;                                                 // This should allow build to be incremented.
            }

            //cout << "MajorRevision " << dinfo._majorRevision << endl;

            // store it off for the write.
            fileStrings.push_back(origstr);

         }
         else if(!str.find("minorrevision")!=string::npos)
         {
             boost::regex e1("[0-9]+");
             boost::match_results<std::string::const_iterator> what;

            if( boost::regex_search(str, what, e1, boost::match_default) )
            {
                tstr = string(what[0]);
               dinfo._minorRevision = atoi(tstr.c_str());
            }

            if(gMinorRevision >= 0 && dinfo._minorRevision != gMinorRevision)
            {
               // We need to generate a new buildnumber define!
               ::sprintf(oldnum, "%d", dinfo._minorRevision);

               dinfo._minorRevision = gMinorRevision;
               ::sprintf(newnum, "%d", dinfo._minorRevision);

               e1.assign(oldnum);
               origstr = boost::regex_replace(origstr, e1, newnum, boost::match_default | boost::format_all | boost::format_first_only);
            }
            else
            {
               gMinorRevision = -1;                                                 // This should allow build to be incremented.
            }

            //cout << "MinorRevision " << dinfo._minorRevision << endl;

            // store it off for the write.
            fileStrings.push_back(origstr);
         } else if(!str.find("buildnumber")!=string::npos)
         {
             boost::regex e1("[0-9]+");
             boost::match_results<std::string::const_iterator> what;
            if( boost::regex_search(str, what, e1, boost::match_default) )
            {
               tstr = string(what[0]);
               dinfo._buildNumber = atoi(tstr.c_str()) - 1;
            }

            if(gBuildRevision >= 0 && dinfo._buildNumber != gBuildRevision)         // If specified and different.
            {
               // We need to generate a new buildnumber define!
               ::sprintf(oldnum, "%d", dinfo._buildNumber + 1);

               dinfo._buildNumber = gBuildRevision;
               ::sprintf(newnum, "%d", dinfo._buildNumber);

               e1.assign(oldnum);
               origstr = boost::regex_replace(origstr, e1, newnum, boost::match_default | boost::format_all | boost::format_first_only);


               cout << __LINE__ << " " << gBuildRevision << endl;
            }
            else if(gMinorRevision >= 0 || gMajorRevision >= 0)   // Need to reset this then.
            {
               // We need to generate a new buildnumber define!
              ::sprintf(oldnum, "%d", dinfo._buildNumber + 1);

               dinfo._buildNumber = 0;
               ::sprintf(newnum, "%d", dinfo._buildNumber);

               cout << __LINE__ << endl;
            }
            else
            {
               // We need to generate a new buildnumber define!
               ::sprintf(oldnum, "%d", dinfo._buildNumber + 1);
               ::sprintf(newnum, "%d", dinfo._buildNumber);

               cout << __LINE__ << endl;
            }

            e1.assign(oldnum);
            origstr = boost::regex_replace(origstr, e1, newnum, boost::match_default | boost::format_all | boost::format_first_only);

            //cout << "BuildNumber " << dinfo._buildNumber << endl;

            // store it off for the write.
            fileStrings.push_back(origstr);
         }
         else
         {
            // just store it off for the write.
            fileStrings.push_back(origstr);
         }

      }

      ::fclose(fp);


      // cout << dinfo << endl;


      if( !access(cidfile.c_str(), 2) )
      {
         bWritable = true;
      }
      else
      {
          cout << "Could not open " << cidfile << " for writing" << endl;
          return;
      }


      fp = ::fopen(cidfile.c_str(), "w");

      if(fp != NULL)
      {
         vector< string >::iterator fit;

         for(fit = fileStrings.begin(); fit != fileStrings.end(); fit++)
         {
            string str = *fit;
            // cout << str << endl;
            ::fprintf(fp, "%s\n", str.c_str());
         }
         ::fclose(fp);

         fileStrings.clear();
      }
      else
      {
         cout << "Could not open " << cidfile << " for writing" << endl;
      }
   }
   else
   {
      cout << " Couldn't open CID file " << cidfile.c_str() << " for writing" << endl;
   }
}


int main(int argc, char **argv)
{
   INT i;
   INT flag = 0;
   CTIFILEVECTOR vect;

   string infoname;

   if(argc < 2)
   {
      cout << "What cid header file please?" << endl;
      return -1;
   }

   if( argc > 2 )
   {
       infoname = string( argv[2] );
   }

   char temp[128];
   string path(".");

   if(GetEnvironmentVariable("YUKON_MAJOR_REVISION", temp, 128) > 0)
   {
      gMajorRevision = atoi(temp);
      gDoCheckouts = true;

      //cout << "Major Revision " << gMajorRevision << endl;
   }

   if(GetEnvironmentVariable("YUKON_MINOR_REVISION", temp, 128) > 0)
   {
      gDoCheckouts = true;
      gMinorRevision = atoi(temp);
      //cout << "Minor Revision " << gMinorRevision << endl;
   }

   if(GetEnvironmentVariable("YUKON_BUILD_REVISION", temp, 128) > 0)
   {
      gDoCheckouts = true;
      gBuildRevision = atoi(temp);
      //cout << "Minor Revision " << gMinorRevision << endl;
   }

   if(GetEnvironmentVariable("COMPILEBASE", temp, 128) > 0)
   {
      gCompileBase = temp;
      path = temp;
      std::transform(gCompileBase.begin(), gCompileBase.end(), gCompileBase.begin(), ::tolower);


      if(gCompileBase[gCompileBase.length()-1] != '\\')
      {
         gCompileBase = gCompileBase + "\\";
      }
      boost::regex e1("\\\\");
      gCompileBase = boost::regex_replace(gCompileBase, e1, "\\\\", boost::match_default | boost::format_all);

   }

   // First find out if it has a CID.H file in it!

   CtiDirBuild db;

   string cidname(argv[1]);

   if(gDoCheckouts)
   {
       ProcessCID(db, cidname);
   }

   return(0);
}

