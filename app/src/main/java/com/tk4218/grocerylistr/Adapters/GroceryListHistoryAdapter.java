package com.tk4218.grocerylistr.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tk4218.grocerylistr.GroceryListActivity;
import com.tk4218.grocerylistr.Model.GroceryList;
import com.tk4218.grocerylistr.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by taylo on 11/25/2017.
 */

public class GroceryListHistoryAdapter extends RecyclerView.Adapter<GroceryListHistoryAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<GroceryList> mGroceryListHistory;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());


    public GroceryListHistoryAdapter(Context context, ArrayList<GroceryList> groceryListHistory){
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mGroceryListHistory = groceryListHistory;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.listview_grocerylist_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.groceryList = mGroceryListHistory.get(position);

        Date dateFrom = mGroceryListHistory.get(position).getMealPlanDateStart();
        Date dateTo = mGroceryListHistory.get(position).getMealPlanDateEnd();
        String groceryListDate = dateFormat.format(dateFrom) + (!dateFrom.equals(dateTo) ? " to " + dateFormat.format(dateTo) : "");
        holder.groceryListItem.setText(groceryListDate);

        if(mGroceryListHistory.get(position).getGroceryListCompleted()){
            Date dateCompleted = mGroceryListHistory.get(position).getCompletedDate();
            holder.groceryListCompleted.setText("Completed: " + dateFormat.format(dateCompleted));
        } else {
            holder.groceryListCompleted.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mGroceryListHistory.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView groceryListItem;
        TextView groceryListCompleted;
        GroceryList groceryList;

        ViewHolder(View itemView){
            super(itemView);
            groceryListItem = (TextView) itemView.findViewById(R.id.item_grocerylist);
            groceryListCompleted = (TextView) itemView.findViewById(R.id.item_grocerylist_completed);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(groceryList != null){
                if(groceryList.getGroceryListKey() != 0){
                    Intent intent = new Intent(mContext, GroceryListActivity.class);
                    intent.putExtra("groceryListKey", groceryList.getGroceryListKey());
                    mContext.startActivity(intent);
                }
            }
        }
    }
}
