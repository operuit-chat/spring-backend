package at.operuit.restapi.database;

import at.operuit.restapi.util.data.Pair;

import java.util.List;
import java.util.Optional;

public record Row<T>(List<Pair<String, T>> fields) {
    
    public Pair<String, T> get(int index) {
        return fields.get(index);
    }
    
    public Pair<String, T> get(String name) {
        return fields.stream().filter(field -> field.key().equals(name)).findFirst().orElse(new Pair<>(name, null));
    }
    
    public RowOptional secure() {
        return new RowOptional();
    }
    
    public class RowOptional {

        public Optional<Pair<String, T>> get(int index) {
            return Optional.ofNullable(fields.get(index));
        }

        public Optional<Pair<String, T>> get(String name) {
            return fields.stream().filter(pair -> pair.key().equals(name)).findFirst();
        }
        
    }
    
}
