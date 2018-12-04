package prj666.a03.cryptboard;


import android.app.AppOpsManager;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.v13.view.inputmethod.EditorInfoCompat;
import android.support.v13.view.inputmethod.InputConnectionCompat;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;

import prj666.a03.cryptboard.ContactBase.DatabaseHandler;
import prj666.a03.cryptboard.RSAStrings.RSAStrings;

public class CryptBoard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener{

    private static final int KEYCODE_ENCRYPT = -100; // ENCRYPT KEYCODE_CAMERA
    private static final int KEYCODE_PHOTO = -107; // SEND
    private static final int KEYCODE_DECRYPT = -102; // TOGGLE
    private static final int KEYCODE_CONTACTS = -104;
    private static final int KEYCODE_CLEAR = -105;
    private static final int KEYCODE_CAM = -106;

    private KeyboardView keyboardView;
    private Keyboard keyboard;
    private Keyboard keyboardNum;
    private Keyboard keyboardNormal;
    private Keyboard keyboardNumNormal;

    private InputConnection ic;
    private boolean caps;
    private boolean capsLock;
    private boolean numMode ;
    private boolean stegMode;


    long holdStartTime = 0;
    long lastClickTime = 0;
    int lastPrimaryCode = 0;
    boolean doubleTap = false;

    private String text = null;

    DatabaseHandler db;
    frontEndHelper control;

    @Override
    public void onStartInputView(EditorInfo editorInfo, boolean restarting) {
        if (!restarting) {
            keyboardView.setPreviewEnabled(false);
            keyboardView.setKeyboard(keyboardNormal);
            keyboardView.setOnKeyboardActionListener(this);

            caps = false;
            capsLock = false;
            numMode = false;
            stegMode = false;
        }
    }

    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this, R.xml.qwerty);
        keyboardNum = new Keyboard(this, R.xml.alt_qwerty);
        keyboardNormal = new Keyboard(this, R.xml.qwerty_normal);
        keyboardNumNormal = new Keyboard(this, R.xml.alt_qwerty_normal);

        return keyboardView;
    }

    @Override
    public void onPress(int primaryCode) {
        holdStartTime = Calendar.getInstance().getTimeInMillis();
        if (!doubleTap && lastPrimaryCode == primaryCode &&
                Calendar.getInstance().getTimeInMillis() - lastClickTime < 500){
            doubleTap = true;
        }
        lastClickTime = Calendar.getInstance().getTimeInMillis();
        lastPrimaryCode = primaryCode;
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                break;
            case Keyboard.KEYCODE_SHIFT:
                break;
            case KEYCODE_CAM:
                break;
            case KEYCODE_PHOTO:
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                break;
            case Keyboard.KEYCODE_DONE:
                break;
            case KEYCODE_ENCRYPT:
                break;
            case KEYCODE_DECRYPT:
                break;
            case KEYCODE_CONTACTS:
                break;
            case KEYCODE_CLEAR:
                break;
            default:
        }
    }

    @Override
    public void onRelease(int primaryCode) {
        doubleTap = false;
        long holdDuration = (Calendar.getInstance().getTimeInMillis() - holdStartTime);
        switch (primaryCode){
            case Keyboard.KEYCODE_DONE:
                if (holdDuration > 800) {
                    toggleStegMode(!stegMode);
                }
                else{
                    ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                }
                break;
        }
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes){
        ic = getCurrentInputConnection();
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1,0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                if (doubleTap && caps) {
                    capsLock = true;
                    lastPrimaryCode = 0;
                } else if (caps) {
                    capsLock = false;
                    caps = false;
                    keyboardView.getKeyboard().setShifted(caps);
                    keyboardView.invalidateAllKeys();
                }
                else {
                    caps = true;
                    keyboardView.getKeyboard().setShifted(caps);
                    keyboardView.invalidateAllKeys();
                }
                break;
            case KEYCODE_CAM:
                break;
            case KEYCODE_PHOTO:
                launchPhotos();
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
                break;
            case  KEYCODE_ENCRYPT:
                Intent EncryptPhoto = new Intent(this, CarrierSelection.class);
                //EncryptPhoto.putExtra("MODE", 2);
                EncryptPhoto.putExtra("Msg",getMessage());
                clearMessage();
                startActivity(EncryptPhoto);
                break;
            case KEYCODE_DECRYPT:
                Intent DecryptPhoto = new Intent(this, DecodePhoto.class);
                DecryptPhoto.putExtra("MODE", 2);
                startActivity(DecryptPhoto);
                break;
            case KEYCODE_CONTACTS:
                launchContacts();
                break;
            case KEYCODE_CLEAR:
                clearMessage();
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
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeDown() {}

    @Override
    public void swipeUp() {}

    private void launchContacts(){
        Intent contacts = new Intent(this, Contact_List_Main.class);
        startActivity(contacts);
    }

    private void launchPhotos(){
        Intent photo = new Intent(this, CarrierSelection.class);
        photo.putExtra("MODE", 2);
        startActivity(photo);
    }

    private String getMessage(){
        text = ic.getExtractedText(new ExtractedTextRequest(), 0).text.toString();
        return text;
    }

    private void clearMessage(){
        CharSequence currentText = ic.getExtractedText(new ExtractedTextRequest(), 0).text;
        CharSequence beforeCursorText = ic.getTextBeforeCursor(currentText.length(), 0);
        CharSequence afterCursorText = ic.getTextAfterCursor(currentText.length(), 0);
        ic.deleteSurroundingText(beforeCursorText.length(), afterCursorText.length());
    }

    private void toggleStegMode(boolean stegModeOn){
        if (stegModeOn) {
            if (numMode) {
                keyboardView.setKeyboard(keyboardNum);
            } else {
                keyboardView.setKeyboard(keyboard);
            }
            keyboardView.invalidateAllKeys();
            stegMode = true;

            if (frontEndHelper.getInstance() == null) {
                db = DatabaseHandler.getInstance(this);
                control = new frontEndHelper(db);
            }
        }
        else {
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
    }

}
