#ifndef DATE_TIME_PARSE_FORMAT_BASE__
#define DATE_TIME_PARSE_FORMAT_BASE__

/* Copyright (c) 2002,2003 CrystalClear Software, Inc.
 * Use, modification and distribution is subject to the 
 * Boost Software License, Version 1.0. (See accompanying
 * file LICENSE-1.0 or http://www.boost.org/LICENSE-1.0)
 * Author: Jeff Garland 
 * $Date: 2004/05/17 21:12:06 $
 */

namespace boost {
namespace date_time {

  //! Enum for distinguishing parsing and formatting options
  enum month_format_spec {month_as_integer, month_as_short_string, 
                          month_as_long_string};

  //! Enum for distinguishing the order of Month, Day, & Year.
  /*! Enum for distinguishing the order in which Month, Day, & Year
   * will appear in a date string */
  enum ymd_order_spec {ymd_order_iso,  //order is year-month-day
                       ymd_order_dmy,  //day-month-year
                       ymd_order_us};  //order is month-day-year


} }//namespace date_time

#endif
