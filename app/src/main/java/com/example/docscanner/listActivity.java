package com.example.docscanner;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.docscanner.utils.ImgConstants;
import com.example.docscanner.utils.ListViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;


/*
 *   created by : ppotta
 *   https://recipes4dev.tistory.com/43
 */

public class listActivity extends ListActivity {

    ListView listview ;
    ListViewAdapter adapter;

    FloatingActionButton btnCamera;

    File file;
    File[] fileList;
    String sdPath = ImgConstants.sdPath;

    ArrayList list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        // Adapter 생성
        adapter = new ListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.lisview);
        listview.setAdapter(adapter);

        btnCamera = (FloatingActionButton) findViewById(R.id.fabCamera);

        file = new File(sdPath);
        fileList = file.listFiles();

        for( int i = 0; i < fileList.length; i++){
            list.add(fileList[i].getName());
        }

        setListAdapter(new ArrayAdapter(this, android.R.layout.activity_list_item, list));

/*
        // 첫 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_account_box_black_36dp),
                "Box", "Account Box Black 36dp") ;
        // 두 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_account_circle_black_36dp),
                "Circle", "Account Circle Black 36dp") ;
        // 세 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_assignment_ind_black_36dp),
                "Ind", "Assignment Ind Black 36dp") ;


 */
    }

}
