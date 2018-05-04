package com.app.rzm.test;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.rzm.R;
import com.rzm.commonlibrary.utils.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class TestRxjavaActivity extends AppCompatActivity {

    private static final java.lang.String TAG = "TestRxjavaActivity";
    private TextView mTextView;
    private EditText mEditView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_rxjava);
        mTextView = (TextView) findViewById(R.id.text);
        mEditView = (EditText) findViewById(R.id.edit);
        mEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });
    }

    public void start(View view) {
        Observable.just("aaa").map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                LogUtils.i(TAG, "apply");
                return s;
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                LogUtils.i(TAG, "onSubscribe");
            }

            @Override
            public void onNext(String s) {
                LogUtils.i(TAG, "onNext");
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.i(TAG, "onError");
            }

            @Override
            public void onComplete() {
                LogUtils.i(TAG, "onComplete");
            }
        });
    }

    /**
     * 关键词搜索案例,当输入文件变化时就进行一次搜索，为了放置不必要的网络请求
     * 而使用关键词
     *
     * @param s
     */
    public void search(final String s) {
        Observable.just(s).map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                return null;
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                if (!TextUtils.isEmpty(s) && s.contains("a")) {
                    return true;
                }
                return false;
            }
        })//switchMap和flatMap只有一点区别，在这个场景下，由于每次输入的文字变化都会进行搜索，而且搜索
                //结果不一定是先请求的先返回，有可能是本来我要搜索abc,当我输入ab的时候进行了一次搜索，然后输入
                //完abc又请求了一次，但是由于各种原因，第一次请求的结果返回晚于第二次，那么第一次搜索的不是我想要
                //的结果，但是由于它返回的晚，反而把理想的搜索结果覆盖了，用switchMap可以解决这个问题，它会
                //返回最近一次请求的结果，即便是由于上边的原因导致的问题
        .switchMap(new Function<String, ObservableSource<List<String>>>() {
            @Override
            public ObservableSource<List<String>> apply(String s) throws Exception {
                List<String> list = new ArrayList<String>();
                list.add("搜索结果a");
                list.add("搜索结果B");
                return Observable.just(list);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> strings) throws Exception {
                        //将搜索结果展示在列表中
                        for (String string : strings) {
                            LogUtils.i(TAG, string);
                        }
                    }
                });

    }

    class User {

    }

    class UserParams {
        String name;
        int id;
    }

    class LoginResult {

        public LoginResult(UserParams params) {

        }

    }

    public UserParams getUserParams() {
        UserParams params = new UserParams();
        params.name = "张三";
        params.id = 111;
        return params;
    }

    /**
     * 用户登录案例
     *
     * @param view
     */
    public void start7(View view) {
        Observable.just(getUserParams()).flatMap(new Function<UserParams, ObservableSource<LoginResult>>() {
            @Override
            public ObservableSource<LoginResult> apply(UserParams userParams) throws Exception {
                //do something 模拟登录传入登录参数后获取到服务器返回值
                LoginResult result = new LoginResult(userParams);
                return Observable.just(result);
            }
        }).flatMap(new Function<LoginResult, ObservableSource<User>>() {
            @Override
            public ObservableSource<User> apply(LoginResult loginResult) throws Exception {
                //do something 模拟从登录返回值中根据userid获取到用户信息，返回User对象
                return Observable.just(new User());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                //显示用户姓名
                LogUtils.i(TAG, user.toString());
            }
        });
    }

    public void start6(View view) {
        Observable.just(1).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return 1 + "哈哈，下雨了";
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                LogUtils.i(TAG, s);
            }
        });
    }

    public void start5(View view) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<String> observableEmitter) throws Exception {
                String url1 = "http://is.snssdk.com/2/essay/discovery/v3/?iid=6152551759&aid=7";
                URL url = new URL(url1);
                //得到connection对象。
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //设置请求方式
                connection.setRequestMethod("GET");
                //连接
                connection.connect();
                //得到响应码
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    //得到响应流
                    InputStream inputStream = connection.getInputStream();
                    //将响应流转换成字符串
                    String result = stream2String(inputStream);//将流转换为字符串。
                    LogUtils.i(TAG, "onSuccess");
                    observableEmitter.onNext(result);
                } else {
                    observableEmitter.onError(new Exception("失败"));
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable disposable) {
                LogUtils.i(TAG, "onSubscribe");
            }

            @Override
            public void onNext(@NonNull String s) {
                LogUtils.i(TAG, "onNext");
                mTextView.setText(s);

            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                LogUtils.i(TAG, "onError");
                throwable.printStackTrace();
            }

            @Override
            public void onComplete() {
                LogUtils.i(TAG, "onComplete");
            }
        });
    }

    public void start4(View view) {
        Observable.just("今天", "明天", "昨天", "前天").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                LogUtils.i(TAG, "accept:" + s);
            }
        });
        Observable.fromArray("星期一", "星期二").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                LogUtils.i(TAG, "accept2:" + s);
            }
        });
    }

    public void start3(View view) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> observableEmitter) throws Exception {
                LogUtils.i(TAG, "subscribe");
                observableEmitter.onNext("onNext");
                observableEmitter.onComplete();
                observableEmitter.onError(new IllegalAccessException("发送错误"));
            }
        }).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                LogUtils.i(TAG, "subscribe(new Consumer() " + o.toString());
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtils.i(TAG, "new Consumer<Throwable>");
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                LogUtils.i(TAG, "new Action()");
            }
        });
    }

    public void start2(View view) {
        Observable observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                try {
                    observableEmitter.onNext("星期一");
                    observableEmitter.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    observableEmitter.onError(new NullPointerException("发生异常"));
                }
            }
        });

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                LogUtils.i(TAG, "onSubscribe");
            }

            @Override
            public void onNext(String s) {
                LogUtils.i(TAG, "onNext");
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtils.i(TAG, "onError");
            }

            @Override
            public void onComplete() {
                LogUtils.i(TAG, "onComplete");
            }
        };
        observable.subscribe(observer);

    }

    public static String stream2String(InputStream inputStream) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int length = 0;
            byte[] buffer = new byte[1024];
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                baos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }
}
