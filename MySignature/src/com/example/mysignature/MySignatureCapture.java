package com.example.mysignature;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class MySignatureCapture extends Activity {
	//Objects
	Spinner spinner;
	LinearLayout background, myLayout;
	HW_Recognition myHandWriting;
	Button b_save, b_clear, b_back, b_analyze;	//save, clear, cancel buttons
	Button b_black, b_blue, b_red;
	TextView tv, tv1;
	ListView lv;
	private ArrayAdapter<String> listAdapter;
	public static String t_directory;
	private Bitmap mybmp;
	private boolean flag = true;
	private int parse_select, initial=1;
	
	public String hw_fileName;
	File hw_png_file;
	
	public native int[] stringFromJNI(double[] array);
    static {
        System.loadLibrary("hello-jni");
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);		//set this User Interface as a smaller UI
		setContentView(R.layout.activity_my_signature_capture);
		background = (LinearLayout)findViewById(R.id.background);
		background.setBackgroundColor(0xFFFFD700);
		//ex_dir = 
		t_directory  = Environment.getExternalStorageDirectory().toString();
		File directory = new File(t_directory+"/myWritingBmp");
		if(!directory.exists())
			directory.mkdir();
		
		hw_fileName = getDateAndTime()+ ".png";
		hw_png_file = new File(directory, hw_fileName);
		
		myLayout = (LinearLayout)findViewById(R.id.mylayout);
		myHandWriting = new HW_Recognition(this, null);
		myHandWriting.setBackgroundColor(Color.WHITE);
		myLayout.addView(myHandWriting, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		b_clear = (Button)findViewById(R.id.b_clear);
		b_save  = (Button)findViewById(R.id.b_save);
		b_save.setEnabled(false);		
		b_back = (Button)findViewById(R.id.b_back);
		b_analyze = (Button)findViewById(R.id.b_analyze);
		b_analyze.setBackgroundColor(0xFF33B5E5);
		
		b_black = (Button)findViewById(R.id.b_black);
		b_black.setBackgroundColor(Color.BLACK);
		b_blue  = (Button)findViewById(R.id.b_blue);
		b_blue.setBackgroundColor(Color.BLUE);
		b_red   = (Button)findViewById(R.id.b_red);
		b_red.setBackgroundColor(Color.RED);
		
		spinner = (Spinner)findViewById(R.id.spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.number_arrays, R.layout.simplerow);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos,  long id) {
				if(pos == 0){
					Log.v("Item",parent.getItemAtPosition(pos).toString());
				}else if(pos == 1){
					Log.v("Item",parent.getItemAtPosition(pos).toString());
				}else if(pos == 2){
					Log.v("Item",parent.getItemAtPosition(pos).toString());
				}else if(pos == 3){
					Log.v("Item",parent.getItemAtPosition(pos).toString());
				}else if(pos == 4){
					Log.v("Item",parent.getItemAtPosition(pos).toString());
				}else if(pos == 5){
					Log.v("Item",parent.getItemAtPosition(pos).toString());
				}else if(pos == 6){
					Log.v("Item",parent.getItemAtPosition(pos).toString());
				}else if(pos == 7){
					Log.v("Item",parent.getItemAtPosition(pos).toString());
				}else if(pos == 8){
					Log.v("Item",parent.getItemAtPosition(pos).toString());
				}else if(pos == 9){
					Log.v("Item",parent.getItemAtPosition(pos).toString());
				}
				if(initial != 1){
					flag = false;
					parse_select = Integer.parseInt(parent.getItemAtPosition(pos).toString());
					b_analyze.setText("Correct!");
				}
				initial++;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		tv = (TextView)findViewById(R.id.tv);
		tv.setTextColor(Color.RED);
		tv.setVisibility(View.GONE);
		tv.setTextSize(70f);
		
		tv1 = (TextView)findViewById(R.id.tv1);
		tv1.setTextColor(0xFF006400);
		tv1.setVisibility(View.GONE);
		tv1.setTextSize(25f);
		
		//Clear Button
		b_clear.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				myHandWriting.clear();
				tv.setVisibility(View.GONE);
				tv1.setVisibility(View.GONE);
				b_save.setEnabled(false);
			}
		});
		
		//Save Button
		b_save.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				myHandWriting.save_to_png(myLayout);
			}
		});
		
		//Back Button
		b_back.setOnClickListener(new OnClickListener(){
			public void onClick(View v){				
				Bundle b = new Bundle();
				b.putString("status", "back");
				Intent intent = new Intent();
				intent.putExtras(b);
				setResult(RESULT_OK, intent);
				finish();	//Exit this activity and return to previous one
			}
		});
		
		//Analyze Button
		b_analyze.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				if(flag){
					myHandWriting.black_white_conversion(myLayout);
				}else{
					flag = true;
					Toast.makeText(getApplicationContext(), parse_select+" is stored for traing!", Toast.LENGTH_LONG).show();
					tv.setText(""+parse_select);
					b_analyze.setText("Analyze");
				}
			}
		});
		
		//Black Button
		b_black.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				myHandWriting.set_paint_color(Color.BLACK);
			}
		});
		
		//Blue Button
		b_blue.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				myHandWriting.set_paint_color(Color.BLUE);
			}
		});
		//
		b_red.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				myHandWriting.set_paint_color(Color.RED);
			}
		});
	}
	
	public boolean isExternalStorageWritable(){
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state))
			return true;
		return false;
	}
	private String getDateAndTime(){
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day_of_month = c.get(Calendar.DAY_OF_MONTH);
		int hour_of_day = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		return year + "_" + (month+1) + "_" + day_of_month + "_" + hour_of_day + "_" + minute;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_signature_capture, menu);
		return true;
	}
	
	public class HW_Recognition extends View{
		
		private static final float STROKE_WIDTH = 70f;
		private static final float HALF_STROKE_WIDTH = STROKE_WIDTH/2;
		private int DIVISION_NUM;
		
		private Paint paint = new Paint();	//describe the colors, styles, etc...
		private Path path = new Path();
		
		private float last_x;
		private float last_y;
		private final RectF dirtyRect = new RectF();
		
		//constructor
		public HW_Recognition(Context context, AttributeSet attrs){
			super(context, attrs);
			paint.setAntiAlias(true);		//Try changing to false and see what happens WOHAHA
			paint.setColor(Color.BLACK);	//Blue paint
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeWidth(STROKE_WIDTH);
			DIVISION_NUM = Color.BLACK;
		}
		
		public void set_paint_color(int color){
			paint.setColor(color);
			DIVISION_NUM = color;
		}
		
		public void save_to_png(View view){
			Log.v("TAG", "View Width:" + view.getWidth());
			Log.v("TAG", "View Height:" + view.getHeight());
			if(!isExternalStorageWritable()){
				Toast.makeText(getApplicationContext(), "File System is not Mounted", Toast.LENGTH_LONG).show();
			}
			create_bmp(view);

			try{
				FileOutputStream myFStreamOut = new FileOutputStream(hw_png_file);
				Toast.makeText(getApplicationContext(), "Stored at :"+hw_png_file.getAbsolutePath(), Toast.LENGTH_LONG).show();
				
				mybmp.compress(Bitmap.CompressFormat.PNG, 100, myFStreamOut);
				myFStreamOut.flush();
				myFStreamOut.close();
				String url = Images.Media.insertImage(getContentResolver(), mybmp, "title", null);
				Log.v("TAG", url);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		private void create_bmp(View view){
			mybmp = null;
			mybmp = Bitmap.createBitmap(myLayout.getWidth(), myLayout.getHeight(), Bitmap.Config.RGB_565);	
			
			Canvas canvas = new Canvas(mybmp);
			try{
				view.draw(canvas);
			}catch(Exception e){
				Log.v("Array","Error occurred drawing canvas");
				e.printStackTrace();
			}
		}
		public void black_white_conversion(View view){
			create_bmp(view);
			Bitmap scaled_bmp = Bitmap.createScaledBitmap(mybmp, 28, 28, true);
			int white = 0;
			double []array = new double[785];
			for(int x=0; x<28 ;x++){
				for(int y=0 ; y<28 ;y++){
					int value = scaled_bmp.getPixel(x, y);
					double distance = Math.abs(value);
//					int distance_black = Math.abs(value-DIVISION_NUM);
					if(distance == 1){
						array[x*28+y] = distance/Math.abs(DIVISION_NUM);
						white++;
					}else{
						array[x*28+y] = distance/Math.abs(DIVISION_NUM);
					}
//					double temp;
//					if(value == -1){
//						array[x*28+y] = 0.0;
//						temp = 0.0;
//						white++;
//					}else{
//						temp = (double)distance/(Math.abs(DIVISION_NUM));
//						array[x*28+y] = temp;
//					}
//					Log.v("Array", "array[" + (x*28+y) + "] = " + temp);
//					int distance = Math.abs(value);
//					double temp;
//					if(value == -1){
//						array[x*28+y] = 0.0;
//						temp = 0.0;
//						white++;
//					}else{
//						temp = (double)distance/(Math.abs(DIVISION_NUM));
//						array[x*28+y] = temp;
//					}
//					Log.v("Array", "array[" + (x*28+y) + "] = " + temp);
				}
			}
			Log.v("Array","white :"+white);
			array[784] = 1.0;//biased
			
			int []rev = stringFromJNI(array);
			Log.v("Array","rev[0]= "+rev[0]);
			Log.v("Array","rev[1]= "+rev[1]);
			Log.v("Array","rev[2]= "+rev[2]);
			
			if(white != 784){
				tv.setVisibility(View.VISIBLE);
				tv.setText(""+rev[0]);
				
				tv1.setVisibility(View.VISIBLE);
				tv1.setText("p:"+rev[1]+","+rev[2]);
			}
			//scaled_bmp
		}
		
		//onDraw() is called when invalidate() is called on the view
		@Override
		protected void onDraw(Canvas canvas){
			canvas.drawPath(path, paint);
		}
		
		public void clear(){
			path.reset();
			invalidate();
			mybmp = null;
		}
		
		@Override
		public boolean onTouchEvent(MotionEvent event){
			float event_x = event.getX();
			float event_y = event.getY();
			b_save.setEnabled(true); 	//enable the save button
			
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:	//press gesture has started
				path.moveTo(event_x, event_y);
				last_x = event_x;
				last_y = event_y;
				return true;
			case MotionEvent.ACTION_MOVE:	
				
			case MotionEvent.ACTION_UP:		//press gesture has ended
				resetDirtyRect(event_x, event_y);
				int historySize = event.getHistorySize();
				for(int i=0 ;i < historySize ; i++){
					float historicalX = event.getHistoricalX(i);
					float historicalY = event.getHistoricalY(i);
					expandDirtyRect(historicalX, historicalY);
					path.lineTo(historicalX, historicalY);
				}
				path.lineTo(event_x, event_y);
				break;
			
			default:
				return false;
			}
			
			invalidate( (int)(dirtyRect.left - HALF_STROKE_WIDTH),
					    (int)(dirtyRect.top - HALF_STROKE_WIDTH),
					    (int)(dirtyRect.right + HALF_STROKE_WIDTH),
					    (int)(dirtyRect.bottom + HALF_STROKE_WIDTH));	//adding some padding to the boundaries
			
			last_x = event_x;
			last_y = event_y;
			return true;
		}
		
		private void resetDirtyRect(float eventX, float eventY){
			dirtyRect.left   = Math.min(last_x, eventX);
			dirtyRect.right  = Math.max(last_x, eventX);
			dirtyRect.top    = Math.min(last_y, eventY);
			dirtyRect.bottom = Math.max(last_y, eventY);
		}
		
		private void expandDirtyRect(float historicalX, float historicalY){
			if(historicalX < dirtyRect.left){
				dirtyRect.left = historicalX;
			}else if(historicalX > dirtyRect.right){
				dirtyRect.right = historicalY;
			}
			
			if(historicalY < dirtyRect.top){
				dirtyRect.top = historicalY;
			}else if(historicalY > dirtyRect.bottom){
				dirtyRect.bottom = historicalY;
			}
		}
	}
}
