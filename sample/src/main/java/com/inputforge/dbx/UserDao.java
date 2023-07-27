package com.inputforge.dbx;

import java.sql.ResultSet;
import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM users WHERE id = ?")
    ResultSet getUserById(long id);

    @Query("SELECT count(*) FROM users WHERE id = ?")
    int countUserById(long id);

    @Query("SELECT id FROM users")
    List<Integer> getUserIds();

}
