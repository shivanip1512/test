#pragma once

#define DLLEXPORT             __declspec(dllexport)
#define DLLIMPORT             __declspec(dllimport)

#ifdef _DLL_CTIPIL
   #define IM_EX_CTIPIL       __declspec(dllexport)
#else
   #define IM_EX_CTIPIL       __declspec(dllimport)
#endif

#ifdef CTISVR                  // The ctisvr.dll
   #define IM_EX_CTISVR       __declspec(dllexport)
#else
   #define IM_EX_CTISVR       __declspec(dllimport)
#endif

#ifdef CTIBASE                // The ctibase.dll
   #define IM_EX_CTIBASE      __declspec(dllexport)
#else
   #define IM_EX_CTIBASE      __declspec(dllimport)
#endif

#ifdef _DLL_SERVICE
   #define IM_EX_SERVICE __declspec(dllexport)
#else
   #define IM_EX_SERVICE __declspec(dllimport)
#endif

#ifdef _DLL_MESSAGE
   #define IM_EX_MSG __declspec(dllexport)
#else
   #define IM_EX_MSG __declspec(dllimport)
#endif

#ifdef CTIYUKONDB                // The ctidb.dll
   #define IM_EX_CTIYUKONDB   __declspec(dllexport)
#else
   #define IM_EX_CTIYUKONDB   __declspec(dllimport)
#endif

#ifdef _DLL_DEVDB                // The ctidb.dll
   #define IM_EX_DEVDB   __declspec(dllexport)
#else
   #define IM_EX_DEVDB   __declspec(dllimport)
#endif

#ifdef _DLL_PNTDB                // The ctidb.dll
   #define IM_EX_PNTDB   __declspec(dllexport)
#else
   #define IM_EX_PNTDB   __declspec(dllimport)
#endif

#ifdef _DLL_PRTDB                // The ctidb.dll
   #define IM_EX_PRTDB   __declspec(dllexport)
#else
   #define IM_EX_PRTDB   __declspec(dllimport)
#endif

#ifdef _DLL_HOLIDAYDB                // The ctiholiday.dll
   #define IM_EX_HOLIDAYDB   __declspec(dllexport)
#else
   #define IM_EX_HOLIDAYDB   __declspec(dllimport)
#endif

#ifdef _DLL_SEASONDB                // The ctiseason.dll
   #define IM_EX_SEASONDB   __declspec(dllexport)
#else
   #define IM_EX_SEASONDB   __declspec(dllimport)
#endif

#ifdef _DLL_HOLIDAYMGR
    #define IM_EX_DLL_HOLIDAYMGR __declspec(dllexport)
#else
    #define IM_EX_DLL_HOLIDAYMGR __declspec(dllimport)
#endif

#ifdef _DLL_SEASONMGR
    #define IM_EX_DLL_SEASONMGR __declspec(dllexport)
#else
    #define IM_EX_DLL_SEASONMGR __declspec(dllimport)
#endif

#ifdef _DLL_PORTGLOB                // The ctisvr.dll
   #define IM_EX_PORTGLOB       __declspec(dllexport)
#else
   #define IM_EX_PORTGLOB       __declspec(dllimport)
#endif

#ifdef _DLL_PROT
   #define IM_EX_PROT   __declspec(dllexport)
#else
   #define IM_EX_PROT   __declspec(dllimport)
#endif

#ifdef _DLL_CONFIG                // The ctibase.dll
   #define IM_EX_CONFIG      __declspec(dllexport)
#else
   #define IM_EX_CONFIG      __declspec(dllimport)
#endif

#ifdef _DLL_DYNPAOINFO                // The dynpaoinfo.dll
   #define IM_EX_DYNPAOINFO   __declspec(dllexport)
#else
   #define IM_EX_DYNPAOINFO   __declspec(dllimport)
#endif

#ifdef _DLL_RFN_E2E                // The rfn-e2e.dll
   #define IM_EX_RFN_E2E   __declspec(dllexport)
#else
   #define IM_EX_RFN_E2E   __declspec(dllimport)
#endif


#ifdef _DLL_FDRBASE
   #define IM_EX_FDRBASE       __declspec(dllexport)
#else
   #define IM_EX_FDRBASE       __declspec(dllimport)
#endif

#ifdef _DLL_FDRCYGNET
   #define IM_EX_FDRCYGNET       __declspec(dllexport)
#else
   #define IM_EX_FDRCYGNET       __declspec(dllimport)
#endif

#ifdef _DLL_FDRACS
   #define IM_EX_FDRACS       __declspec(dllexport)
#else
   #define IM_EX_FDRACS       __declspec(dllimport)
