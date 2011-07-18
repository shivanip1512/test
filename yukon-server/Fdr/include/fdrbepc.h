/*****************************************************************************
*
*    FILE NAME: fdrbepc.h
*
*    DATE: 04/11/2003
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrbepc.h-arc  $
*    REVISION     :  $Revision: 1.6.2.2 $
*    DATE         :  $Date: 2008/11/18 20:11:30 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Text file export for Basin Electric Public Coop members
*
****************************************************************************
*/

#ifndef __FDRBEPC_H__
#define __FDRBEPC_H__



#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "dlldefs.h"
#include "fdrtextfilebase.h"

class IM_EX_FDRBEPC CtiFDR_BEPC : public CtiFDRTextFileBase
{
    typedef CtiFDRTextFileBase Inherited;

public:
    // constructors and destructors
    CtiFDR_BEPC();

    virtual ~CtiFDR_BEPC();
    virtual BOOL    init( void );
    virtual BOOL    run( void );
    virtual BOOL    stop( void );

    int readConfig( void );
    bool sendMessageToForeignSys ( CtiMessage *aMessage );

    std::string YukonToForeignTime (CtiTime aTime);
    CHAR YukonToForeignQuality (USHORT aQuality);
    CHAR YukonToForeignDST (bool aFlag);

    bool shouldAppendToFile() const;
    CtiFDR_BEPC &setAppendToFile (bool aFlag);

    void threadFunctionWriteToFile( void );
    virtual bool loadTranslationLists(void);
    virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList = false);
    virtual void cleanupTranslationPoint(CtiFDRPointSPtr &translationPoint, bool recvList);

    // ddefine these for each interface type
    static const CHAR * KEY_INTERVAL;
    static const CHAR * KEY_DRIVE_AND_PATH;
    static const CHAR * KEY_DB_RELOAD_RATE;
    static const CHAR * KEY_QUEUE_FLUSH_RATE;
    static const CHAR * KEY_APPEND_FILE;

private:
    RWThreadFunction _threadWriteToFile;
    bool _appendFlag;
    std::map<std::string,std::string> coopIdToFileName;
};

#endif


