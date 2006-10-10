-- Script to populate defualt Configuration, Category and Item type data including mapping between the types

-- Remove all existing data
delete from dcconfigurationcategorytype;
delete from dcdeviceconfigurationtype;
delete from dcconfigurationcategory;
delete from dcdeviceconfiguration;
delete from dcconfiguration;
delete from dcconfigurationtype;
delete from dccategoryitemtype;
delete from dccategoryitem;
delete from dccategory;
delete from dccategorytype;
delete from dcitemvalue;
delete from dcitemtype;


-- Insert default data


-- Configuration types

INSERT INTO dcconfigurationtype VALUES (1, 'MCT410', 'MCT Configuration', 'MCT Configuration')
INSERT INTO dcconfigurationtype VALUES (2, 'CBC', 'CBC Configuration', 'CBC Configuration')
INSERT INTO dcconfigurationtype VALUES (3, 'MCT470', 'MCT 470 Configuration', 'MCT 470 Configuration')

-- Device type / Configuration Type mappings

INSERT INTO dcdeviceconfigurationtype VALUES (1, 'MCT-410IL')
INSERT INTO dcdeviceconfigurationtype VALUES (2, 'CBC Expresscom')
INSERT INTO dcdeviceconfigurationtype VALUES (3, 'MCT-470')

-- Category Types

-- MCT 470
INSERT INTO dccategorytype VALUES (1, 'MCT Addressing', 'Addressing', null, 'high', 'Addressing')
INSERT INTO dccategorytype VALUES (2, 'MCT daylight savings time', 'Daylight Savings Time', null, 'low', 'Daylight Savings Time')
INSERT INTO dccategorytype VALUES (3, 'MCT Options', 'Options', null, 'low', 'Options')
INSERT INTO dccategorytype VALUES (4, 'MCT Demand And Load Profile', 'Demand And Load Profile', 'Load Profile', 'low', 'Demand Load Profile')
INSERT INTO dccategorytype VALUES (5, 'MCT Voltage Threshold', 'Voltage Threshold', null, 'low', 'Voltage Threshold')
INSERT INTO dccategorytype VALUES (7, 'MCT Long Load Profile', 'Long Load Profile', 'Load Profile', 'low', 'Long Load Profile')
INSERT INTO dccategorytype VALUES (8, 'MCT Holidays', 'Holidays', null, 'low', 'Holiday')
INSERT INTO dccategorytype VALUES (9, 'MCT Load Profile Channels', 'Load Profile Channels', 'Load Profile', 'low', 'Load Profile Channels')
INSERT INTO dccategorytype VALUES (10, 'MCT Relays', 'Relays', null, 'low', 'Relays')
INSERT INTO dccategorytype VALUES (11, 'MCT Precanned Table', 'Precanned Table', null, 'low', 'Precanned Table')
INSERT INTO dccategorytype VALUES (12, 'MCT System Options', 'System Options', null, 'low', 'System Options')
INSERT INTO dccategorytype VALUES (24, 'MCT TOU', 'TOU', 'Time of Use', 'low', 'Time of Use Schedule')
INSERT INTO dccategorytype VALUES (25, 'MCT Centron', 'Centron', null, 'low', 'Centron')
INSERT INTO dccategorytype VALUES (26, 'MCT DNP', 'DNP', null, 'low', 'DNP')

-- MCT 410
-- These are duplicated here so that the 410 categories can have different items than the 470's.
INSERT INTO dccategorytype VALUES (27, 'MCT Addressing', 'Addressing', null, 'high', 'Addressing')
INSERT INTO dccategorytype VALUES (28, 'MCT daylight savings time', 'Daylight Savings Time', null, 'low', 'Daylight Savings Time')
INSERT INTO dccategorytype VALUES (29, 'MCT Options', 'Options', null, 'low', 'Options')
INSERT INTO dccategorytype VALUES (30, 'MCT Demand And Load Profile', 'Demand And Load Profile', 'Load Profile', 'low', 'Demand Load Profile')
INSERT INTO dccategorytype VALUES (31, 'MCT Voltage Threshold', 'Voltage Threshold', null, 'low', 'Voltage Threshold')
INSERT INTO dccategorytype VALUES (32, 'MCT Disconnect', 'Disconnect', null, 'low', 'Disconnect')
INSERT INTO dccategorytype VALUES (33, 'MCT Long Load Profile', 'Long Load Profile', 'Load Profile', 'low', 'Long Load Profile')
INSERT INTO dccategorytype VALUES (34, 'MCT Holidays', 'Holidays', null, 'low', 'Holiday')
INSERT INTO dccategorytype VALUES (36, 'MCT TOU', 'TOU', 'Time of Use', 'low', 'Time of Use Schedule')
INSERT INTO dccategorytype VALUES (37, 'MCT Rate Schedule', 'Rate Schedule', 'Time of Use', 'low', 'Rate Schedule')

-- CBC
INSERT INTO dccategorytype VALUES (13, 'CBC Voltage', 'Voltage', null, 'high', 'Voltage')
INSERT INTO dccategorytype VALUES (14, 'CBC Comms Lost', 'Comms Lost', null, 'low', 'Comms lost')
INSERT INTO dccategorytype VALUES (15, 'CBC Neutral Current', 'Neutral Current', null, 'low', 'Neutral Current')
INSERT INTO dccategorytype VALUES (16, 'CBC Fault Detection', 'Fault Detection', null, 'low', 'Fault Detection')
INSERT INTO dccategorytype VALUES (17, 'CBC Season 1 Time and Temp', 'Season 1 Time and Temp', 'Time and Temp', 'low', 'Season 1 Time and Temp')
INSERT INTO dccategorytype VALUES (18, 'CBC Season 2 Time and Temp', 'Season 2 Time and Temp', 'Time and Temp', 'low', 'Season 2 Time and Temp')
INSERT INTO dccategorytype VALUES (19, 'CBC Control Times', 'Control Times', null, 'low', 'Control Times')
INSERT INTO dccategorytype VALUES (20, 'CBC Data Logging', 'Data Logging', null, 'low', 'Data Logging')
INSERT INTO dccategorytype VALUES (21, 'CBC DNP', 'DNP', null, 'low', 'DNP')
INSERT INTO dccategorytype VALUES (22, 'CBC UDP', 'UDP', null, 'low', 'UDP')
INSERT INTO dccategorytype VALUES (23, 'CBC Addressing', 'Addressing', null, 'high', 'Addressing')



-- Configuration / Category mappings

-- MCT410 config
INSERT INTO dcconfigurationcategorytype VALUES (1, 27)
INSERT INTO dcconfigurationcategorytype VALUES (1, 28)
INSERT INTO dcconfigurationcategorytype VALUES (1, 29)
INSERT INTO dcconfigurationcategorytype VALUES (1, 30)
INSERT INTO dcconfigurationcategorytype VALUES (1, 31)
INSERT INTO dcconfigurationcategorytype VALUES (1, 32)
INSERT INTO dcconfigurationcategorytype VALUES (1, 33)
INSERT INTO dcconfigurationcategorytype VALUES (1, 34)
INSERT INTO dcconfigurationcategorytype VALUES (1, 36)
INSERT INTO dcconfigurationcategorytype VALUES (1, 37)

-- CBC config
INSERT INTO dcconfigurationcategorytype VALUES (2, 13)
INSERT INTO dcconfigurationcategorytype VALUES (2, 14)
INSERT INTO dcconfigurationcategorytype VALUES (2, 15)
INSERT INTO dcconfigurationcategorytype VALUES (2, 16)
INSERT INTO dcconfigurationcategorytype VALUES (2, 17)
INSERT INTO dcconfigurationcategorytype VALUES (2, 18)
INSERT INTO dcconfigurationcategorytype VALUES (2, 19)
INSERT INTO dcconfigurationcategorytype VALUES (2, 20)
INSERT INTO dcconfigurationcategorytype VALUES (2, 21)
INSERT INTO dcconfigurationcategorytype VALUES (2, 22)
INSERT INTO dcconfigurationcategorytype VALUES (2, 23)

