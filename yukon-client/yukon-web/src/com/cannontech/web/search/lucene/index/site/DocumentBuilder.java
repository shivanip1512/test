package com.cannontech.web.search.lucene.index.site;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.cannontech.web.search.lucene.index.PageType;
import com.google.common.base.Joiner;

/**
 * A simple class help with building Lucene documents for the site search index; to help centralize
 * some of the work that needs to be done to every document. For example, all fields used in page
 * arguments and summary arguments should generally be searchable so adding page or summary
 * arguments will add it to the searchable field. This will also build the Lucene document with all the
 * proper field types and such.
 */
public final class DocumentBuilder {
    /**
     * The maximum page arguments that are ever used.  This should be incremented if {@link #pageArgs(String...)}
     * is ever called with more than the number specified here.
     */
    public final static int MAX_PAGE_ARGS = 1;

    /**
     * The maximum number of summary arguments are ever used.  This should be incremented if
     * {@link #summaryArgs(String...)} is ever called with more arguments than specified here.
     */
    public final static int MAX_SUMMARY_ARGS = 5;

    private final static Joiner searchStringJoiner = Joiner.on(" ").skipNulls();

    private String pageKey;
    private PageType pageType = PageType.USER_PAGE;
    private Integer ecId;
    private final List<String> primarySearchValues = new ArrayList<>();
    private String module;
    private String pageName;
    private String path;
    private String[] pageArgs;
    private String[] summaryArgs;
    private String theme;
    private Locale locale;
    private Map<String, String> dataFields = new HashMap<>();

    public DocumentBuilder pageKey(String pageKey) {
        this.pageKey = pageKey;
        return this;
    }

    public DocumentBuilder pageType(PageType pageType) {
        this.pageType= pageType;
        return this;
    }

    public DocumentBuilder ecId(int ecId) {
        this.ecId = ecId;
        return this;
    }

    public DocumentBuilder module(String module) {
        this.module = module;
        return this;
    }

    public DocumentBuilder pageName(String pageName) {
        this.pageName = pageName;
        return this;
    }

    public DocumentBuilder path(String path) {
        this.path = path;
        return this;
    }

    /**
     * Add page arguments to the document.  If you need more than {@value #MAX_PAGE_ARGS}, you should update
     * {@link #MAX_PAGE_ARGS}.
     */
    public DocumentBuilder pageArgs(String... pageArgs) {
        checkState(pageArgs.length <= MAX_PAGE_ARGS, "Update DocumentBuilder.MAX_PAGE_ARGS please.");
        this.pageArgs = pageArgs;
        primarySearchValues.addAll(Arrays.asList(pageArgs));
        return this;
    }

    /**
     * Add summary arguments to the document.  If you need more than {@value #MAX_SUMMARY_ARGS}, you should update
     * {@link #MAX_SUMMARY_ARGS}.
     */
    public DocumentBuilder summaryArgs(String... summaryArgs) {
        checkState(summaryArgs.length <= MAX_SUMMARY_ARGS, "Update DocumentBuilder.MAX_SUMMARY_ARGS please.");
        this.summaryArgs = summaryArgs;
        primarySearchValues.addAll(Arrays.asList(summaryArgs));
        return this;
    }

    public DocumentBuilder theme(String theme) {
        this.theme = theme;
        return this;
    }

    public DocumentBuilder locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    /**
     * This method allows us to add generic data fields which may only be present in a certain subset of documents.
     * For example, we add the PaoType as a data field when indexing PAOs.
     */
    public DocumentBuilder dataField(String name, String value) {
        dataFields.put(name, value);
        return this;
    }

    public Document build() {
        Document document = new Document();

        // Search Fields
        document.add(new Field("pageKey", pageKey, Field.Store.YES, Field.Index.NOT_ANALYZED));
        document.add(new Field("pageType", pageType.name(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        String ecIdStr = ecId == null ? "none" : ecId.toString();
        document.add(new Field("energyCompanyId", ecIdStr, Field.Store.YES, Field.Index.NOT_ANALYZED));

        // "primarySearch" in case we want to add another, lower scored search field later.
        String primarySearch = searchStringJoiner.join(primarySearchValues);
        document.add(new Field("primarySearch", primarySearch, Field.Store.NO, Field.Index.ANALYZED));

        // Result Fields
        if (pageType == PageType.USER_PAGE || pageType == PageType.LEGACY) {
            document.add(new Field("module", module, Field.Store.YES, Field.Index.NOT_ANALYZED));
            document.add(new Field("pageName", pageName, Field.Store.YES, Field.Index.NOT_ANALYZED));
            document.add(new Field("path", path, Field.Store.YES, Field.Index.NOT_ANALYZED));
        }

        for (int index = 0; index < pageArgs.length; index++) {
            document.add(new Field("pageArg" + index, pageArgs[index], Field.Store.YES, Field.Index.ANALYZED));
        }

        if (summaryArgs != null) {
            for (int index = 0; index < summaryArgs.length; index++) {
                String summaryArg = summaryArgs[index] == null ? "" : summaryArgs[index];
                document.add(new Field("summaryArg" + index, summaryArg, Field.Store.YES, Field.Index.ANALYZED));
            }
        }

        if (theme != null && locale != null) {
            document.add(new Field("theme", theme, Field.Store.NO, Field.Index.NOT_ANALYZED));
            document.add(new Field("locale", locale.toLanguageTag(), Field.Store.NO, Field.Index.NOT_ANALYZED));
        }

        for (Map.Entry<String, String> entry : dataFields.entrySet()) {
            document.add(new Field(entry.getKey(), entry.getValue(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        }
        return document;
    }
}
