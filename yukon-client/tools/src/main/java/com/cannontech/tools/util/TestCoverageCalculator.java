package com.cannontech.tools.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.cannontech.clientutils.CTILogger;

public class TestCoverageCalculator {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        try {

            String fisheyeBaseUrl = "http://fisheye.cannontech.com/";

            String query = "select revisions " + 
            "from directory /yukon-client " + 
            "where " + 
            "  path like *.java " + 
            "  and date in [thisyear, now] " + 
            "  and added on branch MAIN " + 
            "  and is added " + 
            "order by date " + 
            "return path, author, date ";

            String encodedQuery = URLEncoder.encode(query, "UTF-8");

            String getChangeSetUrl = fisheyeBaseUrl + "api/rest/query?query=" + encodedQuery + "&rep=yukon";

            URL url = new URL(getChangeSetUrl);
            InputStream stream = url.openStream();
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(stream);
            Element rootElement = document.getRootElement();


            List<Element> children = rootElement.getChildren("row");

            Map<String,AuthorStats> authors = new HashMap<String, AuthorStats>();
            int regularFileCount = 0;
            int testFileCount = 0;

            for (Element row : children) {
                String path = row.getChildText("path");
                String author = row.getChildText("author");

                AuthorStats authorStats = authors.get(author);
                if (authorStats == null) {
                    authorStats = new AuthorStats();
                    authors.put(author, authorStats);
                }

                if (path.matches(".*Test\\.java")) {
                    authorStats.testFileCount++;
                    testFileCount++;
                    System.out.println("Got new test " + path + " from " + author);
                } else {
                    authorStats.regularFileCount++;
                    regularFileCount++;
                }
            }

            NumberFormat percentInstance = DecimalFormat.getPercentInstance();
            percentInstance.setMinimumFractionDigits(2);
            for (String author : authors.keySet()) {
                AuthorStats stats = authors.get(author);
                System.out.println(author);
                System.out.println("   New Files: " + stats.regularFileCount);
                System.out.println("   New Tests: " + stats.testFileCount);
                double d = ((double)stats.testFileCount / (double)stats.regularFileCount);
                String ratio = percentInstance.format(d);
                System.out.println("   Ratio: " + ratio);
            }
            
            System.out.println("Total");
            System.out.println("   New Files: " + regularFileCount);
            System.out.println("   New Tests: " + testFileCount);
            double d = ((double)testFileCount / (double)regularFileCount);
            String ratio = percentInstance.format(d);
            System.out.println("   Ratio: " + ratio);


        } catch (Exception e) {
            CTILogger.error(e);
        }
    }

    public static class AuthorStats {
        int regularFileCount = 0;
        int testFileCount = 0;
    }

}
