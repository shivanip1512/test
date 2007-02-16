#ifndef ORANULL_H
#define ORANULL_H
#include <rw/db/dbsrc/bknulli.h>

/**************************************************************************
 *
 * $Id: 
 *
 ***************************************************************************
 *
 * Copyright (c) 1994-1999 Rogue Wave Software, Inc.  All Rights Reserved.
 *
 * This computer software is owned by Rogue Wave Software, Inc. and is
 * protected by U.S. copyright laws and other laws and by international
 * treaties.  This computer software is furnished by Rogue Wave Software,
 * Inc. pursuant to a written license agreement and may be used, copied,
 * transmitted, and stored only in accordance with the terms of such
 * license and with the inclusion of the above copyright notice.  This
 * computer software or any other copies thereof may not be provided or
 * otherwise made available to any other person.
 *
 * U.S. Government Restricted Rights.  This computer software is provided
 * with Restricted Rights.  Use, duplication, or disclosure by the
 * Government is subject to restrictions as set forth in subparagraph (c)
 * (1) (ii) of The Rights in Technical Data and Computer Software clause
 * at DFARS 252.227-7013 or subparagraphs (c) (1) and (2) of the
 * Commercial Computer Software – Restricted Rights at 48 CFR 52.227-19,
 * as applicable.  Manufacturer is Rogue Wave Software, Inc., 5500
 * Flatiron Parkway, Boulder, Colorado 80301 USA.
 *
 **************************************************************************/

// Interface level, what the user sees.
class RWDBAccessExport RWDBOracleBulkNullIndicatorImp : public RWDBBulkNullIndicatorImp 
{
public:
        virtual void
         setNull(size_t n)
        {
               ( (short*)data())[n] = -1;
        }

        ~RWDBOracleBulkNullIndicatorImp()
        {
               delete [] (short*)data();
        }

        RWDBBulkNullIndicatorImp* clone(size_t sz)
        {
               RWDBOracleBulkNullIndicatorImp* rVal = new RWDBOracleBulkNullIndicatorImp;
               short* dat = new short[sz];
               for (int i = 0; i < (int)sz; i++) {
                     dat[i] = ( (short*)data())[i];
               }

               rVal->setData( dat );
        return rVal;
        }


        virtual void
         unsetNull(size_t n)
        {
               ( (short*)data())[n] = 0;
        }
    
        virtual size_t  at(size_t index)
        {
               return (size_t)( (unsigned short*)data())[index];
        }

    void   width(size_t i, size_t w)
        {
               ( (short*)data())[i] = w;
        }



        virtual RWBoolean
        isNull(size_t index)
        {
               return ( (short*)data())[index] == -1;
        }
};



#endif

