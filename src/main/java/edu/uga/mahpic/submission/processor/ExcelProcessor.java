package edu.uga.mahpic.submission.processor;

import edu.uga.mahpic.submission.dao.Tuple;
import edu.uga.mahpic.submission.processor.util.ExcelUtil;
import edu.uga.mahpic.submission.spec.*;
import javafx.geometry.Orientation;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.uga.mahpic.submission.processor.util.ExcelUtil.getFormatProperties;

/**
 * Created by mnural on 7/30/15.
 */
public class ExcelProcessor extends FileProcessor {
    private final Logger logger = LoggerFactory.getLogger(ExcelProcessor.class);

    private Workbook workbook;

    private DataFormat dataFormat;

    public ExcelProcessor(Specification spec) {
        super(spec);
    }

    public void processTemplate() {
        File template = spec.getTemplateFile();
        File output = spec.getOutputFile();
        InputStream inputStream = null;
        if (template != null) {
            try {
                Files.copy(template.toPath(), output.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                logger.error("Can not create output file ", e);
            }
            try {
                inputStream = new FileInputStream(output);
                workbook = WorkbookFactory.create(inputStream);
            } catch (IOException e) {
                logger.error("Can't open the template for writing.", e);
            } catch (InvalidFormatException e) {
                logger.error("The template is not a valid excel (.xls, .xlsx) file.", e);
            }
        }else{
            workbook = prepareBlankWorkbook();

        }
        dataFormat = workbook.createDataFormat();
        List<SpecSheet> sheets = spec.getSheets();

        //Process each specSheet
        for (SpecSheet sheet : sheets) {
            Sheet worksheet;
            if (sheet.getName() != null) {
                worksheet = workbook.getSheet(sheet.getName());
            } else if (sheet.getSheetIndex() != null) {
                worksheet = workbook.getSheetAt(sheet.getSheetIndex());
            } else {
                logger.warn("Either worksheet name or index must be specified. Can not locate worksheet, skipping.");
                continue;
            }
            logger.debug("Processing Worksheet:" + worksheet.getSheetName());
            for (int i=0; i < sheet.getQueries().size(); i++) {
                Query query = sheet.getQueries().get(i);
                if (query.getQueryString() == null || query.getQueryString().trim().isEmpty()) {
                    continue;
                }
                SheetStyle sheetStyle = sheet.getSheetStyle();

                try {
                    insertIntoWorksheet(worksheet, query, sheet, false, -1, spec.getParameters());
                    //TODO PUT A GLOBAL FLAG TO TOGGLE FORMATTING
                    if (sheetStyle.isResizeColsToFit()) {
                        for (int j=0; j < getLastColumn(worksheet) ; j++){
                            worksheet.autoSizeColumn(j,true);
                            if(worksheet.getColumnWidth(j) > sheetStyle.getMaxColWidth()){
                                worksheet.setColumnWidth(j, sheetStyle.getMaxColWidth());
                            }
                        }
                    }
                    if(sheetStyle.hasFreezePane()){
                        worksheet.createFreezePane(sheetStyle.getFreezeColumns(),sheetStyle.getFreezeRows());
                    }
                    if(sheetStyle.autoResizeRows()){
                        autoResizeRows(worksheet);
                    }
                } catch (Exception e) {
                    logger.warn("Can not process query. Skipping.", e);
                }
            }
        }

        try {
            if (inputStream != null) {
                inputStream.close();
            }
            workbook.write(new FileOutputStream(output));
            workbook.close();
        } catch (IOException e) {
            logger.error("Can not save changes to the output file.", e);
        }
    }

    private int getLastColumn(Sheet sheet) {
        int maxNum = 0;
        for (int row= 0; row <= sheet.getLastRowNum(); row ++){
            int colNum = sheet.getRow(row).getLastCellNum();
            maxNum = Math.max(maxNum, colNum);
        }
        return maxNum;
    }

    private void autoResizeRows(Sheet worksheet) {
        //TODO FIX ColumnWidth calculation would fail if default font is not used.
        for (int r=0; r < worksheet.getLastRowNum(); r++) {
            Row row = worksheet.getRow(r);
            if (row == null) {
                continue;
            }
            int maxLineCount = 0;

            for (int col = 0; col <= row.getLastCellNum(); col++) {
                Cell cell = row.getCell(col);
                if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING) {
                    String cellValue = cell.getStringCellValue();
                    double mergedCellWidth = worksheet.getColumnWidthInPixels(col) *.95;

                    //Find the merged cell width.
                    for (int mr=0; mr<worksheet.getNumMergedRegions(); mr++) {
                        CellRangeAddress cr = worksheet.getMergedRegion(mr);
                        if (cr.getFirstRow() == r && cr.getFirstColumn() == col) //topLeft
                        {
                            for(int i = cr.getFirstColumn() +1 ; i <= cr.getLastColumn(); i++){
                                mergedCellWidth += worksheet.getColumnWidthInPixels(i) * .95;
                            }
                        }
                    }

                    Font poiFont = workbook.getFontAt(cell.getCellStyle().getFontIndex());
                    AttributedString attrStr = new AttributedString(cellValue);
                    copyAttributes(poiFont, attrStr, 0 ,1);

                    // Use LineBreakMeasurer to count number of lines needed for the text
                    FontRenderContext frc = new FontRenderContext(null, true, true);
                    LineBreakMeasurer measurer = new LineBreakMeasurer(attrStr.getIterator(),
                            frc);
                    int nextPos = 0;
                    int lineCnt = 0;
                    while (measurer.getPosition() < cellValue.length()) {
                        nextPos = measurer.nextOffset( (int) mergedCellWidth );
                        lineCnt++;
                        measurer.setPosition(nextPos);
                    }
                    if (lineCnt > maxLineCount) {
                        maxLineCount = lineCnt;
                    }
                }
            }

            row.setHeight((short) (worksheet.getDefaultRowHeight() * maxLineCount));
        }
    }

