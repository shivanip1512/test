#include "yukon.h"
#include <windows.h>
#include <iostream>
#include <vector>
using namespace std;

#include <stdio.h>
#include <string.h>

#include <io.h>
#include <sys/stat.h>

#include <rw\re.h>
#include <rw/rwfile.h>
#include <rw/cstring.h>

static RWCString gCompileBase;
static INT gMajorRevision = -1;
static INT gMinorRevision = -1;
static INT gBuildRevision = -1;
static bool gDoCheckouts = false;

class CtiPVCS
{
public:

   RWCString filename;
   RWCString rev;
   RWCString date;

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

   RWCString _dir;
   RWCString _project;

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

void ProcessCID(CtiDirBuild &db, RWCString &cidfile);
void ProcessFile(CTIFILEVECTOR &vect, RWCString &filename);
void ProcessDirectory( CTIFILEVECTOR &vect, const RWCString &path);
void GenerateVInfo(CtiDirBuild &db, CTIFILEVECTOR &vect, RWCString &infoname);


void ProcessDirectory(CTIFILEVECTOR &vect, const RWCString &path)
{
   WIN32_FIND_DATA wfd;
   HANDLE fHandle;

   RWCString findthis = path;
   RWCString newdir;
   RWCString filename;

   findthis += "\\*";

   fHandle = FindFirstFile( findthis, &wfd );

   if( fHandle != INVALID_HANDLE_VALUE )
   {
      do
      {
         filename = wfd.cFileName;


         // Sleep(1000);

         if(filename[filename.length() - 1] != '.' && wfd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY)
         {
            // cout << "Processing directory " << filename << endl;
            ProcessDirectory(vect, RWCString(path + "\\" + filename));
         }
         else if( !(filename.match("\\.h")).isNull() )
         {
            //cout << "Found header file named " << filename << endl;
            ProcessFile(vect, path + "\\" + filename);
         }
         else if( !(filename.match("\\.cpp")).isNull() )
         {
            //cout << "Found cpp file named " << filename << endl;
            ProcessFile(vect, path + "\\" + filename);
         }
      } while(FindNextFile(fHandle, &wfd));
   }

   return;
}

void ProcessFile(CTIFILEVECTOR &vect, RWCString &filename)
{

   FILE *fp;

   CtiPVCS   finfo;

   char temp[128];

   RWCString tstr;

   filename.toLower();


   if( !filename.match("id.h").isNull() ||            // Any file with id.h in it.
       !filename.match("version.cpp").isNull() ||
       !filename.match("version.h").isNull() ||
       !filename.match("vinfo.h").isNull() )
   {
      // Do not include these files in the output..
      return;
   }
   else
   {
      finfo.filename = filename;
      finfo.rev = "Unknown";
      finfo.date = "Unknown";

      fp = fopen(filename.data(), "rt");

      if(fp != NULL)
      {
         int linecnt = 0;
         while( fgets(temp, 127, fp)  && linecnt++ < 50)
         {
            temp[ strlen(temp) - 1 ] = '\0';

            RWCString str(temp);

            if(!str.match("Revision").isNull())
            {
               if( !(tstr = str.match("\\$Revision:[ \t0-9\\.]*")).isNull() )
               {
                  finfo.rev = tstr.strip(RWCString::both, '$');
                  //cout << filename << " : " << finfo.rev << endl;
               }
            }

            if(!str.match("Date").isNull())
            {
               if( !(tstr = str.match("\\$Date:[ \ta-zA-Z0-9\\.:/]*")).isNull() )
               {
                  tstr = tstr.strip(RWCString::both, '$');
                  tstr.replace("Date:","");
                  tstr = tstr.strip(RWCString::both);
                  finfo.date = tstr;
                  //cout << filename << " : " << finfo.date << endl;
               }
            }
         }

         fclose(fp);

         vect.push_back(finfo);
      }
      else
      {
         cout << "**** Checkpoint **** " << filename << " " << __FILE__ << " (" << __LINE__ << ")" << endl;
      }
   }


}

void ProcessCID(CtiDirBuild &db, RWCString &cidfile)
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
   vector< RWCString > fileStrings;

