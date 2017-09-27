package edu.uga.mahpic.submission.processor;

import edu.uga.mahpic.submission.spec.Specification;

/**
 * Created by mnural on 7/30/15.
 */
public abstract class FileProcessor {

    protected Specification spec;

    public FileProcessor(Specification spec){
        this.spec = spec;
    }

    public abstract void processTemplate();
}
