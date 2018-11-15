package com.yj.testrxjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


/**
 * RxJava2.0 学习博客 : https://blog.csdn.net/zhaoyanjun6/article/details/76443347
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rxJava5();
    }

    // 最简单的RxJava
    void rxJava1() {
        // 被订阅者者
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("1");
                e.onNext("2");
                e.onNext("3");
                e.onComplete(); //结束
                e.onNext( "4" );
            }
        });
        // 订阅者
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("MyTAG", "onSubscribe "+d.isDisposed());
            }

            @Override
            public void onNext(String s) {
                Log.d("MyTAG", "onNext: "+s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("MyTAG", "onError");
            }

            @Override
            public void onComplete() {
                Log.d("MyTAG", "onComplete");
            }
        };
        // 订阅
        observable.subscribe(observer);

        // 输出结果 :
        // onSubscribe false
        //    onNext: 1
        //    onNext: 2
        //    onNext: 3
        //    onComplete
    }

    // 使用Consumer替代Observer
    void rxJava2(){
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("1");
                e.onNext("2");
                e.onNext("3");
                e.onComplete(); //结束
                e.onNext( "4" );
            }
        });
        observable.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("MyTAG", "onNext: "+s);            }
        });

        // 输出结果 :
        // onNext: 1
        // onNext: 2
        // onNext: 3
    }

    // 使用map
    // map 用于 1转1
    // Student 转 String
    void rxJava3() {
        Student student = new Student("张三");
        Observable<Student> observable = Observable.just(student);
        observable.map(new Function<Student, String>() {
            @Override
            public String apply(Student student) throws Exception {
                return student.getName();
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d("MyTAG", "Name: "+s);
            }
        });

        // 输出结果：
        // Name: 张三
    }

    // 使用flatMap
    // flatMap用于1转多
    // Student 转 List<Course>
    // TODO  无序输出？？？
    void rxJava4() {
        List<Course> courses = new ArrayList<>();
        Course course1 = new Course("课程1");
        Course course2 = new Course("课程2");
        courses.add(course1);
        courses.add(course2);
        final Student student = new Student("张三", courses);

        List<Course> courses2 = new ArrayList<>();
        Course course3 = new Course("课程3");
        Course course4 = new Course("课程4");
        courses2.add(course3);
        courses2.add(course4);
        final Student student2 = new Student("李四", courses2);

        List<Course> courses3 = new ArrayList<>();
        Course course5 = new Course("课程5");
        Course course6 = new Course("课程6");
        courses3.add(course5);
        courses3.add(course6);
        final Student student3 = new Student("王五", courses3);

        Observable<Student> observable = Observable.create(new ObservableOnSubscribe<Student>() {
            @Override
            public void subscribe(ObservableEmitter<Student> e) throws Exception {
                e.onNext(student);
                e.onNext(student2);
                e.onNext(student3);
            }
        });
        observable.flatMap(new Function<Student, ObservableSource<Course>>() {
            @Override
            public ObservableSource<Course> apply(Student student) throws Exception {
                return Observable.fromIterable(student.getCourseList()).delay(0, TimeUnit.SECONDS);
            }
        }).subscribe(new Consumer<Course>() {
            @Override
            public void accept(Course course) throws Exception {
                Log.d("MyTAG", "课程名字: "+course.name);
            }
        });

        //  输出结果:
        //  课程名字: 课程1
        //  课程名字: 课程2
        //  课程名字: 课程5
        //  课程名字: 课程6
        //  课程名字: 课程3
        //  课程名字: 课程4

        // 可以看出输出结果是无序的，每次执行该方法输出结果都不一样；
        // todo: 经我自己测试，如果不加上delay，输出结果是有序的！很奇怪！
    }

    // 使用concatMap替代flatMap
    // 有序输出
    void rxJava5() {
        List<Course> courses = new ArrayList<>();
        Course course1 = new Course("课程1");
        Course course2 = new Course("课程2");
        courses.add(course1);
        courses.add(course2);
        final Student student = new Student("张三", courses);

        List<Course> courses2 = new ArrayList<>();
        Course course3 = new Course("课程3");
        Course course4 = new Course("课程4");
        courses2.add(course3);
        courses2.add(course4);
        final Student student2 = new Student("李四", courses2);

        List<Course> courses3 = new ArrayList<>();
        Course course5 = new Course("课程5");
        Course course6 = new Course("课程6");
        courses3.add(course5);
        courses3.add(course6);
        final Student student3 = new Student("王五", courses3);

        Observable<Student> observable = Observable.create(new ObservableOnSubscribe<Student>() {
            @Override
            public void subscribe(ObservableEmitter<Student> e) throws Exception {
                e.onNext(student);
                e.onNext(student2);
                e.onNext(student3);
            }
        });
        observable.concatMap(new Function<Student, ObservableSource<Course>>() {
            @Override
            public ObservableSource<Course> apply(Student student) throws Exception {
                return Observable.fromIterable(student.getCourseList()).delay(0, TimeUnit.SECONDS);
            }
        }).subscribe(new Consumer<Course>() {
            @Override
            public void accept(Course course) throws Exception {
                Log.d("MyTAG", "课程名字: "+course.name);
            }
        });

        //  输出结果:
        //  课程名字: 课程1
        //  课程名字: 课程2
        //  课程名字: 课程3
        //  课程名字: 课程4
        //  课程名字: 课程5
        //  课程名字: 课程6

        // 可以看出输出结果是有序的；
    }
}
