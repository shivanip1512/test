<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <xsl:template match="notificationmessage">
    <emailmsg>
      <subject>HECO Curtailment Event</subject>
      <body>HECO is calling for customers enrolled in the CIDLC program to curtail electric use.  
Curtailment will start on <xsl:value-of select="startdate"/> at <xsl:value-of select="starttime"/> and last for approximately <xsl:value-of select="durationhours"/> hours.  
      
Thank you for your participation.</body>
    </emailmsg>
  </xsl:template>
  
</xsl:stylesheet>