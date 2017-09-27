package edu.uga.mahpic.submission.spec;

import edu.uga.mahpic.submission.Config;
import edu.uga.mahpic.submission.dao.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

/**
 * Created by mnural on 7/30/15.
 * Factory class for creating Specification objects from the XML specification file.
 */
public class SpecificationFactory {
    private static Logger logger = LoggerFactory.getLogger(SpecificationFactory.class);

    public static Specification createSpecFromFile(String filename) throws JAXBException {
        return createSpecFromFile(filename, false);
    }

    public static Specification createSpecFromFile(String filename, boolean validate) throws JAXBException {
        File specFile = new File(filename);
        if (!specFile.exists()){
            logger.info("Couldn't locate template specification with the provided path. Will check in the specifications folder");
            if(specFile.getParent() == null){
                Config config = BeanFactory.getBean(Config.class);
                specFile = new File(config.getProperty("specificationsFolder") + File.separator + filename);
            }else{
                logger.error("Specification can not be found in the given location");
                specFile = null;
            }
        }
        return createSpecFromFile(specFile, validate);
    }
    public static Specification createSpecFromFile(File file, boolean validate) throws JAXBException {
        JAXBContext jaxbContext = null;
        Source xmlFile = new StreamSource(file);

        if(validate){
            StreamSource schemaFile = new StreamSource(
                    ClassLoader.getSystemResourceAsStream("template_specification.xsd")
            );
            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/XML/XMLSchema/v1.1");
            Schema schema = null;
            try {
                schema = schemaFactory.newSchema(schemaFile);
            } catch (SAXException e) {
                e.printStackTrace();
            }
            try {
                Validator validator = schema.newValidator();
                validator.validate(xmlFile);
                logger.info(xmlFile.getSystemId() + " is a valid template specification.");
            } catch (SAXException e) {
                logger.warn(xmlFile.getSystemId() + " is NOT valid.");
                logger.warn("Reason: " + e.getLocalizedMessage());
                throw new ValidationException(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        jaxbContext = JAXBContext.newInstance(Specification.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        return (Specification) unmarshaller.unmarshal(xmlFile);
    }





}
