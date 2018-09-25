package com.junkchen.hetdemo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.junkchen.hetdemo.adapter.AnswerAdapter;
import com.junkchen.hetdemo.entity.AnswerItem;
import com.junkchen.hetdemo.entity.QuestionType;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AnswerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AnswerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnswerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private int questionType;
    private int mParam3;

    private OnFragmentInteractionListener mListener;

    private AnswerAdapter answerAdapter;

    public AnswerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnswerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnswerFragment newInstance(String param1, int param2, int param3) {
        AnswerFragment fragment = new AnswerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            questionType = getArguments().getInt(ARG_PARAM2);
            mParam3 = getArguments().getInt(ARG_PARAM3);
        }
        answerAdapter = new AnswerAdapter(getActivity());
        answerAdapter.setQuestionType(questionType);

        List<AnswerItem> answerList = new ArrayList<>();
        answerList.add(new AnswerItem("bashfulness"));
        answerList.add(new AnswerItem("outgoing"));

        List<AnswerItem> multipleAnswerList = new ArrayList<>();
        multipleAnswerList.add(new AnswerItem("不爱说话"));
        multipleAnswerList.add(new AnswerItem("不爱表达"));
        multipleAnswerList.add(new AnswerItem("不爱与人交往"));
        multipleAnswerList.add(new AnswerItem("小心谨慎"));
        multipleAnswerList.add(new AnswerItem("难以适应新环境"));
        multipleAnswerList.add(new AnswerItem("易发脾气"));

        if (questionType == QuestionType.SINGLE) {
            answerAdapter.setAnswerList(answerList);
        } else {
            answerAdapter.setAnswerList(multipleAnswerList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_answer, container, false);
        RecyclerView rv_answer = view.findViewById(R.id.rv_answer);
        TextView tv_question_title = view.findViewById(R.id.tv_question_title);
        Button btn_next = view.findViewById(R.id.btn_next);
        tv_question_title.setText(mParam1);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(mParam3);
            }
        });
        rv_answer.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_answer.setAdapter(answerAdapter);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void onButtonClicked(int i);
    }
}
