package edu.uga.mahpic.submission.spec.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by mnural on 7/30/15.
 */
@XmlRootElement (name = "PARAMETERS")
public class ParameterListType {


    @XmlElement(name = "PARAMETER")
    private List<ParameterType> parameterTypeList;

    public List<ParameterType> getParameters() {
        return parameterTypeList;
    }
}
