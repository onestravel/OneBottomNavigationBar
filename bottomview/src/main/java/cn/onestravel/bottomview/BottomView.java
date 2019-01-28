package cn.onestravel.bottomview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.onestravel.bottomview.utils.DensityUtils;
import cn.onestravel.bottomview.utils.EventUtils;

/**
 * @author onestravel
 * @createTime 2019/1/20 9:48 AM
 * @description TODO
 */
public class BottomView extends View {
    private String TAG = "BottomView";
    private int drawableId = 0;
    private List<Item> itemList = new ArrayList<>();
    private int mWidth = 0;
    private int mHeight = 0;
    private int mItemWidth = 0;
    private int mItemHeight = 0;
    private int topPadding = DensityUtils.dpToPx(getResources(), 3);
    private int bottomPadding = DensityUtils.dpToPx(getResources(), 3);
    private int textTop = DensityUtils.dpToPx(getResources(), 3);
    private Paint mPaint;
    private int itemTextColorRes;
    private ColorStateList itemIconTintRes;
    private ColorStateList itemColorStateList;
    private OnItemSelectedListener onItemSelectedListener;
    private int checkedPosition = -1;

    public BottomView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public BottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public BottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StyleBottomLayout);
            int xmlRes = ta.getResourceId(R.styleable.StyleBottomLayout_xml, 0);
            parseXml(xmlRes);
            itemIconTintRes = ta.getColorStateList(R.styleable.StyleBottomLayout_itemIconTint);
            itemColorStateList = ta.getColorStateList(R.styleable.StyleBottomLayout_itemTextColor);
        }
    }

    private void parseXml(int xmlRes) {
        XmlResourceParser xmlParser = getResources().getXml(xmlRes);
        try {
            int event = xmlParser.getEventType();   //先获取当前解析器光标在哪
            while (event != XmlPullParser.END_DOCUMENT) {    //如果还没到文档的结束标志，那么就继续往下处理
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        Log.e(TAG, "xml解析开始");
                        break;
                    case XmlPullParser.START_TAG:
                        //一般都是获取标签的属性值，所以在这里数据你需要的数据
                        Log.e(TAG, "当前标签是：" + xmlParser.getName());
                        if (xmlParser.getName().equals("item")) {
                            Item item = new Item();
                            for (int i = 0; i < xmlParser.getAttributeCount(); i++) {
                                //两种方法获取属性值
                                Log.e(TAG, "第" + (i + 1) + "个属性：" + xmlParser.getAttributeName(i)
                                        + ": " + xmlParser.getAttributeValue(i));
                                if ("id".equalsIgnoreCase(xmlParser.getAttributeName(i))) {
                                    item.id = xmlParser.getAttributeResourceValue(i, 0);
                                } else if ("icon".equalsIgnoreCase(xmlParser.getAttributeName(i))) {
                                    drawableId = xmlParser.getAttributeResourceValue(i, 0);
//                                    Drawable drawable = ResourcesCompat.getDrawable(getResources(),drawableId,null);
                                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), drawableId);
                                    item.icon = bmp;
                                } else if ("title".equalsIgnoreCase(xmlParser.getAttributeName(i))) {
                                    item.title = xmlParser.getAttributeValue(i);
                                } else if ("floating".equalsIgnoreCase(xmlParser.getAttributeName(i))) {
                                    item.floating = xmlParser.getAttributeBooleanValue(i, false);
                                } else if ("checked".equalsIgnoreCase(xmlParser.getAttributeName(i))) {
                                    item.checked = xmlParser.getAttributeBooleanValue(i, false);
                                }
                            }
                            itemList.add(item);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        Log.e(TAG, "Text:" + xmlParser.getText());
                        break;
                    case XmlPullParser.END_TAG:
                        Log.e(TAG, "xml解析结束");
                        break;
                    default:
                        break;
                }
                event = xmlParser.next();   //将当前解析器光标往下一步移
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mItemWidth = (mWidth - getPaddingLeft() - getPaddingRight()) / itemList.size();
        mItemHeight = mHeight > mItemWidth ? mItemWidth : mHeight;
        topPadding = getPaddingTop();
        bottomPadding = getPaddingBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (itemList.size() > 0) {
            for (int i = 0; i < itemList.size(); i++) {
                Item item = itemList.get(i);
                drawItem(canvas, item, i);
            }
        }

    }

    /**
     * @param canvas
     * @param item
     */
    private void drawItem(Canvas canvas, Item item, int position) {
        if (item == null) {
            return;
        }
        int width = mItemHeight - topPadding - bottomPadding;
        int height = mItemHeight - topPadding - bottomPadding;
        if (!TextUtils.isEmpty(item.title)) {
            int color = item.checked ? itemColorStateList.getColorForState(new int[]{android.R.attr.state_checked}, itemColorStateList.getDefaultColor()) : itemColorStateList.getDefaultColor();
            createPaint(item.titleSize == 0 ? DensityUtils.dpToPx(getResources(), 14) : item.titleSize, color);
            int textHeight = getTextHeight(item.title, mPaint);
            int textY = mItemHeight - bottomPadding - textHeight / 4;
            int w = textY - textHeight / 2 - topPadding;
            width = w > width ? width : w;
            width = height = width - textTop;
            canvas.drawText(item.title, position * mItemWidth + getPaddingLeft() + mItemWidth / 2, textY, mPaint);
        }
        if (item.icon != null) {
            Bitmap bitmap = item.icon;
            Rect src = new Rect();
            src.left = 0;
            src.top = 0;
            src.right = item.icon.getWidth();
            src.bottom = item.icon.getHeight();
            Rect to = new Rect();
            to.left = getPaddingLeft() + position * mItemWidth + (mItemWidth - width) / 2;
            to.top = topPadding;
            to.right = to.left + width;
            to.bottom = topPadding + height;
            Paint paint = new Paint();

            if (itemIconTintRes != null) {
                int pColor = item.checked ? itemIconTintRes.getColorForState(new int[]{android.R.attr.state_checked}, itemIconTintRes.getDefaultColor()) : itemIconTintRes.getDefaultColor();
                paint.setColorFilter(new PorterDuffColorFilter(pColor, PorterDuff.Mode.MULTIPLY));
            }
            canvas.drawBitmap(item.icon, src, to, paint);
        }
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public static Bitmap tintBitmap(Bitmap inBitmap, int tintColor) {
        if (inBitmap == null) {
            return null;
        }
        Bitmap outBitmap = Bitmap.createBitmap(inBitmap.getWidth(), inBitmap.getHeight(), inBitmap.getConfig());
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint();
//        paint.setColorFilter( new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN)) ;
//        canvas.drawBitmap(inBitmap , 0, 0, paint) ;
        //从原位图中提取只包含alpha的位图
        Bitmap alphaBitmap = inBitmap.extractAlpha();
        //在画布上（mAlphaBitmap）绘制alpha位图
        canvas.drawBitmap(alphaBitmap, 0, 0, paint);

        return outBitmap;
    }

    private Paint createPaint(int textSize, int textColor) {
        if (mPaint == null) {
            mPaint = new Paint();
        }
        mPaint.setColor(textColor);//设置画笔的颜色
        mPaint.setTextSize(textSize);//设置文字大小
//        mPaint.setStrokeWidth(2);//设置画笔的宽度
        mPaint.setAntiAlias(true);//设置抗锯齿功能 true表示抗锯齿 false则表示不需要这功能
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        return mPaint;
    }


    private int getTextWidth(String text, Paint paint) {
        Rect rect = new Rect(); // 文字所在区域的矩形
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.width();
    }

    private int getTextHeight(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        double x = (double) event.getRawX();
        double y = (double) event.getRawY();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                for (int i = 0; i < itemList.size(); i++) {
                    if (x > getPaddingLeft() + mItemWidth * i && x < getPaddingLeft() + mItemWidth * (i + 1)) {
                        if (itemList.get(i).checkable) {
                            if (checkedPosition >= 0) {
                                itemList.get(checkedPosition).checked = false;
                            }
                            itemList.get(i).checked = true;
                            checkedPosition = i;
                        }
                        if (onItemSelectedListener != null) {
                            onItemSelectedListener.onItemSelected(itemList.get(i), i);
                        }
                    }
                }
                postInvalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    public interface OnItemSelectedListener {
        void onItemSelected(Item item, int position);
    }

    public class Item {
        private int id;
        private Bitmap icon;
        private String title;
        private int titleSize;
        private boolean floating = false;
        private boolean checked = false;
        private boolean checkable = true;
    }
}
