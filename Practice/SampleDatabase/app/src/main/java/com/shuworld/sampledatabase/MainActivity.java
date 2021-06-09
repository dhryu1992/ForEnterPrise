package com.shuworld.sampledatabase;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import static java.sql.DriverManager.println;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    EditText editText2;
    TextView textView;

    SQLiteDatabase database;

    String tableName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        textView = findViewById(R.id.textView);

        Button DataBaseButton = findViewById(R.id.MakeDatabaseButton);
        DataBaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String databaseName = editText.getText().toString();
                createDatabase(databaseName);
            }
        });

        Button MakeTableButton = findViewById(R.id.MakeTableButton);
        MakeTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableName = editText2.getText().toString();
                createTable(tableName);

                insertRecord();
            }
        });
    }

    private void createTable(String name) {
        println("createTable 호출됨");

        if(database == null) {
            println("데이터베이스 생성하세요.");
            return;
        }

        database.execSQL("create table if not exists " + name + "(" + " _id integer PRIMARY KEY autoincrement,"
        + " name text, " + " age integer," + " mobile text)");

        println("테이블 생성함: " + name);
    }

    private void createDatabase(String name) {
        println("createDatabase 호출됨.");

        database = openOrCreateDatabase(name, MODE_PRIVATE, null);

        println("데이터베이스 생성함: " + name);
    }

    private void insertRecord() {
        println("insertRecord 호출됨");
        if (database == null) {
            println("데이터베이스를 먼저 생성하세요.");
            return;
        }

        if (tableName == null) {
            println("테이블을 먼저 생성하세요.");
        }

        database.execSQL("insert into " + tableName + "(name, age, mobile)" + " values " +
                "( 'John', 20, ' 010-1000-1000')");

        println("레코드 추가함. ");
    }

    public void println(String data) {
        textView.append(data + "\n");
    }

}