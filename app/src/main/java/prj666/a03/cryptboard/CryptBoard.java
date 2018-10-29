package prj666.a03.cryptboard;


import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.PopupWindow;
import android.widget.Toast;

public class CryptBoard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener{

    private static final int KEYCODE_CAMERA = -100;
    private static final int KEYCODE_PHOTO = -101;
    private static final int KEYCODE_DOCUMENT = -102;
    private static final int KEYCODE_TOGGLE = -103;
    private static final int KEYCODE_CONTACTS = -104;
    private static final int KEYCODE_ADD = -105;
    private static final int KEYCODE_MATH_MODE = -200;

    private KeyboardView keyboardView;
    private Keyboard keyboard;
    private Keyboard keyboardNum;
    private Keyboard keyboardNormal;
    private Keyboard keyboardNumNormal;
    private View contactsView;
    private PopupWindow popup;

    private InputConnection ic;
    private boolean caps = false;
    private boolean numMode = false;
    private boolean unlock = true;
    private boolean stegMode = true;


    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this, R.xml.qwerty);
        keyboardNum = new Keyboard(this, R.xml.alt_qwerty);
        keyboardNormal = new Keyboard(this, R.xml.qwerty_normal);
        keyboardNumNormal = new Keyboard(this, R.xml.alt_qwerty_normal);

        contactsView = getLayoutInflater().inflate(R.layout.activity_main, null);
        popup = new PopupWindow();
        popup.setContentView(contactsView);
        popup.setWidth(400);
        popup.setHeight(400);
        popup.setClippingEnabled(false);


        keyboardView.setPreviewEnabled(false);
        keyboardView.setKeyboard(keyboardNormal);
        keyboardView.setOnKeyboardActionListener(this);

        return keyboardView;
    }

    private void playClick(int keyCode) {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
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
        }
    }
    @Override
    public void onPress(int primaryCode) {
        ic = getCurrentInputConnection();
        playClick(primaryCode);
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1,0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                caps = !caps;
                keyboardView.getKeyboard().setShifted(caps);
                keyboardView.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                caps = false;
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
                break;
            case  KEYCODE_CAMERA:
                break;
            case KEYCODE_PHOTO:
                break;
            case KEYCODE_DOCUMENT:
                break;
            case KEYCODE_TOGGLE:
                Toast.makeText(this, "Toggle!", Toast.LENGTH_SHORT).show();
                break;
            case KEYCODE_CONTACTS:
                //popup.showAtLocation(keyboardView,0,0, -1);
                //Toast.makeText(this, "Contacts!", Toast.LENGTH_SHORT).show();
                Intent contacts = new Intent(this, Contact_List_Main.class);
                //Intent contacts = new Intent(this, Contact_List.class);

                startActivity(contacts);
                break;
            case KEYCODE_ADD:
                break;
            case KEYCODE_MATH_MODE:
                break;
            default:
                char code = (char) primaryCode;
                if(Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code),1);
        }

    }

    @Override
    public void onRelease(int primaryCode) {

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
            unlock = false;
            stegMode = false;
        }
        else{
            unlock = true;
        }

    }

    @Override
    public void swipeUp() {
        if (unlock) {
            if (numMode){
                keyboardView.setKeyboard(keyboardNum);
            }
            else{
                keyboardView.setKeyboard(keyboard);
            }
            keyboardView.invalidateAllKeys();
            stegMode = true;
            unlock = true;
        }
    }

}
