# OkCache

针对`okhttp`的缓存方案，使用拦截器形式将该库添加进入`okhttp`中，入参拼接在`HttpHeader`或`Query`中，拦截器会在获取入参后删除这些额外的参数

## 支持post请求
`get`请求的`key`取`request.url()`MD5值

`post`请求的`key`取`request.url() + "_" + requestBodyStr`MD5值

## 缓存策略
### 基础策略
[okcache]()只提供了基础策略，一些常用的操作：

```
    /**
     * 只读取缓存
     */
    public static final int ONLY_CACHE = 1;
    /**
     * 只读取网络
     */
    public static final int ONLY_NETWORK = 2;
    /**
     * 读取缓存，如果缓存不存在则读取网络
     */
    public static final int CACHE_ELSE_NETWORK = 3;
    /**
     * 先读取网络，如果网络请求失败则读取缓存
     */
    public static final int NETWORK_ELSE_CACHE = 4;

    /**
     * 无缓存或缓存到期时使用网络，否则使用缓存
     */
    public static final int BY_STALE = 5;
```

### 进阶策略
如果策略本身需要执行多次网络请求，向上层返回多次数据，则需要根据基础策略组合：进阶策略

对此本库暂时只为最常用的情况提供了适配，`retrofit + rxjava`

为`Retrofit`自定义了一个`CallAdapter`[okcache-retrofit-adapters/rxjava]()

```
.addCallAdapterFactory(RxJavaCallAdapterFactory.create());
```
调用时：

```
    @GET("/repos/{owner}/{repo}/contributors")
    OkCacheObservable<List<Contributor>> contributorsObservable(@Path("owner") String owner
            , @Path("repo") String repo);
```
返回值为`OkCacheObservable `,拿到此返回值对象后在调用`subscribe`之前，调用其`setCallInterceptor`方法设置组合策略，`onNext`可能会调用多次

```
service.contributorsObservable("square","retrofit")
                .setCallInterceptor(new CacheWhileNetCallInterceptor<List<Contributor>>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Contributor>>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(RetrofitActivity.this, "完成", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Contributor> contributors) {
                        tvContent.setText(contributors.toString());
                    }
                });
```
## 接口文档
参加[wiki](http://gitlab.idc.safecenter.cn/mobile-technology/OkCache/wikis/home)
