#include "yukon.h"

#include <windows.h>
#include <fstream>
#include <iostream>
using namespace std;

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <memory.h>
#include <process.h>

#include <rw\tvdlist.h>
#include <rw\rwtime.h>
#include <rw\rwDate.h>

#include "cticalls.h"
#include "ctinexus.h"
#include "proclog.h"
#include "dllmain.h"
#include "errmsg.h"
#include "errclient.h"

#include "errserver.h"

#define MAINLOGNAME        "main.clg"
#define MAINLOGLENGTH      30
#define DEFAULTLOGLENGTH   5

INT   ShortenLogFile(WIN32_FIND_DATA *Data, INT Days);

// Must define static members in a C++ file!
RWTPtrOrderedVector<CErrLogFile> CErrServer::FileList = RWTPtrOrderedVector<CErrLogFile>();   // Define static member at file scope!
INT CErrServer::ClientConnections = 0;

// Default
CErrServer::CErrServer()
{
   Nexus = (CTINEXUS*)malloc(sizeof( CTINEXUS ));
   return;
}

CErrServer::CErrServer(const CErrServer& Cpy)
{
   if(this == &Cpy)
      return;

   Nexus = (CTINEXUS*) malloc(sizeof( CTINEXUS ));
   memcpy(Nexus, &Cpy.Nexus, sizeof(CTINEXUS));

   return;
}

INT CErrServer::Close()
{
   INT i = 0;

   ClientConnections--;

   if(Nexus->NexusState != CTINEXUS_STATE_NULL)
   {
      i = Nexus->CTINexusClose();
   }

   return i;
}

INT CErrServer::CloseMain()
{
   Initialized = FALSE;
   return Initialized;
}

INT CErrServer::Listen()
{
   extern VOID ErrorThread(VOID *ptr);    // Found in proclog.cpp

   INT      nRet = 0;
   INT      i    = 0;
   HANDLE   hEv = CreateEvent(NULL, TRUE, FALSE, NULL);

   CErrServer  *NewServer;
   // Make an entry for main.clg
   CErrLogFile *EMainFile = new CErrLogFile(MAINLOGNAME);
   ListAdd(EMainFile);              // Add to "static class member" Server file list
   EMainFile->BumpImportance();     // Identify me as a special/main file...

   strcpy(Nexus->Name, "Error Console Server Side Listening Socket");

   /*
    * Manager needs a CErrServer pointer so that he has a connection to the
    * FileList collection class
    */
   _beginthread(FileCleanupThread, 0, (void*)this);  // One cleanup thread.
   _beginthread(FileManagerThread, 0, (void*)this);  // One manager thread.

   if(!Nexus->CTINexusCreate(PROCLOGNEXUS))
   {
      Initialized = TRUE;

      for(;;)
      {
         NewServer = new CErrServer;

         sprintf(NewServer->Nexus->Name, "Server Side %d Connected Socket", i++);

         // cout << "Waiting for a new connection" << endl;
         nRet = Nexus->CTINexusConnect(NewServer->Nexus, (HANDLE*)NULL);

         if(!nRet)
         {
            /* Someone has connected to us.. */
            // cout << "Connection " << i << " Established with client application" << endl;
            _beginthread(ErrorThread, 0, (VOID*)NewServer);
            ErrorThreadCount++;
         }
      }

      if(Nexus->NexusState != CTINEXUS_STATE_NULL)
      {
         Nexus->CTINexusClose();
      }


   }

   CloseHandle(hEv);

   return 0;
}

/*
 *  This too, is a SERVER side call. An app WILL/MUST never call this!
 *  This is actually where the server application does all his work.
 */

