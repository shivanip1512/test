<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!-- The next five lines should not be edited for an installation. -->
    <xsl:template match="notificationmessage">
        <emailmsg>
            <xsl:apply-templates />
        </emailmsg>
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
         previous section. Note that every character between the opening <body> and the
         closing </body> will be output. Because of that, take care not to introduce 
         extra whitespace (spaces, tabs, new lines) just to make things line up
         within this file. -->

    <!-- First Starting Script -->
    <xsl:template name="startFirst">
        <subject>First Curtailment Event Start</subject>
        <body>Utility is notifying all customers enrolled in the program of a Curtailment Event.

Today is <xsl:value-of select="startdate" />. Utility is requesting that you control your electric load to your contracted firm demand level beginning at <xsl:value-of select="starttime" /> and continuing until <xsl:value-of select="stoptime" />.
<xsl:call-template name="closing" />
        </body>
    </xsl:template>

    <!-- First Ending Script -->
    <xsl:template name="endFirst">
        <subject>First Curtailment Event End</subject>
        <body>Utility is notifying all customers enrolled that the event has completed.

You may restore your loads.

Thank you for your participation.
        </body>
    </xsl:template>

    <!-- First Adjusting Script -->
    <xsl:template name="adjustFirst">
        <subject>First Curtailment Event Adjust</subject>
        <body>Utility is notifying all customers enrolled that an Adjustment to the current Curtailment Event.

Today is <xsl:value-of select="startdate" />. Utility is extending the event which originally started at <xsl:value-of select="starttime" /> and will now continue until <xsl:value-of select="stoptime" />.
<xsl:call-template name="closing" />
        </body>
    </xsl:template>




    <!-- Default Starting Script -->
    <xsl:template name="startDefault">
        <subject>Default Event Start</subject>
        <body>Utility is notifying all customers enrolled in the program of a Curtailment Event.

Today is <xsl:value-of select="startdate" />. Utility is requesting that you control your electric load to your contracted firm demand level beginning at <xsl:value-of select="starttime" /> and continuing until <xsl:value-of select="stoptime" />.
<xsl:call-template name="closing" />
        </body>
    </xsl:template>

    <!-- Default Ending Script -->
    <xsl:template name="endDefault">
        <subject>Default Event End</subject>
        <body>Utility is notifying all customers enrolled in program that the event has completed.

You may restore your loads.

Thank you for your participation.
        </body>
    </xsl:template>

    <!-- Default Adjusting Script -->
    <xsl:template name="adjustDefault">
        <subject>Default Event Adjust</subject>
        <body>Utility is notifying all customers enrolled in the program of an Adjustment to the current Curtailment Event.

Today is <xsl:value-of select="startdate" />. Utility is extending the event which originally started at <xsl:value-of select="starttime" /> and will now continue until <xsl:value-of select="stoptime" />.
<xsl:call-template name="closing" />
        </body>
    </xsl:template>

    <!-- Standard Message Scripts -->
    <xsl:template name="closing">
You may log-in to the web site to view your load profile using your assigned login name and password.

If you have questions regarding this control period, please call your local utility representative.

Thank you for your cooperation.
    </xsl:template>


</xsl:stylesheet>
