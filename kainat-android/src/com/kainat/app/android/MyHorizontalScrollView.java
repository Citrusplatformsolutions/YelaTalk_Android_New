
package com.kainat.app.android;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;

import com.kainat.app.android.core.BusinessProxy;


public class MyHorizontalScrollView extends HorizontalScrollView {
    public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyHorizontalScrollView(Context context) {
        super(context);
        init(context);
    }
    
    @Override
    public void scrollTo(int x, int y) {
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
//				FeedRequester.requestLeftMenu(getContext()) ;//comment leftmenu request
			}
		}).start();
    		
    	super.scrollTo(x, y);
    }
   
    void init(Context context) {
        // remove the fading as the HSV looks better without it
        setHorizontalFadingEdgeEnabled(false);
        setVerticalFadingEdgeEnabled(false);
    }

   
    public void initViews(View[] children, int scrollToViewIdx, SizeCallback sizeCallback) {
        // A ViewGroup MUST be the only child of the HSV
        ViewGroup parent = (ViewGroup) getChildAt(0);

        // Add all the children, but add them invisible so that the layouts are calculated, but you can't see the Views
        for (int i = 0; i < children.length; i++) {
            children[1].setVisibility(View.VISIBLE);
         
            parent.addView(children[i]);
        }

        // Add a layout listener to this HSV
        // This listener is responsible for arranging the child views.
        listener = new MyOnGlobalLayoutListener(parent, children, scrollToViewIdx, sizeCallback);
       getViewTreeObserver().addOnGlobalLayoutListener(listener);
 
    }
    MyOnGlobalLayoutListener listener ;
    
    public void replaceMainView(View main, int scrollToViewIdx, SizeCallback sizeCallback) {
        // A ViewGroup MUST be the only child of the HSV
        ViewGroup parent = (ViewGroup) getChildAt(0);
        parent.removeViewAt(1) ;
        parent.addView(main);
        View[] children = new View[2] ;
        children[0] = parent.getChildAt(0) ;
        children[1] = parent.getChildAt(1) ;
        
        
        listener.parent = parent;
        listener.children = children;
        listener.scrollToViewIdx = scrollToViewIdx;
        listener.sizeCallback = sizeCallback;
        listener.onGlobalLayout();
//       OnGlobalLayoutListener listener = new MyOnGlobalLayoutListener(parent, children, scrollToViewIdx, sizeCallback);
//       getViewTreeObserver().addOnGlobalLayoutListener(listener);
        
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Do not allow touch events.
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Do not allow touch events.
        return false;
    }

   
    class MyOnGlobalLayoutListener implements OnGlobalLayoutListener {
        ViewGroup parent;
        View[] children;
        int scrollToViewIdx;
        int scrollToViewPos = 0;
        SizeCallback sizeCallback;

       
        public MyOnGlobalLayoutListener(ViewGroup parent, View[] children, int scrollToViewIdx, SizeCallback sizeCallback) {
            this.parent = parent;
            this.children = children;
            this.scrollToViewIdx = scrollToViewIdx;
            this.sizeCallback = sizeCallback;
        }

        @Override
        public void onGlobalLayout() {
            // System.out.println("onGlobalLayout");

            final HorizontalScrollView me = MyHorizontalScrollView.this;

            // The listener will remove itself as a layout listener to the HSV
            me.getViewTreeObserver().removeGlobalOnLayoutListener(this);

          
            sizeCallback.onGlobalLayout();

            parent.removeViewsInLayout(0, children.length);

            final int w = me.getMeasuredWidth();
            final int h = me.getMeasuredHeight();
            	
            // System.out.println("w=" + w + ", h=" + h);

           
            int[] dims = new int[2];
            scrollToViewPos = 0;
            for (int i = 0; i < children.length; i++) {
            	//System.out.println("children.length==="+children.length);
                sizeCallback.getViewSize(i, w, h, dims);
              //   System.out.println("addView w=" + dims[0] + ", h=" + dims[1]);
                children[1].setVisibility(View.VISIBLE);
                parent.addView(children[i], dims[0], dims[1]);
                if (i < scrollToViewIdx) {
                    scrollToViewPos += dims[0];
                }
            }

           
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                	BusinessProxy.sSelf.leftmenuScrolIndex=scrollToViewPos;
//                	if(BusinessProxy.sSelf.leftmenuScrolIndex==0)
                	{
                		
                	}
                    me.scrollBy(scrollToViewPos, 0);
                }
            });
        }
    }
   
    public interface SizeCallback {
        public void onGlobalLayout();
        public void getViewSize(int idx, int w, int h, int[] dims);
    }
}
