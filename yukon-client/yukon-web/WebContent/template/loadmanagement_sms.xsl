<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <xsl:template match="notificationmessage">
    <emailmsg>
	  <xsl:apply-templates/>
    </emailmsg>
  </xsl:template>
  
  <xsl:template match="loadmanagement">
      <subject>HECO Curtailment Event</subject>
      <body>Curtailment will start on <xsl:value-of select="startdate"/> at <xsl:value-of select="starttime"/> and last for <xsl:value-of select="durationhours"/> hours.</body>
  </xsl:template>
  
  
</xsl:stylesheet>