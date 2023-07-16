package com.example.phonebook;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    @NonNull
    Context context;
    private static final int EDIT_REQUEST_CODE = 1;
    ArrayList<Person> contact_list ;
//    ArrayList<Person> filtered_list ;
//    ArrayList<Person> favourites_list ;
    public void setFilteredList(ArrayList<Person> filteredList){
        this.contact_list=filteredList;
    }
    public void setFavouritesList(ArrayList<Person> favouritesList){
        this.contact_list=favouritesList;
    }
    public void setContactList(ArrayList<Person> contact_list) {
        this.contact_list = contact_list;
    }
    public ContactAdapter(Context context, ArrayList<Person> contact_list){
        this.context=context;
        this.contact_list=contact_list;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_rows,parent,false);
        ViewHolder viewHolder= new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {

        holder.first_name.setText(contact_list.get(position).fname);
        holder.last_name.setText(contact_list.get(position).lname);
        holder.image.setImageURI(Uri.parse(contact_list.get(position).uri));
        holder.rvrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Person person= contact_list.get(position);
                Intent intent = new Intent(v.getContext(),DetailsActivity.class);
                intent.putExtra("object",person);
                intent.putExtra("fname",contact_list.get(position).fname);
                intent.putExtra("lname",contact_list.get(position).lname);
                intent.putExtra("cname",contact_list.get(position).cname);
                intent.putExtra("pnumber",contact_list.get(position).pnumber);
                intent.putExtra("email",contact_list.get(position).emailid);
                intent.putExtra("dob",contact_list.get(position).dob);
                intent.putExtra("uri",contact_list.get(position).uri);
                intent.putExtra("id",contact_list.get(position).id);
                intent.putExtra("favourites",contact_list.get(position).isFavourite());
                ((Activity) v.getContext()).startActivityForResult(intent, EDIT_REQUEST_CODE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contact_list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView first_name,last_name;
        ImageView image;
        LinearLayout rvrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            first_name=itemView.findViewById(R.id.first_name);
            last_name=itemView.findViewById(R.id.last_name);
            image = itemView.findViewById(R.id.image);
            rvrow=itemView.findViewById(R.id.rvrow);
        }
    }
}