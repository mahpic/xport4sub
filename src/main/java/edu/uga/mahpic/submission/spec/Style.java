package edu.uga.mahpic.submission.spec;


import org.apache.poi.ss.usermodel.CellStyle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by mnural on 1/12/16.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Style {

    private short borderTop;

    private short borderBottom;

    private String fontWeight;

    private String fontStyle;

    public Style(){

    }

    private Style(Style source) {
        this.borderBottom = source.borderBottom;
        this.borderTop = source.borderTop;
        if (source.fontWeight != null) {
            this.fontWeight = source.fontWeight;
        }
        if (source.fontStyle != null) {
            this.fontStyle = source.fontStyle;
        }
    }

    public short getBorderTop() {
        return borderTop;
    }

    @XmlAttribute(name = "borderTop")
    public void setBorderTop(short borderTop) {
        this.borderTop = getBorderStyle(borderTop);
    }

    public short getBorderBottom() {
        return borderBottom;
    }

    @XmlAttribute(name = "borderBottom")
    public void setBorderBottom(short borderBottom) {
        this.borderBottom = getBorderStyle(borderBottom);
    }

    private short getBorderStyle(short thickness) {
        short borderStyle = CellStyle.BORDER_NONE;
        switch (thickness){
            case 1:
                borderStyle = CellStyle.BORDER_THIN;
                break;
            case 2:
                borderStyle = CellStyle.BORDER_MEDIUM;
                break;
            case 3:
                borderStyle = CellStyle.BORDER_THICK;
                break;
        }

        return borderStyle;
    }

    public boolean isBold() {
        if (this.fontWeight == null) {
            this.fontWeight = "normal";
        }
        switch (this.fontWeight){
            case "normal":
                return false;
            case "bold":
                return true;
            default:
                return false;
        }
    }

    @XmlAttribute(name = "fontWeight")
    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    public boolean isItalic() {
        if (this.fontStyle == null) {
            this.fontStyle = "normal";
        }
        switch (this.fontStyle){
            case "normal":
                return false;
            case "italic":
                return true;
            default:
                return false;
        }
    }

    @XmlAttribute(name = "fontStyle")
    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public Style cloneStyle() {
        return new Style(this);
    }
}