VOID ErrorThread(VOID *ptr)
{
   INT      nRet;
   INT      MyPID = _getpid();
   BOOL     bExit = FALSE;

   CErrLogFile *EFile;
   CErrLogFile *temp;
   CErrServer  *EServer = (CErrServer*)ptr;
   CErrLogFile *EMainFile = EServer->ListGet(&CErrLogFile(MAINLOGNAME));
   CErrMsg     *Err;


   // Add one to the instance count
   EServer->NewInstance();

   do
   {
      // assert(Err != 0);
      if(!(Err = new CErrMsg))
      {
         cerr << "Nakked on new call" << endl;
         Sleep(1000);
         continue;
      }

      /* Go out and get an error from the connection */
      EServer->getErrMsg(Err);
      // cerr << __FILE__ << " (" << __LINE__ << ")" << endl;

      /* Create an error output file object from this error message */
      EFile = new CErrLogFile(Err->getEMsgFile());    // Pass in the filename
      // cerr << __FILE__ << " (" << __LINE__ << ")" << endl;


      if(!EServer->ListHas(EFile))
      {
         //cerr << __FILE__ << " (" << __LINE__ << ")" << endl;
         /* Add the new file to the list */
         EServer->ListAdd(EFile);
      }
      else
      {
         // cerr << __FILE__ << " (" << __LINE__ << ")" << endl;
         /* Use the existing list entry!... */
         temp  = EFile;                            // Store the newly created one
         EFile = EServer->ListGet(EFile);          // Find the match to this filename
         delete temp;                              // Delete the newly created object
      }

      if(!(Err->getEMsgOpFlags() & PLOG_OP_NOMASTER))
      {
         // cerr << __FILE__ << " (" << __LINE__ << ")" << endl;
         /* Add _a copy_ to the master file's list of errors */
         EMainFile->AddACopy(Err);
      }
      // cerr << __FILE__ << " (" << __LINE__ << ")" << endl;
      // Add the error to this file's list o'stuff to log about!
      nRet = EFile->Add(Err);
      if(CMD_CLOSEMYSOCKET == nRet    || CMD_CLOSEPROCLOG  == nRet)
      {
         // cerr << __FILE__ << " (" << __LINE__ << ")" << endl;
         bExit = TRUE;
      }

   } while(!bExit);

   // Close my connection.
   EServer->Close();
   delete EServer;

   if(--ErrorThreadCount == 0)
   {
      // No one is connected anymore .....

      cout << "Last client has terminated ... " << endl;
      exit(0);
   }

   return;
}

VOID FileManagerThread(VOID *ptr)
{
   INT         i;
   BOOL        bExit = FALSE;

   CErrLogFile    *EFile;
   CErrServer     *EServer = (CErrServer*)ptr;


   for(;!bExit;)
   {
      Sleep(500);

      for (i = 0; i < EServer->getListLen(); i++)
      {
         EFile = EServer->getListEntry(i);
         if(EFile->DoLogDump())
         {
            EFile->LogDump();
         }
      }
   }

   cerr << "FileManagerThread Exiting" << endl;

   return;
}

/* Cleanup and Truncation thread.  Runs every X seconds */
VOID FileCleanupThread(VOID *ptr)
{
   HANDLE                  hFileNext;
   WIN32_FIND_DATA         Data;
   FILETIME                SystemFileTime;
   LARGE_INTEGER           li_SystemTime;
   LARGE_INTEGER           li_LastWrite;
   LONGLONG                bfn;

   for(;;)
   {

      GetSystemTimeAsFileTime(&SystemFileTime);

      li_SystemTime.LowPart  = SystemFileTime.dwLowDateTime;
      li_SystemTime.HighPart = SystemFileTime.dwHighDateTime;

      hFileNext = FindFirstFile("*.clg", &Data);

      do
      {
         if(hFileNext != INVALID_HANDLE_VALUE)
         {

            li_LastWrite.LowPart  = Data.ftLastWriteTime.dwLowDateTime;
            li_LastWrite.HighPart = Data.ftLastWriteTime.dwHighDateTime;

            bfn = li_SystemTime.QuadPart - li_LastWrite.QuadPart;

            bfn = bfn / (10000000);   // bfn is number of seconds since last write in seconds.

            if(bfn > (86400 * 30))
            {
               cout << "File " << Data.cFileName << " unused for " << (LONG)bfn/86400 << " days" << endl;
               cout << "\tIt will be deleted" << endl;
               DeleteFile(Data.cFileName);
            }
            else
            {
               ProcessLogFile(&Data);
            }
         }

      } while(hFileNext != INVALID_HANDLE_VALUE && FindNextFile(hFileNext, &Data) );

      FindClose (hFileNext);

      CTISleep(60000L);
   }

   cerr << "FileCleanupThread Exiting" << endl;

   return;
}


