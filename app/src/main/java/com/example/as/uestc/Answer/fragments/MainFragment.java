package com.example.as.uestc.Answer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.as.uestc.Answer.ZoomOutPageTransformer;
import com.example.as.uestc.Answer.beans.CurrentClass;
import com.example.as.uestc.Answer.beans.ScorePost;
import com.example.as.uestc.Answer.networks.RetrofitManager;
import com.example.as.uestc.Answer.view.AnswerActivity;
import com.example.as.uestc.Answer.view.adapter.MyPagerAdapter;
import com.example.as.uestc.R;
import com.example.as.uestc.base.mvp.EventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by as on 2017/11/5.
 * 展示当前班级的Fragment
 */

public class MainFragment extends Fragment {

    private String TOKEN,ID;    //当前评委的token和id
    private MainFragment context;
    private CircleIndicator indicator;
    private TextView back,classRank,rank,classer,details,push,description,history;
    private ViewPager viewPager;    //展示五张班级详情的图片的ViewPager
    private List<View> views = new ArrayList<>();    //保存五张图片对应的ImageView的list
    private CurrentClass currentClass;
    private int POSITION;

    private int STATE;
    public MainFragment()
    {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        TOKEN = getArguments().getString("token");
        ID = currentClass.getClassID();
        POSITION = getArguments().getInt("position");
        STATE = getArguments().getInt("state");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.answer_fragment,container,false);
        if(currentClass.getImages()!=null) {
            int length = currentClass.getImages().size();
            Log.d("currentClass", String.valueOf(currentClass.getImages().size()));
            for (int i = 0; i < length; i++) {
                View view1 = inflater.inflate(R.layout.answer_fragment_pager_item, null, false);
                ImageView imageView = (ImageView) view1.findViewById(R.id.answer_fragment_pager_item_imageview);
                Glide.with(getActivity()).load(currentClass.getImages().get(i)).into(imageView);
                Log.e("", "MainFragment==========" + currentClass.getImages().get(i));
                views.add(view1);
            }
            init(view);
            addListeners();
        }
        return view;
    }

    private void init(View view) {
        classRank = (TextView)view.findViewById(R.id.answer_fragment_class_rank);
        classer = (TextView)view.findViewById(R.id.answer_fragment_class);
        rank = (TextView) view.findViewById(R.id.answer_fragment_rank);
        details = (TextView)view.findViewById(R.id.answer_fragment_details);
        //description=(TextView)view.findViewById(R.id.answer_fragment_description);
        push = (TextView)view.findViewById(R.id.answer_fragment_push);
//        history = (TextView)view.findViewById(R.id.answer_fragment_record);

        if (currentClass != null) {
            classRank.setText(currentClass.getClassID());
            classer.setText(currentClass.getAcademy());
            rank.setText("答辩序号:"+currentClass.getOrderNum());
        }
        viewPager = (ViewPager)view.findViewById(R.id.answer_fragment_viewpager);
        MyPagerAdapter adapter = new MyPagerAdapter(views);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        ZoomOutPageTransformer transformation = new ZoomOutPageTransformer();
        viewPager.setPageTransformer(true,transformation);
        indicator = (CircleIndicator)view.findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        /*
        如果已经投过票了，就换成已投的图标，并且不可点击
         */
        if (STATE == 1) {
            push.setEnabled(false);
            push.setBackground(getActivity().getDrawable(R.drawable.shape_has));
            push.setTextColor(Color.parseColor("#dddddddd"));
        }
    }

    private void addListeners() {
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailsDiaolg detailsDiaolg = new DetailsDiaolg();
                assert getFragmentManager() != null;
                detailsDiaolg.show(getFragmentManager(),null);
            }
        });

        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PushDiaolg diaolg = new PushDiaolg();
                Bundle data = new Bundle();
                data.putString("classID",ID);
                data.putString("token",TOKEN);
                diaolg.setArguments(data);
                diaolg.setTargetFragment(context,0);
                diaolg.show(getFragmentManager(),null);
            }
        });

//        history.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                ClassHistoryDialog classHistoryDialog = new ClassHistoryDialog();
//                Bundle data = new Bundle();
//                int length = currentClass.getImages().size();
//                data.putString("url",currentClass.getImages().get(length - 1));
//                classHistoryDialog.setArguments(data);
//                classHistoryDialog.show(getFragmentManager(), null);
//            }
//        });
    }

    /**
     * “打分”按钮点击打分成功后，状态改变
     */
    public void resetDrawable() {
        push.setBackground(getActivity().getDrawable(R.drawable.shape_has));
        push.setEnabled(false);
        push.setTextColor(Color.parseColor("#dddddddd"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            //处理提交数据的事件
            String token = data.getExtras().getString("token");
            String id = data.getExtras().getString("classID");
            String score = data.getExtras().getString("score");
            ((AnswerActivity)getActivity()).postScore(new ScorePost(id,score,token),POSITION);
        }
    }


    public void setCurrentClass(CurrentClass currentClass)
    {
        this.currentClass = currentClass;
    }
}
