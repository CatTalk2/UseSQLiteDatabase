package com.coder.usesqlitedatabase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener{

    private ListView students;
    private StudentAdapter adapter;
    private Button btnAdd,btnSearch;
    private DatabaseHandler dbHandler;
    private List<Student> studentList;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        students= (ListView) findViewById(R.id.stduent_list);
        btnAdd= (Button) findViewById(R.id.btn_add);
        btnSearch= (Button) findViewById(R.id.btn_search);


        btnSearch.setOnClickListener(this);
        btnAdd.setOnClickListener(this);

        dbHandler=new DatabaseHandler(this);

        //获取全部学生信息
        studentList=dbHandler.getALllStudent();
        adapter=new StudentAdapter(this,studentList);
        students.setAdapter(adapter);

        //点击ListView item跳转到详细界面
        students.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(MainActivity.this,StudentActivity.class);

                //注意这里的request是为了区分是通过什么跳转到详细界面的
                intent.putExtra("request","Look");
                intent.putExtra("id",studentList.get(i).getId());
                intent.putExtra("name",studentList.get(i).getName());
                intent.putExtra("grade",studentList.get(i).getGrade());
                //
                startActivityForResult(intent, 0);
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add:
                Intent i=new Intent(MainActivity.this,StudentActivity.class);

                i.putExtra("request","Add");
                startActivityForResult(i, 1);
                break;
            case R.id.btn_search:
                AlertDialog.Builder builder=new AlertDialog.Builder(this);

                //自定义View的Dialog
                final LinearLayout searchView= (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_search,null);
                builder.setView(searchView);
                final AlertDialog dialog=builder.create();
                dialog.show();

                //为自定义View的Dialog的控件添加事件监听。
                final EditText searchName= (EditText) searchView.findViewById(R.id.search_name);
                Button btnDialogSearch= (Button) searchView.findViewById(R.id.btn_search_dialog);
                btnDialogSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        searchName.setVisibility(View.GONE);
                        ListView list = (ListView) searchView.findViewById(R.id.search_result);
                        List<Student> resultList = new ArrayList<Student>();
                        final Student searchStudent = dbHandler.getStudent(searchName.getText().toString());
                        if (searchStudent != null) {
                            resultList.add(searchStudent);
                            StudentAdapter resultAdapter = new StudentAdapter(getApplicationContext(), resultList);
                            list.setAdapter(resultAdapter);
                            list.setVisibility(View.VISIBLE);
                            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(MainActivity.this, StudentActivity.class);
                                    intent.putExtra("request", "Look");
                                    intent.putExtra("id", searchStudent.getId());
                                    intent.putExtra("name", searchStudent.getName());
                                    intent.putExtra("grade", searchStudent.getGrade());
                                    startActivityForResult(intent, 0);
                                }
                            });
                        } else {
                            //关闭Dialog
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "无此学生", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //根据返回的resultCode判断是通过哪种操作返回的，并提示相关信息；
        switch (requestCode){
            case 0:
                if (resultCode==2)
                    Toast.makeText(this,"修改成功",Toast.LENGTH_SHORT).show();
                if (resultCode==3)
                    Toast.makeText(this,"已删除",Toast.LENGTH_SHORT).show();
                break;
            case 1:
                if (resultCode==RESULT_OK)
                    Toast.makeText(this,"添加成功",Toast.LENGTH_SHORT).show();
                break;
        }
        /**
         * 如果这里仅仅使用adapter.notifyDataSetChanged()是不会刷新界面ListView的，
         * 因为此时adapter中传入的studentList并没有给刷新，即adapter也没有被刷新，所以你可以
         * 重新获取studentList后再改变adapter，我这里通过调用onCreate()重新刷新了整个界面
         */

//        studentList=dbHandler.getALllStudent();
//        adapter=new StudentAdapter(this,studentList);
//        students.setAdapter(adapter);
        onCreate(null);
    }
}
