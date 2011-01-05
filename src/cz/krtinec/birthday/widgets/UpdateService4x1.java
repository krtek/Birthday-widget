/*
 * This file is part of Birthday Widget.
 *
 * Birthday Widget is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Birthday Widget is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Birthday Widget.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) Lukas Marek, 2011.
 */

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
