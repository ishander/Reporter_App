package com.rajasthnapatrika_prod.pick;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.activities.AppController_Patrika;
import com.rajasthnapatrika_prod.fragments.Detailed_Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class CustomGalleryActivity extends Activity {

	ListView gridGallery;
	Handler handler;
	GalleryAdapter adapter;

	Button btnGalleryOk;

	String action;
//	private ImageLoader imageLoader;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gallery);

		action = getIntent().getAction();
		if (action == null) {
			finish();
		}
		init();
	}

	private void init() {

		handler = new Handler();
		gridGallery = (ListView) findViewById(R.id.gridGallery);
		adapter = new GalleryAdapter(getApplicationContext());
		if (action.equalsIgnoreCase(AppController_Patrika.ACTION_MULTIPLE_PICK_IMAGES)) {

			findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
			gridGallery.setOnItemClickListener(mItemMulClickListener);
			adapter.checkImageOR_Video(false);

		} else if (action.equalsIgnoreCase(AppController_Patrika.ACTION_MULTIPLE_PICK_VIDEOS)) {

			findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
			gridGallery.setOnItemClickListener(mItemMulClickListener);
			adapter.checkImageOR_Video(true);
		}


		gridGallery.setAdapter(adapter);

		btnGalleryOk = (Button) findViewById(R.id.btnGalleryOk);
		btnGalleryOk.setOnClickListener(mOkClickListener);

		new Thread() {

			@Override
			public void run() {
				Looper.prepare();
				handler.post(new Runnable() {

					@Override
					public void run() {
						adapter.addAll(getGalleryPhotos());
//						checkImageStatus();
					}
				});
				Looper.loop();
			};

		}.start();

	}

	View.OnClickListener mOkClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
		call_intent();
		}
	};
	AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
//                adapter.changeSelection(, position);

		}
	};

	void call_intent()
	{
		ArrayList<CustomGallery> selected = adapter.getSelected();

		String[] allPath = new String[selected.size()];
		for (int i = 0; i < allPath.length; i++) {
			allPath[i] = selected.get(i).sdcardPath;
		}

		Intent data = new Intent().putExtra("all_path", allPath);
		setResult(RESULT_OK, data);
		finish();
	}

	@Override
	public void onBackPressed() {
		call_intent();
		super.onBackPressed();
	}

	private ArrayList<CustomGallery> getGalleryPhotos() {
		ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();

		try {

			Cursor imagecursor = null;
          if(action.equals(AppController_Patrika.ACTION_MULTIPLE_PICK_IMAGES)) {
			  String[] columns = { MediaStore.Images.Media.DATA,MediaStore.Images.Media._ID };
			   String orderBy = MediaStore.Images.Media._ID;
			  imagecursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
		  }else if(action.equals(AppController_Patrika.ACTION_MULTIPLE_PICK_VIDEOS)){
			  String[] columns = { MediaStore.Video.Media.DATA,MediaStore.Video.Media._ID };
			   String orderBy = MediaStore.Video.Media._ID;

			  String[] videoProjection = { MediaStore.Video.Media._ID,MediaStore.Video.Media.DATA,
					  MediaStore.Video.Media.DISPLAY_NAME,MediaStore.Video.Media.SIZE };
			  imagecursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,videoProjection, null, null, null);
		  }
			if (imagecursor != null && imagecursor.getCount() > 0) {

				while (imagecursor.moveToNext()) {
					CustomGallery item = new CustomGallery();

					int dataColumnIndex = 0;
					if(action.equals(AppController_Patrika.ACTION_MULTIPLE_PICK_IMAGES))
						dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
					else if(action.equals(AppController_Patrika.ACTION_MULTIPLE_PICK_VIDEOS))
						dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Video.Media.DATA);

					item.sdcardPath = imagecursor.getString(dataColumnIndex);
					File file = new File(item.sdcardPath);

					if(file.exists()) {
						if (action.equals(AppController_Patrika.ACTION_MULTIPLE_PICK_IMAGES)) {
							for (int i = 0; i < Detailed_Fragment.Image_list.size(); i++) {
								if (file.getName().equals(new File(Detailed_Fragment.Image_list.get(i).get("Path")).getName())) {
									item.isSeleted = true;
									break;
								} else
									item.isSeleted = false;
							}
						} else if (action.equals(AppController_Patrika.ACTION_MULTIPLE_PICK_VIDEOS)) {
							for (int i = 0; i < Detailed_Fragment.Video_list.size(); i++) {
								if (file.getName().equals(new File(Detailed_Fragment.Video_list.get(i).get("Path")).getName())) {
									item.isSeleted = true;
									break;
								} else
									item.isSeleted = false;
							}
						}
						galleryList.add(item);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// show newest photo at beginning of the list
		Collections.reverse(galleryList);
		return galleryList;
	}

}
