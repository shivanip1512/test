#include "yukon.h"

#include <windows.h>
#include "wordbuilder.h"

INT BuildVersacomWord( UINT Address,







#if 0
SUB VwordFill (Vst AS VStruct, GrpRec AS GROUP, VCommandType%, ControlParam%)
'this sub fill in the versacomm word struct with addressing

    IF GrpRec.VersaComm.Unique <> 0 THEN
        'command by Serial number
        Vst.UtilityID = 0
        Vst.Section = 0
        Vst.Class = 0
        Vst.Division = 0
        Vst.Address = GrpRec.VersaComm.Unique
    ELSE
        Vst.Address = 0
        Vst.UtilityID = GrpRec.VersaComm.UtilityID
        Vst.Section = GrpRec.VersaComm.Section
        Vst.Class = GrpRec.VersaComm.Class
        Vst.Division = GrpRec.VersaComm.Division
    END IF

    SELECT CASE VCommandType%

        CASE VSHED
            Vst.CommandType = 4         ' load control
            ' MESS-4 Relay (Command type 4)
            Vst.Load(1).ControlType = TRUE              'discrete shed
            Vst.Load(1).TimeCode = ControlParam%        'control time
            FOR Count = 1 TO 3

                IF GrpRec.VersaComm.Load = Count THEN
                    Vst.Load(1).Relay(Count) = TRUE
                ELSE
                    Vst.Load(1).Relay(Count) = FALSE
                END IF

            NEXT Count

        CASE VCYCLE
            Vst.CommandType = 4         ' load control
            ' MESS-4 Relay (Command type 4)
            Vst.Load(1).ControlType = FALSE             'cycle command
            Vst.Load(1).TimeCode = ControlParam%        'control time
            FOR Count = 1 TO 3

                IF GrpRec.VersaComm.Load = Count THEN
                    Vst.Load(1).Relay(Count) = TRUE
                ELSE
                    Vst.Load(1).Relay(Count) = FALSE
                END IF

            NEXT Count

        CASE EVCYCLE
            ' E-MESS Cycle Command (Command type 4)
            Vst.CommandType = 14                        'load control
            Vst.ELoad.ControlType = 0                   'cycle command
            Vst.ELoad.CycleType = 0                     'cycle (not a bump) command
            Vst.ELoad.Relay = GrpRec.VersaComm.Load     'relay number
            Vst.ELoad.Percent = ControlParam%           'cycle rate
            IF Period% < 1 THEN
                Vst.ELoad.Window = 30                   'if percent, window
            ELSE
                Vst.ELoad.Window = Period%              'if percent, window
            END IF
            IF CycleCount% < 1 THEN
                Vst.ELoad.Count = 8                     'if percent, count
            ELSE
                Vst.ELoad.Count = CycleCount%           'if percent, count
            END IF
            Vst.ELoad.DelayTime = 0                     'no delay before starting for now

        CASE VRESTORE, VTERMINATE
            Vst.CommandType = 4         ' load control
            ' MESS-4 Relay (Command type 4)
            IF VCommandType% = VRESTORE THEN
                Vst.Load(1).ControlType = 1
            ELSE
                'cycle comand
                Vst.Load(1).ControlType = 0
            END IF
            Vst.Load(1).TimeCode = 0        'restore or terminate
            FOR Count = 1 TO 3

                IF GrpRec.VersaComm.Load = Count THEN
                    Vst.Load(1).Relay(Count) = TRUE
                ELSE
                    Vst.Load(1).Relay(Count) = FALSE
                END IF

            NEXT Count

        CASE VINITIATOR
            Vst.CommandType = 5         ' Command initiator
            Vst.Initiator = ControlParam%

        CASE VCONFIGCODE
            Vst.CommandType = 6         ' Configuration Command

        CASE VCONFIGLIGHTS
            Vst.CommandType = 6         ' Configuration Command

        CASE VSERVICEOUT
            Vst.CommandType = 9         ' Contractual Out-of-Service Command
            Vst.Service = 4

        CASE VSERVICETOUT
            Vst.CommandType = 9         ' Temporary Out-of-Service Command
            Vst.Service = 1

        CASE VSERVICEIN
            Vst.CommandType = 9         ' Contractual IN Service Command
            Vst.Service = 8

        CASE VSERVICETIN
            Vst.CommandType = 9         ' Contractual IN Service Command
            Vst.Service = 2

        CASE VPROP
            Vst.CommandType = 12        ' prop test
            IF ControlParam% = 1 THEN
                Vst.PropDIT = 2
            ELSEIF ControlParam% = 2 THEN
                'display
                Vst.PropDIT = 4
            ELSE
                'terminate
                Vst.PropDIT = 1
            ENDIF

        CASE VCOUNTRESET
            Vst.CommandType = 8         ' Counter reset
            Vst.CountReset = ControlParam%

        CASE VDATAMESS
            ' DATA WORD
            Vst.CommandType = 13
            Vst.VData.DataType = 0

    END SELECT

END SUB

SUB InitVStruct(Vst AS VStruct)
'inits the versacom structure

    Vst.Port = 0                ' Port number
    Vst.Remote = 0              ' VCU Number
    Vst.Amp = 0                 ' Amp 0 or 1
    Vst.Feeder = 0              ' Feeder number
    Vst.Stages = 0              ' Number or repeater stages
    Vst.RepFixed = 0            ' Repeater fixed bits
    Vst.RepVar = 0              ' Repeater variable bits
    Vst.Priority = 0            ' Priority to place entry on Queue
    Vst.Retry = 0               ' Number of times to retry
    Vst.Section = 0             ' Section (0 = NONE)
    Vst.Class = 0               ' Class (0 = NONE)
    Vst.Division = 0            ' Division (0 = NONE)
    Vst.Address = 0             ' Unique (if SCD all 0)
    Vst.CommandType = 0         ' Command type (15 = SCRAM)

    ' MESS-4 Relay (Command type 4)
    FOR Count = 1 TO 3

        Vst.Load(Count).ControlType = 0
        Vst.Load(Count).TimeCode = 0
        Vst.Load(Count).Relay(1) = 0
        Vst.Load(Count).Relay(2) = 0
        Vst.Load(Count).Relay(3) = 0

    NEXT Count

    Vst.ELoad.ControlType = 0               '0 = cycle or bump, !0 = discrete
    Vst.ELoad.CycleType = 0                 '0 = Cycle, !0 = bump
    Vst.ELoad.Relay = 0                     '0 = ALL, 1-7 individual
    Vst.ELoad.Percent = 0                   'If cycle (1 - 100) or bump (+-50) , Percent
    Vst.ELoad.ControlTime = 0               'If discrete, control time
    Vst.ELoad.RandomTime = 0                'IF discrete, Randomization time
    Vst.ELoad.Window = 0                    'if percent, window
    Vst.ELoad.Count = 0                     'if percent, count
    Vst.ELoad.DelayTime = 0                 'delay before starting

    Vst.Initiator = 0                       ' Initiator code (Command type 5)
    Vst.VData.VBytes = SPACE$(6)
    Vst.VData.DataType = 0                  ' 0 = ASCII, 1 = TLR

    Vst.Config.ConfigType = 0               ' Configuration Message (Command Type 6)
    Vst.Config.VDATA = SPACE$(5)

    Vst.Service = 0             ' Service Flags (Command Type 9)
    Vst.PropDIT = 0             ' Propogation DIT (Command Type 12)
    Vst.CountReset = 0          ' Reset Counter Flags (Command type 8)

END SUB

#endif
