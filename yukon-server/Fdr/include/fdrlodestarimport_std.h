#pragma once

#include <windows.h>

#include "dlldefs.h"
#include "fdrtextfilebase.h"
#include "fdrlodestarinfo.h"

class IM_EX_STD_FDRLODESTARIMPORT CtiFDR_StandardLodeStar : public CtiFDR_LodeStarImportBase
{
    typedef CtiFDR_LodeStarImportBase Inherited;

public:
    DEBUG_INSTRUMENTATION;

    // constructors and destructors
    CtiFDR_StandardLodeStar();

    virtual BOOL    init( void );
    virtual BOOL    run( void );
    virtual BOOL    stop( void );

    virtual std::vector<CtiFDR_LodeStarInfoTable> getFileInfoList() const;
    virtual std::vector< CtiFDR_LodeStarInfoTable > & getFileInfoList ();

    virtual std::string getCustomerIdentifier(void);
    virtual CtiTime    getlodeStarStartTime(void);
    virtual CtiTime    getlodeStarStopTime(void);
    virtual long       getlodeStarSecsPerInterval(void);
    virtual long       getlodeStarPointId(void);
    virtual void       reinitialize(void);
    virtual bool decodeFirstHeaderRecord(std::string& aLine, int fileIndex);
    virtual bool decodeSecondHeaderRecord(std::string& aLine);
    virtual bool decodeThirdHeaderRecord(std::string& aLine);
    virtual bool decodeFourthHeaderRecord(std::string& aLine);
    virtual bool decodeDataRecord(std::string& aLine, CtiMultiMsg* multiDispatchMsg);
    virtual const CHAR * getKeyInterval();
    virtual const CHAR * getKeyFilename();
    virtual const CHAR * getKeyImportDrivePath();
    virtual const std::string& getFileImportBaseDrivePath();
    virtual const std::string& setFileImportBaseDrivePath(std::string importBase);
    virtual const CHAR * getKeyDBReloadRate();
    virtual const CHAR * getKeyQueueFlushRate();
    virtual const CHAR * getKeyDeleteFile();
    virtual const CHAR * getKeyRenameSave();
    virtual int getSubtractValue();
    virtual int getExpectedNumOfEntries();

    CtiTime ForeignToYukonTime (std::string aTime, CHAR aDstFlag);
    static const CHAR * KEY_INTERVAL;
    static const CHAR * KEY_FILENAME;
    static const CHAR * KEY_IMPORT_BASE_PATH;
    static const CHAR * KEY_DB_RELOAD_RATE;
    static const CHAR * KEY_QUEUE_FLUSH_RATE;
    static const CHAR * KEY_DELETE_FILE;
    static const CHAR * KEY_RENAME_SAVE_FILE;

private:
    //information obtained from the first header record
    std::string  _stdLsCustomerIdentifier;
    long        _pointId;//determined from the Customer Identifier
    long        _stdLsChannel;
    CtiTime     _stdLsStartTime;
    CtiTime     _stdLsStopTime;
    long        _stdLsIntervalsPerHour;
    long        _stdLsUnitOfMeasure;
    long        _stdLsAltFormat;
    std::string  _stdLsFiller;
    double        _stdLsSecondsPerInterval;//calculated value from intervals per hour

    //information obtained from the second header record
    double      _stdLsMeterStartReading;
    double      _stdLsMeterStopReading;
    double      _stdLsMeterMultiplier;
    double      _stdLsMeterOffset;
    double      _stdLsPulseMultiplier;
    double      _stdLsPulseOffset;

    //information obtained from the third header record
    std::string  _stdLsDescriptor;
    double      _stdLsAltPulseMultiplier;
    double      _stdLsPopulation;
    double      _stdLsWeight;


    std::string _fileImportBaseDrivePath;

    int _stdLsExpectedNumEntries;

    std::vector <CtiFDR_LodeStarInfoTable> _fileInfoList;
};
