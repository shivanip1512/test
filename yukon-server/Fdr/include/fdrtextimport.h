#pragma warning( disable : 4786 )  
/*****************************************************************************
*
*    FILE NAME: fdrtextimport.cpp
*
*    DATE: 04/11/2003
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/fdrtextimport.cpp-arc  $
*    REVISION     :  $Revision: 1.2 $
*    DATE         :  $Date: 2003/04/22 20:44:47 $
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
      Revision 1.2  2003/04/22 20:44:47  dsutton
      Interfaces FDRTextExport and FDRTextImport and all the pieces needed
      to make them compile and work

****************************************************************************
*/

#ifndef __FDRTEXTIMPORT_H__
#define __FDRTEXTIMPORT_H__


#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this

#include "dlldefs.h"
#include "fdrtextfilebase.h"

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
                           RWTime aTimestamp, 
                           int aQuality,
                           RWCString aTranslationName,
                           CtiMessage **aRetMsg);
    USHORT ForeignToYukonQuality (RWCString aQuality);
    RWTime ForeignToYukonTime (RWCString aTime, CHAR aDstFlag);

    bool processFunctionOne (RWCString &aLine, CtiMessage **aRetMsg);

    bool shouldDeleteFileAfterImport() const;
    CtiFDR_TextImport &setDeleteFileAfterImport (bool aFlag);

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

private:
    RWThreadFunction    _threadReadFromFile;
    bool                _deleteFileAfterImportFlag;
};



#endif  //  #ifndef __FDR_STEC_H__

