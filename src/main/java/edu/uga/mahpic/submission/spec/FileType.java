package edu.uga.mahpic.submission.spec;

/**
 * Created by mnural on 7/30/15.
 */
public enum FileType {
    EXCEL("xlsx"), XML("xml"), CSV("csv");


    private String extension;

    FileType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

}
