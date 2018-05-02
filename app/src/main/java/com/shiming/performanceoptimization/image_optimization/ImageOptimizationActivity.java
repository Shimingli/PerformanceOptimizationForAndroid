package com.shiming.performanceoptimization.image_optimization;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shiming.performanceoptimization.R;

/**
 * 图片优化Demo activity
 *
 */
public class ImageOptimizationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_optimization);

        /**
         * JPEG
         * 是一种广泛使用的有损压缩图像标准格式，它不支持透明和多帧动画，一般摄影的作品是JEPG格式的，通过控制压缩比，可以调整图片的大小
         */
        /**
         * PNG：
         * 是一种无损压缩的图片格式，他支持完整的透明通道，从图片处理的领域来讲，JEPG只有RGB三个通道，而PNG有ARGB四个通道，因此PNG图片占用空间一般比较大，会无形的增加app的大小，在做app瘦身时一般都要对PNG图片进行梳理以减小其占用的体积
         */

        /**
         * GIF
         * 是一种古老的图片的格式，诞生于1987年，随着初代互联网流行开来，他的特别是支持多帧动画，表情图，
         */

        /**
         * Webp
         * google于2010年发布，支持有损和无损、支持完整的透明通道、也支持多帧动画，目前主流的APP都已经使用了Webp，淘宝，微信，即保证了图片的大小和质量
         */

        //无损压缩ImageOptin，在不牺牲图片质量的前提下，即减下来PNG图片占用的空间，又提高了图片的加载速度  https://imageoptim.com/api

        //有损压缩ImageAlpha，图片大小得到极大的缩小，如果需要使用的话，一定要ui设计师看能否使用  https://pngmini.com/

        //有损压缩TinyPNG 比较知名的png压缩的工具，也需要ui设计师看能够使用不  https://tinypng.com/

        //PNG/JPEG 转化为 wepb  ：智图  ：http://zhitu.isux.us/


        /**
         * 尽量使用 .9.png 点9图   小黑点表示 可拉伸区域，黑边表示纵向显示内容的范围
         */
    }

    /**
     * 在安卓应用开发中能够使用编解码格式的只有三种 JEPG PNG WEBP
     */
    public enum CompressFormat {
        JPEG    (0),
        PNG     (1),
        WEBP    (2);//安卓4.0后开始支持

        CompressFormat(int nativeInt) {
            this.nativeInt = nativeInt;
        }
        final int nativeInt;
    }
}
