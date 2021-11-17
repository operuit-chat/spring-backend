package at.operuit.restapi.database;

import java.util.ArrayList;
import java.util.List;

public class QueryResultFactory {

    private final List<Row<?>> rows;
    
    protected QueryResultFactory() {
        this.rows = new ArrayList<>();
    }
    
    public void addRow(Row<?> row) {
        this.rows.add(row);
    }
    
    public QueryResult make() {
        return new QueryResult(rows, rows.iterator());
    }
    
}
