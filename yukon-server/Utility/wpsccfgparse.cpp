#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   wpsccfgparse
*
* Date:   8/23/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/UTILITY/wpsccfgparse.cpp-arc  $
* REVISION     :  $Revision: 1.6 $
* DATE         :  $Date: 2005/02/10 23:24:04 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <windows.h>
#include <winbase.h>
#include <fstream>
#include <iostream>
#include <string>
using namespace std;


void usage();
bool openfile( int op, int serial, ofstream &oFile, const string &dir);
void mergefiles(const string &dir);

int main(int argc, char **argv)
{
   int status = 0;

   if(argc < 3)
   {
      usage();
   }
   else
   {
      ifstream iFile(argv[1]);         // open for exclusive access for input
      string dirname(argv[2]);
      ofstream oFile;

      if(iFile.is_open())
      {
         int op;
         char delim;
         int serial;
         char temp[MAX_PATH];

         do
         {
            iFile >> op;
            // iFile >> delim;
            iFile >> serial;

            iFile.getline(temp, MAX_PATH);

            if(openfile( op, serial, oFile, dirname ) )
            {
               oFile << op << " " << serial << " " << temp << endl;
            }

            // cout << op << "," << serial << temp << endl;

         } while (!iFile.eof());


         iFile.close();
      }

      oFile.close();

      mergefiles(dirname);
   }




   return status;
}

void usage()
{
   cout << "wpsccfgparse infilename tempdir" << endl;
}

bool openfile( int op, int serial, ofstream &oFile , const string &dir)
{
   string fname;
   char snbuf[80];

   if(oFile.is_open())
   {
      oFile.close();
   }

   itoa( serial, snbuf, 10 );

   if(op == 4 || op == 5)
   {
      fname = dir + string("\\wcp_srvc_") + string(snbuf) + string(".cfg");
   }
   else
   {
      fname = dir + string("\\wcp_addr_") + string(snbuf) + string(".cfg");
   }

   // cout << fname << endl;

   oFile.open( fname.data() );

   return oFile.is_open();
}



void mergefiles(const string &dir)
{
   WIN32_FIND_DATA wfd;
   HANDLE fHandle;

   string findthis = dir;
   string newdir;
   string filename;
   char temp[MAX_PATH];

   ofstream oFile(".\\cleaned.rcv");

   if(oFile.is_open())
   {
      findthis += "\\wcp*.cfg";

      fHandle = FindFirstFile( findthis.data(), &wfd );

      if( fHandle != INVALID_HANDLE_VALUE )
      {
         do
         {
            filename = dir + string("\\") + string(wfd.cFileName);

            ifstream iFile(filename.data());         // open for exclusive access for input

            if(iFile.is_open())
            {
               iFile.getline(temp, MAX_PATH);
               oFile << temp << endl;

               iFile.close();
            }

            if(!DeleteFile(filename.data()))
            {
               cout << "Could not delete file " << filename << endl;
            }
            else
            {
               cout << " Deleted temporary file " << filename << endl;
            }

         } while(FindNextFile(fHandle, &wfd));

         FindClose(fHandle);
      }
   }

   return;
}
