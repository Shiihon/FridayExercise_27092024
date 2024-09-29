package org.example.daos;

import org.example.dtos.HotelDTO;

import java.util.Set;

public interface IDAO<T> {

    T getById(Long id);

    Set<T> getAll();

    T create(T t);

    T update(T t);

    void delete(Long id);
}
