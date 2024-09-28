package org.example.daos;

import java.util.Set;

public interface IDAO<T> {

    Set<T> getAll();

    T getById(Long id);

    T create(T t);

    void update(T t);

    void delete(Long id);
}
