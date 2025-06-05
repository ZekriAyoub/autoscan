package scanmycar.model.repository;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    ID save(T entity);
    void deleteById(ID id);
    void close();
}
