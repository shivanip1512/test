#ifndef __DLLDEFS_H__
#define __DLLDEFS_H__

#define DLLEXPORT             __declspec(dllexport)
#define DLLIMPORT             __declspec(dllimport)
#define BASEDLL_IMPORT        __declspec(dllimport)
#define SCANGLOB_DLL_IMPORT   __declspec(dllimport)

#ifdef _DLL_CTIVGC
   #define IM_EX_CTIVGC       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_CTIVGC
#else
   #define IM_EX_CTIVGC       __declspec(dllimport)
#endif

#ifdef _DLL_CTIIPC
   #define IM_EX_CTIIPC       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_CTIIPC
#else
   #define IM_EX_CTIIPC       __declspec(dllimport)
#endif

#ifdef _DLL_SIGNAL
   #define IM_EX_SIGNAL       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_SIGNAL
#else
   #define IM_EX_SIGNAL       __declspec(dllimport)
#endif

#ifdef _DLL_CTIPIL
   #define IM_EX_CTIPIL       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_CTIPIL
#else
   #define IM_EX_CTIPIL       __declspec(dllimport)
#endif

#ifdef CTISVR                  // The ctisvr.dll
   #define IM_EX_CTISVR       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_CTISVR
#else
   #define IM_EX_CTISVR       __declspec(dllimport)
#endif

#ifdef CTIMSG                 // The ctibase.dll
   #define IM_EX_CTIMSG       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_CTIMSG
#else
   #define IM_EX_CTIMSG       __declspec(dllimport)
#endif

#ifdef CTIQUEUE                // The ctisvr.dll
   #define IM_EX_CTIQUEUE     __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_CTIQUEUE
#else
   #define IM_EX_CTIQUEUE     __declspec(dllimport)
#endif

#ifdef CTIBASE                // The ctibase.dll
   #define IM_EX_CTIBASE      __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_CTIBASE
#else
   #define IM_EX_CTIBASE      __declspec(dllimport)
#endif

#ifdef CTIVANGOGH
   #define IM_EX_CTIVANGOGH   __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_CTIVANGOGH
#else
   #define IM_EX_CTIVANGOGH   __declspec(dllimport)
#endif

#ifdef _DLL_SERVICE
   #define IM_EX_SERVICE __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_SERVICE
#else
   #define IM_EX_SERVICE __declspec(dllimport)
#endif

#ifdef _DLL_MESSAGE
   #define IM_EX_MSG __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_MSG
#else
   #define IM_EX_MSG __declspec(dllimport)
#endif

#ifdef _DLL_INTERP
   #define IM_EX_INTERP __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_INTERP
#else
   #define IM_EX_INTERP __declspec(dllimport)
#endif

#ifdef _DLL_MCCMD
   #define IM_EX_MCCMD __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_MCCMD
#else
   #define IM_EX_MCCMD __declspec(dllimport)
#endif

#ifdef _DLL_CPARM
   #define IM_EX_CPARM   __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_CPARM
#else
   #define IM_EX_CPARM   __declspec(dllimport)
#endif

#ifdef _DLL_C_CPARM
   #define IM_EX_C_CPARM   __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_C_CPARM
#else
   #define IM_EX_C_CPARM   __declspec(dllimport)
#endif

#ifdef _DLL_SCANSUP
   #define IM_EX_SCANSUP   __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_SCANSUP
#else
   #define IM_EX_SCANSUP   __declspec(dllimport)
#endif

#ifdef CTIYUKONDB                // The ctidb.dll
   #define IM_EX_CTIYUKONDB   __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_CTIYUKONDB
#else
   #define IM_EX_CTIYUKONDB   __declspec(dllimport)
#endif

#ifdef PORTDB                 // The ctibase.dll
   #define IM_EX_PORTDB       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_PORTDB
#else
   #define IM_EX_PORTDB       __declspec(dllimport)
#endif

#ifdef _DLL_DEVDB                // The ctidb.dll
   #define IM_EX_DEVDB   __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_DEVDB
#else
   #define IM_EX_DEVDB   __declspec(dllimport)
#endif

#ifdef _DLL_PNTDB                // The ctidb.dll
   #define IM_EX_PNTDB   __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_PNTDB
