package com.example.biji;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDatabase extends SQLiteOpenHelper {

    // 一些全局常量
    public static final String TABLE_NAME = "notes"; // 名称
    public static final String CONTENT = "content"; // 内容
    public static final String ID = "_id"; // id
    public static final String TIME = "time"; // 时间
    public static final String MODE = "mode"; // 标签
    public NoteDatabase(Context context){
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME // CREATE TABLE: 创建一个表  TABLE_NAME：表的名字
                + "(" // 用括号括住一些东西来表明每一列的属性都是什么（定义每一列）
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," // 一个可以自增长的整数类型的主键（用于定位）
                + CONTENT + " TEXT NOT NULL," // 非空的内容
                + TIME + " TEXT NOT NULL," //  非空的时间
                + MODE + " INTEGER DEFAULT 1)" // 默认值为 1 的标签
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
