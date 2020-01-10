package teacherkothai.example.com.teacherkothai;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;



public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatDataProvider> messageList;
    private DatabaseReference databaseReference;

    public ChatAdapter(List<ChatDataProvider> messageList) {

        this.messageList = messageList;
        databaseReference = FirebaseDatabase.getInstance().getReference();

    }


    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sender_messagelayout ,parent, false);

        return new ChatViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatDataProvider chatDataProvider = messageList.get(position);
        holder.messageTextView.setText(chatDataProvider.getMessage());
        holder.messageTimeView.setText(chatDataProvider.getTime());

        String type = chatDataProvider.getType();

        if(type.equals("sent")){
            holder.itemView.setBackgroundResource(R.drawable.sender_background);


        }else{
            holder.itemView.setBackgroundResource(R.drawable.reciever_background);

        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{

        private TextView messageTextView;
        private TextView messageTimeView;

        public ChatViewHolder(View view) {
            super(view);
            messageTextView = (TextView) view.findViewById(R.id.chat_view_messagebox);
            messageTimeView = (TextView) view.findViewById(R.id.chat_view_timebox);
        }
    }
}

