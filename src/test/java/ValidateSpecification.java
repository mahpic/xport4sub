import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

/**
 * Created by mnural on 8/7/15.
 */
public class ValidateSpecification {

    public static void main(String[] args) throws ClassNotFoundException {
        File schemaFile = null;
        File templateFile;
        Source xmlFile;


        if(args.length < 1 || args.length > 2){
            printUsage();
            System.exit(0);
        }else if(args.length == 1){
            schemaFile = new File("specifications/template_specification.xsd");
        }else{
            schemaFile = new File(args[1]);
        }
        if (!schemaFile.exists()) {
            System.out.println("Schema file path is invalid.\nPath: " + schemaFile.getAbsolutePath());
            System.exit(0);
        }
        templateFile = new File(args[0]);
        if (!templateFile.exists()) {
            System.out.println("The specification file path is invalid.\nPath: " + templateFile.getAbsolutePath());
            System.exit(0);
        }

        xmlFile = new StreamSource(templateFile);
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
            System.out.println(xmlFile.getSystemId() + " is valid");
        } catch (SAXException e) {
            System.out.println(xmlFile.getSystemId() + " is NOT valid");
            System.out.println("Reason: " + e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printUsage() {
        System.out.println("ValidateSpecification templateFilePath [schemaFilePath]");
    }
}
