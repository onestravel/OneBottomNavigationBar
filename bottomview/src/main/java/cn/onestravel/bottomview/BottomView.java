package cn.onestravel.bottomview;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
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
                                        int drawableId = xmlParser.getAttributeResourceValue(i, 0);
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
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mItemWidth = (mWidth - getPaddingLeft() - getPaddingRight()) / itemList.size();
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
        int width = DensityUtils.dpToPx(getResources(), 35);
        int height = DensityUtils.dpToPx(getResources(), 35);
        if (item.icon != null) {
            Rect src = new Rect();
            src.left = 0;
            src.top = 0;
            src.right = item.icon.getWidth();
            src.bottom = item.icon.getHeight();
            Rect to = new Rect();
            to.left = getPaddingLeft()+position * mItemWidth + (mItemWidth - width) / 2;
            to.top = topPadding;
            to.right = to.left + width;
            to.bottom = topPadding + height;
            canvas.drawBitmap(item.icon, src, to, new Paint());
        }else if(!TextUtils.isEmpty(item.title)){

        }
    }


    private class Item {
        private int id;
        private Bitmap icon;
        private String title;
        private int titleSize;
        private boolean floating = false;
        private boolean checked = false;
    }
}
