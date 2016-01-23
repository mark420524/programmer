package com.silivall.programmer.activity;

import com.silivall.programmer.R;
import com.silivall.programmer.util.dialog.LoginLoadDialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class DialogActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_dialog);
	}

	public void buttonClick(View view) {
		onCreateDialog(view.getId());
	}
	
	protected Dialog onCreateDialog(int id) { 
	    Dialog dialog = null; 
	    switch(id) { 
	    case R.id.buttonOne: 
	    	new ProgressDialog(this).show();  
	        break; 
	    case R.id.buttonTwo: 
	    	ProgressDialog.show(this, "", getResources().getString(R.string.loginIng));  
	        break; 
	    case R.id.buttonThree: 
	    	ProgressDialog.show(this, "提示", "正在登陆中", false);  
	        break; 
	    case R.id.buttonFour: 
	    	ProgressDialog.show(this, "提示", "正在登陆中",false, true);  
	        break; 
	    case R.id.buttonFive: 
	    	ProgressDialog.show(this, "提示", "正在登陆中", true,true, cancelListener);  
	        break; 
	    case R.id.buttonSix: 
	    	LoginLoadDialog.createLoadingDialog(this, getResources().getString(R.string.loginIng)).show();
	        break; 
	    default: 
	        dialog = null; 
	        break;
	    } 
	    return dialog; 
	} 
	private OnCancelListener cancelListener = new OnCancelListener() {  
		  
	    @Override  
	    public void onCancel(DialogInterface dialog) {  
	        Toast.makeText(DialogActivity.this, "进度条被取消", Toast.LENGTH_LONG)  
	                .show();  
	    }  
	};  
	@Override 
	protected void onPrepareDialog(int id, Dialog dialog) { 
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener(){ 
            @Override 
            public void onDismiss(DialogInterface dialog) { 
                Toast.makeText(getApplicationContext(), 
                        "dismiss listener!", 
                        Toast.LENGTH_SHORT) 
                .show(); 
            } 
        }); 
    	dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				Toast.makeText(getApplicationContext(), 
                        "cancel listener!", 
                        Toast.LENGTH_SHORT) 
                .show(); 
			}
		});
    } 
}
