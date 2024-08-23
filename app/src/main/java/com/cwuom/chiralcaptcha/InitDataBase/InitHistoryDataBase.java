package com.cwuom.chiralcaptcha.InitDataBase;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.cwuom.chiralcaptcha.Dao.HistoryDao;
import com.cwuom.chiralcaptcha.Entity.EntityHistory;

@Database(entities = {EntityHistory.class}, version = 2, exportSchema = false)
public abstract class InitHistoryDataBase extends RoomDatabase {
    public abstract HistoryDao historyDao();
}
