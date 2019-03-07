package prj666.a03.cryptboard.Tests;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import prj666.a03.cryptboard.RSAStrings.RSAStrings;
import prj666.a03.cryptboard.frontEndHelper;

public class tester {

    public boolean test_KeyCration(){
        KeyPair tmp = null;
        Boolean x = false;
        try {
            tmp = RSAStrings.getKeys();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if(tmp!= null){
            System.out.print(String.format("|%15.15s||%10.10s||\n","KeyCration","Pass"));
            x = true;
        }

        return true;
    }

    public boolean test_databaseLoaded(){
        boolean x = true;
        try{frontEndHelper.getInstance().getNamesAll();}
        catch(Exception e){
            x = false;
        }
        System.out.print(String.format("|%15.15s||%10.10s||\n","Load Database",x));
        return  x;
    }

    public tester(){
        int passes = 0;
        int fails = 0;
        System.out.print(String.format("|%15.15s||%10.10s||\n","Test Type","Pass/Fail"));
        System.out.print(String.format("------------------------------\n"));

        // TEST 1 tests to see if keys can be made
        if(test_KeyCration()){passes++;}
        else{fails++;}

        if(test_databaseLoaded()){passes++;}
        else{fails++;}

        System.out.print(String.format("------------------------------\n"));
        System.out.print(String.format("|%15.15s||%10.10s||\n","Passes","Fails"));
        System.out.print(String.format("------------------------------\n"));
        System.out.print(String.format("|%15.15s||%10.10s||\n",passes,fails));
    }





}
