#ifndef __FDRTEXTEXPORT_H__
#define __FDRTEXTEXPORT_H__



#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "dlldefs.h"
#include "fdrtextfilebase.h"

class IM_EX_FDRTEXTEXPORT CtiFDR_TextExport : public CtiFDRTextFileBase
{
    typedef CtiFDRTextFileBase Inherited;

public:
    // constructors and destructors
    CtiFDR_TextExport();

    enum {formatOne=1,
        survalent=100
    };

    virtual ~CtiFDR_TextExport();
    virtual BOOL    init( void );
    virtual BOOL    run( void );
    virtual BOOL    stop( void );

    int readConfig( void );
    bool sendMessageToForeignSys ( CtiMessage *aMessage );

    std::string YukonToForeignTime (CtiTime aTime);
    CHAR YukonToForeignQuality (USHORT aQuality);
    CHAR YukonToForeignDST (bool aFlag);

    bool shouldAppendToFile() const;
    CtiFDR_TextExport &setAppendToFile (bool aFlag);

    void threadFunctionWriteToFile( void );
    virtual bool loadTranslationLists(void);
    virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList = false);

    void processPointToSurvalent (FILE* aFilePtr, CtiFDRPointSPtr aPoint, CtiTime aTime);
    // ddefine these for each interface type
    static const CHAR * KEY_INTERVAL;
    static const CHAR * KEY_FILENAME;
    static const CHAR * KEY_DRIVE_AND_PATH;
    static const CHAR * KEY_DB_RELOAD_RATE;
    static const CHAR * KEY_QUEUE_FLUSH_RATE;
    static const CHAR * KEY_APPEND_FILE;
    static const CHAR * KEY_FORMAT;
private:
    RWThreadFunction    _threadWriteToFile;
    bool                _appendFlag;
    int                 _format;
};
#endif

