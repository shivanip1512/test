#pragma once

#include <windows.h>

#include "dlldefs.h"
#include "fdrtextfilebase.h"

class IM_EX_FDRBEPC CtiFDR_BEPC : public CtiFDRTextFileBase
{
    typedef CtiFDRTextFileBase Inherited;

public:
    DEBUG_INSTRUMENTATION;

    // constructors and destructors
    CtiFDR_BEPC();

    virtual ~CtiFDR_BEPC();
    virtual BOOL    init( void );
    virtual BOOL    run( void );
    virtual BOOL    stop( void );

    bool readConfig() override;
    void sendMessageToForeignSys ( CtiMessage *aMessage );

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
    Cti::WorkerThread _threadWriteToFile;
    bool _appendFlag;
    std::map<std::string,std::string> coopIdToFileName;
};
