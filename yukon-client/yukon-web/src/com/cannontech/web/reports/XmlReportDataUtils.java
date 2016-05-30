package com.cannontech.web.reports;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

import com.cannontech.simplereport.ColumnInfo;

public class XmlReportDataUtils {

    public static void outputReportData(List<List<String>> data,
            List<ColumnInfo> columnInfo, OutputStream oStream)
            throws IOException {

        Element root = new Element("data");
        for (List<String> columnList : data) {
            Element row = new Element("row");
            Iterator<ColumnInfo> infoIterator = columnInfo.iterator();
            for (String columnData : columnList) {
                ColumnInfo info = infoIterator.next();
                Element column = new Element(info.getName());
                column.addContent(columnData);
                row.addContent(column);
            }
            root.addContent(row);
        }

        Document doc = new Document(root);

        XMLOutputter out = new XMLOutputter();
        out.output(doc, oStream);
    }
}
