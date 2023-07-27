package com.inputforge.dbx;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResultSetMapper {
    public static <T> List<T> mapToList(ResultSet resultSet, int columnIndex, Class<T> clazz) throws SQLException {
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(resultSet.getObject(columnIndex, clazz));
        }
        return list;
    }

    public static <T> Set<T> mapToSet(ResultSet resultSet, int columnIndex, Class<T> clazz) throws SQLException {
        Set<T> set = new HashSet<>();
        while (resultSet.next()) {
            set.add(resultSet.getObject(columnIndex, clazz));
        }
        return set;
    }
}
