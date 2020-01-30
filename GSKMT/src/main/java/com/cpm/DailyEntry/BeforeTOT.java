package com.cpm.DailyEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;


import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.delegates.TOTBean;
import com.cpm.message.AlertMessage;
import com.example.gsk_mtt.R;

public class BeforeTOT extends Activity{
	
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor = null;static int mposition = -1;
	
	public static String store_id, category_id, process_id, date, intime, username, app_version, imgDate, cat_id;
	public static String img1 = "", img2="", img3="";
	public  String _path="", reason_id="0", remark="";;
	private static String str;
	int row_pos;

	protected static String _pathforcheck = "";
	protected static String _pathforcheck2 = "";
	protected static String _pathforcheck3= "";
	Button save_btn;
	ListView lv;
	GSKMTDatabase db;
	private CustomKeyboardView mKeyboardView;
	private Keyboard mKeyboard;
	public static ArrayList<TOTBean> data = new ArrayList<TOTBean>();
	ArrayList<CoverageBean> coveragelist = new ArrayList<CoverageBean>();
	static int currentVersion=1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.before_tot_main);
		save_btn = (Button)findViewById(R.id.save);
		lv = (ListView)findViewById(R.id.list);
		
		
		
		preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
		category_id = preferences.getString(CommonString.KEY_CATEGORY_ID, null);
		process_id = preferences.getString(CommonString.KEY_PROCESS_ID, null);
		date = preferences.getString(CommonString.KEY_DATE, null);
		intime = preferences.getString(CommonString.KEY_IN_TIME, null);
		username = preferences.getString(CommonString.KEY_USERNAME, null);
		app_version = preferences.getString(CommonString.KEY_VERSION, null);
		
		/*currentVersion = android.os.Build.VERSION.SDK_INT;
		
		
		mKeyboard = new Keyboard(this, R.xml.keyboard);
		
		mKeyboardView = (CustomKeyboardView) findViewById(R.id.keyboard_view);
		mKeyboardView.setKeyboard(mKeyboard);
		mKeyboardView
				.setOnKeyboardActionListener(new BasicOnKeyboardActionListener(
						this));*/
		
		db = new GSKMTDatabase(BeforeTOT.this);
		db.open();
		

		  if((new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/")).exists()){
            Log.i("directory is created", "directory is created");
     }else{
            (new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/")).mkdir();
     }
		
		  
		  str = Environment.getExternalStorageDirectory() + "/MT_GSK_Images/";
		
		imgDate = date.replace("/", "-");
		data = db.getTOTData(store_id, process_id, category_id);
		
		if (data.size()>0) {
			
			lv.setAdapter(new MyAdaptor(this));
			
			System.out.println(""+data.size());
		} else {
			
		}
		
		coveragelist = db.getCoverageData(date, store_id, process_id);

		
		save_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (!check_condition()) {
					Toast.makeText(getApplicationContext(),
							AlertMessage.MESSAGE_INVALID_DATA, Toast.LENGTH_SHORT)
							.show();
				} else {
					
					if (!check_conditionForImages()) {
						Toast.makeText(getApplicationContext(),
								AlertMessage.MESSAGE_IMAGE, Toast.LENGTH_SHORT)
								.show();
					} else {

					

					AlertDialog.Builder builder = new AlertDialog.Builder(
							BeforeTOT.this);
					builder.setMessage("Do you want to save the data ")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int id) {
																		 
											db.InsertBeforeTOTData(store_id, data, category_id, process_id);

												Intent i = new Intent(
														getApplicationContext(),
														DailyEntryMainMenu.class);
												startActivity(i);
												BeforeTOT.this.finish();



										}
									})
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int id) {
											dialog.cancel();
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
					}
				}
				
				
			}
		});
	
		
	}
	
	
	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		
		
		/* if (mKeyboardView.getVisibility() == View.VISIBLE){
			mKeyboardView.setVisibility(View.INVISIBLE);
		 }
			else{*/

		AlertDialog.Builder builder = new AlertDialog.Builder( this);
        builder.setMessage("Are you sure you want to quit ?") .setCancelable(false).setPositiveButton(  "Yes",
        new DialogInterface.OnClickListener() {
          public void onClick(  DialogInterface dialog, int id) {
					
					
					Intent in = new Intent(BeforeTOT.this, DailyEntryMainMenu.class);
					startActivity(in);
					BeforeTOT.this.finish();


                }
                 }).setNegativeButton( "No",
                 new DialogInterface.OnClickListener() {public void onClick( DialogInterface dialog, int id) {
                                          dialog.cancel();
                                              }
                                                });
                AlertDialog alert = builder.create();

                   alert.show();
