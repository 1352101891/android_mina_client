package com.lq.im;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RxjavaActivity extends AppCompatActivity {
    public static final String TAG="RxjavaActivity";
    public RecyclerView recyclerView;
    public LinearLayoutManager layoutManager;
    public String[] srcs= new String[]{"测试一下000","lq","你是欧冠","我是一个上课的那首空间的","asa","sasdaksdn","s你晒晒大大撒旦吉萨家可达三点"};
    public int[] colors= new int[]{R.color.blue,R.color.purple,R.color.green,R.color.yellow,R.color.orange,R.color.light_blue};
    public int[] gravitys = new int[]{Gravity.BOTTOM,Gravity.CENTER,Gravity.TOP};
    public int[] ids = new int[]{R.layout.item,R.layout.item1,R.layout.item2,R.layout.item3};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava);
        recyclerView=findViewById(R.id.recycle_view);
        new LinearLayoutManager(this);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new MyRecycleView(this));

        rxJava();
    }

    class MyRecycleView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        Context context;

        public MyRecycleView(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(ids[(int) (Math.random()*i)],viewGroup,false);
            return new MyViewHold(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ViewGroup.LayoutParams lp = viewHolder.itemView.getLayoutParams();
            setholder((MyViewHold) viewHolder,i);
        }

        public void setholder(MyViewHold viewHold,int pos){
            TextView textView=viewHold.textView;
            textView.setGravity(gravitys[(int) (Math.random()*gravitys.length)]);
            textView.setText(srcs[pos%srcs.length]);
            textView.setBackgroundColor(context.getResources().getColor(colors[(int) (Math.random()*colors.length)]));
        }

        @Override
        public int getItemCount() {
            return 100;
        }

        class MyViewHold extends RecyclerView.ViewHolder {
            public TextView textView;
            public MyViewHold(@NonNull View itemView) {
                super(itemView);
                textView=itemView.findViewById(R.id.text);
            }
        }
    }

    public void rxJava(){
        Handler handler =new Handler();
        handler.obtainMessage();
        new Observable<String>() {
            @Override
            protected void subscribeActual(Observer<? super String> observer) {
                observer.onNext("测试1");
                observer.onNext("测试2");
                observer.onComplete();
            }
        }
        .subscribeOn(Schedulers.io())
        .map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                return s;
            }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG,"onSubscribe");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG,"onNext:" + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG,"onSubscribe");
            }

            @Override
            public void onComplete() {
                Log.e(TAG,"onComplete");
            }
        });
    }
}
