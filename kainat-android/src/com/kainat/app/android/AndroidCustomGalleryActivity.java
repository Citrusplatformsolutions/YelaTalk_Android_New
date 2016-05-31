package com.kainat.app.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.ImageUtils;
import com.kainat.app.android.util.ResponseObject;

public class AndroidCustomGalleryActivity extends UIActivityManager {
	private int count;
	private Bitmap[] thumbnails;
	private int[] idThumb;
	private boolean[] thumbnailsselection;
	private String[] arrPath;
	private ImageAdapter imageAdapter;
	private List<String> mImagesPath = new ArrayList<String>();
	private int maxImageToAttached = -1 ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multi_gal);
		
		
		final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
		final String orderBy = MediaStore.Images.Media._ID;
		Cursor imagecursor = managedQuery(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, orderBy);
		int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
		this.count = imagecursor.getCount();
		this.thumbnails = new Bitmap[this.count];
		this.idThumb = new int[this.count];
		maxImageToAttached = getIntent().getIntExtra("MAX", 20) ;
		this.arrPath = new String[this.count];
		this.thumbnailsselection = new boolean[this.count];
		for (int i = 0; i < this.count; i++) {
			imagecursor.moveToPosition(i);
			int id = imagecursor.getInt(image_column_index);
			idThumb[i] = id ;
			int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
			/*thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
					getApplicationContext().getContentResolver(), id,
					MediaStore.Images.Thumbnails.MICRO_KIND, null);*/
			arrPath[i]= imagecursor.getString(dataColumnIndex);
		}
		GridView imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
		imageAdapter = new ImageAdapter();
		imagegrid.setAdapter(imageAdapter);
		imagecursor.close();
		
		final Button selectBtn = (Button) findViewById(R.id.selectBtn);
		selectBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String [] data = new String[mImagesPath.size()];				
				for (int i = 0; i < mImagesPath.size(); i++) {
					data[i] = mImagesPath.get(i) ;
				}
				DataModel.sSelf.storeObject(DMKeys.MULTI_IMAGES, data);
				setResult(RESULT_OK) ;
				finish() ;
				
			}
		});
		
		
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		try{
			String s[] = (String[])DataModel.sSelf.removeObject("3333");
			if(s != null )
			for (int i = 0; i < s.length; i++) {
				try{
				mImagesPath.add(s[i]) ;
				
				}catch (Exception e) {
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Override
	public void onBackPressed() {
		
//		super.onBackPressed();
		if(onBackPressedCheck())return;
		setResult(RESULT_OK) ;
		finish() ;
	}
	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public ImageAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return count;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.galleryitem, null);
				holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
				holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
				
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.checkbox.setId(position);
			holder.imageview.setId(position);
			holder.checkbox.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					if(maxImageToAttached>-1 && mImagesPath.size() >= maxImageToAttached){
						Toast.makeText(AndroidCustomGalleryActivity.this, "You can not select more than "+maxImageToAttached +" images", Toast.LENGTH_LONG).show() ;
						CheckBox cb = (CheckBox) v;
						cb.setChecked(false);
						return ;
					}
//					else{
					CheckBox cb = (CheckBox) v;
					int id = cb.getId();
					String capturedPath = arrPath[id] ;
					if (thumbnailsselection[id]){
						cb.setChecked(false);
						thumbnailsselection[id] = false;
						for (int i = 0; i < mImagesPath.size(); i++) {
							if(mImagesPath.get(i).equalsIgnoreCase(capturedPath)){
								mImagesPath.remove(i) ;
								long size = ImageUtils.getImageSize(capturedPath) ;
								totalSize = totalSize -size ;
								((TextView)findViewById(R.id.info)).setText(String
										.format("Max count: "+maxImageToAttached+" within max size: 4 MB. Current attached size: %s MB",
												(totalSize / 1024.0 / 1024.0)));
								break;
							}
						}
						return;
					} else {
						cb.setChecked(true);
						thumbnailsselection[id] = true;
					}
//				}
					////////////////////////
					
				
					long size = ImageUtils.getImageSize(capturedPath) ;
					if (mImagesPath.contains(capturedPath)) {
						cb.setChecked(true);
						thumbnailsselection[id] = true;
						showSimpleAlert("Error",
								"This image has already been attached!");
						return;
					}
					if (isSizeReachedMaximum(size)) {
						cb.setChecked(false);
						thumbnailsselection[id] = false;
						
						showSimpleAlert("Error",
								"Max attachment reached. Can not attach more!");
						return;
					}
					/*Bitmap bm1 = ImageUtils.getImageFor(capturedPath, 4);
					if (isSizeReachedMaximum(bm1)) {
						cb.setChecked(false);
						thumbnailsselection[id] = false;
						bm1.recycle();
						bm1 = null ;
						showSimpleAlert("Error",
								"Max attachment reached. Can not attach more!");
						return;
					}
					bm1.recycle();
					bm1 = null ;*/
					System.gc();
					mImagesPath.add(capturedPath);
										
					((TextView)findViewById(R.id.info)).setText(String
							.format("Max count: "+maxImageToAttached+" within max size: 4 MB. Current attached size: %s MB",
									(totalSize / 1024.0 / 1024.0)));
//					Toast.makeText(AndroidCustomGalleryActivity.this, String
//						.format("Max count: 20 within max size: 4 MB. Current attached size: %s MB",
//								(totalSize / 1024.0 / 1024.0)), Toast.LENGTH_LONG).show() ;
//										/////////////////////////

				}
			});
			holder.imageview.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					/*int id = v.getId();
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse("file://" + arrPath[id]), "image/*");
					startActivity(intent);*/
				}
			});
			if(thumbnails[position] == null)
			thumbnails[position] = MediaStore.Images.Thumbnails.getThumbnail(
					getApplicationContext().getContentResolver(), idThumb[position],
					MediaStore.Images.Thumbnails.MICRO_KIND, null);
			
			holder.imageview.setImageBitmap(thumbnails[position]);
			holder.checkbox.setChecked(thumbnailsselection[position]);
			holder.id = position;
			return convertView;
		}
	}
	class ViewHolder {
		ImageView imageview;
		CheckBox checkbox;
		int id;
	}
	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub
		
	}
	
	private long totalSize = 0;

	private boolean isSizeReachedMaximum(Bitmap bm) {
		long newSize = 0;
		if (null != bm) {
			newSize = ImageUtils.getImageSize(bm);
		}
		if ((totalSize + newSize) > Constants.MAX_IMAGE_ATTACH_SIZE) {
			return true;
		}
		totalSize += newSize;
		return false;
	}
	private boolean isSizeReachedMaximum(long newSize) {
		
		if ((totalSize + newSize) > Constants.MAX_IMAGE_ATTACH_SIZE) {
			return true;
		}
		totalSize += newSize;
		return false;
	}
}