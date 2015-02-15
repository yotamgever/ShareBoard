package finalproject.shareboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

    private SharedPreferences preferences;
    private SharedPreferences.Editor prefEdit;

    Dialog registerUserDialog;
    Dialog adValidDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_board_list);

        preferences =
                getSharedPreferences("ShareBoard",MODE_PRIVATE);
        prefEdit = preferences.edit();
        ((ShareBoardApplication)getApplication()).setUserID(preferences.getInt("UserID", 0));

        if (((ShareBoardApplication)getApplication()).getUserID() == 0) {
            initalizeRegisterUserDialog();
        }
    }

    private void initalizeRegisterUserDialog() {
        registerUserDialog = new Dialog(MyBoardListActivity.this);
        registerUserDialog.setContentView(R.layout.register_user);
        registerUserDialog.setTitle("Registration");

        final EditText registerUserInput = (EditText) registerUserDialog.findViewById(R.id.etRegisterUser);
        Button registerUserSaveButton = (Button) registerUserDialog.findViewById(R.id.btnRegisterUserSave);

        AlertDialog.Builder adValidDialogBuilder = new AlertDialog.Builder(this);
        adValidDialogBuilder.setMessage("Oops! Something went wrong... Please try correct your details or try again later")
                .setCancelable(false)
                .setNeutralButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        adValidDialog = adValidDialogBuilder.create();

        registerUserSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Integer UserID = Integer.parseInt(registerUserInput.getText().toString());
                registerNewUser(UserID);
            }
        });

        registerUserDialog.setCancelable(false);
        registerUserDialog.show();
    }

    private void registerNewUser(Integer NewUserID) {
        dialog.show();
        new insertNewUser(NewUserID).execute(new ApiConnector());
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
        if (id == R.id.add_new_board) {
            Intent AddCreateActivity = new Intent(this, CreateBoardActivity.class);
            startActivity(AddCreateActivity);
            return true;
        } else if (id == R.id.refresh_boards) {
            onResume();
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
            if (userBoards != null) {
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
                        boardToAdd.addUserToBoard(((ShareBoardApplication) getApplication()).getCurrUser(), authType);
                        lstMyBoardsList.add(boardToAdd);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    @Override
    protected void onResume() {
        super.onResume();
        if (((ShareBoardApplication)getApplication()).getUserID() != 0) {
            initializeComponents();
        }
    }

    private class insertNewUser extends AsyncTask<ApiConnector, Long, Integer> {
        private Integer UserId;

        protected insertNewUser(Integer UserIDToAdd) {
            super();
            UserId = UserIDToAdd;
        }

        @Override

        protected Integer doInBackground(ApiConnector... apiConnectors) {
            return apiConnectors[0].InsertUser(UserId);
        }

        @Override
        protected void onPostExecute(Integer success) {
            if (success != 0) {
                ((ShareBoardApplication) getApplication()).setUserID(UserId);
                prefEdit.putInt("UserID", UserId).apply();
                registerUserDialog.dismiss();
                dialog.dismiss();
                initializeComponents();
            } else {
                dialog.dismiss();
                adValidDialog.show();
            }
        }
    }
}
