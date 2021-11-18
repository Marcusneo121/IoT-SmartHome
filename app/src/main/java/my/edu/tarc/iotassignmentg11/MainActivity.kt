package my.edu.tarc.iotassignmentg11

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import my.edu.tarc.iotassignmentg11.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var condition = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Firebase.database.setPersistenceEnabled(true)


//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        this.window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
//        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        if (!condition) {
            val options = FirebaseOptions.Builder()
                .setApplicationId("1:155508844801:android:ef5127a80fe8595a94d300")
                .setApiKey("AIzaSyCCmz4_im6v7LEnzj0liiKce5BQI4N67U0")
                .setDatabaseUrl("https://iotassignment-1de1e-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .setProjectId("iotassignment-1de1e")
                .build()
            //FirebaseApp.initializeApp(this.context, options, "IoTAssignment") }
            FirebaseApp.initializeApp(this, options, "IoTAssignment")
            condition = true
        }

        val navController = findNavController(R.id.nav_host_fragment_content_main2)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main2)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}