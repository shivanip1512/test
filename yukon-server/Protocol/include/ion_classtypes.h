#pragma warning( disable : 4786 )

#ifndef __ION_CLASSTYPES_H__
#define __ION_CLASSTYPES_H__

/*-----------------------------------------------------------------------------*
 *
 * File:   ion_classtypes.h
 *
 * Class:  CtiIONClass, CtiIONRegister, CtiIONModule, CtiIONManagerModule
 * Date:   07/06/2001
 *
 * Author: Matthew Fisher
 *
 *         ION manager/module/register classes for the ION serial protocol
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include <vector>

using namespace std;

class CtiIONClassType
{
public:

    CtiIONClassType( ) { };
    ~CtiIONClassType( ) { };

    void setHandle( int handle );
    int getHandle( void );
    int getParentHandle( void );

protected:

    int _handle;
};



class CtiIONRegister : public CtiIONClassType
{
public:

    CtiIONRegister( )  { };
    ~CtiIONRegister( ) { };

    void setContents( void );
    void getContents( void );
    
private:
    
//    T _registerContents;
};



class CtiIONModule : public CtiIONClassType
{
public:

    CtiIONModule( )  { };
    ~CtiIONModule( ) { };

protected:

    vector<CtiIONRegister *> _registers;
};



class CtiIONModuleManager : public CtiIONClassType
{
public:
    
    CtiIONModuleManager( )  { };
    ~CtiIONModuleManager( ) { };

private:

};

#endif  //  #ifndef __ION_CLASSTYPES_H__

