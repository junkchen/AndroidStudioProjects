package com.junkchen.hetdemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.junkchen.hetdemo.R;
import com.junkchen.hetdemo.entity.AnswerItem;
import com.junkchen.hetdemo.entity.QuestionType;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {

    private Context context;
    private List<AnswerItem> answerList;

    private int questionType = 1;

    public AnswerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View answerView = LayoutInflater.from(context)
                .inflate(R.layout.item_answer, parent, false);
        return new AnswerViewHolder(answerView);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        holder.bindHolder(answerList.get(position));
    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }

    protected class AnswerViewHolder extends RecyclerView.ViewHolder {
        private AppCompatCheckBox cb_answer;

        public AnswerViewHolder(View itemView) {
            super(itemView);
            cb_answer = itemView.findViewById(R.id.cb_answer);
            cb_answer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    AnswerItem answer = answerList.get(getAdapterPosition());
                    if (questionType == QuestionType.SINGLE) {
                        answer.setSelected(isChecked);
                        if (isChecked) {
                            for (int i = 0; i < answerList.size(); i++) {
                                if (getAdapterPosition() == i) continue;
                                answerList.get(i).setSelected(false);
                            }
                            notifyDataSetChanged();
                        }
                    } else if (questionType == QuestionType.MULTIPLE) {
                        answer.setSelected(isChecked);
                    }
                }
            });
        }

        public void bindHolder(AnswerItem answer) {
            cb_answer.setText(answer.getDescription());
            cb_answer.setChecked(answer.isSelected());
        }
    }

    public void setAnswerList(List<AnswerItem> answerList) {
        this.answerList = answerList;
    }

    public void setQuestionType(int questionType) {
        this.questionType = questionType;
    }
}