    private static void copyAttributes(Font font, AttributedString str, int startIdx, int endIdx) {
        str.addAttribute(TextAttribute.FAMILY, font.getFontName(), startIdx, endIdx);
        str.addAttribute(TextAttribute.SIZE, (float)font.getFontHeightInPoints());
        if (font.getBoldweight() == Font.BOLDWEIGHT_BOLD) str.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, startIdx, endIdx);
        if (font.getItalic() ) str.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE, startIdx, endIdx);
        if (font.getUnderline() == Font.U_SINGLE ) str.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, startIdx, endIdx);
    }

    private int insertIntoWorksheet(Sheet worksheet, Query query, SpecSheet specSheet, Boolean isChild, int lastIndex, Map<String, String> parameters) throws Exception {
        List<Tuple> results = query.runQuery(parameters);
        Map<String, Object> style = createStyle(query.getStyle(), null);
        if (specSheet.getSheetStyle() != null && specSheet.getSheetStyle().isWrapText()){
            style.put(CellUtil.WRAP_TEXT, true);
        }
        Map<String, Object> headerStyle = createStyle(query.getHeaderStyle(), query.getStyle());
        Field[] fieldIndices = query.getFieldIndices();
        if (results.get(0).getLabels().size() != query.getFieldIndices().length) {
            logger.info("Length of field indices in the spec is not equal to length of fields returned by the query. Ignoring field indices and doing a sequential insert");
            fieldIndices = null;
        }

        if (!isChild) {
            if (query.getShiftAfter() != null && query.getShiftAfter() < results.size()) {
                if (query.getOrientation() == Orientation.HORIZONTAL) {
                    int shiftAmount = results.size() - query.getShiftAfter() + 1;
                    worksheet.shiftRows(query.getStartRow() + query.getShiftAfter(), worksheet.getLastRowNum(), shiftAmount);
                    List<Query> queries = specSheet.getQueries();
                    if (queries.indexOf(query) + 1 < queries.size()) {
                        for (int i = queries.indexOf(query) + 1; i < queries.size(); i++) {
                            int currentRow = queries.get(i).getStartRow();
                            queries.get(i).setStartRow(currentRow + shiftAmount);
                        }
                    }
                } else {
                    logger.warn("Shifting columns is not supported at this time. Results may overwrite existing content in the worksheet.");
                }
            }
        } else if (query.getShiftAfter() != null && query.getShiftAfter() < results.size()){
            logger.warn("Shifting is only limited to the size of the root query. All child queries are ignored.");
        }
        int rowIndex = isChild? lastIndex : query.getStartRow();
        int columnIndex = query.getStartColumn();

        if (query.getOrientation() == Orientation.HORIZONTAL) {
            //If this is a child query, only include headers when there are results.
            if(query.isIncludeHeaders() && !(isChild && results.get(0).getEntries().size() == 0)){
                insertRow(worksheet, rowIndex++, columnIndex, fieldIndices, results.get(0).getLabels(), headerStyle);
            }
            for (Tuple tuple : results) {
                if(tuple.getEntries().size() == 0){
                    break;
                }
                if (tuple.getEntries().size() > 0) {
                    insertRow(worksheet, rowIndex++, columnIndex, fieldIndices, tuple.getEntries(), style);
                }


                if (query.getChild() != null){
                    //FIX prepare query overwrites original query string therefore new params are ignored.
                    Map<String, String> subParameters = spec.getParameters();
                    Query child = query.getChild();
                    subParameters.put(child.getLinkToParent(), tuple.getWithLabel(child.getLinkToParent()));
//                    query.getChild().prepareQuery(params);
                    rowIndex += insertIntoWorksheet(worksheet, child, specSheet, true, rowIndex, subParameters);
                }
            }

        } else if(query.getOrientation() == Orientation.VERTICAL){
            if(query.isIncludeHeaders()){
                insertColumn(worksheet, rowIndex, columnIndex++, fieldIndices, results.get(0).getLabels(), headerStyle);
            }
            for (Tuple tuple : results){
                if(tuple.getEntries().size() == 0){
                    break;
                }
                insertColumn(worksheet, rowIndex, columnIndex++, fieldIndices, tuple.getEntries(), style);
            }
        }
        return results.size() + (query.isIncludeHeaders() ? 1 : 0);
    }

    private void insertColumn(Sheet worksheet, int rowIndex, int columnIndex, Field[] fieldIndices, List<String> values, Map<String, Object> style){
        if (fieldIndices == null) {
            fieldIndices = new Field[values.size()];
            for (int i = 0; i < fieldIndices.length; i++){
                Field f = new Field();
                f.setIndex(i);
                fieldIndices[i] = f;
            }
        }
        for(int i = 0; i< values.size(); i++){
            Row row = CellUtil.getRow(rowIndex + fieldIndices[i].getIndex(), worksheet);
//            Row row = worksheet.getRow(rowIndex + fieldIndices[i].getIndex());
//            if (row == null) {
//                row = worksheet.getRow(rowIndex + fieldIndices[i].getIndex());
//            }
            insertCell(row, columnIndex, 0, values.get(i), style);
        }
    }

    private void insertRow(Sheet worksheet, int rowIndex, int columnIndex, Field[] fieldIndices, List<String> values, Map<String, Object> style) {
        Row row = CellUtil.getRow(rowIndex, worksheet);
//        if (row == null) {
//            row = worksheet.createRow(rowIndex);
//        }
        for(int i = 0; i< values.size(); i++ ){
            int shift = (fieldIndices != null) ? fieldIndices[i].getIndex() : i;
            if (fieldIndices != null && fieldIndices[i].getMergeTo() != null){
                int from = columnIndex + shift;
                int to = columnIndex + fieldIndices[i].getMergeTo();
                CellRangeAddress rangeAddress = new CellRangeAddress(row.getRowNum(), row.getRowNum(), from , to);
                worksheet.addMergedRegion(rangeAddress);
//                CellPropertySetter cps = new CellPropertySetter(workbook, CellUtil.BORDER_BOTTOM, border);
//                for (int col = colStart; col <= colEnd; col++) {
//                    cps.setProperty(row, col);
//                }
                for(int col = from + 1 ; col <=to ; col ++){
                    int colIdx = col;
                    style.forEach((s, o) -> CellUtil.setCellStyleProperty(CellUtil.getCell(row, colIdx), workbook, s, o));

                }

            }
            if (fieldIndices != null) {
                if(fieldIndices[i].getDataFormat() != null) {
                    style.put(CellUtil.DATA_FORMAT, dataFormat.getFormat(fieldIndices[i].getDataFormat()));
                }
            }
            insertCell(row, columnIndex, shift, values.get(i), style);
        }
    }

    private Cell insertCell(Row row, int columnIndex, int shift, String cellValue, Map<String, Object> style) {
        Cell cell = CellUtil.getCell(row, columnIndex + shift);
        try{
            cell.setCellValue(Double.parseDouble(cellValue));
        }catch (Exception e){
            cell.setCellValue(cellValue);
        }

        //IF the cell has a style different than the default workbook style,
        //We will apply it after applying the default row & column styles.
        Map<String, Object> originalStyleProperties = null;
        if (!workbook.getCellStyleAt((short) 0).equals(cell.getCellStyle())) {
            originalStyleProperties = ExcelUtil.getFormatProperties(cell.getCellStyle());
        }
        
        //Apply Default Row Style First
        if (row.getRowStyle() != null) {
            getFormatProperties(row.getRowStyle())
                    .forEach((s, o) -> CellUtil.setCellStyleProperty(cell, workbook, s, o));
        }

        //Apply Default Column Style Next
        if (row.getSheet().getColumnStyle(columnIndex + shift) != null) {
            ExcelUtil
                    .getFormatProperties(row.getSheet().getColumnStyle(columnIndex + shift))
                    .forEach((s, o) -> CellUtil.setCellStyleProperty(cell, workbook, s, o));
        }

        if (originalStyleProperties != null) {
            originalStyleProperties.forEach((s, o) -> CellUtil.setCellStyleProperty(cell, workbook, s, o));
        }
        
        //Finally apply the user defined style
        style.forEach((s, o) -> CellUtil.setCellStyleProperty(cell, workbook, s, o));

        return cell;
    }

    private Map<String,Object> createStyle(Style style, Style parentStyle) {
        Map<String,Object> styleProperties;

        //If there's a parent style, apply it first.
        if (parentStyle != null) {
            styleProperties = createStyle(parentStyle, null);
        }else{
            styleProperties = new HashMap<>();
        }

        //Finally, play styles on top of the parent style.
        if (style != null) {
            styleProperties.put(CellUtil.BORDER_BOTTOM, style.getBorderBottom());
            styleProperties.put(CellUtil.BORDER_TOP,style.getBorderTop());

            Font font = workbook.createFont();
            font.setBold(style.isBold());
            font.setItalic(style.isItalic());
            styleProperties.put(CellUtil.FONT, font.getIndex());
        }
        return styleProperties;
    }

    private Workbook prepareBlankWorkbook() {
        //TODO Take care of the situation where only the index is specified but not the name.
        Workbook blankWorkbook = new XSSFWorkbook();
        for (SpecSheet sheet : spec.getSheets()){
            blankWorkbook.createSheet(sheet.getName());
        }
        return blankWorkbook;
    }
}
