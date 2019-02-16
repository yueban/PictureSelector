### 集成步骤

#### 1. 添加 gradle 引用

模块 `build.gradle` 添加:

```groovy
dependencies {
    implementation 'com.yueban:picture_library:2.3.11'
}
```

#### 2. AndroidManifest 中添加权限

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.INTERNET"/>
```

#### 3. WebView 所在的 Activity 中添加:

```java
public class MainActivity extends AppCompatActivity {
    private PictureSelectorUtil pictureSelectorUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ...

        // getInstance() 方法只在界面初始化时调用一次，建议按示例代码将 PictureSelectorUtil 对象声明为全局变量
        pictureSelectorUtil = PictureSelectorUtil.getInstance(this);
        // 集成至对应 WebView 组件
        pictureSelectorUtil.integrateWithWebView(webView);

        // ...
        // webView.loadUrl(".....");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 监听 onActivityResult 方法以获取图片选择的结果，并执行上传操作
        pictureSelectorUtil.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 监听 Activity 销毁方法，清除组件缓存，避免内存泄露
        pictureSelectorUtil.onDestroy();
    }
}
```

#### 4. 混淆规则
```
# PictureSelector
-keep class com.yueban.qiniu_lib.** { *; }
-keep class com.luck.picture.lib.** { *; }

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }
   
# rxjava
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
 long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
 rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# rxandroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
```