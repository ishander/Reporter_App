package com.rajasthnapatrika_prod.pick;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.activities.AppController_Patrika;
import com.rajasthnapatrika_prod.fragments.Detailed_Fragment;
import com.rajasthnapatrika_prod.utils.AppUtills;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class GalleryAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<CustomGallery> data = new ArrayList<CustomGallery>();
//	ImageLoader imageLoader;
    ArrayList<Boolean> selected = new ArrayList<Boolean>();
	private boolean isImg_Video;
	public GalleryAdapter(Context c) {
		infalter = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = c;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public CustomGallery getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void checkImageOR_Video(boolean isImg_Video)
    {
		this.isImg_Video = isImg_Video;
	}


	public ArrayList<CustomGallery> getSelected() {
		ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
		Log.e("getSelected","getSelected data "+data.size());
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSeleted) {
				dataT.add(data.get(i));
			}
		}
		Log.e("getSelected","getSelected dataT "+dataT.size());
		return dataT;
	}

	public void addAll(ArrayList<CustomGallery> files) {

		try {
			this.data.clear();
			this.data.addAll(files);

		} catch (Exception e) {
			e.printStackTrace();
		}

		notifyDataSetChanged();
	}

	public void changeSelection(int position) {
	File file = new File(data.get(position).sdcardPath);
        if (data.get(position).isSeleted) {
            data.get(position).isSeleted = false;
			selected.add(false);
			if(!isImg_Video)
			{
				for (int i = 0; i < Detailed_Fragment.Image_list.size(); i++)
				{
					if (file.getName().equals(new File(Detailed_Fragment.Image_list.get(i).get("Path")).getName())) {
						Detailed_Fragment.Image_list.remove(i);
						break;
					}
				}
			}
			else if(isImg_Video)
			{
				for(int i = 0;i<Detailed_Fragment.Video_list.size();i++)
				{
					if (file.getName().equals(new File(Detailed_Fragment.Video_list.get(i).get("Path")).getName())) {
						Detailed_Fragment.Video_list.remove(i);
						break;
					}
				}
			}

        } else {
			selected.add(true);
          if(!isImg_Video) {
              if ((Detailed_Fragment.Image_list.size()) < 10) {
				  data.get(position).isSeleted = true;
				  HashMap<String, String> hm = new HashMap<>();
				  hm.put("Path", "" + data.get(position).sdcardPath);
				  hm.put("Type", "Image");
				  hm.put("Status", "queue");
				  hm.put("Status_process", "new");
				  Detailed_Fragment.Image_list.add(hm);
				  Log.e("sdcardPath click","data.get(psdcardPath  "+data.get(position).sdcardPath);
              } else
                  Toast.makeText(mContext, "10 से अधिक इमेज को साझा नहीं कर सकते", Toast.LENGTH_SHORT).show();

          }else
          if(isImg_Video) {
              if (( Detailed_Fragment.Video_list.size()) < 20) {
				  data.get(position).isSeleted = true;
				  HashMap<String, String> hm = new HashMap<>();
				  hm.put("Path", "" + data.get(position).sdcardPath);
				  hm.put("Type", "Video");
				  hm.put("Status", "queue");
				  hm.put("Status_process", "new");
				  Detailed_Fragment.Video_list.add(hm);
              } else
                  Toast.makeText(mContext, "20 से अधिक वीडियो साझा नहीं कर सकते", Toast.LENGTH_SHORT).show();
          }
        }
	}


	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;

			convertView = infalter.inflate(R.layout.gallery_item, null);
			holder = new ViewHolder();
			holder.imgQueue = (ImageView) convertView.findViewById(R.id.imgQueue);
			holder.main_linear_layout = (LinearLayout) convertView.findViewById(R.id.main_linear_layout);

			holder.imgQueueMultiSelected = (ImageView) convertView.findViewById(R.id.imgQueueMultiSelected);
			holder.txt_file_name = (TextView) convertView.findViewById(R.id.txt_file_name);

		try {
			if (!isImg_Video) {
				AppController_Patrika.getImageLoader().displayImage("file://" + data.get(position).sdcardPath,holder.imgQueue);
			}
			else if(isImg_Video)
				holder.imgQueue.setImageBitmap(AppUtills.getBitmapFromPath(data.get(position).sdcardPath));

			holder.txt_file_name.setText(new File(data.get(position).sdcardPath).getName());
			holder.imgQueueMultiSelected.setSelected(data.get(position).isSeleted);

		} catch (Exception e) {
			e.printStackTrace();
		}

		holder.main_linear_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				changeSelection(position);
				holder.imgQueueMultiSelected.setSelected(data.get(position).isSeleted);
			}
		});


		return convertView;
	}

	public class ViewHolder {
		ImageView imgQueue;
		ImageView imgQueueMultiSelected;
		LinearLayout main_linear_layout;
		TextView txt_file_name;
	}

	public void clear() {
		data.clear();
		notifyDataSetChanged();
	}
}
