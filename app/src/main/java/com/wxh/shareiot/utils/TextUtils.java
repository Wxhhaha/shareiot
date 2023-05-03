package com.wxh.shareiot.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.wxh.shareiot.R;


/**
 * 创建时间:2021/9/25 14:42
 * 作者:wxh
 */
public class TextUtils {

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {

        private SpannableStringBuilder strBuilder;

        private Builder() {
            strBuilder = new SpannableStringBuilder();
        }

        public Builder append(CharSequence text) {
            strBuilder.append(text);
            return this;
        }

        public Builder append(CharSequence text, int color) {

            int start = strBuilder.length();
            strBuilder.append(text);
            int end = strBuilder.length();
            strBuilder.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public Builder replace(CharSequence text, int color, String... replaces) {

            strBuilder.append(text);
            for (int i = 0; i < replaces.length; i++) {
                String replace = replaces[i];
                int start = text.toString().indexOf(replace);
                if (start >= 0) {
                    int end = start + replace.length();
                    strBuilder.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            return this;
        }

        public Builder click(CharSequence text, final int color, final OnClickListener onClickListener,
                             String... clickTexts) {

            strBuilder.append(text);

            for (int i = 0; i < clickTexts.length; i++) {

                String clickText = clickTexts[i];
                final int postion = i;
                int start = text.toString().indexOf(clickText);
                if (start >= 0) {
                    int end = start + clickText.length();
                    strBuilder.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            if (onClickListener != null) {
                                onClickListener.onClick(postion);
                            }
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setColor(color);
                            ds.setUnderlineText(false);
                        }
                    }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            return this;
        }

        private boolean isChecked = false;

        //设置复选框 因为该方法没有调strBuilder.append()&#xff0c;故请务必在调用该方法前保证strBuilder不为空&#xff0c;即调用了前面的方法
        public Builder checkBox(Context context, TextView tv, OnImageClickListener listener) {
            setImageSpan(context, strBuilder, R.mipmap.ic_unchecked);
            strBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    isChecked = !isChecked;
                    if (isChecked) {
                        setImageSpan(context, strBuilder, R.mipmap.ic_checked);
                        tv.setText(strBuilder);//刷新显示
                        listener.onChecked();
                    } else {
                        setImageSpan(context, strBuilder, R.mipmap.ic_unchecked);
                        tv.setText(strBuilder);
                        listener.onUnChecked();
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.WHITE);
                    ds.setUnderlineText(false);
                }
            }, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return this;
        }

        public Builder clickInto(TextView tv) {
            tv.setMovementMethod(LinkMovementMethod.getInstance());//设置可点击状态
            tv.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明
            tv.setText(strBuilder);
            return this;
        }

        public Builder into(TextView tv) {
            tv.setText(strBuilder);
            return this;
        }
    }

    public interface OnClickListener {
        void onClick(int position);
    }

    public interface OnImageClickListener {
        void onChecked();

        void onUnChecked();
    }

    private static void setImageSpan(Context context, SpannableStringBuilder builder, int resourceId) {
        MyImageSpan imageSpan = new MyImageSpan(context, resourceId, 2);//居中对齐
        builder.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static class MyImageSpan extends ImageSpan {
        //因为这里文字存在换行,系统的ImageSpan图标无法进行居中&#xff0c;所以我们自定义一个ImageSpan,重写draw方法,解决了该问题
        public MyImageSpan(Context context, int resourceId, int verticalAlignment) {
            super(context, resourceId, verticalAlignment);
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom,
                         Paint paint) {
            Drawable drawable = getDrawable();
            canvas.save();
            //获取画笔的文字绘制时的具体测量数据
            Paint.FontMetricsInt fm = paint.getFontMetricsInt();
            int transY = bottom - drawable.getBounds().bottom;
            if (mVerticalAlignment == ALIGN_BASELINE) {
                transY -= fm.descent;
            } else if (mVerticalAlignment == ALIGN_CENTER) {//自定义居中对齐
                //与文字的中间线对齐&#xff08;这种方式不论是否设置行间距都能保障文字的中间线和图片的中间线是对齐的&#xff09;
                // y+ascent得到文字内容的顶部坐标&#xff0c;y+descent得到文字的底部坐标&#xff0c;&#xff08;顶部坐标+底部坐标&#xff09;/2=文字内容中间线坐标
                transY = ((y + fm.descent) + (y + fm.ascent)) / 2 - drawable.getBounds().bottom / 2;
            }
            canvas.translate(x, transY);
            drawable.draw(canvas);
            canvas.restore();
        }
    }
}
