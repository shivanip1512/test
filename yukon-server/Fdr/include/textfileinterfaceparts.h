#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: textfileinterfaceparts.h
*
*    DATE: 04/14/2003
*
*    AUTHOR: David Sutton
*
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/textfileinterfaceparts.cpp-arc  $
*    REVISION     :  $Revision: 1.2 $
*    DATE         :  $Date: 2003/04/22 20:44:47 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Contains information useful to all text file based interfaces
*
*    DESCRIPTION: 
*
*    ---------------------------------------------------
*    History: 
      $Log: textfileinterfaceparts.h,v $
      Revision 1.2  2003/04/22 20:44:47  dsutton
      Interfaces FDRTextExport and FDRTextImport and all the pieces needed
      to make them compile and work

****************************************************************************
*/
#ifndef __TEXTFILEINTERFACEPARTS_H__
#define __TEXTFILEINTERFACEPARTS_H__

#include <windows.h>    
#include "dlldefs.h"

class IM_EX_FDRBASE CtiFDRTextFileInterfaceParts 
{
    public:
        // constructors and destructors
        CtiFDRTextFileInterfaceParts(RWCString &aFileName=RWCString ("yukon.txt"), RWCString &aPath=RWCString("\\yukon\\server\\import"), int aInterval=900); 
        virtual ~CtiFDRTextFileInterfaceParts();
    
        RWCString & getFileName();
        RWCString  getFileName() const;
        CtiFDRTextFileInterfaceParts &setFileName (RWCString aName);
    
        RWCString & getDriveAndPath();
        RWCString  getDriveAndPath() const;
        CtiFDRTextFileInterfaceParts &setDriveAndPath (RWCString aDriveAndPath);

        int getInterval() const;
        CtiFDRTextFileInterfaceParts &setInterval (int aInterval);
    
    private:
        RWCString      _fileName;
        RWCString      _driveAndPath;
        int            _interval;
};

#endif  //  #ifndef __TEXTFILEINTERFACEPARTS_H




