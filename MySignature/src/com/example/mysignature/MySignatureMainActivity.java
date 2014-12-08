package com.example.mysignature;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MySignatureMainActivity extends Activity {
		
	public static final int SIG_MAIN = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_signature_main);
		
		Button startDrawing = (Button)findViewById(R.id.startDraw);
		startDrawing.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MySignatureMainActivity.this, MySignatureCapture.class);
				startActivityForResult(intent, SIG_MAIN);
			}
		});	
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == SIG_MAIN){
			if(resultCode == RESULT_OK){
				Bundle bundle = data.getExtras();
				String status = bundle.getString("status");
				if(status.equals("done")){
//					Toast.makeText(this, "Captured hand writing!", Toast.LENGTH_SHORT).show();
				}
				if(status.equals("back")){
				}
			}
		}
	}
}
