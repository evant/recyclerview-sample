package me.tatarka.recyclerviewtest;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.recyclerviewtest.itemanimator.BaseItemAnimator;
import me.tatarka.recyclerviewtest.itemanimator.SlideInFromLeftItemAnimator;


public class MyActivity extends Activity {
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setHasFixedSize(true);
        list.setItemAnimator(new SlideInFromLeftItemAnimator(list));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);
        list.setAdapter(myAdapter = new MyAdapter());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_add) {
            myAdapter.add();
        } else if (id == R.id.action_remove) {
            myAdapter.remove();
        }
        return super.onOptionsItemSelected(item);
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<String> items = new ArrayList<String>();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_my, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.text.setText(items.get(i));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void add() {
            String item = "Recycle View is Cool! (" + Math.random() + ")";
            items.add(0, item);
            notifyItemInserted(0);
        }

        public void remove() {
            if (items.isEmpty()) return;
            items.remove(0);
            notifyItemRemoved(0);
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView text;

            public ViewHolder(View itemView) {
                super(itemView);
                text = (TextView) itemView.findViewById(R.id.text);
            }
        }
    }
}
