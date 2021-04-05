package com.iramml.zirusapp.user.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import java.util.*


class Utilities {
    companion object {
        fun displayMessage(view: View?, context: Context?, toastString: String) {
            if (view != null) {
                try {
                    Snackbar.make(view, toastString, Snackbar.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    try {
                        Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show()
                    } catch (ee: Exception) {
                        ee.printStackTrace()
                    }
                }
            } else {
                try {
                    Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show()
                } catch (ee: Exception) {
                    ee.printStackTrace()
                }
            }
        }

        fun hideKeypad(context: Context, view: View?) {
            if (view != null) {
                val imm: InputMethodManager =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        fun hideKeyboard(activity: Activity) {
            val imm: InputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view: View? = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun <T> getPageOfList(sourceList: List<T>?, page: Int, pageSize: Int): List<T>? {
            require(!(pageSize <= 0 || page <= 0)) { "invalid page size: $pageSize" }

            val fromIndex = (page - 1) * pageSize
            return if (sourceList == null || sourceList.size <= fromIndex) {
                Collections.emptyList()
            } else {
                sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size))
            }
        }
    }
}