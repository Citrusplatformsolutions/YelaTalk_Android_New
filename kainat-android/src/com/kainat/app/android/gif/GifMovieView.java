
package com.kainat.app.android.gif;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.SystemClock;
import android.view.View;

public class GifMovieView extends View {

    private Movie mMovie;

    private long mMoviestart;

    public GifMovieView(Context context, InputStream stream) {
        super(context);
        try{
        mMovie = Movie.decodeStream(stream);       
        }catch (Exception e) {
			// TODO: handle exception
		}
    }
    public GifMovieView(Context context) {
        super(context);
        
       
    }
    
    public void GetPlay(Context context, InputStream stream){
    	 
    	try{
    	mMovie = Movie.decodeStream(stream);     
    	}catch (Exception e) {
			// TODO: handle exception
		}
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	try{
        canvas.drawColor(Color.TRANSPARENT);
        
        super.onDraw(canvas);
        final long now = SystemClock.uptimeMillis();
       // if(!BusinessProxy.sSelf.animPlay)
        	//return;
       if (mMoviestart == 0 ) { 
            mMoviestart = now;
        }
     //   getWidth()/2 - imageWidth/2,
      //  getHeight()/2 - imageHeight/2
        final int relTime = (int)((now - mMoviestart) % (mMovie.duration()));
        mMovie.setTime(relTime);
        mMovie.draw(canvas, getWidth()/2-mMovie.width()/2, getHeight()/2-mMovie.height()/2);
        
        this.invalidate();
       // System.out.println("relTime==="+relTime);
       // System.out.println("mMoviestart=="+mMoviestart);
    	}catch (Exception e) {
			// TODO: handle exception
		}
        
    }
}
