#pragma warning( disable : 4786 )  
/*****************************************************************************
*
*    FILE NAME: fdrtextimport.cpp
*
*    DATE: 04/11/2003
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrtextimport.cpp-arc  $
*    REVISION     :  $Revision: 1.6 $
*    DATE         :  $Date: 2008/03/20 21:27:14 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Generic text import ascii import
*
*    DESCRIPTION: 
*
*    ---------------------------------------------------
*    History: 
      $Log: fdrtextimport.h,v $
      Revision 1.6  2008/03/20 21:27:14  tspar
      YUK-5541 FDR Textimport and other interfaces incorrectly use the boost tokenizer.

      Changed all uses of the tokenizer to have a local copy of the string being tokenized.

      Revision 1.5  2007/08/30 17:03:39  tspar
      YUK-4318

      Fixed the way we read from files to be more efficient, and changed code flow to allow for more unit testing.

      Changed cparms to more show an intuitive difference between them.
      FDR_TEXTIMPORT_IMPORT_PATH is now
      FDR_TEXTIMPORT_DEFAULT_POINTIMPORT_PATH

      Changed the processe function that was causing a large memory leak over runtime.
      -Changed the code so it doesn't leak anymore, worked a long time trying to pin down the actual reason before just re-working the code.

      Changed the delete cparm to no longer default to true since we have two options, delete or rename.
      How it works now:
      1)specifying rename and delete will default to rename.
      2)not specifying either will default to delete.
      3)specifying one or the other will work as expected.

      Revision 1.4  2006/12/22 19:40:23  jrichter
      Bug Id: 716
      -check first character to see if it is digit, + or - sign before calling atof function
      -also merged thain's changes to head..

      Revision 1.3  2005/12/20 17:17:16  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.2.18.2  2005/08/12 19:53:48  jliu
      Date Time Replaced

      Revision 1.2.18.1  2005/07/12 21:08:39  jliu
      rpStringWithoutCmpParser

      Revision 1.2  2003/04/22 20:44:47  dsutton
      Interfaces FDRTextExport and FDRTextImport and all the pieces needed
      to make them compile and work

****************************************************************************
*/

#ifndef __FDRTEXTIMPORT_H__
#define __FDRTEXTIMPORT_H__


//#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this

#include "dlldefs.h"
#include "fdrtextfilebase.h"
#include "textfileinterfaceparts.h"
#include <iostream.h>
#include <list>
#include "ctistring.h"
#include <boost/tokenizer.hpp>

using std::list;

class IM_EX_FDRTEXTIMPORT CtiFDR_TextImport : public CtiFDRTextFileBase
{
    typedef CtiFDRTextFileBase Inherited;

public:
    // constructors and destructors
    CtiFDR_TextImport(); 

    virtual ~CtiFDR_TextImport();
    virtual BOOL    init( void );   
    virtual BOOL    run( void );
    virtual BOOL    stop( void );

    int readConfig( void );
    bool buildAndAddPoint (CtiFDRPoint &aPoint, 
                           DOUBLE aValue, 
                           CtiTime aTimestamp, 
                           int aQuality,
                           string aTranslationName,
                           CtiMessage **aRetMsg);
    USHORT ForeignToYukonQuality (char aQuality);
    CtiTime ForeignToYukonTime (string& aTime, CHAR aDstFlag);

    bool processFunctionOne (Tokenizer& cmdLine, CtiMessage **aRetMsg);

    bool shouldDeleteFileAfterImport() const;
    CtiFDR_TextImport &setDeleteFileAfterImport (bool aFlag);

    bool shouldRenameSaveFileAfterImport() const; 
    CtiFDR_TextImport &setRenameSaveFileAfterImport (bool aFlag); 
    
    bool validateAndDecodeLine( string& input, CtiMessage **aRetMsg);
    vector <CtiFDRTextFileInterfaceParts>* getFileInfoList() {return &_fileInfoList;}; 
    
    CtiString& getFileImportBaseDrivePath(); 
    CtiString& setFileImportBaseDrivePath(CtiString importBase); 

    list<string> parseFiles();
    list<string> getFileNames();

    bool moveFiles   ( std::list<string> &fileNames );
    bool moveFile   ( string fileName );
    bool deleteFiles ( std::list<string> &fileNames );
    bool deleteFile ( string fileName );
    void handleFilePostOp( string fileName );

    void threadFunctionReadFromFile( void );
    virtual bool loadTranslationLists(void);
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
    RWThreadFunction    _threadReadFromFile;
    bool                _deleteFileAfterImportFlag;
    bool _renameSaveFileAfterImportFlag; 
    bool _legacyDrivePath;
    CtiString _fileImportBaseDrivePath; 

    vector <CtiFDRTextFileInterfaceParts> _fileInfoList; 
    std::map<string,int> nameToPointId;
};



#endif  //  #ifndef __FDR_STEC_H__

