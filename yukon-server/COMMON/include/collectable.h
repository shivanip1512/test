#pragma once

/*
 *  Defines for DEFINE_COLLECTABLES
 */
#define NOTIF_CUST_EMAIL_MSG_ID           701
#define NOTIF_EMAIL_MSG_ID                702
#define NOTIF_EMAIL_ATTCH_MSG_ID          703
#define NOTIF_VOICE_MSG_ID                704
#define NOTIF_ALARM_MSG_ID                706
#define NOTIF_LMCONTROL_MSG_ID            707
#define NOTIF_VOICEDATA_REQUEST_ID        708
#define NOTIF_VOICEDATA_RESPONESE_ID      709
#define NOTIF_CURTAILMENTEVENT_ID         710


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

#define CLASS_APPLDEF                     ((MSG_BASE) + 100)

#define MSG_QUEUEDATA                     ((MSG_BASE) + 150)
#define MSG_REQUESTCANCEL                 ((MSG_BASE) + 151)



#define DECLARE_COLLECTABLE( classname ) \
virtual int isA() const; \
virtual std::string stringID() const;

#define DEFINE_COLLECTABLE( classname , id) \
int classname::isA() const { return id; } \
std::string classname::stringID() const { return std::string(#classname); };



