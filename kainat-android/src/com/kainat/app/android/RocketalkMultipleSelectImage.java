package com.kainat.app.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.kainat.app.android.core.BusinessProxy;
import com.kainat.app.android.util.Constants;
import com.kainat.app.android.util.DMKeys;
import com.kainat.app.android.util.DataModel;
import com.kainat.app.android.util.ImageUtils;
import com.kainat.app.android.util.ResponseObject;

public class RocketalkMultipleSelectImage extends UIActivityManager {
	private int count;
	private Bitmap[] thumbnails;
	private int[] idThumb;
	private boolean[] thumbnailsselection;
	private String[] arrPath;

	//	public static Map<Integer, SoftReference<Bitmap>> imageCache;
	private ImageAdapter imageAdapter;
	private List<String> mImagesPath = new ArrayList<String>();
	private List<Integer> mImagesPathID = new ArrayList<Integer>();
	private int maxImageToAttached = -1 ;
	private  Button selectBtn ;
	int width ;
	int height ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multi_gal);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels/3;
		//		height = width * mainImage.getHeight() / mainImage.getWidth(); //mainImage is the Bitmap I'm drawing
		//		addView(mainImageView,new LinearLayout.LayoutParams( 
		//		        width, height));

		//		if (imageCache == null)
		//			imageCache = new HashMap<Integer, SoftReference<Bitmap>>();

		try{


			final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
			final String orderBy = sortOrder();//MediaStore.Images.Media._ID;
			String condition = MediaStore.Images.Media.DATA+" like '%/GetImageFromThisDirectory/%'";

			Cursor imagecursor = managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
					null, orderBy);
			int image_column_index = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
			this.count = imagecursor.getCount();
			this.thumbnails = new Bitmap[this.count];
			this.idThumb = new int[this.count];
			maxImageToAttached = getIntent().getIntExtra("MAX", Constants.MAX_IMAGE_SIZE) ;
			this.arrPath = new String[this.count];
			this.thumbnailsselection = new boolean[this.count];
			//		for (int i = this.count-1; i > 0; i--) {
			Vector vidThumb = new Vector();
			Vector vidarrPath = new Vector();
			for (int i = 0; i < this.count; i++) {
				imagecursor.moveToPosition(i);
				int id = imagecursor.getInt(image_column_index);

				int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
				//			if(imagecursor.getString(dataColumnIndex).endsWith(".png"))
				//				continue ;

				vidarrPath.add(imagecursor.getString(dataColumnIndex)) ;

				//idThumb[i] = id ;
				vidThumb.add(id) ;
				//			int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
				/*thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
					getApplicationContext().getContentResolver(), id,
					MediaStore.Images.Thumbnails.MICRO_KIND, null);*/
				//			arrPath[i]= imagecursor.getString(dataColumnIndex);
				//			vidarrPath.add(imagecursor.getString(dataColumnIndex)) ;
			}

			this.thumbnails = new Bitmap[vidThumb.size()];
			this.idThumb = new int[vidThumb.size()];
			this.arrPath = new String[vidThumb.size()];
			count = vidThumb.size() ;
			int indexl= 0 ;
			for (int i = vidThumb.size()-1; i > -1; i--) {
				idThumb[indexl++] = (Integer)vidThumb.get(i) ;
			}
			indexl = 0 ;
			for (int i = vidarrPath.size()-1; i > -1; i--) {
				arrPath[indexl++] = (String)vidarrPath.get(i) ;
			}
			vidThumb.clear();
			vidarrPath.clear();
			//            for(int i = 0; i < arrPath.length; i++)  
			//            {  
			//                String  temp = arrPath[i];  
			//                arrPath[i] = arrPath[arrPath.length - i - 1];  
			//                arrPath[arrPath.length - 1] = temp;  
			////                System.out.println("myArray[" + i + "] = " + arrayOne[i]);  
			//            }  
			//            for(int i = 0; i < idThumb.length; i++)  
			//            {  
			//                int temp = idThumb[i];  
			//                idThumb[i] = idThumb[idThumb.length - i - 1];  
			//                idThumb[idThumb.length - 1] = temp;  
			////                System.out.println("myArray[" + i + "] = " + arrayOne[i]);  
			//            }  
			GridView imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
			imageAdapter = new ImageAdapter();
			imagegrid.setAdapter(imageAdapter);
			imagecursor.close();
			imagegrid.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {				
					// TODO Auto-generated method stub

					//				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					//					System.out.println("-----------onScrollStateChanged-------");
					//					System.out.println("-----------scrollState-------"+scrollState);
					//					System.out.println("-----------view.getFirstVisiblePosition()-------"+view.getFirstVisiblePosition());
					//					System.out.println("-----------getLastVisiblePosition()-------"+view.getLastVisiblePosition());		
					//					int tot  = view.getLastVisiblePosition() - view.getFirstVisiblePosition() ;
					//					System.out.println("-----------AsyncTaskVec size-------"+AsyncTaskVec.size());
					//				}
					//				if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					//					if(asyncTaskMulti != null)
					//					asyncTaskMulti.cancel(true) ;
					//					asyncTaskMulti = null ;
					//					AsyncTaskVec = new Vector<AsyncTask>();
					//					index = 0 ;
					//				}

					if (scrollState != 0) 
					{					
						//					AsyncTaskVec = new Stack<AsyncTask>();
						if(AsyncTaskVec!=null)
							AsyncTaskVec.clear() ;
						imageAdapter.isScrolling = true;
					} 
					else 
					{
						imageAdapter.isScrolling = false;
						imageAdapter.notifyDataSetChanged();
						//					imageAdapter.notifyDataSetInvalidated();
					}
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					//				if(AsyncTaskVec.size() > 100)
					//				AsyncTaskVec = new Vector<AsyncTask>();
					// TODO Auto-generated method stub
					//				System.out.println("-----------onScroll-------");
					//				System.out.println("-----------totalItemCount-------"+totalItemCount);
					//				System.out.println("-----------visibleItemCount-------"+visibleItemCount);
					//				System.out.println("-----------firstVisibleItem-------"+firstVisibleItem);
					//				System.out.println("-----------view.getFirstVisiblePosition()-------"+view.getFirstVisiblePosition());
					//				System.out.println("-----------getLastVisiblePosition()-------"+view.getLastVisiblePosition());

				}
			});
		    selectBtn = (Button) findViewById(R.id.selectBtn);
			selectBtn.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					String [] data = new String[mImagesPath.size()];				
					for (int i = 0; i < mImagesPath.size(); i++) {
						data[i] = mImagesPath.get(i) ;
					}
					Integer [] dataId = new Integer[mImagesPathID.size()];				
					for (int i = 0; i < mImagesPathID.size(); i++) {
						dataId[i] = mImagesPathID.get(i) ;
					}
					DataModel.sSelf.storeObject(DMKeys.MULTI_IMAGES, data);
					DataModel.sSelf.storeObject(DMKeys.MULTI_IMAGES+"ID", dataId);
					setResult(RESULT_OK) ;
					finish() ;

				}
			});

		}catch (Exception e) {
			setResult(RESULT_OK) ;
			finish() ;
		}

		((ImageView)findViewById(R.id.back_arrow_iv)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}
	protected String sortOrder()
	{
		String str;
		//	    if (this.mSort != 1)
		//	      str = " DESC";
		//	    else
		str = " ASC";
		return "case ifnull(datetaken,0) when 0 then date_modified*1000 else datetaken end" + str + ", _id" + str;
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
						long size = ImageUtils.getImageSize(s[i]) ;

						/*if (isSizeReachedMaximum(size)) {
					if(BusinessProxy.sSelf.MaxSizeData==2){
						showSimpleAlert("Error",
						"You should move to a WiFi or 3G network for higher file sizes. On slower networks we restrict attachments to 2MB");
					}else{

					showSimpleAlert("Error","Max attachment reached. Can not attach more!");
					}
					return;
				}*/
					}catch (Exception e) {
					}
				}

			Integer sInt[] = (Integer[])DataModel.sSelf.removeObject("3333"+"ID");
			if(sInt != null )
			{
				for (int i = 0; i < sInt.length; i++) {
					try{
						mImagesPathID.add(sInt[i]) ;

					}catch (Exception e) {
					}
				}
			}
			else if(mImagesPath != null && mImagesPath.size() > 0)
			{
				for (int i = 0; i < mImagesPath.size(); i++) {
					try{
						mImagesPathID.add(1) ;
					}catch (Exception e) {
					}
				}
			}


			((TextView)findViewById(R.id.info)).setText((getString(R.string.max_count)+": "+maxImageToAttached));//+" within max size: "+BusinessProxy.sSelf.MaxSizeData+" MB. Current attached size: "+onMbKbReturnResult(totalSize)


		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		onBackPressed() ;
	}
	@Override
	public void onBackPressed() {	
		if(onBackPressedCheck())return;
		setResult(RESULT_OK) ;
		finish() ;

	}
	@Override
	protected void onStop() {
		super.onStop();
		realeseResources();
	}
	public void realeseResources(){
		for (int i = 0; i < thumbnails.length; i++) {
			try{
				if(thumbnails[i] != null && !thumbnails[i].isRecycled()){
					thumbnails[i].recycle();
					thumbnails[i] = null ;
					//			System.out.println("---- recycle thumb "+i);
				}

			}catch (Exception e) {
				e.printStackTrace() ;
			}
		}

	}
	class AsyncTaskMulti extends AsyncTask<ViewHolder, Void, Bitmap>{
		private ViewHolder v;

		@Override
		protected Bitmap doInBackground(ViewHolder... params) {
			v = params[0];
			//	    	System.out.println("-------thumb for "+v.pos);
			if(thumbnails[v.pos] !=null)
				return  thumbnails[v.pos];
			return MediaStore.Images.Thumbnails.getThumbnail(
					getApplicationContext().getContentResolver(), idThumb[v.pos],
					MediaStore.Images.Thumbnails.MINI_KIND, null);

		}

		@Override
		protected void onPostExecute(final Bitmap result) {
			//	        super.onPostExecute(result);
			//	        {				
			//				v.loaded = true ;

			//	            runOnUiThread(new Runnable() {					
			//					@Override
			//					public void run() {	
			try{	
				//						System.out.println("---------onPostExecute----");
				//						 v.imageview.setBackground(new BitmapDrawable(getResources(),result));//ImageBitmap(result);
				v.imageview.setImageBitmap(result);
				v.imageview.invalidate() ;
				thumbnails[v.pos] = result ;

				//				         SoftReference  bitmapRef = new SoftReference<Bitmap>(result);
				//							if (result != null) {
				//								imageCache.put( idThumb[v.pos], result);
				//							}

				asyncTaskMulti = null ;
				if(asyncTaskMulti == null){
					asyncTaskMulti = new AsyncTaskMulti();
					asyncTaskMulti.execute((ViewHolder)AsyncTaskVec.pop()) ;
				}}catch (Exception e) {
					// TODO: handle exception
					//							e.printStackTrace();
				}
			//					}
			//				});
			//	        }
		}
	};
	int index = 0 ;
	AsyncTaskMulti asyncTaskMulti ;
	Stack AsyncTaskVec = new Stack<AsyncTask>();
	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		public boolean isScrolling = false; 
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

			//			SoftReference<Bitmap> bitmapRef = (SoftReference<Bitmap>) imageCache
			//					.get(f.getPath());


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
				//				thumbnails[holder.pos] = null ;
				if(thumbnails[position] !=null)
					holder.imageview.setImageBitmap(thumbnails[position]);
				//					holder.imageview.setBackground(new BitmapDrawable(getResources(),thumbnails[position]));//ImageBitmap(result);
				else
				{
					holder.imageview.setImageBitmap(null);
					//						holder.imageview.setBackground(null);
				}
				//				holder.imageview.setImageBitmap(null);
				//				if(holder.loaded)
				//				recycleImageView(holder.imageview);
				//				holder.loaded = false ;
				//holder.imageview.setImageResource(R.drawable.im_avatar_picture_border_normal);
			}
			holder.pos = position ;
			holder.checkbox.setId(position);
			holder.imageview.setId(position);

			for (int i = 0; i < mImagesPath.size(); i++) {
				if(mImagesPath.get(i).equalsIgnoreCase(arrPath[position])){
					holder.checkbox.setChecked(true);
					thumbnailsselection[position] = true;
				}
			}
			holder.imageview.setTag(holder) ;
			holder.imageview.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {	
					//					ViewHolder holder
					ViewHolder holder  = (ViewHolder) v.getTag();

					CheckBox cb = holder.checkbox ;

					if(maxImageToAttached>-1 && mImagesPath.size() >= maxImageToAttached && !cb.isChecked()){
					/*	showSimpleAlert("Error",getString(R.string.max_attachment_reached)
								);	*/
						
						new AlertDialog.Builder(RocketalkMultipleSelectImage.this).setMessage(getString(R.string.max_attachment_reached))
						.setPositiveButton(R.string.done,
								new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int whichButton) {
								
							}
						}).show();
						cb.setChecked(false);
						return ;
					}

					//					cb = (CheckBox) v;
					int id = cb.getId();
					String capturedPath = arrPath[id] ;
					int capturedPathID = idThumb[id] ;
					if (thumbnailsselection[id]){
						cb.setChecked(false);
						thumbnailsselection[id] = false;
						for (int i = 0; i < mImagesPath.size(); i++) {
							if(mImagesPath.get(i).equalsIgnoreCase(capturedPath)){
								mImagesPath.remove(i) ;
								mImagesPathID.remove(i) ;
								long size = ImageUtils.getImageSize(capturedPath) ;
								totalSize = totalSize -size ;
								//								((TextView)findViewById(R.id.info)).setText((getString(R.string.max_attachment_reached)+": "+maxImageToAttached));
								break;
							}
						}
						return;
					} else {
						cb.setChecked(true);
						thumbnailsselection[id] = true;
					}
					long size = ImageUtils.getImageSize(capturedPath) ;
					if (mImagesPath.contains(capturedPath)) {
						cb.setChecked(true);
						thumbnailsselection[id] = true;
						showSimpleAlert("Error",
								"This image has already been attached!");
						return;
					}

					System.gc();
					mImagesPath.add(capturedPath);
					mImagesPathID.add(capturedPathID) ;
					//					((TextView)findViewById(R.id.info)).setText((getString(R.string.max_attachment_reached)+": "+maxImageToAttached));			
					selectBtn.performClick();
				}
			});
			holder.checkbox.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {				
					CheckBox cb = (CheckBox) v;
					if(maxImageToAttached>-1 && mImagesPath.size() >= maxImageToAttached && cb.isChecked()){
						showSimpleAlert("Error",getString(R.string.max_attachment_reached)
								);				
						cb.setChecked(false);
						return ;
					}

					cb = (CheckBox) v;
					int id = cb.getId();
					String capturedPath = arrPath[id] ;
					int capturedPathID = idThumb[id] ;
					if (thumbnailsselection[id]){
						cb.setChecked(false);
						thumbnailsselection[id] = false;
						for (int i = 0; i < mImagesPath.size(); i++) {
							if(mImagesPath.get(i).equalsIgnoreCase(capturedPath)){
								mImagesPath.remove(i) ;
								mImagesPathID.remove(i) ;
								long size = ImageUtils.getImageSize(capturedPath) ;
								totalSize = totalSize -size ;
								//								((TextView)findViewById(R.id.info)).setText((getString(R.string.max_attachment_reached)+": "+maxImageToAttached));
								break;
							}
						}
						return;
					} else {
						cb.setChecked(true);
						thumbnailsselection[id] = true;
					}
					long size = ImageUtils.getImageSize(capturedPath) ;
					if (mImagesPath.contains(capturedPath)) {
						cb.setChecked(true);
						thumbnailsselection[id] = true;
						showSimpleAlert("Error"," This image has already been attached!");
						return;
					}

					System.gc();
					mImagesPath.add(capturedPath);
					mImagesPathID.add(capturedPathID) ;
					//					((TextView)findViewById(R.id.info)).setText((getString(R.string.max_attachment_reached)+": "+maxImageToAttached));			
				}
			});
			//			holder.imageview.setOnClickListener(new OnClickListener() {
			//				
			//				public void onClick(View v) {
			//					// TODO Auto-generated method stub
			//					/*int id = v.getId();
			//					Intent intent = new Intent();
			//					intent.setAction(Intent.ACTION_VIEW);
			//					intent.setDataAndType(Uri.parse("file://" + arrPath[id]), "image/*");
			//					startActivity(intent);*/
			//				}
			//			});
			//			if(thumbnails[position] == null){
			//			thumbnails[position] = MediaStore.Images.Thumbnails.getThumbnail(
			//					getApplicationContext().getContentResolver(), idThumb[position],
			//					MediaStore.Images.Thumbnails.MICRO_KIND, null);
			//			if(thumbnails[position] != null )
			//			holder.loaded = true ;
			//			}


			//.execute(holder);

			holder.checkbox.setChecked(thumbnailsselection[position]);
			holder.id = position;

			//			holder.imageview.setImageURI(Uri.withAppendedPath(
			//                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + idThumb[position]));


			if(!isScrolling){
				AsyncTaskVec.push(holder);
				if(asyncTaskMulti == null || asyncTaskMulti.isCancelled()){
					asyncTaskMulti = new AsyncTaskMulti();
					asyncTaskMulti.execute(holder) ;
				}else if(asyncTaskMulti != null && asyncTaskMulti.getStatus() == AsyncTask.Status.PENDING){
					asyncTaskMulti.execute(holder) ;
				}
			}
			return convertView;
		}
	}
	class ViewHolder {
		ImageView imageview;
		CheckBox checkbox;
		int id;
		int pos;
		//		boolean loaded;
	}
	@Override
	public void notificationFromTransport(ResponseObject response) {
		// TODO Auto-generated method stub

	}

	private long totalSize = 0;
	long MAX_IMAGE_ATTACH_SIZE=BusinessProxy.sSelf.MaxSizeData * 1024 * 1024;
	private boolean isSizeReachedMaximum(Bitmap bm) {
		long newSize = 0;
		if (null != bm) {
			newSize = ImageUtils.getImageSize(bm);
		}
		if ((totalSize + newSize) > MAX_IMAGE_ATTACH_SIZE) {
			return true;
		}
		totalSize += newSize;
		return false;
	}
	private boolean isSizeReachedMaximum(long newSize) {
		//		System.out.println("----newSize---"+newSize);
		//		System.out.println("----totalSize + newSize---"+(totalSize + newSize));
		//		System.out.println("----Constants.MAX_IMAGE_ATTACH_SIZE---"+Constants.MAX_IMAGE_ATTACH_SIZE);
		if ((totalSize + newSize) > MAX_IMAGE_ATTACH_SIZE) {
			return true;
		}
		totalSize += newSize;
		return false;
	}
	public void recycleImageView(ImageView imageview){
		Drawable drawable = imageview.getDrawable();
		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			Bitmap bitmap = bitmapDrawable.getBitmap();
			bitmap.recycle();
			bitmapDrawable = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(thumbnails!=null)
			for (int i = 0; i < thumbnails.length; i++) {
				if(thumbnails[i]!=null && !thumbnails[i].isRecycled())
					thumbnails[i].recycle() ;
			}
	}
}