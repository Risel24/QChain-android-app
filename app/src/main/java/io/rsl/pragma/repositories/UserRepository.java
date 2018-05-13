package io.rsl.pragma.repositories;

import android.os.AsyncTask;

import io.rsl.pragma.db.dao.UserDao;
import io.rsl.pragma.db.dbmodels.DBUser;

public class UserRepository {

    private UserDao userDao;

    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    public void saveUser(DBUser user) {
        AsyncTask.execute(() -> {
            if(getCount() > 0) userDao.clear();
            userDao.insert(user);
        });
    }

    public void deleteUser(DBUser user) {
        AsyncTask.execute(() -> userDao.delete(user));
    }

    private int getCount() {
        return userDao.count();
    }

    public DBUser getUser() {
        return userDao.getUser();
    }

    public boolean hasUser() {
        return getCount() == 1;
    }

}