//			}
	}
	
	 static class ViewHolder{
		 TextView brand_name, display_name, target_quantity;
		 ImageView image1, image2 , image3;
		EditText actual_quanity, stock_count;
		Button refimage;
		
	}
	 
	 
	 public String getCurrentTime() {

         Calendar m_cal = Calendar.getInstance();

         String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":"
                                         + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);

         return intime;

}
	 
		public boolean check_condition() {
			boolean result = true;

			for (int i = 0; i < data.size(); i++) {

				if (!data.get(i).getBEFORE_QTY().equalsIgnoreCase("")) {
					
					if (Integer.parseInt(data.get(i).getBEFORE_QTY()) > (Integer
							.parseInt(data.get(i).getTrg_quantity())) ){
						
						result = false;
						break;
						
					} else {
						result = true;
					}

					

				} else {
					result = false;
					break;
				}
			}

			return result;

		}
		
		
		public boolean check_conditionForImages() {
			boolean result = true;

			for (int i = 0; i < data.size(); i++) {
				
				if (data.get(i).getBEFORE_QTY().equalsIgnoreCase("0")) {
					
					if (data.get(i).getImage1().equalsIgnoreCase("") && data.get(i).getImage2().equalsIgnoreCase("")
							&& data.get(i).getImage3().equalsIgnoreCase("")) {

						result = true;

					}
				}

				 else if (!data.get(i).getBEFORE_QTY().equalsIgnoreCase("0")){
					 
					 if (data.get(i).getImage1().equalsIgnoreCase("") || data.get(i).getImage2().equalsIgnoreCase("")
								|| data.get(i).getImage3().equalsIgnoreCase("")) {

							result = false;
							break;

						}
				}
			}

			return result;

		}
	
	private class MyAdaptor extends BaseAdapter{
		
		LayoutInflater mInflater;
		private Context mcontext;
		
		
		public MyAdaptor(Context context) {
			
			mInflater = LayoutInflater.from(context);
			mcontext = context;
			
		}
		
		@Override
		public int getCount() {
			
			return data.size() ;
		}

		@Override
		public Object getItem(int position) {
			
			return position;
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
		
			
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.before_tot,
						null);
				holder = new ViewHolder();
				holder.brand_name = (TextView) convertView
						.findViewById(R.id.brand_name);
				
				holder.display_name = (TextView) convertView
						.findViewById(R.id.display_name);

				holder.target_quantity = (TextView) convertView.findViewById(R.id.trgt_quantity);

				holder.actual_quanity = (EditText) convertView
						.findViewById(R.id.actual_quantity);
				
				
				/*holder.stock_count = (EditText) convertView
						.findViewById(R.id.stock_count);*/
				
				holder.refimage = (Button) convertView
						.findViewById(R.id.refimage);
				
				holder.image1 = (ImageView) convertView.findViewById(R.id.cam1);
				holder.image2 = (ImageView) convertView.findViewById(R.id.cam2);
				holder.image3 = (ImageView) convertView.findViewById(R.id.cam3);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			
			 
 /*       	if ( currentVersion >= 11) {
                holder.stock_count.setTextIsSelectable(true);
                holder.stock_count.setRawInputType(InputType.TYPE_CLASS_TEXT);
                
                
                holder.actual_quanity.setTextIsSelectable(true);
                holder.actual_quanity.setRawInputType(InputType.TYPE_CLASS_TEXT);
                                
                
          } else {
                holder.stock_count.setInputType(0);
                holder.actual_quanity.setInputType(0);
                
               
                
          }*/
			
			
	/*		if (coveragelist.get(0).getImage_allow().equalsIgnoreCase("NOT Allowed")) {
				
				holder.image1.setEnabled(false);
				holder.image2.setEnabled(false);
				holder.image3.setEnabled(false);
				
			} else {
				
				holder.image1.setEnabled(true);
				holder.image2.setEnabled(true);
				holder.image3.setEnabled(true);
			}*/
			
			if (data.size() == 1 || (data.size()-1 == position)) {

			holder.actual_quanity.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					
					data.get(position).setBEFORE_QTY(s.toString().replaceAll("[&^<>{}'$]", ""));
					
					lv.invalidateViews();
					
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					
					
				}
				
				
			});
			
			} else {

			
		holder.actual_quanity.setOnFocusChangeListener(new OnFocusChangeListener() {
				public void onFocusChange(View v, boolean hasFocus) {
					
//					showKeyboardWithAnimation();

					if (!hasFocus) {
						final int position = v.getId();
						final EditText Caption = (EditText) v;
						String value1 = Caption.getText().toString();
						
						if (value1.equals("")) {

							data.get(position).setBEFORE_QTY("");
							lv.invalidateViews();

						} else {

							data.get(position).setBEFORE_QTY(value1);
							lv.invalidateViews();
						
						}

					}
				}
			});
			
			
	
			}
			
	
			
			
	/*		holder.stock_count.setOnFocusChangeListener(new OnFocusChangeListener() {
				public void onFocusChange(View v, boolean hasFocus) {
//					showKeyboardWithAnimation();
					
					if (!hasFocus) {
						final int position = v.getId();
						final EditText Caption = (EditText) v;
						String value1 = Caption.getText().toString();
						
						if (value1.equals("")) {

							data.get(position).setStock_count("");
							list_view_for_stock.invalidateViews();

						} else {

							data.get(position).setStock_count(value1);
							list_view_for_stock.invalidateViews();
						
						}

					}
				}
			});*/
			
			
			holder.refimage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					final Dialog dialog = new Dialog(BeforeTOT.this);
					dialog.setContentView(R.layout.popup);
					ImageView refimage = (ImageView)dialog.findViewById(R.id.displayimage);
					dialog.setTitle("Reference Image");
					
					if (data.get(position).getImage_url().equalsIgnoreCase("Chem1st.jpg")) {
						refimage.setBackgroundResource(R.drawable.chemfirst);
					} 					
					 else if (data.get(position).getImage_url().equalsIgnoreCase("DumpBIn.jpg")){
						 
							refimage.setBackgroundResource(R.drawable.dumpbin);
						}
					
					 else if (data.get(position).getImage_url().equalsIgnoreCase("FloorStickers.jpg")){
							refimage.setBackgroundResource(R.drawable.floorstickers);
						}
					
					 else if (data.get(position).getImage_url().equalsIgnoreCase("Onewayvision.jpg")){
							refimage.setBackgroundResource(R.drawable.onewayvision);
						}
					
					 else if (data.get(position).getImage_url().equalsIgnoreCase("DropDown.jpg")){
							refimage.setBackgroundResource(R.drawable.dropdown);                                                                                                                                           
						}

					 else if (data.get(position).getImage_url().equalsIgnoreCase("Skirting.jpg")){
							refimage.setBackgroundResource(R.drawable.skirting);                                                                                                                                           
						}
					
					 else if (data.get(position).getImage_url().equalsIgnoreCase("FloorStack.jpg")){
							refimage.setBackgroundResource(R.drawable.floorstack);                                                                                                                                           
						}
					
					 else if (data.get(position).getImage_url().equalsIgnoreCase("Endcap.jpg")){
							refimage.setBackgroundResource(R.drawable.endcap);                                                                                                                                           
						}
					
					 else if (data.get(position).getImage_url().equalsIgnoreCase("FSU.jpg")){
							refimage.setBackgroundResource(R.drawable.fsu);                                                                                                                                           
						}
					
					 else if (data.get(position).getImage_url().equalsIgnoreCase("PillarBranding.jpg")){
							refimage.setBackgroundResource(R.drawable.pillarbranding);                                                                                                                                           
						}
					
					 else if (data.get(position).getImage_url().equalsIgnoreCase("Parasite.jpg")){
							refimage.setBackgroundResource(R.drawable.parasite);                                                                                                                                           
						}
					 else if (data.get(position).getImage_url().equalsIgnoreCase("catman.jpg")){
							refimage.setBackgroundResource(R.drawable.catman);                                                                                                                                           
						}
					
					 else if (data.get(position).getImage_url().equalsIgnoreCase("ClipStrips.jpg")){
							refimage.setBackgroundResource(R.drawable.clipstrips);                                                                                                                                           
						}
					dialog.show();
					
					
				}
			});
			
			
			
			
			if(data.get(position).getBEFORE_QTY().equalsIgnoreCase("")){
				holder.actual_quanity.setText("");
				
			}else{
				holder.actual_quanity.setText(data.get(position).getBEFORE_QTY());
			}

			/*if(data.get(position).getStock_count().equalsIgnoreCase("")){
				holder.stock_count.setText("");
				
			}else{
				holder.stock_count.setText(data.get(position).getStock_count());
			}
			*/
			if (data.get(position).getBEFORE_QTY().equalsIgnoreCase("0")) {
				
//				data.get(position).setStock_count("0");
//				holder.stock_count.setText("0");
//				holder.stock_count.setEnabled(false);
				holder.image1.setImageResource(R.drawable.camera_disabled);
				holder.image2.setImageResource(R.drawable.camera_disabled);
				holder.image3.setImageResource(R.drawable.camera_disabled);
				
				data.get(position).setImage1("");
				data.get(position).setImage2("");
				data.get(position).setImage3("");
				
				
				data.get(position).setCamera1("NA");
				data.get(position).setCamera2("NA");
				data.get(position).setCamera3("NA");
				
				
				
				
				holder.image1.setEnabled(false);
				holder.image2.setEnabled(false);
				holder.image3.setEnabled(false);
				
			}else if (data.get(position).getBEFORE_QTY().equalsIgnoreCase("") || ! data.get(position).getBEFORE_QTY().equalsIgnoreCase("0")){
				
				holder.image1.setImageResource(R.drawable.camera_ico);
				holder.image2.setImageResource(R.drawable.camera_ico);
				holder.image3.setImageResource(R.drawable.camera_ico);
				
				holder.image1.setEnabled(true);
				holder.image2.setEnabled(true);
				holder.image3.setEnabled(true);
				
				/*data.get(position).setCamera1("NO");
				data.get(position).setCamera2("NO");
				data.get(position).setCamera3("NO");
				
				holder.image1.setBackgroundResource(R.drawable.camera_ico);
				holder.image2.setBackgroundResource(R.drawable.camera_ico);
				holder.image3.setBackgroundResource(R.drawable.camera_ico);*/
//				holder.stock_count.setEnabled(true);
				
				
				
				
			}
			
			
			holder.image1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
				/*	if (data.get(position).getBEFORE_QTY().equalsIgnoreCase("0")) {
						
						v.setEnabled(false);
						
					}else{
						v.setEnabled(true);
					}*/
					
					mposition = position;
					_pathforcheck = store_id+"_"+ process_id + username +imgDate + "left" + data.get(position).getDisplay()
							  +".jpg";
				         _path = str + _pathforcheck;
				         startCameraActivity();
					
				}
			});
			
			holder.image2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mposition = position;
					  _pathforcheck2 = store_id+"_"+ process_id +username +imgDate + "front" + data.get(position).getDisplay()
							  +".jpg";
				         _path = str + _pathforcheck2;
				         startCameraActivity();
					
				}
			});
			
						
			holder.image3.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					mposition = position;
					_pathforcheck3 = store_id+"_"+process_id +  username + imgDate + "right" + data.get(position).getDisplay()
							  +".jpg";
				         _path = str + _pathforcheck3;
				         startCameraActivity();
					
				}
			});
			
			
			
			if (!img1.equalsIgnoreCase("")) {
				if (position == mposition) {
					data.get(position).setCamera1("YES");
					data.get(position).setImage1(img1);
					img1 = "";

				}
			}
			
			
			if (!img2.equalsIgnoreCase("")) {
				if (position == mposition) {
					data.get(position).setCamera2("YES");
					data.get(position).setImage2(img2);
					img2 = "";

				}
			}
			
			if (!img3.equalsIgnoreCase("")) {
				if (position == mposition) {
					data.get(position).setCamera3("YES");
					data.get(position).setImage3(img3);
					img3 = "";

				}
			}
			
			if (data.get(position).getCamera1().equalsIgnoreCase("NO")) {
				holder.image1.setImageResource(R.drawable.camera_ico);
			} else if(data.get(position).getCamera1().equalsIgnoreCase("YES")) {
				holder.image1.setImageResource(R.drawable.camera_tick_ico);
			}else if (data.get(position).getCamera1().equalsIgnoreCase("NA")){
				
					holder.image1.setImageResource(R.drawable.camera_disabled);
				
				
				
			}
			
			
			if (data.get(position).getCamera2().equalsIgnoreCase("NO")) {
				holder.image2.setImageResource(R.drawable.camera_ico);
			} else if(data.get(position).getCamera2().equalsIgnoreCase("YES")) {
				holder.image2.setImageResource(R.drawable.camera_tick_ico);
			}else if (data.get(position).getCamera2().equalsIgnoreCase("NA")){
				holder.image2.setImageResource(R.drawable.camera_disabled);
			}
			
			
			if (data.get(position).getCamera3().equalsIgnoreCase("NO")) {
				holder.image3.setImageResource(R.drawable.camera_ico);
			} else if (data.get(position).getCamera3().equalsIgnoreCase("YES")){
				holder.image3.setImageResource(R.drawable.camera_tick_ico);
			}else if (data.get(position).getCamera3().equalsIgnoreCase("NA")) {
				holder.image3.setImageResource(R.drawable.camera_disabled);
			}
			
			if (position == 0) {
				holder.brand_name.setText(data.get(position).getBrand());
				holder.display_name.setText(data.get(position).getDisplay()+"("+data.get(position).getType()+")");
				holder.target_quantity.setText(data.get(position).getTrg_quantity());

			} else{
				holder.brand_name.setText(data.get(position).getBrand());
				holder.display_name.setText(data.get(position).getDisplay()+"("+data.get(position).getType()+")");
				holder.target_quantity.setText(data.get(position).getTrg_quantity());
				
			}
			
			

			holder.actual_quanity.setId(position);