   cidfile.toLower();

   RWCString tstr = cidfile.match("([_a-z0-9.]+h)$");

   // cout << tstr << endl;

   if(tstr.length() == 0)
   {
      tstr = ".\\";
      cout << tstr << endl;
   }

   if(!GetFullPathName(cidfile, 128, dir, &ptr))
   {
      cout << "It failed" << endl;
   }
   else
   {
      strcpy(file, ptr);
      *ptr = '\0';
   }

   dinfo._dir = cidfile;
   dinfo._dir = dinfo._dir.replace(tstr, "");

   fp = fopen(cidfile.data(), "rt");

   if(fp != NULL)
   {
      while( fgets(temp, 127, fp) )
      {
         temp[ strlen(temp) - 1 ] = '\0';

         RWCString str(temp);
         RWCString origstr(temp);
         str.toLower();

         if(!str.match("project").isNull())
         {
            // store it off for the write.
            fileStrings.push_back(origstr);

            str.replace("#define","");
            str.replace("project","");
            str.strip (RWCString::both);

            dinfo._project = str;
            // cout << "Project " << dinfo._project << endl;
         }
         else if(!str.match("majorrevision").isNull())
         {
            if( !(tstr = str.match("[0-9]+")).isNull() )
            {
               dinfo._majorRevision = atoi(tstr.data());
            }

            if(gMajorRevision > 0 && dinfo._majorRevision != gMajorRevision)
            {
               // We need to generate a CTIDBG_new buildnumber define!
               sprintf(oldnum, "%d", dinfo._majorRevision);

               dinfo._majorRevision = gMajorRevision;
               sprintf(newnum, "%d", dinfo._majorRevision);

               origstr.replace(oldnum,newnum);
            }
            else
            {
               gMajorRevision = -1;                                                 // This should allow build to be incremented.
            }

            //cout << "MajorRevision " << dinfo._majorRevision << endl;

            // store it off for the write.
            fileStrings.push_back(origstr);

         }
         else if(!str.match("minorrevision").isNull())
         {
            if( !(tstr = str.match("[0-9]+")).isNull() )
            {
               dinfo._minorRevision = atoi(tstr.data());
            }

            if(gMinorRevision >= 0 && dinfo._minorRevision != gMinorRevision)
            {
               // We need to generate a CTIDBG_new buildnumber define!
               sprintf(oldnum, "%d", dinfo._minorRevision);

               dinfo._minorRevision = gMinorRevision;
               sprintf(newnum, "%d", dinfo._minorRevision);

               origstr.replace(oldnum,newnum);
            }
            else
            {
               gMinorRevision = -1;                                                 // This should allow build to be incremented.
            }

            //cout << "MinorRevision " << dinfo._minorRevision << endl;

            // store it off for the write.
            fileStrings.push_back(origstr);
         } else if(!str.match("buildnumber").isNull())
         {
            if( !(tstr = str.match("[0-9]+")).isNull() )
            {
               dinfo._buildNumber = atoi(tstr.data()) + 1;
            }

            if(gBuildRevision >= 0 && dinfo._buildNumber != gBuildRevision)         // If specified and different.
            {
               // We need to generate a CTIDBG_new buildnumber define!
               sprintf(oldnum, "%d", dinfo._buildNumber - 1);

               dinfo._buildNumber = gBuildRevision;
               sprintf(newnum, "%d", dinfo._buildNumber);

               origstr.replace(oldnum,newnum);

               cout << __LINE__ << " " << gBuildRevision << endl;
            }
            else if(gMinorRevision >= 0 || gMajorRevision >= 0)   // Need to reset this then.
            {
               // We need to generate a CTIDBG_new buildnumber define!
               sprintf(oldnum, "%d", dinfo._buildNumber - 1);

               dinfo._buildNumber = 0;
               sprintf(newnum, "%d", dinfo._buildNumber);

               cout << __LINE__ << endl;
            }
            else
            {
               // We need to generate a CTIDBG_new buildnumber define!
               sprintf(oldnum, "%d", dinfo._buildNumber - 1);
               sprintf(newnum, "%d", dinfo._buildNumber);

               cout << __LINE__ << endl;
            }

            origstr.replace(oldnum,newnum);

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

      fclose(fp);


      // cout << dinfo << endl;


      if( !access(cidfile, 2) )
      {
         bWritable = true;
      }
      else
      {
          cout << "Could not open " << cidfile << " for writing" << endl;
          return;
      }


      fp = fopen(cidfile.data(), "w");

      if(fp != NULL)
      {
         vector< RWCString >::iterator fit;

         for(fit = fileStrings.begin(); fit != fileStrings.end(); fit++)
         {
            RWCString str = *fit;
            // cout << str << endl;
            fprintf(fp, "%s\n", str.data());
         }
         fclose(fp);

         fileStrings.clear();
      }
      else
      {
         cout << "Could not open " << cidfile << " for writing" << endl;
      }
   }
   else
   {
      cout << " Couldn't open CID file " << cidfile.data() << " for writing" << endl;
   }
}

void GenerateVInfo(CtiDirBuild &db, CTIFILEVECTOR &vect, RWCString &infoname)
{
   char temp[128];
   char dir[128];
   char file[128];
   char *ptr = NULL;
   FILE *fp;

   RWCString vinfo(db._dir);
   vinfo = vinfo + "\\" + infoname;

   if(!GetFullPathName(vinfo, 128, dir, &ptr))
   {
      cout << "It failed" << endl;
   }
   else
   {
      strcpy(file, ptr);
      *ptr = '\0';
   }


   // Now prepare to generate the id_vinfo.h file

   fp = fopen(vinfo.data(), "w");

   if(fp != NULL)
   {

      fprintf(fp, \
              "static struct {\n" \
              "   char *fname;\n" \
              "   double rev;\n" \
              "   char *date;\n} VersionInfo[] = {\n");


      CTIFILEVECTOR::iterator it;

      for(it = vect.begin(); it != vect.end(); it++)
      {
         CtiPVCS pS = *it;

         RWCString tstr = pS.rev.match("[0-9.]+");
         double revision = 0;

         if(!tstr.isNull())
         {
            revision = atof(tstr.data());
         }

         pS.filename = pS.filename.replace(gCompileBase, "");
         pS.filename = pS.filename.replace("\\\\", "\\\\", RWCString::all);

         fprintf(fp, "{ \"%s\", %f, \"%s\" },\n", pS.filename, revision, pS.date);
      }

      fprintf(fp, "{ NULL, 0.0, NULL },\n");
      fprintf(fp, "};\n");
      fclose(fp);

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

   RWCString infoname;

   if(argc < 2)
   {
      cout << "What cid header file please?" << endl;
      return -1;
   }

   if( argc > 2 )
   {
       infoname = RWCString( argv[2] );
   }

   char temp[128];
   RWCString path(".");

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
      gCompileBase.toLower();

      if(gCompileBase[gCompileBase.length()-1] != '\\')
      {
         gCompileBase = gCompileBase + "\\";
      }

      gCompileBase = gCompileBase.replace("\\\\", "\\\\", RWCString::all);
   }

   // First find out if it has a CID.H file in it!

   CtiDirBuild db;

   RWCString cidname(argv[1]);

   if(gDoCheckouts)
   {
       ProcessCID(db, cidname);

       if(!infoname.isNull())
       {
           ProcessDirectory(vect, path);
           GenerateVInfo(db, vect, infoname);
           vect.clear();
       }
   }

   return(0);
}

