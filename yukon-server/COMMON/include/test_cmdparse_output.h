#ifndef _TESTCMDPARSE_OUTPUT_
#define _TESTCMDPARSE_OUTPUT_

#include "test_cmdparse_input.h"

std::string parse_asString[TEST_SIZE] = {
"command=1:flags=32::type=versacom,0,0.000",
"command=1:flags=32::device=Test device,-1,-1.000:type=versacom,0,0.000",
"command=1:flags=32::device= Test  multispace  device     ,-1,-1.000:type=versacom,0,0.000",
"command=1:flags=268435968::offset=(none),3,3.000:type=versacom,0,0.000",
"command=8:flags=1024::relaymask=(none),3,3.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:type=emetcon,7,7.000",
"command=8:flags=1024::sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:type=versacom,0,0.000",
"command=6:flags=0::led=(none),224,224.000:type=versacom,0,0.000",
"command=6:flags=0::service=(none),10,10.000:type=versacom,0,0.000",
"command=6:flags=0::service=(none),4,4.000:type=versacom,0,0.000",
"command=8:flags=512::relaymask=(none),4,4.000:sa_dlc_mode=(none),0,0.000:sa_f0bit=(none),1,1.000:sa_f1bit=(none),0,0.000:sa_reps=(none),1,1.000:sa_restore=(none),1,1.000:sa_strategy=(none),61,61.000:type=versacom,0,0.000",
"command=8:flags=512::relaymask=(none),2,2.000:sa_dlc_mode=(none),0,0.000:sa_f0bit=(none),1,1.000:sa_f1bit=(none),0,0.000:sa_reps=(none),1,1.000:sa_restore=(none),1,1.000:sa_strategy=(none),61,61.000:type=versacom,0,0.000",
"command=8:flags=512::relaymask=(none),1,1.000:sa_dlc_mode=(none),0,0.000:sa_f0bit=(none),1,1.000:sa_f1bit=(none),0,0.000:sa_reps=(none),1,1.000:sa_restore=(none),1,1.000:sa_strategy=(none),61,61.000:type=versacom,0,0.000",
"command=8:flags=2048::cycle=(none),50,50.000:cycle_period=(none),30,30.000:relaymask=(none),1,1.000:sa_dlc_mode=(none),0,0.000:sa_f0bit=(none),1,1.000:sa_f1bit=(none),0,0.000:type=versacom,0,0.000",
"command=8:flags=256::relaymask=(none),4,4.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:sa_reps=(none),0,0.000:sa_restore=(none),1,1.000:sa_strategy=(none),61,61.000:type=versacom,0,0.000",
"command=8:flags=256::relaymask=(none),2,2.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:sa_reps=(none),0,0.000:sa_restore=(none),1,1.000:sa_strategy=(none),61,61.000:type=versacom,0,0.000",
"command=8:flags=256::relaymask=(none),1,1.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:sa_reps=(none),0,0.000:sa_restore=(none),1,1.000:sa_strategy=(none),61,61.000:type=versacom,0,0.000",
"command=8:flags=1024::relaymask=(none),4,4.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:shed=(none),300,300.000:type=versacom,0,0.000",
"command=8:flags=1024::relaymask=(none),2,2.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:shed=(none),300,300.000:type=versacom,0,0.000",
"command=8:flags=1024::relaymask=(none),1,1.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:shed=(none),300,300.000:type=versacom,0,0.000",
"command=1:flags=512::type=versacom,0,0.000",
"command=6:flags=0::timesync=TRUE,-2147483648,0.000:type=emetcon,7,7.000",
"command=1:flags=2048::type=versacom,0,0.000",
"command=1:flags=256::type=versacom,0,0.000",
"command=6:flags=0::precanned_table=(none),11,11.000:type=versacom,0,0.000",
"command=6:flags=0::precanned_table=(none),11,11.000:read_interval=(none),800,800.000:type=versacom,0,0.000",
"command=6:flags=0::coldload_r1=(none),600,600.000:type=versacom,0,0.000",
"command=6:flags=0::template=?LoadGroup,-2147483648,0.000:templateinservice=(none),-2147483648,0.000:type=versacom,0,0.000",
"command=6:flags=0::cycle_r1=(none),50,50.000:type=versacom,0,0.000",
"command=6:flags=0::type=expresscom,8,8.000:xcpservice=(none),128,128.000",
"command=6:flags=0::type=expresscom,8,8.000:xctservicebitl=(none),0,0.000:xctservicebitp=(none),0,0.000:xctservicecancel=(none),1,1.000:xctservicetime=(none),24,24.000",
"command=6:flags=0::type=expresscom,8,8.000:xcrawmaint= 0x01 0x80,-2147483648,0.000",
"command=6:flags=0::type=expresscom,8,8.000:xcrawmaint= 0x01 0x40,-2147483648,0.000",
"command=6:flags=0::type=expresscom,8,8.000:xcrawconfig= 0x05 0x00,-2147483648,0.000",
"command=4:flags=0::type=expresscom,8,8.000:xcproptest=(none),1,1.000",
"command=6:flags=0::type=expresscom,8,8.000:xcrawconfig= 0x30 0x00 0x02 0x58,-2147483648,0.000",
"command=6:flags=0::class=(none),0,0.000:classoffset=(none),1,1.000:ied=(none),1,1.000:type=emetcon,7,7.000",
"command=6:flags=0::class=(none),0,0.000:classoffset=(none),0,0.000:ied=(none),1,1.000:type=emetcon,7,7.000",
"command=1:flags=4352::type=versacom,0,0.000",
"command=6:flags=0::class=(none),72,72.000:classoffset=(none),2,2.000:ied=(none),1,1.000:type=emetcon,7,7.000",
"command=6:flags=0::class=(none),72,72.000:classoffset=(none),1,1.000:ied=(none),1,1.000:type=emetcon,7,7.000",
"command=3:flags=0::eventlog=(none),1,1.000:type=versacom,0,0.000",
"command=9:flags=0::scantype=(none),3,3.000:type=versacom,0,0.000",
"command=9:flags=0::scantype=(none),0,0.000:type=versacom,0,0.000",
"command=7:flags=0::count=(none),1,1.000:type=versacom,0,0.000",
"command=6:flags=0::install=(none),1,1.000:type=emetcon,7,7.000",
"command=5:flags=0::rolenum=(none),1,1.000:type=versacom,0,0.000",
"command=6:flags=0::led=(none),224,224.000:type=versacom,0,0.000",
"command=4:flags=16::type=versacom,0,0.000",
"command=6:flags=0::service=(none),10,10.000:type=versacom,0,0.000",
"command=6:flags=0::service=(none),4,4.000:type=versacom,0,0.000",
"command=8:flags=512::sa_dlc_mode=(none),0,0.000:sa_f0bit=(none),1,1.000:sa_f1bit=(none),0,0.000:sa_reps=(none),1,1.000:sa_restore=(none),1,1.000:sa_strategy=(none),61,61.000:type=versacom,0,0.000",
"command=8:flags=2048::cycle=(none),50,50.000:cycle_count=(none),4,4.000:cycle_period=(none),30,30.000:sa_dlc_mode=(none),0,0.000:sa_f0bit=(none),1,1.000:sa_f1bit=(none),0,0.000:type=versacom,0,0.000",
"command=8:flags=256::sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:sa_reps=(none),0,0.000:sa_restore=(none),1,1.000:sa_strategy=(none),61,61.000:type=versacom,0,0.000",
"command=8:flags=1024::sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:shed=(none),3600,3600.000:type=versacom,0,0.000",
"command=8:flags=1024::sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:shed=(none),1800,1800.000:type=versacom,0,0.000",
"command=8:flags=1024::sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:shed=(none),900,900.000:type=versacom,0,0.000",
"command=8:flags=1024::sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:shed=(none),420,420.000:type=versacom,0,0.000",
"command=4:flags=16::type=versacom,0,0.000",
"command=7:flags=0::count=(none),1,1.000:type=versacom,0,0.000",
"command=7:flags=0::count=(none),1,1.000:type=versacom,0,0.000",
"command=7:flags=0::count=(none),1,1.000:type=versacom,0,0.000",
"command=4:flags=0::ovuv=(none),1,1.000:type=versacom,0,0.000",
"command=4:flags=0::ovuv=(none),0,0.000:type=versacom,0,0.000",
"command=8:flags=32::sa_f1bit=(none),0,0.000:type=versacom,0,0.000",
"command=8:flags=16::sa_f1bit=(none),0,0.000:type=versacom,0,0.000",
"command=9:flags=0::scantype=(none),3,3.000:type=versacom,0,0.000",
"command=8:flags=256::sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:sa_reps=(none),0,0.000:sa_restore=(none),1,1.000:sa_strategy=(none),61,61.000:type=versacom,0,0.000",
"command=8:flags=1024::sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:shed=(none),300,300.000:type=versacom,0,0.000",
"command=2:flags=0::ied=(none),1,1.000:reset=(none),1,1.000:type=versacom,0,0.000",
"command=5:flags=0::ied=TRUE,-2147483648,0.000:scan=TRUE,-2147483648,0.000:type=versacom,0,0.000",
"command=5:flags=0::ied=TRUE,-2147483648,0.000:time=TRUE,-2147483648,0.000:type=versacom,0,0.000",
"command=1:flags=4128::type=versacom,0,0.000",
"command=1:flags=4352::type=versacom,0,0.000",
"command=5:flags=0::sync=TRUE,-2147483648,0.000:time=TRUE,-2147483648,0.000:type=versacom,0,0.000",
"command=5:flags=0::time=TRUE,-2147483648,0.000:type=versacom,0,0.000",
"command=3:flags=128::type=versacom,0,0.000",
"command=3:flags=512::type=versacom,0,0.000",
"command=8:flags=64::sa_f1bit=(none),0,0.000:type=versacom,0,0.000",
"command=8:flags=128::sa_dlc_mode=(none),0,0.000:sa_f0bit=(none),1,1.000:sa_f1bit=(none),0,0.000:type=versacom,0,0.000",
"command=3:flags=16::type=versacom,0,0.000",
"command=6:flags=0::type=emetcon,7,7.000",
"command=5:flags=0::type=versacom,0,0.000",
"command=6:flags=0::type=emetcon,7,7.000",
"command=5:flags=0::multchannel=(none),1,1.000:multiplier=(none),1,1.000:type=versacom,0,0.000",
"command=3:flags=256::type=versacom,0,0.000",
"command=1:flags=8192::type=versacom,0,0.000",
"command=2:flags=0::kyz=(none),1,1.000:kyz_offset=(none),1,1.000:reset=(none),1,1.000:type=versacom,0,0.000",
"command=5:flags=0::model=TRUE,-2147483648,0.000:type=versacom,0,0.000",
"command=1:flags=256::type=versacom,0,0.000",
"command=1:flags=32::type=versacom,0,0.000",
"command=5:flags=0::interval=intervals,-2147483648,0.000:type=versacom,0,0.000",
"command=5:flags=0::model=TRUE,-2147483648,0.000:type=versacom,0,0.000",
"command=2:flags=0::analog=(none),1,1.000:analogoffset=(none),1,1.000:analogvalue=(none),3,3.000:type=versacom,0,0.000",
"command=2:flags=0::kyz=(none),1,1.000:kyz_offset=(none),1,1.000:type=versacom,0,0.000",
"command=4:flags=16::type=versacom,0,0.000",
"command=4:flags=16::type=versacom,0,0.000",
"command=2:flags=0::power=(none),1,1.000:reset=(none),1,1.000:type=versacom,0,0.000",
"command=5:flags=0::rawloc=(none),147,147.000:type=versacom,0,0.000",
"command=5:flags=0::rawlen=(none),2,2.000:rawloc=(none),591922,591922.000:type=versacom,0,0.000",
"command=6:flags=0::rawdata=�92,-2147483648,0.000:rawloc=(none),38947,38947.000:type=emetcon,7,7.000",
"command=1:flags=512::channel=(none),2,2.000:lp_channel=(none),2,2.000:lp_command=peak,-2147483648,0.000:lp_date_start=12/31/2003,-2147483648,0.000:lp_peaktype=interval,-2147483648,0.000:lp_range=(none),31,31.000:type=versacom,0,0.000",
"command=4:flags=0::type=expresscom,8,8.000:xcproptest=(none),2,2.000",
"command=4:flags=0::type=expresscom,8,8.000:xcproptest=(none),1,1.000",
"command=4:flags=0::type=expresscom,8,8.000:xcproptest=(none),0,0.000",
"command=4:flags=0::type=expresscom,8,8.000:xcproptest=(none),3,3.000",
"command=4:flags=0::type=expresscom,8,8.000:xcproptest=(none),4,4.000",
"command=4:flags=0::type=expresscom,8,8.000:xcproptest=(none),128,128.000",
"command=6:flags=0::type=expresscom,8,8.000:xcextier=(none),1,1.000:xcextiercmd=(none),5,5.000:xcextierdisp=(none),3,3.000:xcextierlevel=(none),2,2.000:xcextierrate=(none),254,254.000:xctierdelay=(none),5432,5432.000:xctiertimeout=(none),600,600.000",
"command=6:flags=0::type=expresscom,8,8.000:xcdisplay=(none),1,1.000:xclcddisplay=(none),1,1.000",
"command=6:flags=0::type=expresscom,8,8.000:xcdisplay=(none),1,1.000:xclcddisplay=(none),0,0.000",
"command=6:flags=0::type=expresscom,8,8.000:xcdisplay=(none),1,1.000:xcdisplaymessage=ThisisMessage2,-2147483648,0.000:xcdisplaymessageid=(none),2,2.000",
"command=6:flags=0::type=expresscom,8,8.000:xcconfig=(none),1,1.000:xcthermoconfig=(none),34,34.000",
"command=6:flags=0::type=expresscom,8,8.000:xcchan_0=(none),0,0.000:xcchan_1=(none),1,1.000:xcchan_2=(none),2,2.000:xcchan_3=(none),3,3.000:xcchanbucket_0=(none),0,0.000:xcchanbucket_1=(none),1,1.000:xcchanbucket_2=(none),2,2.000:xcchanbucket_3=(none),3,3.000:xcchanvalue_0=(none),34,34.000:xcchanvalue_1=(none),35,35.000:xcchanvalue_2=(none),36,36.000:xcchanvalue_3=(none),37,37.300:xcnumutilvalues=(none),4,4.000:xcutilusage=(none),1,1.000",
"command=6:flags=0::type=expresscom,8,8.000:xcchargecents=(none),1,1.000:xcdisplaycost=(none),1,1.000:xcdisplayusage=(none),1,1.000:xcoptionalstring=Channel 1,-2147483648,0.000:xcutilchan=(none),0,0.000:xcutilinfo=(none),1,1.000",
"command=8:flags=0::cycle=(none),12,12.000:cycle_count=(none),35,35.000:cycle_period=(none),2,2.000:delaytime_sec=(none),240,240.000:sa_f1bit=(none),0,0.000:type=expresscom,8,8.000:xcctrltemp=(none),45,45.000:xclimitfbp=(none),4,4.000:xclimittemp=(none),34,34.000:xctcycle=(none),1,1.000",
"command=8:flags=0::sa_f1bit=(none),0,0.000:type=expresscom,8,8.000:xcbump=(none),1,1.000:xcdsb=(none),4,4.000:xcdsd=(none),-1,-1.000:xcdsf=(none),11,11.000:xcholdtemp=(none),1,1.000:xcmaxtemp=(none),50,50.000:xcmintemp=(none),2,2.000:xcsetpoint=(none),1,1.000:xcstage=(none),15,15.000:xcta=(none),26,26.000:xctb=(none),27,27.000:xctc=(none),30,30.000:xctd=(none),29,29.000:xcte=(none),32,32.000:xctf=(none),35,35.000:xctr=(none),25,25.000",
"command=6:flags=0::relaymask=(none),1,1.000:type=expresscom,8,8.000:xcfanstate=(none),3,3.000:xcholdprog=(none),1,1.000:xcrunprog=(none),1,1.000:xcsetcooltemp=(none),33,33.000:xcsetstate=(none),1,1.000:xcsysstate=(none),28,28.000:xctimeout=(none),40,40.000:xctwosetpoints=(none),1,1.000",
"command=6:flags=0::relaymask=(none),1,1.000:type=expresscom,8,8.000:xcfanstate=(none),2,2.000:xcrunprog=(none),1,1.000:xcsetcooltemp=(none),33,33.000:xcsetheattemp=(none),43,43.000:xcsetstate=(none),1,1.000:xcsysstate=(none),4,4.000:xctimeout=(none),40,40.000:xctwosetpoints=(none),1,1.000",
"command=6:flags=0::relaymask=(none),1,1.000:type=expresscom,8,8.000:xcfanstate=(none),1,1.000:xcrunprog=(none),1,1.000:xcsetheattemp=(none),43,43.000:xcsetstate=(none),1,1.000:xcsysstate=(none),8,8.000:xctimeout=(none),40,40.000:xctwosetpoints=(none),1,1.000",
"command=6:flags=0::relaymask=(none),1,1.000:type=expresscom,8,8.000:xcfanstate=(none),1,1.000:xcrunprog=(none),1,1.000:xcsetheattemp=(none),43,43.000:xcsetstate=(none),1,1.000:xcsysstate=(none),12,12.000:xctimeout=(none),40,40.000:xctwosetpoints=(none),1,1.000",
"command=6:flags=0::relaymask=(none),1,1.000:type=expresscom,8,8.000:xcfanstate=(none),1,1.000:xcrunprog=(none),1,1.000:xcsetcooltemp=(none),33,33.000:xcsetheattemp=(none),43,43.000:xcsetstate=(none),1,1.000:xcsysstate=(none),16,16.000:xctimeout=(none),40,40.000:xctwosetpoints=(none),1,1.000",
"command=8:flags=0::sa_dlc_mode=(none),0,0.000:sa_f0bit=(none),1,1.000:sa_f1bit=(none),0,0.000:type=expresscom,8,8.000:xcbacklight=(none),1,1.000:xcbacklightcycle=(none),23,23.000:xcbacklightduty=(none),34,34.000:xcbacklightperiod=(none),45,45.000",
"command=6:flags=0::type=expresscom,8,8.000:xcascii=(none),1,1.000:xcclear=(none),1,1.000:xcdata=Julie rocks!,-2147483648,0.000:xcdataport=(none),2,2.000:xcdatapriority=(none),7,7.000:xcdatatimeout=(none),30,30.000:xcdeletable=(none),1,1.000:xchour=(none),1,1.000",
"command=6:flags=0::type=expresscom,8,8.000:xccold=(none),1,1.000:xccoldload_r=(none),600,600.000",
"command=6:flags=0::type=expresscom,8,8.000:xccold=(none),1,1.000:xccoldload_r1=(none),600,600.000",
"command=6:flags=0::type=expresscom,8,8.000:xccold=(none),1,1.000:xccoldload_r1=(none),600,600.000:xccoldload_r14=(none),600,600.000:xccoldload_r2=(none),7200,7200.000",
"command=6:flags=0::lcrmode=(none),1,1.000:modeemetcon=(none),1,1.000:modegolay=(none),1,1.000:modevcom=(none),1,1.000:modexcom=(none),1,1.000:type=expresscom,8,8.000",
"command=6:flags=0::lcrmode=(none),1,1.000:modegolay=(none),1,1.000:modevcom=(none),1,1.000:modexcom=(none),1,1.000:type=expresscom,8,8.000",
"command=6:flags=0::gold=(none),1,1.000:type=expresscom,8,8.000",
"command=6:flags=0::silver=(none),1,1.000:type=expresscom,8,8.000",
"command=6:flags=0::gold=(none),4,4.000:type=expresscom,8,8.000",
"command=6:flags=0::silver=(none),60,60.000:type=expresscom,8,8.000",
"command=6:flags=0::lcrmode=e,-2147483648,0.000:type=versacom,0,0.000",
"command=6:flags=0::lcrmode=v,-2147483648,0.000:type=versacom,0,0.000",
"command=6:flags=0::gold=(none),1,1.000:type=versacom,0,0.000",
"command=6:flags=0::silver=(none),1,1.000:type=versacom,0,0.000",
"command=6:flags=0::gold=(none),4,4.000:type=versacom,0,0.000",
"command=6:flags=0::silver=(none),60,60.000:type=versacom,0,0.000",
"command=6:flags=0::raw=04w,-2147483648,0.000:serial=(none),200148000,200148000.000:type=versacom,0,0.000",
"command=6:flags=0::serial=(none),200148000,200148000.000:type=expresscom,8,8.000:xccmdinitiator=(none),2,2.000",
"command=6:flags=0::serial=(none),200148000,200148000.000:type=expresscom,8,8.000:xcpricetier=(none),3,3.000",
"command=6:flags=0::serial=(none),200148000,200148000.000:type=expresscom,8,8.000:xccomparerssi=(none),1,1.000",
"command=8:flags=0::sa_f1bit=(none),0,0.000:serial=(none),200148000,200148000.000:type=expresscom,8,8.000:xccontroltime=(none),3600,3600.000:xccpp=(none),1,1.000:xcdelta=(none),1,1.000:xcleave=(none),254,254.000:xcminheat=(none),71,71.000:xcreturn=(none),255,255.000:xcsleep=(none),4,4.000:xcwake=(none),2,2.000",
"command=8:flags=0::sa_f1bit=(none),0,0.000:serial=(none),200148000,200148000.000:type=expresscom,8,8.000:xccontroltime=(none),3600,3600.000:xccpp=(none),1,1.000:xcmaxcool=(none),71,71.000:xcmode=(none),1,1.000:xcreturn=(none),76,76.000:xcsleep=(none),72,72.000:xcwake=(none),78,78.000",
"command=8:flags=0::sa_f1bit=(none),0,0.000:serial=(none),200148000,200148000.000:type=expresscom,8,8.000:xccelsius=(none),1,1.000:xccontroltime=(none),3600,3600.000:xccpp=(none),1,1.000:xcdelta=(none),1,1.000:xcleave=(none),3,3.000:xcmaxcool=(none),29,29.000:xcmode=(none),1,1.000:xcsleep=(none),254,254.000:xcwake=(none),1,1.000",
"command=6:flags=0::phasedetect=TRUE,-2147483648,0.000:phasedetectclear=TRUE,-2147483648,0.000:type=emetcon,7,7.000",
"command=6:flags=0::phase=a,-2147483648,0.000:phasedelta=(none),3,3.000:phasedetect=TRUE,-2147483648,0.000:phaseinterval=(none),30,30.000:phasenum=(none),4,4.000:type=emetcon,7,7.000",
"command=5:flags=0::phasedetect=(none),1,1.000:phasedetectread=(none),1,1.000:type=versacom,0,0.000",
"command=6:flags=0::serial=(none),1112345,1112345.000:type=expresscom,8,8.000:xca_geo=(none),2,2.000:xca_serial_target=(none),1112345,1112345.000:xcgenericaddress=(none),1,1.000",
"command=6:flags=0::type=expresscom,8,8.000:xca_feeder=(none),14,14.000:xca_feeder_target=(none),4,4.000:xca_geo=(none),12,12.000:xca_geo_target=(none),2,2.000:xca_load=(none),3,3.000:xca_program=(none),17,17.000:xca_program_target=(none),7,7.000:xca_spid=(none),10,10.000:xca_spid_target=(none),1,1.000:xca_splinter=(none),18,18.000:xca_splinter_target=(none),8,8.000:xca_sub=(none),13,13.000:xca_sub_target=(none),3,3.000:xca_uda=(none),16,16.000:xca_uda_target=(none),6,6.000:xca_zip=(none),15,15.000:xca_zip_target=(none),5,5.000:xcgenericaddress=(none),1,1.000",
"command=6:flags=0::alarm_mask=(none),2432,2432.000:type=emetcon,7,7.000",
"command=6:flags=0::alarm_mask=(none),1027,1027.000:alarm_mask_meter=(none),17459,17459.000:type=emetcon,7,7.000",
"command=6:flags=0::alarm_mask=(none),1663,1663.000:disconnect=(none),1,1.000:type=emetcon,7,7.000",
"command=6:flags=0::alarm_mask=(none),2432,2432.000:alarm_mask_meter=(none),17,17.000:type=emetcon,7,7.000",
"command=6:flags=0::alarm_mask=(none),2432,2432.000:alarm_mask_meter=(none),17,17.000:config_byte=(none),7,7.000:type=emetcon,7,7.000",
"command=1:flags=0::channel=(none),2,2.000:daily_read=(none),1,1.000:daily_read_date_begin=02/02/2000,-2147483648,0.000:daily_read_detail=(none),1,1.000:type=versacom,0,0.000",
"command=1:flags=0::channel=(none),3,3.000:daily_read=(none),1,1.000:daily_read_detail=(none),1,1.000:type=versacom,0,0.000",
"command=0:flags=0::type=versacom,0,0.000",
"command=0:flags=0::type=versacom,0,0.000",
"command=0:flags=0::type=versacom,0,0.000"
};

#endif
