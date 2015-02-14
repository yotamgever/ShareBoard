package finalproject.shareboard;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import finalproject.shareboard.framework.ApiConnector;
import finalproject.shareboard.framework.Globals;
import finalproject.shareboard.model.Ad;
import finalproject.shareboard.model.Board;
import finalproject.shareboard.model.User;


public class MyBoardActivity extends ShareBoardActivity {

    private Integer boardID = null;
    private Globals.userAuthType currUserAuth = null;
    Board currBoard = null;
    ArrayList<Ad> currBoardAds = new ArrayList<Ad>();

    private LinearLayout llBoardBackground = null;
    private Button btnLargeNote1 = null;
    private Button btnLargeNote2 = null;
    private Button btnSmallNote1 = null;
    private Button btnSmallNote2 = null;
    private TextView emptyBoardText = null;

    private ArrayList<Button> lstLargeNotes = new ArrayList<Button>();
    private ArrayList<Button> lstSmallNotes = new ArrayList<Button>();

    private HashMap<Button, Ad> buttonAds = new HashMap<Button, Ad>();

    private View.OnClickListener onAdClicked = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_board);

        InitializeComponents();
    }

    private void InitializeComponents() {
        Bundle extras = getIntent().getExtras();
        boardID = extras.getInt("BoardID");

        llBoardBackground = (LinearLayout) findViewById(R.id.llBoardBackground);
        btnLargeNote1 = (Button) findViewById(R.id.btnLargeNote1);
        btnLargeNote2 = (Button) findViewById(R.id.btnLargeNote2);
        btnSmallNote1 = (Button) findViewById(R.id.btnSmallNote1);
        btnSmallNote2 = (Button) findViewById(R.id.btnSmallNote2);

        lstLargeNotes.add(btnLargeNote1);
        lstLargeNotes.add(btnLargeNote2);
        lstSmallNotes.add(btnSmallNote1);
        lstSmallNotes.add(btnSmallNote2);

        emptyBoardText = (TextView) findViewById(R.id.tvEmptyBoardText);

        onAdClicked = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent adActivity = new Intent(MyBoardActivity.this, AdActivity.class);
                Bundle AdDetails = new Bundle();
                AdDetails.putInt("AdID", Integer.valueOf(view.getTag().toString()));
                Ad wantedAd = getAdByID(Integer.valueOf(view.getTag().toString()));
                AdDetails.putString("AdTitle", wantedAd.getAdTitle());
                AdDetails.putInt("AdType", wantedAd.getAdType().ordinal());
                AdDetails.putString("AdDesc", wantedAd.getAdDesc());
                AdDetails.putInt("AdPriority", wantedAd.getAdPriority().ordinal());
                AdDetails.putString("AdFromDate", wantedAd.getFromTime());
                AdDetails.putString("AdToDate", wantedAd.getToTime());
                AdDetails.putInt("UserAuth", currUserAuth.ordinal());
                AdDetails.putInt("BoardID", boardID);
                adActivity.putExtras(AdDetails);
                startActivity(adActivity);
            }
        };

