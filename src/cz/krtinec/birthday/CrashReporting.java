package cz.krtinec.birthday;

import org.acra.CrashReportingApplication;

import android.os.Bundle;

public class CrashReporting extends CrashReportingApplication {

	@Override
	public String getFormId() {
		return "dF9CY0t4UHgyTHJtc1dHS1JoZmJFLUE6MQ";
	}
	
	@Override
	public Bundle getCrashResources() {
	    Bundle result = new Bundle();
	    result.putInt(RES_TOAST_TEXT, R.string.crash_toast_text);
	    return result;
	}

}
