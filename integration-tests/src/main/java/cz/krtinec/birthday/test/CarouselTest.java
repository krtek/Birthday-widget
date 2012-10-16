

package cz.krtinec.birthday.test;

import android.test.ActivityInstrumentationTestCase2;

import cz.krtinec.birthday.ui.CarouselActivity;


/**
 * Test displaying of carousel.
 */
public class CarouselTest extends ActivityInstrumentationTestCase2<CarouselActivity> {

    /**
     * Create test for {@link CarouselActivity}
     */
    public CarouselTest() {
        super(CarouselActivity.class);
    }

    /**
     * Verify activity exists
     */
    public void testActivityExists() {
        assertNotNull(getActivity());
    }
}
