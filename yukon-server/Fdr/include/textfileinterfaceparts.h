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
*    REVISION     :  $Revision: 1.3 $
*    DATE         :  $Date: 2005/12/20 17:17:16 $
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
      Revision 1.3  2005/12/20 17:17:16  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.2.18.1  2005/07/12 21:08:39  jliu
      rpStringWithoutCmpParser

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
        CtiFDRTextFileInterfaceParts(string &aFileName=string ("yukon.txt"), string &aPath=string("\\yukon\\server\\import"), int aInterval=900); 
        virtual ~CtiFDRTextFileInterfaceParts();
    
        string & getFileName();
        string  getFileName() const;
        CtiFDRTextFileInterfaceParts &setFileName (string aName);
    
        string & getDriveAndPath();
        string  getDriveAndPath() const;
        CtiFDRTextFileInterfaceParts &setDriveAndPath (string aDriveAndPath);

        int getInterval() const;
        CtiFDRTextFileInterfaceParts &setInterval (int aInterval);
    
    private:
        string      _fileName;
        string      _driveAndPath;
        int            _interval;
};

#endif  //  #ifndef __TEXTFILEINTERFACEPARTS_H




