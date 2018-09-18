package com.luzeping.aria.commonutils.utils;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;

public class StringUtils {
    /**
     * 为TextView设置不同样式的文本
     * @param args 按照文本(String)、文本大小(int)、文本颜色(int) 字体(Typeface，不需设置特殊字体时传Typeface.DEFAULT)的顺序设置参数
     * @return
     */
    public static SpannableStringBuilder parseSpecialString(Object... args){
        try {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            for (int i = 0;i < args.length;i++){
                String text = (String) args[i++];
                int textSize = (int) args[i++];
                int textColor = (int) args[i++];
                Typeface typeface = (Typeface) args[i];
                int start = spannableStringBuilder.length();
                int end = start + text.length();
                spannableStringBuilder.append(text);
                spannableStringBuilder.setSpan(new AbsoluteSizeSpan(textSize),start,end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.setSpan(new ForegroundColorSpan(textColor),start,end,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.setSpan(new CustomTypefaceSpan(text,typeface),start,end,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            return spannableStringBuilder;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static class CustomTypefaceSpan extends TypefaceSpan {

        private final Typeface newType;

        public CustomTypefaceSpan(String family,Typeface type) {
            super(family);
            newType = type;
        }

        private void applyCustomTypeface(Paint paint){
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~newType.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }

            paint.setTypeface(newType);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            applyCustomTypeface(ds);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            super.updateMeasureState(paint);
            applyCustomTypeface(paint);
        }
    }
}
