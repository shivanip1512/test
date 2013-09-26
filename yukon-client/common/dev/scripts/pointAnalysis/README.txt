2013 July 30 Tues

rfnPointScript.groovy

USAGE:  At the command line
        > groovy rfnPointScript.groovy


PURPOSE: This script outputs 3 files based on the 2 input XMLs, particularly about the RFN devices' point mappings.
         SEE PAO_VS_RFN for more details.
         IT ALSO has the ability to compare different versions of the same file, BUT THAT WAS DISABLED for the last run (see the flags at the top of the script) because of rfn file structural changes.

PAO_VS_RFN: the paoVSrfnAnalysis.txt file contains a similar format to the other output files in that it shows a list of all points along with which models have those points.
            ADDITIONALLY this file has a key at the top describing what else is compared:
            * if a point exists in one file versus another (eg. several "per Interval" calculations exist only in paoDefinition.xml)
            * if an attribute exists/doesn't exist
            * ...

FUTURE:  The script currently does not check the points' modifiers, multipliers, nor references for accuracy between the two files or wrt expected 'normal' (ie. kWh has .001 multiplier)
         It also does not deal with any non-RFN devices - in particular it depends on the list of RFN devices within the script,
            and part of the script currently depends on being able to compare the pao and rfn XML files.

SETUP: you will need to download the Groovy JAR, and you can run it from the command line or by using the Groovy Console (eg. C:/Groovy-2.1.1/bin/groovyConsole.exe)

INPUTS: paoDefinition.xml, rfnPointMapping.xml
        You define the directory at the top of the script

OUTPUTS: rfnAnalysis.txt            Points are listed vertically, alphabetically, with symbols per associated model #s (see paoVSrfnAnalysis.txt for key)
         paoAnalysis.txt            Points are grouped by usages by model, vertically, starting with the A's at the top and ending with the 440s at the bottom.
         paoVSrfnAnalysis.txt       Points are grouped same as paoAnalysis.txt, then lists attributes, then tags.
         STDOUT                     It also currently prints out the pao IDs in a roughly hierarchical way (needs work)