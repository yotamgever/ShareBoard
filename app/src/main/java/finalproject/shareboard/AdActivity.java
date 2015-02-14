package finalproject.shareboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import finalproject.shareboard.framework.ApiConnector;
import finalproject.shareboard.framework.Globals;
import finalproject.shareboard.model.Ad;


public class AdActivity extends ShareBoardActivity {
    Integer AdID = null;
    Integer BoardID = null;
    Globals.userAuthType userAuth = null;

    private TextView tvAdId;

    private Spinner spAdType;
    private EditText etAdTitle;
    private Spinner spAdPriority;
    private EditText etAdDesc;
    private EditText etAdFromDate;
    private EditText etAdToDate;

    private Button btnAdSave;
    private Button btnAdCancel;

    private LinearLayout llGeneralAd;
    private LinearLayout llEventAd;
    private LinearLayout llEditAdButtons;

    AlertDialog adValidDialog;

    Ad adToAdd;

    boolean bIsAtEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);

        Bundle extras = getIntent().getExtras();
        AdID = extras.getInt("AdID");
        BoardID = extras.getInt("BoardID");
        userAuth = Globals.userAuthType.fromOrdinal(extras.getInt("UserAuth"));

        invalidateOptionsMenu();

        InitializeComponent();
    }

    private void InitializeComponent() {
        tvAdId = (TextView) findViewById(R.id.tvAdIdText);

        spAdType = (Spinner) findViewById(R.id.spAdTypes);
        etAdTitle = (EditText) findViewById(R.id.etAdTitle);
        spAdPriority = (Spinner) findViewById(R.id.spAdPriority);
        etAdDesc = (EditText) findViewById(R.id.etAdDesc);
        etAdFromDate = (EditText) findViewById(R.id.etAdFromDate);
        etAdToDate = (EditText) findViewById(R.id.etAdToDate);

        btnAdSave = (Button) findViewById(R.id.btnAdSave);
        btnAdCancel = (Button) findViewById(R.id.btnAdCancel);

        llGeneralAd = (LinearLayout) findViewById(R.id.llGeneralAdSection);
        llEventAd = (LinearLayout) findViewById(R.id.llEventAdSection);
        llEditAdButtons = (LinearLayout) findViewById(R.id.llAdEditButtons);

        spAdType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == Globals.adType.General.ordinal()) {
                    llGeneralAd.setVisibility(View.VISIBLE);
                    llEventAd.setVisibility(View.INVISIBLE);
                    etAdFromDate.setText("");
                    etAdToDate.setText("");
                } else if (position == Globals.adType.Event.ordinal()){
                    llGeneralAd.setVisibility(View.INVISIBLE);
                    llEventAd.setVisibility(View.VISIBLE);
                    etAdDesc.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnAdSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertUpdateAd();
            }
        });

        btnAdCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelEditAd();
            }
        });

        if (AdID != null && AdID != 0) {
            tvAdId.setText(getResources().getText(R.string.ad_id_text) + " " + AdID.toString());
            setAdForInsertOrUpdate(true);
        } else {
            setAdForInsertOrUpdate(false);
            tvAdId.setVisibility(View.INVISIBLE);
            llEditAdButtons.setVisibility(View.VISIBLE);
        }

        AlertDialog.Builder adValidDialogBuilder = new AlertDialog.Builder(this);
        adValidDialogBuilder.setMessage("Oops! Something went wrong... Please correct ad details")
                .setCancelable(false)
                .setNeutralButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        adValidDialog = adValidDialogBuilder.create();
    }

    private void cancelEditAd() {

    }

    private void setAdForInsertOrUpdate(boolean isEdit) {
        if (!isEdit) {
            spAdType.setClickable(true);
            spAdPriority.setClickable(true);
        }

        etAdTitle.setEnabled(true);
        etAdDesc.setEnabled(true);
        etAdFromDate.setEnabled(true);
        etAdToDate.setEnabled(true);
    }

    private void insertUpdateAd() {
        if (checkAdValidity()) {
            Integer UserId = ((ShareBoardApplication)getApplication()).getCurrUser().getUserId();
            Globals.adType AdType = Globals.adType.fromOrdinal(spAdType.getSelectedItemPosition());
            String AdTitle = etAdTitle.getText().toString();
            Globals.adPriority AdPriority = Globals.adPriority.fromOrdinal(spAdPriority.getSelectedItemPosition());
            String AdDesc = etAdDesc.getText().toString();

            adToAdd = new Ad(AdID, BoardID, UserId, AdType, AdTitle, AdPriority, AdDesc, null, null, null, null, UserId);

            if ((AdID == null) || (AdID == 0)) {
                dialog.show();
                new InsertAd().execute(new ApiConnector());
            } else {

            }
        } else {
            adValidDialog.show();
        }
    }

    private boolean checkAdValidity() {
        if (etAdTitle.getText().toString() == "") {
            return false;
        }

        if ((spAdType.getSelectedItemPosition() == Globals.adType.General.ordinal()) &&
                etAdDesc.getText().toString() == "") {
            return false;
        } else if ((spAdType.getSelectedItemPosition() == Globals.adType.Event.ordinal() &&
                ((etAdFromDate.getText().toString() == "") || (etAdToDate.getText().toString() == "")))) {
            return false;
        }

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ad, menu);

        if (Globals.userAuthType.Admin.compareTo(userAuth) == 0) {
            if (!bIsAtEditMode) {
                menu.findItem(R.id.itmEditAd).setVisible(true);
                menu.findItem(R.id.itmDeleteAd).setVisible(true);
            } else {
                menu.findItem(R.id.itmEditAd).setVisible(false);
                menu.findItem(R.id.itmDeleteAd).setVisible(false);
            }
        } else if (Globals.userAuthType.Edit.compareTo(userAuth) == 0) {
            if (!bIsAtEditMode) {
                menu.findItem(R.id.itmEditAd).setVisible(true);
            } else {
                menu.findItem(R.id.itmEditAd).setVisible(false);
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
        if (id == R.id.itmEditAd) {
            llEditAdButtons.setVisibility(View.VISIBLE);
            bIsAtEditMode = true;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class InsertAd extends AsyncTask<ApiConnector, Long, Integer> {
        @Override
        protected Integer doInBackground(ApiConnector... apiConnectors) {
            return apiConnectors[0].InsertAd(adToAdd);
        }

        @Override
        protected void onPostExecute(Integer adID) {


            dialog.dismiss();
            finish();
        }
    }
}
