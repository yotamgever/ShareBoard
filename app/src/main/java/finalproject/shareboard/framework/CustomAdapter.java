package finalproject.shareboard.framework;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import finalproject.shareboard.R;
import finalproject.shareboard.ShareBoardApplication;
import finalproject.shareboard.model.Board;
import finalproject.shareboard.model.User;

public class CustomAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<Board> data;
    private static LayoutInflater inflater=null;

    public CustomAdapter(Activity a, ArrayList<Board> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public Integer getBoardID(int position) {
        return data.get(position).getBoardID();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);

        ImageView ivBoardImage = (ImageView)vi.findViewById(R.id.ivBoardImage);
        TextView tvBoardName = (TextView)vi.findViewById(R.id.tvRowBoardName);
        TextView tvCreatorName = (TextView)vi.findViewById(R.id.lstRowCreatorName);
        TextView tvBoardId = (TextView)vi.findViewById(R.id.lstRowBoardID);
        ImageView ivAuthImage = (ImageView)vi.findViewById(R.id.ivAuthImage);

        Board board = data.get(position);

        // Setting all values in listview
        setBoardImage(ivBoardImage, board.getBoardType());
        tvBoardName.setText(board.getBoardName());
        tvCreatorName.setText(board.getCreator().getUserId().toString());
        tvBoardId.setText("Board ID:" + " " + board.getBoardID().toString());
        //tvLastUpdate.setText(song.get(CustomizedListView.KEY_DURATION));
        setUserAuthImage(ivAuthImage, board.getUsers().values().iterator().next());

        vi.setTag(board.getBoardID());

        return vi;
    }

    private void setUserAuthImage(ImageView ivAuthImage, Globals.userAuthType userAuth) {
        if (Globals.userAuthType.Display.compareTo(userAuth) == 0) {
            ivAuthImage.setImageResource(R.drawable.display);
        } else if (Globals.userAuthType.Edit.compareTo(userAuth) == 0) {
            ivAuthImage.setImageResource(R.drawable.edit);
        } else if (Globals.userAuthType.Admin.compareTo(userAuth) == 0) {
            ivAuthImage.setImageResource(R.drawable.admin);
        }
    }

    private void setBoardImage(ImageView ivBoardImage, Globals.boardTypes boardType) {
        if (Globals.boardTypes.Fridge.compareTo(boardType) == 0) {
            ivBoardImage.setImageResource(R.drawable.fridge);
        } else if (Globals.boardTypes.Wood.compareTo(boardType) == 0) {
            ivBoardImage.setImageResource(R.drawable.wood);
        } else if (Globals.boardTypes.Bulletin.compareTo(boardType) == 0) {
            ivBoardImage.setImageResource(R.drawable.bulletin);
        } else if (Globals.boardTypes.WhiteBoard.compareTo(boardType) == 0) {
            ivBoardImage.setImageResource(R.drawable.whiteboard);
        }
    }
}
