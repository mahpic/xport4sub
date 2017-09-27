package edu.uga.mahpic.submission.spec;

import edu.uga.mahpic.submission.Config;
import edu.uga.mahpic.submission.dao.BeanFactory;
import edu.uga.mahpic.submission.spec.jaxb.ParameterXMLAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mnural on 7/29/15.
 */
@XmlRootElement(name = "TEMPLATE")
@XmlAccessorType(XmlAccessType.NONE)
public class Specification {

    private final Logger logger = LoggerFactory.getLogger(Specification.class);
    private final Config config = BeanFactory.getBean(Config.class);

    @XmlAttribute(name = "fileType")
    private FileType fileType;

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "location")
    private String templateFilename;

    @XmlAttribute(name = "outputFile")
    private String outputFilename;

    @XmlElement(name = "PARAMETERS")
    @XmlJavaTypeAdapter(value = ParameterXMLAdapter.class)
    private Map<String, String> parameters;

    @XmlElementWrapper (name = "SHEETS")
    @XmlElement(name = "SHEET")
    private List<SpecSheet> sheets;

    public List<SpecSheet> getSheets() {
        return sheets;
    }

    public void setSheets(List<SpecSheet> sheets) {
        this.sheets = sheets;
    }

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    public void setTemplateFilename(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    /**
     * Default: EXCEL
     * @return file type if specified in the template specification.
     */
    public FileType getFileType() {
        if (fileType == null) {
            this.fileType = FileType.EXCEL;
        }
        return fileType;
    }

    public Map<String, String> getParameters() {
        if (parameters == null) {
            parameters = new HashMap<String, String>();
        }
        return parameters;
    }

    public File getTemplateFile() {
        if (templateFilename == null || templateFilename.isEmpty()) {
            logger.info("No template file is specified. A blank file will be created.");
            return null;
        }

        File templateFile = new File(templateFilename);
        if (templateFile.exists()){
            return templateFile;
        }else{
            logger.info("Couldn't locate template with the provided path. Will check in the templates folder");
            if(templateFile.getParent() == null){
                templateFile = new File(config.getProperty("templatesFolder") + File.separator + templateFilename);
            }else{
                logger.error("Template can not be found in the given location. A blank file will be created.");
                return null;
            }
        }
        return templateFile;
    }

    public File getOutputFile() {
        File outputFile;
        File outputDir = new File(config.getProperty("outputFolder"));
        if (!outputDir.exists()){
            outputDir.mkdir();
        }
        if (outputFilename != null && !outputFilename.isEmpty()) {
            outputFile = new File (outputFilename);
            if (outputFile.getParent() != null && outputFile.getParentFile().exists()) {
                return outputFile;
            }else {
                outputFile = new File (outputDir, outputFilename);
            }
        }else{
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (templateFilename != null && !templateFilename.isEmpty()) {
                outputFile = new File (outputDir, dateFormat.format(new Date()) + "_" + templateFilename);
            }else {
                outputFile = new File (outputDir, dateFormat.format(new Date()) + "_" + this.name + "." + fileType.getExtension());
            }
        }
        return outputFile;
    }

    /**
     * Default constructor for JAXb
     */
    public Specification(){}

    /**
     * Copy constructor
     * @param source
     */
    private Specification(Specification source){
        if (source.fileType != null) {
            this.fileType = source.fileType;
        }
        if (source.name != null) {
            this.name = source.name;
        }
        if (source.templateFilename != null) {
            this.templateFilename = source.templateFilename;
        }
        if (source.outputFilename != null) {
            this.outputFilename = source.outputFilename;
        }
        if (source.parameters != null) {
            this.parameters = new HashMap<>();
            source.parameters.forEach((key,value) -> this.parameters.put(key,value));
        }
        if (source.sheets != null) {
            this.sheets = new ArrayList<>();
            source.sheets.forEach(sheet -> this.sheets.add(sheet.cloneSheet()));
        }
    }

    public Specification cloneSpecification(){
        return new Specification(this);
    }
}
