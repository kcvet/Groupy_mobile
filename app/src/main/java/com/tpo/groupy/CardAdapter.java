package com.tpo.groupy;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

    private Context mContext;
    private List<Card> albumList;
    LinearLayout layout;
    private View subItem;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            subItem = itemView.findViewById(R.id.sub_item);
            layout = view.findViewById(R.id.card_layout);
            //Collapse card in the beginning
            ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.height = 770;
            layout.setLayoutParams(params);
            //
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext, "YOUR: "+this.getLayoutPosition(), Toast.LENGTH_SHORT).show();
        }

        private void bind(Card album) {
            boolean expanded = album.isExpanded();
            subItem.setVisibility(expanded ? View.VISIBLE : View.GONE);

            title.setText(album.getName());
            count.setText(album.getNumOfSongs() + " people");

        }
    }


    public CardAdapter(Context mContext, List<Card> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.carddesign, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Card album = albumList.get(position);
        final int position1 = position;
        holder.bind(album);
        /*
        holder.title.setText(album.getName());
        holder.count.setText(album.getNumOfSongs() + " people");
            */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expand();
                boolean expanded = album.isExpanded();
                album.setExpanded(!expanded);
                notifyItemChanged(position1);
            }
        });
        // loading album cover using Glide library
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.card_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "You successfully joined the group!", Toast.LENGTH_SHORT).show();

                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();

                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }



    public void expand(){
// Gets the layout params that will allow you to resize the layout
        ViewGroup.LayoutParams params = layout.getLayoutParams();
// Changes the height and width to the specified *pixels*
        if(params.height == 770){
            params.height = 1470;
            ValueAnimator animator = ValueAnimator.ofInt(770, 1470);
            animator.setDuration(1000);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    int val = (Integer) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
                    layoutParams.height = val;
                    layout.setLayoutParams(layoutParams);

                }
            });
            animator.start();
        }
        else{
            params.height = 770;
            ValueAnimator animator = ValueAnimator.ofInt(1470, 770);
            animator.setDuration(1000);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    int val = (Integer) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
                    layoutParams.height = val;
                    layout.setLayoutParams(layoutParams);

                }
            });
            animator.start();
        }

    }

}
