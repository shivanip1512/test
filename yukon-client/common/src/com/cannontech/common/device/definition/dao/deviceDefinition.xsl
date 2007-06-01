<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes" encoding="UTF-8" />
  <xsl:output doctype-public="-//W3C//DTD HTML 4.01//EN" />
  <xsl:output doctype-system="http://www.w3.org/TR/html4/strict.dtd" />
  <xsl:key name="list" match="device" use="displayGroup/@value" />
  <xsl:template match="deviceDefinition">
    <html>
      <head>
        <title>Yukon Device Definitions</title>
        <style type="text/css">
          <![CDATA[
  .pointTable {
    border: 1px solid black;
    border-collapse: collapse;
    font-size: .8em;
  }
  .pointTable td, .pointTable th {
    border: 1px solid black;
    padding-right: .3em;
  }
  h3 {
    margin-top: 1em;
    margin-bottom: .2em;
    margin-left: 1em;
  }
  
  .deviceBody {
    margin-left: 2em;
  }
  ]]>
        </style>
      </head>
      <body>
        <h1>Yukon Devices</h1>
        <!-- I don't know how the following works, but it groups by displayGroup. -->
        <xsl:for-each
          select="device[generate-id(.)=generate-id(key('list', displayGroup/@value))]/displayGroup/@value">
          <xsl:sort />
          <h2>
            <xsl:value-of select="." />
          </h2>
          <xsl:for-each select="key('list', .)">
            <xsl:sort select="./displayName/@value"/>
            <h3>
              <xsl:value-of select="./displayName/@value" />
            </h3>
            <div class="deviceBody">
              Type ID:
              <xsl:value-of select="type/@value" />
              <br />
              Java Constant:
              <xsl:value-of select="type/@javaConstant" />
              <br />
              <xsl:if test="type/@changeGroup">
                Change Group:
                <xsl:value-of select="type/@changeGroup" />
                <br />
              </xsl:if>

              <xsl:if test="count(./points/point) > 0">
                <table class="pointTable">
                  <tr>
                    <th>Name</th>
                    <th>Attribute</th>
                    <th>Type</th>
                    <th>Offset</th>
                    <th>Multiplier</th>
                    <th>UofM</th>
                  </tr>
                  <xsl:for-each select="./points/point">
                    <xsl:sort select="@type" />
                    <xsl:sort select="offset/@value" data-type="number" />
                    <tr>
                      <td>
                        <xsl:value-of select="name" />
                        <xsl:if test="@init = 'true'">
                          <xsl:text>*</xsl:text>
                        </xsl:if>
                      </td>
                      <td>
                        <xsl:value-of select="attribute/@name" />
                      </td>
                      <td>
                        <xsl:value-of select="@type" />
                      </td>
                      <td>
                        <xsl:value-of select="offset/@value" />
                      </td>
                      <td>
                        <xsl:value-of select="multiplier/@value" />
                      </td>
                      <td>
                        <xsl:value-of select="unitofmeasure/@value" />
                      </td>
                    </tr>
                  </xsl:for-each>
                </table>
                <xsl:if test="count(./points/point/@init) > 0">
                  *=Initially created by default
                </xsl:if>
              </xsl:if>
              
              <h4>
                <xsl:value-of select="./displayName/@value" /> - Commands
              </h4>
			  <xsl:if test="count(./commands/command) > 0">
                <table class="pointTable">
                  <tr>
                    <th>Name</th>
                    <th>Command</th>
                    <th>PointName</th>
                  </tr>
                  <xsl:for-each select="./commands/command">
                    <tr>
                      <td>
                        <xsl:value-of select="@name" />
                      </td>
                      <td>
                        <xsl:value-of select="cmd/@text" />
                      </td>
                      <td>
	                    <xsl:for-each select="./pointRef">
	                      <li><xsl:value-of select="@name" /></li>
				  	    </xsl:for-each>
                      </td>
                    </tr>
                  </xsl:for-each>
                </table>
              </xsl:if>
            </div>
          </xsl:for-each>
        </xsl:for-each>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
