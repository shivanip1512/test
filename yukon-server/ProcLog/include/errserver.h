#ifndef __ERRSERVER_HPP__
#define __ERRSERVER_HPP__

#include <rw\tpordvec.h>
#include "ctinexus.h"

#include "errlogfile.h"

VOID ErrorThread(VOID *ptr);     // Done by each connecting process once to accept incoming data.
VOID FileCleanupThread(VOID *ptr);
VOID FileManagerThread(VOID *ptr);     // Done by each connecting process once to accept incoming data.
INT  ProcessLogFile(WIN32_FIND_DATA *Data);



class CErrServer
{
private:

   // Data Members
   CTINEXUS       *Nexus;                          // The connection to the other side.

   static INT ClientConnections;
   // RogueWave Collection Class
   // Collection of output files to deal with!
   static RWTPtrOrderedVector<CErrLogFile> FileList;


public:

   // Constructors
   IM_EX_PROCLOG CErrServer();                             // Default
   IM_EX_PROCLOG CErrServer(const CErrServer& Cpy);        // Copy

   // Destructor
   IM_EX_PROCLOG ~CErrServer() {
      free(Nexus);
   }

   // Methods

   IM_EX_PROCLOG INT   Close();
   IM_EX_PROCLOG INT   CloseMain();
   IM_EX_PROCLOG INT   Listen();

   INT            NewInstance() { return (++ClientConnections); }

   INT            ListHas(CErrLogFile*);
   VOID           ListAdd(CErrLogFile*);
   CErrLogFile*   ListGet(CErrLogFile* EFile);

   INT            getErrMsg(CErrMsg* Err);

   CTINEXUS*      getNexus() { return (Nexus); }

   INT            getListLen() { return (FileList.length()); }
   CErrLogFile*   getListEntry(INT n) { return (FileList[n]); }

};

#endif       //#ifndef __ERRSERVER_HPP__


