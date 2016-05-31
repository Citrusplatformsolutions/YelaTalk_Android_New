package com.kainat.app.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CustomMenu {

        private static volatile CustomMenu mMenu = null;

        private ArrayList<CustomMenuItem> mMenuItems;
        private ArrayList<CustomMenuItem> drableIconData;
        private OnMenuItemSelectedListener mListener = null;
        private Activity mContext = null;
        private static volatile PopupWindow mPopupWindow = null;
        private static boolean mIsShowing = false;
        private List<ActionItem> actionItems = new ArrayList<ActionItem>();
        public interface OnMenuItemSelectedListener {
                public void MenuItemSelectedEvent(Integer selection);
        }

        public static boolean isShowing() {
                return mIsShowing;
        }
        public CustomMenu(Activity activity, OnMenuItemSelectedListener listener,
                        HashMap<Integer, String> items ,HashMap<Integer, Integer> drableIcon) {

                mListener = listener;
                mMenuItems = new ArrayList<CustomMenuItem>();
                drableIconData =new ArrayList<CustomMenuItem>();
                mContext = activity;

                // Add items to the menu
                for (Integer id : items.keySet()) {

                        String name = items.get(id);
                        int imageIdVaue =-1;
                        if(drableIcon!=null)
                        imageIdVaue =drableIcon.get(id);
                       
                        CustomMenuItem cmi = new CustomMenuItem();
                        cmi.setCaption(name);
                        cmi.setId(id);
                        if(imageIdVaue!=-1){
                        cmi.setImageId(imageIdVaue);
                        }
                        mMenuItems.add(cmi);

                }

        }


        public void show(View v,HashMap<Integer, Integer> drableIcon) {

        	
        	//Drawable icon=drawable;
                // The menu is shown
        	//System.out.println("click=========== menu action");
                mIsShowing = true;
                int itemCount = mMenuItems.size();

                if (itemCount < 1)
                        return; // Nothing to show

                if (mPopupWindow != null)
                        return; // The menu is opened

                // Display settings
                Display display = ((WindowManager) mContext
                                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

                // The view to show
                View mView = ((Activity) mContext).getLayoutInflater().inflate(
                                R.layout.custom_menu, null);

                 // Create popup window to show
                mPopupWindow = new  PopupWindow(mView, LayoutParams.FILL_PARENT,
                                LayoutParams.WRAP_CONTENT, false);
//                mPopupWindow.setWidth(width-20);
               
//                mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                
                
                
               
//                WindowManager.LayoutParams lp = mPopupWindow.getAttributes();  
//    			lp.dimAmount = 0.4f;
//    			lDialog.getWindow().setAttributes(lp);  
//    			lDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);  
//    			lDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    			
                if(itemCount>=7)
                    mPopupWindow.setHeight(display.getHeight()-(display.getHeight()/3));
                
                mPopupWindow.setOutsideTouchable(true);
         
               // mPopupWindow.setAnimationStyle(R.style.Animations_GrowFromBottom);
         
                mPopupWindow.setTouchInterceptor(new OnTouchListener() {
         
                     
         
                    public boolean onTouch(View v, MotionEvent event) 
         
                    {
         
                        if(event.getAction() == MotionEvent.ACTION_OUTSIDE) 
         
                        {
                           	hide();

                        	//mPopupWindow.dismiss();
         
                            return true;
         
                        }
         
                        return false;
         
                    }
         
                });
               /* mPopupWindow.setBackgroundDrawable (new BitmapDrawable());
                mPopupWindow.setFocusable(false);
                mPopupWindow.setOutsideTouchable(false); 
                mPopupWindow.setTouchable(false);
                mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                    	   System.out.println("-----------touch action--r--");
                        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        	hide();
							System.out.println("-----------touch action----");
                            mPopupWindow.dismiss();               
                            return true;
                        }
                        return false;
                    }
                });*/
//                mPopupWindow.setAnimationStyle(R.style.DialogAnimation);
                mPopupWindow.setAnimationStyle((1==2) ? R.style.Animations_PopUpMenu_Center : R.style.Animations_PopDownMenu_Center);
                mPopupWindow.setWidth(display.getWidth()-50);
//                Utilities.startAnimition(mContext,mView.findViewById(R.id.custom_menu),  R.anim.rail);
                mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, bottomPos);
              
                
               // mPopupWindow.setTouchable(false);
                //mPopupWindow.setFocusable(false);
               // mPopupWindow.setOutsideTouchable(true);		
               
                
                /*mPopupWindow.setTouchInterceptor(new OnTouchListener() {
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
							hide();
							System.out.println("-----------touch action----");
							return false;
						}else{
						System.out.println("-----------touch action---nontouc-");
						}
						return false;
					}
				});*/
                mPopupWindow.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						hide();
					System.out.println("-----------touch action   disniss----");
					}
				});
                // mPopupWindow.setTouchable(false);    
              // mPopupWindow.setFocusable(false);    
              // mPopupWindow.setOutsideTouchable(false);  
               // mPopupWindow.setTouchable(true);
             
                    //  mwindow.setFocusable(true);
               
                    //  mwindow.setOutsideTouchable(true);

               /* mPopupWindow.setTouchInterceptor(new OnTouchListener() {
                	
                	            public boolean onTouch(View v, MotionEvent event) {
                
                	                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                
                	                	mPopupWindow.dismiss();
                
                	                     
                	
                	                    return true;
                	
                	                }
                	
                	                 
                
                	                return false;
                	
                	            }
              
                	        });*/
                
               

              
                // Add menu items
                TableLayout table = (TableLayout) mView
                                .findViewById(R.id.custom_menu_table);
                table.removeAllViews();

                
                for (int i = 0; i < itemCount; i++) {

                        TableRow row = null;
                        TextView btn = null;
                       ImageView icon=null;
                       // Drawable iconDraw= drawable[i];
                        // create headers
                        row = new TableRow(mContext);
                        row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                                        LayoutParams.WRAP_CONTENT));

                        final CustomMenuItem cmi = mMenuItems.get(i);
                        View itemLayout = ((Activity) mContext).getLayoutInflater()
                                        .inflate(R.layout.custom_menu_item, null);
                       
                        btn = (TextView) itemLayout
                                        .findViewById(R.id.custom_menu_item_caption);
                        icon = (ImageView) itemLayout
                                .findViewById(R.id.custom_menu_item_caption_image);
                       	if(cmi.getImageId()!=-1){
                        	
                       		icon.setVisibility(View.VISIBLE);
                        	//Integer intId =Integer.parseInt(cmi.getImageId());
                        	icon.setBackgroundResource(cmi.getImageId());
                          }else{
                        		icon.setVisibility(View.GONE);
                          }
                        btn.setText(cmi.getCaption());
                       
                        if(cmi.getCaption().indexOf("ancel")!= -1){
//                        	itemLayout.setBackgroundResource(R.drawable.forcanclemenu);
                        	//btn.setTextColor(Color.WHITE) ;
                        }

                        btn.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        mListener.MenuItemSelectedEvent(cmi.getId());
                                        hide();
                                }
                        });

                        row.addView(itemLayout);
                        table.addView(row);
                }
