package edu.uga.mahpic.submission.dao;

import java.util.List;

/**
 * Created by mnural on 2/12/16.
 */
public interface DataSource {
    List<Tuple> runQuery(String queryString);
}
