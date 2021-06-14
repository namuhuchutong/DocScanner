package com.example.docscanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import java.util.Random;
/*
    파일 이름 지정 팝업
 */
public class PopUpActivity extends Activity {

    EditText textView;
    String FileName;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        textView = (EditText) findViewById(R.id.txtText);

    }

    public void btnOkClick(View v){

        FileName = textView.getText().toString();
        Intent intent = new Intent();

        if(FileName.length() != 0)
        {
            intent.putExtra("file name", FileName);
            setResult(RESULT_OK, intent);

        }else{
            // 임의로 이름 지정함.
            Random rnd = new Random();
            String randomStr = String.valueOf((char) ((int) (rnd.nextInt(26)) + 97));

            intent.putExtra("file name", randomStr);
            setResult(RESULT_OK, intent);
        }

        finish();
    }

    public void btnNoClick(View v){
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);

        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        // 영역 밖에 터치 방지
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed(){
        // 뒤로가기 방지
        return;
    }
}
