#pragma once

#include <windows.h>

#include "dlldefs.h"
#include "fdrtextfilebase.h"
#include "fdrlodestarinfo.h"

class IM_EX_ENH_FDRLODESTARIMPORT CtiFDR_EnhancedLodeStar : public CtiFDR_LodeStarImportBase
{
    typedef CtiFDR_LodeStarImportBase Inherited;

public:
    DEBUG_INSTRUMENTATION;

    // constructors and destructors
    CtiFDR_EnhancedLodeStar();

    virtual BOOL    init( void );
    virtual BOOL    run( void );
    virtual BOOL    stop( void );

    virtual std::vector<CtiFDR_LodeStarInfoTable> getFileInfoList() const;
    virtual std::vector< CtiFDR_LodeStarInfoTable > & getFileInfoList ();

    //virtual CtiFDR_EnhancedLodeStar& setFileInfoList  (CtiFDR_EnhancedLodeStar &aList);
    //virtual CtiFDR_EnhancedLodeStar& setFileInfoList (vector< CtiFDR_LodeStarInfoTable > &aList);

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
    std::string  _lsCustomerIdentifier;
    long        _pointId;   //determined from the Customer Identifier
    long        _lsChannel;
    CtiTime     _lsStartTime;
    CtiTime     _lsStopTime;
    std::string  _lsDSTFlag;
    std::string  _lsInvalidRecordFlag;
    double  _lsMeterStartReading;
    double  _lsMeterStopReading;
    double  _lsMeterMultiplier;
    double  _lsMeterOffset;
    double  _lsPulseMultiplier;
    double  _lsPulseOffset;
    long    _lsSecondsPerInterval;
    long    _lsUnitOfMeasure;
    long    _lsBasicUnitCode;
    long    _lsTimeZone;
    double  _lsPopulation;
    double  _lsWeight;
    std::string _lsDescriptor;
    CtiTime     _lsTimeStamp;
    std::string  _lsOrigin;

    std::string _fileImportBaseDrivePath;

    int _lsExpectedNumEntries;

    std::vector <CtiFDR_LodeStarInfoTable> _fileInfoList;

};
