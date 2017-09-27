package edu.uga.mahpic.submission.spec;

import edu.uga.mahpic.submission.Config;
import edu.uga.mahpic.submission.dao.BeanFactory;
import edu.uga.mahpic.submission.dao.DataSource;
import edu.uga.mahpic.submission.dao.Tuple;
import javafx.geometry.Orientation;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mnural on 7/29/15.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Query implements Comparable<Query>, Serializable{

    private DataSource dataSource = BeanFactory.getBean(DataSource.class);
    private final Config config = BeanFactory.getBean(Config.class);

    @XmlAttribute(name = "name")
    private String id;

    @XmlAttribute(name = "orientation")
    private Orientation orientation;

    @XmlAttribute(name = "headers")
    private boolean includeHeaders;

    @XmlAttribute(name = "startRow")
    private int startRow;

    @XmlAttribute(name = "startColumn")
    private int startColumn;

    @XmlAttribute(name = "shiftAfter")
    private Integer shiftAfter;

    @XmlAttribute(name = "linkToParent")
    private String linkToParent;

    @XmlElement(name = "QUERY_STRING")
    private String queryString;

    @XmlElementWrapper(name = "FIELD_MAPPING")
    @XmlElement(name = "FIELD")
    private Field[] fieldIndices;

    @XmlElement(name = "QUERY")
    private Query child;

    @XmlElement(name = "STYLE")
    private Style style;

    @XmlElement(name = "HEADER_STYLE")
    private Style headerStyle;

    /**
     * Empty Constructor for Spring/JAXb Injection
     */
    public Query(){}

    /**Copy Constructor
     * @param source Source Query
     */
    private Query(Query source) {
//        this.dataSource = source.dataSource;
//        this.config = source.config;
        if (source.id != null) {
            this.id = source.id;
        }
        if (source.orientation != null) {
            this.orientation = source.orientation;
        }
        this.includeHeaders = source.includeHeaders;
        this.startColumn = source.startColumn;
        this.startRow = source.startRow;
        if (source.shiftAfter != null) {
            this.shiftAfter = source.shiftAfter;
        }
        if (source.linkToParent != null) {
            this.linkToParent = source.linkToParent;
        }
        if (source.queryString != null) {
            this.queryString = source.queryString;
        }
        if (source.fieldIndices != null) {
            this.fieldIndices = new Field[source.fieldIndices.length];
            for (int i = 0; i < source.fieldIndices.length; i++){
                this.fieldIndices[i]= source.fieldIndices[i].cloneField();
            }
        }
        if (source.child != null) {
            this.child = source.child.cloneQuery();
        }
        if (source.style != null) {
            this.style = source.style.cloneStyle();
        }
        if (source.headerStyle != null) {
            this.headerStyle = source.headerStyle.cloneStyle();
        }
    }

    public Orientation getOrientation() {
        if (orientation == null){
            return Orientation.HORIZONTAL;
        }
        return orientation;
    }

    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public void setStartColumn(int startColumn) {
        this.startColumn = startColumn;
    }

    public Integer getShiftAfter() {
        return shiftAfter;
    }

    public String getLinkToParent() {
        return linkToParent;
    }

    public String getQueryString() {
        return queryString;
    }

    public Field[] getFieldIndices() {
        if (fieldIndices == null) {
            fieldIndices = new Field[0];
        }
        return fieldIndices;
    }

    public Query getChild() {
        return child;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public Style getStyle() {
        return style;
    }

    public Style getHeaderStyle() {
        return headerStyle;
    }

    private String prepareQuery(Map<String, String> parameters) throws Exception {
        String parameterIdentifier = config.getProperty("paramIdentifier");
        if (queryString.contains(parameterIdentifier)) {
            StringBuffer sb = new StringBuffer();
            String patternRegex = "(" + parameterIdentifier + ")(\\w+)(\\s|;|'|$|\\z)";
            Pattern pattern = Pattern.compile(patternRegex);
            Matcher matcher = pattern.matcher(queryString);
            while (matcher.find()) {
                String param = matcher.group(2);
                if (parameters.containsKey(param)) {
                    matcher.appendReplacement(sb,parameters.get(param) + "$3");
//                    System.out.println(sb);
                } else {
                    throw new Exception("Parameter " + param + " is used in a query but not specified.");
                }
            }
            matcher.appendTail(sb);
            return sb.toString();
        }
        return queryString;
    }

    public List<Tuple> runQuery(Map<String, String> parameters) throws Exception {
        List<Tuple> results = dataSource.runQuery(prepareQuery(parameters));
        return results;
    }

    @Autowired
    public void setDataSourceDao(DataSource dataSourceDao) {
        this.dataSource = dataSourceDao;
    }

    public int compareTo (Query anotherQuery){
        if ((this.startRow - anotherQuery.startRow) == 0){
            return this.startColumn - anotherQuery.startColumn;
        }
        return this.startRow - anotherQuery.startRow;
    }

    public Query cloneQuery(){
        return new Query(this);
    }
}
