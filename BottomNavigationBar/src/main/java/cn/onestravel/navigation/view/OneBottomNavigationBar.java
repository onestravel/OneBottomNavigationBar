package cn.onestravel.navigation.view;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.MenuRes;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.onestravel.navigation.R;
import cn.onestravel.navigation.utils.DensityUtils;

/**
 * @author onestravel
 * @createTime 2019/1/20 9:48 AM
 * @description 可以凸起的底部导航菜单VIEW
 */
public class OneBottomNavigationBar extends View {
    private String TAG = "BottomNavigationBar";
    // 导航菜单键列表
    private List<Item> itemList = new ArrayList<>();
    //总宽度 width
    private int mWidth = 0;
    //总高度 height
    private int mHeight = 0;
    //每个菜单的宽度 item width
    private int mItemWidth = 0;
    //每个菜单的告诉 item height
    private int mItemHeight = 0;
    //整体的上边距
    private int topPadding = DensityUtils.dpToPx(getResources(), 3);
    //整体下边距
    private int bottomPadding = DensityUtils.dpToPx(getResources(), 3);
    //文字相对于图标的边距
    private int textTop = DensityUtils.dpToPx(getResources(), 3);
    //画笔
    private Paint mPaint;
    //图标的状态颜色列表
    private ColorStateList itemIconTintRes;
    //文字的状态颜色列表
    private ColorStateList itemColorStateList;
    //Item菜单的选中事件
    private OnItemSelectedListener onItemSelectedListener;
    // 当前选中的坐标位置
    private int checkedPosition = 0;
    //是否开启上浮
    private boolean floatingEnable;
    //上浮距离
    private int floatingUpInit;
    private int floatingUp;
    //背景资源
    private Drawable background;
    //菜单的布局文件
    private @MenuRes
    int menuRes;
    private int titleSize;
    private int itemIconWidth;
    private int itemIconHeight;
    private Map<Integer, Fragment> fragmentMap;
    private FragmentManager manager;
    private View containerView;
    private Fragment currentFragment;
    private boolean isReplace;
    private Paint linePaint;
    private int topLineColor;

    public OneBottomNavigationBar(Context context) {
        super(context);
        init(context, null, 0);
    }

