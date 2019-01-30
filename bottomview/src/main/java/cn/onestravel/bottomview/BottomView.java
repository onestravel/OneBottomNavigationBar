package cn.onestravel.bottomview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.onestravel.bottomview.utils.DensityUtils;

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
    private ColorStateList itemIconTintRes;
    private ColorStateList itemColorStateList;
    private OnItemSelectedListener onItemSelectedListener;
    private int checkedPosition = 0;
    private int floatingUp;
    private Drawable background;
    private boolean floatingEnable;

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


    /**
     * 初始化，获取该View的自定义属性，以及item 列表
     *
     * @param context      上下文
     * @param attrs        属性
     * @param defStyleAttr 默认样式
     */
    @SuppressLint("ResourceType")
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StyleBottomLayout);
            itemIconTintRes = ta.getColorStateList(R.styleable.StyleBottomLayout_itemIconTint);
            itemColorStateList = ta.getColorStateList(R.styleable.StyleBottomLayout_itemTextColor);
            if (itemIconTintRes == null) {
                itemIconTintRes = ResourcesCompat.getColorStateList(getResources(), R.drawable.default_blue_tab_tint, null);
            }
            if (itemColorStateList == null) {
                itemColorStateList = ResourcesCompat.getColorStateList(getResources(), R.drawable.default_blue_tab_tint, null);
            }
            floatingEnable = ta.getBoolean(R.styleable.StyleBottomLayout_floatingEnable, false);
            if (floatingEnable) {
                floatingUp = (int) ta.getDimension(R.styleable.StyleBottomLayout_floatingUp, 0);
            }
            int xmlRes = ta.getResourceId(R.styleable.StyleBottomLayout_menu, 0);
            parseXml(xmlRes);
        }
        if (itemList.size() > 5) {
            itemList = itemList.subList(0, 5);
        }
        if (getBackground() != null && getBackground() instanceof ColorDrawable) {
            background = getBackground();
        } else {
            background = new ColorDrawable(Color.WHITE);
        }
    }

    /**
     * 解析 menu 的 xml 的文件，得到相关的 导航栏菜单
     *
     * @param xmlRes
     */
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
                                    Drawable drawable = ResourcesCompat.getDrawable(getResources(), drawableId, null);
                                    item.drawable = drawable.getConstantState().newDrawable();
                                    StateListDrawable stateListDrawable = new StateListDrawable();
                                    if (drawable instanceof StateListDrawable) {
                                        stateListDrawable = (StateListDrawable) drawable;
                                        stateListDrawable.setState(new int[]{android.R.attr.state_checked});
                                        stateListDrawable.mutate();
                                    } else {
                                        Drawable selectedDrawable = tintListDrawable(drawable, itemIconTintRes);
                                        selectedDrawable.setState(new int[]{android.R.attr.state_checked});
                                        stateListDrawable.addState(new int[]{android.R.attr.state_checked}, selectedDrawable.getCurrent());
                                        stateListDrawable.addState(new int[]{android.R.attr.state_selected},selectedDrawable.getCurrent());
                                        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, selectedDrawable.getCurrent());
                                        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, selectedDrawable.getCurrent());
                                        selectedDrawable.setState(new int[]{});
                                        stateListDrawable.addState(new int[]{}, selectedDrawable.getCurrent());
                                    }
                                    item.icon = stateListDrawable;
                                } else if ("title".equalsIgnoreCase(xmlParser.getAttributeName(i))) {
                                    item.title = xmlParser.getAttributeValue(i);
                                } else if ("floating".equalsIgnoreCase(xmlParser.getAttributeName(i))) {
                                    item.floating = xmlParser.getAttributeBooleanValue(i, false);
                                } else if ("checked".equalsIgnoreCase(xmlParser.getAttributeName(i))) {
                                    item.checked = xmlParser.getAttributeBooleanValue(i, false);
                                } else if ("checkable".equalsIgnoreCase(xmlParser.getAttributeName(i))) {
                                    item.checkable = xmlParser.getAttributeBooleanValue(i, false);
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
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setSelected(checkedPosition);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mItemWidth = (mWidth - getPaddingLeft() - getPaddingRight()) / itemList.size();
        topPadding = getPaddingTop();
        bottomPadding = getPaddingBottom();
        mHeight += floatingUp;
        mItemHeight = mHeight > mItemWidth ? mItemWidth : mHeight;
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.getMode(heightMeasureSpec)));
    }


    @Override
    public ViewGroup.LayoutParams getLayoutParams() {
        ViewGroup.LayoutParams params = super.getLayoutParams();
        return params;
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        floatingUp = floatingUp > params.height / 2 ? params.height / 2 : floatingUp;
        if (params instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) params;
            layoutParams.topMargin = layoutParams.topMargin - floatingUp;
        } else if (params instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) params;
            layoutParams.topMargin = layoutParams.topMargin - floatingUp;
        } else if (params instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) params;
            layoutParams.topMargin = layoutParams.topMargin - floatingUp;
        }
        super.setLayoutParams(params);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画背景
        background.setBounds(0, floatingUp, mWidth, mHeight);
        background.draw(canvas);
        //画Floating
        drawFloating(canvas);
        if (itemList.size() > 0) {
            for (int i = 0; i < itemList.size(); i++) {
                Item item = itemList.get(i);
                drawItem(canvas, item, i);
            }
        }

    }

    private void drawFloating(Canvas canvas) {
        if (itemList.size() > 0) {
            for (int i = 0; i < itemList.size(); i++) {
                Item item = itemList.get(i);
                if (item.floating) {
                    int startTop = 0;
                    //图片文字内容宽度
                    int width = mItemHeight - topPadding - bottomPadding;
                    //图片文字内容高度
                    int height = mItemHeight - topPadding - bottomPadding;
                    startTop = topPadding;
                    if (!TextUtils.isEmpty(item.title)) {
                        int color = item.checked ? itemColorStateList.getColorForState(new int[]{android.R.attr.state_checked}, itemColorStateList.getDefaultColor()) : itemColorStateList.getDefaultColor();
                        createTextPaint(item.titleSize == 0 ? DensityUtils.dpToPx(getResources(), 14) : item.titleSize, color);
                        int textHeight = getTextHeight(item.title, mPaint);
                        int textY = startTop + height - textHeight / 4;//上边距+图片文字内容高度
                        int w = textY - textHeight / 2 - topPadding;
//                        width = height = height - textHeight - textTop;
                    }
                    int x = getPaddingLeft() + i * mItemWidth + (mItemWidth - width) / 2 + width / 2;
                    int y = mItemHeight / 2;
                    int r = mItemHeight / 2;
                    Paint paint = createPaint(Color.WHITE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        paint.setColorFilter(background.getColorFilter());
                    }
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(x, y, r, paint);
                }
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
        //图片文字内容宽度
        int width = mItemHeight - topPadding - bottomPadding;
        //图片文字内容高度
        int height = mItemHeight - topPadding - bottomPadding;
        int startTop = 0;
        if (!item.floating) {
            startTop = topPadding + floatingUp;
            width = width - floatingUp;
            height = height - floatingUp;
        } else {
            startTop = topPadding;
        }
        if (!TextUtils.isEmpty(item.title)) {
            int color = item.checked ? itemColorStateList.getColorForState(new int[]{android.R.attr.state_checked}, itemColorStateList.getDefaultColor()) : itemColorStateList.getDefaultColor();
            if (!item.checkable) {
                color = itemColorStateList.getColorForState(new int[]{android.R.attr.state_checked}, itemColorStateList.getDefaultColor());
            }
            createTextPaint(item.titleSize == 0 ? DensityUtils.dpToPx(getResources(), 14) : item.titleSize, color);
            int textHeight = getTextHeight(item.title, mPaint);
            int textY = startTop + height - textHeight / 4;//上边距+图片文字内容高度
            int w = textY - textHeight / 2 - topPadding;
            width = height = height - textHeight - textTop;
            canvas.drawText(item.title, position * mItemWidth + getPaddingLeft() + mItemWidth / 2, textY, mPaint);
        }
        if (item.icon != null) {
//            Bitmap bitmap = item.icon;
////            Rect src = new Rect();
////            src.left = 0;
////            src.top = 0;
////            src.right = item.icon.getWidth();
////            src.bottom = item.icon.getHeight();
            Rect to = new Rect();
            to.left = getPaddingLeft() + position * mItemWidth + (mItemWidth - width) / 2;
            to.top = startTop;
            to.right = to.left + width;
            to.bottom = topPadding + height;
            if (!item.floating) {
                to.bottom = (int) (topPadding + height + floatingUp);
            }
////            Paint paint = new Paint();
////
////            if (itemIconTintRes != null) {
////                int pColor = item.checked ? itemIconTintRes.getColorForState(new int[]{android.R.attr.state_checked}, itemIconTintRes.getDefaultColor()) : itemIconTintRes.getDefaultColor();
////                paint.setColorFilter(new PorterDuffColorFilter(pColor, PorterDuff.Mode.MULTIPLY));
////            }
////            canvas.drawBitmap(item.icon, src, to, paint);
            Drawable drawable;
            if (item.checkable) {
                if (item.checked) {
                    item.icon.setState(new int[]{android.R.attr.state_checked});
                    drawable = item.icon.getCurrent();
                } else {
                    item.icon.setState(new int[]{});
                    drawable = item.icon.getCurrent();
                }
            } else {
                drawable = item.drawable;
            }
            drawable.setBounds(to);
            drawable.draw(canvas);
        }
        if (item.msgCount != 0) {
            int x = 0;
            int y = 0;
            int r = 0;
            if (item.msgCount > 0) {
                createTextPaint(item.titleSize == 0 ? DensityUtils.dpToPx(getResources(), 9) : item.titleSize, Color.WHITE);
                r = getTextWidth("99+", mPaint) / 2 + 1;
                String count = "";
                if (item.msgCount > 99) {
                    count = "99+";
                    createTextPaint(item.titleSize == 0 ? DensityUtils.dpToPx(getResources(), 8) : item.titleSize, Color.WHITE);
                } else {
                    count = String.valueOf(item.msgCount);
                }
                x = getPaddingLeft() + position * mItemWidth + (mItemWidth - width) / 2 + width - r / 4;
                y = startTop + r - r / 3;
                Paint paint = createPaint(Color.RED);
                canvas.drawCircle(x, y, r, paint);
                canvas.drawText(count, x, y + r / 2, mPaint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth(DensityUtils.dpToPx(getResources(), 1));
                canvas.drawCircle(x, y, r, paint);
            } else {
                r = 9;
                x = getPaddingLeft() + position * mItemWidth + (mItemWidth - width) / 2 + width - r;
                y = startTop + r;
                Paint paint = createPaint(Color.RED);
                canvas.drawCircle(x, y, r, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth(DensityUtils.dpToPx(getResources(), 1));
                canvas.drawCircle(x, y, r, paint);
            }

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

    private Paint createTextPaint(int textSize, int textColor) {
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

    private Paint createPaint(int color) {
        Paint mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);//设置抗锯齿功能 true表示抗锯齿 false则表示不需要这功能
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setStyle(Paint.Style.FILL);
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

    /**
     * 更改图片颜色
     *
     * @param drawable
     * @param color
     * @return
     */
    public Drawable tintDrawable(Drawable drawable, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
//        DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.MULTIPLY);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }


    /**
     * 更改图片颜色
     *
     * @param drawable
     * @param colors
     * @return
     */
    public Drawable tintListDrawable(Drawable drawable, ColorStateList colors) {
        Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.MULTIPLY);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        double x = (double) event.getRawX();
        double y = (double) event.getRawY();
        //获取控件在屏幕的位置
        int[] location = new int[2];
        getLocationOnScreen(location);
        int locationY = location[1];
        y = y - locationY;
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                for (int i = 0; i < itemList.size(); i++) {
                    Item item = itemList.get(i);
                    int startTop = 0;
                    if (!item.floating) {
                        startTop = getPaddingTop() + floatingUp;
                    }
                    if (x > getPaddingLeft() + mItemWidth * i && x < getPaddingLeft() + mItemWidth * (i + 1)) {
                        //图片文字内容宽度
                        int width = mItemHeight - topPadding - bottomPadding;
                        //图片文字内容高度
                        int height = mItemHeight - topPadding - bottomPadding;
                        if (!TextUtils.isEmpty(item.title)) {
                            int color = item.checked ? itemColorStateList.getColorForState(new int[]{android.R.attr.state_checked}, itemColorStateList.getDefaultColor()) : itemColorStateList.getDefaultColor();
                            createTextPaint(item.titleSize == 0 ? DensityUtils.dpToPx(getResources(), 14) : item.titleSize, color);
                            int textHeight = getTextHeight(item.title, mPaint);
                            int textY = startTop + height - textHeight / 4;//上边距+图片文字内容高度
                            int w = textY - textHeight / 2 - topPadding;
//                        width = height = height - textHeight - textTop;
                        }
                        int centerX = getPaddingLeft() + i * mItemWidth + (mItemWidth - width) / 2 + width / 2;
                        int centerY = mItemHeight / 2;
                        int r = mItemHeight / 2;
                        if (y >= floatingUp || (item.floating && isInCircle(centerX, centerY, r, (int) x, (int) y))) {
                            setSelected(i);
                        }
                    }
                }
                postInvalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setSelected(int position) {
        Item item = itemList.get(position);
        if (item.checkable) {
            if (checkedPosition >= 0) {
                itemList.get(checkedPosition).checked = false;
            }
            item.checked = true;
            checkedPosition = position;
        }
        if (onItemSelectedListener != null) {
            onItemSelectedListener.onItemSelected(itemList.get(position), position);
        }
    }

    public void setMsgCount(int position, int count) {
        if (position < itemList.size()) {
            itemList.get(position).msgCount = count;
            postInvalidate();
        }
    }


    /**
     * 判断触摸位置是否在圆形内部
     *
     * @param vCenterX 圆形的 X 坐标
     * @param vCenterY 圆形的 Y 坐标
     * @param r        圆形的半径
     * @param touchX   触摸位置的 X 坐标
     * @param touchY   触摸位置的 Y 坐标
     * @return
     */
    private boolean isInCircle(int vCenterX, int vCenterY, int r, int touchX, int touchY) {
        //点击位置x坐标与圆心的x坐标的距离
        int distanceX = Math.abs(vCenterX - touchX);
        //点击位置y坐标与圆心的y坐标的距离
        int distanceY = Math.abs(vCenterY - touchY);
        //点击位置与圆心的直线距离
        int distanceZ = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

        //如果点击位置与圆心的距离大于圆的半径，证明点击位置没有在圆内
        if (distanceZ > r) {
            return false;
        }
        return true;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(Item item, int position);
    }

    public class Item {
        private int id;
        private StateListDrawable icon;
        private Drawable drawable;
        private String title;
        private int titleSize;
        private boolean floating = false;
        private boolean checked = false;
        private boolean checkable = true;
        private int msgCount = 0;

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public boolean isFloating() {
            return floating;
        }

        public boolean isChecked() {
            return checked;
        }

        public boolean isCheckable() {
            return checkable;
        }

        public int getMsgCount() {
            return msgCount;
        }
    }
}
