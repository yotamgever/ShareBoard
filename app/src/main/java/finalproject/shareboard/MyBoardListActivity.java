package finalproject.shareboard;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import finalproject.shareboard.framework.ApiConnector;
import finalproject.shareboard.framework.CustomAdapter;
import finalproject.shareboard.framework.Globals;
import finalproject.shareboard.model.Board;
import finalproject.shareboard.model.User;


public class MyBoardListActivity extends ShareBoardActivity {

    ListView lstMyBoardsListView = null;
    ArrayList<Board> lstMyBoardsList = null;
    CustomAdapter boardsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_board_list);

        initializeComponents();
    }

    private void initializeComponents() {
        lstMyBoardsListView = (ListView) findViewById(R.id.lstMyBoardsList);



        loadMyBoardsList();
    }

    private void loadMyBoardsList() {
        lstMyBoardsList = new ArrayList<Board>();
        dialog.show();
        new GetUserBoards().execute(new ApiConnector());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_board_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetUserBoards extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... apiConnectors) {
            Integer userID = ((ShareBoardApplication)getApplication()).getCurrUser().getUserId();
            return apiConnectors[0].getUserBoards(userID);
        }

        @Override
        protected void onPostExecute(JSONArray userBoards) {
            for (int boardIndex = 0; boardIndex < userBoards.length(); boardIndex++) {
                JSONObject board = null;
                try {
                    board = userBoards.getJSONObject(boardIndex);
                    Integer boardId = board.getInt("BoardID");
                    String boardName = board.getString("BoardName");
                    Globals.boardTypes boardType = Globals.boardTypes.fromOrdinal(board.getInt("BoardType"));
                    User creator = new User(board.getInt("CreatorID"));
                    Board boardToAdd = new Board(boardId, boardName, creator, boardType);
                    Globals.userAuthType authType = Globals.userAuthType.fromOrdinal(board.getInt("PermissionCode"));
                    boardToAdd.addUserToBoard(((ShareBoardApplication)getApplication()).getCurrUser(), authType);
                    lstMyBoardsList.add(boardToAdd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            boardsListAdapter = new CustomAdapter(MyBoardListActivity.this, lstMyBoardsList);
            lstMyBoardsListView.setAdapter(boardsListAdapter);

            AdapterView.OnItemClickListener boardClicked = new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Integer boardId = Integer.valueOf(view.getTag().toString());

                    Intent myBoardActivity = new Intent(MyBoardListActivity.this, MyBoardActivity.class);
                    Bundle BoardDetails = new Bundle();
                    BoardDetails.putInt("BoardID", boardId);
                    myBoardActivity.putExtras(BoardDetails);
                    startActivity(myBoardActivity);
                }
            };

            lstMyBoardsListView.setOnItemClickListener(boardClicked);

            dialog.dismiss();
        }
    }
}
