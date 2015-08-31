package nfc;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;


import util.GlobalClass;

import static gcm.CommonUtilities.SHOW_PROGRESS_DIALOG;
import static gcm.CommonUtilities.TRIGGER_NFC_ACTION;


/** The class to trigger when the NFC device is contacted with NFC reader*/
@TargetApi(21)
public class ValidateHostApduService extends HostApduService {



    /** Sending the message from NFC enable device to reader*/
    @Override
	public byte[] processCommandApdu(byte[] apdu, Bundle extras) {
        byte[] returnmessage=null;
		if (selectAidApdu(apdu)) {

            returnmessage = getInitialMessage();

            Intent intent = new Intent(TRIGGER_NFC_ACTION);
            intent.putExtra(SHOW_PROGRESS_DIALOG, false);
            sendBroadcast(intent);
		}
        return returnmessage;
	}

    /** The first message from NFC enabled device to reader*/
	private byte[] getInitialMessage() {
		return "Hello Desktop!".getBytes();
	}



	private boolean selectAidApdu(byte[] apdu) {

		return apdu.length >= 2 && apdu[0] == (byte)0 && apdu[1] == (byte)0xa4;
	}

	@Override
	public void onDeactivated(int reason) {
		Log.i("HCEDEMO", "Deactivated: " + reason);
	}
}