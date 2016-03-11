package com.example.button;

import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.button.R;
import com.example.button.MainActivity;
public class MainActivity extends ActionBarActivity implements OnTouchListener{
	private static String show_str="";
	private static int MAXINT = 65536;
	private static int i=0;
	private static char[] tempchars = new char[MAXINT];
	private static String location = "/mnt/sdcard/";
	private static String[] java_word= {"abstract","assert","boolean","break","byte","case","catch","char"
		,"class","const","continue","default","do","double","else","enum","extends","final","finally","float"
		,"for","goto","if","implements","import","instanceof","int","interface","long","native","new","package"
		,"private","protected","public","return","strictfp","short","static","super","switch","synchronized"
		,"this","throw","throws","transient","try","void","volatile","while"};
	private static TextView mTextView01;
	private Button press;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.activity_main);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.title);
		press = (Button) findViewById(R.id.ClickButton);
		press.setOnClickListener(Listener);
		press.setOnTouchListener(this);
		mTextView01=(TextView) findViewById(R.id.TextView01);
		mTextView01.setMovementMethod(ScrollingMovementMethod.getInstance()); 
	}
	public boolean onTouch(View v,MotionEvent event){
		int action = event.getAction();
		if(action==MotionEvent.ACTION_DOWN)
			press.setBackgroundResource(R.drawable.bs);
		else if(action==MotionEvent.ACTION_UP)
			press.setBackgroundResource(R.drawable.btn);
		return false;
	}
	private static void show_java(String path){
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(path),"UTF-8");
			reader.read(tempchars);
			char ch;
			for(i=0;tempchars[i]!='\0';i++){
				ch=tempchars[i];
				if(MainActivity.is_char(ch)){
					MainActivity.word();
					i--;
				}
				else if(MainActivity.is_num(ch)){
					MainActivity.number();
				}
				else if(ch=='"'){
					MainActivity.quotation();
				}
				else if(ch=='\''){
					MainActivity.single_quotation();
				}
				else if(ch=='/'){
					i++;
					MainActivity.annotation();
				}
				else{
					judgechar(ch);
				}
			}
			reader.close();
			mTextView01.setText(Html.fromHtml(show_str));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mTextView01.setText("未找到此文件！");
		}
	}
	private Button.OnClickListener Listener = new Button.OnClickListener() {
		public void onClick(View v){
			EditText edit_text = (EditText) findViewById(R.id.edit_text);
			CharSequence edit_text_value = edit_text.getText();
			String local_Path=String.valueOf(edit_text_value);
			show_str="";
			show_java(location+local_Path);
		}
	};
	private static void quotation(){
		show_str+="<font color = '#0000E3'>";
		show_str+=tempchars[i];
		i++;
		while(i<tempchars.length){
			if(tempchars[i]=='"'){
				show_str+=tempchars[i]+"</font>";
				break;
			}
			judgechar(tempchars[i++]);
		}
	}
	private static void single_quotation(){
		show_str+="<font color = '#FF0000'>";
		show_str+=(tempchars[i]);
		i++;
		while(i<tempchars.length){
			if(tempchars[i]=='\''){
				show_str+=(tempchars[i]+"</font>");
				break;
			}
			judgechar(tempchars[i++]);
		}
	}
	private static void annotation(){
		if(tempchars[i]=='/'){
			show_str+=("<font color = '#006000'>"+tempchars[i-1]+tempchars[i++]);
			while(true){
				if(tempchars[i]=='\r'){
					show_str+=("</font><br>");
					break;
				}
				/*else if((tempchars[i]=='*'&&tempchars[i+1]=='/')){
					show_str+='*';
					show_str+='/';
					show_str+=("</font><br>");
					i+=2;
					break;
				}*/
				judgechar(tempchars[i++]);
			}
		}
		else if(tempchars[i]=='*'){
			show_str+=("<font color = '#006000'>"+tempchars[i-1]+tempchars[i++]);
			while(true){
				if((tempchars[i]=='*'&&tempchars[i+1]=='/')){
					show_str+='*';
					show_str+='/';
					show_str+=("</font><br>");
					i+=2;
					break;
				}
				judgechar(tempchars[i++]);
			}
		}
	}
	private static void number(){
		if(!MainActivity.is_char(tempchars[i-1])){
			show_str+=("<font color = '#930093'>");
			while(MainActivity.is_num(tempchars[i])){
				judgechar(tempchars[i++]);
			}
			show_str+=("</font>"+tempchars[i]);
		}
		else{
			show_str+=(tempchars[i]);
		}
	}
	private static void word(){
		int i_start = i,count= 0;
		boolean flag = false;
		while(MainActivity.is_char(tempchars[i])){
			count++;
			i++;
		}
		String str_temp = String.valueOf(tempchars,i_start,count);
		for(String str : java_word){
			if(str_temp.equals(str)){
				show_str+=("<font color = '#800000'>"+str+"</font>");
				flag = true;
				break;
			}
		}
		if(flag==false){
			for(int j=i_start;j<i_start+count;j++)
				show_str+=(tempchars[j]);
		}
	}
	private static boolean is_char(char ch){
		return (ch>='A'&&ch<='Z')||(ch>='a'&&ch<='z');
	}
	private static boolean is_num(char ch){
		return (ch>='0'&&ch<='9');
	}
	private static void judgechar(char ch){
		if(ch=='\r'){
			show_str+="<br>";
			i++;
		}
		else if(ch==' '){
			show_str+="&nbsp;";
		}
		else if(ch=='\t'){
			show_str+="&nbsp;&nbsp;&nbsp;&nbsp;";
		}
		else
			show_str+=(ch);
	}
}
