package com.inputforge.dbx;

import java.sql.Connection;
import java.util.Optional;

public interface DaoRegistry {
    <T> Optional<? extends T> get(Class<T> daoClass, Connection connection);
}
