package io.rsl.pragma.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import io.rsl.pragma.db.dbmodels.DBUser;

@Dao
public interface UserDao {

    @Insert
    void insert(DBUser dbUser);

    @Delete
    void delete(DBUser dbUser);

    @Query("DELETE FROM dbuser")
    void clear();

    @Query("SELECT COUNT(*) FROM dbuser")
    int count();

    @Query("SELECT * FROM dbuser")
    DBUser getUser();

}
