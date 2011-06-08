#pragma warning( disable : 4786 )  
/*****************************************************************************
*
*    FILE NAME: fdrlodestarimport.cpp
*
*    DATE: 03/22/2004
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrlodestarimport.cpp-arc  $
*    REVISION     :  $Revision: 1.5.24.2 $
*    DATE         :  $Date: 2008/11/18 20:11:30 $
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
      Revision 1.5.24.2  2008/11/18 20:11:30  jmarks
      [YUKRV-525] Comment: YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008

      * Responded to reviewer comments
      * Changed monitor's version to MUTEX version
      * Other changes for compilation

      Revision 1.5.24.1  2008/11/13 17:23:46  jmarks
      YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008

      Responded to reviewer comments again.

      I eliminated excess references to windows.h .

      This still left over 100 references to it where "yukon.h" or "precompiled.h" was not obviously included.  Some other chaining of references could still be going on, and of course it is potentially possible that not all the files in the project that include windows.h actually need it - I didn't check for that.

      None-the-less, I than added the NOMINMAX define right before each place where windows.h is still included.
      Special note:  std::min<LONG>(TimeOut, 500); is still required for compilation.

      In this process I occasionally deleted a few empty lines, and when creating the define, also added some.

      This may not have affected every file in the project, but while mega-editing it certainly seemed like it did.


      Revision 1.5  2005/12/20 17:17:16  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.4.4.2  2005/08/12 19:53:47  jliu
      Date Time Replaced

      Revision 1.4.4.1  2005/07/12 21:08:39  jliu
      rpStringWithoutCmpParser

      Revision 1.4  2004/08/18 21:46:02  jrichter
      1.  Added try{} catch(..) blocks to threadReadFromFile function to try and pinpoint where thread was killed.
      2.  Cleared out fileInfoList to get a fresh list of files upon each loadTranslationList call (so files aren't read once the point they reference is deleted from database).
      3.  Added path/filename to translationName, so points located in duplicate files (with different names) are not reprocessed and sent multiple times.

      Revision 1.3  2004/07/14 19:27:27  jrichter
      modified lodestar files to work when fdr is run on systems where yukon is not on c drive.

      Revision 1.2  2004/06/15 19:34:00  jrichter
      Added FDR lodestar tag point def / fixed time stamp issue / modified backup file to append time stamp

      Revision 1.1  2004/04/06 21:10:18  jrichter
      jrichter1 Lodestar changes to handle standard format and files are read in based on point parameters.

      
****************************************************************************
*/

#ifndef __FDRLODESTARIMPORT_STD_H__
#define __FDRLODESTARIMPORT_STD_H__



#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>    

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

#endif  //  #ifndef __FDRLODESTARIMPORT_STD_H__


