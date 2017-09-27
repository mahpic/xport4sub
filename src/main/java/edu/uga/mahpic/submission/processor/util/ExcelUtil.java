package edu.uga.mahpic.submission.processor.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;
import java.util.Map;

import static org.apache.poi.ss.util.CellUtil.*;

/**
 * Created by mnural on 3/10/16.
 */
public class ExcelUtil {
    private static UnicodeMapping unicodeMappings[];

    private static final class UnicodeMapping {

        public final String entityName;
        public final String resolvedValue;

        public UnicodeMapping(String pEntityName, String pResolvedValue) {
            entityName = "&" + pEntityName + ";";
            resolvedValue = pResolvedValue;
        }
    }

    public static Map<String, Object> getFormatProperties(CellStyle style) {
        Map<String, Object> properties = new HashMap<String, Object>();
        putShort(properties, ALIGNMENT, style.getAlignment());
        putShort(properties, BORDER_BOTTOM, style.getBorderBottom());
        putShort(properties, BORDER_LEFT, style.getBorderLeft());
        putShort(properties, BORDER_RIGHT, style.getBorderRight());
        putShort(properties, BORDER_TOP, style.getBorderTop());
        putShort(properties, BOTTOM_BORDER_COLOR, style.getBottomBorderColor());
        putShort(properties, DATA_FORMAT, style.getDataFormat());
        putShort(properties, FILL_BACKGROUND_COLOR, style.getFillBackgroundColor());
        putShort(properties, FILL_FOREGROUND_COLOR, style.getFillForegroundColor());
        putShort(properties, FILL_PATTERN, style.getFillPattern());
        putShort(properties, FONT, style.getFontIndex());
        putBoolean(properties, HIDDEN, style.getHidden());
        putShort(properties, INDENTION, style.getIndention());
        putShort(properties, LEFT_BORDER_COLOR, style.getLeftBorderColor());
        putBoolean(properties, LOCKED, style.getLocked());
        putShort(properties, RIGHT_BORDER_COLOR, style.getRightBorderColor());
        putShort(properties, ROTATION, style.getRotation());
        putShort(properties, TOP_BORDER_COLOR, style.getTopBorderColor());
        putShort(properties, VERTICAL_ALIGNMENT, style.getVerticalAlignment());
        putBoolean(properties, WRAP_TEXT, style.getWrapText());
        return properties;
    }

    /**
     * Sets the format properties of the given style based on the given map.
     *
     * @param style cell style
     * @param workbook parent workbook
     * @param properties map of format properties (String -> Object)
     * @see #getFormatProperties(CellStyle)
     */
    private static void setFormatProperties(CellStyle style, Workbook workbook, Map<String, Object> properties) {
        style.setAlignment(getShort(properties, ALIGNMENT));
        style.setBorderBottom(getShort(properties, BORDER_BOTTOM));
        style.setBorderLeft(getShort(properties, BORDER_LEFT));
        style.setBorderRight(getShort(properties, BORDER_RIGHT));
        style.setBorderTop(getShort(properties, BORDER_TOP));
        style.setBottomBorderColor(getShort(properties, BOTTOM_BORDER_COLOR));
        style.setDataFormat(getShort(properties, DATA_FORMAT));
        style.setFillBackgroundColor(getShort(properties, FILL_BACKGROUND_COLOR));
        style.setFillForegroundColor(getShort(properties, FILL_FOREGROUND_COLOR));
        style.setFillPattern(getShort(properties, FILL_PATTERN));
        style.setFont(workbook.getFontAt(getShort(properties, FONT)));
        style.setHidden(getBoolean(properties, HIDDEN));
        style.setIndention(getShort(properties, INDENTION));
        style.setLeftBorderColor(getShort(properties, LEFT_BORDER_COLOR));
        style.setLocked(getBoolean(properties, LOCKED));
        style.setRightBorderColor(getShort(properties, RIGHT_BORDER_COLOR));
        style.setRotation(getShort(properties, ROTATION));
        style.setTopBorderColor(getShort(properties, TOP_BORDER_COLOR));
        style.setVerticalAlignment(getShort(properties, VERTICAL_ALIGNMENT));
        style.setWrapText(getBoolean(properties, WRAP_TEXT));
    }

    /**
     * Utility method that returns the named short value form the given map.
     * @return zero if the property does not exist, or is not a {@link Short}.
     *
     * @param properties map of named properties (String -> Object)
     * @param name property name
     * @return property value, or zero
     */
    private static short getShort(Map<String, Object> properties, String name) {
        Object value = properties.get(name);
        if (value instanceof Short) {
            return ((Short) value).shortValue();
        }
        return 0;
    }

    /**
     * Utility method that returns the named boolean value form the given map.
     * @return false if the property does not exist, or is not a {@link Boolean}.
     *
     * @param properties map of properties (String -> Object)
     * @param name property name
     * @return property value, or false
     */
    private static boolean getBoolean(Map<String, Object> properties, String name) {
        Object value = properties.get(name);
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        return false;
    }

    /**
     * Utility method that puts the named short value to the given map.
     *
     * @param properties map of properties (String -> Object)
     * @param name property name
     * @param value property value
     */
    private static void putShort(Map<String, Object> properties, String name, short value) {
        properties.put(name, Short.valueOf(value));
    }

    /**
     * Utility method that puts the named boolean value to the given map.
     *
     * @param properties map of properties (String -> Object)
     * @param name property name
     * @param value property value
     */
    private static void putBoolean(Map<String, Object> properties, String name, boolean value) {
        properties.put(name, Boolean.valueOf(value));
    }

    /**
     *  Looks for text in the cell that should be unicode, like &alpha; and provides the
     *  unicode version of it.
     *
     *@param  cell  The cell to check for unicode values
     *@return       translated to unicode
     */
    public static Cell translateUnicodeValues(Cell cell) {

        String s = cell.getRichStringCellValue().getString();
        boolean foundUnicode = false;
        String lowerCaseStr = s.toLowerCase();

        for (int i = 0; i < unicodeMappings.length; i++) {
            UnicodeMapping entry = unicodeMappings[i];
            String key = entry.entityName;
            if (lowerCaseStr.indexOf(key) != -1) {
                s = s.replaceAll(key, entry.resolvedValue);
                foundUnicode = true;
            }
        }
        if (foundUnicode) {
            cell.setCellValue(cell.getRow().getSheet().getWorkbook().getCreationHelper()
                    .createRichTextString(s));
        }
        return cell;
    }

    static {
        unicodeMappings = new UnicodeMapping[] {
                um("alpha",   "\u03B1" ),
                um("beta",    "\u03B2" ),
                um("gamma",   "\u03B3" ),
                um("delta",   "\u03B4" ),
                um("epsilon", "\u03B5" ),
                um("zeta",    "\u03B6" ),
                um("eta",     "\u03B7" ),
                um("theta",   "\u03B8" ),
                um("iota",    "\u03B9" ),
                um("kappa",   "\u03BA" ),
                um("lambda",  "\u03BB" ),
                um("mu",      "\u03BC" ),
                um("nu",      "\u03BD" ),
                um("xi",      "\u03BE" ),
                um("omicron", "\u03BF" ),
        };
    }

    private static UnicodeMapping um(String entityName, String resolvedValue) {
        return new UnicodeMapping(entityName, resolvedValue);
    }
}
