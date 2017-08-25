# 缓存模式OkCache(支持okhttp和retrofit使用get请求时缓存)

## 依赖
1.仓库地址
```java
allprojects {
    repositories {
        //外网
        maven{ url 'http://120.194.4.152:8081/nexus/content/groups/public/'}
        jcenter()
    }
}
```
2.项目依赖
```java
    compile 'com.xdja.okcache:cache-retrofit:0.1.0'//retrofit cache依赖
    compile 'com.xdja.okcache:cache-okhttp:0.1.0'//okhttp cache依赖
```
## 使用方法
### 1.okhttp中cache使用

1）.需要在application中进行初始化
 
```java
  OkHttpCacheUtils.init(this);
``` 

2）.提供了同步请求和异步请求两种方式

(1).同步请求

```java
OkHttpCacheUtils.getInstance().okhttpGetByCacheType(url, null, null, 1);
```

(2).异步请求

```java
OkHttpCacheUtils.getInstance().okHttpASyncGet("", null, null, 1, new IAsyncCallBack() {
                     @Override
                     public void onFailure(Call arg0, IOException e) {
                     }
                     @Override
                     public void onResponse(Response response) {
                     }
                 });
```

### 2.retrofit中cache使用

1).需要在application中进行初始化

```java
  RetrofitCacheGenerator.init(this);
```

2).创建你需要的请求接口和请求仓库ApiInterface和ApiFactory

 注：在请求接口中需要添加headers(后边数字对应不同的缓存策略)
  0-只读取缓存,1-只读取网络,2-读取缓存，如果缓存不存在则读取网络,3-先读取网络，如果网络请求失败则读取缓存
  
```java
   @Headers("requestCacheType: 1")
```

3).使用仓库生成对应的service然后发起请求

## 优点：
提供四种缓存策略：

```java
    /**
     * 只读取缓存
     */
    public static final int ONLY_CACHE = 0;
    /**
     * 只读取网络
     */
    public static final int ONLY_NETWORK = 1;
    /**
     * 读取缓存，如果缓存不存在则读取网络
     */
    public static final int CACHE_ELSE_NETWORK= 2;
    /**
     * 先读取网络，如果网络请求失败则读取缓存
     */
    public static final int NETWORK_ELSE_CACHE = 3;

```

## 已知问题

 - 目前Okhttp只是支持get请求缓存，后续考虑支持post缓存模式
 
## 接口文档
参加[wiki](http://gitlab.idc.safecenter.cn/mobile-technology/OkCache/wikis/home)
