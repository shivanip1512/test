#ifndef _CTITYPES_H
#define _CTITYPES_H
 
/* header file to define types for CTI modules for portability betweeen OS's */

#if defined (IC_MOTIF) || defined (__xlC__)
/* For AIX and other Unix platforms */

#define VOID    void
#define CONST   const
typedef int INT;
typedef unsigned int UINT;

typedef INT BOOL;
#define FALSE 0
#define TRUE 1
#define NORMAL 0

typedef float FLOAT;
typedef double DOUBLE;

typedef unsigned char BYTE;
typedef unsigned short WORD;
typedef unsigned long DWORD;

typedef char CHAR;
typedef unsigned char UCHAR;
typedef short SHORT;
typedef unsigned short USHORT;
typedef long LONG;
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

#endif   /* IC_MOTIF || __xlC__   */


#endif  /* _CTITYPES_H */


