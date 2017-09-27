package edu.uga.mahpic.submission;

import edu.uga.mahpic.submission.dao.BeanFactory;
import edu.uga.mahpic.submission.processor.ExcelProcessor;
import edu.uga.mahpic.submission.processor.FileProcessor;
import edu.uga.mahpic.submission.spec.Specification;
import edu.uga.mahpic.submission.spec.SpecificationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;

/**
 * This class extracts data from a configured data source and insert it into standard excel template (.xls or .xlsx)
 * or other file types (planned in future).
 * edu.uga.mahpic.submission.Excel template contains all standard headers and data validation properties.
 * For working with information related to excel file, edu.uga.mahpic.submission.Excel class has been used and for getting
 * various information about the sheet, queries, fields of xml configuration file, configExtractor class is used.
 */
public class SpecProcessor {

    private static Logger logger = LoggerFactory.getLogger(SpecProcessor.class);
    private static Config config = BeanFactory.getBean(Config.class);
    /**
     * @param args: the first argument or arg[0] is set to rna_seq_template.xml which is the name of the config file
     * @throws Exception
     */
    public static void main(String[] args) {
        processTemplate(args[0]);
    }

    /**
     * Populate or Create an excel template according to the specified input specification
     * @param specFile The path to the XML specification file
     */
    public static void processTemplate(String specFile){
        Specification spec = null;
        try {
            spec = SpecificationFactory.createSpecFromFile(specFile, config.getBooleanProperty("validateSpec"));
        } catch (JAXBException e) {
            logger.error("Can not parse the template specification. " +
                    "Please fix the following errors in the spec and run the program again.", e);
            System.exit(0);
        }
        processTemplate(spec);
    }

    /**
     * Populate or Create an excel template according to the specified input specification
     * @param spec The object containing input specification
     */
    public static void processTemplate(Specification spec){
        FileProcessor processor = null;
        switch (spec.getFileType()) {
            case EXCEL:
                processor = new ExcelProcessor(spec);
                break;
            case CSV:
                System.err.println("The program cannot work with .csv files at this time");
                break;
            case XML:
                System.err.println("The program cannot work with .xml files at this time");
                break;
        }
        processor.processTemplate();
    }

}
