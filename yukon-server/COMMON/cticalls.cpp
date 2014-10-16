#include "precompiled.h"

#include "cticalls.h"
#include "dllbase.h"
#include "logger.h"
#include "win_helper.h"

#include <string>

using Cti::getSystemErrorMessage;


void CTISleep(ULONG SleepTime)
{
    Sleep(SleepTime); // Win32 does not return anything.
}


YukonError_t CTIScanEnv(PSZ Name, PSZ *Result)
{
   *Result = NULL;
   static char Env[512];    // This IS be non-threadsafe

   if( ! GetEnvironmentVariable(Name, Env, 500) )
   {
       return ClientErrors::Abnormal;
   }
   
   *Result = Env;

   return ClientErrors::None;
}


YukonError_t CTICloseMutexSem(PHMTX phmtx)
{
   if( ! CloseHandle(*phmtx) )
   {
       const DWORD error = GetLastError();
       if(error != ERROR_FILE_NOT_FOUND)
       {
           CTILOG_ERROR(dout, "CloseHandle() failed with error code "<< error <<" / "<< getSystemErrorMessage(error));
       }
       return ClientErrors::Abnormal;
   }

   return ClientErrors::None;
}


YukonError_t CTICreateMutexSem(PSZ pszName, PHMTX phmtx, ULONG Flags, BOOL bState)
{
   if(!(*phmtx = CreateMutex( (LPSECURITY_ATTRIBUTES)NULL, FALSE, pszName))) // NULL = default security
   {
       const DWORD error = GetLastError();
       CTILOG_ERROR(dout, "CreateMutex() failed with error code "<< error <<" / "<< getSystemErrorMessage(error));

       return ClientErrors::Abnormal;
   }

   return ClientErrors::None;
}


YukonError_t CTIRequestMutexSem(HMTX hmtx, ULONG duration)
{
   const DWORD rVal = WaitForSingleObject(hmtx, duration);

   switch(rVal)
   {
   case WAIT_OBJECT_0:
       {
           return ClientErrors::None;
       }
   case WAIT_ABANDONED:
       {
           CTILOG_WARN(dout, "Wait Abandoned");
           return ClientErrors::None;
       }
   case WAIT_TIMEOUT:
       {
           return ClientErrors::Abnormal;
       }
   case WAIT_FAILED:
       {
           const DWORD err = GetLastError();
           CTILOG_ERROR(dout, "Wait failed with error code "<< err <<" / "<< getSystemErrorMessage(err));
           return ClientErrors::Abnormal;
       }
   default:
       {
           CTILOG_ERROR(dout, "Unexpected Wait Result ("<< rVal <<")");
           return ClientErrors::Abnormal;
       }
   }
}


YukonError_t CTIReleaseMutexSem(HMTX hmtx)
{
   return ReleaseMutex(hmtx)
           ? ClientErrors::None  // Win32 returns positive if successful.
           : ClientErrors::Abnormal;
}


YukonError_t CTIDelete(PSZ old)
{
   return DeleteFile(old)
           ? ClientErrors::None  // Win32 returns positive if successful.
           : ClientErrors::Abnormal;
}

// Function to return the current thread ID - 32-bit only
DWORD CurrentTID()
{
   return GetCurrentThreadId();
}


YukonError_t CTIClose(HANDLE &hFile)
{
   YukonError_t status = ClientErrors::None;

   // Win32 returns positive if successful.
   if( ! CloseHandle(hFile) )
   {
      status = ClientErrors::Abnormal;
   }

   hFile = (HANDLE)NULL;

   return status;
}


YukonError_t CTIRead(HANDLE  &hFile,
                     PVOID    pBuf,
                     ULONG    BufLen,
                     PULONG   pBytesRead)
{
    return ReadFile(hFile, pBuf, BufLen, pBytesRead, NULL)
            ? ClientErrors::None  // Win32 returns positive if successful.
            : ClientErrors::Abnormal;
}


