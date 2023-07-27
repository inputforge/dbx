package com.inputforge.dbx;

import java.sql.Connection;
import java.util.Optional;
import java.util.ServiceLoader;

public class DaoFactory {
    public static <T> T create(Class<T> daoClass, Connection connection) {
        var loader = ServiceLoader.load(DaoRegistry.class);

        for (DaoRegistry registry : loader) {
            Optional<? extends T> dao = registry.get(daoClass, connection);
            if (dao.isPresent()) {
                return dao.get();
            }
        }

        throw new IllegalArgumentException("Dao not found: " + daoClass.getName());
    }

}
