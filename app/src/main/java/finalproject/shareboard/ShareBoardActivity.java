package finalproject.shareboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class ShareBoardActivity extends Activity {
    ProgressDialog dialog = null;
    AlertDialog backButtonPressedDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setTitle("");
            dialog.setMessage("Loading. Please wait...");
            dialog.setCancelable(false);
        }

        AlertDialog.Builder backButtonPressedDialogBuilder;

        backButtonPressedDialogBuilder = new AlertDialog.Builder(this);
        backButtonPressedDialogBuilder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int id)
                            { dialogInterface.cancel();} })
                .setNegativeButton("Yes", new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int id)
                            {ShareBoardActivity.this.finish(); } });
        backButtonPressedDialog = backButtonPressedDialogBuilder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shareboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.item_exit_application) {
//            finish();
//            android.os.Process.killProcess(android.os.Process.myPid());
//            //System.exit(1);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backButtonPressedDialog.show();
    }
}
