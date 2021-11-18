package my.edu.tarc.iotassignmentg11

import android.app.AlertDialog

class ProgressBarSignIn(val fragment: SignIn) {


    private lateinit var isDialog:AlertDialog
    fun startLoading(){
        //Set View
        val inflater = fragment.layoutInflater
        val dialogView = inflater.inflate(R.layout.progress_bar_signin, null)
        //Set Dialog
        val builder = AlertDialog.Builder(fragment.context)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()
        //isDialog.setCanceledOnTouchOutside(true)
        isDialog.window?.setLayout(990,600) //Controlling width and height.
    }

    fun isDismiss(){
        isDialog.dismiss()
    }
}