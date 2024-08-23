package com.cwuom.chiralcaptcha.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import lombok.Data;

@Entity(tableName = "t_history")
@Data
public class EntityHistory {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "history_id")
    public long hID;

    @ColumnInfo(name = "molecule_pool_index")
    public int cMoleculePoolIndex;

    @ColumnInfo(name = "filename")
    public String cFileName;

    @ColumnInfo(name = "mol_path")
    public String cMolPath;

    @ColumnInfo(name = "chiral_carbons_count")
    public int chiralCarbonsCount;

    @ColumnInfo(name = "finish_time")
    public String finishTime;

    @ColumnInfo(name = "cheating")
    public int cheating;

    @ColumnInfo(name = "passed")
    public int passed;

    @ColumnInfo(name = "check_count")
    public int checkCount;

    @ColumnInfo(name = "is_timeout")
    public int isTimeout;

    @ColumnInfo(name = "ctime")
    public long ctime;



    public EntityHistory(long hID, int cMoleculePoolIndex, String cFileName, String cMolPath, int chiralCarbonsCount, String finishTime, int cheating, int passed, int checkCount, int isTimeout, long ctime) {
        this.hID = hID;
        this.cMoleculePoolIndex = cMoleculePoolIndex;
        this.cFileName = cFileName;
        this.cMolPath = cMolPath;
        this.chiralCarbonsCount = chiralCarbonsCount;
        this.finishTime = finishTime;
        this.cheating = cheating;
        this.passed = passed;
        this.checkCount = checkCount;
        this.isTimeout = isTimeout;
        this.ctime = ctime;
    }

    @Ignore
    public EntityHistory() {}
}