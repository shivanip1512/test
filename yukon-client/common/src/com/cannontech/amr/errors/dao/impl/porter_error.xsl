<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes" encoding="UTF-8" />
  <xsl:output doctype-public="-//W3C//DTD HTML 4.01//EN" />
  <xsl:output doctype-system="http://www.w3.org/TR/html4/strict.dtd" />

<xsl:template match="portererrorcodes">
  <html>
        <style type="text/css">
          <![CDATA[
  table {
    border: 1px solid black;
    border-collapse: collapse;
    font-size: .8em;
  }
  td, th {
    border: 1px solid black;
    padding-right: .3em;
  }
  ]]>
        </style>
  <title>Porter Error Codes</title>
  <body>
    <table>
      <tr>
        <th>Code</th>
        <th>Description</th>
        <th>Troubleshooting Options</th>
      </tr>
      <xsl:apply-templates select="error"/>
    </table>
  </body>
  </html>
</xsl:template>

<xsl:template match="error">
      <tr>
        <td><xsl:value-of select="@code"/></td>
        <td><xsl:copy-of select="description"/></td>
        <td><xsl:copy-of select="troubleshooting"/>
<!--        <div>If the problem persists, please contact Technical Support</div> --></td>
      </tr>
</xsl:template>
</xsl:stylesheet>