#else
   #define IM_EX_PNTDB   __declspec(dllimport)
#endif

#ifdef _DLL_PRTDB                // The ctidb.dll
   #define IM_EX_PRTDB   __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_PRTDB
#else
   #define IM_EX_PRTDB   __declspec(dllimport)
#endif

#ifdef _DLL_HOLIDAYDB                // The ctiholiday.dll
   #define IM_EX_HOLIDAYDB   __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_HOLIDAYDB
#else
   #define IM_EX_HOLIDAYDB   __declspec(dllimport)
#endif

#ifdef _DLL_SEASONDB                // The ctiseason.dll
   #define IM_EX_SEASONDB   __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_SEASONDB
#else
   #define IM_EX_SEASONDB   __declspec(dllimport)
#endif

#ifdef _DLL_DBMGR
   #define IM_EX_DLL_DBMGR     __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_DLL_DBMGR
#else
   #define IM_EX_DLL_DBMGR     __declspec(dllimport)
#endif

#ifdef _DLL_HOLIDAYMGR
    #define IM_EX_DLL_HOLIDAYMGR __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
    #define IM_EX_DLL_HOLIDAYMGR
#else
    #define IM_EX_DLL_HOLIDAYMGR __declspec(dllimport)
#endif    

#ifdef _DLL_SEASONMGR
    #define IM_EX_DLL_SEASONMGR __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
    #define IM_EX_DLL_SEASONMGR
#else
    #define IM_EX_DLL_SEASONMGR __declspec(dllimport)
#endif    

#ifdef ROUTEDB                // The ctibase.dll
   #define IM_EX_ROUTEDB      __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_ROUTEDB
#else
   #define IM_EX_ROUTEDB      __declspec(dllimport)
#endif

#ifdef POINTDB                // The ctibase.dll
   #define IM_EX_POINTDB      __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_POINTDB
#else
   #define IM_EX_POINTDB      __declspec(dllimport)
#endif

#ifdef MEMPOINTDB                // The ctibase.dll
   #define IM_EX_MEMPOINTDB      __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_MEMPOINTDB
#else
   #define IM_EX_MEMPOINTDB      __declspec(dllimport)
#endif

#ifdef _DLL_CMDLINE                // The ctibase.dll
   #define IM_EX_CMDLINE      __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_CMDLINE
#else
   #define IM_EX_CMDLINE      __declspec(dllimport)
#endif

#ifdef DEVICEDB                // The ctibase.dll
   #define IM_EX_DEVICEDB      __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_DEVICEDB
#else
   #define IM_EX_DEVICEDB      __declspec(dllimport)
#endif

#ifdef _DLL_PROCLOG                 // The ctisvr.dll
   #define IM_EX_PROCLOG      __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_PROCLOG
#else
   #define IM_EX_PROCLOG      __declspec(dllimport)
#endif

#ifdef _DLL_TCPSUP                  // The ctisvr.dll
   #define IM_EX_TCPSUP       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_TCPSUP
#else
   #define IM_EX_TCPSUP       __declspec(dllimport)
#endif

#ifdef _DLL_CTISCN
   #define IM_EX_CTISCN       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_CTISCN
#else
   #define IM_EX_CTISCN       __declspec(dllimport)
#endif

#ifdef _DLL_PORTGLOB                // The ctisvr.dll
   #define IM_EX_PORTGLOB       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_PORTGLOB
#else
   #define IM_EX_PORTGLOB       __declspec(dllimport)
#endif

#ifdef _DLL_XCHG                // The ctisvr.dll
   #define IM_EX_XCHG       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_XCHG
#else
   #define IM_EX_XCHG       __declspec(dllimport)
#endif

#ifdef _DLL_PROT
   #define IM_EX_PROT   __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_PROT
#else
   #define IM_EX_PROT   __declspec(dllimport)
#endif

#ifdef  EXPORT
   #define IM_EX_PORT         __declspec(dllexport)
#elif defined(IMPORT)
   #define IM_EX_PORT         __declspec(dllimport)
#else
   #define IM_EX_PORT
#endif


#ifdef _DLL_FDRBASE
   #define IM_EX_FDRBASE       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_FDRBASE
