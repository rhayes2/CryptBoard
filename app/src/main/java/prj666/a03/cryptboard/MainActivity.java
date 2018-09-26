package prj666.a03.cryptboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import prj666.a03.RSAStrings.RSAStrings;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());


        /// TEST BLOCK FOR ACCESSING KEYS
        KeyPair tmpPair;
        tmpPair = null;
        try {
            tmpPair = RSAStrings.getKeys();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }finally {
            String x  = tmpPair.getPrivate().toString();
            System.out.println("----------------------------");
            System.out.println(x);
            System.out.println("----------------------------");
            System.out.println("----------------------------");
            System.out.println(x);
            System.out.println("----------------------------");

        }



    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
