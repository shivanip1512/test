package com.cannontech.customtasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class ExecServerUnitTests extends Task {
    private static final String INPUT_ARGS = "--report_format=XML --report_level=short";
    private static final Properties props = System.getProperties();
    private static final String FILE_SEPARATOR = props.getProperty("file.separator");
    private static final FilenameFilter filter;
    private boolean failonerror = false;
    private String todir;
    private String dir;

    static {
        filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.matches("^test_.+\\.exe$");
            }
        };
    }

    public void execute() {
        if (todir == null) {
            throw new BuildException("todir must be set");
        }

        if (dir == null) {
            throw new BuildException("dir must be set");
        }

        File readDir = new File(dir);
        if (!readDir.isDirectory()) {
            throw new BuildException(dir + " is not a directory");
        }

        File[] fileList = readDir.listFiles(filter);
        if (fileList == null) {
            throw new BuildException("could not read " + dir);
        }

        for (final File file : fileList) {
            runTest(file);
        }
    }

    private void runTest(final File file) throws BuildException {
        try {
            Process p = Runtime.getRuntime().exec(file.getAbsolutePath() + " " + INPUT_ARGS);

            String stdout = toString(p.getInputStream());
            processInputStream(p.getErrorStream(), stdout, file);

            int result = p.exitValue();
            if (result != 0 && failonerror) {
                throw new BuildException("exec returned " + result + " error code" +
                                         props.getProperty("line.separator") + stdout);
            }
        } catch (IOException e) {
            if (failonerror) throw new BuildException(e);
        } catch (JDOMException ex) {
            if (failonerror) throw new BuildException(ex);
        }
    }

    private void processInputStream(final InputStream input, final String stdout, final File file) throws IOException,JDOMException {
        Document doc = new SAXBuilder().build(input);
        Element root = doc.getRootElement();
        Element testSuiteElement = root.getChild("TestSuite");

        String testName = testSuiteElement.getAttributeValue("name");
        String testResult = testSuiteElement.getAttributeValue("result");
        String testPassed = testSuiteElement.getAttributeValue("assertions_passed");
        String testFailed = testSuiteElement.getAttributeValue("assertions_failed");

        Document newDoc = buildDocument(stdout, testName, testResult, testPassed, testFailed);
        writeDocument(newDoc, file.getName());
    }

    private Document buildDocument(final String stdout, final String testName, final String testResult,
            final String testPassed, final String testFailed) {

        final Document doc = new Document();

        final Element root = new Element("testsuite");
        root.setAttribute("errors", "0");
        root.setAttribute("failures", testFailed);
        root.setAttribute("hostname", System.getenv("COMPUTERNAME"));
        root.setAttribute("name", testName);
        root.setAttribute("tests", Integer.toString(Integer.parseInt(testPassed) + Integer.parseInt(testFailed)));
        root.setAttribute("time", "0");
        root.setAttribute("timestamp", new Date().toString());

        final Element properties = new Element("properties");
        Enumeration keys = props.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            Element property = new Element("property");
            property.setAttribute("name", key);
            property.setAttribute("value", props.getProperty(key));
            properties.addContent(property);
        }

        final Element testcase = new Element("testcase");
        testcase.setAttribute("classname", "");
        testcase.setAttribute("name", testName);
        testcase.setAttribute("time", "0");

        final Element systemOut = new Element("system-out");
        systemOut.addContent(new CDATA(stdout));

        final Element systemErr = new Element("system-err");
        systemErr.addContent(new CDATA(""));

        root.addContent(properties);
        root.addContent(testcase);
        root.addContent(systemOut);
        root.addContent(systemErr);

        doc.setRootElement(root);
        return doc;
    }

    private void writeDocument(final Document doc, final String fileName) throws IOException {
        final String fullFileName = todir + FILE_SEPARATOR + fileName + ".xml";
        final XMLOutputter outp = new XMLOutputter();
        final FileOutputStream fileOut = new FileOutputStream(new File(fullFileName));
        outp.output(doc, fileOut);
    }

    private String toString(final InputStream in) throws IOException {
        final StringBuilder sb = new StringBuilder();
        final BufferedInputStream buffedIn = new BufferedInputStream(in);

        int c;
        while ((c = buffedIn.read()) != -1) {
            sb.append((char) c);
        }
        return sb.toString();
    }

    public void setFailOnError(final boolean failonerror) {
        this.failonerror = failonerror;
    }

    public void setToDir(final String todir) {
        this.todir = todir;
    }

    public void setDir(final String dir) {
        this.dir = dir;
    }

}
