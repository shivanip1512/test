#pragma warning( disable : 4786)

#ifndef __COLLECTABLE_H__
#define __COLLECTABLE_H__
/*
 *  Defines for DEFINE_COLLECTABLES
 */
#define NOTIF_CUST_EMAIL_MSG_ID           701
#define NOTIF_EMAIL_MSG_ID                702
#define NOTIF_EMAIL_ATTCH_MSG_ID          703
#define NOTIF_VOICE_MSG_ID                704
#define NOTIF_ALARM_MSG_ID                706
#define NOTIF_LMCONTROL_MSG_ID            707



#define MSG_BASE                          1500
#define MSG_NULL                          (MSG_BASE)
#define MSG_DEFAULT                       ((MSG_BASE) + 5 )
#define MSG_TRACE                         ((MSG_BASE) + 20)
#define MSG_COMMAND                       ((MSG_BASE) + 30)
#define MSG_REGISTER                      ((MSG_BASE) + 40)
#define MSG_SERVER_REQUEST                ((MSG_BASE) + 50)
#define MSG_SERVER_RESPONSE               ((MSG_BASE) + 51)
#define MSG_POINTREGISTRATION             ((MSG_BASE) + 70)
#define MSG_DBCHANGE                      ((MSG_BASE) + 80)
#define MSG_PCREQUEST                     ((MSG_BASE) + 85)
#define MSG_PCRETURN                      ((MSG_BASE) + 90)

#define MSG_MULTI                         ((MSG_BASE) + 91)
#define MSG_TAG                           ((MSG_BASE) + 94)
#define MSG_POINTDATA                     ((MSG_BASE) + 95)
#define MSG_SIGNAL                        ((MSG_BASE) + 96)
#define MSG_LMCONTROLHISTORY              ((MSG_BASE) + 98)
#define MSG_COMMERRORHISTORY              ((MSG_BASE) + 99)

#define CLASS_APPLDEF                     ((MSG_BASE) + 100)

#endif      // #ifndef __COLLECTABLE_H__





