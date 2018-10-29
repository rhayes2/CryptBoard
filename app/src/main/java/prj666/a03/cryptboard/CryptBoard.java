package prj666.a03.cryptboard;


import android.annotation.TargetApi;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Calendar;

import prj666.a03.cryptboard.RSAStrings.RSAStrings;

public class CryptBoard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener{

    private static final int KEYCODE_ENCRYPT = -100; // ENCRYPT KEYCODE_CAMERA
    private static final int KEYCODE_PHOTO = -101; // SEND
    private static final int KEYCODE_DECRYPT = -102; // TOGGLE
    private static final int KEYCODE_CONTACTS = -104;
    private static final int KEYCODE_CLEAR = -105;
    private static final int KEYCODE_MATH_MODE = -200;

    private KeyboardView keyboardView;
    private Keyboard keyboard;
    private Keyboard keyboardNum;
    private Keyboard keyboardNormal;
    private Keyboard keyboardNumNormal;

    private InputConnection ic;
    private boolean caps = false;
    private boolean capsLock = false;
    private boolean numMode = false;
    private boolean stegMode = false;

    private String text = "";
    private String decryptedText = "";
    private String encryptedText = "";
    private PublicKey  publicKey =  null;
    private PrivateKey privateKey = null;

    long currentTime = 0;

    @Override

    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this, R.xml.qwerty);
        keyboardNum = new Keyboard(this, R.xml.alt_qwerty);
        keyboardNormal = new Keyboard(this, R.xml.qwerty_normal);
        keyboardNumNormal = new Keyboard(this, R.xml.alt_qwerty_normal);

        keyboardView.setPreviewEnabled(false);
        keyboardView.setKeyboard(keyboardNormal);
        keyboardView.setOnKeyboardActionListener(this);

        return keyboardView;
    }

    private void playClick(int keyCode) {
/*        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        switch(keyCode) {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }*/
    }
    @Override
    @TargetApi(25)
    public void onPress(int primaryCode) {
        ic = getCurrentInputConnection();
        playClick(primaryCode);
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1,0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                if (capsLock) {
                    capsLock = false;
                    caps = false;
                    keyboardView.getKeyboard().setShifted(caps);
                    keyboardView.invalidateAllKeys();
                }
                else {
                    if (caps) {
                        capsLock = true;
                    }
                    else {
                        caps = true;
                        keyboardView.getKeyboard().setShifted(caps);
                        keyboardView.invalidateAllKeys();
                    }
                }
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                caps = false;
                capsLock = false;
                keyboardView.getKeyboard().setShifted(caps);

                numMode = !numMode;
                if (stegMode) {
                    if (numMode)
                        keyboardView.setKeyboard(keyboardNum);
                    else
                        keyboardView.setKeyboard(keyboard);
                }
                else{
                    if (numMode)
                        keyboardView.setKeyboard(keyboardNumNormal);
                    else
                        keyboardView.setKeyboard(keyboardNormal);
                }
                keyboardView.invalidateAllKeys();
                break;

            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                currentTime = Calendar.getInstance().getTimeInMillis();
                break;
            case  KEYCODE_ENCRYPT:
                EncryptMessage();
                break;
            case KEYCODE_PHOTO:
                getMessage();
                break;
            case KEYCODE_DECRYPT:
                DecryptMessage();
                break;
            case KEYCODE_CONTACTS:
                Intent contacts = new Intent(this, Contact_List_Main.class);
                startActivity(contacts);
                break;
            case KEYCODE_CLEAR:
                clearMessage();
                break;
            case KEYCODE_MATH_MODE:
                break;
            default:
                char code = (char) primaryCode;
                if(Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code),1);
                if (!capsLock && caps){
                    caps = false;
                    keyboardView.getKeyboard().setShifted(caps);
                    keyboardView.invalidateAllKeys();
                }
        }

    }

    @Override
    public void onRelease(int primaryCode) {
        // this isnt working
/*        if (primaryCode == Keyboard.KEYCODE_DONE){
            if (stegMode) {
                if (numMode) {
                    keyboardView.setKeyboard(keyboardNumNormal);
                }
                else {
                    keyboardView.setKeyboard(keyboardNormal);
                }
                keyboardView.invalidateAllKeys();
                stegMode = false;
            }
            else {
                if (numMode) {
                    keyboardView.setKeyboard(keyboardNum);
                } else {
                    keyboardView.setKeyboard(keyboard);
                }
                keyboardView.invalidateAllKeys();
                stegMode = true;
            }
        }*/
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeDown() {
        if (stegMode) {
            if (numMode){
                keyboardView.setKeyboard(keyboardNumNormal);
            }
            else{
                keyboardView.setKeyboard(keyboardNormal);
            }
            keyboardView.invalidateAllKeys();
            stegMode = false;
        }
    }

    @Override
    public void swipeUp() {
        if (numMode){
            keyboardView.setKeyboard(keyboardNum);
        }
        else{
            keyboardView.setKeyboard(keyboard);
        }
        keyboardView.invalidateAllKeys();
        stegMode = true;
    }

/*    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", "test");
        return Uri.parse(path);
    }

    public void commitImage(Uri contentUri, String imageDescription) {
        InputContentInfoCompat inputContentInfo = new InputContentInfoCompat(
                contentUri,
                new ClipDescription(imageDescription, new String[]{"image/gif"}),
                null
        );
        InputConnection inputConnection = ic;
        EditorInfo editorInfo = getCurrentInputEditorInfo();
        int flags = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            flags |= InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
        }
        InputConnectionCompat.commitContent(
                inputConnection, editorInfo, inputContentInfo, flags, null);
    }

    public void saveAndGetImage(Context inContext, Bitmap inImage){

        try (FileOutputStream out = new FileOutputStream("test.jpeg")) {
            inImage.compress(Bitmap.CompressFormat.PNG, 100, out);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", "test");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

    private void getMessage(){
        text = ic.getExtractedText(new ExtractedTextRequest(), 0).text.toString();
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void clearMessage(){
        CharSequence currentText = ic.getExtractedText(new ExtractedTextRequest(), 0).text;
        CharSequence beforeCursorText = ic.getTextBeforeCursor(currentText.length(), 0);
        CharSequence afterCursorText = ic.getTextAfterCursor(currentText.length(), 0);
        ic.deleteSurroundingText(beforeCursorText.length(), afterCursorText.length());
    }

    private void EncryptMessage(){
        try {
            getMessage();
            RSAStrings.encryptString(publicKey, text.trim());
            setMessage(encryptedText);
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void DecryptMessage(){
        try {
            getMessage();
            decryptedText = RSAStrings.decryptString(privateKey, text.trim().getBytes()).toString();
            setMessage(decryptedText);
            Toast.makeText(this, decryptedText, Toast.LENGTH_LONG).show();
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setMessage(String message){
        clearMessage();
        ic.commitText(message,1);
    }


/*    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }*/
}
