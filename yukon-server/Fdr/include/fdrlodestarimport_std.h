#pragma warning( disable : 4786 )  
/*****************************************************************************
*
*    FILE NAME: fdrlodestarimport.cpp
*
*    DATE: 03/22/2004
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrlodestarimport.cpp-arc  $
*    REVISION     :  $Revision: 1.1 $
*    DATE         :  $Date: 2004/04/06 21:10:18 $
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
      $Log: fdrlodestarimport_std.h,v $
      Revision 1.1  2004/04/06 21:10:18  jrichter
      jrichter1 Lodestar changes to handle standard format and files are read in based on point parameters.

      
****************************************************************************
*/

#ifndef __FDRLODESTARIMPORT_STD_H__
#define __FDRLODESTARIMPORT_STD_H__


#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this

#include "dlldefs.h"
#include "fdrtextfilebase.h"
#include "fdrlodestarinfo.h"

class IM_EX_STD_FDRLODESTARIMPORT CtiFDR_StandardLodeStar : public CtiFDR_LodeStarImportBase
{
    typedef CtiFDR_LodeStarImportBase Inherited;

public:
    // constructors and destructors
    CtiFDR_StandardLodeStar(); 

    virtual ~CtiFDR_StandardLodeStar();
    virtual BOOL    init( void );   
    virtual BOOL    run( void );
    virtual BOOL    stop( void );
    
    virtual vector<CtiFDR_LodeStarInfoTable> getFileInfoList() const;
    virtual vector< CtiFDR_LodeStarInfoTable > & getFileInfoList ();
   
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
    virtual const CHAR * getKeyDrivePath();
    virtual const CHAR * getKeyDBReloadRate();
    virtual const CHAR * getKeyQueueFlushRate();
    virtual const CHAR * getKeyDeleteFile();
    virtual const CHAR * getKeyRenameSave();
    virtual int getSubtractValue();


    RWTime ForeignToYukonTime (RWCString aTime, CHAR aDstFlag);
    static const CHAR * KEY_INTERVAL;
    static const CHAR * KEY_FILENAME;
    static const CHAR * KEY_DRIVE_AND_PATH;
    static const CHAR * KEY_DB_RELOAD_RATE;
    static const CHAR * KEY_QUEUE_FLUSH_RATE;
    static const CHAR * KEY_DELETE_FILE;
    static const CHAR * KEY_RENAME_SAVE_FILE;

private:
    //information obtained from the first header record
    RWCString  _stdLsCustomerIdentifier;
    long        _pointId;//determined from the Customer Identifier
    long        _stdLsChannel;
    RWTime     _stdLsStartTime;
    RWTime     _stdLsStopTime;
    long        _stdLsIntervalsPerHour;
    long        _stdLsUnitOfMeasure; 
    long        _stdLsAltFormat;
    RWCString  _stdLsFiller;
    double        _stdLsSecondsPerInterval;//calculated value from intervals per hour
        
    //information obtained from the second header record
    double      _stdLsMeterStartReading;
    double      _stdLsMeterStopReading;
    double      _stdLsMeterMultiplier;
    double      _stdLsMeterOffset;
    double      _stdLsPulseMultiplier;
    double      _stdLsPulseOffset;
                            
    //information obtained from the third header record
    RWCString  _stdLsDescriptor;
    double      _stdLsAltPulseMultiplier;
    double      _stdLsPopulation;
    double      _stdLsWeight;

    vector <CtiFDR_LodeStarInfoTable> _fileInfoList;
};

#endif  //  #ifndef __FDRLODESTARIMPORT_STD_H__


