package net.twisterrob.real.test

import android.support.annotation.CheckResult
import android.support.annotation.IntRange
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom
import android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.v7.widget.RecyclerView
import android.view.View
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

@CheckResult(suggest = "onItemView")
internal fun scrollToInsidePosition(@IntRange(from = 0) position: Int): ViewAction =
	object : ViewAction {
		private val listMatcher =
			allOf(isAssignableFrom(RecyclerView::class.java), isDisplayed())

		override fun getConstraints(): Matcher<View> =
			isDescendantOfA(listMatcher)

		override fun getDescription(): String =
			"scroll to view at RecyclerView position $position"

		override fun perform(uiController: UiController, view: View) {
			// no point in scrolling if the view is already fully visible
			if (isDisplayed().matches(view)) return

			val recyclerView = generateSequence(view.parent) { it.parent }
				.find { listMatcher.matches(it) } as? RecyclerView
				?: return

			// make sure list item's ViewHolder is bound
			recyclerView.scrollToPosition(position)
			// make sure the view inside the list item is visible by moving it to the top of the RecyclerView
			recyclerView.smoothScrollBy(0, -recyclerView.verticalOffset(view))
			uiController.loopMainThreadUntilIdle()
		}

		private fun View.verticalOffset(view: View): Int {
			val loc = intArrayOf(0, 0)
			this.getLocationOnScreen(loc)
			val parentY = loc[1]
			view.getLocationOnScreen(loc)
			val childY = loc[1]
			return parentY - childY
		}
	}
