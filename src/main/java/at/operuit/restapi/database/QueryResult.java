package at.operuit.restapi.database;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

@AllArgsConstructor
public class QueryResult implements Iterator<Row<?>> {

    @Getter
    private final List<Row<?>> rows;
    private Iterator<Row<?>> iterator;

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Row<?> next() {
        return iterator.next();
    }
    
    @Override
    public void forEachRemaining(Consumer<? super Row<?>> action) {
        iterator.forEachRemaining(action);
    }
}
