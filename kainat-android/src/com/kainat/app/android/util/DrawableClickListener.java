package com.kainat.app.android.util;

import android.view.View;

public interface DrawableClickListener {

public static enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT };
public void onClick(DrawablePosition target); 
public void onClick(DrawablePosition target,View view); }