//                if(mPopupWindow.getHeight()>(display.getHeight()-100))
               
                
//                Utilities.startAnimition(mContext,table,  R.anim.grow_from_bottom);

        }

        /**
         * Hide your menu.
         * 
         * @return void
         */
        public static void hide() {
                mIsShowing = false;
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                	try{
                        mPopupWindow.dismiss();
                        mPopupWindow = null;
                        mMenu = null;
                	}catch (Exception e) {
						// TODO: handle exception
					}
                }
                return;
        }
        public static boolean hideRet() {
            mIsShowing = false;
            if (mPopupWindow != null && mPopupWindow.isShowing()) {
            	try{
                    mPopupWindow.dismiss();
                    mPopupWindow = null;
                    mMenu = null;
                    return true;
            	}catch (Exception e) {
					// TODO: handle exception
				}
            }
            return false;
    }

        public static synchronized void show(Activity context, HashMap<Integer, String> items,
                        OnMenuItemSelectedListener listener,HashMap<Integer, Integer> drableIcon) {
        	bottomPos = 0;
                if (mMenu != null && CustomMenu.isShowing()) {
                        hide();
                        return;
                }
                CustomMenu menu = new CustomMenu(context, listener, items,drableIcon);
                mMenu = menu;
                menu.show(context.findViewById(android.R.id.content).getRootView(),drableIcon);

        }
        static int bottomPos = 0;
        public static synchronized void showPos(Activity context, HashMap<Integer, String> items,
                OnMenuItemSelectedListener listener,int bottomPosl ,HashMap<Integer, Integer> drableIcon) {
        	bottomPos = bottomPosl;
        if (mMenu != null && CustomMenu.isShowing()) {
                hide();
                return;
        }
        CustomMenu menu = new CustomMenu(context, listener, items,drableIcon);
        mMenu = menu;
        menu.show(context.findViewById(android.R.id.content).getRootView(),drableIcon);

}

        /**
         * Menu item class
         * 
         * @author horribile
         * 
         */
        private class CustomMenuItem {

                private String mCaption = null;
                private int mId = -1;
                private int imageId=-1;
                public void setCaption(String caption) {
                        mCaption = caption;
                }

                public String getCaption() {
                        return mCaption;
                }

                public void setId(int id) {
                        mId = id;
                }

                public int getId() {
                        return mId;
                }
                
                public void setImageId(int id){
                	imageId=id;
                }
                public int getImageId(){
                	return imageId;
                }

        }

}