#else
   #define IM_EX_FDRBASE       __declspec(dllimport)
#endif

#ifdef _DLL_FDRCYGNET
   #define IM_EX_FDRCYGNET       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_FDRCYGNET
#else
   #define IM_EX_FDRCYGNET       __declspec(dllimport)
#endif

#ifdef _DLL_FDRACS
   #define IM_EX_FDRACS       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_FDRACS
#else
   #define IM_EX_FDRACS       __declspec(dllimport)
#endif

#ifdef _DLL_FDRVALMET
   #define IM_EX_FDRVALMET       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_FDRVALMET
#else
   #define IM_EX_FDRVALMET       __declspec(dllimport)
#endif

#ifdef _DLL_FDRINET
   #define IM_EX_FDRINET       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_FDRINET
#else
   #define IM_EX_FDRINET       __declspec(dllimport)
#endif

#ifdef _DLL_FDRRCCS
   #define IM_EX_FDRRCCS       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_FDRRCCS
#else
   #define IM_EX_FDRRCCS       __declspec(dllimport)
#endif

#ifdef _DLL_FDRTRISTATE
   #define IM_EX_FDRTRISTATE       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_FDRTRISTATE
#else
   #define IM_EX_FDRTRISTATE       __declspec(dllimport)
#endif

#ifdef _DLL_FDRSTEC
   #define IM_EX_FDRSTEC       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_FDRSTEC
#else
   #define IM_EX_FDRSTEC       __declspec(dllimport)
#endif
#ifdef _DLL_FDRRDEX
   #define IM_EX_FDRRDEX       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_FDRRDEX
#else
   #define IM_EX_FDRRDEX       __declspec(dllimport)
#endif
#ifdef _DLL_FDRDSM2IMPORT
   #define IM_EX_FDRDSM2IMPORT       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_FDRDSM2IMPORT
#else
   #define IM_EX_FDRDSM2IMPORT       __declspec(dllimport)
#endif
#ifdef _DLL_FDRTELEGYRAPI
   #define IM_EX_FDRTELEGYRAPI       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_FDRTELEGYRAPI
#else
   #define IM_EX_FDRTELEGYRAPI       __declspec(dllimport)
#endif
#ifdef _DLL_FDRPIBASEAPI
   #define IM_EX_FDRPIBASEAPI       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_FDRPIBASEAPI
#else
   #define IM_EX_FDRPIBASEAPI       __declspec(dllimport)
#endif
#ifdef _DLL_FDRXA21LM
   #define IM_EX_FDRXA21LM       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_FDRXA21LM
#else
   #define IM_EX_FDRXA21LM       __declspec(dllimport)
#endif
#ifdef _DLL_FDRTEXTIMPORT
   #define IM_EX_FDRTEXTIMPORT       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_FDRTEXTIMPORT
#else
   #define IM_EX_FDRTEXTIMPORT       __declspec(dllimport)
#endif
#ifdef _DLL_FDRTEXTEXPORT
   #define IM_EX_FDRTEXTEXPORT       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_FDRTEXTEXPORT
#else
   #define IM_EX_FDRTEXTEXPORT       __declspec(dllimport)
#endif
#ifdef _DLL_ENH_FDRLODESTARIMPORT
   #define IM_EX_ENH_FDRLODESTARIMPORT       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_ENH_FDRLODESTARIMPORT
#else
   #define IM_EX_ENH_FDRLODESTARIMPORT       __declspec(dllimport)
#endif 
#ifdef _DLL_STD_FDRLODESTARIMPORT
   #define IM_EX_STD_FDRLODESTARIMPORT       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_STD_FDRLODESTARIMPORT
#else
   #define IM_EX_FDRLODESTARIMPORT       __declspec(dllimport)
#endif
#ifdef _DLL_FDRDSM2FILEIN
   #define IM_EX_FDRDSM2FILEIN       __declspec(dllexport)
#elif defined( STI_UNDERSTAND )
   #define IM_EX_FDRDSM2FILEIN
#else
   #define IM_EX_FDRDSM2FILEIN       __declspec(dllimport)
#endif










#endif   // #ifndef __DLLDEFS_H__