-- MCT470 config
INSERT INTO dcconfigurationcategorytype VALUES (3, 1)
INSERT INTO dcconfigurationcategorytype VALUES (3, 2)
INSERT INTO dcconfigurationcategorytype VALUES (3, 3)
INSERT INTO dcconfigurationcategorytype VALUES (3, 4)
INSERT INTO dcconfigurationcategorytype VALUES (3, 5)
INSERT INTO dcconfigurationcategorytype VALUES (3, 7)
INSERT INTO dcconfigurationcategorytype VALUES (3, 8)
INSERT INTO dcconfigurationcategorytype VALUES (3, 9)
INSERT INTO dcconfigurationcategorytype VALUES (3, 10)
INSERT INTO dcconfigurationcategorytype VALUES (3, 11)
INSERT INTO dcconfigurationcategorytype VALUES (3, 12)
INSERT INTO dcconfigurationcategorytype VALUES (3, 24)
INSERT INTO dcconfigurationcategorytype VALUES (3, 25)
INSERT INTO dcconfigurationcategorytype VALUES (3, 26)


-- Item types

-- MCT

-- Addressing
INSERT INTO dcitemtype VALUES (2, 'Bronze Address', 'Bronze Address', 'numeric', 'Y', 0, 255, null, 'Bronze address')
INSERT INTO dcitemtype VALUES (3, 'Lead Address', 'Lead Address', 'numeric', 'Y', 0, 4096, null, 'Lead address')
INSERT INTO dcitemtype VALUES (4, 'Service Provider ID', 'Service Provider ID', 'numeric', 'Y', 0, 255, null, 'Service Provider Id')
INSERT INTO dcitemtype VALUES (5, 'Collection Address', 'Collection Address', 'numeric', 'Y', 0, 50, null, 'Collection Address')

-- DST
INSERT INTO dcitemtype VALUES (7, 'DST Begin', 'DST Begin', '', 'Y', 0, 0, null, 'The time which DST begins, sent as number of seconds since January 1, 1970')
INSERT INTO dcitemtype VALUES (8, 'DST End', 'DST End', '', 'Y', 0, 0, null, 'The Time/Date which DST ends, sent as number of seconds since January 1, 1970')
INSERT INTO dcitemtype VALUES (9, 'Time Zone Offset', 'Time Zone Offset', '', 'Y', 0, 0, null, 'Signed offset from UTC presented in 15-minute increments.  EST = -20, CST = -24 etc.')

-- Options
INSERT INTO dcitemtype VALUES (11, 'Alarm Mask Meter', 'Alarm Mask Meter', '', 'N', 0, 0, null, '')
INSERT INTO dcitemtype VALUES (12, 'Alarm Mask Event 1', 'Alarm Mask Event 1', '', 'N', 0, 0, null, 'Bit field, bits ordered 76543210, defined as: 7 NegativeTimeSync 6 DST Change 5 Holiday Flag 4 RTC Adjusted 3 Power Fail Carryover 2 Stack Overflow 1 Electronic Meter Communication Error 0 Power Fail Event')
INSERT INTO dcitemtype VALUES (13, 'Alarm Mask Event 2', 'Alarm Mask Event 2', '', 'N', 0, 0, null, 'Bit field, bits ordered 76543210, defined as: 7,6,5 Unused. 4: Address Corruption, 3: Zero Usage Channel 4, 2: Zero Usage Channel 3, 1: Zero Usage Channel 2. 0: Zero Usage Channel 1')
INSERT INTO dcitemtype VALUES (14, 'Time Adjust Tolerance', 'Time Adjust Tolerance', '', 'N', 0, 0, null, '')
INSERT INTO dcitemtype VALUES (15, 'Configuration', 'Configuration', '', 'N', 0, 0, null, 'For MCT 470')
INSERT INTO dcitemtype VALUES (177, 'Configuration', 'Configuration', '', 'N', 0, 0, null, 'For MCT 410')
INSERT INTO dcitemtype VALUES (16, 'Options', 'Options', '', 'N', 0, 0, null, '')
INSERT INTO dcitemtype VALUES (17, 'Outage Cycles', 'Outage Cycles', '', 'N', 0, 0, null, '')

-- Demand Load Profile
INSERT INTO dcitemtype VALUES (19, 'Demand Interval', 'Demand Interval', 'numeric', 'Y', 1, 60, null, 'Interval in minutes over which most recent demand is calculated')
INSERT INTO dcitemtype VALUES (20, 'Load Profile Interval', 'Load Profile Interval', 'numeric', 'Y', 0, 60, null, 'Interval in minutes over which load profile values are kept. Valid values are 5, 15, 30, and 60')
INSERT INTO dcitemvalue VALUES (20, '5', 1)
INSERT INTO dcitemvalue VALUES (20, '15', 2)
INSERT INTO dcitemvalue VALUES (20, '30', 3)
INSERT INTO dcitemvalue VALUES (20, '60', 4)

INSERT INTO dcitemtype VALUES (21, 'Voltage LP Interval', 'Voltage LP Interval', 'numeric', 'Y', 0, 50, null, 'Interval in minutes over which voltage load profile values are kept. Valid values are 5, 15, 30, and 60')
INSERT INTO dcitemvalue VALUES (21, '5', 1)
INSERT INTO dcitemvalue VALUES (21, '15', 2)
INSERT INTO dcitemvalue VALUES (21, '30', 3)
INSERT INTO dcitemvalue VALUES (21, '60', 4)

INSERT INTO dcitemtype VALUES (22, 'Demand V Interval', 'Demand V Interval', 'numeric', 'Y', 0, 255, null, 'Interval over which demand is averaged over, in 15 second increments. 0 = disabled, 1 = 15 seconds, 2 = 30 seconds, ect..')
INSERT INTO dcitemtype VALUES (23, 'Load Profile Interval 2', 'Load Profile Interval 2', 'numeric', 'Y', 0, 60, null, 'Interval in minutes over which load profile values are kept. Valid values are 5, 15, 30, and 60')
INSERT INTO dcitemvalue VALUES (23, '5', 1)
INSERT INTO dcitemvalue VALUES (23, '15', 2)
INSERT INTO dcitemvalue VALUES (23, '30', 3)
INSERT INTO dcitemvalue VALUES (23, '60', 4)

-- V Threshold
INSERT INTO dcitemtype VALUES (25, 'Under VThreshold', 'Under VThreshold', 'numeric', 'Y', 0, 65535, null, 'Under Voltage Threshold, stored as a multiple of .1 volts. For example, 2200 = 220 volts.')
INSERT INTO dcitemtype VALUES (26, 'Over VThreshold', 'Over VThreshold', 'numeric', 'Y', 0, 65535, null, 'Over Voltage Threshold, stored as a multiple of .1 volts. For example, 2200 = 220 volts.')

-- Disconnect
INSERT INTO dcitemtype VALUES (28, 'Demand Threshold', 'Demand Threshold', 'numeric', 'Y', 0, 400000, null, 'Threshold measured in WHr per demand interval')
INSERT INTO dcitemtype VALUES (29, 'Connect Delay', 'Connect Delay', 'numeric', 'Y', 0, 10, null, 'Time in minutes for a reconnect')
INSERT INTO dcitemtype VALUES (24, 'Cycling Disconnect Minutes', 'Cycling Disconnect Minutes', 'numeric', 'Y', 5, 60, null, 'Time in disconnected state per cycle (5 minute increments)')
INSERT INTO dcitemtype VALUES (27, 'Cycling Connect Minutes', 'Cycling Connect Minutes', 'numeric', 'Y', 5, 60, null, 'Time in connected state per cycle (5 minute increments)')

