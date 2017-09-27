package edu.uga.mahpic.submission.dao;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.List;

/**
 * Created by mnural on 8/4/15.
 */
public class JdbcDataSource extends JdbcDaoSupport implements DataSource {

    @Override
    public List<Tuple> runQuery(String queryString) {
        List<Tuple> results = getJdbcTemplate().query(queryString, (rs, rowNum) -> {
            int numFields = rs.getMetaData().getColumnCount();
            Tuple tuple = new Tuple(numFields);

            for (int i = 1; i<= numFields; i++){
                tuple.getEntries().add(rs.getString(i));
                tuple.getLabels().add(rs.getMetaData().getColumnLabel(i));
            }
            return tuple;
        });

        if (results.size() == 0) {
            Tuple headers = getJdbcTemplate().query(queryString, rs -> {
                int numFields = rs.getMetaData().getColumnCount();
                Tuple header = new Tuple(numFields);
                for (int i = 1; i<= numFields; i++){
                    header.getLabels().add(rs.getMetaData().getColumnLabel(i));
                }
                return header;
            });
            results.add(headers);
        }

        return results;
    }

}