YukonError_t CTIWrite(HANDLE  &hFile,
                      PVOID    pBuf,
                      ULONG    BufLen,
                      PULONG   pBytesWritten)
{
    if( ! WriteFile(hFile, pBuf, BufLen, pBytesWritten, NULL) )
    {
        const DWORD error = GetLastError();
        CTILOG_ERROR(dout, "WriteFile() failed with error code "<< error <<" / "<< getSystemErrorMessage(error));

        return ClientErrors::Abnormal;
    }

    return ClientErrors::None;
}


YukonError_t CTICreateEventSem(PSZ pszName, PHEV phev, ULONG Flags, BOOL32 bState)
{
    if( !(*phev = CreateEvent((LPSECURITY_ATTRIBUTES)NULL,  // NULL = default security
                              TRUE,                         // Manual ReSet Event
                              bState,                       // TRUE == owned by this thread
                              pszName)) )
    {
        const DWORD error = GetLastError();
        CTILOG_ERROR(dout, "CreateEvent() failed with error code "<< error <<" / "<< getSystemErrorMessage(error));

        return ClientErrors::Abnormal;
    }

    return ClientErrors::None;
}


YukonError_t CTICloseEventSem(PHEV phev)
{
   if( ! CloseHandle(*phev) )
   {
      const DWORD error = GetLastError();
      if( error != ERROR_FILE_NOT_FOUND )
      {
          CTILOG_ERROR(dout, "CloseHandle() failed with error code "<< error <<" / "<< getSystemErrorMessage(error));
      }

      return ClientErrors::Abnormal;
   }

   return ClientErrors::None;
}


YukonError_t CTIResetEventSem(HEV hev, PULONG pulPostCt)
{
   return ResetEvent(hev)
           ? ClientErrors::None   // Win32 returns positive if successful.
           : ClientErrors::Abnormal;
}


YukonError_t CTIPostEventSem(HEV hev)
{
   return SetEvent(hev)
           ? ClientErrors::None   // Win32 returns positive if successful.
           : ClientErrors::Abnormal;
}


