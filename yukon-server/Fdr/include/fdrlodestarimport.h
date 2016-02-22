#pragma once

#include <windows.h>

#include "dlldefs.h"
#include "fdrtextfilebase.h"
#include "fdrlodestarinfo.h"
#include "rtdb.h"
#include <list>

class IM_EX_FDRBASE CtiFDR_LodeStarImportBase : public CtiFDRTextFileBase, CtiRTDB< CtiFDRPoint >
{
    typedef CtiFDRTextFileBase Inherited;

public:
    DEBUG_INSTRUMENTATION

    // constructors and destructors
    //CtiFDR_LodeStarImportBase();
    CtiFDR_LodeStarImportBase(std::string &aInterface);

    virtual ~CtiFDR_LodeStarImportBase();
    virtual BOOL    init( void );
    virtual BOOL    run( void );
    virtual BOOL    stop( void );

    typedef MapIterator CTIFdrLodeStarIterator;

    virtual std::vector<CtiFDR_LodeStarInfoTable> getFileInfoList() const = 0;
    virtual std::vector< CtiFDR_LodeStarInfoTable > & getFileInfoList () = 0;

    virtual std::string getCustomerIdentifier(void)=0;
    virtual CtiTime     getlodeStarStartTime(void)=0;
    virtual CtiTime     getlodeStarStopTime(void)=0;
    virtual long        getlodeStarSecsPerInterval(void) = 0;
    virtual long        getlodeStarPointId(void) = 0;
    virtual void        reinitialize(void) = 0;
    virtual bool        decodeFirstHeaderRecord(std::string& aLine, int fileIndex) = 0;
    virtual bool        decodeSecondHeaderRecord(std::string& aLine) = 0;
    virtual bool        decodeThirdHeaderRecord(std::string& aLine) = 0;
    virtual bool        decodeFourthHeaderRecord(std::string& aLine) = 0;
    virtual bool        decodeDataRecord(std::string& aLine, CtiMultiMsg* multiDispatchMsg) = 0;
    virtual const CHAR * getKeyInterval() = 0;
    virtual const CHAR * getKeyFilename() = 0;
    virtual const CHAR * getKeyImportDrivePath() = 0;
    virtual const std::string& getFileImportBaseDrivePath() = 0;
    virtual const std::string& setFileImportBaseDrivePath(std::string importBase) = 0;

    virtual const CHAR * getKeyDBReloadRate() = 0;
    virtual const CHAR * getKeyQueueFlushRate() = 0;
    virtual const CHAR * getKeyDeleteFile() = 0;
    virtual const CHAR * getKeyRenameSave() = 0;
    virtual int getSubtractValue() = 0;
    virtual int getExpectedNumOfEntries() = 0;


    bool readConfig() override;

    const char * getIntervalKey();
    USHORT ForeignToYukonQuality (std::string aQuality);
    CtiTime ForeignToYukonTime (std::string aTime, CHAR aDstFlag);

    bool fillUpMissingTimeStamps(CtiMultiMsg* multiDispatchMsg, std::list< CtiMultiMsg* > &dispatchList, const CtiTime& savedStartTime,const CtiTime& savedStopTime,long stdLsSecondsPerInterval, std::string savedCustomerIdentifier, std::string FileName);

    bool shouldDeleteFileAfterImport() const;
    CtiFDR_LodeStarImportBase &setDeleteFileAfterImport (bool aFlag);

    bool shouldRenameSaveFileAfterImport() const;
    CtiFDR_LodeStarImportBase &setRenameSaveFileAfterImport (bool aFlag);

    bool validateAndDecodeLine( std::string &input, CtiMessage **aRetMsg);

    void threadFunctionReadFromFile( void );
    virtual bool loadTranslationLists(void);
    virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList = false);

private:
    Cti::WorkerThread   _threadReadFromFile;
    bool                _deleteFileAfterImportFlag;
    bool                _renameSaveFileAfterImportFlag;

};
