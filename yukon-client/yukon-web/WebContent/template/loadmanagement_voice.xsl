<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:template match="notificationmessage">
  <vxml version="2.0" xmlns="http://www.w3.org/2001/vxml">
  <form id="message">
    <block>
	  <xsl:apply-templates/>
      Thank you for your participation.
      <xsl:if test="loadmanagement/customername != ''">
      This call was to: <xsl:value-of select="loadmanagement/customername"/>
      </xsl:if>
      <!-- How can we make this wait until the above has been read out??? -->
      <goto fetchint="safe" next="confirm.jsp?COMPLETE=yes"/>
    </block>
    
  </form>
  </vxml>
  </xsl:template>
  
  
  <xsl:template match="loadmanagement[programname='direct prog'][action='starting']">
      Start Direct Program!!!
      HECO is calling for customers enrolled in the CIDLC program to curtail electric use.  
      Curtailment will start on <xsl:value-of select="startdate"/> at <xsl:value-of select="starttime"/> 
      and last for approximately <xsl:value-of select="durationhours"/> hours.  
  </xsl:template>
  
  <xsl:template match="loadmanagement[programname='direct prog'][action='finishing']">
      HECO is calling for customers enrolled in the CIDLC program to curtail electric use.  
      Curtailment will stop at <xsl:value-of select="stoptime"/>. 
  </xsl:template>
  
  <xsl:template match="loadmanagement[programname='other program']" priority="-3">
      Other Program!!!
      HECO is calling for customers enrolled in the CIDLC program to curtail electric use.  
      Curtailment will start on <xsl:value-of select="startdate"/> at <xsl:value-of select="starttime"/> 
      and last for approximately <xsl:value-of select="durationhours"/> hours.  
  </xsl:template>
  
  <xsl:template match="loadmanagement" priority="-5">
      HECO is calling for customers enrolled in the CIDLC program to curtail electric use.  
      Curtailment will start on <xsl:value-of select="startdate"/> at <xsl:value-of select="starttime"/> 
      and last for approximately <xsl:value-of select="durationhours"/> hours.  
  </xsl:template>
  
</xsl:stylesheet>