YukonError_t CTIOpen (PSZ         pszFileName,
                      PHFILE      pHf,
                      PULONG      pAction,
                      ULONG       ulFSize,
                      ULONG       ulAttrib,
                      ULONG       Flags,
                      ULONG       Mode,
                      PEAOP2      peaop2)
{
   DWORD    dwAccess = 0;      // GENERIC_READ/WRITE
   DWORD    dwShare  = 0;      // share mode  FILE_SHARE_DELETE/READ/WRITE
   DWORD    dwCreate = 0;      // how to create
   DWORD    dwAttFlags = 0;    // file attributes

   // Set the attributes
   if(ulAttrib & FILE_NORMAL)    dwAttFlags  |= FILE_ATTRIBUTE_NORMAL;
   if(ulAttrib & FILE_READONLY)  dwAttFlags  |= FILE_ATTRIBUTE_READONLY;
   if(ulAttrib & FILE_HIDDEN)    dwAttFlags  |= FILE_ATTRIBUTE_HIDDEN;
   if(ulAttrib & FILE_SYSTEM)    dwAttFlags  |= FILE_ATTRIBUTE_SYSTEM;
   if(ulAttrib & FILE_ARCHIVED)  dwAttFlags  |= FILE_ATTRIBUTE_ARCHIVE;

   dwCreate    |= OPEN_EXISTING;

   // Set the creation flags
   if(Flags & OPEN_ACTION_FAIL_IF_EXISTS) dwCreate    |= CREATE_NEW;
   else if(Flags & OPEN_ACTION_CREATE_IF_NEW) dwCreate    |= OPEN_ALWAYS;
   else if(Flags & OPEN_ACTION_REPLACE_IF_EXISTS) dwCreate    |= CREATE_ALWAYS;
   else if((Flags & OPEN_ACTION_OPEN_IF_EXISTS) || (Flags & OPEN_ACTION_FAIL_IF_NEW)) dwCreate    |= OPEN_EXISTING;

   // This is the tricky one.... First lets Get the sharing attributes
   if(Mode & OPEN_SHARE_DENYNONE)              dwShare = (FILE_SHARE_READ | FILE_SHARE_WRITE);
   if(Mode & OPEN_SHARE_DENYWRITE)             dwShare = (FILE_SHARE_READ);
   if(Mode & OPEN_SHARE_DENYREAD)              dwShare = (FILE_SHARE_WRITE);
   if(Mode & OPEN_SHARE_DENYREADWRITE)         dwShare = 0;

   // Now the access parameters are gleaned from the Mode
   if(Mode & OPEN_ACCESS_READONLY)             dwAccess |= GENERIC_READ;
   if(Mode & OPEN_ACCESS_WRITEONLY)            dwAccess |= GENERIC_WRITE;
   if(Mode & OPEN_ACCESS_READWRITE)            dwAccess |= GENERIC_READ | GENERIC_WRITE;

   // OS2 Mode also contains these elements... which are in Win32s AttrandFlags member

   if(Mode & OPEN_FLAGS_SEQUENTIAL)            dwAttFlags |= FILE_FLAG_SEQUENTIAL_SCAN;
   if(Mode & OPEN_FLAGS_RANDOM)                dwAttFlags |= FILE_FLAG_RANDOM_ACCESS;
   if(Mode & OPEN_FLAGS_NO_CACHE)              dwAttFlags |= FILE_FLAG_WRITE_THROUGH;
   if(Mode & OPEN_FLAGS_WRITE_THROUGH)         dwAttFlags |= FILE_FLAG_WRITE_THROUGH;

//   if (Mode & OPEN_FLAGS_NOINHERIT)             dwAttFlags |= FILE_FLAG_;
//   if (Mode & OPEN_FLAGS_NO_LOCALITY)           dwAttFlags |= FILE_FLAG_;
//   if (Mode & OPEN_FLAGS_RANDOMSEQUENTIAL)      dwAttFlags |= FILE_FLAG_;
//   if (Mode & OPEN_FLAGS_FAIL_ON_ERROR)         dwAttFlags |= FILE_FLAG_;
//   if (Mode & OPEN_FLAGS_DASD)                  dwAttFlags |= FILE_FLAG_;

   /*
    *  If this is done on a port > COM9, we need to add the device identifiaer \\.\COMXX to the name
    */

   char portname[80];

   if( !strnicmp(pszFileName, "COM", 3) )
   {
      strcpy(portname, "\\\\.\\");
      strncat(portname,pszFileName, 70);
   }
   else
   {
      strncpy(portname, pszFileName, 80);
   }

   *pHf = CreateFile( portname, dwAccess, dwShare, NULL, dwCreate, dwAttFlags, NULL );

   if(*pHf == INVALID_HANDLE_VALUE)
   {
      const DWORD error = GetLastError();

      if( error == ERROR_FILE_NOT_FOUND || error == ERROR_PATH_NOT_FOUND )
      {
         // printf("Error in CTIOpen (Win32 CreateFile):(%s) %5d\n", pszFileName,err);
      }
      else
      {
          CTILOG_ERROR(dout, "CreateFile() failed with error code "<< error <<" / "<< getSystemErrorMessage(error));

         *pHf = NULL; // Most of the code expects this!
      }
      return ClientErrors::Abnormal;    // Error of some sort occured! timeout or otherwise
   }

   *pAction = 1;
   return ClientErrors::None;    // We have the file handle now!
}

//TODO: OUMESS and INMESS should inherit Loggable instead - this function can be removed
IM_EX_CTIBASE std::string outMessageToString(const OUTMESS* Om)
{
    Cti::FormattedList itemList;

    itemList.add("Device ID")   << Om->DeviceID;
    itemList.add("Target ID")   << Om->TargetID;
    itemList.add("Port")        << Om->Port;
    itemList.add("Remote")      << Om->Remote;
    itemList.add("Sequence")    << Om->Sequence;
    itemList.add("Priority")    << Om->Priority;
    itemList.add("TimeOut")     << Om->TimeOut;
    itemList.add("Retry")       << Om->Retry;
    itemList.add("OutLength")   << Om->OutLength;
    itemList.add("InLength")    << Om->InLength;
    itemList.add("Source")      << Om->Source;
    itemList.add("Destination") << Om->Destination;
    itemList.add("Command")     << Om->Command;
    itemList.add("Function")    << Om->Function;
    itemList.add("EventCode")   << Om->EventCode;

    return itemList.toString();
};
