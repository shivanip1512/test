package com.cannontech.rest.api.utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

    private static XSSFWorkbook workbook;

    public static Object[][] getTableArray(String filePath, String sheetName) throws Exception {

        Object[][] tabArray = null;

        try {
            FileInputStream excelFile = new FileInputStream(filePath);
            // Access the required test data sheet
            workbook = new XSSFWorkbook(excelFile);
            Sheet sheet = workbook.getSheet(sheetName);
            int totalRows = sheet.getLastRowNum();
            int totalCols = sheet.getRow(0).getLastCellNum();

            tabArray = new Object[totalRows][totalCols];
            Log.info("Test Data provided is : ");

            for (int i = 0; i < totalRows; i++) {

                for (int j = 0; j < totalCols; j++) {

                    Cell cell = sheet.getRow(i + 1).getCell(j);

                    switch (cell.getCellType()) {

                    case STRING:
                        tabArray[i][j] = cell.getStringCellValue();
                        break;
                    case NUMERIC:
                        DataFormatter formatter = new DataFormatter();
                        String formattedValue = formatter.formatCellValue(cell);
                        tabArray[i][j] = formattedValue;
                        break;
                    case BOOLEAN:
                        tabArray[i][j] = cell.getBooleanCellValue();
                        break;
                    case BLANK:
                        tabArray[i][j] = "";
                        break;
                    }
                    Log.info("Row " + i + "\t" + "Col " + j + "\t" + tabArray[i][j]);
                }
            }
            workbook.close();
            excelFile.close();
        } catch (FileNotFoundException e) {
            Log.error("Could not read the Excel sheet", e);
        } catch (IOException e) {
            Log.error("Could not read the Excel sheet", e);
        }
        return tabArray;
    }
}