//        dialog.show();
//        loadBoardDetails(boardID);
    }

    private void loadBoardDetails(Integer boardID) {
        new GetBoard().execute(new ApiConnector());
    }

    private Ad getAdByID(Integer adID) {
        Ad toReturn = null;
        for (Ad curr : currBoardAds) {
            if (curr.getAdId().compareTo(adID) == 0) {
                toReturn = curr;
                break;
            }
        }

        return toReturn;
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (Button btn : lstLargeNotes) {
            btn.setVisibility(View.INVISIBLE);
        }

        for (Button btn : lstSmallNotes) {
            btn.setVisibility(View.INVISIBLE);
        }

        currBoardAds.clear();
        buttonAds.clear();
        dialog.show();
        loadBoardDetails(boardID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_board, menu);

        if (currUserAuth != null) {
            if (Globals.userAuthType.Admin.compareTo(currUserAuth) == 0) {
                menu.findItem(R.id.itmAddNewAd).setVisible(true);
            } else {
                menu.findItem(R.id.itmAddNewAd).setVisible(false);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.itmAddNewAd) {
            Intent adActivity = new Intent(MyBoardActivity.this, AdActivity.class);
            Bundle extras = new Bundle();
            extras.putInt("UserAuth", Globals.userAuthType.Admin.ordinal());
            extras.putInt("BoardID", boardID);
            adActivity.putExtras(extras);
            startActivity(adActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetBoard extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... apiConnectors) {
            return apiConnectors[0].getBoard(boardID);
        }

        @Override
        protected void onPostExecute(JSONArray wantedBoard) {
            if (wantedBoard.length() == 1) {
                JSONObject board = null;
                try {
                    board = wantedBoard.getJSONObject(0);
                    String boardName = board.getString("BoardName");
                    Globals.boardTypes boardType = Globals.boardTypes.fromOrdinal(board.getInt("BoardType"));
                    User creator = new User(board.getInt("CreatorID"));
                    currBoard = new Board(boardID, boardName, creator, boardType);
                    MyBoardActivity.this.setTitle(boardName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new GetUserBoardAuth().execute(new ApiConnector());
            }


        }
    }

    private class GetUserBoardAuth extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... apiConnectors) {
            return apiConnectors[0].getUserBoardAuth(boardID, ((ShareBoardApplication)getApplication()).getCurrUser().getUserId());
        }

        @Override
        protected void onPostExecute(JSONArray wantedAuth) {
            if (wantedAuth.length() == 1) {
                JSONObject auth = null;
                try {
                    auth = wantedAuth.getJSONObject(0);
                    currUserAuth = Globals.userAuthType.fromOrdinal(auth.getInt("PermissionCode"));
                    invalidateOptionsMenu();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (currUserAuth != null) {
                new GetBoardAds().execute(new ApiConnector());
            }
        }
    }

    private class GetBoardAds extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... apiConnectors) {
            return apiConnectors[0].getBoardAds(boardID);
        }

        @Override
        protected void onPostExecute(JSONArray boardAds) {
            if (boardAds != null) {
                for (int boardIndex = 0; boardIndex < boardAds.length(); boardIndex++) {
                    JSONObject ad = null;
                    try {
                        ad = boardAds.getJSONObject(boardIndex);
                        Integer adId = ad.getInt("AdID");
                        Integer boardId = ad.getInt("BoardID");
                        Integer userId = ad.getInt("UserID");
                        //Date createTime = ad.getString("CreateTime");
                        Globals.adType AdType = Globals.adType.fromOrdinal(ad.getInt("AdType"));
                        String adTitle = ad.getString("Title");
                        Globals.adPriority AdPriority = Globals.adPriority.fromOrdinal(ad.getInt("Priority"));
                        String adDescription = ad.getString("Desctiption");
                        String fromDate =
                                Integer.parseInt(ad.getString("FromDate").substring(8,10)) + "/" +
                                Integer.parseInt(ad.getString("FromDate").substring(5,7)) + "/" +
                                ad.getString("FromDate").substring(0,4);
                        String toDate =
                                Integer.parseInt(ad.getString("ToDate").substring(8,10)) + "/" +
                                        Integer.parseInt(ad.getString("ToDate").substring(5,7)) + "/" +
                                        ad.getString("ToDate").substring(0,4);
//                        String toDate = ad.getString("ToDate");
                        //Date lastUpdate = ad.getString("LastUpdate");
                        Integer lastUpdatedBy = ad.getInt("LastUpdateBy");
                        Ad adToAdd = new Ad(adId, boardId, userId, AdType, adTitle, AdPriority, adDescription, null, fromDate, toDate, null, lastUpdatedBy);

                        currBoardAds.add(adToAdd);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            setBoardDesign();

            dialog.dismiss();
        }
    }

    private void setBoardDesign() {
        if (Globals.boardTypes.Fridge.compareTo(currBoard.getBoardType()) == 0) {
            llBoardBackground.setBackgroundDrawable(getResources().getDrawable(R.drawable.fridge));
            for (Button btn : lstLargeNotes) {
                btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.fridge_large_note));
            }
            for (Button btn : lstSmallNotes) {
                btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.fridge_small_note));
            }
        } else if (Globals.boardTypes.Wood.compareTo(currBoard.getBoardType()) == 0) {
            llBoardBackground.setBackgroundDrawable(getResources().getDrawable(R.drawable.wood));
            for (Button btn : lstLargeNotes) {
                btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.wood_large_note));
            }
            for (Button btn : lstSmallNotes) {
                btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.wood_small_note));
            }
        } else if (Globals.boardTypes.Bulletin.compareTo(currBoard.getBoardType()) == 0) {
           llBoardBackground.setBackgroundDrawable(getResources().getDrawable(R.drawable.bulletin));
            for (Button btn : lstLargeNotes) {
                btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bulletin_large_note));
            }
            for (Button btn : lstSmallNotes) {
                btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.bulletin_small_note));
            }
        } else if (Globals.boardTypes.WhiteBoard.compareTo(currBoard.getBoardType()) == 0) {
            llBoardBackground.setBackgroundDrawable(getResources().getDrawable(R.drawable.whiteboard));
            for (Button btn : lstLargeNotes) {
                btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.whiteboard_large_note));
            }
            for (Button btn : lstSmallNotes) {
                btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.whiteboard_small_note));
            }
        }

        int largeNotesCount = 0;
        int smallNotesCount = 0;

        if (currBoardAds.size() == 0) {
            //emptyBoardText.setVisibility(View.VISIBLE);
        } else {

            for (Ad ad : currBoardAds) {
                if (Globals.adPriority.High.compareTo(ad.getAdPriority()) == 0) {
                    if (largeNotesCount < lstLargeNotes.size()) {
                        buttonAds.put(lstLargeNotes.get(largeNotesCount), ad);
                        largeNotesCount++;
                    }
                } else {
                    if (smallNotesCount < lstSmallNotes.size()) {
                        buttonAds.put(lstSmallNotes.get(smallNotesCount), ad);
                        smallNotesCount++;
                    }
                }
            }

            for (Button btn : buttonAds.keySet()) {
                btn.setVisibility(View.VISIBLE);
                btn.setClickable(true);
                btn.setText(buttonAds.get(btn).getAdTitle());
                btn.setTag(buttonAds.get(btn).getAdId());
                btn.setOnClickListener(onAdClicked);
            }
        }
    }
}
