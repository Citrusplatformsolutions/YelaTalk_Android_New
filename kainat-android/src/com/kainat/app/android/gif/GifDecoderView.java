
package com.kainat.app.android.gif;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;


public class GifDecoderView extends ImageView {

    private boolean mIsPlayingGif = false;

    private GifDecoder mGifDecoder;

    private Bitmap mTmpBitmap;

    final Handler mHandler = new Handler();

    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            if (mTmpBitmap != null && !mTmpBitmap.isRecycled()) {
                GifDecoderView.this.setImageBitmap(mTmpBitmap);
            }
        }
    };
    public GifDecoderView(Context context) {
        super(context);
       
    }
    public GifDecoderView(Context context, InputStream stream) {
        super(context);
       playGif(stream);
        
      //  playGifImage task=new playGifImage(stream);
		//task.execute(new String[] { ""});
    }

    
    public void recycleView(){
    	if(mGifDecoder!=null){
    	mGifDecoder.recycleimage();
    	}
    	if(mTmpBitmap!=null){
    		mTmpBitmap.recycle();
    	}
    	System.gc();
    }
    public void playGif(InputStream stream) {
        mGifDecoder = new GifDecoder();
        mGifDecoder.read(stream);

        mIsPlayingGif = true;
   
        new Thread(new Runnable() {
            public void run() {
                final int n = mGifDecoder.getFrameCount();
                final int ntimes = mGifDecoder.getLoopCount();
                int repetitionCounter = 0;
                do {
                	//System.out.println("playing");
                    for (int i = 0; i < n; i++) {
                        mTmpBitmap = mGifDecoder.getFrame(i);
                        int t = mGifDecoder.getDelay(i);
                        mHandler.post(mUpdateResults);
                        try {
                            Thread.sleep(t);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(n==0)
                        mHandler.post(mUpdateResults);
                    if(ntimes != 0) {
                        repetitionCounter ++;
                    }
                } while (mIsPlayingGif && (repetitionCounter <= ntimes));
            }
        }).start();
    }
    public void playGifLoopCountOne(InputStream stream) {
    	try{
        mGifDecoder = new GifDecoder();
        mGifDecoder.read(stream);

        mIsPlayingGif = true;
   
        new Thread(new Runnable() {
            public void run() {
                final int n = mGifDecoder.getFrameCount();
                final int ntimes =1;// mGifDecoder.getLoopCount();
                int repetitionCounter = 0;
                do {
                	//System.out.println("playing");
                    for (int i = 0; i < n; i++) {
                        mTmpBitmap = mGifDecoder.getFrame(i);
                        int t = mGifDecoder.getDelay(i);
                        mHandler.post(mUpdateResults);
                        try {
                            Thread.sleep(t);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(n==0)
                        mHandler.post(mUpdateResults);
                    if(ntimes != 0) {
                        repetitionCounter ++;
                    }
                } while (mIsPlayingGif && (repetitionCounter < ntimes));
            }
        }).start();
    	}catch (Exception e) {
			// TODO: handle exception
		}
    }
    public void playGifOneFrame(InputStream stream) {
        mGifDecoder = new GifDecoder();
        mGifDecoder.read(stream);

        mIsPlayingGif = true;
   
        new Thread(new Runnable() {
            public void run() {
                final int n = mGifDecoder.getFrameCount();
                final int ntimes = mGifDecoder.getLoopCount();
                int repetitionCounter = 0;
                do {
                	//System.out.println("playing");
                    for (int i = 0; i < 1; i++) {
                        mTmpBitmap = mGifDecoder.getFrame(i);
                        int t = mGifDecoder.getDelay(i);
                        mHandler.post(mUpdateResults);
                        try {
                            Thread.sleep(t);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(n==0)
                        mHandler.post(mUpdateResults);
                    if(ntimes != 0) {
                        repetitionCounter ++;
                    }
                } while (mIsPlayingGif && (repetitionCounter <= ntimes));
            }
        }).start();
    }
    
    private class playGifImage extends AsyncTask<String, Void, String> {
		InputStream stream;
		 public playGifImage(InputStream stream){
		this.stream=stream;
		
		 }
		    @Override
		    protected String doInBackground(String... urls) {
		      String response = "";
		      mGifDecoder = new GifDecoder();
		        mGifDecoder.read(stream);

		        mIsPlayingGif = true;
		     
		        new Thread(new Runnable() {
		            public void run() {
		                final int n = mGifDecoder.getFrameCount();
		                final int ntimes = mGifDecoder.getLoopCount();
		                int repetitionCounter = 0;
		                do {
		                    for (int i = 0; i < n; i++) {
		                        mTmpBitmap = mGifDecoder.getFrame(i);
		                        int t = mGifDecoder.getDelay(i);
		                        mHandler.post(mUpdateResults);
		                        try {
		                            Thread.sleep(t);
		                        } catch (InterruptedException e) {
		                            e.printStackTrace();
		                        }
		                    }
		                    if(n==0)
		                        mHandler.post(mUpdateResults);
		                    if(ntimes != 0) {
		                        repetitionCounter ++;
		                    }
		                } while (mIsPlayingGif && (repetitionCounter <= ntimes));
		            }
		        }).start();
		        return response;
		    }

		    @Override
		    protected void onPostExecute(String result) {
		    
		    }
		  }
    
    public void stopRendering() {
        mIsPlayingGif = true;
    }
}
