#pragma once

#include "dlldefs.h"
#include "fdrtextfilebase.h"
#include "textfileinterfaceparts.h"
#include <iostream>
#include <list>
#include <boost/tokenizer.hpp>

class IM_EX_FDRTEXTIMPORT CtiFDR_TextImport : public CtiFDRTextFileBase
{
    typedef CtiFDRTextFileBase Inherited;

public:
    DEBUG_INSTRUMENTATION;

    // constructors and destructors
    CtiFDR_TextImport();

    virtual ~CtiFDR_TextImport();
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
    USHORT ForeignToYukonQuality (char aQuality);
    CtiTime ForeignToYukonTime (std::string& aTime, CHAR aDstFlag);

    bool processFunctionOne (Tokenizer& cmdLine, CtiMessage **aRetMsg);

    bool shouldDeleteFileAfterImport() const;
    CtiFDR_TextImport &setDeleteFileAfterImport (bool aFlag);

    bool shouldRenameSaveFileAfterImport() const;
    CtiFDR_TextImport &setRenameSaveFileAfterImport (bool aFlag);

    bool validateAndDecodeLine( std::string& input, CtiMessage **aRetMsg);
    std::vector <CtiFDRTextFileInterfaceParts>* getFileInfoList() {return &_fileInfoList;};

    std::string& getFileImportBaseDrivePath();
    std::string& setFileImportBaseDrivePath(std::string importBase);

    std::list<std::string> parseFiles();
    std::list<std::string> getFileNames();

    bool moveFiles   ( std::list<std::string> &fileNames );
    bool moveFile   ( std::string fileName );
    bool deleteFiles ( std::list<std::string> &fileNames );
    bool deleteFile ( std::string fileName );
    void handleFilePostOp( std::string fileName );

    void threadFunctionReadFromFile( void );

    //Load all points
    virtual bool loadTranslationLists(void);
    virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList = false);

    //remove single point maintaining current lists
    virtual void cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList);

    // ddefine these for each interface type
    static const CHAR * KEY_INTERVAL;
    static const CHAR * KEY_FILENAME;
    static const CHAR * KEY_LEGACY_DRIVE_AND_PATH;
    static const CHAR * KEY_DB_RELOAD_RATE;
    static const CHAR * KEY_QUEUE_FLUSH_RATE;
    static const CHAR * KEY_DELETE_FILE;
    static const CHAR * KEY_POINTIMPORT_DEFAULT_PATH;
    static const CHAR * KEY_RENAME_SAVE_FILE;

    bool getLegacy();
    void setLegacy( bool val );

private:

    Cti::WorkerThread   _threadReadFromFile;
    bool                _deleteFileAfterImportFlag;
    bool _renameSaveFileAfterImportFlag;
    bool _legacyDrivePath;
    std::string _fileImportBaseDrivePath;

    std::vector <CtiFDRTextFileInterfaceParts> _fileInfoList;
    std::map<std::string,int> nameToPointId;
};
