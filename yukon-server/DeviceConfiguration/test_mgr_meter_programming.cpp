#include <boost/test/unit_test.hpp>

#include "boostutil.h"
#include "mgr_meter_programming.h"

BOOST_AUTO_TEST_SUITE(test_mgr_meter_programming)

static const std::string testPassword = "00000000000000000000";
static const std::string testProgram = R"testString(<?xml version="1.0" encoding="iso-8859-1"?>
<!-- 2019-01-04 15:35:41 Creation Date -->
<eer version="1" xmlns:mml="http://www.w3.org/1998/Math/MathML" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="EER-Schema.xsd">
<target manufacturer="EE  " model="A3R     " sspec="271">
<minimum-firmware version="5" revision="0"/>
<maximum-firmware version="5" revision="9"/>
</target>
<target manufacturer="EE  " model="A3R     " sspec="324">
<minimum-firmware version="6" revision="0"/>
<maximum-firmware version="6" revision="9"/>
</target>
<config-data>
	<meterdata product="Metercat" version="6.4.2.0_12938" sdkversion="6.9.1.7561" meter="RATES_1_0" utc_datetime="2019-01-04 20:35" comp_type="6" create_datetime="2018-02-23 11:59:15" create_user_id="{7CE2595E-FAC7-4965-ACA4-3D1FE65462EA}" guid="{0000FF72-B201-49F6-9DFB-0FEC0A78FCAC}" hash="1FFE807055AFD1134B0CFF12F8A3574D" mod_datetime="2018-02-23 11:59:16" mod_user_id="{7CE2595E-FAC7-4965-ACA4-3D1FE65462EA}" name="A3R Load_Profile" mtr_family="7" mtr_type="31" pgm_type="6">
	<table name="COMPONENT_HEADER" address="">
	   <item name="VERSION">0</item>
	   <item name="FAMILY">7</item>
	   <item name="MTR_TYPE">31</item>
	   <item name="PROGRAM">6</item>
	</table>
	<table name="COMPONENT_RATES_SWITCH_TIMES" address="">
	   <item name="GENERATE_MIDNIGHT_SWITCHES">1</item>
	   <item name="DEFAULT_RATE">2</item>
	   <item name="NBR_SWITCH_TIMES">0</item>
	</table>
	<table name="COMPONENT_RATES_DAY_TYPES" address="">
	   <record name="DAY_TYPES">
		  <item name="NBR_SEASONS">4</item>
		  <array name="SEASONS">
			 <element index="0">
				<item name="SUNDAY">1</item>
				<item name="MONDAY">0</item>
				<item name="TUESDAY">0</item>
				<item name="WEDNESDAY">0</item>
				<item name="THURSDAY">0</item>
				<item name="FRIDAY">0</item>
				<item name="SATURDAY">1</item>
				<item name="HOLIDAY">2</item>
			 </element>
			 <element index="1">
				<item name="SUNDAY">1</item>
				<item name="MONDAY">0</item>
				<item name="TUESDAY">0</item>
				<item name="WEDNESDAY">0</item>
				<item name="THURSDAY">0</item>
				<item name="FRIDAY">0</item>
				<item name="SATURDAY">1</item>
				<item name="HOLIDAY">2</item>
			 </element>
			 <element index="2">
				<item name="SUNDAY">1</item>
				<item name="MONDAY">0</item>
				<item name="TUESDAY">0</item>
				<item name="WEDNESDAY">0</item>
				<item name="THURSDAY">0</item>
				<item name="FRIDAY">0</item>
				<item name="SATURDAY">1</item>
				<item name="HOLIDAY">2</item>
			 </element>
			 <element index="3">
				<item name="SUNDAY">1</item>
				<item name="MONDAY">0</item>
				<item name="TUESDAY">0</item>
				<item name="WEDNESDAY">0</item>
				<item name="THURSDAY">0</item>
				<item name="FRIDAY">0</item>
				<item name="SATURDAY">1</item>
				<item name="HOLIDAY">2</item>
			 </element>
		  </array>
	   </record>
	</table></meterdata>
	<meterdata product="Metercat" version="6.4.2.0_12938" sdkversion="6.9.1.7561" meter="SPECIAL_DATES_3_0" utc_datetime="2019-01-04 20:35" comp_type="7" create_datetime="2018-02-23 11:59:15" create_user_id="{7CE2595E-FAC7-4965-ACA4-3D1FE65462EA}" guid="{4BE65931-31A1-4C85-9A0E-591DE075EB70}" hash="B44CF4BD2D2BB469F88B0CF7733CC63B" mod_datetime="2018-09-07 18:41:35" mod_user_id="{7CE2595E-FAC7-4965-ACA4-3D1FE65462EA}" name="A3R Load_Profile" mtr_family="7" mtr_type="31" pgm_type="6">
	<table name="COMPONENT_HEADER" address="">
	   <item name="VERSION">0</item>
	   <item name="FAMILY">7</item>
	   <item name="MTR_TYPE">31</item>
	   <item name="PROGRAM">6</item>
	</table>
	<table name="COMPONENT_SPECIAL_DATES" address="">
	   <item name="PERFORM_DEMAND_RESET_ON_SEASON_CHANGE">0</item>
	   <item name="MAX_DAYS_BETWEEN_DMD_RESETS">0</item>
	   <item name="DAYS_BEFORE_END">-1</item>
	   <record name="DEMAND_RESET_TIME">
		  <item name="HOUR">0</item>
		  <item name="MINUTE">0</item>
	   </record>
	   <item name="EER_EXPORTABLE">1</item>
	   <item name="NBR_NON_RECURR_DATES">0</item>
	   <item name="NBR_RECURR_DATES">3</item>
	   <array name="RECURR_DATES">
		  <element index="0">
			 <record name="RECURR_DATE">
				<item name="SELECT">3</item>
				<item name="OFFSET">2</item>
				<item name="WEEKDAY">0</item>
				<item name="DAY">8</item>
			 </record>
			 <record name="CALENDAR_ACTION">
				<item name="CALENDAR_CTRL">1</item>
				<item name="DEMAND_RESET_FLAG">0</item>
				<item name="SELF_READ_FLAG">0</item>
				<item name="FILLER">0</item>
			 </record>
		  </element>
		  <element index="1">
			 <record name="RECURR_DATE">
				<item name="SELECT">11</item>
				<item name="OFFSET">2</item>
				<item name="WEEKDAY">0</item>
				<item name="DAY">1</item>
			 </record>
			 <record name="CALENDAR_ACTION">
				<item name="CALENDAR_CTRL">2</item>
				<item name="DEMAND_RESET_FLAG">0</item>
				<item name="SELF_READ_FLAG">0</item>
				<item name="FILLER">0</item>
			 </record>
		  </element>
		  <element index="2">
			 <record name="RECURR_DATE">
				<item name="SELECT">13</item>
				<item name="OFFSET">0</item>
				<item name="WEEKDAY">0</item>
				<item name="DAY">1</item>
			 </record>
			 <record name="CALENDAR_ACTION">
				<item name="CALENDAR_CTRL">19</item>
				<item name="DEMAND_RESET_FLAG">1</item>
				<item name="SELF_READ_FLAG">1</item>
				<item name="FILLER">0</item>
			 </record>
		  </element>
	   </array>
	</table></meterdata>
	<meterdata product="Metercat" version="6.4.2.0_12938" sdkversion="6.9.1.7561" meter="INSTRUMENTATION_PROFILING_1_0" utc_datetime="2019-01-04 20:35" comp_type="11" create_datetime="2018-02-23 11:59:16" create_user_id="{7CE2595E-FAC7-4965-ACA4-3D1FE65462EA}" guid="{ECA903E1-458D-46DB-BC6D-D99151F181F8}" hash="A22549A47F8DD4AF06EE6E3DB865E944" mod_datetime="2018-02-23 11:59:16" mod_user_id="{7CE2595E-FAC7-4965-ACA4-3D1FE65462EA}" name="A3R Load_Profile" mtr_family="1" mtr_type="15" pgm_type="2">
		<table name="COMPONENT_HEADER" address="">
		   <item name="VERSION">0</item>
		   <item name="FAMILY">1</item>
		   <item name="MTR_TYPE">15</item>
		   <item name="PROGRAM">2</item>
		</table>
		<table name="COMPONENT_INSTRUMENTATION_PROFILING" address="">
		   <array name="SETS">
			  <element index="0">
				 <item name="DAYS_OF_STORAGE">0</item>
				 <item name="INTERVAL">30</item>
				 <item name="NBR_CHANNELS">3</item>
				 <array name="CHANNELS">
					<element index="0">
					   <item name="MT_17_INDEX">165</item>
					   <item name="NAME"></item>
					   <item name="UOM">0</item>
					   <item name="ALGORITHM">0</item>
					</element>
					<element index="1">
					   <item name="MT_17_INDEX">183</item>
					   <item name="NAME"></item>
					   <item name="UOM">1</item>
					   <item name="ALGORITHM">0</item>
					</element>
					<element index="2">
					   <item name="MT_17_INDEX">166</item>
					   <item name="NAME"></item>
					   <item name="UOM">2</item>
					   <item name="ALGORITHM">0</item>
					</element>
				 </array>
			  </element>
			  <element index="1">
				 <item name="DAYS_OF_STORAGE">0</item>
				 <item name="INTERVAL">15</item>
				 <item name="NBR_CHANNELS">0</item>
			  </element>
		   </array>
		</table>
	</meterdata>

	<meterdata product="Metercat" version="6.4.2.0_12938" sdkversion="6.9.1.7561" meter="PULSES_1_0" utc_datetime="2019-01-04 20:35" comp_type="22" create_datetime="2018-02-23 11:59:16" create_user_id="{7CE2595E-FAC7-4965-ACA4-3D1FE65462EA}" guid="{375E20F7-9EE3-4549-BEE4-E2E72BF96FC2}" hash="C2BE0F03A8B7F70448BA5B2321FED708" mod_datetime="2018-02-23 11:59:16" mod_user_id="{7CE2595E-FAC7-4965-ACA4-3D1FE65462EA}" name="A3R Load_Profile" mtr_family="1" mtr_type="4" pgm_type="2">
		<table name="COMPONENT_HEADER" address="">
		   <item name="VERSION">0</item>
		   <item name="FAMILY">1</item>
		   <item name="MTR_TYPE">4</item>
		   <item name="PROGRAM">2</item>
		</table>
		<table name="COMPONENT_PULSES" address="">
		   <item name="DAYS_OF_STORAGE">0</item>
		   <item name="INTERVAL">30</item>
		   <item name="INTERVAL_DATA_MODE">0</item>
		   <item name="PULSE_DIVISOR">1</item>
		   <item name="NBR_QUANTITIES">2</item>
		   <array name="QUANTITIES">
			  <element index="0">0</element>
			  <element index="1">1</element>
		   </array>
		</table>
	</meterdata>
	<mappings>
		<!-- Rates -->
		<tag comp_type="6" format="hexBinary" number="2056" offset="53" length="16"/>
		<!-- Special Dates -->
		<tag comp_type="7" format="hexBinary" number="2056" offset="69" length="16"/>
		<!-- Instrumentation Profiling -->
		<tag comp_type="11" format="hexBinary" number="2056" offset="343" length="16"/>
		<!-- Load Profile -->
		<tag comp_type="22" format="hexBinary" number="2056" offset="359" length="16"/>
	</mappings>
