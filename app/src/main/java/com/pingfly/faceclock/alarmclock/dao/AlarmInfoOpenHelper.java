package com.pingfly.faceclock.alarmclock.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.pingfly.faceclock.alarmclock.util.ConsUtils;

public class AlarmInfoOpenHelper extends SQLiteOpenHelper {

    private Context mContext;
    // 创建一个名为AlarmInfo的table
    /**
     *  integer: 表示整型;
     *  primary key: 表示主键;
     *  autoincrement: 表示自增长
     *
     */

    /*表名*/
    //private final String TABLE_NAME_ALARM_INFO = "AlarmInfo";

    /*创建表语句 语句对大小写不敏感 create table 表名(字段名 类型，字段名 类型，…)*/
    private static final String CREATE_ALARMINFO = "create table AlarmInfo" +
            "(id integer primary key autoincrement," +
            ConsUtils.ALARM_HOUR + " integer," +
            ConsUtils.ALARM_MINUTE + " integer ," +
            ConsUtils.ALARM_LAZY_LEVEL + " integer," +
            ConsUtils.ALARM_RING + " text," +
            ConsUtils.ALARM_TAG + " text," +
            ConsUtils.ALARM_REPEAT_DAY + " text," +
            ConsUtils.ALARM_RING_ID + " text," +
            ConsUtils.ALARM_ID + " text" +
            ")";



    public AlarmInfoOpenHelper(Context context) {
        // SQLDB_NAME="alarm.db"
        super(context, ConsUtils.SQLDB_NAME, null, 1);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建一个数据表
        db.execSQL(CREATE_ALARMINFO);
        //Toast.makeText(mContext, "创建AlarmInfo Table成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Toast.makeText(mContext, "数据库升级了", Toast.LENGTH_LONG).show();
    }
}
