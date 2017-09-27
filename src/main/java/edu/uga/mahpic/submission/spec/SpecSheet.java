package edu.uga.mahpic.submission.spec;


import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mnural on 7/29/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SpecSheet implements Serializable {

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "index")
    private Integer sheetIndex;


    @XmlElementWrapper (name = "QUERIES")
    @XmlElement(name = "QUERY")
    private List<Query> queries;

    @XmlElement(name = "SHEET_STYLE")
    private SheetStyle sheetStyle;

    public String getName() {
        return name;
    }

    public Integer getSheetIndex() {
        return sheetIndex;
    }

    public List<Query> getQueries() {
        Collections.sort(queries);
        return queries;
    }

    public SheetStyle getSheetStyle() {
        if (sheetStyle == null) {
            this.sheetStyle = SheetStyle.getDefaultStyle();
        }
        return sheetStyle;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public void setSheetStyle(SheetStyle sheetStyle) {
        this.sheetStyle = sheetStyle;
    }

    /**
     * Default constructor for JAXb
     */
    public SpecSheet(){}

    /*Copy constructor*/
    private SpecSheet(SpecSheet source) {
        if (source.name != null) {
            this.name = source.name;
        }
        if (source.sheetIndex != null) {
            this.sheetIndex = source.sheetIndex;
        }
        if (source.queries != null) {
            this.queries = new ArrayList<>();
            source.queries.forEach(sourceQuery -> queries.add(sourceQuery.cloneQuery()));
        }
        if (source.sheetStyle != null) {
            this.sheetStyle = source.sheetStyle.cloneSheetStyle();
        }
    }

    public SpecSheet cloneSheet(){
        return new SpecSheet(this);
    }
}
