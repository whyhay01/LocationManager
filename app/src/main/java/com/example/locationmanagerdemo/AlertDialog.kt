package com.example.locationmanagerdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.locationmanagerdemo.databinding.AlertDialogBinding


class AlertDialog : DialogFragment() {

    private lateinit var title: String
    private lateinit var message: String
    private var positiveButtonTitle: String? = null
    private var negativeButtonTitle: String? = null
    private lateinit var positiveButtonClick: AlertDialogFragmentListener
    private var negativeButtonClick: AlertDialogFragmentListener? =  null
    private var showNegativeButton = false

    private lateinit var binding: AlertDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val arg = arguments
            ?: throw IllegalArgumentException("Create the instance of this dialog using the newInstance method")

        arg?.let {
            title = it.getString(ARGS_TITLE)!!
            message = it.getString(ARGS_MESSAGE)!!
            positiveButtonTitle = it.getString(ARGS_POSITIVE_BUTTON_TEXT)
            negativeButtonTitle = it.getString(ARGS_NEGATIVE_BUTTON_TEXT)
            showNegativeButton = it.getBoolean(ARGS_SHOW_NEGATIVE_BUTTON)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<AlertDialogBinding?>(
            inflater,
            R.layout.alert_dialog,
            container,
            false
        ).apply {
            title.text = this@AlertDialog.title
            message.text = this@AlertDialog.message
            positiveButton.text = positiveButtonTitle
            negativeButton.text = negativeButtonTitle
            negativeButton.setOnClickListener {
                negativeButtonClick?.onclick()
                dismiss()
            }
            positiveButton.setOnClickListener {
                if (::positiveButtonClick.isInitialized){
                    positiveButtonClick.onclick()
                    dismiss()
                }
            }


        }
        return binding.root
    }

    companion object {
        private const val ARGS_TITLE = "args_title"
        private const val ARGS_MESSAGE = "args_message"
        private const val ARGS_POSITIVE_BUTTON_TEXT = "positive_button_text"
        private const val ARGS_NEGATIVE_BUTTON_TEXT = "negative_button_text"
        private const val ARGS_SHOW_NEGATIVE_BUTTON = "show_negative_button"


        fun newInstance(
            title: String = "Required action",
            message: String = "Allow app to switch GPS on",
            positiveButtonTitle: String = "Yes",
            negativeButtonTitle: String = "No",
            showNegativeButton: Boolean = true
        ): AlertDialog {
            val bundle = Bundle().apply {
                putString(ARGS_TITLE, title)
                putString(ARGS_MESSAGE, message)
                putString(ARGS_POSITIVE_BUTTON_TEXT, positiveButtonTitle)
                putString(ARGS_NEGATIVE_BUTTON_TEXT, negativeButtonTitle)
                putBoolean(ARGS_SHOW_NEGATIVE_BUTTON, showNegativeButton)
            }
            return AlertDialog().apply {
                arguments = bundle
            }
        }
    }

    interface AlertDialogFragmentListener {

        fun onclick()
    }
}