</config-data>
<validations>
<!-- BEGIN;
END -->
</validations>
<operations>
<write number="2157" offset="0" length="1616">
<hexdata>
 01 EE EE 02 00 00 00 FF FF FF FF FF 67 B8 B2 5D
 E9 32 BB 8B D0 B1 BA A1 95 9A 27 F2 ED 03 44 00
 02 71 00 03 24 00 00 00 05 00 06 09 10 00 8F 3B
 51 A7 7D BE 2D 3A 83 49 36 F3 91 50 05 A9 06 06
 88 F1 01 BF E3 1C 03 27 EC 8A 77 03 01 4E 90 30
 FA 50 5D 3E 90 AD D2 99 9A 4A E2 79 07 42 3F 20
 22 0C DD 2A F4 C6 50 83 57 33 1E 4C 20 09 F0 66
 70 D9 7E FB 04 76 82 99 EA 24 1F FF C5 84 00 6B
 8D 86 89 29 30 1A 0D FC AA AC 12 70 7D 15 D6 0C
 1C 6D 17 C6 86 1E 5F C1 50 85 07 A8 7A 94 EC 13
 0C 10 4A 2B 24 EB BA FF 09 4A 96 76 66 42 7E C9
 E3 45 DC 21 A3 76 4E 57 02 BB 20 C4 15 10 AF 66
 ED 86 D1 E0 56 96 83 DC 0C 2C 7D 69 0E 8B 98 FF
 0F E2 D8 EA 22 1D 0F B3 3C 71 08 8F E0 10 E7 3B
 EB A8 47 3B 68 51 DC 2D E3 5C 23 70 BB 3F 52 B5
 78 A8 6C 98 90 E5 23 76 97 6E 28 90 18 36 72 08
 63 D9 5A 26 D3 C6 1A FB 0D D8 2C AA 80 4C 13 77
 39 9C 19 94 46 97 8A 02 61 E3 F0 79 71 96 1F 39
 FA 85 1D 35 13 BE 84 BE 9E 4D 0C 69 BE 80 92 B4
 E5 75 66 42 92 27 BA 62 1F 91 F0 2E 47 D0 F9 B9
 21 D5 EA BC 45 B1 D7 01 CA 05 1E 15 8D 23 57 1D
 D4 19 AF 29 9E A4 51 87 D6 1C 7E F0 68 E0 B1 B3
 21 7C 17 11 31 65 1A FC B2 98 53 41 BD 1F 88 BC
 10 57 58 24 12 4C 5C A0 43 B2 2F 18 1D C9 A3 11
 64 51 C1 FE 69 11 5F 9C 4E CD 64 31 8B 7D CA 61
 7F CC 77 C0 52 2D F1 4F 1C ED 30 54 7B AB F2 16
 62 63 98 C8 B8 6A 1B 13 72 2C B7 3B 41 2F D6 0A
 62 0E D3 14 BF 40 C1 2D 91 22 1E 06 AA 45 80 B5
 B5 D1 12 2D A3 FB 3D 0D E9 F9 8C 77 37 BA AE 0D
 7A 60 34 D8 B7 31 08 A6 02 22 64 89 1C 81 26 99
 39 9F 39 C7 37 0A A5 41 16 B9 44 8A 62 D1 96 E4
 F8 E1 70 A5 0F 70 38 7B FC 6B 1F 21 9D 0E 87 A1
 F9 5F 18 B5 76 4C 44 07 FB C2 BD D4 AC 5C 06 DE
 F7 29 D5 99 13 D7 C2 2D CA 7A 77 CF 3D F0 F0 62
 02 59 AD A8 5D C7 0B 7D 8E EB 0D 6B CC 03 81 EA
 07 CF 53 F6 DD A9 1D 09 63 47 15 B8 57 44 78 01
 A6 6C 3B 2B 4B C3 38 B8 0D 90 25 F6 F8 22 E4 28
 31 D4 79 15 7E 6D E6 B3 6B B6 20 EA C4 FA 8B 46
 36 F1 91 83 EA F6 3B 1E DA 56 46 0F 29 C6 BE BD
 C6 88 74 5B 50 79 3C 9B 0B E6 D8 3C 5C 3F CC 01
 E8 74 2E 39 AB 9A 17 B6 66 D8 08 3D E2 C4 8F 06
 AC DA E4 4D 2B E8 EB F1 78 0A 9C 56 71 59 C0 F4
 C2 E4 79 D1 E4 DF 56 32 73 92 76 86 B0 E5 5B FD
 D7 D2 6B 37 22 1D F9 BC 00 75 22 D9 53 9B AA B7
 1A CA 17 98 5E 1F 30 B2 3A 8A 4B 03 C6 C0 7F 9E
 09 D0 22 FE AE 51 A4 6B 63 58 3C 52 57 5D 5A F6
 10 97 76 B3 A8 72 66 49 29 8E D6 4E 4D 7F 6D 53
 E2 94 61 92 CF 40 F9 91 02 9A 0F 73 BF EB D7 E3
 32 DC 4D D9 7B 5E FA 9B 85 DD CE 60 6B 1A 4A C2
 2D 6C 44 23 E8 68 F9 1F 7A E7 28 C7 65 78 AE 3F
 34 27 18 7D 97 B0 3A 69 D0 B2 98 30 1D B9 64 71
 89 6B B4 80 C2 FF D5 99 DB A4 6F 69 A0 7E D4 95
 28 30 9C 29 EE 0F 26 77 2D 3C 9B 7C 05 E7 8C 41
 28 26 6B CB 76 8B 43 93 43 81 5C FB D9 C6 AB A7
 98 28 32 5D 21 20 35 06 F0 56 3C 5D 93 83 1E 8A
 0E AB 68 25 DA 51 B8 71 E3 9F 5F 1E AC D8 1F D9
 27 FB 70 A3 B4 CB C8 F6 0C 4A AC B5 09 5E F9 90
 3D 6A BF 77 78 D0 F8 30 C4 49 29 62 8E A7 34 F9
 72 43 53 BA 5E 2D C0 16 F7 F6 43 AE B8 E9 06 7A
 BD 05 33 D1 E5 08 90 BF 23 24 A2 DB CD 9B 73 5F
 F6 77 87 9F B7 D1 A8 CF 60 A1 EA 19 DF BC 63 D3
 22 49 8F A6 BF 49 11 5C B8 83 74 88 4A A1 14 49
 33 B7 64 14 00 C4 6F B6 7E 83 BD 21 6F 3F EF AC
 84 80 B6 EB DE 57 CE C3 98 D5 E1 F8 8E F2 9C A2
 10 59 EA C5 4C 1A B9 FC A7 54 93 62 C4 D9 49 5B
 90 03 A8 2D 99 9B 1F 31 30 E8 96 4F 25 53 56 3A
 13 60 B0 3C 27 70 54 FE 13 C9 02 65 EE 16 03 96
 54 A2 B0 F7 E2 01 B2 82 32 89 EA 15 4B 18 3E 95
 94 9D 48 1D 09 78 AF F6 50 40 D5 0C DD 53 B2 88
 E5 B5 F4 B4 93 70 59 F1 9B 67 02 24 A4 32 0D BD
 41 3C FB 75 5B 8D 5D EF 53 69 23 07 1F 77 82 58
 A1 77 2B CF 1A 1E BB 2C 33 A2 15 97 BD C8 1E 15
 AE 09 75 3B AA B0 20 80 40 E4 BC 7C 00 20 56 04
 B8 DC 20 2F 4C 38 52 D6 AE E6 68 CB CE 9B 71 77
 27 59 C7 4E 2D 24 98 A8 86 D6 B9 33 1C 37 1A 93
 58 4A 2F D2 D0 6E 63 C4 F3 AD 94 FB 68 6C 00 42
 8B 8D 0C 09 5E 44 EC 50 D6 0D 85 F4 7E 8F EF 2D
 79 73 4E F5 4A 16 02 E9 A6 B9 A8 72 D3 DE 21 DC
 2E A5 AA 7B 6A A1 3C 8A 3D 57 C3 9F 27 0F 05 18
 AE E9 7B 3F B7 D1 41 49 4F E1 9D D2 31 AE 95 08
 F2 2F D2 BF D1 D4 97 7B 01 5E 02 57 3D 56 B7 7A
 CD 1A 87 B4 6F 3B 76 59 6B 4B 55 90 B0 00 66 94
 70 83 C8 5D 67 FE 38 D9 E9 E5 10 61 6D 45 CD BF
 52 11 43 8E F6 D9 F9 87 88 AA C9 B6 A3 60 43 C1
 3E 60 1D 2A 94 1F FE 55 87 25 C3 4F 60 D8 A0 FF
 5A E1 CC AA 03 8C EC 8B CC 98 68 7F 01 18 2E 9A
 A8 FE 46 13 8F CF 14 6D 4C 5C 7A 8A BC 09 F0 A2
 F2 F6 04 17 1A D1 0B DB 09 39 2F A0 96 71 25 F9
 8D 4C 32 E1 BF 41 D2 F7 E9 02 FB 3E 92 16 29 2B
 01 41 DE 73 1B 02 FF FB 35 5F EB 3F 7C 0D 75 1D
 82 31 F5 CC E1 2F 93 6D A4 16 00 53 97 9B DF CB
 36 E4 15 65 A5 EE 3D DA EB 86 A2 32 A6 38 44 C6
 CE DF 54 0E 9B 5D 1F 62 54 E7 70 ED 1B 54 F6 FF
 E3 77 A1 39 5C 80 64 20 65 AB 57 89 51 D9 75 BA
 AD 7A B4 C3 FD 87 44 5F 7F 7C B2 58 6A 7D 6A 64
 36 B0 C9 E2 6B 94 2F 3D 53 17 50 A8 CA FF FC 39
 26 2C 08 AF A3 9A DC 27 A6 C3 F0 A6 88 E0 E0 63
 42 33 6E 4D 38 9C B8 0D 17 39 30 E4 D7 70 DC 69
 DF EA CB 9E B8 6C EB 9B 16 FB EB 69 BF E3 F7 24
 E8 5E 95 79 46 00 37 A4 84 D6 EF EF 00 E9 76 C7
 B7 46 CA 7E 3F 0C 8A 38 AD F9 D0 79 B6 96 7E 3B
