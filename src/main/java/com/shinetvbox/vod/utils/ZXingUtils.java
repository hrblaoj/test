package com.shinetvbox.vod.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.Gravity;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 *   生成条形码和二维码的工具
 */
public class ZXingUtils {

    protected static Map<String,Bitmap> qrcodeCache = new HashMap<>(  );
    /**
     * 二维码添加到ImageView
     * @param imageView
     * @param border 边框大小
     */
    public static void setQrcodeToImageView(ImageView imageView, String url, int border) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        imageView.setImageBitmap( ZXingUtils.createQRcode(url,lp.width,lp.height,border,true) );
    }
    public static void setQrcodeToImageViewNoCache(ImageView imageView, String url, int border) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        imageView.setImageBitmap( ZXingUtils.createQRcode(url,lp.width,lp.height,border,false) );
    }
    /**
     * 二维码添加到ImageView
     * @param imageView
     * @param border 边框大小
     */
    public static void setQrcodeToImageView(ImageView imageView, String url, int border, int width, int height) {
        imageView.setImageBitmap( ZXingUtils.createQRcode(url,width,height,border,true) );
    }
    public static void setQrcodeToImageViewNoCache(ImageView imageView, String url, int border, int width, int height) {
        imageView.setImageBitmap( ZXingUtils.createQRcode(url,width,height,border,false) );
    }

    /**
     * 生成二维码 要转换的地址或字符串,可以是中文
     * @param url
     * @param width
     * @param height
     * @param border 边框大小
     * @return
     */
    public static Bitmap createQRcode(String url, int width, int height , int border, boolean cache) {
        if(qrcodeCache.containsKey( url ) && cache){
            return qrcodeCache.get( url );
        }
        try {
            // 判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix matrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, width, height);
            matrix = resetWhite(matrix,border);//删除白边
            width = matrix.getWidth();
            height = matrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = Color.BLACK;
                    } else {
                        pixels[y * width + x] = Color.WHITE;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            if(cache){
                qrcodeCache.put( url,bitmap );
            }

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static BitMatrix resetWhite(BitMatrix matrix, int border) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2];
        int resHeight = rec[3];
        border = (int) (border*(Math.max( (float)resWidth /matrix.getWidth() ,(float) resHeight/matrix.getHeight())));
        resWidth = rec[2]+border*2;
        resHeight = rec[3]+border*2;
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0]-border, j + rec[1]-border))
                    resMatrix.set(i, j);
            }
        }
        return resMatrix;
    }
    /**
     * 生成条形码
     * @param context
     * @param contents 需要生成的内容
     * @param desiredWidth 生成条形码的宽带
     * @param desiredHeight 生成条形码的高度
     * @param displayCode 是否在条形码下方显示内容
     * @return
     */
    public static Bitmap creatBarcode(Context context, String contents,
                                      int desiredWidth, int desiredHeight, boolean displayCode) {
        Bitmap ruseltBitmap = null;
        /**
         * 图片两端所保留的空白的宽度
         */
        int marginW = 20;
        /**
         * 条形码的编码类型
         */
        BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;

        if (displayCode) {
            Bitmap barcodeBitmap = encodeAsBitmap(contents, barcodeFormat,
                    desiredWidth, desiredHeight);
            Bitmap codeBitmap = creatCodeBitmap(contents, desiredWidth + 2
                    * marginW, desiredHeight, context);
            ruseltBitmap = mixtureBitmap(barcodeBitmap, codeBitmap, new PointF(
                    0, desiredHeight));
        } else {
            ruseltBitmap = encodeAsBitmap(contents, barcodeFormat,
                    desiredWidth, desiredHeight);
        }

        return ruseltBitmap;
    }

    /**
     * 生成条形码的Bitmap
     * @param contents 需要生成的内容
     * @param format 编码格式
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    protected static Bitmap encodeAsBitmap(String contents,
                                           BarcodeFormat format, int desiredWidth, int desiredHeight) {
        final int WHITE = 0xFFFFFFFF;
        final int BLACK = 0xFF000000;

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = null;
        try {
            result = writer.encode(contents, format, desiredWidth,
                    desiredHeight, null);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        // All are 0, or black, by default
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * 生成显示编码的Bitmap
     * @param contents
     * @param width
     * @param height
     * @param context
     * @return
     */
    protected static Bitmap creatCodeBitmap(String contents, int width,
                                            int height, Context context) {
        TextView tv = new TextView(context);
        LayoutParams layoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setText(contents);
        tv.setHeight(height);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setWidth(width);
        tv.setDrawingCacheEnabled(true);
        tv.setTextColor(Color.BLACK);
        tv.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

        tv.buildDrawingCache();
        Bitmap bitmapCode = tv.getDrawingCache();
        return bitmapCode;
    }

    /**
     * 将两个Bitmap合并成一个
     * @param first
     * @param second
     * @param fromPoint 第二个Bitmap开始绘制的起始位置（相对于第一个Bitmap）
     * @return
     */
    protected static Bitmap mixtureBitmap(Bitmap first, Bitmap second,
                                          PointF fromPoint) {
        if (first == null || second == null || fromPoint == null) {
            return null;
        }
        int marginW = 20;
        Bitmap newBitmap = Bitmap.createBitmap(
                first.getWidth() + second.getWidth() + marginW,
                first.getHeight() + second.getHeight(), Config.ARGB_4444);
        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first, marginW, 0, null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        cv.save();
//        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();

        return newBitmap;
    }

    public static void onDestroy(){
        qrcodeCache.clear();
    }
}