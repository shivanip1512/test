package com.cannontech.database.data.pao;

/**
 * @author rneuharth
 * Sep 6, 2002 at 12:42:10 PM
 * 
 * Used to keep all subclasses from conflicting with int types
 */
abstract interface TypeBase
{
   final static int DEVICE_OFFSET      = 0;
   final static int ROUTE_OFFSET       = 1000;
   final static int PORT_OFFSET        = 2000;
   final static int CAPCONTROL_OFFSET  = 3000;
   final static int CUSTOMER_OFFSET    = 4000;
   
}