//			holder.stock_count.setId(position);
			holder.image1.setId(position);
			holder.image2.setId(position);
			holder.image3.setId(position);
			
			return convertView;
		}
		
	}
	
	
	private void showpopup()
    {
       LayoutInflater inflater = this.getLayoutInflater();
       View mView= inflater.inflate(R.layout.thumbnail,(ViewGroup)findViewById(R.id.image));
       PopupWindow mPopupWindow = new PopupWindow(mView,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT, false);
       mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

       WebView TV=(WebView)this.findViewById(R.id.image);          
      // TableLayout L1 = (TableLayout)findViewById(R.id.tblntarialview);

       mPopupWindow.showAtLocation(TV, Gravity.CENTER, 45, 0);

     }
	
	
	   private final TextWatcher passwordWatcher = new TextWatcher() {
	        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	        }

	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	           
	        }

	        public void afterTextChanged(Editable s) {
//	            if (s.length() == 0) {
//	                textView.setVisibility(View.GONE);
//	            } else{
//	                textView.setText("You have entered : " + passwordEditText.getText());
//	            }
	        }

			
	    };
	
	private  void showKeyboardWithAnimation() {
		
		if (mKeyboardView.getVisibility() == View.GONE) {
			Animation animation = AnimationUtils
					.loadAnimation(BeforeTOT.this,
							R.anim.slide_in_bottom);
			mKeyboardView.showWithAnimation(animation);
		}else if (mKeyboardView.getVisibility() == View.INVISIBLE){
			mKeyboardView.setVisibility(View.VISIBLE);
		}
	}
	
	protected void startCameraActivity() {

		try {
			Log.i("MakeMachine", "startCameraActivity()");
			File file = new File(_path);
			Uri outputFileUri = Uri.fromFile(file);

			Intent intent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

			startActivityForResult(intent, 0);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("MakeMachine", "resultCode: " + resultCode);
		switch (resultCode) {
		case 0:
			Log.i("MakeMachine", "User cancelled");
			break;

		case -1:

			if (_pathforcheck != null && !_pathforcheck.equals("")) {
				if (new File(str + _pathforcheck).exists()) {

					img1 = _pathforcheck;
					lv.invalidateViews();
					_pathforcheck = "";
					break;

				}
			}
			
			
			if (_pathforcheck2 != null && !_pathforcheck2.equals("")) {
				if (new File(str + _pathforcheck2).exists()) {

					img2 = _pathforcheck2;
					lv.invalidateViews();
					_pathforcheck2 = "";
					break;

				}
			}
			
			
			if (_pathforcheck3 != null && !_pathforcheck3.equals("")) {
				if (new File(str + _pathforcheck3).exists()) {

					img3 = _pathforcheck3;
					lv.invalidateViews();
					_pathforcheck3 = "";
					break;

				}
			}

			break;
		}
	}
	
	

}
