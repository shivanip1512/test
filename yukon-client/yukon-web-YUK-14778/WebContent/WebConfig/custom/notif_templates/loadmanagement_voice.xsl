<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- The next ten lines should not be edited for an installation. -->
    <xsl:template match="notificationmessage">
        <vxml version="2.0" xmlns="http://www.w3.org/2001/vxml">
            <form id="message">
                <block>
                    <xsl:apply-templates />
                    <goto fetchint="safe" next="confirm.jsp?COMPLETE=yes" />
                </block>
            </form>
        </vxml>
    </xsl:template>

    <!-- Available script parameters:  
            programname - name of LM program
            action - {starting,finishing,adjusting}
            startdate - date the event starts (e.g. "Tuesday, May 31")
            starttime - time the event starts (e.g. ""3:45 PM")
            stopdate - date the event stops (e.g. "Tuesday, May 31")
            stoptime - time the event stops (e.g. "5:45 PM")
            durationhours - number of whole hours in event
            durationminutes - numer of minutes in event
            remainingminutes - equal to: durationminutes - (durationhours * 60)
            openended - {yes,no}  
            customername - name of the CICustomer being notified 
    -->


    <!-- The following section maps the Programs to Scripts.  A matching template is 
         required for each load program.  If no template is found the default 
         template is used.  -->
        
    <!-- Event Selection -->
    <xsl:template match="loadmanagement[programname='First'][action='starting']">
        <xsl:call-template name="startFirst" />
    </xsl:template>
    <xsl:template match="loadmanagement[programname='First'][action='finishing']">
        <xsl:call-template name="endFirst" />
    </xsl:template>
    <xsl:template match="loadmanagement[programname='First'][action='adjusting']">
        <xsl:call-template name="adjustFirst" />
    </xsl:template>

    <!-- Default Selection -->
    <xsl:template match="loadmanagement[action='starting']" priority="-1">
        <xsl:call-template name="startDefault" />
    </xsl:template>
    <xsl:template match="loadmanagement[action='finishing']" priority="-1">
        <xsl:call-template name="endDefault" />
    </xsl:template>
    <xsl:template match="loadmanagement[action='adjusting']" priority="-1">
        <xsl:call-template name="adjustDefault" />
    </xsl:template>
    <!-- The following ensures that one of the XSLT default templates won't 
         get applied when there are no matches. -->
    <xsl:template match="loadmanagement" priority="-5" />
    


    <!-- The following section defines the specific Scripts that are called from the
         previous section. Unlike the email templates, whitespace is not important here. -->

    <!-- First Starting Script -->
    <xsl:template name="startFirst">
        Utility is declaring a First Program Interruption. 
        Today is <xsl:value-of select="startdate" />. 
        Utility is requesting that you control your electric load to your 
        contracted firm demand level beginning at <xsl:value-of select="starttime" />. 
        The event will last until <xsl:value-of select="stoptime" />.

        Once again, the interruption begins at <xsl:value-of select="starttime" />. 
        The event will last until <xsl:value-of select="stoptime" />.
        <xsl:call-template name="closing" />
    </xsl:template>
    
    <!-- First Ending Script -->
    <xsl:template name="endFirst">
        The First Program Interruption is complete. You may restore your loads.
    </xsl:template>
    
    <!-- First Adjusting Script -->
    <xsl:template name="adjustFirst">
        Utility is extending the First Program Interruption. 
        Today is <xsl:value-of select="startdate" />. 
        Utility is extending the event that originally 
        began at <xsl:value-of select="starttime" />. The 
        event will now last for approximately <xsl:value-of select="durationhours" />
        hours and <xsl:value-of select="remainingminutes" /> minutes.

        Once again, the control period that began at
        <xsl:value-of select="starttime" /> and will continue for
        <xsl:value-of select="durationhours" /> hours and
        <xsl:value-of select="remainingminutes" /> minutes.
        <xsl:call-template name="closing" />
    </xsl:template>



    <!-- Default Starting Script -->
    <xsl:template name="startDefault">
        Utility is declaring an Interruption. Today is
        <xsl:value-of select="startdate" />. Utility is requesting 
        that you control your electric load to your contracted firm demand level beginning at
        <xsl:value-of select="starttime" />. The event will last for approximately
        <xsl:value-of select="durationhours" /> hours and
        <xsl:value-of select="remainingminutes" /> minutes.

        Once again, the interrupting begins at
        <xsl:value-of select="starttime" />. The event will last for approximately
        <xsl:value-of select="durationhours" /> hours and
        <xsl:value-of select="remainingminutes" /> minutes.
        <xsl:call-template name="closing" />
    </xsl:template>
    
    <!-- Default Ending Script -->
    <xsl:template name="endDefault">
        The Interruption is complete. You may restore your loads.
    </xsl:template>
    
    <!-- Default Adjusting Script -->
    <xsl:template name="adjustDefault">
        Utility is extending the Interruption. Today is
        <xsl:value-of select="startdate" />. Utility is extending 
        the event that originally began at
        <xsl:value-of select="starttime" />. The event will now last for approximately
        <xsl:value-of select="durationhours" /> hours and
        <xsl:value-of select="remainingminutes" /> minutes.

        Once again, the control period that began at
        <xsl:value-of select="starttime" />
        and will continue for
        <xsl:value-of select="durationhours" />
        hours and
        <xsl:value-of select="remainingminutes" />
        minutes.

        <xsl:call-template name="closing" />
    </xsl:template>

    <!-- Standard Message Scripts -->
    <xsl:template name="closing">
        You may log-in to the Utility web site to view your load profile.

        If you have questions regarding this control period, please call 
        1-555-123-4567 or your local Utility representative.
    </xsl:template>

</xsl:stylesheet>
