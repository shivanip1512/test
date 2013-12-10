#pragma once
/*
 *  Some define Nexus's and their socket numbers
 */

#define  NEXUSBASE                                          1500
#define  PORTCONTROLNEXUS                                   (NEXUSBASE + 0  )
#define  PORTSHARENEXUS                                     (NEXUSBASE + 430)
//  Next allocation should start at NEXUSBASE+530 to allow for 100 shared ports

