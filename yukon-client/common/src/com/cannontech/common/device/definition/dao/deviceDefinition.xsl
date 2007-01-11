<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
      <html>
      <body>
        <h2>Devices</h2>
        <table border="0">
        <xsl:for-each select="deviceDefinition/device">
        <xsl:sort select="displayGroup/@value" />
        <xsl:sort select="displayName/@value" />
        
        <tr bgcolor="#cccccc">
            <td><b><xsl:value-of select="displayName/@value"/></b></td>
            <td></td>
        </tr>
        <tr>
            <td align="right">Device Group:</td>
            <td><xsl:value-of select="displayGroup/@value"/></td>
        </tr>
        <tr>
            <td align="right">Java Constant:</td>
            <td><xsl:value-of select="type/@javaConstant"/></td>
        </tr>
        <tr>
            <td align="right">Device Type:</td>
            <td><xsl:value-of select="type/@value"/></td>
        </tr>
            <tr>
                <td></td>
                <td  bgcolor="#00CCFF"><h3>Pulse Accumulator Points</h3></td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <table bgcolor="#00CCFF">
                        <tr>
                            <th>Offset</th>
                            <th>Name</th>
                            <th>Multipler</th>
                            <th>Unit of Measure</th>
                        </tr>    
                        <xsl:for-each select="points/point">
                            <xsl:if test="@type='PulseAccumulator'">
                        <tr>
                            <td align="center"><xsl:value-of select="offset/@value"/></td>
                            <td><xsl:value-of select="name"/></td>
                            <td align="center"><xsl:value-of select="multiplier/@value"/></td>
                            <td align="center"><xsl:value-of select="unitofmeasure/@value"/></td>
                        </tr>
                           </xsl:if>
                        </xsl:for-each>
                    </table>
                </td>
            </tr>
            <tr>
                <td></td>
                <td bgcolor="#33FF00"><h3>Demand Accumulator Points</h3></td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <table bgcolor="#33FF00">
                        <tr>
                            <th>Offset</th>
                            <th>Name</th>
                            <th>Multipler</th>
                            <th>Unit of Measure</th>
                        </tr>    
                        <xsl:for-each select="points/point[@type='DemandAccumulator']">
                        <tr>
                            <td align="center"><xsl:value-of select="offset/@value"/></td>
                            <td><xsl:value-of select="name"/></td>
                            <td align="center"><xsl:value-of select="multiplier/@value"/></td>
                            <td align="center"><xsl:value-of select="unitofmeasure/@value"/></td>
                        </tr>
                        </xsl:for-each>
                    </table>
                </td>
            </tr>
            <tr>
                <td></td>
                <td bgcolor="#FFFF00"><h3>Analog Points</h3></td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <table bgcolor="#FFFF00">
                        <tr>
                            <th>Offset</th>
                            <th>Name</th>
                            <th>Multipler</th>
                            <th>Unit of Measure</th>
                        </tr>    
                        <xsl:for-each select="points/point">
                            <xsl:if test="@type='Analog'">
                        <tr>
                            <td align="center"><xsl:value-of select="offset/@value"/></td>
                            <td><xsl:value-of select="name"/></td>
                            <td align="center"><xsl:value-of select="multiplier/@value"/></td>
                            <td align="center"><xsl:value-of select="unitofmeasure/@value"/></td>
                        </tr>
                           </xsl:if>
                        </xsl:for-each>
                    </table>
                </td>
            </tr>
            <tr>
                <td></td>
                <td bgcolor="#FF6633"><h3>Status Points</h3></td>
            </tr>
            <tr>
                <td></td>
                <td>
                    <table bgcolor="#FF6633">
                        <tr>
                            <th>Offset</th>
                            <th>Name</th>
                            <th>State Group</th>
                        </tr>    
                        <xsl:for-each select="points/point">
                            <xsl:if test="@type='Status'">
                        <tr>
                            <td align="center"><xsl:value-of select="offset/@value"/></td>
                            <td><xsl:value-of select="name"/></td>
                            <td align="center"><xsl:value-of select="stategroup/@value"/></td>
                        </tr>
                           </xsl:if>
                        </xsl:for-each>
                    </table>
                </td>
            </tr>
            <br /><br /><br />
        </xsl:for-each>
        </table>
      </body>
      </html>
    </xsl:template>

</xsl:stylesheet>