package com.abc.localproduceapp;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;


    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>{
        private ArrayList<MyData> listdata;

    public ItemAdapter(ArrayList<MyData> listdata) {
            this.listdata = listdata;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
            return new ViewHolder(listItem);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final MyData myListData = listdata.get(position);
            holder.name_View.setText(listdata.get(position).getItemName());
            holder.price_View.setText(listdata.get(position).getItemPrice());
            holder.seller_View.setText(listdata.get(position).getItemSeller());
            holder.item_Image.setImageBitmap(listdata.get(position).getItemImage());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),Item_view.class);
                    i.putExtra("token",listdata.get(position).getToken());
                    v.getContext().startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listdata.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView name_View;
            public TextView price_View;
            public TextView seller_View;
            public android.widget.LinearLayout LinearLayout;
            public ImageView item_Image;
            public ViewHolder(View itemView) {
                super(itemView);
                this.item_Image = (ImageView) itemView.findViewById(R.id.itemimg);
                this.name_View = (TextView) itemView.findViewById(R.id.itemtitle);
                this.price_View = (TextView) itemView.findViewById(R.id.itemprice);
                this.seller_View = (TextView) itemView.findViewById(R.id.itemSeller);
                this.LinearLayout = (LinearLayout)itemView.findViewById(R.id.Main_List);
            }
        }
    }

