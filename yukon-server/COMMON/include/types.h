//should this be here?
#define STANDNAMLEN 20
#ifndef  NORMAL
   #define NORMAL 0
#endif
#define VOID    void
#define CONST   const

#if 0
typedef int INT;
typedef unsigned int UINT;
typedef float FLOAT;
typedef char CHAR;
typedef short SHORT;
typedef long LONG;
typedef INT BOOL;


typedef double DOUBLE;

typedef unsigned char BYTE;
typedef unsigned short WORD;
typedef unsigned long DWORD;

typedef unsigned char UCHAR;
typedef unsigned short USHORT;
typedef unsigned long ULONG;

typedef char * PCHAR;
typedef unsigned char * PUCHAR;
typedef unsigned char * PBYTE;
typedef short * PSHORT;
typedef unsigned short * PUSHORT;
typedef long * PLONG;
typedef unsigned long * PULONG;
typedef int * PINT;
typedef unsigned int * PUINT;
typedef void * PVOID;
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
#define  PPVOID   VOID**            // Pointer to void pointer ??
#define  PSZ      LPSTR
#define  SEL      USHORT            // A Thread ID.
#define  TID      DWORD             // A Thread ID.




