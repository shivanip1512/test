#ifndef _TESTCMDPARSE_OUTPUT_
#define _TESTCMDPARSE_OUTPUT_
#include "test_cmdparse_input.h"
std::string  outputString[TEST_SIZE]= {
"command=getvalue,1,1.000:flag=(none),32,32.000:offset=(none),0,0.000:type=versacom,0,0.000",
"command=getvalue,1,1.000:device=test device,-1,-1.000:flag=(none),32,32.000:offset=(none),0,0.000:type=versacom,0,0.000",
"command=getvalue,1,1.000:device= test  multispace  device     ,-1,-1.000:flag=(none),32,32.000:offset=(none),0,0.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),1024,1024.000:offset=(none),0,0.000:relaymask=(none),3,3.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:type=emetcon,7,7.000",
"command=control,8,8.000:flag=(none),1024,1024.000:offset=(none),0,0.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:type=versacom,0,0.000",
"command=putconfig,6,6.000:led=(none),224,224.000:type=versacom,0,0.000",
"command=putconfig,6,6.000:service=(none),10,10.000:type=versacom,0,0.000",
"command=putconfig,6,6.000:service=(none),4,4.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),512,512.000:offset=(none),0,0.000:relaymask=(none),4,4.000:sa_dlc_mode=(none),0,0.000:sa_f0bit=(none),1,1.000:sa_f1bit=(none),0,0.000:sa_reps=(none),1,1.000:sa_restore=(none),1,1.000:sa_strategy=(none),61,61.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),512,512.000:offset=(none),0,0.000:relaymask=(none),2,2.000:sa_dlc_mode=(none),0,0.000:sa_f0bit=(none),1,1.000:sa_f1bit=(none),0,0.000:sa_reps=(none),1,1.000:sa_restore=(none),1,1.000:sa_strategy=(none),61,61.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),512,512.000:offset=(none),0,0.000:relaymask=(none),1,1.000:sa_dlc_mode=(none),0,0.000:sa_f0bit=(none),1,1.000:sa_f1bit=(none),0,0.000:sa_reps=(none),1,1.000:sa_restore=(none),1,1.000:sa_strategy=(none),61,61.000:type=versacom,0,0.000",
"command=control,8,8.000:cycle=(none),50,50.000:cycle_period=(none),30,30.000:flag=(none),2048,2048.000:offset=(none),0,0.000:relaymask=(none),1,1.000:sa_dlc_mode=(none),0,0.000:sa_f0bit=(none),1,1.000:sa_f1bit=(none),0,0.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),256,256.000:offset=(none),0,0.000:relaymask=(none),4,4.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:sa_reps=(none),0,0.000:sa_restore=(none),1,1.000:sa_strategy=(none),61,61.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),256,256.000:offset=(none),0,0.000:relaymask=(none),2,2.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:sa_reps=(none),0,0.000:sa_restore=(none),1,1.000:sa_strategy=(none),61,61.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),256,256.000:offset=(none),0,0.000:relaymask=(none),1,1.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:sa_reps=(none),0,0.000:sa_restore=(none),1,1.000:sa_strategy=(none),61,61.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),1024,1024.000:offset=(none),0,0.000:relaymask=(none),4,4.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:shed=(none),300,300.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),1024,1024.000:offset=(none),0,0.000:relaymask=(none),2,2.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:shed=(none),300,300.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),1024,1024.000:offset=(none),0,0.000:relaymask=(none),1,1.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:shed=(none),300,300.000:type=versacom,0,0.000",
"command=getvalue,1,1.000:flag=(none),512,512.000:offset=(none),0,0.000:type=versacom,0,0.000",
"command=putconfig,6,6.000:timesync=TRUE,-2147483648,0.000:type=emetcon,7,7.000",
"command=getvalue,1,1.000:flag=(none),2048,2048.000:offset=(none),0,0.000:type=versacom,0,0.000",
"command=getvalue,1,1.000:flag=(none),256,256.000:offset=(none),0,0.000:type=versacom,0,0.000",
"coldload_r1=(none),600,600.000:command=putconfig,6,6.000:type=versacom,0,0.000",
"command=putconfig,6,6.000:template=?loadgroup,-2147483648,0.000:templateinservice=(none),-2147483648,0.000:type=versacom,0,0.000",
"command=putconfig,6,6.000:cycle_r1=(none),50,50.000:type=versacom,0,0.000",
"command=putconfig,6,6.000:type=expresscom,8,8.000:xcpservice=(none),128,128.000",
"command=putconfig,6,6.000:type=expresscom,8,8.000:xctservicebitl=(none),0,0.000:xctservicebitp=(none),0,0.000:xctservicecancel=(none),1,1.000:xctservicetime=(none),24,24.000",
"command=putconfig,6,6.000:type=expresscom,8,8.000:xcrawmaint= 0x01 0x80,-2147483648,0.000",
"command=putconfig,6,6.000:type=expresscom,8,8.000:xcrawmaint= 0x01 0x40,-2147483648,0.000",
"command=putconfig,6,6.000:type=expresscom,8,8.000:xcrawconfig= 0x05 0x00,-2147483648,0.000",
"command=putstatus,4,4.000:type=expresscom,8,8.000:xcproptest=(none),1,1.000",
"command=putconfig,6,6.000:type=expresscom,8,8.000:xcrawconfig= 0x30 0x00 0x02 0x58,-2147483648,0.000",
"class=(none),0,0.000:classoffset=(none),1,1.000:command=putconfig,6,6.000:ied=(none),1,1.000:type=emetcon,7,7.000",
"class=(none),0,0.000:classoffset=(none),0,0.000:command=putconfig,6,6.000:ied=(none),1,1.000:type=emetcon,7,7.000",
"command=getvalue,1,1.000:flag=(none),4352,4352.000:offset=(none),0,0.000:type=versacom,0,0.000",
"class=(none),72,72.000:classoffset=(none),2,2.000:command=putconfig,6,6.000:ied=(none),1,1.000:type=emetcon,7,7.000",
"class=(none),72,72.000:classoffset=(none),1,1.000:command=putconfig,6,6.000:ied=(none),1,1.000:type=emetcon,7,7.000",
"command=getstatus,3,3.000:eventlog=(none),1,1.000:flag=(none),0,0.000:offset=(none),0,0.000:type=versacom,0,0.000",
"command=scan,9,9.000:flag=(none),0,0.000:scantype=(none),3,3.000:type=versacom,0,0.000",
"command=scan,9,9.000:flag=(none),0,0.000:scantype=(none),0,0.000:type=versacom,0,0.000",
"command=loop,7,7.000:count=(none),1,1.000:type=versacom,0,0.000",
"command=putconfig,6,6.000:install=(none),1,1.000:type=emetcon,7,7.000",
"command=getconfig,5,5.000:flag=(none),0,0.000:rolenum=(none),1,1.000:type=versacom,0,0.000",
"command=putconfig,6,6.000:led=(none),224,224.000:type=versacom,0,0.000",
"command=putstatus,4,4.000:flag=(none),16,16.000:type=versacom,0,0.000",
"command=putconfig,6,6.000:service=(none),10,10.000:type=versacom,0,0.000",
"command=putconfig,6,6.000:service=(none),4,4.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),512,512.000:offset=(none),0,0.000:sa_dlc_mode=(none),0,0.000:sa_f0bit=(none),1,1.000:sa_f1bit=(none),0,0.000:sa_reps=(none),1,1.000:sa_restore=(none),1,1.000:sa_strategy=(none),61,61.000:type=versacom,0,0.000",
"command=control,8,8.000:cycle=(none),50,50.000:cycle_count=(none),4,4.000:cycle_period=(none),30,30.000:flag=(none),2048,2048.000:offset=(none),0,0.000:sa_dlc_mode=(none),0,0.000:sa_f0bit=(none),1,1.000:sa_f1bit=(none),0,0.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),256,256.000:offset=(none),0,0.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:sa_reps=(none),0,0.000:sa_restore=(none),1,1.000:sa_strategy=(none),61,61.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),1024,1024.000:offset=(none),0,0.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:shed=(none),3600,3600.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),1024,1024.000:offset=(none),0,0.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:shed=(none),1800,1800.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),1024,1024.000:offset=(none),0,0.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:shed=(none),900,900.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),1024,1024.000:offset=(none),0,0.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:shed=(none),420,420.000:type=versacom,0,0.000",
"command=putstatus,4,4.000:flag=(none),16,16.000:type=versacom,0,0.000",
"command=loop,7,7.000:count=(none),1,1.000:type=versacom,0,0.000",
"command=loop,7,7.000:count=(none),1,1.000:type=versacom,0,0.000",
"command=ping,7,7.000:count=(none),1,1.000:type=versacom,0,0.000",
"command=putstatus,4,4.000:ovuv=(none),1,1.000:type=versacom,0,0.000",
"command=putstatus,4,4.000:ovuv=(none),0,0.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),32,32.000:offset=(none),0,0.000:sa_f1bit=(none),0,0.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),16,16.000:offset=(none),0,0.000:sa_f1bit=(none),0,0.000:type=versacom,0,0.000",
"command=scan,9,9.000:flag=(none),0,0.000:scantype=(none),3,3.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),256,256.000:offset=(none),0,0.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:sa_reps=(none),0,0.000:sa_restore=(none),1,1.000:sa_strategy=(none),61,61.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),1024,1024.000:offset=(none),0,0.000:sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:shed=(none),300,300.000:type=versacom,0,0.000",
"command=putvalue,2,2.000:flag=(none),0,0.000:ied=(none),1,1.000:reset=(none),1,1.000:type=versacom,0,0.000",
"command=getconfig,5,5.000:flag=(none),0,0.000:ied=TRUE,-2147483648,0.000:scan=TRUE,-2147483648,0.000:type=versacom,0,0.000",
"command=getconfig,5,5.000:flag=(none),0,0.000:ied=TRUE,-2147483648,0.000:time=TRUE,-2147483648,0.000:type=versacom,0,0.000",
"command=getvalue,1,1.000:flag=(none),4128,4128.000:offset=(none),0,0.000:type=versacom,0,0.000",
"command=getvalue,1,1.000:flag=(none),4352,4352.000:offset=(none),0,0.000:type=versacom,0,0.000",
"command=getconfig,5,5.000:flag=(none),0,0.000:sync=TRUE,-2147483648,0.000:time=TRUE,-2147483648,0.000:type=versacom,0,0.000",
"command=getconfig,5,5.000:flag=(none),0,0.000:time=TRUE,-2147483648,0.000:type=versacom,0,0.000",
"command=getstatus,3,3.000:flag=(none),128,128.000:offset=(none),0,0.000:type=versacom,0,0.000",
"command=getstatus,3,3.000:flag=(none),512,512.000:offset=(none),0,0.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),64,64.000:offset=(none),0,0.000:sa_f1bit=(none),0,0.000:type=versacom,0,0.000",
"command=control,8,8.000:flag=(none),128,128.000:offset=(none),0,0.000:sa_dlc_mode=(none),0,0.000:sa_f0bit=(none),1,1.000:sa_f1bit=(none),0,0.000:type=versacom,0,0.000",
"command=getstatus,3,3.000:flag=(none),16,16.000:offset=(none),0,0.000:type=versacom,0,0.000",
"command=putconfig,6,6.000:type=emetcon,7,7.000",
"command=getconfig,5,5.000:flag=(none),0,0.000:type=versacom,0,0.000",
"command=putconfig,6,6.000:type=emetcon,7,7.000",
"command=getconfig,5,5.000:flag=(none),0,0.000:multchannel=(none),1,1.000:multiplier=(none),1,1.000:type=versacom,0,0.000",
"command=getstatus,3,3.000:flag=(none),256,256.000:offset=(none),0,0.000:type=versacom,0,0.000",
"command=getvalue,1,1.000:flag=(none),8192,8192.000:offset=(none),0,0.000:type=versacom,0,0.000",
"command=putvalue,2,2.000:flag=(none),0,0.000:kyz=(none),1,1.000:kyz_offset=(none),1,1.000:reset=(none),1,1.000:type=versacom,0,0.000",
"command=getconfig,5,5.000:flag=(none),0,0.000:model=TRUE,-2147483648,0.000:type=versacom,0,0.000",
"command=getvalue,1,1.000:flag=(none),256,256.000:offset=(none),0,0.000:type=versacom,0,0.000",
"command=getvalue,1,1.000:flag=(none),32,32.000:offset=(none),0,0.000:type=versacom,0,0.000",
"command=getconfig,5,5.000:flag=(none),0,0.000:interval=intervals,-2147483648,0.000:type=versacom,0,0.000",
"command=getconfig,5,5.000:flag=(none),0,0.000:model=TRUE,-2147483648,0.000:type=versacom,0,0.000",
"analog=(none),1,1.000:analogoffset=(none),1,1.000:analogvalue=(none),3,3.000:command=putvalue,2,2.000:flag=(none),0,0.000:type=versacom,0,0.000",
"command=putvalue,2,2.000:flag=(none),0,0.000:kyz=(none),1,1.000:kyz_offset=(none),1,1.000:type=versacom,0,0.000",
"command=putstatus,4,4.000:flag=(none),16,16.000:type=versacom,0,0.000",
"command=putstatus,4,4.000:flag=(none),16,16.000:type=versacom,0,0.000",
"command=putvalue,2,2.000:flag=(none),0,0.000:power=(none),1,1.000:reset=(none),1,1.000:type=versacom,0,0.000",
"command=getconfig,5,5.000:flag=(none),0,0.000:rawloc=(none),147,147.000:type=versacom,0,0.000",
"command=getconfig,5,5.000:flag=(none),0,0.000:rawlen=(none),2,2.000:rawloc=(none),591922,591922.000:type=versacom,0,0.000",
"command=putconfig,6,6.000:rawdata=�92,-2147483648,0.000:rawloc=(none),38947,38947.000:type=emetcon,7,7.000",
"channel=(none),2,2.000:command=getvalue,1,1.000:flag=(none),0,0.000:lp_channel=(none),2,2.000:lp_command=peak,-2147483648,0.000:lp_date_start=12/31/2003,-2147483648,0.000:lp_peaktype=interval,-2147483648,0.000:lp_range=(none),31,31.000:offset=(none),0,0.000:type=versacom,0,0.000",
"command=putstatus,4,4.000:type=expresscom,8,8.000:xcproptest=(none),2,2.000",
"command=putstatus,4,4.000:type=expresscom,8,8.000:xcproptest=(none),1,1.000",
"command=putstatus,4,4.000:type=expresscom,8,8.000:xcproptest=(none),0,0.000",
"command=putstatus,4,4.000:type=expresscom,8,8.000:xcproptest=(none),3,3.000",
"command=putstatus,4,4.000:type=expresscom,8,8.000:xcproptest=(none),4,4.000",
"command=putstatus,4,4.000:type=expresscom,8,8.000:xcproptest=(none),128,128.000",
"command=putconfig,6,6.000:type=expresscom,8,8.000:xcextier=(none),1,1.000:xcextiercmd=(none),5,5.000:xcextierdisp=(none),3,3.000:xcextierlevel=(none),2,2.000:xcextierrate=(none),254,254.000:xctierdelay=(none),5432,5432.000:xctiertimeout=(none),600,600.000",
"command=putconfig,6,6.000:type=expresscom,8,8.000:xcdisplay=(none),1,1.000:xclcddisplay=(none),1,1.000",
"command=putconfig,6,6.000:type=expresscom,8,8.000:xcdisplay=(none),1,1.000:xclcddisplay=(none),0,0.000",
"command=putconfig,6,6.000:type=expresscom,8,8.000:xcdisplay=(none),1,1.000:xcdisplaymessage=thisismessage2,-2147483648,0.000:xcdisplaymessageid=(none),2,2.000",
"command=putconfig,6,6.000:type=expresscom,8,8.000:xcconfig=(none),1,1.000:xcthermoconfig=(none),34,34.000",
"command=putconfig,6,6.000:type=expresscom,8,8.000:xcchan_0=(none),1,1.000:xcchan_1=(none),2,2.000:xcchan_2=(none),4,4.000:xcchanvalue_0=(none),34,34.000:xcchanvalue_1=(none),36,36.000:xcchanvalue_2=(none),56,56.300:xcnumutilchans=(none),3,3.000:xcutilusage=(none),1,1.000",
"command=putconfig,6,6.000:type=expresscom,8,8.000:xcchargedollars=(none),0,0.000:xccurrency=dollars,-2147483648,0.000:xcdeleteid=(none),2,2.000:xcparametername=julie,-2147483648,0.000:xcpastcharge=(none),555,555.000:xcpastusage=(none),33,33.000:xcpresentcharge=(none),333,333.000:xcpresentusage=(none),2,2.000:xcutilchan=(none),1,1.000:xcutilflags=(none),1,1.000:xcutilinfo=(none),1,1.000",
"command=control,8,8.000:cycle=(none),12,12.000:cycle_count=(none),35,35.000:cycle_period=(none),2,2.000:delaytime_sec=(none),240,240.000:flag=(none),0,0.000:offset=(none),0,0.000:sa_f1bit=(none),0,0.000:type=expresscom,8,8.000:xcctrltemp=(none),45,45.000:xclimitfbp=(none),4,4.000:xclimittemp=(none),34,34.000:xctcycle=(none),1,1.000",
"command=control,8,8.000:flag=(none),0,0.000:offset=(none),0,0.000:sa_f1bit=(none),0,0.000:type=expresscom,8,8.000:xcbump=(none),1,1.000:xcdsb=(none),4,4.000:xcdsd=(none),-1,-1.000:xcdsf=(none),11,11.000:xcholdtemp=(none),1,1.000:xcmaxtemp=(none),50,50.000:xcmintemp=(none),2,2.000:xcsetpoint=(none),1,1.000:xcstage=(none),15,15.000:xcta=(none),26,26.000:xctb=(none),27,27.000:xctc=(none),30,30.000:xctd=(none),29,29.000:xcte=(none),32,32.000:xctf=(none),35,35.000:xctr=(none),25,25.000",
"command=putconfig,6,6.000:relaymask=(none),1,1.000:type=expresscom,8,8.000:xcfanstate=(none),3,3.000:xcholdprog=(none),1,1.000:xcrunprog=(none),1,1.000:xcsetcooltemp=(none),33,33.000:xcsetstate=(none),1,1.000:xcsysstate=(none),28,28.000:xctimeout=(none),40,40.000:xctwosetpoints=(none),1,1.000",
"command=putconfig,6,6.000:relaymask=(none),1,1.000:type=expresscom,8,8.000:xcfanstate=(none),2,2.000:xcrunprog=(none),1,1.000:xcsetcooltemp=(none),33,33.000:xcsetheattemp=(none),43,43.000:xcsetstate=(none),1,1.000:xcsysstate=(none),4,4.000:xctimeout=(none),40,40.000:xctwosetpoints=(none),1,1.000",
"command=putconfig,6,6.000:relaymask=(none),1,1.000:type=expresscom,8,8.000:xcfanstate=(none),1,1.000:xcrunprog=(none),1,1.000:xcsetheattemp=(none),43,43.000:xcsetstate=(none),1,1.000:xcsysstate=(none),8,8.000:xctimeout=(none),40,40.000:xctwosetpoints=(none),1,1.000",
"command=putconfig,6,6.000:relaymask=(none),1,1.000:type=expresscom,8,8.000:xcfanstate=(none),1,1.000:xcrunprog=(none),1,1.000:xcsetheattemp=(none),43,43.000:xcsetstate=(none),1,1.000:xcsysstate=(none),12,12.000:xctimeout=(none),40,40.000:xctwosetpoints=(none),1,1.000",
"command=putconfig,6,6.000:relaymask=(none),1,1.000:type=expresscom,8,8.000:xcfanstate=(none),1,1.000:xcrunprog=(none),1,1.000:xcsetcooltemp=(none),33,33.000:xcsetheattemp=(none),43,43.000:xcsetstate=(none),1,1.000:xcsysstate=(none),16,16.000:xctimeout=(none),40,40.000:xctwosetpoints=(none),1,1.000",
"command=control,8,8.000:flag=(none),0,0.000:offset=(none),0,0.000:sa_dlc_mode=(none),0,0.000:sa_f0bit=(none),1,1.000:sa_f1bit=(none),0,0.000:type=expresscom,8,8.000:xcbacklight=(none),1,1.000:xcbacklightcycle=(none),0,0.000:xcbacklightduty=(none),0,0.000:xcbacklightperiod=(none),0,0.000",
"command=putconfig,6,6.000:type=expresscom,8,8.000:xcascii=(none),1,1.000:xcclear=(none),1,1.000:xcdata=julie rocks!,-2147483648,0.000:xcdataport=(none),2,2.000:xcdatatimeout=(none),30,30.000:xcdeletable=(none),1,1.000:xchour=(none),1,1.000:xcpriority=(none),7,7.000"
};
#endif
