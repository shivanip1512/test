#pragma once

#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "dlldefs.h"
#include "fdrtextfilebase.h"

class IM_EX_FDRDSM2FILEIN CtiFDR_Dsm2Filein : public CtiFDRTextFileBase
{
    typedef CtiFDRTextFileBase Inherited;

public:
    // constructors and destructors
    CtiFDR_Dsm2Filein();

    virtual ~CtiFDR_Dsm2Filein();
    virtual BOOL    init( void );
    virtual BOOL    run( void );
    virtual BOOL    stop( void );

    bool readConfig() override;
    bool buildAndAddPoint (CtiFDRPoint &aPoint,
                           DOUBLE aValue,
                           CtiTime aTimestamp,
                           int aQuality,
                           std::string aTranslationName,
                           CtiMessage **aRetMsg);
    USHORT ForeignToYukonQuality (std::string aQuality);
    CtiTime ForeignToYukonTime (std::string aTime);

    bool processFunctionOne (std::string &aLine, CtiMessage **aRetMsg);
    bool processFunctionTwo (std::string &aLine, CtiMessage **aRetMsg);

    bool shouldDeleteFileAfterImport() const;
    CtiFDR_Dsm2Filein &setDeleteFileAfterImport (bool aFlag);

    bool useSystemTime() const;
    CtiFDR_Dsm2Filein &setUseSystemTime (bool aFlag);

    bool validateAndDecodeLine( std::string &input, CtiMessage **aRetMsg);

    void threadFunctionReadFromFile( void );
    virtual bool loadTranslationLists(void);
    virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList = false);

    // ddefine these for each interface type
    static const CHAR * KEY_INTERVAL;
    static const CHAR * KEY_FILENAME;
    static const CHAR * KEY_DRIVE_AND_PATH;
    static const CHAR * KEY_DB_RELOAD_RATE;
    static const CHAR * KEY_QUEUE_FLUSH_RATE;
    static const CHAR * KEY_DELETE_FILE;
    static const CHAR * KEY_USE_SYSTEM_TIME;

private:
    RWThreadFunction    _threadReadFromFile;
    bool                _deleteFileAfterImportFlag;
    bool                _useSystemTime;
};
