//should this be here?
#define STANDNAMLEN 20
#ifndef  NORMAL
   #define NORMAL 0
#endif

/****************************************************************************
 * OS2 - Win32 type translations
 ***************************************************************************/
#define  APIRET   DWORD
#define  BOOL32   ULONG
#define  HEV      HANDLE            // Event Handle
// #define  HANDLE    HANDLE            // File handle
#define  HMTX     HANDLE            // Mutex Handle
#define  HPIPE    HANDLE            // Pipe handle
#define  HQUEUE   HANDLE            // QUEUE handle
#define  PEAOP2   UINT
#define  PFN      FARPROC
#define  PHEV     HANDLE*
#define  PHFILE   HANDLE*
#define  PHMODULE HANDLE*           // Pointer to handle of a DLL
#define  PHMTX    HANDLE*           // Pointer to a mutex handle
#define  PHPIPE   HANDLE*           // Pipe handle
#define  PID      ULONG             // OS2 Process ID is a ULONG
#define  PPVOID   void**            // Pointer to void pointer ??
#define  PSZ      LPSTR
#define  SEL      USHORT            // A Thread ID.
#define  TID      DWORD             // A Thread ID.




