package edu.uga.mahpic.submission.spec.jaxb;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by mnural on 7/30/15.
 */
public class ParameterType {

    @XmlAttribute (name = "id")
    private String id;

    @XmlAttribute (name = "value")
    private String value;


    public String getValue() {
        return value;
    }

    public String getId() {
        return id;
    }


}
