package at.operuit.restapi.database;

import at.operuit.restapi.database.query.QueryResult;
import at.operuit.restapi.database.query.QueryResultFactory;
import at.operuit.restapi.util.data.Pair;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.mariadb.r2dbc.MariadbConnectionConfiguration;
import org.mariadb.r2dbc.MariadbConnectionFactory;
import org.mariadb.r2dbc.api.MariadbConnection;
import org.mariadb.r2dbc.api.MariadbStatement;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public record DatabaseService(Credentials credentials) {

    public MariadbConnection getConnection() {
        return getConnectionFactory().create().block();
    }

    public CompletableFuture<QueryResult> execute(Supplier<String> statement, String... args) {
        CompletableFuture<QueryResult> future = new CompletableFuture<>();
        MariadbConnection connection = getConnection();
        try {
            QueryResultFactory resultFactory = getQueryResultFactory();
            Mono.from(bindAll(connection.createStatement(statement.get()), args).execute()).blockOptional().ifPresent(mariadbResult -> mariadbResult.map(Pair::new).subscribe(pair -> {
                Row row = pair.key();
                RowMetadata metadata = pair.value();
                List<Pair<String, Object>> result = new ArrayList<>();
                for (String column : metadata.getColumnNames())
                    result.add(new Pair<>(column, row.get(column)));
                resultFactory.addRow(new at.operuit.restapi.database.Row<>(result));
            }, future::completeExceptionally, () -> future.complete(resultFactory.make())));
        } finally {
            connection.close();
        }
        return future;
    }

    private MariadbConnectionConfiguration getConfiguration() {
        return MariadbConnectionConfiguration.builder()
                .host(credentials.hostname())
                .port(credentials.port())
                .username(credentials.username())
                .password(credentials.password())
                .database(credentials.database())
                .build();
    }

    private MariadbConnectionFactory getConnectionFactory() {
        return new MariadbConnectionFactory(getConfiguration());
    }
    
    private QueryResultFactory getQueryResultFactory() {
        return new QueryResultFactory();
    }

    private MariadbStatement bindAll(MariadbStatement statement, String... args) {
        for (int i = 0; i < args.length; i++)
            statement.bind(i, args[i]);
        return statement;
    }

}
