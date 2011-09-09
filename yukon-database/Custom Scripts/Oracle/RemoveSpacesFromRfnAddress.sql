-- !!! This script MUST be run when upgrading from NM 5.0 to NM 5.1  !!!
-- !!! ONLY run this script if running or upgrading to NM 5.1        !!!
-- !!! as it will break an installation that uses NM 5.0.            !!!
-- !!!                                                               !!!
-- !!! DO NOT run this script if you are using Network Mangager 5.0  !!!

UPDATE rfnAddress
SET SerialNumber = TRIM(SerialNumber);