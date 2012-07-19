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
            pointtype - the name of the point type (e.g. "Analog")
            notificationgroup - the name of the notification group to which this is being sent
            abnormal - the abnormal flag {true,false}
            acknowledged - the acknowledged flag {true,false}
            condition - the text of the condition that was violated
            category - the name of the category that caused the alarm
            alarmdate - the date of the alarm (e.g. "Tuesday, May 31")
            alarmtime - the time of the alarm (e.g. "3:45 PM")
    -->

  <xsl:template match="alarm">
      <subject>Yukon Alarm</subject>
      <body>The value of <xsl:value-of select="paoname"/> / <xsl:value-of select="pointname"/> is now <xsl:value-of select="value"/><xsl:value-of select="unitofmeasure"/> at <xsl:value-of select="alarmdate"/><xsl:text> </xsl:text><xsl:value-of select="alarmtime"/>.
      </body>
  </xsl:template>
  
  
</xsl:stylesheet>