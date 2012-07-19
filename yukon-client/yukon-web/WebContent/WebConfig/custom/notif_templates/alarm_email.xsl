<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <xsl:template match="notificationmessage">
    <emailmsg>
      <xsl:apply-templates/>
    </emailmsg>
  </xsl:template>
  
    <!-- Available script parameters:  
            pointname - name of point that caused alarm
            pointid - id of point that caused alarm
            rawvalue - value of point
            value - for status points = state text, for all others = raw value
            unitofmeasure - the unit of measure text, or nothing if undefined
            paoname - the name of the PAO the point is attached to
            meternumber - the meternumber (for "meter" device types, else nothing)
            paodescription - the description of the PAO (often this is the CapBank Address)
            pointtype - the name of the point type (e.g. "Analog")
            notificationgroup - the name of the notification group to which this is being sent
            abnormal - the abnormal flag {true,false}
            acknowledged - the acknowledged flag {true,false}
            condition - the text of the condition that was violated
            category - the name of the category that caused the alarm
            alarmdate - the date of the alarm (e.g. "Tuesday, May 31")
            alarmtime - the time of the alarm (e.g. "3:45 PM")
            
        Values from PointFormattingService 
          These formats are used throughout Yukon to provide a uniform display of a point's value. 
          Numeric values are adjusted the point's decimal places. 
          As of 4.1, the following are customizable as well as being adjusted for timezone and locale.
            shortvalue - The SHORT formatting of the point value (usually <value> <unit>)
            fullvalue - The FULL formatting of the point value (usually <value> <unit> <date> <time>)
            datevalue - The DATE formatting of teh point value (usually <date> <time>)
    -->

  <xsl:template match="alarm">
      <subject>Yukon Alarm <xsl:value-of select="paoname"/> / <xsl:value-of select="pointname"/></subject>
      <body>The value of <xsl:value-of select="paoname"/> / <xsl:value-of select="pointname"/> is now <xsl:value-of select="value"/><xsl:text> </xsl:text><xsl:value-of select="unitofmeasure"/>.

Alarm Time: <xsl:value-of select="alarmdate"/><xsl:text> </xsl:text><xsl:value-of select="alarmtime"/>
Condition: <xsl:value-of select="condition"/>
Category: <xsl:value-of select="category"/>
Abnormal: <xsl:value-of select="abnormal"/>
Acknowledged: <xsl:value-of select="acknowledged"/>
Point Type: <xsl:value-of select="pointtype"/>
Notification Group: <xsl:value-of select="notificationgroup"/>    
      </body>
  </xsl:template>
  
  
</xsl:stylesheet>