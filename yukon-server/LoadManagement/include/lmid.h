#pragma once

#include "ctitime.h"

/* Various message classes */
#define CTILMMESSAGE_ID                         600
#define CTILMCOMMAND_ID                         601
#define CTILMCONTROLAREA_MSG_ID                 602
#define CTILMCONTROLAREA_ID                     604
#define CTILMCONTROLAREATRIGGER_ID              605
#define CTILMPROGRAMDIRECT_ID                   606
#define CTILMPROGRAMDIRECTGEAR_ID               607
#define CTILMCURTAILCUSTOMER_ID                 608
#define CTILMGROUPEMETCON_ID                    609
#define CTILMGROUPVERSACOM_ID                   610
#define CTILMPROGRAMCONTROLWINDOW_ID            611
#define CTILMMANUALCONTROLREQUEST_ID            612
#define CTILMMANUALCONTROLRESPONSE_ID           613
#define CTILMPROGRAMCURTAILMENT_ID              614
#define CTILMCURTAILMENTACK_MSG_ID              615
#define CTILMENERGYEXCHANGECUSTOMER_ID          616
#define CTILMPROGRAMENERGYEXCHANGE_ID           617
#define CTILMENERGYEXCHANGEOFFERREVISION_ID     618
#define CTILMENERGYEXCHANGEOFFER_ID             619
#define CTILMENERGYEXCHANGEHOURLYCUSTOMER_ID    620
#define CTILMENERGYEXCHANGEHOURLYOFFER_ID       621
#define CTILMENERGYEXCHANGECUSTOMERREPLY_ID     622
#define CTILMENERGYEXCHANGECONTROLMSG_ID        623
#define CTILMENERGYEXCHANGEACCEPTMSG_ID         624
#define CTILMGROUPRIPPLE_ID                     625
#define CTILMGROUPPOINT_ID                      626
#define CTILMGROUPEXPRESSCOM_ID                 627
#define CTILMPROGRAMTHERMOSTATGEAR_ID           628
#define CTILMGROUPMCT_ID                        629
#define CTILMGROUPSA105_ID                      630
#define CTILMGROUPSA205_ID                      631
#define CTILMGROUPSA305_ID                      632
#define CTILMGROUPSADIGITAL_ID                  633
#define CTILMGROUPGOLAY_ID                      634
#define CTILMDYNAMICPROGRAMMSG_ID               635
#define CTILMDYNAMICGROUPMSG_ID                 636
#define CTILMDYNAMICCONTROLAREAMSG_ID           637
#define CTILMDYNAMICLMTRIGGERMSG_ID             638
#define CTILMCONSTRAINTVIOLATION_ID             639
#define LMGROUPDIGISEP_ID                       640
#define LMGROUPECOBEE_ID                        641
#define LMGROUPHONEYWELL_ID                     642


/* Various debug levels */
#define LM_DEBUG_NONE              0x00000000
#define LM_DEBUG_STANDARD          0x00000001
#define LM_DEBUG_POINT_DATA        0x00000002
#define LM_DEBUG_DATABASE          0x00000004
#define LM_DEBUG_CLIENT            0x00000008
#define LM_DEBUG_CONTROL_PARAMS    0x00000010
#define LM_DEBUG_DYNAMIC_DB        0x00000020
#define LM_DEBUG_CONSTRAINTS       0x00000040
#define LM_DEBUG_DIRECT_NOTIFY     0x00000080
#define LM_DEBUG_IN_MESSAGES       0x00000100
#define LM_DEBUG_OUT_MESSAGES      0x00000200
#define LM_DEBUG_TIMING            0x00000400

#define LM_DEBUG_EXTENDED          0x10000000

extern CtiTime gInvalidCtiTime;
extern ULONG gInvalidCtiTimeSeconds;

extern CtiTime gEndOfCtiTime;
extern ULONG gEndOfCtiTimeSeconds;
