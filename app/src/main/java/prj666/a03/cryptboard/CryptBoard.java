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

import prj666.a03.cryptboard.RSAStrings.RSAStrings;

public class CryptBoard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener{

    private static final int KEYCODE_ENCRYPT = -100; // ENCRYPT KEYCODE_CAMERA
    private static final int KEYCODE_PHOTO = -107; // SEND
    private static final int KEYCODE_DECRYPT = -102; // TOGGLE
    private static final int KEYCODE_CONTACTS = -104;
    private static final int KEYCODE_CLEAR = -105;
    private static final int KEYCODE_CAM = -106;

    private static final String TAG = "ImageKeyboard";
    private static final String AUTHORITY = "com.example.android.commitcontent.ime.inputcontent";
    private static final String MIME_TYPE_PNG = "image/png";
    private File file;

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

    private boolean goodEdit = false;
    private PrivateKey privateKey = null;
    long holdStartTime = 0;
    long lastClickTime = 0;
    int lastPrimaryCode = 0;
    boolean doubleTap = false;

    private File mPngFile;

    KeyFactory rsaKeyFac;

    private String text = null;

    @Override
    public void onStartInputView(EditorInfo editorInfo, boolean restarting) {
//        goodEdit = isCommitContentSupported(editorInfo, MIME_TYPE_PNG);
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
    //@TargetApi(25)
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
                //launchCamera();
                //commitImage();  //TODO fix
//                if (goodEdit) {
//                    final File imagesDir = new File(getFilesDir(), "images");
//                    imagesDir.mkdirs();
//                    //doCommitContent("test", MIME_TYPE_PNG, getFileForResource(this, R.raw.cat, imagesDir, "image.png"));
//                }
//                else{
//                    Toast.makeText(this, "PNG not supported", Toast.LENGTH_LONG).show();
//                }
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
                EncryptPhoto.putExtra("MODE", 2);
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
    public void swipeDown() {
        toggleStegMode(false);
    }

    @Override
    public void swipeUp() {
        toggleStegMode(true);
    }

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

    private void setMessage(String message){
        clearMessage();
        ic.commitText(message,1);
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

    private void launchCamera(){
        Intent camera = new Intent(this, CarrierSelection.class);
        camera.putExtra("MODE", 1);
        startActivity(camera);
    }
 /*
    public void commitImage() {
        //Uri path = Uri.parse("android.resource://prj666.a03.cryptboard/" + R.raw.cat);
        //commitPngImage(path, "cat");
    }


    *//*
     * Commits a PNG image
     *
     * @param contentUri Content URI of the GIF image to be sent
     * @param imageDescription Description of the GIF image to be sent
     *//*
    public void commitPngImage(Uri contentUri, String imageDescription) {
        InputContentInfoCompat inputContentInfo = new InputContentInfoCompat(
                contentUri,
                new ClipDescription(imageDescription, new String[]{MIME_TYPE_PNG}),
                null
        );
        EditorInfo editorInfo = getCurrentInputEditorInfo();
        Integer flags = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            flags |= InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
        }
        InputConnectionCompat.commitContent(
                ic, editorInfo, inputContentInfo, flags, null);
    }

    private boolean isCommitContentSupported(
            @Nullable EditorInfo editorInfo, @NonNull String mimeType){
        if (editorInfo == null) {
            return false;
        }

        ic = getCurrentInputConnection();
        if (ic == null) {
            return false;
        }

        if (!validatePackageName(editorInfo)) {
            return false;
        }

        final String[] supportedMimeTypes = EditorInfoCompat.getContentMimeTypes(editorInfo);

        for (String supportedMimeType : supportedMimeTypes) {
            if (ClipDescription.compareMimeTypes(mimeType, supportedMimeType)) {
                return true;
            }
        }
        return false;
    }

    private boolean validatePackageName(@Nullable EditorInfo editorInfo) {
        if (editorInfo == null) {
            return false;
        }
        final String packageName = editorInfo.packageName;
        if (packageName == null) {
            return false;
        }

        // In Android L MR-1 and prior devices, EditorInfo.packageName is not a reliable identifier
        // of the target application because:
        //   1. the system does not verify it [1]
        //   2. InputMethodManager.startInputInner() had filled EditorInfo.packageName with
        //      view.getContext().getPackageName() [2]
        // [1]: https://android.googlesource.com/platform/frameworks/base/+/a0f3ad1b5aabe04d9eb1df8bad34124b826ab641
        // [2]: https://android.googlesource.com/platform/frameworks/base/+/02df328f0cd12f2af87ca96ecf5819c8a3470dc8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        }

        final InputBinding inputBinding = getCurrentInputBinding();
        if (inputBinding == null) {
            // Due to b.android.com/225029, it is possible that getCurrentInputBinding() returns
            // null even after onStartInputView() is called.
            // TODO: Come up with a way to work around this bug....
            Log.e(TAG, "inputBinding should not be null here. "
                    + "You are likely to be hitting b.android.com/225029");
            return false;
        }
        final int packageUid = inputBinding.getUid();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final AppOpsManager appOpsManager =
                    (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            try {
                appOpsManager.checkPackage(packageUid, packageName);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        final PackageManager packageManager = getPackageManager();
        final String possiblePackageNames[] = packageManager.getPackagesForUid(packageUid);
        for (final String possiblePackageName : possiblePackageNames) {
            if (packageName.equals(possiblePackageName)) {
                return true;
            }
        }
        return false;
    }

    private void doCommitContent(@NonNull String description, @NonNull String mimeType,
                                 @NonNull File file) {
        final EditorInfo editorInfo = getCurrentInputEditorInfo();

        // Validate packageName again just in case.
        if (!validatePackageName(editorInfo)) {
            return;
        }

        final Uri contentUri = FileProvider.getUriForFile(this, AUTHORITY, file);

        // As you as an IME author are most likely to have to implement your own content provider
        // to support CommitContent API, it is important to have a clear spec about what
        // applications are going to be allowed to access the content that your are going to share.
        final int flag;
        if (Build.VERSION.SDK_INT >= 25) {
            // On API 25 and later devices, as an analogy of Intent.FLAG_GRANT_READ_URI_PERMISSION,
            // you can specify InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION to give
            // a temporary read access to the recipient application without exporting your content
            // provider.
            flag = InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
        } else {
            // On API 24 and prior devices, we cannot rely on
            // InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION. You as an IME author
            // need to decide what access control is needed (or not needed) for content URIs that
            // you are going to expose. This sample uses Context.grantUriPermission(), but you can
            // implement your own mechanism that satisfies your own requirements.
            flag = 0;
            try {
                // TODO: Use revokeUriPermission to revoke as needed.
                grantUriPermission(
                        editorInfo.packageName, contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception e){
                Log.e(TAG, "grantUriPermission failed packageName=" + editorInfo.packageName
                        + " contentUri=" + contentUri, e);
            }
        }

        final InputContentInfoCompat inputContentInfoCompat = new InputContentInfoCompat(
                contentUri,
                new ClipDescription(description, new String[]{mimeType}),
                null *//* linkUrl *//*);
        InputConnectionCompat.commitContent(
                getCurrentInputConnection(), getCurrentInputEditorInfo(), inputContentInfoCompat,
                flag, null);
    }

    public void saveAndGetImage(Context inContext, Bitmap inImage){
        try (FileOutputStream out = new FileOutputStream("test.jpeg")) {
            inImage.compress(Bitmap.CompressFormat.PNG, 100, out);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", "test");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static File getFileForResource(
            @NonNull Context context, @RawRes int res, @NonNull File outputDir,
            @NonNull String filename) {
        final File outputFile = new File(outputDir, filename);
        final byte[] buffer = new byte[4096];
        InputStream resourceReader = null;
        try {
            try {
                resourceReader = context.getResources().openRawResource(res);
                OutputStream dataWriter = null;
                try {
                    dataWriter = new FileOutputStream(outputFile);
                    while (true) {
                        final int numRead = resourceReader.read(buffer);
                        if (numRead <= 0) {
                            break;
                        }
                        dataWriter.write(buffer, 0, numRead);
                    }
                    return outputFile;
                } finally {
                    if (dataWriter != null) {
                        dataWriter.flush();
                        dataWriter.close();
                    }
                }
            } finally {
                if (resourceReader != null) {
                    resourceReader.close();
                }
            }
        } catch (IOException e) {
            return null;
        }
    }


*/
}
