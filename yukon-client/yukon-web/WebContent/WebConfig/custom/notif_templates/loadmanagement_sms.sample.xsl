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
        action - {starting,finishing,adjusting,scheduling,canceling}
        startdate - date the event starts (e.g. "Tuesday, May 31")
        starttime - time the event starts (e.g. "3:45 PM")
        startdatetime - time and date the event starts (e.g. "2015-10-13T09:21:00")
        stopdate - date the event stops (e.g. "Tuesday, May 31")
        stoptime - time the event stops (e.g. "5:45 PM")
        stopdatetime - time and date the event stops (e.g. "2015-10-13T09:21:00")
        durationhours - number of whole hours in event
        durationminutes - numer of minutes in event
        remainingminutes - equal to: durationminutes - (durationhours * 60)
        openended - {yes,no}  
        customername - name of the CICustomer being notified 
        timezone - Time zone for the event's time.
        today - Today's date 
    -->


    <!-- The following section maps the Programs to Scripts.  A matching template is 
        required for each load program.  If no template is found the default 
        template is used.  -->

    <!-- Event Selection -->
    <xsl:template match="loadmanagement[programname='firstprogram'][action='starting']">
        <xsl:call-template name="startFirst" />
    </xsl:template>
    <xsl:template match="loadmanagement[programname='firstprogram'][action='finishing']">
        <xsl:call-template name="endFirst" />
    </xsl:template>
    <xsl:template match="loadmanagement[programname='firstprogram'][action='adjusting']">
        <xsl:call-template name="adjustFirst" />
    </xsl:template>
    <xsl:template match="loadmanagement[programname='firstprogram'][action='scheduling']">
        <xsl:call-template name="scheduleFirst" />
    </xsl:template>
    <xsl:template match="loadmanagement[programname='firstprogram'][action='canceling']">
        <xsl:call-template name="cancelFirst" />
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
    <xsl:template match="loadmanagement[action='scheduling']">
        <xsl:call-template name="scheduleDefault" />
    </xsl:template>
    <xsl:template match="loadmanagement[action='canceling']" priority="-1">
        <xsl:call-template name="cancelDefault" />
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
        <subject>First Event Start</subject>
        <body>First Start: <xsl:value-of select="startdate" /> at <xsl:value-of select="starttime" /> until <xsl:value-of select="stoptime" />. Specific Start Message.</body>
    </xsl:template>

    <!-- First Ending Script -->
    <xsl:template name="endFirst">
        <subject>First Event End</subject>
        <body>First Event Complete. You may restore your loads. Thank you.</body>
    </xsl:template>

    <!-- First Adjusting Script -->
    <xsl:template name="adjustFirst">
        <subject>First Event Adjust</subject>
        <body>Economic Adjust: <xsl:value-of select="startdate" /> at <xsl:value-of select="starttime" /> until <xsl:value-of select="stoptime" />. Specific Adjust Message.</body>
    </xsl:template>

    <!--  First Scheduling Script -->
    <xsl:template name="scheduleFirst">
        <subject>First Event Schedule</subject>
        <body>First Schedule: <xsl:value-of select="startdate" /> at <xsl:value-of select="starttime" /> until <xsl:value-of select="stoptime" />. Specific Schedule Message.</body>
    </xsl:template>

    <!-- First Cancel Script -->
    <xsl:template name="cancelFirst">
        <subject>First Event Cancel</subject>
        <body>First Event Canceled: Event on <xsl:value-of select="startdate" /> at <xsl:value-of select="starttime" /> is canceled. Specific Cancel Message </body>
    </xsl:template>

    <!-- Default Starting Script -->
    <xsl:template name="startDefault">
        <subject>Generic Event Start</subject>
        <body>Event Start Date: <xsl:value-of select="startdate" /> at <xsl:value-of select="starttime" /> until <xsl:value-of select="stoptime" />. Generic Start Message.</body>
    </xsl:template>

    <!-- Default Ending Script -->
    <xsl:template name="endDefault">
        <subject>Generic Event End</subject>
        <body>Generic Event Complete. You may restore your loads. Thank you.</body>
    </xsl:template>

    <!-- Default Adjusting Script -->
    <xsl:template name="adjustDefault">
        <subject>Generic Event Adjust</subject>
        <body>Event Start Date: <xsl:value-of select="startdate" /> at <xsl:value-of select="starttime" />. New Completion <xsl:value-of select="stoptime" />. Generic Adjust Message.</body>
    </xsl:template>

    <!-- Default Scheduling Script -->
    <xsl:template name="scheduleDefault">
        <subject>Default Event Schedule</subject>
        <body>Event Schedule: <xsl:value-of select="startdate" /> at <xsl:value-of select="starttime" /> until <xsl:value-of select="stoptime" />. Generic Schedule Message.</body>
    </xsl:template>

    <!-- Default Cancel Script -->
    <xsl:template name="cancelDefault">
        <subject>Default Event Cancel</subject>
        <body>Generic Event Canceled: Event on <xsl:value-of select="startdate" /> at <xsl:value-of select="starttime" /> is canceled. Generic Cancel Message </body>
    </xsl:template>
</xsl:stylesheet>

