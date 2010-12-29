package cz.krtinec.birthday.widgets;

import android.content.ComponentName;
import cz.krtinec.birthday.R;

public class UpdateService4x1 extends UpdateService2x2 {

	@Override
	int getLayout() {
		return R.layout.widget4x1;
	}

	@Override
	public ComponentName getComponentName() {
		return new ComponentName(this, BirthdayWidget4x1.class);
	}
}
