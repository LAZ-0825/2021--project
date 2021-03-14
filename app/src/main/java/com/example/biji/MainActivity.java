package com.example.biji;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.SocketKeepalive;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private NoteDatabase dbHelper; // 数据库

    private Context context = this;
    final String TAG = "tag";
    FloatingActionButton btn;
    TextView tv;
    private ListView lv;  // 根据传入的信息分条显示，并且长度无限，可以滚动

    private NoteAdapter adapter; // 数据库的适配器
    private List<Note> noteList = new ArrayList<Note>();// 清单
    private Toolbar myToolbar; // 工具栏

    // 下拉刷新
    private SwipeRefreshLayout swipeRefreshLayout;

    //弹出菜单
    private PopupWindow popupWindow;
    private PopupWindow popupCover;
    private ViewGroup customView;
    private ViewGroup coverView;
    private LayoutInflater layoutInfater;
    private RelativeLayout main;
    private WindowManager wm;
    private DisplayMetrics metrics;
    private TextView setting_text;
    private ImageView setting_image;

    private EditText editText;
    private ImageView imageView;

    // 刷新的进度条，只能刷新一次
    private ProgressBar progressBar;
    private Button Refresh;
    private int progress = 0;
    private Thread thread;

    // 多页滑动不知道为啥没显示
    private ViewPager viewPager;
    private List<View> views;
    private MyNewAdapter myNewAdapter;


    private Handler handler = new Handler(){
        //消息处理方法
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            progressBar.setProgress(msg.arg1);
            return ;

        }
    };


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwipeRefreshLayout swip_refresh_layout=findViewById(R.id.swipeLayout);
        swip_refresh_layout.setColorSchemeResources(R.color.colorAccent);
        swip_refresh_layout.setProgressBackgroundColorSchemeColor(R.color.colorPrimaryDark);

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.lvBackground, typedValue, true);
        Log.d(TAG, "onCreate: " + typedValue.data + " " + typedValue.resourceId);
        Log.d(TAG, "onCreate: " + getTheme().toString());


//        thread = new Thread(new Runnable() { // 刷新线程
//            @Override
//            public void run() {
//                while(true){
//                    //拿到主线程Handler的Message
//                    Message msg = handler.obtainMessage();
//                    //将进度值作为消息的参数包装进去，进度自加1
//                    msg.arg1 = progress ++;
//                    //将消息发送给主线程的Handler
//                    handler.sendMessage(msg);
//                    //这个例子是反复循环，实际项目中可能会进行页面跳转或其他处理
//                    try{
//                        //为了让您看到进度滚动效果，放慢进度上升的速度
//                        Thread.sleep(15);
//                    }
//                    catch(InterruptedException e){
//                        e.printStackTrace();
//                    }
//                    if(progress == 100){
//                        progress = 0;
//                        progressBar.setVisibility(View.INVISIBLE); // 使滚动不可见
//                        refreshListView(); // 刷新界面
////                        msg.arg1 = 0;
//                        break;
//                    }
//                }
//            }
//        });


        // 多页滑动不知道为啥没显示
        viewPager = findViewById(R.id.viewpager);
        views = new ArrayList<View>();
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());

//        View view1 = layoutInflater.inflate(R.layout.activity_main,null);
        View view2 = layoutInflater.inflate(R.layout.a1,null);
        View view3 = layoutInflater.inflate(R.layout.a2,null);
