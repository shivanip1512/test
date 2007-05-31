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
void ProcessFile(CTIFILEVECTOR &vect, string &filename);
void ProcessDirectory( CTIFILEVECTOR &vect, const string &path);
void GenerateVInfo(CtiDirBuild &db, CTIFILEVECTOR &vect, string &infoname);


void ProcessDirectory(CTIFILEVECTOR &vect, const string &path)
{
   WIN32_FIND_DATA wfd;
   HANDLE fHandle;

   string findthis = path;
   string newdir;
   string filename;

   findthis += "\\*";

   fHandle = FindFirstFile( findthis.c_str(), &wfd );

   if( fHandle != INVALID_HANDLE_VALUE )
   {
      do
      {
         filename = wfd.cFileName;


         // Sleep(1000);
         boost::regex e1("\\.h");
         boost::regex e2("\\.cpp");

         if(filename[filename.length() - 1] != '.' && wfd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
         {
            // cout << "Processing directory " << filename << endl;
            ProcessDirectory(vect, string(path + "\\" + filename));


         }
         else if( boost::regex_search(filename, e1, boost::match_default) )
         {
            //cout << "Found header file named " << filename << endl;
            ProcessFile(vect, path + "\\" + filename);
         }
         else if( boost::regex_search(filename, e2, boost::match_default) )
         {
            //cout << "Found cpp file named " << filename << endl;
            ProcessFile(vect, path + "\\" + filename);
         }
      } while(FindNextFile(fHandle, &wfd));
   }

   return;
}

void ProcessFile(CTIFILEVECTOR &vect, string &filename)
{

   FILE *fp;

   CtiPVCS   finfo;

   char temp[128];

   string tstr;

   std::transform(filename.begin(), filename.end(), filename.begin(), ::tolower);


   if( !filename.find("id.h")!=string::npos ||            // Any file with id.h in it.
       !filename.find("version.cpp")!=string::npos ||
       !filename.find("version.h")!=string::npos ||
       !filename.find("vinfo.h")!=string::npos )
   {
      // Do not include these files in the output..
      return;
   }
   else
   {
      finfo.filename = filename;
      finfo.rev = "Unknown";
      finfo.date = "Unknown";

      fp = ::fopen(filename.c_str(), "rt");

      if(fp != NULL)
      {
         int linecnt = 0;
         while( fgets(temp, 127, fp)  && linecnt++ < 50)
         {
            temp[ ::strlen(temp) - 1 ] = '\0';

            string str(temp);
            boost::match_results<std::string::const_iterator> what;

            if(!str.find("Revision")!=string::npos)
            {
                boost::regex e1("\\$Revision:[ \t0-9\\.]*");
               if( boost::regex_search(str, what, e1, boost::match_default) )
               {
                   tstr = string(what[0]);
                  // finfo.rev = trim(tstr, "$");
                  //cout << filename << " : " << finfo.rev << endl;
               }
            }

            if(!str.find("Date")!=string::npos)
            {
                boost::regex e1("\\$Date:[ \ta-zA-Z0-9\\.:/]*");
               if( boost::regex_search(str, what, e1, boost::match_default) )
               {
                  tstr = string(what[0]);
                  // tstr = trim(tstr, "$");
                  boost::regex e2 = "Date:";
                  tstr = boost::regex_replace(tstr, e1, "", boost::match_default | boost::format_all | boost::format_first_only);

                  tstr = trim(tstr);
                  finfo.date = tstr;
                  //cout << filename << " : " << finfo.date << endl;
               }
            }
         }

         ::fclose(fp);

         vect.push_back(finfo);
      }
      else
      {
         cout << "**** Checkpoint **** " << filename << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
      }
   }


}

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
               dinfo._buildNumber = atoi(tstr.c_str()) + 1;
            }

            if(gBuildRevision >= 0 && dinfo._buildNumber != gBuildRevision)         // If specified and different.
            {
               // We need to generate a new buildnumber define!
               ::sprintf(oldnum, "%d", dinfo._buildNumber - 1);

               dinfo._buildNumber = gBuildRevision;
               ::sprintf(newnum, "%d", dinfo._buildNumber);

               e1.assign(oldnum);
               origstr = boost::regex_replace(origstr, e1, newnum, boost::match_default | boost::format_all | boost::format_first_only);


               cout << __LINE__ << " " << gBuildRevision << endl;
            }
            else if(gMinorRevision >= 0 || gMajorRevision >= 0)   // Need to reset this then.
            {
               // We need to generate a new buildnumber define!
              ::sprintf(oldnum, "%d", dinfo._buildNumber - 1);

               dinfo._buildNumber = 0;
               ::sprintf(newnum, "%d", dinfo._buildNumber);

               cout << __LINE__ << endl;
            }
            else
            {
               // We need to generate a new buildnumber define!
               ::sprintf(oldnum, "%d", dinfo._buildNumber - 1);
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

void GenerateVInfo(CtiDirBuild &db, CTIFILEVECTOR &vect, string &infoname)
{
   char temp[128];
   char dir[128];
   char file[128];
   char *ptr = NULL;
   FILE *fp;

   string vinfo(db._dir);
   vinfo = vinfo + "\\" + infoname;

   if(!GetFullPathName(vinfo.c_str(), 128, dir, &ptr))
   {
      cout << "It failed" << endl;
   }
   else
   {
      ::strcpy(file, ptr);
      *ptr = '\0';
   }


   // Now prepare to generate the id_vinfo.h file

   fp = ::fopen(vinfo.c_str(), "w");

   if(fp != NULL)
   {

      ::fprintf(fp, \
              "static struct {\n" \
              "   char *fname;\n" \
              "   double rev;\n" \
              "   char *date;\n} VersionInfo[] = {\n");


      CTIFILEVECTOR::iterator it;

      for(it = vect.begin(); it != vect.end(); it++)
      {
         CtiPVCS pS = *it;

         boost::regex e1("[0-9.]+");
         boost::match_results<std::string::const_iterator> what;
         boost::regex_search(pS.rev, what, e1, boost::match_default);

         string tstr = string(what[0]);
         double revision = 0;

         if(!tstr.empty())
         {
            revision = atof(tstr.c_str());
         }
         e1.assign(gCompileBase);
         pS.filename = boost::regex_replace(pS.filename, e1, "", boost::match_default | boost::format_all | boost::format_first_only);
         e1.assign("\\\\");
         pS.filename = boost::regex_replace(pS.filename, e1, "\\\\", boost::match_default | boost::format_all);


         ::fprintf(fp, "{ \"%s\", %f, \"%s\" },\n", pS.filename, revision, pS.date);
      }

      ::fprintf(fp, "{ NULL, 0.0, NULL },\n");
      ::fprintf(fp, "};\n");
      ::fclose(fp);

   }
   else
   {
      cout << "Could not open " << vinfo << " for writing" << endl;
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

       if(!infoname.empty())
       {
           ProcessDirectory(vect, path);
           GenerateVInfo(db, vect, infoname);
           vect.clear();
       }
   }

   return(0);
}

