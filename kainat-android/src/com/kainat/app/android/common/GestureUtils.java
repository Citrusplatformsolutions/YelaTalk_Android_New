package com.kainat.app.android.common;

import android.graphics.Point;

public class GestureUtils
{
  public static final int Gesture_Circle = 17;
  public static final int Gesture_DOWN = 2;
  public static final int Gesture_LEFT = 3;
  public static final int Gesture_LEFTDOWN = 6;
  public static final int Gesture_LEFTUP = 5;
  public static final int Gesture_RIGHT = 4;
  public static final int Gesture_RIGHTDOWN = 8;
  public static final int Gesture_RIGHTUP = 7;
  public static final int Gesture_Rect_DOWNLEFT = 15;
  public static final int Gesture_Rect_DOWNRIGHT = 16;
  public static final int Gesture_Rect_LEFTDOWN = 10;
  public static final int Gesture_Rect_LEFTUP = 9;
  public static final int Gesture_Rect_RIGHTDOWN = 12;
  public static final int Gesture_Rect_RIGHTUP = 11;
  public static final int Gesture_Rect_UPLEFT = 13;
  public static final int Gesture_Rect_UPRIGHT = 14;
  public static final int Gesture_Tap = -1;
  public static final int Gesture_UP = 1;
  public static final int Gesture_Unknown = 0 ;
  public int DEGREE = 5;
  public Point bottom;
  public Point end;
  public Point left;
  public Point right;
  public Point start;
  public Point top;

  private double abs(double paramDouble)
  {
    double d;
    if (paramDouble < 0.0D)
      d = -1.0D * paramDouble;
    else
      d = paramDouble;
    return d;
  }

  public int recongize(Point[] paramArrayOfPoint)
  {
    int i = 0;
    if (paramArrayOfPoint.length > this.DEGREE)
    {
      double d1;
      double d3;
      double d5;
      int l;
      this.left.x = paramArrayOfPoint[0].x;
      this.left.y = paramArrayOfPoint[0].y;
      this.right.x = paramArrayOfPoint[0].x;
      this.right.y = paramArrayOfPoint[0].y;
      this.top.x = paramArrayOfPoint[0].x;
      this.top.y = paramArrayOfPoint[0].y;
      this.bottom.x = paramArrayOfPoint[0].x;
      this.bottom.y = paramArrayOfPoint[0].y;
      this.start.x = paramArrayOfPoint[0].x;
      this.start.y = paramArrayOfPoint[0].y;
      this.end.x = paramArrayOfPoint[(paramArrayOfPoint.length - 1)].x;
      this.end.y = paramArrayOfPoint[(paramArrayOfPoint.length - 1)].y;
      for (int k = 0; k < paramArrayOfPoint.length; ++k)
      {
        if (paramArrayOfPoint[k].x < this.left.x)
        {
          this.left.x = paramArrayOfPoint[k].x;
          this.left.y = paramArrayOfPoint[k].y;
        }
        if (paramArrayOfPoint[k].x > this.right.x)
        {
          this.right.x = paramArrayOfPoint[k].x;
          this.right.y = paramArrayOfPoint[k].y;
        }
        if (paramArrayOfPoint[k].y < this.bottom.y)
        {
          this.bottom.x = paramArrayOfPoint[k].x;
          this.bottom.y = paramArrayOfPoint[k].y;
        }
        if (paramArrayOfPoint[k].y > this.top.y)
        {
          this.top.x = paramArrayOfPoint[k].x;
          this.top.y = paramArrayOfPoint[k].y;
        }
      }
      double d4 = (this.left.x + this.right.x + this.bottom.x + this.top.x + this.start.x + this.end.x) / 6;
      double d2 = (this.left.y + this.right.y + this.bottom.y + this.top.y + this.start.y + this.end.y) / 6;
      if ((abs(d4 - this.left.x) <= this.DEGREE) && (abs(d4 - this.right.x) <= this.DEGREE) && (abs(d4 - this.top.x) <= this.DEGREE) && (abs(d4 - this.bottom.x) <= this.DEGREE) && (abs(d4 - this.start.x) <= this.DEGREE) && (abs(d4 - this.end.x) <= this.DEGREE) && (this.start.y > this.end.y))
        i = 1;
      if ((abs(d4 - this.left.x) <= this.DEGREE) && (abs(d4 - this.right.x) <= this.DEGREE) && (abs(d4 - this.top.x) <= this.DEGREE) && (abs(d4 - this.bottom.x) <= this.DEGREE) && (abs(d4 - this.start.x) <= this.DEGREE) && (abs(d4 - this.end.x) <= this.DEGREE) && (this.start.y < this.end.y))
        i = 2;
      if ((abs(d2 - this.left.y) <= this.DEGREE) && (abs(d2 - this.right.y) <= this.DEGREE) && (abs(d2 - this.top.y) <= this.DEGREE) && (abs(d2 - this.bottom.y) <= this.DEGREE) && (abs(d2 - this.start.y) <= this.DEGREE) && (abs(d2 - this.end.y) <= this.DEGREE) && (this.start.x > this.end.x))
        i = 3;
      if ((abs(d2 - this.left.y) <= this.DEGREE) && (abs(d2 - this.right.y) <= this.DEGREE) && (abs(d2 - this.top.y) <= this.DEGREE) && (abs(d2 - this.bottom.y) <= this.DEGREE) && (abs(d2 - this.start.y) <= this.DEGREE) && (abs(d2 - this.end.y) <= this.DEGREE) && (this.start.x < this.end.x))
        i = 4;
      if ((abs(this.start.x - this.right.x) <= this.DEGREE) && (abs(this.start.y - this.bottom.y) <= this.DEGREE) && (abs(this.end.x - this.left.x) <= this.DEGREE) && (abs(this.end.y - this.top.y) <= this.DEGREE))
        i = 5;
      if ((abs(this.start.x - this.right.x) <= this.DEGREE) && (abs(this.start.y - this.top.y) <= this.DEGREE) && (abs(this.end.x - this.left.x) <= this.DEGREE) && (abs(this.end.y - this.bottom.y) <= this.DEGREE))
        i = 6;
      if ((abs(this.start.x - this.left.x) <= this.DEGREE) && (abs(this.start.y - this.bottom.y) <= this.DEGREE) && (abs(this.end.x - this.right.x) <= this.DEGREE) && (abs(this.end.y - this.top.y) <= this.DEGREE))
        i = 7;
      if ((abs(this.start.x - this.right.x) <= this.DEGREE) && (abs(this.start.y - this.top.y) <= this.DEGREE) && (abs(this.end.x - this.left.x) <= this.DEGREE) && (abs(this.end.y - this.bottom.y) <= this.DEGREE))
        i = 8;
      if ((abs(this.start.x - this.end.x) <= this.DEGREE) && (abs(this.start.y - this.end.y) <= this.DEGREE))
      {
        d1 = (this.left.x + this.right.x) / 2;
        d3 = (this.top.y + this.bottom.y) / 2;
        d5 = (this.right.x + this.bottom.y - this.left.x - this.top.y) / 4;
        l = 0;
      }
     /* while (true)
      {
        if ((l >= paramArrayOfPoint.length) || ((paramArrayOfPoint[l].x - d1) * (paramArrayOfPoint[l].x - d1) + (paramArrayOfPoint[l].y - d3) * (paramArrayOfPoint[l].y - d3) - d5 * d5 > this.DEGREE))
        {
          if (l != paramArrayOfPoint.length)
            j = 0;
          else
            j = 17;
          j = j;
          break label1807:
        }
        ++l;
      }*/
    }
    int j = -1;
    label1807: return j;
  }
}