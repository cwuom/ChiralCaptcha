package com.cwuom.chiralcaptcha.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.cwuom.chiralcaptcha.Entity.EntityHistory;

import java.util.List;


@Dao
public interface HistoryDao {
    @Query("SELECT * FROM t_history ORDER BY history_id DESC LIMIT :limit OFFSET :offset")
    List<EntityHistory> loadHistoryEntries(int offset, int limit);
    @Query("SELECT * FROM t_history ORDER BY history_id ASC LIMIT :limitOffset, :limitCount")
    List<EntityHistory> getOldestHistories(int limitOffset, int limitCount);
    @Query("DELETE FROM t_history WHERE history_id NOT IN (SELECT history_id FROM t_history ORDER BY history_id DESC LIMIT :x)")
    void deleteOlderThanX(int x);

    @Insert
    void insertHistory(EntityHistory e);


    @Query("SELECT * FROM t_history")
    List<EntityHistory> getHistory();

    @Query("DELETE FROM t_history")
    void deleteHistory();


    @Query("SELECT * FROM t_history WHERE history_id = :ID")
    EntityHistory getHistoryByID(long ID);

    @Query("DELETE FROM t_history WHERE history_id = :id")
    void deleteHistoryById(long id);


    @Query("SELECT * FROM t_history WHERE mol_path = :path")
    EntityHistory getHistoryByPath(String path);

    @Query("SELECT * FROM t_history WHERE chiral_carbons_count = :count")
    List<EntityHistory> getHistoryByCCC(int count);

    @Query("SELECT * FROM t_history WHERE cheating = :cheating")
    List<EntityHistory> getHistoryByIsCheating(int cheating);

    @Query("SELECT * FROM t_history WHERE molecule_pool_index = :index")
    List<EntityHistory> getHistoryByPoolIndex(int index);

    @Query("SELECT * FROM t_history WHERE passed = :passed")
    List<EntityHistory> getHistoryByIsPassed(int passed);


    @Update
    void updateHistory(EntityHistory e);

    @Delete
    void deleteHistory(EntityHistory e);

    @Query("DELETE FROM t_history WHERE ctime = :ctime")
    void deleteHistoryByCTime(long ctime);


    @Query("DELETE FROM t_history WHERE cheating = 1")
    void deleteCheatingHistory();

    @Query("SELECT * FROM t_history WHERE ctime <= (SELECT MAX(ctime) FROM t_history) ORDER BY ctime DESC LIMIT 1 OFFSET 1")
    EntityHistory getPreviousHistoryEntry();
}
