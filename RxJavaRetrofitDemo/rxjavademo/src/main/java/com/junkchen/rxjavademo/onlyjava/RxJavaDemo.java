package com.junkchen.rxjavademo.onlyjava;

import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class RxJavaDemo {
    private static final String TAG = RxJavaDemo.class.getCanonicalName();

    public static void main(String[] args) {
//        useRxJava();
        hello(new String[]{"Java", "Kotlin", "Swift", "Python", "C++"});
    }

    private static void useRxJava() {
        // 1. 创建 Observer
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("onSubscribe: ");
            }

            @Override
            public void onNext(String s) {
                System.out.println("onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError: ");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete: ");
            }
        };

        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("onSubscribe: subscriber");
            }

            @Override
            public void onNext(String s) {
                System.out.println("onNext: subscriber: " + s);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError: subscriber");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete: subscriber");
            }
        };

        // 2. 创建 Observable
        Observable observable = Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("Hello");
                emitter.onNext("Hi");
                emitter.onNext("Alpha");
                emitter.onComplete();
            }
        });
        Observable observable2 = Observable.just("Hello", "Hi", "Alpha");
        String[] words = new String[] {"Hello", "Hi", "Alpha"};
        Observable observable3 = Observable.fromArray(words);
        /*
        将会依次调用
        emitter.onNext("Hello");
        emitter.onNext("Hi");
        emitter.onNext("Alpha");
        emitter.onComplete();
         */

        // 3. Subscribe 订阅
        observable.subscribe(observer);
        observable2.subscribe(observer);
        observable3.subscribe(observer);
//        observable.subscribe(subscriber);
    }

    public static void hello(String... names) {
        Observable.fromArray(names)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("Hello " + s + "!");
                    }
                });

        Flowable.just("Android", "IOS")
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("Hello " + s + "!");
                    }
                });
    }
}
