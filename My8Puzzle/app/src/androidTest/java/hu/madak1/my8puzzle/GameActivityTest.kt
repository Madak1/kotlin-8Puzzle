package hu.madak1.my8puzzle

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameActivityTest {
    companion object {
        @BeforeClass
        @JvmStatic
        fun setupTestClass() {
            System.setProperty("test", "true")
        }
    }

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testCanGoToTheClassicMode() {
        Espresso.onView(withId(R.id.main_start_btn)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.start_number_btn)).perform(ViewActions.click())
    }

    @Test
    fun testCanGoToThePictureMode() {
        Espresso.onView(withId(R.id.main_start_btn)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.start_picture_btn)).perform(ViewActions.click())
    }

    @Test
    fun testTileTap() {
        Espresso.onView(withId(R.id.main_start_btn)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.start_number_btn)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile11_iv)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile12_iv)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile13_iv)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile21_iv)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile22_iv)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile23_iv)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile31_iv)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile32_iv)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile33_iv)).perform(ViewActions.click())
    }

    @Test
    fun testTileTapWhilePause() {
        Espresso.onView(withId(R.id.main_start_btn)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.start_number_btn)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.game_pause_btn)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile11_iv)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile12_iv)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile13_iv)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile21_iv)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile22_iv)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile23_iv)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile31_iv)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile32_iv)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile33_iv)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.game_pause_btn)).perform(ViewActions.click())
    }

    @Test
    fun testRapidTileTap() {
        Espresso.onView(withId(R.id.main_start_btn)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.start_number_btn)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.board_tile11_iv)).perform(ViewActions.doubleClick())
    }

    @Test
    fun testExit() {
        Espresso.onView(withId(R.id.main_start_btn)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.start_number_btn)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.game_exit_btn)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.pop_yes_btn)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.main_logo_iv))
            .check(ViewAssertions.matches(isDisplayed()))
    }


}
