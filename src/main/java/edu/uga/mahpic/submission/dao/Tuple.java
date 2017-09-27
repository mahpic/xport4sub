package edu.uga.mahpic.submission.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mnural on 7/30/15.
 */
public class Tuple {

    private List<String> entries;
    private List<String> labels;

    private int size;

    public Tuple(int columnCount) {
        this.size = columnCount;
        this.entries = new ArrayList<>(columnCount);
        this.labels = new ArrayList<>(columnCount);
    }

    public List<String> getEntries() {
        return entries;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        if (labels.size() == this.size){
            this.labels = labels;
        }
    }

    public String getWithLabel(String label) {
        return entries.get(labels.indexOf(label));
    }
}
