package com.example.biji;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.textclassifier.ConversationAction;

import java.util.ArrayList;
import java.util.List;

public class CRUD { // 增删改除
    SQLiteOpenHelper dbHandler;// 数据库处理器
    SQLiteDatabase db;

    private static final String[] columns = {
            NoteDatabase.ID,
            NoteDatabase.CONTENT,
            NoteDatabase.TIME,
            NoteDatabase.MODE
    };

    public CRUD(Context context) { // 构造
        dbHandler = new NoteDatabase(context); // 新建一个NoteDatabase
    }

    public void open(){
        db = dbHandler.getWritableDatabase(); // 打开数据库处理器，进入写入模式
    }

    public void close(){
        dbHandler.close();  // 关闭数据库处理器，不可以再对立面的诗句进行访问和修改
    }

    // 增
    //把note 加入到database里面
    public Note addNote(Note note){
        //add a note object to database
        ContentValues contentValues = new ContentValues(); // 初始化
        // 加入
        contentValues.put(NoteDatabase.CONTENT, note.getContent());
        contentValues.put(NoteDatabase.TIME, note.getTime());
        contentValues.put(NoteDatabase.MODE, note.getTag());
        long insertId = db.insert(NoteDatabase.TABLE_NAME, null, contentValues);
        note.setId(insertId);
        return note;
    }

    // 查
    // 根据id获取任意一个note
    public Note getNote(long id){
        //get a note from database using cursor index
        // 根据cursor（指针）查询语句   语法见官方文档
        Cursor cursor = db.query(NoteDatabase.TABLE_NAME, columns, NoteDatabase.ID + "=?",
                new String[] {String.valueOf(id)}, null, null, null, null);
        // 如果找到了的话
        if (cursor != null)
            cursor.moveToFirst(); // 移动到最前面
        // 浅复制并返回Note
        Note e = new Note(cursor.getString(1), cursor.getString(2), cursor.getInt(3));
        return e;
    }

    // 查（获得全部）
    public List<Note> getAllNotes(){// 获取全部Note
        // 访问TABLE里面所有的数据
        Cursor cursor = db.query(NoteDatabase.TABLE_NAME, columns, null, null, null, null, null);

        List<Note> notes = new ArrayList<>();
        if (cursor.getCount() > 0){
            while (cursor.moveToNext()){
                // 通过循环不断地向List里面添加数据
                Note note = new Note();
                // 将数据放入note内
                note.setId(cursor.getLong(cursor.getColumnIndex(NoteDatabase.ID)));
                note.setContent(cursor.getString(cursor.getColumnIndex(NoteDatabase.CONTENT)));
                note.setTime(cursor.getString(cursor.getColumnIndex(NoteDatabase.TIME)));
                note.setTag(cursor.getInt(cursor.getColumnIndex(NoteDatabase.MODE)));
                notes.add(note); // 向List中添加note
            }
        }
        return notes;
    }

    // 改
    public int updateNote(Note note) {
        // 更新note
        ContentValues values = new ContentValues();
        values.put(NoteDatabase.CONTENT, note.getContent());
        values.put(NoteDatabase.TIME, note.getTime());
        values.put(NoteDatabase.MODE, note.getTag());
        //更新每一行
        return db.update(NoteDatabase.TABLE_NAME, values,
                NoteDatabase.ID + "=?", new String[] { String.valueOf(note.getId())});
    }

    // 删
    public void removeNote(Note note){ // 删除note
        //根据id来删除note
        db.delete(NoteDatabase.TABLE_NAME, NoteDatabase.ID + "=" + note.getId(), null);
    }

}
