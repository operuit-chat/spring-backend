package at.operuit.restapi.database.query;

import at.operuit.restapi.database.Row;

import java.util.ArrayList;
import java.util.List;

public class QueryResultFactory {

    private final List<Row<?>> rows = new ArrayList<>();

    public void addRow(Row<?> row) {
        this.rows.add(row);
    }
    
    public QueryResult make() {
        return new QueryResult(rows, rows.iterator());
    }
    
}
