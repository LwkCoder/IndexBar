# IndexBar

## 效果图
![](https://github.com/Vanish136/IndexBar/raw/master/pics/demo.gif) <br />

## 使用方式
【最新版本号以[这里](https://github.com/Vanish136/IndexBar/releases)为准】

#### ① 添加Gradle依赖
```
#last-version请查看上面的最新版本号

#AndroidStudio3.0以下
compile 'com.lwkandroid:IndexBar:last-version'

#AndroidStudio3.0以上
implementation 'com.lwkandroid:IndexBar:last-version'
```
<br />

#### ② 代码中使用
```
    //在xml中定义
    <com.lwkandroid.widget.indexbar.IndexBar
        android:id="@+id/indexBar"
        android:layout_width="40dp"
        android:layout_height="match_parent"
        app:bg_color_normal="@android:color/transparent" //普通状态下背景颜色，默认透明
        app:bg_color_pressed="#10000000" //触摸时背景颜色，默认透明
        app:text_color_normal="#3c3c3c" //普通状态下文字颜色，默认黑色
        app:text_color_pressed="#000093" //触摸时文字颜色，默认蓝色
        app:text_size_normal="14sp" //普通状态下文字大小，默认10sp
        app:text_size_pressed="16sp"/> //触摸时文字大小，默认15sp

    //代码中
    IndexBar mIndexBar = (IndexBar) findViewById(indexBar);
    //自定义索引数组，默认是26个大写字母
    mIndexBar.setTextArray(new CharSequence[]{...});
    //添加相关监听
    mIndexBar.setOnIndexLetterChangedListener(new IndexBar.OnIndexLetterChangedListener(){
        @Override
        public void onTouched(boolean touched)
        {
            //TODO 手指按下和抬起会回调这里
        }

         @Override
        public void onLetterChanged(CharSequence indexChar, int index, float y)
        {
            //TODO 索引字母改变时会回调这里
        }
    });
```
**更详细的使用方法可参考Demo**
<br />

### 混淆配置
```
-dontwarn com.lwkandroid.widget.indexbar.**
-keep class com.lwkandroid.widget.indexbar.**{*;}
```

### 开源参考
1. Demo中实现`指定RecyclerView滚动到指定位置`的参考博客：http://blog.csdn.net/tyzlmjj/article/details/49227601
2. Demo中有引用我另外一个开源项目[RecyclerViewAdapter](https://github.com/Vanish136/RecyclerViewAdapter)，只是为了配合演示RecyclerView和IndexBar联动效果，实际开发过程可以去掉该库，两个库之间没有必然依赖（当然也很欢迎你一起了解下）。
