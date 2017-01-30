<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">

    <xsl:template match="@* | text() | comment() | processing-instruction()">
        <xsl:copy />
    </xsl:template>

    <xsl:template match="*">
        <xsl:element name="{name()}" namespace="{namespace-uri()}">
            <xsl:apply-templates select="@* | node()" />
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>