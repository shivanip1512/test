<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  
  <xsl:template match="notificationmessage">
    <block>
    HECO is dispatching a load control event according to the rules of the CIDLC program.
    The curtailment event will start on <xsl:value-of select="startdate"/> at <xsl:value-of select="starttime"/>
    and end at <xsl:value-of select="stoptime"/>.
      </block>
  </xsl:template>
  
</xsl:stylesheet>