-- Long Load Profile
INSERT INTO dcitemtype VALUES (31, 'Channel 1 Length', 'Channel 1 Length', 'numeric', 'Y', 0, 255, null, 'Number of days at 15 minute interval')
INSERT INTO dcitemtype VALUES (32, 'Channel 2 Length', 'Channel 2 Length', 'numeric', 'Y', 0, 255, null, 'Number of days at 15 minute interval')
INSERT INTO dcitemtype VALUES (33, 'Channel 3 Length', 'Channel 3 Length', 'numeric', 'Y', 0, 255, null, 'Number of days at 15 minute interval')
INSERT INTO dcitemtype VALUES (34, 'Channel 4 Length', 'Channel 4 Length', 'numeric', 'Y', 0, 255, null, 'Number of days at 15 minute interval')

-- Holiday
INSERT INTO dcitemtype VALUES (36, 'Holiday Date 1', 'Holiday Date 1', 'date', 'Y', 0, 50, null, 'Date of holiday1, begins and ends at midnight local time')
INSERT INTO dcitemtype VALUES (37, 'Holiday Date 2', 'Holiday Date 2', 'date', 'Y', 0, 50, null, 'Date of holiday2, begins and ends at midnight local time')
INSERT INTO dcitemtype VALUES (38, 'Holiday Date 3', 'Holiday Date 3', 'date', 'Y', 0, 50, null, 'Date of holiday3, begins and ends at midnight local time')

