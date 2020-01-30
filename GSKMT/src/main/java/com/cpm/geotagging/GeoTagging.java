package com.cpm.geotagging;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.CityBean;
import com.cpm.delegates.GeotaggingBeans;
import com.cpm.delegates.StoreBean;
import com.cpm.gsk_mt.MainMenuActivity;

import com.example.gsk_mtt.R;

public class GeoTagging extends ListActivity implements OnClickListener {

	static ArrayList<StoreBean> storedetails = new ArrayList<StoreBean>();
	ArrayList<GeotaggingBeans> geotaglist = new ArrayList<GeotaggingBeans>();
	ListView l1;
	static Editor e1, e3, e11;
	private GSKMTDatabase data;

	private class EfficientAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			
			return storedetails.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();

				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.geoviewlist, null);

				holder.storename = (TextView) convertView.findViewById(R.id.geolistviewxml_storename);

				holder.imgtick = (ImageView) convertView
						.findViewById(R.id.imageView1);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.storename.setText(storedetails.get(position).getSTORE());
			// holder.text1.setText(storedetails.get(position).getAddress());

			if (storedetails.get(position).getGeotag_status().equalsIgnoreCase("U")) {
				holder.imgtick.setVisibility(View.VISIBLE);

				holder.imgtick.setBackgroundResource(R.drawable.tick_u);

			} else if (storedetails.get(position).getGeotag_status()
					.equalsIgnoreCase("Y")) {
				holder.imgtick.setVisibility(View.VISIBLE);

			} else if (storedetails.get(position).getGeotag_status()
					.equalsIgnoreCase("N")) {
				holder.imgtick.setVisibility(View.INVISIBLE);

			}

			else if (storedetails.get(position).getGeotag_status()
					.equalsIgnoreCase("P")) {
				holder.imgtick.setVisibility(View.VISIBLE);

				holder.imgtick.setBackgroundResource(R.drawable.pointer1);

			} else if (storedetails.get(position).getGeotag_status().equalsIgnoreCase("D")){
				
				holder.imgtick.setVisibility(View.VISIBLE);
				
				holder.imgtick.setBackgroundResource(R.drawable.tick_d);
				
			}
			
			else {
				holder.imgtick.setVisibility(View.INVISIBLE);
			}

			return convertView;
		}

		private class ViewHolder {
			TextView storename, storeaddress;
			ImageView imgtick;

		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.geoname);

		data = new GSKMTDatabase(this);
		data.open();

		validate_status();

		storedetails = data.getGeoStores();
		setListAdapter(new EfficientAdapter());

	}

	public void validate_status() {
		geotaglist = data.getGeotaggingStatusData();
		for (int i = 0; i < geotaglist.size(); i++) {
			data.updateGeoTagStatus(geotaglist.get(i).getStoreid(), geotaglist
					.get(i).getStatus(), geotaglist.get(i).getLatitude(),
					geotaglist.get(i).getLongitude());
		}

	}

	public void generatelistview() {
		storedetails = data.getGeoStores();
		l1 = (ListView) findViewById(R.id.list);
		setListAdapter(new EfficientAdapter());

		l1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
	}

	public void GeoTagActivity() {

		this.finish();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(GeoTagging.this, MainMenuActivity.class);

		startActivity(intent);
		GeoTagging.this.finish();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		storedetails = data.getGeoStores();
		StoreBean sb = storedetails.get(position);

		// When clicked, show a toast with the TextView text
		if (storedetails.get(position).getGeotag_status().equalsIgnoreCase("U")) {

			Toast.makeText(getBaseContext(), "Store Uploaded",
					Toast.LENGTH_LONG).show();

		} else if (storedetails.get(position).getGeotag_status().equalsIgnoreCase("D")){
			
			////??
			/*Intent intent = new Intent(GeoTagging.this, GeoTagActivity.class);
			intent.putExtra("StoreName", storedetails.get(position).getSTORE());
			intent.putExtra("Storeid", storedetails.get(position).getSTORE_ID());
			intent.putExtra("storelatitude", storedetails.get(position).getLatitude());

			intent.putExtra("storelongitude", storedetails.get(position).getLongitude());
			startActivity(intent);
			GeoTagActivity();*/
			
			Toast.makeText(getBaseContext(), "Store Geo Data Uploaded",
					Toast.LENGTH_LONG).show();
		}

		else if (storedetails.get(position).getGeotag_status().equalsIgnoreCase("Y")) {

			/*Intent intent = new Intent(GeoTagging.this, GeoTagActivity.class*//*GeoTagActivity.class*//*);
			intent.putExtra("StoreName", storedetails.get(position).getSTORE());
			intent.putExtra("Storeid", storedetails.get(position).getSTORE_ID());
			intent.putExtra("storelatitude", storedetails.get(position).getLatitude());

			intent.putExtra("storelongitude", storedetails.get(position).getLongitude());

			startActivity(intent);
			GeoTagActivity();*/

			Toast.makeText(getBaseContext(), "Store Already Geotagged",
					Toast.LENGTH_LONG).show();

		} else if (storedetails.get(position).getGeotag_status().equalsIgnoreCase("P")) {

			/*Intent intent = new Intent(GeoTagging.this, GeoTagActivity.class*//*GeoTagActivity.class*//*);
			intent.putExtra("StoreName", storedetails.get(position).getSTORE());
			intent.putExtra("Storeid", storedetails.get(position).getSTORE_ID());

			intent.putExtra("storelatitude", storedetails.get(position).getLatitude());

			intent.putExtra("storelongitude", storedetails.get(position).getLongitude());

			startActivity(intent);
			GeoTagActivity();
*/
			Toast.makeText(getBaseContext(), "Store Already Geotagged",
					Toast.LENGTH_LONG).show();



		} else {

			Intent intent = new Intent(GeoTagging.this, GeoTagActivity.class/*GeoTagActivity.class*/);
			intent.putExtra("StoreName", storedetails.get(position).getSTORE());
			intent.putExtra("Storeid", storedetails.get(position).getSTORE_ID());

			intent.putExtra("storelatitude", "0");

			intent.putExtra("storelongitude", "0");

			startActivity(intent);
			GeoTagActivity();

		}

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}
}
