package nfc;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import com.securepreferences.SecurePreferences;

import util.GlobalClass;

@TargetApi(21)
public class ValidateHostApduService extends HostApduService {


    /** SharedPreferences to store and retrieve values. SecurePreferences is used for securely storing and retrieving.*/
    SharedPreferences sharedPreferences;

    /** Tags declaration*/
    String mTagWorkNumber = GlobalClass.WORK_NUMBER;
    String mTagHomeNumber = GlobalClass.HOME_NUMBER;


    /** Sending the message from NFC enable device to reader*/
    @Override
	public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
		if (selectAidApdu(apdu)) {

			return getInitialMessage();
		}
		else {

			return getUserStoredMessage();
		}
	}

    /** The first message from NFC enabled device to reader*/
	private byte[] getInitialMessage() {
		return "Hello Desktop!".getBytes();
	}

    /** Consecutive message from NFC enabled device to reader*/
	private byte[] getUserStoredMessage() {

        /** Get the stored data of the user and pass to the NFC reader*/
        sharedPreferences = new SecurePreferences(this);
        String msendMessage =  sharedPreferences.getString(mTagWorkNumber,"")+","+sharedPreferences.getString(mTagHomeNumber,"");
        return msendMessage.getBytes();
	}


	private boolean selectAidApdu(byte[] apdu) {

		return apdu.length >= 2 && apdu[0] == (byte)0 && apdu[1] == (byte)0xa4;
	}

	@Override
	public void onDeactivated(int reason) {
		Log.i("HCEDEMO", "Deactivated: " + reason);
	}
}