-- Load Profile Channels
INSERT INTO dcitemtype VALUES (40, 'Channel Config 1', 'Channel Config 1', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (41, 'Channel Config 2', 'Channel Config 2', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (42, 'Channel Config 3', 'Channel Config 3', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (43, 'Channel Config 4', 'Channel Config 4', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (44, 'Meter Ratio 1', 'Meter Ratio 1', '', 'N', 0, 50, null, 'For each pulse, the accumulator is incremented by this count')
INSERT INTO dcitemtype VALUES (45, 'Meter Ratio 2', 'Meter Ratio 2', '', 'N', 0, 50, null, 'For each pulse, the accumulator is incremented by this count')
INSERT INTO dcitemtype VALUES (46, 'Meter Ratio 3', 'Meter Ratio 3', '', 'N', 0, 50, null, 'For each pulse, the accumulator is incremented by this count')
INSERT INTO dcitemtype VALUES (47, 'Meter Ratio 4', 'Meter Ratio 4', '', 'N', 0, 50, null, 'For each pulse, the accumulator is incremented by this count')
INSERT INTO dcitemtype VALUES (48, 'K Ratio 1', 'K Ratio 1', '', 'N', 0, 50, null, 'Consumption resolution. When Accumulator reaches this value, it is cleared and meter reading is incremented')
INSERT INTO dcitemtype VALUES (49, 'K Ratio 2', 'K Ratio 2', '', 'N', 0, 50, null, 'Consumption resolution. When Accumulator reaches this value, it is cleared and meter reading is incremented')
INSERT INTO dcitemtype VALUES (50, 'K Ratio 3', 'K Ratio 3', '', 'N', 0, 50, null, 'Consumption resolution. When Accumulator reaches this value, it is cleared and meter reading is incremented')
INSERT INTO dcitemtype VALUES (51, 'K Ratio 4', 'K Ratio 4', '', 'N', 0, 50, null, 'Consumption resolution. When Accumulator reaches this value, it is cleared and meter reading is incremented')

-- Relays
INSERT INTO dcitemtype VALUES (53, 'Relay A Timer', 'Relay A Timer', '', 'N', 0, 50, null, 'Time the relay will remain closed in 250ms increments')
INSERT INTO dcitemtype VALUES (54, 'Relay B Timer', 'Relay B Timer', '', 'N', 0, 50, null, 'Time the relay will remain closed in 250ms increments')

-- Precanned Table
INSERT INTO dcitemtype VALUES (56, 'Table Read Interval', 'Table Read Interval', '', 'N', 0, 50, null, 'Seconds between each read of the attached IED. In 15 second increments.')
INSERT INTO dcitemtype VALUES (57, 'Meter Number', 'Meter Number', '', 'N', 0, 50, null, 'Meter number to be read')
INSERT INTO dcitemtype VALUES (58, 'Table Type', 'Table Type', '', 'N', 0, 50, null, 'Table to use. Use caution when changing.')

-- System Options
INSERT INTO dcitemtype VALUES (61, 'Demand Meters to Scan', 'Demand Meters to Scan', '', 'N', 0, 50, null, 'May be "All", "IED", or "Pulse"')

-- TOU Schedule
INSERT INTO dcitemtype VALUES (201, 'Schedule1Time1', 'Schedule1Time1', 'tou', 'Y', 0, 50, null, 'Rate change time 1')
INSERT INTO dcitemtype VALUES (202, 'Schedule1Rate1', 'Schedule1Rate1', 'tou', 'Y', 0, 50, null, 'Rate 1')
INSERT INTO dcitemtype VALUES (203, 'Schedule1Time2', 'Schedule1Time2', 'tou', 'Y', 0, 50, null, 'Rate change time 2')
INSERT INTO dcitemtype VALUES (204, 'Schedule1Rate2', 'Schedule1Rate2', 'tou', 'Y', 0, 50, null, 'Rate 2')
INSERT INTO dcitemtype VALUES (205, 'Schedule1Time3', 'Schedule1Time3', 'tou', 'Y', 0, 50, null, 'Rate change time 3')
INSERT INTO dcitemtype VALUES (206, 'Schedule1Rate3', 'Schedule1Rate3', 'tou', 'Y', 0, 50, null, 'Rate 3')
INSERT INTO dcitemtype VALUES (207, 'Schedule1Time4', 'Schedule1Time4', 'tou', 'Y', 0, 50, null, 'Rate change time 4')
INSERT INTO dcitemtype VALUES (208, 'Schedule1Rate4', 'Schedule1Rate4', 'tou', 'Y', 0, 50, null, 'Rate 4')
INSERT INTO dcitemtype VALUES (209, 'Schedule1Time5', 'Schedule1Time5', 'tou', 'Y', 0, 50, null, 'Rate change time 5')
INSERT INTO dcitemtype VALUES (210, 'Schedule1Rate5', 'Schedule1Rate5', 'tou', 'Y', 0, 50, null, 'Rate 5')
INSERT INTO dcitemtype VALUES (211, 'Schedule1Time0', 'Schedule1Time0', 'tou', 'Y', 0, 50, null, 'Midnight Rate change')
INSERT INTO dcitemtype VALUES (212, 'Schedule1Rate0', 'Schedule1Rate0', 'tou', 'Y', 0, 50, null, 'Midnight Rate')

INSERT INTO dcitemtype VALUES (221, 'Schedule2Time1', 'Schedule2Time1', 'tou', 'Y', 0, 50, null, 'Rate change time 1')
INSERT INTO dcitemtype VALUES (222, 'Schedule2Rate1', 'Schedule2Rate1', 'tou', 'Y', 0, 50, null, 'Rate 1')
INSERT INTO dcitemtype VALUES (223, 'Schedule2Time2', 'Schedule2Time2', 'tou', 'Y', 0, 50, null, 'Rate change time 2')
INSERT INTO dcitemtype VALUES (224, 'Schedule2Rate2', 'Schedule2Rate2', 'tou', 'Y', 0, 50, null, 'Rate 2')
INSERT INTO dcitemtype VALUES (225, 'Schedule2Time3', 'Schedule2Time3', 'tou', 'Y', 0, 50, null, 'Rate change time 3')
INSERT INTO dcitemtype VALUES (226, 'Schedule2Rate3', 'Schedule2Rate3', 'tou', 'Y', 0, 50, null, 'Rate 3')
INSERT INTO dcitemtype VALUES (227, 'Schedule2Time4', 'Schedule2Time4', 'tou', 'Y', 0, 50, null, 'Rate change time 4')
INSERT INTO dcitemtype VALUES (228, 'Schedule2Rate4', 'Schedule2Rate4', 'tou', 'Y', 0, 50, null, 'Rate 4')
INSERT INTO dcitemtype VALUES (229, 'Schedule2Time5', 'Schedule2Time5', 'tou', 'Y', 0, 50, null, 'Rate change time 5')
INSERT INTO dcitemtype VALUES (230, 'Schedule2Rate5', 'Schedule2Rate5', 'tou', 'Y', 0, 50, null, 'Rate 5')
INSERT INTO dcitemtype VALUES (231, 'Schedule2Time0', 'Schedule2Time0', 'tou', 'Y', 0, 50, null, 'Midnight Rate change')
INSERT INTO dcitemtype VALUES (232, 'Schedule2Rate0', 'Schedule2Rate0', 'tou', 'Y', 0, 50, null, 'Midnight Rate')

INSERT INTO dcitemtype VALUES (241, 'Schedule3Time1', 'Schedule3Time1', 'tou', 'Y', 0, 50, null, 'Rate change time 1')
INSERT INTO dcitemtype VALUES (242, 'Schedule3Rate1', 'Schedule3Rate1', 'tou', 'Y', 0, 50, null, 'Rate 1')
INSERT INTO dcitemtype VALUES (243, 'Schedule3Time2', 'Schedule3Time2', 'tou', 'Y', 0, 50, null, 'Rate change time 2')
INSERT INTO dcitemtype VALUES (244, 'Schedule3Rate2', 'Schedule3Rate2', 'tou', 'Y', 0, 50, null, 'Rate 2')
INSERT INTO dcitemtype VALUES (245, 'Schedule3Time3', 'Schedule3Time3', 'tou', 'Y', 0, 50, null, 'Rate change time 3')
INSERT INTO dcitemtype VALUES (246, 'Schedule3Rate3', 'Schedule3Rate3', 'tou', 'Y', 0, 50, null, 'Rate 3')
INSERT INTO dcitemtype VALUES (247, 'Schedule3Time4', 'Schedule3Time4', 'tou', 'Y', 0, 50, null, 'Rate change time 4')
INSERT INTO dcitemtype VALUES (248, 'Schedule3Rate4', 'Schedule3Rate4', 'tou', 'Y', 0, 50, null, 'Rate 4')
INSERT INTO dcitemtype VALUES (249, 'Schedule3Time5', 'Schedule3Time5', 'tou', 'Y', 0, 50, null, 'Rate change time 5')
INSERT INTO dcitemtype VALUES (250, 'Schedule3Rate5', 'Schedule3Rate5', 'tou', 'Y', 0, 50, null, 'Rate 5')
INSERT INTO dcitemtype VALUES (251, 'Schedule3Time0', 'Schedule3Time0', 'tou', 'Y', 0, 50, null, 'Midnight Rate change')
INSERT INTO dcitemtype VALUES (252, 'Schedule3Rate0', 'Schedule3Rate0', 'tou', 'Y', 0, 50, null, 'Midnight Rate')

INSERT INTO dcitemtype VALUES (261, 'Schedule4Time1', 'Schedule4Time1', 'tou', 'Y', 0, 50, null, 'Rate change time 1')
INSERT INTO dcitemtype VALUES (262, 'Schedule4Rate1', 'Schedule4Rate1', 'tou', 'Y', 0, 50, null, 'Rate 1')
INSERT INTO dcitemtype VALUES (263, 'Schedule4Time2', 'Schedule4Time2', 'tou', 'Y', 0, 50, null, 'Rate change time 2')
INSERT INTO dcitemtype VALUES (264, 'Schedule4Rate2', 'Schedule4Rate2', 'tou', 'Y', 0, 50, null, 'Rate 2')
INSERT INTO dcitemtype VALUES (265, 'Schedule4Time3', 'Schedule4Time3', 'tou', 'Y', 0, 50, null, 'Rate change time 3')
INSERT INTO dcitemtype VALUES (266, 'Schedule4Rate3', 'Schedule4Rate3', 'tou', 'Y', 0, 50, null, 'Rate 3')
INSERT INTO dcitemtype VALUES (267, 'Schedule4Time4', 'Schedule4Time4', 'tou', 'Y', 0, 50, null, 'Rate change time 4')
INSERT INTO dcitemtype VALUES (268, 'Schedule4Rate4', 'Schedule4Rate4', 'tou', 'Y', 0, 50, null, 'Rate 4')
INSERT INTO dcitemtype VALUES (269, 'Schedule4Time5', 'Schedule4Time5', 'tou', 'Y', 0, 50, null, 'Rate change time 5')
INSERT INTO dcitemtype VALUES (270, 'Schedule4Rate5', 'Schedule4Rate5', 'tou', 'Y', 0, 50, null, 'Rate 5')
INSERT INTO dcitemtype VALUES (271, 'Schedule4Time0', 'Schedule4Time0', 'tou', 'Y', 0, 50, null, 'Midnight Rate change')
INSERT INTO dcitemtype VALUES (272, 'Schedule4Rate0', 'Schedule4Rate0', 'tou', 'Y', 0, 50, null, 'Midnight Rate')

-- Rate Schedule
INSERT INTO dcitemtype VALUES (200, 'Default Rate', 'Default Rate', 'tou', 'Y', 0, 50, null, 'Default rate')

INSERT INTO dcitemtype VALUES (281, 'Monday', 'Monday', 'tou', 'Y', 0, 50, null, 'Who knows.')
INSERT INTO dcitemtype VALUES (282, 'Tuesday', 'Tuesday', 'tou', 'Y', 0, 50, null, 'Who knows.')
INSERT INTO dcitemtype VALUES (283, 'Wednesday', 'Wednesday', 'tou', 'Y', 0, 50, null, 'Who knows.')
INSERT INTO dcitemtype VALUES (284, 'Thursday', 'Thursday', 'tou', 'Y', 0, 50, null, 'Who knows.')
INSERT INTO dcitemtype VALUES (285, 'Friday', 'Friday', 'tou', 'Y', 0, 50, null, 'Who knows.')
INSERT INTO dcitemtype VALUES (286, 'Saturday', 'Saturday', 'tou', 'Y', 0, 50, null, 'Who knows.')
INSERT INTO dcitemtype VALUES (287, 'Sunday', 'Sunday', 'tou', 'Y', 0, 50, null, 'Who knows.')
INSERT INTO dcitemtype VALUES (288, 'Holiday', 'Holiday', 'tou', 'Y', 0, 50, null, 'Who knows.')


-- Centron
INSERT INTO dcitemtype VALUES (147, 'Parameters', 'Parameters', '', 'N', 0, 50, null, 'Use great caution. Is a bitfield.')
INSERT INTO dcitemtype VALUES (148, 'Transformer Ratio', 'Transformer Ratio', 'numeric', 'N', 1, 255, null, '')

-- MCT DNP
INSERT INTO dcitemtype VALUES (149, 'collection 1 binary a', 'collection 1 binary a', '', 'Y', 0, 0, null, 'Set up DNP points for collection 1 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. For example: 1 2 3 4, or 0x01 0x02 3 4. There are 15 possible dnp points between Collection Binary A and Collection Binary B.')
INSERT INTO dcitemtype VALUES (150, 'collection 1 binary b', 'collection 1 binary b', '', 'Y', 0, 0, null, '')
INSERT INTO dcitemtype VALUES (151, 'collection 2 binary a', 'collection 2 binary a', '', 'Y', 0, 0, null, 'Set up DNP points for collection 2 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. For example: 1 2 3 4, or 0x01 0x02 3 4. There are 15 possible dnp points between Collection Binary A and Collection Binary B.')
INSERT INTO dcitemtype VALUES (152, 'collection 2 binary b', 'collection 2 binary b', '', 'Y', 0, 0, null, '')
INSERT INTO dcitemtype VALUES (153, 'collection 1 analog', 'collection 1 analog', '', 'Y', 0, 0, null, 'Set up DNP points for collection 1 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. For example: 1 2 3 4, or 0x01 0x02 3 4. There are 3 possible DNP points')
INSERT INTO dcitemtype VALUES (154, 'collection 2 analog', 'collection 2 analog', '', 'Y', 0, 0, null, 'Set up DNP points for collection 2 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. For example: 1 2 3 4, or 0x01 0x02 3 4. There are 3 possible DNP points')
INSERT INTO dcitemtype VALUES (155, 'collection 1 accumulator', 'collection 1 accumulator', '', 'Y', 0, 0, null, 'Set up DNP points for collection 1 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 2 possible DNP points')
INSERT INTO dcitemtype VALUES (156, 'collection 2 accumulator', 'collection 2 accumulator', '', 'Y', 0, 0, null, 'Set up DNP points for collection 2 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 2 possible DNP points')
INSERT INTO dcitemtype VALUES (157, 'analog 1', 'analog 1', '', 'Y', 0, 0, null, 'Set up DNP points for analog 1 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 6 possible DNP points')
INSERT INTO dcitemtype VALUES (158, 'analog 2', 'analog 2', '', 'Y', 0, 0, null, 'Set up DNP points for analog 2 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 6 possible DNP points')
INSERT INTO dcitemtype VALUES (159, 'analog 3', 'analog 3', '', 'Y', 0, 0, null, 'Set up DNP points for analog 3 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 6 possible DNP points')
INSERT INTO dcitemtype VALUES (160, 'analog 4', 'analog 4', '', 'Y', 0, 0, null, 'Set up DNP points for analog 4 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 6 possible DNP points')
INSERT INTO dcitemtype VALUES (161, 'analog 5', 'analog 5', '', 'Y', 0, 0, null, 'Set up DNP points for analog 5 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 6 possible DNP points')
INSERT INTO dcitemtype VALUES (162, 'analog 6', 'analog 6', '', 'Y', 0, 0, null, 'Set up DNP points for analog 6 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 6 possible DNP points')
INSERT INTO dcitemtype VALUES (163, 'analog 7', 'analog 7', '', 'Y', 0, 0, null, 'Set up DNP points for analog 7 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 6 possible DNP points')
INSERT INTO dcitemtype VALUES (164, 'analog 8', 'analog 8', '', 'Y', 0, 0, null, 'Set up DNP points for analog 8 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 6 possible DNP points')
INSERT INTO dcitemtype VALUES (165, 'analog 9', 'analog 9', '', 'Y', 0, 0, null, 'Set up DNP points for analog 9 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 6 possible DNP points')
INSERT INTO dcitemtype VALUES (166, 'analog 10', 'analog 10', '', 'Y', 0, 0, null, 'Set up DNP points for analog 10 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 6 possible DNP points')
INSERT INTO dcitemtype VALUES (167, 'accumulator 1', 'accumulator 1', '', 'Y', 0, 0, null, 'Set up DNP points for accumulator 1 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 6 possible DNP points')
INSERT INTO dcitemtype VALUES (168, 'accumulator 2', 'accumulator 2', '', 'Y', 0, 0, null, 'Set up DNP points for accumulator 2 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 6 possible DNP points')
INSERT INTO dcitemtype VALUES (169, 'accumulator 3', 'accumulator 3', '', 'Y', 0, 0, null, 'Set up DNP points for accumulator 3 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 6 possible DNP points')
INSERT INTO dcitemtype VALUES (170, 'accumulator 4', 'accumulator 4', '', 'Y', 0, 0, null, 'Set up DNP points for accumulator 4 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 6 possible DNP points')
INSERT INTO dcitemtype VALUES (171, 'accumulator 5', 'accumulator 5', '', 'Y', 0, 0, null, 'Set up DNP points for accumulator 5 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 6 possible DNP points')
INSERT INTO dcitemtype VALUES (172, 'accumulator 6', 'accumulator 6', '', 'Y', 0, 0, null, 'Set up DNP points for accumulator 6 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 6 possible DNP points')
INSERT INTO dcitemtype VALUES (173, 'accumulator 7', 'accumulator 7', '', 'Y', 0, 0, null, 'Set up DNP points for accumulator 7 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 6 possible DNP points')
INSERT INTO dcitemtype VALUES (174, 'accumulator 8', 'accumulator 8', '', 'Y', 0, 0, null, 'Set up DNP points for accumulator 8 read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 6 possible DNP points')
INSERT INTO dcitemtype VALUES (175, 'binary byte 1a', 'binary byte 1a', '', 'Y', 0, 0, null, 'Set up DNP points for binary read. DNP points are offset by 1, 0 is disabled. Values must be seperated by a space. There are 12 possible DNP points betweein binary 1a and binary 1b')
INSERT INTO dcitemtype VALUES (176, 'binary byte 1b', 'binary byte 1b', '', 'Y', 0, 0, null, 'Second half of binary bytes, see binary byte 1a.')



--CBC

-- Voltage
INSERT INTO dcitemtype VALUES (63, 'Active Settings', 'Active Settings', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (64, 'Under Voltage Close Point', 'Under Voltage Close Point', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (65, 'Over Voltage Trip Point', 'Over Voltage Trip Point', '', 'N', 0, 50, null, 'Over Voltage Trip Point')
INSERT INTO dcitemtype VALUES (66, 'Over Under Voltage Control Trig Time', 'Over Under Voltage Control Trig Time', '', 'N', 0, 50, null, 'Over Under Voltage Control Trig Time')
INSERT INTO dcitemtype VALUES (67, 'Adaptive Voltage Hysteresis', 'Adaptive Voltage Hysteresis', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (68, 'Adaptive Voltage Flag', 'Adaptive Voltage Flag', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (69, 'Emergency Under Voltage Point', 'Emergency Under Voltage Point', '', 'N', 0, 50, null, 'Emergency Under Voltage Point')
INSERT INTO dcitemtype VALUES (70, 'Emergency Over Voltage Point', 'Emergency Over Voltage Point', '', 'N', 0, 50, null, 'Emergency Over Voltage Point')
INSERT INTO dcitemtype VALUES (71, 'Emergency Voltage Time', 'Emergency Voltage Time', '', 'N', 0, 50, null, '')

-- Comms Lost
INSERT INTO dcitemtype VALUES (73, 'Under Voltage Close Point', 'Under Voltage Close Point', '', 'N', 0, 50, null, 'Under Voltage Close Point')
INSERT INTO dcitemtype VALUES (74, 'Over Voltage Trip Point', 'Over Voltage Trip Point', '', 'N', 0, 50, null, 'Over Voltage Trip Point')
INSERT INTO dcitemtype VALUES (75, 'Comms Lost Time', 'Comms Lost Time', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (76, 'Comms Lost Action', 'Comms Lost Action', '', 'N', 0, 50, null, '')

-- Neutral Current
INSERT INTO dcitemtype VALUES (78, 'Fault Current Set Point', 'Fault Current Set Point', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (79, 'State Change Set Point', 'State Change Set Point', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (80, 'Neutral Current Retry', 'Neutral Current Retry', '', 'N', 0, 50, null, '')

-- Fault Detection
INSERT INTO dcitemtype VALUES (81, 'Fault detection active', 'Fault detection active', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (82, 'Ai1 Average Time', 'Ai1 Average Time', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (83, 'Ai2 Average Time', 'Ai2 Average Time', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (84, 'Ai3 Average Time', 'Ai3 Average Time', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (85, 'Ai1 Peak Samples', 'Ai1 Peak Samples', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (86, 'Ai2 Peak Samples', 'Ai2 Peak Samples', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (87, 'Ai3 Peak Samples', 'Ai3 Peak Samples', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (88, 'Ai1 Ratio Threshold', 'Ai1 Ratio Threshold', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (89, 'Ai2 Ratio Threshold', 'Ai2 Ratio Threshold', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (90, 'Ai3 Ratio Threshold', 'Ai3 Ratio Threshold', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (91, 'Battery On Time', 'Battery On Time', '', 'N', 0, 50, null, '')

-- Season 1 Time And Temp
INSERT INTO dcitemtype VALUES (93, 'Season Start 1', 'Season Start 1', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (94, 'Weekday Timed Control Close 1', 'Weekday Timed Control Close 1', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (95, 'Weekend Timed Control Close 1', 'Weekend Timed Control Close 1', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (96, 'Weekday Timed Control Trip 1', 'Weekday Timed Control Trip 1', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (97, 'Weekend Timed Control Trip 1', 'Weekend Timed Control Trip 1', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (98, 'Off Time State 1', 'Off Time State 1', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (99, 'Temp Min Threshold 1', 'Temp Min Threshold 1', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (100, 'Temp Min Threshold Action 1', 'Temp Min Threshold Action 1', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (101, 'Temp Min Hysterisis 1', 'Temp Min Hysterisis 1', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (102, 'Temp Min Threshold Trig Time 1', 'Temp Min Threshold Trig Time 1', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (103, 'Temp Max Threshold 1', 'Temp Max Threshold 1', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (104, 'Temp Max Threshold Action 1', 'Temp Max Threshold Action 1', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (105, 'Temp Max Hysterisis 1', 'Temp Max Hysterisis 1', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (106, 'Temp Max Threshold Trig Time 1', 'Temp Max Threshold Trig Time 1', '', 'N', 0, 50, null, '')

-- Season 2 Time And Temp
INSERT INTO dcitemtype VALUES (108, 'Season Start 2', 'Season Start 2', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (109, 'Weekday Timed Control Close 2', 'Weekday Timed Control Close 2', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (110, 'Weekend Timed Control Close 2', 'Weekend Timed Control Close 2', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (111, 'Weekday Timed Control Trip 2', 'Weekday Timed Control Trip 2', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (112, 'Weekend Timed Control Trip 2', 'Weekend Timed Control Trip 2', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (113, 'Off Time State 2', 'Off Time State 2', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (114, 'Temp Min Threshold 2', 'Temp Min Threshold 2', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (115, 'Temp Min Threshold Action 2', 'Temp Min Threshold Action 2', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (116, 'Temp Min Hysterisis 2', 'Temp Min Hysterisis 2', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (117, 'Temp Min Threshold Trig Time 2', 'Temp Min Threshold Trig Time 2', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (118, 'Temp Max Threshold 2', 'Temp Max Threshold 2', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (119, 'Temp Max Threshold Action 2', 'Temp Max Threshold Action 2', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (120, 'Temp Max Hysterisis 2', 'Temp Max Hysterisis 2', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (121, 'Temp Max Threshold Trig Time 2', 'Temp Max Threshold Trig Time 2', '', 'N', 0, 50, null, '')

-- Control Times
INSERT INTO dcitemtype VALUES (123, 'Contact Closure Time', 'Contact Closure Time', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (124, 'manual Control Delay Trip', 'manual Control Delay Trip', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (125, 'Manual Control Delay Close', 'Manual Control Delay Close', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (126, 'Reclose Delay Time', 'Reclose Delay Time', '', 'N', 0, 50, null, '')

-- Data Logging
INSERT INTO dcitemtype VALUES (128, 'Data Log Flags', 'Data Log Flags', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (129, 'Log Time Interval', 'Log Time Interval', '', 'N', 0, 50, null, '')

-- Addressing
INSERT INTO dcitemtype VALUES (131, 'Geo Address', 'Geo Address', '', 'N', 0, 50, null, 'Geo Address')
INSERT INTO dcitemtype VALUES (132, 'Substation Address', 'Substation Address', '', 'N', 0, 50, null, 'Substation Address')
INSERT INTO dcitemtype VALUES (133, 'Feeder Address', 'Feeder Address', '', 'N', 0, 50, null, 'Feeder Address')
INSERT INTO dcitemtype VALUES (134, 'Zip Address', 'Zip Address', '', 'N', 0, 50, null, 'Zip Address')
INSERT INTO dcitemtype VALUES (135, 'User Defined Address', 'User Defined Address', '', 'N', 0, 50, null, 'User Defined Address')
INSERT INTO dcitemtype VALUES (136, 'Program Address', 'Program Address', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (137, 'Splinter Address', 'Splinter Address', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (138, 'Requred Address Level', 'Requred Address Level', '', 'N', 0, 50, null, '')

-- DNP
INSERT INTO dcitemtype VALUES (140, 'Line Voltage Dead Band', 'Line Voltage Dead Band', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (141, 'Delta Voltage Dead Band', 'Delta Voltage Dead Band', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (142, 'Analog Dead Band', 'Analog Dead Band', '', 'N', 0, 50, null, 'Analog Value Dead Band')

-- UDP
INSERT INTO dcitemtype VALUES (144, 'Retry Delay', 'Retry Delay', '', 'N', 0, 50, null, '')
INSERT INTO dcitemtype VALUES (145, 'Poll Timeout', 'Poll Timeout', '', 'N', 0, 50, null, '')


-- Category / Item mappings

-- MCT470 Addressing
INSERT INTO dccategoryitemtype VALUES (1, 2)
INSERT INTO dccategoryitemtype VALUES (1, 3)
INSERT INTO dccategoryitemtype VALUES (1, 4)
INSERT INTO dccategoryitemtype VALUES (1, 5)

-- MCT410 Addressing
INSERT INTO dccategoryitemtype VALUES (27, 2)
INSERT INTO dccategoryitemtype VALUES (27, 3)
INSERT INTO dccategoryitemtype VALUES (27, 4)
INSERT INTO dccategoryitemtype VALUES (27, 5)


-- MCT470 DST
INSERT INTO dccategoryitemtype VALUES (2, 7)
INSERT INTO dccategoryitemtype VALUES (2, 8)
INSERT INTO dccategoryitemtype VALUES (2, 9)

-- MCT410 DST
INSERT INTO dccategoryitemtype VALUES (28, 7)
INSERT INTO dccategoryitemtype VALUES (28, 8)
INSERT INTO dccategoryitemtype VALUES (28, 9)

-- MCT470 Options
INSERT INTO dccategoryitemtype VALUES (3, 12)
INSERT INTO dccategoryitemtype VALUES (3, 13)
INSERT INTO dccategoryitemtype VALUES (3, 14)
INSERT INTO dccategoryitemtype VALUES (3, 15)

-- MCT410 Options
INSERT INTO dccategoryitemtype VALUES (29, 11)
INSERT INTO dccategoryitemtype VALUES (29, 12)
INSERT INTO dccategoryitemtype VALUES (29, 13)
INSERT INTO dccategoryitemtype VALUES (29, 14)
INSERT INTO dccategoryitemtype VALUES (29, 177)
INSERT INTO dccategoryitemtype VALUES (29, 16)
INSERT INTO dccategoryitemtype VALUES (29, 17)

-- MCT470 Demand Load Profile
INSERT INTO dccategoryitemtype VALUES (4, 20)
INSERT INTO dccategoryitemtype VALUES (4, 23)

-- MCT410 Demand Load Profile
INSERT INTO dccategoryitemtype VALUES (30, 19)
INSERT INTO dccategoryitemtype VALUES (30, 20)
INSERT INTO dccategoryitemtype VALUES (30, 21)
INSERT INTO dccategoryitemtype VALUES (30, 22)

-- MCT470 V Threshold
INSERT INTO dccategoryitemtype VALUES (5, 25)
INSERT INTO dccategoryitemtype VALUES (5, 26)

-- MCT410 V Threshold
INSERT INTO dccategoryitemtype VALUES (31, 25)
INSERT INTO dccategoryitemtype VALUES (31, 26)

-- MCT410 Disconnect
INSERT INTO dccategoryitemtype VALUES (32, 24)
INSERT INTO dccategoryitemtype VALUES (32, 27)
INSERT INTO dccategoryitemtype VALUES (32, 28)
INSERT INTO dccategoryitemtype VALUES (32, 29)

-- MCT470 Long Load Profile
INSERT INTO dccategoryitemtype VALUES (7, 31)
INSERT INTO dccategoryitemtype VALUES (7, 32)
INSERT INTO dccategoryitemtype VALUES (7, 33)
INSERT INTO dccategoryitemtype VALUES (7, 34)

-- MCT410 Long Load Profile
INSERT INTO dccategoryitemtype VALUES (33, 31)
INSERT INTO dccategoryitemtype VALUES (33, 32)
INSERT INTO dccategoryitemtype VALUES (33, 33)
INSERT INTO dccategoryitemtype VALUES (33, 34)

-- MCT470 Holiday
INSERT INTO dccategoryitemtype VALUES (8, 36)
INSERT INTO dccategoryitemtype VALUES (8, 37)
INSERT INTO dccategoryitemtype VALUES (8, 38)

-- MCT410 Holiday
INSERT INTO dccategoryitemtype VALUES (34, 36)
INSERT INTO dccategoryitemtype VALUES (34, 37)
INSERT INTO dccategoryitemtype VALUES (34, 38)

-- MCT470 Load Profile Channels
INSERT INTO dccategoryitemtype VALUES (9, 40)
INSERT INTO dccategoryitemtype VALUES (9, 41)
INSERT INTO dccategoryitemtype VALUES (9, 42)
INSERT INTO dccategoryitemtype VALUES (9, 43)
INSERT INTO dccategoryitemtype VALUES (9, 44)
INSERT INTO dccategoryitemtype VALUES (9, 45)
INSERT INTO dccategoryitemtype VALUES (9, 46)
INSERT INTO dccategoryitemtype VALUES (9, 47)
INSERT INTO dccategoryitemtype VALUES (9, 48)
INSERT INTO dccategoryitemtype VALUES (9, 49)
INSERT INTO dccategoryitemtype VALUES (9, 50)
INSERT INTO dccategoryitemtype VALUES (9, 51)

-- Relays
INSERT INTO dccategoryitemtype VALUES (10, 53)
INSERT INTO dccategoryitemtype VALUES (10, 54)

-- Precanned Table
INSERT INTO dccategoryitemtype VALUES (11, 56)
INSERT INTO dccategoryitemtype VALUES (11, 57)
INSERT INTO dccategoryitemtype VALUES (11, 58)

-- System Options
INSERT INTO dccategoryitemtype VALUES (12, 61)

-- Voltage
INSERT INTO dccategoryitemtype VALUES (13, 63)
INSERT INTO dccategoryitemtype VALUES (13, 64)
INSERT INTO dccategoryitemtype VALUES (13, 65)
INSERT INTO dccategoryitemtype VALUES (13, 66)
INSERT INTO dccategoryitemtype VALUES (13, 67)
INSERT INTO dccategoryitemtype VALUES (13, 68)
INSERT INTO dccategoryitemtype VALUES (13, 69)
INSERT INTO dccategoryitemtype VALUES (13, 70)
INSERT INTO dccategoryitemtype VALUES (13, 71)

-- Comms Lost
INSERT INTO dccategoryitemtype VALUES (14, 73)
INSERT INTO dccategoryitemtype VALUES (14, 74)
INSERT INTO dccategoryitemtype VALUES (14, 75)
INSERT INTO dccategoryitemtype VALUES (14, 76)

-- Neutral Current
INSERT INTO dccategoryitemtype VALUES (15, 78)
INSERT INTO dccategoryitemtype VALUES (15, 79)
INSERT INTO dccategoryitemtype VALUES (15, 80)

-- Fault Detection
INSERT INTO dccategoryitemtype VALUES (16, 82)
INSERT INTO dccategoryitemtype VALUES (16, 83)
INSERT INTO dccategoryitemtype VALUES (16, 84)
INSERT INTO dccategoryitemtype VALUES (16, 85)
INSERT INTO dccategoryitemtype VALUES (16, 86)
INSERT INTO dccategoryitemtype VALUES (16, 87)
INSERT INTO dccategoryitemtype VALUES (16, 88)
INSERT INTO dccategoryitemtype VALUES (16, 89)
INSERT INTO dccategoryitemtype VALUES (16, 90)
INSERT INTO dccategoryitemtype VALUES (16, 91)

-- Season 1 Time and Temp
INSERT INTO dccategoryitemtype VALUES (17, 93)
INSERT INTO dccategoryitemtype VALUES (17, 94)
INSERT INTO dccategoryitemtype VALUES (17, 95)
INSERT INTO dccategoryitemtype VALUES (17, 96)
INSERT INTO dccategoryitemtype VALUES (17, 97)
INSERT INTO dccategoryitemtype VALUES (17, 98)
INSERT INTO dccategoryitemtype VALUES (17, 99)
INSERT INTO dccategoryitemtype VALUES (17, 100)
INSERT INTO dccategoryitemtype VALUES (17, 101)
INSERT INTO dccategoryitemtype VALUES (17, 102)
INSERT INTO dccategoryitemtype VALUES (17, 103)
INSERT INTO dccategoryitemtype VALUES (17, 104)
INSERT INTO dccategoryitemtype VALUES (17, 105)
INSERT INTO dccategoryitemtype VALUES (17, 106)

-- Season 2 Time and Temp
INSERT INTO dccategoryitemtype VALUES (18, 108)
INSERT INTO dccategoryitemtype VALUES (18, 109)
INSERT INTO dccategoryitemtype VALUES (18, 110)
INSERT INTO dccategoryitemtype VALUES (18, 111)
INSERT INTO dccategoryitemtype VALUES (18, 112)
INSERT INTO dccategoryitemtype VALUES (18, 113)
INSERT INTO dccategoryitemtype VALUES (18, 114)
INSERT INTO dccategoryitemtype VALUES (18, 115)
INSERT INTO dccategoryitemtype VALUES (18, 116)
INSERT INTO dccategoryitemtype VALUES (18, 117)
INSERT INTO dccategoryitemtype VALUES (18, 118)
INSERT INTO dccategoryitemtype VALUES (18, 119)
INSERT INTO dccategoryitemtype VALUES (18, 120)
INSERT INTO dccategoryitemtype VALUES (18, 121)

-- Control Times
INSERT INTO dccategoryitemtype VALUES (19, 123)
INSERT INTO dccategoryitemtype VALUES (19, 124)
INSERT INTO dccategoryitemtype VALUES (19, 125)
INSERT INTO dccategoryitemtype VALUES (19, 126)

-- Data Logging
INSERT INTO dccategoryitemtype VALUES (20, 128)
INSERT INTO dccategoryitemtype VALUES (20, 129)

-- DNP
INSERT INTO dccategoryitemtype VALUES (21, 140)
INSERT INTO dccategoryitemtype VALUES (21, 141)
INSERT INTO dccategoryitemtype VALUES (21, 142)

-- UDP
INSERT INTO dccategoryitemtype VALUES (22, 144)
INSERT INTO dccategoryitemtype VALUES (22, 145)

-- Addressing
INSERT INTO dccategoryitemtype VALUES (23, 131)
INSERT INTO dccategoryitemtype VALUES (23, 132)
INSERT INTO dccategoryitemtype VALUES (23, 133)
INSERT INTO dccategoryitemtype VALUES (23, 134)
INSERT INTO dccategoryitemtype VALUES (23, 135)
INSERT INTO dccategoryitemtype VALUES (23, 136)
INSERT INTO dccategoryitemtype VALUES (23, 137)
INSERT INTO dccategoryitemtype VALUES (23, 138)

-- MCT470 TOU Schedule
INSERT INTO dccategoryitemtype VALUES (37, 201)
INSERT INTO dccategoryitemtype VALUES (37, 202)
INSERT INTO dccategoryitemtype VALUES (37, 203)
INSERT INTO dccategoryitemtype VALUES (37, 204)
INSERT INTO dccategoryitemtype VALUES (37, 205)
INSERT INTO dccategoryitemtype VALUES (37, 206)
INSERT INTO dccategoryitemtype VALUES (37, 207)
INSERT INTO dccategoryitemtype VALUES (37, 208)
INSERT INTO dccategoryitemtype VALUES (37, 209)
INSERT INTO dccategoryitemtype VALUES (37, 210)
INSERT INTO dccategoryitemtype VALUES (37, 211)
INSERT INTO dccategoryitemtype VALUES (37, 212)

INSERT INTO dccategoryitemtype VALUES (37, 221)
INSERT INTO dccategoryitemtype VALUES (37, 222)
INSERT INTO dccategoryitemtype VALUES (37, 223)
INSERT INTO dccategoryitemtype VALUES (37, 224)
INSERT INTO dccategoryitemtype VALUES (37, 225)
INSERT INTO dccategoryitemtype VALUES (37, 226)
INSERT INTO dccategoryitemtype VALUES (37, 227)
INSERT INTO dccategoryitemtype VALUES (37, 228)
INSERT INTO dccategoryitemtype VALUES (37, 229)
INSERT INTO dccategoryitemtype VALUES (37, 230)
INSERT INTO dccategoryitemtype VALUES (37, 231)
INSERT INTO dccategoryitemtype VALUES (37, 232)

INSERT INTO dccategoryitemtype VALUES (37, 241)
INSERT INTO dccategoryitemtype VALUES (37, 242)
INSERT INTO dccategoryitemtype VALUES (37, 243)
INSERT INTO dccategoryitemtype VALUES (37, 244)
INSERT INTO dccategoryitemtype VALUES (37, 245)
INSERT INTO dccategoryitemtype VALUES (37, 246)
INSERT INTO dccategoryitemtype VALUES (37, 247)
INSERT INTO dccategoryitemtype VALUES (37, 248)
INSERT INTO dccategoryitemtype VALUES (37, 249)
INSERT INTO dccategoryitemtype VALUES (37, 250)
INSERT INTO dccategoryitemtype VALUES (37, 251)
INSERT INTO dccategoryitemtype VALUES (37, 252)

INSERT INTO dccategoryitemtype VALUES (37, 261)
INSERT INTO dccategoryitemtype VALUES (37, 262)
INSERT INTO dccategoryitemtype VALUES (37, 263)
INSERT INTO dccategoryitemtype VALUES (37, 264)
INSERT INTO dccategoryitemtype VALUES (37, 265)
INSERT INTO dccategoryitemtype VALUES (37, 266)
INSERT INTO dccategoryitemtype VALUES (37, 267)
INSERT INTO dccategoryitemtype VALUES (37, 268)
INSERT INTO dccategoryitemtype VALUES (37, 269)
INSERT INTO dccategoryitemtype VALUES (37, 270)
INSERT INTO dccategoryitemtype VALUES (37, 271)
INSERT INTO dccategoryitemtype VALUES (37, 272)

INSERT INTO dccategoryitemtype VALUES (24, 200)
INSERT INTO dccategoryitemtype VALUES (24, 281)
INSERT INTO dccategoryitemtype VALUES (24, 282)
INSERT INTO dccategoryitemtype VALUES (24, 283)
INSERT INTO dccategoryitemtype VALUES (24, 284)
INSERT INTO dccategoryitemtype VALUES (24, 285)
INSERT INTO dccategoryitemtype VALUES (24, 286)
INSERT INTO dccategoryitemtype VALUES (24, 287)
INSERT INTO dccategoryitemtype VALUES (24, 288)

-- MCT410 TOU Schedule

INSERT INTO dccategoryitemtype VALUES (36, 200)
INSERT INTO dccategoryitemtype VALUES (36, 281)
INSERT INTO dccategoryitemtype VALUES (36, 282)
INSERT INTO dccategoryitemtype VALUES (36, 283)
INSERT INTO dccategoryitemtype VALUES (36, 284)
INSERT INTO dccategoryitemtype VALUES (36, 285)
INSERT INTO dccategoryitemtype VALUES (36, 286)
INSERT INTO dccategoryitemtype VALUES (36, 287)
INSERT INTO dccategoryitemtype VALUES (36, 288)


-- MCT Centron
INSERT INTO dccategoryitemtype VALUES (25, 147)
INSERT INTO dccategoryitemtype VALUES (25, 148)

--MCT DNP
INSERT INTO dccategoryitemtype VALUES (26, 149)
INSERT INTO dccategoryitemtype VALUES (26, 150)
INSERT INTO dccategoryitemtype VALUES (26, 151)
INSERT INTO dccategoryitemtype VALUES (26, 152)
INSERT INTO dccategoryitemtype VALUES (26, 153)
INSERT INTO dccategoryitemtype VALUES (26, 154)
INSERT INTO dccategoryitemtype VALUES (26, 155)
INSERT INTO dccategoryitemtype VALUES (26, 156)
INSERT INTO dccategoryitemtype VALUES (26, 157)
INSERT INTO dccategoryitemtype VALUES (26, 158)
INSERT INTO dccategoryitemtype VALUES (26, 159)
INSERT INTO dccategoryitemtype VALUES (26, 160)
INSERT INTO dccategoryitemtype VALUES (26, 161)
INSERT INTO dccategoryitemtype VALUES (26, 162)
INSERT INTO dccategoryitemtype VALUES (26, 163)
INSERT INTO dccategoryitemtype VALUES (26, 164)
INSERT INTO dccategoryitemtype VALUES (26, 165)
INSERT INTO dccategoryitemtype VALUES (26, 166)
INSERT INTO dccategoryitemtype VALUES (26, 167)
INSERT INTO dccategoryitemtype VALUES (26, 168)
INSERT INTO dccategoryitemtype VALUES (26, 169)
INSERT INTO dccategoryitemtype VALUES (26, 170)
INSERT INTO dccategoryitemtype VALUES (26, 171)
INSERT INTO dccategoryitemtype VALUES (26, 172)
INSERT INTO dccategoryitemtype VALUES (26, 173)
INSERT INTO dccategoryitemtype VALUES (26, 174)
INSERT INTO dccategoryitemtype VALUES (26, 175)
INSERT INTO dccategoryitemtype VALUES (26, 176)
