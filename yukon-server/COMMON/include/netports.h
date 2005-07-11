#ifndef __CTINETPORTS_H__
#define __CTINETPORTS_H__
/*
 *  Some define Nexus's and their socket numbers
 */

#define  NEXUSBASE                                          1500
#define  PORTCONTROLNEXUS                                   (NEXUSBASE + 0  )
#define  PORTGUINEXUS                                       (NEXUSBASE + 1  )
#define  VANGOGHNEXUS                                       (NEXUSBASE + 10 )
#define  NOTIFICATIONNEXUS                                     (NEXUSBASE + 15 )

#define  PROCLOGNEXUS                                       (NEXUSBASE + 20)
#define  PORTERINTERFACENEXUS                               (NEXUSBASE + 40)

#define  SES92NEXUS                                         (NEXUSBASE + 120)
#define  WELCONEXUS                                         (NEXUSBASE + 121)
#define  CCU710NEXUS                                        (NEXUSBASE + 122)

#define  CAPCONTROLNEXUS                                    (NEXUSBASE + 410)
#define  LOADMANAGEMENTNEXUS                                (NEXUSBASE + 420)

#define  PORTSHARENEXUS                                     (NEXUSBASE + 430)
//  Next allocation should start at NEXUSBASE+530 to allow for 100 shared ports

#endif
