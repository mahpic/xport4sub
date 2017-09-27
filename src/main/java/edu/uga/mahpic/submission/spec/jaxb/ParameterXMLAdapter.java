package edu.uga.mahpic.submission.spec.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mnural on 7/30/15.
 */
public class ParameterXMLAdapter extends XmlAdapter<ParameterListType, Map<String,String>> {


    @Override
    public Map<String, String> unmarshal(ParameterListType v) throws Exception {
        Map<String, String> parameterMap = new HashMap<>();
        for (ParameterType param : v.getParameters()) {
            parameterMap.put(param.getId(), param.getValue());
        }
        return parameterMap;
    }

    @Override
    public ParameterListType marshal(Map<String, String> v) throws Exception {
        return null;
    }


}
