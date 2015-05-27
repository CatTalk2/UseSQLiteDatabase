package com.coder.usesqlitedatabase;

/**
 * Created by QiWangming on 2015/5/26.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class StudentActivity extends Activity implements View.OnClickListener{

    private EditText etName,etGrade;
    private ImageView imageView;
    private Button btnChange,btnDelete,btnAdd;
    private int id;
    private DatabaseHandler handler;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student);

        etName= (EditText) findViewById(R.id.student_name);
        etGrade= (EditText) findViewById(R.id.student_grade);
        btnChange= (Button) findViewById(R.id.btn_change);
        btnDelete= (Button) findViewById(R.id.btn_delete);
        btnAdd= (Button) findViewById(R.id.btn_add_student);
        imageView= (ImageView) findViewById(R.id.student_image);

        handler=new DatabaseHandler(this);

        intent=getIntent();

        //通过request判断，是通过那个Button点击进入的，之后隐藏或者显示相应的Button
        String request=intent.getStringExtra("request");
        switch (request){
            //点击添加按钮进入的
            case "Add":
                btnChange.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
                btnAdd.setVisibility(View.VISIBLE);
                break;
            //通过ListView Item进入的
            case "Look":
                id=intent.getExtras().getInt("id");
                etName.setText(intent.getStringExtra("name"));
                etGrade.setText(intent.getStringExtra("grade"));
                if(id%2==0)
                {
                    imageView.setImageResource(R.mipmap.girl1);
                }else{
                    imageView.setImageResource(R.mipmap.boy2);
                }
                break;
        }
        btnAdd.setOnClickListener(this);
        btnChange.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add_student:
                Student newStudent=new Student(id,etName.getText().toString(),etGrade.getText().toString());
                handler.addStudent(newStudent);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.btn_change:
                Student student=new Student(id,etName.getText().toString(),etGrade.getText().toString());
                handler.updateStudent(student);
                //这里设置resultCode是为了区分是修改后返回主界面的还是删除后返回主界面的。
                setResult(2,intent);
                finish();
                break;
            case R.id.btn_delete:
                Student s=new Student(id,etName.getText().toString(),etGrade.getText().toString());
                handler.deleteStudent(s);
                setResult(3, intent);
                finish();
                break;
        }
    }
}
