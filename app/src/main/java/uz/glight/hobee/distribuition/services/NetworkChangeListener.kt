package uz.glight.hobee.distribuition.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import uz.glight.hobee.distribuition.utils.NetworkHelper

class NetworkChangeListener : BroadcastReceiver() {
    private lateinit var internetConnectionHelper: NetworkHelper
    override fun onReceive(context: Context?, intent: Intent?) {
        internetConnectionHelper = NetworkHelper(context!!)
        if (!internetConnectionHelper.isNetworkConnected()) {
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("Error")
            dialog.setCancelable(false)
            dialog.setMessage("Please check internet connection or turn on wifi")
            dialog.setPositiveButton(
                "OK"
            ) { dialog, _ ->
                onReceive(context, intent)
            }
            val alertDialog = dialog.create()
            alertDialog.show()
        }
    }
}