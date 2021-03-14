package com.example.biji;

public class Note {
    private long id; // 自增长，在构造的时候不用管
    private String content; // 内容
    private  String time; // 编辑时间
    private int tag; // 标签(用于笔记的筛选分类)

    public Note(){

    }

    public Note(String content, String time, int tag){
        this.content = content;
        this.time = time;
        this.tag = tag;
    }

    // 由于属性是private的，利用友元有些麻烦，于是就创建了下面这些函数来读写成员变量
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return content + "\n" + time.substring(5,16) + " " + id;
    }
}
