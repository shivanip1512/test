#pragma warning( disable : 4786 )  
/*****************************************************************************
*
*    FILE NAME: fdrlodestarimport.h
*
*    DATE: 03/22/2004
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrlodestarimport.cpp-arc  $
*    REVISION     :  $Revision: 1.3 $
*    DATE         :  $Date: 2004/07/14 19:27:27 $
*
*
*    AUTHOR: Josh Wolberg
*
*    PURPOSE: Import of ascii lodestar import
*
*    DESCRIPTION: 
*
*    ---------------------------------------------------
*    History: 
      $Log: fdrlodestarimport_enh.h,v $
      Revision 1.3  2004/07/14 19:27:27  jrichter
      modified lodestar files to work when fdr is run on systems where yukon is not on c drive.

      Revision 1.2  2004/06/15 19:34:00  jrichter
      Added FDR lodestar tag point def / fixed time stamp issue / modified backup file to append time stamp

      Revision 1.1  2004/04/06 21:10:18  jrichter
      jrichter1 Lodestar changes to handle standard format and files are read in based on point parameters.

      Revision 1.3  2003/07/18 21:46:16  jwolberg


****************************************************************************
*/

#ifndef __FDRLODESTARIMPORT_ENH_H__
#define __FDRLODESTARIMPORT_ENH_H__


#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this

#include "dlldefs.h"
#include "fdrtextfilebase.h"
#include "fdrlodestarinfo.h"

class IM_EX_ENH_FDRLODESTARIMPORT CtiFDR_EnhancedLodeStar : public CtiFDR_LodeStarImportBase
{
    typedef CtiFDR_LodeStarImportBase Inherited;

public:
    // constructors and destructors
    CtiFDR_EnhancedLodeStar(); 

    virtual ~CtiFDR_EnhancedLodeStar();
    virtual BOOL    init( void );   
    virtual BOOL    run( void );
    virtual BOOL    stop( void );

    virtual vector<CtiFDR_LodeStarInfoTable> getFileInfoList() const;
    virtual vector< CtiFDR_LodeStarInfoTable > & getFileInfoList ();

    //virtual CtiFDR_EnhancedLodeStar& setFileInfoList  (CtiFDR_EnhancedLodeStar &aList);
    //virtual CtiFDR_EnhancedLodeStar& setFileInfoList (vector< CtiFDR_LodeStarInfoTable > &aList);

    virtual RWCString getCustomerIdentifier(void);
    virtual RWTime    getlodeStarStartTime(void);
    virtual RWTime    getlodeStarStopTime(void);
    virtual long       getlodeStarSecsPerInterval(void);
    virtual long       getlodeStarPointId(void);
    virtual void       reinitialize(void);
    virtual bool decodeFirstHeaderRecord(RWCString& aLine);
    virtual bool decodeSecondHeaderRecord(RWCString& aLine);
    virtual bool decodeThirdHeaderRecord(RWCString& aLine);
    virtual bool decodeFourthHeaderRecord(RWCString& aLine);
    virtual bool decodeDataRecord(RWCString& aLine, CtiMultiMsg* multiDispatchMsg);
    virtual const CHAR * getKeyInterval();
    virtual const CHAR * getKeyFilename();
    virtual const CHAR * getKeyImportDrivePath();
    virtual const RWCString& getFileImportBaseDrivePath();
    virtual const RWCString& setFileImportBaseDrivePath(RWCString importBase);
    virtual const CHAR * getKeyDBReloadRate();
    virtual const CHAR * getKeyQueueFlushRate();
    virtual const CHAR * getKeyDeleteFile();
    virtual const CHAR * getKeyRenameSave();
    virtual int getSubtractValue();
    virtual int getExpectedNumOfEntries();

    RWTime ForeignToYukonTime (RWCString aTime, CHAR aDstFlag);

    static const CHAR * KEY_INTERVAL;
    static const CHAR * KEY_FILENAME;
    static const CHAR * KEY_IMPORT_BASE_PATH;
    static const CHAR * KEY_DB_RELOAD_RATE;
    static const CHAR * KEY_QUEUE_FLUSH_RATE;
    static const CHAR * KEY_DELETE_FILE;
    static const CHAR * KEY_RENAME_SAVE_FILE;
private:

    //information obtained from the first header record
    RWCString  _lsCustomerIdentifier;
    long        _pointId;   //determined from the Customer Identifier
    long        _lsChannel;
    RWTime     _lsStartTime;
    RWTime     _lsStopTime;
    RWCString  _lsDSTFlag;
    RWCString  _lsInvalidRecordFlag;
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
    RWCString _lsDescriptor;
    RWTime     _lsTimeStamp;
    RWCString  _lsOrigin;

    RWCString _fileImportBaseDrivePath;

    int _lsExpectedNumEntries;

    vector <CtiFDR_LodeStarInfoTable> _fileInfoList;

};

#endif  //  #ifndef __FDRLODESTARIMPORT_ENH_H__