INT CErrServer::getErrMsg(CErrMsg* Err)
{
   return(Err->Get(Nexus, CTINEXUS_INFINITE_TIMEOUT));     // get an error message
}

INT CErrServer::ListHas(CErrLogFile* EFile)
{
   return(FileList.contains(EFile));     // get an error message
}

VOID CErrServer::ListAdd(CErrLogFile* EFile)
{
   FileList.insert(EFile);
}

CErrLogFile* CErrServer::ListGet(CErrLogFile* EFile)
{
   return(FileList.find(EFile));
}

/*
 *  If the file is larger than 100 kBytes, we will truncate it to 5 days worth of data.  If the
 *  file is main.clg, we will not observe these rules, but rather will truncate always to 30 days
 *  of data.
 */
INT   ProcessLogFile(WIN32_FIND_DATA *Data)
{
   #if 0
   if(Data->nFileSizeHigh || Data->nFileSizeLow >= 100000)
   {
      cout << "**** File " << Data->cFileName << " is larger than recommended " << Data->nFileSizeLow / 1000 << " kBytes to be exact" << endl;

      if(!strcmp(Data->cFileName, MAINLOGNAME))
      {
         /* This is the master clg file, so we use a 30 day interval */

         ShortenLogFile(Data, MAINLOGLENGTH);
      }
      else
      {
         ShortenLogFile(Data, DEFAULTLOGLENGTH);
      }
   }
   #endif

   return 0;
}


INT   ShortenLogFile(WIN32_FIND_DATA *Data, INT Days)
{
   INT      Month, Day, Year;
   CHAR     ch;
   INT      nRet = 0;
   CHAR     TempFile[MAX_PATH];
   CHAR     Temp[MAX_PATH];
   ifstream iFile(Data->cFileName);         // open for exclusive access for input
   ofstream oFile;

   streampos   posTemp, inWhere;

   RWDate   TempNew, TempOld;
   RWDate   Today;                           // Todays Date
   RWDate   FileStart;                       // When we want file to start.. Initializes to today.

   TempOld  -= 100;     // Point at 100 days ago to start.
   Today    -= Days;    // How long ago till we care

   /* First we sweep to the end getting the largest RWDate we can find */
   if(iFile.is_open())
   {
      inWhere = iFile.tellg();
      do
      {
         posTemp = iFile.tellg();

         iFile >> Month;
         iFile >> ch;
         iFile >> Day;
         iFile >> ch;
         iFile >> Year;

         TempNew = RWDate(Day, Month, Year);

         if( TempNew > TempOld)
         {
            inWhere = posTemp;
            TempOld = TempNew;

            if(TempOld >= Today) break;         // get outa here if we have what we wanted.
         }

         iFile.getline(Temp, MAX_PATH);

         cout << __FILE__ << " (" << __LINE__ << ")" << endl;

      } while (!iFile.eof());
   }

   if(GetTempFileName(".", "cti", 0, TempFile))
   {
      oFile.open(TempFile);

      if(iFile.is_open())
      {
         iFile.clear();                               // clear the ios flags.
         iFile.seekg(inWhere);


         iFile.getline(Temp, MAX_PATH);
         while(!iFile.eof())
         {
            oFile << Temp << endl;
            iFile.getline(Temp, MAX_PATH);
         }

         iFile.close();
      }

      if(oFile.is_open())
      {
         oFile.close();

         iFile.open(TempFile);
         oFile.open(Data->cFileName);

         if(oFile.is_open() && iFile.is_open())
         {
            iFile.getline(Temp, MAX_PATH);
            while(!iFile.eof())
            {
               oFile << Temp << endl;
               iFile.getline(Temp, MAX_PATH);
            }

            iFile.close();
            oFile.close();
         }
         DeleteFile(TempFile);
      }
   }
   else
   {
      cout << "Pish posh " << endl;
   }

   return nRet;
}
