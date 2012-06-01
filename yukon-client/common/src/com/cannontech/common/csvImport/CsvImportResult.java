package com.cannontech.common.csvImport;

import java.util.Arrays;
import java.util.List;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.Lists;

/**
 * An object that represents the results of an import attempt for a single data row.
 */
public class CsvImportResult implements ImportResult {
    private CsvImportResultType resultType;
    private List<String> arguments = Lists.newArrayList();
    
    /**
     * Create a new import result. The result type is used to determine the message. The
     * action and any arguments are passed as parameters to the message.
     */
    public CsvImportResult(ImportAction action, CsvImportResultType resultType, String... arguments) {
        this.resultType = resultType;
        this.arguments.add(action.getDbString());
        this.arguments.addAll(Arrays.asList(arguments));
    }
    
    /**
     * Create a new import result. The result type is used to determine the message. The
     * arguments are passed as parameters to the message.
     */
    public CsvImportResult(CsvImportResultType resultType, String... arguments) {
        this.resultType = resultType;
        this.arguments.addAll(Arrays.asList(arguments));
    }
    
    /**
     * @return The MessageSourceResolvable describing the results of the import attempt.
     */
    public YukonMessageSourceResolvable getMessage() {
        return new YukonMessageSourceResolvable(resultType.getFormatKey(), arguments.toArray());
    }
    
    /**
     * @return True if the import attempt was successful, false if it failed.
     */
    public boolean isSuccess() {
        return resultType.isSuccess();
    }
}