    public OneBottomNavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public OneBottomNavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    /**
     * 设置选中的监听事件
     *
     * @param onItemSelectedListener
     */
    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    /**
     * 设置选中
     *
     * @param position 选中位置
     */
    public void setSelected(int position) {
        Item item = itemList.get(position);
        if (item.checkable) {
            if (checkedPosition >= 0) {
                itemList.get(checkedPosition).checked = false;
            }
            item.checked = true;
            checkedPosition = position;
        }
        postInvalidate();
        if (onItemSelectedListener != null) {
            onItemSelectedListener.onItemSelected(item, position);
        }
        try {
            if (manager == null) {
                throw new RuntimeException("FragmentManager is null,please use setFragmentManager(getFragmentManager(),fragmentContainerView) in Activity");
            }
            if (containerView == null) {
                throw new RuntimeException("fragmentContainerView is null,please use setFragmentManager(getFragmentManager(),fragmentContainerView) set Fragment's ContainerView");
            }
            if (!(containerView instanceof ViewGroup)) {
                throw new RuntimeException("fragmentContainerView is not viewGroup ");
            }
            if (containerView.getId() == NO_ID) {
                throw new RuntimeException("fragmentContainerView not id");
            }
            Fragment fragment = fragmentMap.get(item.id);
            if (fragment == null) {
                throw new RuntimeException("[" + item.id + "] fragment is null ");
            }
            selectFragment(fragment);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 设置未读消息数
     *
     * @param position 未读消息数的位置
     * @param count    未读消息数量 <0 是显示为小红点，没有数字
     *                 == 0  时不显示未读消息红点
     *                 >0 && <100 时显示对应的消息数量
     *                 >=100 时显示 99+
     */
    public void setMsgCount(int position, int count) {
        if (position < itemList.size()) {
            itemList.get(position).msgCount = count;
            postInvalidate();
        }
    }


    /**
     * 设置Menu 菜单资源文件
     *
     * @param menuRes
     */
    public void setMenu(@MenuRes int menuRes) {
        this.menuRes = menuRes;
        parseXml(menuRes);
        format();
        postInvalidate();
    }

    /**
     * 设置Item 菜单的图标颜色状态列表
     *
     * @param resId 图标颜色状态的资源文件
     */
    public void setItemIconTint(@DrawableRes @ColorRes int resId) {
        this.itemIconTintRes = ResourcesCompat.getColorStateList(getResources(), resId, null);
        parseXml(menuRes);
        format();
        postInvalidate();
    }

    /**
     * 设置Item 菜单的文字颜色状态列表
     *
     * @param resId 文字颜色状态的资源文件
     */
    public void setItemColorStateList(@DrawableRes @ColorRes int resId) {
        this.itemColorStateList = ResourcesCompat.getColorStateList(getResources(), resId, null);
        postInvalidate();
    }


    /**
     * 设置分割线颜色
     * @param color
     */
    public void setTopLineColor(@ColorInt int color) {
        this.topLineColor = color;
    }


    /**
     * 设置分割线颜色
     * @param colorRes
     */
    public void setTopLineColorRes(@ColorRes int colorRes) {
        this.topLineColor = getResources().getColor(colorRes);
    }



    /**
     * 设置是否开启浮动
     *
     * @param floatingEnable
     */
    public void setFloatingEnable(boolean floatingEnable) {
        this.floatingEnable = floatingEnable;
        postInvalidate();
    }

    /**
     * 设置上浮距离，不能超过导航栏高度的1/2
     *
     * @param floatingUp
     */
    public void setFloatingUp(int floatingUp) {
        this.floatingUp = floatingUp;
        postInvalidate();
    }

    /**
     * 是否替换Fragment,替换后Fragment 数据会清空
     *
     * @return
     */
    public boolean isReplace() {
        return isReplace;
    }

    /**
     * 设置是否替换Fragment,替换后Fragment 数据会清空
     *
     * @param replace
     */
    public void setReplace(boolean replace) {
        isReplace = replace;
    }

    /**
     * 设置FragmentManager，管理fragment
     *
     * @param fragmentManager       fragment管理
     * @param fragmentContainerView fragment 将要添加的view
     */
    public void setFragmentManager(FragmentManager fragmentManager, View fragmentContainerView) {
        this.manager = fragmentManager;
        this.containerView = fragmentContainerView;
    }

    /**
     * 添加Fragment
     *
     * @param tabId
     * @param fragment
     */
    public void addFragment(@IdRes int tabId, Fragment fragment) {
        fragmentMap.put(tabId, fragment);
    }

    /**
     * 获取布局参数
     *
     * @return
     */
    @Override
    public ViewGroup.LayoutParams getLayoutParams() {
        ViewGroup.LayoutParams params = super.getLayoutParams();
        return params;
    }

    /**
     * 设置图标大小
     *
     * @param itemIconWidth
     * @param itemIconHeight
     */
    public void setItemIconSize(int itemIconWidth, int itemIconHeight) {
        this.itemIconWidth = itemIconWidth;
        this.itemIconHeight = itemIconHeight;
        format();
        postInvalidate();
    }

    /**
     * 设置Title文字大小
     *
     * @param titleSize
     */
    public void setTitleSize(int titleSize) {
        this.titleSize = titleSize;
        format();
        postInvalidate();
    }

    /**
     * 设置图标和title文字间距
     *
     * @param textTopMargin
     */
    public void setTextTopMargin(int textTopMargin) {
        this.textTop = textTopMargin;
        postInvalidate();
    }

    /**
     * 设置布局参数
     *
     * @param params
     */
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
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.One_StyleBottomLayout);
            itemIconTintRes = ta.getColorStateList(R.styleable.One_StyleBottomLayout_oneItemIconTint);
            itemColorStateList = ta.getColorStateList(R.styleable.One_StyleBottomLayout_oneItemTextColor);
            if (itemIconTintRes == null) {
                itemIconTintRes = ResourcesCompat.getColorStateList(getResources(), R.drawable.default_blue_tab_tint, null);
            }
            if (itemColorStateList == null) {
                itemColorStateList = ResourcesCompat.getColorStateList(getResources(), R.drawable.default_blue_tab_tint, null);
            }
            topLineColor = ta.getColor(R.styleable.One_StyleBottomLayout_oneItemTopLineColor,Color.parseColor("#CCCCCC"));
            floatingEnable = ta.getBoolean(R.styleable.One_StyleBottomLayout_oneFloatingEnable, false);
            floatingUp = floatingUpInit = (int) ta.getDimension(R.styleable.One_StyleBottomLayout_oneFloatingUp, 0);
            titleSize = (int) ta.getDimension(R.styleable.One_StyleBottomLayout_oneItemTextSize, DensityUtils.spToPx(getResources(), 14));
            textTop = (int) ta.getDimension(R.styleable.One_StyleBottomLayout_oneItemTextTopMargin, DensityUtils.dpToPx(getResources(), 3));
            itemIconWidth = (int) ta.getDimension(R.styleable.One_StyleBottomLayout_oneItemIconWidth, 0);
            itemIconHeight = (int) ta.getDimension(R.styleable.One_StyleBottomLayout_oneItemIconHeight, 0);
            int xmlRes = ta.getResourceId(R.styleable.One_StyleBottomLayout_oneMenu, 0);
            parseXml(xmlRes);
        }
        fragmentMap = new HashMap<>();
        format();
    }

    /**
     * 处理数据
     */
    private void format() {
        if (itemList.size() > 5) {
            itemList = itemList.subList(0, 5);
        }
        if (getBackground() != null && getBackground() instanceof ColorDrawable) {
            background = getBackground();
        } else if (getBackground() instanceof StateListDrawable) {
            background = getBackground();
        } else if (getBackground() instanceof GradientDrawable) {
            background = getBackground();
        } else {
            background = new ColorDrawable(Color.WHITE);
        }
        for (Item item : itemList) {
            item.titleSize = titleSize;
            item.iconWidth = itemIconWidth;
            item.iconHeight = itemIconHeight;
        }
        linePaint = createPaint(topLineColor);
        linePaint.setStrokeWidth(DensityUtils.dpToPx(getContext(), 1));
    }

    /**
     * 解析 menu 的 xml 的文件，得到相关的 导航栏菜单
     *
     * @param xmlRes
     */
    private void parseXml(int xmlRes) {
        try {
            if (xmlRes == 0) {
                return;
            }
            itemList.clear();
            XmlResourceParser xmlParser = getResources().getXml(xmlRes);
            int event = xmlParser.getEventType();   //先获取当前解析器光标在哪
            while (event != XmlPullParser.END_DOCUMENT) {    //如果还没到文档的结束标志，那么就继续往下处理
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        Log.e(TAG, "xml解析开始");
                        break;
                    case XmlPullParser.START_TAG:
                        //一般都是获取标签的属性值，所以在这里数据你需要的数据
//                        Log.e(TAG, "当前标签是：" + xmlParser.getName());
                        if (xmlParser.getName().equals("item")) {
                            Item item = new Item();
                            for (int i = 0; i < xmlParser.getAttributeCount(); i++) {
                                //两种方法获取属性值
//                                Log.e(TAG, "第" + (i + 1) + "个属性：" + xmlParser.getAttributeName(i)
//                                        + ": " + xmlParser.getAttributeValue(i));
                                if ("id".equalsIgnoreCase(xmlParser.getAttributeName(i))) {
                                    item.id = xmlParser.getAttributeResourceValue(i, 0);
                                } else if ("icon".equalsIgnoreCase(xmlParser.getAttributeName(i))) {
                                    int drawableId = xmlParser.getAttributeResourceValue(i, 0);
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
                                        stateListDrawable.addState(new int[]{android.R.attr.state_selected}, selectedDrawable.getCurrent());
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
                            if (item.checkable && item.checked) {
                                checkedPosition = itemList.size();
                            }
                            itemList.add(item);
                        }
                        break;
                    case XmlPullParser.TEXT:
//                        Log.e(TAG, "Text:" + xmlParser.getText());
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


    /**
     * 当初始化布局以后，进行默认选中
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setSelected(checkedPosition);
    }

    /**
     * 尺寸测量
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mItemWidth = (mWidth - getPaddingLeft() - getPaddingRight()) / itemList.size();
        topPadding = getPaddingTop();
        bottomPadding = getPaddingBottom();
        if (specMode == MeasureSpec.AT_MOST) {
            createTextPaint(titleSize, Color.BLACK);
            int iconHeight = itemIconHeight > 50 ? itemIconHeight : 50;
            int textHeight = getTextHeight("首页", mPaint);
            mHeight = topPadding + bottomPadding + iconHeight + textHeight + textTop;
        } else {
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
        }
        mHeight += floatingUpInit;
        topPadding = getPaddingTop() + floatingUpInit;
        mItemHeight = mHeight;
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mHeight, specMode));
    }


    /**
     * 进行绘制
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!floatingEnable) {
            floatingUp = 0;
        } else {
            floatingUp = floatingUpInit;
        }
        Bitmap bitmap = drawable2Bitmap(background);
        canvas.drawBitmap(bitmap, 0, floatingUpInit, mPaint);
        Rect rectInit = new Rect();
        rectInit.set(0, floatingUpInit, mWidth, mHeight);
        canvas.drawLine(0, floatingUpInit, mWidth, floatingUpInit, linePaint);
        //画背景
        drawFloating(canvas);
        background.setBounds(rectInit);
        background.draw(canvas);

        //画Floating
        //画出所有导航菜单
        if (itemList.size() > 0) {
            for (int i = 0; i < itemList.size(); i++) {
                Item item = itemList.get(i);
                drawItem(canvas, item, i);
            }
        }

    }

    /**
     * 画出上浮图标的背景
     *
     * @param canvas
     */
    private void drawFloating(Canvas canvas) {
        if (!floatingEnable) {
            return;
        }
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
                    linePaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(x, y, r + DensityUtils.dpToPx(getContext(), 1)/2, linePaint);
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(x, y, r, paint);
                }
            }
        }
    }

    /**
     * 画出每一个Item导航菜单
     *
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
            startTop = topPadding;
        } else {
            startTop = topPadding - floatingUp;
            width = width + floatingUp;
            height = height + floatingUp;
        }
        if (!TextUtils.isEmpty(item.title)) {
            int color = item.checked ? itemColorStateList.getColorForState(new int[]{android.R.attr.state_checked}, itemColorStateList.getDefaultColor()) : itemColorStateList.getDefaultColor();
            if (!item.checkable) {
                color = itemColorStateList.getColorForState(new int[]{android.R.attr.state_checked}, itemColorStateList.getDefaultColor());
            }
            createTextPaint(item.titleSize == 0 ? DensityUtils.dpToPx(getResources(), 14) : item.titleSize, color);
            int textHeight = getTextHeight(item.title, mPaint);
            int textY = startTop + height - textHeight / 4;//上边距+图片文字内容高度
            width = height = height - textHeight - textTop;
            canvas.drawText(item.title, position * mItemWidth + getPaddingLeft() + mItemWidth / 2, textY, mPaint);
        }
        if (item.icon != null) {
            Rect to = new Rect();
            to.left = getPaddingLeft() + position * mItemWidth + (mItemWidth - width) / 2;
            to.top = startTop;
            to.right = to.left + width;
            to.bottom = topPadding + height;
            if (item.floating) {
                to.bottom = (int) (topPadding + height - floatingUp);
            }
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
                createTextPaint(DensityUtils.dpToPx(getResources(), 9), Color.WHITE);
                r = getTextWidth("99+", mPaint) / 2 + 1;
                String count = "";
                if (item.msgCount > 99) {
                    count = "99+";
                    createTextPaint(DensityUtils.dpToPx(getResources(), 8), Color.WHITE);
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


    /**
     * 创建文字类型的画笔
     *
     * @param textSize  文字大小
     * @param textColor 文字颜色
     * @return
     */
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

    /**
     * 创建图形的画笔
     *
     * @param color 画笔颜色
     * @return
     */
    private Paint createPaint(int color) {
        Paint mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);//设置抗锯齿功能 true表示抗锯齿 false则表示不需要这功能
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setStyle(Paint.Style.FILL);
        return mPaint;
    }


    /**
     * 获取文字宽度
     *
     * @param text  文字
     * @param paint 画笔
     * @return
     */
    private int getTextWidth(String text, Paint paint) {
        Rect rect = new Rect(); // 文字所在区域的矩形
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.width();
    }

    /**
     * 获取文字高度
     *
     * @param text  文字
     * @param paint 画笔
     * @return
     */
    private int getTextHeight(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
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

    /**
     * 触摸事件监听
     *
     * @param event
     * @return
     */
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
        Log.e(TAG, "action = " + action);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                return true;
//                break;
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

    /**
     * 选中监听事件的接口
     */
    public interface OnItemSelectedListener {
        void onItemSelected(Item item, int position);
    }

    /**
     * 导航菜单Item 的实体
     */
    public class Item {
        private int id;
        private StateListDrawable icon;
        private Drawable drawable;
        private String title;
        private int titleSize;
        private int iconWidth;
        private int iconHeight;
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


    Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            Bitmap bitmap;
            if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    /**
     * 改变Fragment
     *
     * @param to
     */
    private void selectFragment(Fragment to) {
        if (!isReplace) {
            hiddenFragment(currentFragment, to);
        } else {
            replaceFragment(currentFragment, to);
        }

    }

    /**
     * 隐藏Fragment
     *
     * @param from
     * @param to
     */
    private void hiddenFragment(Fragment from, Fragment to) {
        FragmentTransaction transaction = manager.beginTransaction();
        if (from != null && from != to) {
            if (to.isAdded()) {
                transaction.hide(from).show(to);
            } else {
                transaction.add(containerView.getId(), to).hide(from).show(to);
            }
        } else {
            transaction.replace(containerView.getId(), to);
        }
        transaction.commit();
        currentFragment = to;
    }

    /**
     * 替换Fragment
     *
     * @param from
     * @param to
     */
    private void replaceFragment(Fragment from, Fragment to) {
        FragmentTransaction transaction = manager.beginTransaction();
        if (from != null && from != to) {
            transaction.remove(from);
            transaction.replace(containerView.getId(), to);
        } else {
            transaction.replace(containerView.getId(), to);
        }
        transaction.commit();
        currentFragment = to;
    }
}