</hexdata>
</write>
<execute number="2157" length="3">
<hexdata>
 00 EE EE
</hexdata>
</execute>
<read number="2158" offset="0" length="289"/>
</operations>
<verifications>
<!-- BEGIN;
    IF NOT meter.MT_110_CONFIG_RESULTS.FLAGS.IS_VALID THEN
        THROW i"The remote queue execution failed.";
    END;
    IF meter.MT_110_CONFIG_RESULTS.STATUS.RESULT_CODE <> 0 THEN
        THROW i"The queued requests failed.";
    END;
END -->
<defn>MT_110_CONFIG_RESULTS.FLAGS
<table-item number="2158" offset="2" length="1" format="unsignedInt"/>
</defn>
<defn>MT_110_CONFIG_RESULTS.FLAGS.IS_VALID
<mml:apply>
<mml:csymbol>bitmask</mml:csymbol>
<mml:ci>MT_110_CONFIG_RESULTS.FLAGS</mml:ci>
<mml:cn type="integer" base="16">80</mml:cn>
</mml:apply>
</defn>
<defn>MT_110_CONFIG_RESULTS.STATUS.RESULT_CODE
<table-item number="2158" offset="3" length="1" format="unsignedInt"/>
</defn>
<mml:math>
 <mml:cerror><mml:csymbol>The remote queue execution failed.</mml:csymbol>
 <mml:apply><mml:not />
 <mml:ci>MT_110_CONFIG_RESULTS.FLAGS.IS_VALID</mml:ci>
 </mml:apply>
 </mml:cerror>
 <mml:cerror><mml:csymbol>The queued requests failed.</mml:csymbol>
 <mml:apply><mml:neq />
 <mml:ci>MT_110_CONFIG_RESULTS.STATUS.RESULT_CODE</mml:ci>
 <mml:cn type="integer">0</mml:cn>
 </mml:apply>
 </mml:cerror>
</mml:math>
</verifications>
</eer>)testString";

struct test_MeterProgrammingManager : Cti::MeterProgramming::MeterProgrammingManager
{
    RawProgram loadRawProgram(const std::string guid) override
    {
        return { { testProgram.begin(), testProgram.end() }, 
                 { testPassword.begin(), testPassword.end() } };
    }
};

BOOST_AUTO_TEST_CASE(test_getProgram)
{
    test_MeterProgrammingManager mgr;

    const auto buf = mgr.getProgram("not-really-a-guid");

    BOOST_CHECK_EQUAL(buf.size(), 1742);
}

BOOST_AUTO_TEST_SUITE_END()

