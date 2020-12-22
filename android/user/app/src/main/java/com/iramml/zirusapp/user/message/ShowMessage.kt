package com.iramml.zirusapp.user.message

import android.content.Context
import android.view.View
import android.widget.Toast
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.util.Utilities


class ShowMessage {
    companion object {
        fun message(root: View?, context: Context, messages: Messages?) {
            var message = ""
            when (messages) {
                Messages.RATIONALE_LOCATION -> message = context.resources.getString(R.string.permission_denied_location)
                Messages.REQUEST_SENT_SUCCESS -> message = context.resources.getString(R.string.request_sent)
            }

            displayMessage(root, context, message)
        }

        fun messageForm(root: View?, context: Context, messages: FormMessages?) {
            var message = ""
            when (messages) {
                FormMessages.FILL_FIRST_NAME -> message = context.resources.getString(R.string.insert_first_name)
                FormMessages.FILL_LAST_NAME -> message = context.resources.getString(R.string.insert_last_name)
                FormMessages.FILL_EMAIL -> message = context.resources.getString(R.string.insert_email)
                FormMessages.FILL_PASSWORD -> message = context.resources.getString(R.string.insert_password)
                FormMessages.FILL_PHONE -> message = context.resources.getString(R.string.insert_phone)
                FormMessages.FILL_RUT -> message = context.resources.getString(R.string.insert_rut)
                FormMessages.FILL_ADDRESS -> message = context.resources.getString(R.string.insert_address)
                FormMessages.FILL_DESCRIPTION -> message = context.resources.getString(R.string.insert_description)
                FormMessages.SELECT_PHOTO -> message = context.resources.getString(R.string.select_photo)
            }

            displayMessage(root, context, message)
        }

        fun messageError(root: View?, context: Context, errors: Errors?) {
            var message = ""

            when (errors) {
                Errors.FAILED -> message = context.resources.getString(R.string.failed)
                Errors.LOCATION_PERMISSION_DENIED -> message = context.resources.getString(R.string.permission_denied)
            }

            displayMessage(root, context, message)
        }

        private fun displayMessage(root: View?, context: Context, message: String) {
            Utilities.displayMessage(root, context, message)
        }
    }
}