#pragma warning( disable : 4786 )  
/*****************************************************************************
*
*    FILE NAME: fdrlodestarimport.cpp
*
*    DATE: 05/16/2003
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrlodestarimport.cpp-arc  $
*    REVISION     :  $Revision: 1.6 $
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
      $Log: fdrlodestarimport.h,v $
      Revision 1.6  2004/07/14 19:27:27  jrichter
      modified lodestar files to work when fdr is run on systems where yukon is not on c drive.

      Revision 1.5  2004/06/15 19:33:59  jrichter
      Added FDR lodestar tag point def / fixed time stamp issue / modified backup file to append time stamp

      Revision 1.4  2004/04/06 21:10:18  jrichter
      jrichter1 Lodestar changes to handle standard format and files are read in based on point parameters.

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
#include "fdrlodestarinfo.h"

class IM_EX_FDRBASE CtiFDR_LodeStarImportBase : public CtiFDRTextFileBase, CtiRTDB< CtiFDRPoint >
{
    typedef CtiFDRTextFileBase Inherited;

public:
    // constructors and destructors
    //CtiFDR_LodeStarImportBase(); 
    CtiFDR_LodeStarImportBase(RWCString &aInterface);
    
    virtual ~CtiFDR_LodeStarImportBase();
    virtual BOOL    init( void );   
    virtual BOOL    run( void );
    virtual BOOL    stop( void );  
    
    typedef CtiRTDBIterator CTIFdrLodeStarIterator;
    
    virtual vector<CtiFDR_LodeStarInfoTable> getFileInfoList() const = 0;
    virtual vector< CtiFDR_LodeStarInfoTable > & getFileInfoList () = 0;
        
    virtual RWCString getCustomerIdentifier(void)=0;
    virtual RWTime    getlodeStarStartTime(void)=0;
    virtual RWTime    getlodeStarStopTime(void)=0;
    virtual long       getlodeStarSecsPerInterval(void) = 0;
    virtual long       getlodeStarPointId(void) = 0;
    virtual void       reinitialize(void) = 0;
    virtual bool decodeFirstHeaderRecord(RWCString& aLine) = 0;
    virtual bool decodeSecondHeaderRecord(RWCString& aLine) = 0;
    virtual bool decodeThirdHeaderRecord(RWCString& aLine) = 0;
    virtual bool decodeFourthHeaderRecord(RWCString& aLine) = 0;
    virtual bool decodeDataRecord(RWCString& aLine, CtiMultiMsg* multiDispatchMsg) = 0;
    virtual const CHAR * getKeyInterval() = 0;
    virtual const CHAR * getKeyFilename() = 0;
    virtual const CHAR * getKeyImportDrivePath() = 0;
    virtual const RWCString& getFileImportBaseDrivePath() = 0;
    virtual const RWCString& setFileImportBaseDrivePath(RWCString importBase) = 0;

    virtual const CHAR * getKeyDBReloadRate() = 0;
    virtual const CHAR * getKeyQueueFlushRate() = 0;
    virtual const CHAR * getKeyDeleteFile() = 0;
    virtual const CHAR * getKeyRenameSave() = 0;
    virtual int getSubtractValue() = 0;
    virtual int getExpectedNumOfEntries() = 0;
    

    int readConfig( void );

    const char * getIntervalKey();
    USHORT ForeignToYukonQuality (RWCString aQuality);
    RWTime ForeignToYukonTime (RWCString aTime, CHAR aDstFlag);

    
    bool fillUpMissingTimeStamps(CtiMultiMsg* multiDispatchMsg,const RWTime& savedStartTime,const RWTime& savedStopTime,long lsSecondsPerInterval);

    bool shouldDeleteFileAfterImport() const;
    CtiFDR_LodeStarImportBase &setDeleteFileAfterImport (bool aFlag);

    bool shouldRenameSaveFileAfterImport() const;
    CtiFDR_LodeStarImportBase &setRenameSaveFileAfterImport (bool aFlag);

    bool validateAndDecodeLine( RWCString &input, CtiMessage **aRetMsg);

    void threadFunctionReadFromFile( void );
    virtual bool loadTranslationLists(void);

private:
    RWThreadFunction    _threadReadFromFile;
    bool                _deleteFileAfterImportFlag;
    bool                _renameSaveFileAfterImportFlag;

};



#endif  //  #ifndef  __FDRLODESTARIMPORT_H__