#endif

#ifdef _DLL_FDRACSMULTI
   #define IM_EX_FDRACSMULTI       __declspec(dllexport)
#else
   #define IM_EX_FDRACSMULTI       __declspec(dllimport)
#endif

#ifdef _DLL_FDRDNPSLAVE
   #define IM_EX_FDRDNPSLAVE       __declspec(dllexport)
#else
   #define IM_EX_FDRDNPSLAVE       __declspec(dllimport)
#endif

#ifdef _DLL_FDRVALMET
   #define IM_EX_FDRVALMET       __declspec(dllexport)
#else
   #define IM_EX_FDRVALMET       __declspec(dllimport)
#endif

#ifdef _DLL_FDRVALMETMULTI
   #define IM_EX_FDRVALMETMULTI       __declspec(dllexport)
#else
   #define IM_EX_FDRVALMETMULTI       __declspec(dllimport)
#endif

#ifdef _DLL_FDRINET
   #define IM_EX_FDRINET       __declspec(dllexport)
#else
   #define IM_EX_FDRINET       __declspec(dllimport)
#endif

#ifdef _DLL_FDRRCCS
   #define IM_EX_FDRRCCS       __declspec(dllexport)
#else
   #define IM_EX_FDRRCCS       __declspec(dllimport)
#endif

#ifdef _DLL_FDRTRISTATE
   #define IM_EX_FDRTRISTATE       __declspec(dllexport)
#else
   #define IM_EX_FDRTRISTATE       __declspec(dllimport)
#endif

#ifdef _DLL_FDRSTEC
   #define IM_EX_FDRSTEC       __declspec(dllexport)
#else
   #define IM_EX_FDRSTEC       __declspec(dllimport)
#endif
#ifdef _DLL_FDRRDEX
   #define IM_EX_FDRRDEX       __declspec(dllexport)
#else
   #define IM_EX_FDRRDEX       __declspec(dllimport)
#endif
#ifdef _DLL_FDRDSM2IMPORT
   #define IM_EX_FDRDSM2IMPORT       __declspec(dllexport)
#else
   #define IM_EX_FDRDSM2IMPORT       __declspec(dllimport)
#endif
#ifdef _DLL_FDRTELEGYRAPI
   #define IM_EX_FDRTELEGYRAPI       __declspec(dllexport)
#else
   #define IM_EX_FDRTELEGYRAPI       __declspec(dllimport)
#endif
#ifdef _DLL_FDRPIBASEAPI
   #define IM_EX_FDRPIBASEAPI       __declspec(dllexport)
#else
   #define IM_EX_FDRPIBASEAPI       __declspec(dllimport)
#endif
#ifdef _DLL_FDRLIVEDATAAPI
   #define IM_EX_FDRLIVEDATAAPI       __declspec(dllexport)
#else
   #define IM_EX_FDRLIVEDATAAPI       __declspec(dllimport)
#endif
#ifdef _DLL_FDRXA21LM
   #define IM_EX_FDRXA21LM       __declspec(dllexport)
#else
   #define IM_EX_FDRXA21LM       __declspec(dllimport)
#endif
#ifdef _DLL_FDRTEXTIMPORT
   #define IM_EX_FDRTEXTIMPORT       __declspec(dllexport)
#else
   #define IM_EX_FDRTEXTIMPORT       __declspec(dllimport)
#endif
#ifdef _DLL_FDRTEXTEXPORT
   #define IM_EX_FDRTEXTEXPORT       __declspec(dllexport)
#else
   #define IM_EX_FDRTEXTEXPORT       __declspec(dllimport)
#endif
#ifdef _DLL_ENH_FDRLODESTARIMPORT
   #define IM_EX_ENH_FDRLODESTARIMPORT       __declspec(dllexport)
#else
   #define IM_EX_ENH_FDRLODESTARIMPORT       __declspec(dllimport)
#endif
#ifdef _DLL_STD_FDRLODESTARIMPORT
   #define IM_EX_STD_FDRLODESTARIMPORT       __declspec(dllexport)
#else
   #define IM_EX_FDRLODESTARIMPORT       __declspec(dllimport)
#endif
#ifdef _DLL_FDRDSM2FILEIN
   #define IM_EX_FDRDSM2FILEIN       __declspec(dllexport)
#else
   #define IM_EX_FDRDSM2FILEIN       __declspec(dllimport)
#endif
#ifdef _DLL_FDRBEPC
   #define IM_EX_FDRBEPC       __declspec(dllexport)
#else
   #define IM_EX_FDRBEPC       __declspec(dllimport)
#endif

