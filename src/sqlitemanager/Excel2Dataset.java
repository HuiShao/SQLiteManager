/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sqlitemanager;

import datasetjava.DataSet;
import datasetjava.DataTable;
import datasetjava.DataTable.fieldType;
import datasetjava.Field;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Shao
 */
public class Excel2Dataset {

    public static List<DataTable> readExcel(String inPath, boolean hasIntColumns, int colsHasInt) {
        List<DataTable> out = new ArrayList();
        try {

            // Create a work book reference
            Workbook workbook = null;
            if (inPath.endsWith(".xls")) {
                workbook = new HSSFWorkbook(new FileInputStream(inPath));
            } else if (inPath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(new FileInputStream(inPath));
            } else {
                System.err.println("No XLS or XLSX file found!");
                return out;
            }

            //Create a count of the sheets in the file
            short sheetsCount = (short) workbook.getNumberOfSheets();

            //create a reference of sheet, cell, first head, last head, head name, 
            //sheet name, row count and row content
            Sheet sheet;
            Row row;
            Cell cell;
            int firstIndex = Integer.MIN_VALUE;
            int lastIndex = Integer.MAX_VALUE;
            String[] headName;
            fieldType[] fieldTypes;

            String sheetName;

            int rowCount;

            Object cellValue;

            for (int i = 0; i < sheetsCount; i++) {
                sheetName = workbook.getSheetName(i);
                try {
                    sheet = workbook.getSheetAt(i);
                    rowCount = sheet.getLastRowNum() + 1;
                    if (rowCount < 1) {
                        break;
                    }

//                row = sheet.getRow(0);
//                for (int j = 0; j < rowCount; j++) {
//                    row = sheet.getRow(j);
//                    if (firstIndex < row.getFirstCellNum()) {
//                        firstIndex = row.getFirstCellNum();
//                    }
//                    if (lastIndex > row.getLastCellNum()) {
//                        lastIndex = row.getLastCellNum();
//                    }
//                }
                    row = sheet.getRow(0); // Head row
                    firstIndex = row.getFirstCellNum();
                    lastIndex = row.getLastCellNum();
                    headName = new String[lastIndex];
                    fieldTypes = new fieldType[lastIndex];
                    List<String> names = new ArrayList();

                    for (int index = firstIndex; index < lastIndex; index++) {
                        String name = row.getCell(index).toString();
                        if (names.contains(name)) {
                            JOptionPane.showMessageDialog(null, String.format("Field \"%s\" duplicated!", name), "Notice", JOptionPane.ERROR_MESSAGE);
                            return null;
                        } else {
                            names.add(name);
                        }
                        headName[index] = name;
                        fieldTypes[index] = fieldType.Double;
                    }

                    // Detect field types
                    for (int k = 1; k < rowCount; k++) {
                        row = sheet.getRow(k);

                        if (row == null) {
                            break;
                        }

                        for (int index = firstIndex; index < lastIndex; index++) {
                            if (fieldTypes[index] != fieldType.String) {
                                if (row.getCell(index) != null) {
                                    fieldTypes[index] = fieldType.getType(getCellType(row.getCell(index).getCellType()));
                                } else {
                                    fieldTypes[index] = fieldType.String;
                                }
                            }
                        }
                    }

                    DataTable tempTable = new DataTable(sheetName);

                    for (int index = firstIndex; index < lastIndex; index++) {
                        tempTable.addField(headName[index], fieldTypes[index]);
                    }

                    for (int k = 1; k < rowCount; k++) {
                        row = sheet.getRow(k);

                        if (row == null) {
                            break;
                        }
                        tempTable.addRecord();

                        for (int index = firstIndex; index < lastIndex; index++) {
                            cell = row.getCell(index);
                            if (fieldTypes[index] == fieldType.Double) {
                                try {
                                    cellValue = cell.getNumericCellValue();
                                } catch (Exception e) {
                                    System.err.println(String.format("Error reading Sheet: %s, Row: %d, Column: %d", cell.getSheet().getSheetName(), cell.getRowIndex(), cell.getColumnIndex()));
                                    cellValue = cell.getStringCellValue().trim();
                                }
                            } else if (fieldTypes[index] == fieldType.Integer) {
                                try {
                                    cellValue = (int) cell.getNumericCellValue();
                                } catch (Exception e) {
                                    System.err.println(String.format("Error reading Sheet: %s, Row: %d, Column: %d", cell.getSheet().getSheetName(), cell.getRowIndex(), cell.getColumnIndex()));
                                    cellValue = cell.getStringCellValue().trim();
                                }
                            } else {
                                if (cell == null) {
                                    cellValue = "";
                                } else {
                                    try {
                                        try {
                                            cellValue = cell.getNumericCellValue();
                                        } catch (Exception e) {
                                            cellValue = cell.getStringCellValue().trim();
                                        }
                                    } catch (Exception e) {
                                        System.err.println(String.format("Error reading Sheet: %s, Row: %d, Column: %d", cell.getSheet().getSheetName(), cell.getRowIndex(), cell.getColumnIndex()));
                                        cellValue = cell.getNumericCellValue();
                                    }
                                }
                            }
                            tempTable.getField(index).set(tempTable.getRecordCount() - 1, cellValue);
                        }
                    }

                    if (hasIntColumns) {
                        DataTable table = new DataTable(tempTable.getName());
                        List<Integer> updateFields = new ArrayList();
                        if (colsHasInt < 1) { // 0 or negative means check all columns
                            colsHasInt = tempTable.getRecordCount();
                        }
                        int cols4Check = Math.min(colsHasInt, tempTable.getRecordCount());

                        for (int j = 0; j < cols4Check; j++) {
                            Field f = tempTable.getField(j);
                            if (f.getType() != fieldType.Double) {
                                continue;
                            }
                            boolean isIntColumn = true;
                            for (int recNum = 0; recNum < tempTable.getRecordCount(); recNum++) {
                                double value = Double.valueOf(f.get(recNum).toString());
                                double checkValue = Double.valueOf(String.valueOf((int) value));
                                if (value != checkValue) {
                                    isIntColumn = false;
                                    break;
                                }
                            }

                            if (isIntColumn) {
                                updateFields.add(j);
                            }
                        }

                        for (int j = 0; j < tempTable.getFieldCount(); j++) {
                            fieldType type = tempTable.getField(j).getType();
                            if (updateFields.contains(j)) {
                                type = fieldType.Integer;
                            }
                            table.addField(tempTable.getField(j).getName(), type);
                        }

                        for (int recNum = 0; recNum < tempTable.getRecordCount(); recNum++) {
                            table.addRecord();
                            for (int col = 0; col < tempTable.getFieldCount(); col++) {
                                Object rowItem;

                                if (updateFields.contains(col)) {
                                    Double value = (double) tempTable.getRecord(recNum).get(col);
                                    rowItem = value.intValue();
                                } else {
                                    rowItem = tempTable.getRecord(recNum).get(col);
                                }
                                table.getField(col).set(table.getRecordCount() - 1, rowItem);
                            }
                        }
                        out.add(table);
                    } else {
                        out.add(tempTable);
                    }
                } catch (Exception e) {
                    Logger.getLogger(Excel2Dataset.class.getName()).log(Level.SEVERE, null, e);
                    JOptionPane.showMessageDialog(null, String.format("Loading sheet %s error!", sheetName), "Notice", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Excel2Dataset.class.getName()).log(Level.SEVERE, null, ex);
        }
        return out;
    }

    public static void export2Excel(String inPath, String outPath) {
        if (inPath == null || !new File(inPath).exists()) {
            System.err.println("DataSet not found!");
            return;
        }

        export2Excel(DataSet.importSQLiteDatabase(inPath).getTables(), outPath);
    }

    public static void export2Excel(DataSet ds, String outPath) {
        if (ds == null || ds.getTableCount() == 0) {
            System.err.println("DataSet is null or no table to export!");
            return;
        }

        export2Excel(ds.getTables(), outPath);
    }

    public static void export2Excel(List<DataTable> dts, String outPath) {
        if (dts == null || dts.isEmpty()) {
            System.err.println("No DataTable was found!");
            return;
        }

        try {

            // Create a work book reference
            Workbook excel = null;
            if (outPath.endsWith(".xls")) {
                excel = new HSSFWorkbook();
            } else if (outPath.endsWith(".xlsx")) {
                excel = new XSSFWorkbook();
            } else {
                System.err.println("No XLS or XLSX file found!");
                return;
            }
            DataTable dt;
            for (int i = 0; i < dts.size(); i++) {
                dt = dts.get(i);
                excel.createSheet(dt.getName());
                Sheet sheet = excel.getSheet(dt.getName());

                sheet.createRow(0);
                Row r;
                Cell c;
                int fieldCt = dt.getFieldCount();
                fieldType[] types = dt.getFieldTypes();
                for (int j = 0; j < dt.getRecordCount() + 1; j++) {
                    sheet.createRow(j);
                    for (int k = 0; k < fieldCt; k++) {
                        r = sheet.getRow(j);
                        r.createCell(k);
                    }
                }

                for (int j = 0; j < fieldCt; j++) {
                    r = sheet.getRow(0);
                    r.createCell(j);
                    r.getCell(j).setCellValue(dt.getFieldNames()[j]);
                }

                for (int j = 0; j < fieldCt; j++) {
                    switch (types[j]) {
                        case Integer:
                            for (int k = 1; k < dt.getRecordCount() + 1; k++) {
                                sheet.getRow(k).getCell(j).setCellValue((int) dt.getField(j).get(k - 1));
                            }
                            break;
                        case Double:
                            for (int k = 1; k < dt.getRecordCount() + 1; k++) {
                                sheet.getRow(k).getCell(j).setCellValue((double) dt.getField(j).get(k - 1));
                            }
                            break;
                        case String:
                            for (int k = 1; k < dt.getRecordCount() + 1; k++) {
                                sheet.getRow(k).getCell(j).setCellValue((String) dt.getField(j).get(k - 1));
                            }
                            break;
                    }
                }
            }

            excel.write(new FileOutputStream(outPath));

        } catch (Exception ex) {
            System.err.println(String.format("Exporting to %s ERROR!", outPath));
            Logger.getLogger(Excel2Dataset.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void export2Sheet() {

    }

    public static DataSet readExcel(String inPath, String outPath, boolean hasIntColumns, int colsHasInt) {
        DataSet out = new DataSet(outPath);

        for (DataTable table : readExcel(inPath, hasIntColumns, colsHasInt)) {
            out.insertTable(table);
        }

        return out;
    }

    public static List<DataTable> readExcelFromPath(String inPath, boolean hasIntColumns, int colsHasInt) {
        List<DataTable> out = new ArrayList();

        File f = new File(inPath);

        FilenameFilter textFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                if (lowercaseName.endsWith(".xls")) {
                    return true;
                } else if (lowercaseName.endsWith(".xlsx")) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        for (File file : f.listFiles(textFilter)) {
            for (DataTable table : readExcel(file.getAbsolutePath(), hasIntColumns, colsHasInt)) {
                out.add(table);
            }
        }

        return out;
    }

    private static String getCellType(int cellType) {
        switch (cellType) {
            case 0:
                return "Double";
            case 1:
                return "String";
            case 2:
                return "String";
            case 3:
                return "String";
            case 4:
                return "String";
            case 5:
                return "String";
        }
        return "String";
    }

    public static void main(String[] args) {
        String inPath = "C:\\Users\\Shawn\\Desktop\\test01.xlsx";
        String outPath = "C:\\Users\\Shawn\\Desktop\\test.db3";

        DataSet ds = new DataSet(outPath);

        for (DataTable table : readExcel(inPath, true, 1)) {
            if (ds.containsTable(table.getName())) {
                ds.removeTableIfExists(table.getName());
            }
            ds.insertTable(table);
        }

        ds.save();

//        String inPath = "C:\\Users\\Shao\\Desktop\\test.xlsx";
//        String outPath = "C:\\Users\\Shao\\Desktop\\test.db3";
//
//        DataSet ds = readExcel(inPath, outPath, false);
//
//        ds.save();
    }
}