//        views.add(view1);
        views.add(view2);
        views.add(view3);

        myNewAdapter = new MyNewAdapter(views);
        viewPager.setAdapter(myNewAdapter);


        Refresh = (Button) findViewById(R.id.Refresh); // 将两个代码联系起来(定位) // 刷新界面的按钮
        progressBar = (ProgressBar) findViewById(R.id.Progress_Bar);
        Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.Refresh:
                        while (true) {
                            progressBar.setVisibility(View.VISIBLE); // 使滚动可见
                            try {
                                Thread.currentThread().sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            int progerss = progressBar.getProgress();
                            progerss = 100;
//                            progressBar.setProgress(progerss);
                            try {
                                Thread.currentThread().sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (progerss == 100) {
                                progressBar.setProgress(progerss);
                                progerss = 0;
                                Toast.makeText(MainActivity.this, "刷新中，请稍后. . .", Toast.LENGTH_SHORT).show();
                                refreshListView();

                                try {
                                    Thread.currentThread().sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(MainActivity.this, "刷新完毕！", Toast.LENGTH_SHORT).show();
                                // 刷新后进度条回不去了，使用下面的话让它显示0的话，就直接没有变化
//                                progressBar.setProgress(progerss); // 不知道为啥，它直接就显示出来了，看起来不受延时函数的影响
//                            progressBar.setVisibility(View.INVISIBLE); // 不知道为啥，它直接就不显示了，看起来不受延时函数的影响
                                break;
                            }
                        }

                        break;
                    default:
                        break;
                }
            }
        }); // 监听器

        // 下拉刷新
        swip_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swip_refresh_layout.setRefreshing(false);
                    }
                },2000);
            }
        });

        btn = findViewById(R.id.fab); // 将两个代码联系起来(定位)
        lv = findViewById(R.id.lv); // 将两个代码联系起来(定位)
        myToolbar = findViewById(R.id.myToolbar); // 将两个代码联系起来(定位)
        adapter = new NoteAdapter(getApplicationContext(), noteList); // 将两个代码联系起来(定位)
        refreshListView(); // 刷新界面
        lv.setAdapter(adapter);// 初始化

        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);      // 这两句提起就是设置自己的toolbar来代替原来的actionbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 这两句提起就是设置自己的toolbar来代替原来的actionbar
        initPopUpView();
        myToolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: shit");
                showPopUpView();
            }
        });

        lv.setOnItemClickListener(this); // 监听器

        btn.setOnClickListener(new View.OnClickListener() { // 监听器
            @Override
            public void onClick(View v) {
                // Log.d(TAG, "onClick: click");  // 之前用的测试点击事件的代码
                Intent intent = new Intent(MainActivity.this, EditActivity.class); // 定义跳转
                intent.putExtra("mode", 4);
                startActivityForResult(intent, 0);  // 启动跳转并获得结果(返回值等)
            }
        });
    }

    @Override
    protected void needRefresh() {
        Log.d(TAG, "needRefresh: Main");
        setNightMode();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void initPopUpView(){
        layoutInfater = (LayoutInflater)MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        customView = (ViewGroup) layoutInfater.inflate(R.layout.setting_layout, null);
        coverView = (ViewGroup) layoutInfater.inflate(R.layout.setting_cover, null);
        main = findViewById(R.id.main_layout);
        wm = getWindowManager();
        metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
    }

    public void showPopUpView(){
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        popupCover = new PopupWindow(coverView, width, height, false);
        popupWindow = new PopupWindow(customView, (int)(width*0.7), height, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        //在主界面加载成功之后 显示弹出
        findViewById(R.id.main_layout).post(new Runnable() {
            @Override
            public void run() {
                popupCover.showAtLocation(main, Gravity.NO_GRAVITY, 0, 0);
                popupWindow.showAtLocation(main, Gravity.NO_GRAVITY, 0, 0);

                setting_image = customView.findViewById(R.id.setting_settings_image);
                setting_text = customView.findViewById(R.id.setting_settings_text);

                setting_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, UserSettingsActivity.class));
                    }
                });

                setting_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, UserSettingsActivity.class));
                    }
                });

                coverView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });

                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        popupCover.dismiss();
                        Log.d(TAG, "onDismiss: test");
                    }
                });
            }


        });
    }

    //接收startActivtyForResult的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        int returnMode;
        long note_Id;
        returnMode = data.getExtras().getInt("mode", -1);
        note_Id = data.getExtras().getLong("id", 0);

        if (returnMode == 1) {  //update current note

            String content = data.getStringExtra("content"); // 获取输入的字符串，并储存在content里面
            String time = data.getStringExtra("time"); // 获取时间的字符串，并储存在time里面
            int tag = data.getExtras().getInt("tag", 1);

            Note newNote = new Note(content, time, tag);
            newNote.setId(note_Id);
            CRUD op = new CRUD(context);
            op.open();
            op.updateNote(newNote);
            op.close();
        } else if (returnMode == 0) {  // create new note
            String content = data.getExtras().getString("content");
            String time = data.getExtras().getString("time");
            int tag = data.getExtras().getInt("tag", 1);

            Note newNote = new Note(content, time, tag);
            CRUD op = new CRUD(context);
            op.open();
            op.addNote(newNote);
            op.close();
        }else if (returnMode == 2) { // delete
            Note curNote = new Note();
            curNote.setId(note_Id);
            CRUD op = new CRUD(context);
            op.open();
            op.removeNote(curNote);
            op.close();
        }else{

        }
        refreshListView();
        super.onActivityResult(requestCode, resultCode, data);
        /*String content = data.getStringExtra("content");
        String time = data.getStringExtra("time");
        Note note = new Note(content, time, 1);
        CRUD op = new CRUD(context);
        op.open();
        op.addNote(note);
        op.close();
        refreshListView();*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        //查找
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();

        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_clear:
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("删除全部吗？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper = new NoteDatabase(context);
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                db.delete("notes", null, null);
                                db.execSQL("update sqlite_sequence set seq=0 where name='notes'");
                                refreshListView();
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    // 刷新界面
    public void refreshListView(){

        CRUD op = new CRUD(context);
        op.open();
        // set adapter
        // 设置适配器
        if (noteList.size() > 0) noteList.clear();
        noteList.addAll(op.getAllNotes());
        op.close();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //                             父元素  指代整个页面   索引(子元素的位置)   与子元素一一对应的值(是一种可以唯一指代某个元素的值)
        // 上面的解释和参数一一对应
        switch (parent.getId()) {
            case R.id.lv:
                Note curNote = (Note) parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("content", curNote.getContent());
                intent.putExtra("id", curNote.getId());
                intent.putExtra("time", curNote.getTime());
                intent.putExtra("mode", 3); // MODE of 'click to edit' 传入一个已写好的笔记
                intent.putExtra("tag", curNote.getTag());
                startActivityForResult(intent, 1);      //collect data from edit
                Log.d(TAG, "onItemClick: " + position);  // 传入intent并获得其返回值
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
        if (popupCover != null && popupCover.isShowing()) {
            popupCover.dismiss();
            popupCover = null;
        }
    }

    @Override
    public void onClick(View v) {

    }


}


//                        if(progressBar.getVisibility() == View.INVISIBLE) {
//                            try {
//                                Thread.currentThread().sleep(300);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            refreshListView();
//                            progressBar.setVisibility(View.INVISIBLE); // 使滚动可见
//                        }
//                        else {
//                            try {
//                                Thread.currentThread().sleep(400);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            progressBar.setVisibility(View.INVISIBLE);
//                        }


//    int progerss = progressBar.getProgress();
//                            progerss += 10;
//                                    progressBar.setProgress(progerss);
//                                    if(progerss == 100) {
//                                    progerss = 0;
//                                    progressBar.setProgress(progerss);
//                                    break;
//                                    }


//                        progerss += 10;
//                        progressBar.setProgress(progerss);
//                        if(progerss == 100) {
//                            progerss = 0;
//                            progressBar.setProgress(progerss);
//                        }


//                        thread.start(); // 开启线程


//                        int progerss = progressBar.getProgress();
//                        while(true) {
//                            if(progerss < 75) {
//                                progerss += 10;
//                                    progressBar.setProgress(progerss);
//                                try {
//                                    Thread.currentThread().sleep(200);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                            } else if (progerss < 100){
//                                    progerss += 5;
//                                    progressBar.setProgress(progerss);
//                                    try {
//                                        Thread.currentThread().sleep(300);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                    } else if(progerss == 100)
//                                        break;
//                        }