#pragma warning( disable : 4786 )
/*****************************************************************************
*
*    FILE NAME: fdrlodestarimport.cpp
*
*    DATE: 05/16/2003
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrlodestarimport.cpp-arc  $
*    REVISION     :  $Revision: 1.16.2.2 $
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
      $Log: fdrlodestarimport.h,v $
      Revision 1.16  2008/10/02 23:57:15  tspar
      YUK-5013 Full FDR reload should not happen with every point

      YUKRV-325  review changes

      Revision 1.15  2008/09/23 15:15:22  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Review changes. Most notable is mgr_fdrpoint.cpp now encapsulates CtiSmartMap instead of extending from rtdb.

      Revision 1.14  2008/09/15 21:09:16  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Changed interfaces to handle points on an individual basis so they can be added
      and removed by point id.

      Changed the fdr point manager to use smart pointers to help make this transition possible.

      Revision 1.13  2008/08/13 22:42:52  jrichter
      YUK-3163
      FDR doesn't log reason for failure to import LSE data

      Revision 1.12  2007/07/17 16:53:39  jrichter
      YUK-3163
      FDR doesn't log reason for failure to import LSE data

      Revision 1.11  2006/04/24 14:47:33  tspar
      RWreplace: replacing a few missed or new Rogue Wave elements

      Revision 1.10  2006/03/02 23:03:19  tspar
      Phase Three: Final  phase of RWTPtrSlist replacement.

      Revision 1.9  2005/12/20 17:17:16  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.8  2005/08/17 17:42:48  jrichter
      Merged  changes from 3.1.  handled massive point data with list of multimsg.  handled white space in data record for optional interval time field, handled massively long file format (extended workbuffer to 1500 bytes)
      Revision 1.7.4.2  2005/08/12 19:53:47  jliu
      Date Time Replaced

      Revision 1.7.4.1  2005/07/12 21:08:38  jliu
      rpStringWithoutCmpParser

      Revision 1.7  2004/08/18 21:46:01  jrichter
      1.  Added try{} catch(..) blocks to threadReadFromFile function to try and pinpoint where thread was killed.
      2.  Cleared out fileInfoList to get a fresh list of files upon each loadTranslationList call (so files aren't read once the point they reference is deleted from database).
      3.  Added path/filename to translationName, so points located in duplicate files (with different names) are not reprocessed and sent multiple times.

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



#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "dlldefs.h"
#include "fdrtextfilebase.h"
#include "fdrlodestarinfo.h"
#include "rtdb.h"
#include <list>
using std::list;

class IM_EX_FDRBASE CtiFDR_LodeStarImportBase : public CtiFDRTextFileBase, CtiRTDB< CtiFDRPoint >
{
    typedef CtiFDRTextFileBase Inherited;

public:
    // constructors and destructors
    //CtiFDR_LodeStarImportBase();
    CtiFDR_LodeStarImportBase(string &aInterface);

    virtual ~CtiFDR_LodeStarImportBase();
    virtual BOOL    init( void );
    virtual BOOL    run( void );
    virtual BOOL    stop( void );

    typedef MapIterator CTIFdrLodeStarIterator;

    virtual vector<CtiFDR_LodeStarInfoTable> getFileInfoList() const = 0;
    virtual vector< CtiFDR_LodeStarInfoTable > & getFileInfoList () = 0;

    virtual string getCustomerIdentifier(void)=0;
    virtual CtiTime    getlodeStarStartTime(void)=0;
    virtual CtiTime    getlodeStarStopTime(void)=0;
    virtual long       getlodeStarSecsPerInterval(void) = 0;
    virtual long       getlodeStarPointId(void) = 0;
    virtual void       reinitialize(void) = 0;
    virtual bool decodeFirstHeaderRecord(string& aLine, int fileIndex) = 0;
    virtual bool decodeSecondHeaderRecord(string& aLine) = 0;
    virtual bool decodeThirdHeaderRecord(string& aLine) = 0;
    virtual bool decodeFourthHeaderRecord(string& aLine) = 0;
    virtual bool decodeDataRecord(string& aLine, CtiMultiMsg* multiDispatchMsg) = 0;
    virtual const CHAR * getKeyInterval() = 0;
    virtual const CHAR * getKeyFilename() = 0;
    virtual const CHAR * getKeyImportDrivePath() = 0;
    virtual const string& getFileImportBaseDrivePath() = 0;
    virtual const string& setFileImportBaseDrivePath(string importBase) = 0;

    virtual const CHAR * getKeyDBReloadRate() = 0;
    virtual const CHAR * getKeyQueueFlushRate() = 0;
    virtual const CHAR * getKeyDeleteFile() = 0;
    virtual const CHAR * getKeyRenameSave() = 0;
    virtual int getSubtractValue() = 0;
    virtual int getExpectedNumOfEntries() = 0;


    int readConfig( void );

    const char * getIntervalKey();
    USHORT ForeignToYukonQuality (string aQuality);
    CtiTime ForeignToYukonTime (string aTime, CHAR aDstFlag);

    bool fillUpMissingTimeStamps(CtiMultiMsg* multiDispatchMsg, list< CtiMultiMsg* > &dispatchList, const CtiTime& savedStartTime,const CtiTime& savedStopTime,long stdLsSecondsPerInterval, string savedCustomerIdentifier, string FileName);

    bool shouldDeleteFileAfterImport() const;
    CtiFDR_LodeStarImportBase &setDeleteFileAfterImport (bool aFlag);

    bool shouldRenameSaveFileAfterImport() const;
    CtiFDR_LodeStarImportBase &setRenameSaveFileAfterImport (bool aFlag);

    bool validateAndDecodeLine( string &input, CtiMessage **aRetMsg);

    void threadFunctionReadFromFile( void );
    virtual bool loadTranslationLists(void);
    virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList = false);

private:
    RWThreadFunction    _threadReadFromFile;
    bool                _deleteFileAfterImportFlag;
    bool                _renameSaveFileAfterImportFlag;

};



#endif  //  #ifndef  __FDRLODESTARIMPORT_H__


