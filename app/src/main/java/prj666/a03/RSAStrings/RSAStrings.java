package prj666.a03.RSAStrings;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.System.out;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;



/**
 *
 * @author toyz89
 */
public class RSAStrings {
        public static void main(String [] args) throws Exception {}


    public static KeyPair getKeys() throws NoSuchAlgorithmException{
        final int keySize= 2048;
        KeyPairGenerator pair = KeyPairGenerator.getInstance("RSA"); // HERE WE CAN SPECIFY THE ALG
        pair.initialize(keySize);
        return pair.genKeyPair();}

    public static byte[] encryptString(PublicKey priv, String msg) throws
            NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException{
        Cipher crypt = Cipher.getInstance("RSA");
        crypt.init(Cipher.ENCRYPT_MODE, priv);
        return crypt.doFinal(msg.getBytes());}

    public static byte[] decryptString(PrivateKey pub, byte [] encrypted) throws Exception {
        Cipher crypt = Cipher.getInstance("RSA");
        crypt.init(Cipher.DECRYPT_MODE, pub);
        return crypt.doFinal(encrypted);}

    public static void savePrivkey(PrivateKey privkey, String Fname) throws IOException{
        FileOutputStream out = new FileOutputStream(Fname + ".key");
        out.write(privkey.getEncoded());
        out.close();
    }

    public static void savePubkey(PublicKey pubkey, String Fname) throws IOException{
        FileOutputStream out = new FileOutputStream(Fname + ".pub");
        out.write(pubkey.getEncoded());
        out.close();}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static PublicKey loadPub(String pub) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
        Path path = Paths.get(pub);
        byte[] bytes = Files.readAllBytes(path);

        /* Generate public key. */
        X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pubkey1 = kf.generatePublic(ks);

        return pubkey1;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static PrivateKey loadPriv(String pub) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
        Path path = Paths.get(pub);
        byte[] bytes = Files.readAllBytes(path);

        /* Generate public key. */
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privkey1 = kf.generatePrivate(ks);

        return privkey1;
    }
}