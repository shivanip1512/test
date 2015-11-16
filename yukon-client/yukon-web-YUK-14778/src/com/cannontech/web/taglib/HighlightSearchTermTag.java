package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.HtmlUtils;

import com.cannontech.web.search.lucene.PrefixTokenizer;

public class HighlightSearchTermTag extends BodyTagSupport {
    private String searchTerm;
    private boolean asLuceneTerms = false;

    @Override
    public int doStartTag() throws JspException {
        return EVAL_BODY_BUFFERED;
    }

    @Override
    public void setBodyContent(BodyContent bodyContent) {
        super.setBodyContent(bodyContent);
    }

    @Override
    public int doAfterBody() throws JspException {
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            String output = bodyContent.getString();

            // Skip this if either string is blank/null
            if (!StringUtils.isEmpty(searchTerm) && !StringUtils.isEmpty(output)) {
                if (!asLuceneTerms) {
                    output = doReplace(output, searchTerm, false);
                } else {
                    // Find Lucene-specific unique search terms.  In other words, keep only the largest version for
                    // similar terms.  For example, use "metering" if both "meter" and "metering" were searched.
                    String[] rawTerms = searchTerm.split(PrefixTokenizer.TOKEN_DELIMITER_PATTERN + "+");
                    List<String> terms = new ArrayList<>();
                    for (String raw : rawTerms) {
                        boolean found = false;
                        for (String term : terms) {
                            if (StringUtils.containsIgnoreCase(term, raw)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            terms.add(raw);
                        }
                    }
                    for (String term : terms) {
                        output = doReplace(output, term, true);
                    }
                }
            }

            pageContext.getOut().write(output);
        } catch (IOException e) {
            throw new JspException("Unable to output highlighted search term.", e);
        }
        return EVAL_PAGE;
    }

    private static String doReplace(String replaceWithin, String replaceWhat, boolean startWordDivision) {
        String result = replaceWithin.replaceFirst("(?i)"
                + (startWordDivision ? "(?<=" + PrefixTokenizer.TOKEN_DELIMITER_PATTERN + ")" : "")
                + "(" + replaceWhat + ")", "<strong>$1</strong>");
        return result;
    }

    public void setTerm(String searchTerm) {
        this.searchTerm = HtmlUtils.htmlEscape(searchTerm);
    }

    public void setAsLuceneTerms(boolean asLuceneTerms) {
        this.asLuceneTerms = asLuceneTerms;
    }
}
