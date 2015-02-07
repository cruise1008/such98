package me.tgmerge.such98;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.Header;

import java.util.Vector;


public class DisplayActivity extends ActionBarActivity {

    private Activity that;
    private EditText text;

    Vector<APIUtil.APIRequest> tests = new Vector<>();
    int testNo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        text = (EditText) findViewById(R.id.editText);
        that = this;

        class MyCallback extends APIUtil.APICallback {
            @Override
            public void onSuccess(int statCode, Header[] headers, byte[] body) {
                if (body != null) {
                    text.setText(new String(body));
                } else {
                    text.setText("Empty response, statcode=" + statCode);
                }
            }
            @Override
            public void onFailure(int statCode, Header[] headers, byte[] body, Throwable error) {
                text.setText("ERROR " + statCode + ", " + error.toString());
            }
        }

        // topic
        tests.add(new APIUtil.GetNewTopic(0, null, 10, new MyCallback()));
        tests.add(new APIUtil.GetBoardTopic(100, 0, null, 10, new MyCallback()));
        tests.add(new APIUtil.GetHotTopic(0, null, 10, new MyCallback()));
            // untested: PostBoardTopic
        tests.add(new APIUtil.GetTopic(4473926, new MyCallback()));

        // post
            // untested: tests.add(new APIUtil.PostTopicPost(2803718, "title", "content", new MyCallback()));
        tests.add(new APIUtil.GetTopicPost(2803718, 0, null, 10, new MyCallback()));
        tests.add(new APIUtil.GetPost(786144012, new MyCallback()));
            // untested: tests.add(new APIUtil.PutPost();

        // user
        tests.add(new APIUtil.GetNameUser("tgmerge", new MyCallback()));
        tests.add(new APIUtil.GetIdUser(389794, new MyCallback()));

        // board
        tests.add(new APIUtil.GetRootBoard(0, null, 10, new MyCallback()));
        tests.add(new APIUtil.GetSubBoards(6, 0, null, 5, new MyCallback()));
        tests.add(new APIUtil.GetBoard(6, new MyCallback()));
        int [] a = {6, 100};
        tests.add(new APIUtil.GetMultiBoards(a, 0, null, 10, new MyCallback()));

        // me
        tests.add(new APIUtil.GetMe(new MyCallback()));
        tests.add(new APIUtil.GetBasicMe(new MyCallback()));
        tests.add(new APIUtil.GetCustomBoardsMe(new MyCallback()));
            // untested: put me

        // Systemconfig
        tests.add(new APIUtil.GetSystemSetting(new MyCallback()));

        // message
        tests.add(new APIUtil.GetMessage(23878541, new MyCallback()));
        tests.add(new APIUtil.GetUserMessage("tgmerge", APIUtil.GetUserMessage.FILTER_SEND, 0, null, 10, new MyCallback()));
        tests.add(new APIUtil.DeleteMessage(23880005, new MyCallback()));
            // untested: put message
        tests.add(new APIUtil.PostMessage("tgmerge", "testTitle", "testContent", new MyCallback()));
    }

    public void test() {
        if(testNo < tests.size()) {
            Toast.makeText(that, "Test #" + testNo + ", " + tests.get(testNo).getClass().getName(), Toast.LENGTH_LONG).show();
            tests.get(testNo).execute();
            testNo ++;
        } else {
            Toast.makeText(that, "Test all done", Toast.LENGTH_LONG).show();
        }
    }

    public void onTestButtonClicked(View view) {
        test();
    }

    public void onReloginButtonClicked(View view) {
        OAuthUtil oa = OAuthUtil.getInstance();
        if (oa != null) {
            oa.clearToken();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display, menu);
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
}
