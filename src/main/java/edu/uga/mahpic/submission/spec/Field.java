package edu.uga.mahpic.submission.spec;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Created by mnural on 1/19/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Field {

    @XmlValue
    private Integer index;

    @XmlAttribute
    private Integer mergeTo;

    @XmlAttribute
    private String dataFormat;

    /**
     * Empty contructor for JAXb
     */
    public Field(){}

    /**
     * Copy Constructor
     * @param source Source Field Object to Copy
     */
    private Field(Field source) {
        if (source.index != null) {
            this.index = source.index;
        }
        if (source.mergeTo != null) {
            this.mergeTo = source.mergeTo;
        }
        if (source.dataFormat != null) {
            this.dataFormat = source.dataFormat;
        }
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getMergeTo() {
        return mergeTo;
    }

    public void setMergeTo(Integer mergeTo) {
        this.mergeTo = mergeTo;
    }

    public String getDataFormat() {
//        if (dataFormat == null) {
//            this.dataFormat = "General";
//        }
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public Field cloneField() {
        return new Field(this);
    }
}
