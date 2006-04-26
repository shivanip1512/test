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
            pointtype - the name of the point type (e.g. "Analog")
    -->

  <xsl:template match="alarm">
      <subject>Alarm Notification</subject>
      <body>The value of point <xsl:value-of select="pointname"/> is now <xsl:value-of select="value"/><xsl:value-of select="unitofmeasure"/>.</body>
  </xsl:template>
  
  
</xsl:stylesheet>