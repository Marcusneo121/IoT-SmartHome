package my.edu.tarc.iotassignmentg11

import android.app.AlertDialog

class ProgressBarEmailSend (val fragment: Register) {
    private lateinit var isDialog: AlertDialog
    fun startLoading(){
        //Set View
        val inflater = fragment.layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_bar_email_sent, null)
        //Set Dialog
        val builder = AlertDialog.Builder(fragment.context)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
        isDialog.setCanceledOnTouchOutside(true)
        isDialog.window?.setLayout(990,1000) //Controlling width and height.
    }

    fun isDismiss(){
        isDialog.dismiss()
    }
}