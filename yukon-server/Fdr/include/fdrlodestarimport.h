#pragma warning( disable : 4786 )  
/*****************************************************************************
*
*    FILE NAME: fdrlodestarimport.cpp
*
*    DATE: 05/16/2003
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrlodestarimport.cpp-arc  $
*    REVISION     :  $Revision: 1.3 $
*    DATE         :  $Date: 2003/07/18 21:46:16 $
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
      $Log: fdrlodestarimport.h,v $
      Revision 1.3  2003/07/18 21:46:16  jwolberg
      Fixes based on answers to questions asked of Xcel.

      Revision 1.2  2003/06/09 16:14:21  jwolberg
      Added FDR LodeStar interface.


****************************************************************************
*/

#ifndef __FDRLODESTARIMPORT_H__
#define __FDRLODESTARIMPORT_H__


#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this

#include "dlldefs.h"
#include "fdrtextfilebase.h"

class IM_EX_FDRLODESTARIMPORT CtiFDR_LodeStarImport : public CtiFDRTextFileBase
{
    typedef CtiFDRTextFileBase Inherited;

public:
    // constructors and destructors
    CtiFDR_LodeStarImport(); 

    virtual ~CtiFDR_LodeStarImport();
    virtual BOOL    init( void );   
    virtual BOOL    run( void );
    virtual BOOL    stop( void );

    int readConfig( void );

    USHORT ForeignToYukonQuality (RWCString aQuality);
    RWTime ForeignToYukonTime (RWCString aTime, CHAR aDstFlag);

    bool decodeFirstHeaderRecord(RWCString& aLine,RWCString& lsCustomerIdentifier,long& pointId,long& lsChannel,RWTime& lsStartTime,
                                 RWTime& lsStopTime,RWCString& lsDSTFlag,RWCString& lsInvalidRecordFlag);
    bool decodeSecondHeaderRecord(RWCString& aLine, double& lsMeterStartReading, double& lsMeterStopReading,
                                  double& lsMeterMultiplier, double& lsMeterOffset, double& lsPulseMultiplier, double& lsPulseOffset,
                                  long& lsSecondsPerInterval, long& lsUnitOfMeasure, long& lsBasicUnitCode, long& lsTimeZone,
                                  double& lsPopulation, double& lsWeight);
    bool decodeThirdHeaderRecord(RWCString& aLine, RWCString& lsDescriptor);
    bool decodeFourthHeaderRecord(RWCString& aLine, RWTime& lsTimeStamp, RWCString& lsOrigin);
    bool decodeDataRecord(RWCString& aLine, long pointId, double lsMeterMultiplier, double lsMeterOffset, double lsPulseMultiplier, double lsPulseOffset, CtiMultiMsg* multiDispatchMsg);

    bool fillUpMissingTimeStamps(CtiMultiMsg* multiDispatchMsg,const RWTime& savedStartTime,const RWTime& savedStopTime,long lsSecondsPerInterval);

    bool shouldDeleteFileAfterImport() const;
    CtiFDR_LodeStarImport &setDeleteFileAfterImport (bool aFlag);

    bool shouldRenameSaveFileAfterImport() const;
    CtiFDR_LodeStarImport &setRenameSaveFileAfterImport (bool aFlag);

    bool validateAndDecodeLine( RWCString &input, CtiMessage **aRetMsg);

    void threadFunctionReadFromFile( void );
    virtual bool loadTranslationLists(void);
    // ddefine these for each interface type
    static const CHAR * KEY_INTERVAL;
    static const CHAR * KEY_FILENAME;
    static const CHAR * KEY_DRIVE_AND_PATH;
    static const CHAR * KEY_DB_RELOAD_RATE;
    static const CHAR * KEY_QUEUE_FLUSH_RATE;
    static const CHAR * KEY_DELETE_FILE;
    static const CHAR * KEY_RENAME_SAVE_FILE;

private:
    RWThreadFunction    _threadReadFromFile;
    bool                _deleteFileAfterImportFlag;
    bool                _renameSaveFileAfterImportFlag;
};



#endif  //  #ifndef __FDR_STEC_H__

